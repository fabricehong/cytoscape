package cytoscape.data.readers;
import java.io.StreamTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.io.Writer;
import java.text.ParseException;


/**
 * The purpose of hte class is to translate gml into an object tree, and print out an object treee
 * into GML
 */
public class GMLParser{
  /**
   * This character begsin and ends a string literal in a GML file
   */
  static int QUOTE_CHAR = '"';
  /**
   * String versio of above
   */
  static String QUOTE_STRING = "\"";
  /**
   * This regex pattern will match a valid key
   */
  static Pattern keyPattern = Pattern.compile("\\w+");;
  /**
   * This regex pattern will match a valid integer
   */
  static Pattern integerPattern = Pattern.compile("(\\+|\\-){0,1}\\d+");;
  /**
   * This regex pattern will match a valid real (double)
   */
  static Pattern realPattern = Pattern.compile("(\\+|\\-){0,1}\\d+\\.\\d+((E|e)(\\+|\\-){0,1}\\d+){0,1}");;
  /*
   * This string opens a list
   */
  static String LIST_OPEN = "[";
  /**
   * This string close a list
   */
  static String LIST_CLOSE = "]";
  StreamTokenizer tokenizer;
    
  /**
   * Constructor has to initialize the relevenat
   * regular expression patterns
   */
  public GMLParser(StreamTokenizer tokenizer){
    this.tokenizer = tokenizer;
  }

  /**
   * Make a stream tokenizer out of the given this file and read in the file
   */
  public GMLParser(String file) throws IOException{
    tokenizer = new StreamTokenizer(new BufferedReader(new FileReader(file)));
    tokenizer.resetSyntax();
    tokenizer.commentChar('#');
    tokenizer.quoteChar('"');
    tokenizer.eolIsSignificant(false);
    /*
     * Set up the relevant word and whitespace characters
     */
    tokenizer.wordChars(' ', '!');
    tokenizer.wordChars('$','~');
    tokenizer.wordChars('\u00A0','\u00FF');
    tokenizer.whitespaceChars('\u0000','\u0020');
    tokenizer.nextToken();
  }
    
  /**
   * A short test of the regeular expressio patterns
   */
  public static void main(String [] args){
    //test pattersn
    if(!integerPattern.matcher("9").matches()) System.out.println("integer failed");
    if(!integerPattern.matcher("+11").matches()) System.out.println("integer failed");
    if(!realPattern.matcher("11.9e-07").matches()) System.out.println("real failed");
    try{
      GMLParser parser = new GMLParser("test.gml");
      List list = parser.parseList();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Public method to print out a given object tree(list)
   * using the supplied filewriter
   */
  public static void printList(List list,Writer writer) throws IOException{
    printList(list,"",writer);
  }

  /**
   * Protected recurive helper method to print out an object tree
   */
  protected static void printList(List list,String indent,Writer writer) throws IOException{
    for(Iterator it = list.iterator();it.hasNext();){
      KeyValue keyVal = (KeyValue)it.next();
      if(keyVal.value instanceof List){
	/*
	 * If the value is a list, print that list recursively
	 * surrounded by the list open and close characters
	 */
	writer.write(indent+keyVal.key+"\t");
	writer.write(LIST_OPEN+"\n");
	printList((List)keyVal.value,indent+"\t",writer);
	writer.write(indent+LIST_CLOSE+"\n");
      }else if(keyVal.value instanceof String){
	/*
	 * Surround a string with the quote characters
	 */
	writer.write(indent+keyVal.key+"\t");
	writer.write(QUOTE_STRING+keyVal.value+QUOTE_STRING+"\n");
      }else if(keyVal.value instanceof Double){
	/*
	 * If the double contains a non-number, we will refuse to write it out
	 * because the result will be invalid gml
	 */
	Double value = (Double)keyVal.value;
	if(!(value.isNaN() || value.isInfinite())){
	  writer.write(indent+keyVal.key+"\t");
	  writer.write(keyVal.value+"\n");
	} 
      }else if(keyVal.value instanceof Integer){
      	/*
	 * Everything else (Integer, double) relies upon the default toString() method
	 * of hte object
	 */
	writer.write(indent+keyVal.key+"\t");
	writer.write(keyVal.value+"\n");
      }
    }
  }

  /**
   * A list consists of zero or more paris of keys and values
   */
  public Vector parseList() throws IOException,ParseException{
    Vector result = new Vector();	
    while(isKey()){
      String key = parseKey();
      if(key == null){
	throw new ParseException("Bad key",tokenizer.lineno());
      }
      tokenizer.nextToken();
      Object value = parseValue();
      if(value == null){
	throw new ParseException("Bad value associated with key "+key,tokenizer.lineno());
      }
      result.add(new KeyValue(key,value));
      tokenizer.nextToken();

    }
    return result;
	
  }

  /**
   * A key consists of one or more occurrences of lower/upper-case[A-Z][0-9]
   */
  private boolean isKey(){
    /*
     * A key must be some kind of wrod
     */
    if(tokenizer.ttype != StreamTokenizer.TT_WORD){
      return false;
    }
    return keyPattern.matcher(tokenizer.sval).matches();
  }

   /**
   * An integer consists of hte characters [0-9]
   * Assuming hte precondition that we have already checked
   * for the end of file
   */
  private boolean isInteger(){
    return integerPattern.matcher(tokenizer.sval).matches();
  }

  /**
   * A real consists of 1 or more digits followed by a ., followed by one or more digits
   * and an optional mantissa. Assuming the preconiditon that we have already
   * checked for hte end of file
   */
  private boolean isReal(){
    return realPattern.matcher(tokenizer.sval).matches();
  }

  /**
   * A string consits of a string that begins and ends with the quote character
   * The streamtokenizer will basically check for this for me, so I just
   * have to check the token type and see if it is a quote char
   * It's also not supposed ot have a couple things in it, but I don't really 
   * have a check for that.
   */
  private boolean isString(){
    return tokenizer.ttype == QUOTE_CHAR;
  }

  /**
   * A list starts with the list open character
   * I'm assuming I get some white space before and
   * after the list delimiter, that might not actually
   * be true according to the spec
   */
  private boolean isList(){
    return tokenizer.sval.equals(LIST_OPEN);
  }
  
  /**
   * Verify that the current value is a key,
   * and then return the key that is found
   */
  private String parseKey(){
    if(isKey()){
      return tokenizer.sval;
    }else{
      return null;
    }
  }

  /**
   * Parse out a value, it can be a integer,real,string, or a list. Assume is has already been found
   * to not be the end of file
   */
  private Object parseValue() throws IOException, ParseException{
    Object result = null;
    if(tokenizer.ttype == StreamTokenizer.TT_EOL){
      return result;
    }
    if(isString()){
      return tokenizer.sval;
    }
    if(isInteger()){
      return new Integer(tokenizer.sval);
    }
    if(isReal()){
      return new Double(tokenizer.sval);
    }
    if(isList()){
      tokenizer.nextToken();
      List list =  parseList();
      if(!tokenizer.sval.equals(LIST_CLOSE)){
	throw new ParseException("Unterminated list", tokenizer.lineno());
      }
      return list;
    }
    return result;
  }
  
}

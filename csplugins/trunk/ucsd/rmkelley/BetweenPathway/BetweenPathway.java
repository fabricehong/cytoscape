package ucsd.rmkelley.Temp;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;

/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class BetweenPathway extends CytoscapePlugin{
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public BetweenPathway(){
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new TestAction() );
    }
    
   

    public class TestAction extends AbstractAction{
    
	public TestAction() {super("Test Action");}
    
	/**
	 * This method is called when the user selects the menu item.
	 */
	public void actionPerformed(ActionEvent ae) {
	    Thread t = new BetweenPathwayThread();
	    t.start();
	}

    }
}

class BetweenPathwayThread extends Thread{
    double absent_score = .00001;
    double logBeta = Math.log(0.9);
    double logOneMinusBeta = Math.log(0.1);
    public void run(){
	System.err.println("Between Pathway Thread started");
	//number of physical interactions allowed between pathways
	int cross_count_limit = 1;
	//get the two networks which will be used for the search
	BetweenPathwayOptionsDialog dialog = new BetweenPathwayOptionsDialog();
	dialog.show();
	CyNetwork physicalNetwork = dialog.getPhysicalNetwork();
	CyNetwork geneticNetwork = dialog.getGeneticNetwork();
	
	System.err.println("Genetic network is "+geneticNetwork.getTitle());

	//set up the array of genetic scores
	double [][] geneticScores = getScores(dialog.getGeneticScores(),geneticNetwork);
	double [][] physicalScores = getScores(dialog.getPhysicalScores(),physicalNetwork);
    
	System.err.println("There are "+geneticNetwork.getEdgeCount()+" genetic interactions");
	Vector results = new Vector();
	//start a search from each individual genetic interactions
	ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,geneticNetwork.getEdgeCount());
	int progress = 0;
    
	for(Iterator geneticIt = geneticNetwork.edgesIterator();geneticIt.hasNext();){
	    if(progress == 25){
		break;
	    }

	    myMonitor.setProgress(progress++);
	    Edge seedInteraction = (Edge)geneticIt.next();
	    int cross_count_total = 0;
	    int score = 0;
	    //performs a sanity check, shouldn't have a genetic interaction between a gene and itself
	    if(seedInteraction.getSource().getRootGraphIndex() == seedInteraction.getTarget().getRootGraphIndex()){
		break;
	    }
      
	    //initialize the sets that represent the two pathways
	    Set [] members = new Set[2];
	    members[0] = new HashSet();
	    members[1] = new HashSet();
	    members[0].add(seedInteraction.getSource());
	    members[1].add(seedInteraction.getTarget());
      
	    //initialize the sets that represent the neighbors in the physical network
	    Set [] neighbors = new Set[2];
	    for(int idx = 0;idx<neighbors.length;idx++){
		Node member = (Node)members[idx].iterator().next();
		if(physicalNetwork.containsNode(member)){
		    neighbors[idx] = new HashSet(physicalNetwork.neighborsList(member));
		}
		else{
		    neighbors[idx] = new HashSet();
		}
	    }
	    if(neighbors[1].contains(members[0].iterator().next())){
		cross_count_total++;
	    }
            
	    boolean improved = true;
	    double significant_increase = 0;
	    while(improved){
		improved = false;
		for(int idx=0;idx<2;idx++){
		    int opposite = 1-idx;
		    Node bestCandidate = null;
		    double best_increase = significant_increase;
		    int best_cross_count = 0;
	  
		    //keeps track of nodes which will have to be removed (from where?)
		    Vector remove = new Vector();
	  
		    //iterate through neighbors in the physical network and calculate a score for each
		    for(Iterator candidateIt = neighbors[idx].iterator();candidateIt.hasNext();){
			double increase = 0;
			Node candidate = (Node)candidateIt.next();

			//this neighbor is already the member of a pathway, we don't want to consider it further as a neigbhor
			if(members[0].contains(candidate) || members[1].contains(candidate)){
			    remove.add(candidate);
			    continue;
			}
	    
			//determine how many cross pathway interactions adding in this neighbor would induce,
			//we want to make sure that we do not exceed the limit
			int cross_count = cross_count_total;
			for(Iterator candidateNeighborIt = physicalNetwork.neighborsList(candidate).iterator();candidateNeighborIt.hasNext();){
			    if(members[opposite].contains(candidateNeighborIt.next())){
				cross_count++;
				if(cross_count >= cross_count_limit){
				    break;
				}
			    }
			}
	    
			if(cross_count >= cross_count_limit){
			    continue;
			}
	    
			increase += calculateIncrease(physicalNetwork,physicalScores,members[idx],candidate);
			increase += calculateIncrease(geneticNetwork,geneticScores,members[opposite],candidate);

			//if necessary, update the information for the best candidate
			//found so far
			if(increase > best_increase){
			    bestCandidate = candidate;
			    best_increase = increase;
			    best_cross_count = cross_count;
			}
		    }
		    neighbors[idx].removeAll(remove);
		    if(best_increase > significant_increase){
			improved = true;
			score += best_increase;
			cross_count_total = best_cross_count;
			members[idx].add(bestCandidate);
			neighbors[idx].addAll(physicalNetwork.neighborsList(bestCandidate));
			neighbors[idx].remove(bestCandidate);
		    }
		}  
	    }
	    results.add(new NetworkModel(progress,members[0],members[1],score));
	}
	myMonitor.close();
	Collections.sort(results);
	JDialog betweenPathwayDialog = new BetweenPathwayResultDialog(geneticNetwork, physicalNetwork, results);
	betweenPathwayDialog.show();
    }

    public double calculateIncrease(CyNetwork cyNetwork, double[][] scores, Set memberSet, Node candidate){
    
	double result = 0;
	if(cyNetwork.containsNode(candidate)){
	    int candidate_index = cyNetwork.getIndex(candidate);
	    for(Iterator memberIt = memberSet.iterator();memberIt.hasNext();){
		Node member = (Node)memberIt.next();
		if(cyNetwork.containsNode(member)){
		    int member_index = cyNetwork.getIndex(member);
		    int one,two;
		    if(candidate_index < member_index){
			one = member_index-1;
			two = candidate_index-1;
		    }
		    else{
			one = candidate_index-1;
			two = member_index-1;
		    }
		    if(cyNetwork.isNeighbor(candidate,member)){
			result += logBeta;
			result -= Math.log(scores[one][two]);
		    }
		    else{
			result += logOneMinusBeta;
			result -= Math.log(1-scores[one][two]);
		    }
		}
		else{
		    result += logOneMinusBeta;
		    result -= Math.log(1-absent_score);
		}
	    }
	}
	else{
	    result =  memberSet.size()*(logOneMinusBeta-Math.log(1-absent_score));
	}
	return result;
	
    }

    public double [][] getScores(File scoreFile, CyNetwork cyNetwork){
	double [][] result = new double[cyNetwork.getNodeCount()][];
	String [] names = new String [cyNetwork.getNodeCount()];
	for(int idx=result.length-1;idx>-1;idx--){
	    result[idx] = new double[idx];
	}
    
	try{
	    BufferedReader reader = new BufferedReader(new FileReader(scoreFile));
	    int line_number = 0;
	    String iterationString = reader.readLine();
	    double iterations = (new Integer(iterationString)).doubleValue();
	    System.err.println(iterations);
	    while(reader.ready()){
		String line = reader.readLine();
		String [] splat = line.split("\t");
		names[line_number++] = splat[0];
		int one = cyNetwork.getIndex(Cytoscape.getCyNode(splat[0]));
		for(int idx=1;idx<splat.length;idx++){
		    int two = cyNetwork.getIndex(Cytoscape.getCyNode(names[idx-1]));
		    if(one < two){
			result[two-1][one-1] = (new Integer(splat[idx])).intValue()/iterations;
		    }
		    else{
			result[one-1][two-1] = (new Integer(splat[idx])).intValue()/iterations;
		    }

		}
	    }
	}catch(Exception e){
	    e.printStackTrace();
	    System.exit(-1);
	}
	return result;
    }
}


class NetworkModel implements Comparable{
    public NetworkModel(int ID,Set one,Set two,double score){
	this.ID = ID;
	this.one = one;
	this.two = two;
	this.score = score;
    }

    public int compareTo(Object o){
	return -(new Double(score)).compareTo(new Double(((NetworkModel)o).score));
    }
    public Set one;
    public Set two;
    public int ID;
    public double score;
}

class BetweenPathwayOptionsDialog extends JDialog{
    NetworkSelectionPanel geneticPanel,physicalPanel;
    public BetweenPathwayOptionsDialog(){
	setModal(true);
	geneticPanel = new NetworkSelectionPanel();
	physicalPanel = new NetworkSelectionPanel();
	JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.add("Select Genetic Network",geneticPanel);
	tabbedPane.add("Select Physical Network",physicalPanel);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(tabbedPane,BorderLayout.CENTER);
	JButton ok = new JButton("OK");
	JPanel southPanel = new JPanel();
	southPanel.add(ok);
	getContentPane().add(southPanel,BorderLayout.SOUTH);
	ok.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    BetweenPathwayOptionsDialog.this.dispose();
		}
	    });
	pack();
    }
	
    public CyNetwork getGeneticNetwork(){
	return geneticPanel.getSelectedNetwork();
    }
    
    public CyNetwork getPhysicalNetwork(){
	return physicalPanel.getSelectedNetwork();
    }
    
    public File getPhysicalScores(){
	return physicalPanel.getScoreFile();
    }
    
    public File getGeneticScores(){
	return geneticPanel.getScoreFile();
    }
    
}

class NetworkSelectionPanel extends JPanel{
    File scoreFile;
    JTextField fileText;
    JList list;
    public NetworkSelectionPanel(){
	setLayout(new BorderLayout());
	Vector model = new Vector();
	for(Iterator networkIt = Cytoscape.getNetworkSet().iterator();networkIt.hasNext();){
	    model.add(new NetworkContainer((CyNetwork)networkIt.next()));
	}
	list = new JList(model);
	list.setSelectedIndex(0);
	add(list,BorderLayout.CENTER);
	JPanel southPanel = new JPanel();
	southPanel.add(new JLabel("Score file"));
	fileText = new JTextField("none");
	southPanel.add(fileText);
	JButton change = new JButton("Select score file");
	change.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    JFileChooser chooser = new JFileChooser();
		    int returnVal = chooser.showOpenDialog(Cytoscape.getDesktop());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
			scoreFile = chooser.getSelectedFile();
			fileText.setText(scoreFile.getName());
		    }
		}});
	southPanel.add(change);
	add(southPanel,BorderLayout.NORTH);
    }

    public CyNetwork getSelectedNetwork(){
	return ((NetworkContainer)list.getSelectedValue()).getNetwork();
    }

    public File getScoreFile(){
	return scoreFile;
    }


}

class NetworkContainer{
    CyNetwork network;
    public NetworkContainer(CyNetwork network){
	this.network = network;
    }

    public String toString(){
	return network.getTitle();
    }

    public CyNetwork getNetwork(){
	return network;
    }
}


class BetweenPathwayResultDialog extends JDialog implements ListSelectionListener{
    Vector results;
    JTable table;
    CyNetwork geneticNetwork,physicalNetwork;
    public BetweenPathwayResultDialog(CyNetwork geneticNetwork, CyNetwork physicalNetwork, Vector results){
	this.results = results;
	this.geneticNetwork = geneticNetwork;
	this.physicalNetwork = physicalNetwork;
	table = new JTable(new BetweenPathwayResultModel(results));
	table.getSelectionModel().addListSelectionListener(this);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(new JScrollPane(table),BorderLayout.CENTER);
	JButton viewButton = new JButton("view");
	viewButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		    NetworkModel model = (NetworkModel)BetweenPathwayResultDialog.this.results.get(table.getSelectionModel().getMinSelectionIndex());
		    List allNodes = new Vector();
		    allNodes.addAll(model.one);
		    allNodes.addAll(model.two);
	  
		    List allEdges = new Vector();
		    allEdges.addAll(BetweenPathwayResultDialog.this.geneticNetwork.getConnectingEdges(allNodes));
		    allEdges.addAll(BetweenPathwayResultDialog.this.physicalNetwork.getConnectingEdges(allNodes));

		    CyNetwork newNetwork = Cytoscape.createNetwork(allNodes,allEdges,"Between-Pathway");
		    CyNetworkView newView = Cytoscape.getNetworkView(newNetwork.getIdentifier());
		    if(newView != null){
			CircleGraphLayout layout = new CircleGraphLayout(newView,model.one,model.two);
			layout.construct();
		    }
		}
	    });
	getContentPane().add(viewButton,BorderLayout.SOUTH);
	pack();
    }

    public void valueChanged(ListSelectionEvent e){
	int index = e.getFirstIndex();
	NetworkModel model = (NetworkModel)results.get(index);
	geneticNetwork.setFlaggedNodes(model.one,true);
	geneticNetwork.setFlaggedNodes(model.two,true);
	physicalNetwork.setFlaggedNodes(model.one,true);
	physicalNetwork.setFlaggedNodes(model.two,true);
    }
  
}

class BetweenPathwayResultModel extends AbstractTableModel{
    String [] columnNames;
    List data;
    public BetweenPathwayResultModel(List data){
	columnNames = new String[]{"ID","Score"};
	this.data = data;
    }
    public int getColumnCount(){
	return columnNames.length;
    }

    public int getRowCount(){
	return data.size();
    }

    public String getColumnName(int col){
	return columnNames[col];
    }

    public Object getValueAt(int row,int col){
	if(col == 0){
	    return new Integer(((NetworkModel)data.get(row)).ID);
	}
	else if(col == 1){
	    return new Double(((NetworkModel)data.get(row)).score);
	}
	else{
	    return null;
	}
    }

    public Class getColumnClass(int c){
	return getValueAt(0,c).getClass();
    }
}


class CircleGraphLayout extends AbstractLayout{
    
    Collection leftCollection;
    Collection rightCollection;
    
    public CircleGraphLayout ( CyNetworkView networkView, Collection leftCollection, Collection rightCollection) {
	super( networkView );
	this.leftCollection = leftCollection;
	this.rightCollection = rightCollection;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public Object construct () {
	initialize();
	int[] nodes = networkView.getNetwork().getNodeIndicesArray();
    
	int r = (int)currentSize.getHeight();
	double OFFSET = r*2.0;
	double phi = Math.PI / (leftCollection.size()-1);
	int i=0;
	for(Iterator nodeIt = leftCollection.iterator();nodeIt.hasNext();i++){
	    int node = ((Node)nodeIt.next()).getRootGraphIndex();
	    networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_X_POSITION, 	OFFSET + r + r * Math.sin(i * phi) );
	    networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_Y_POSITION, 	r + r * Math.cos(i * phi) );
	    PNodeView nodeView = (PNodeView)networkView.getNodeView(node);
	    nodeView.setNodePosition(false);
	}

	phi = Math.PI / (rightCollection.size()-1);
	i = 0;
	for(Iterator nodeIt = rightCollection.iterator();nodeIt.hasNext();i++){
	    int node = ((Node)nodeIt.next()).getRootGraphIndex();
	    networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_X_POSITION, 	r - r * Math.sin(i * phi) );
	    networkView.setNodeDoubleProperty( node , CyNetworkView.NODE_Y_POSITION, 	r + r * Math.cos(i * phi) );
	    PNodeView nodeView = (PNodeView)networkView.getNodeView(node);
	    nodeView.setNodePosition(false);
	}



	return null;
    }
 
}

  

    

package cytoscape.dialogs.preferences;

import cytoscape.*;
import cytoscape.util.CyFileFilter;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

public class PreferenceValueDialog extends JDialog {
    String      preferenceName = null;
    String      preferenceValue = null;
    String 	title = null;
    JLabel      preferenceNameL = null;
    JTextField  value           = null;
    JButton     browseButton    = null;
    JButton     okButton        = null;
    JButton     cancelButton    = null;
    TableModel	tableModel	= null;
    boolean	includeBrowseBtn = false;

    PreferencesDialog callerRef = null;

    public PreferenceValueDialog(Dialog owner, String name,
		String value, PreferencesDialog caller, TableModel tm,
		String title, boolean includeBrowse) {
	super(owner,true);
        callerRef = caller;
	tableModel = tm;
	this.title=title;
	includeBrowseBtn = includeBrowse;
        
        preferenceName = new String(name);
        preferenceValue = new String(value);

        showDialog(owner);
    }
        
    protected void showDialog(Dialog owner) {

	preferenceNameL = new JLabel(preferenceName);
        value = new JTextField(preferenceValue, 32);
        browseButton  = new JButton("Browse..");
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        browseButton.addActionListener(new
			BrowseButtonListener(this,callerRef));
        okButton.addActionListener(new OkButtonListener(this, callerRef));
        cancelButton.addActionListener(new CancelButtonListener(this,
								callerRef));

	JPanel outerPanel = new JPanel(new BorderLayout());
	JPanel valuePanel = new JPanel(new FlowLayout());
	JPanel buttonPanel = new JPanel(new FlowLayout());
        valuePanel.add(preferenceNameL);
        valuePanel.add(value);
	// take a guess and look for names with directory or file in them
	// and include the browse button if found
        if (includeBrowseBtn ||
		preferenceName.toUpperCase().indexOf("DIRECTORY") >= 0 ||
		preferenceName.toUpperCase().indexOf("FILE") >= 0) {
            valuePanel.add(browseButton);
        }
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
	outerPanel.add(valuePanel,BorderLayout.NORTH);
	outerPanel.add(buttonPanel,BorderLayout.SOUTH);
        
        this.getContentPane().add(outerPanel, BorderLayout.CENTER);
	pack();

	this.setTitle(title);
	// popup relative to owner/parent
	this.setLocationRelativeTo(owner);
        this.setVisible(true);
        
    }
    
    class BrowseButtonListener implements ActionListener {
        PreferenceValueDialog motherRef = null;
        PreferencesDialog grandmotherRef = null;

        JFileChooser fc = null;
        
        public BrowseButtonListener(PreferenceValueDialog mother,
					PreferencesDialog grandmother) {
            super();
            motherRef = mother;
            grandmotherRef = grandmother;
        }

        public void actionPerformed(ActionEvent e) {

	    // use relative paths (since command line args do by default)
	    // or absolute paths if explicitly specifed (by not using
	    // the file chooser)
	    String startingDir = grandmotherRef.prefsTM.getProperty("mrud");
	    if (startingDir == null)
		startingDir = System.getProperty("user.dir");

            fc = new JFileChooser(startingDir);
            CyFileFilter filter = new CyFileFilter();
            filter.addExtension("jar");
            filter.setDescription("Plugins");

            if (motherRef.preferenceName.equals("Local")) {
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setFileFilter(filter);
            } else if (motherRef.preferenceName.equals("mrud")) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            }            

            int returnVal =  fc.showOpenDialog(motherRef);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
		String selection = file.getAbsolutePath();

                if (motherRef.preferenceName.equals("Local")) {
			String plugins = validatedPluginData(motherRef,
						selection);
			motherRef.value.setText(plugins);
		}  else if (motherRef.preferenceName.equals(
						"bioDataServer")) {
				motherRef.preferenceValue = selection;
			motherRef.value.setText(motherRef.preferenceValue);
		}  else if (motherRef.preferenceName.equals("mrud")) {
				motherRef.preferenceValue = selection;
			motherRef.value.setText(motherRef.preferenceValue);
		}
	    } else {
		motherRef.dispose();
	    }
	}
    } 

	    public String validatedPluginData(Component comp, String selection){
		String validatedData = null;
		File file = new File(selection);
		// directory was specified
		if (file.isDirectory()) {
		    // must expand as list of files, as the autoloading tries to
		    // use the specified values as JAR files.
		    String [] fileList = file.list();	// could use FileFilter
		    String pluginFiles = new String("");
		    for (int j=0;j<fileList.length; j++) {
			String jarString = file.getAbsolutePath()+
						File.separator+ fileList[j];
			if (jarString.endsWith(".jar")) {
			  if (pluginFiles.length()>0) {
			    pluginFiles = pluginFiles+","+jarString;
			  } else {
			    pluginFiles = new String(jarString);
		 	  }
			}
		    }
		    if (pluginFiles.length() == 0) {
			// no *.jar files found in directory, popup info dialog
			JOptionPane.showMessageDialog(comp,
				"No plugins (*.jar files) found.","Information",
				JOptionPane.INFORMATION_MESSAGE);
		    } else {
			validatedData = pluginFiles;
		    }
		} else {
		// ordinary file was specified
		    if (!selection.endsWith(".jar")) {
			 // no *.jar files found in input string, popup info
			 // dialog
			 JOptionPane.showMessageDialog(comp,
				"No plugins (*.jar files) found.","Information",
				JOptionPane.INFORMATION_MESSAGE);
		    } else {
			validatedData = selection;
		    }
		}
		return validatedData;
	    }


	    class OkButtonListener implements ActionListener {
		PreferenceValueDialog motherRef = null;
		PreferencesDialog grandmotherRef = null;
		public OkButtonListener(PreferenceValueDialog mother,
					PreferencesDialog grandmother) {
		    super();
		    motherRef = mother;
		    grandmotherRef = grandmother;
		}

		public void actionPerformed(ActionEvent e) {
		    if (tableModel == grandmotherRef.pluginsTM) {
			// plugins
			String plugins = validatedPluginData(motherRef,
					motherRef.value.getText());
		      if (plugins != null && plugins.length() >0) {
            	        grandmotherRef.setParameter(tableModel,
					motherRef.preferenceName,
					plugins);
                        motherRef.dispose();
		      } else {
		        motherRef.value.setText("");
		      }
		    } else {
			// properties
		      motherRef.preferenceValue = motherRef.value.getText();
		      grandmotherRef.setParameter(tableModel,
					motherRef.preferenceName,
					motherRef.preferenceValue);
		      motherRef.dispose();
		    }
		}
	    }

    class CancelButtonListener implements ActionListener {
        PreferenceValueDialog motherRef = null;
        PreferencesDialog grandmotherRef = null;
        public CancelButtonListener(PreferenceValueDialog mother,
					PreferencesDialog grandmother) {
            super();
            motherRef = mother;
            grandmotherRef = grandmother;
        }
        public void actionPerformed(ActionEvent e) {
            motherRef.dispose();
        }
    }
}

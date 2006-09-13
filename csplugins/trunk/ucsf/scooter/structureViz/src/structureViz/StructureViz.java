/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz;

// System imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

// structureViz imports
import structureViz.Chimera;
import structureViz.ui.ModelNavigatorDialog;
import structureViz.model.Structure;
import structureViz.model.ChimeraModel;


public class StructureViz extends CytoscapePlugin 
  implements NodeContextMenuListener, PropertyChangeListener {

	public static final int NONE = 0;
	public static final int OPEN = 1;
	public static final int CLOSE = 2;
	public static final int ALIGN = 3;
	public static final int EXIT = 4;

  /**
   * Create our action and add it to the plugins menu
   */
  public StructureViz() {
		try {
			// Set ourselves up to listen for new networks
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
	
			// Add ourselves to the current network context menu
			((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}
	    
		JMenu menu = new JMenu("Structure Visualization");
		menu.addMenuListener(new StructureVizMenuListener());

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

  }

	public class StructureVizMenuListener implements MenuListener {
		private StructureVizCommandListener staticHandle;

		StructureVizMenuListener() {
			this.staticHandle = new StructureVizCommandListener(NONE,null);
		}

	  public void menuCanceled (MenuEvent e) {};
		public void menuDeselected (MenuEvent e) {};
		public void menuSelected (MenuEvent e)
		{
			JMenu m = (JMenu)e.getSource();
			// Clear the menu
			Component[] subMenus = m.getMenuComponents();
			for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

			// Add our menu items
			{
			  JMenu item = new JMenu("Open structure(s)");
				List structures =  getSelectedStructures();
				addSubMenu(item, "all", OPEN, structures);
				Iterator iter = structures.iterator();
				while (iter.hasNext()) {
					Structure structure = (Structure)iter.next();
					addSubMenu(item, structure.name(), OPEN, structure);
				}
				m.add(item);
			}
			{
				JMenuItem item = new JMenuItem("Align structures");
				StructureVizCommandListener l = new StructureVizCommandListener(ALIGN, null);
				item.addActionListener(l);
				if (l.getChimera() == null || !l.getChimera().isLaunched()) item.setEnabled(false);
				m.add(item);
			}
			{
				if (staticHandle.getChimera() == null || !staticHandle.getChimera().isLaunched())  
				{
			  	JMenuItem item = new JMenuItem("Close structure(s)");
					item.setEnabled(false);
			  	m.add(item);
				} else {
			  	JMenu item = new JMenu("Close structure(s)");
					List<Structure>openStructures = staticHandle.getOpenStructs();
					addSubMenu(item, "all", CLOSE, openStructures);
					Iterator iter = openStructures.iterator();
					while (iter.hasNext()) {
						Structure structure = (Structure)iter.next();
						addSubMenu(item, structure.name(), CLOSE, structure);
					}
					m.add(item);
				}
			}
			{
				JMenuItem item = new JMenuItem("Exit Chimera");
				StructureVizCommandListener l = new StructureVizCommandListener(EXIT, null);
				item.addActionListener(l);
				if (l.getChimera() == null || !l.getChimera().isLaunched()) item.setEnabled(false);
				m.add(item);
			}
		}

		private void addSubMenu(JMenu menu, String label, int command, Object userData) {
			System.out.println("Adding item "+label);
			JMenuItem item = new JMenuItem(label);
			StructureVizCommandListener l = new StructureVizCommandListener(command, userData);
			item.addActionListener(l);
		  menu.add(item);
		}

		private List getSelectedStructures() {
			List<Structure>structureList = new ArrayList<Structure>();
      //get the network object; this contains the graph
      CyNetwork network = Cytoscape.getCurrentNetwork();
      //get the network view object
      CyNetworkView view = Cytoscape.getCurrentNetworkView();
      //get the list of node attributes
      CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
      //can't continue if any of these is null
      if (network == null || view == null || cyAttributes == null) {return structureList;}
      //put up a dialog if there are no selected nodes
      if (view.getSelectedNodes().size() == 0) {
        JOptionPane.showMessageDialog(view.getComponent(),
                    "Please select one or more nodes.");
				return structureList;
      }
      //iterate over every node view
      for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
        NodeView nView = (NodeView)i.next();
        //first get the corresponding node in the network
        CyNode node = (CyNode)nView.getNode();
        String nodeID = node.getIdentifier();
        // See if there is a 'structure' attribute
        if (cyAttributes.hasAttribute(nodeID, "Structure")) {
          // Yes, add it to our list
          String structure = cyAttributes.getStringAttribute(nodeID, "Structure");
          structureList.add(new Structure(structure,node));
        }
        else if (cyAttributes.hasAttribute(nodeID, "pdb")) {
          // Yes, add it to our list
          String structure = cyAttributes.getStringAttribute(nodeID, "pdb");
          structureList.add(new Structure(structure,node));
        }
        else if (cyAttributes.hasAttribute(nodeID, "pdbFileName")) {
          // Yes, add it to our list
          String structure = cyAttributes.getStringAttribute(nodeID, "pdbFileName");
          structureList.add(new Structure(structure,node));
        }
      }
			return structureList;
		}
	}
	
  /**
   * This class gets attached to the menu item.
   */
  static class StructureVizCommandListener implements ActionListener {
  	private static final long serialVersionUID = 1;
		private static Chimera chimera = null;
		private static ModelNavigatorDialog mnDialog = null;
		private int command;
		private Object userData = null; // Either a Structure or an ArrayList

		StructureVizCommandListener(int command, Object userData) {
			this.command = command;
			this.userData = userData;
		}

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
			String label = ae.getActionCommand();
			if (command == OPEN) {
				openAction(label);
			} else if (command == EXIT) {
				exitAction();
			} else if (command == ALIGN) {
				alignAction(label);
			} else if (command == CLOSE) {
				closeAction(label);
			}
		}

		public Chimera getChimera() {
			return chimera;
		}

		public List<Structure>getOpenStructs() {
	
			ArrayList<Structure>st = new ArrayList<Structure>();
			if (chimera == null) return st;

			List modelList = chimera.getChimeraModels();
			if (modelList == null) return st;

			Iterator modelIter = modelList.iterator();
			while (modelIter.hasNext()) {
				Structure structure = ((ChimeraModel)modelIter.next()).getStructure();
				if (structure != null)
					st.add(structure);
			}
			return st;
		}

		private void alignAction(String label) {
			// This is a quick-and-dirty example
		}

		private void exitAction() {
			if (chimera != null) {
				chimera.exit();
				chimera = null;
			}
			if (mnDialog != null) {
				// get rid of the dialog
				mnDialog.setVisible(false);
				mnDialog.dispose();
				mnDialog = null;
			}
		}

		private void closeAction(String commandLabel) {
			List<Structure>structList;
			if (chimera != null) {
				if (commandLabel.compareTo("all") != 0) {
					structList = new ArrayList<Structure>();
					structList.add((Structure)userData);
				} else {
					structList = (ArrayList)userData;
				}
				ListIterator iter = structList.listIterator();
				while (iter.hasNext()) {
					Structure structure = (Structure)iter.next();
					chimera.close(structure);
					// Not open any more -- remove it
					iter.remove();
				}
			}
			if (mnDialog != null) mnDialog.modelChanged();
		}

		private void openAction(String commandLabel) {
			boolean isLaunched = (chimera != null && chimera.isLaunched());
			if (!isLaunched) {
      	// Launch Chimera
      	try {
       	 // Get a chimera instance
       	 chimera = new Chimera(Cytoscape.getCurrentNetworkView());
       	 chimera.launch();
      	} catch (java.io.IOException e) {
       	 // Put up error panel
       	 JOptionPane.showMessageDialog(Cytoscape.getCurrentNetworkView().getComponent(),
       	 			"Unable to launch Chimera", "Unable to launch Chimera",
       	   			JOptionPane.ERROR_MESSAGE);
       		return;
      	}
			}
			ArrayList<Structure>structList = null;
			if (commandLabel.compareTo("all") == 0) {
				structList = (ArrayList)userData;
			} else {
				structList = new ArrayList<Structure>();
				structList.add((Structure)userData);
			}

      // Send initial commands
			Iterator iter = structList.iterator();
			while (iter.hasNext()) {
				Structure structure = (Structure) iter.next();
				chimera.open(structure);
			}

			if (mnDialog == null || !isLaunched) {
				// Finally, open up our navigator dialog
				mnDialog = new ModelNavigatorDialog(Cytoscape.getDesktop(), chimera);
				mnDialog.pack();
				mnDialog.setLocationRelativeTo(Cytoscape.getDesktop());
				mnDialog.setVisible(true);
    	} else {
				mnDialog.modelChanged();
			}
		}
  }

  public void propertyChange(PropertyChangeEvent evt) {
    if ( evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED ){
      // Add menu to the context dialog
			((DGraphView)Cytoscape.getCurrentNetworkView())
				.addNodeContextMenuListener(this);
    }
  }

	/**
	 * Implements addNodecontextMenuItems
	 * @param nodeView
	 * @param menu
	 */
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu pmenu) {
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		JMenu menu = new JMenu("Structure Visualization");
		menu.addMenuListener(new StructureVizMenuListener());
		pmenu.add(menu);
	}
}

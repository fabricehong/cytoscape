package csplugins.mcode;

import cytoscape.view.CyWindow;
import cytoscape.data.CyNetwork;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import giny.view.GraphView;
import giny.model.GraphPerspective;

import javax.swing.*;

/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Gary Bader
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 ** User: Gary Bader
 ** Date: Jan 22, 2004
 ** Time: 9:04:33 PM
 ** Description
 **/
public class MCODEFindAction implements ActionListener {
	/**
	 * Cytoscape Window.
	 */
	private CyWindow cyWindow;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public MCODEFindAction(CyWindow cyWindow) {
		this.cyWindow = cyWindow;
	}

	/**
	 * This method is called when the user selects the menu item.
	 * @param event Menu Item Selected.
	 */
	public void actionPerformed(ActionEvent event) {
		//get the graph view object from the window.
		GraphView graphView = cyWindow.getView();
		//get the network object; this contains the graph
		CyNetwork network = cyWindow.getNetwork();
		//can't continue if either of these is null
		if (graphView == null || network == null) {
			return;
		}

		//inform listeners that we're doing an operation on the network
		String callerID = "MCODEScoreAction.actionPerformed";
		network.beginActivity(callerID);
		//this is the graph structure; it should never be null,
		GraphPerspective gpInputGraph = network.getGraphPerspective();
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ":");
			System.err.println("Unexpected null gpInputGraph in network.");
			network.endActivity(callerID);
			return;
		}
		//and the view should be a view on this structure
		if (graphView.getGraphPerspective() != gpInputGraph) {
			System.err.println("In " + callerID + ":");
			System.err.println("Graph view is not a view on network's graph perspective.");
			network.endActivity(callerID);
			return;
		}

		if (gpInputGraph.getNodeCount() < 1) {
			JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
			        "You must have a network loaded to run this plugin.");
			network.endActivity(callerID);
			return;
		}

		//run MCODE complex finding algorithm after the nodes have been scored
		ArrayList complexes = MCODE.getInstance().alg.findComplexes(gpInputGraph);
		if(complexes==null) {
			JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
			        "The network has not been scored.  Please run the scoring step first.");
			network.endActivity(callerID);
			return;
		}
		//print out complexes
		for (int i = 0; i < complexes.size(); i++) {
			ArrayList complex = (ArrayList) complexes.get(i);
			System.err.println("Complex "+(i+1)+":" + complex.toString());
		}

		//and tell listeners that we're done
		network.endActivity(callerID);
	}
}

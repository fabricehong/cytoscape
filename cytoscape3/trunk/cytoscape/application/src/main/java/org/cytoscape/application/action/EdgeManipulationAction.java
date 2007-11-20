/*
  File: EdgeManipulationAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

//-------------------------------------------------------------------------
// $Revision: 9565 $
// $Date: 2007-02-13 11:36:50 -0800 (Tue, 13 Feb 2007) $
// $Author: mes $
//-------------------------------------------------------------------------
package org.cytoscape.application.action;





//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cytoscape.application.dialog.GinyEdgeControlDialog;
import org.cytoscape.application.util.Cytoscape;
import org.cytoscape.model.attribute.CyAttributes;
import org.cytoscape.view.network.CyNetworkView;


//-------------------------------------------------------------------------
/**
 *
 */
public class EdgeManipulationAction extends AbstractAction {
	CyNetworkView networkView;

	/**
	 * Creates a new EdgeManipulationAction object.
	 *
	 * @param networkView  DOCUMENT ME!
	 */
	public EdgeManipulationAction(CyNetworkView networkView) {
		super("Edge select or hide by attributes...");
		this.networkView = networkView;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();

		String[] edgeAttributeNames = edgeAtts.getAttributeNames();
		HashMap attributesTree = new HashMap();

		for (int i = 0; i < edgeAttributeNames.length; i++) {
			String att = edgeAttributeNames[i];

			if (edgeAtts.getType(att) == CyAttributes.TYPE_STRING) {
				// This iterator contains the edge uids that contain a value
				// for this attribute
				Iterator it = edgeAtts.getMultiHashMap().getObjectKeys(att);
				HashSet nameSet = new HashSet();

				while (it.hasNext()) {
					String edgeId = (String) it.next();
					nameSet.add(edgeAtts.getStringAttribute(edgeId, att));
				}

				attributesTree.put(att, (String[]) nameSet.toArray(new String[nameSet.size()]));
			} // if a string attribute
		} // for i

		if (attributesTree.size() > 0) {
			JDialog dialog = new GinyEdgeControlDialog(networkView, attributesTree,
			                                           "Edge Selection Control");
			dialog.pack();
			dialog.setLocationRelativeTo(networkView.getComponent());
			dialog.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null,
			                              "There are no String edge attributes suitable for controlling edge display");
		}
	} // actionPerformed
}

/*
  File: StraightenSelectedEdgesAction.java

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
// $Revision: 9905 $
// $Date: 2007-04-05 17:38:32 -0700 (Thu, 05 Apr 2007) $
// $Author: mes $
//-------------------------------------------------------------------------
package org.cytoscape.application.action;




import giny.view.EdgeView;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import javax.swing.event.MenuEvent;

import org.cytoscape.application.util.Cytoscape;
import org.cytoscape.view.network.CyNetworkView;


//-------------------------------------------------------------------------
/**
 *
 */
public class StraightenSelectedEdgesAction extends CytoscapeAction {
	/**
	 * Creates a new StraightenSelectedEdgesAction object.
	 */
	public StraightenSelectedEdgesAction() {
		super("Straighten selected edges");
		setPreferredMenu("Select.Edges");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		final int[] selectedEdges = view.getSelectedEdgeIndices();

		if (selectedEdges != null) {
			for (int i = 0; i < selectedEdges.length; i++) {
				EdgeView ev = view.getEdgeView(selectedEdges[i]);
				ev.setLineType(EdgeView.STRAIGHT_LINES);
			}

			view.updateView();
		}
	}

    public void menuSelected(MenuEvent e) {
        enableForNetworkAndView();
    }
}

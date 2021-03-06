/*
 File: NodeBypassMenuListener.java

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
package cytoscape.visual.ui;

import ding.view.NodeContextMenuListener;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;


/**
 * NodeBypassMenuListener implements NodeContextMenuListener
 * When a node is selected it calls bypass andd add
 */
class NodeBypassMenuListener implements NodeContextMenuListener {
	NodeBypassMenuListener() {
	}

	/**
	 * @param nodeView The clicked NodeView
	 * @param menu popup menu to add the Bypass menu
	 */
	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) {
		NodeBypass nb = new NodeBypass();

		if (menu == null)
			menu = new JPopupMenu();

		/*
		 * Add Node ID as label.
		 */
		final String nodeID = nodeView.getNode().getIdentifier();
		final JLabel nodeLabel = new JLabel(nodeID);

		nodeLabel.setForeground(new Color(10, 50, 250, 150));
		nodeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
		nodeLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
		menu.add(nodeLabel);

		menu.add(nb.addMenu(nodeView.getNode()));
	}
}

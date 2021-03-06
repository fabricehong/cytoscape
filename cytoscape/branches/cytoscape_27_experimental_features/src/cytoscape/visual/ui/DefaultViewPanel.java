/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.ding.DingNetworkView;

import cytoscape.giny.CytoscapeFingRootGraph;

import cytoscape.view.CyNetworkView;

import ding.view.DGraphView;

import giny.view.GraphView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


/**
 * Panel to show the default properties visually (as graphics).
 *
 * @version 0.6
 * @since Cytoscape 2.5
 * @author kono
  */
public class DefaultViewPanel extends JPanel {
	private static final int PADDING = 20;

	// Dummy network and its view.
	private static final CyNetworkView view;
	private static final CyNetwork dummyNet;

	// Background color of this view.
	private Color background = Color.white;
	private static final DefaultViewPanel panel;

	/*
	 * Dummy graph component
	 */
	private static final CytoscapeFingRootGraph dummyGraph;
	private static final CyNode source;
	private static final CyNode target;
	private static final CyEdge edge;

	static {
		dummyGraph = new CytoscapeFingRootGraph();
		source = (CyNode) dummyGraph.getNode(dummyGraph.createNode());
		source.setIdentifier("Source");
		target = (CyNode) dummyGraph.getNode(dummyGraph.createNode());
		target.setIdentifier("Target");
		edge = (CyEdge) dummyGraph.getEdge(dummyGraph.createEdge(source, target));
		edge.setIdentifier("dummyInteraction");

		List<CyNode> nodes = new ArrayList<CyNode>();
		List<CyEdge> edges = new ArrayList<CyEdge>();
		nodes.add(source);
		nodes.add(target);
		edges.add(edge);

		dummyNet = dummyGraph.createNetwork(nodes, edges);
		dummyNet.setTitle("Default Appearance");

		// Create default view
		view = new DingNetworkView(dummyNet, "Default Appearence");

		view.setIdentifier(dummyNet.getIdentifier());
		view.setTitle(dummyNet.getTitle());

		view.getNodeView(source).setOffset(0, 0);
		view.getNodeView(target).setOffset(150, 10);

		panel = new DefaultViewPanel();
		
		// Setup dummy attribute values
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		nodeAttr.setAttribute("Source", "hiddenLabel", "Source");
		nodeAttr.setAttribute("Target", "hiddenLabel", "Target");
		nodeAttr.setUserVisible("hiddenLabel", false);
		nodeAttr.setUserEditable("hiddenLabel", false);

		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		edgeAttr.setUserVisible("dummyInteraction", false);
		edgeAttr.setUserEditable("dummyInteraction", false);
	}

	protected static DefaultViewPanel getDefaultViewPanel() {
		return panel;
	}

	/**
	 * Creates a new NodeFullDetailView object.
	 */
	private DefaultViewPanel() {
		background = Cytoscape.getVisualMappingManager().getVisualStyle()
		                      .getGlobalAppearanceCalculator().getDefaultBackgroundColor();
		this.setBackground(background);
	}

	protected void updateBackgroungColor(final Color newColor) {
		background = newColor;
		this.setBackground(background);
		repaint();
	}

	/**
	 * DOCUMENT ME!
	 */
	protected void updateView() {
		Cytoscape.getVisualMappingManager().setNetworkView(view);
		view.setVisualStyle(Cytoscape.getVisualMappingManager().getVisualStyle().getName());

		final Dimension panelSize = this.getSize();
		((DGraphView) view).getCanvas()
		 .setSize(new Dimension((int) panelSize.getWidth() - PADDING,
		                        (int) panelSize.getHeight() - PADDING));
		// TODO: This is not always necessary.
		view.fitContent();

		final Component canvas = view.getComponent();

		for (MouseListener listener : canvas.getMouseListeners())
			canvas.removeMouseListener(listener);

		this.removeAll();
		this.add(canvas);

		canvas.setLocation(PADDING / 2, PADDING / 2);
		
		Cytoscape.getVisualMappingManager().applyAppearances();

		canvas.setBackground(background);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphView getView() {
		return view;
	}
}

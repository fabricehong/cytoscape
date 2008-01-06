package org.genmapp.subgeneviewer.splice;

import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.genmapp.subgeneviewer.splice.controller.SpliceController;
import org.genmapp.subgeneviewer.splice.model.SpliceEvent;
import org.genmapp.subgeneviewer.splice.model.SpliceRegion;
import org.genmapp.subgeneviewer.view.SGVNodeAppearanceCalculator;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * 
 */
public class SpliceViewPanel extends JPanel {

	private static final int PADDING = 20;

	private CyNetworkView view;

	private CyNetworkView oldView;

	private static CyNetwork dummyNet;

	private Color background;

	private static String nodeId;

	private static CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();

	private static CyNode feature;

	private static List<CyNode> features = new ArrayList<CyNode>();

	private static List<String> featureList = new ArrayList<String>();

	private static SpliceRegion _region;

	private static List edges = new ArrayList();

	private int panelHeight = 150;

	private int panelWidth = 200; // set to cummulative feature node
											// layout

	private Component canvas = null;

	private Component canvas2 = null;

	/**
	 * Processes feature, structure and splice attributes.
	 */
	public SpliceViewPanel() {

		
		// Get parent node identifier
		nodeId = SpliceController.get_nodeId();

		processFeatures();

	}

	/**
	 * Collect feature attributes from parent node, create CyNodes, set
	 * attributes, and create dummy network
	 */
	public void processFeatures() {
		List<String> featureAttsMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_feature");

		features.clear();
		featureList.clear();

		// Foreach list of feature attributes
		for (String featureAttsList : featureAttsMetaList) {

			// Split list of attributes per feature
			String[] featureAtts = featureAttsList.split(":");

			// Create CyNode: Concatenate feature_label and region_id to
			// generate a unique node identifier.Use feature_label for display.
			String feature_name = featureAtts[0] + "_" + featureAtts[1];
			feature = Cytoscape.getCyNode(feature_name, true);

			// Set label
			Cytoscape.getNodeAttributes().setAttribute(feature_name, "label",
					featureAtts[0]);
			Cytoscape.getNodeAttributes().setAttributeDescription("label",
					"Feature label");
			Cytoscape.getNodeAttributes().setUserVisible("label", true);
			Cytoscape.getNodeAttributes().setUserEditable("label", false);

			// Set region id
			Cytoscape.getNodeAttributes().setAttribute(feature_name,
					"region_id", featureAtts[1]);
			Cytoscape.getNodeAttributes().setAttributeDescription("region_id",
					"Region identifier");
			Cytoscape.getNodeAttributes().setUserVisible("region_id", false);
			Cytoscape.getNodeAttributes().setUserEditable("region_id", false);

			// Set affy probeset id
			Cytoscape.getNodeAttributes().setAttribute(feature_name,
					"Affy_probeset", featureAtts[2]);
			Cytoscape.getNodeAttributes().setAttributeDescription(
					"Affy_probeset", "Affymetrix probeset identifiers");
			Cytoscape.getNodeAttributes()
					.setUserVisible("Affy_probeset", false);
			Cytoscape.getNodeAttributes().setUserEditable("Affy_probeset",
					false);

			// Add to list of CyNodes
			features.add(feature);
			// Add to list of unique feature identifiers
			featureList.add(feature_name);

		}

		dummyNet = Cytoscape.getRootGraph().createNetwork(features, edges);
		dummyNet.setTitle(nodeId);
		// Cytoscape.getCurrentNetwork().appendNetwork(dummyNet);

		oldView = Cytoscape.getVisualMappingManager().getNetworkView();

		// background = Cytoscape.getVisualMappingManager().getVisualStyle()
		// .getGlobalAppearanceCalculator().getDefaultBackgroundColor();

		// this.setBackground(background);

	}

	/**
	 * Collect structure attributes from parent node, create Region objects,
	 * pass Region properties
	 */
	public void processStructure() {
		List<String> structureMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_structure");
		for (String structureList : structureMetaList) {

			// Split list of attributes per region
			String[] structureAtts = structureList.split(":");

			// Transform attribute data types
			String region_name = structureAtts[0] + "_" + structureAtts[1];
			int units = Integer.parseInt(structureAtts[3]);
			boolean constitutive = false;
			boolean start = false;
			if (structureAtts[4].equalsIgnoreCase("y")) {
				constitutive = true;
			}
			if (structureAtts[5].equalsIgnoreCase("y")) {
				start = true;
			}

			// Create Region with properties
			SpliceRegion region = new SpliceRegion(region_name, view,
					structureAtts[1], structureAtts[2], units, constitutive,
					start, structureAtts[6]);

			// Add Region to Canvas
			DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
			// DGraphView dview = (DGraphView) view;
			DingCanvas aLayer = dview
					.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
			aLayer.add(region);

			// hack
			dview.setZoom(dview.getZoom() * 0.99999999999999999d);

			// region.setId(structureAtts[1]);
			// region.setType(structureAtts[2]);
			// region.setUnits(units); // units wide
			// region.setConstitutive(constitutive);
			// region.setContainsStartSite(start);
			// region.setAnnotation(structureAtts[6]);
		}

	}

	/**
	 * Collect splicing attributes from parent node, draw splice events between
	 * regions
	 */
	public void processSplicing() {
		List<String> spliceMetaList = nodeAttribs.getListAttribute(nodeId,
				"sgv_splice");
		for (String spliceList : spliceMetaList) {

			// Split list of attributes per event
			String[] spliceAtts = spliceList.split(":");

			// Create Splice Events and set properties
			SpliceEvent event = new SpliceEvent(spliceAtts[0], spliceAtts[1],
					view);
			
			//Add Splice Events to canvas
			DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
			// DGraphView dview = (DGraphView) view;
			DingCanvas aLayer = dview
					.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
			aLayer.add(event);

			// hack
			dview.setZoom(dview.getZoom() * 0.99999999999999999d);


		}
	}

	/**
	 * 
	 */
	public Component getCanvas() {
		return canvas;
	}

	public static final int NODE_HEIGHT = SGVNodeAppearanceCalculator.FEATURE_NODE_HEIGHT;

	public static final int NODE_WIDTH = SGVNodeAppearanceCalculator.FEATURE_NODE_WIDTH;

	public static final int VGAP = NODE_HEIGHT / 2;

	public static final int BLOCK_HEIGHT = NODE_HEIGHT / 2;

	public static final int HGAP = NODE_WIDTH / 2;

	public static final int LABEL_TRACK_HEIGHT = NODE_HEIGHT;

	public static final int TITLE_LEGEND_HEIGHT = NODE_HEIGHT;

	int xOffset = HGAP;

	/**
	 * Create dummy network
	 */
	protected void createDummyNetworkView() {
		view = new SubgeneNetworkView(dummyNet, dummyNet.getTitle());
		view.setIdentifier(dummyNet.getIdentifier());
		// view.setTitle(dummyNet.getTitle());
		
		xOffset = HGAP;
		//fudge factor?!
		double initialOffset = -37.5 * features.size();
		
		for (Object f : features) {
			NodeView nv = view.getNodeView((CyNode) f);
			nv.setOffset(initialOffset + xOffset + HGAP / 2, VGAP);
			xOffset += HGAP + NODE_WIDTH;
		}

		this.setPanelWidth(xOffset);

		processStructure();

		processSplicing();

		applySGVVisualStyle();

		// test
		DingCanvas aLayer = ((DGraphView) view)
				.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
		System.out.println("subgeneviewer_fore: " + aLayer);
		DingCanvas bLayer = ((DGraphView) Cytoscape.getCurrentNetworkView())
				.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
		System.out.println("network_fore: " + bLayer);

		// renderView();
	}

	/**
	 * creates SGV visual style for feature nodes and applies it to the network
	 */
	public void applySGVVisualStyle() {
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		VisualStyle sgvStyle = catalog.getVisualStyle("SGV");
		if (sgvStyle == null) { // Create the SGV visual style
			try {
				sgvStyle = (VisualStyle) vmm.getVisualStyle().clone();
			} catch (CloneNotSupportedException e) {
				sgvStyle = new VisualStyle("SGV");
			}
			sgvStyle.setName("SGV");
			sgvStyle
					.setNodeAppearanceCalculator(new SGVNodeAppearanceCalculator());
			catalog.addVisualStyle(sgvStyle);
		}

		background = new Color(250, 240, 160);
		this.setBackground(background);

		// vmm.setNetworkView(view);
		vmm.setVisualStyle(sgvStyle);
		// view.setVisualStyle("SGV");
	}

	/**
	 * DOCUMENT ME!
	 */
	public void clean() {
		Cytoscape.destroyNetwork(dummyNet);
		Cytoscape.getVisualMappingManager().setNetworkView(oldView);
		dummyNet = null;
		canvas = null;
	}

	/**
	 * DOCUMENT ME!
	 */
	protected void updateView() {
		if (view != null) {
			Cytoscape.getVisualMappingManager().setNetworkView(view);

			final Dimension panelSize = this.getSize();
			((DGraphView) view).getCanvas().setSize(
					new Dimension((int) panelSize.getWidth() - PADDING,
							(int) panelSize.getHeight() - PADDING));
			// view.fitContent();
			canvas = (((DGraphView) view).getCanvas());
			// canvas = (view.getComponent());
			
			//Force LOD (!? doesn't work on sgv window!?)
			CytoscapeInit.getProperties().setProperty("render.nodeLabelThreshold", "200");

			// for (MouseListener listener : canvas.getMouseListeners())
			// canvas.removeMouseListener(listener);

			this.removeAll();
			this.add(canvas);

			canvas.setLocation(PADDING / 2, PADDING / 2);
			Cytoscape.getVisualMappingManager().applyAppearances();

			if ((background != null) && (canvas != null)) {
				canvas.setBackground(background);
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public GraphView getView() {
		return view;
	}

	/**
	 * @return the panelHeight
	 */
	public  int getPanelHeight() {
		return panelHeight;
	}

	/**
	 * @param panelHeight the panelHeight to set
	 */
	public void setPanelHeight(int panelHeight) {
		this.panelHeight = panelHeight;
	}

	/**
	 * @return the panelWidth
	 */
	public  int getPanelWidth() {
		return panelWidth;
	}

	/**
	 * @param panelWidth the panelWidth to set
	 */
	public  void setPanelWidth(int panelWidth) {
		this.panelWidth = panelWidth;
	}

}

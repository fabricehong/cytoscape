package cytoscape.visual.mappings.rangecalculators;

import cytoscape.visual.NodeShape;
import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.parsers.NodeShapeParser;
import cytoscape.visual.parsers.ValueParser;

public class NodeShapeRangeValueCalculator implements
		RangeValueCalculator<NodeShape> {

	private ValueParser parser;

	public NodeShapeRangeValueCalculator() {
		parser = new NodeShapeParser();
	}

	
	public NodeShape getRange(Object attrValue) {
		if (attrValue instanceof String) {
			NodeShape obj = (NodeShape) parser.parseStringValue((String) attrValue);
			return obj;
		}
		return null;
	}


	public boolean isCompatible(Class<?> type) {
		if(type.isAssignableFrom(NodeShape.class))
			return true;
		else
			return false;
	}

	
}

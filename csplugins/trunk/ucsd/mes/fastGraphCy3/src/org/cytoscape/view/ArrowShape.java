package org.cytoscape.view;

public enum ArrowShape {
	NONE("No Arrow"),
	DIAMOND("Diamond"),
	DELTA("Delta"),
	ARROW("Arrow"),
	T("T"),
	CIRCLE("Circle"),
	;

	private String name;
	private ArrowShape(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}
}

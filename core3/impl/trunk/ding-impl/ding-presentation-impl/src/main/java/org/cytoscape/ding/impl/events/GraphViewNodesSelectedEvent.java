package org.cytoscape.ding.impl.events;

/*
 * #%L
 * Cytoscape Ding View/Presentation Impl (ding-presentation-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;

import org.cytoscape.ding.GraphView;
import org.cytoscape.model.CyNode;

public final class GraphViewNodesSelectedEvent extends GraphViewChangeEventAdapter {
	private final static long serialVersionUID = 1202416512158746L;
	
	private final List<CyNode> selectedNodes;

	public GraphViewNodesSelectedEvent(final GraphView view, final List<CyNode> selectedNodeInx) {
		super(view);
		selectedNodes = selectedNodeInx;
	}

	
	@Override public final int getType() {
		return NODES_SELECTED_TYPE;
	}

	
	@Override public final CyNode[] getSelectedNodes() {
		final int selectedSize = selectedNodes.size();
		final CyNode[] returnThis = new CyNode[selectedSize];
		
		for (int i=0; i < selectedSize; i++)
			returnThis[i] = selectedNodes.get(i);

		return returnThis;
	}
}

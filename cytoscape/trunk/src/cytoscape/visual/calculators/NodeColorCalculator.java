//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.awt.Color;
import javax.swing.JPanel;

import y.base.Node;

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface NodeColorCalculator extends Calculator {
    
    Color calculateNodeColor(Node node, CyNetwork network);
}


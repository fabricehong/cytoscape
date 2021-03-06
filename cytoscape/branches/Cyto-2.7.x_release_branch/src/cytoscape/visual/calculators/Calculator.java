/*
 File: Calculator.java

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

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

//------------------------------------------------------------------------------
import giny.model.Edge;
import giny.model.Node;

import java.util.Properties;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;


/**
 */
public interface Calculator extends Cloneable {

	public static final String ID = "ID";

    /**
     * Get the UI for a calculator.
     *
     * @param parent
     *            Parent JDialog for the UI
     * @param network
     *            CyNetwork object containing underlying graph data
     */
    JPanel getUI(JDialog parent, CyNetwork network);

    /**
     * Gets calculator name.
     */
    public String toString();

    /**
     * Set calculator name. <b>DO NOT CALL THIS METHOD</b> unless you first get
     * a valid name from the CalculatorCatalog. Even if you have a guaranteed
     * valid name from the CalculatorCatalog, it is still preferrable to use the
     * renameCalculator method in the CalculatorCatalog.
     */
    public void setName(String newName);

    /**
     * Clone the calculator.
     */
    public Object clone()
        throws CloneNotSupportedException;

    /**
     * Get a description of this calculator as a Properties object.
     */
    public Properties getProperties();

    /**
     * Add a ChangeListener to the calcaultor. When the state underlying the
     * calculator changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay
     * consistent with the data held in the mappings.
     *
     * @param l
     *            ChangeListener to add
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Remove a ChangeListener from the calcaultor. When the state underlying
     * the calculator changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay
     * consistent with the data held in the mappings.
     *
     * @param l
     *            ChangeListener to add
     */
    public void removeChangeListener(ChangeListener l);

    /*
     * Use public VisualPropertyType getVisualPropertyType() instead.
     */

    /**
     *
     * This method replaces the three methods above.
     *
     * @return
     */
    public VisualPropertyType getVisualPropertyType();

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param e DOCUMENT ME!
     * @param net DOCUMENT ME!
     */
    public void apply(Appearance appr, Edge e, CyNetwork net);

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param n DOCUMENT ME!
     * @param net DOCUMENT ME!
     */
    public void apply(Appearance appr, Node n, CyNetwork net);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector<ObjectMapping> getMappings();

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ObjectMapping getMapping(int i);
}

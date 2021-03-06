/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.visual.properties;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.parsers.FontParser;

import cytoscape.visual.ui.icon.LineTypeIcon;

import giny.view.EdgeView;
import giny.view.Label;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import java.util.Properties;

import javax.swing.Icon;


/**
 *
 */
public class EdgeFontFaceProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.EDGE_FONT_FACE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(final Object value) {
		return new LineTypeIcon() {
				public void paintIcon(Component c, Graphics g, int x, int y) {
					super.setColor(new Color(10, 10, 10, 0));
					super.paintIcon(c, g, x, y);
					g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
					g2d.setColor(Color.DARK_GRAY);

					final Font font = (Font) value;
					g2d.setFont(new Font(font.getFontName(), font.getStyle(), 40));
					g2d.setColor(new Color(10, 10, 10, 40));
					g2d.drawString("Font", c.getX() + 15, c.getY() - 10);
					g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
				}
			};
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ev DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToEdgeView(EdgeView ev, Object o) {
		if ((o == null) || (ev == null))
			return;

		Label nodelabel = ev.getLabel();

		if (!((Font) o).equals(nodelabel.getFont()))
			nodelabel.setFont((Font) o);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param props DOCUMENT ME!
	 * @param baseKey DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object parseProperty(Properties props, String baseKey) {
		String s = props.getProperty(VisualPropertyType.EDGE_FONT_FACE.getDefaultPropertyKey(baseKey));

		if (s != null)
			return (new FontParser()).parseFont(s);
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return new Font("SanSerif", Font.PLAIN, 10);
	}
}

/*
  File: CyColorChooser.java

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
package org.cytoscape.util.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This is an annoying re-implementation of JColorChooser.showDialog() that remembers
 * recently used colors between invocations of the chooser dialog.
 *
 * @CyAPI.Static.Class 
 */
public final class CyColorChooser {
	
	private static JColorChooser chooser = new JColorChooser();
	private static ColorListener listener = new ColorListener();
	
	private static Color color;

	private CyColorChooser() {}

	/**
	 * Display custom color chooser dialog.
	 * 
	 * @param parent parent component of this dialog
	 * @param title Title of this dialog
	 * @param initialColor Initially selected color.
	 * 
	 * @return New Color or null if canceled.
	 */
	public static Color showDialog(final Component parent, final String title, final Color initialColor) {
		if (initialColor != null)
			chooser.setColor(initialColor);

		final JDialog dialog = JColorChooser.createDialog(parent, title, true, chooser, listener, null);
		dialog.setVisible(true);

		return color;
	}

	private static final class ColorListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			color = chooser.getColor();
		}
	}
}

/*
  File: CheckBoxFileChooser.java

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

package cytoscape.actions;

import java.awt.*;
import java.awt.event.*;

import java.io.File;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;


/** 
 * extends JFileChooser by adding a JCheckBox at the bottom of the chooser
 * @deprecated This isn't apparently used anywhere. If you are using it,
 * let us know! If you need a file use FileUtil.getFile().  Will be
 * removed 5/2008.
 */
@Deprecated
public class CheckBoxFileChooser extends JFileChooser {
	JCheckBox jcb;
	boolean jcbFlag = false;
	String jcbLabel;

	/**
	 * Creates a new CheckBoxFileChooser object.
	 *
	 * @param currentDirectory  DOCUMENT ME!
	 * @param checkBoxLabel  DOCUMENT ME!
	 */
	public CheckBoxFileChooser(File currentDirectory, String checkBoxLabel) {
		super(currentDirectory, (FileSystemView) null);
		jcbLabel = checkBoxLabel;
	}

	protected JDialog createDialog(Component parent) throws HeadlessException {
		JDialog jd = super.createDialog(parent);
		jcb = new JCheckBox(jcbLabel);
		jcb.setSelected(jcbFlag);
		jcb.addItemListener(new CheckBoxListener());

		Container contentPane = jd.getContentPane();
		contentPane.add(jcb, BorderLayout.SOUTH);
		jd.pack();

		return jd;
	}

	/** inner class for listening to the JCheckBox jcb
	 *  and updating the boolean jcbFlag when appropriate
	 */
	private class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				jcbFlag = true;
			else if (e.getStateChange() == ItemEvent.DESELECTED)
				jcbFlag = false;
		}
	}

	/** method for accessing last state of JCheckBox jcb */
	public boolean getCheckBoxState() {
		return jcbFlag;
	}
}

/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package groupTool.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.WindowConstants.*;
import javax.swing.border.*;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.event.*;

import cytoscape.groups.*;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

/**
 * The CreateByAttributeDialog is the dialog displayed to the user to 
 * allow them to select an attribute to use for group creation. 
 */
public class CreateByAttributeDialog extends JDialog 
                                     implements ActionListener {

	// Dialog components
	private JLabel titleLabel;
	private JComboBox attributeSelector;
	private JComboBox viewerSelector;
	private JTextField groupCount;
	private JPanel buttonBox;
	private JButton createButton;
	private JButton cancelButton;
	private CyAttributes nodeAttributes;

	// Models
	private GroupTableModel tableModel;

	/**
	 * Create a GroupToolDialog
	 *
	 * @param parent the Frame acting as the parent of this Dialog
	 */
	public CreateByAttributeDialog (Frame parent) {
		super(parent, false);
		initComponents();
	}

	/**
	 * Initialize all of the graphical components of the dialog
	 */
	private void initComponents() {
		this.setTitle("Create Groups By Attributes");
		System.out.println("initComponents");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create a panel for the main content
		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
		dataPanel.setLayout(layout);

		// Get the list of atributes
		nodeAttributes = Cytoscape.getNodeAttributes();
	
		// Create the Attribute list
		JPanel attributePanel = new JPanel();
		attributeSelector = new JComboBox();

		// Add them to the combo box
		String nodeNames[] = nodeAttributes.getAttributeNames();
		attributeSelector.addItem("Select attribute to group by");
		for (int i = 0; i < nodeNames.length; i++) {
			if (nodeAttributes.getType(nodeNames[i]) == CyAttributes.TYPE_STRING) {
				attributeSelector.addItem(nodeNames[i]);
			}
		}


		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder attributeBorder = BorderFactory.createTitledBorder(selBorder, "Select Attribute");
		attributeBorder.setTitlePosition(TitledBorder.LEFT);
		attributeBorder.setTitlePosition(TitledBorder.TOP);
		attributePanel.setBorder(attributeBorder);
		attributePanel.add(attributeSelector);
		dataPanel.add(attributePanel);

		// Create the Viewer list
		JPanel viewerPanel = new JPanel();
		viewerSelector = new JComboBox();

		// Get the list of viewers
		Collection<CyGroupViewer>viewers = CyGroupManager.getGroupViewers();
		viewerSelector.addItem("Select viewer for groups");
		// Add them to the combo box
		Iterator<CyGroupViewer>viewIter = viewers.iterator();
		while (viewIter.hasNext()) {
			CyGroupViewer viewer = viewIter.next();
			viewerSelector.addItem(viewer.getViewerName());
		}

		TitledBorder viewerBorder = BorderFactory.createTitledBorder(selBorder, "Select Viewer");
		viewerBorder.setTitlePosition(TitledBorder.LEFT);
		viewerBorder.setTitlePosition(TitledBorder.TOP);
		viewerPanel.setBorder(viewerBorder);
		viewerPanel.add(viewerSelector);
		dataPanel.add(viewerPanel);

		// Create the button box
		JPanel buttonBox = new JPanel();
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);

		createButton = new JButton("Create Groups");
		createButton.setActionCommand("create");
		createButton.setEnabled(true);
		createButton.addActionListener(this);

		buttonBox.add(createButton);
		buttonBox.add(cancelButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		dataPanel.add(buttonBox);
		setContentPane(dataPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if ("cancel".equals(e.getActionCommand())) {
			this.dispose();
		} else if ("create".equals(e.getActionCommand())) {
			String viewer = (String)viewerSelector.getSelectedItem();
			String attribute = (String)attributeSelector.getSelectedItem();
			// Did we select everything?
			if (viewer.startsWith("Select ") || attribute.startsWith("Select ")) {
				return;
			}
			// Yes, see if we have any matches
			CyNetwork network = Cytoscape.getCurrentNetwork();
			HashMap<String,List<CyNode>>nodeMap = new HashMap();
			Iterator<CyNode> nodeIter = network.nodesIterator();
			while (nodeIter.hasNext()) {
				// Get the node
				CyNode node = nodeIter.next();
				// Get the attribute value (if any)
				String key = nodeAttributes.getStringAttribute(node.getIdentifier(), attribute);
				if (key == null) continue;
				if (!nodeMap.containsKey(key))
					nodeMap.put(key, new ArrayList());
				nodeMap.get(key).add(node);
			}

			Set<String>groupNames = nodeMap.keySet();
			Iterator<String> groupIter = groupNames.iterator();
			while (groupIter.hasNext()) {
				String groupName = groupIter.next();
				List<CyNode>nodeList = nodeMap.get(groupName);
				CyGroup group = CyGroupManager.createGroup(groupName, nodeList, viewer);
				CyGroupViewer groupViewer = CyGroupManager.getGroupViewer(viewer);
				groupViewer.groupCreated(group);
			}
			this.dispose();
		}
	}
}

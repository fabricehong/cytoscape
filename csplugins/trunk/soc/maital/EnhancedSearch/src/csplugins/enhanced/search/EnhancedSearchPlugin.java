/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.enhanced.search;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeToolBar;

public class EnhancedSearchPlugin extends CytoscapePlugin implements
		PropertyChangeListener {

	private EnhancedSearchPanel enhancedSearchToolBar;

	public EnhancedSearchPlugin() {
		initListeners();
		initToolBar();
	}

	/**
	 * Initialize all Cytoscape listeners.
	 */
	private void initListeners() {
		// to catch network create/destroy events
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);

		// to catch network modified events
		NetworkModifiedListener networkModifiedListener = new NetworkModifiedListener();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(
				networkModifiedListener);
	}

	/**
	 * Initialize the Tool Bar.
	 */
	private void initToolBar() {

		CytoscapeToolBar cytoscapeToolBar = Cytoscape.getDesktop().getCyMenus()
				.getToolBar();
		enhancedSearchToolBar = new EnhancedSearchPanel();

		cytoscapeToolBar.add(enhancedSearchToolBar);
		cytoscapeToolBar.validate();

	}

	/**
	 * Property change listener - to get network create/destroy events.
	 * 
	 * @param event PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent event) {
		final EnhancedSearch enhancedSearch = EnhancedSearchFactory
				.getGlobalEnhancedSearchInstance();

		if (event.getPropertyName() != null) {
			if (event.getPropertyName().equals(Cytoscape.NETWORK_CREATED)) {
				enhancedSearchToolBar.enableAllEnhancedSearchButtons();
			} else if (event.getPropertyName().equals(
					Cytoscape.NETWORK_DESTROYED)) {
				String networkID = event.getNewValue().toString();
				CyNetwork cyNetwork = Cytoscape.getNetwork(networkID);
				enhancedSearch.removeNetworkIndex(cyNetwork);

				// If there was only one network, and it was destroyed, disable Enhanced Search
				Set networkSet = Cytoscape.getNetworkSet();
				if (networkSet.size() == 1) {
					enhancedSearchToolBar.disableAllEnhancedSearchButtons();
				}
			}
		}
	}

	/**
	 * Determine if any network is loaded. If not, disable enhanced search.
	 * EnhancedSearch is intentionally enabled on networks with no view, to allow
	 * search on large networks without the need to create a view.
	 */
	private void isNetworkLoaded(EnhancedSearch enhancedSearch) {
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

		if (currentNetwork != null) {
			enhancedSearchToolBar.enableAllEnhancedSearchButtons();
		} else {
			enhancedSearchToolBar.disableAllEnhancedSearchButtons();
		}
	}

}

class NetworkModifiedListener implements PropertyChangeListener {

	/**
	 * Property change listener - to get network modified events.
	 * 
	 * @param event
	 *            PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent event) {
		final EnhancedSearch enhancedSearch = EnhancedSearchFactory
				.getGlobalEnhancedSearchInstance();

		if (event.getPropertyName() != null) {
			if (event.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {

				// Mark that re-indexing of the network is needed.
				// In future versions: update the network's index.
				final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
				enhancedSearch.setNetworkIndexStatus(cyNetwork,
						EnhancedSearch.REINDEX);
			}
		}
	}

}

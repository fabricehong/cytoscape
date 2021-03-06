/*
 File: NewNetworkSelectedNodesEdgesTaskFactory.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.task.internal.creation;  


import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskIterator;


public class NewNetworkSelectedNodesEdgesTaskFactory extends AbstractNetworkTaskFactory {

    private CyNetworkManager netmgr;
    private final CyNetworkViewManager networkViewManager;
    private CyRootNetworkFactory crnf;
    private CyNetworkViewFactory cnvf;
    private CyNetworkNaming naming;
    private VisualMappingManager vmm;
    private final RenderingEngineManager reMgr;

    public NewNetworkSelectedNodesEdgesTaskFactory(CyRootNetworkFactory crnf,
                              CyNetworkViewFactory cnvf, CyNetworkManager netmgr,
                              final CyNetworkViewManager networkViewManager,
                              CyNetworkNaming naming, VisualMappingManager vmm,
                              final RenderingEngineManager reMgr)
    {
        this.netmgr = netmgr;
        this.networkViewManager = networkViewManager;
        this.crnf = crnf;
        this.cnvf = cnvf;
        this.naming = naming;
        this.vmm = vmm;
		this.reMgr = reMgr;
    }

    public TaskIterator getTaskIterator() {
        return new TaskIterator(new NewNetworkSelectedNodesEdgesTask(net,crnf, cnvf, netmgr, networkViewManager, naming, vmm, reMgr));
    }

}

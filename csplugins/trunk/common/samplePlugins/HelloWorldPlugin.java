package csplugins.common.samplePlugins;

import javax.swing.JOptionPane;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

public class HelloWorldPlugin extends CytoscapePlugin {

    public HelloWorldPlugin() {
        String message = "Hello, world!";
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
    }
}


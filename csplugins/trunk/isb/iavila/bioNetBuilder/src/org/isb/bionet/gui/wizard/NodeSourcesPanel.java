/**
 *
 */
package org.isb.bionet.gui.wizard;

import java.awt.event.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import java.io.File;

import org.isb.bionet.gui.*;

public class NodeSourcesPanel extends JPanel {

    protected File myListFile;
    
    /**
     *  Creates a panel with node sources
     */
    public NodeSourcesPanel (){
        create();
    }
    
    /**
     * @return if a file has been selected, it returns it, returns null otherwise
     */
    public File getMyListFile (){
        return this.myListFile;
    }
    
    /**
     * Creates the panel
     */
    protected void create() {
        
        final JButton annotsButton = new JButton("Nodes with selected annotations...");
        annotsButton.setEnabled(false);
        final JButton listButton = new JButton("Nodes from my list...");
        final JFileChooser fileChooser = new JFileChooser();
        listButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        int returnVal = fileChooser.showOpenDialog(NodeSourcesPanel.this);
                        if(returnVal == JFileChooser.APPROVE_OPTION) {
                            myListFile = fileChooser.getSelectedFile();
                        }
                    }//actionPerformed
                    
                }//AbstractAction
        );
        listButton.setEnabled(false);
        final JButton netsButton  =  new JButton("Nodes from loaded networks...");
        netsButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent event){
                        CyNetworksDialog.showDialog(NodeSourcesPanel.this);
                    }//actionPerformed
                }
        );
        netsButton.setEnabled(false);
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagLayout gridbag = new GridBagLayout();
        this.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.ipadx = 5;
        Component emptyBox = Box.createHorizontalGlue();
        gridbag.setConstraints(emptyBox, c);
        this.add(emptyBox);

        JLabel use = new JLabel("Node Source");
        gridbag.setConstraints(use, c);
        this.add(use);

        c.gridwidth = GridBagConstraints.REMAINDER; // end row

        JLabel stats = new JLabel("Num Nodes");
        gridbag.setConstraints(stats, c);
        this.add(stats);

        c.gridwidth = 1; // reset to the default

        JCheckBox useAnnotations = new JCheckBox();
        useAnnotations.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        annotsButton.setEnabled(source.isSelected());
                    }
                }
        );
        useAnnotations.setSelected(false);
        gridbag.setConstraints(useAnnotations, c);
        this.add(useAnnotations);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(annotsButton, c);
        this.add(annotsButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        JTextField annotsNodes = new JTextField(4);
        annotsNodes.setEditable(false);
        gridbag.setConstraints(annotsNodes, c);
        this.add(annotsNodes);

        c.gridwidth = 1;

        c.fill = GridBagConstraints.NONE;
        JCheckBox useList = new JCheckBox();
        useList.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        listButton.setEnabled(source.isSelected());
                    }
                }
        );
        useList.setSelected(false);
        gridbag.setConstraints(useList, c);
        this.add(useList);

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        gridbag.setConstraints(listButton, c);
        this.add(listButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        JTextField listNodes = new JTextField(4);
        listNodes.setEditable(false);
        gridbag.setConstraints(listNodes, c);
        this.add(listNodes);

        c.gridwidth = 1;

        c.fill = GridBagConstraints.NONE;
        JCheckBox useNets = new JCheckBox();
        useNets.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        netsButton.setEnabled(source.isSelected());
                    }
                }
        );
        useNets.setSelected(false);
        gridbag.setConstraints(useNets, c);
        this.add(useNets);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(netsButton, c);
        this.add(netsButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        JTextField netsNodes = new JTextField(4);
        netsNodes.setEditable(false);
        gridbag.setConstraints(netsNodes, c);
        this.add(netsNodes);
    }
}
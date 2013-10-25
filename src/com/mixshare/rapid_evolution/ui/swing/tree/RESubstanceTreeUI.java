package com.mixshare.rapid_evolution.ui.swing.tree;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.jvnet.substance.SubstanceTreeUI;
import javax.swing.plaf.basic.BasicTreeUI;

public class RESubstanceTreeUI extends SubstanceTreeUI {
    
    private static Logger log = Logger.getLogger(RESubstanceTreeUI.class);
    
    public RESubstanceTreeUI() {        
        super();
        if (log.isDebugEnabled()) log.debug("RESubstanceTreeUI(): being called!");
    }

    protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
            Rectangle bounds, TreePath path, int row, boolean isExpanded,
            boolean hasBeenExpanded, boolean isLeaf) {
        
    }
}
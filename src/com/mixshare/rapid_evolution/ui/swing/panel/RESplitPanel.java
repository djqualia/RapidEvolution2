package com.mixshare.rapid_evolution.ui.swing.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

import rapid_evolution.ui.SkinManager;

import org.apache.log4j.Logger;

public class RESplitPanel extends JSplitPane implements PropertyChangeListener{

    private static Logger log = Logger.getLogger(RESplitPanel.class);
    
    public RESplitPanel() {
        super();
        addPropertyChangeListener(this);
        setOpaque(false);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (log.isTraceEnabled()) log.trace("propertyChange(): evt=" + evt);
        // this was added to fix some stupid swing repaint bug
        SkinManager.instance.getFrame("main_frame").repaint();
    }
    
}

package com.mixshare.rapid_evolution.ui.swing.tooltip;

import java.awt.Component;
import java.awt.Container;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

import org.apache.log4j.Logger;

import rapid_evolution.ui.RapidEvolutionUI;

public class ToolTipRemover {
        
    private static Logger log = Logger.getLogger(ToolTipRemover.class);
    
    static public void removeToolTips(JComponent parentComponent) {
        if (log.isTraceEnabled())
            log.trace("removeToolTips(): parentComponent=" + parentComponent);
        ToolTipManager.sharedInstance().unregisterComponent(parentComponent);
        Stack s = new Stack();
        s.push(parentComponent);
        while (!s.isEmpty()) {
            Component c = (Component) s.pop();
            if (c instanceof Container) {
                Container d = (Container) c;
                for (int i = 0; i < d.getComponentCount(); i++) {
                    if (d.getComponent(i) instanceof JComponent) {
                        ToolTipManager.sharedInstance().unregisterComponent((JComponent)d.getComponent(i));
                    }
                    s.push(d.getComponent(i));
                }
            }
        } 
    }    
    
}

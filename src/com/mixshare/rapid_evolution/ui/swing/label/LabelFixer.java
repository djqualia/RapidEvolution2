package com.mixshare.rapid_evolution.ui.swing.label;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import rapid_evolution.ui.SkinManager;

public class LabelFixer {
    
    public LabelFixer(JComponent component) {
        Color color = (Color)SkinManager.instance.colormap.get("slider_label_foreground");
        if (color == null) color = component.getForeground();
        JLabel[] labels = getLabels(component);
        for (int l = 0; l < labels.length; ++l) {
            labels[l].setForeground(Color.red);
        }
    }
    
    protected JLabel[] getLabels(JComponent parentComponent) {
        Vector v = new Vector();
        Stack s = new Stack();
        s.push(parentComponent);
        while (!s.isEmpty()) {
            Component c = (Component) s.pop();
            if (c instanceof Container) {
                Container d = (Container) c;
                for (int i = 0; i < d.getComponentCount(); i++) {
                    if (d.getComponent(i) instanceof JLabel)
                        v.add(d.getComponent(i));
                    else
                        s.push(d.getComponent(i));
                }
            }
        } 
        JLabel[] arr = new JLabel[v.size()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = (JLabel) v.get(i);
        return arr;
    }    
    
}

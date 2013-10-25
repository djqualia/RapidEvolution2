package com.mixshare.rapid_evolution.ui.swing.slider;

import java.awt.Component;
import java.awt.Container;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;

public class SliderFixer {

    public SliderFixer(JComponent component) {
        Component[] components = getComponents(component);
        for (int c = 0; c < components.length; ++c) {
            components[c].setFocusable(false);
        }
    }
    
    protected Component[] getComponents(JComponent parentComponent) {
        Vector v = new Vector();
        Stack s = new Stack();
        s.push(parentComponent);
        while (!s.isEmpty()) {
            Component c = (Component) s.pop();
            if (c instanceof Container) {
                Container d = (Container) c;
                for (int i = 0; i < d.getComponentCount(); i++) {
                    if (d.getComponent(i) instanceof Component)
                        v.add(d.getComponent(i));
                    else
                        s.push(d.getComponent(i));
                }
            }
        } 
        Component[] arr = new Component[v.size()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = (JComponent) v.get(i);
        return arr;
    }    
    
}

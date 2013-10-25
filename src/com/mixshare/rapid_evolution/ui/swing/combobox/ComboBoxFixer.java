package com.mixshare.rapid_evolution.ui.swing.combobox;

import java.awt.Component;
import java.awt.Container;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JComponent;

public class ComboBoxFixer {
    
    public ComboBoxFixer(JComponent component) {
        JComponent[] components = getJComponents(component);
        for (int b = 0; b < components.length; ++b) {
            JComponent subComponent = components[b];
            subComponent.setBackground(component.getBackground());
            subComponent.setForeground(component.getForeground());
        }
    }
    
    protected JComponent[] getJComponents(JComponent parentComponent) {
        Vector v = new Vector();
        Stack s = new Stack();
        s.push(parentComponent);
        while (!s.isEmpty()) {
            Component c = (Component) s.pop();
            if (c instanceof Container) {
                Container d = (Container) c;
                for (int i = 0; i < d.getComponentCount(); i++) {
                    if (d.getComponent(i) instanceof JComponent)
                        v.add(d.getComponent(i));
                    else
                        s.push(d.getComponent(i));
                }
            }
        } 
        JComponent[] arr = new JComponent[v.size()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = (JComponent) v.get(i);
        return arr;
    }    
    
}

package com.mixshare.rapid_evolution.ui.swing.button;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import rapid_evolution.ui.SkinManager;

public class ButtonFixer implements ActionListener {

    private Map buttomMap = new HashMap();    
    
    public ButtonFixer(JComponent component) {        
        if ((REButton.getCurrentButtonType() == REButton.BUTTON_TYPE_LIQUID) && SkinManager.instance.use_button_fixer) {
            JButton[] button = getButtons(component);
            for (int b = 0; b < button.length; ++b) {
                JButton oldButton = button[b];
                JButton newButton = new REButton(oldButton.getText());
                newButton.addActionListener(this);
                buttomMap.put(newButton, oldButton);
                replace(button[b], newButton);
            }
        }
        JButton[] button = getButtons(component);
        for (int b = 0; b < button.length; ++b) {
            JButton oldbutton = button[b];
            if (oldbutton.getText().equalsIgnoreCase("yes"))
                oldbutton.setText(SkinManager.instance.getString("YES_TEXT"));
            if (oldbutton.getText().equalsIgnoreCase("no"))
                oldbutton.setText(SkinManager.instance.getString("NO_TEXT"));
            if (oldbutton.getText().equalsIgnoreCase("ok"))
                oldbutton.setText(SkinManager.instance.getString("OK_TEXT"));
            if (oldbutton.getText().equalsIgnoreCase("cancel"))
                oldbutton.setText(SkinManager.instance.getString("CANCEL_TEXT"));            
        }
    }
    
    protected JButton[] getButtons(JComponent parentComponent) {
        Vector v = new Vector();
        Stack s = new Stack();
        s.push(parentComponent);
        while (!s.isEmpty()) {
            Component c = (Component) s.pop();
            if (c instanceof Container) {
                Container d = (Container) c;
                for (int i = 0; i < d.getComponentCount(); i++) {
                    if (d.getComponent(i) instanceof JButton)
                        v.add(d.getComponent(i));
                    else
                        s.push(d.getComponent(i));
                }
            }
        } 
        JButton[] arr = new JButton[v.size()];
        for (int i = 0; i < arr.length; i++)
            arr[i] = (JButton) v.get(i);
        return arr;
    }    
    
    private static void replace(JButton oldButton, JButton newButton) {
        int index = getIndex(oldButton);
        Container c = oldButton.getParent();
        c.remove(index);
        c.add(newButton, index); 
        newButton.setPreferredSize(oldButton.getPreferredSize());
        newButton.setMinimumSize(oldButton.getMinimumSize());
        newButton.setMaximumSize(oldButton.getMaximumSize());
    }
 
    private static int getIndex(Component c) {
        Container p = c.getParent();
        for (int i = 0; i < p.getComponentCount(); i++) {
            if (p.getComponent(i) == c)
                return i;
        }
        return -1;
    }
    
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        JButton oldButton = (JButton)buttomMap.get(src);
        if (oldButton != null) {
            ActionListener[] listeners = oldButton.getActionListeners();
            if (listeners != null) {
                evt.setSource(oldButton);
                for (int l = 0; l < listeners.length; ++l) {
                    listeners[l].actionPerformed(evt);
                }
            }
        }        
    }    
}

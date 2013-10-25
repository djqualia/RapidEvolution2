package com.mixshare.rapid_evolution.ui.swing.button;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.SearchPane;
import java.util.*;

import rapid_evolution.ui.OptionsUI;

public class SearchHistoryButton extends JButton {

    private static int limit = 15;
    
    private JPopupMenu popupMenu;
    private LinkedList prevSearches  = new LinkedList();
    
    public LinkedList getPreviousSearches() { return prevSearches; }
    
    public SearchHistoryButton() {
        addMouseListener (new MouseAdapter() {
            public void mousePressed (MouseEvent me) {
                popMenu (me.getX(), me.getY());
            }
        });        
        
    }
    
    private JToolTip tooltip;  
    public JToolTip createToolTip() {
        if (SkinManager.instance.use_custom_tooltips) {
            if (tooltip == null) {
                tooltip = new CustomToolTip();
                tooltip.setComponent(this);            
            }
            return tooltip;
        } else {
            return super.createToolTip();
        }
    }    
    
    public void popMenu (int x, int y) {
        if (prevSearches.size() > 0) {
            popupMenu = new JPopupMenu();
            Iterator it = prevSearches.iterator();
            while (it.hasNext())
                popupMenu.add (new PrevSearchAction(it.next().toString()));
            popupMenu.show (this, x, y);
        }
    }
    
    public void addSearchText(String text) {
        if ((text == null) || text.equals("")) return;
        Iterator iter = prevSearches.iterator();
        while (iter.hasNext()) {
            String previousText = iter.next().toString();
            if (text.equalsIgnoreCase(previousText)) {
                iter.remove();
            }
        }
        prevSearches.add(0, text);
        if (prevSearches.size() > limit)
            prevSearches.remove(limit);
    }
        
    public class PrevSearchAction extends AbstractAction {
        String term;
        public PrevSearchAction (String s) {
            term = s;
            putValue (Action.NAME, term);
        }
        public String toString() { return term; }
        public void actionPerformed (ActionEvent e) {
            SearchPane.instance.searchfield.setText(term);

            if (!OptionsUI.instance.automaticsearchonuserinput.isSelected()) {
                RapidEvolutionUI.instance.findsearched = true;
                RapidEvolutionUI.instance.bpmsearched = false;
                RapidEvolutionUI.instance.keysearched = false;
                RapidEvolutionUI.instance.Search();
            }
            
            // don't need this - setText fires a DocumentEvent
            // that FilterField handles
            // ((FilterModel)getModel()).refilter();
        }        
    }
    
    
    
}

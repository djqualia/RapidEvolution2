package com.mixshare.rapid_evolution.ui.swing.popupmenu;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

import com.mixshare.rapid_evolution.ui.swing.border.ShadowBorder;

public class CustomPopupMenuUI extends BasicPopupMenuUI {

    static public ComponentUI createUI(JComponent c) {
        return new CustomPopupMenuUI();
    }
    
    public Popup getPopup(JPopupMenu popup, int x, int y) {
        Popup pp = super.getPopup(popup, x, y);
        JPanel panel = (JPanel)popup.getParent();
        panel.setBorder(new ShadowBorder(3, 3));
        panel.setOpaque(false);
        return pp;
    }
    
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D)g;
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g2, c);
    }
    
}

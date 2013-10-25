package com.mixshare.rapid_evolution.ui.swing.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

public class MoreInfoPanel extends JPanel {

    private static Logger log = Logger.getLogger(MoreInfoPanel.class);
    
    public Component topComponent;
    protected SpinWidget spinWidget;
    public Component bottomComponent;

    public static final int SPIN_WIDGET_HEIGHT = 14;
    
    private int BOX_AXIS = BoxLayout.X_AXIS;

    public MoreInfoPanel() {
        super();
        init();
    }
    
    public MoreInfoPanel (Component tc, Component mic) {
        super();
        init();
        topComponent = tc;
        bottomComponent = mic;
        doMyLayout();
    }
    
    private void init() {
        spinWidget = new SpinWidget();
        setOpaque(false);
    }

    public void setTopComponent(Component topComponent) {
        this.topComponent = topComponent;        
    }
    
    public void setBoxLayoutAxis(int axis) {
        BOX_AXIS = axis;
    }
    
    public void setSpinColor(Color color) {
        spinWidget.setColor(color);
    }
    
    public void setSpinColorHovering(Color color) {
        spinWidget.setHoverColor(color);
    }
    
    public void setBottomComponent(Component bottomComponent) {
        this.bottomComponent = bottomComponent;
        doMyLayout();
    }
    
    public void removeBottomComponent(Component bottomComponent) {
        this.bottomComponent = null;
    }

    protected void doMyLayout() {
        setLayout (new BoxLayout (this, BOX_AXIS));
        if (topComponent != null) add (topComponent);
        add (spinWidget);
        if (bottomComponent != null) add (bottomComponent);
        resetBottomVisibility();
    }

    protected void resetBottomVisibility() {
        if ((bottomComponent == null) ||
            (spinWidget == null))
            return;
        bottomComponent.setVisible (spinWidget.isOpen());
        revalidate();
        if (isShowing()) {
            Container ancestor = getTopLevelAncestor();
//            if ((ancestor != null) && (ancestor instanceof Window))
//                ((Window) ancestor).pack();
            repaint();
        }
    }

    public void showBottom (boolean b) {
        spinWidget.setOpen (b);
    }

    public boolean isBottomShowing () {
        return spinWidget.isOpen();
    }    
    
    
    public class SpinWidget extends JPanel {
        boolean open;
        boolean hovering = false;
        Color color = null;
        Color hoverColor = null;
        Dimension mySize = new Dimension (SPIN_WIDGET_HEIGHT,
                                          SPIN_WIDGET_HEIGHT);
        final int HALF_HEIGHT = SPIN_WIDGET_HEIGHT / 2;        
        int[] openXPoints =
            { 1, HALF_HEIGHT, SPIN_WIDGET_HEIGHT-1};
        int[] openYPoints =
            { HALF_HEIGHT - (SPIN_WIDGET_HEIGHT - 1 - HALF_HEIGHT) / 2, SPIN_WIDGET_HEIGHT-1 - (SPIN_WIDGET_HEIGHT - 1 - HALF_HEIGHT) / 2, HALF_HEIGHT - (SPIN_WIDGET_HEIGHT - 1 - HALF_HEIGHT) / 2};
        int[] closedXPoints =
            { 1, 1, HALF_HEIGHT};
        int[] closedYPoints =
            { 1, SPIN_WIDGET_HEIGHT-1, HALF_HEIGHT };
        Polygon openTriangle = 
            new Polygon (openXPoints, openYPoints, 3);
        Polygon closedTriangle = 
            new Polygon (closedXPoints, closedYPoints, 3);

        public SpinWidget() {
            setOpen (false);
            addMouseListener (new MouseAdapter() {
                    public void mouseClicked (MouseEvent e) {
                        handleClick();
                    }
                    public void mouseEntered(MouseEvent e) {
                        hovering = true;
                        repaint();
                    }
                    public void mouseExited(MouseEvent e) {
                        hovering = false;
                        repaint();
                    }
                    
                });
            setOpaque(false);
        }
        
        public void handleClick() {
            setOpen (! isOpen());
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen (boolean o) {
            open = o;
            resetBottomVisibility();
        }
        
        public void setColor(Color color) {
            this.color = color;
        }
        
        public void setHoverColor(Color color) {
            this.hoverColor = color;
        }

        public Dimension getMinimumSize() { return mySize; }
        public Dimension getPreferredSize() { return mySize; }

        // don't override update(), get the default clear
        public void paint (Graphics g) {
            //if (log.isTraceEnabled())
                //log.trace("paint(): hovering=" + hovering);
            Graphics2D g2 = (Graphics2D)g;
            if (rapid_evolution.RapidEvolution.aaEnabled)
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = this.color;
            if (color == null)
                color = SkinManager.instance.getColor("moreinfo_spin_color");
            Color hoverColor = this.hoverColor;
            if (hoverColor == null)
                hoverColor = SkinManager.instance.getColor("moreinfo_spin_hovering_color");;
            g2.setColor(hovering ? hoverColor : color);
            if (isOpen())
                g2.fillPolygon (openTriangle);
            else
                g2.fillPolygon (closedTriangle);
        }

    }

}

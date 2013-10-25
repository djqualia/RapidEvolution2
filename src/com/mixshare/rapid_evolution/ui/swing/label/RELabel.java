package com.mixshare.rapid_evolution.ui.swing.label;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JToolTip;

import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

public class RELabel extends RichJLabel {

    static private int default_tracking = 0;
    
    public RELabel() {
        super(default_tracking);
        init();
    }
    
    public RELabel(String text) {
        super(text, default_tracking);
        init();
    }
    
    public RELabel(Icon image) {
        super(image);
        init();
    }
    
    private void init() {
//        setLeftShadow(1, 1, Color.white);
//        setRightShadow(2, 3, Color.black);
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
    
}

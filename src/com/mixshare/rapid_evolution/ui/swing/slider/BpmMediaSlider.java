package com.mixshare.rapid_evolution.ui.swing.slider;

import javax.swing.JSlider;

public class BpmMediaSlider extends MediaSlider {
    
    public BpmMediaSlider() {
        super(JSlider.VERTICAL, -800, 800, 0);
    }
    
    public String toString() {
        StringBuffer value = new StringBuffer("");
        float shift = ((float)-getValue()) / 100.0f;
        if (shift > 0) value.append("+");
        value.append(String.valueOf(shift));
        value.append("%");
        return value.toString();
    }

}

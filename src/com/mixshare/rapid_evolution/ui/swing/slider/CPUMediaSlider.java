package com.mixshare.rapid_evolution.ui.swing.slider;

import javax.swing.JSlider;

public class CPUMediaSlider extends MediaSlider {
    
    public CPUMediaSlider() {
        super(JSlider.HORIZONTAL, 1, 10, 9);
    }
    
    public String toString() {
        return getValue() + "0%";
    }

}

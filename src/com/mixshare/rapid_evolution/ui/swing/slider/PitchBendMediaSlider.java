package com.mixshare.rapid_evolution.ui.swing.slider;

import rapid_evolution.ui.SkinManager;
import javax.swing.JSlider;

public class PitchBendMediaSlider extends MediaSlider {

    
    public PitchBendMediaSlider() {
        super(JSlider.VERTICAL, 4096, 12288, 8192);
    }
    
    public String toString() {
        if (getValue() != 8192) {
            int val = getValue();
            if (val > 8192) {
              int diff = (val - 8192) * 100 / 4096;
              if (diff != 0) return " +" + String.valueOf(diff) + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
            } else {
              int diff = (8192 - val) * 100 / 4096;
              if (diff != 0) return " -" + String.valueOf(diff) + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
            }
          }        
        return "0" + SkinManager.instance.getMessageText("pitch_bend_cents_prefix");
    }
}

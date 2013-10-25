package rapid_evolution.ui;

import java.awt.Color;

import javax.swing.JSlider;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;

public class MySliderLabel extends RELabel {
  
    private JSlider slider = null;
    public MySliderLabel(String text, JSlider slider) {
        super(text);
        this.slider = slider;
    }        
    
    public Color getForeground() {
//        Color color = (Color)SkinManager.instance.colormap.get("slider_label_foreground");
//        if (color != null)
//            return color;
        if (slider != null)
            return slider.getForeground();
        return super.getForeground();
    }
    
}

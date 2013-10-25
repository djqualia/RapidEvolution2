package com.mixshare.rapid_evolution.ui.swing.filechooser;

import javax.swing.JFileChooser;

import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class REFileChooser extends JFileChooser {

    public REFileChooser() {
        super();
        if ((REButton.getCurrentButtonType() == REButton.BUTTON_TYPE_LIQUID) && SkinManager.instance.use_button_fixer) {
            FileChooserFixer ffc = new FileChooserFixer(this, null);        
        }
    }
    
}

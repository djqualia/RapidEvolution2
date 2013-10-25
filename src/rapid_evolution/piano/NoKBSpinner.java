package rapid_evolution.piano;

import java.awt.event.KeyEvent;

import javax.swing.SpinnerModel;

import com.mixshare.rapid_evolution.ui.swing.spinner.RESpinner;

public class NoKBSpinner extends RESpinner {

    public NoKBSpinner(SpinnerModel sm) {
        super(sm);
    }
    
    boolean ignorekb = true;
    
    public void processKeyEvent(KeyEvent e) {
        if (!ignorekb) super.processKeyEvent(e);
    }    
    
}

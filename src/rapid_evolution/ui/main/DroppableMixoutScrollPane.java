package rapid_evolution.ui.main;

import java.awt.Component;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import rapid_evolution.ui.dnd.REDrop;
import rapid_evolution.ui.dnd.REMixoutSectionDropListener;

public class DroppableMixoutScrollPane extends JScrollPane  {

    private static Logger log = Logger.getLogger(DroppableMixoutScrollPane.class);
    
    public DroppableMixoutScrollPane(Component view) {
    	super(view);
    	new REDrop(this, new REMixoutSectionDropListener());       
    }

}

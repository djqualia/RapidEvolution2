package rapid_evolution.ui;

import rapid_evolution.*;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

public class REBasicTreeUI extends BasicTreeUI {
    private static Logger log = Logger.getLogger(REBasicTreeUI.class);
        
    protected boolean isLocationInExpandControl(TreePath path, int mouseX, int mouseY) {
        try {
            return super.isLocationInExpandControl(path, mouseX, mouseY);
        } catch (Exception e) {
            log.error("isLocationInExpandControl(): error", e);
        }
        return false;
    }
    
}

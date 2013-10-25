package rapid_evolution.ui.styles;

import java.util.Map;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import rapid_evolution.ui.styles.StylesUI;

import org.apache.log4j.Logger;

import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;

public class DeleteStyleUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(DeleteStyleUIUpdateThread.class);
    
    private StyleLinkedList style;
    public DeleteStyleUIUpdateThread(StyleLinkedList style) {
        this.style = style;
    }
    
    public void run() {
        try {
            for (int i = 0; i < StylesUI.styles_trees.size(); ++i) {
                JTree tree = (JTree)StylesUI.styles_trees.get(i);
                Map map = (Map)StylesUI.style_maps.get(tree);
                map.remove(style);
                tree.getModel().getRoot();
                StylesUI.RecursiveDelete(style, (TreeNode)tree.getModel().getRoot(), tree);                          
            }                            
            OptionsUI.instance.filter1.removeStyle(style);
            OptionsUI.instance.filter2.removeStyle(style);
            OptionsUI.instance.filter3.removeStyle(style);            

        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
    }
    
}

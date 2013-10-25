package rapid_evolution.ui;

import rapid_evolution.RapidEvolution;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.SkinManager;

import javax.swing.tree.TreePath;
import javax.swing.JButton;
import javax.swing.JTree;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class StyleClearSelectionHandler implements TreeSelectionListener {
    private JButton clearbutton;
    private JButton editbutton = null;
    private JButton addbutton = null;
    private JButton deletebutton = null;
    private JTree tree;    
    public StyleClearSelectionHandler(RETree tree, JButton clearbutton) {
        this.clearbutton = clearbutton;
        this.tree = tree;
    }
    public StyleClearSelectionHandler(RETree tree, JButton clearbutton, JButton editbutton, JButton addbutton, JButton deletebutton) {
        this.clearbutton = clearbutton;
        this.tree = tree;
        this.editbutton = editbutton;
        this.addbutton = addbutton;
        this.deletebutton = deletebutton;
    }
    public void valueChanged(TreeSelectionEvent e) {
        int selection_count = tree.getSelectionCount();
        if (selection_count > 0) clearbutton.setEnabled(true);
        else clearbutton.setEnabled(false);
        if (selection_count == 1) {
            if (editbutton != null) editbutton.setEnabled(true);
            if (deletebutton != null) deletebutton.setEnabled(true);
        } else if (selection_count > 1) {
            if (editbutton != null) editbutton.setEnabled(false);            
            if (deletebutton != null) deletebutton.setEnabled(true);
        } else {
            // selection_count = 0
            if (editbutton != null) editbutton.setEnabled(false);
            if (deletebutton != null) deletebutton.setEnabled(false);
        }
    }
}

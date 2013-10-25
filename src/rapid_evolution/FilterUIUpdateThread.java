package rapid_evolution;

import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import rapid_evolution.ui.styles.StylesUI;

import org.apache.log4j.Logger;

import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;

public class FilterUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(FilterUIUpdateThread.class);
    
    private Filter filter;
    private boolean selectionChanged;
    public FilterUIUpdateThread(Filter filter, boolean selectionChanged) {
        this.filter = filter;
        this.selectionChanged = selectionChanged;
    }
    
    public void run() {
        try {
            if (!OptionsUI.instance.enablefilter.isSelected()) return;
            if (filter.isDisabled()) return;
            log.debug("updateList(): starting");
            try {
                filter.removeSelectionListeners();
                int[] indices = filter.list.getSelectedIndices();
                Vector selected_keys = new Vector();
                DefaultListModel dlm = (DefaultListModel) filter.list.getModel();        
                for (int i = 0; i < indices.length; ++i) {
                    Object value = dlm.getElementAt(indices[i]);
                    selected_keys.add(value);
                }                
                filter.populateList();
                Vector selected_indices = new Vector();
                for (int s = 0; s < selected_keys.size(); ++s) {
                    Object key = selected_keys.get(s);
                    if (filter.keys.containsKey(key)) {
                        int i = 0;
                        boolean done = false;
                        while ((i < dlm.size()) && !done) {
                            Object key2 = dlm.getElementAt(i);
                            if (key.equals(key2)) {
                                done = true;
                                selected_indices.add(new Integer(i));                    
                            }
                            ++i;
                        }
                    }
                }
                int[] selected = new int[selected_indices.size()];
                for (int s = 0; s < selected.length; ++s) {
                    selected[s] = ((Integer)selected_indices.get(s)).intValue();
                }
                filter.list.setSelectedIndices(selected);
                if (selectionChanged || (indices.length != selected.length)) {
                    filter.valueChanged(null, true);
                }
                filter.addSelectionListener();
            } catch (Exception e) {
                log.error("updateList(): error", e);
            }
            log.debug("updateList(): finished");            
        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
    }

}

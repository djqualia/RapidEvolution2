package rapid_evolution.ui.styles;

import java.util.Map;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.AddStyleRunnable;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class InsertStyleUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(InsertStyleUIUpdateThread.class);
    
    private static Semaphore updateSem = new Semaphore(1);
    
    private StyleLinkedList style;
    private AddStyleRunnable r;
    public InsertStyleUIUpdateThread(StyleLinkedList style, AddStyleRunnable r) {
        this.style = style;
        this.r = r;
    }
 
    public static void process(StyleLinkedList style, AddStyleRunnable r) {
        try {
        	updateSem.acquire();
            if (log.isTraceEnabled()) log.trace("process(): started, style=" + style);
            for (int i = 0; i < StylesUI.styles_trees.size(); ++i) {
                JTree tree = (JTree)StylesUI.styles_trees.get(i);
                Map map = (Map)StylesUI.style_maps.get(tree);            
                MutableTreeNode rootnode = (MutableTreeNode)tree.getModel().getRoot();
                DefaultTreeModel dtm = (DefaultTreeModel)tree.getModel();

                MyMutableStyleNode newnode = new MyMutableStyleNode(style, style.getName());
                if (rootnode.getChildCount() == 0) {
                  DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");
                  top.add(newnode);
                  dtm.setRoot(top);             
                } else {
                    int insert_index = rootnode.getChildCount();
                    int j = 0;
                    boolean done = false;
                    String styleName = style.getName();
                    if (log.isTraceEnabled()) log.trace("process(): styleName=" + styleName);
                    while ((j < rootnode.getChildCount()) && !done) {
                        MyMutableStyleNode childnode = (MyMutableStyleNode)rootnode.getChildAt(j);
                        String compareName = childnode.getStyle().getName();
                        if (log.isTraceEnabled()) log.trace("process(): compareName=" + compareName);
                        if (styleName.compareToIgnoreCase(compareName) < 0) {
                            insert_index = j;                          
                            done = true;
                        }
                        ++j;                      
                    }
                    if (log.isTraceEnabled()) log.trace("process(): insert index=" + insert_index);                    
                    dtm.insertNodeInto(newnode, rootnode, insert_index);
                    if (log.isTraceEnabled()) log.trace("process(): removing selection path for=" + insert_index);
                    tree.removeSelectionPath(tree.getPathForRow(insert_index));                
                }
                
                Vector nodes = new Vector();
                nodes.add(newnode);
                map.put(style, nodes);
                //RELabel label = new com.mixshare.rapid_evolution.ui.swing.label.RELabel(newnode.getUserObject().toString());
                //label.setOpaque(false);
                //newnode.setUserObject(label);
                if (log.isTraceEnabled()) log.trace("insertStyle(): adding child map node: " + newnode);

                
                //tree.updateUI();

            }                         
            
            OptionsUI.instance.filter1.addStyle(style);
            OptionsUI.instance.filter2.addStyle(style);
            OptionsUI.instance.filter3.addStyle(style);      
            
            /*
            // auto hierarchy creation
            // TODO: need to figure out where to put this so selections work properly...
            try {
          	  // look for possible parents
      	      int parentSeperatorIndex = style.getName().lastIndexOf(", ");
      	      if (parentSeperatorIndex >= 0) {
      	    	  String parentName = style.getName().substring(0, parentSeperatorIndex);
      	    	  if (log.isDebugEnabled())
      	    		  log.debug("addStyle(): looking for parent style=" + parentName);
      	    	  
      	    	  boolean parentFound = false;
      	    	  StyleLinkedList siter = SongDB.instance.masterstylelist;
      	    	  while (!parentFound && (siter != null)) {
      	    		  if (siter.getName().equalsIgnoreCase(parentName)) {
      	    			  parentFound = true;
      	    	    	  if (log.isDebugEnabled())
      	    	    		  log.debug("addStyle(): parent found=" + siter.getName());
      	    			  UpdateHierarchyUIUpdateThread.updateHierarchyNow(StyleLinkedList.root_style, siter, style, false);
      	    		  }
      	    		  siter = siter.next;
      	    	  }
      	      }
      	      // look for possible children (can't guarantee the parent is added first)
      	      String target = style.getName().toLowerCase() + ", ";
          	  StyleLinkedList siter = SongDB.instance.masterstylelist;
          	  while (siter != null) {
          		  if (siter.getName().toLowerCase().startsWith(target)) {
          	    	  if (log.isDebugEnabled())
          	    		  log.debug("addStyle(): child found=" + siter.getName());
          	    	UpdateHierarchyUIUpdateThread.updateHierarchyNow(siter.isRootStyle() ? StyleLinkedList.root_style : null, style, siter, siter.isRootStyle() ? false : true);
          		  }
          		  siter = siter.next;
          	  }
      	      
            } catch (Exception e) {
          	  log.error("addStyle(): error during auto hierarchy creation", e);
            }       
            */
            
            if (r != null) {
                r.run();
            }
            
        } catch (Exception e) {
            log.error("process(): error Exception", e);
        }        
        updateSem.release();
    }
    
    public void run() {
        process(style, r);
    }

}

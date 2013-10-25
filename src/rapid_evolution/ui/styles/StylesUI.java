package rapid_evolution.ui.styles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Container;

import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import rapid_evolution.ui.AddStyleRunnable;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.RETree;

import org.jvnet.substance.SubstanceTreeUI;
import javax.swing.plaf.TreeUI;

import java.awt.MouseInfo;

import rapid_evolution.ui.RETreeCellRenderer;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class StylesUI {    

    private static Logger log = Logger.getLogger(StylesUI.class);
        
  //set/add styles thread count?:
   public static int processsetcount = 0;
   
   static public void RepopulateExcludeStyleList() {
       javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() { 
         if (EditStyleUI.instance.editedstyle == null) return;
         try {
           EditStyleUI.instance.editedstyle.ExcludeStyleListSem.acquire();
           DefaultListModel dlm2 = (DefaultListModel) EditStyleUI.instance.styleexcludelist.getModel();
           dlm2.clear();
           for (int i = 0; i < EditStyleUI.instance.editedstyle.styleexcludedisplayvector.size(); ++i) {
             dlm2.addElement((String)EditStyleUI.instance.editedstyle.styleexcludedisplayvector.get(i));
           }
         } catch (Exception e) { log.error("RepopulateExcludeStyleList(): error", e); }
         EditStyleUI.instance.editedstyle.ExcludeStyleListSem.release();
       } });
   }

   static public void RepopulateIncludeStyleList() {
       javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() { 
         if (EditStyleUI.instance.editedstyle == null) return;
         try {
           EditStyleUI.instance.editedstyle.IncludeStyleListSem.acquire();
           DefaultListModel dlm2 = (DefaultListModel)EditStyleUI.instance.editstylekeywordslist.getModel();
           dlm2.clear();
           for (int i = 0; i < EditStyleUI.instance.editedstyle.styleincludedisplayvector.size(); ++i) {
             dlm2.addElement((String)EditStyleUI.instance.editedstyle.styleincludedisplayvector.get(i));
           }
         } catch (Exception e) { log.error("RepopulateIncludeStyleList(): error", e); }
         EditStyleUI.instance.editedstyle.IncludeStyleListSem.release();
       } });
   }

  static public void AddIncludeSelectedIndex(int index) {
    int[] selectedindices = new int[EditStyleUI.instance.editstylekeywordslist.getSelectedIndices().length + 1];
    for (int i = 0; i < EditStyleUI.instance.editstylekeywordslist.getSelectedIndices().length; ++i) {
        selectedindices[i] = EditStyleUI.instance.editstylekeywordslist.getSelectedIndices()[i];
    }
    selectedindices[EditStyleUI.instance.editstylekeywordslist.getSelectedIndices().length] = index;
    EditStyleUI.instance.editstylekeywordslist.setSelectedIndices(selectedindices);
  }

  static public  void AddExcludeSelectedIndex(int index) {
    int[] selectedindices = new int[EditStyleUI.instance.styleexcludelist.getSelectedIndices().length + 1];
    for (int i = 0; i < EditStyleUI.instance.styleexcludelist.getSelectedIndices().length; ++i) {
        selectedindices[i] = EditStyleUI.instance.styleexcludelist.getSelectedIndices()[i];
    }
    selectedindices[EditStyleUI.instance.styleexcludelist.getSelectedIndices().length] = index;
    EditStyleUI.instance.styleexcludelist.setSelectedIndices(selectedindices);
  }
  
  public static HashMap style_maps = new HashMap();
  public static Vector styles_trees = new Vector(); 
  
  public static Map getMap(JTree tree) { 
      return (Map)style_maps.get(tree);
  }
  
  static public void removeStyle(StyleLinkedList style) {
      javax.swing.SwingUtilities.invokeLater(new DeleteStyleUIUpdateThread(style));
  }
  	
	static public void renameStyle(StyleLinkedList style) {
          javax.swing.SwingUtilities.invokeLater(new StyleRenameUIUpdateThread(style));
    }

	static public String getHierarchyName(StyleLinkedList style, MyMutableStyleNode parent) {
	    String name = style.getName();
	    while (parent != null) {
	        name = StringUtil.filterParentName(name, parent.getStyle().getName());
	        TreeNode next_parent = parent.getParent();
	        if (next_parent instanceof MyMutableStyleNode) {
		        parent = (MyMutableStyleNode)next_parent;	            
	        } else {
	            parent = null;
	        }
	    }
	    return name;
	}	 

    static public void rolloverFixCheck() {
        for (int i = 0; i < styles_trees.size(); ++i) {
            JTree tree = (JTree)styles_trees.get(i);
            TreeUI treeUI = tree.getUI();
            if (treeUI instanceof SubstanceTreeUI) {
                SubstanceTreeUI substanceTreeUI = (SubstanceTreeUI)treeUI;         
                Point point = MouseInfo.getPointerInfo().getLocation();
                Container target = tree.getParent();
                javax.swing.SwingUtilities.convertPointFromScreen(point, target); 

                Rectangle bounds = target.getBounds();
                //bounds = new Rectangle((int)(bounds.getWidth() - RETreeCellRenderer.rowSelectionFixBuffer), (int)bounds.getHeight());
                //if (log.isTraceEnabled()) log.trace("rolloverFixCheck(): treeBounds=" + bounds + ", tree parent bounds=" + tree.getParent().getBounds());
                if (!bounds.contains(point))
                    substanceTreeUI.fadeOutRollover();
            }
            
        }
    }
    
	static public void updateTreeUIs() {
        for (int i = 0; i < styles_trees.size(); ++i) {
            JTree tree = (JTree)styles_trees.get(i);
            tree.updateUI();
        }
	}
    static public void insertStyle(StyleLinkedList style) {
        insertStyle(style, null);
    }
	static public void insertStyle(StyleLinkedList style, AddStyleRunnable r) {
        try {
            //if (log.isTraceEnabled()) log.trace("insertStyle(): event dispatch thread=" + javax.swing.SwingUtilities.isEventDispatchThread());
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                log.debug("insertStyle(): processing immediately...");
                InsertStyleUIUpdateThread.process(style, null);
            } else {                
                if (log.isTraceEnabled())
                    log.debug("insertStyle(): calling invokeAndWait...");
                // BUG: the invokeAndWait was causing hangs!?
                //javax.swing.SwingUtilities.invokeAndWait(new InsertStyleUIUpdateThread(style));
                javax.swing.SwingUtilities.invokeLater(new InsertStyleUIUpdateThread(style, r));
            }
        } catch (Exception e) {
            log.error("insertStyle(): error Exception", e);
            //javax.swing.SwingUtilities.invokeLater(new InsertStyleUIUpdateThread(style));
        }
	}
	
	static public void resetSelectionModes() { 
        for (int i = 0; i < styles_trees.size(); ++i) {
            JTree tree = (JTree)styles_trees.get(i);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        }	    
	}

	
	  public static void createStyleNodes() {
	      
	      for (int i = 0; i < styles_trees.size(); ++i) {
	          JTree tree = (JTree)styles_trees.get(i);
	          Map map = (Map)style_maps.get(tree);
	          map.clear();
	          DefaultMutableTreeNode top = new DefaultMutableTreeNode("root");

		      StyleLinkedList iter = SongDB.instance.masterstylelist;
		      while (iter != null) {
		          if (iter.isRootStyle()) {
		              MyMutableStyleNode node = new MyMutableStyleNode(iter, iter.getName());
		              top.add(node);
		              recursiveInsertChildStyles(node, iter, map);
		              Vector nodes = (Vector)map.get(iter);
		              if (nodes != null) {
		                  nodes.add(node);
		                  log.trace("createStyleNodes(): updating map node: " + node);
		              } else {
		                  nodes = new Vector();
		                  nodes.add(node);
		                  map.put(iter, nodes);
		                  log.trace("createStyleNodes(): adding child map node: " + node);
		              }
		          }
		          iter = iter.next;
		      }
	          	          
	          DefaultTreeModel treemodel = (DefaultTreeModel) tree.getModel();
	          treemodel.setRoot(top);
	          
	      }
	      	            
	  }

	  public static void recursiveInsertChildStyles(MyMutableStyleNode node, StyleLinkedList style, Map map) {
          StyleLinkedList[] child_styles = style.getChildStyles();
          for (int i = 0; i < child_styles.length; ++i) {
              StyleLinkedList child_style = child_styles[i];
              String hierarchyname = getHierarchyName(child_style, node);
              MyMutableStyleNode child_node = new MyMutableStyleNode(child_style, hierarchyname);
              int insert_index = node.getChildCount();
              int c = 0;
              while ((c < node.getChildCount()) && (insert_index == node.getChildCount())) {
                  MyMutableStyleNode compare = (MyMutableStyleNode)node.getChildAt(c);
                  if (hierarchyname.compareToIgnoreCase(getHierarchyName(compare.getStyle(), node)) < 0) {
                      insert_index = c;
                  }
                  ++c;
              }
              node.insert(child_node, insert_index);
              
              Vector nodes = (Vector)map.get(child_style);
              if (nodes != null) {
                  nodes.add(child_node);
                  log.trace("recursiveInsertChildStyles(): updating child map node: " + child_node);
             } else {
                  nodes = new Vector();
                  nodes.add(child_node);
                  map.put(child_style, nodes);
                  log.trace("recursiveInsertChildStyles(): adding child map node: " + child_node);
              }
              
              recursiveInsertChildStyles(child_node, child_style, map);
          }
      }

	  
	  public static void addTree(JTree tree, Map map) {
	      styles_trees.add(tree);
	      style_maps.put(tree, map);
	  }

	  public static void updateHierarchy(StyleLinkedList oldParentStyle, StyleLinkedList newParentStyle, StyleLinkedList childStyle, boolean copyAction) {
	      try {
	          javax.swing.SwingUtilities.invokeLater(new UpdateHierarchyUIUpdateThread(oldParentStyle, newParentStyle, childStyle, copyAction));
	      } catch (Exception e) {
	          log.error("updateHierarchy(): error", e);
	      }
	  }
	  	  
        public static void RecursiveDelete(StyleLinkedList style, TreeNode node, JTree tree) {
            for (int i = 0; i < node.getChildCount(); ++i) {
                MyMutableStyleNode thisnode = (MyMutableStyleNode)node.getChildAt(i);
                if (thisnode.getStyle().equals(style)) {
                    DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                    model.removeNodeFromParent(thisnode);
                } else {
                    RecursiveDelete(style, thisnode, tree);
                }
            }
        }
      

}

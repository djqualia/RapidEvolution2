package rapid_evolution.ui.styles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import rapid_evolution.ui.styles.StylesUI;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;

public class UpdateHierarchyUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(UpdateHierarchyUIUpdateThread.class);
    
    private StyleLinkedList oldParentStyle;
    private StyleLinkedList newParentStyle;
    private StyleLinkedList childStyle;
    boolean copyAction;
    public UpdateHierarchyUIUpdateThread(StyleLinkedList oldParentStyle, StyleLinkedList newParentStyle, StyleLinkedList childStyle, boolean copyAction) {
        this.oldParentStyle = oldParentStyle;
        this.newParentStyle = newParentStyle;
        this.childStyle = childStyle;
        this.copyAction = copyAction;
    }
    
    // call this method directly only to update immediately, otherwise StylesUI.updateHierarchy
    static public void updateHierarchyNow(StyleLinkedList oldParentStyle, StyleLinkedList newParentStyle, StyleLinkedList childStyle, boolean copyAction) {
        try {
            log.trace("updateHierarchy(): old parent style=" + oldParentStyle + ", new parent style=" + newParentStyle + ", child style=" + childStyle + ", copyAction=" + copyAction);
            for (int i = 0; i < StylesUI.styles_trees.size(); ++i) {
                JTree tree = (JTree)StylesUI.styles_trees.get(i);
                log.trace("updateHierarchy(): tree: " + tree);
                Map map = (Map)StylesUI.style_maps.get(tree);
                
                Map selected_styles = new HashMap();
                TreePath[] paths = tree.getSelectionPaths();
                for (int p = 0; p < paths.length; ++p) {
                    MyMutableStyleNode node = (MyMutableStyleNode)paths[p].getLastPathComponent();
                    selected_styles.put(node.getStyle(), null);
                }
                
                log.trace("updateHierarchy(): map before: " + map);
                if (!copyAction) { // i.e move
                    if ((oldParentStyle != null) && (oldParentStyle != StyleLinkedList.root_style)) {
                        // remove child from old parent
                        Vector nodes = (Vector)map.get(oldParentStyle);
                        if (nodes != null) {
                            for (int n = 0; n < nodes.size(); ++n) {
                                MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(n);
                                for (int c = 0; c < node.getChildCount(); ++c) {
                                    MyMutableStyleNode childnode = (MyMutableStyleNode)node.getChildAt(c);
                                    if (childnode.getStyle().equals(childStyle)) {
                                        recursiveRemove(childnode, map);
                                        node.remove(c);
                                        --c;
                                        Vector childnodes = (Vector)map.get(childStyle);
                                        if (childnodes != null) {
                                            for (int j = 0; j < childnodes.size(); ++j) {
                                                if (childnode == childnodes.get(j)) {
                                                    log.trace("updateHierarchy(): found child map node for removal");
                                                    childnodes.remove(j);
                                                    --j;
                                                }
                                            }
                                            if (childnodes.size() == 0) map.remove(childStyle);
                                        }
                                    }
                                }
                                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                                model.reload(node);                                 
                            }
                        }
                        if (i == 0) {
                            childStyle.removeParentStyle(oldParentStyle);
                            oldParentStyle.removeChildStyle(childStyle);
                        }
                    } else {
                        // remove from root
                        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
                        for (int c = 0; c < root.getChildCount(); ++c) {
                            MyMutableStyleNode childnode = (MyMutableStyleNode)root.getChildAt(c);
                            if (childnode.getStyle().equals(childStyle)) {
                                recursiveRemove(childnode, map);
                                root.remove(c);
                                --c;
                                Vector childnodes = (Vector)map.get(childStyle);
                                if (childnodes != null) {
                                    for (int j = 0; j < childnodes.size(); ++j) {
                                        if (childnode == childnodes.get(j)) {
                                            log.trace("updateHierarchy(): found child map node for removal: " + childnode);
                                            childnodes.remove(j);
                                            --j;                                              
                                        }
                                    }
                                    if (childnodes.size() == 0) map.remove(childStyle);
                                }
                            }
                        }
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        model.reload(root);                         
                        if (i == 0) {
                            childStyle.removeParentStyle(StyleLinkedList.root_style);                   
                        }
                    }
                }    
                // add child to new parent
                Vector parent_nodes = (Vector)map.get(newParentStyle);
                if (parent_nodes != null) {
                    for (int pn = 0; pn < parent_nodes.size(); ++pn) {
                        MyMutableStyleNode parent_node = (MyMutableStyleNode)parent_nodes.get(pn);
                        if (!parent_node.containsStyle(childStyle)) {
                            boolean inserted = false;
                            String hierarchyname = StylesUI.getHierarchyName(childStyle, parent_node);
                            int insert_index = parent_node.getChildCount();
                            int c = 0;
                            while ((c < parent_node.getChildCount()) && !inserted) {
                                MyMutableStyleNode child_node = (MyMutableStyleNode)parent_node.getChildAt(c);
                                if (hierarchyname.compareToIgnoreCase(StylesUI.getHierarchyName(child_node.getStyle(), parent_node)) < 0) {
                                    inserted = true;
                                    insert_index = c;
                                }                           
                                ++c;
                            }
                            
                            MyMutableStyleNode child_node = new MyMutableStyleNode(childStyle, hierarchyname);
                            parent_node.insert(child_node, insert_index);
                            
                            Vector child_nodes = (Vector)map.get(childStyle);
                            if (child_nodes == null) {
                                child_nodes = new Vector();
                                child_nodes.add(child_node);
                                map.put(childStyle, child_nodes);
                                log.trace("updateHierarchy(): adding child map node: " + child_node);
                            } else {
                                child_nodes.add(child_node);                            
                                log.trace("updateHierarchy(): updating child map node: " + child_node);
  
                            }
  
                            StylesUI.recursiveInsertChildStyles(child_node, childStyle, map);
                            
                            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                            model.reload(parent_node);
                        }
                        if (i == 0) {
                            childStyle.addParentStyle(newParentStyle);
                            newParentStyle.addChildStyle(childStyle);
                        }
                    }
                } else {
                    // insert at root
                    DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode)tree.getModel().getRoot();
                    
                    if (parent_node.getLevel() == 1) {
                        boolean inserted = false;
                        int insert_index = parent_node.getChildCount();
                        int c = 0;
                        while ((c < parent_node.getChildCount()) && !inserted) {
                            MyMutableStyleNode child_node = (MyMutableStyleNode)parent_node.getChildAt(c);
                            if (childStyle.getName().compareToIgnoreCase(child_node.getStyle().getName()) < 0) {
                                inserted = true;
                                insert_index = c;
                            }                  
                            ++c;
                        }
                        
                        MyMutableStyleNode child_node = new MyMutableStyleNode(childStyle, childStyle.getName());
                        parent_node.insert(child_node, insert_index);
                        
                        Vector child_nodes = (Vector)map.get(childStyle);
                        if (child_nodes == null) {
                            child_nodes = new Vector();
                            child_nodes.add(child_node);
                            map.put(childStyle, child_nodes);
                            log.trace("updateHierarchy(): adding child map node: " + child_node);
                        } else {
                            child_nodes.add(child_node);                            
                            log.trace("updateHierarchy(): updating child map node: " + child_node);
  
                        }
  
                        StylesUI.recursiveInsertChildStyles(child_node, childStyle, map);
                        
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        model.reload(parent_node);
                        
                        if (i == 0) {
                            childStyle.addParentStyle(StyleLinkedList.root_style);
                        }
                    }
                    
                }
                
//              tree.setExpandsSelectedPaths(false);
                Set selected_set = selected_styles.keySet();
                if (selected_set != null) {
                    Iterator sel_iter = selected_set.iterator();
                    while (sel_iter.hasNext()) {
                        StyleLinkedList style = (StyleLinkedList)sel_iter.next();
                        Vector nodes = (Vector)map.get(style);
                        if (nodes != null) {
                            for (int n = 0; n < nodes.size(); ++n) {
                                MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(n);
                                tree.addSelectionPath(new TreePath(node.getPath()));
                            }
                        }
                    }
                }
//              tree.setExpandsSelectedPaths(true);                 
                
                SongLinkedList siter = SongDB.instance.SongLL;
                while (siter != null) {
                    if (childStyle.containsLogical(siter)) siter.logicalstyleidscached = false;
                    siter = siter.next;
                }

                log.trace("updateHierarchy(): map after: " + map);
                                                
            }

      } catch (Exception e) {
          log.error("run(): error Exception", e);
      }    	
    }
    
    public void run() {
    	updateHierarchyNow(oldParentStyle, newParentStyle, childStyle, copyAction);
    }

      private static void recursiveRemove(MyMutableStyleNode node, Map map) {
          for (int i = 0; i < node.getChildCount(); ++i) {
              MyMutableStyleNode child_node = (MyMutableStyleNode)node.getChildAt(i);
              recursiveRemove(child_node, map);
              Vector childnodes = (Vector)map.get(child_node.getStyle());
              if (childnodes != null) {
                  for (int j = 0; j < childnodes.size(); ++j) {
                      if (child_node == childnodes.get(j)) {
                          log.trace("updateHierarchy(): found child map node for removal: " + child_node);
                          childnodes.remove(j);
                          --j;                                              
                      }
                  }
                  if (childnodes.size() == 0) map.remove(child_node.getStyle());
              }
          }
      }
    
}

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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import rapid_evolution.ui.styles.StylesUI;

import org.apache.log4j.Logger;

import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.RETree;

public class StyleRenameUIUpdateThread extends Thread {
    
    private static Logger log = Logger.getLogger(StyleRenameUIUpdateThread.class);
    
    private StyleLinkedList style;
    public StyleRenameUIUpdateThread(StyleLinkedList style) {
        this.style = style;
    }
    
    public void run() {
        try {
            style = StyleLinkedList.getStyle(style.getStyleId());
            for (int i = 0; i < StylesUI.styles_trees.size(); ++i) {
                RETree tree = (RETree)StylesUI.styles_trees.get(i);
                
                Map selected_styles = new HashMap();
                TreePath[] paths = tree.getSelectionPaths();
                for (int p = 0; p < paths.length; ++p) {
                    MyMutableStyleNode node = (MyMutableStyleNode)paths[p].getLastPathComponent();
                    selected_styles.put(node.getStyle(), null);
                }
                
                Map map = (Map)StylesUI.style_maps.get(tree);
                boolean is_selected = tree.isADirectSelectedStyle(style);
                MutableTreeNode rootnode = (MutableTreeNode)tree.getModel().getRoot();
                StylesUI.RecursiveDelete(style, rootnode, tree);                                            
                DefaultTreeModel dtm = (DefaultTreeModel)tree.getModel();

                map.remove(style);            
                
                if (style.isRootStyle()) {
                    int insert_index = rootnode.getChildCount();
                    int j = 0;
                    boolean done = false;
                    while ((j < rootnode.getChildCount()) && !done) {
                        MyMutableStyleNode childnode = (MyMutableStyleNode)rootnode.getChildAt(j);
                        if (style.getName().compareToIgnoreCase(childnode.getStyle().getName()) < 0) {
                            insert_index = j;                          
                            done = true;
                        }
                        ++j;                      
                    }
                    MyMutableStyleNode newnode = new MyMutableStyleNode(style, style.getName());
                    dtm.insertNodeInto(newnode, rootnode, insert_index);
                    if (is_selected) tree.addSelectionPath(tree.getPathForRow(insert_index));
                    else tree.removeSelectionPath(tree.getPathForRow(insert_index));
                    
                  Vector nodes = (Vector)map.get(style);
                  if (nodes != null) {
                      nodes.add(newnode);
                      log.trace("renameStyle(): updating map node: " + newnode);
                  } else {
                      nodes = new Vector();
                      nodes.add(newnode);
                      map.put(style, nodes);
                      log.trace("renameStyle(): adding child map node: " + newnode);
                  } 
                  
                  StylesUI.recursiveInsertChildStyles(newnode, style, map);

                  DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                  model.reload(newnode);                                          
                }
                
                StyleLinkedList[] styles = style.getParentStyles();
                for (int s = 0; s < styles.length; ++s) {
                   Vector parent_nodes = (Vector)map.get(styles[s]);                   
                   if (parent_nodes != null) {
                       for (int n = 0; n < parent_nodes.size(); ++n) {
                           MyMutableStyleNode parent_node = (MyMutableStyleNode)parent_nodes.get(n);
                           
                           String hierarchyname = StylesUI.getHierarchyName(style, parent_node);
                           int insert_index = parent_node.getChildCount();
                           int j = 0;
                           boolean done = false;
                           while ((j < parent_node.getChildCount()) && !done) {
                               MyMutableStyleNode childnode = (MyMutableStyleNode)parent_node.getChildAt(j);
                               if (hierarchyname.compareToIgnoreCase(StylesUI.getHierarchyName(childnode.getStyle(), parent_node)) < 0) {
                                   insert_index = j;                          
                                   done = true;
                               }
                               ++j;                      
                           }
                           MyMutableStyleNode newnode = new MyMutableStyleNode(style, hierarchyname);
                           dtm.insertNodeInto(newnode, parent_node, insert_index);

                           if (is_selected) tree.addSelectionPath(tree.getPathForRow(insert_index));
                           else tree.removeSelectionPath(tree.getPathForRow(insert_index));                           
                           
                      Vector nodes = (Vector)map.get(style);
                      if (nodes != null) {
                          nodes.add(newnode);
                       log.trace("renameStyle(): updating map node: " + newnode);
                      } else {
                          nodes = new Vector();
                          nodes.add(newnode);
                          map.put(style, nodes);
                       log.trace("renameStyle(): adding child map node: " + newnode);
                      }    
                      
                      StylesUI.recursiveInsertChildStyles(newnode, style, map);

                      DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                      model.reload(newnode);                          

                       }
                    
                    }
                }            
                  Set selected_set = selected_styles.keySet();
                  if (selected_set != null) {
                      Iterator sel_iter = selected_set.iterator();
                      while (sel_iter.hasNext()) {
                          StyleLinkedList node_style = (StyleLinkedList)sel_iter.next();
                          Vector nodes = (Vector)map.get(node_style);
                          if (nodes != null) {
                              for (int n = 0; n < nodes.size(); ++n) {
                                  MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(n);
                                  tree.addSelectionPath(new TreePath(node.getPath()));
                              }
                          }
                      }
                  }

            }             
        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
    }
    
}

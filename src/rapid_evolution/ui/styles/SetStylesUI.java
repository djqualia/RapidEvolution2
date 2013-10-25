package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.RETree;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.StyleClearSelectionHandler;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class SetStylesUI extends REDialog implements ActionListener {
    public SetStylesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static SetStylesUI instance = null;
    public RETree setstylestree = new RETree();
    public JButton setstylesclearbt = new REButton();
    public JButton setstylesokbutton = new REButton();
    public JButton setstylescancelbutton = new REButton();

    private void setupDialog() {
        setstylesclearbt.setEnabled(false);
        
        setstylestree.setRootVisible(false);

        StylesUI.addTree(setstylestree, style_tree_map);
        setstylestree.getSelectionModel().addTreeSelectionListener(new StyleClearSelectionHandler(setstylestree, setstylesclearbt));
        
    }

    Map style_tree_map = java.util.Collections.synchronizedMap(new HashMap());
    

    public void SetStyles() {
      if (!isVisible()) {
        setstylestree.clearSelection();
        StyleLinkedList siter = SongDB.instance.masterstylelist;
        while (siter != null) {
          for (int i = 0; i < setstylesongs.length; ++i) {
            SongLinkedList editedsong = setstylesongs[i];
            if (siter.containsDirect(editedsong)) {
                Vector nodes = (Vector)style_tree_map.get(siter);
                if (nodes != null) {
                    for (int j = 0; j < nodes.size(); ++j) {
                        MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(j);
                        setstylestree.addSelectionPath(new TreePath(node.getPath()));
                    }
                }
            }
          }
          siter = siter.next;
        }
        setstylestree.minimizeTree();    
        Display();
      } else requestFocus();
    }

    public SongLinkedList[] setstylesongs = null;
    public class ProcessSetStyles extends Thread {
      public ProcessSetStyles() {}
      public void run() {
        StylesUI.processsetcount++;
        SkinManager.instance.setEnabled("style_add_button", false);
        AddStyleUI.instance.addstyleokbutton.setEnabled(false);
        SongLinkedList[] setstylesongs2 = setstylesongs;
        TreePath[] paths = setstylestree.getSelectionPaths();
        for (int j = 0; j < setstylesongs2.length; ++j) {
          try {
            SongLinkedList editedsong = setstylesongs2[j];
            boolean changed = false;

            DefaultMutableTreeNode root = (DefaultMutableTreeNode)setstylestree.getModel().getRoot();
            for (int i = 0; i < root.getChildCount(); ++i) {
                MyMutableStyleNode node = (MyMutableStyleNode)root.getChildAt(i);
                RecurseRemoveUnselected(node, editedsong, paths);
            }
                        
            for (int p = 0; p < paths.length; ++p) {
                MyMutableStyleNode node = (MyMutableStyleNode)paths[p].getLastPathComponent();
                StyleLinkedList styleiter = node.getStyle();
                if (!styleiter.containsDirect(editedsong)) {
                    if (styleiter.containsExcludeSong(editedsong.uniquesongid)) {
                      styleiter.removeExcludeSong(editedsong);
                      changed = true;
                    }
                    String[] ekeywords = styleiter.getExcludeKeywords();
                  for (int i = 0; i < ekeywords.length; ++i) {
                    if (styleiter.matchesExcludeKeywords(editedsong, ekeywords[i])) {
                      String display = SkinManager.instance.getDialogMessageText("delete_style_exclude_keyword");
                      display = StringUtil.ReplaceString("%style%", display, styleiter.getName());
                      display = StringUtil.ReplaceString("%keyword%", display, ekeywords[i]);
                      display = StringUtil.ReplaceString("%songid%", display, editedsong.getSongIdShort());
                      int n = IOptionPane.showConfirmDialog(
                          getDialog(),
                          display,
                          SkinManager.instance.getDialogMessageTitle("delete_style_exclude_keyword"),
                          IOptionPane.YES_NO_OPTION);
                      if (n == 0) {
                        styleiter.removeExcludeKeyword(ekeywords[i]);
                        --i;
                        changed = true;
                      }
                    }
                  }
                  changed = true;
                  styleiter.insertSong(editedsong);
                }
                
            }
            
            if (changed && OptionsUI.instance.enablefilter.isSelected()) {
                OptionsUI.instance.filter1.updatedSong(editedsong, false);
                OptionsUI.instance.filter2.updatedSong(editedsong, false);
                OptionsUI.instance.filter3.updatedSong(editedsong, false);
            }
          } catch (Exception e) { }
        }
        StylesUI.processsetcount--;
        if (StylesUI.processsetcount == 0) {
          SkinManager.instance.setEnabled("style_add_button", true);
          AddStyleUI.instance.addstyleokbutton.setEnabled(true);
        }
      }
    }

    private void RecurseRemoveUnselected(MyMutableStyleNode node, SongLinkedList editedsong, TreePath[] paths) {
        if (!isMemberOfPath(paths, new TreePath(node.getPath())) && node.getStyle().containsDirect(editedsong)) {
//        if (!setstylestree.getSelectionModel().isPathSelected(new TreePath(node.getPath())) && node.getStyle().containsDirect(editedsong)) {
            node.getStyle().removeSong(editedsong);
            node.getStyle().insertExcludeSong(editedsong);            
        }
        for (int i = 0; i < node.getChildCount(); ++i) {
            MyMutableStyleNode child = (MyMutableStyleNode)node.getChildAt(i);
            RecurseRemoveUnselected(child, editedsong, paths);
        }
    }
    
    private boolean isMemberOfPath(TreePath[] paths, TreePath path) {
        for (int i = 0; i < paths.length; ++i) if (paths[i].equals(path)) return true;
        return false;
    }

    private void setupActionListeners() {
        setstylesclearbt.addActionListener(this);
        setstylesokbutton.addActionListener(this);
        setstylescancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == setstylesclearbt) {
             setstylestree.clearSelection();
      } else if (ae.getSource() == setstylesokbutton) {
             new ProcessSetStyles().start();
             setVisible(false);
      } else if (ae.getSource() == setstylescancelbutton) {
             setVisible(false);
      }
    }

}

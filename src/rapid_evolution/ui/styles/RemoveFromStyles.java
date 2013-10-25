package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.tree.TreePath;

import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.RETree;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.StyleClearSelectionHandler;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class RemoveFromStyles extends REDialog implements ActionListener {
    public RemoveFromStyles(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static RemoveFromStyles instance = null;
    public RETree removefromstylestree = new RETree();
    public JButton removefromstylesclearbt = new REButton();
    public JButton removefromstylesokbutton = new REButton();
    public JButton removefromstylescancelbutton = new REButton();

    private void setupDialog() {
        removefromstylesclearbt.setEnabled(false);
        
        removefromstylestree.setRootVisible(false);
        
        StylesUI.addTree(removefromstylestree, style_tree_map);
        removefromstylestree.getSelectionModel().addTreeSelectionListener(new StyleClearSelectionHandler(removefromstylestree, removefromstylesclearbt));
        
    }

    public Map style_tree_map = java.util.Collections.synchronizedMap(new HashMap());
    
    public SongLinkedList[] removefromstylesongs = null;
    public class ProcessRemoveFromStyles extends Thread {
      public ProcessRemoveFromStyles() {}
      public void run() {
        StylesUI.processsetcount++;
        SkinManager.instance.setEnabled("style_add_button", false);
        AddStyleUI.instance.addstyleokbutton.setEnabled(false);
        SongLinkedList[] removefromstylesongs2 = removefromstylesongs;
        TreePath[] paths = removefromstylestree.getSelectionPaths();
        for (int j = 0; j < removefromstylesongs2.length; ++j) {
          try {
            SongLinkedList editedsong = removefromstylesongs2[j];
            boolean changed = false;
            
            for (int p = 0; p < paths.length; ++p) {
                MyMutableStyleNode node = (MyMutableStyleNode)paths[p].getLastPathComponent();
                StyleLinkedList styleiter = node.getStyle();
                if (styleiter.containsDirect(editedsong)) {
                    if (styleiter.containsSong(editedsong.uniquesongid))
                        styleiter.removeSong(editedsong);
                  changed = true;
                  styleiter.insertExcludeSong(editedsong);
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

    private void setupActionListeners() {
       removefromstylesclearbt.addActionListener(this);
       removefromstylesokbutton.addActionListener(this);
       removefromstylescancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == removefromstylesclearbt) {
          removefromstylestree.clearSelection();
      } else if (ae.getSource() == removefromstylesokbutton) {
              new ProcessRemoveFromStyles().start();
              setVisible(false);
      } else if (ae.getSource() == removefromstylescancelbutton) {
              setVisible(false);
      }
    }

    public boolean PreDisplay() {
      removefromstylestree.clearSelection();
      removefromstylestree.minimizeTree();
      return true;
    }

}

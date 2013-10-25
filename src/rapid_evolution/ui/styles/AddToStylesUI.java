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
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.RETree;
import rapid_evolution.ui.StyleClearSelectionHandler;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class AddToStylesUI extends REDialog implements ActionListener {
    public AddToStylesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddToStylesUI instance = null;
    public RETree addtostylestree = new RETree();
    public JButton addtostylesclearbt = new REButton();
    public JButton addtostylesokbutton = new REButton();
    public JButton addtostylescancelbutton = new REButton();

    private void setupDialog() {
        addtostylesclearbt.setEnabled(false);
        
        addtostylestree.setRootVisible(false);
        StylesUI.addTree(addtostylestree, style_tree_map);
        addtostylestree.getSelectionModel().addTreeSelectionListener(new StyleClearSelectionHandler(addtostylestree, addtostylesclearbt));

    }
    
    public Map style_tree_map = java.util.Collections.synchronizedMap(new HashMap());

    private void setupActionListeners() {
       addtostylesclearbt.addActionListener(this);
       addtostylesokbutton.addActionListener(this);
       addtostylescancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addtostylesclearbt) {
              addtostylestree.clearSelection();
      } else if (ae.getSource() == addtostylesokbutton) {
          TreePath[] paths = addtostylestree.getSelectionPaths();
          StyleLinkedList[] styles = new StyleLinkedList[paths.length];
          for (int p = 0; p < paths.length; ++p) {
              MyMutableStyleNode node = (MyMutableStyleNode)paths[p].getLastPathComponent();
              StyleLinkedList styleiter = node.getStyle();
              styles[p] = styleiter;
          }          
              new AddProcessSetStyles(styles, songs).start();
              setVisible(false);
      } else if (ae.getSource() == addtostylescancelbutton) {
              setVisible(false);
      }
    }
    
    private SongLinkedList[] songs;

    public boolean PreDisplay() {
      addtostylestree.clearSelection();
      addtostylestree.minimizeTree();
      songs = ( SongLinkedList[])display_parameter;
      return true;
    }

}

package rapid_evolution.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RETree;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.MyStyleTreeRenderer;
import rapid_evolution.ui.styles.StylesUI;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class StylesPane implements ActionListener {

    private static Logger log = Logger.getLogger(StylesPane.class);

    public StylesPane() {
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    static public StylesPane instance = null;
    public JPanel stylepanel = new JPanel();
    public RETree styletree = new RETree();
    static public JCheckBox dynamixstylescheckbox = new RECheckBox();
    public JButton clearstylesbutton = new REButton();
    public JButton deletestylebutton = new REButton();
    public JButton editstylebutton = new REButton();
    static public boolean disableautohighlight = false;

    private void setupDialog() {
        // styles panel
        
        styletree.setRootVisible(false);        
        StylesUI.addTree(styletree, style_tree_map);
        
//        MouseListener[] listeners = styletree.getMouseListeners();
//        for (int l = 0; l < listeners.length; ++l) {
//            System.out.println(listeners[l].getClass());
//            styletree.removeMouseListener(listeners[l]);
//        }
        styletree.addTreeSelectionListener(new StyleTreeSelectionHandler());
        if (!disableautohighlight) styletree.setCellRenderer(new MyStyleTreeRenderer(styletree));
        
        // other:
        clearstylesbutton.setEnabled(false);
        deletestylebutton.setEnabled(false);
    }

    public int getNumSelectedStyles() {
        if (styletree.isSelectionEmpty()) return 0;
        return styletree.getSelectionPaths().length;        
    }

    public Map style_tree_map = java.util.Collections.synchronizedMap(new HashMap());
    
    public boolean isMemberOfCurrentStyle(SongLinkedList song) {    
        boolean ismember = isMemberOfCurrentStyleSub(song);
        if (ismember) {
            // filter required
            StyleLinkedList siter = SongDB.instance.masterstylelist;
            while ((siter != null) && ismember) {
                if (siter.isRequired() && !siter.containsLogical(song))
                    ismember = false;
                siter = siter.next;
            }
            if (ismember) {
                // filter excluded
                siter = SongDB.instance.masterstylelist;
                while ((siter != null) && ismember) {
                    if (siter.isExcluded() && siter.containsLogical(song))
                        ismember = false;
                    siter = siter.next;
                }
            }
        }
        return ismember;
    }
    public boolean isMemberOfCurrentStyleSub(SongLinkedList song) {    
        if (styletree.isSelectionEmpty()) return true;
        if (OptionsUI.instance.strictstylessearch.isSelected()) {
            TreePath[] treepaths = styletree.getSelectionPaths();
            for (int i = 0; i < treepaths.length; ++i) {
                MyMutableStyleNode node = (MyMutableStyleNode)treepaths[i].getLastPathComponent();
                if (!node.getStyle().containsLogical(song)) return false;
            }
            return true;
        } else {
            TreePath[] treepaths = styletree.getSelectionPaths();
            for (int i = 0; i < treepaths.length; ++i) {
                MyMutableStyleNode node = (MyMutableStyleNode)treepaths[i].getLastPathComponent();
                if (node.getStyle().containsLogical(song)) return true;
            }
            return false;
        }        
    }

    private void setupActionListeners() {
        clearstylesbutton.addActionListener(this);
        deletestylebutton.addActionListener(this);
        dynamixstylescheckbox.addActionListener(this);
        editstylebutton.addActionListener(this);
    }
     
    public void highlightCurrentSongStyles() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() { 
            log.debug("highlightCurrentSongStyles(): begin");
            if (RapidEvolutionUI.instance.currentsong == null) return;
            StyleLinkedList siter = SongDB.instance.masterstylelist;
            styletree.clearSelection();          
            int count = 0;
            while (siter != null) {
              if (!siter.isExcluded() && !siter.isRequired() && siter.containsLogical(RapidEvolutionUI.instance.currentsong)) {
                  Vector nodes = (Vector)style_tree_map.get(siter);
                  if (nodes != null) {
                      for (int j = 0; j < nodes.size(); ++j) {
                          MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(j);
                          log.debug("highlightCurrentSongStyles(): adding selection path to node=" + node);
                          styletree.addSelectionPath(new TreePath(node.getPath()));
                      }
                  }
              }
              count++;
              siter = siter.next;
            }
            styletree.minimizeTree();
            log.debug("highlightCurrentSongStyles(): end");        
        } });
    }
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == clearstylesbutton) {
          StyleLinkedList siter = SongDB.instance.masterstylelist;
          while (siter != null) {
              siter.setRequired(false);
              siter.setExcluded(false);
              siter = siter.next;
          }
          styletree.clearSelection();   
          styletree.repaint();
          clearstylesbutton.setEnabled(false);
      } else if (ae.getSource() == dynamixstylescheckbox) {
        if (dynamixstylescheckbox.isSelected()) {
            highlightCurrentSongStyles();
        }
      } else if (ae.getSource() == deletestylebutton) {
          deleteSelectedStyles(styletree);
        } else if (ae.getSource() == editstylebutton) {                            
            if (styletree.getSelectionPath() != null) {
                MyMutableStyleNode selected_node = (MyMutableStyleNode)styletree.getSelectionPath().getLastPathComponent();
                EditStyleUI.instance.display_parameter = selected_node.getStyle();
                EditStyleUI.instance.Display();
            }                
        }
    }
    
    static public void deleteSelectedStyles(RETree tree) {
        boolean selected = !tree.isSelectionEmpty();
        if (selected) {
          int n = IOptionPane.showConfirmDialog(
              SkinManager.instance.getFrame("main_frame"),
              SkinManager.instance.getDialogMessageText("delete_styles"),
              SkinManager.instance.getDialogMessageTitle("delete_styles"),
              IOptionPane.YES_NO_OPTION);
          if (n == 0) {
              DefaultTreeModel dlm1 = (DefaultTreeModel)tree.getModel();
              StyleLinkedList siter = SongDB.instance.masterstylelist;
              while (siter != null) {
                  if (tree.isADirectSelectedStyle(siter)) {
                      if (siter.getChildStyles().length > 0) {
                          String text = SkinManager.instance.getDialogMessageText("delete_style_with_children");
                          text = rapid_evolution.StringUtil.ReplaceString("%style%", text, siter.getName());
                          IOptionPane.showMessageDialog(
                                  SkinManager.instance.getFrame("main_frame"),
                                  text,
                                  SkinManager.instance.getDialogMessageTitle("delete_style_with_children"),
                                  IOptionPane.ERROR_MESSAGE);
                      } else {
	                      if ((EditStyleUI.instance.isVisible()) && (EditStyleUI.instance.editedstyle.equals(siter))) EditStyleUI.instance.setVisible(false);
	                      SongDB.instance.RemoveStyle( siter);
	
	                      StylesUI.removeStyle(siter);
	
	                      SongDB.instance.num_styles--;                      
                      }
                  }                  
                  siter = siter.next;
              }
            }
          }
        }        
    
	        
}

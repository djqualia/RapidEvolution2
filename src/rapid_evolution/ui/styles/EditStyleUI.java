package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REList;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.StylesPane;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class EditStyleUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(EditStyleUI.class);

    public EditStyleUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupSubDialogs();
        setupActionListeners();
    }

    public static EditStyleUI instance = null;
    public JButton editstyleokbutton = new REButton();
    public JTextField editstylenamefield = new RETextField();
    public JTextField styledescription = new RETextField();
    public JCheckBox categoryOnlyCheckBox = new RECheckBox();
    public JPanel editstylekeywordpanel = new JPanel();
    public REList editstylekeywordslist = new REList();
    public static JCheckBox showexcludekeywords = new RECheckBox();
    public JButton editstyleremovekeywordbutton = new REButton();
    public JPanel editstyleexcludekeywordpanel = new JPanel();
    public REList styleexcludelist = new REList();
    public JButton removestyleexclude = new REButton();

    public ListStyleSongsUI liststylesongs_ui;
    public AddStyleSongsUI addstylesongs_ui;
    public ExcludeStyleSongsUI excludestylesongs_ui;
    public AddStyleKeywordUI addstylekeyword_ui;
    public AddStyleExcludeKeywordUI addstyleexcludekeyword_ui;

    public StyleLinkedList editedstyle;

    private void setupDialog() {
        // edit style dialog
        removestyleexclude.setEnabled(false);
        editstyleremovekeywordbutton.setEnabled(false);
        //editstylekeywordslist.setSelectionModel(new ToggleSelectionModel());
        editstylekeywordslist.setModel(new DefaultListModel());
        styleexcludelist.setModel(new DefaultListModel());
        editstylekeywordslist.addListSelectionListener(new EditStyleListener());
        styleexcludelist.addListSelectionListener(new EditStyleExcludeListener());
        KeyListener[] listeners = editstylekeywordslist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) editstylekeywordslist.removeKeyListener(listeners[i]);
        editstylekeywordslist.addKeyListener(new StyleIncludeKeyListener());
        listeners = styleexcludelist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) styleexcludelist.removeKeyListener(listeners[i]);
        styleexcludelist.addKeyListener(new StyleExcludeKeyListener());
        editstylekeywordslist.addMouseListener(new StyleIncludeMouse());
        styleexcludelist.addMouseListener(new StyleExcludeMouse());
    }

    public void PostLoadInit() {
    }

    private void setupSubDialogs() {
        liststylesongs_ui = new ListStyleSongsUI("edit_style_song_list_dialog");
        addstylesongs_ui = new AddStyleSongsUI("style_include_add_songs_dialog");
        excludestylesongs_ui = new ExcludeStyleSongsUI("style_exclude_add_songs_dialog");
        addstylekeyword_ui = new AddStyleKeywordUI("style_include_add_keyword_dialog");
        addstyleexcludekeyword_ui = new AddStyleExcludeKeywordUI("style_exclude_add_keyword_dialog");

    }

    class EditStyleListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
          if (editstylekeywordslist.getSelectedIndex() >= 0) editstyleremovekeywordbutton.setEnabled(true);
          else editstyleremovekeywordbutton.setEnabled(false);

          if (editstylekeywordslist.getSelectedIndices().length > 1) {
              StyleIncludeMouse.instance.edititem.setEnabled(false);
              StyleIncludeMouse.instance.playselection.setEnabled(true);
              StyleIncludeMouse.instance.removeselection.setEnabled(true);
          } else if (editstylekeywordslist.getSelectedIndices().length == 1) {
              try {
                Long val = (Long)editedstyle.styleincludevector.get(editstylekeywordslist.getSelectedIndex());
                StyleIncludeMouse.instance.edititem.setEnabled(true);
              } catch (Exception e2) {
                StyleIncludeMouse.instance.edititem.setEnabled(false);
              }
              StyleIncludeMouse.instance.playselection.setEnabled(true);
              StyleIncludeMouse.instance.removeselection.setEnabled(true);
          } else {
              StyleIncludeMouse.instance.edititem.setEnabled(false);
              StyleIncludeMouse.instance.playselection.setEnabled(false);
              StyleIncludeMouse.instance.removeselection.setEnabled(false);
          }
        }
    }

    class EditStyleExcludeListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (styleexcludelist.getSelectedIndex() >= 0) removestyleexclude.setEnabled(true);
            else removestyleexclude.setEnabled(false);
            if (styleexcludelist.getSelectedIndices().length > 1) {
                StyleExcludeMouse.instance.edititem.setEnabled(false);
                StyleExcludeMouse.instance.playselection.setEnabled(true);
                StyleExcludeMouse.instance.removeselection.setEnabled(true);
            } else if (styleexcludelist.getSelectedIndices().length == 1) {
                try {
                  Long val = (Long)editedstyle.styleexcludevector.get(styleexcludelist.getSelectedIndex());
                  StyleExcludeMouse.instance.edititem.setEnabled(true);
                } catch (Exception e2) {
                  StyleExcludeMouse.instance.edititem.setEnabled(false);
                }
                StyleExcludeMouse.instance.playselection.setEnabled(true);
                StyleExcludeMouse.instance.removeselection.setEnabled(true);
            } else {
                StyleExcludeMouse.instance.edititem.setEnabled(false);
                StyleExcludeMouse.instance.playselection.setEnabled(false);
                StyleExcludeMouse.instance.removeselection.setEnabled(false);
            }
        }
    }

    private void setupActionListeners() {
        editstyleokbutton.addActionListener(this);
        removestyleexclude.addActionListener(this);
        editstyleremovekeywordbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == editstyleokbutton) {          
        if (!editstylenamefield.getText().equals(oldeditedstylename)) {

            StyleLinkedList siter = SongDB.instance.masterstylelist;
            int oldindex = -1;
            int count = 0;          
            while (siter != null) {
              if (siter == editedstyle) oldindex = count;
              siter = siter.next;
              count++;
            }
            if (oldindex != -1) {
                try {
                    SongDB.instance.UpdateSongStyleArraysSem.acquire();
                    SongDB.instance.StyleSIndexSem.acquire();
                    editedstyle.setName(editstylenamefield.getText());
                    OptionsUI.instance.filter1.renameStyle(editedstyle);
                    OptionsUI.instance.filter2.renameStyle(editedstyle);
                    OptionsUI.instance.filter3.renameStyle(editedstyle);
                    SongDB.instance.masterstylelist = SongDB.instance.sortStyleList(SongDB.instance.masterstylelist);
                    siter = SongDB.instance.masterstylelist;
                    count = 0;
                    int newindex = -1;
                    while (siter != null) {
                      if (siter.equals(editedstyle)) newindex = count;
                      count++;
                      siter = siter.next;
                    }
                    SongLinkedList iter = SongDB.instance.SongLL;
                    while (iter != null) {
                      boolean[] newbool = new boolean[SongDB.instance.num_styles];
                      count = 0;
                      for (int i = 0; i < SongDB.instance.num_styles; ++i) {
                        if (count == oldindex) count++;
                        if (i == newindex) newbool[i] = iter.getStyle(oldindex);
                        else newbool[i] = iter.getStyle(count++);
                      }
                      iter.setStyles(newbool);
                      iter = iter.next;
                    }
                    
                    StylesUI.renameStyle(editedstyle);
                                  
                  } catch (Exception e) { log.error("actionPerformed(): error", e);
                  } finally {
                	  SongDB.instance.StyleSIndexSem.release();
                	  SongDB.instance.UpdateSongStyleArraysSem.release();
                  }
                
            }
        }
        setVisible(false);
        editstyleremovekeywordbutton.setEnabled(false);
//      new UpdateStyleThread(editedstyle).start();
        editedstyle.setDescription(styledescription.getText());
        editedstyle.setCategoryOnly(categoryOnlyCheckBox.isSelected());
        editedstyle = null;
      } else if (ae.getSource() == editstyleremovekeywordbutton) {
          RemoveSelectedIncludeKeywords();
      } else if (ae.getSource() == removestyleexclude) {
          RemoveSelectedExcludeKeywords();
      }
    }

    public String oldeditedstylename = null;

    public boolean PreDisplay() {
        try {
            StyleLinkedList editstyle = (StyleLinkedList)display_parameter; 
            if (editstyle == null) {
                if (StylesPane.instance.styletree.getSelectionPath() != null) {
                    MyMutableStyleNode selected_node = (MyMutableStyleNode)StylesPane.instance.styletree.getSelectionPath().getLastPathComponent();
                    editstyle = selected_node.getStyle();
                }
            }
            if (editstyle != null) {
		      editstylenamefield.setText(editstyle.getName());
		      styledescription.setText(editstyle.getDescription());
              categoryOnlyCheckBox.setSelected(editstyle.isCategoryOnly());
		      oldeditedstylename = editstyle.getName();
		      StyleLinkedList siter = SongDB.instance.masterstylelist;
		      editedstyle = editstyle;
		      return true;
            } else return false;
        } catch (Exception e) {
            log.error("PreDisplay(): error", e);
        }
        return false;
    }

    public void PostDisplay() {
      StylesUI.RepopulateIncludeStyleList();
      StylesUI.RepopulateExcludeStyleList();
    }

    class EditStyleKeywordListner extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == e.VK_ESCAPE) || (e.getKeyCode() == e.VK_ENTER)) {
          setVisible(false);
        }
      }
    }

    public void RemoveSelectedIncludeKeywords() {
        if (editstylekeywordslist.getSelectedIndex() >= 0) {
          int n = IOptionPane.showConfirmDialog(
              getDialog(),
              SkinManager.instance.getDialogMessageText("delete_style_include_keywords"),
              SkinManager.instance.getDialogMessageTitle("delete_style_include_keywords"),
              IOptionPane.YES_NO_OPTION);
          if (n == 0) {
            for (int i = 0; i < editedstyle.getTotalEntries(); ++i) {
              if (editstylekeywordslist.isSelectedIndex(i)) {
                try {
                  long value = ((Long)editedstyle.styleincludevector.get(i)).longValue();
                  editedstyle.removeSong(value);
                  i--;
                } catch (Exception e) {
                  try {
                    String value = (String)editedstyle.styleincludevector.get(i);
                    editedstyle.removeKeyword(value);
                    i--;
                  } catch (Exception e2) { }
                }
              }
            }
          }
        }
    }

    public void RemoveSelectedExcludeKeywords() {
        if (styleexcludelist.getSelectedIndex() >= 0) {
          int n = IOptionPane.showConfirmDialog(
              getDialog(),
              SkinManager.instance.getDialogMessageText("delete_style_exclude_keywords"),
              SkinManager.instance.getDialogMessageTitle("delete_style_exclude_keywords"),
              IOptionPane.YES_NO_OPTION);
          if (n == 0) {
            for (int i = 0; i < editedstyle.getTotalExcludeEntries(); ++i) {
              if (styleexcludelist.isSelectedIndex(i)) {
                try {
                  long value = ((Long)editedstyle.styleexcludevector.get(i)).longValue();
                  editedstyle.removeExcludeSong(value);
                  --i;
                } catch (Exception e) {
                  try {
                    String value = (String)editedstyle.styleexcludevector.get(i);
                    editedstyle.removeExcludeKeyword(value);
                    --i;
                  } catch (Exception e2) { }
                }
              }
            }
          }
        }
    }
}

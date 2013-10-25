package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Point;
import rapid_evolution.SongLinkedList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import rapid_evolution.SearchParser;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.StyleIncludeKeyListener;
import rapid_evolution.ui.styles.StyleIncludeMouse;
import java.awt.event.KeyListener;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.REList;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class MixStartsWithUI extends REDialog implements ActionListener {
    public MixStartsWithUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static MixStartsWithUI instance = null;

    public REList mixstartswithlist = new REList();
    public JButton mixstartsearchbutton = new REButton();
    public JButton mixstartcancelbutton = new REButton();
    public JButton mixstartokbutton = new REButton();
    public JTextField mixstartsearchfield = new RETextField();

    public Vector mixstartsearchsongvector = null;

    private void setupDialog() {
        mixstartswithlist.setModel(new DefaultListModel());
        mixstartswithlist.addMouseListener(new MixStartMouseHandler());
        mixstartsearchfield.addKeyListener(new MixStartsKeyListener());
        KeyListener[] listeners = mixstartswithlist.getKeyListeners();
        for (int i = 0; i < listeners.length; ++i) mixstartswithlist.removeKeyListener(listeners[i]);
        mixstartswithlist.addKeyListener(new MixSetStartsWithKeyListener());
    }

    class MixStartMouseHandler extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          Point pt = e.getPoint();
          DefaultListModel dlm = (DefaultListModel) mixstartswithlist.getModel();
          int index = mixstartswithlist.locationToIndex(pt);
          MixGeneratorUI.instance.startswith = (SongLinkedList)mixstartsearchsongvector.get(index);
          MixGeneratorUI.instance.usestartswith = true;
          MixGeneratorUI.instance.startswithfield.setText(MixGeneratorUI.instance.startswith.getSongId());
          setVisible(false);
          MixGeneratorUI.instance.clearstartswith.setEnabled(true);
        }
      }
    }

    class MixStartsKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          DefaultListModel dlm = (DefaultListModel) mixstartswithlist.getModel();
          dlm.removeAllElements();
          SearchParser include = new SearchParser(mixstartsearchfield.getText());
          mixstartsearchsongvector = new Vector();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
              dlm.addElement(iter.getSongId());
              mixstartsearchsongvector.add(iter);
            }
            iter = iter.next;
          }
        }
      }
    }

    private void setupActionListeners() {
        mixstartokbutton.addActionListener(this);
        mixstartcancelbutton.addActionListener(this);
        mixstartsearchbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == mixstartsearchbutton) {
        DefaultListModel dlm = (DefaultListModel) mixstartswithlist.getModel();
        dlm.removeAllElements();
        SearchParser include = new SearchParser(mixstartsearchfield.getText());
        mixstartsearchsongvector = new Vector();
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          if (include.getStatus(iter.getSongId()) || include.getStatus(iter.getComments())) {
            dlm.addElement(iter.getSongId());
            mixstartsearchsongvector.add(iter);
          }
          iter = iter.next;
        }
      } else if (ae.getSource() == mixstartcancelbutton) {
        CancelMixSetStartsWith();
      } else if (ae.getSource() == mixstartokbutton) {
          MixStartsOk();
      }
    }

    public void CancelMixSetStartsWith() {
        setVisible(false);
    }

    public void MixStartsOk() {
      int index = mixstartswithlist.getSelectedIndex();
      if (index >= 0) {
          MixGeneratorUI.instance.startswith = (SongLinkedList) mixstartsearchsongvector.
                                 get(index);
          MixGeneratorUI.instance.usestartswith = true;
          MixGeneratorUI.instance.startswithfield.setText(MixGeneratorUI.instance.startswith.getSongId());
          MixGeneratorUI.instance.clearstartswith.setEnabled(true);
      }
      setVisible(false);
    }

    public class MixSetStartsWithKeyListener extends KeyAdapter {
      String quickstrokestartswith = new String("");
      long lastkeystroke = 0;

      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_BACK_SPACE) {
          if (quickstrokestartswith.length() >= 1) quickstrokestartswith = quickstrokestartswith.substring(0, quickstrokestartswith.length() - 1);
          return;
        } else if (e.getKeyCode() == e.VK_SPACE) {
          if (quickstrokestartswith.length() >= 1) quickstrokestartswith += " ";
          return;
        } else if (e.getKeyCode() == e.VK_ENTER) {
          MixStartsOk();
        } else if (e.getKeyCode() == e.VK_ESCAPE) {
          CancelMixSetStartsWith();
        }

        char c = Character.toLowerCase(e.getKeyChar());
        if (((c >= 'a') && (c <= 'z')) || ((c >= '0') && (c <= '9')) || (c == '.') || (c == ',') || (c == '-') || (c == '_') || (c == '+') || (c == '\'') || (c == ':') || (c == ';') || (c == '\\') || (c == '/') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '`') || (c == '!') || (c == '@') || (c == '#') || (c == '$') || (c == '%') || (c == '^') || (c == '&') || (c == '*') || (c == '(') || (c == ')') || (c == '|') || (c == '~')) {
          if ((lastkeystroke == 0) || ((System.currentTimeMillis() - lastkeystroke) < 1500)) quickstrokestartswith += c;
          else quickstrokestartswith =  new String("" + c);
          lastkeystroke = System.currentTimeMillis();
          DefaultListModel dlm = (DefaultListModel) mixstartswithlist.getModel();
          int start = mixstartswithlist.getSelectedIndex();
          int index = start;
          if (index >= dlm.getSize()) index = 0;
          if (index < 0) index = 0;
          boolean found = false;
          while (!found) {
            String value = ((String)dlm.getElementAt(index)).toLowerCase();
            if (value.startsWith(quickstrokestartswith)) found = true;
            else index++;
            if (index >= dlm.getSize()) index = 0;
            if (index == start) found = true;
          }
          if (index != start) {
            mixstartswithlist.setSelectedIndex(index);
            mixstartswithlist.ensureIndexIsVisible(index);
          }
        }
      }
    }
}

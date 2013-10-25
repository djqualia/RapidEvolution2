package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JTextField;
import rapid_evolution.RapidEvolution;
import java.awt.event.KeyAdapter;
import javax.swing.JLabel;
import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class AddStyleKeywordUI extends REDialog implements ActionListener {
    public AddStyleKeywordUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddStyleKeywordUI instance = null;
    public JTextField addkeywordfield = new RETextField();
    public JButton addkeywordmorebutton = new REButton();
    public JButton addkeywordokbutton = new REButton();
    public JButton addkeywordcancelbutton = new REButton();

    private void setupDialog() {
      // add keyword dialog
      addkeywordfield.addKeyListener(new StyleKeywordKeyListener());
    }

    class StyleKeywordKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ESCAPE) {
          setVisible(false);
          EditStyleUI.instance.requestFocus();
        } else if (e.getKeyCode() == e.VK_ENTER) {
          SongDB.instance.addStyleKeyword(EditStyleUI.instance.editedstyle, addkeywordfield.getText());
          addkeywordfield.setText("");
          addkeywordfield.requestFocus();
        }
      }
    }

    public void PostInit() {
      if (!RapidEvolution.instance.loaded) addKeyListener(new StyleKeywordKeyListener());
    }

    private void setupActionListeners() {
        addkeywordokbutton.addActionListener(this);
        addkeywordcancelbutton.addActionListener(this);
        addkeywordmorebutton.addActionListener(this);

    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addkeywordcancelbutton) {
        setVisible(false);
      } else if (ae.getSource() == addkeywordokbutton) {
        SongDB.instance.addStyleKeyword(EditStyleUI.instance.editedstyle, addkeywordfield.getText());
        setVisible(false);
      } else if (ae.getSource() == addkeywordmorebutton) {
        SongDB.instance.addStyleKeyword(EditStyleUI.instance.editedstyle, addkeywordfield.getText());
        addkeywordfield.setText("");
        addkeywordfield.requestFocus();
      }
    }

    public boolean PreDisplay() {
      addkeywordfield.setText("");
      return true;
    }

    public void PostDisplay() {
      addkeywordfield.requestFocus();
    }
}

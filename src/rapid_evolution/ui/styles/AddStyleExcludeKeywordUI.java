package rapid_evolution.ui.styles;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import rapid_evolution.RapidEvolution;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class AddStyleExcludeKeywordUI extends REDialog implements ActionListener {
    public AddStyleExcludeKeywordUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddStyleExcludeKeywordUI instance = null;
    public JTextField addexcludekeywordfield = new RETextField();
    public JButton addexcludekeywordokbutton = new REButton();
    public JButton addexcludekeywordcancelbutton = new REButton();
    public JButton addexcludekeywordmorebutton = new REButton();

    private void setupDialog() {
      // add exclude keyword dialog
      addexcludekeywordfield.addKeyListener(new StyleExcludeKeywordKeyListener());
    }

    class StyleExcludeKeywordKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ESCAPE) {
          setVisible(false);
          EditStyleUI.instance.requestFocus();
        } else if (e.getKeyCode() == e.VK_ENTER) {
          SongDB.instance.addStyleExcludeKeyword(EditStyleUI.instance.editedstyle, addexcludekeywordfield.getText());
          addexcludekeywordfield.setText("");
          addexcludekeywordfield.requestFocus();
        }
      }
    }

    private void setupActionListeners() {
        addexcludekeywordokbutton.addActionListener(this);
        addexcludekeywordmorebutton.addActionListener(this);
        addexcludekeywordcancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addexcludekeywordcancelbutton) {
              setVisible(false);
      } else if (ae.getSource() == addexcludekeywordokbutton) {
              SongDB.instance.addStyleExcludeKeyword(EditStyleUI.instance.editedstyle, addexcludekeywordfield.getText());
              setVisible(false);
      } else if (ae.getSource() == addexcludekeywordmorebutton) {
              SongDB.instance.addStyleExcludeKeyword(EditStyleUI.instance.editedstyle, addexcludekeywordfield.getText());
              addexcludekeywordfield.setText("");
              addexcludekeywordfield.requestFocus();
      }
    }

    public void PostDisplay() {
      addexcludekeywordfield.requestFocus();
    }
}

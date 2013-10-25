package rapid_evolution.ui.styles;

import java.awt.event.ActionEvent;
import rapid_evolution.ui.PlayListUI;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.REDialog;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class AddStyleUI extends REDialog implements ActionListener {
    public AddStyleUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddStyleUI instance = null;
    public JButton addstylecancelbutton = new REButton();
    public JButton addstyleokbutton = new REButton();
    public JTextField stylenamefield = new RETextField();

    private void setupDialog() {
        // add style dialog
        stylenamefield.addKeyListener(new StyleKeyListener());
    }

    class StyleKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ESCAPE) {
          setVisible(false);
        } else if (e.getKeyCode() == e.VK_ENTER) {
          setVisible(false);
          String stylename = new String(stylenamefield.getText());
          SongDB.instance.addStyle(stylename, true);
        }
      }
    }

    private void setupActionListeners() {
      addstylecancelbutton.addActionListener(this);
      addstyleokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addstyleokbutton) {
              setVisible(false);
              String stylename = new String(stylenamefield.getText());
              SongDB.instance.addStyle(stylename, true);
      } else if (ae.getSource() == addstylecancelbutton) {
              setVisible(false);
      }
    }

    public void PostDisplay() {
      stylenamefield.requestFocus();
    }

    public boolean PreDisplay() {
      stylenamefield.setText("");
      return true;
    }

}

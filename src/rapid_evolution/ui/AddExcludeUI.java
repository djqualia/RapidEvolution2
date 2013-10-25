package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.main.SearchPane;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class AddExcludeUI extends REDialog implements ActionListener {
    public AddExcludeUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddExcludeUI instance = null;

    public JButton addexcludeyesbutton = new REButton();
    public JButton addexcludenobutton = new REButton();
    public JTextField excludefromfield = new RETextField();
    public JTextField excludetofield = new RETextField();
    public JCheckBox addreverseexcludebt = new RECheckBox();

    private void setupDialog() {
    }

    private void setupActionListeners() {
       addexcludeyesbutton.addActionListener(this);
       addexcludenobutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addexcludeyesbutton) {
         RapidEvolutionUI.instance.ConfirmAddExcludes();
      } else if (ae.getSource() == addexcludenobutton) {
        // cancel add exclude
        Hide();
      }
    }

    public boolean PreDisplay() {
      RapidEvolutionUI.instance.rootsinvoked = 0;
      if (SearchPane.instance.searchtable.getSelectedRowCount() > 1) {
        RapidEvolutionUI.instance.excludesong = new Vector();
        for (int i = 0; i < SearchPane.instance.searchtable.getSelectedRowCount(); ++i) {
          SongLinkedList excludedsong = (SongLinkedList) (SearchPane.instance.searchtable.getModel().getValueAt(((SearchPane.instance.searchtable.getSelectedRows())[i]), SearchPane.instance.searchcolumnconfig.num_columns));
          RapidEvolutionUI.instance.excludesong.add(excludedsong);
        }
        int n = IOptionPane.showConfirmDialog(
            SkinManager.instance.getFrame("main_frame"),
            SkinManager.instance.getDialogMessageText("add_exclude_confirm"),
            SkinManager.instance.getDialogMessageTitle("add_exclude_confirm"),
            IOptionPane.YES_NO_OPTION);
        if (n == 0) {
          RapidEvolutionUI.instance.ConfirmAddExcludes();
        }
        return false;
      }
      addreverseexcludebt.setSelected(false);
      excludefromfield.setText(RapidEvolutionUI.instance.currentsong.getSongIdShort());
      RapidEvolutionUI.instance.excludesong = new Vector();
      SongLinkedList excludedsong = (SongLinkedList) (SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns));
      RapidEvolutionUI.instance.excludesong.add(excludedsong);
      excludetofield.setText(excludedsong.getSongIdShort());
      addreverseexcludebt.setSelected(false);
      return true;
    }

    private void DisplayAction() {
      addexcludenobutton.requestFocus();
    }
}

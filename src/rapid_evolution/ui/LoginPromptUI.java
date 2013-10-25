package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import rapid_evolution.net.MixshareClient;
import rapid_evolution.net.MyConnectThread;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.library.RootMusicDirectoryScanner;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.REPasswordField;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

import rapid_evolution.SongDB;

public class LoginPromptUI extends REDialog implements ActionListener {
    public LoginPromptUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static LoginPromptUI instance = null;
    public JTextField initialusernameprompt = new RETextField();
    public JPasswordField initialpasswordprompt = new REPasswordField();
    public JTextField initialemailprompt = new RETextField();
    public JTextField initialuserwebsite = new RETextField();
    public JButton loginpromptokbutton = new REButton();

    // TO DO: setup dialog if skin doesn't specify so it can't be hacked around
    private void setupDialog() {
            // login prompt dialog
    }

    private void setupActionListeners() {
        loginpromptokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == loginpromptokbutton) {
        if (initialusernameprompt.getText().equals("")) return;
        String passwordstr = new String(initialpasswordprompt.getPassword());
        if (passwordstr.equals("")) return;
        OptionsUI.instance.username.setText(initialusernameprompt.getText());
        OptionsUI.instance.password.setText(passwordstr);// = new JPasswordField(passwordstr);
        OptionsUI.instance.emailaddress.setText(initialemailprompt.getText());
        OptionsUI.instance.userwebsite.setText(initialuserwebsite.getText());
        setVisible(false);
        MixshareClient.instance.servercommand = new MyConnectThread();
        MixshareClient.instance.servercommand.start();
        if (SongDB.instance.SongLL == null) {
            IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                    SkinManager.instance.getDialogMessageText("new_library_message"),
                  SkinManager.instance.getDialogMessageTitle("new_library_message"),
                  IOptionPane.INFORMATION_MESSAGE);
            JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
            File tmp = fc.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
          	  OptionsUI.instance.rootMusicDirectory.setText(tmp.getAbsolutePath());
        	  new RootMusicDirectoryScanner().start();          	  
            }    	          	
        }
      }
    }

    public boolean PreDisplay() {
      initialusernameprompt.setText(OptionsUI.instance.username.getText());
      initialpasswordprompt.setText(new String(OptionsUI.instance.password.
                                               getPassword()));
      initialemailprompt.setText(OptionsUI.instance.emailaddress.getText());
      initialuserwebsite.setText(OptionsUI.instance.userwebsite.getText());
      return true;
    }

}

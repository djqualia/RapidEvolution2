package rapid_evolution.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rapid_evolution.Mixout;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongUtil;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class EditMixoutUI extends REDialog implements ActionListener, FocusListener {
    public EditMixoutUI(String id ) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static EditMixoutUI instance = null;
    public JButton editmixoutcancelbutton = new REButton();
    public JButton editmixoutokbutton = new REButton();
    public JTextField editmixoutfromfield = new RETextField();
    public JTextField editmixouttofield = new RETextField();
    public JButton editmixoutcalcbpmdiffbutton = new REButton();
    public JTextArea editmixoutcommentsfield = new RETextArea();
    public JTextField editmixoutbpmdifffield = new RETextField();
    public JTextField editmixoutscorefield = new RETextField();
    public JCheckBox editmixoutaddoncb = new RECheckBox();

    private void setupDialog() {
      // add mixout dialog
      editmixoutbpmdifffield.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          editmixoutbpmdifffield.setText(StringUtil.validate_bpmdiff(editmixoutbpmdifffield.getText()));
        }
      });
      editmixoutscorefield.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          editmixoutscorefield.setText(StringUtil.validate_rank(editmixoutscorefield.getText()));
        }});

      editmixoutcommentsfield.addFocusListener(this);
      editmixoutscorefield.addFocusListener(this);
      editmixoutbpmdifffield.addFocusListener(this);
      editmixoutaddoncb.addFocusListener(this);
      editmixoutcalcbpmdiffbutton.addFocusListener(this);
    
      EditMixoutKeyListener keyListener = new EditMixoutKeyListener();
      editmixoutcancelbutton.addKeyListener(keyListener);
      editmixoutokbutton.addKeyListener(keyListener);
      editmixoutfromfield.addKeyListener(keyListener);
      editmixouttofield.addKeyListener(keyListener);
      editmixoutcalcbpmdiffbutton.addKeyListener(keyListener);
      editmixoutcommentsfield.addKeyListener(keyListener);
      editmixoutbpmdifffield.addKeyListener(keyListener);
      editmixoutscorefield.addKeyListener(keyListener);
      editmixoutaddoncb.addKeyListener(keyListener);
      
    }

    private void setupActionListeners() {
       editmixoutcalcbpmdiffbutton.addActionListener(this);
       editmixoutokbutton.addActionListener(this);
       editmixoutcancelbutton.addActionListener(this);
    }
    
    int lastfocus = 0;
    public void focusGained(FocusEvent fe) {
        if (fe.getSource() == editmixoutcommentsfield) lastfocus = 0;
        else if (fe.getSource() == editmixoutscorefield) lastfocus = 1;            
        else if (fe.getSource() == editmixoutbpmdifffield) lastfocus = 2;            
        else if (fe.getSource() == editmixoutaddoncb) lastfocus = 3;            
        else if (fe.getSource() == editmixoutcalcbpmdiffbutton) lastfocus = 4;            
    }
    public void focusLost(FocusEvent fe) {
        
    }
    
    public void closeEditMixoutDialog() {
        if (isVisible()) {
            try {
                fromsong.setMixoutDetails(tosong, editmixoutcommentsfield.getText(), Float.parseFloat(editmixoutbpmdifffield.getText()), Integer.parseInt(editmixoutscorefield.getText()), editmixoutaddoncb.isSelected());
                setVisible(false);
              } catch (Exception e) {
                  IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                   SkinManager.instance.getDialogMessageText("invalid_mixout_field"),
                  SkinManager.instance.getDialogMessageTitle("invalid_mixout_field"),
                  IOptionPane.ERROR_MESSAGE);
              }        
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == editmixoutokbutton) {
          closeEditMixoutDialog();
      } else if (ae.getSource() == editmixoutcalcbpmdiffbutton) {
        float usebpm = fromsong.getStartbpm();
        if (fromsong.getEndbpm() != 0) usebpm = fromsong.getEndbpm();
        float bpmdiff = SongUtil.get_bpmdiff(tosong.getStartbpm(), usebpm);
        editmixoutbpmdifffield.setText(StringUtil.getBpmDiffText(bpmdiff));
      } else if (ae.getSource() == editmixoutcancelbutton) {
        setVisible(false);
      }
    }

    private SongLinkedList fromsong;
    private SongLinkedList tosong;

    public boolean PreDisplay() {
      Mixout mixout = (Mixout)display_parameter;
      fromsong = mixout.fromsong;
      tosong = mixout.tosong;
      for (int i = 0; i < fromsong.getNumMixoutSongs(); ++i) {
        if (fromsong.mixout_songs[i] == tosong.uniquesongid) {
          editmixoutfromfield.setText(fromsong.getSongIdShort());
          editmixouttofield.setText(tosong.getSongIdShort());
          editmixoutcommentsfield.setText(fromsong.getMixoutComments(i));
          editmixoutbpmdifffield.setText(StringUtil.getBpmDiffText(fromsong.
              getMixoutBpmdiff(i)));
          editmixoutscorefield.setText(String.valueOf(fromsong.getMixoutRank(i)));
          editmixoutaddoncb.setSelected(fromsong.getMixoutAddon(i));
        }
      }
      return true;
    }

    public void PostDisplay() {
        if (lastfocus == 0) editmixoutcommentsfield.requestFocus();
        else if (lastfocus == 1) editmixoutscorefield.requestFocus();
        else if (lastfocus == 2) editmixoutbpmdifffield.requestFocus();
        else if (lastfocus == 3) editmixoutaddoncb.requestFocus();
        else if (lastfocus == 4) editmixoutcalcbpmdiffbutton.requestFocus();
    }
}

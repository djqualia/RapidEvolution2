package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import rapid_evolution.RapidEvolution;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import rapid_evolution.SongUtil;
import rapid_evolution.ui.main.*;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.REDialog;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongDB;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class AddMixoutUI extends REDialog implements ActionListener, FocusListener {
    public AddMixoutUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddMixoutUI instance = null;
    public JButton addmixoutcancelbutton = new REButton();
    public JButton addmixoutokbutton = new REButton();
    public JTextField addmixoutfromfield = new RETextField();
    public JTextField addmixouttofield = new RETextField();
    public JButton addmixoutcalcbpmdiffbutton = new REButton();
    public JTextArea addmixoutcommentsfield = new RETextArea();
    public JTextField addmixoutbpmdifffield = new RETextField();
    public JTextField addmixoutscorefield = new RETextField();
    public JCheckBox addmixoutaddoncb = new RECheckBox();

    private void setupDialog() {
      // add mixout dialog
      addmixoutbpmdifffield.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          addmixoutbpmdifffield.setText(StringUtil.validate_bpmdiff(addmixoutbpmdifffield.getText()));
        }
      });
      addmixoutscorefield.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          addmixoutscorefield.setText(StringUtil.validate_rank(addmixoutscorefield.getText()));
        }});
      
      addmixoutcommentsfield.addFocusListener(this);
      addmixoutscorefield.addFocusListener(this);
      addmixoutbpmdifffield.addFocusListener(this);
      addmixoutaddoncb.addFocusListener(this);
      addmixoutcalcbpmdiffbutton.addFocusListener(this);

    }

    int lastfocus = 0;
    public void focusGained(FocusEvent fe) {
        if (fe.getSource() == addmixoutcommentsfield) lastfocus = 0;
        else if (fe.getSource() == addmixoutscorefield) lastfocus = 1;            
        else if (fe.getSource() == addmixoutbpmdifffield) lastfocus = 2;            
        else if (fe.getSource() == addmixoutaddoncb) lastfocus = 3;            
        else if (fe.getSource() == addmixoutcalcbpmdiffbutton) lastfocus = 4;            
    }
    public void focusLost(FocusEvent fe) {
        
    }
    
    private void setupActionListeners() {
       addmixoutcalcbpmdiffbutton.addActionListener(this);
       addmixoutokbutton.addActionListener(this);
       addmixoutcancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == addmixoutokbutton) {
        MixoutPane.instance.ConfirmAddMixout();
      } else if (ae.getSource() == addmixoutcalcbpmdiffbutton) {
        float usebpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
        if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0.0f) usebpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
        if ((usebpm != 0.0f) && (addmixoutsong.getStartbpm() != 0.0f)) {
            float bpmdiff = SongUtil.get_bpmdiff(addmixoutsong.getStartbpm(), usebpm);
            String bpmtext = String.valueOf(bpmdiff);
            if (bpmdiff > 0) bpmtext = new String("+" + bpmtext);
            int length = bpmtext.length();
            if (length > 6) length = 6;
            addmixoutbpmdifffield.setText(bpmtext.substring(0,length));
        }
      } else if (ae.getSource() == addmixoutcancelbutton) {
        Hide();
      }
    }


    public int source_id;
    public boolean PreDisplay(Object source) {
      if (source == SkinManager.instance.getObject("add_mixout_button")) {
        if (SearchPane.instance.searchtable.getSelectedRowCount() == 1) {
          source_id = 0;
          AddMixout((SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns));
        }
        return true;
      } else if (source == SkinManager.instance.getObject("roots_add_mixout_button")) {
        if (RootsUI.instance.rootstable.getSelectedRowCount() == 1) {
          source_id = 1;
          AddMixoutUI.instance.AddMixout((SongLinkedList)RootsUI.instance.rootstable.getModel().getValueAt(RootsUI.instance.rootstable.getSelectedRow(), RootsUI.instance.rootscolumnconfig.num_columns));
        }
        return true;
      } else if (source == SkinManager.instance.getObject("suggested_mixouts_add_mixout_button")) {
        if (SuggestedMixesUI.instance.suggestedtable.getSelectedRowCount() == 1) {
          source_id = 3;
          AddMixoutUI.instance.AddMixout((SongLinkedList)SuggestedMixesUI.instance.suggestedtable.getModel().getValueAt(SuggestedMixesUI.instance.suggestedtable.getSelectedRow(), SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns));
        }
        return true;
      } else if (source == SearchListMouse.instance.addmixout) {
          if (SearchPane.instance.searchtable.getSelectedRowCount() == 1) {
              source_id = 0;
              AddMixout((SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns));
            }
            return true;
      } else if (source == SearchListMouse.instance.setcurrent) {
          if (SearchPane.instance.searchtable.getSelectedRowCount() == 1) {
              source_id = 5;
              SongLinkedList song = (SongLinkedList)SearchPane.instance.searchtable.getModel().getValueAt(SearchPane.instance.searchtable.getSelectedRow(), SearchPane.instance.searchcolumnconfig.num_columns);
              AddMixout(song);
            }
            return true;
      }

      return true;
    }

    public SongLinkedList addmixoutsong = null;
    public void AddMixout(SongLinkedList mixtosong) {
      if (mixtosong == null) return;
      addmixoutsong = mixtosong;
      addmixoutfromfield.setText(RapidEvolutionUI.instance.currentsong.getSongIdShort());
      addmixouttofield.setText(mixtosong.getSongIdShort());
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == mixtosong.uniquesongid) return;
      }
      addmixoutcommentsfield.setText("");
      if (((RapidEvolutionUI.instance.currentsong.getStartbpm() != 0) || (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0)) &&
          (mixtosong.getStartbpm() != 0)) {
        float frombpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
        if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) frombpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
        String bpmtext = String.valueOf(SongUtil.get_bpmdiff(mixtosong.getStartbpm(), frombpm));
        int maxlen = 6;
        if (SongUtil.get_bpmdiff(mixtosong.getStartbpm(), frombpm) > 0) bpmtext = new String("+" +
            bpmtext);
        if (bpmtext.length() < maxlen) maxlen = bpmtext.length();
        addmixoutbpmdifffield.setText(bpmtext.substring(0, maxlen));
      }
      else addmixoutbpmdifffield.setText("");
      addmixoutscorefield.setText("");
      addmixoutaddoncb.setSelected(false);
    }

    public void PostDisplay(Object source) {
        if (lastfocus == 0) addmixoutcommentsfield.requestFocus();
        else if (lastfocus == 1) addmixoutscorefield.requestFocus();
        else if (lastfocus == 2) addmixoutbpmdifffield.requestFocus();
        else if (lastfocus == 3) addmixoutaddoncb.requestFocus();
        else if (lastfocus == 4) addmixoutcalcbpmdiffbutton.requestFocus();
    }

    public void PostInit() {
      if (!RapidEvolution.instance.loaded) SkinManager.instance.setEnabled("add_mixout_button", false);
    }
}

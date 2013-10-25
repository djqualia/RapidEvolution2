package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rapid_evolution.MixoutObject;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongUtil;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.main.MixoutPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class EditMixoutSongTrailUI extends REDialog implements ActionListener {
    public EditMixoutSongTrailUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static EditMixoutSongTrailUI instance = null;

    public JButton editmixoutcancelbutton = new REButton();
    public JButton editmixoutokbutton = new REButton();
    public JTextField editmixoutfromfield = new RETextField();
    public JTextField editmixouttofield = new RETextField();
    public JTextField editmixoutbpmdifffield = new RETextField();
    public JButton editmixoutcalcbpmdiffbutton = new REButton();
    public JTextField editmixoutscorefield = new RETextField();
    public JCheckBox editmixoutaddoncb = new RECheckBox();
    public JTextArea editmixoutcommentsfield = new RETextArea();

    private void setupDialog() {
        // edit mixout dialog
        editmixoutbpmdifffield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            editmixoutbpmdifffield.setText(StringUtil.validate_bpmdiff(editmixoutbpmdifffield.getText()));
          }
        });
        editmixoutscorefield.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            editmixoutscorefield.setText(StringUtil.validate_rank(editmixoutscorefield.getText()));
          }});
    }

    public SongLinkedList editmixoutfromsong = null;
    public SongLinkedList editmixouttosong = null;
    public MixoutObject editedmixout = null;

    public void EditMixoutConfirm() {
      try {
        editedmixout.song.setMixoutComments(editedmixout.index,
            editmixoutcommentsfield.getText());
        editedmixout.song.setMixoutBpmdiff(editedmixout.index, Float.parseFloat(
            editmixoutbpmdifffield.getText()));
        editedmixout.song.setMixoutRank(editedmixout.index, Integer.parseInt(
            editmixoutscorefield.getText()));
        editedmixout.song.setMixoutAddon(editedmixout.index, editmixoutaddoncb.
            isSelected());
        if (editedmixout.song == RapidEvolutionUI.instance.currentsong) MixoutPane.instance.RedrawMixoutTable();
        SongTrailUI.instance.StartRedrawSongTrailThread();
      } catch (Exception e) { }
    }

    private void setupActionListeners() {
      editmixoutcalcbpmdiffbutton.addActionListener(this);
      editmixoutokbutton.addActionListener(this);
      editmixoutcancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == editmixoutokbutton) {
              EditMixoutConfirm();
              setVisible(false);
      } else if (ae.getSource() == editmixoutcalcbpmdiffbutton) {
            float usebpm = editmixoutfromsong.getStartbpm();
            if (editmixoutfromsong.getEndbpm() != 0) usebpm = editmixoutfromsong.getEndbpm();
            if ((editmixouttosong.getStartbpm() == 0) || (editmixoutfromsong.getStartbpm() == 0)) return;
            float bpmdiff = SongUtil.get_bpmdiff(editmixouttosong.getStartbpm(), usebpm);
            String bpmtext = String.valueOf(bpmdiff);
            if (bpmdiff > 0) bpmtext = new String("+" + bpmtext);
            int length = bpmtext.length();
            if (length > 6) length = 6;
            editmixoutbpmdifffield.setText(bpmtext.substring(0,length));
      } else if (ae.getSource() == editmixoutcancelbutton) {
          setVisible(false);
      }
    }
}

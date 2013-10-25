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

public class EditMixoutMixGenUI extends REDialog implements ActionListener {
    public EditMixoutMixGenUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static EditMixoutMixGenUI instance = null;

    public JButton editmixout2calcbpmdiffbutton = new REButton();
    public JButton editmixout2okbutton = new REButton();
    public JButton editmixout2cancelbutton = new REButton();
    public JTextField editmixout2fromfield = new RETextField();
    public JTextField editmixout2tofield = new RETextField();
    public JTextArea editmixout2commentsfield = new RETextArea();
    public JTextField editmixout2bpmdifffield = new RETextField();
    public JTextField editmixout2scorefield = new RETextField();
    public JCheckBox editmixout2addoncb = new RECheckBox();

    private void setupDialog() {
      // edit mixout dialog
      editmixout2bpmdifffield.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          editmixout2bpmdifffield.setText(StringUtil.validate_bpmdiff(editmixout2bpmdifffield.getText()));
        }
      });
      editmixout2scorefield.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          editmixout2scorefield.setText(StringUtil.validate_rank(editmixout2scorefield.getText()));
        }});
    }

    public SongLinkedList editmixout2fromsong = null;
    public SongLinkedList editmixout2tosong = null;
    public MixoutObject editedmixout2 = null;

    void EditMixout2Confirm() {
      try {
        editedmixout2.song.setMixoutComments(editedmixout2.index,
            editmixout2commentsfield.getText());
        editedmixout2.song.setMixoutBpmdiff(editedmixout2.index, Float.parseFloat(
            editmixout2bpmdifffield.getText()));
        editedmixout2.song.setMixoutRank(editedmixout2.index, Integer.parseInt(
            editmixout2scorefield.getText()));
        editedmixout2.song.setMixoutAddon(editedmixout2.index, editmixout2addoncb.
            isSelected());
        if (editedmixout2.song == RapidEvolutionUI.instance.currentsong) MixoutPane.instance.RedrawMixoutTable();
        MixGeneratorUI.instance.DisplayMix(MixGeneratorUI.instance.current_mix);
      } catch (Exception e) { }
    }

    private void setupActionListeners() {
       editmixout2calcbpmdiffbutton.addActionListener(this);
       editmixout2okbutton.addActionListener(this);
       editmixout2cancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == editmixout2okbutton) {
              EditMixout2Confirm();
              setVisible(false);
      } else if (ae.getSource() == editmixout2calcbpmdiffbutton) {
            float usebpm = editmixout2fromsong.getStartbpm();
            if (editmixout2fromsong.getEndbpm() != 0) usebpm = editmixout2fromsong.getEndbpm();
            if ((editmixout2tosong.getStartbpm() == 0) || (editmixout2fromsong.getStartbpm() == 0)) return;
            float bpmdiff = SongUtil.get_bpmdiff(editmixout2tosong.getStartbpm(), usebpm);
            String bpmtext = String.valueOf(bpmdiff);
            if (bpmdiff > 0) bpmtext = new String("+" + bpmtext);
            int length = bpmtext.length();
            if (length > 6) length = 6;
            editmixout2bpmdifffield.setText(bpmtext.substring(0,length));
      } else if (ae.getSource() == editmixout2cancelbutton) {
            setVisible(false);
      }
    }
}

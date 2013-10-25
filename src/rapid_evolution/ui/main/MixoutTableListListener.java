package rapid_evolution.ui.main;

import javax.swing.event.ListSelectionListener;
import rapid_evolution.SongLinkedList;
import rapid_evolution.RapidEvolution;
import javax.swing.event.ListSelectionEvent;
import rapid_evolution.ui.main.MixListMouse;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.RapidEvolutionUI;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class MixoutTableListListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      if ((MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) && !MixoutPane.instance.recreatingmixouts) {
        MixListMouse.instance.edititem2.setEnabled(true);
        MixListMouse.instance.detectkeys2.setEnabled(true);
        MixListMouse.instance.detectbpms2.setEnabled(true);
        MixListMouse.instance.detectcolors2.setEnabled(true);
        MixListMouse.instance.playselection2.setEnabled(true);
        MixListMouse.instance.readtagsselected2.setEnabled(true);
        MixListMouse.instance.writetagsselected2.setEnabled(true);
        MixListMouse.instance.deleteselected2.setEnabled(true);
        MixListMouse.instance.editmixout.setEnabled(true);
        MixListMouse.instance.setcurrent.setEnabled(true);
      } else {
        MixListMouse.instance.edititem2.setEnabled(false);
        MixListMouse.instance.setcurrent.setEnabled(false);
        MixListMouse.instance.detectkeys2.setEnabled(false);
        MixListMouse.instance.detectbpms2.setEnabled(false);
        MixListMouse.instance.detectcolors2.setEnabled(false);
        MixListMouse.instance.playselection2.setEnabled(false);
        MixListMouse.instance.readtagsselected2.setEnabled(false);
        MixListMouse.instance.writetagsselected2.setEnabled(false);
        MixListMouse.instance.deleteselected2.setEnabled(false);
        MixListMouse.instance.editmixout.setEnabled(false);
      }
      if ((MixoutPane.instance.mixouttable.getSelectedRowCount() == 1) && (!MixoutPane.instance.mixoutscore.hasFocus()) && (!MixoutPane.instance.mixoutcomments.hasFocus()) && (!MixoutPane.instance.addoncheckbox.hasFocus()) && (!MixoutPane.instance.bpmdifffield.hasFocus())) {
        try {
          MixoutPane.instance.selectedmixout = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns);
        } catch (Exception e1) { }
      }
      if (((RapidEvolutionUI.instance.currentsong != null) && !MixoutPane.instance.recreatingmixouts) && (!MixoutPane.instance.mixoutscore.hasFocus()) && (!MixoutPane.instance.mixoutcomments.hasFocus()) && (!MixoutPane.instance.addoncheckbox.hasFocus()) && (!MixoutPane.instance.bpmdifffield.hasFocus())) {
        int row = MixoutPane.instance.mixouttable.getSelectedRow();
        if ((row >= RapidEvolutionUI.instance.currentsong.getNumMixoutSongs()) || (row < 0)) {
          MixoutPane.instance.addoncheckbox.setSelected(false);
          MixoutPane.instance.addoncheckbox.setEnabled(false);
          MixoutPane.instance.mixoutcomments.setEnabled(false);
          MixoutPane.instance.mixoutcomments.setText("");
          MixoutPane.instance.mixoutscore.setEnabled(false);
          MixoutPane.instance.mixoutscore.setText("");
          MixoutPane.instance.bpmdifffield.setEnabled(false);
          MixoutPane.instance.bpmdifffield.setText("");
          MixoutPane.instance.calculatebpmdiffbutton.setEnabled(false);
          MixoutPane.instance.mixoutcommentslabel.setEnabled(false);
          MixoutPane.instance.scorefield.setEnabled(false);
          MixoutPane.instance.bpmdifflabel.setEnabled(false);
          return;
        }
        SongLinkedList song = (SongLinkedList)MixoutPane.instance.mixouttable.getModel().getValueAt(MixoutPane.instance.mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns);
        MixoutPane.instance.selectedmixout = song;
        MixoutPane.instance.addoncheckbox.setEnabled(true);
        MixoutPane.instance.mixoutcomments.setEnabled(true);
        MixoutPane.instance.mixoutscore.setEnabled(true);
        MixoutPane.instance.bpmdifffield.setEnabled(true);
        MixoutPane.instance.calculatebpmdiffbutton.setEnabled(true);
        MixoutPane.instance.mixoutcommentslabel.setEnabled(true);
        MixoutPane.instance.scorefield.setEnabled(true);
        MixoutPane.instance.bpmdifflabel.setEnabled(true);
        for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
          if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == song.uniquesongid) {
            MixoutPane.instance.mixoutcomments.setText(RapidEvolutionUI.instance.currentsong.getMixoutComments(i));
            MixoutPane.instance.mixoutscore.setText(String.valueOf(RapidEvolutionUI.instance.currentsong.getMixoutRank(i)));
            String bpmtext = String.valueOf(RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i));
            int maxlen = 6;
            if (RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i) > 0) bpmtext = new String("+" + bpmtext);
            if (bpmtext.length() < maxlen) maxlen = bpmtext.length();
            MixoutPane.instance.bpmdifffield.setText(bpmtext.substring(0, maxlen));
            if (RapidEvolutionUI.instance.currentsong.getMixoutAddon(i)) MixoutPane.instance.addoncheckbox.setSelected(true);
            else MixoutPane.instance.addoncheckbox.setSelected(false);
          }
        }
      }
    }
}

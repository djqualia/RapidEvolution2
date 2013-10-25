package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class CopyFieldsUI extends REDialog implements ActionListener {
    public CopyFieldsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static CopyFieldsUI instance = null;
    public REComboBox copyfieldfrom = new REComboBox();
    public REComboBox copyfieldto = new REComboBox();
    public JButton copyfieldokbutton = new REButton();
    public JButton copyfieldcancelbutton = new REButton();

    private void setupDialog() {
    }

    public SongLinkedList[] copyfieldsongs = null;
    Semaphore ProcessCopyFieldSem = new Semaphore(1);
    public class ProcessCopyFields extends Thread {
      public ProcessCopyFields(Object from, Object to)  {
          try {
              fromtype = ColumnConfig.getIndex(from.toString());
              totype = ColumnConfig.getIndex(to.toString());
          } catch (Exception e) {
              IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                      SkinManager.instance.getDialogMessageText("copy_fields_no_selection_error"),
                      SkinManager.instance.getDialogMessageTitle("copy_fields_no_selection_error"),
                      IOptionPane.ERROR_MESSAGE);
          }
      }
      int fromtype = -1;
      int totype = -1;
      public void run()  {
          if ((fromtype == -1) || (totype == -1)) return;
      boolean duplicatefound = false;
      try {
      SongLinkedList[] copyfieldsongs2 = new SongLinkedList[copyfieldsongs.length];
      for (int z = 0; z < copyfieldsongs.length; ++z) copyfieldsongs2[z] = copyfieldsongs[z];
      ProcessCopyFieldSem.acquire();
      for (int z = 0; z < copyfieldsongs2.length; ++z) {
        if (RapidEvolution.instance.terminatesignal) throw new Exception();
        SongLinkedList iter = copyfieldsongs2[z];
        OldSongValues old_values = new OldSongValues(iter);
        String oldfieldvalue = null;
        
        String replacevalue = iter.get_column_data(fromtype).toString();
        int replacevalue_int = 0;
        try {
            replacevalue_int = Integer.parseInt(replacevalue);
        } catch (Exception e) { }
        
        if (totype == ColumnConfig.COLUMN_ARTIST) {         
            oldfieldvalue = iter.getArtist();
            iter.setArtist(replacevalue);
        }
        if (totype == ColumnConfig.COLUMN_ALBUM) {
            oldfieldvalue = iter.getAlbum();
            iter.setAlbum(replacevalue);
        }
        if (totype == ColumnConfig.COLUMN_TRACK){
            oldfieldvalue = iter.getTrack();
            iter.setTrack(replacevalue);
        }
        if (totype == ColumnConfig.COLUMN_TIME) iter.setTime(replacevalue);
        if (totype == ColumnConfig.COLUMN_BPMACCURACY) iter.setBpmAccuracy(replacevalue_int);
        if (totype == ColumnConfig.COLUMN_KEYACCURACY) iter.setKeyAccuracy(replacevalue_int);
        if (totype == ColumnConfig.COLUMN_TIMESIG) iter.setTimesig(replacevalue);
        if (totype == ColumnConfig.COLUMN_TITLE) {
            oldfieldvalue = iter.getSongname();
            iter.setSongname(replacevalue);
        }
        if (totype == ColumnConfig.COLUMN_BPMSTART) {
          try { iter.setStartbpm(Float.parseFloat(replacevalue)); } catch (Exception e) { }
          if (replacevalue.equals("")) iter.setStartbpm(0.0f);
        }
        if (totype == ColumnConfig.COLUMN_KEYSTART) iter.setStartkey(replacevalue);
        if (totype == ColumnConfig.COLUMN_BPMEND) {
          try { iter.setEndbpm(Float.parseFloat(replacevalue)); } catch (Exception e) { }
          if (replacevalue.equals("")) iter.setEndbpm(0.0f);
        }
        if (totype == ColumnConfig.COLUMN_KEYEND) iter.setEndkey(replacevalue);
        if (totype == ColumnConfig.COLUMN_SONGCOMMENTS) iter.setComments(replacevalue);
        if (totype == ColumnConfig.COLUMN_USER1) iter.setUser1(replacevalue);
        if (totype == ColumnConfig.COLUMN_USER2) iter.setUser2(replacevalue);
        if (totype == ColumnConfig.COLUMN_USER3) iter.setUser3(replacevalue);
        if (totype == ColumnConfig.COLUMN_USER4) iter.setUser4(replacevalue);
        if (totype == ColumnConfig.COLUMN_FILENAME) iter.setFilename(replacevalue);
        if (totype == ColumnConfig.COLUMN_REMIX) {
            oldfieldvalue = iter.getRemixer();
            iter.setRemixer(replacevalue);
        }        
        
        String newuniqueid = SongLinkedList.calculate_unique_id(iter.getArtist(), iter.getAlbum(), iter.getTrack(), iter.getSongname(), iter.getRemixer());
        SongLinkedList testsong = SongDB.instance.OldGetSongPtr(newuniqueid);
        if (!((testsong == null) || (testsong == iter))) {
            if (totype == ColumnConfig.COLUMN_ARTIST) {
                iter.setArtist(oldfieldvalue);
            } else if (totype == ColumnConfig.COLUMN_ALBUM) {
                iter.setAlbum(oldfieldvalue);
            } else if (totype == ColumnConfig.COLUMN_TRACK) {
                iter.setTrack(oldfieldvalue);
            } else if (totype == ColumnConfig.COLUMN_TITLE) {
                iter.setSongname(oldfieldvalue);
            } else if (totype == ColumnConfig.COLUMN_REMIX) {
                iter.setRemixer(oldfieldvalue);
            }
            duplicatefound = true;
        } else {
          SongDB.instance.UpdateSong(iter, old_values);
        }
        
        
      }
      } catch (Exception e) { }
      ProcessCopyFieldSem.release();
      if (duplicatefound) {
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
            SkinManager.instance.getDialogMessageText("copy_fields_error"),
            SkinManager.instance.getDialogMessageTitle("copy_fields_error"),
            IOptionPane.ERROR_MESSAGE);
      }
    }
    }

    private void setupActionListeners() {
        copyfieldokbutton.addActionListener(this);
        copyfieldcancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == copyfieldokbutton) {
        new ProcessCopyFields(copyfieldfrom.getSelectedItem(), copyfieldto.getSelectedItem()).start();
        setVisible(false);
      } else if (ae.getSource() == copyfieldcancelbutton) {
        setVisible(false);
      }
    }

    public boolean PreDisplay() {
      return true;
    }

    public void PostDisplay() {
    }
}


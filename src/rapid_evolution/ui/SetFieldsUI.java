package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class SetFieldsUI extends REDialog implements ActionListener {
    public SetFieldsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static SetFieldsUI instance = null;
    public AutoTextField setfieldfield = new AutoTextField();
    public JButton setfieldokbutton = new REButton();
    public JButton setfieldcancelbutton = new REButton();

    private void setupDialog() {
            setfieldfield.addKeyListener(new SetFieldKeyListener());
    }

    class SetFieldKeyListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          new ProcessSetField(setfieldtype).start();
          setVisible(false);
        } else if (e.getKeyCode() == e.VK_ESCAPE) setVisible(false);
      }
    }

    public SongLinkedList[] setfieldssongs = null;
    public int setfieldtype = 0;
    Semaphore ProcessSetFieldSem = new Semaphore(1);
    public class ProcessSetField extends Thread {
      public ProcessSetField(int in_setfieldtype)  { setfieldtype = in_setfieldtype; }
      int setfieldtype;
      public void run()  {
      boolean duplicatefound = false;
      try {
      if ((setfieldtype == 3) && (!StringUtil.validate_time(setfieldfield.getText()).equals(setfieldfield.getText()))) {
        return;
      }
      if ((setfieldtype == 4) && (!StringUtil.validate_timesig(setfieldfield.getText()).equals(setfieldfield.getText()))) {
        return;
      }
      SongLinkedList[] setfieldssongs2 = new SongLinkedList[setfieldssongs.length];
      for (int z = 0; z < setfieldssongs.length; ++z) setfieldssongs2[z] = setfieldssongs[z];
      String replacevalue = setfieldfield.getText();
      int setfieldtype2 = setfieldtype;
      ProcessSetFieldSem.acquire();
      for (int z = 0; z < setfieldssongs2.length; ++z) {
        if (RapidEvolution.instance.terminatesignal) throw new Exception();
        SongLinkedList iter = setfieldssongs2[z];
        OldSongValues old_values = new OldSongValues(iter);
        String oldfieldvalue = null;
        if (setfieldtype2 == 0) {
            oldfieldvalue = iter.getArtist();
            iter.setArtist(replacevalue);
        }
        if (setfieldtype2 == 1) {
            oldfieldvalue = iter.getAlbum();
            iter.setAlbum(replacevalue);
        }
        if (setfieldtype2 == 2){
            oldfieldvalue = iter.getTrack();
            iter.setTrack(replacevalue);
        }
        if (setfieldtype2 == 3) iter.setTime(replacevalue);
        if (setfieldtype2 == 4) iter.setTimesig(replacevalue);
        if (setfieldtype2 == 5) {
            oldfieldvalue = iter.getSongname();
            iter.setSongname(replacevalue);
        }
        if (setfieldtype2 == 6) {
          try { iter.setStartbpm(Float.parseFloat(replacevalue)); } catch (Exception e) { }
          if (replacevalue.equals("")) iter.setStartbpm(0.0f);
        }
        if (setfieldtype2 == 7) iter.setStartkey(replacevalue);
        if (setfieldtype2 == 8) {
          try { iter.setEndbpm(Float.parseFloat(replacevalue)); } catch (Exception e) { }
          if (replacevalue.equals("")) iter.setEndbpm(0.0f);
        }
        if (setfieldtype2 == 9) iter.setEndkey(replacevalue);
        if (setfieldtype2 == 10) iter.setComments(replacevalue);
        if (setfieldtype2 == 20) iter.setUser1(replacevalue);
        if (setfieldtype2 == 21) iter.setUser2(replacevalue);
        if (setfieldtype2 == 22) iter.setUser3(replacevalue);
        if (setfieldtype2 == 23) iter.setUser4(replacevalue);
        if (setfieldtype2 == 24) {
            oldfieldvalue = iter.getRemixer();
            iter.setRemixer(replacevalue);
        }
        String newuniqueid = SongLinkedList.calculate_unique_id(iter.getArtist(), iter.getAlbum(), iter.getTrack(), iter.getSongname(), iter.getRemixer());
        SongLinkedList testsong = SongDB.instance.OldGetSongPtr(newuniqueid);
        if (!((testsong == null) || (testsong == iter))) {
            if (setfieldtype2 == 0) {
                iter.setArtist(oldfieldvalue);
            } else if (setfieldtype2 == 1) {
                iter.setAlbum(oldfieldvalue);
            } else if (setfieldtype2 == 2) {
                iter.setTrack(oldfieldvalue);
            } else if (setfieldtype2 == 5) {
                iter.setSongname(oldfieldvalue);
            } else if (setfieldtype2 == 24) {
                iter.setRemixer(oldfieldvalue);
            }
            duplicatefound = true;
        } else {
          SongDB.instance.UpdateSong(iter, old_values);
        }
      }
      } catch (Exception e) { }
      ProcessSetFieldSem.release();
      if (duplicatefound) {
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
            SkinManager.instance.getDialogMessageText("set_fields_error"),
            SkinManager.instance.getDialogMessageTitle("set_fields_error"),
            IOptionPane.ERROR_MESSAGE);
      }
    }
    }

    private void setupActionListeners() {
        setfieldokbutton.addActionListener(this);
        setfieldcancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == setfieldokbutton) {
        new ProcessSetField(setfieldtype).start();
        setVisible(false);
      } else if (ae.getSource() == setfieldcancelbutton) {
        setVisible(false);
      }
    }

    public boolean PreDisplay() {
        String text = (String)display_parameter;
      setfieldfield.setText(text);
      if ((text != null) && !text.equals("")) {
          setfieldfield.setSelectionStart(0);
          setfieldfield.setSelectionEnd(text.length());
      }
      
      return true;
    }

    public void PostDisplay() {
      setfieldfield.requestFocus();
    }
}

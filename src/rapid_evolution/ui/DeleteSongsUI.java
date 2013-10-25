package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.mixshare.rapid_evolution.io.FileLockManager;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class DeleteSongsUI extends REDialog implements ActionListener {
	
    private static Logger log = Logger.getLogger(DeleteSongsUI.class);
	
    public DeleteSongsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static DeleteSongsUI instance = null;
    public JButton deleteokbutton = new  REButton();
    public JCheckBox deleteFilesCheckbox = new RECheckBox();
    
    private void setupDialog() {
    }

    public boolean PreDisplay() {
    	deleteFilesCheckbox.setSelected(false);   
    	return true;
    }

    public void PostDisplay() {
    }

    public void deleteSongs(SongLinkedList[] songs) {
    	if ((songs == null) || (songs.length == 0))
    		return;
    	if (!isVisible()) {
    		this.songs = songs;
    		Display();
    	}    	
    }
    
    SongLinkedList[] songs = null;

    void deleteFilesProc() {
      new Thread() {
        boolean deleteFiles = deleteFilesCheckbox.isSelected();
        public void run() {
          SongDB.instance.switchto = null;
          for (int i = 0; i < songs.length; ++i) {
            SongLinkedList song = songs[i];
            SongDB.instance.DeleteSong(song);
            if (deleteFiles && (song.getFileName() != null)) {
            	FileLockManager.startFileWrite(song.getFileName());
            	if (log.isDebugEnabled())
            		log.debug("deleteFilesProc(): deleting filename=" + song.getFileName());
            	File file = new File(song.getFileName());
            	file.delete();
            	FileLockManager.endFileWrite(song.getFileName());
            }            	 
          }
          if (SongDB.instance.switchto != null) RapidEvolutionUI.instance.change_current_song(SongDB.instance.switchto, 0, true, true);
          else SongTrailUI.instance.StartRedrawSongTrailThread();          
        }
      }.start();

      setVisible(false);
    }

    private void setupActionListeners() {
    	deleteokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == deleteokbutton) {
    	  deleteFilesProc();
      }
    }
}

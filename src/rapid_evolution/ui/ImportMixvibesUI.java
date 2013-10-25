package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.apache.log4j.Logger;

import rapid_evolution.ImportLib;
import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;

import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class ImportMixvibesUI extends REDialog implements ActionListener {
    private static Logger log = Logger.getLogger(ImportMixvibesUI.class);

    public ImportMixvibesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ImportMixvibesUI instance = null;
    public JCheckBox title = new  RECheckBox();
    public JCheckBox artist = new  RECheckBox();
    public JCheckBox album = new  RECheckBox();
    public JCheckBox comment = new  RECheckBox();
    public JCheckBox style = new  RECheckBox();
    public JCheckBox remix = new  RECheckBox();
    public JCheckBox year = new  RECheckBox();
    public JCheckBox time = new  RECheckBox();
    public JCheckBox bpm = new  RECheckBox();
    public JButton importmixvibesokbutton = new REButton();
    public JButton importmixvibescancelbutton = new REButton();
    public JCheckBox overwrite_ifprevious = new RECheckBox();

    private void setupDialog() {
        // import mixvibes dialog
        title.setSelected(true);
        artist.setSelected(true);
        album.setSelected(true);
        comment.setSelected(true);
        style.setSelected(true);
        remix.setSelected(true);
        year.setSelected(true);
        time.setSelected(true);
        bpm.setSelected(true);
    }

    public class ImportOkThread extends Thread {
      public ImportOkThread() { }
      public void run() {
        try {
          setVisible(false);
          HashMap reference = null;
          boolean overwrite = overwrite_ifprevious.isSelected();
  	      PaceMaker pacer = new PaceMaker();
          FileReader inputstream = new FileReader(ImportLib.importfilestr);
          BufferedReader inputbuffer = new BufferedReader(inputstream);
          try {
            String newline = inputbuffer.readLine();
            while ( (newline != null) && !newline.equals("")) {
                pacer.startInterval();
                try {
	                while (StringUtil.countChar(newline, ';') < 11) {
	                    newline += inputbuffer.readLine(); 
	                }
	                SongLinkedList tempsong = new SongLinkedList();        
	                Vector tokens = StringUtil.Parse(newline, ';');
	
	                int count = 0;
	                if (reference == null) {
	                    reference = new HashMap();
	                    for (int i = 0; i < tokens.size(); ++i) {                    
	                        String value = StringUtil.cleanString((String)tokens.get(i));
	                        reference.put(new Integer(count++), value);
	                    }
	                } else {
	                    for (int i = 0; i < tokens.size(); ++i) {                    
	                        String value = StringUtil.cleanString((String)tokens.get(i));
	                        String ref = (String)reference.get(new Integer(count));
	                        if (ref.equalsIgnoreCase("file")) tempsong.setFilename( value);
	                        else if (ref.equalsIgnoreCase("title")) tempsong.setSongname(value);
	                        else if (ref.equalsIgnoreCase("artist")) tempsong.setArtist(value);
	                        else if (ref.equalsIgnoreCase("album")) tempsong.setAlbum(value);
	                        else if (ref.equalsIgnoreCase("comment")) tempsong.setComments(value);
	                        else if (ref.equalsIgnoreCase("remix")) tempsong.setRemixer(value);
	                        else if (ref.equalsIgnoreCase("genre")) {
	                            if (!value.equals(""))
	                                tempsong.stylelist = new String[] { value } ;
	                            else tempsong.stylelist = new String[0];
	                        }
	                        else if (ref.equalsIgnoreCase("file length")) tempsong.setTime(value);
	                        else if (ref.equalsIgnoreCase("tempo")) tempsong.setStartbpm(Float.parseFloat(value));
	                        ++count;
	                    }
	                    
	                    if ( (tempsong.getArtist().equals("")) &&
	                            (tempsong.getAlbum().equals("")) &&
	                            (tempsong.getSongname().equals("")) &&
	                            (tempsong.getTrack().equals("")) &&
	                            (tempsong.getRemixer().equals(""))) tempsong.setSongname(tempsong.getFileName());
	
	                        SongLinkedList newsong = SongDB.instance.getSongLinkedList(tempsong, tempsong.getFileName());
	
	                        if (newsong != null) {
	                          SongLinkedList iter = newsong;
	                          OldSongValues old_values = new OldSongValues(iter);
	                          if (overwrite) {
	                            if (title.isSelected()) if (!tempsong.getSongname().equals("")) iter.setSongname(tempsong.getSongname());
	                            if (artist.isSelected()) if (!tempsong.getArtist().equals("")) iter.setArtist(tempsong.getArtist());
	                            if (album.isSelected()) if (!tempsong.getAlbum().equals("")) iter.setAlbum(tempsong.getAlbum());
	                            if (comment.isSelected()) if (!tempsong.getComments().equals("")) iter.setComments(tempsong.getComments());
	                            if (remix.isSelected()) if (!tempsong.getRemixer().equals("")) iter.setRemixer(tempsong.getRemixer());
	                            if (time.isSelected()) if (!tempsong.getTime().equals("")) iter.setTime(tempsong.getTime());
	                            if (bpm.isSelected()) if (tempsong.getStartbpm() != 0.0f) iter.setStartbpm(tempsong.getStartbpm());
	                          }
	                          else {
	                              if (title.isSelected()) if (iter.getSongname().equals("")) iter.setSongname(tempsong.getSongname());
	                              if (artist.isSelected()) if (iter.getArtist().equals("")) iter.setArtist(tempsong.getArtist());
	                              if (album.isSelected()) if (iter.getAlbum().equals("")) iter.setAlbum(tempsong.getAlbum());
	                              if (comment.isSelected()) if (iter.getComments().equals("")) iter.setComments(tempsong.getComments());
	                              if (remix.isSelected()) if (iter.getRemixer().equals("")) iter.setRemixer(tempsong.getRemixer());
	                              if (time.isSelected()) if (iter.getTime().equals("")) iter.setTime(tempsong.getTime());
	                              if (bpm.isSelected()) if (iter.getStartbpm() == 0.0f) iter.setStartbpm(tempsong.getStartbpm());
	                          }                          
	                          if (style.isSelected()) {
	                            SongDB.instance.UpdateSong(iter, old_values, tempsong.stylelist);
	                          } else {
	                              SongDB.instance.UpdateSong(iter, old_values);
	                          }
	                        }
	                        else {
	                            if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
	                                rapid_evolution.net.MixshareClient.instance.querySongDetails(tempsong, true);
	                            }
	                            Vector stylevector = new Vector();
	                    		if (tempsong.stylelist != null) {
	                                StyleLinkedList siter = SongDB.instance.masterstylelist;
	                                while (siter != null) {
	                                  if (StringUtil.areStyleNamesEqual(siter.getName(), tempsong.stylelist[0])) {
	                                      stylevector.add(siter);
	                                  }
	                                  siter = siter.next;
	                                }
	                    		}
	                         SongDB.instance.AddSingleSong(tempsong, tempsong.stylelist);
	                        }
	                      }
	                	newline = inputbuffer.readLine();    
                } catch (Exception e) {
                    log.error("run(): error", e);                
                }
                pacer.endInterval();
            }                        
          }
          catch (Exception e) {
              log.error("run(): error", e);
          }
//        UpdateThread ut = new UpdateThread();
//        ut.start();
          RapidEvolutionUI.instance.UpdateRoutine();
          inputbuffer.close();
          inputstream.close();
        }
        catch (Exception e) {
            IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
             SkinManager.instance.getDialogMessageText("import_data_error"),
           SkinManager.instance.getDialogMessageTitle("import_data_error"),
           IOptionPane.ERROR_MESSAGE);
        }
      }
    }

    private void setupActionListeners() {
        importmixvibesokbutton.addActionListener(this);
        importmixvibescancelbutton.addActionListener(this);
    }

    public boolean PreDisplay() {
      return true;
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == importmixvibesokbutton) {
             new ImportOkThread().start();
      } else if (ae.getSource() == importmixvibescancelbutton) {
         setVisible(false);
      }
    }
}

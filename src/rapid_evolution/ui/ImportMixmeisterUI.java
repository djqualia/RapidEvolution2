package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
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

public class ImportMixmeisterUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(ImportMixmeisterUI.class);
    
    public ImportMixmeisterUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ImportMixmeisterUI instance = null;
    public JCheckBox mixmeistersongname = new  RECheckBox();
    public JCheckBox mixmeisterartist = new  RECheckBox();
    public JCheckBox mixmeisteralbum = new  RECheckBox();
    public JCheckBox mixmeisterbpm = new  RECheckBox();
    public JCheckBox mixmeistertime = new  RECheckBox();
    public JCheckBox mixmeisterkey = new  RECheckBox();
    public JCheckBox mixmeisteryear = new  RECheckBox();
    public JCheckBox mixmeistergenre = new  RECheckBox();
    public JButton importmixmeisterokbutton = new REButton();
    public JButton importmixmeistercancelbutton = new REButton();
    public JCheckBox mixmeister_ifprevious = new  RECheckBox();

    private void setupDialog() {
            // import mixmeister dialog
            mixmeistersongname.setSelected(true);
            mixmeisterartist.setSelected(true);
            mixmeisteralbum.setSelected(true);
            mixmeisterbpm.setSelected(true);
            mixmeistertime.setSelected(true);
            mixmeisterkey.setSelected(true);
            mixmeisteryear.setSelected(true);
            mixmeistergenre.setSelected(true);
    }

    public class ImportOkThread extends Thread {
      public ImportOkThread(boolean bpm_only_mode) { bpmonly = bpm_only_mode; }
      boolean bpmonly;
      public void run() {
        try {
          setVisible(false);
          boolean overwrite = mixmeister_ifprevious.isSelected();
  	      PaceMaker pacer = new PaceMaker();
          FileReader inputstream = new FileReader(ImportLib.importfilestr);
          BufferedReader inputbuffer = new BufferedReader(inputstream);
          try {
            String newline = inputbuffer.readLine();
            while ( (newline != null) && !newline.equals("")) {
                pacer.startInterval();
                try {
	                Vector parsed = StringUtil.ParseTabs(newline);
	
	              SongLinkedList tempsong = new SongLinkedList();
	              String filename = ( (String) parsed.get(0));
	              tempsong.setFilename( filename);
	              if (parsed.size() <= 2) {
	                newline = newline + inputbuffer.readLine();
	                parsed = StringUtil.ParseTabs(newline);
	              }
	              tempsong.setSongname((String) parsed.get(1));
	
	              tempsong.setArtist((String) parsed.get(2));
	              if (!bpmonly) {
	                tempsong.setAlbum((String) parsed.get(3));
	              }
	              if (mixmeisterbpm.isSelected()) {
	                try {
	                  tempsong.setStartbpm(Float.parseFloat( (String) parsed.get(bpmonly ? 3 : 4)));
	                }
	                catch (Exception e) {}
	              }
	              if (!bpmonly) {
	                if (mixmeistertime.isSelected()) tempsong.setTime((String) parsed.get(7));
	                if (mixmeisterkey.isSelected()) tempsong.setStartkey(com.mixshare.rapid_evolution.music.Key.getPreferredKeyNotation((String) parsed.get(8)));
	
	//            if (mixmeisteryear.isSelected()) tempsong.year = (String) parsed.get(5);
	//            if (mixmeistergenre.isSelected()) ... (String) parsed.get(6);
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
	                  if (mixmeistersongname.isSelected()) if (! ( ( (String) parsed.get(1)).equals(""))) iter.setSongname((String) parsed.get(1));
	                  if (mixmeisterartist.isSelected()) if (! ( ( (String) parsed.get(2)).equals(""))) iter.setArtist((String) parsed.get(2));
	                  if (!bpmonly) if (mixmeisteralbum.isSelected()) if (! ( ( (String) parsed.get(3)).equals(""))) iter.setAlbum((String) parsed.get(3));
	                  if (mixmeisterbpm.isSelected()) { if (! ( ( (String) parsed.get(bpmonly ? 3 : 4)).equals("")))try {
	                    iter.setStartbpm(Float.parseFloat( (String) parsed.get(bpmonly ? 3 : 4)));
	                  }
	                  catch (Exception e) {} }
	                  if (!bpmonly) {
	                    if (mixmeistertime.isSelected()) if (! ( ( (String) parsed.get(7)).equals(""))) iter.
	                      setTime((String) parsed.get(7));
	                    if (mixmeisterkey.isSelected()) if (! ( ( (String) parsed.get(8)).equals(""))) iter.
	                      setStartkey((String) parsed.get(8));
	                  }
	                }
	                else {
	                  if (mixmeistersongname.isSelected()) if (iter.getSongname().equals("")) iter.setSongname((String)parsed.get(1));
	                  if (mixmeisterartist.isSelected()) if (iter.getArtist().equals("")) iter.setArtist((String)parsed.get(2));
	                  if (!bpmonly) if (mixmeisteralbum.isSelected()) if (iter.getAlbum().equals("")) iter.setAlbum((String) parsed.get(3));
	                  if (mixmeisterbpm.isSelected()) {if (iter.getStartbpm() == 0)try {
	                    iter.setStartbpm(Float.parseFloat( (String) parsed.get(bpmonly ? 3 : 4)));
	                  }
	                  catch (Exception e) {}}
	                  if (!bpmonly) {
	                    if (mixmeistertime.isSelected()) if (iter.getTime().equals("")) iter.setTime((String)
	                      parsed.get(7));
	                    if (mixmeisterkey.isSelected()) if (!iter.getStartKey().isValid()) iter.setStartkey((String)
	                      parsed.get(8));
	                  }
	                }                
	                if (mixmeistergenre.isSelected() && !bpmonly) {
	                  String style = (String) parsed.get(6);
	                  SongDB.instance.UpdateSong(iter, old_values, new String[] { style });
	                } else {
	                    SongDB.instance.UpdateSong(iter, old_values);
	                }
	                newline = inputbuffer.readLine();
	              }
	              else {
	                if (!mixmeistersongname.isSelected()) tempsong.setSongname(new String(""));
	                if (!mixmeisterartist.isSelected()) tempsong.setArtist(new String(""));
	                if (!mixmeisteralbum.isSelected())  tempsong.setAlbum(new String(""));
	                if ( (tempsong.getArtist().equals("")) &&
	                    (tempsong.getAlbum().equals("")) &&
	                    (tempsong.getSongname().equals("")) &&
	                    (tempsong.getTrack().equals("")) &&
	                    (tempsong.getRemixer().equals(""))) tempsong.setSongname(tempsong.getFileName());
	
	               String[] stylelist = null;
	               if (mixmeistergenre.isSelected() && !bpmonly) {
	                 stylelist =new String[1];
	                 stylelist[0] = (String) parsed.get(6);
	               }
	               
	               if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
	                   rapid_evolution.net.MixshareClient.instance.querySongDetails(tempsong, true);
	               }               
	               SongDB.instance.AddSingleSong(tempsong, stylelist);
	              }
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
          log.error("ImportOkThread(): error", e);
        }
      }
    }

    private void setupActionListeners() {
        importmixmeisterokbutton.addActionListener(this);
        importmixmeistercancelbutton.addActionListener(this);
    }

    public boolean PreDisplay() {
      if (display_parameter == ImportLib.mixMeisterBpmFileFilter) {
        mixmeistertime.setEnabled(false);
        mixmeisterkey.setEnabled(false);
        mixmeisteryear.setEnabled(false);
        mixmeistergenre.setEnabled(false);
        bpm_only_mode = true;
      } else {
        mixmeistertime.setEnabled(true);
        mixmeisterkey.setEnabled(true);
        mixmeisteryear.setEnabled(true);
        mixmeistergenre.setEnabled(true);
        bpm_only_mode = false;
      }
      return true;
    }

    private boolean bpm_only_mode = false;

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == importmixmeisterokbutton) {
             new ImportOkThread(bpm_only_mode).start();
      } else if (ae.getSource() == importmixmeistercancelbutton) {
         setVisible(false);
      }
    }
}

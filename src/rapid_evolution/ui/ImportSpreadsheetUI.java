package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;

import rapid_evolution.ImportLib;
import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;

public class ImportSpreadsheetUI extends REDialog implements ActionListener {
    private static Logger log = Logger.getLogger(ImportSpreadsheetUI.class);
    public ImportSpreadsheetUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ImportSpreadsheetUI instance = null;
    public JButton importcancelbutton = new REButton();
    public JButton importokbutton = new REButton();
    public JComboBox importexcelartistcombo = new JComboBox();
    public JComboBox importexcelalbumcombo = new JComboBox();
    public JComboBox importexceltrackcombo = new JComboBox();
    public JComboBox importexceltimecombo = new JComboBox();
    public JComboBox importexceltimesigcombo = new JComboBox();
    public JComboBox importexceltitlecombo = new JComboBox();
    public JComboBox importexcelremixercombo = new JComboBox();
    public JComboBox importexcelstartbpmcombo = new JComboBox();
    public JComboBox importexcelendbpmcombo = new JComboBox();
    public JComboBox importexcelstartkeycombo = new JComboBox();
    public JComboBox importexcelendkeycombo = new JComboBox();
    public JComboBox importexcelcommentscombo = new JComboBox();
    public JComboBox importexceluser1combo = new JComboBox();
    public JComboBox importexceluser2combo = new JComboBox();
    public JComboBox importexceluser3combo = new JComboBox();
    public JComboBox importexceluser4combo = new JComboBox();
    public JComboBox importexcelfilenamecombo = new JComboBox();
    public JLabel user1 = new RELabel();
    public JLabel user2 = new RELabel();
    public JLabel user3 = new RELabel();
    public JLabel user4 = new RELabel();

    private void setupDialog() {
            // import spreadsheet dialog
    }

    public class ImportOkButtonThread extends Thread {
      public ImportOkButtonThread() { }
      public void run() {
        try {
          int n = IOptionPane.showConfirmDialog(
                  SkinManager.instance.getFrame("main_frame"),
              SkinManager.instance.getDialogMessageText("import_data_confirm"),
              SkinManager.instance.getDialogMessageTitle("import_data_confirm"),
              IOptionPane.YES_NO_CANCEL_OPTION);
          boolean overwrite = false;
          if ((n == -1) || (n == 2)) return;
          if (n == 0) overwrite = true;

	      PaceMaker pacer = new PaceMaker();
          RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue(0);
          RapidEvolutionUI.instance.importprogress_ui.Display();
          
          for (int s = 1; s <= ImportLib.sheet.getLastRowNum(); ++s) {
              if (ImportLib.stopimporting) return;
              RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue((int)(((float)s) / ImportLib.sheet.getLastRowNum() * 100));
              pacer.startInterval();
              try {
	            HSSFRow row = ImportLib.sheet.getRow(s);
	            SongLinkedList tempsong = new SongLinkedList();            
	            
	            if (importexcelartistcombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setArtist(row.getCell((short)(importexcelartistcombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setArtist(String.valueOf(row.getCell((short)(importexcelartistcombo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexcelalbumcombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setAlbum(row.getCell((short)(importexcelalbumcombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setAlbum(String.valueOf(row.getCell((short)(importexcelalbumcombo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexceltrackcombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setTrack(row.getCell((short)(importexceltrackcombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setTrack(String.valueOf((int)row.getCell((short)(importexceltrackcombo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexceltimecombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setTime(StringUtil.validate_time(row.getCell((short)(importexceltimecombo.getSelectedIndex() - 1)).getStringCellValue()));
	              } catch (Exception e) { }
	              try {
	                tempsong.setTime(StringUtil.validate_time(String.valueOf(row.getCell((short)(importexceltimecombo.getSelectedIndex() - 1)).getNumericCellValue())));
	              } catch (Exception e) { }
	              try {
	                Date tempdate = row.getCell((short)(importexceltimecombo.getSelectedIndex() - 1)).getDateCellValue();
	                tempsong.setTime(StringUtil.validate_time(StringUtil.extract_date(tempdate.toString())));
	              } catch (Exception e) { }
	            }
	            if (importexceltimesigcombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setTimesig(StringUtil.validate_timesig(row.getCell((short)(importexceltimesigcombo.getSelectedIndex() - 1)).getStringCellValue()));
	              } catch (Exception e) { }
	              try {
	                tempsong.setTimesig(StringUtil.validate_timesig(String.valueOf(row.getCell((short)(importexceltimesigcombo.getSelectedIndex() - 1)).getNumericCellValue())));
	              } catch (Exception e) { }
	            }
	            if (importexceltitlecombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setSongname(row.getCell((short)(importexceltitlecombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setSongname(String.valueOf(row.getCell((short)(importexceltitlecombo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexcelremixercombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setRemixer(row.getCell((short)(importexcelremixercombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setRemixer(String.valueOf(row.getCell((short)(importexcelremixercombo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexcelstartbpmcombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setStartbpm(Float.parseFloat(StringUtil.validate_bpm(row.getCell((short)(importexcelstartbpmcombo.getSelectedIndex() - 1)).getStringCellValue())));
	              } catch (Exception e) { }
	              try {
	                tempsong.setStartbpm((float)row.getCell((short)(importexcelstartbpmcombo.getSelectedIndex() - 1)).getNumericCellValue());
	              } catch (Exception e) { }
	            }
	            if (importexcelendbpmcombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setEndbpm(Float.parseFloat(StringUtil.validate_bpm(row.getCell((short)(importexcelendbpmcombo.getSelectedIndex() - 1)).getStringCellValue())));
	              } catch (Exception e) { }
	              try {
	                tempsong.setEndbpm((float)row.getCell((short)(importexcelendbpmcombo.getSelectedIndex() - 1)).getNumericCellValue());
	              } catch (Exception e) { }
	            }
	            if (importexcelstartkeycombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setStartkey(com.mixshare.rapid_evolution.music.Key.getPreferredKeyNotation(StringUtil.validate_keyformat(row.getCell((short)(importexcelstartkeycombo.getSelectedIndex() - 1)).getStringCellValue())));
	              } catch (Exception e) { }
	            }
	            if (importexcelendkeycombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setEndkey(com.mixshare.rapid_evolution.music.Key.getPreferredKeyNotation(StringUtil.validate_keyformat(row.getCell((short)(importexcelendkeycombo.getSelectedIndex() - 1)).getStringCellValue())));
	              } catch (Exception e) { }
	            }
	            if (importexcelcommentscombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setComments(row.getCell((short)(importexcelcommentscombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setComments(String.valueOf(row.getCell((short)(importexcelcommentscombo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexceluser1combo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setUser1(row.getCell((short)(importexceluser1combo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setUser1(String.valueOf(row.getCell((short)(importexceluser1combo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexceluser2combo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setUser2( row.getCell((short)(importexceluser2combo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setUser2( String.valueOf(row.getCell((short)(importexceluser2combo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexceluser3combo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setUser3( row.getCell((short)(importexceluser3combo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setUser3( String.valueOf(row.getCell((short)(importexceluser3combo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexceluser4combo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setUser4( row.getCell((short)(importexceluser4combo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	              try {
	                tempsong.setUser4( String.valueOf(row.getCell((short)(importexceluser4combo.getSelectedIndex() - 1)).getNumericCellValue()));
	              } catch (Exception e) { }
	            }
	            if (importexcelfilenamecombo.getSelectedIndex() != 0) {
	              try {
	                tempsong.setFilename( row.getCell((short)(importexcelfilenamecombo.getSelectedIndex() - 1)).getStringCellValue());
	              } catch (Exception e) { }
	            }
	            if ((tempsong.getArtist().equals("")) &&
	                (tempsong.getAlbum().equals("")) &&
	                (tempsong.getSongname().equals("")) &&
	                (tempsong.getTrack().equals("")) &&
	                (tempsong.getRemixer().equals(""))) tempsong.setSongname(tempsong.getFileName());
	            SongLinkedList newsong = SongDB.instance.getSongLinkedList(tempsong, tempsong.getFileName());
	            if (newsong != null) {
	                OldSongValues old_values = new OldSongValues(newsong);
	              if (overwrite) {
	                if (!(tempsong.getSongname().equals(""))) newsong.setSongname(tempsong.getSongname());
	                if (!(tempsong.getArtist().equals(""))) newsong.setArtist(tempsong.getArtist());
	                if (!(tempsong.getAlbum().equals(""))) newsong.setAlbum(tempsong.getAlbum());
	                if (!(tempsong.getTrack().equals(""))) newsong.setTrack(tempsong.getTrack());
	                if (!(tempsong.getTime().equals(""))) newsong.setTime(tempsong.getTime());
	                if (!(tempsong.getRemixer().equals(""))) newsong.setRemixer(tempsong.getRemixer());
	                if (!(tempsong.getTimesig().equals(""))) newsong.setTimesig(tempsong.getTimesig());
	                if ((tempsong.getStartKey().isValid())) newsong.setStartkey(tempsong.getStartKey().toString());
	                if ((tempsong.getEndKey().isValid())) newsong.setEndkey(tempsong.getEndKey().toString());
	                if (!(tempsong.getStartbpm() == 0)) newsong.setStartbpm(tempsong.getStartbpm());
	                if (!(tempsong.getEndbpm() == 0)) newsong.setEndbpm(tempsong.getEndbpm());
	                if (!(tempsong.getComments().equals(""))) newsong.setComments(tempsong.getComments());
	                if (!(tempsong.getUser1().equals(""))) newsong.setUser1( tempsong.getUser1());
	                if (!(tempsong.getUser2().equals(""))) newsong.setUser2( tempsong.getUser2());
	                if (!(tempsong.getUser3().equals(""))) newsong.setUser3( tempsong.getUser3());
	                if (!(tempsong.getUser4().equals(""))) newsong.setUser4(tempsong.getUser4());
	                if (!(tempsong.getFileName().equals(""))) newsong.setFilename(tempsong.getFileName());
	              }
	              else {
	                if ((newsong.getSongname().equals(""))) newsong.setSongname(tempsong.getSongname());
	                if ((newsong.getArtist().equals(""))) newsong.setArtist(tempsong.getArtist());
	                if ((newsong.getAlbum().equals(""))) newsong.setAlbum(tempsong.getAlbum());
	                if ((newsong.getTrack().equals(""))) newsong.setTrack(tempsong.getTrack());
	                if ((newsong.getTime().equals(""))) newsong.setTime(tempsong.getTime());
	                if ((newsong.getRemixer().equals(""))) newsong.setRemixer(tempsong.getRemixer());
	                if ((newsong.getTimesig().equals(""))) newsong.setTimesig(tempsong.getTimesig());
	                if (!(newsong.getStartKey().isValid())) newsong.setStartkey(tempsong.getStartKey().toString());
	                if (!(newsong.getEndKey().isValid())) newsong.setEndkey(tempsong.getEndKey().toString());
	                if ((tempsong.getStartbpm() == 0)) newsong.setStartbpm(tempsong.getStartbpm());
	                if ((tempsong.getEndbpm() == 0)) newsong.setEndbpm(tempsong.getEndbpm());
	                if ((newsong.getComments().equals(""))) newsong.setComments(tempsong.getComments());
	                if ((newsong.getUser1().equals(""))) newsong.setUser1(tempsong.getUser1());
	                if ((newsong.getUser2().equals(""))) newsong.setUser2(tempsong.getUser2());
	                if ((newsong.getUser3().equals(""))) newsong.setUser3( tempsong.getUser3());
	                if ((newsong.getUser4().equals(""))) newsong.setUser4(tempsong.getUser4());
	                if ((newsong.getFileName().equals(""))) newsong.setFilename( tempsong.getFileName());
	              }
	              SongDB.instance.UpdateSong(newsong, old_values);
	            } else {
	              if ((!tempsong.getArtist().equals("")) ||
	                  (!tempsong.getAlbum().equals("")) ||
	                  (!tempsong.getSongname().equals("")) ||
	                  (!tempsong.getTrack().equals("")) ||
	                  (!tempsong.getRemixer().equals(""))) {
	
	                  SongLinkedList addedsong = tempsong;
	                  if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
	                      rapid_evolution.net.MixshareClient.instance.querySongDetails(addedsong, true);
	                  }
	                  SongDB.instance.AddSingleSong(addedsong, new Vector());
	              }
	          }
            } catch (Exception e) {
                log.error("run(): error", e);
            }
            pacer.endInterval();
          }
          RapidEvolutionUI.instance.importprogress_ui.Hide();
        } catch (Exception e) { log.error("run(): error", e); }
        
        setVisible(false);
      }
    }

    private void setupActionListeners() {
       importcancelbutton.addActionListener(this);
       importokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == importcancelbutton) {
              setVisible(false);
      } else if (ae.getSource() == importokbutton) {
              new ImportOkButtonThread().start();
      }
    }

    public boolean PreDisplay() {
      user1.setText(OptionsUI.instance.customfieldtext1.getText());
      user2.setText(OptionsUI.instance.customfieldtext2.getText());
      user3.setText(OptionsUI.instance.customfieldtext3.getText());
      user4.setText(OptionsUI.instance.customfieldtext4.getText());
      return true;
    }
}

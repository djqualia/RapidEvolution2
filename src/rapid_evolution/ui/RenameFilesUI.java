package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import com.mixshare.rapid_evolution.music.Key;
import rapid_evolution.ui.main.SearchPane;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

import com.ibm.iwt.IOptionPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.io.FileLockManager;

public class RenameFilesUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(RenameFilesUI.class);
    
    public static DecimalFormat decimal_format = new DecimalFormat("###.#");

    public RenameFilesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }
    
    public static RenameFilesUI instance = null;
    public JTextField renamepatternfield = new RETextField();
    public JButton ok_button = new  REButton();
    public JButton cancel_button = new  REButton();

    private void setupDialog() {
            // numgeneratedlg dialog
        renamepatternfield.addKeyListener(new RenamePatternFieldListener());
        renamepatternfield.setText("{%keycode%}{, %bpm%}{ %artist%}{ - %album%}{ - [%track%]}{ - %title%}{ (%remix%)}");        
    }

    class RenamePatternFieldListener extends KeyAdapter {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == e.VK_ENTER) {
          setVisible(false);
          renamefieldokproc();
        } else if (e.getKeyCode() == e.VK_ESCAPE) {
          setVisible(false);
        }
      }
    }

    public static boolean stopRenaming = false;
    
    private static Semaphore renamepatternsem = new Semaphore(1);
    
    public int source_id = -1;
    
    void renamefieldokproc() {

        
        setVisible(false);               
        
        new Thread() {
            public void run() {
                try {
                    renamepatternsem.acquire();
                stopRenaming = false;
                RenameFilesProgressUI.instance.progressbar.setValue(0);
                RenameFilesProgressUI.instance.Display();
                if (songs == null) {
                    songs = RapidEvolutionUI.instance.getSelectedSearchSongs();
                }
        int successes = 0;
        int failures = 0;
        for (int i = 0; i < songs.length; ++i) {
            if (RapidEvolution.instance.terminatesignal || stopRenaming ) {
                renamepatternsem.release();
                return;
            }
            failures++;
            SongLinkedList song = songs[i]; 
            
            boolean success = renameSongFileName(song, renamepatternfield.getText());
                        
            if (success) {
                successes++;
                --failures;
                if (source_id == 1)
                    AddSongsUI.instance.addsongsfilenamefield.setText(song.getFileName());
                else if (source_id == 2) {
                    EditSongUI.instance.editsongsfilenamefield.setText(song.getFileName());
                }                                
            }
            
            int progress = (i * 100) / songs.length;
            
            //blah;
            RenameFilesProgressUI.instance.progressbar.setValue(progress);
        }
                
        RenameFilesProgressUI.instance.Hide();

        java.awt.Component parent = null;
        if (source_id == 0) parent = SkinManager.instance.getFrame("main_frame");
        else if (source_id == 1) parent = AddSongsUI.instance.getParentComponent();
        else if (source_id == 2) parent = EditSongUI.instance.getParentComponent();
        
        if (source_id == 0) {
            String title = SkinManager.instance.getDialogMessageTitle("rename_files");
            String text = SkinManager.instance.getDialogMessageText("rename_files");;
            text = text.replaceAll("%successes%", String.valueOf(successes));
            text = text.replaceAll("%total%", String.valueOf(successes + failures));
            IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"), text, title, IOptionPane.PLAIN_MESSAGE);   
        } else {
            if (failures > 0) {
                String title = SkinManager.instance.getDialogMessageTitle("rename_file_error");
                String text = SkinManager.instance.getDialogMessageText("rename_file_error");;                
                IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"), text, title, IOptionPane.ERROR_MESSAGE);   
            }
        }
                } catch (Exception e) {
                    RenameFilesProgressUI.instance.Hide();
                    log.error("renamefieldokproc(): error", e);
                }
                renamepatternsem.release();
        }
        }.start();        
        
    }    
    
    private static String getVariableText(SongLinkedList song, String variable) {
        if (variable.equalsIgnoreCase("artist")) return song.getArtist();
        if (variable.equalsIgnoreCase("album")) return song.getAlbum();
        if (variable.equalsIgnoreCase("track")) return song.getTrack();
        if (variable.equalsIgnoreCase("title")) return song.getSongname();
        if (variable.equalsIgnoreCase("remix")) return song.getRemixer();
        if (variable.equalsIgnoreCase("key")) return song.getStartKey().getPreferredKeyNotation();
        if (variable.equalsIgnoreCase("keycode")) return song.getStartKey().getKeyCode().toFileFriendlyString();
        if (variable.equalsIgnoreCase("filename")) return getFileNameNoDirectoryNoExtension(song);
        if (variable.equalsIgnoreCase("bpm")) {
            if (song.getStartbpm() != 0.0f)
                return myDecimalFormat(decimal_format.format(song.getStartbpm()));
        }
        return "";
    }
    
    private static String getFileNameNoDirectoryNoExtension(SongLinkedList song) {
    	String filename = song.getFile().getName();
    	int extensionIndex = filename.lastIndexOf(".");
    	if (extensionIndex >= 0)
    		return filename.substring(0, extensionIndex);
    	return filename;
    }
    
    static private String myDecimalFormat(String bpm) {
        if (bpm.indexOf(".") >= 0) return bpm;
        return bpm + ".0";
    }
    
    private void setupActionListeners() {
        ok_button.addActionListener(this);
        cancel_button.addActionListener(this);
    }

    public SongLinkedList[] songs = null;
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == cancel_button) {
              setVisible(false);
      } else if (ae.getSource() == ok_button) {
          renamefieldokproc();
      }
    }

    static public boolean renameSongFileName(SongLinkedList song) {
        return renameSongFileName(song, instance.renamepatternfield.getText());
    }
    static public boolean renameSongFileName(SongLinkedList song, String pattern) {
        boolean success = false;
        if (song.getFileName() != null) {
            
            String newfilename = StringUtil.checkFullPathLength(FileUtil.correctPathSeperators(getSongFilename(song, pattern)));
            
            if (!newfilename.equals(song.getFileName())) {
                if (log.isDebugEnabled()) log.debug("renameSongFileName(): new filename: " + newfilename + ", song: " + song);
                
                // rename the song's file
                try {
                	FileLockManager.startFileWrite(song.getFileName());
                    
                    if (log.isTraceEnabled()) log.trace("renameSongFileName(): existing filename=" + song.getFileName());
                    String existingFileName = FileUtil.correctPathSeperators(song.getFileName());
                    File existingfile = new File(existingFileName);
                                        
                    if ((newfilename.equalsIgnoreCase(existingFileName)) && !newfilename.equals(existingFileName)) {
                        if (log.isTraceEnabled()) log.trace("renameSongFileName(): case change only detected");
                        // changing case of file name
                        File tmp = new File(newfilename + ".rename_temp");
                        success = existingfile.renameTo(tmp);
                        if (success) {
                            File newfile = new File(newfilename);
                            success = tmp.renameTo(newfile);
                        } else {
                            if (log.isTraceEnabled()) log.trace("renameSongFileName(): could not rename to .rename_temp");
                        }
                    } else if (existingfile.exists()) {
                        if (log.isTraceEnabled()) log.trace("renameSongFileName(): new filename detected");
                        File newfile = new File(newfilename);
                        if (!newfile.exists()) {
                            success = existingfile.renameTo(newfile);
                        } else {
                            if (log.isTraceEnabled()) log.trace("renameSongFileName(): newfile already exists!");
                        }
                    }
                    
                    if (success) {                        
                        File asd_file = new File(song.getFileName() + ".asd");
                        if (asd_file.exists()) {
                            File new_asd_file = new File(newfilename + ".asd");
                            asd_file.renameTo(new_asd_file);
                        }
                        song.setFilename(newfilename);                                                    
                    }
                    
                } catch (Exception e) {
                    log.error("renameSongFileName(): error", e);
                }
            	FileLockManager.endFileWrite(song.getFileName());
                
            }
            
        }
        return success;
    }
    
    static private String getSongFilename(SongLinkedList song, String pattern) {
        String filename = song.getFileName();
        try {        
            String directory = FileUtil.getDirectoryFromFilename(song.getFileName());
            String file = FileUtil.getFilenameMinusDirectory(song.getFileName());            
            
            StringBuffer newname = new StringBuffer();
            StringTokenizer tokenizer = new StringTokenizer(pattern, "{");
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();
                String suffix = "";
                if (token.indexOf("}") >= 0) {
                    if (token.indexOf("}") + 1 != token.length())
                        suffix = token.substring(token.indexOf("}") + 1);
                    token = token.substring(0, token.indexOf("}"));
                }
                if (token.endsWith("}")) token = token.substring(0, token.length() - 1);
                int first_token_start = token.indexOf("%");                
                int first_token_end = token.indexOf("%", first_token_start + 1);
                String variable = token.substring(first_token_start + 1, first_token_end);
                String vartext = getVariableText(song, variable);
                if (!vartext.equals("")) {
                    if (newname.toString().equals("")) {
                        if (first_token_start > 0) {
                            if (token.charAt(first_token_start - 1) == '[') newname.append("[");
                        }
                        newname.append(vartext);
                        newname.append(token.substring(first_token_end + 1));                        
                    } else {
                        newname.append(token.substring(0, first_token_start));
                        newname.append(vartext);
                        newname.append(token.substring(first_token_end + 1));
                    }
                }
                newname.append(suffix);
            }
            int extension_index = file.lastIndexOf(".");
            if (extension_index >= 0)
                newname.append(file.substring(extension_index));
            
            String new_filename = newname.toString();
            
            new_filename = StringUtil.makeValidFilename(new_filename);
                        
            return directory + new_filename;
        } catch (Exception e) {
            log.error("getSongFilename(): error", e);
            return song.getFileName();
        }
    }
    
}

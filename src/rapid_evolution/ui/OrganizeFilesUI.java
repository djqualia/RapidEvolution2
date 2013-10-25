package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.audio.tags.TagManager;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;

import rapid_evolution.FileUtil;
import rapid_evolution.ImageSet;
import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.io.FileLockManager;

public class OrganizeFilesUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(OrganizeFilesUI.class);

    public OrganizeFilesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static OrganizeFilesUI instance = null;
    public JTextField organize_directory_field = new  RETextField();
    public JTextField backup_directory_field = new  RETextField();
    public JButton organize_directory_browse_button = new REButton();
    public JButton backup_directory_browse_button = new REButton();
    public JCheckBox delete_old_files = new RECheckBox();
    public JCheckBox rename_files = new RECheckBox();
    public JCheckBox write_tags = new RECheckBox();
    public JButton ok_button = new REButton();
    public JButton cancel_button = new REButton();

    public static boolean organizestopped;
    public static String organizedPersistDir = "";
    public static String organizedBackupPersistDir = "";
    
    private void setupDialog() {
    }

    private void setupActionListeners() {
        ok_button.addActionListener(this);
        cancel_button.addActionListener(this);
        organize_directory_browse_button.addActionListener(this);
        backup_directory_browse_button.addActionListener(this);
    }

    public class OrganizeThread extends Thread {
        private String rootDirectory;
        private String backupDirectory;
        private boolean deleteOld;
        private boolean performRename;
        private boolean writeTags;
        public OrganizeThread(String rootDirectory, String backupDirectory, boolean deleteOld, boolean performRename, boolean writeTags) {
            this.rootDirectory = rootDirectory;
            this.backupDirectory = backupDirectory;
            this.deleteOld = deleteOld;
            this.performRename = performRename;
            this.writeTags = writeTags;
        }
        public void run() {
            try {
                organizestopped = false;
                boolean errors_occurred = false;
                if (rootDirectory != null) {
                    File rootDir = new File(rootDirectory);
                    File backupDir = new File(backupDirectory);
                    if (!backupDir.isDirectory()) backupDir = null;
                    if (rootDir.isDirectory()) {
                        SongLinkedList[] songs = RapidEvolutionUI.getSelectedSearchSongs();
                	      PaceMaker pacer = new PaceMaker();                          
                        RapidEvolutionUI.instance.organizefilesprogress_ui.progressbar.setValue(0); // 0 to 100
                        RapidEvolutionUI.instance.organizefilesprogress_ui.Display();
                        int i = 0;
                        while (!organizestopped && (i < songs.length)) {
                            pacer.startInterval();
                          SongLinkedList song = songs[i];
                          OldSongValues old_values = new OldSongValues(song);
                          if (!organizeSong(song, rootDir, backupDir, deleteOld, performRename, writeTags)) errors_occurred = true;
                          SongDB.instance.UpdateSong(song, old_values);
                          int progress = (int)(((float)i + 1) / songs.length * 100.0f);
                          RapidEvolutionUI.instance.organizefilesprogress_ui.progressbar.setValue(progress);                    
                          ++i;
                          pacer.endInterval();
                        }
                        RapidEvolutionUI.instance.organizefilesprogress_ui.Hide();                       
                    }
                    organizedPersistDir = rootDirectory;
                    organizedBackupPersistDir = backupDirectory;
                }
                if (errors_occurred) {
                    IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
                            SkinManager.instance.getDialogMessageText("organize_files_error"),
                          SkinManager.instance.getDialogMessageTitle("organize_files_error"),
                          IOptionPane.ERROR_MESSAGE);              
                }
                                
            } catch (Exception e) {
                log.error("run(): error Exception", e);
            }
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == ok_button) {
          Hide();
          String rootDirectory = organize_directory_field.getText();
          String backupDirectory = backup_directory_field.getText();
          new OrganizeThread(rootDirectory, backupDirectory, delete_old_files.isSelected(), rename_files.isSelected(), write_tags.isSelected()).start();                   
      } else if (ae.getSource() == cancel_button) {
          setVisible(false);
      } else if (ae.getSource() == organize_directory_browse_button) {                              
          JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
          fc.setDialogTitle(SkinManager.instance.getMessageText("select_organize_directory_location"));
          if (!organize_directory_field.getText().equals("")) fc.setCurrentDirectory(new File(organize_directory_field.getText()));
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fc.setMultiSelectionEnabled(false);
          int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            organize_directory_field.setText(fc.getSelectedFile().getAbsolutePath());
            organizedPersistDir = organize_directory_field.getText();
          }
      } else if (ae.getSource() == backup_directory_browse_button) {                              
          JFileChooser fc = new JFileChooser();
          fc.setDialogTitle(SkinManager.instance.getMessageText("select_backup_directory_location"));
          if (!backup_directory_field.getText().equals("")) fc.setCurrentDirectory(new File(backup_directory_field.getText()));
          fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fc.setMultiSelectionEnabled(false);
          int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
          if (returnVal == JFileChooser.APPROVE_OPTION) {
              backup_directory_field.setText(fc.getSelectedFile().getAbsolutePath());
              organizedBackupPersistDir = backup_directory_field.getText();
          }
      }
    }
    
    public boolean PreDisplay() {
        organize_directory_field.setText(organizedPersistDir);        
        backup_directory_field.setText(organizedBackupPersistDir);                
        return true;
    }
    
    public void Display() {        
        super.Display();
        organize_directory_field.requestFocus();
    }
    
    public boolean organizeSong(SongLinkedList song, File rootDir, File backupDir, boolean deleteOld, boolean rename, boolean writeTags) {
        boolean success = true;
   	   try {   	       
   	       if (rename) RenameFilesUI.renameSongFileName(song);
   	       if (writeTags) TagManager.writeTags(song);
   	       String filename = song.getFileName();
   	       if (filename != null) {
   	           File songFile = new File(filename);
                	String artist = song.getArtist();
                	if ((artist == null) || (artist.equals("")))
                	    artist = "__noartist";
                	char firstLetter = artist.charAt(0);
                	String directory1 = String.valueOf(firstLetter).toLowerCase();
                	if (!Character.isLetter(firstLetter))
                	    directory1 = "__other";
                	
                	File dir1 = new File(rootDir.getAbsolutePath() + FileUtil.getFileSeperator() + directory1);
                	if (!dir1.exists())
                	    dir1.mkdirs();
                	File backup_dir1 = null;
                	if (backupDir != null) {
                	    backup_dir1 = new File(backupDir.getAbsolutePath() + FileUtil.getFileSeperator() + directory1);
                    	if (!backup_dir1.exists())
                    	    backup_dir1.mkdirs();
                	}
                	
                	String directory2 = artist;
                 directory2 = StringUtil.makeValidFilename(directory2);
                 File dir2 = new File(dir1.getAbsolutePath() + FileUtil.getFileSeperator() + directory2);
                 if (!dir2.exists())
                     dir2.mkdirs();
                 File backup_dir2 = null;
                 if (backupDir != null) {
                     backup_dir2 = new File(backup_dir1.getAbsolutePath() + FileUtil.getFileSeperator() + directory2);
                	if (!backup_dir2.exists())
                	    backup_dir2.mkdirs();                     
                 }
                 
                 String album = song.getAlbum();
                	if ((album == null) || (album.equals("")))
                	    album = "__noalbum";
                	else {                	    
                	    String year = getYearForAlbum(song.getArtist(), song.getAlbum());
                	    if ((year!=null) && !year.equals("")) {
                	        album += " [" + year + "]";
                	    }
                	}   	       
                	String directory3 = album;
                	directory3 = StringUtil.makeValidFilename(directory3);
                 File dir3 = new File(dir2.getAbsolutePath() + FileUtil.getFileSeperator() + directory3);
                 if (!dir3.exists())
                     dir3.mkdirs();
                 File backup_dir3 = null;
                 if (backupDir != null) {
                     backup_dir3 = new File(backup_dir2.getAbsolutePath() + FileUtil.getFileSeperator() + directory3);
                	if (!backup_dir3.exists())
                	    backup_dir3.mkdirs();                     
                 }
                 
                 String newFilePath = StringUtil.checkFullPathLength(FileUtil.correctPathSeperators(dir3.getAbsolutePath() + FileUtil.getFileSeperator() + FileUtil.getFilenameMinusDirectory(filename)));
                 String newBackupFilePath = null;
                 if (backupDir != null) {
                     newBackupFilePath = StringUtil.checkFullPathLength(FileUtil.correctPathSeperators(backup_dir3.getAbsolutePath() + FileUtil.getFileSeperator() + FileUtil.getFilenameMinusDirectory(filename)));
                 }
                 
                 if (log.isTraceEnabled()) log.trace("organizeSong(): new path=" + newFilePath + ", for song=" + song);
                 
                 File newFile = new File(newFilePath);
                 File newBackupFile = null;
                 if (backupDir != null) {
                     newBackupFile = new File(newBackupFilePath);
                 }
                 if (songFile.exists()) {
                     try {
                         if ((newBackupFile != null) && !newBackupFile.equals(new File(filename))) {
                             FileUtil.copy(filename, newBackupFilePath);    	                    
                         }
                     } catch (Exception e) {
                         log.error("organizeSong(): error copying song file to backup location", e);
                             success = false;                                            
                      }
                     if (!newFile.equals(new File(filename))) {
   	                
 	  	                FileUtil.copy(filename, newFilePath); 
 	  	                song.setFilename(newFilePath);
 	  	                
 	  	                if (deleteOld) {
 	  	                    try {
 	  	                    	FileLockManager.startFileWrite(filename); 	  	                    	
		 	  	                File oldFile = new File(filename);
		 	  	                File oldDirectory = new File(FileUtil.getDirectoryFromFilename(filename));
		 	  	                oldFile.delete();
		 	  	                File[] oldDirFiles = oldDirectory.listFiles();
		 	  	                while ((oldDirFiles == null) || (oldDirFiles.length == 0)) {
		 	  	                    oldDirectory.delete();
		 	  	                    oldDirectory = oldDirectory.getParentFile();
		 	  	                    if (oldDirectory == null)
		 	  	                        oldDirFiles = null;
		 	  	                    else
		 	  	                        oldDirFiles = oldDirectory.listFiles();
		 	  	                }
                             } catch (Exception e) {
                                  log.error("organizeSong(): error removing old song files", e);
                                  success = false;
                             }
                             FileLockManager.endFileWrite(filename);
 	  	                }
 	  	                
   	                }
   	                
   	           } else {
   	               if (newFile.exists())
   	                   song.setFilename(newFilePath);
   	           }
                 
                ImageSet imageSet = SongDB.instance.getAlbumCoverImageSet(song);
                if (imageSet != null) {
                    String[] image_filenames = imageSet.getFiles();
                    if (image_filenames != null) {
                        for (int i = 0; i < image_filenames.length; ++i) {
                            String image_filename = image_filenames[i];
                            File image_file = new File(image_filename);
                            String newImageFilename = FileUtil.correctPathSeperators(dir3.getAbsolutePath() + FileUtil.getFileSeperator() + FileUtil.getFilenameMinusDirectory(image_filename));
                            String newBackupImageFilename = null;
                            if (backupDir != null)
                                newBackupImageFilename = FileUtil.correctPathSeperators(backup_dir3.getAbsolutePath() + FileUtil.getFileSeperator() + FileUtil.getFilenameMinusDirectory(image_filename));
                            File new_image_file = new File(newImageFilename);
                            File new_backup_image_file = null;
                            if (backupDir != null)
                                new_backup_image_file = new File(newBackupImageFilename);
                            if (image_file.exists()) {
                                String image_directory = FileUtil.getDirectoryFromFilename(image_filename);
                                File image_dir = new File(image_directory);
                                if (!dir3.equals(image_dir) && !newImageFilename.equals(image_filename)) {
                                    if (log.isTraceEnabled()) log.trace("organizeSong(): new image path=" + newImageFilename + ", from=" + image_filename);

                                    try {
                                        try {
	                                       if (backupDir != null)
	                                           FileUtil.copy(image_filename, newBackupImageFilename); 
                                        } catch (Exception e) {
                                           log.error("organizeSong(): error copying album cover to backup location", e);
 	                                          success = false;                                            
                                        }
 	                                   FileUtil.copy(image_filename, newImageFilename); 
 	                                   image_filenames[i] = newImageFilename;
 	        	  	                
 	                                   if (deleteOld) {
 	                                       try {
		 	                                   File oldFile = new File(image_filename);
		 	                                   File oldDirectory = new File(FileUtil.getDirectoryFromFilename(image_filename));
		 	                                   oldFile.delete();
		 	                                   File[] oldDirFiles = oldDirectory.listFiles();
		 	                                   while ((oldDirFiles == null) || (oldDirFiles.length == 0)) {
		 	                                       oldDirectory.delete();
		 	                                       oldDirectory = oldDirectory.getParentFile();
		 	                                       if (oldDirectory == null)
		 	                                           oldDirFiles = null;
		 	                                       else
		 	                                           oldDirFiles = oldDirectory.listFiles();
		 	                                   }
 	                                       } catch (Exception e) {
 	                                           log.error("organizeSong(): error removing old album covers", e);
 	                                          success = false;
 	                                       }
 	                                   }
 	                                   
                                    } catch (Exception e) {
                                        log.error("organizeSong(): error Exception", e);
                                    }
                                }
                            } else {
                                if (new_image_file.exists()) {
                                    image_filenames[i] = newImageFilename;
                                }
                            }
                        }
                    }
                    imageSet.setFiles(image_filenames);
                }
   	       }
   	   } catch (Exception e) {
   	       log.error("organizeSong(): error organizing song=" + song, e);
   	    success = false;   	       
   	   }
   	   return success;
   	}
    
    static private String getYearForAlbum(String artist, String album) {
        SongLinkedList iter = SongDB.instance.SongLL;
        Map yearCount = new HashMap();
        while (iter != null) {
            if (iter.getArtist().equals(artist) && iter.getAlbum().equals(album)) {
                String year = "";
        	    if (OptionsUI.instance.custom_field_1_tag_combo.getSelectedItem().toString().equals(SkinManager.instance.getMessageText("custom_field_id3_tag_year")))
        	        year = iter.getUser1();
        	    else if (OptionsUI.instance.custom_field_2_tag_combo.getSelectedItem().toString().equals(SkinManager.instance.getMessageText("custom_field_id3_tag_year")))
        	        year = iter.getUser2();
        	    else if (OptionsUI.instance.custom_field_3_tag_combo.getSelectedItem().toString().equals(SkinManager.instance.getMessageText("custom_field_id3_tag_year")))
        	        year = iter.getUser3();
        	    else if (OptionsUI.instance.custom_field_4_tag_combo.getSelectedItem().toString().equals(SkinManager.instance.getMessageText("custom_field_id3_tag_year")))
        	        year = iter.getUser4();
        	    if (!year.equals("")) {
        	        Integer count = (Integer)yearCount.get(year);
        	        if (count == null) {
        	            yearCount.put(year, new Integer(1));
        	        } else {
        	            yearCount.put(year, new Integer(count.intValue() + 1));
        	        }
        	    }
            }
            iter = iter.next;
        }
        String returnValue = "";
        Iterator yearIter = yearCount.entrySet().iterator();
        int max = 0;
        while (yearIter.hasNext()) {
            Map.Entry entry = (Map.Entry)yearIter.next();
            int count = ((Integer)entry.getValue()).intValue();
            String year = entry.getKey().toString();
            if (count > max) {
                max = count;
                returnValue = year;
            }
        }
        return returnValue;
    }
        
}

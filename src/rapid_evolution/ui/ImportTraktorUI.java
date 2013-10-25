package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLDecoder;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rapid_evolution.ImportLib;
import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import java.io.File;
import com.mixshare.rapid_evolution.music.Key;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class ImportTraktorUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(ImportTraktorUI.class);
    
    public ImportTraktorUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ImportTraktorUI instance = null;
    
    public JCheckBox import_songinfo = new RECheckBox();
    public JCheckBox import_time = new RECheckBox();
    public JCheckBox import_bpm = new RECheckBox();
    public JCheckBox import_key = new RECheckBox();
    public JCheckBox import_genre = new RECheckBox();
    public JCheckBox import_comments = new RECheckBox();
    public JCheckBox updateonly = new RECheckBox();
    public JCheckBox overwrite = new RECheckBox();
    public JButton okbutton = new REButton();
    public JButton cancelbutton = new REButton();
    
    private void setupDialog() {
    }

    public class ImportOkThread extends Thread {
        private String importfilestr;
      public ImportOkThread(String importfilestr) { this.importfilestr = importfilestr; }
      public void run() {
        try {
            log.trace("run(): importing traktor collection filename=" + importfilestr + ", updateonly=" + updateonly.isSelected());
            RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue(0);
            RapidEvolutionUI.instance.importprogress_ui.Display();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(importfilestr).toURI().toString());              
            Element elem = document.getDocumentElement();
            int nml_version = Integer.parseInt(elem.getAttribute("VERSION"));
            if (log.isDebugEnabled())
            	log.debug("run(): nml version=" + nml_version);
            if (nml_version < 12) {
            	NodeList root_nodelist = elem.getChildNodes();
                int songcount = 0;
                for (int root_iter = 0; root_iter < root_nodelist.getLength(); ++root_iter) {
                    Node root_node = root_nodelist.item(root_iter);
                    if (root_node.getNodeName().equalsIgnoreCase("?xml")) {
                    } else if (root_node.getNodeName().equalsIgnoreCase("PLAYLIST")) {
                        NodeList song_nodelist = root_node.getChildNodes();
                        for (int songnode_iter = 0; songnode_iter < song_nodelist.getLength(); ++songnode_iter) {
                            Node song_node = song_nodelist.item(songnode_iter);
                            if (song_node.getNodeName().equalsIgnoreCase("ENTRY")) {
                                // a new song has been encountered
                                songcount++;
                            }
                        }
                    }
                }
                log.trace("run(): counted " + songcount + " songs");
        	      PaceMaker pacer = new PaceMaker();
                int songsprocessed = 0;
                for (int root_iter = 0; root_iter < root_nodelist.getLength(); ++root_iter) {
                    Node root_node = root_nodelist.item(root_iter);
                    if (root_node.getNodeName().equalsIgnoreCase("?xml")) {
                    } else if (root_node.getNodeName().equalsIgnoreCase("PLAYLIST")) {
                        NodeList song_nodelist = root_node.getChildNodes();
                        for (int songnode_iter = 0; songnode_iter < song_nodelist.getLength(); ++songnode_iter) {
                            Node song_node = song_nodelist.item(songnode_iter);
                            if (song_node.getNodeName().equalsIgnoreCase("ENTRY")) {
                                // a new song has been encountered
                                pacer.startInterval();
                                try {
    	                            songsprocessed++;
    	                            if (ImportLib.stopimporting) return;
    	                            SongLinkedList newsong = new SongLinkedList();
    	                            
    	                            String artist = getAttributeValue(song_node, "ARTIST");
    	                            if (artist != null) newsong.setArtist(artist);
    	                            String title = getAttributeValue(song_node, "TITLE");
    	                            if (title != null) newsong.setSongname(title);
    	                            
    	                            // assimilate data from key/value pairs
    	                            NodeList song_proplist = song_node.getChildNodes();
    	                            for (int prop_iter = 0; prop_iter < song_proplist.getLength(); ++prop_iter) {
    	                                Node propNode = song_proplist.item(prop_iter);
    	
    	                                if (propNode.getNodeName().equalsIgnoreCase("LOCATION")) {
    	                                    String filename = getAttributeValue(propNode, "DIR") + getAttributeValue(propNode, "FILE");                                            
    	                                    String volume = getAttributeValue(propNode, "VOLUME");
    	                                    if ((volume != null) && (volume.length() > 1) && (volume.charAt(1) == ':')) {
    	                                        filename = volume + filename;
    	                                    }
    	                                    if (filename != null) newsong.setFilename(filename);
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("ALBUM")) {
    	                                    String of_tracks = getAttributeValue(propNode, "OF_TRACKS");
    	                                    String album = getAttributeValue(propNode, "TITLE");
    	                                    String track = getAttributeValue(propNode, "TRACK");
    	                                    if (album != null) newsong.setAlbum(album);
    	                                    if (track != null) {
    	                                        if (of_tracks != null) {
    	                                            if (of_tracks.length() == 1) of_tracks = "0" + of_tracks;
    	                                            if (track.length() == 1) track = "0" + track;
    	                                            track += "/" + of_tracks;
    	                                        }
    	                                        newsong.setTrack(track);
    	                                    }                                            
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("INFO")) {
    	                                    if (import_genre.isSelected()) {
    	                                        String genre = getAttributeValue(propNode, "GENRE"); 
    	                                        if (genre != null) {
    	                                            newsong.stylelist = new String[1];
    	                                            newsong.stylelist [0] = genre;
    	                                        }
    	                                    }
    	                                    if (import_comments.isSelected()) {
    	                                        String comment = getAttributeValue(propNode, "COMMENT");
    	                                        if (comment != null) {
    	                                            newsong.setComments(comment);
    	                                        }                                                
    	                                    }
    	                                    if (import_key.isSelected()) {
    	                                        String keyStr = getAttributeValue(propNode, "KEY");
    	                                        if (keyStr != null) {
    	                                        	Key key = Key.getKey(keyStr);
    	                                        	if (key.isValid()) {
    	                                        		newsong.setStartkey(key);
    	                                        	}
    	                                        }
    	                                    }
    	                                    if (import_time.isSelected()) {
    	                                        String time = getAttributeValue(propNode, "PLAYTIME");
    	                                        if (time != null) {
    	                                            newsong.setTime(StringUtil.seconds_to_time(Integer.parseInt(time)));
    	                                        }
    	                                    }
    	                                    
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("TEMPO")) {
    	                                    if (import_bpm.isSelected()) {
    	                                        String bpm = getAttributeValue(propNode, "BPM");
    	                                        String bpm_quality = getAttributeValue(propNode, "BPM_QUALITY");
    	                                        if (bpm != null) {
    	                                            newsong.setStartbpm(Float.parseFloat(bpm));
    	                                            if (bpm_quality != null) {
    	                                                newsong.setBpmAccuracy((int)Float.parseFloat(bpm_quality));
    	                                            }
    	                                        }
    	                                    }
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("LOUDNESS")) {
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("EXTDATA")) {
    	                                }
    	                            }
    	                            
    	                            	newsong.calculate_unique_id();
    	                            	log.trace("run(): read newsong=" + newsong);
    	                                // add the song:                                     
    	                                boolean found = false;
    	                                SongLinkedList existing_song = null;                                        
    	                                SongLinkedList iter = SongDB.instance.SongLL;
    	                                File newfile = new File(newsong.getFileName());
    	                                while (!found && (iter != null)) {
    	                                    String filename = iter.getFileName();
    	                                    File file = new File(filename);
    	                                    if (newfile.equals(file)) {
    	//                                            if (!filename.equals("") && filename.equalsIgnoreCase(newsong.getFileName())) {
    	                                        existing_song = iter;
    	                                        found = true;
    	                                    }
    	                                    iter = iter.next;
    	                                }
    	                                if (!newsong.getSongname().equals("")) {
    	                                    iter = SongDB.instance.SongLL;
    	                                    while (!found && (iter != null)) {
    	                                        if (iter.uniquestringid.equals(newsong.uniquestringid)) {
    	                                            existing_song = iter;
    	                                            found = true;
    	                                        }
    	                                        iter = iter.next;
    	                                    }
    	                                }
    	                                if (existing_song != null) {
    	                                    log.trace("found matching existing song=" + existing_song);
    	                                    OldSongValues oldvalues = new OldSongValues(existing_song);
    	                                    if (import_songinfo.isSelected() && !newsong.getArtist().equals("") && (existing_song.getArtist().equals("") || overwrite.isSelected()))
    	                                        existing_song.setArtist(newsong.getArtist());
    	                                    if (import_songinfo.isSelected() && !newsong.getTrack().equals("") && (existing_song.getTrack().equals("") || overwrite.isSelected()))
    	                                        existing_song.setTrack(newsong.getTrack());
    	                                    if (import_songinfo.isSelected() && !newsong.getAlbum().equals("") && (existing_song.getAlbum().equals("") || overwrite.isSelected()))
    	                                        existing_song.setAlbum(newsong.getAlbum());
    	                                    if (import_songinfo.isSelected() && !newsong.getSongname().equals("") && (existing_song.getSongname().equals("") || overwrite.isSelected()))
    	                                        existing_song.setSongname(newsong.getSongname());
    	                                    if (import_songinfo.isSelected() && !newsong.getRemixer().equals("") && (existing_song.getRemixer().equals("") || overwrite.isSelected()))
    	                                        existing_song.setRemixer(newsong.getRemixer());
    	                                    if (!newsong.getFileName().equals("") && (existing_song.getFileName().equals("") || overwrite.isSelected()))
    	                                        existing_song.setFilename(newsong.getFileName());
    	                                    if (!newsong.getTime().equals("") && (existing_song.getTime().equals("") || overwrite.isSelected()))
    	                                        existing_song.setTime(newsong.getTime());
    	                                    if ((newsong.getStartbpm() != 0.0f) && ((existing_song.getStartbpm() == 0.0f) || overwrite.isSelected()))
    	                                        existing_song.setStartbpm(newsong.getStartbpm());
    	                                    if (!newsong.getComments().equals("") && (existing_song.getComments().equals("") || overwrite.isSelected()))
    	                                        existing_song.setComments(newsong.getComments());
    	                                    if ((newsong.getRating() != 0) && ((existing_song.getRating() == 0) || overwrite.isSelected()))
    	                                        existing_song.setRating(newsong.getRating());
    	                                    SongDB.instance.UpdateSong(existing_song, oldvalues, newsong.stylelist);
    	                                } else {
    	                                    if (!updateonly.isSelected()) {
    	                                        log.trace("no matching existing song found, creating a new one");
    	                                        if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
    	                                            rapid_evolution.net.MixshareClient.instance.querySongDetails(newsong, true);
    	                                        }
    	                                        SongDB.instance.AddSingleSong(newsong, newsong.stylelist);
    	                                    }
    	                                }
    	                                RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue((int)(((float)songsprocessed) / songcount * 100));
    	                        } catch (Exception e) {
    	                            log.error("run(): error", e);	                            
    	                        }
    	                        pacer.endInterval();
                                }
                            }
                                                
                        }
                    }
            } else {
            	// nml version is >= 12
            	NodeList root_nodelist = elem.getChildNodes();
                int songcount = 0;
                for (int root_iter = 0; root_iter < root_nodelist.getLength(); ++root_iter) {
                    Node root_node = root_nodelist.item(root_iter);
                    if (root_node.getNodeName().equalsIgnoreCase("?xml")) {
                    } else if (root_node.getNodeName().equalsIgnoreCase("COLLECTION")) {
                        NodeList song_nodelist = root_node.getChildNodes();
                        for (int songnode_iter = 0; songnode_iter < song_nodelist.getLength(); ++songnode_iter) {
                            Node song_node = song_nodelist.item(songnode_iter);
                            if (song_node.getNodeName().equalsIgnoreCase("ENTRY")) {
                                // a new song has been encountered
                                songcount++;
                            }
                        }
                    }
                }
                log.trace("run(): counted " + songcount + " songs");
        	      PaceMaker pacer = new PaceMaker();
                int songsprocessed = 0;
                for (int root_iter = 0; root_iter < root_nodelist.getLength(); ++root_iter) {
                    Node root_node = root_nodelist.item(root_iter);
                    if (root_node.getNodeName().equalsIgnoreCase("?xml")) {
                    } else if (root_node.getNodeName().equalsIgnoreCase("COLLECTION")) {
                        NodeList song_nodelist = root_node.getChildNodes();
                        for (int songnode_iter = 0; songnode_iter < song_nodelist.getLength(); ++songnode_iter) {
                            Node song_node = song_nodelist.item(songnode_iter);
                            if (song_node.getNodeName().equalsIgnoreCase("ENTRY")) {
                                // a new song has been encountered
                                pacer.startInterval();
                                try {
    	                            songsprocessed++;
    	                            if (ImportLib.stopimporting) return;
    	                            SongLinkedList newsong = new SongLinkedList();
    	                            
    	                            String artist = getAttributeValue(song_node, "ARTIST");
    	                            if (artist != null) newsong.setArtist(artist);
    	                            String title = getAttributeValue(song_node, "TITLE");
    	                            if (title != null) newsong.setSongname(title);
    	                            
    	                            // assimilate data from key/value pairs
    	                            NodeList song_proplist = song_node.getChildNodes();
    	                            for (int prop_iter = 0; prop_iter < song_proplist.getLength(); ++prop_iter) {
    	                                Node propNode = song_proplist.item(prop_iter);
    	
    	                                if (propNode.getNodeName().equalsIgnoreCase("LOCATION")) {
    	                                	String dir = getAttributeValue(propNode, "DIR");
    	                                	dir = StringUtil.ReplaceString("/:", dir, "/");
    	                                    String filename = dir + getAttributeValue(propNode, "FILE");                                            
    	                                    String volume = getAttributeValue(propNode, "VOLUME");
    	                                    if ((volume != null) && (volume.length() > 1) && (volume.charAt(1) == ':')) {
    	                                        filename = volume + filename;
    	                                    }
    	                                    if (filename != null) newsong.setFilename(filename);
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("ALBUM")) {
    	                                    String of_tracks = getAttributeValue(propNode, "OF_TRACKS");
    	                                    String album = getAttributeValue(propNode, "TITLE");
    	                                    String track = getAttributeValue(propNode, "TRACK");
    	                                    if (album != null) newsong.setAlbum(album);
    	                                    if (track != null) {
    	                                        if (of_tracks != null) {
    	                                            if (of_tracks.length() == 1) of_tracks = "0" + of_tracks;
    	                                            if (track.length() == 1) track = "0" + track;
    	                                            track += "/" + of_tracks;
    	                                        }
    	                                        newsong.setTrack(track);
    	                                    }                                            
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("INFO")) {
    	                                    if (import_genre.isSelected()) {
    	                                        String genre = getAttributeValue(propNode, "GENRE"); 
    	                                        if (genre != null) {
    	                                            newsong.stylelist = new String[1];
    	                                            newsong.stylelist [0] = genre;
    	                                        }
    	                                    }
    	                                    if (import_comments.isSelected()) {
    	                                        String comment = getAttributeValue(propNode, "COMMENT");
    	                                        if (comment != null) {
    	                                            newsong.setComments(comment);
    	                                        }                                                
    	                                    }
    	                                    if (import_time.isSelected()) {
    	                                        String time = getAttributeValue(propNode, "PLAYTIME");
    	                                        if (time != null) {
    	                                            newsong.setTime(StringUtil.seconds_to_time(Integer.parseInt(time)));
    	                                        }
    	                                    }
    	                                    
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("TEMPO")) {
    	                                    if (import_bpm.isSelected()) {
    	                                        String bpm = getAttributeValue(propNode, "BPM");
    	                                        String bpm_quality = getAttributeValue(propNode, "BPM_QUALITY");
    	                                        if (bpm != null) {
    	                                            newsong.setStartbpm(Float.parseFloat(bpm));
    	                                            if (bpm_quality != null) {
    	                                                newsong.setBpmAccuracy((int)Float.parseFloat(bpm_quality));
    	                                            }
    	                                        }
    	                                    }
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("LOUDNESS")) {
    	                                } else if (propNode.getNodeName().equalsIgnoreCase("EXTDATA")) {
    	                                }
    	                            }
    	                            
    	                            	newsong.calculate_unique_id();
    	                            	log.trace("run(): read newsong=" + newsong);
    	                                // add the song:                                     
    	                                boolean found = false;
    	                                SongLinkedList existing_song = null;                                        
    	                                SongLinkedList iter = SongDB.instance.SongLL;
    	                                File newfile = new File(newsong.getFileName());
    	                                while (!found && (iter != null)) {
    	                                    String filename = iter.getFileName();
    	                                    File file = new File(filename);
    	                                    if (newfile.equals(file)) {
    	//                                            if (!filename.equals("") && filename.equalsIgnoreCase(newsong.getFileName())) {
    	                                        existing_song = iter;
    	                                        found = true;
    	                                    }
    	                                    iter = iter.next;
    	                                }
    	                                if (!newsong.getSongname().equals("")) {
    	                                    iter = SongDB.instance.SongLL;
    	                                    while (!found && (iter != null)) {
    	                                        if (iter.uniquestringid.equals(newsong.uniquestringid)) {
    	                                            existing_song = iter;
    	                                            found = true;
    	                                        }
    	                                        iter = iter.next;
    	                                    }
    	                                }
    	                                if (existing_song != null) {
    	                                    log.trace("found matching existing song=" + existing_song);
    	                                    OldSongValues oldvalues = new OldSongValues(existing_song);
    	                                    if (import_songinfo.isSelected() && !newsong.getArtist().equals("") && (existing_song.getArtist().equals("") || overwrite.isSelected()))
    	                                        existing_song.setArtist(newsong.getArtist());
    	                                    if (import_songinfo.isSelected() && !newsong.getTrack().equals("") && (existing_song.getTrack().equals("") || overwrite.isSelected()))
    	                                        existing_song.setTrack(newsong.getTrack());
    	                                    if (import_songinfo.isSelected() && !newsong.getAlbum().equals("") && (existing_song.getAlbum().equals("") || overwrite.isSelected()))
    	                                        existing_song.setAlbum(newsong.getAlbum());
    	                                    if (import_songinfo.isSelected() && !newsong.getSongname().equals("") && (existing_song.getSongname().equals("") || overwrite.isSelected()))
    	                                        existing_song.setSongname(newsong.getSongname());
    	                                    if (import_songinfo.isSelected() && !newsong.getRemixer().equals("") && (existing_song.getRemixer().equals("") || overwrite.isSelected()))
    	                                        existing_song.setRemixer(newsong.getRemixer());
    	                                    if (!newsong.getFileName().equals("") && (existing_song.getFileName().equals("") || overwrite.isSelected()))
    	                                        existing_song.setFilename(newsong.getFileName());
    	                                    if (!newsong.getTime().equals("") && (existing_song.getTime().equals("") || overwrite.isSelected()))
    	                                        existing_song.setTime(newsong.getTime());
    	                                    if ((newsong.getStartbpm() != 0.0f) && ((existing_song.getStartbpm() == 0.0f) || overwrite.isSelected()))
    	                                        existing_song.setStartbpm(newsong.getStartbpm());
    	                                    if (!newsong.getComments().equals("") && (existing_song.getComments().equals("") || overwrite.isSelected()))
    	                                        existing_song.setComments(newsong.getComments());
    	                                    if ((newsong.getRating() != 0) && ((existing_song.getRating() == 0) || overwrite.isSelected()))
    	                                        existing_song.setRating(newsong.getRating());
    	                                    SongDB.instance.UpdateSong(existing_song, oldvalues, newsong.stylelist);
    	                                } else {
    	                                    if (!updateonly.isSelected()) {
    	                                        log.trace("no matching existing song found, creating a new one");
    	                                        if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
    	                                            rapid_evolution.net.MixshareClient.instance.querySongDetails(newsong, true);
    	                                        }
    	                                        SongDB.instance.AddSingleSong(newsong, newsong.stylelist);
    	                                    }
    	                                }
    	                                RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue((int)(((float)songsprocessed) / songcount * 100));
    	                        } catch (Exception e) {
    	                            log.error("run(): error", e);	                            
    	                        }
    	                        pacer.endInterval();
                                }
                            }
                                                
                        }
                    }            	
            }
        }
        catch (Exception e) {
            IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
             SkinManager.instance.getDialogMessageText("import_data_error"),
           SkinManager.instance.getDialogMessageTitle("import_data_error"),
           IOptionPane.ERROR_MESSAGE);
          log.error("run(): error", e);
        }
        RapidEvolutionUI.instance.importprogress_ui.Hide();        
      }
    }

    private void setupActionListeners() {
        okbutton.addActionListener(this);
        cancelbutton.addActionListener(this);
    }

    private String importfilestr;
    public boolean PreDisplay() {
        importfilestr = (String)display_parameter;
      return true;
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == okbutton) {
          setVisible(false);
          new ImportOkThread(importfilestr).start();
      } else if (ae.getSource() == cancelbutton) {
         setVisible(false);
      }
    }
    
    private static String getAttributeValue(Node node, String id) {
        Node attribute_node = node.getAttributes().getNamedItem(id);
        if (attribute_node != null) {
            return attribute_node.getNodeValue().trim();
        }
        return null;
    }
}

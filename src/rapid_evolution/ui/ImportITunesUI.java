package rapid_evolution.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;
import java.net.URLDecoder;

import rapid_evolution.StyleLinkedList;
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
import rapid_evolution.ui.AddStyleRunnable;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;

public class ImportITunesUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(ImportITunesUI.class);
    
    public ImportITunesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ImportITunesUI instance = null;
    
    public JCheckBox import_songinfo = new RECheckBox();
    public JCheckBox import_time = new RECheckBox();
    public JCheckBox import_bpm = new RECheckBox();
    public JCheckBox import_genre = new RECheckBox();
    public JCheckBox import_rating = new RECheckBox();
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
            log.debug("run(): importing itunes filename=" + importfilestr + ", updateonly=" + updateonly.isSelected());
            RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue(0);
            RapidEvolutionUI.instance.importprogress_ui.Display();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(importfilestr).toURI().toString());
            Element elem = document.getDocumentElement();
            NodeList root_nodelist = elem.getChildNodes();
            int songcount = 0;
            for (int root_iter = 0; root_iter < root_nodelist.getLength(); ++root_iter) {
                Node root_node = root_nodelist.item(root_iter);
                if (root_node.getNodeName().equalsIgnoreCase("?xml")) {
                } else if (root_node.getNodeName().equalsIgnoreCase("dict")) {
                    NodeList l1_nodelist = root_node.getChildNodes();
                    for (int l1_iter = 0; l1_iter < l1_nodelist.getLength(); ++l1_iter) {
                        Node l1_node = l1_nodelist.item(l1_iter);
                        if (l1_node.getNodeName().equalsIgnoreCase("dict")) {
                            NodeList song_nodelist = l1_node.getChildNodes();
                            for (int songnode_iter = 0; songnode_iter < song_nodelist.getLength(); ++songnode_iter) {
                                Node song_node = song_nodelist.item(songnode_iter);
                                if (song_node.getNodeName().equalsIgnoreCase("dict")) {
                                    // a new song has been encountered
                                    songcount++;
                                }
                            }
                        }
                    }
                }
            }
            int songsprocessed = 0;
            boolean onPlaylistKey = false;
            HashMap trackIdMap = new HashMap();
  	      PaceMaker pacer = new PaceMaker();
            for (int root_iter = 0; root_iter < root_nodelist.getLength(); ++root_iter) {
                Node root_node = root_nodelist.item(root_iter);
                if (root_node.getNodeName().equalsIgnoreCase("?xml")) {
                } else if (root_node.getNodeName().equalsIgnoreCase("dict")) {
                    NodeList l1_nodelist = root_node.getChildNodes();
                    for (int l1_iter = 0; l1_iter < l1_nodelist.getLength(); ++l1_iter) {
                        Node l1_node = l1_nodelist.item(l1_iter);
                        if (l1_node.getNodeName().equalsIgnoreCase("dict")) {
                            NodeList song_nodelist = l1_node.getChildNodes();
                            for (int songnode_iter = 0; songnode_iter < song_nodelist.getLength(); ++songnode_iter) {
                                Node song_node = song_nodelist.item(songnode_iter);
                                if (song_node.getNodeName().equalsIgnoreCase("dict")) {
                                    // a new song has been encountered
                                    songsprocessed++;
                                    if (ImportLib.stopimporting) return;
                                    pacer.startInterval();
                                    try {
	                                    SongLinkedList newsong = new SongLinkedList();
	                                    // assimilate data from key/value pairs
	                                    String key = null;
	                                    String value = null;
	                                    NodeList song_proplist = song_node.getChildNodes();
	                                    for (int prop_iter = 0; prop_iter < song_proplist.getLength(); ++prop_iter) {
	                                        Node propNode = song_proplist.item(prop_iter);
	                                        if (propNode.getNodeName().equalsIgnoreCase("key")) {
	                                            if (propNode.getLastChild() != null) {
	                                                key = propNode.getLastChild().getNodeValue().toLowerCase();
	                                            }
	                                        } else {
	                                            if (propNode.getLastChild() != null) {
	                                                value = propNode.getLastChild().getNodeValue();
	                                                try {
	                                                    value = URLDecoder.decode(value, "UTF-8");
	                                                } catch (Exception e) {
	                                                    //if (RapidEvolution.debugmode) e.printStackTrace();
	                                                }
	                                                // new key/value pair found
	                                                if (key.equals("name")) newsong.setSongname(value);
	                                                else if (key.equals("artist")) newsong.setArtist(value);
	                                                else if (key.equals("album")) newsong.setAlbum(value);
	                                                else if (key.equals("track id")) {
	                                                  newsong.itunes_id = value;
	                                                } else if (key.equals("track number")) {
	                                                  if (value.length() == 1) newsong.setTrack("0" + value);
	                                                  else newsong.setTrack(value);
	                                                } else if (key.equals("disc number")) {
	                                                  if (value.length() == 1) newsong.setTrack("0" + value);
	                                                  else newsong.setTrack(value);
	                                                } else if (key.equals("location")) {
	                                                  newsong.setFilename(value);
	                                                  if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
	                                                      if (newsong.getFileName().toLowerCase().startsWith("file://localhost/")) {
	                                                          newsong.setFilename(newsong.getFileName().substring(17));
	                                                        }                                                      
	                                                      while (newsong.getFileName().startsWith("/")) newsong.setFilename(newsong.getFileName().substring(1));
	                                                  } else {
	                                                      if (newsong.getFileName().toLowerCase().startsWith("file://localhost/")) {
	                                                          newsong.setFilename(newsong.getFileName().substring(16));
	                                                        }                                                      
	                                                  }
	                                                  if (newsong.getFileName().toLowerCase().endsWith("\\")) {
	                                                    newsong.setFilename(newsong.getFileName().substring(0, newsong.getFileName().length() - 1));
	                                                  }
	                                                  if (newsong.getFileName().toLowerCase().endsWith("/")) {
	                                                    newsong.setFilename(newsong.getFileName().substring(0, newsong.getFileName().length() - 1));
	                                                  }
	                                                } else if (key.equals("genre") && import_genre.isSelected()) {
	                                                  newsong.stylelist = new String[1];
	                                                  newsong.stylelist [0] = value;
	                                                } else if (key.equals("total time") && import_time.isSelected()) {
	                                                  try {
	                                                  newsong.setTime(StringUtil.seconds_to_time(Integer.parseInt(value) / 1000));
	                                                  } catch (Exception e) { }
	                                                } else if (key.equals("bpm") && import_bpm.isSelected()) {
	                                                    try {
	                                                        newsong.setStartbpm(Integer.parseInt(value));
	                                                    } catch (Exception e) { }
	                                                } else if (key.equals("comments") && import_comments.isSelected()) {
	                                                    newsong.setComments(value);
	                                                }else if (key.equals("rating") && import_rating.isSelected()) {
	                                                    newsong.setRating((char)(Integer.parseInt(value) / 20));
	                                                }
	                                                }                                              
	                                            }
	                                        }
	                                    	newsong.calculate_unique_id();
	                                    	log.debug("run(): read newsong=" + newsong);
	                                        // add the song:                                     
	                                        boolean found = false;
	                                        SongLinkedList existing_song = null;                                        
	                                        SongLinkedList iter = SongDB.instance.SongLL;
	                                    	log.debug("run(): checking for an existing song with matching itunes id=" + newsong.itunes_id);
	                                        while (!found && (iter != null)) {
	                                            if ((iter.itunes_id != null) && (iter.itunes_id.equals(newsong.itunes_id))) {
	                                                existing_song = iter;
	                                                found = true;
	                                            }
	                                            iter = iter.next;
	                                        }
	                                        if (!found) {
	                                            log.debug("run(): checking for an existing song with matching filename=" + newsong.getFileName());                                        
	                                            iter = SongDB.instance.SongLL;
	                                            File newfile = new File(newsong.getFileName());
	                                            while (!found && (iter != null)) {
	                                                String filename = iter.getFileName();
	                                                File file = new File(filename);
	                                                if (newfile.equals(file)) {
	//                                                if (!filename.equals("") && filename.equalsIgnoreCase(newsong.getFileName())) {
	                                                    existing_song = iter;
	                                                    found = true;
	                                                }
	                                                iter = iter.next;
	                                            }
	                                        }
	                                        if (!found && !newsong.getSongname().equals("")) {
	                                            log.debug("run(): checking for an existing song with matching uniquestringid=" + newsong.uniquestringid);
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
	                                            log.debug("found matching existing song=" + existing_song);
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
	                                            trackIdMap.put(existing_song.itunes_id, existing_song);
	                                        } else {
	                                            if (!updateonly.isSelected()) {
		                                            log.debug("no matching existing song found, adding a new one");
		                                            if (OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
		                                                rapid_evolution.net.MixshareClient.instance.querySongDetails(newsong, true);
		                                            }
		                                            newsong = SongDB.instance.AddSingleSong(newsong, newsong.stylelist);
                                                    trackIdMap.put(newsong.itunes_id, newsong);
	                                            }
	                                        }
	                                        RapidEvolutionUI.instance.importprogress_ui.instance.progressbar.setValue((int)(((float)songsprocessed) / songcount * 100));
                                    } catch (Exception e) { log.error("run(): error during import", e); }
                                	pacer.endInterval();
                                    }
                                }
                            
                            } else if (l1_node.getNodeName().equalsIgnoreCase("key")) {
                                if ("Playlists".equalsIgnoreCase(l1_node.getTextContent())) {
                                    onPlaylistKey = true; 
                                }                            
                            } else if (l1_node.getNodeName().equalsIgnoreCase("array")) {
                                if (onPlaylistKey) {
                                    NodeList l2_nodelist = l1_node.getChildNodes();
                                    for (int l2_iter = 0; l2_iter < l2_nodelist.getLength(); ++l2_iter) {
                                        Node l2_node = l2_nodelist.item(l2_iter);
                                        if (l2_node.getNodeName().equalsIgnoreCase("dict")) {
                                            String playlistName = null;
                                            Vector songs = new Vector();
                                            NodeList key_nodelist = l2_node.getChildNodes();
                                            for (int keynode_iter = 0; keynode_iter < key_nodelist.getLength(); ++keynode_iter) {
                                                Node key_node = key_nodelist.item(keynode_iter);
                                                if (key_node.getNodeName().equalsIgnoreCase("key")) {
                                                    if (key_node.getTextContent().equals("Name")) {
                                                        playlistName = key_nodelist.item(++keynode_iter).getTextContent();
                                                    }
                                                } else if (key_node.getNodeName().equalsIgnoreCase("array")) {
                                                    NodeList trackNodes = key_node.getChildNodes();
                                                    for (int t = 0; t < trackNodes.getLength(); ++t) {
                                                        Node trackNode = trackNodes.item(t);
                                                        if (trackNode.getNodeName().equals("dict")) {
                                                            NodeList trackAttributes = trackNode.getChildNodes();
                                                            for (int a = 0; a < trackAttributes.getLength(); ++a) {
                                                                Node trackAttr = trackAttributes.item(a);
                                                                if (trackAttr.getNodeName().equalsIgnoreCase("key") && trackAttr.getTextContent().equalsIgnoreCase("Track ID")) {
                                                                	Object song = trackIdMap.get(trackAttributes.item(++a).getTextContent());
                                                                	if (song != null)
                                                                		songs.add(song);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (log.isDebugEnabled())
                                                log.debug("run(): playlist name=" + playlistName + ", songs=" + songs);
                                            if (songs.size() > 0) {
                                                StyleLinkedList style = SongDB.instance.getStyle(playlistName);                                            
                                                if (style == null) {
                                                    style = SongDB.instance.addStyle(playlistName, false);
                                                    if (log.isDebugEnabled())
                                                        log.debug("run(): style id=" + style.getStyleId() + ", s_index=" + style.get_sindex());
                                                }
                                                for (int t = 0; t < songs.size(); ++t) {
                                            		style.insertSong((SongLinkedList)songs.get(t));
                                                }                                            
                                            }
                                        }
                                    }
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
          log.error("run(): error importing itunes xml=" + importfilestr, e);
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
}

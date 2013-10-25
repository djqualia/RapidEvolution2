package com.mixshare.rapid_evolution.data.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rapid_evolution.ImportLib;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;
import com.mixshare.rapid_evolution.audio.dsps.rga.RGAData;

import rapid_evolution.StringUtil;
import java.util.Vector;
import rapid_evolution.StyleLinkedList;
import java.text.SimpleDateFormat;

import rapid_evolution.util.OSHelper;

/**
 * This class exports to an iTunes XML file.
 */
public class iTunesExporter extends Thread {

    static private Logger log = Logger.getLogger(iTunesExporter.class);
    
    static private String encoding = "UTF-8";
    static private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    static private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        
    public static String KEY_TRACKS = "Tracks";
    public static String KEY_PLAYLISTS = "Playlists";
    public static String KEY_TRACK_ID = "Track ID";
    public static String KEY_NAME = "Name";
    public static String KEY_ARTIST = "Artist";
    public static String KEY_ALBUM = "Album";
    public static String KEY_SORT_ALBUM = "Sort Album";
    public static String KEY_GENRE = "Genre";    
    public static String KEY_BPM = "BPM";
    public static String KEY_GROUPING = "Grouping";
    public static String KEY_YEAR = "Year";
    public static String KEY_RATING = "Rating";
    public static String KEY_TRACK_NUMBER = "Track Number";
    public static String KEY_TRACK_TYPE = "Track Type";
    public static String KEY_LOCATION = "Location";
    public static String KEY_COMMENTS = "Comments";
    public static String KEY_PLAYCOUNT = "Play Count";
    public static String KEY_PLAYLIST_ITEMS = "Playlist Items";
    public static String KEY_FILE_FOLDER_COUNT = "File Folder Count";
    public static String KEY_LIBRARY_FOLDER_COUNT = "Library Folder Count";
    public static String KEY_PLAYLIST_ID = "Playlist ID";
    public static String KEY_ALL_ITEMS = "All Items";
    public static String KEY_GAPLESS_ALBUM = "Part Of Gapless Album";
    public static String KEY_VOLUME_ADJUSTMENT = "Volume Adjustment";
    public static String KEY_DATE_ADDED = "Date Added";
    public static String KEY_DESCRIPTION = "Description";
    
    private String filename;
    private boolean use_pacer;
    
    public iTunesExporter(String filename, boolean use_pacer) {
        this.filename = filename;
        this.use_pacer = use_pacer;
    }
    
    public void run() {
        exportDatabaseToITunes(filename, use_pacer);
    }
    
    static private Element buildDocument(Document document, Map data) {
        Element dict = document.createElement("dict");
        Iterator iter = data.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry)iter.next();            
            // key
            Element key = document.createElement("key");
            key.setTextContent(entry.getKey().toString());
            // value
            Object value = entry.getValue();
            Element elemValue = null;
            if (value instanceof Long) {
                elemValue = document.createElement("integer");
                elemValue.setTextContent(value.toString());
            } else if (value instanceof Data) {
                elemValue = document.createElement("data");
                elemValue.setTextContent(value.toString());                                
            } else if (value instanceof Date) {
                elemValue = document.createElement("date");
                elemValue.setTextContent(value.toString());                                
            } else if (value instanceof String) {
                elemValue = document.createElement("string");
                elemValue.setTextContent(value.toString());                
            } else if (value instanceof Boolean) {
                if (((Boolean)value).booleanValue()) {
                    elemValue = document.createElement("true");
                } else {
                    elemValue = document.createElement("false");
                }
            } else if (value instanceof Map) {
                elemValue = buildDocument(document, (Map)value);                
            } else if (value instanceof ArrayList) {
                elemValue = document.createElement("array");
                ArrayList array = (ArrayList)value;
                Iterator aiter = array.iterator();
                while (aiter.hasNext()) {
                    elemValue.appendChild(buildDocument(document, (Map)aiter.next()));
                }
            } else {
                log.warn("buildDocument(): unknown value=" + value + ", key=" + entry.getKey().toString());
            }            
            if (elemValue != null) {
                dict.appendChild(key);                
                dict.appendChild(elemValue);
            }
        }
        return dict;
    }
    
    static private Map parseDictElement(Node node) {
        Map result = new LinkedHashMap();
        NodeList nodes = node.getChildNodes();
        int n = 0;
        while (n < nodes.getLength()) {
            // key
            Node keyNode = nodes.item(n);
            while ((keyNode != null) && !keyNode.getNodeName().equals("key") && (n + 1 < nodes.getLength()))
                keyNode = nodes.item(++n);
            if (keyNode != null) {
                String key = keyNode.getTextContent();                    
                // value
                Node valuenode = nodes.item(++n);
                Object value = parseValue(valuenode);
                while ((value == null) && (n + 1 < nodes.getLength())) {
                    valuenode = nodes.item(++n);
                    value = parseValue(valuenode);
                }
                if (value != null) {
                    //log.trace("exportDatabaseToITunes(): key=" + key + ", value=" + value);
                    result.put(key, value);
                }
            }
            ++n;
        }
        return result;
    }
    
    static private Object parseValue(Node node) {
        if (node == null)
            return null;
        String nodeType = node.getNodeName();
        if (nodeType.equalsIgnoreCase("string"))
            return node.getTextContent();
        if (nodeType.equalsIgnoreCase("date"))
            return new Date(node.getTextContent());
        if (nodeType.equalsIgnoreCase("integer"))
            return Long.parseLong(node.getTextContent());
        if (nodeType.equalsIgnoreCase("true"))
            return Boolean.TRUE;
        if (nodeType.equalsIgnoreCase("false"))
            return Boolean.FALSE;
        if (nodeType.equalsIgnoreCase("#text"))
            return null;
        if (nodeType.equalsIgnoreCase("data"))
            return new Data(node.getTextContent());
        if (nodeType.equalsIgnoreCase("dict"))
            return parseDictElement(node);
        if (nodeType.equalsIgnoreCase("array")) {
            ArrayList newArray = new ArrayList();
            NodeList childNodes = node.getChildNodes();
            for (int c = 0; c < childNodes.getLength(); ++c) {
                Object child = parseValue(childNodes.item(c));
                if (child != null) {
                    newArray.add(child);
                }
            }
            return newArray;
        }
        log.warn("unknown value node type=" + nodeType);
        return null;
    }
    
    // for testing
    static public void main(String[] args) {
        try {
            PropertyConfigurator.configure("log4j.properties");   
            exportDatabaseToITunes("C:\\Documents and Settings\\db2admin\\My Documents\\My Music\\iTunes\\iTunes Music Library.xml", false);
        } catch (Exception e) {
            log.error("main(): error", e);
        }
    }
    
    static public class Date {
        private String date = null;
        public Date(String date) { this.date = date; }
        public String getDate() { return date; }
        public String toString() { return date.toString(); }
    }    
    static public class Data {
        private String data = null;
        public Data(String data) { this.data = data; }
        public String getData() { return data; }
        public String toString() { return data.toString(); }
    }
    
    static public boolean exportDatabaseToITunes(String save_filename, boolean use_pacer) {
        boolean success = false;
        if (RapidEvolutionUI.instance != null) {
            RapidEvolutionUI.instance.exportprogress_ui.instance.progressbar.setValue(0);
            RapidEvolutionUI.instance.exportprogress_ui.Display();
        }
        try {
            PaceMaker pacer = new PaceMaker();
            if (log.isDebugEnabled()) log.debug("exportDatabaseToITunes(): filename=" + save_filename);
            if (log.isTraceEnabled()) log.trace("exportDatabaseToITunes(): use_pacer=" + use_pacer);
            
            Map documentDict = null;
            
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xPath = xPathfactory.newXPath();              
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            try {
                // read in itunes db info
                document = builder.parse(new File(save_filename).toURI().toString());
                Element mainDict = (Element)xPath.evaluate("//plist/dict", document, XPathConstants.NODE);
                documentDict = parseDictElement(mainDict);
                // determine max track ID
                long maxTrackId = 0;
                long maxPlaylistId = 0;
                Map tracks = (Map)documentDict.get(KEY_TRACKS);
                ArrayList playlists = (ArrayList)documentDict.get(KEY_PLAYLISTS);
                Vector masterPlaylists = new Vector();
                Iterator playlistIter = playlists.iterator();
                while (playlistIter.hasNext()) {
                    Map playlist = (Map)playlistIter.next();
                    String name = (String)playlist.get(KEY_NAME);
                    long playlistId = (Long)playlist.get(KEY_PLAYLIST_ID);
                    maxPlaylistId = Math.max(maxPlaylistId, playlistId);
                    boolean add = false;
                    if (name.equalsIgnoreCase("Library"))
                        add = true;
                    else if (name.equalsIgnoreCase("Music"))
                        add = true;
                    if (add) {
                        ArrayList playlistItems = (ArrayList)playlist.get(KEY_PLAYLIST_ITEMS);
                        if (playlistItems == null)
                            playlistItems = new ArrayList();
                        masterPlaylists.add(playlistItems);
                    }
                }
                Iterator trackIter = tracks.keySet().iterator();
                while (trackIter.hasNext()) {
                    Long uniqueId = Long.parseLong(trackIter.next().toString());
                    maxTrackId = Math.max(maxTrackId, uniqueId.longValue());
                }
                // add/update re2 track info
                int count = 0;
                boolean emptyMode = (tracks.size() == 0);
                int totalSongs = SongDB.instance.getSongCount();
                SongLinkedList iter = SongDB.instance.SongLL;            
                pacer.reset();            
                while ((iter != null) && !ImportLib.stopexporting) {
                    if (use_pacer) pacer.startInterval();                    
                    boolean passes_rating_check = true;
                    if ((iter.getRatingInt() != 0) && (iter.getRatingInt() < 3))
                        passes_rating_check = false;
                    if (iter.isDisabled())
                    	passes_rating_check = false;
                    if (!iter.getFileName().equals("") && passes_rating_check) {
                        try {
                            File iterFile = new File(iter.getFileName());
                            // see if the song already exists in itunes DB
                            trackIter = tracks.entrySet().iterator();
                            Map updateTrack = null;
                            boolean existingFound = false;
                            if (!emptyMode) {
                                while (trackIter.hasNext() && !existingFound) {
                                    Entry entry = (Entry)trackIter.next();
                                    Map track = (Map)entry.getValue();                                
                                    Long trackID = (Long)track.get(KEY_TRACK_ID);                                                                
                                    boolean iTunesIDMatch = false; //((iter.itunes_id != null) && (iter.itunes_id.length() > 0) && (trackID.longValue() == Long.parseLong(iter.itunes_id)));
                                    boolean fileMatch = false;
                                    if (!iTunesIDMatch) {
                                        String filename = (String)track.get(KEY_LOCATION);
                                        filename = StringUtil.ReplaceString("+", filename, "__myplussign__");
                                        try {
                                            filename = URLDecoder.decode(filename, encoding);
                                        } catch (Exception e) {
                                            filename = StringUtil.ReplaceString("%20", filename, " ");
                                            filename = StringUtil.ReplaceString("%5B", filename, "[");
                                            filename = StringUtil.ReplaceString("%5D", filename, "]");
                                            filename = StringUtil.ReplaceString("&#38;", filename, "&");
                                            filename = StringUtil.ReplaceString("%23", filename, "#");
                                        }
                                        filename = StringUtil.ReplaceString("__myplussign__", filename, "+");
                                        // old code taken from itunes import class, can't remember why it's all necessary...
                                        if (OSHelper.isWindows()) {
                                            if (filename.toLowerCase().startsWith("file://localhost/")) {
                                                filename = filename.substring(17);
                                              }                                                      
                                            while (filename.startsWith("/")) filename = filename.substring(1);
                                        } else {
                                            if (filename.toLowerCase().startsWith("file://localhost/")) {
                                                filename = filename.substring(16);
                                              }                                                      
                                        }
                                        if (filename.toLowerCase().endsWith("\\")) {
                                            filename = filename.substring(0, filename.length() - 1);
                                        }
                                        if (filename.toLowerCase().endsWith("/")) {
                                            filename = filename.substring(0, filename.length() - 1);
                                        }                                                                        
                                        File file = new File(filename);
                                        if (file.equals(iterFile))
                                            fileMatch = true;
                                    }
                                    if (iTunesIDMatch || fileMatch) {
                                        // existing song found
                                        existingFound = true;
                                        //log.trace("exportDatabaseToITunes(): existing track found=" + track);
                                        updateTrack = track;                                    
                                    }                                                                
                                }
                            }
                            if (!existingFound) {
                                log.trace("exportDatabaseToITunes(): adding new song=" + iter);
                                updateTrack = new LinkedHashMap();
                                
                                // track id
                                long trackId = ++maxTrackId;
                                updateTrack.put(KEY_TRACK_ID, trackId);
                                
                                // location
                                StringBuffer location = new StringBuffer();                                
                                String encodedFilename = iter.getFileName();

                                encodedFilename = StringUtil.ReplaceString("\\", encodedFilename, "/");
                                StringTokenizer tokenizer = new StringTokenizer(encodedFilename, "/");
                                StringBuffer newFilename = new StringBuffer();
                                while (tokenizer.hasMoreTokens()) {
                                    String token = tokenizer.nextToken();
                                    if (newFilename.length() > 0)
                                        newFilename.append("/");
                                    token = URLEncoder.encode(token, encoding);
                                    token = StringUtil.ReplaceString("+", token, "%20");
                                    newFilename.append(token);
                                }
                                encodedFilename = newFilename.toString();
                                /*
                                encodedFilename = StringUtil.ReplaceString(" ", encodedFilename, "%20");
                                encodedFilename = StringUtil.ReplaceString("[", encodedFilename, "%5B");
                                encodedFilename = StringUtil.ReplaceString("]", encodedFilename, "%5D");
                                encodedFilename = StringUtil.ReplaceString("\\", encodedFilename, "/");
                                encodedFilename = StringUtil.ReplaceString("&", encodedFilename, "&#38;");                                
                                encodedFilename = StringUtil.ReplaceString("#", encodedFilename, "%23");
                                */
                                
                                location.append(encodedFilename);
                                // old code taken from itunes import class, can't remember why it's all necessary...
                                if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                                    location.insert(0, "file://localhost/");
                                } else {
                                    if (location.charAt(0) == '/')
                                        location.insert(0, "file://localhost");
                                    else
                                        location.insert(0, "file://localhost/");
                                }
                                updateTrack.put(KEY_LOCATION, location.toString());
                                
                                // misc crap
                                updateTrack.put(KEY_FILE_FOLDER_COUNT, new Long(-1));
                                updateTrack.put(KEY_LIBRARY_FOLDER_COUNT, new Long(-1));                                
                                
                                // add track references to master playlists
                                Map newTrack = new LinkedHashMap();
                                newTrack.put(KEY_TRACK_ID, trackId);
                                playlistIter = masterPlaylists.iterator();
                                while (playlistIter.hasNext()) {
                                    ArrayList playlistItems = (ArrayList)playlistIter.next();
                                    playlistItems.add(newTrack);
                                }
                                
                            }      
                            
                            // name
                            StringBuffer name = new StringBuffer();
                            name.append(iter.getSongname());
                            if (iter.getRemixer().length() > 0) {
                                name.append(" (");
                                name.append(iter.getRemixer());
                                name.append(")");
                            }
                            if (name.length() > 0)
                                updateTrack.put(KEY_NAME, name.toString());
                            
                            // artist
                            if (iter.getArtist().length() > 0)
                                updateTrack.put(KEY_ARTIST, iter.getArtist());
                  
                            // album
                            if (iter.getAlbum().length() > 0) {
                                updateTrack.put(KEY_ALBUM, iter.getAlbum());
                                updateTrack.put(KEY_SORT_ALBUM, iter.getAlbum());
                            }

                            // genre
                            String genre = iter.getGenre();
                            if (genre == null)
                                genre = "Unknown";
                            if (genre.length() > 0)
                                updateTrack.put(KEY_GENRE, genre);
        
                            // comments
                            String comments = iter.getComments();
                            if (comments.length() > 0)
                                updateTrack.put(KEY_COMMENTS, iter.getComments());
                            
                            // play count
                            Long playCount = (Long)updateTrack.get(KEY_PLAYCOUNT);
                            if (playCount == null)
                                playCount = new Long(iter.getTimesPlayed());
                            else
                                playCount = new Long(Math.max(playCount.longValue(), iter.getTimesPlayed()));
                            updateTrack.put(KEY_PLAYCOUNT, playCount);
                            
                            // track number
                            try {
                                long trackNumber = Long.parseLong(iter.getTrack());
                                updateTrack.put(KEY_TRACK_NUMBER, trackNumber);
                            } catch (Exception e) { }
                            
                            // year
                            try {
                                long year = Long.parseLong(iter.getCustomDataType(SkinManager.instance.getMessageText("custom_field_id3_tag_year")));
                                updateTrack.put(KEY_YEAR, year);
                            } catch (Exception e) { }
                                                        
                            // label
                            try {
                                String label = iter.getCustomDataType(SkinManager.instance.getMessageText("custom_field_id3_tag_publisher"));
                                if ((label != null) && (label.length() > 0))
                                    updateTrack.put(KEY_DESCRIPTION, label);
                            } catch (Exception e) { }
                            
                            // bpm
                            long bpm = (long)iter.getStartbpm();
                            if (bpm == 0)
                                bpm = (long)iter.getEndbpm();
                            if (bpm != 0)
                                updateTrack.put(KEY_BPM, bpm);
                            
                            // key
                            Key key = iter.getStartKey();
                            if (!key.isValid())
                                key = iter.getEndKey();
                            if (key.isValid()) {
                                updateTrack.put(KEY_GROUPING, key.getGroupSSLString());
                            }
                            
                            // rating
                            long rating = iter.getRatingInt();
                            if (rating > 0) {
                                rating *= 20; // to get 0-100 scale
                                updateTrack.put(KEY_RATING, rating);
                            }                                      
                            
                            // track type
                            updateTrack.put(KEY_TRACK_TYPE, "File");
                                                        
                            // gapless album
                            if (!updateTrack.containsKey(KEY_GAPLESS_ALBUM))
                                updateTrack.put(KEY_GAPLESS_ALBUM, Boolean.FALSE);
                            
                            // volume adjust
                            RGAData rga = iter.getRGA();
                            if (rga != null) {
                                long value = (long)rga.getDifference() * 10;
                                value = Math.min(250, value);
                                value = Math.max(-250, value);
                                updateTrack.put(KEY_VOLUME_ADJUSTMENT, value);
                            }
                    
                            // date added
                            StringBuffer dateAdded = new StringBuffer();
                            dateAdded.append(dateformat.format(iter.getDateAdded()));
                            dateAdded.append("T");
                            dateAdded.append(timeFormat.format(iter.getDateAdded()));
                            dateAdded.append("Z");
                            updateTrack.put(KEY_DATE_ADDED, new Date(dateAdded.toString()));
                            
                            // update track references to style playlists
                            Map trackReference = new LinkedHashMap();
                            long thisTrackId = (Long)updateTrack.get(KEY_TRACK_ID);
                            trackReference.put(KEY_TRACK_ID, thisTrackId);
                            StyleLinkedList siter = SongDB.instance.masterstylelist;
                            while (siter != null) {
                                if (siter.containsLogical(iter)) {
                                    // check if style already exists as a playlist
                                    boolean found = false;
                                    playlistIter = playlists.iterator();
                                    while (playlistIter.hasNext() && !found) {
                                        Map playlist = (Map)playlistIter.next();
                                        String playlistName = (String)playlist.get(KEY_NAME);
                                        if (playlistName.equalsIgnoreCase(siter.getName())) {
                                            found = true;
                                            ArrayList playlistItems = (ArrayList)playlist.get(KEY_PLAYLIST_ITEMS);
                                            // see if a track reference already exists
                                            boolean reference_found = false;
                                            Iterator refIter = playlistItems.iterator();
                                            while (refIter.hasNext() && !reference_found) {
                                                Map ref = (Map)refIter.next();
                                                long trackId = (Long)ref.get(KEY_TRACK_ID);
                                                if (trackId == thisTrackId) {
                                                    reference_found = true;
                                                }
                                            }
                                            if (!reference_found)
                                                playlistItems.add(trackReference);
                                        }
                                    }
                                    if (!found) {
                                        Map newPlaylist = new LinkedHashMap();
                                        newPlaylist.put(KEY_NAME, siter.getName());
                                        newPlaylist.put(KEY_PLAYLIST_ID, ++maxPlaylistId);
                                        newPlaylist.put(KEY_ALL_ITEMS, Boolean.TRUE);
                                        ArrayList playlistItems = new ArrayList();
                                        playlistItems.add(trackReference);
                                        newPlaylist.put(KEY_PLAYLIST_ITEMS, playlistItems);
                                        playlists.add(newPlaylist);
                                    }
                                } else {
                                    // check if style already exists as a playlist
                                    boolean found = false;
                                    playlistIter = playlists.iterator();
                                    while (playlistIter.hasNext() && !found) {
                                        Map playlist = (Map)playlistIter.next();
                                        String playlistName = (String)playlist.get(KEY_NAME);
                                        if (playlistName.equalsIgnoreCase(siter.getName())) {
                                            found = true;
                                            ArrayList playlistItems = (ArrayList)playlist.get(KEY_PLAYLIST_ITEMS);
                                            // see if a track reference already exists
                                            boolean reference_found = false;
                                            Iterator refIter = playlistItems.iterator();
                                            while (refIter.hasNext() && !reference_found) {
                                                Map ref = (Map)refIter.next();
                                                long trackId = (Long)ref.get(KEY_TRACK_ID);
                                                if (trackId == thisTrackId) {
                                                    reference_found = true;
                                                    refIter.remove();
                                                }
                                            }
                                        }
                                    }                                    
                                }
                                siter = siter.next;
                            }
                            
                            // add the track
                            tracks.put(updateTrack.get(KEY_TRACK_ID).toString(), updateTrack);
                            
                        } catch (Exception e) {
                            log.error("exportDatabaseToITunes(): error", e);
                        }                
                    }
                    iter = iter.next;
                    if (use_pacer) pacer.endInterval();
                    ++count;
                    RapidEvolutionUI.instance.exportprogress_ui.instance.progressbar.setValue(count * 100 / totalSongs);                
                }
                                
            } catch (java.io.FileNotFoundException fnfe) {
                log.debug("exportDatabaseToITunes(): file does not currently exist");
            }
            
            document = builder.newDocument();            
            Element plist = document.createElement("plist");
            plist.setAttribute("version", "1.0");
            plist.appendChild(buildDocument(document, documentDict));
            document.appendChild(plist);
                
            if (!ImportLib.stopexporting) {
                if (log.isDebugEnabled())
                    log.debug("exportDatabaseToITunes(): writing to XML");
                writeXmlFile(document, save_filename);
                
                success = true;
                if (log.isDebugEnabled())
                    log.debug("exportDatabaseToITunes(): success!");
                
                // purposely corrupt iTunes database
                String iTunesXML = "iTunes Music Library.xml";
                String iTunesITL = "iTunes Library.itl";
                int xmlIndex = save_filename.indexOf(iTunesXML);
                if (xmlIndex >= 0) {
                    String itl_filename = StringUtil.ReplaceString(iTunesXML, save_filename, iTunesITL);
                    File file = new File(itl_filename);
                    if (file.exists()) {
                        FileWriter outputstream = new FileWriter(itl_filename);
                        BufferedWriter outputbuffer = new BufferedWriter(outputstream);
                        outputbuffer.write("Sorry iTunes, please recreate me.");
                        outputbuffer.close();
                        outputstream.close();
                    }                    
                }
                
            }                
            
        }
        catch (Exception e) {
            log.error("exportDatabaseToITunes: error", e);
        }
        if (RapidEvolutionUI.instance != null)
            RapidEvolutionUI.instance.exportprogress_ui.Hide();
        return success;
    }
        
    // This method writes a DOM document to a file
    public static void writeXmlFile(Document doc, String filename)
    {
        try
        {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);
            // Prepare the output file
            File file = new File(filename);
            Result result = new StreamResult(file);
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, result);
        }
        catch (TransformerConfigurationException e)
        {
            log.error("writeXmlFile(): error", e);
        }
        catch (TransformerException e)
        {
            log.error("writeXmlFile(): error", e);
        }
    }   
        
}

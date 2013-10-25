package com.mixshare.rapid_evolution.data.export;

import java.io.File;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import rapid_evolution.ImportLib;

import com.mixshare.rapid_evolution.util.timing.PaceMaker;

/**
 * This class exports to a Native Instruments Traktor music library file (.NML)
 */
public class NMLExporter extends Thread {

    static private Logger log = Logger.getLogger(NMLExporter.class);
    
    private String filename;
    private boolean use_pacer;
    
    public NMLExporter(String filename, boolean use_pacer) {
        this.filename = filename;
        this.use_pacer = use_pacer;
    }
    
    public void run() {
        exportDatabaseToTraktor(filename, use_pacer);
    }
    
    static public boolean exportDatabaseToTraktor(String save_filename, boolean use_pacer) {
        boolean success = false;
        RapidEvolutionUI.instance.exportprogress_ui.instance.progressbar.setValue(0);
        RapidEvolutionUI.instance.exportprogress_ui.Display();
        try {
            PaceMaker pacer = new PaceMaker();
            if (log.isDebugEnabled()) log.debug("exportDatabaseToTraktor(): filename=" + save_filename);
            if (log.isTraceEnabled()) log.trace("exportDatabaseToTraktor(): use_pacer=" + use_pacer);
            
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xPath = xPathfactory.newXPath();              
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            try {
                document = builder.parse(new File(save_filename).toURI().toString());
            } catch (java.io.FileNotFoundException fnfe) {
                log.debug("exportDatabaseToTraktor(): file does not currently exist");
            }
            if (document == null)
                document = builder.newDocument();
            
            Element root = (Element)xPath.evaluate("//NML", document, XPathConstants.NODE);
            if (root == null) {
                // create the root element (NML)
                root = document.createElement("NML");
                root.setAttribute("VERSION", "9");
                document.appendChild(root);
            }
                        
            Element head = (Element)xPath.evaluate("//NML/HEAD", document, XPathConstants.NODE);
            if (head == null) {                
                // create sub-element HEAD
                head = document.createElement("HEAD");
                root.appendChild(head);
                head.setAttribute("COMPANY", "www.native-instruments.com");
                head.setAttribute("PROGRAM", "Traktor 3 - Native Instruments");
            }
            
            Element plist = (Element)xPath.evaluate("//NML/PLAYLIST", document, XPathConstants.NODE);
            if (plist == null) {
                // create playlists entry
                plist = document.createElement("PLAYLIST");
                root.appendChild(plist);
                plist.setAttribute("ENTRIES", "1");
                plist.setAttribute("MODE", "LIST");
            }
            
            Element mf = (Element)xPath.evaluate("//NML/PLAYLIST/MUSICFOLDERS", document, XPathConstants.NODE);
            if (mf == null) {
                // add musicfolders entry
                mf = document.createElement("MUSICFOLDERS");
                plist.appendChild(mf);
            }
            
            if (log.isDebugEnabled())
                log.debug("exportToTraktor(): saving songs...");
            int totalSongs = SongDB.instance.getSongCount();
            int count = 0;
            Object releasesObj = xPath.evaluate("//NML/PLAYLIST/ENTRY", document, XPathConstants.NODESET);
            NodeList releaseNodes = (NodeList) releasesObj;
            String lcOSName = System.getProperty("os.name").toLowerCase();
            boolean MAC_OS_X = lcOSName.startsWith("mac os x");
            SongLinkedList iter = SongDB.instance.SongLL;            
            pacer.reset();            
            while ((iter != null) && !ImportLib.stopexporting) {
                if (use_pacer) pacer.startInterval();
                
                if (!iter.getFileName().equals("")) {
                    try {
                        Element entry = null;
                        // first, see if the song already exists in the document
                        int n = 0;
                        boolean found = false;
                        while ((n < releaseNodes.getLength()) && !found) {
                            Node item = releaseNodes.item(n);
                            String artist = getAttributeValue(item.getAttributes(), "ARTIST");
                            String title = getAttributeValue(item.getAttributes(), "TITLE");
                            String filename = null;
                            String album = null;
                            String track = null;
                            for (int c = 0; c < item.getChildNodes().getLength(); ++c) {
                                Node child = item.getChildNodes().item(c);
                                if (child.getNodeName().equals("LOCATION")) {
                                    String dir = getAttributeValue(child.getAttributes(), "DIR");
                                    String file = getAttributeValue(child.getAttributes(), "FILE");
                                    String volume = getAttributeValue(child.getAttributes(), "VOLUME");
                                    StringBuffer fullPath = new StringBuffer();
                                    if (MAC_OS_X)
                                        fullPath.append(volumesId);
                                    fullPath.append(volume);
                                    fullPath.append(dir);
                                    fullPath.append(file);
                                    filename = fullPath.toString();
                                } else if (child.getNodeName().equals("ALBUM")) {
                                    album = getAttributeValue(child.getAttributes(), "TITLE");
                                    track = getAttributeValue(child.getAttributes(), "TRACK");
                                }
                            }
                            if (filename != null) {
                                if (iter.getFile().equals(new File(filename))) {
                                    found = true;
                                    entry = (Element)item;
                                    if (log.isDebugEnabled())
                                        log.debug("exportDatabaseToTraktor(): found existing tag for=" + iter);
                                }
                            }
                            ++n;
                        }
                        
                        if (entry == null) {
                            if (log.isDebugEnabled())
                                log.debug("exportDatabaseToTraktor(): creating new tag for=" + iter);
                            entry = document.createElement("ENTRY");
                            plist.appendChild(entry);
                            entry.setAttribute("TYPE", "1");    // TODO unknown value in traktor NML file
                            
                            // create LOCATION subtag
                            Element location = document.createElement("LOCATION");
                            entry.appendChild(location);
                            String paramArr[] = new String[3];
                            extractFilePathInfo(iter.getFileName(), paramArr);
                            location.setAttribute("DIR", paramArr[1]);
                            location.setAttribute("FILE", paramArr[2]);
                            location.setAttribute("VOLUME", paramArr[0]);                    
                        }
                        if (!iter.getArtist().equals("")) entry.setAttribute("ARTIST", iter.getArtist());
                        //songelement.setAttribute("AUDIO_ID", ""); // TODO generated by traktor ?!
                        if (!iter.getSongname().equals("")) entry.setAttribute("TITLE", iter.getSongname());
                         
                        Element albumNode = null;
                        Element infoNode = null;
                        Element tempoNode = null;
                        Element extDataNode = null;
                        for (int c = 0; c < entry.getChildNodes().getLength(); ++c) {
                            Node child = entry.getChildNodes().item(c);
                            if (child.getNodeName().equals("ALBUM")) {
                                albumNode = (Element)child;
                            } else if (child.getNodeName().equals("INFO")) {
                                infoNode = (Element)child;
                            } else if (child.getNodeName().equals("TEMPO")) {
                                tempoNode = (Element)child;
                            } else if (child.getNodeName().equals("EXTDATA")) {
                                extDataNode = (Element)child;
                            }
                        }
                        
                        if (albumNode == null) {
                            // create ALBUM subtag
                            albumNode = document.createElement("ALBUM");
                            entry.appendChild(albumNode);
                        }
                        if (!iter.getAlbum().equals("")) albumNode.setAttribute("TITLE", iter.getAlbum());
                        if (!iter.getTrack().equals("")) albumNode.setAttribute("TRACK", iter.getTrack());
    
                        if (infoNode == null) {
                            // create INFO subtag
                            infoNode = document.createElement("INFO");
                            entry.appendChild(infoNode);                    
                        }
                        String catNo = iter.getCustomDataType(SkinManager.instance.getMessageText("custom_field_id3_tag_catalogid"));
                        if ((catNo != null) && !catNo.equals(""))
                            infoNode.setAttribute("CATALOG_NO", catNo);
                        String comments = StringUtil.processcomments(iter.getComments()).toString();
                        if ((comments != null) && !comments.equals(""))
                            infoNode.setAttribute("COMMENT", comments);
                        String genre = iter.getGenre();
                        if ((genre != null) && !genre.equals(""))
                            infoNode.setAttribute("GENRE", genre);
                        String label = iter.getCustomDataType(SkinManager.instance.getMessageText("custom_field_id3_tag_publisher"));
                        if ((label != null) && !label.equals(""))
                            infoNode.setAttribute("LABEL", label);
                        if (!iter.getRemixer().equals(""))
                            infoNode.setAttribute("MIX", iter.getRemixer());
                        if (iter.getStartKey().isValid())
                            infoNode.setAttribute("KEY", iter.getStartKey().toString());
                        if (iter.getRating() > 0) {
                            String rating = "0";
                            if (iter.getRating() == 5) rating = "255";
                            if (iter.getRating() == 4) rating = "204";
                            if (iter.getRating() == 3) rating = "153";
                            if (iter.getRating() == 2) rating = "102";
                            if (iter.getRating() == 1) rating = "51";                    
                            infoNode.setAttribute("RANKING", rating);
                        }
                        
                        
                        if (tempoNode == null) {
                            // create TEMPO subtag
                            tempoNode = document.createElement("TEMPO");
                            entry.appendChild(tempoNode);
                        }
                        if (iter.getStartbpm() != 0.0f)
                            tempoNode.setAttribute("BPM", String.valueOf(iter.getStartbpm()));
    
                        // TODO create cue tags if RE2 ever supports
                        
                        if (extDataNode == null) {
                            // create EXTDATA subtag
                            Element extdata = document.createElement("EXTDATA");
                            entry.appendChild(extdata);                    
                        }
                        
                    } catch (Exception e) {
                        log.error("exportToTraktor(): error", e);
                    }                
                }

                iter = iter.next;
                if (use_pacer) pacer.endInterval();
                ++count;
                RapidEvolutionUI.instance.exportprogress_ui.instance.progressbar.setValue(count * 100 / totalSongs);                
            }           
            
            if (!ImportLib.stopexporting) {
                if (log.isDebugEnabled())
                    log.debug("exportToTraktor(): writing to XML");
                writeXmlFile(document, save_filename);
                
                success = true;
                if (log.isDebugEnabled())
                    log.debug("exportToTraktor(): success!");
            }
        }
        catch (Exception e) {
            log.error("exportToTraktor: error", e);
        }
        RapidEvolutionUI.instance.exportprogress_ui.Hide();
        return success;
    }

    private static String volumesId = "/Volumes/";
    
    /**
     * Main class, imports the RE2 xml data
     * into custom "Song" objects and then
     * exports it to traktor NML format
     * @param filename  - the input filename
     * @param params - the output, filename splittet into 0: volume 1: path 2: filename
     */
    public static void extractFilePathInfo(String filename, String params[])
    {
        File f = new File(filename);
               
        // search for the volume identifier
        if (filename.startsWith(volumesId)) {
            // mac
            int index = filename.indexOf("/", volumesId.length());
            params[0] = filename.substring(volumesId.length(), index);
        } else {
            // windows
            File s = f;
            while(s.getParentFile() != null)
                s = s.getParentFile();
            String volume = s.getAbsolutePath();
            params[0] = volume;
        }
        
        //extract the path
        String path = f.getAbsolutePath();        
        path = path.substring(path.indexOf(params[0]) + params[0].length()); // remove the volume
        path = path.substring(0, path.length() - f.getName().length()); // remove the filename
        params[1] = path;
        
        // extract the filename
        params[2] = f.getName();
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
    
    private static String getFirstValue(Node node) 
    {
        Node child = node.getFirstChild();
        if (child != null) 
        {
            String value = child.getNodeValue();
            if (value != null) return value;
        }
        return "";
    }
    
    private static String getAttributeValue(NamedNodeMap attributes, String id) {
        Node node = attributes.getNamedItem(id);
        if (node != null)
            return node.getTextContent();
        return "";
    }
    
}

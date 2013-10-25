package rapid_evolution.ui;

import rapid_evolution.SongLinkedList;
import java.io.IOException;
import rapid_evolution.RapidEvolution;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.Vector;
import java.awt.datatransfer.DataFlavor;

import org.apache.log4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FileSelection implements Transferable
{
    private static Logger log = Logger.getLogger(FileSelection.class);
    
    final static int FILE = 0;
    final static int STRING = 1;
    final static int PLAIN = 2;
    DataFlavor flavors[] = {DataFlavor.javaFileListFlavor,
                            DataFlavor.stringFlavor};

    private Vector songs;
    private Vector files;
    
    public FileSelection(File file, SongLinkedList songptr)
    {
        files = new Vector();
        if ((file != null) && file.exists()) {
            if (log.isTraceEnabled())
                log.trace("FileSelection(): dragging file=" + file);
            /*
            try {
                byte[] theBytes = file.getAbsolutePath().getBytes("UTF8");
                String inUTF8 = new String(theBytes, "UTF8");            
                if (log.isTraceEnabled())
                    log.trace("FileSelection(): inUTF8=" + inUTF8);
                file = new File(inUTF8);
            } catch (Exception e) {
                log.error("FileSelection(): error", e);
            }
            */
            files.addElement(file);
        }
        songs = new Vector();
        songs.add(new Long(songptr.uniquesongid));
    }

    public FileSelection(Vector _files, SongLinkedList[] _songs)
    {
        files = _files;
        songs = new Vector();
        for (int i = 0; i < _songs.length; ++i) {
            SongLinkedList song = _songs[i];
            songs.addElement(new Long(song.uniquesongid));
        }        
        for (int f = 0; f < files.size(); ++f) {
            File file = (File)files.get(f);          
            if (log.isTraceEnabled())
                log.trace("FileSelection(): dragging file=" + file);                
            /*
            try {
                byte[] theBytes = file.getAbsolutePath().getBytes("UTF8");
                String inUTF8 = new String(theBytes, "UTF8");            
                if (log.isTraceEnabled())
                    log.trace("FileSelection(): inUTF8=" + inUTF8);                
                file = new File(inUTF8);
                files.set(f, file);
            } catch (Exception e) {
                log.error("FileSelection(): error", e);
            } 
            */           
        }
    }

    /* Returns the array of flavors in which it can provide the data. */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /* Returns whether the requested flavor is supported by this object. */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.getRepresentationClass().equals(rapid_evolution.StyleLinkedList.class)) return true;
        boolean b  = false;
        b |= flavor.equals(flavors[FILE]);
        b |= flavor.equals(flavors[STRING]);
            return (b);
    }

    public synchronized Object getTransferData(DataFlavor flavor)
                        throws UnsupportedFlavorException, IOException {
        log.trace("getTransferData(): flavor: " + flavor);
        if (flavor.equals(flavors[FILE])) {
            if (log.isTraceEnabled())
                log.trace("getTransferData(): returning files=" + files);
            return files;
//        } else if (flavor.equals(flavors[PLAIN])) {
  //          return song.uniquesongid; //new StringReader(((File)elementAt(0)).getAbsolutePath());
        } else if (flavor.equals(flavors[STRING]) || flavor.getRepresentationClass().equals(rapid_evolution.StyleLinkedList.class)) {
            return songs; //File)elementAt(0)).getAbsolutePath();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    public SongLinkedList getFirstSong() {
        return (SongLinkedList)songs.get(0);
    }
}

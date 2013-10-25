package rapid_evolution;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.io.File;
import javax.swing.ImageIcon;
import rapid_evolution.ui.SkinManager;

import org.apache.log4j.Logger;

public class ImageSet {
    
    private static Logger log = Logger.getLogger(ImageSet.class);
    
    public ImageSet() { }
    public ImageSet(String thumbnail_filename) {
        this.thumbnail_filename = thumbnail_filename;
        files.put(thumbnail_filename, null);
    }
    private HashMap files = new HashMap();
    private String thumbnail_filename = null;
    public void computeThumbnailFilename() {
        String thumbnail = null;
        int smallest_area = 0;
        Set fileset = files.keySet();
        if (fileset != null) {
            Iterator iter = fileset.iterator();
            while (iter.hasNext()) {
                String filename = (String)iter.next();
                try {
                    ImageIcon icon = new ImageIcon(filename);
                    if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                        int area = icon.getIconHeight() * icon.getIconWidth();
                        if (area > 0) {
                            if ((thumbnail == null) || (area < smallest_area)) {
                                smallest_area = area;
                                thumbnail = filename;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("computeThumbnailFilename(): error", e);
                }
            }
        }
        thumbnail_filename = thumbnail;       
    }
    public int getNumFile() { return files.size(); }
    public String getThumbnailFilename() {
        String filename = getThumbnailFilenameAux();
        if (filename != null) {
            File file = new File(filename);
            if (!file.exists()) {
                validate();
                filename = getThumbnailFilenameAux();
            }
        }
        return filename;
    }
    public String getThumbnailFilenameAux() {
        if (thumbnail_filename != null) return thumbnail_filename;
        Set fileset = files.keySet();
        if (fileset != null) {
            Iterator iter = fileset.iterator();
            if (iter.hasNext()) {
                return (String)iter.next();
            }
        }
        return null;
    }
    public void setThumbnailFilename(String filename) {
        thumbnail_filename = filename;
    }
    public void addFile(String filename) {
        files.put(filename, null);
    }
    public String[] getFiles() {
        String[] result = new String[files.size()];
        Iterator iter = files.keySet().iterator();
        int count = 0;
        while (iter.hasNext()) result[count++] = (String)iter.next();
        return result;
    }
    public void setFiles(String[] filenames) {
        files.clear();
        for (int i = 0; i < filenames.length; ++i) addFile(filenames[i]);
    }
    
    public void validate() {
        Iterator iter = files.keySet().iterator();
        boolean removed = false;
        while (iter.hasNext()) {
            String filename = (String)iter.next();
            File file = new File(filename);
            ImageIcon icon = SkinManager.instance.getImageIcon(filename);
            if (!file.exists() || (icon.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE)) {
                iter.remove();
                removed = true;
            }
        }
        if (removed) computeThumbnailFilename();        
    }
    
    public boolean isValid() {
        validate();
        return ((files.size() > 0) && (thumbnail_filename != null));
    }
    
}

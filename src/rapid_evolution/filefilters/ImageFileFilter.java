package rapid_evolution.filefilters;

import rapid_evolution.RapidEvolution;
import java.io.File;
import rapid_evolution.ui.SkinManager;

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

public class ImageFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File file) {
        String filename = file.getName();
        if (file.isDirectory()) return true;
        return isImage(filename);
    }
    public static boolean isImage(String filename) {
        if (filename.toLowerCase().endsWith(".gif")) return true;
        if (filename.toLowerCase().endsWith(".jpg")) return true;
        if (filename.toLowerCase().endsWith(".jpeg")) return true;
        if (filename.toLowerCase().endsWith(".png")) return true;
        if (filename.toLowerCase().endsWith(".bmp")) return true;
        if (filename.toLowerCase().endsWith(".tiff")) return true;
        if (filename.toLowerCase().endsWith(".tif")) return true;
        return false;
    }    
    public String getDescription() {
        return SkinManager.instance.getMessageText("file_filter_images");
    }
  }

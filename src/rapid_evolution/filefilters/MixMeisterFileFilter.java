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
public class MixMeisterFileFilter extends javax.swing.filechooser.FileFilter {
      public boolean accept(File file) {
          String filename = file.getName();
          if (file.isDirectory()) return true;
          return (filename.toLowerCase().endsWith(".txt"));
      }
      public String getDescription() {
          return SkinManager.instance.getMessageText("file_filter_mixmeister");
      }
}

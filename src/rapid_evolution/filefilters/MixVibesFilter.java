package rapid_evolution.filefilters;

import java.io.File;

import rapid_evolution.ui.SkinManager;


public class MixVibesFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File file) {
        String filename = file.getName();
        if (file.isDirectory()) return true;
        return (filename.toLowerCase().endsWith(".txt"));
    }
    public String getDescription() {
        return SkinManager.instance.getMessageText("file_filter_mixvibes");
    }
}

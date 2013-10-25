package com.mixshare.rapid_evolution.library;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.ui.swing.lookfeel.LookAndFeelManager;
import com.mixshare.rapid_evolution.util.io.TextFileReader;
import com.mixshare.rapid_evolution.util.io.TextFileWriter;
import rapid_evolution.util.OSHelper;

public class RootMusicExcludeList {

    private static Logger log = Logger.getLogger(RootMusicExcludeList.class);
	
	static private Vector<String> excludedFiles = null;
	
	static public boolean isFileExcluded(String filename) {
		if (excludedFiles == null)
			loadExcludedFiles();
		return excludedFiles.contains(filename);
	}
	
	static public void addExcludedFile(String filename) {
		if ((filename == null) || (filename.equals("")))
			return;
		if (excludedFiles == null)
			loadExcludedFiles();
		if (!excludedFiles.contains(filename))
			excludedFiles.add(filename);
	}
	
	static private void loadExcludedFiles() {
		excludedFiles = new Vector<String>();
        String text = new TextFileReader(OSHelper.getWorkingDirectory() + "/" + "excludedFiles.list").getText();
        if (text != null) {
            StringTokenizer tokenizer = new StringTokenizer(text, "\n");
            while (tokenizer.hasMoreTokens()) {
            	excludedFiles.add(tokenizer.nextToken());
            }
        }
	}
	
	static public void save() {
		try {
			if (excludedFiles != null) {
				TextFileWriter fileWriter = new TextFileWriter(OSHelper.getWorkingDirectory() + "/" + "excludedFiles.list");
				for (String file : excludedFiles)
					fileWriter.writeLine(file);
				fileWriter.close();
			}
		} catch (Exception e) {
			log.error("save(): error", e);
		}
	}
	
}

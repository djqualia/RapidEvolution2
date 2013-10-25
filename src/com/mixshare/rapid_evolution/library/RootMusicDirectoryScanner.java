package com.mixshare.rapid_evolution.library;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;

public class RootMusicDirectoryScanner extends Thread {

    private static Logger log = Logger.getLogger(RootMusicDirectoryScanner.class);
	
    static private RootMusicDirectoryScanner instance = null;
    
    public RootMusicDirectoryScanner() {
    	setPriority(Thread.NORM_PRIORITY - 1);
    }
    
	public void run() {
		try {	
			if (instance != null)
				return;
			instance = this;
			if (log.isDebugEnabled())
				log.debug("run(): scanning for new music...");
			String rootDir = OptionsUI.instance.rootMusicDirectory.getText();
			if (rootDir != null) {
				Vector filenames = new Vector();
				FileUtil.RecurseFileTree(rootDir, filenames);
				if (log.isDebugEnabled())
					log.debug("run(): total # files=" + filenames.size());
				for (int f = 0; f < filenames.size(); ++f) {
					String filename = (String)filenames.get(f);
					filename = StringUtil.ReplaceString("\\", filename, "/");
					boolean found = RootMusicExcludeList.isFileExcluded(filename);
					if (!found)
						found = FileUtil.getFilenameMinusDirectory(filename).startsWith("."); // will exclude files that start with "."
					SongLinkedList iter = SongDB.instance.SongLL;
					while (!found && (iter != null)) {
						String iter_filename = iter.getFileName();
						iter_filename = StringUtil.ReplaceString("\\", iter_filename, "/");
						if (iter_filename.equalsIgnoreCase(filename))
							found = true;
						iter = iter.next;
					}
					if (found) {
						filenames.remove(f);
						--f;
					}
				}
				if (log.isDebugEnabled())
					log.debug("run(): total # new files=" + filenames.size());
				File[] files = new File[filenames.size()];
				for (int f = 0; f < files.length; ++f) {
					files[f] = new File((String)filenames.get(f));
				}
				RapidEvolutionUI.instance.AcceptBrowseAddSongs(files, !AddSongsUI.instance.isVisible());
			}			
		} catch (Exception e) {
			log.error("run(): error", e);
		}
		instance = null;
	}
	
}

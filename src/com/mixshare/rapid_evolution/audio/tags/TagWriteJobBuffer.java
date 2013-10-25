package com.mixshare.rapid_evolution.audio.tags;

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.io.FileLockManager;

public class TagWriteJobBuffer extends Thread {

    private static Logger log = Logger.getLogger(TagWriteJobBuffer.class);
	
	static private Vector<SongLinkedList> todoSongs = new Vector<SongLinkedList>();
	
	static public void addSong(SongLinkedList song) {
		if (log.isDebugEnabled())
			log.debug("addSong(): song=" + song);
		if (!todoSongs.contains(song)) {
			todoSongs.add(song);
		}
	}
	
	static public void processQueue() {
		for (int i = 0; i < todoSongs.size(); ++i) {
			SongLinkedList song = todoSongs.get(i);
			if (FileLockManager.isWriteAvailable(song.getFileName())) {
				TagManager.writeTags(song);
				todoSongs.remove(i);
				--i;
			}
		}
	}
	
	static public boolean hasWorkRemaining() {
		return (todoSongs.size() > 0);
	}
	
	static public int getSize() {
		return todoSongs.size();
	}
	
	public void run() {
		try {
			while (!RapidEvolution.instance.terminatesignal) {
				Thread.sleep(15000);
				processQueue();
			}
		} catch (Exception e) {
			log.error("run(): error", e);
		}
	}
	
	
}

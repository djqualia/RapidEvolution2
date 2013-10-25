package com.mixshare.rapid_evolution.net.mining;

import org.apache.log4j.Logger;

import com.chango.data.lastfm.LastfmAlbumProfileData;

public class LastfmAlbumJob extends Thread implements MiningJobInterface {
	
    private static Logger log = Logger.getLogger(LastfmAlbumJob.class);
	
	private String artistName;
	private String albumName;
	private boolean done = false;
	private LastfmAlbumProfileData result = null;	
	
	public LastfmAlbumJob(String artistName, String albumName) {
		this.artistName = artistName;
		this.albumName = albumName;
		setPriority(Thread.NORM_PRIORITY - 2);
	}
	
	public void run() {
		try {
			result = new LastfmAlbumProfileData(artistName, albumName);
		} catch (Exception e) {
			log.error("run(): error", e);
		}
		done = true;		
	}
	
	public boolean isDone() {
		return done;
	}
	
	public Object getResult() { return result; }
	
	
}

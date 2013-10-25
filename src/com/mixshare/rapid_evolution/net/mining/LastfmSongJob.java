package com.mixshare.rapid_evolution.net.mining;

import org.apache.log4j.Logger;
import com.chango.data.lastfm.LastfmSongProfileData;

public class LastfmSongJob extends Thread implements MiningJobInterface {
	
    private static Logger log = Logger.getLogger(LastfmSongJob.class);
	
	private String artistName;
	private String songName;
	private boolean done = false;	
	private LastfmSongProfileData result = null;
	
	public LastfmSongJob(String artistName, String songName) {
		this.artistName = artistName;
		this.songName = songName;
		setPriority(Thread.NORM_PRIORITY - 2);
	}
	
	public void run() {
		try {
			result = new LastfmSongProfileData(artistName, songName);
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

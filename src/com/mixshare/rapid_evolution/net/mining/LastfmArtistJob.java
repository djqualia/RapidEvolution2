package com.mixshare.rapid_evolution.net.mining;

import org.apache.log4j.Logger;

import com.chango.data.lastfm.LastfmArtistProfileData;

public class LastfmArtistJob extends Thread implements MiningJobInterface {
	
    private static Logger log = Logger.getLogger(LastfmArtistJob.class);
	
	private String artistName;
	private boolean done = false;
	private LastfmArtistProfileData result = null;
	
	public LastfmArtistJob(String artistName) {
		this.artistName = artistName;	
		setPriority(Thread.NORM_PRIORITY - 2);
	}
	
	public void run() {
		try {
			result = new LastfmArtistProfileData(artistName);
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

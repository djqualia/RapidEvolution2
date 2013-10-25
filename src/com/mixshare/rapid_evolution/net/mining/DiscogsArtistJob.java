package com.mixshare.rapid_evolution.net.mining;

import org.apache.log4j.Logger;

import com.chango.data.discogs.DiscogsArtistProfileData;
import com.mixshare.rapid_evolution.data.retriever.discogs.DiscogsAPIWrapper;
import com.mixshare.rapid_evolution.data.retriever.discogs.DiscogsArtist;

public class DiscogsArtistJob extends Thread implements MiningJobInterface {
	
    private static Logger log = Logger.getLogger(DiscogsArtistJob.class);
	
	private String artistName;
	private DiscogsArtistProfileData result;
	private boolean done = false;
	
	public DiscogsArtistJob(String artistName) {
		this.artistName = artistName;
		setPriority(Thread.NORM_PRIORITY - 2);
	}
	
	public void run() {
		try {
			DiscogsArtist artist = DiscogsAPIWrapper.getArtist(artistName);
			if (artist != null) {
				result = new DiscogsArtistProfileData(artist);
			}
		} catch (Exception e) {
			log.error("run(): error", e);
		}
		done = true;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public DiscogsArtistProfileData getResult() { return result; }

}

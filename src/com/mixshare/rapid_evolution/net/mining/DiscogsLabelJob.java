package com.mixshare.rapid_evolution.net.mining;

import org.apache.log4j.Logger;

import com.chango.data.discogs.DiscogsLabelProfileData;
import com.mixshare.rapid_evolution.data.retriever.discogs.DiscogsAPIWrapper;
import com.mixshare.rapid_evolution.data.retriever.discogs.DiscogsLabel;

public class DiscogsLabelJob extends Thread implements MiningJobInterface {
	
    private static Logger log = Logger.getLogger(DiscogsLabelJob.class);
	
	private String labelName;
	private DiscogsLabelProfileData result = null;
	private boolean done = false;
	
	public DiscogsLabelJob(String labelName) {
		this.labelName = labelName;
		setPriority(Thread.NORM_PRIORITY - 2);
	}
	
	public void run() {
		try {
			DiscogsLabel label = DiscogsAPIWrapper.getLabel(labelName);
			if (label != null) {
				result = new DiscogsLabelProfileData(label);
			}			
		} catch (Exception e) {
			log.error("run(): error", e);
		}
		done = true;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public DiscogsLabelProfileData getResult() { return result; }

}

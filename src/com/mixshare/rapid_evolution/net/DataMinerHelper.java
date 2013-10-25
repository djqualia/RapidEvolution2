package com.mixshare.rapid_evolution.net;

import com.mixshare.rapid_evolution.net.mining.MiningJobInterface;

public class DataMinerHelper {

	static private MiningJobInterface currentJob = null;
	
	static public boolean canAcceptJob() {
		return (currentJob == null);
	}
	
	static public void setCurrentJob(MiningJobInterface job) {
		currentJob = job;
	}
	
	static public boolean hasResult() {
		if ((currentJob != null) && (currentJob.isDone()))
			return true;			
		return false;
	}
	
	static public MiningJobInterface getCurrentJob() { return currentJob; }
	static public void clearCurrentJob() { currentJob = null; }
		
}

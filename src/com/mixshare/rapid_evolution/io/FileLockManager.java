package com.mixshare.rapid_evolution.io;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.RWSemaphore;

public class FileLockManager {
	
    private static Logger log = Logger.getLogger(FileLockManager.class);
	
	static public void startFileRead(String filename) {
		if (log.isDebugEnabled())
			log.debug("startFileRead(): filename=" + filename);
		filename = unify(filename);
		RWSemaphore sem = updateRWSemaphore(filename, true);
		try {
			sem.startRead("startFileRead");
		} catch (Exception e) { }
	}
	static public void endFileRead(String filename) {
		if (log.isDebugEnabled())
			log.debug("endFileRead(): filename=" + filename);
		filename = unify(filename);		
		RWSemaphore sem = updateRWSemaphore(filename, true); 
		sem.endRead();
		updateRWSemaphore(filename, false);
	}
	
	static public void startFileWrite(String filename) {
		if (log.isDebugEnabled())
			log.debug("startFileWrite(): filename=" + filename);
		filename = unify(filename);		
		RWSemaphore sem = updateRWSemaphore(filename, true);
		try {
			sem.startWrite("startFileWrite");		
		} catch (Exception e) { }
	}
	static public void endFileWrite(String filename) {
		if (log.isDebugEnabled())
			log.debug("endFileWrite(): filename=" + filename);
		filename = unify(filename);		
		RWSemaphore sem = updateRWSemaphore(filename, true); 
		sem.endWrite();
		updateRWSemaphore(filename, false);		
	}
	
	static public boolean isWriteAvailable(String filename) {
		filename = unify(filename);
		RWSemaphore result = semMap.get(filename);
		if (result == null)
			return true;
		return false;		
	}
	
	static private Map<String, RWSemaphore> semMap = new HashMap<String, RWSemaphore>();
	static private synchronized RWSemaphore updateRWSemaphore(String filename, boolean fetch) {
		if (fetch) {
			RWSemaphore result = semMap.get(filename);
			if (result == null) {
				result = new RWSemaphore(-1);
				semMap.put(filename, result);
			}
			return result;
		} else {
			RWSemaphore result = semMap.get(filename);
			if (result != null) {
				if (!result.isUsed())
					semMap.remove(filename);
			}
			return null;
		}
	}
	
	static private String unify(String filename) {
		return filename.toLowerCase();
	}
}

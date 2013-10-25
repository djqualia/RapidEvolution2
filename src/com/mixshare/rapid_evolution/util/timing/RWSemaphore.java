package com.mixshare.rapid_evolution.util.timing;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

// allows at most 1 writer at a time and multiple readers...
public class RWSemaphore implements Serializable {

    static private Logger log = Logger.getLogger(RWSemaphore.class);
    static private final long serialVersionUID = 0L;    
	
    ////////////
    // FIELDS //
    ////////////
    
    private long timeOutMillis = -1;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true); // fair
    private Lock read  = readWriteLock.readLock();
    private Lock write = readWriteLock.writeLock();
    private long numReaders = 0;
    private long numWriters = 0;
    
    /////////////////
    // CONSTRUCTOR //
    /////////////////
        
    private RWSemaphore() { }
    public RWSemaphore(long timeOutMillis) {
    	this.timeOutMillis = timeOutMillis;
    }
    
    /////////////
    // METHODS //
    /////////////
    
    public void startRead(String description) throws InterruptedException {
    	if (timeOutMillis != -1) {
    		if (!read.tryLock(timeOutMillis, TimeUnit.MILLISECONDS))
    			log.warn("startRead(): timed out, description=" + description);
    	} else {
    		read.lock();
    	}
    	++numReaders;
    }   
    public void endRead() { try { read.unlock(); } catch (IllegalMonitorStateException ime) { } --numReaders; }
   
    public void startWrite(String description) throws InterruptedException {
    	if (timeOutMillis != -1) {
    		if (!write.tryLock(timeOutMillis, TimeUnit.MILLISECONDS))
    			log.warn("startWrite(): timed out, description=" + description);
    	} else {
    		write.lock();
    	}
    	++numWriters;
    }   
    public void endWrite() { try { write.unlock(); } catch (IllegalMonitorStateException ime) { } --numWriters; }
    
    public boolean isUsed() {
    	return numReaders > 0 || numWriters > 0;
    }
    
}

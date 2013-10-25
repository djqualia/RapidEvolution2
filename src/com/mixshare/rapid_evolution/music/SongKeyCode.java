package com.mixshare.rapid_evolution.music;

import org.apache.log4j.Logger;


public class SongKeyCode implements Comparable {

    static private Logger log = Logger.getLogger(SongKeyCode.class);
    
    protected SongKey songKey = null;
    private String cachedToString;

    public SongKeyCode(SongKey songKey) {
        if (log.isTraceEnabled()) log.trace("SongKeyCode(): songKey=" + songKey);
        this.songKey = songKey;
    }    
    
    public String toString() {
        if (cachedToString == null) {
	        StringBuffer returnValue = new StringBuffer();
	        if ((songKey.getStartKey() != null) && songKey.getStartKey().isValid()) {
	            returnValue.append(songKey.getStartKey().getKeyCode());
	        }
	        if ((songKey.getEndKey() != null) && songKey.getEndKey().isValid()) {
	            if (!returnValue.toString().equals(""))
	                returnValue.append("->");
	            returnValue.append(songKey.getEndKey().getKeyCode());
	        }
	        cachedToString = returnValue.toString();
        }
        return cachedToString;
    }
    
    public int compareTo(Object o) {
        if (o instanceof SongKeyCode) {
            SongKeyCode oKeyCode = (SongKeyCode)o;
            Key key1 = songKey.getFirstValidKey();
            Key key2 = oKeyCode.getSongKey().getFirstValidKey();
            if ((key1 != null) && (key2 != null)) {
                return key1.getKeyCode().compareTo(key2.getKeyCode());
            } else if (key1 != null) {
                return 1;
            } else if (key2 != null) {
                return -1;
            }
            
        }
        return 0;
    }
    
    public int hashCode() {
        return toString().toLowerCase().hashCode() + 1;
    }
    
    public boolean equals(Object o) {
        return toString().equalsIgnoreCase(o.toString());
    }
    
    public SongKey getSongKey() {
        return songKey;
    }
    
    public void setSongKey(SongKey songKey) {
        this.songKey = songKey;
        cachedToString = null;
    }
    
    public void invalidate() {
        cachedToString = null;
    }
    
}

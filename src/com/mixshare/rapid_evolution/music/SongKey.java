package com.mixshare.rapid_evolution.music;

import org.apache.log4j.Logger;

public class SongKey implements Comparable {

    static private Logger log = Logger.getLogger(SongKey.class);
    
    protected Key startKey;
    protected Key endKey;
    private SongKeyCode keyCode;
    private String cachedToString;
    
    public SongKey(Key startKey, Key endKey) {
        if (log.isTraceEnabled()) log.trace("SongKey(): startKey=" + startKey + ", endKey=" + endKey);
        this.startKey = startKey;
        this.endKey = endKey;
    }
    
    public String toString() {
        if (cachedToString == null) {
            if (log.isTraceEnabled()) log.trace("toString(): begin");
            StringBuffer returnValue = new StringBuffer();
            if ((startKey != null) && startKey.isValid()) {
                returnValue.append(startKey.toString());
            }
            if ((endKey != null) && endKey.isValid()) {
                if (!returnValue.toString().equals(""))
                    returnValue.append("->");
                returnValue.append(endKey.toString());
            }
            cachedToString = returnValue.toString();            
        }
        return cachedToString;
    }
    
    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
    
    public boolean equals(Object o) {
        return (compareTo(o) == 0);
    }
    
    public int hashCode() {
        return toString().toLowerCase().hashCode() + 1;
    }
    
    public boolean setStartKey(String startKey) {
        return setStartKey(Key.getKey(startKey));
    }    
    public boolean setStartKey(Key startKey) {
        if (!this.startKey.equals(startKey)) {
            this.startKey = startKey;
            invalidate();
            return true;
        }
        return false;
    }
    public Key getStartKey() {
        return startKey;
    }
    
    public boolean setEndKey(String endKey) {
        return setEndKey(Key.getKey(endKey));
    }
    public boolean setEndKey(Key endKey) {
        if (!this.endKey.equals(endKey)) {
            this.endKey = endKey;
            invalidate();
            return true;
        }
        return false;
    }
    public Key getEndKey() {
        return endKey;
    }
    
    public boolean isValid() {
        return (((startKey != null) && startKey.isValid()) || ((endKey != null) && endKey.isValid()));
    }
            
    public Key getFirstValidKey() {
        if ((startKey != null) && startKey.isValid())
            return startKey;
        if ((endKey != null) && endKey.isValid())
            return endKey;
        return null;        
    }
    
    public Key getLastValidKey() {
        if ((endKey != null) && endKey.isValid())
            return endKey;
        if ((startKey != null) && startKey.isValid())
            return startKey;
        return null;                
    }
    
    public SongKeyCode getKeyCode() {
        if (keyCode == null) {
            keyCode = new SongKeyCode(this);
        }
        return keyCode;
    }
    
    public void invalidate() {
        if (keyCode != null) keyCode.invalidate();
        cachedToString = null;
    }
}

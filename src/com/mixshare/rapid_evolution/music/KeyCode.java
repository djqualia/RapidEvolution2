package com.mixshare.rapid_evolution.music;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid_evolution.ui.OptionsUI;

public class KeyCode implements Comparable {

    static private Logger log = Logger.getLogger(KeyCode.class);
    
    static public KeyCode NO_KEYCODE = new KeyCode();
    
    private byte keyValue = Byte.MIN_VALUE;
    private char scaleType = Character.MIN_VALUE;
    private byte shift = Byte.MIN_VALUE;
    private String cachedToString;
    private String cachedToStringWithDetails;
    
    private KeyCode() { }    
    private KeyCode(byte keyValue, char scaleType, byte shift) {
        if (log.isTraceEnabled()) log.trace("KeyCode(): keyValue=" + keyValue + ", scaleType=" + scaleType + ", shift=" + shift);
        this.keyValue = keyValue;
        this.scaleType = scaleType;
        this.shift = shift;
    }

    public String toString() {
        return toString((OptionsUI.instance != null) && OptionsUI.instance.show_advanced_key_information.isSelected());
    }
    
    public String toString(boolean showDetails) {
        if (showDetails) {
            if (cachedToStringWithDetails == null) {
                cachedToStringWithDetails = calculateKeyCodeString(showDetails);
            }
            return cachedToStringWithDetails;
        } else {
            if (cachedToString == null) {
                cachedToString = calculateKeyCodeString(showDetails);
            }
            return cachedToString;
        }        
    }
        
    public String toFileFriendlyString() {
        String shortKeyCode = toString(false);
        if ((shortKeyCode == null) || shortKeyCode.equals(""))
            return "";
        if (shortKeyCode.length() <= 2)
            return "0" + shortKeyCode;
        return shortKeyCode;
    }
    
    public void invalidate() {
        cachedToString = null;
        cachedToStringWithDetails = null;
    }
    
    public int compareTo(Object o) {
        if (o instanceof KeyCode) {
            KeyCode oKeyCode = (KeyCode)o;
            if (keyValue < oKeyCode.keyValue) return -1;
            if (keyValue > oKeyCode.keyValue) return 1;
            if (isMinor() && !oKeyCode.isMinor()) return -1;
            if (!isMinor() && oKeyCode.isMinor()) return 1;
            if (scaleType < oKeyCode.scaleType) return -1;
            if (scaleType > oKeyCode.scaleType) return 1;
            if (shift < oKeyCode.shift) return -1;
            if (shift > oKeyCode.shift) return 1;
        }
        return 0;
    }        
        
    public boolean isValid() {
        return ((keyValue != Byte.MIN_VALUE) && (scaleType != Character.MIN_VALUE) && (shift != Byte.MIN_VALUE));        
    }
    
    public boolean isMinor() {
        return ((scaleType == 'A') || (scaleType == 'D') || (scaleType == 'P') || (scaleType == 'C'));
    }
    
    private String calculateKeyCodeString(boolean showDetails) {
        if (!isValid()) return "";
        else {
	        StringBuffer result = new StringBuffer();
	        result.append(keyValue);
	        if (showDetails) {
                char displayScaleType = scaleType;
                if (!OptionsUI.instance.show_advanced_key_information.isSelected() &&
                        displayScaleType == 'I')
                    displayScaleType = 'B';
	            result.append(displayScaleType);
		        if (shift > 0) {
		            result.append(" +");
		            result.append(shift);
		        } else if (shift < 0) {
		            result.append(" ");
		            result.append(shift);            
		        }                
	        } else {
	            if ((scaleType == 'I') || (scaleType == 'L') || (scaleType == 'M'))
	                result.append('B');
	            else
	                result.append('A');
	        }
	        return result.toString();
        }        
    }
    
    // ************
    // ** STATIC **
    // ************
        
    static public KeyCode getKeyCode(byte keyValue, char scaleType, byte shift) {
        String primaryKey = calculatePrimaryKey(keyValue, scaleType, shift);
        KeyCode result = (KeyCode)keyCodeFlyWeights.get(primaryKey);
        if (result == null) {
            result = new KeyCode(keyValue, scaleType, shift);
            keyCodeFlyWeights.put(primaryKey, result);
        }
        return result;
    }
    
    static public void invalidateCache() {
        Iterator iter = keyCodeFlyWeights.values().iterator();
        while (iter.hasNext()) {
            KeyCode keyCode = (KeyCode)iter.next();
            keyCode.invalidate();
        }
    }
    
    static private Map keyCodeFlyWeights = new HashMap();
    
    static private String calculatePrimaryKey(byte keyValue, char scaleType, byte shift) {
        StringBuffer result = new StringBuffer();
        result.append(keyValue);
        result.append(",");
        result.append(scaleType);
        result.append(",");
        result.append(shift);
        return result.toString();
    }
}

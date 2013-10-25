package com.mixshare.rapid_evolution.music;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.KeyFormatUI;
import rapid_evolution.ui.OptionsUI;

/**
 * This class represents a musical key.
 * 
 * Since there are a finite number of keys, a fly-weight pattern is used to conserve memory.  Therefore, this
 * class must remain immutable.  Methods which change the value of the key will return a new copy of a Key.
 * Furthermore, factory methods must be used to retrieve new values (i.e. getKey(...)).
 */
public class Key implements Serializable, Comparable {

    static private Logger log = Logger.getLogger(Key.class);

    // ***************
    // ** CONSTANTS **
    // ***************
    
    // 100 cents = 1 semitone = 1 half step    
    static public byte semiTonesPerOctave = 12;
    // the percent BPM shift equivalent to a change in 1 semitone pitch (a half-step, or 100 cents)
    static public double bpmDifferencePerSemitone = Math.pow(Math.E, Math.log(2) / semiTonesPerOctave) * 100.0 - 100.0;
    
    // supported scale types/modes
    static public byte SCALE_UNKNOWN = -1;
    static public byte SCALE_IONIAN = 0; // standard major
    static public byte SCALE_AEOLIAN = 1; // natural minor
    static public byte SCALE_LYDIAN = 2; // major with raised 4th
    static public byte SCALE_MIXOLYDIAN = 3; // major with lowered 7th
    static public byte SCALE_DORIAN = 4; // minor with raised 6th
    static public byte SCALE_PHRYGIAN = 5; // minor with lowered 2nd
    static public byte SCALE_LOCRIAN = 6;
    
    static public double ROOT_UNKNOWN = Double.NEGATIVE_INFINITY;
    
    // **************    
    // ** INSTANCE **
    // **************
    
    private double root_value = ROOT_UNKNOWN; // ~0-12 range, corresponds with each note on the keyboard
    private byte scale_type = -1;    
    private ShiftedKeyNotation shifted_notation;

    // cached variables:
    private transient String cachedToString;    
    private transient KeyCode cachedKeyCode;
    private transient KeyRelation cachedKeyRelation;
    private transient Key cachedKeyRelationKey;
        
    // no public constructors (use static getKey() factory methods)
    private Key() { }
    private Key(double root_value, byte scale_type) {
        this.root_value = root_value;
        this.scale_type = scale_type;
        validateRootValue();
        shifted_notation = new ShiftedKeyNotation(root_value);
        if (log.isTraceEnabled()) log.trace("Key(): root_value=" + root_value + ", root note=" + getRootNoteDescription() + ", shift=" + getShiftDescription() + ", scale_type=" + scale_type + ", scale description=" + getScaleTypeDescription());
    }

    public void invalidate() {
        cachedToString = null;
        cachedKeyCode = null;
    }
    
    public String toStringExact() {
        return getPreferredKeyNotation(true);
    }
    public String toString() {
        if (cachedToString == null) {
            cachedToString = getPreferredKeyNotation();
        }
        return cachedToString;
    }
    
    public boolean equals(Object object) {
        if (object instanceof Key) {
            Key compare_key = (Key)object;
            return this.getKeyNotationSharp(true).equals(compare_key.getKeyNotationSharp(true));
/*
            return ((compare_key.getShiftedKeyNotation().getRootNote() == getShiftedKeyNotation().getRootNote()) &&
                    (compare_key.scale_type == scale_type) &&
                    (compare_key.getShiftedKeyNotation().getShiftInCents() == getShiftedKeyNotation().getShiftInCents()));
                    */
        }  
        return false;
    }
    public int hashCode() {
        return this.getKeyNotationSharp(true).hashCode();
        //return (int)((scale_type + 1) * 1200 + (root_value * 100.0));
    }
        
    public int compareTo(Object compare) {
        if (compare instanceof Key) {
            Key compareKey = (Key)compare;
            if (root_value < compareKey.root_value)
                return -1;
            else if (root_value > compareKey.root_value)
                return 1;
            else {
                if (scale_type < compareKey.scale_type)
                    return -1;
                else if (scale_type > compareKey.scale_type)
                    return 1;
                return 0;
            }            
        }
        return 0;
    }
    
    public boolean isValid() {
        return (root_value != ROOT_UNKNOWN);
    }
    
    static public String[] wellFormedKeys = {
            "A", "Am", "A#", "Bb", "A#m", "Bbm", "B", "Bm", "C", "Cm", "Db", "C#", "Dbm", "C#m", "D", "Dm", "Eb", "D#", "Ebm", "D#m", "E", "Em",
            "F", "Fm", "Gb", "F#", "Gbm", "F#m", "G", "Gm", "Ab", "G#", "Abm", "G#m"
    };
    
    static public String[] advancedModes = {
            "ionian", "aeolian", "lydian", "mixolydian", "dorian", "phrygian", "locrian"
    };
    
    static public String[] keycodeSuffixes = {
            "A", "B", "I", "L", "M", "D", "P", "C"
    };
    
    static public boolean isWellFormed(String input) {
        if ((input == null) || input.equals("")) return false;
        input = input.toLowerCase();
        // remove shift
        int shift_index = input.indexOf("+");
        if (shift_index >= 0)
            input = input.substring(0, shift_index).trim();
        shift_index = input.indexOf("-");
        if (shift_index >= 0)
            input = input.substring(0, shift_index).trim();
        // remove advanced modes
        for (int i = 0; i < advancedModes.length; ++i) {
            int mode_index = input.indexOf(advancedModes[i]);
            if (mode_index >= 0)
                input = input.substring(0, mode_index).trim();
        }
        // check for normal keys
        for (int i = 0; i < wellFormedKeys.length; ++i) {
            if (input.equalsIgnoreCase(wellFormedKeys[i]))
                return true;
        }
        // check for valid keycode
        if (input.length() > 3) return false;
        if (input.length() < 2) return false;
        for (int i = 0; i < keycodeSuffixes.length; ++i) {
            if (input.endsWith(keycodeSuffixes[i].toLowerCase()))
                input = input.substring(0, input.length() - 1);
        }
        try {
            int value = Integer.parseInt(input);
            if ((value >= 1) && (value <= 12))
                return true;
        } catch (Exception e) { }
        return false;
    }
    
    public double getRootValue() {        
        return root_value;
    }    
    
    public String getRootNoteDescription() {
        int root_note = shifted_notation.getRootNote();
        if (root_note == 0) return "A";
        if (root_note == 1) return KeyFormatUI.bflatoption.isSelected() ? "Bb" : "A#";
        if (root_note == 2) return "B";
        if (root_note == 3) return "C";
        if (root_note == 4) return KeyFormatUI.dflatoption.isSelected() ? "Db" : "C#";
        if (root_note == 5) return "D";
        if (root_note == 6) return KeyFormatUI.eflatoption.isSelected() ? "Eb" : "D#";
        if (root_note == 7) return "E";
        if (root_note == 8) return "F";
        if (root_note == 9) return KeyFormatUI.gflatoption.isSelected() ? "Gb" : "F#";
        if (root_note == 10) return "G";
        if (root_note == 11) return KeyFormatUI.aflatoption.isSelected() ? "Ab" : "G#";
        return "UNKNOWN";
    }
    
    public ShiftedKeyNotation getShiftedKeyNotation() {
        return shifted_notation;
    }
    
    public String getShiftDescription() {
        double shift = shifted_notation.getShift();
        int shift_in_cents = (int)(shift * 100.0);
        if (shift_in_cents == 0) return "NONE";
        if (shift_in_cents > 0) return "+" + shift_in_cents;
        else return String.valueOf(shift_in_cents);
    }
    
    public byte getScaleType() {
        return scale_type;
    }

    public String getScaleTypeDescription() {
        if (scale_type == 0) return "ionian";
        else if (scale_type == 1) return "aeolian";
        else if (scale_type == 2) return "lydian";
        else if (scale_type == 3) return "mixolydian";
        else if (scale_type == 4) return "dorian";
        else if (scale_type == 5) return "phrygian";
        else if (scale_type == 6) return "locrian";
        else return "UNKNOWN";
    }
    
    public boolean isMinor() {
        return ((scale_type == SCALE_AEOLIAN) ||
                (scale_type == SCALE_DORIAN) ||
                (scale_type == SCALE_PHRYGIAN) ||
                (scale_type == SCALE_LOCRIAN));
    }            
                
    public String getGroupSSLString() {
        if (root_value == ROOT_UNKNOWN) return "";
        StringBuffer result = new StringBuffer();
        result.append(getKeyCode());
        result.append(" ");
        result.append(getKeyNotationSharp());
        return result.toString();
    }
    

    public KeyCode getKeyCode() {
        if (cachedKeyCode == null) {
	        if (root_value == ROOT_UNKNOWN) {
	            cachedKeyCode = KeyCode.NO_KEYCODE;
	        } else {
		        int root_note = shifted_notation.getRootNote();
		        byte keyCodeValue = -1;
		        boolean isMinor = isMinor();
		        char scaleType = isMinor ? 'A' : 'B';
		        if (isMinor) {
		            if (root_note == 0)keyCodeValue = 8;
		            else if (root_note == 1) keyCodeValue = 3;
		            else if (root_note == 2) keyCodeValue = 10;
		            else if (root_note == 3) keyCodeValue = 5;
		            else if (root_note == 4) keyCodeValue = 12;
		            else if (root_note == 5) keyCodeValue = 7;
		            else if (root_note == 6) keyCodeValue = 2;
		            else if (root_note == 7) keyCodeValue = 9;
		            else if (root_note == 8) keyCodeValue = 4;
		            else if (root_note == 9) keyCodeValue = 11;
		            else if (root_note == 10) keyCodeValue = 6;
		            else if (root_note == 11) keyCodeValue = 1;
		        } else {
		            if (root_note == 0) keyCodeValue = 11;
		            else if (root_note == 1) keyCodeValue = 6;
		            else if (root_note == 2) keyCodeValue = 1;
		            else if (root_note == 3) keyCodeValue = 8;
		            else if (root_note == 4) keyCodeValue = 3;
		            else if (root_note == 5) keyCodeValue = 10;
		            else if (root_note == 6) keyCodeValue = 5;
		            else if (root_note == 7) keyCodeValue = 12;
		            else if (root_note == 8) keyCodeValue = 7;
		            else if (root_note == 9) keyCodeValue = 2;
		            else if (root_note == 10) keyCodeValue = 9;
		            else if (root_note == 11) keyCodeValue = 4;
		        }        
		        if (scale_type == SCALE_DORIAN) {
		            scaleType = 'D';
		            ++keyCodeValue;
		        } else if (scale_type == SCALE_PHRYGIAN) {
		            scaleType = 'P';
		            --keyCodeValue;
		        } else if (scale_type == SCALE_LYDIAN) {
		            scaleType = 'L';
		            ++keyCodeValue;
		        } else if (scale_type == SCALE_MIXOLYDIAN) {
		            scaleType = 'M';
		            --keyCodeValue;
		        } else if (scale_type == SCALE_LOCRIAN) {
		            scaleType = 'C';
		            keyCodeValue -= 2;
		        } else if (scale_type == SCALE_IONIAN) {
		            scaleType = 'I';            
		        }
		        if (keyCodeValue < 1) keyCodeValue += 12;
		        if (keyCodeValue > 12) keyCodeValue -= 12;
		        byte shift = (byte)(shifted_notation.getShift() * 100.0);
		        cachedKeyCode = KeyCode.getKeyCode(keyCodeValue, scaleType, shift);
	        }
        }
        return cachedKeyCode;
    }    
    
    public String getKeyNotationFlat() {
        return getKeyNotationFlat((OptionsUI.instance != null) && OptionsUI.instance.show_advanced_key_information.isSelected());
    }    
    public String getKeyNotationFlat(boolean showDetails) {
        if (root_value == ROOT_UNKNOWN) return "";
        int root_note = shifted_notation.getRootNote();
        StringBuffer result = new StringBuffer();        
        if (isMinor()) {
            if (root_note == 0) result.append("Am");
            if (root_note == 1) result.append("Bbm");
            if (root_note == 2) result.append("Bm");
            if (root_note == 3) result.append("Cm");
            if (root_note == 4) result.append("Dbm");
            if (root_note == 5) result.append("Dm");
            if (root_note == 6) result.append("Ebm");
            if (root_note == 7) result.append("Em");
            if (root_note == 8) result.append("Fm");
            if (root_note == 9) result.append("Gbm");
            if (root_note == 10) result.append("Gm");
            if (root_note == 11) result.append("Abm");            
        } else {
            if (root_note == 0) result.append("A");
            if (root_note == 1) result.append("Bb");
            if (root_note == 2) result.append("B");
            if (root_note == 3) result.append("C");
            if (root_note == 4) result.append("Db");
            if (root_note == 5) result.append("D");
            if (root_note == 6) result.append("Eb");
            if (root_note == 7) result.append("E");
            if (root_note == 8) result.append("F");
            if (root_note == 9) result.append("Gb");
            if (root_note == 10) result.append("G");
            if (root_note == 11) result.append("Ab");            
        }
        if (showDetails) {
            appendMode(result);
            appendShift(result);
        }
        return result.toString();
    }
    
    public String getKeyNotationSharp() {
        return getKeyNotationSharp((OptionsUI.instance != null) && OptionsUI.instance.show_advanced_key_information.isSelected());
    }    
    public String getKeyNotationSharp(boolean showDetails) {
        if (root_value == ROOT_UNKNOWN) return "";
        int root_note = shifted_notation.getRootNote();
        StringBuffer result = new StringBuffer();        
        if (isMinor()) {
            if (root_note == 0) result.append("Am");
            if (root_note == 1) result.append("A#m");
            if (root_note == 2) result.append("Bm");
            if (root_note == 3) result.append("Cm");
            if (root_note == 4) result.append("C#m");
            if (root_note == 5) result.append("Dm");
            if (root_note == 6) result.append("D#m");
            if (root_note == 7) result.append("Em");
            if (root_note == 8) result.append("Fm");
            if (root_note == 9) result.append("F#m");
            if (root_note == 10) result.append("Gm");
            if (root_note == 11) result.append("G#m");            
        } else {
            if (root_note == 0) result.append("A");
            if (root_note == 1) result.append("A#");
            if (root_note == 2) result.append("B");
            if (root_note == 3) result.append("C");
            if (root_note == 4) result.append("C#");
            if (root_note == 5) result.append("D");
            if (root_note == 6) result.append("D#");
            if (root_note == 7) result.append("E");
            if (root_note == 8) result.append("F");
            if (root_note == 9) result.append("F#");
            if (root_note == 10) result.append("G");
            if (root_note == 11) result.append("G#");            
        }
        if (showDetails) {
            appendMode(result);
            appendShift(result);
        }
        return result.toString();        
    }

    public String getCustomKeyNotation() {
        return getCustomKeyNotation((OptionsUI.instance != null) && OptionsUI.instance.show_advanced_key_information.isSelected());
    }
    public String getCustomKeyNotation(boolean showDetails) {
        if (root_value == ROOT_UNKNOWN) return "";
        StringBuffer result = new StringBuffer();        
        result.append(getRootNoteDescription());
        if (isMinor()) result.append("m");
        if (showDetails) {
            appendMode(result);
            appendShift(result);
        }
        return result.toString(); 
    }

    /**
     * The ID3 safe notation excludes the shift and does not use keycodes or modes.
     */
    public String getID3SafeKeyNotation() {
        return getKeyNotationSharp(false);
    }
    
    public String getShortKeyNotation() {
        return getPreferredKeyNotation(false);
    }
        
    public String getPreferredKeyNotation() {
        return getPreferredKeyNotation((OptionsUI.instance != null) && OptionsUI.instance.show_advanced_key_information.isSelected());
    }
    public String getPreferredKeyNotation(boolean showDetails) {
        if (log.isTraceEnabled()) log.trace("getPreferredKeyNotation(): showDetails=" + showDetails);
        if (OptionsUI.instance.keyformatcombo.getSelectedIndex() == 0) return getKeyNotationSharp(showDetails);
        if (OptionsUI.instance.keyformatcombo.getSelectedIndex() == 1) return getKeyNotationFlat(showDetails);
        if (OptionsUI.instance.keyformatcombo.getSelectedIndex() == 2) return getKeyCode().toString(showDetails);
        if (OptionsUI.instance.keyformatcombo.getSelectedIndex() == 3) return getCustomKeyNotation(showDetails);        
        return getKeyNotationSharp(showDetails);
    }   
     
    public Key getShiftedKeyByBpmDifference(double bpm_difference) {
        return Key.getKey(root_value + bpm_difference / bpmDifferencePerSemitone, scale_type);
    }
    
    public Key getShiftedKeyBySemitones(double semitones) {
        return Key.getKey(root_value + semitones, scale_type);
    }
    
    public KeyRelation getKeyRelationTo(Key targetKey) {
        if ((targetKey == null) || !targetKey.isValid()) return KeyRelation.INVALID_RELATION;
        if ((cachedKeyRelation == null) || !cachedKeyRelationKey.equals(targetKey)) {
            cachedKeyRelationKey = targetKey;
	        if (log.isTraceEnabled()) log.trace("getKeyRelationTo(): this=" + getKeyNotationSharp(true) + ", targetKey=" + targetKey.getKeyNotationSharp(true));
	        boolean isRelative = false;
	        double compare_root_value = root_value;
	        double relative_shift = 0.0;
	        if (scale_type != targetKey.scale_type) {
	            relative_shift = getRelativeShift(scale_type, targetKey.scale_type);
	            compare_root_value += relative_shift;
	            if (compare_root_value >= 12) compare_root_value -= 12;
	            isRelative = true;
	        }
	        if (relative_shift < 0.0) relative_shift += 12;
	        // check for tonic 1st
	        double actual_root_value = compare_root_value;
	        double difference = targetKey.getRootValue() - actual_root_value;
	        while (difference >= 6.0) difference -= 12.0;
	        while (difference < -6.0) difference += 12.0;
	        if ((difference <= 0.5) && (difference >= -0.5)) {
	            byte relationType = KeyRelation.RELATION_TONIC;
	            if (isRelative) {
	                int root_difference = shifted_notation.getRootNote() - targetKey.getShiftedKeyNotation().getRootNote();
	                if (root_difference < 0) root_difference += 12;
	                if (root_difference >= 12) root_difference -= 12;
	                if (root_difference == 0)
	                    relationType = KeyRelation.RELATION_TONIC_MODAL;
	                else if (root_difference == 7)
	                    relationType = KeyRelation.RELATION_DOMINANT_MODAL;
	                else if (root_difference == 5)
	                    relationType = KeyRelation.RELATION_SUBDOMINANT_MODAL;
	                else
	                    relationType = KeyRelation.RELATION_RELATIVE_TONIC;
	            }
	            cachedKeyRelation = new KeyRelation(difference, relationType);
	        } else {
		        double minimum_difference = difference;
		        // check for dominant 2nd
		        actual_root_value = compare_root_value + 5.0;
		        if (actual_root_value >= 12) actual_root_value -= 12;
		        difference = targetKey.getRootValue() - actual_root_value;
		        while (difference >= 6.0) difference -= 12.0;
		        while (difference < -6.0) difference += 12.0;
		        if ((difference <= 0.5) && (difference >= -0.5)) {
		            byte relationType = KeyRelation.RELATION_DOMINANT;
		            if (isRelative) {
		                int root_difference = shifted_notation.getRootNote() - targetKey.getShiftedKeyNotation().getRootNote();
		                if (root_difference < 0) root_difference += 12;
		                if (root_difference >= 12) root_difference -= 12;
		                if (root_difference == 0) {
		                    relationType = KeyRelation.RELATION_TONIC_MODAL;
		                    /*
		                } else if (root_difference == 2) {
		                    if ((getScaleType() == Key.SCALE_PHRYGIAN) &&
		                            ((targetKey.getScaleType() == Key.SCALE_AEOLIAN) || (targetKey.getScaleType() == Key.SCALE_PHRYGIAN)))
		                        relationType = KeyRelation.RELATION_NONE;
		                    else if ((getScaleType() == Key.SCALE_MIXOLYDIAN) &&
		                            ((targetKey.getScaleType() == Key.SCALE_IONIAN) || (targetKey.getScaleType() == Key.SCALE_MIXOLYDIAN)))
		                        relationType = KeyRelation.RELATION_NONE;
		                    else
		                        relationType = KeyRelation.RELATION_RELATIVE_DOMINANT;
		                        */
		                } else if (root_difference == 7) {
		                    relationType = KeyRelation.RELATION_DOMINANT_MODAL;
		                } else if (root_difference == 5) {
		                    relationType = KeyRelation.RELATION_SUBDOMINANT_MODAL;
		                } else {
		                    relationType = KeyRelation.RELATION_RELATIVE_DOMINANT;                
		                }
		            }
		            cachedKeyRelation = new KeyRelation(difference, relationType);
		        } else {
			        if (Math.abs(difference) < Math.abs(minimum_difference))
			            minimum_difference = difference;
			        // check for subdominant 3rd
			        actual_root_value = compare_root_value + 7.0;
			        if (actual_root_value >= 12) actual_root_value -= 12;
			        difference = targetKey.getRootValue() - actual_root_value;
			        while (difference >= 6.0) difference -= 12.0;
			        while (difference < -6.0) difference += 12.0;
			        if ((difference <= 0.5) && (difference >= -0.5)) {
			            byte relationType = KeyRelation.RELATION_SUBDOMINANT;
			            if (isRelative) {
			                int root_difference = shifted_notation.getRootNote() - targetKey.getShiftedKeyNotation().getRootNote();
			                if (root_difference < 0) root_difference += 12;
			                if (root_difference >= 12) root_difference -= 12;
			                if (root_difference == 0) {
			                    relationType = KeyRelation.RELATION_TONIC_MODAL;
			            	} else if (root_difference == 7) {
			                    relationType = KeyRelation.RELATION_DOMINANT_MODAL;
			                } else if (root_difference == 5) {
			                    relationType = KeyRelation.RELATION_SUBDOMINANT_MODAL;
			                    /*
			                } else if (root_difference == 10) {
			                    if ((targetKey.getScaleType() == Key.SCALE_PHRYGIAN) &&
			                            ((getScaleType() == Key.SCALE_AEOLIAN) || (getScaleType() == Key.SCALE_PHRYGIAN)))
			                        relationType = KeyRelation.RELATION_NONE;
			                    else if ((targetKey.getScaleType() == Key.SCALE_MIXOLYDIAN) &&
			                            ((getScaleType() == Key.SCALE_IONIAN) || (getScaleType() == Key.SCALE_MIXOLYDIAN)))
			                        relationType = KeyRelation.RELATION_NONE;
			                    else
			                        relationType = KeyRelation.RELATION_RELATIVE_SUBDOMINANT;
			                        */
			                } else {
			                    relationType = KeyRelation.RELATION_RELATIVE_SUBDOMINANT;
			                }
			            }
			            cachedKeyRelation = new KeyRelation(difference, relationType);
			        } else {
				        if (Math.abs(difference) < Math.abs(minimum_difference))
				            minimum_difference = difference;
				        // no key relation...
				        cachedKeyRelation = new KeyRelation(minimum_difference, KeyRelation.RELATION_NONE);
			        }
		        }
	        }
        }
        return cachedKeyRelation;
    }
    
    // PRIVATE:

    private void validateRootValue() {    
        root_value = validateRootValue(root_value);
    }

    private void appendMode(StringBuffer result) {
        result.append(" ");
        result.append(getScaleTypeDescription());
    }
    
    private int appendShift(StringBuffer result) {
        int shift = (int)(shifted_notation.getShift() * 100.0);
        if (shift > 0) {
            result.append(" +");
            result.append(shift);
        } else if (shift < 0) {
            result.append(" ");
            result.append(shift);
        }
        return shift;
    }
        
    // ************
    // ** STATIC **
    // ************    
    
    static public Key NO_KEY = new Key(ROOT_UNKNOWN, SCALE_UNKNOWN);
    
    // FACTORY METHODS:
    
    static public Key getKey(String description) {
        try {
            if (log.isTraceEnabled())
                log.trace("getKey(): description=" + description);
	        if ((description == null) || description.trim().equals("")) return NO_KEY;
	        
	        double root_value = Key.ROOT_UNKNOWN;
	        byte scale_type = Key.SCALE_UNKNOWN;
	        
	        // parse out the shift from the end:
	        boolean negative_shift = false;
	        double shift = 0.0;
	        int shift_index = description.indexOf("+");
	        if (shift_index == -1) {
	            shift_index = description.indexOf("-");
	            negative_shift = true;
	        }
	        if (shift_index >= 0) {
	            int start_index = shift_index + 1;
	            while ((start_index < description.length()) && !Character.isDigit(description.charAt(start_index))) {
	                ++start_index;
	            }
	            int end_index = start_index + 1;            
	            while ((end_index < description.length()) && Character.isDigit(description.charAt(end_index))) {
	                ++end_index;
	            }
	            try {
	                // the shift should be in cents, 100 cents = 1 semitone
	                shift = Double.parseDouble(description.substring(start_index, end_index)) / 100.0;
	                if (negative_shift) shift = -shift;
	            } catch (Exception e) { }
	            description = description.substring(0, shift_index);
	        }
	        
	        description = StringUtil.trim(description.toLowerCase());
	        
	        // look for keycodes:
	        int numericIndex = 0;
	        while ((numericIndex < description.length()) && Character.isDigit(description.charAt(numericIndex))) {
	            ++numericIndex;
	        }
	        if ((numericIndex > 0) && (description.length() > numericIndex)) {
	            int keyCodeNumber = Integer.parseInt(description.substring(0, numericIndex));
		        if (keyCodeNumber == 1) root_value = 11.0; // G#            
		        else if (keyCodeNumber == 2) root_value = 6.0; // D#            
		        else if (keyCodeNumber == 3) root_value = 1.0; // A#           
		        else if (keyCodeNumber == 4) root_value = 8.0; // F            
		        else if (keyCodeNumber == 5) root_value = 3.0; // C           
		        else if (keyCodeNumber == 6) root_value = 10.0; // G          
		        else if (keyCodeNumber == 7) root_value = 5.0; // D          
		        else if (keyCodeNumber == 8) root_value = 0.0; // A
		        else if (keyCodeNumber == 9) root_value = 7.0; // E           
		        else if (keyCodeNumber == 10) root_value = 2.0; // B            
		        else if (keyCodeNumber == 11) root_value = 9.0; // F#            
		        else if (keyCodeNumber == 12) root_value = 4.0; // C#
		        if (root_value != ROOT_UNKNOWN) {
			        char keyCodeScaleType = description.charAt(numericIndex);
			        if (keyCodeScaleType == 'a') {
			            scale_type = SCALE_AEOLIAN;
			        } else if ((keyCodeScaleType == 'b') || (keyCodeScaleType == 'i')) {
			            scale_type = SCALE_IONIAN;
			            root_value += 3.0;
			        } else if (keyCodeScaleType == 'd') {
			            scale_type = SCALE_DORIAN;
			            root_value += 5.0;		            
			        } else if (keyCodeScaleType == 'l') {
			            scale_type = SCALE_LYDIAN;
			            root_value -= 4.0;
			        } else if (keyCodeScaleType == 'm') {
			            scale_type = SCALE_MIXOLYDIAN;
			            root_value -= 2.0;		            
			        } else if (keyCodeScaleType == 'p') {
			            scale_type = SCALE_PHRYGIAN;
			            root_value -= 5.0;
			        } else if (keyCodeScaleType == 'c') {
			            scale_type = SCALE_LOCRIAN;
			            root_value += 2.0;
			        }
		        }
	        }
	        
	        if (root_value == ROOT_UNKNOWN) {
	            // for for standard key notation:
		        if (description.startsWith("a")) root_value = 0.0;
		        if (description.startsWith("b")) root_value = 2.0;
		        if (description.startsWith("c")) root_value = 3.0;
		        if (description.startsWith("d")) root_value = 5.0;
		        if (description.startsWith("e")) root_value = 7.0;
		        if (description.startsWith("f")) root_value = 8.0;
		        if (description.startsWith("g")) root_value = 10.0;
		        if (root_value != ROOT_UNKNOWN) {
		            for (int i = 1; i < description.length(); ++i) {
		                if (description.charAt(i) == '#') root_value += 1.0;
		                if (description.charAt(i) == 'b') root_value -= 1.0;
		            }
		            if (root_value < 0) root_value += 12;
			        // determine the scale type:
			        if (StringUtil.substring("m", description) && 
			                !StringUtil.substring("major", description) &&
			                !StringUtil.substring("mix", description))
			            scale_type = SCALE_AEOLIAN;
			        else
			            scale_type = SCALE_IONIAN;
		        }
	        }
	        
	        if ((root_value != ROOT_UNKNOWN) && (shift != 0.0)) root_value += shift;
	        root_value = validateRootValue(root_value);
	        
	        // look for non-standard modes:
	        if (StringUtil.substring("dor", description)) scale_type = SCALE_DORIAN;
	        else if (StringUtil.substring("phr", description)) scale_type = SCALE_PHRYGIAN;
	        else if (StringUtil.substring("mix", description)) scale_type = SCALE_MIXOLYDIAN;
	        else if (StringUtil.substring("lyd", description)) scale_type = SCALE_LYDIAN;
	        else if (StringUtil.substring("loc", description)) scale_type = SCALE_LOCRIAN;

	        Key result = checkFactory(root_value, scale_type);	        
            if (log.isTraceEnabled())
                log.trace("getKey(): result=" + result);            
	        return result;
	        
        } catch (Exception e) {
            log.error("Key(): error Exception", e);
        }   
        return NO_KEY;
    }
    
    static public Key getKey(double root_value, byte scale_type) {
        Key result = checkFactory(root_value, scale_type);	        
        return result;
    }

    static public void invalidateCache() {
        Iterator iter = keyFlyWeights.values().iterator();
        while (iter.hasNext()) {
            Key key = (Key)iter.next();
            key.invalidate();
        }
    }
       
    static public KeyRelation getClosestKeyRelation(double sourceBpm, Key sourceKey, double targetBpm, Key targetKey) {
        double bpmDifference = Bpm.getBpmDifference(sourceBpm, targetBpm);
        return getClosestKeyRelation(sourceBpm, sourceKey, targetBpm, targetKey, bpmDifference);
    }
    
    static public KeyRelation getClosestKeyRelation(double sourceBpm, Key sourceKey, double targetBpm, Key targetKey, double bpmDifference) {
        if ((sourceKey == null) || (targetKey == null) || !sourceKey.isValid() || !targetKey.isValid()) return KeyRelation.INVALID_RELATION;
        Key shiftedSourceKey = sourceKey.getShiftedKeyByBpmDifference(bpmDifference);        
        KeyRelation sourceRelationship = shiftedSourceKey.getKeyRelationTo(targetKey);
        return sourceRelationship;
    }

    static public SongKeyRelation getClosestKeyRelation(SongLinkedList sourceSong, double targetBpm, Key targetKey, double bpmDifference) {
        double sourceBpm = sourceSong.getStartbpm();
        if (sourceBpm == 0.0) sourceBpm = sourceSong.getEndbpm();
        Key sourceKey = sourceSong.getStartKey();
        if (!sourceKey.isValid())
            sourceKey = sourceSong.getEndKey();
        SongKeyRelation result = new SongKeyRelation();
        KeyRelation relationWithoutKeylock = (Double.NEGATIVE_INFINITY == bpmDifference) ? getClosestKeyRelation(sourceBpm, sourceKey, targetBpm, targetKey) : getClosestKeyRelation(sourceBpm, sourceKey, targetBpm, targetKey, bpmDifference);
        result.setRelationWithoutKeylock(relationWithoutKeylock);
        if (!OptionsUI.instance.disablekeylockfunctionality.isSelected()) {
            KeyRelation relationWithKeylock = getClosestKeyRelation(targetBpm, sourceKey, targetBpm, targetKey);
            result.setRelationWithKeylock(relationWithKeylock);
        }
        return result;
    }    
    
    static public SongKeyRelation getClosestKeyRelation(SongLinkedList sourceSong, double targetBpm, Key targetKey) {
        return getClosestKeyRelation(sourceSong, targetBpm, targetKey, Double.NEGATIVE_INFINITY);
    }

    static public SongKeyRelation getClosestKeyRelation(SongLinkedList sourceSong, SongLinkedList targetSong) {
        // TODO: re-think this, ANY 2 songs can be made compatible by using key lock on one song
        // and not on another, and stretching bpm to a certain point, so this is really a search for the closest
        // way to make 2 songs compatible...
        float targetBpm = targetSong.getStartbpm();
        if (targetSong.getEndbpm() != 0.0f)
            targetBpm = targetSong.getEndbpm();
        Key targetKey = targetSong.getEndKey().isValid() ? targetSong.getEndKey() : targetSong.getStartKey();
        return getClosestKeyRelation(sourceSong, targetBpm, targetKey);
    }

    static public boolean isValid(String key) {
        if (key == null) return false;
        if (key.equals("")) return false;
        boolean val = false;
        if (key.charAt(0) == 'a') val = true;
        if (key.charAt(0) == 'A') val = true;
        if (key.charAt(0) == 'b') val = true;
        if (key.charAt(0) == 'B') val = true;
        if (key.charAt(0) == 'c') val = true;
        if (key.charAt(0) == 'C') val = true;
        if (key.charAt(0) == 'd') val = true;
        if (key.charAt(0) == 'D') val = true;
        if (key.charAt(0) == 'e') val = true;
        if (key.charAt(0) == 'E') val = true;
        if (key.charAt(0) == 'f') val = true;
        if (key.charAt(0) == 'F') val = true;
        if (key.charAt(0) == 'g') val = true;
        if (key.charAt(0) == 'G') val = true;
        if (!val) {
            // check for key code format
            try {
                boolean firstnumeric = Character.isDigit(key.charAt(0));
                boolean secondnumeric = Character.isDigit(key.charAt(1));
                if (firstnumeric && isValidKeyCodeLetter(key.charAt(1))) val = true;
                if (firstnumeric && secondnumeric && isValidKeyCodeLetter(key.charAt(2))) val = true;
            } catch (Exception e) { }
        }
        return val;
    }    
    
    static private boolean isValidKeyCodeLetter(char c) {
        c = Character.toLowerCase(c);
        if (c == 'a') return true;
        if (c == 'b') return true;
        if (c == 'i') return true;
        if (c == 'd') return true;
        if (c == 'p') return true;
        if (c == 'm') return true;
        if (c == 'l') return true;
        if (c == 'c') return true;
        return false;
    }
    
    static public String getKeyDescription(SongLinkedList song) {
        StringBuffer key = new StringBuffer();
        if (song.getStartKey().isValid()) {
	        Key startkey = song.getStartKey();
	        if (startkey.getKeyCode().toString().length() == 2)
	            key.append("0");
	        key.append(startkey.getKeyCode());
	        key.append(", ");
	        key.append(startkey.getID3SafeKeyNotation());
        }
        if (song.getEndKey().isValid()) {
            if (key.length() > 0)
                key.append("->");
            Key endkey = song.getEndKey();
            if (endkey.getKeyCode().toString().length() == 2)
                key.append("0");
            key.append(endkey.getKeyCode());
            key.append(", ");
            key.append(endkey.getID3SafeKeyNotation());
        }
        return key.toString();        
    }   
        
    static public String getPreferredKeyNotation(String key) {
        return Key.getKey(key).getPreferredKeyNotation();
    }    
    
    /**
     * This returns the shift required to transform the source scale type to the target scale type.
     * 
     * @param int sourceScaleType
     * @param int targetScaleType
     * @return double shift from source to target
     */
    static public double getRelativeShift(int sourceScaleType, int targetScaleType) {
        // convert source scale type to ionian:
        double sourceDifference = getShiftToIonian(sourceScaleType);
        double targetDifference = getShiftToIonian(targetScaleType);
        return (sourceDifference - targetDifference);        
    }    
    
    static private Map keyFlyWeights = new HashMap();
    
    /**
     * This returns the shift required to transform the scale type to ionian (major).
     * 
     * @param int scaleType
     * @return double shift to ionian
     */
    static private double getShiftToIonian(int scaleType) {
        double difference = 0.0;
        if (scaleType == SCALE_AEOLIAN)
            difference = 3.0;
        else if (scaleType == SCALE_LYDIAN)
            difference = 7.0;
        else if (scaleType == SCALE_MIXOLYDIAN)
            difference = 5.0;
        else if (scaleType == SCALE_DORIAN)
            difference = 10.0;
        else if (scaleType == SCALE_PHRYGIAN)
            difference = 8.0;
        else if (scaleType == SCALE_LOCRIAN)
            difference = 1.0;
        return difference;
    }          
    
    static private double validateRootValue(double root_value) {
        if (root_value == Double.NEGATIVE_INFINITY)
            return root_value;
        while (root_value >= 11.5) root_value -= 12;
        while (root_value < -0.5) root_value += 12;
        return root_value;
    }    
    
    static private String calculatePrimaryKey(int shifted_key, byte scale_type) {
        StringBuffer result = new StringBuffer();
        result.append(shifted_key);
        result.append(",");
        result.append(scale_type);
        return result.toString();
    }
    
    static private Key checkFactory(double root_value, byte scale_type) {
        String primaryKey = calculatePrimaryKey((int)(root_value * 100.0), scale_type);
        //if (log.isTraceEnabled()) log.trace("checkFactory(): root_value=" + root_value + ", scale_type=" + scale_type + ", primaryKey=" + primaryKey);
        Key result = (Key)keyFlyWeights.get(primaryKey);
        if (result == null) {
            if (log.isTraceEnabled()) log.trace("checkFactory(): no existing key found");
            result = new Key(root_value, scale_type);
            keyFlyWeights.put(primaryKey, result);
        } else {
            //if (log.isTraceEnabled()) log.trace("checkFactory(): existing key reused!");
        }
        return result;
    }

    
}

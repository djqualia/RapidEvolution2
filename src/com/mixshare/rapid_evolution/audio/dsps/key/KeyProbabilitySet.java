package com.mixshare.rapid_evolution.audio.dsps.key;

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.comparables.MySortObject;
import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.music.Key;

public class KeyProbabilitySet {

    static private Logger log = Logger.getLogger(KeyProbabilitySet.class);
    
    double[] normalizedProbabilities;
    private boolean major;
    private String type;
    
    
    public KeyProbabilitySet(double[] normalizedProbabilities, boolean major, String type) {
        this.normalizedProbabilities = normalizedProbabilities;
        this.major = major;
        this.type = type;
        if (log.isDebugEnabled()) {
            double total = 0.0;
            for (int i = 0; i < normalizedProbabilities.length; ++i) {
                total += normalizedProbabilities[i];
            }
            double diff = Math.abs(total - 1.0);
            if (diff > 0.00001)
                log.debug("KeyProbabilitySet(): improper probability set, total=" + total);
        }
    }
        
    public double getProbability(int index) {
        return normalizedProbabilities[index];
    }
    
    public double getMaxProbability() {
        double maximum = Double.MIN_VALUE;
        for (int i = 0; i < 12; ++i) {
            if (normalizedProbabilities[i] > maximum)
                maximum = normalizedProbabilities[i];
        }
        return maximum;                
    }
    
    public double getTotalProbability() {
        double total = 0.0;
        for (int i = 0; i < 12; ++i) {
            total += normalizedProbabilities[i];
        }
        return total;        
    }
    
    public double getMinProbability() {
        double minimum = Double.MAX_VALUE;
        for (int i = 0; i < 12; ++i) {
            if (normalizedProbabilities[i] < minimum)
                minimum = normalizedProbabilities[i];
        }
        return minimum;        
    }
    
    public String getKey() {
        double maximum = Double.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < 12; ++i) {
            if (normalizedProbabilities[i] > maximum) {
                maximum = normalizedProbabilities[i];
                index = i;
            }
        }
        return getKeyString(index, major, type);        
    }
    
    public String getType() { return type; }
    public boolean isMajor() { return major; }
    
    public void addResults(Vector results) {
        for (int i = 0; i < 12; ++i) {
            results.add(new MySortObject(getKeyString(i, major, type), normalizedProbabilities[i]));
        }        
    }
    
    static private String getKeyString(int index, boolean major, String type) {
        StringBuffer key = new StringBuffer();
        if (index == 0) key.append("A");
        else if (index == 1) key.append("A#");
        else if (index == 2) key.append("B");
        else if (index == 3) key.append("C");
        else if (index == 4) key.append("C#");
        else if (index == 5) key.append("D");
        else if (index == 6) key.append("D#");
        else if (index == 7) key.append("E");
        else if (index == 8) key.append("F");
        else if (index == 9) key.append("F#");
        else if (index == 10) key.append("G");
        else if (index == 11) key.append("G#");
        else return "";
        if (!major) key.append("m");
        if (!OptionsUI.instance.detect_advanced_keys.isSelected())
            return Key.getKey(key.toString()).getPreferredKeyNotation();
        key.append(" ");
        key.append(type);
        return key.toString().trim();
    }    
    
}

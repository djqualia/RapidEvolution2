package com.mixshare.rapid_evolution.music;

import org.apache.log4j.Logger;

public class ShiftedKeyNotation {

    static private Logger log = Logger.getLogger(ShiftedKeyNotation.class);
    
    private byte root_note;
    private double shift;
    
    public ShiftedKeyNotation(double root_value) {
        if (log.isTraceEnabled()) log.trace("ShiftedKeyNotation(): root_value=" + root_value);
        for (byte i = 0; i < 12; ++i) {
            double diff = root_value - (double)i;
            if (diff >= 11) diff -= 12;
            if (diff <= -11) diff += 12;
            if (Math.abs(diff) <= 0.5) {
                root_note = i;
                shift = diff;
            }
        }        
    }
    
    public ShiftedKeyNotation(byte root_note, double shift) {
        this.root_note = root_note;
        this.shift = shift;
    }
    
    public byte getRootNote() { return root_note; }    
    public double getShift() { return shift; }
    public int getShiftInCents() {
        return (int)(100.0 * shift);
    }
    
}

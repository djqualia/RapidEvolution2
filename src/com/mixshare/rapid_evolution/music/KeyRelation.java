package com.mixshare.rapid_evolution.music;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

public class KeyRelation implements Comparable {
    
    static private Logger log = Logger.getLogger(KeyRelation.class);
    
    static public byte RELATION_TONIC = 0;
    static public byte RELATION_TONIC_MODAL = 1;
    static public byte RELATION_DOMINANT = 2;
    static public byte RELATION_DOMINANT_MODAL = 3;
    static public byte RELATION_SUBDOMINANT = 4;
    static public byte RELATION_SUBDOMINANT_MODAL = 5;
    static public byte RELATION_RELATIVE_TONIC = 6;
    static public byte RELATION_RELATIVE_DOMINANT = 7;
    static public byte RELATION_RELATIVE_SUBDOMINANT = 8;
    static public byte RELATION_NONE = Byte.MAX_VALUE;
    
    private double difference = Double.NEGATIVE_INFINITY;
    private byte relationship = RELATION_NONE;

    static public KeyRelation INVALID_RELATION = new KeyRelation(Double.NEGATIVE_INFINITY, RELATION_NONE);
    
    public KeyRelation(double difference, byte relationship) {
        if (log.isTraceEnabled()) log.trace("KeyRelation(): difference=" + difference + ", relationship=" + relationship);
        this.difference = difference;
        this.relationship = relationship;
    }
    
    public int compareTo(Object o) {
        if (o instanceof KeyRelation) {
            KeyRelation oRelation = (KeyRelation)o;
            if (relationship < oRelation.getRelationship()) return -1;
            if (relationship > oRelation.getRelationship()) return 1;
        }
        return 0;
    }
    
    public boolean hasDifference() { return (difference != Double.NEGATIVE_INFINITY); }
    public double getDifference() { return difference; }
    public byte getRelationship() { return relationship; }
        
    public boolean isCompatible() {
        return relationship != RELATION_NONE;
    }
    
    public boolean isValid() {
        return difference != Double.NEGATIVE_INFINITY;
    }
    
    public String toString() {
        if (relationship == RELATION_NONE) {
            return "";
        } else if (relationship == RELATION_TONIC) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_tonic_text");
            return "tonic";
        } else if (relationship == RELATION_SUBDOMINANT) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_subdominant_text");
            return "subdominant";
        } else if (relationship == RELATION_DOMINANT) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_dominant_text");
            return "dominant";            
        } else if (relationship == RELATION_TONIC_MODAL) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_tonic_modal_text");
            return "tonic modal";
        } else if (relationship == RELATION_SUBDOMINANT_MODAL) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_subdominant_modal_text");
            return "subdominant modal";
        } else if (relationship == RELATION_DOMINANT_MODAL) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_dominant_modal_text");
            return "dominant modal";            
        } else if (relationship == RELATION_RELATIVE_TONIC) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_relative_tonic_text");
            return "relative tonic";            
        } else if (relationship == RELATION_RELATIVE_SUBDOMINANT) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_relative_subdominant_text");
            return "relative subdominant";            
        } else if (relationship == RELATION_RELATIVE_DOMINANT) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("key_mode_relative_dominant_text");
            return "relative dominant";            
        }
        return null;
    }
    
}

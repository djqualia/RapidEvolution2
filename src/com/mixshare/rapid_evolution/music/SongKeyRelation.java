package com.mixshare.rapid_evolution.music;

import org.apache.log4j.Logger;

import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

public class SongKeyRelation {

    static private Logger log = Logger.getLogger(SongKeyRelation.class);
    
    private KeyRelation withKeylock = null;
    private KeyRelation withoutKeylock = null;
    
    public SongKeyRelation() { }

    public void setRelationWithKeylock(KeyRelation relation) {
        withKeylock = relation;
    }    
    public KeyRelation getRelationWithKeylock() {
        if (withKeylock == null)
            return KeyRelation.INVALID_RELATION;
        return withKeylock;
    }

    public void setRelationWithoutKeylock(KeyRelation relation) {
        withoutKeylock = relation;
    }        
    public KeyRelation getRelationWithoutKeylock() {
        if (withoutKeylock == null)
            return KeyRelation.INVALID_RELATION;
        return withoutKeylock;
    }
 
    public KeyRelation getBestKeyRelation() {
        if ((withKeylock != null) && (withoutKeylock != null)) {
            if (OptionsUI.instance.searchexcludenokeylock.isSelected() ||
                (Math.abs(withKeylock.getDifference()) < Math.abs(withoutKeylock.getDifference())))
                return withKeylock;
            else
                return withoutKeylock;
        } else if (withoutKeylock != null) {
            return withoutKeylock;
        } else {
            return withKeylock;
        }        
    }

    public boolean isBestRelationWithKeylock() {
        if ((withKeylock != null) && (withoutKeylock != null)) {
            if (Math.abs(withKeylock.getDifference()) < Math.abs(withoutKeylock.getDifference()))
                return true;
        }
        return false;
    }
    
    public boolean isCompatibleWithKeylock() {
        return ((withKeylock != null) && withKeylock.isCompatible() && ((OptionsUI.instance == null) || !OptionsUI.instance.disablekeylockfunctionality.isSelected()));
    }

    public boolean isCompatibleWithoutKeylock() {
        return ((withoutKeylock != null) && withoutKeylock.isCompatible() && ((OptionsUI.instance == null) || (!OptionsUI.instance.searchexcludenokeylock.isSelected() || OptionsUI.instance.disablekeylockfunctionality.isSelected())));
    }
    
    public boolean isCompatible() {
        return (isCompatibleWithKeylock() || isCompatibleWithoutKeylock());
    }
    
    public String getRecommendedKeyLockSetting() {
        if (((withKeylock == null) || !withKeylock.isValid()) && ((withoutKeylock == null) || !withoutKeylock.isValid())) return "";
        if (!OptionsUI.instance.disablekeylockfunctionality.isSelected()) {
            if (OptionsUI.instance.searchexcludenokeylock.isSelected() ||
                (getRelationWithKeylock().isValid() && (!getRelationWithoutKeylock().isValid() || (Math.abs(getRelationWithKeylock().getDifference()) < Math.abs(getRelationWithoutKeylock().getDifference()))))) {
                if (SkinManager.instance != null)
                    return SkinManager.instance.getMessageText("default_true_text");
                return "yes";
            }
        }
        if (getRelationWithoutKeylock().isValid()) {
            if (SkinManager.instance != null)
                return SkinManager.instance.getMessageText("default_false_text");
            return "no";
        }
        return "***";        
    }
    
}

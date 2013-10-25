package com.mixshare.rapid_evolution.test.music;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.music.KeyRelation;
import com.mixshare.rapid_evolution.test.RETestCase;

public class KeyTest extends RETestCase {

    static private Logger log = Logger.getLogger(KeyTest.class);
    
    /**
      * Mixolydian (-1)
      *	Phrygian (-1)
      * Dorian (+1)
      * Lydian (+1)
      *
     */
    public void testKeyNotation() {

        // test minor: Am
        Key key = Key.getKey("Am");
        if (!key.getKeyCode().toString(false).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("8A");
        if (!key.getKeyCode().toString(false).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("Am aeolian");
        if (!key.getKeyCode().toString(false).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("8A aeolian");
        if (!key.getKeyCode().toString(false).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("8A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");         
        
        key = Key.getKey("Am dorian");
        if (!key.getKeyCode().toString(false).equals("9A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("9D")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("8A dorian");
        if (!key.getKeyCode().toString(false).equals("9A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("9D")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");            
        
        key = Key.getKey("Am phrygian");
        if (!key.getKeyCode().toString(false).equals("7A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("7P")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("8A phrygian");
        if (!key.getKeyCode().toString(false).equals("7A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("7P")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");             

        key = Key.getKey("Am locrian");
        if (!key.getKeyCode().toString(false).equals("6A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyCode().toString(true).equals("6C")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Am locrian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationFlat(false).equals("Am")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Am locrian")) fail("incorrect key notation sharp");
        if (!key.getKeyNotationSharp(false).equals("Am")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           
        
        // test minor: A#m            
        key = Key.getKey("A#m");
        if (!key.getKeyCode().toString(true).equals("3A")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(true).equals("Bbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("3A")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Bbm");
        if (!key.getKeyCode().toString(true).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("3A");
        if (!key.getKeyCode().toString(true).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect"); 

        key = Key.getKey("A#m aeolian");
        if (!key.getKeyCode().toString(true).equals("3A")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(true).equals("Bbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("3A")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Bbm aeolian");
        if (!key.getKeyCode().toString(true).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("3A aeolian");
        if (!key.getKeyCode().toString(true).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("3A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("A#m dorian");
        if (!key.getKeyCode().toString(true).equals("4D")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(true).equals("Bbm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("4A")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Bbm dorian");
        if (!key.getKeyCode().toString(true).equals("4D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("4A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("3A dorian");
        if (!key.getKeyCode().toString(true).equals("4D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("4A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");    
        
        key = Key.getKey("A#m phrygian");
        if (!key.getKeyCode().toString(true).equals("2P")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(true).equals("Bbm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2A")) fail("incorrect key code, is=" + key.getKeyCode() );
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Bbm phrygian");
        if (!key.getKeyCode().toString(true).equals("2P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("3A phrygian");
        if (!key.getKeyCode().toString(true).equals("2P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bbm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        // test minor: Bm
        key = Key.getKey("Bm");
        if (!key.getKeyCode().toString(true).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("10A");
        if (!key.getKeyCode().toString(true).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");   

        key = Key.getKey("Bm aeolian");
        if (!key.getKeyCode().toString(true).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("10A aeolian");
        if (!key.getKeyCode().toString(true).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("10A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");   
        
        key = Key.getKey("Bm dorian");
        if (!key.getKeyCode().toString(true).equals("11D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("10A dorian");
        if (!key.getKeyCode().toString(true).equals("11D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");   
        
        key = Key.getKey("Bm phrygian");
        if (!key.getKeyCode().toString(true).equals("9P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("9A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("10A phrygian");
        if (!key.getKeyCode().toString(true).equals("9P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Bm phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("9A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Bm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Bm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        // test minor: Cm
        key = Key.getKey("Cm");
        if (!key.getKeyCode().toString(true).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("5A");
        if (!key.getKeyCode().toString(true).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("Cm aeolian");
        if (!key.getKeyCode().toString(true).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("5A aeolian");
        if (!key.getKeyCode().toString(true).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("5A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");            

        key = Key.getKey("Cm dorian");
        if (!key.getKeyCode().toString(true).equals("6D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("6A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("5A dorian");
        if (!key.getKeyCode().toString(true).equals("6D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("6A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");    
        
        key = Key.getKey("Cm phrygian");
        if (!key.getKeyCode().toString(true).equals("4P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("4A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("5A phrygian");
        if (!key.getKeyCode().toString(true).equals("4P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Cm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Cm phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("4A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Cm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("Cm")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");    
        
        // test minor: C#m            
        key = Key.getKey("C#m");
        if (!key.getKeyCode().toString(true).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Dbm");
        if (!key.getKeyCode().toString(true).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12A");
        if (!key.getKeyCode().toString(true).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("C#m aeolian");
        if (!key.getKeyCode().toString(true).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Dbm aeolian");
        if (!key.getKeyCode().toString(true).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12A aeolian");
        if (!key.getKeyCode().toString(true).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("C#m dorian");
        if (!key.getKeyCode().toString(true).equals("1D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Dbm dorian");
        if (!key.getKeyCode().toString(true).equals("1D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12A dorian");
        if (!key.getKeyCode().toString(true).equals("1D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");        
        
        key = Key.getKey("C#m phrygian");
        if (!key.getKeyCode().toString(true).equals("11P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Dbm phrygian");
        if (!key.getKeyCode().toString(true).equals("11P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12A phrygian");
        if (!key.getKeyCode().toString(true).equals("11P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dbm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Dbm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("C#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        // test minor: Dm
        key = Key.getKey("Dm");
        if (!key.getKeyCode().toString(true).equals("7A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Dm aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 5.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("7A");
        if (!key.getKeyCode().toString(true).equals("7A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Dm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Dm aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 5.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        // test minor: D#m            
        key = Key.getKey("D#m");
        if (!key.getKeyCode().toString(true).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ebm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D#m aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 6.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Ebm");
        if (!key.getKeyCode().toString(true).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ebm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D#m aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 6.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("2A");
        if (!key.getKeyCode().toString(true).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ebm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D#m aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 6.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        // test minor: Em
        key = Key.getKey("Em");
        if (!key.getKeyCode().toString(true).equals("9A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Em aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Em aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("9A");
        if (!key.getKeyCode().toString(true).equals("9A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Em aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Em aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        // test minor: Fm
        key = Key.getKey("Fm");
        if (!key.getKeyCode().toString(true).equals("4A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Fm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Fm aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 8.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("4A");
        if (!key.getKeyCode().toString(true).equals("4A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Fm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Fm aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 8.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        // test minor: F#m            
        key = Key.getKey("F#m");
        if (!key.getKeyCode().toString(true).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F#m aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 9.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Gbm");
        if (!key.getKeyCode().toString(true).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F#m aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 9.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("11A");
        if (!key.getKeyCode().toString(true).equals("11A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gbm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F#m aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 9.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        // test minor: Gm
        key = Key.getKey("Gm");
        if (!key.getKeyCode().toString(true).equals("6A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Gm aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        key = Key.getKey("6A");
        if (!key.getKeyCode().toString(true).equals("6A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("Gm aeolian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");           

        // test minor: G#m            
        key = Key.getKey("G#m");
        if (!key.getKeyCode().toString(true).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Abm");
        if (!key.getKeyCode().toString(true).equals("1A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Abm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1A");
        if (!key.getKeyCode().toString(true).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");            
        
        key = Key.getKey("G#m aeolian");
        if (!key.getKeyCode().toString(true).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Abm aeolian");
        if (!key.getKeyCode().toString(true).equals("1A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Abm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1A aeolian");
        if (!key.getKeyCode().toString(true).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm aeolian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");  
        
        key = Key.getKey("G#m dorian");
        if (!key.getKeyCode().toString(true).equals("2D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Abm dorian");
        if (!key.getKeyCode().toString(true).equals("2D")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Abm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1A dorian");
        if (!key.getKeyCode().toString(true).equals("2D")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm dorian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m dorian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");   
        
        key = Key.getKey("G#m phrygian");
        if (!key.getKeyCode().toString(true).equals("12P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Abm phrygian");
        if (!key.getKeyCode().toString(true).equals("12P")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Abm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1A phrygian");
        if (!key.getKeyCode().toString(true).equals("12P")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Abm phrygian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G#m phrygian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12A")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("Abm")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("G#m")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (!key.isMinor()) fail("isMinor() is incorrect");         
        
        // test major:
        key = Key.getKey("A");
        if (!key.getKeyCode().toString(true).equals("11I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("11B");
        if (!key.getKeyCode().toString(true).equals("11I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("A ionian");
        if (!key.getKeyCode().toString(true).equals("11I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("11B ionian");
        if (!key.getKeyCode().toString(true).equals("11I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");        

        key = Key.getKey("A lydian");
        if (!key.getKeyCode().toString(true).equals("12L")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A lydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A lydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("11B lydian");
        if (!key.getKeyCode().toString(true).equals("12L")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A lydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A lydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");  
        
        key = Key.getKey("A mixolydian");
        if (!key.getKeyCode().toString(true).equals("10M")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A mixolydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A mixolydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("10B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("11B mixolydian");
        if (!key.getKeyCode().toString(true).equals("10M")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("A mixolydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A mixolydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("10B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("A")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("A")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 0.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");  
        
        // test major: A#
        key = Key.getKey("A#");
        if (!key.getKeyCode().toString(true).equals("6I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("Bb");
        if (!key.getKeyCode().toString(true).equals("6I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("6B");
        if (!key.getKeyCode().toString(true).equals("6I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Bb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("A# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 1.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        // test major: B
        key = Key.getKey("B");
        if (!key.getKeyCode().toString(true).equals("1I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1B");
        if (!key.getKeyCode().toString(true).equals("1I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("B ionian");
        if (!key.getKeyCode().toString(true).equals("1I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1B ionian");
        if (!key.getKeyCode().toString(true).equals("1I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("B lydian");
        if (!key.getKeyCode().toString(true).equals("2L")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B lydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B lydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1B lydian");
        if (!key.getKeyCode().toString(true).equals("2L")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B lydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B lydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("2B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("B mixolydian");
        if (!key.getKeyCode().toString(true).equals("12M")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B mixolydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B mixolydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("1B mixolydian");
        if (!key.getKeyCode().toString(true).equals("12M")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("B mixolydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("B mixolydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("B")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("B")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 2.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
                        
        // test major: C
        key = Key.getKey("C");
        if (!key.getKeyCode().toString(true).equals("8I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("C ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("8B");
        if (!key.getKeyCode().toString(true).equals("8I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("C ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 3.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
    
        // test major: C#
        key = Key.getKey("C#");
        if (!key.getKeyCode().toString(true).equals("3I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Db ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("Db");
        if (!key.getKeyCode().toString(true).equals("3I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Db ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("3B");
        if (!key.getKeyCode().toString(true).equals("3I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Db ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("C# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 4.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        // test major: D
        key = Key.getKey("D");
        if (!key.getKeyCode().toString(true).equals("10I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("D ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 5.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("10B");
        if (!key.getKeyCode().toString(true).equals("10I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("D ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 5.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        // test major: D#
        key = Key.getKey("D#");
        if (!key.getKeyCode().toString(true).equals("5I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Eb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 6.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("Eb");
        if (!key.getKeyCode().toString(true).equals("5I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Eb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 6.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("5B");
        if (!key.getKeyCode().toString(true).equals("5I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Eb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("D# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 6.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        // test major: E
        key = Key.getKey("E");
        if (!key.getKeyCode().toString(true).equals("12I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12B");
        if (!key.getKeyCode().toString(true).equals("12I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("E ionian");
        if (!key.getKeyCode().toString(true).equals("12I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12B ionian");
        if (!key.getKeyCode().toString(true).equals("12I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E ionian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("12B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("E lydian");
        if (!key.getKeyCode().toString(true).equals("1L")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E lydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E lydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12B lydian");
        if (!key.getKeyCode().toString(true).equals("1L")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E lydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E lydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("1B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("E mixolydian");
        if (!key.getKeyCode().toString(true).equals("11M")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E mixolydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E mixolydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("12B mixolydian");
        if (!key.getKeyCode().toString(true).equals("11M")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("E mixolydian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("E mixolydian")) fail("incorrect key notation sharp");
        if (!key.getKeyCode().toString(false).equals("11B")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(false).equals("E")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(false).equals("E")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 7.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        // test major: F
        key = Key.getKey("F");
        if (!key.getKeyCode().toString(true).equals("7I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("F ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 8.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("7B");
        if (!key.getKeyCode().toString(true).equals("7I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("F ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 8.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        // test major: F#
        key = Key.getKey("F#");
        if (!key.getKeyCode().toString(true).equals("2I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 9.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("Gb");
        if (!key.getKeyCode().toString(true).equals("2I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 9.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("2B");
        if (!key.getKeyCode().toString(true).equals("2I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Gb ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("F# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 9.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        // test major: G
        key = Key.getKey("G");
        if (!key.getKeyCode().toString(true).equals("9I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("G ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("9B");
        if (!key.getKeyCode().toString(true).equals("9I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("G ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        // test major: G#
        key = Key.getKey("G#");
        if (!key.getKeyCode().toString(true).equals("4I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("Ab");
        if (!key.getKeyCode().toString(true).equals("4I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("4B");
        if (!key.getKeyCode().toString(true).equals("4I")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 11.0) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        // test shifts
        key = Key.getKey("G#+50");
        if (!key.getKeyCode().toString(true).equals("4I +50")) fail("incorrect key code: " + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Ab ionian +50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian +50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != -0.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Ab +50");
        if (!key.getKeyCode().toString(true).equals("4I +50")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian +50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian +50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != -0.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("4B +50cents");
        if (!key.getKeyCode().toString(true).equals("4I +50")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian +50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian +50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != -0.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("G#-50");
        if (!key.getKeyCode().toString(true).equals("4I -50")) fail("incorrect key code, is=" + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Ab ionian -50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian -50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("Ab -50");
        if (!key.getKeyCode().toString(true).equals("4I -50")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian -50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian -50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");
        
        key = Key.getKey("4B -50cents");
        if (!key.getKeyCode().toString(true).equals("4I -50")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian -50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian -50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        // test misc. things
        key = Key.getKey("    4b    -50   cents    ");
        if (!key.getKeyCode().toString(true).equals("4I -50")) fail("incorrect key code");
        if (!key.getKeyNotationFlat(true).equals("Ab ionian -50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian -50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("    g  #    -    50   cents    ");
        if (!key.getKeyCode().toString(true).equals("4I -50")) fail("incorrect key code: " + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Ab ionian -50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian -50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != 10.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey("    g  #    +    50   cents    ");
        if (!key.getKeyCode().toString(true).equals("4I +50")) fail("incorrect key code: " + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("Ab ionian +50")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("G# ionian +50")) fail("incorrect key notation sharp");
        if (key.getRootValue() != -0.5) fail("incorrect key value");
        if (key.isMinor()) fail("isMinor() is incorrect");

        key = Key.getKey(" ");
        if (!key.getKeyCode().toString(true).equals("")) fail("incorrect key code: " + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("")) fail("incorrect key notation sharp");
        if (key.isValid()) fail("key should be invalid");

        key = Key.getKey("");
        if (!key.getKeyCode().toString(true).equals("")) fail("incorrect key code: " + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("")) fail("incorrect key notation sharp");
        if (key.isValid()) fail("key should be invalid");

        key = Key.getKey("zyx");
        if (!key.getKeyCode().toString(true).equals("")) fail("incorrect key code: " + key.getKeyCode());
        if (!key.getKeyNotationFlat(true).equals("")) fail("incorrect key notation flat");
        if (!key.getKeyNotationSharp(true).equals("")) fail("incorrect key notation sharp");
        if (key.isValid()) fail("key should be invalid");
        
    }
    
    public void testKeyShifting() {
        Key key = Key.getKey("Am");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone);
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian")) fail("incorrect shift, is=" + key.getKeyNotationSharp());
        
        key = Key.getKey("Am");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone / 4);
        if (!key.getKeyNotationSharp(true).equals("Am aeolian +25")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("Am");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone * 3 / 4);
        if (!key.getKeyNotationSharp(true).equals("A#m aeolian -25")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("Am");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone * 5);
        if (!key.getKeyNotationSharp(true).equals("Dm aeolian")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("Am");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone * 7);
        if (!key.getKeyNotationSharp(true).equals("Em aeolian")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("G#m");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone);
        if (!key.getKeyNotationSharp(true).equals("Am aeolian")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));
        
        key = Key.getKey("G#m");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone / 4);
        if (!key.getKeyNotationSharp(true).equals("G#m aeolian +25")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("G#m");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone * 3 / 4);
        if (!key.getKeyNotationSharp(true).equals("Am aeolian -25")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("G#m");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone * 5);
        if (!key.getKeyNotationSharp(true).equals("C#m aeolian")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        

        key = Key.getKey("G#m");
        key = key.getShiftedKeyByBpmDifference(Key.bpmDifferencePerSemitone * 7);
        if (!key.getKeyNotationSharp(true).equals("D#m aeolian")) fail("incorrect shift, is=" + key.getKeyNotationSharp(true));        
        
    }
    
    public void testKeyRelations() {
        KeyRelation relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am dorian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC_MODAL) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am phrygian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC_MODAL) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("B locrian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("C lydian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("C mixolydian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("F lydian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G mixolydian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Dm dorian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT_MODAL) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Em phrygian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT_MODAL) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 101.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0 / Key.bpmDifferencePerSemitone) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Em"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("C"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("G"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("F"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("A#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Bm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Cm-50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.5) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Cm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("C#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("D#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Fm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("F#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("F#m+50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.5) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("Gm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 101.0, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (Math.abs(relation.getDifference() - (-1.0 / Key.bpmDifferencePerSemitone)) > 0.000001) fail("incorrect relation difference, is=" + relation.getDifference() + ", expecting=" + -1.0 / Key.bpmDifferencePerSemitone);
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("D#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("C#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("B"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("F#"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("E"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("A#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Bm-50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.5) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Bm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Cm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Em"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Fm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Fm+50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.5) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("F#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0, Key.getKey("Gm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Em"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("C"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("G"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("F"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("A#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Bm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Cm-50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.5)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Cm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("C#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("D#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Fm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("F#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("F#m+50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.5)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Gm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G#m"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("D#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("C#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("B"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("F#"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("E"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("A#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Bm-50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.5) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Bm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Cm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Em"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Fm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Fm+50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.5) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("F#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -2.0) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Gm"), 100.0 + Key.bpmDifferencePerSemitone, Key.getKey("Gm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -1.0) fail("incorrect relation difference, is=" + relation.getDifference());        

        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());
                
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Em"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("C"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("G"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("F"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("A#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Bm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Cm-50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.5)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Cm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("C#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("D#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Fm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("F#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("F#m+50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.5)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Gm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("A#m"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("G#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());
                
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("D#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("C#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("B"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("F#"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_SUBDOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("E"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 0.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("A#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Bm-50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.5)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Bm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Cm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Em"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Fm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), 2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Fm+50"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.5)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("F#m"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -2.0)) fail("incorrect relation difference, is=" + relation.getDifference());        
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Am"), 100.0 * (100.0 - Key.bpmDifferencePerSemitone) / 100.0, Key.getKey("Gm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (!areEqual(relation.getDifference(), -1.0)) fail("incorrect relation difference, is=" + relation.getDifference());        

        // test difficult situations (are these valid?):

        /*
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Bm phrygian"), 100.0, Key.getKey("Em phrygian"));
        if (relation.getRelationship() != KeyRelation.RELATION_DOMINANT) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Dm"), 100.0, Key.getKey("Em phrygian"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Em phrygian"), 100.0, Key.getKey("Dm"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Bm phrygian"), 100.0, Key.getKey("Am"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Dm phrygian"), 100.0, Key.getKey("Em phrygian"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 2.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Dm dorian"), 100.0, Key.getKey("Em phrygian"));
        if (relation.getRelationship() != KeyRelation.RELATION_RELATIVE_TONIC) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("F"), 100.0, Key.getKey("G mixolydian"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());

        relation = Key.getClosestKeyRelation(100.0, Key.getKey("G mixolydian"), 100.0, Key.getKey("F"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != 0.0) fail("incorrect relation difference, is=" + relation.getDifference());
        
        relation = Key.getClosestKeyRelation(100.0, Key.getKey("Dm locrian"), 100.0, Key.getKey("Em phrygian"));
        if (relation.getRelationship() != KeyRelation.RELATION_NONE) fail("incorrect relation type, is=" + relation.getRelationship());
        if (relation.getDifference() != -3.0) fail("incorrect relation difference, is=" + relation.getDifference());
        */
        
    }
     
}

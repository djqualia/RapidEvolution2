package com.mixshare.rapid_evolution.data;

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.FieldIndex;

public class MusicDatabase {

    ////////////
    // STATIC //
    ////////////
    
    static private Logger log = Logger.getLogger(MusicDatabase.class);
        
    //////////////
    // INSTANCE //
    //////////////
    
    private Vector userLibraries = new Vector();

    private FieldIndex artistIndex = new FieldIndex();
    private FieldIndex albumIndex = new FieldIndex();
    private FieldIndex custom1Index = new FieldIndex();
    private FieldIndex custom2Index = new FieldIndex();
    private FieldIndex custom3Index = new FieldIndex();
    private FieldIndex custom4Index = new FieldIndex();
    
    ////////////
    // PUBLIC //
    ////////////
    
    public void addUserLibrary(UserLibrary library) {
        userLibraries.add(library);
    }
    
    public void removeUserLibrary(UserLibrary library) {
        userLibraries.remove(library);
    }
    
    public FieldIndex getArtistIndex() { return artistIndex; }
    public FieldIndex getAlbumIndex() { return albumIndex; }
    public FieldIndex getCustom1Index() { return custom1Index; }
    public FieldIndex getCustom2Index() { return custom2Index; }
    public FieldIndex getCustom3Index() { return custom3Index; }
    public FieldIndex getCustom4Index() { return custom4Index; }
    
    
    
        
}

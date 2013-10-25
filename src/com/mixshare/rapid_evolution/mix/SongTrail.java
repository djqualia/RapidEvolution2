package com.mixshare.rapid_evolution.mix;

import rapid_evolution.SongLinkedList;

public class SongTrail {

    private int numSongs;
    private SongLinkedList[] songs = null;
    
    public SongTrail(int numSongs, SongLinkedList[] songs) {
        this.numSongs = numSongs;
        this.songs = songs;
    }
    
    public int getNumSongs() {
        return numSongs;        
    }
    
    public SongLinkedList[] getSongs() { return songs; }
    
    public SongLinkedList getSong(int index) {
        if ((songs != null) && (index >= 0) && (index < songs.length))
            return songs[index];
        return null;
    }
    
}

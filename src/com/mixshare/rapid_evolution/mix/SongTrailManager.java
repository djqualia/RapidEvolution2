package com.mixshare.rapid_evolution.mix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongStack;
import rapid_evolution.ui.RapidEvolutionUI;

public class SongTrailManager {

    static private Logger log = Logger.getLogger(SongTrailManager.class);
    
    static public boolean saveSongTrail(SongTrail songTrail, String filename) {
        try {            
            if (!filename.toLowerCase().endsWith(".mix")) filename += ".mix";
            FileWriter outputstream = new FileWriter(filename);
            BufferedWriter outputbuffer = new BufferedWriter(outputstream);
            outputbuffer.write("version=1.1");
            outputbuffer.newLine();
            outputbuffer.write(String.valueOf(songTrail.getNumSongs()));
            outputbuffer.newLine();
            for (int i = 0; i < songTrail.getNumSongs(); ++i) {
                SongLinkedList song = songTrail.getSong(i);
                outputbuffer.write(song.uniquestringid);
                outputbuffer.newLine();                
                outputbuffer.write(String.valueOf(song.uniquesongid));
                outputbuffer.newLine();                
            }    
            outputbuffer.close();
            outputstream.close();                        
            return true;
        } catch (Exception e) {
            log.error("saveSongTrail(): error saving song trail", e);
        }
        return false;
    }
    
    static public SongTrail loadSongTrail(String filename) {
        SongTrail result = null;
        try {
            FileReader inputstream = new FileReader(filename);
            BufferedReader inputbuffer = new BufferedReader(inputstream);
            String firstLine = inputbuffer.readLine();
            String versionString = "version=";
            int numSongs;
            SongLinkedList[] songs = null;
            if (firstLine.startsWith(versionString)) {
                float version = Float.parseFloat(firstLine.substring(versionString.length()));
                numSongs = Integer.parseInt(inputbuffer.readLine());
                songs = new SongLinkedList[numSongs];
                for (int i = 0; i < numSongs; ++i) {
                    String uniqueStringId = inputbuffer.readLine();
                    long uniqueSongId = Long.parseLong(inputbuffer.readLine());   
                    SongLinkedList song = SongDB.instance.NewGetSongPtr(uniqueSongId);
                    if (song == null)
                        song = SongDB.instance.OldGetSongPtr(uniqueStringId);
                    if (song == null) {
                        log.error("loadSongTrail(): song not found, id=" + uniqueSongId + ", stringId=" + uniqueStringId);
                        return null;
                    }                        
                    songs[i] = song;
                }                                
            } else {
                numSongs = Integer.parseInt(firstLine);
                songs = new SongLinkedList[numSongs];
                for (int i = 0; i < numSongs; ++i) {
                    String uniqueStringId = inputbuffer.readLine();
                    SongLinkedList song = SongDB.instance.OldGetSongPtr(uniqueStringId);
                    if (song == null) {
                        log.error("loadSongTrail(): song not found, stringId=" + uniqueStringId);
                        return null;
                    }                        
                    songs[i] = song;
                }                
            }
            inputbuffer.close();
            inputstream.close();
            if (numSongs > 0)
                result = new SongTrail(numSongs, songs);
        } catch (Exception e) {
            log.error("loadSongTrail(): error loading song trails", e);
        }
        return result;
    }
    
    static public SongTrail getCurrentSongTrail() {        
        int numSongs = 0;        
        SongStack tmpstack = null;
        SongStack siter = RapidEvolutionUI.instance.prevstack;
        while (siter != null) {
          tmpstack = new SongStack(siter.songid, tmpstack);
          siter = siter.next;
        }
        siter = tmpstack;
        while (siter != null) {
            ++numSongs;
            siter = siter.next;
        }
        if (RapidEvolutionUI.instance.currentsong != null) {
            ++numSongs;
        }
        siter = RapidEvolutionUI.instance.nextstack;
        while (siter != null) {
            ++numSongs;
            siter = siter.next;
        }
        SongLinkedList[] songs = new SongLinkedList[numSongs];
        int index = 0;
        siter = tmpstack;        
        while (siter != null) {
            SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
            songs[index++] = addsong;
            siter = siter.next;
        }
        if (RapidEvolutionUI.instance.currentsong != null) {
            songs[index++] = RapidEvolutionUI.instance.currentsong;
        }
        siter = RapidEvolutionUI.instance.nextstack;
        while (siter != null) {
            SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
            songs[index++] = addsong;
            siter = siter.next;
        }
        return new SongTrail(numSongs, songs);
    }
    
    
}

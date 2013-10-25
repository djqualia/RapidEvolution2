package com.mixshare.rapid_evolution.audio.detect;

import org.apache.log4j.Logger;

import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.audio.DetectedKey;
import rapid_evolution.audio.KeyDetector;

import com.mixshare.rapid_evolution.io.FileLockManager;
import com.mixshare.rapid_evolution.thread.Job;
import com.mixshare.rapid_evolution.thread.Task;

public class KeyDetectionJob implements Job {
    
    private static Logger log = Logger.getLogger(KeyDetectionJob.class);
    
    private Task task;
    private SongLinkedList song;
    private boolean overwrite;
    
    public KeyDetectionJob(Task task, SongLinkedList song, boolean overwrite) {
        this.task = task;
        this.song = song;
        this.overwrite = overwrite;
    }
    
    public Object execute() {
        boolean passed = true;
        if (!overwrite) {
            if (song.getStartKey().isValid()
                    || song.getEndKey().isValid())
                passed = false;
        }
        if (!song.getFileName().equals("") && passed) {
            try {
        		FileLockManager.startFileRead(song.getRealFileName());            	
                KeyDetector keydetector = new KeyDetector();
                keydetector.setDetectTask(task);
                DetectedKey key = keydetector.detectKeyFromFile(song.getRealFileName());
                if ((key != null) && !key.getStartKey().equals("")) {
                    OldSongValues old_values = new OldSongValues(
                            song);
                    song.setStartkey(key.getStartKey());
                    song.setEndkey(key.getEndKey());
                    song.setKeyAccuracy((int) (key
                                    .getAccuracy() * 100));
                    SongDB.instance.UpdateSong(song, old_values);
                }
            } catch (Exception e) {
                log.debug("run(): error", e);
            }
    		FileLockManager.endFileRead(song.getRealFileName());            	
        }
        return null;
    }

}

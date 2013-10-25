package com.mixshare.rapid_evolution.audio.detect;

import org.apache.log4j.Logger;

import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.audio.dsps.rga.RGAData;
import com.mixshare.rapid_evolution.audio.dsps.rga.RGADetector;
import com.mixshare.rapid_evolution.io.FileLockManager;
import com.mixshare.rapid_evolution.thread.Job;
import com.mixshare.rapid_evolution.thread.Task;

public class ReplayGainDetectionJob implements Job {
    
    private static Logger log = Logger.getLogger(ReplayGainDetectionJob.class);
    
    private Task task;
    private SongLinkedList song;
    private boolean overwrite;
    
    public ReplayGainDetectionJob(Task task, SongLinkedList song, boolean overwrite) {
        this.task = task;
        this.song = song;
        this.overwrite = overwrite;
    }
    
    public Object execute() {
        boolean passed = true;
        if (!overwrite) {
            if (song.getRGA().isValid())
                passed = false;
        }
        if (!song.getFileName().equals("") && passed) {
            try {
        		FileLockManager.startFileRead(song.getRealFileName());            	            	
                RGADetector rgadetector = new RGADetector();
                rgadetector.setTask(task);
                RGAData rga = rgadetector
                        .detectRGA(song.getRealFileName());
                if ((rga != null) && rga.isValid()) {
                    OldSongValues old_values = new OldSongValues(song);
                    song.setRGA(rga);
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
package com.mixshare.rapid_evolution.audio.detect;

import org.apache.log4j.Logger;

import rapid_evolution.AudioColor;
import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.thread.Job;
import com.mixshare.rapid_evolution.thread.Task;
import com.mixshare.rapid_evolution.io.FileLockManager;

public class BeatIntensityDetectionJob implements Job {

    private static Logger log = Logger.getLogger(BeatIntensityDetectionJob.class);
    
    private Task task;
    private SongLinkedList song;
    private boolean overwrite;
    private boolean beatintensity_only;
    
    public BeatIntensityDetectionJob(Task task, SongLinkedList song, boolean overwrite, boolean beatintensity_only) {
        this.task = task;
        this.song = song;
        this.overwrite = overwrite;
        this.beatintensity_only = beatintensity_only;
    }
    
    public Object execute() {        
        try {
            if (overwrite
                    || (beatintensity_only ? (song.getBeatIntensity() == 0)
                            : (song.color == null))) {
            	try {
            		FileLockManager.startFileRead(song.getRealFileName());
            		AudioColor.DetermineColorOfSong(song,
            				beatintensity_only, task);
            		log.trace("run(): song=" + song + ", detected color="
            				+ song.color);
            		if (!task.isCancelled())
            			SongDB.instance.UpdateSong(song,
            					new OldSongValues(song));
            	} catch (Exception e) {
            		log.error("execute(): error", e);
            	}
            	FileLockManager.endFileRead(song.getRealFileName());
            }
        } catch (Exception e) {
            log.error("run(): error", e);
        }        
        return null;
    }
    
}

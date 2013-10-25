package com.mixshare.rapid_evolution.audio.detect;

import org.apache.log4j.Logger;

import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.audio.Bpm;
import rapid_evolution.audio.DetectedBpm;
import rapid_evolution.audio.DetectedKey;
import rapid_evolution.audio.KeyDetector;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.RapidEvolutionUI;

import com.mixshare.rapid_evolution.io.FileLockManager;
import com.mixshare.rapid_evolution.thread.Job;
import com.mixshare.rapid_evolution.thread.Task;

public class BpmDetectionJob implements Job {

    private static Logger log = Logger.getLogger(BpmDetectionJob.class);
    
    private Task task;
    private SongLinkedList song;
    private boolean overwrite;
    
    public BpmDetectionJob(Task task, SongLinkedList song, boolean overwrite) {
        this.task = task;
        this.song = song;
        this.overwrite = overwrite;
    }
    
    public Object execute() {        
        if ((song.getStartbpm() == 0.0f) || overwrite) {
        	try {
	            double measurescale = 1.0;
	            try {
	                String input = song.getTimesig();
	                int seperator = 0;
	                while (((seperator < input.length()) && ((input
	                        .charAt(seperator) != '/'))))
	                    seperator++;
	                int num = Integer.parseInt(input.substring(0,
	                        seperator));
	                int num2 = Integer.parseInt(input.substring(
	                        seperator + 1, input.length()));
	                measurescale = (double) num / (double) num2;
	            } catch (Exception e) {
	                log.error("run(): error", e);
	            }
        		FileLockManager.startFileRead(song.getRealFileName());        		
	            DetectedBpm detectedbpm = Bpm.GetBpmFromFile(
	                    song.getRealFileName(), measurescale, task);
	            if (detectedbpm != null) {
	                float bpm = (float) detectedbpm.getBpm();
	                if (RapidEvolution.instance.terminatesignal || task.isCancelled()) {
	            		FileLockManager.endFileRead(song.getRealFileName());        	
	                    return null;
	                }
	                if (bpm != 0.0f) {
	                    song.setStartbpm(bpm);
	                    song.setBpmAccuracy((int) (detectedbpm
	                            .getAccuracy() * 100));
	                    if (overwrite)
	                        song.setEndbpm(0.0f);
	                    if ((song == EditSongUI.instance.getEditedSong())
	                            && (EditSongUI.instance.isVisible())) {
	                        EditSongUI.instance.editsongsstartbpmfield
	                                .setText(String.valueOf(bpm));
	                    }
	                    SongDB.instance.UpdateSong(song, new OldSongValues(song));
	                }
	            }	            
        	} catch (Exception e) {
        		log.error("execute(): error", e);
        	}
    		FileLockManager.endFileRead(song.getRealFileName());        	
        }
        return null;
    }

}

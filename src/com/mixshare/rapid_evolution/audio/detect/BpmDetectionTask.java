package com.mixshare.rapid_evolution.audio.detect;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;

import com.mixshare.rapid_evolution.thread.Job;
import com.mixshare.rapid_evolution.thread.Task;

public class BpmDetectionTask implements Task {
    
    private int count = 0;
    private int finished = 0;
    private boolean stopFlag = false;
    private SongLinkedList[] songs;
    private boolean overwrite;
    
    public BpmDetectionTask(SongLinkedList[] songs, boolean overwrite) {
        this.songs = songs;
        this.overwrite = overwrite;
        RapidEvolutionUI.instance.detectbpmbatchprogress_ui.progressbar.setValue(0);
        RapidEvolutionUI.instance.detectbpmbatchprogress_ui.setTask(this);
        RapidEvolutionUI.instance.detectbpmbatchprogress_ui.Display();        
    }
    
    public synchronized Job getNextJob() {
        if (RapidEvolution.instance.terminatesignal || stopFlag)
            return null;
        if (count < songs.length)
            return new BpmDetectionJob(this, songs[count++], overwrite);
        return null;
    }
    
    public void finishedJob(Job job, Object subResult) {
        ++finished;
        int percentDone = (int) (((double) finished) / songs.length * 100);
        if (!stopFlag) {
            RapidEvolutionUI.instance.detectbpmbatchprogress_ui.progressbar.setValue(percentDone);        
            if (percentDone == 100) {
                cleanUp();
            }
        }
    }
    
    public boolean isFinished() {
        return (stopFlag || (finished >= songs.length));
    }
    
    public void cancel() {
        stopFlag = true;
        cleanUp();
    }
    
    public boolean isCancelled() {
        return stopFlag;
    }
    
    public void cleanUp() {
        RapidEvolutionUI.instance.detectbpmbatchprogress_ui.Hide();            
    }
    
    public Object getResult() { return null; }
    

}

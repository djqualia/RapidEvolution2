package com.mixshare.rapid_evolution.audio.detect;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;

import com.mixshare.rapid_evolution.thread.Job;
import com.mixshare.rapid_evolution.thread.Task;

import java.util.Vector;

public class AllDetectTask implements Task {

    private int count = 0;
    private int finished = 0;
    private boolean stopFlag = false;
    private SongLinkedList[] songs;
    private boolean overwrite;
    private Vector jobs = new Vector();
    
    public AllDetectTask(SongLinkedList[] songs, boolean overwrite) {
        this.songs = songs;
        this.overwrite = overwrite;
        RapidEvolutionUI.instance.detectallbatchprogress_ui.progressbar.setValue(0);
        RapidEvolutionUI.instance.detectallbatchprogress_ui.setTask(this);
        RapidEvolutionUI.instance.detectallbatchprogress_ui.Display();       
        for (int i = 0; i < songs.length; ++i) {
            jobs.add(new KeyDetectionJob(this, songs[i], overwrite));
            jobs.add(new BpmDetectionJob(this, songs[i], overwrite));
            jobs.add(new BeatIntensityDetectionJob(this, songs[i], overwrite, true));
        }
    }
    
    public synchronized Job getNextJob() {
        if (RapidEvolution.instance.terminatesignal || stopFlag)
            return null;
        if (count < jobs.size())
            return (Job)jobs.get(count++);
        return null;
    }
    
    public void finishedJob(Job job, Object subResult) {
        ++finished;
        int percentDone = (int) (((double) finished) / jobs.size() * 100);
        if (!stopFlag) {
            RapidEvolutionUI.instance.detectallbatchprogress_ui.progressbar.setValue(percentDone);        
            if (percentDone == 100) {
                cleanUp();
            }
        }
    }
    
    public boolean isFinished() {
        return (stopFlag || (finished >= jobs.size()));
    }
    
    public void cancel() {
        stopFlag = true;
        cleanUp();
    }
    
    public boolean isCancelled() {
        return stopFlag;
    }
    
    public void cleanUp() {
        RapidEvolutionUI.instance.detectallbatchprogress_ui.Hide();            
    }
    
    public Object getResult() { return null; }
    

}

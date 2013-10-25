package com.mixshare.rapid_evolution.thread;

/**
 * Is comprised of a collection of Jobs which need to be completed for the task
 */
public interface Task {

    public Job getNextJob();
    
    public void finishedJob(Job job, Object result);
    
    public void cancel();
    
    public boolean isCancelled();
    
    public boolean isFinished();
    
    public Object getResult();
    
}

package com.mixshare.rapid_evolution.thread;

import java.util.concurrent.CountDownLatch;

public class JobRunner extends Thread {

    private Task task;
    private CountDownLatch doneSignal;
    
    public JobRunner(Task task, CountDownLatch doneSignal) {
        this.task = task;
        this.doneSignal = doneSignal;
    }
    
    public void run() {
        Job nextJob = task.getNextJob();
        while (nextJob != null) {
            Object result = nextJob.execute();
            task.finishedJob(nextJob, result);
            nextJob = task.getNextJob();
        }
        doneSignal.countDown();
    }
    
}

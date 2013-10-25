package com.mixshare.rapid_evolution.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import rapid_evolution.ui.OptionsUI;

public class TaskRunner {

    private static Logger log = Logger.getLogger(TaskRunner.class);
	
    static public Object executeTask(Task task) {
        int availableProcessors = OptionsUI.instance.detect_disable_multithreaded.isSelected() ? 1 : getAvailableProcessors();
        if (log.isDebugEnabled())
        	log.debug("executeTask(): using " + availableProcessors + " processors");
        CountDownLatch doneSignal = new CountDownLatch(availableProcessors);
        ExecutorService e = Executors.newFixedThreadPool(availableProcessors);
        for (int i = 0; i < availableProcessors; ++i) {
            JobRunner jobRunner = new JobRunner(task, doneSignal);
            e.execute(jobRunner);
        }
        try {
           doneSignal.await(); // wait for all to finish
        } catch (InterruptedException ie) { }
        e.shutdown();  
        return task.getResult();
    }
    
    static public int getAvailableProcessors() {
    	return java.lang.Runtime.getRuntime().availableProcessors();
    }
    
}

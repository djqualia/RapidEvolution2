package com.mixshare.rapid_evolution.util.timing;

import org.apache.log4j.Logger;

import java.util.Date;

public class PaceMaker {

    static private Logger log = Logger.getLogger(PaceMaker.class);
        
    private long total_work_time = 0;
    private long num_work_intervals = 0;
    private long total_wait_time = 0;
    private long num_wait_intervals = 0;
    private long current_interval_start = 0;
    
    public PaceMaker() { }
    
    public void reset() {
        total_work_time = 0;
        num_work_intervals = 0;
        total_wait_time = 0;
        num_wait_intervals = 0;
        current_interval_start = 0;
    }
    
    public void startInterval() {
        try {            
            while (!shouldProcess()) {
                // wait a bit
                long average_work = Math.max(total_work_time / num_work_intervals, 1);
                if (log.isTraceEnabled()) log.trace("startInterval(): waiting for " + average_work + "ms");
                Thread.sleep(average_work);
                ++num_wait_intervals;
                total_wait_time += average_work;                
            }
            current_interval_start = new Date().getTime();
        } catch (Exception e) {
            log.error("startInterval(): error Exception", e);
        }
    }
    
    public void endInterval() {
        try {
            ++num_work_intervals;
            total_work_time += (new Date().getTime() - current_interval_start);            
        } catch (Exception e) {
            log.error("endInterval(): error Exception", e);
        }
    }
    
    private boolean shouldProcess() {
        int utilization = rapid_evolution.ui.OptionsUI.instance.cpuutilization.getValue();
        if (utilization == 10) return true;
        boolean process = true;
        if (total_work_time > 0) {
            float percent_work = ((float)total_work_time) / (total_work_time + total_wait_time);
            float work_percentage = ((float)utilization) / 10.0f;
            if (percent_work > work_percentage)
                process = false;
        }
        return process;
    }
    
}

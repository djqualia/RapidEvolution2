package com.mixshare.rapid_evolution.test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class RETestCase extends TestCase {

    private static Logger log = Logger.getLogger(RETestCase.class);
   
    protected void setUp() throws Exception {        
        super.setUp();
        try {
            PropertyConfigurator.configure("log4j.properties");
        } catch (Exception e) {
            log.error("setUp(): error Exception", e);
        }
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        try {            

        } catch (Exception e) {
            log.error("tearDown(): error Exception", e);
        }
    }
    
    public boolean areEqual(double val1, double val2) {
        return areEqual(val1, val2, 0.01);
    }

    public boolean areEqual(double val1, double val2, double precision) {
        return (Math.abs(val1 - val2) < precision);
    }
    
}

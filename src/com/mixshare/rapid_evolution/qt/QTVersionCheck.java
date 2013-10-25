package com.mixshare.rapid_evolution.qt;

import org.apache.log4j.Logger;

import quicktime.QTSession;
import quicktime.util.QTBuild;

public class QTVersionCheck {
  
    private static Logger log = Logger.getLogger(QTVersionCheck.class);
    
    static public String getQTVersion() {
        String result = "N/A";
        try {        	
            QTSession.open( );
            result = QTSession.getMajorVersion() +
                "." +
                QTSession.getMinorVersion();
            QTSession.close( );
        } catch (Exception e) {
            log.debug("getQTVersion(): error Exception", e);
        }        
        return result;
    }
    
    static public String getQTJVersion() {
        String result = "N/A";
        try {
            QTSession.open( );
            result = QTBuild.getVersion() +
                "." +
                QTBuild.getSubVersion();
            QTSession.close( );
        } catch (Exception e) {
            log.debug("getQTJVersion(): error Exception", e);
        }        
        return result;
    }

}

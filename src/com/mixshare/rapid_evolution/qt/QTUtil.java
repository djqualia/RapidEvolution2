package com.mixshare.rapid_evolution.qt;

import quicktime.QTSession;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.audio.codecs.decoders.QTAudioDecoder;

public class QTUtil {

    private static Logger log = Logger.getLogger(QTUtil.class);
    
    public static int maxSupportedFilenameSize = 63; // this excludes the directory part, and is currently a lame and unfortunate quicktime limitation
    
    static public boolean isQuickTimeSupported() {
        try {
            return !QTVersionCheck.getQTJVersion().equals("N/A");
        } catch (java.lang.Error e) {
            log.debug("isQuickTimeSupported(): error Exception", e);
        } catch (Exception e) {
            log.debug("isQuickTimeSupported(): error Exception", e);            
        }
        return false;
    }
    
    static public String getVersionString() {
        try {
            return "QT Version: " + QTVersionCheck.getQTVersion() + ", QTJ Version: " + QTVersionCheck.getQTJVersion();
        } catch (java.lang.Error e) {
            log.debug("getVersionString(): error Exception", e);
        } catch (Exception e) {
            log.debug("getVersionString(): error Exception", e);            
        }
        return null;
    }

    static public void closeQT() {
        try {
            QTSession.close();
        } catch (java.lang.Error e) {
            log.debug("closeQT(): error Exception", e);
        } catch (Exception e) {
            log.debug("closeQT(): error Exception", e);            
        }
    }
    
    static public double getTotalSeconds(String filename) {
        double result = 0.0;
        try {
            QTAudioDecoder decoder = new QTAudioDecoder(filename);
            result = decoder.getTotalSeconds();
            decoder.close();
        } catch (java.lang.Error e) {
            log.debug("getTotalSeconds(): error Exception", e);
        } catch (Exception e) {
            log.debug("getTotalSeconds(): error Exception", e);
        }        
        return result;
    }
    
}

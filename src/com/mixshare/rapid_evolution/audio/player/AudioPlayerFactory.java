package com.mixshare.rapid_evolution.audio.player;

import org.apache.log4j.Logger;
import com.mixshare.rapid_evolution.qt.QTUtil;

public class AudioPlayerFactory {

    static private Logger log = Logger.getLogger(AudioPlayerFactory.class);
    
    static public PlayerInterface getPlayer(String filename, PlayerCallBack callBack) {
        PlayerInterface result = null;
        try {
            if (QTUtil.isQuickTimeSupported()) {
                result = new QTPlayer();
                result.open(filename);
                if (!result.isFileSupported()) {
                    result.close();
                    result = null;
                }
            }
            if (result == null) {
                result = new JavaSoundPlayer();
                result.open(filename);                
            }
            if (result != null) {
                result.setCallBack(callBack);
            }
        } catch (Exception e) {
            log.error("getPlayer(): error Exception", e);
        }
        return result;
    }
    
}

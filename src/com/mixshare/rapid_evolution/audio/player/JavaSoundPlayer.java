package com.mixshare.rapid_evolution.audio.player;

import java.io.File;

import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.StringUtil;
import rapid_evolution.audio.AudioEngine;

import com.mixshare.rapid_evolution.audio.codecs.AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoderFactory;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;

import com.mixshare.rapid_evolution.io.FileLockManager;

public class JavaSoundPlayer implements PlayerInterface {

    private static Logger log = Logger.getLogger(JavaSoundPlayer.class);
    
    private AudioDecoder audioDecoder = null;
    private double currentSeconds;
    private PlayerThread playThread;
    private long bytesRead;
    private String oldTime;
    private PlayerCallBack callBack;
    private boolean stopFlag;
    private String filename;
    
    public boolean open(String filename) {
        if (log.isDebugEnabled()) log.debug("open(): filename=" + filename);
        this.filename = filename;        
        boolean success = false;
        try {
            File file = new File(filename);
            if (file.exists()) {
                FileLockManager.startFileRead(filename);            	
                audioDecoder = AudioDecoderFactory.getAudioDecoder(file.getAbsolutePath());   
                if (audioDecoder != null) {
                    currentSeconds = 0.0;
                    success = true;
                }
            }
        } catch (java.lang.Error e) {
            log.error("open(): error Exception", e);
        } catch (Exception e) {
            log.error("open(): error Exception", e);
        }
        return success;
    }
    
    public void close() {
    	try {
    		stop();
    		if (audioDecoder != null)
    			audioDecoder.close();
    	} catch (Exception e) {
    		log.error("close(): error", e);
    	}
        FileLockManager.endFileRead(filename);        
    }
    
    public boolean isFileSupported() {
        if (audioDecoder != null) {
            return audioDecoder.isFileSupported();
        }
        return false;
    }
    
    public void stop() {
        stopFlag = true;
        try {
            if (playThread != null) {
                while (playThread.isRunning())
                    Thread.sleep(100);
            }
        } catch (Exception e) {
            log.error("stop(): error Exception", e);
        }
        playThread = null;
    }
    
    public void start() {
        stopFlag = false;
        if (playThread == null) {
            playThread = new PlayerThread();
        }
        if (!playThread.isRunning())
            playThread.start();
    }
    
    public boolean isPlaying() {
        return ((playThread != null) && playThread.isRunning());
    }
    
    public void setCallBack(PlayerCallBack callBack) {
        this.callBack = callBack;
    }
    
    public double getTotalTime() {
        return audioDecoder.getTotalSeconds();
    }
    
    public void setPosition(double percentage) {
        try {
            boolean wasPlaying = isPlaying();
            stop();                
            double seconds = percentage * audioDecoder.getTotalSeconds();
            long newstartplayinglocation = (long)(seconds * audioDecoder.getSampleRate() * audioDecoder.getAudioFormat().getChannels() * (audioDecoder.getAudioFormat().getSampleSizeInBits() / 8));
            if (newstartplayinglocation < bytesRead) {
                stop();
                audioDecoder.reset();
                bytesRead = 0;
            }
            byte[] data = new byte[1024];
            long gained = 0;
            while (gained < newstartplayinglocation - bytesRead) {
                // TODO: try skip Bytes here! -> i think i did before...
                long skipped = audioDecoder.readBytes(data, 0, data.length);//             currentlyplaying_din.skip(1024);
                if (skipped == -1) break;
                gained += skipped;
            }
            bytesRead += gained;        
            if (wasPlaying)
                start();
        } catch (Exception e) {
            log.error("setPosition(): error Exception", e);
        }
    }
    
    
    private FloatControl volumeControl;
    public void setVolume(double percentage) {        
        if (volumeControl != null) {
            float range = volumeControl.getMaximum() - volumeControl.getMinimum();            
            volumeControl.setValue((float)(range * percentage + volumeControl.getMinimum()));
        }        
    }
        
    private class PlayerThread extends Thread {
        private boolean isRunning = false;
        public boolean isRunning() { return isRunning; }
        public void run() {
            try {
                isRunning = true;
                if (callBack != null)
                    callBack.setIsPlaying(true);                
                byte[] data = new byte[4096];
                SourceDataLine line = AudioEngine.instance.getLine(audioDecoder.getAudioFormat());
                if (log.isTraceEnabled()) {
                    Control[] controls = line.getControls();
                    if (controls != null) {
                        for (int c = 0; c < controls.length; ++c) {
                            log.trace("isRunning(): supported control=" + controls[c].toString());
                        }
                    }
                }
                volumeControl = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
                if (line != null) {
                    // Start
                    line.start();
                    int nBytesRead = 0;
                    int nBytesWritten = 0;
                    while ((nBytesRead != -1) &&
                            (!stopFlag) &&
                            (!RapidEvolution.instance.terminatesignal)) {
                        try {
                            nBytesRead = (int)audioDecoder.readBytes(data, 0, data.length);
                            if (nBytesRead != -1) {
                                nBytesWritten = line.write(data, 0, nBytesRead);
                                bytesRead += nBytesWritten;
                                currentSeconds = (((double) bytesRead) / audioDecoder.getSampleRate() / audioDecoder.getAudioFormat().getChannels() / (audioDecoder.getAudioFormat().getSampleSizeInBits() / 8));
                                if (callBack != null) {
                                    String newTime = StringUtil.seconds_to_time((int)currentSeconds);
                                    if (!newTime.equals(oldTime)) {
                                        callBack.setTime(newTime);
                                        oldTime = newTime;
                                    }                          
                                    double totalpercentage = currentSeconds / audioDecoder.getTotalSeconds();  
                                    callBack.setProgress(totalpercentage);
                                }                          
                            }
                        } catch (Exception e) {
                            log.error("PlayerThread(): error Exception", e);
                        }
                    }
                    // Stop
                    line.drain();
                    line.stop();
                    line.close();                        
                }
            } catch (Exception e) {
                log.error("PlayerThread(): error Exception", e);
            }
            if (callBack != null) {
                callBack.setIsPlaying(false);                
                if (!stopFlag)
                    callBack.donePlayingSong();    
            }
            isRunning = false;
        }        
    }
        
}

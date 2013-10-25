package com.mixshare.rapid_evolution.audio.codecs;

import java.io.File;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.audio.AudioFileType;
import com.mixshare.rapid_evolution.audio.codecs.decoders.DefaultAudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.decoders.FAAD2AudioDecoder;
import com.mixshare.rapid_evolution.qt.QTUtil;

public class AudioDecoderFactory {

    private static Logger log = Logger.getLogger(AudioDecoderFactory.class);
    
    public static AudioDecoder getAudioDecoder(String filename) {
        if (log.isDebugEnabled())
            log.debug("getAudioDecoder(): filename=" + filename);
        try {
            if (QTUtil.isQuickTimeSupported()) {
                return QTAudioDecoderFactory.getAudioDecoder(filename);
            }
        } catch (java.lang.Error e) { 
        } catch (Exception e) { }
        AudioDecoder decoder = null;
        try {
            File test_file = new File(filename);
            if (test_file.exists()) {                
                int audio_file_type = AudioFileType.getAudioFileType(filename);
                if ((audio_file_type == AudioFileType.MP4) || (audio_file_type == AudioFileType.AAC)) {
                    decoder = new FAAD2AudioDecoder(filename);
                } else {
                    decoder = new DefaultAudioDecoder(filename);
                }
            }            
        } catch (Exception e) {
            log.error("getAudioDecoder(): error Exception", e);
        }
        return decoder;
    }
    
}

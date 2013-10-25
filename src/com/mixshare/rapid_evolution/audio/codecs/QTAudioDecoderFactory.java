package com.mixshare.rapid_evolution.audio.codecs;

import java.io.File;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.audio.AudioFileType;
import com.mixshare.rapid_evolution.audio.codecs.decoders.DefaultAudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.decoders.FAAD2AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.decoders.QTAudioDecoder;
import com.mixshare.rapid_evolution.qt.QTUtil;

public class QTAudioDecoderFactory {

    private static Logger log = Logger.getLogger(QTAudioDecoderFactory.class);
    
    public static AudioDecoder getAudioDecoder(String filename) {
        if (log.isDebugEnabled())
            log.debug("getAudioDecoder(): filename=" + filename);
        AudioDecoder decoder = null;
        try {
            File test_file = new File(filename);
            if (test_file.exists()) {                
                int audio_file_type = AudioFileType.getAudioFileType(filename);
                if ((audio_file_type == AudioFileType.MP4) || (audio_file_type == AudioFileType.AAC)) {
                    if (QTUtil.isQuickTimeSupported()) {
                        decoder = new QTAudioDecoder(filename);
                        if (!decoder.isFileSupported()) {
                            decoder.close();
                            decoder = null;
                        }
                    }
                    if (decoder == null)
                        decoder = new FAAD2AudioDecoder(filename);
                } else {
                    try {
                        decoder = new DefaultAudioDecoder(filename);
                        if (!decoder.isFileSupported()) {
                            decoder.close();
                            decoder = null;
                        }
                    } catch (Exception e) {
                        log.debug("getAudioDecoder(): error Exception", e);                        
                    }
                    if (decoder == null) {
                        if (QTUtil.isQuickTimeSupported()) {
                            decoder = new QTAudioDecoder(filename);
                            if (!decoder.isFileSupported()) {
                                decoder.close();                            
                                decoder = null;
                            }
                        }
                    }
                }
            }            
        } catch (Exception e) {
            log.error("getAudioDecoder(): error Exception", e);
        }
        return decoder;
    }
    
}

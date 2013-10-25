package com.mixshare.rapid_evolution.audio.tags;

import java.io.File;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.audio.AudioFileType;
import com.mixshare.rapid_evolution.audio.tags.removers.mp3.JID3TagRemover;

public class TagRemoverFactory {

    private static Logger log = Logger.getLogger(TagRemoverFactory.class);
    
    public static TagRemover getTagRemover(String filename) {
        TagRemover remover = null;
        File test_file = new File(filename);
        if (test_file.exists()) {                
            int audio_file_type = AudioFileType.getAudioFileType(filename);
            if (audio_file_type == AudioFileType.MP3) {
                remover = new JID3TagRemover(filename);
            } else {
                if (log.isDebugEnabled()) log.debug("getTagRemover(): no remover available for filename=" + filename);
            }
        }
        return remover;
    }
    
}

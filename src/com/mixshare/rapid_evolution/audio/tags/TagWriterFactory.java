package com.mixshare.rapid_evolution.audio.tags;

import java.io.File;

import org.apache.log4j.Logger;

import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.audio.AudioFileType;
import com.mixshare.rapid_evolution.audio.tags.writers.EntaggedTagWriter;
import com.mixshare.rapid_evolution.audio.tags.writers.mp3.JAudioTagWriter;
import com.mixshare.rapid_evolution.audio.tags.writers.mp3.JID3TagWriter;
import com.mixshare.rapid_evolution.audio.tags.writers.mp4.JAudioMP4TagWriter;
import com.mixshare.rapid_evolution.util.cache.LRUCache;

public class TagWriterFactory implements TagConstants {

    private static Logger log = Logger.getLogger(TagWriterFactory.class);
    
    private static LRUCache writer_cache = new LRUCache(1);
    
    public static TagWriter getTagWriter(String filename) {
        String writer_key = String.valueOf(OptionsUI.instance.id3writer.getSelectedIndex()) + "///" + filename;
        TagWriter writer = (TagWriter)writer_cache.get(writer_key);
        if (writer != null) return writer;        
        int preferred_ID3_format = ID3_V_2_4;
        if (OptionsUI.instance.prefer_id3_v23.isSelected())
            preferred_ID3_format = ID3_V_2_3;        
        File test_file = new File(filename);
        if (test_file.exists()) {            
	        int audio_file_type = AudioFileType.getAudioFileType(filename);
	        // note: tag mode is not currently used by re2 or been tested...
	        if (audio_file_type == AudioFileType.MP3) {
                if (OptionsUI.instance.id3writer.getSelectedIndex() == 2)
                    writer = new JID3TagWriter(filename, TagConstants.TAG_MODE_UPDATE);                
                else
                    writer = new JAudioTagWriter(filename, TagConstants.TAG_MODE_UPDATE, preferred_ID3_format);
            } else if (audio_file_type == AudioFileType.MP4) {
                // TODO: test Jaudio with FLAC files?
                writer = new JAudioMP4TagWriter(filename, TagConstants.TAG_MODE_UPDATE);                
//            } else if ((audio_file_type == AudioFileType.APE)) {
//                writer = new JmacAPETagWriter(filename);
	        } else if ((audio_file_type == AudioFileType.FLAC) ||
	                   (audio_file_type == AudioFileType.OGG) ||
	                   (audio_file_type == AudioFileType.MPC) ||
                       (audio_file_type == AudioFileType.APE) ||
	                   (audio_file_type == AudioFileType.MP_PLUS) ||
	                   (audio_file_type == AudioFileType.WMA)) {	            
	            writer = new EntaggedTagWriter(filename, TagConstants.TAG_MODE_UPDATE);
	        } else {
	            if (log.isDebugEnabled()) log.debug("getTagWriter(): no writer available for filename=" + filename);
	        }
        }
        if (writer != null) writer_cache.add(writer_key, writer);
        return writer;
    }
    
}

package com.mixshare.rapid_evolution.audio.tags;

import java.io.File;

import org.apache.log4j.Logger;

import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.audio.AudioFileType;
import com.mixshare.rapid_evolution.audio.tags.readers.DefaultTagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.EntaggedTagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.ape.JmacAPETagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.mp3.JAudioTagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.mp3.JID3TagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.mp3.QTID3TagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.mp4.JAudioMP4TagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.mp4.QTAACTagReader;

public class TagReaderFactory {

    private static Logger log = Logger.getLogger(TagReaderFactory.class);
    
    // TODO: add isTagFound() method to readers, that way if not found it can check other libraries...
    // TODO: add separate options for tag readers/writers as well as different types.
    public static TagReader getTagReader(String filename) {
        TagReader reader = null;
        File test_file = new File(filename);
        if (test_file.exists()) {                
            int audio_file_type = AudioFileType.getAudioFileType(filename);
            if (audio_file_type == AudioFileType.MP3) {
                if (OptionsUI.instance.id3reader.getSelectedIndex() == 2) {
                    reader = new JID3TagReader(filename);
                    if (!reader.isFileSupported())
                        reader = null;
                } else if (OptionsUI.instance.id3reader.getSelectedIndex() == 3) {
                    reader = new QTID3TagReader(filename);
                    if (!reader.isFileSupported())
                        reader = null;
                }
                if (reader == null)
                    reader = new JAudioTagReader(filename);
            } else if ((audio_file_type == AudioFileType.APE)) {
                reader = new JmacAPETagReader(filename);
            } else if ((audio_file_type == AudioFileType.FLAC) ||
                       (audio_file_type == AudioFileType.OGG) ||
	                   (audio_file_type == AudioFileType.MPC) ||
                       (audio_file_type == AudioFileType.MP_PLUS) ||                       
//	                   (audio_file_type == AudioFileType.APE) ||
	                   (audio_file_type == AudioFileType.WMA)) {
                reader = new EntaggedTagReader(filename);
	        } else if ((audio_file_type == AudioFileType.AAC) || (audio_file_type == AudioFileType.MP4)) {
	            if (OptionsUI.instance.id3reader.getSelectedIndex() == 3) {
                    reader = new QTAACTagReader(filename);
                    if (!reader.isFileSupported())
                        reader = null;
                }
	            if (reader == null)
	                reader = new JAudioMP4TagReader(filename);
                // TODO: should JavaID3TagReader or DefaultMP4TagReader be used for AAC files as default as it did previously?
            } else {
                if (log.isDebugEnabled()) log.debug("getTagReader(): no specific reader available for filename=" + filename);
                reader = new DefaultTagReader(filename);
            }
        }
        return reader;
    }
    
}

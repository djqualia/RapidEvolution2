package com.mixshare.rapid_evolution.audio.tags.readers;

import rapid_evolution.StringUtil;
import rapid_evolution.audio.AudioLib;


public abstract class BaseTagReader extends AbstractTagReader {
        
    public boolean isFileSupported() { return false; }    
    public String getAlbum() { return null; }
    public String getAlbumCoverFilename() { return null; }
    public String getArtist() { return null; }
    public Integer getBeatIntensity() { return null; }    
    public Integer getBpmAccuracy() { return null; }
    public Float getBpmStart() { return null; }
    public Float getBpmEnd() { return null; }
    public String getCatalogId() { return null; }    
    public String getComments() { return null; }
    public String getContentGroupDescription() { return null; }
    public String getContentType() { return null; }
    public String getEncodedBy() { return null; }
    public String getFilename() { return null; }    
    public String getFileType() { return null; }
    public String getGenre() { return null; }
    public Integer getKeyAccuracy() { return null; }
    public String getKeyStart() { return null; }
    public String getKeyEnd() { return null; }
    public String getLanguages() { return null; }
    public String getLyrics() { return null; }    
    public String getPublisher() { return null; }
    public Integer getRating() { return null; }
    public String getRemix() { return null; }
    public Float getReplayGain() { return null; }
    public Integer getSizeInBytes() { return null; }
    public String[] getStyles() { return null; }
    public String getTime() {
        double seconds = AudioLib.get_track_time(getFilename());
        if (seconds != 0.0) {
            return StringUtil.seconds_to_time((int)Math.max(seconds, 1.0));
        }
        return "";        
    }
    public String getTimeSignature() { return null; }
    public String getTitle() { return null; }
    public String getTrack() { return null; }
    public Integer getTotalTracks() { return null; }
    public String getUser1() { return null; } 
    public String getUser2() { return null; } 
    public String getUser3() { return null; } 
    public String getUser4() { return null; }
    public String getYear() { return null; }
    
}

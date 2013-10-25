package com.mixshare.rapid_evolution.audio.tags;

public interface TagReader {

    public boolean isFileSupported();
    public String getAlbum(); 
    public String getAlbumCoverFilename();
    public String getArtist();
    public Integer getBeatIntensity();
    public Integer getBpmAccuracy();
    public Float getBpmStart();
    public Float getBpmEnd();
    public String getCatalogId();
    public String getComments();
    public String getContentGroupDescription();
    public String getContentType();
    public String getEncodedBy();
    public String getFilename();
    public String getFileType();
    public String getGenre();
    public Integer getKeyAccuracy();
    public String getKeyStart();
    public String getKeyEnd();
    public String getLanguages();
    public String getLyrics();
    public String getPublisher();
    public Integer getRating();
    public String getRemix();
    public Float getReplayGain();
    public Integer getSizeInBytes();
    public String[] getStyles();
    public String getTime();
    public String getTimeSignature();
    public String getTitle();
    public String getTrack();
    public Integer getTotalTracks();
    public String getUser1();
    public String getUser2();
    public String getUser3();
    public String getUser4();
    public String getYear();
    
}

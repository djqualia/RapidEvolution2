package com.mixshare.rapid_evolution.audio.player;

public interface PlayerCallBack {

    /**
     * Where value is like "4:20"
     */
    public void setTime(String value);
    
    /**
     * Where value is between 0 and 1
     */
    public void setProgress(double value);
    
    public void donePlayingSong();
    
    public void setIsPlaying(boolean playing);
    
}

package com.mixshare.rapid_evolution.audio;


public class AudioFileType {

    public static int NONE = 0;
    public static int MP3 = 1;
    public static int OGG = 2;
    public static int FLAC = 3;
    public static int WAV = 4;
    public static int AIF = 5;
    public static int WMA = 6;
    public static int APE = 7;
    public static int MP_PLUS = 8;
    public static int MPC = 9;
    public static int GSM = 10;
    public static int MP4 = 11;
    public static int AAC = 12;
    
    public static int getAudioFileType(String filename) {
        if (filename != null) {
            String lc_filename = filename.toLowerCase();
            if (lc_filename.endsWith(".mp3")) return MP3;
            if (lc_filename.endsWith(".ogg")) return OGG;
            if (lc_filename.endsWith(".flac")) return FLAC;
            if (lc_filename.endsWith(".wav")) return WAV;
            if (lc_filename.endsWith(".aif")) return AIF;
            if (lc_filename.endsWith(".aiff")) return AIF;
            if (lc_filename.endsWith(".mpc")) return MPC;
            if (lc_filename.endsWith(".mp+")) return MP_PLUS;
            if (lc_filename.endsWith(".ape")) return APE;
            if (lc_filename.endsWith(".wma")) return WMA;
            if (lc_filename.endsWith(".gsm")) return GSM;
            if (lc_filename.endsWith(".mp4")) return MP4;
            if (lc_filename.endsWith(".m4p")) return MP4;
            if (lc_filename.endsWith(".m4a")) return MP4;
            if (lc_filename.endsWith(".aac")) return AAC;
        }
        return NONE;
    }
    
    public static String[] getAllAudioExtensions() {
        return new String[] { ".mp3", ".ogg", ".flac", ".wav", ".aif", ".aiff", ".mpc", ".mp+", ".ape", ".ape",
                ".wma", ".gsm", ".mp4", "m4a", "m4p", "aac" };
    }
        
}

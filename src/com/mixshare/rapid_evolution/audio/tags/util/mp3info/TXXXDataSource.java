package com.mixshare.rapid_evolution.audio.tags.util.mp3info;

import de.ueberdosis.mp3info.id3v2.DataSource;

public class TXXXDataSource implements DataSource {

    private byte[] data = null;
    private long counter = 0;
    
    public TXXXDataSource(String description, String value) {
        data = new byte[description.length() + value.length() + 2];
        int count = 0;
        data[count++] = 0;
        for (int i = 0; i < description.length(); ++i)
            data[count++] = (byte)description.charAt(i);
        data[count++] = 0;
        for (int i = 0; i < value.length(); ++i)
            data[count++] = (byte)value.charAt(i);        
    }
    
    public byte getByte(long val) {
        return data[(int)val];
    }
    
    public byte getByte() {
        int c = (int)(counter++);
        if (c >= data.length)
            return 0;
        return data[c];
    }
    
    public byte[] getBytes(long val) {
        if (val == -1)
            val = data.length;
        byte[] returnval = new byte[(int)val];
        for (int i = 0; i < val; ++i)
            returnval[i] = data[(int)(counter++)];
        return returnval;
    }
    
    public byte[] getBytesTo(byte val) {
        byte[] returnval = new byte[(int)val];
        for (int i = 0; i < val; ++i)
            returnval[i] = data[i];
        return returnval;
    }
    
    public void reset() {
        counter = 0;
    }
    
    public boolean hasMoreBytes() {
        return (counter < data.length);
    }
    
    public long getBytesLeft() {
        return Math.max(data.length - counter, 0);
    }
    
    public void seek(long val) {
        counter = val;
    }
    
  }

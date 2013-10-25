package com.mixshare.rapid_evolution.audio.tags.util.mp3info;

import de.ueberdosis.mp3info.id3v2.DataSource;

public class TextDataSource implements DataSource {
  public TextDataSource(String input) { data = new byte[input.length()]; for (int i = 0; i < input.length(); ++i) data[i] = (byte)input.charAt(i); }
  public byte getByte(long val) { return data[(int)val]; }
  public byte getByte() { int c = (int)counter; counter++; if (c >= data.length) return 0; return data[c]; }
  public byte[] getBytes(long val) { if (val == -1) val = data.length; byte[] returnval = new byte[(int)val]; for (int i = 0; i < val; ++i) returnval[i] = data[i]; return returnval; }
  public byte[] getBytesTo(byte val) { byte[] returnval = new byte[(int)val]; for (int i = 0; i < val; ++i) returnval[i] = data[i]; return returnval; }
  public void reset() { counter = 0; };
  public boolean hasMoreBytes() { return (counter < data.length); }
  public long getBytesLeft() { return (data.length - counter); }
  public void seek(long val) { counter = val; }
  public byte[] data;
  long counter = 0;
}

package rapid_evolution.filefilters;

import rapid_evolution.RapidEvolution;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class FileFormats {
  public final static String wav = "wav";
  public final static String mp3 = "mp3";
  public final static String gsm = "gsm";
  public final static String mp4 = "mp4";
  public final static String m4a = "m4a";
  public final static String aac = "aac";
  public final static String ogg = "ogg";
  public final static String aifc = "aifc";
  public final static String aiff = "aiff";
  public final static String aif = "aif";
  public final static String au = "au";
  public final static String snd = "snd";
  public final static String flac = "flac";
  public final static String wma = "wma";
  public final static String ape = "ape";
  public final static String mac = "mac";
  public final static String apl = "apl";
  public final static String mpc = "mpc";
  public final static String mp_plus = "mp+";
  public final static String[] allformats = { wav, mp3, gsm, ogg, aifc, aiff, aif, au, snd, mp4, m4a, aac, flac, wma, ape, apl, mac, mp3, mp_plus };
  public static boolean acceptsFile(String filename) {
    for (int i = 0; i < FileFormats.allformats.length; ++i) {
      if (filename.toLowerCase().endsWith(FileFormats.allformats[i])) return true;
    }
    return false;
  }
  
}

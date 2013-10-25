package rapid_evolution;

import java.util.*;

import org.apache.log4j.Logger;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
public class Artist {

    private static Logger log = Logger.getLogger(Artist.class);

  static public String encodeString(String text) {
      StringBuffer encoded = new StringBuffer();
      for (int iter = 0; iter < text.length(); ++iter) {
          char character = text.charAt(iter);
          if (Character.isLetterOrDigit(character))
              encoded.append(character);
      }
      return encoded.toString().toLowerCase();
  }
  
  public Artist(SongLinkedList song) {
      name = encodeString(song.getArtist());
      songs.put(new Long(song.uniquesongid), new Long(song.uniquesongid));    
  }
  public Artist(String _name, HashMap _songs, HashMap _stylescount, boolean _stylescached, HashMap _profile) {
    name = _name;
    songs = _songs;
    stylescount = _stylescount;
    stylescached = _stylescached;
    profile = _profile;
  }

  String name = new String("");
  HashMap songs = new HashMap();

  HashMap stylescount = new HashMap();
  boolean stylescached = false;

  HashMap profile = new HashMap();

  void ComputeStyles() {
      try {
          stylescount.clear();
          Collection songcollection = songs.values();
          if (songcollection != null) {
	          Iterator iter = songcollection.iterator();
	          while (iter.hasNext()) {
	              SongLinkedList song = SongDB.instance.NewGetSongPtr(((Long)iter.next()).longValue());
	              if (song != null) {
	                  String[] styles = song.getStyleStrings();
	                  for (int j = 0; j < styles.length; ++j) {
	                      String style = styles[j];
	                      boolean found = false;                      
	                      if (stylescount.containsKey(style)) {
	                          int val = ((Integer)stylescount.get(style)).intValue();
	                          stylescount.put(style, new Integer(val + 1));                          
	                      } else {
	                          stylescount.put(style, new Integer(1));
	                      }
	                  }
	              }
	          }
          }          
      } catch (Exception e) { log.error("ComputeStyles(): error", e); }
  }

  public HashMap getStyleProfile() {
    if (stylescached) return profile;
    ComputeStyles();
    profile.clear();
    double totalweight = 0.0;
    Collection coll = stylescount.entrySet();
    if (coll != null) {
	    Iterator iter = coll.iterator();
	    while (iter.hasNext()) {
	        Map.Entry entry = (Map.Entry)iter.next();        
	        double dblval = (((double)(((Integer)entry.getValue()).intValue())) / ((double)songs.size()));
	        profile.put(entry.getKey(), new Double(dblval));
	        totalweight += dblval;
	    }
	    stylescached = true;	    
    }    
    return profile;
  }

  double getStyleSimilarity(Artist artist) {
    HashMap profile1 = getStyleProfile();
    HashMap profile2 = artist.getStyleProfile();
    HashMap uniquestyles = new HashMap();
    Iterator styles = profile1.keySet().iterator();
    while (styles.hasNext()) {
        String stylename = (String)styles.next();
        uniquestyles.put(stylename, stylename);
    }
    boolean at_least_one_in_common = false;
    styles = profile2.keySet().iterator();
    while (styles.hasNext()) {
        String stylename = (String)styles.next();
        if (!uniquestyles.containsKey(stylename)) {
            uniquestyles.put(stylename, stylename);
        } else {
            at_least_one_in_common = true;
        }
    }   
    if (uniquestyles.size() == 0) return 0.0;
    if (!at_least_one_in_common) return 0.0;
    double numerator = 0.0;
    double denom = 0.0;
    Iterator unique_iter = uniquestyles.values().iterator();
    while (unique_iter.hasNext()) {
      String ustyle = (String)unique_iter.next();
      boolean found = false;
      Iterator p1_iter = profile1.entrySet().iterator();
      while (!found && p1_iter.hasNext()) {
          Map.Entry p1 = (Map.Entry)p1_iter.next();
        String style = (String)p1.getKey();
        if (style.equals(ustyle)) {
          double val = ((Double)p1.getValue()).doubleValue();
          Iterator p2_iter = profile2.entrySet().iterator();
          while (!found && p2_iter.hasNext()) {
              Map.Entry p2 = (Map.Entry)p2_iter.next();
            style = (String)p2.getKey();
            if (style.equals(ustyle)) {
              double val2 = ((Double)p2.getValue()).doubleValue() * val;
              numerator += val2;
              denom += val2;
              found = true;
            }
          }
          if (!found) denom += val;
          found = true;
        }
      }
      if (!found) {
          Iterator p2_iter = profile2.entrySet().iterator();
          while (!found && p2_iter.hasNext()) {
              Map.Entry p2 = (Map.Entry)p2_iter.next();
          String style = (String)p2.getKey();
          if (style.equals(ustyle)) {
            double val2 = ((Double)p2.getValue()).doubleValue();
            denom += val2;
            found = true;
          }
        }
      }
    }
    return numerator / denom;
  }

  public void addSong(SongLinkedList song) {
    try {
        if (!songs.containsKey(new Long(song.uniquesongid))) {
            songs.put(new Long(song.uniquesongid), new Long(song.uniquesongid));
            stylescached = false;
        }
    } catch (Exception e) { log.error("addSong(): error", e); }
  }

  public boolean removeSong(long songid) {
    boolean found = false;
    try {
        if (songs.containsKey(new Long(songid))) {
            found = true;
            songs.remove(new Long(songid));
            stylescached = false;
        }
    } catch (Exception e) { log.error("removeSong(): error", e); }
    return found;
  }

  public void removeSong(SongLinkedList song) {
      removeSong(song.uniquesongid);
  }
  
  String getSearchName() {
    return name;
  }

  static public void InsertToArtistList(SongLinkedList song) {
      String searchartist = encodeString(song.getArtist());
      if (searchartist.equals("")) return;
    try {
      masterartistlistsem.acquire();
      if (!masterartistlist.containsKey(searchartist)) {
          Artist artist = new Artist(song);
          masterartistlist.put(searchartist, artist);
      } else {
          Artist artist = (Artist)masterartistlist.get(searchartist);
          artist.addSong(song);          
      }
    } catch (Exception e) { log.error("InsertToArtistList(): error", e); }
    masterartistlistsem.release();
  }

  static public void RemoveSongFromArtistList(SongLinkedList song) {
      String searchartist = encodeString(song.getArtist());
      if (searchartist.equals("")) return;

      try {
          masterartistlistsem.acquire();

          if (masterartistlist.containsKey(searchartist)) {
              Artist artist = (Artist)masterartistlist.get(searchartist);
              artist.removeSong(song);
              if (artist.songs.size() == 0)
                  masterartistlist.remove(searchartist);          
          }
                   
      } catch (Exception e) { log.error("RemoveSongFromArtistList(): error", e); }
      masterartistlistsem.release();
  }

  static private Semaphore masterartistlistsem = new Semaphore(1);
  static public HashMap masterartistlist = new HashMap();
}

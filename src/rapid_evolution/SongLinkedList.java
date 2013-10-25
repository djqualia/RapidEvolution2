package rapid_evolution;

import java.awt.Color;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import rapid_evolution.comparables.MyCents;
import rapid_evolution.comparables.MyInteger;
import rapid_evolution.comparables.MyString;
import rapid_evolution.comparables.MyStringFloat;
import rapid_evolution.comparables.MyTrackString;
import rapid_evolution.comparables.MyUserObject;
import rapid_evolution.comparables.myBoolean;
import rapid_evolution.comparables.myBpmObject;
import rapid_evolution.comparables.myColorFloat;
import rapid_evolution.comparables.myFloat;
import rapid_evolution.comparables.myImageIcon;
import rapid_evolution.comparables.myLength;
import rapid_evolution.comparables.myPercentageFloat;
import rapid_evolution.comparables.myPercentageInteger;
import rapid_evolution.ui.ColumnConfig;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RatingToolBar;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.util.OSHelper;

import com.mixshare.rapid_evolution.audio.dsps.rga.RGAData;
import com.mixshare.rapid_evolution.data.index.IndexItem;
import com.mixshare.rapid_evolution.music.Bpm;
import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.music.KeyRelation;
import com.mixshare.rapid_evolution.music.SongKey;
import com.mixshare.rapid_evolution.music.SongKeyRelation;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class SongLinkedList implements IndexItem {

    private static Logger log = Logger.getLogger(SongLinkedList.class);
      
    public Boolean iscompilation = null;
    public String[] stylelist = null;
    public boolean logicalstyleidscached = false;
    boolean[] _logicalstyleids = null;
    private myBpmObject bpm = null;
    private MyString dateadded = null;
    private myPercentageInteger bpm_accuracy = myPercentageInteger.default_value;
    private myPercentageInteger key_accuracy = myPercentageInteger.default_value;
    public int pendingEditRequests = 0;
    private Integer times_played = new Integer(0);
    private static Semaphore getUniqueIDSem = new Semaphore(1);
    public myBoolean hasAlbumCover = null;
    private SongKey songKey = new SongKey(Key.NO_KEY, Key.NO_KEY);
    private MyString processed_comments = null;
    private myBoolean analog = null;
    private myBoolean digital = null;
    private Integer nummixouts = null;
    private Integer numaddons = null;
    boolean stylestringscached = false;
    String[] _stylestrings = null;
    private MyInteger beat_intensity = MyInteger.default_value;
    private MyString rating = new MyString("");
    private RatingToolBar ratingbar = new RatingToolBar(this);
    private MyString allstylesstring = null;
    private MyString _artist = new MyString("");
    private MyString _album = new MyString("");
    private MyTrackString _track = MyTrackString.default_value;
    private MyString _songname = new MyString("");
    private MyString _remixer = new MyString("");
    private String _comments = "";
    private boolean vinylonlybit = false;
    private boolean nonvinylonlybit = false;
    private myBoolean disabled = myBoolean.default_value;
    private myFloat _bpm = new myFloat(0);
    private myFloat _endbpm = new myFloat(0);
    public long[] mixout_songs = new long[0];
    private String[] mixout_songnames = null;
    float[] _mixout_bpmdiff = null;
    public boolean[] mixout_servercache = null;
    String[] _mixout_comments = null;
    int[] _mixout_ranks = null;
    boolean[] _mixout_addons = null;
    public long[] exclude_songs = new long[0];
    private String[] exclude_songnames = null;
    public static int minuniqueid = 1;
    public static boolean checkuniqueid = false;
    public int uniquesongid = 0;
    public String uniquestringid = new String("");
    public SongLinkedList next = null;
    private MyUserObject user1 = MyUserObject.default1_value;
    private MyUserObject user2 = MyUserObject.default2_value;
    private MyUserObject user3 = MyUserObject.default3_value;
    private MyUserObject user4 = MyUserObject.default4_value;
    private MyString filename = new MyString("");
    private myLength _tracktime = myLength.default_value;
    private MyString _timesig = new MyString("4/4");
    public SongColor color = null;
    public boolean servercached;
    boolean[] _styles = null;
    public String itunes_id = null;
    private MyString lastmodified = new MyString("");
    
  public SongLinkedList() {
    servercached = false;
    _styles = new boolean[SongDB.instance.num_styles];
    //for (int h = 0; h < SongDB.instance.num_styles; ++h) _styles[h] = false;
    dateadded = getStringDateFormat(new java.util.Date());
  }
    
  public SongLinkedList(SongLinkedList iter) {
    _artist = new MyString(iter.getArtist());
    _album = new MyString(iter.getAlbum());
    _track = new MyTrackString(iter.getTrack());
    _songname = new MyString(iter.getSongname());
    _comments = new String(iter.getComments());
    _remixer = new MyString(iter.getRemixer());
    setUser1(new String(iter.getUser1()));
    setUser2(new String(iter.getUser2()));
    setUser3(new String(iter.getUser3()));
    setUser4(new String(iter.getUser4()));
    vinylonlybit = iter.vinylonlybit;
    nonvinylonlybit = iter.nonvinylonlybit;
    _bpm = new myFloat(iter.getStartbpm());
    _endbpm = new myFloat(iter.getEndbpm());
    beat_intensity = new MyInteger(iter.getBeatIntensity());
    //mixout_songs = null; //new String[num_mixouts];
    mixout_songnames = null;
    exclude_songnames = null;
    _mixout_addons = null; //new boolean[num_mixouts];
    _mixout_bpmdiff = null; //new float[num_mixouts];
    _mixout_comments = null; //new String[num_mixouts];
    _mixout_ranks = null; //new int[num_mixouts];
    servercached = iter.servercached;
    //exclude_songs = null; //new String[num_excludes];
    _styles = new boolean[SongDB.instance.num_styles];
    mixout_servercache = null; //new boolean[num_mixouts];
    for (int h = 0; h < SongDB.instance.num_styles; ++h) _styles[h] = iter.getStyle(h);
    next = null;
    shortid = null;
    shortidwinfo = null;
    uniquesongid = iter.uniquesongid;
    filename = iter.filename;
    songKey.setStartKey(iter.getStartKey());
    songKey.setEndKey(iter.getEndKey());
    _tracktime = new myLength(iter.getTime());
    _timesig = new MyString(iter.getTimesig());
    disabled = iter.disabled;
    calculate_unique_id();
    dateadded = iter.dateadded;
    lastmodified = iter.lastmodified;
    itunes_id = iter.itunes_id;
    _songid = new MyString(song_to_string(this));
    init();
  }
  public SongLinkedList(int uniqueid, String artistname, String albumname, String trackcode, String songtitle, String songremixer, String songcomments, boolean vinylbit, boolean nonvinyl, float avgbpm, float endingbpm, int nmixouts, int nexcludes, Key nstartkey, Key nendkey, String nfilename, String ntracktime, String ntimesig, boolean ndisabled, boolean nservercached, String suser1, String suser2, String suser3, String suser4, SongLinkedList nextptr, String[] instyles) {
    setInitialFields(uniqueid, artistname, albumname, trackcode, songtitle, songremixer, songcomments, vinylbit, nonvinyl,  avgbpm,  endingbpm,  nmixouts,  nexcludes,  nstartkey,  nendkey,  nfilename,  ntracktime,  ntimesig,  ndisabled,  nservercached,  suser1,  suser2,  suser3, suser4, nextptr);
    stylelist = instyles;
    init();
  }
  void setInitialFields(int uniqueid, String artistname, String albumname, String trackcode, String songtitle, String songremixer, String songcomments, boolean vinylbit, boolean nonvinyl, float avgbpm, float endingbpm, int nmixouts, int nexcludes, Key nstartkey, Key nendkey, String nfilename, String ntracktime, String ntimesig, boolean ndisabled, boolean nservercached, String suser1, String suser2, String suser3, String suser4, SongLinkedList nextptr) {
    uniquesongid = uniqueid;
    _artist = new MyString(artistname);
    _album = new MyString(albumname);
    _track = new MyTrackString(trackcode);
    _songname = new MyString(songtitle);
    _comments = new String(songcomments);
    _remixer = new MyString(songremixer);
    setUser1(suser1);
    setUser2(suser2);
    setUser3(suser3);
    setUser4(suser4);
    vinylonlybit = vinylbit;
    nonvinylonlybit = nonvinyl;
    _bpm = new myFloat(avgbpm);
    _endbpm = new myFloat(endingbpm);
    mixout_songs = new long[nmixouts];
    mixout_songnames = new String[nmixouts];
    _mixout_addons = new boolean[nmixouts];
    _mixout_bpmdiff = new float[nmixouts];
    _mixout_comments = new String[nmixouts];
    _mixout_ranks = new int[nmixouts];
    servercached = nservercached;
    exclude_songs = new long[nexcludes];
    exclude_songnames = new String[nexcludes];
    _styles = new boolean[SongDB.instance.num_styles];
    mixout_servercache = new boolean[nmixouts];
    for (int h = 0; h < SongDB.instance.num_styles; ++h) _styles[h] = false;
    next = nextptr;
    shortid = null;
    shortidwinfo = null;
    filename = new MyString(nfilename);
    songKey.setStartKey(nstartkey);
    songKey.setEndKey(nendkey);
    _tracktime = new myLength(ntracktime);
    _timesig = new MyString(ntimesig);
    if (_timesig.toString().equals("")) _timesig = new MyString("4/4");
    disabled = new myBoolean(ndisabled);
    calculate_unique_id();
    dateadded = getStringDateFormat(new java.util.Date());
    _songid = new MyString(song_to_string(this));
    }
  public SongLinkedList(int uniqueid, String artistname, String albumname, String trackcode, String songtitle, String songremixer, String songcomments, boolean vinylbit, boolean nonvinyl, float avgbpm, float endingbpm, int nmixouts, int nexcludes, Key nstartkey, Key nendkey, String nfilename, String ntracktime, String ntimesig, boolean ndisabled, boolean nservercached, String suser1, String suser2, String suser3, String suser4, SongLinkedList nextptr) {
    setInitialFields(uniqueid, artistname, albumname, trackcode, songtitle, songremixer, songcomments, vinylbit, nonvinyl,  avgbpm,  endingbpm,  nmixouts,  nexcludes,  nstartkey,  nendkey,  nfilename,  ntracktime,  ntimesig,  ndisabled,  nservercached,  suser1,  suser2,  suser3, suser4, nextptr);
    init();
  }
  
  private void init() {
  }

//    public SongLinkedList(String m_artist, String m_album, String m_track, String m_songname, String comments, boolean vinylbit, boolean nonvinyl, float m_startbpm, float m_endbpm, String nstartkey, String nendkey, String nfilename, String ntracktime, String timesig, boolean ndisabled, SongLinkedList nextptr) { }
//    public SongLinkedList(SongLinkedList song, SongLinkedList nextptr) { }
//          ~SongLinkedList();

  public String toString() { return song_to_string(this); }

  public int compareTo(SongLinkedList b) {
    return (getSongIdShort().compareToIgnoreCase(b.getSongIdShort()));
  }

  Semaphore insertMixoutSem = new Semaphore(1);
  Semaphore insertExcludeSem = new Semaphore(1);

  public void insertMixOut(SongLinkedList songptr, String comments, float bpmdiff, int score, boolean addon) {
      int num_mixouts = mixout_songs.length;
      for (int i = 0; i < num_mixouts; ++i) if (songptr.uniquesongid == mixout_songs[i]) return;
    try {
    insertMixoutSem.acquire();
    ++num_mixouts;
    String[] new_mixout_songnames = new String[num_mixouts];
    long[] new_mixout_songs = new long[num_mixouts];
    float[] new_mixout_bpmdiff = new float[num_mixouts];
    String[] new_mixout_comments = new String[num_mixouts];
    int[] new_mixout_ranks = new int[num_mixouts];
    boolean[] new_mixout_addons = new boolean[num_mixouts];
    boolean[] new_mixout_servercache = new boolean[num_mixouts];
    for (int i = 0; i < num_mixouts - 1; ++i) {
      new_mixout_songs[i] = mixout_songs[i];
      new_mixout_addons[i] = _mixout_addons[i];
      new_mixout_bpmdiff[i] = _mixout_bpmdiff[i];
      new_mixout_comments[i] = _mixout_comments[i];
      new_mixout_ranks[i] = _mixout_ranks[i];
      new_mixout_servercache[i] = mixout_servercache[i];
      new_mixout_songnames[i] = mixout_songnames[i];
    }
    mixout_songnames = new_mixout_songnames;
    mixout_songs = new_mixout_songs;
    _mixout_bpmdiff = new_mixout_bpmdiff;
    _mixout_comments = new_mixout_comments;
    _mixout_addons = new_mixout_addons;
    _mixout_ranks = new_mixout_ranks;
    mixout_servercache = new_mixout_servercache;
    mixout_songs[num_mixouts - 1] = songptr.uniquesongid;
    mixout_songnames[num_mixouts - 1] = songptr.uniquestringid;
    _mixout_ranks[num_mixouts - 1] = score;
    _mixout_bpmdiff[num_mixouts - 1] = bpmdiff;
    _mixout_addons[num_mixouts - 1] = addon;
    _mixout_comments[num_mixouts - 1] = new String(comments);
    mixout_servercache[num_mixouts - 1] = false;
    setSongId(song_to_string(this));
    setShortIdWInfo(song_to_shortidwinfo(this));
    sort_mixouts();
//      if (connectedtoserver) {
//          for (int i = 0; i < num_mixouts; ++i) {
//              if (mixout_songs[i].equals(songptr.uniquesongid)) new UpdateServerThread(this, i).start();
//          }
//      }
    if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
    } catch (Exception e) { }
    nummixouts = null;
    numaddons = null;
    insertMixoutSem.release();
  }

  public void setMixoutDetails(SongLinkedList tosong, String comments, float bpmdiff, int rank, boolean addon) {
    try {
      insertMixoutSem.acquire();
      ++SearchPane.instance.dontfiretablechange;
      int num_mixouts = mixout_songs.length;
      for (int i = 0; i < num_mixouts; ++i) {
        if (mixout_songs[i] == tosong.uniquesongid) {
          setMixoutComments(i, comments);
          setMixoutBpmdiff(i, bpmdiff);
          setMixoutRank(i, rank);
          setMixoutAddon(i, addon);
          if (this == RapidEvolutionUI.instance.currentsong) {
            for (int r = 0; r < MixoutPane.instance.mixouttable.getRowCount(); ++r) {
              SongLinkedList song = (SongLinkedList)MixoutPane.instance.mixouttable.getValueAt(r, MixoutPane.instance.mixoutcolumnconfig.num_columns);
              if (log.isTraceEnabled()) log.trace("setMixoutDetails(): row=" + r + ", song=" + song);
              if (song.uniquesongid == tosong.uniquesongid) {
                  if (log.isTraceEnabled()) log.trace("setMixoutDetails(): settomg details on row=" + r);
                for (int j = 0; j < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++j) {
                  MixoutPane.instance.mixouttable.getModel().setValueAt(tosong.get_column_mixdata(MixoutPane.instance.mixoutcolumnconfig.columnindex[j]), r, j);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) { log.error("setMixoutDetails(): error", e); }
    insertMixoutSem.release();
    --SearchPane.instance.dontfiretablechange;
  }

  public void insertExclude(SongLinkedList songptr) {
    try {
        int num_excludes = exclude_songs.length;
    for (int z = 0; z < num_excludes; ++z) if (songptr.uniquesongid == exclude_songs[z]) return;
    insertExcludeSem.acquire();
    ++num_excludes;
    long[] new_exclude_songs = new long[num_excludes];
    String[] new_exclude_songnames = new String[num_excludes];
    for (int i = 0; i < num_excludes - 1; ++i) {
      new_exclude_songs[i] = exclude_songs[i];
      new_exclude_songnames[i] = exclude_songnames[i];
    }
    exclude_songs = new_exclude_songs;
    exclude_songnames = new_exclude_songnames;
    exclude_songs[num_excludes - 1] = songptr.uniquesongid;
    sort_excludes();
    if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
    } catch (Exception e) { log.error("insertExclude(): error", e); }
    insertExcludeSem.release();
  }

  public void removeMixOut(int index) {
    try {
    insertMixoutSem.acquire();
    //sort_mixouts(); any reason for this???

//      if (connectedtoserver) {
//         new RemoveMixouFromServer(uniquesongid, mixout_songs[index]).start();
//      }
    int num_mixouts = mixout_songs.length - 1;
    String[] new_mixout_songnames = new String[num_mixouts];
    long[] new_mixout_songs = new long[num_mixouts];
    float[] new_mixout_bpmdiff = new float[num_mixouts];
    String[] new_mixout_comments = new String[num_mixouts];
    boolean[] new_mixout_addons = new boolean[num_mixouts];
    boolean[] new_mixout_servercache = new boolean[num_mixouts];
    int[] new_mixout_ranks = new int[num_mixouts];
    int i;
    for (i = 0; i < index; ++i) {
      new_mixout_songnames[i] = mixout_songnames[i];
      new_mixout_songs[i] = mixout_songs[i];
      new_mixout_addons[i] = _mixout_addons[i];
      new_mixout_bpmdiff[i] = _mixout_bpmdiff[i];
      new_mixout_comments[i] = _mixout_comments[i];
      new_mixout_ranks[i] = _mixout_ranks[i];
      new_mixout_servercache[i] = mixout_servercache[i];
    }
    for (i = index; i < num_mixouts; ++i) {
            new_mixout_songnames[i] = mixout_songnames[i+1];
            new_mixout_songs[i] = mixout_songs[i+1];
            new_mixout_addons[i] = _mixout_addons[i+1];
            new_mixout_bpmdiff[i] = _mixout_bpmdiff[i+1];
            new_mixout_comments[i] = _mixout_comments[i+1];
            new_mixout_ranks[i] = _mixout_ranks[i+1];
            new_mixout_servercache[i] = mixout_servercache[i+1];
    }
    mixout_songnames = new_mixout_songnames;
    mixout_songs = new_mixout_songs;
    _mixout_addons = new_mixout_addons;
    _mixout_bpmdiff = new_mixout_bpmdiff;
    _mixout_comments = new_mixout_comments;
    _mixout_ranks = new_mixout_ranks;
    mixout_servercache = new_mixout_servercache;
    if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
    setSongId(song_to_string(this));
    setShortIdWInfo(song_to_shortidwinfo(this));
    } catch (Exception e) { log.error("removeMixOut(): error", e); }
    nummixouts = null;
    numaddons = null;
    insertMixoutSem.release();
  }

  public void removeExclude(int index) {
    try {
    insertExcludeSem.acquire();
    //sort_excludes(); any reason for this???
    int num_excludes = exclude_songs.length - 1;
    long[] new_exclude_songs = new long[num_excludes];
    String[] new_exclude_songnames = new String[num_excludes];
    int i;
    for (i = 0; i < index; ++i) {
            new_exclude_songs[i] = exclude_songs[i];
            new_exclude_songnames[i] = exclude_songnames[i];
    }
    for (i = index; i < num_excludes; ++i) {
            new_exclude_songs[i] = exclude_songs[i+1];
            new_exclude_songnames[i] = exclude_songnames[i+1];
    }
    exclude_songs = new_exclude_songs;
    exclude_songnames = new_exclude_songnames;
    if (RapidEvolution.instance.loaded) SongDB.instance.dirtybit = true;
    } catch (Exception e) { log.error("removeExclude(): error", e); }
    insertExcludeSem.release();
  }

  private void sort_mixouts() {
    try {
        int num_mixouts = mixout_songs.length;
    long[] new_mixout_songs = new long[num_mixouts];
    String[] new_mixout_songnames = new String[num_mixouts];
    boolean[] new_mixout_addons = new boolean[num_mixouts];
    float[] new_mixout_bpmdiff = new float[num_mixouts];
    String[] new_mixout_comments = new String[num_mixouts];
    int[] new_mixout_ranks = new int[num_mixouts];
    boolean[] used = new boolean[num_mixouts];
    boolean[] new_mixout_servercache = new boolean[num_mixouts];
    for (int i = 0; i < num_mixouts; ++i) used[i] = false;
    boolean first;
    int lowest = 0;
    for (int j = 0; j < num_mixouts; ++j) {
      first = true;
      for (int i = 0; i < num_mixouts; ++i) {
        if (!used[i]) {
          if (first) {
            first = false;
            lowest = i;
          } else if (getMixoutSongname(i).compareTo(getMixoutSongname(lowest)) < 0) lowest = i;
//          } else if (_mixout_ranks[i] > _mixout_ranks[lowest]) lowest = i; //change from string->rank so lowest is highest
        }
      }
      used[lowest] = true;
      new_mixout_songs[j] = mixout_songs[lowest];
      new_mixout_songnames[j] = mixout_songnames[lowest];
      new_mixout_bpmdiff[j] = _mixout_bpmdiff[lowest];
      new_mixout_comments[j] = _mixout_comments[lowest];
      new_mixout_addons[j] = _mixout_addons[lowest];
      new_mixout_ranks[j] = _mixout_ranks[lowest];
      new_mixout_servercache[j] = mixout_servercache[lowest];
    }
    mixout_songs = new_mixout_songs;
    mixout_songnames = new_mixout_songnames;
    _mixout_addons = new_mixout_addons;
    _mixout_bpmdiff = new_mixout_bpmdiff;
    _mixout_comments = new_mixout_comments;
    _mixout_ranks = new_mixout_ranks;
    mixout_servercache = new_mixout_servercache;
    } catch (Exception e) { log.error("sort_mixouts(): error", e); }
  }

  public int getUniqueId() {
      return uniquesongid;
  }

  private void sort_excludes() {
    try {
        int num_excludes = exclude_songs.length;
    String[] new_exclude_songnames = new String[num_excludes];
    long[] new_exclude_songs = new long[num_excludes];
    boolean[] used = new boolean[num_excludes];
    for (int i = 0; i < num_excludes; ++i) used[i] = false;
    boolean first;
    int lowest = 0;
    for (int j = 0; j < num_excludes; ++j) {
      first = true;
      for (int i = 0; i < num_excludes; ++i) {
        if (!used[i]) {
          if (first) {
            first = false;
            lowest = i;
          } else if (getExcludeSongname(i).compareTo(getExcludeSongname(lowest)) < 0) lowest = i;
        }
      }
      used[lowest] = true;
      new_exclude_songs[j] = exclude_songs[lowest];
      new_exclude_songnames[j] = exclude_songnames[lowest];
    }
    exclude_songs = new_exclude_songs;
    exclude_songnames = new_exclude_songnames;
    } catch (Exception e) { log.error("sort_excludes(): error", e); }
  }
  
  public void calculate_unique_id() {
      uniquestringid = calculate_unique_id(getArtist(), getAlbum(), getTrack(), getSongname(), getRemixer());
  }

  public String getUniqueStringId() {
      if ((uniquestringid == null) || (uniquestringid.equals(""))) {
          calculate_unique_id();
      }
      return uniquestringid;
  }
  
  public static String calculate_unique_id(String artist, String album, String track, String title, String remix) {
      StringBuffer uniquestringid = new StringBuffer();
      if ((artist != null) && !artist.equals("")) {
        uniquestringid.append(artist.toLowerCase());
        uniquestringid.append(" - ");
      }
      uniquestringid.append(album.toLowerCase());
      uniquestringid.append("   [");
      uniquestringid.append(track.toLowerCase());
      uniquestringid.append("]");
      if ((title != null) && !title.equals("")) {
        uniquestringid.append("   ");
        uniquestringid.append(title.toLowerCase());
      }
      if ((remix != null) && !remix.equals("")) {
        uniquestringid.append("  (");
        uniquestringid.append(remix.toLowerCase());
        uniquestringid.append(")");
      }
      return uniquestringid.toString();
  }

  public Object get_column_rootdata(int cindex) {
      int num_mixouts = mixout_songs.length;
    if (cindex == ColumnConfig.COLUMN_BPMSHIFT) {
      for (int i = 0; i < num_mixouts; ++i) {
        if (mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) {
          float diff1 = (float)-SearchPane.instance.bpmslider.getValue() / 100.0f;
          return new MyStringFloat(String.valueOf(diff1 - _mixout_bpmdiff[i]));
        }
      }
    }
    if (cindex == 22) {
      for (int i = 0; i < num_mixouts; ++i) {
        if (mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) {
          return new Integer(_mixout_ranks[i]);
        }
      }
    }
    if (cindex == 24) {
      for (int i = 0; i <num_mixouts; ++i) {
        if ((mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) && _mixout_addons[i]) return new String(SkinManager.instance.getMessageText("default_true_text"));
      }
      return new String(SkinManager.instance.getMessageText("default_false_text"));
    }
    if (cindex == 25) {
      for (int i = 0; i < num_mixouts; ++i)
        if (mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) return new MyStringFloat(String.valueOf(-_mixout_bpmdiff[i]));
    }
    return get_column_data(cindex);
  }
  
  public int getNumMixoutSongs() { return mixout_songs.length; }
  public int getNumExcludeSongs() { return exclude_songs.length; }
  
  public Object get_column_mixdata(int cindex) {
    if (cindex == 18) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) {
          float diff1 = (float)-SearchPane.instance.bpmslider.getValue() / 100.0f;
          return new MyStringFloat(String.valueOf(diff1 + RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i)));
        }
      }
    }
    if (cindex == 39) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) {
          return StringUtil.processcomments(RapidEvolutionUI.instance.currentsong.getMixoutComments(i));
        }
      }
    }
    if (cindex == 22) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) {
          return new Integer(RapidEvolutionUI.instance.currentsong.getMixoutRank(i));
        }
      }
    }
    if (cindex == 24) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if ((RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) && RapidEvolutionUI.instance.currentsong._mixout_addons[i]) return new String(SkinManager.instance.getMessageText("default_true_text"));
      }
      return new String(SkinManager.instance.getMessageText("default_false_text"));
    }
    if (cindex == 25) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) return new MyStringFloat(String.valueOf(RapidEvolutionUI.instance.currentsong._mixout_bpmdiff[i]));
    }
    if (cindex == 19) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) {
            SongKeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey(), RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i));
            return relation.getRecommendedKeyLockSetting();
        }
    }
    if (cindex == 26) {
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) {
            SongKeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey(), RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i));
            return relation.getBestKeyRelation();
        }
    }

    return get_column_data(cindex);
  }

  public Object get_suggestedcolumn_data(int cindex) {
    if (cindex == 13) {
      for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
        SongLinkedList song = (SongLinkedList)RapidEvolutionUI.instance.suggestedsongs.get(i);
        if (song == this) {
          MixInfo mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);
          return StringUtil.processcomments(mi.comments);
        }
      }
    }
    if (cindex == 22) {
      for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
        SongLinkedList song = (SongLinkedList)RapidEvolutionUI.instance.suggestedsongs.get(i);
        if (song == this) {
          MixInfo mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);
          return new Integer(mi.rating);
        }
      }
    }
    if (cindex == 24) {
      for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
        SongLinkedList song = (SongLinkedList)RapidEvolutionUI.instance.suggestedsongs.get(i);
        if (song == this) {
          MixInfo mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);
          if (mi.addon) return new String(SkinManager.instance.getMessageText("default_true_text"));
        }
        return new String(SkinManager.instance.getMessageText("default_false_text"));
      }
    }
    if (cindex == 25) {
      for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
        SongLinkedList song = (SongLinkedList)RapidEvolutionUI.instance.suggestedsongs.get(i);
        if (song == this) {
          MixInfo mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);
          return new MyStringFloat(mi.bpmdiff);
        }
      }
    }
    if (cindex == 19) {
      try {
        for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
          SongLinkedList song = (SongLinkedList) RapidEvolutionUI.instance.suggestedsongs.get(i);
          if (song == this) {
            MixInfo mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);            
            SongKeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey(), Double.parseDouble(mi.bpmdiff));
            relation.getRecommendedKeyLockSetting();
          }
        }
      } catch (Exception e) { return new String(""); }
    }
    if (cindex == 26) {
      try {
        for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
          SongLinkedList song = (SongLinkedList) RapidEvolutionUI.instance.suggestedsongs.get(i);
          if (song == this) {                            
            MixInfo mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);
            SongKeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey(), Float.parseFloat(mi.bpmdiff));
            return relation.getBestKeyRelation();
          }
        }
      } catch (Exception e) { return new String(""); }
    }
    return get_column_data(cindex);
  }

  int get_num_mixouts() {
    int mixoutcount = 0;
    if (mixout_songs != null) {
        for (int i = 0; i < mixout_songs.length; ++i) if (!_mixout_addons[i]) mixoutcount++;
    }
    return mixoutcount;
  }

  private int colorInt(double val) {
      int returnval = (int)val;
      if (returnval < 0) returnval = 0;
      if (returnval > 255) returnval = 255;
      return returnval;
  }
  
  private Color interpolateColor(Color usecolor, double ratio, Color background) {
      return new Color(colorInt(usecolor.getRed() * ratio + background.getRed() * (1.0 - ratio)),
              colorInt(usecolor.getGreen() * ratio + background.getGreen() * (1.0 - ratio)),
                      colorInt(usecolor.getBlue() * ratio + background.getBlue() * (1.0 - ratio)));
  }
  
  private Color cachedSearchColor = null;
  public void resetSearchColorCache() {
      cachedSearchColor = null;
  }
  public Color getSearchColor() {
      if (cachedSearchColor == null) {
          if (log.isTraceEnabled())	log.trace("getSearchColor(): this=" + this);
          try {
              if (isDisabled()) {
                  cachedSearchColor = SkinManager.instance.getColor("song_background_disabled");
              } else {
                  if (RapidEvolutionUI.instance.currentsong != null) {
                      int mixindex = -1;
                      int i = 0;
                      while ((mixindex == -1)
                              && (i < RapidEvolutionUI.instance.currentsong
                                      .getNumMixoutSongs())) {
                          if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid)
                              mixindex = i;
                          ++i;
                      }
                      if (mixindex != -1) {
                          double rank = RapidEvolutionUI.instance.currentsong.getMixoutRank(mixindex);
                          cachedSearchColor = interpolateColor(SkinManager.instance
                                  .getColor("song_background_mixouts"), rank / 100.0,
                                  SkinManager.instance
                                  .getColor("song_background_default"));                          
                      } else {
                          if ((RapidEvolutionUI.instance.getCurrentKey() != null)
                                  && songKey.getStartKey().isValid()) {
                              if ((RapidEvolution.instance.getActualBpm() != 0.0f)
                                      && (_bpm.data != 0)) {
                                  
                                  Key tokey = songKey.getStartKey();
                                  SongKeyRelation closest = Key.getClosestKeyRelation(this,
                                          RapidEvolution.instance.getActualBpm(),
                                          RapidEvolutionUI.instance.getCurrentKey());
                                  
                                  double percent1 = closest.getRelationWithKeylock()
                                  .isCompatible() ? 1.0 - Math.abs(closest
                                          .getRelationWithKeylock().getDifference()) : 0.5;
                                  double percent2 = closest.getRelationWithoutKeylock()
                                  .isCompatible() ? 1.0 - Math.abs(closest
                                          .getRelationWithoutKeylock().getDifference()) : 0.5;
                                  double max = percent1;
                                  if (OptionsUI.instance.disablekeylockfunctionality
                                          .isSelected())
                                      max = percent2;
                                  if (percent2 > percent1)
                                      max = percent2;
                                  if (max > 0.5) {
                                      Color usecolor;
                                      if (get_num_mixouts() <= 0)
                                          usecolor = SkinManager.instance
                                          .getColor("song_background_in_key");
                                      else
                                          usecolor = SkinManager.instance
                                          .getColor("song_background_in_key_with_mixouts");
                                      
                                      cachedSearchColor = interpolateColor(usecolor, (max - 0.5) * 2.0,
                                              SkinManager.instance
                                              .getColor("song_background_default"));
                                  }
                              }
                          }
                          if (cachedSearchColor == null) {
                              if (get_num_mixouts() > 0) {
                                  int x = get_highest_rank_mixout();
                                  if (x < 50) {
                                      cachedSearchColor = SkinManager.instance.getColor("song_background_default");
                                  } else {
	                                  float max = (float) x / 100.0f;
	                                  cachedSearchColor = interpolateColor(SkinManager.instance
	                                          .getColor("song_background_with_mixouts"),
	                                          (max - 0.5) * 2.0, SkinManager.instance
	                                          .getColor("song_background_default"));
                                  }
                              }                                                
                          }                          
                      }
                  }                  
              }
          } catch (Exception e) {
              log.error("getSearchColor(): error Exception", e);
          }
          if (cachedSearchColor == null) cachedSearchColor = SkinManager.instance.getColor("song_background_default");
      }
      return cachedSearchColor;
  }

  public Color getRootColor() {
    if (isDisabled()) return SkinManager.instance.getColor("mixout_background_disabled");
    if (RapidEvolutionUI.instance.currentsong != null) {
      SongLinkedList mixsong = RapidEvolutionUI.instance.currentsong;
      int mixindex = -1;
      for (int i = 0; i < mixout_songs.length; ++i) {
        if (mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) mixindex = i;
      }
      if (_mixout_addons[mixindex] == true) {
        int score = _mixout_ranks[mixindex];
        if (score < 50) return SkinManager.instance.getColor("mixout_background_addon");
        float max = (float)score / 100.0f;
        
        return interpolateColor(SkinManager.instance.getColor("mixout_background_addon_ranked"), (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_addon"));                    
      }
      if (_mixout_ranks[mixindex] >= 50) {
        float max = (float)_mixout_ranks[mixindex] / 100.0f;
        Color usecolor = SkinManager.instance.getColor("mixout_background_ranked");
        if (mixsong.get_num_mixouts() > 0) usecolor = SkinManager.instance.getColor("mixout_background_ranked_with_mixouts");
        return interpolateColor(usecolor, (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_default"));                    
      }
      if (get_num_mixouts() > 0) {
        int x = get_highest_rank_mixout();
        if (x < 50) return SkinManager.instance.getColor("mixout_background_default");
        float max = (float)x / 100.0f;
        return interpolateColor(SkinManager.instance.getColor("mixout_background_with_mixouts"), (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_default"));    
                
      }
    }
    return SkinManager.instance.getColor("mixout_background_default");
  }

// start here later...

  public Color getSuggestedColor() {
    if (isDisabled()) return SkinManager.instance.getColor("mixout_background_disabled");
    if (RapidEvolutionUI.instance.currentsong != null) {
      SongLinkedList mixsong = this;

      MixInfo mi = null;
      for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
        SongLinkedList song = (SongLinkedList)RapidEvolutionUI.instance.suggestedsongs.get(i);
        if (song == this) {
          mi = (MixInfo) RapidEvolutionUI.instance.suggestedinfo.get(i);
        }
      }

      if (mi.addon == true) {
        int score = mi.rating;
        if (score < 50) return SkinManager.instance.getColor("mixout_background_addon");
        float max = (float)score / 100.0f;
        
        return interpolateColor(SkinManager.instance.getColor("mixout_background_addon_ranked"), (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_addon"));
        
      }
      if (mi.rating >= 50) {
        float max = (float)mi.rating / 100.0f;
        Color usecolor = SkinManager.instance.getColor("mixout_background_ranked");
        if (mixsong.get_num_mixouts() > 0) usecolor = SkinManager.instance.getColor("mixout_background_ranked_with_mixouts");

        return interpolateColor(usecolor, (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_default"));
        
      }
      if (mixsong.get_num_mixouts() > 0) {
        int x = mixsong.get_highest_rank_mixout();
        if (x < 50) return SkinManager.instance.getColor("mixout_background_default");
        float max = (float)x / 100.0f;

        return interpolateColor(SkinManager.instance.getColor("mixout_background_with_mixouts"), (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_default"));
        
      }
    }
    return SkinManager.instance.getColor("mixout_background_default");
  }

  public Color getMixColor() {
    if (isDisabled()) return SkinManager.instance.getColor("mixout_background_disabled");
    if (RapidEvolutionUI.instance.currentsong != null) {
      SongLinkedList mixsong = this;
      int mixindex = -1;
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == uniquesongid) mixindex = i;
      }
      if (RapidEvolutionUI.instance.currentsong._mixout_addons[mixindex] == true) {
        int score = RapidEvolutionUI.instance.currentsong.getMixoutRank(mixindex);
        if (score < 50) return SkinManager.instance.getColor("mixout_background_addon");
        float max = (float)score / 100.0f;
        
        return interpolateColor(SkinManager.instance.getColor("mixout_background_addon_ranked"), (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_addon"));
        
      }
      if (RapidEvolutionUI.instance.currentsong.getMixoutRank(mixindex) >= 50) {
        float max = (float)RapidEvolutionUI.instance.currentsong.getMixoutRank(mixindex) / 100.0f;
        Color usecolor = SkinManager.instance.getColor("mixout_background_ranked");
        if (mixsong.get_num_mixouts() > 0) usecolor = SkinManager.instance.getColor("mixout_background_ranked_with_mixouts");

        return interpolateColor(usecolor, (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_default"));
        
      }
      if (mixsong.get_num_mixouts() > 0) {
        int x = mixsong.get_highest_rank_mixout();
        if (x < 50) return SkinManager.instance.getColor("mixout_background_default");
        float max = (float)x / 100.0f;

        return interpolateColor(SkinManager.instance.getColor("mixout_background_with_mixouts"), (max - 0.5) * 2.0, SkinManager.instance.getColor("mixout_background_default"));
        
      }
    }
    return SkinManager.instance.getColor("mixout_background_default");
  }

  static public String song_to_string(SongLinkedList songptr) {
    StringBuffer outputstring = new StringBuffer(song_to_string_short(songptr));
    StringBuffer infostring = new StringBuffer("");
    if ((songptr.getStartbpm() != 0) && OptionsUI.instance.songdisplaystartbpm.isSelected()) {
      infostring.append(String.valueOf(songptr.getStartbpm()));
      if ((songptr.getEndbpm() != 0.0) && OptionsUI.instance.songdisplayendbpm.isSelected()) {
        infostring.append("->");
        infostring.append(String.valueOf(songptr.getEndbpm()));
      }
      infostring.append("bpm");
    } else if ((songptr.getEndbpm() != 0.0) && OptionsUI.instance.songdisplayendbpm.isSelected()) {
      infostring.append(String.valueOf(songptr.getEndbpm()));
      infostring.append("bpm");
    }
    if (songptr.getStartKey().isValid() && OptionsUI.instance.songdisplaystartkey.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.getStartKey().toString());
      if (songptr.getEndKey().isValid() && OptionsUI.instance.songdisplayendkey.isSelected()) {
        infostring.append("->");
        infostring.append(songptr.getEndKey().toString());
      }
    } else if (songptr.getEndKey().isValid() && OptionsUI.instance.songdisplayendkey.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.getEndKey().toString());
    }
    if ((!songptr.getTime().equals("")) && OptionsUI.instance.songdisplaytracktime.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.getTime());
    }
    if ((!songptr.getTimesig().equals("")) && OptionsUI.instance.songdisplaytimesig.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.getTimesig());
    }

    if (infostring.length() > 0) {
        outputstring.append("   (");
        outputstring.append(infostring);
        outputstring.append(")");
    }

    infostring = new StringBuffer("");

    if ((!songptr.getUser1().equals("")) && OptionsUI.instance.songdisplayfield1.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.user1);
    }
    if ((!songptr.getUser2().equals("")) && OptionsUI.instance.songdisplayfield2.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.user2);
    }
    if ((!songptr.getUser3().equals("")) && OptionsUI.instance.songdisplayfield3.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.user3);
    }
    if ((!songptr.getUser4().equals("")) && OptionsUI.instance.songdisplayfield4.isSelected()) {
      if (infostring.length() > 0) infostring.append(", ");
      infostring.append(songptr.user4);
    }
    if (infostring.length() > 0) {
      outputstring.append("   [");
      outputstring.append(infostring);
      outputstring.append("]");
    }

    return outputstring.toString();
  }

  static public String song_to_string_short(SongLinkedList songptr) {
    StringBuffer outputstring = new StringBuffer();
    if ((!songptr.getArtist().equals("")) && OptionsUI.instance.songdisplayartist.isSelected()) outputstring.append(songptr.getArtist());
    if (!songptr.getAlbum().equals("") && OptionsUI.instance.songdisplayalbum.isSelected()) {
      if (outputstring.length() > 0) outputstring.append(" - ");
      outputstring.append(songptr.getAlbum());
    }
    if (!songptr.getTrack().equals("") && OptionsUI.instance.songdisplaytrack.isSelected()) {
      if (outputstring.length() > 0) outputstring.append("  ");
      outputstring.append("[");
      outputstring.append(songptr.getTrack());
      outputstring.append("]");
    }
    if (!songptr.getSongname().equals("") && OptionsUI.instance.songdisplaysongname.isSelected()) {
      if (outputstring.length() > 0) {
        if ((!songptr.getTrack().equals("")) && OptionsUI.instance.songdisplaytrack.isSelected()) outputstring.append("  ");
        else outputstring.append(" - ");
      }
      outputstring.append(songptr.getSongname());
    }
    if (!songptr.getRemixer().equals("") && OptionsUI.instance.songdisplayremixer.isSelected()) {
      if (outputstring.length() > 0) outputstring.append(" ");
      outputstring.append("(");
      outputstring.append(songptr.getRemixer());
      outputstring.append(")");
    }
    return outputstring.toString();
  }

  static public String song_to_string_short(String artist, String album, String track, String songname, String remixer) {
    StringBuffer outputstring = new StringBuffer();
    if (!artist.equals("")) outputstring.append(artist);
    if (!album.equals("")) {
      if (outputstring.length() > 0) outputstring.append(" - ");
      outputstring.append(album);
    }
    if (!track.equals("")) {
      if (outputstring.length() > 0) outputstring.append("  ");
      outputstring.append("[");
      outputstring.append(track);
      outputstring.append("]");
    }
    if (!songname.equals("")) {
      if (outputstring.length() > 0) {
        if (!track.equals("")) outputstring.append("  ");
        else outputstring.append(" - ");
      }
      outputstring.append(songname);
    }
    if (!remixer.equals("")) {
      if (outputstring.length() > 0) outputstring.append(" ");
      outputstring.append("(");
      outputstring.append(remixer);
      outputstring.append(")");
    }
    return outputstring.toString();
  }

  static public String song_to_shortidwinfo(SongLinkedList songptr) {
    StringBuffer outputstring = new StringBuffer();
    if (!songptr.getArtist().equals("")) outputstring.append(songptr.getArtist());
    if (!songptr.getSongname().equals("")) {
      if (outputstring.length() > 0) outputstring.append(" - ");
      outputstring.append(songptr.getSongname());
    }
    if (!songptr.getRemixer().equals("")) {
      if (outputstring.length() > 0) outputstring.append(" ");
      outputstring.append("(");
      outputstring.append(songptr.getRemixer());
      outputstring.append(")");
    }

    if (songptr.getStartbpm() != 0.0) {
      outputstring.append("   (");
      outputstring.append(String.valueOf(songptr.getStartbpm()));
      if (songptr.getEndbpm() != 0.0) {
        outputstring.append("->");
        outputstring.append(String.valueOf(songptr.getEndbpm()));
      }
      outputstring.append("bpm");
      if (songptr.getStartKey().isValid() || songptr.getEndKey().isValid()) {
        if (songptr.getStartKey().isValid() && songptr.getEndKey().isValid()) {
          outputstring.append(", ");
          outputstring.append(songptr.getStartKey().toString());
          outputstring.append("->");
          outputstring.append(songptr.getEndKey().toString());
        } else if (songptr.getStartKey().isValid()) {
          outputstring.append(", ");
          outputstring.append(songptr.getStartKey().toString());
        } else if (songptr.getEndKey().isValid()) {
          outputstring.append(", ");
          outputstring.append(songptr.getEndKey().toString());
        }
      }
      if (!songptr.getTime().equals("")) {
        outputstring.append(", ");
        outputstring.append(songptr.getTime());
      }
      outputstring.append(")");
    } else if (songptr.getStartKey().isValid() || songptr.getEndKey().isValid()) {
      outputstring.append("   (");
      if (songptr.getStartKey().isValid() && songptr.getEndKey().isValid()) {
          outputstring.append(songptr.getStartKey().toString());
          outputstring.append("->");
          outputstring.append(songptr.getEndKey().toString());
      } else if (songptr.getStartKey().isValid()) outputstring.append(songptr.getStartKey().toString());
      else outputstring.append(songptr.getEndKey().toString());
      if (!songptr.getTime().equals("")) {
        outputstring.append(", ");
        outputstring.append(songptr.getTime());
      }
      outputstring.append(")");
    } else if (!songptr.getTime().equals("")) {
      outputstring.append("   (");
      outputstring.append(songptr.getTime());
      outputstring.append(")");
    }
    return outputstring.toString();
  }

  static public String song_to_shortid(SongLinkedList songptr) {
    StringBuffer outputstring = new StringBuffer();
    if (!songptr.getArtist().equals("")) outputstring.append(songptr.getArtist());
    if (!songptr.getSongname().equals("")) {
      if (outputstring.length() > 0) outputstring.append(" - ");
      outputstring.append(songptr.getSongname());
    }
    if (!songptr.getRemixer().equals("")) {
      if (outputstring.length() > 0) outputstring.append(" ");
      outputstring.append("(");
      outputstring.append(songptr.getRemixer());
      outputstring.append(")");
    }
    return outputstring.toString();
  }


  int get_highest_rank_mixout() {
    int max = 0;
    for (int i = 0; i < getNumMixoutSongs(); ++i) if (_mixout_ranks[i] > max) max = _mixout_ranks[i];
    return max;
  }

//    float has_qualified_mixout_shade();

  boolean[] getLogicalStyleIds() {
      if (!logicalstyleidscached) {
          StyleLinkedList siter = SongDB.instance.masterstylelist;
          if ((_logicalstyleids == null) || (_logicalstyleids.length != _styles.length)) {
              _logicalstyleids = new boolean[_styles.length];
          } else {
              for (int i = 0; i < _logicalstyleids.length; ++i) _logicalstyleids[i] = false;
          }
          for (int i = 0; i < _styles.length; ++i) {
              _logicalstyleids[i] = _logicalstyleids[i] || _styles[i];
              if (_styles[i])
                  recurseParents(_logicalstyleids, siter);
              siter = siter.next;
          }          
          logicalstyleidscached = true;
      }
      return _logicalstyleids;
  }
  
  private void recurseParents(boolean[] _logicalstyleids, StyleLinkedList style) {
      Set parents = style.parent_style_ids.keySet();
      if (parents != null) {
          Iterator piter = parents.iterator();
          while (piter.hasNext()) {
              int parent_styleid = ((Integer)piter.next()).intValue();
              StyleLinkedList parent_style = StyleLinkedList.getStyle(parent_styleid);
              if (parent_style.getStyleId() != -1) {
                  int s_index = parent_style.get_sindex();
                  _logicalstyleids[s_index] = true;
                  recurseParents(_logicalstyleids, parent_style);
              }
          }
      }      
  }
  
//    boolean has_qualified_mixout();
  String[] getStyleStrings() {
    if (!stylestringscached) {
      Vector stylesvector = new Vector();
      StyleLinkedList iter = SongDB.instance.masterstylelist;
      for (int i = 0; i < _styles.length; ++i) {
        if (_styles[i]) stylesvector.add(iter.getName());
        iter = iter.next;
      }
      _stylestrings = new String[stylesvector.size()];
      for (int i = 0; i < stylesvector.size(); ++i) _stylestrings[i] = (String)stylesvector.get(i);
      stylestringscached = true;
    }
    return _stylestrings;
  }

  static public SongLinkedList Scrub(SongLinkedList input) {
    input.setArtist(StringUtil.remove_underscores(input.getArtist()));
    input.setAlbum(StringUtil.remove_underscores(input.getAlbum()));
    input.setTrack(StringUtil.remove_underscores(input.getTrack()));
    input.setSongname(StringUtil.remove_underscores(input.getSongname()));
    input.setRemixer(StringUtil.remove_underscores(input.getRemixer()));
    input.setComments(StringUtil.remove_underscores(input.getComments()));
    input.setUser1(StringUtil.remove_underscores(input.getUser1()));
    input.setUser2(StringUtil.remove_underscores(input.getUser2()));
    input.setUser3(StringUtil.remove_underscores(input.getUser3()));
    input.setUser4(StringUtil.remove_underscores(input.getUser4()));
    return input;
  }

  public static boolean determinetimesigcompatibility(SongLinkedList song1, SongLinkedList song2) {
    if (!OptionsUI.instance.evenbpmmultiples.isSelected()) return song1.getTimesig().equals(song2.getTimesig());
    else try {
      String input = song1.getTimesig();
      int seperator = 0;
      while (((seperator < input.length()) && ((input.charAt(seperator) != '/')))) seperator++;
      if (seperator == input.length()) return false;
      int num = Integer.parseInt(input.substring(0, seperator));
      int num2 = Integer.parseInt(input.substring(seperator + 1, input.length()));
      input = song2.getTimesig();
      seperator = 0;
      while (((seperator < input.length()) && ((input.charAt(seperator) != '/')))) seperator++;
      if (seperator == input.length()) return false;
      int num3 = Integer.parseInt(input.substring(0, seperator));
      int num4 = Integer.parseInt(input.substring(seperator + 1, input.length()));
      float ratio1 = (float)num / (float)num2;
      float ratio2 = (float)num3 / (float)num4;
      float threshold = 0.001f;
      if (ratio1 == ratio2) return true;
      if (Math.abs(ratio1 * 2.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 * 4.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 * 8.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 * 16.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 / 2.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 / 4.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 / 8.0f - ratio2) < threshold) return true;
      if (Math.abs(ratio1 / 16.0f - ratio2) < threshold) return true;
      return false;
    } catch (Exception e) { log.error("determinetimesigcompatibility(): error", e); }
    return false;
  }

  public static void PopulateSong(SongLinkedList dest, SongLinkedList datasong) {
      PopulateSong(dest, datasong, true);
  }
  
  public static void PopulateSong(SongLinkedList dest, SongLinkedList datasong, boolean overwrite) {

      if (datasong == null) return;
      try {
          if (! (datasong.getArtist().equals("") && datasong.getAlbum().equals("") &&
                  datasong.getTrack().equals("") && datasong.getSongname().equals("")  && datasong.getRemixer().equals(""))) {            
          	String uniqueid = SongLinkedList.calculate_unique_id(datasong.getArtist(), datasong.getAlbum(),
                                                datasong.getTrack(),
                                                datasong.getSongname(), datasong.getRemixer());
          	SongLinkedList testptr = SongDB.instance.OldGetSongPtr(uniqueid);
          	if ((testptr == null) || (testptr == dest)) {
                  if (overwrite) {
                      if (!datasong.getArtist().equals("")) dest.setArtist(datasong.getArtist());
                      if (!datasong.getAlbum().equals("")) dest.setAlbum(datasong.getAlbum());
                      if (!datasong.getTrack().equals("")) dest.setTrack(datasong.getTrack());
                      if (!datasong.getSongname().equals("")) dest.setSongname(datasong.getSongname());
                      if (!datasong.getRemixer().equals("")) dest.setRemixer(datasong.getRemixer());
                    } else {
                      if (dest.getArtist().equals("")) dest.setArtist(datasong.getArtist());
                      if (dest.getAlbum().equals("")) dest.setAlbum(datasong.getAlbum());
                      if (dest.getTrack().equals("")) dest.setTrack(datasong.getTrack());
                      if (dest.getSongname().equals("")) dest.setSongname(datasong.getSongname());
                      if (dest.getRemixer().equals("")) dest.setRemixer(datasong.getRemixer());
                    }                    
               }                
          }
        } catch (Exception e) { log.error("copyValuesFrom(): error", e); }
        if (overwrite) {
          if (!datasong.getTime().equals("")) dest.setTime(datasong.getTime());
          if (!datasong.getTimesig().equals("")) dest.setTimesig(datasong.getTimesig());
          if (dest.getTimesig().equals("")) dest.setTimesig("4/4");
          if (datasong.getStartbpm() != 0) dest.setStartbpm(datasong.getStartbpm());
          if (datasong.getEndbpm() != 0) dest.setEndbpm(datasong.getEndbpm());
          if (datasong.getBpmAccuracy() != 0) dest.setBpmAccuracy(datasong.getBpmAccuracy());
          if (datasong.getStartKey().isValid()) dest.setStartkey(datasong.getStartKey());
          if (datasong.getEndKey().isValid()) dest.setEndkey(datasong.getEndKey());
          if (datasong.getKeyAccuracy() != 0) dest.setKeyAccuracy(datasong.getKeyAccuracy());
          if (!datasong.getComments().equals("")) dest.setComments(datasong.getComments());
          if (!datasong.getUser1().equals("")) dest.setUser1(datasong.getUser1());
          if (!datasong.getUser2().equals("")) dest.setUser2(datasong.getUser2());
          if (!datasong.getUser3().equals("")) dest.setUser3(datasong.getUser3());
          if (!datasong.getUser4().equals("")) dest.setUser4(datasong.getUser4());
          if (datasong.getRGA().isValid()) dest.setRGA(datasong.getRGA());
          if (datasong.getRatingInt() != 0) dest.setRating(datasong.getRating());
          if (datasong.getBeatIntensity() != 0) dest.setBeatIntensity(datasong.getBeatIntensity());
        } else {
          if (dest.getTime().equals("")) dest.setTime(datasong.getTime());
          if (dest.getTimesig().equals("")) dest.setTimesig(datasong.getTimesig());
          if (dest.getTimesig().equals("")) dest.setTimesig("4/4");
          if (dest.getBpmAccuracy() == 0) dest.setBpmAccuracy(datasong.getBpmAccuracy());
          if (dest.getStartbpm() == 0) dest.setStartbpm(datasong.getStartbpm());
          if (dest.getEndbpm() == 0) dest.setEndbpm(datasong.getEndbpm());
          if (dest.getKeyAccuracy() == 0) dest.setKeyAccuracy(datasong.getKeyAccuracy());
          if (dest.getStartKey().isValid()) dest.setStartkey(datasong.getStartKey());
          if (dest.getEndKey().isValid()) dest.setEndkey(datasong.getEndKey());
          if (dest.getComments().equals("")) dest.setComments(datasong.getComments());
          if (dest.getUser1().equals("")) dest.setUser1(datasong.getUser1());
          if (dest.getUser2().equals("")) dest.setUser2(datasong.getUser2());
          if (dest.getUser3().equals("")) dest.setUser3(datasong.getUser3());
          if (dest.getUser4().equals("")) dest.setUser4(datasong.getUser4());
          if (dest.getRatingInt() == 0) dest.setRating(datasong.getRating());
          if (!dest.getRGA().isValid()) dest.setRGA(datasong.getRGA());
          if (dest.getBeatIntensity() == 0) dest.setBeatIntensity(datasong.getBeatIntensity());
        }
        if (datasong.stylelist != null) {
            StyleLinkedList siter = SongDB.instance.masterstylelist;
            while (siter != null) {
                boolean found = false;
                for (int s = 0; s < datasong.stylelist.length; ++s) {
                    if (datasong.stylelist[s].equalsIgnoreCase(siter.getName())) {
                        found = true;
                        if (!siter.containsDirect(dest))
                            siter.insertSong(dest);
                    }
                }
                if (!found && overwrite) {
                    siter.removeSong(dest);
                }
                siter = siter.next;
            }
        }            
      HashMap newstyles = new HashMap();
      if (datasong.stylelist != null) {                   
          for (int i = 0; i < datasong.stylelist.length; ++i) {
              String stylename = datasong.stylelist[i];
              newstyles.put(stylename, stylename);
          }
      }
      if (dest.stylelist != null) {
          for (int i = 0; i < dest.stylelist.length; ++i) {
              String stylename = dest.stylelist[i];
              newstyles.put(stylename, stylename);
          }          
      }
      Collection coll = newstyles.values();
      if (coll != null) {
          dest.stylelist = new String[newstyles.size()];
          Iterator iter = coll.iterator();
          int index = 0;
          while (iter.hasNext()) {
              String style = (String)iter.next();
              dest.stylelist[index++] = style;
          }
      }    
  }

  private void invalidate() {
      if (RapidEvolution.instance.loaded) {
          servercached = false;
          songKey.invalidate();
          setLastModified();
      }
  }
  
  public String getArtist() {
      if (_artist == null) return "";
      return _artist.toString();
  }
  public void setArtist(String value) {
    if ((_artist == null) || !value.equals(_artist.toString())) {
        invalidate();
        if (_artist == null)
            _artist = new MyString(value);
        else
            _artist.setData(value);
        iscompilation = null;
        hasAlbumCover = null;        
        SongDB.instance.invalidateAlbumCoverCacheForAlbum(getAlbum());
    }        
  }

  public String getAlbum() {
      if (_album == null) return "";
      return _album.toString();      
  }
  public void setAlbum(String value) {
      if ((_album == null) || !value.equals(_album.toString())) {
          invalidate();
          String old_album = _album.toString();
          if (_album == null)
              _album = new MyString(value);
          else
              _album.setData(value);
          iscompilation = null;
          hasAlbumCover = null;
      }
  }
  public String getTrack() {
      if (_track == null) return "";
      return _track.toString();
  }
  public void setTrack(String value) {
    if ((_track == null) || !value.equals(_track.toString())) {
        invalidate();
        _track = new MyTrackString(value);
    }    
  }
  public String getSongname() {
      if (_songname == null) return "";
      return _songname.toString();
  }
  public void setSongname(String value) {
    if ((_songname == null) || !value.equals(_songname.toString())) {
        invalidate();
        if (_songname == null)
            _songname = new MyString(value);
        else
            _songname.setData(value);
    }    
  }
  public String getRemixer() {
      if (_remixer == null) return "";
      return _remixer.toString();
  }  
  public void setRemixer(String value) {
    if ((_remixer == null) || !value.equals(_remixer.toString())) {
        invalidate();
        if (_remixer == null)
            _remixer = new MyString(value);
        else
            _remixer.setData(value);
    }    
  }

  public String getComments() { return _comments; }
  public void setComments(String value) {
    if ((_comments == null) || !value.equals(_comments.toString())) {
        invalidate();
        processed_comments = null;
        _comments = value;
    }    
  }
  public float getStartbpm() { 
      if (_bpm == null) return 0.0f;
      return _bpm.data; }
  public void setStartbpm(float value) {
    if ((_bpm == null) || value != _bpm.data) {
        invalidate();
        _bpm = new myFloat(value);
        bpm = null;
    }
  }
  public float getEndbpm() {
      if (_endbpm == null) return 0.0f;
      return _endbpm.data;
  }
  public void setEndbpm(float value) {
    if ((_endbpm == null) || value != _endbpm.data) {
        invalidate();
        _endbpm = new myFloat(value);
        bpm = null;
    }
  }
  public void setBeatIntensity(int value) {
      if (value != beat_intensity.getValue()) {
          invalidate();
          beat_intensity = new MyInteger(value);
      }          
  }
  public int getBeatIntensity() {
      if (beat_intensity == null) return 0;
      return beat_intensity.getValue();
  }
  
  
  public String getTimesig() {
      if (_timesig == null) return "";
      return _timesig.toString(); }
  
  public void setTimesig(String value) {
    if ((_timesig == null) || !value.equals(_timesig.toString())) {
        invalidate();
        if (_timesig == null)
            _timesig = new MyString(value);
        else
            _timesig.setData(value);
    }    
  }
  public String getTime() {
      if (_tracktime == null) return "";
      return _tracktime.toString(); }
  
  public void setTime(String value) {
    if ((_tracktime == null) || !value.equals(_tracktime.toString())) {
        invalidate();
        _tracktime = new myLength(value);
    }    
  }

  
  public SongKey getKey() { return songKey; }
  public Key getStartKey() {
      return songKey.getStartKey();
  }
  public void setStartkey(Key value) {
      if (songKey.setStartKey(value)) {
          invalidate();          
      }
  }
  public void setStartkey(String value) {
      if (songKey.setStartKey(value)) {
          invalidate();          
      }
  }
  public Key getEndKey() {
      return songKey.getEndKey();
  }
  public void setEndkey(Key value) {
      if (songKey.setEndKey(value)) {
          invalidate();          
      }
  }
  public void setEndkey(String value) {
      if (songKey.setEndKey(value)) {
          invalidate();          
      }
  }

 public int getMixoutRank(int i) { return _mixout_ranks[i]; }
 public void setMixoutRank(int i, int value) {
   if (_mixout_ranks[i] != value) mixout_servercache[i] = false;
   _mixout_ranks[i] = value;
 }
 public float getMixoutBpmdiff(int i) { return _mixout_bpmdiff[i]; }
 public void setMixoutBpmdiff(int i, float value) {
   if (_mixout_bpmdiff[i] != value) mixout_servercache[i] = false;
   _mixout_bpmdiff[i] = value;
 }
 public String getMixoutComments(int i) { return _mixout_comments[i]; }
 public void setMixoutComments(int i, String value) {
   if (!value.equals(_mixout_comments[i])) mixout_servercache[i] = false;
   _mixout_comments[i] = value;
 }
 public boolean getMixoutAddon(int i) { return _mixout_addons[i]; }
 public void setMixoutAddon(int i, boolean value) {
   if (_mixout_addons[i] != value) mixout_servercache[i] = false;
   _mixout_addons[i] = value;
 }
 
 public char getRating() {
     String str = rating.toString();
     if (str.equals("1")) return (char)1;
     if (str.equals("2")) return (char)2;
     if (str.equals("3")) return (char)3;
     if (str.equals("4")) return (char)4;
     if (str.equals("5")) return (char)5;
     return (char)0;
 }
 public int getRatingInt() {
     String str = rating.toString();
     if (str.equals("1")) return 1;
     if (str.equals("2")) return 2;
     if (str.equals("3")) return 3;
     if (str.equals("4")) return 4;
     if (str.equals("5")) return 5;
     return 0;
 }
 public RatingToolBar getRatingBar() { return ratingbar; }
 
 public void setRating(char value, boolean override) {
     if (value != getRating()) {
         invalidate();
         String strValue = null;
         if (value == 1) strValue = "1";
         else if (value == 2) strValue = "2";
         else if (value == 3) strValue = "3";
         else if (value == 4) strValue = "4";
         else if (value == 5) strValue = "5";
         if (strValue != null) {
             if (rating == null)
                 rating = new MyString(strValue);
             else
                 rating.setData(strValue);
         } else {
             rating.setData("");
         }
         if (!override) ratingbar.setRating(value);
     }     
 }
 public void setRating(char value) {
     setRating(value, false);
 }

 public boolean getStyle(int index) { return _styles[index]; }
 public boolean[] getStyles() { return _styles; }
 public void setStyle(int index, boolean val) {
     if ((_styles != null) && (_styles.length > index)) {
         if (RapidEvolution.instance.loaded) {
             if (_styles[index] != val) {         
                 invalidate();
                 stylestringscached = false;   
                 logicalstyleidscached = false;
                 allstylesstring = null;
             }
         }     
         _styles[index] = val;
     }
 }
 public void setStyles(boolean[] val) {     
//     boolean areDifferent = areStyleArraysDifferent(_styles, val);
     _styles = val;
     if (RapidEvolution.instance.loaded) { // && areDifferent) {         
         invalidate();
         stylestringscached = false; 
         logicalstyleidscached = false;
         allstylesstring = null;
     }
  }
 private boolean areStyleArraysDifferent(boolean[] a, boolean[] b) {
     if ((a == null) && (b == null)) return false;
     if (a == null) return true;
     if (b == null) return true;
     if (a.length != b.length) return true;
     for (int i = 0; i < a.length; ++i) {
         if (a[i] != b[i]) return true;
     }
     return false;
 }

  public boolean getVinylOnly() { return vinylonlybit; }
  public void setVinylOnly(boolean val) { 
      if (val != vinylonlybit) {
          vinylonlybit = val;
          analog = null;
          digital = null;
      }
  }
  public boolean getNonVinylOnly() { return nonvinylonlybit; }
  public void setNonVinylOnly(boolean val) {
      if (val != nonvinylonlybit) {
          nonvinylonlybit = val;
          analog = null;
          digital = null;
      }
  }
  public boolean isDisabled() { return disabled.data; }
  public void setDisabled(boolean val) {
      if (val != disabled.data) {
          disabled = new myBoolean(val);
          cachedSearchColor = null;
      }
  }
  public void setTempMixoutSongname(int index, String val) { mixout_songnames[index] = val; }
  public String getTempMixoutSongname(int index) { return mixout_songnames[index]; }
  public String getMixoutSongname(int index) {
      SongLinkedList song =SongDB.instance.NewGetSongPtr(mixout_songs[index]);
      if (song != null)
          return song.uniquestringid;
      else {
          log.error("getMixoutSongname(): Missing mixout songid: " + mixout_songs[index] + ", index: " + index + ", this songid: " + uniquesongid);
          return null;          
      }      
  }
  public void setTempExcludeSongname(int index, String val) { exclude_songnames[index] = val; }
  public String getTempExcludeSongname(int index) { return exclude_songnames[index]; }
  public String getExcludeSongname(int index) {
      SongLinkedList song = SongDB.instance.NewGetSongPtr(exclude_songs[index]);
      if (song != null) return song.uniquestringid;
      else {
          log.error("getExcludeSongname(): Missing exclude songid: " + exclude_songs[index] + ", index: " + index + ", this songid: " + uniquesongid);
          return null;
      }
   }
  
  private MyString _songid = null;
  private MyString songidshort = null;
  private MyString shortid = null;
  private MyString shortidwinfo = null;
  
  public String getSongId() {
      if (_songid == null)
          setSongId(song_to_string(this));
      return _songid.toString();
  }
  public MyString getSongIdMyString() {
      if (_songid == null)
          setSongId(song_to_string(this));
      return _songid;
  }
  public void setSongId(String val) {
      if ((_songid != null) && !val.equals(_songid.toString())) {
          if (_songid == null)
              _songid = new MyString(val);
          else
              _songid.setData(val);
      } else if (_songid == null) {
          _songid = new MyString(val);
      }
  }
  
  public void setSongIdShort(String input) {
      if ((songidshort == null) || !songidshort.toString().equals(input)) {
          if (songidshort == null)
              songidshort = new MyString(input);
          else
              songidshort.setData(input);
      }
  }
  public String getSongIdShort() {
      if (songidshort == null) 
          setSongIdShort(SongLinkedList.song_to_string_short(this));
      return songidshort.toString();
  }
  public MyString getSongIdShortMyString() {
      if (songidshort == null) 
          setSongIdShort(SongLinkedList.song_to_string_short(this));
      return songidshort;      
  }
    
  public String getShortId() {
      if (shortid == null)
          setShortId(SongLinkedList.song_to_shortid(this));
      return shortid.toString(); 
  }
  public MyString getShortIdMyString() {
      if (shortid == null)
          setShortId(SongLinkedList.song_to_shortid(this));
      return shortid;       
  }
  public void setShortId(String val) {
      if ((shortid == null) || !shortid.toString().equals(val)) {
          if (shortid == null)
              shortid = new MyString(val);
          else
              shortid.setData(val);
      }
  }
  
  public String getShortIdWInfo() {
      if (shortidwinfo == null)
          setShortIdWInfo(SongLinkedList.song_to_shortidwinfo(this));
      return shortidwinfo.toString(); 
  }
  public MyString getShortIdWInfoMyString() {
      if (shortidwinfo == null)
          setShortIdWInfo(SongLinkedList.song_to_shortidwinfo(this));
      return shortidwinfo;       
  }
  public void setShortIdWInfo(String val) {
      if ((shortidwinfo == null) || !val.equals(shortidwinfo.toString())) {
          if (shortidwinfo == null)
              shortidwinfo = new MyString(val);
          else
              shortidwinfo.setData(val);
      }
  }
  public String getUser1() {
      if (user1 == null) return "";
      return user1.toString(); }
  public void setUser1(String val) {
      if ((user1 != null) && !val.equals(user1.toString())) {
          invalidate();
          user1 = new MyUserObject(1, val);
      }
  }
  public String getUser2() {
      if (user2 == null) return "";
      return user2.toString(); }
  public void setUser2(String val) {
      if ((user2 != null) && !val.equals(user2.toString())) {
          invalidate();
          user2 = new MyUserObject(2, val);
      }
  }
  public String getUser3() {
      if (user3 == null) return "";
      return user3.toString(); }
  public void setUser3(String val) {
      if ((user3 != null) && !val.equals(user3.toString())) {
          invalidate();
          user3 = new MyUserObject(3, val);
      }
  }
  public String getUser4() {
      if (user4 == null) return "";
      return user4.toString(); }
  public void setUser4(String val) {
      if ((user4 != null) && !val.equals(user4.toString())) {
          invalidate();
          user4 = new MyUserObject(4, val);
      }
  }
  
  public String getFileName() { return filename.toString(); }
  public String getRealFileName() { return FileUtil.getFileObject(filename.toString()).getAbsolutePath(); }
  public void setFilename(String val) {
      if (!val.equals(filename.toString())) {
          if (filename == null)
              filename = new MyString(val);
          else
              filename.setData(val);
          if (SearchPane.instance != null)
              SearchPane.instance.searchlistener.filenameChanged(this);
      }
  }
  public File getFile() {
      return FileUtil.getFileObject(filename.toString());
  }
  
  public void setLastModified() {
      lastmodified = getStringDateFormat(new java.util.Date());
  }
  
  public void setDateAdded(MyString dateadded) { this.dateadded = dateadded; }
  public void setDateAdded(java.util.Date dateadded) { this.dateadded = getStringDateFormat(dateadded); }
  public MyString getDateAddedAsMyString() { return dateadded; }
  public java.util.Date getDateAdded() {
      try {
          return RapidEvolution.instance.yeardateformat.parse(dateadded.toString());
      } catch (Exception e) {         
      }
      return null;
  }
  
  public int getBpmAccuracy() { return bpm_accuracy.data; }
  public void setBpmAccuracy(int accuracy) { bpm_accuracy = new myPercentageInteger(accuracy); }
  
  public int getKeyAccuracy() { return key_accuracy.data; }
  public void setKeyAccuracy(int accuracy) { key_accuracy = new myPercentageInteger(accuracy); }

  public int getTimesPlayed() { return times_played.intValue(); }
  public void setTimesPlayed(int val) { 
      times_played = new Integer(val);
  }

  public static int getNextUniqueID() {
    try {
      getUniqueIDSem.acquire();
      if (!checkuniqueid) {
        minuniqueid++;
        if (minuniqueid == Integer.MAX_VALUE) {
          checkuniqueid = true;
          minuniqueid = 1;
        }
        else {
            int returnval = minuniqueid - 1;
          getUniqueIDSem.release();
          return minuniqueid - 1;
        }
      }      
      while (true) {
        boolean alreadyexists = SongDB.instance.songmap.containsKey(new Integer(minuniqueid));
        if (!alreadyexists) {
            minuniqueid++;
            int returnval = minuniqueid - 1;
          getUniqueIDSem.release();          
          return returnval;
        }
        minuniqueid++;
        if (minuniqueid == Integer.MAX_VALUE) {
            minuniqueid = 1;
        }        
      }
    } catch (Exception e) { log.error("getNextUniqueID(): error", e); }
    getUniqueIDSem.release();
    return 0;
  }

  public boolean hasBpmOrKey() {
    if (getStartbpm() != 0.0f) return true;
    if (getEndbpm() != 0.0f) return true;
    if (getStartKey().isValid()) return true;
    if (getEndKey().isValid()) return true;
    return false;
  }
  
  public static String[] getNewStyleList(String[] stylelist, String newstyle) {        
      if ((newstyle == null) || newstyle.equals("")) return stylelist;
      if (stylelist != null) {
          boolean found = false;
          for (int i = 0; i < stylelist.length; ++i) {
              if (stylelist[i].equalsIgnoreCase(newstyle))
                  found = true;
          }
          if (!found) {
              String[] newstyles = new String[stylelist.length + 1];
              for (int i = 0; i < stylelist.length; ++i) newstyles[i] = stylelist[i];
              newstyles[stylelist.length] = newstyle;                
              stylelist = newstyles;
          }            
      } else {
          stylelist = new String[1];
          stylelist[0] = newstyle;            
      }
      return stylelist;
  }  

  public boolean isCompilationAlbum() {
      if (iscompilation == null) {
          if (isCompilationAlbum(getArtist(), getAlbum()))
              iscompilation = Boolean.TRUE;
          else
              iscompilation = Boolean.FALSE;
      }
      return iscompilation.booleanValue();
  }    
  
  public static boolean isCompilationAlbum(String artist, String album) {
      if ((album == null) || album.equals("")) return false;
      SongLinkedList iter = SongDB.instance.SongLL;
      while (iter != null) {
          if (iter.getAlbum().equalsIgnoreCase(album)) {
              if (!iter.getArtist().equalsIgnoreCase(artist)) return true;
          }
          iter = iter.next;
      }
      return false;
  }    
  
  public boolean hasAlbumCover() {
      if (hasAlbumCover == null) {
          hasAlbumCover = new myBoolean(SongDB.instance.getAlbumCoverImageSet(this) != null);
      }
      return hasAlbumCover.data;
  }
  public myBoolean getHasAlbumCoverMyBoolean() {
      if (hasAlbumCover == null) {
          hasAlbumCover = new myBoolean(SongDB.instance.getAlbumCoverImageSet(this) != null);
      }
      return hasAlbumCover;
  }
  
  public myImageIcon getAlbumCoverLabel() {
      ImageSet imageSet = SongDB.instance.getAlbumCoverImageSet(this);
      if (imageSet != null) {
          myImageIcon imageIcon = ImageIconFactory.getImageIcon(imageSet.getThumbnailFilename(), getAlbum(), myImageIcon.formAlbumCoverToolTip(getArtist(), getAlbum()));
          if (imageIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
              return imageIcon;
          }
      }
      return  new myImageIcon("albumcovers/noalbumcover.gif", getAlbum(), myImageIcon.formAlbumCoverToolTip(getArtist(), getAlbum()));
  }
  
  public MyString getAllStyleString() {
      if (allstylesstring == null) {
          StyleLinkedList siter = SongDB.instance.masterstylelist;
          StringBuffer styles = new StringBuffer();
          int i = 0;
          while (siter != null) {
              if (_styles[i++]) {
                  if (!styles.toString().equals(""))
                          styles.append(", ");
                  styles.append(siter.getName());
              }            
              siter = siter.next;
          }   
          if (allstylesstring == null)
              allstylesstring = new MyString(styles.toString());          
          else
              allstylesstring.setData(styles.toString());
      }
      return allstylesstring;
  }

  public static MyString getStringDateFormat(java.util.Date date) {
      return new MyString(RapidEvolution.yeardateformat.format(date));      
  }
    
  public myBpmObject getBpm() {
      if (bpm == null) {
          bpm = new myBpmObject(_bpm.data, _endbpm.data);
      }
      return bpm;      
  }
  
  public MyString getProcessedComments() {
      if (processed_comments == null) {
          processed_comments = StringUtil.processcomments(_comments);
      }
      return processed_comments;
  }
  
  public myBoolean getAnalog() {
      if (analog == null) {
          analog = new myBoolean(vinylonlybit || !nonvinylonlybit);
      }
      return analog;
  }
  
  public myBoolean getDigital() {
      if (digital == null) {
          digital = new myBoolean(nonvinylonlybit || !vinylonlybit);
      }
      return digital;
  }
  
  private Integer getNumMixouts() {
      if (nummixouts == null) {
          nummixouts = new Integer(get_num_mixouts());
      }
      return nummixouts;
  }
  
  private Integer getNumAddons() {
      if (numaddons == null) {
          int addoncount = 0;
          for (int i = 0; i < getNumMixoutSongs(); ++i) if (_mixout_addons[i]) addoncount++;
          numaddons = new Integer(addoncount);          
      }
      return numaddons;
  }  
  
  public myPercentageFloat getStyleSimilarityWithSelectedStyles() {
      StyleLinkedList siter = SongDB.instance.masterstylelist;
      int denominator = 0;
      int numerator = 0;
      int index = 0;            
      boolean[] ids_B = getLogicalStyleIds();      

      while (siter != null) {
          boolean is_selected = StylesPane.instance.styletree.isALogicalSelectedStyle(siter);
          if (!siter.isCategoryOnly()) {          
              if ((is_selected) || ids_B[index]) denominator++;
              if ((is_selected) && ids_B[index]) numerator++;
          }
        siter = siter.next;
        index++;
      }
      if (denominator == 0) return myPercentageFloat.default_value;
      return new myPercentageFloat((float)numerator / (float)denominator);
  }
  
  public myPercentageFloat getStyleSimilarity(SongLinkedList song) {
      if (song == null) return myPercentageFloat.default_value;
      StyleLinkedList siter = SongDB.instance.masterstylelist;
      int denominator = 0;
      int numerator = 0;            
      boolean[] ids_A = getLogicalStyleIds();
      boolean[] ids_B = song.getLogicalStyleIds();      
      for (int i = 0; i < ids_A.length; ++i) {
          if ((siter.get_sindex() == i) && !siter.isCategoryOnly()) {
              if (ids_A[i] || ids_B[i]) ++denominator;
              if (ids_A[i] && ids_B[i]) ++numerator;
          }
          siter = siter.next;
      }                  
      if (denominator == 0) return myPercentageFloat.default_value;
      return new myPercentageFloat((float)numerator / (float)denominator);      
  }
  
  public Object get_column_data(int cindex) {
      try {
	      if (cindex == ColumnConfig.COLUMN_SONGID) return getSongIdShortMyString();
	      if (cindex == ColumnConfig.COLUMN_ARTIST) return _artist;
	      if (cindex == ColumnConfig.COLUMN_ALBUM) return _album;
	      if (cindex == ColumnConfig.COLUMN_NUM_PLAYS) return times_played;
	      if (cindex == ColumnConfig.COLUMN_KEYACCURACY) return key_accuracy;
	      if (cindex == ColumnConfig.COLUMN_BPMACCURACY) return bpm_accuracy;
	      if (cindex == ColumnConfig.COLUMN_HAS_ALBUM_COVER) return getHasAlbumCoverMyBoolean();        
          if (cindex == ColumnConfig.COLUMN_ALBUM_COVER) return getAlbumCoverLabel();        
	      if (cindex == ColumnConfig.COLUMN_STYLES) return getAllStyleString();
	      if (cindex == ColumnConfig.COLUMN_DATEADDED) return dateadded;
	      if (cindex == ColumnConfig.COLUMN_LAST_MODIFIED) return lastmodified;
	      if (cindex == ColumnConfig.COLUMN_SHORTID) return getShortIdMyString();
	      if (cindex == ColumnConfig.COLUMN_RATING) return ratingbar;
	      if (cindex == ColumnConfig.COLUMN_SHORTIDWINFO) return getShortIdWInfoMyString();
	      if (cindex == ColumnConfig.COLUMN_KEYCODE) return songKey.getKeyCode();
	      if (cindex == ColumnConfig.COLUMN_TRACK) return _track;
	      if (cindex == ColumnConfig.COLUMN_TITLE) return _songname;
	      if (cindex == ColumnConfig.COLUMN_TIME) return _tracktime;
	      if (cindex == ColumnConfig.COLUMN_TIMESIG) return _timesig;
	      if (cindex == ColumnConfig.COLUMN_BPMSTART) return _bpm;
	      if (cindex == ColumnConfig.COLUMN_STYLESIMILARITY)  {
	        if (OptionsUI.instance.useselectedstylesimilarity.isSelected() && !SongDB.instance.isAddingSong) {
	            return getStyleSimilarityWithSelectedStyles();
	        } else {
	            return getStyleSimilarity(RapidEvolutionUI.instance.currentsong);
	        }
	      }
	      if (cindex == ColumnConfig.COLUMN_ARTISTSIMILARITY) {
	        if (RapidEvolutionUI.instance.currentsong != null) {
	            String current_searchartist = Artist.encodeString(RapidEvolutionUI.instance.currentsong.getArtist());          
	            if (current_searchartist.equals("")) return myPercentageFloat.default_value;
	            String searchartist = Artist.encodeString(getArtist());
	            if (searchartist.equals("")) return myPercentageFloat.default_value;
	            Artist artist1 = (Artist)Artist.masterartistlist.get(current_searchartist);
	            Artist artist2 = (Artist)Artist.masterartistlist.get(searchartist);
	            if ((artist1 != null) && (artist2 != null)) {
	                return new myPercentageFloat((float)artist1.getStyleSimilarity(artist2));
	            }
	        }
	        return myPercentageFloat.default_value;
	      }
	      if (cindex == ColumnConfig.COLUMN_BPMEND) return _endbpm;
	      if (cindex == ColumnConfig.COLUMN_BPM) return getBpm();
	      if (cindex == ColumnConfig.COLUMN_KEYSTART) return songKey.getStartKey();
	      if (cindex == ColumnConfig.COLUMN_KEYEND) return songKey.getEndKey();
	      if (cindex == ColumnConfig.COLUMN_KEY) return songKey;
	      if (cindex == ColumnConfig.COLUMN_SONGCOMMENTS) return getProcessedComments();
	      if (cindex == ColumnConfig.COLUMN_REMIX) return _remixer;
	      if (cindex == ColumnConfig.COLUMN_USER1) return user1;
	      if (cindex == ColumnConfig.COLUMN_USER2) return user2;
	      if (cindex == ColumnConfig.COLUMN_USER3) return user3;
	      if (cindex == ColumnConfig.COLUMN_USER4) return user4;
	      if (cindex == ColumnConfig.COLUMN_FILENAME) return filename;
	      if (cindex == ColumnConfig.COLUMN_ANALOG) return getAnalog();
	      if (cindex == ColumnConfig.COLUMN_DIGITAL) return getDigital();
	      if (cindex == ColumnConfig.COLUMN_DISABLED) return disabled;
	      if (cindex == ColumnConfig.COLUMN_BPMSHIFT) {
	        if ((RapidEvolution.instance.getActualBpm() != 0.0f) && (_bpm.data != 0.0f)) {
	          return new MyStringFloat(String.valueOf(SongUtil.get_bpmdiff(_bpm.data, RapidEvolution.instance.getActualBpm())));
	        } else return MyStringFloat.default_value;
	      }
	      if (cindex == ColumnConfig.COLUMN_KEYLOCK) {
            SongKeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey());
            return relation.getRecommendedKeyLockSetting();
	      }
	      if (cindex == ColumnConfig.COLUMN_NUMMIXOUTS) return getNumMixouts();
	      if (cindex == ColumnConfig.COLUMN_SONGIDWINFO) return getSongIdMyString();
	      if (cindex == ColumnConfig.COLUMN_NUM_ADDONS)  return getNumAddons();
	      if (cindex == ColumnConfig.COLUMN_BPMDIFF) {
	        if ((RapidEvolutionUI.instance.currentsong != null) && ((RapidEvolutionUI.instance.currentsong._bpm.data != 0.0f) || (RapidEvolutionUI.instance.currentsong._endbpm.data != 0.0f)) && (_bpm.data != 0.0f)) {
	          float usebpm = RapidEvolutionUI.instance.currentsong._bpm.data;
	          if (RapidEvolutionUI.instance.currentsong._endbpm.data != 0.0f) usebpm = RapidEvolutionUI.instance.currentsong._endbpm.data;
	          return new MyStringFloat(String.valueOf(SongUtil.get_bpmdiff(_bpm.data, usebpm)));
	        } else if ((RapidEvolution.instance.getActualBpm() != 0.0f) && (_bpm.data != 0.0f)) {
	          return new MyStringFloat(String.valueOf(SongUtil.get_bpmdiff(_bpm.data, RapidEvolution.instance.getActualBpm())));
	        } else return MyStringFloat.default_value;
	      }
	      if (cindex == ColumnConfig.COLUMN_KEYRELATION) {
            SongKeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey());
            return relation.getBestKeyRelation();
	      }
	      if (cindex == ColumnConfig.COLUMN_COLORSIMILARITY) {
	        if (RapidEvolutionUI.instance.currentsong != null) {
	          if ((RapidEvolutionUI.instance.currentsong.color != null) && (color != null)) {
	            return new myColorFloat((float)(color.getDiff(RapidEvolutionUI.instance.currentsong.color)));
	          }
	        }
	        return myColorFloat.default_value;
	      }
	      if (cindex == ColumnConfig.COLUMN_BEAT_INTENSITY) {
	          return beat_intensity;
	      }
	      if (cindex == ColumnConfig.COLUMN_ACTUAL_KEY) {
	          double bpmDifference = Bpm.getBpmDifference(_bpm.data, RapidEvolution.instance.getActualBpm());	          
	          return getStartKey().getShiftedKeyByBpmDifference(bpmDifference);
	      }
	      if (cindex == ColumnConfig.COLUMN_ACTUAL_KEY_CODE) {
	          double bpmDifference = Bpm.getBpmDifference(_bpm.data, RapidEvolution.instance.getActualBpm());	          
	          return getStartKey().getShiftedKeyByBpmDifference(bpmDifference).getKeyCode();
	      }
	      if (cindex == ColumnConfig.COLUMN_PITCH_SHIFT) {
	          KeyRelation relation = Key.getClosestKeyRelation(this, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey()).getBestKeyRelation();
	          if (relation.hasDifference())
	              return new MyCents((float)relation.getDifference());
	          return MyCents.NO_CENTS;
	      }
          if (cindex == ColumnConfig.COLUMN_REPLAY_GAIN) {
              return getRGA();
          }
	      log.warn("get_column_data(): no matching column, cindex=" + cindex);
      } catch (Exception e) {
          log.error("get_column_data(): error Exception", e);
      }
      return null;
    }
  
      private RGAData rga = RGAData.NO_RGA;
      public void setRGA(RGAData data) {
          if (data != null)
              rga = data;
      }
      public RGAData getRGA() { return rga; }
      public void setRGA(float value) { rga = new RGAData(value); }
  
  	public void copyValuesFrom(SongLinkedList datasong, boolean overwrite) {
  	    PopulateSong(this, datasong, overwrite);
  	}
  	
  	public String getGenre() {
  	    HashMap style_map = new HashMap();
  	    StyleLinkedList siter = SongDB.instance.masterstylelist;
  	    while (siter != null) {
  	        if (siter.containsLogical(this) && !siter.isCategoryOnly()) {
                addStyleCount(style_map, siter);
                siter.countParentStyles(style_map);
  	        }
  	        siter = siter.next;
  	    }
  	    //if (log.isTraceEnabled()) log.trace("getGenre(): style map=" + style_map);
  	    int max_count = 0;
  	    int max_distance = 0;  	   
  	    int max_total_logical = 0;
  	  StyleLinkedList max_style = null;
  	    Iterator iter = style_map.entrySet().iterator();
  	    while (iter.hasNext()) {
  	        Entry entry = (Entry)iter.next();
  	        StyleLinkedList style = (StyleLinkedList)entry.getKey();
  	        int count = ((Integer)entry.getValue()).intValue();
  	        int distance = style.getDistanceToRoot();
  	        if (!style.equals(StyleLinkedList.root_style)) {
  	            boolean set = false;
  	            if ((max_style == null) || (count > max_count)) {
  	                set = true;
  	            } else if (count == max_count) {
  	                if ((style.getDistanceToRoot() < max_distance)) {
  	                    set = true;
  	                } else if (style.getDistanceToRoot() == max_distance) {
  	                    if (style.countTotalLogicalSongs() < max_total_logical) {
  	                        set = true;
  	                    }
  	                }
  	            }
  	            if (set) {
  	                max_style = style;
  	                max_distance = style.getDistanceToRoot();
  	                max_count = count;
  	                max_total_logical = style.countTotalLogicalSongs();  	                
  	            }
  	        }
  	    }
  	    if (max_style != null)
  	        return max_style.getName();
  	    return "";
  	}
  	
  	private void addStyleCount(HashMap style_map, StyleLinkedList style) {
  	    Integer count = (Integer)style_map.get(style);
  	    if (count != null) {
  	        count = new Integer(count.intValue() + 1);
  	        style_map.put(style, count);
  	    } else {
  	        style_map.put(style, new Integer(1));
  	    }
  	}

  	public void calculateSongDisplayIds() {
  	    calculateSongDisplayIds(false);
  	}  	
  	public void calculateSongDisplayIds(boolean onlyIfEmpty) {
  	    calculateSongDisplayIds(onlyIfEmpty, this);
  	}
  	public void calculateSongDisplayIds(SongLinkedList songToCalculateWith) {
  	  calculateSongDisplayIds(false, songToCalculateWith);
  	}
  	public void calculateSongDisplayIds(boolean onlyIfEmpty, SongLinkedList songToCalculateWith) {
        if (!onlyIfEmpty || ((getSongId() == null) || getSongId().equals(""))) setSongId(SongLinkedList.song_to_string(songToCalculateWith));
        if (!onlyIfEmpty || ((getSongIdShort() == null) || getSongIdShort().equals(""))) setSongIdShort(SongLinkedList.song_to_string_short(songToCalculateWith));
        if (!onlyIfEmpty || ((getShortId() == null) || getShortId().equals(""))) setShortId(SongLinkedList.song_to_shortid(songToCalculateWith));
        if (!onlyIfEmpty || ((getShortIdWInfo() == null) || getShortIdWInfo().equals(""))) setShortIdWInfo(SongLinkedList.song_to_shortidwinfo(songToCalculateWith));  	    
  	}  
  	
  	static public void resetColorCache() {
  	    SongLinkedList iter = SongDB.instance.SongLL;
  	    while (iter != null) {
  	        iter.resetSearchColorCache();
  	        iter = iter.next;
  	    }
  	   
  	}
    
  	public int hashCode() {
  		return (int)uniquesongid;
  	}
  	
    public boolean equals(Object o) {
        if (o instanceof SongLinkedList) {
            SongLinkedList os = (SongLinkedList)o;
            return uniquesongid == os.uniquesongid;
        }
        return false;
    }
    
    public String getCustomDataType(String typeDescription) {
        if (typeDescription.equals(OptionsUI.instance.custom_field_1_tag_combo.getSelectedItem()))
            return user1.toString();
        if (typeDescription.equals(OptionsUI.instance.custom_field_2_tag_combo.getSelectedItem()))
            return user2.toString();
        if (typeDescription.equals(OptionsUI.instance.custom_field_3_tag_combo.getSelectedItem()))
            return user3.toString();
        if (typeDescription.equals(OptionsUI.instance.custom_field_4_tag_combo.getSelectedItem()))
            return user4.toString();
        return null;
    }
  	
};

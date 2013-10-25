package rapid_evolution;

import rapid_evolution.comparables.myImageIcon;
import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.audio.dsps.rga.RGAData;

public class OldSongValues {

    private String uniquestringid = "";
    private String artist = "";
    private String album = "";
    private String user1 = "";
    private String user2 = "";
    private String user3 = "";
    private String user4 = "";
    private String track = "";
    private String title = "";
    private String remix = "";
    private float startbpm = 0.0f;
    private float endbpm = 0.0f;
    private myImageIcon albumCover = null;
    private String comments = "";
    private String genre = "";
    private Key startKey = null;
    private Key endKey = null;
    private int rating;
    private RGAData rga;
    private String[] styles;
    private String time;
    private String timesig;
    
    public OldSongValues(SongLinkedList song) {
        if (song != null) {
            uniquestringid = song.getUniqueStringId();
            artist = song.getArtist();
            album = song.getAlbum();
            user1 = song.getUser1();
            user2 = song.getUser2();
            user3 = song.getUser3();
            user4 = song.getUser4();
            track = song.getTrack();
            title = song.getSongname();
            remix = song.getRemixer();
            startbpm = song.getStartbpm();
            endbpm = song.getEndbpm();
            albumCover = song.getAlbumCoverLabel();
            comments = song.getComments();
            genre = song.getGenre();
            song.getKey();
            startKey = song.getStartKey();
            endKey = song.getEndKey();
            if (startKey == null)
            	startKey = Key.NO_KEY;
            if (endKey == null)
            	endKey = Key.NO_KEY;
            rating = song.getRatingInt();
            rga = song.getRGA();
            styles = song.getStyleStrings();
            time = song.getTime();
            timesig = song.getTimesig();
        }
    }
    
    public String getUniqueStringId() { return uniquestringid; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getUser1() { return user1; }
    public String getUser2() { return user2; }
    public String getUser3() { return user3; }
    public String getUser4() { return user4; }
    public String getTrack() { return track; }
    public String getTitle() { return title; }
    public String getRemix() { return remix; }
    public float getStartBpm() { return startbpm; }
    public float getEndBpm() { return endbpm; }
    public myImageIcon getAlbumCoverLabel() { return albumCover; }
    public String getComments() { return comments; }
    public String getGenre() { return genre; }
    public Key getStartKey() { return startKey; }
    public Key getEndKey() { return endKey; }
    public int getRating() { return rating; }
    public RGAData getRGA() { return rga; }
    public boolean areStylesEqualWith(String[] inputStyles) {
    	if (inputStyles.length != styles.length)
    		return false;
    	for (int i = 0; i < inputStyles.length; ++i) {
    		boolean found = false;
    		int j = 0;
    		while (!found && (j < styles.length)) {
    			if (inputStyles[i].equals(styles[j]))
    				found = true;    				
    			++j;
    		}
    		if (!found)
    			return false;
    	}
    	return true;
    }
    public String getTime() { return time; }
    public String getTimesig() { return timesig; }
    
}

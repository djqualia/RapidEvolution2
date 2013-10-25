package rapid_evolution.ui;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;

public class RatingToolBar implements Comparable {

    private static Logger log = Logger.getLogger(RatingToolBarFlyWeight.class);
    
    private SongLinkedList song;
    private RatingToolBarFlyWeight rating_fw;
    
    public static RatingToolBarFlyWeight rating0 = new RatingToolBarFlyWeight((char)0);
    public static RatingToolBarFlyWeight rating1 = new RatingToolBarFlyWeight((char)1);
    public static RatingToolBarFlyWeight rating2 = new RatingToolBarFlyWeight((char)2);
    public static RatingToolBarFlyWeight rating3 = new RatingToolBarFlyWeight((char)3);
    public static RatingToolBarFlyWeight rating4 = new RatingToolBarFlyWeight((char)4);
    public static RatingToolBarFlyWeight rating5 = new RatingToolBarFlyWeight((char)5);
        
    public String toString() {
        char rating = rating_fw.getRating();
        String rating_str = "none";
        if (rating == 1) rating_str = "1";
        else if (rating == 2) rating_str = "2";
        else         if (rating == 3) rating_str = "3";
        else         if (rating == 4) rating_str = "4";
        else         if (rating == 5) rating_str = "5";
        return rating_str;// + " [" + super.toString() + "]";
    }
    
    private void setRatingFW(char rating) {
        if (rating == 0) rating_fw = rating0;
        else if (rating == 1) rating_fw = rating1;
        else if (rating == 2) rating_fw = rating2;
        else if (rating == 3) rating_fw = rating3;
        else if (rating == 4) rating_fw = rating4;
        else if (rating == 5) rating_fw = rating5;
    }
    
    public RatingToolBar(SongLinkedList song) {
        if ((rapid_evolution.RapidEvolution.instance != null) && rapid_evolution.RapidEvolution.instance.loaded && log.isTraceEnabled()) log.trace("RatingToolBar(): song=" + song + ", rating=" + song.getRating());
        this.song = song;
        setRatingFW(song.getRating());
    }
    
    public RatingToolBarFlyWeight getComponent() { return rating_fw; };
    
    public int compareTo(Object o) {
        if (log.isTraceEnabled()) log.trace("compareTo(): this=" + this + ", o=" + o);
        int returnval = -1;
        if (o instanceof RatingToolBar) {
            RatingToolBar bar = (RatingToolBar)o;
            if ((song.getRating() > 0) && (bar.song.getRating() > 0)) {
                if (song.getRating() < bar.song.getRating()) returnval = -1;
                else if (song.getRating() > bar.song.getRating()) returnval = 1;
                else returnval = 0;
            } else if ((song.getRating() == 0) && (bar.song.getRating() == 0)) {
                returnval = 0;
            } else {
                if (song.getRating() < bar.song.getRating()) returnval = -1;
                else if (song.getRating() > bar.song.getRating()) returnval = 1;
                else returnval = 0;
            }
        }
        if (log.isTraceEnabled()) log.trace("compareTo(): returnval=" + returnval);        
        return returnval;
    }
    
    public boolean equals(Object o) {
        if (o != null) {
            String thisString = toString();
            if (thisString != null) {
                return thisString.equalsIgnoreCase(o.toString());
            }
        }
        return false;
    }
    
    public int hashCode() {
        return toString().toLowerCase().hashCode();
    }
    
    public void setRating(char value) {
        if (log.isTraceEnabled()) log.trace("setRating(): this=" + this + ", value=" + (int)value);
        setRatingFW(value);              
    }
    

}

package rapid_evolution;

import com.mixshare.rapid_evolution.music.SongKey;

public class FilterMusicKey extends SongKey {
    
    public FilterMusicKey(SongKey input) {
        super(input.getStartKey(), input.getEndKey());        
    }
    
    public String toString() {
        String result = super.toString();
        if ((result == null) || result.equals(""))
            return Filter.noValueString;
        return result;
    }

}

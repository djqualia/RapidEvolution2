package rapid_evolution;

import com.mixshare.rapid_evolution.music.SongKeyCode;

public class FilterMusicKeyCode extends SongKeyCode {
    
    public FilterMusicKeyCode(SongKeyCode keyCode) {
        super(keyCode.getSongKey());        
    }

    public String toString() {
        String result = super.toString();
        if ((result == null) || result.equals(""))
            return Filter.noValueString;
        return result;
    } 
}

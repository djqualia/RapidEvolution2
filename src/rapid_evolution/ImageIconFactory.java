package rapid_evolution;

import java.io.File;
import java.util.HashMap;

import rapid_evolution.comparables.myImageIcon;
import rapid_evolution.util.OSHelper;

public class ImageIconFactory {

    private static HashMap imageicons = new HashMap();
    
    public static myImageIcon getImageIcon(String filename, String album, String artist) {
        String key = artist.toLowerCase() + " - " + album.toLowerCase();
        if (imageicons.containsKey(key)) {
            return (myImageIcon)imageicons.get(key);
        } else {            
            File file = new File(filename);
            myImageIcon icon = null;
            if (file.exists()) {
                icon = new myImageIcon(filename, album, myImageIcon.formAlbumCoverToolTip(artist, album)) ;
            } else {
                icon = new myImageIcon("albumcovers/noalbumcover.gif", album, myImageIcon.formAlbumCoverToolTip(artist, album));
            }                
            imageicons.put(key, icon);
            return icon;
        }
    }
    
    public static void reset() {
        imageicons.clear();
    }
   
}

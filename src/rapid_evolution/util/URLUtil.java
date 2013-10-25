package rapid_evolution.util;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import rapid_evolution.Artist;
import rapid_evolution.RapidEvolution;

import java.io.File;

import org.apache.log4j.Logger;

public class URLUtil {

    private static Logger log = Logger.getLogger(URLUtil.class);
    
    public static boolean saveImage(String image_url, String saveas_filename) {
        // returns true if successful
      DataInputStream di = null;
      FileOutputStream fo = null;
      byte [] b = new byte[1];         
      try {
        // input
        URL url = new URL(image_url);
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        di = new DataInputStream(urlConnection.getInputStream());

        // output        
        File saveas = new File(saveas_filename);
        fo = new FileOutputStream(saveas);

        //  copy the actual file
        //   (it would better to use a buffer bigger than this)
        while(-1 != di.read(b,0,1))
          fo.write(b,0,1);        
          di.close();  
          fo.close();    
          return true;
          }
        catch (Exception ex) {
            log.error("saveImage(): failed to save URL to disk: " + image_url, ex);
          return false;
          }
      }
    
}

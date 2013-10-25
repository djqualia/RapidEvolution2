package rapid_evolution.util;

import java.io.*;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class UTF8BufferedWriter extends BufferedWriter {

    public UTF8BufferedWriter(FileWriter fw) {
        super(fw);
    }
    
    public void write(String value, boolean encode) throws IOException {
/*        if (encode) {
            super.write(URLEncoder.encode(value, "UTF8"));
            
//            value = new String(value.getBytes( "UTF-8" ));
//            super.write(value);
            
        } 
        else 
            
            */super.write(value);
    }
    
    
}

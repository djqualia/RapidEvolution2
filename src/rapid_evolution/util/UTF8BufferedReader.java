package rapid_evolution.util;

import java.io.*;
import java.net.URLDecoder;

import rapid_evolution.StringUtil;

public class UTF8BufferedReader extends BufferedReader {

    boolean disable = true;
    public UTF8BufferedReader(FileReader br, float version) {
        super(br);
        if (version >= 2.85f) disable = false;
    }
    
    public String readLine(boolean decode) throws IOException {
/*
        if (!disable && decode) {
            return new String( super.readLine().getBytes(), "UTF-8" );
//            return URLDecoder.decode(super.readLine(), "UTF-8");
        }
        else 
        */
        return StringUtil.cleanString(super.readLine());
    }
       
}

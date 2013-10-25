package com.mixshare.rapid_evolution.audio.tags.readers.mp3;

import org.apache.log4j.Logger;

import quicktime.QTException;
import quicktime.io.IOConstants;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.movies.Movie;
import quicktime.std.movies.media.UserData;
import rapid_evolution.StringUtil;

import com.mixshare.rapid_evolution.audio.tags.readers.QTTagReader;
import com.mixshare.rapid_evolution.qt.QTConstants;
import com.mixshare.rapid_evolution.qt.QTSessionCheck;
  
public class QTID3TagReader extends QTTagReader implements QTConstants {
 
    private static Logger log = Logger.getLogger(QTID3TagReader.class);
       
    private Movie movie = null;
    private UserData userData = null;
  
  public QTID3TagReader(String filename) {
      try {
          QTSessionCheck.check();
          QTFile f = new QTFile(filename);
          OpenMovieFile omf = OpenMovieFile.asRead(f);
          movie = Movie.fromFile(omf);
          userData = movie.getUserData();
          if (userData != null) {
              if (log.isDebugEnabled()) {
                  int nextType = userData.getNextType(0);
                  while (nextType != 0) {
                      String value = userData.getTextAsString(nextType, 1, IOConstants.langUnspecified);
                      log.debug("QTID3TagReader(): tag=" + nextType + ", value=" + value);
                      nextType = userData.getNextType(nextType);
                  }
              }
          }
      } catch (Exception e) {
          log.error("QTID3TagReader(): error Exception", e);
      }
  }
  
  public boolean isFileSupported() {
      return userData != null;
  }  
  
  public String getProperty(int tag) {      
      String result = "";
      try {
          if (userData != null)
              result = userData.getTextAsString(tag, 1, IOConstants.langUnspecified);
      } catch (QTException qte) { } // no such tag
      return result;
  }
  
  public String getTime() {
      String time = "";
      if (movie != null) {
          try {
              int seconds = movie.getDuration() / movie.getTimeScale();
              time = StringUtil.seconds_to_time(seconds / 1000);
          } catch (Exception e) { }
      }
      if (log.isDebugEnabled()) log.debug("getTime(): time=" + time);
      return time;
  }
  
}


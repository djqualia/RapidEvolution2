package rapid_evolution.audio;

import java.util.Vector;
import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.AddSongKeyProgressUI;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.audio.codecs.NoDecoderAvailableException;

import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.audio.AudioEngine;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class adddetectfilethread extends Thread {
    
    private static Logger log = Logger.getLogger(adddetectfilethread.class);        
    
  public adddetectfilethread(String in_file) {
	  filename = in_file;
	  setPriority(Thread.NORM_PRIORITY - 1);
  }
  
  String filename;
  public void run() {
      try {
          KeyDetector keydetector = new KeyDetector(KeyDetector.UI_ADDDIALOG);
          keydetector.detectKeyFromFile(filename);
      } catch (NoDecoderAvailableException nda) {
          new rapid_evolution.threads.ErrorMessageThread("key detector error", nda.toString()).start();          
      } catch (Exception e) {
          log.error("adddetectfilethread(): error", e);
      }
  }
}

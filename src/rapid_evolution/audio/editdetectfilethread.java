package rapid_evolution.audio;

import java.util.Vector;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Logger;

import rapid_evolution.ui.EditSongKeyProgressUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.KeyDetector;
import com.mixshare.rapid_evolution.audio.codecs.NoDecoderAvailableException;

public class editdetectfilethread extends Thread {
    
    private static Logger log = Logger.getLogger(editCaptureThread.class);
    
  public editdetectfilethread(String in_file) {
	  filename = in_file;
	  setPriority(Thread.NORM_PRIORITY - 1);
  }
  String filename;
  public void run() {
      try {
          KeyDetector keydetector = new KeyDetector(KeyDetector.UI_EDITDIALOG);
          keydetector.detectKeyFromFile(filename);
      } catch (NoDecoderAvailableException nda) {
        new rapid_evolution.threads.ErrorMessageThread("key detector error", nda.toString()).start();          
      } catch (Exception e) {
          log.error("run(): error", e);
      }    
  }
}

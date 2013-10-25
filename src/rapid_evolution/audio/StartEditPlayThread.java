package rapid_evolution.audio;

import java.io.File;
import javax.sound.sampled.AudioFormat;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Logger;

import rapid_evolution.ui.EditSongUI;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.ui.SkinManager;

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

public class StartEditPlayThread extends Thread {
    
    private static Logger log = Logger.getLogger(StartEditPlayThread.class);
    
  public StartEditPlayThread(String file) { filename = file; }
  public void run() {
    try {
    File file = new File(filename);
    AudioInputStream in= AudioSystem.getAudioInputStream(file);
//    AudioFileFormat aff = AudioSystem.getAudioFileFormat(in);
    AudioInputStream din = null;
    AudioFormat baseFormat = in.getFormat();
    AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                                                  baseFormat.getSampleRate(),
                                                                                  16,
                                                                                  baseFormat.getChannels(),
                                                                                  baseFormat.getChannels() * 2,
                                                                                  baseFormat.getSampleRate(),
                                                                                  false);
    AudioEngine.instance.mainAudioFormat = decodedFormat;
    din = AudioSystem.getAudioInputStream(decodedFormat, in);
    AudioEngine.instance.rawplay(decodedFormat, din);
    in.close();
    AudioEngine.instance.mainAudioFormat = null;
  } catch (Exception e)
    {
      EditSongUI.instance.editsongsplaybutton.setText(SkinManager.instance.getTextFor(EditSongUI.instance.editsongsplaybutton));
      AudioEngine.instance.editsongsplaying = false;
      AudioEngine.instance.EditSongStopPlayback = true;
      log.error("run(): error", e);
    }
  }
  String filename;
}

package rapid_evolution.audio;

import javax.sound.sampled.AudioFormat;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

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

public class PlayThread extends Thread{
    
    private static Logger log = Logger.getLogger(PlayThread.class);
    
  SourceDataLine srcDataLine;
  int quit;
  AudioFormat audioFormat;
  AudioInputStream audioInputStream;
  public PlayThread(SourceDataLine sdl, int stop, AudioFormat format, AudioInputStream ais) { super(); srcDataLine = sdl; quit = stop; audioFormat = format; audioInputStream = ais; }
  byte tempBuffer[] = new byte[10000];
  public void run(){
    try{
      srcDataLine.open(audioFormat);
      srcDataLine.start();
      int cnt;
      //Keep looping until the input read method
      // returns -1 for empty stream or the
      // user clicks the Stop button causing
      // stopPlayback to switch from false to
      // true.
      if (quit == 0) {
        while ( (cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) !=
               -1
               && AudioEngine.instance.EditSongStopPlayback == false) {
          if (cnt > 0) {
            // Write data to the internal buffer of
            // the data line where it will be
            // delivered to the speaker.
            srcDataLine.write(tempBuffer, 0, cnt);
          }
        }
        srcDataLine.drain();
        srcDataLine.close();
        AudioEngine.instance.EditSongStopPlayback = false;
      }
      if (quit == 1) {
        while ( (cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) !=
               -1
               && AudioEngine.instance.AddSongStopPlayback == false) {
          if (cnt > 0) {
            // Write data to the internal buffer of
            // the data line where it will be
            // delivered to the speaker.
            srcDataLine.write(tempBuffer, 0, cnt);
          }
        }
        srcDataLine.drain();
        srcDataLine.close();
        AudioEngine.instance.AddSongStopPlayback = false;
      }
    } catch (Exception e) { log.error("run(): error", e); }
  }
}

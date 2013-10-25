package rapid_evolution;

import java.io.File;

import org.apache.log4j.Logger;

import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.SubBandSeperator;

import com.mixshare.rapid_evolution.audio.AudioBuffer;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoderFactory;
import com.mixshare.rapid_evolution.thread.Task;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class AudioColor {
  public AudioColor() {
  }

  private static Logger log = Logger.getLogger(AudioColor.class);

//  static File detectcoloroutputfile = null;
  static Semaphore DetectingColorSem = null; //new Semaphore(1);
 
  private static final int EXTERNAL_BUFFER_SIZE = 128000;
  private static int analyzechunksize = 8192;

  
  static public void DetermineColorOfSong(SongLinkedList song, boolean beatintensity_only, Task task) {
      AudioDecoder decoder = null;
      try {
      if (DetectingColorSem != null) DetectingColorSem.acquire();
      log.debug("DetermineColorOfSong(): detecting beat intensity/color from file: " + song.getRealFileName());

      SubBandSeperator subband = null;
      double minbpm = rapid_evolution.audio.Bpm.minbpm;  // minimum bpm to search (lower bound cap)
      double maxbpm = rapid_evolution.audio.Bpm.maxbpm; // maximum bpm to search (upper bound cap)

      File file = FileUtil.getFileObject(song.getFileName());
      AudioEngine.instance.detectbpmoutputfile = file;

      decoder = AudioDecoderFactory.getAudioDecoder(file.getAbsolutePath());
      if (decoder != null) {
          double seconds = decoder.getTotalSeconds();
          if (RapidEvolution.instance.terminatesignal) throw new Exception();
      
          // read WAV data and merge to a single channel, then seperate channel into frequency bands
          if (subband == null) subband = new SubBandSeperator((float)decoder.getSampleRate(), minbpm, maxbpm, seconds, task);
          subband.colormode = true;
          SongColor scolor = subband.scolor;

          double[] wavearray = new double[analyzechunksize];
          long frames_read = decoder.readFrames(wavearray.length);
          boolean done = false;
          boolean aborted = false;
          int total_frames_read = 0;
	      PaceMaker pacer = new PaceMaker();          
          while ((frames_read > 0) && !done) {
              pacer.startInterval();
              if (frames_read == analyzechunksize) {
                  AudioBuffer buffer = decoder.getAudioBuffer();
                  double[][] data = buffer.getSampleData();
                  for (int f = 0; f < wavearray.length; ++f) {
                      double val = 0.0;
                      for (int c = 0; c < buffer.getNumChannels(); ++c) {
                          val += data[c][f];
                      }
                      wavearray[f] = val;
                  }
                  subband.send(wavearray);
                  if (subband.lockedon) done = true;
              }
              total_frames_read += frames_read;
              if (RapidEvolution.instance.terminatesignal || task.isCancelled()) {
                  done = true;
                  aborted = true;
              }
              if (!done)
                  frames_read = decoder.readFrames(wavearray.length);
              pacer.endInterval();
          }
          
          if (!aborted) {
        	  log.debug("DetermineColorOfSong(): " + total_frames_read + " frames read");
              subband.determineBeatProperties(scolor);
              log.debug("DetermineColorOfSong(): song color=" + scolor);
              if (beatintensity_only) {
                  int beat_intensity = subband.getBeatIntensity();
                  song.setBeatIntensity(beat_intensity);
                  log.debug("DetermineColorOfSong(): beat intensity=" + beat_intensity);
              } else {
                  song.color = scolor;
              }                        
          }          
      }      
      
    } catch (Exception e) {
        log.error("DetermineColorOfSong(): error", e);
    }
    if (decoder != null) decoder.close();
    if (DetectingColorSem != null) DetectingColorSem.release();
    //if (detectcoloroutputfile != null) detectcoloroutputfile.delete();
  }
}

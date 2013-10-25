package rapid_evolution.audio;

import java.util.Date;

import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.threads.OutOfMemoryThread;
import rapid_evolution.ui.AddSongKeyProgressUI;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongKeyProgressUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.audio.AudioBuffer;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoderFactory;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;
import com.mixshare.rapid_evolution.audio.codecs.NoDecoderAvailableException;
import com.mixshare.rapid_evolution.thread.Task;

public class KeyDetector {
    
    private static Logger log = Logger.getLogger(KeyDetector.class);
    
  public KeyDetector() { }
  public KeyDetector(int ui_mode) {
    uimode = ui_mode;
  }

  private void init_keycount(double segmentTime) {
      segment_probabilities = new KeyProbability(segmentTime);
    keyenergy = new double[12];
    for (int i = 0; i < keyenergy.length; ++i) keyenergy[i] = 0.0;
  }

  public static int analyzechunksize = 8192; // 0.19 seconds (harmonics produce negligible effects on this sampling size)

  private static final int EXTERNAL_BUFFER_SIZE = 128000;

  private AudioDecoder decoder = null;
  
  private KeyProbability segment_probabilities = null;
  
  private double[] keyenergy = null;

  public void cancel() { waitingtocancel = true; }
  private boolean waitingtocancel = false;
  private int uimode = 0;

  private Task detectTask = null;
  public void setDetectTask(Task detectTask) {
      this.detectTask = detectTask;
  }
  
  private static Semaphore detectfromfilesem = null; //new Semaphore(1);
  public DetectedKey detectKeyFromFile(String strFilename) throws Exception {
    DetectedKey returnval = new DetectedKey();
    DetectedKey start_key = null;
    boolean error = false;
    double report_interval = 10.0;
    double report_time = report_interval;
    log.debug("detectKeyFromFile(): Detecting key from file=" + strFilename);
    Date startime = new Date();
    try {
      if (detectfromfilesem != null)
          detectfromfilesem.acquire();
      
      decoder = AudioDecoderFactory.getAudioDecoder(strFilename);
      if (decoder != null) {
          init_keycount(rapid_evolution.audio.KeyDetector.analyzechunksize / decoder.getAudioFormat().getSampleRate() * 1000);
	      int skiprate = OptionsUI.instance.keydetectionquality.getValue();
	      int skipcount = 0;      
	      boolean done = false;
	      boolean half_way = false;
	      boolean aborted = false;
	      PaceMaker pacer = new PaceMaker();
	      while (!done) {
	          pacer.startInterval();
	          if (skipcount <= skiprate) {
	              long frames_read = decoder.readFrames(analyzechunksize);
	              //if (log.isTraceEnabled()) log.trace("detectKeyFromFile(): frames read=" + frames_read);
	              if ((frames_read < analyzechunksize) && (frames_read > 0)) {
		        	  if (log.isDebugEnabled())
		        		  log.debug("detectKeyFromFile(): insufficient audio found, looping audio segment to meet minimum length required");
	            	  int i = 0;
	            	  for (long j = frames_read; j < analyzechunksize; ++j) {
	            		  for (int c = 0; c < decoder.getAudioFormat().getChannels(); ++c) {
		            		  double copySample = decoder.getAudioBuffer().getSampleData()[c][i];
	            			  decoder.getAudioBuffer().setSampleValue((int)j, c, copySample);
	            		  }
	            		  ++i;	            		  
	            	  }	       
	            	  decoder.skipFrames(analyzechunksize - frames_read);
	            	  frames_read = analyzechunksize;
	            	  done = true;
	              }
	              if (frames_read == analyzechunksize) {
	                  analyzeSegment(decoder.getAudioBuffer());
	                  double audiotime = decoder.getSecondsRead();
	                  DetectedKey start_section = null;
	                  if (half_way) {
		                  start_section = segment_probabilities.getDetectedKey(false);
		                  if (start_section != null) {
		                      if (OptionsUI.instance.detect_start_and_end_keys.isSelected())
		                          returnval.setEndKey(start_section.getStartKey());
		                      else
		                          returnval.setStartKey(start_section.getStartKey());
		                      returnval.setAccuracy(start_section.getAccuracy());	                      
		                  }
	                  } else {
		                  start_section = segment_probabilities.getDetectedKey((!half_way && (audiotime * 2 > decoder.getTotalSeconds())));
		                  if (start_section != null) {
		                      returnval.setStartKey(start_section.getStartKey());
		                      returnval.setAccuracy(start_section.getAccuracy());	                      
		                  }
	                  }
	                  if (!half_way && (audiotime * 2 > decoder.getTotalSeconds())) {
	                      half_way = true;
		                  start_key = segment_probabilities.getDetectedKey(false);
		                  init_keycount(rapid_evolution.audio.KeyDetector.analyzechunksize / decoder.getAudioFormat().getSampleRate() * 1000);	           
	                  }	                  
                      updateUIresults(returnval);
	                  updateUIstatus(audiotime, decoder.getTotalSeconds());
	                  if (audiotime > report_time) {
	                      if (log.isTraceEnabled())
	                          log.trace("detectKeyFromFile(): time=" + audiotime + "s, key=" + start_section.getStartKey());
	                      report_time += report_interval;
	                  }
                      if ((detectTask != null) && detectTask.isCancelled())
                          waitingtocancel = true;
                      if (waitingtocancel || RapidEvolution.instance.terminatesignal) {
                          aborted = true;
                          done = true;
                      }
	              } else if (frames_read == 0) {
	                  done = true;
	              }
	          } else {
	              long frames_skipped = decoder.skipFrames(analyzechunksize);
	              if (frames_skipped == 0)
	                  done = true;
	          }
	          ++skipcount;
	          if (skipcount > 100) skipcount = 0;          
	          if (waitingtocancel || RapidEvolution.instance.terminatesignal) {
	              aborted = true;
	              done = true;
	          }
	          pacer.endInterval();
	      }      
	      decoder.close();
	      if (!aborted) {
	          segment_probabilities.finish();
	          //float totalaudiotime = ((float)totalBytesRead) / bytespersecond;
	          if (!segment_probabilities.hasNoData()) {
	        	  DetectedKey end_key = determineKey(segment_probabilities, keyenergy);
	        	  String key = segment_probabilities.getDetectedKey(true).getStartKey();
	        	  if (returnval.getStartKey().equals(""))
	        		  returnval.setStartKey(key);
	        	  else
	        		  returnval.setEndKey(key);
	        	  if (returnval.getAccuracy() != 0.0)
	        		  returnval.setAccuracy(Math.min(end_key.getAccuracy(), returnval.getAccuracy()));
	        	  else
	        		  returnval.setAccuracy(end_key.getAccuracy());
	        	  if (!OptionsUI.instance.detect_start_and_end_keys.isSelected()) { // use single key                  
	        		  returnval.setStartKey(key);
	        		  returnval.setEndKey("");
	        		  returnval.setAccuracy(returnval.getAccuracy());
	        	  }
              }
              updateUIresults(returnval);
	          Date endtime = new Date();
	          long seconds = (endtime.getTime() - startime.getTime()) / 1000;
	          log.debug("detectKeyFromFile(): detected key=" + returnval + ", in " + seconds + " seconds");
	      } else {
           returnval = new DetectedKey();   
          }
      } else {
          log.warn("detectKeyFromFile(): no decoder available for filename=" + strFilename);
          throw new NoDecoderAvailableException(strFilename);
      }
      cleanupUI();
    } catch (java.lang.OutOfMemoryError e) { error = true; log.error("detectKeyFromFile(): error", e); new OutOfMemoryThread().start();
    } catch (NoDecoderAvailableException nda) {
        error = true;
        cleanupUI();
        if (detectfromfilesem != null)
            detectfromfilesem.release();
        throw nda;
    } catch (Exception e) {
        log.error("detectKeyFromFile(): error", e); 
        error = true;
        cleanupUI();
    	new rapid_evolution.threads.ErrorMessageThread("key detector error", "could not detect key from file: " + strFilename + ", error: " + e.getMessage()).start();
        if (decoder != null) decoder.close();
    }
    if (detectfromfilesem != null)
        detectfromfilesem.release();
    return returnval;
  }

  private void cleanupUI() {
    if (uimode == UI_EDITDIALOG) {
      AudioEngine.instance.editdetectingfromfile = false;
      EditSongKeyProgressUI.instance.setVisible(false);
    } else if (uimode == UI_ADDDIALOG) {
      AudioEngine.instance.adddetectingfromfile = false;
      AddSongKeyProgressUI.instance.Hide();
    }
  }

  public static int UI_EDITDIALOG = 1;
  public static int UI_ADDDIALOG = 2;

  private void updateUIstatus(double amount, double total) {
    if (uimode == UI_EDITDIALOG) {
        EditSongKeyProgressUI.instance.progressbar.setValue( (int) ( 100.0 * amount / total));
        EditSongKeyProgressUI.instance.repaint();
        if (!AudioEngine.instance.editdetectingfromfile) cancel();
    } else if (uimode == UI_ADDDIALOG) {
        AddSongKeyProgressUI.instance.progressbar2.setValue( (int) (100.0 * amount / total));
        AddSongKeyProgressUI.instance.repaint();
        if (!AudioEngine.instance.adddetectingfromfile) cancel();
    }
  }

  private void updateUIresults(DetectedKey key) {
      if (key == null) return;
    if (uimode == UI_EDITDIALOG) {
        EditSongUI.instance.editsongsstartkeyfield.setText(key.getStartKey());
        EditSongUI.instance.editsongsendkeyfield.setText(key.getEndKey());
        EditSongUI.instance.editsong_keyaccuracy.setValue((int)(key.getAccuracy() * 100));
    } else if (uimode == UI_ADDDIALOG) {
        AddSongsUI.instance.addsongsstartkeyfield.setText(key.getStartKey());
        AddSongsUI.instance.addsongsendkeyfield.setText(key.getEndKey());
        AddSongsUI.instance.addsongs_keyaccuracy.setValue((int)(key.getAccuracy() * 100));
    }
  }

  private void analyzeSegment(AudioBuffer audiobuffer) {
    try {
      int num_channels_to_use = 1;
      int maxfrequency = (int) decoder.getMaxFrequency();
      double timeinterval = (double) analyzechunksize / decoder.getSampleRate();
      for (int channel = 0; channel < Math.min(num_channels_to_use, audiobuffer.getNumChannels()); ++channel) {
          for (int i = 0; i < norm_keycount.length; ++i)
              norm_keycount[i] = 0.0;
          countKeyProbabilities( audiobuffer.getSampleData(channel), 0, analyzechunksize, timeinterval, maxfrequency, segment_probabilities, keyenergy, norm_keycount);
      }

    } catch (Exception e) { log.error("analyzeSegment(): error", e); }
  }

  private static int safeindex(int val) {
    while (val > 11) val -= 12;
    return val;
  }
    
  public static DetectedKey determineKey(KeyProbability totals, double[] keyenergy) throws Exception {
      return totals.getDetectedKey(false);
      
      /*
      if (totals.hasNoData()) return null;
        double[] combinedtotals = new double[24];
        for (int i = 0; i < 12; ++i) {
            combinedtotals[i] = totals.major[i];
            combinedtotals[i+12] = totals.max_minor[i];
        }
        int max = 0;
        int min = 0;
        for (int i = 1; i < 24; ++i) {
            if (combinedtotals[i] > combinedtotals[max]) max = i;
            if (combinedtotals[i] < combinedtotals[min]) min = i;
        }        
          int max2 = (max == 0) ? 1 : 0;
          for (int i = max2 + 1; i < 24; ++i) {
              if (i != max) {
                  if (combinedtotals[i] > combinedtotals[max2]) max2 = i;
              }
          }
            
        double accuracy = 0.0;
        try {
          // determine average/standard deviation
            double maxkeyenergy = keyenergy[0];
            double minkeyenergy = keyenergy[0];
          double all_total = 0.0;          
          for (int i = 0; i < 12; ++i) {
              all_total += keyenergy[i];       
              if (keyenergy[i] > maxkeyenergy) maxkeyenergy = keyenergy[i];
              if (keyenergy[i] < minkeyenergy) minkeyenergy = keyenergy[i];
          }
          double average = all_total / 12;
          double variance = 0.0;
          for (int i = 0; i < 12; ++i) {
              double diff = average - keyenergy[i];
              diff *= diff;
              variance += diff;
          }
          variance /= 12;
//          double std_deviation = Math.sqrt(variance);
          //double max_std_deviation = Math.sqrt((((combinedtotals[max] - combinedtotals[min]) / 2) * ((combinedtotals[max] - combinedtotals[min]) / 2)) * 24);          
          double range = maxkeyenergy - minkeyenergy;
//          double deviation = maxkeyenergy - average;
          accuracy = (maxkeyenergy - average) / range;
          //accuracy = (deviation - std_deviation) / deviation;
          if (accuracy < 0) accuracy = 0;
          if (accuracy > 1) accuracy = 1;
        } catch (Exception e) { }
          
          String keystr = new String("");
          if (OptionsUI.instance.keyformatcombo.getSelectedIndex() == 2) {
            if (max == 0) keystr = "8B"; // a
            if (max == 1) keystr = "3B"; // Bb
            if (max == 2) keystr = "10B"; // b
            if (max == 3) keystr = "5B"; // c
            if (max == 4) keystr = "12B"; // Db
            if (max == 5) keystr = "7B"; // d
            if (max == 6) keystr = "2B"; // Eb
            if (max == 7) keystr = "9B"; // e
            if (max == 8) keystr = "4B"; // f
            if (max == 9) keystr = "11B"; // Gb
            if (max == 10) keystr = "6B"; // g
            if (max == 11) keystr = "1B"; // Ab
            if (max == 12) keystr = "8A"; // a
            if (max == 13) keystr = "3A"; // Bb
            if (max == 14) keystr = "10A"; // b
            if (max == 15) keystr = "5A"; // c
            if (max == 16) keystr = "12A"; // Db
            if (max == 17) keystr = "7A"; // d
            if (max == 18) keystr = "2A"; // Eb
            if (max == 19) keystr = "9A"; // e
            if (max == 20) keystr = "4A"; // f
            if (max == 21) keystr = "11A"; // Gb
            if (max == 22) keystr = "6A"; // g
            if (max == 23) keystr = "1A"; // Ab
          } else {
            if (max == 0) keystr = "C";
            if (max == 1) keystr = KeyFormatUI.instance.dflatoption.isSelected() ? "Db" : "C#";
            if (max == 2) keystr = "D";
            if (max == 3) keystr = KeyFormatUI.instance.eflatoption.isSelected() ? "Eb" : "D#";
            if (max == 4) keystr = "E";
            if (max == 5) keystr = "F";
            if (max == 6) keystr = KeyFormatUI.instance.gflatoption.isSelected() ? "Gb" : "F#";
            if (max == 7) keystr = "G";
            if (max == 8) keystr = KeyFormatUI.instance.aflatoption.isSelected() ? "Ab" : "G#";
            if (max == 9) keystr = "A";
            if (max == 10) keystr = KeyFormatUI.instance.bflatoption.isSelected() ? "Bb" : "A#";
            if (max == 11) keystr = "B";
            if (max == 12) keystr = "Am";
            if (max == 13) keystr = KeyFormatUI.instance.bflatoption.isSelected() ? "Bbm" : "A#m";
            if (max == 14) keystr = "Bm";
            if (max == 15) keystr = "Cm";
            if (max == 16) keystr = KeyFormatUI.instance.dflatoption.isSelected() ? "Dbm" : "C#m";
            if (max == 17) keystr = "Dm";
            if (max == 18) keystr = KeyFormatUI.instance.eflatoption.isSelected() ? "Ebm" : "D#m";
            if (max == 19) keystr = "Em";
            if (max == 20) keystr = "Fm";
            if (max == 21) keystr = KeyFormatUI.instance.gflatoption.isSelected() ? "Gbm" : "F#m";
            if (max == 22) keystr = "Gm";
            if (max == 23) keystr = KeyFormatUI.instance.aflatoption.isSelected() ? "Abm" : "G#m";
          }
          return new DetectedKey(keystr, accuracy);
          */
  }

  
  private double[] norm_keycount = new double[] { 0,0,0,0,0,0,0,0,0,0,0,0 };
 public static void countKeyProbabilities(double[] wavedata, long icount, long amt, double time, int maxfreq, KeyProbability segment_probabilities, double[] keyenergy, double[] norm_keycount) {
     KeyDetectMatrix matrix = getMatrix(maxfreq);     
     double[] cwt = new double[12];
     int icountInt = (int)icount;
     for (int p = 0; p < matrix.getMaxOctaves(); p++) {
         for (int ks = 0; ks < matrix.getShifts(); ks++) {
             for (int z = 0; z < 12; ++z) cwt[z] = 0.0;
             for (int m = 0; m < amt; ++m) {
                 for (int z = 0; z < 12; ++z) {
                     double x = matrix.getValue(p, ks, m, z);
                     double y = wavedata[m + icountInt];
                     cwt[z] += y * x;
                 }
             }
             for (int z = 0; z< 12; ++z) norm_keycount[z] += Math.abs(cwt[z]);
         }
     }
     for (int i = 0; i < 12; ++i) keyenergy[i] += norm_keycount[i];    
     segment_probabilities.add(norm_keycount, time);
  }

  static private Map matrixMap = new HashMap();
  static public KeyDetectMatrix getMatrix(int maxFrequency) {
      KeyDetectMatrix result = (KeyDetectMatrix)matrixMap.get(new Integer(maxFrequency));
      if (result == null) {          
          result = new KeyDetectMatrix(maxFrequency);
          matrixMap.put(new Integer(maxFrequency), result);
      }
      return result;
          
  }
 
}

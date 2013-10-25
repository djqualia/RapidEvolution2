package rapid_evolution.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.threads.OutOfMemoryThread;
import rapid_evolution.ui.AddSongKeyProgressUI;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongKeyProgressUI;
import rapid_evolution.ui.EditSongUI;

// the problem: first and last indices are NaN

public class KeyDetectorQ {

    private static Logger log = Logger.getLogger(KeyDetectorQ.class);
    
    // current changes being tested:
    //  - added extra minor notes to consonance filter
    //  - removed hanning window filter
    
    // these parameters can be tuned for performance:
    int step_size = 512; // the # of samples skipped to compute each cqt frame, ideal: 512
    double freq_min = 27.5; // minimum frequency in Hz, ideal: 27.5 (lowest note on grand piano - A)
    double freq_max = 3520; // maximum frequency in Hz, ideal: 3520 (highest note on grand piano - C)
    double max_process_length = 600; // amount of the song to process in seconds  
    int READBUFFERSIZE = 65536; // maximum number of bytes read at a time from the audio file
    
    // these parameters control the algorithm behavior:
    double b = 120.0; // the # of frequency components per octave, 120 gives 0.1 semitone resolution
    double peak_neighbor_ratio = 0.4;
    double tuning_peak_threshold = 0.7;
    int num_peaks_threshold = 8;
    int max_consonance_partials = 20;
    double key_window_seconds = 15;
    double key_window_slide_seconds = 5;
    
    // these variables are used in the precomputed matrices:
    double Q; // ratio of frequency to bandwidth (frequency resolution)
    int K; // the # of components in the whole spectrum
    double[] freq;
    int[] N_samples;
    double[][] N_coeff;
    double[][] N_coeff2;

    
    KeyProbability totals;
    int[] keyenergy = new int[12];
    int[] noteindices = new int[12]; // used for getting real index from circle of fifths
    
    private boolean waitingtocancel = false;    
    int uimode = 0;

    public KeyDetectorQ() { }    
    public KeyDetectorQ(int uimode) {
        this.uimode = uimode;
    }
    
    private void init(double samplerate) {
        step_size = (int)((samplerate / 44100) * step_size);
        log.debug("init(): initializing key detector");
        Q = Math.pow(Math.pow(2, 1.0 / b) - 1, -1);
        K = (int)(b * Math.log(freq_max / freq_min) / Math.log(2));
        freq = new double[K];
        N_samples = new int[K];
        N_coeff = new double[K][];
        N_coeff2 = new double[K][];
        for (int k = 0; k < 2; ++k) {
            freq[k] = 0;
            N_samples[k] = 0;
            N_coeff[k] = new double[0];
            N_coeff2[k] = new double[0];
        }
        for (int k = 2; k < K; ++k) {
            freq[k] = freq_min * Math.pow(2, (k - 1) / b);
            N_samples[k] = (int)(Q * samplerate / freq[k]);            
            int Nk = N_samples[k]; 
            if (Nk > 0) {
                N_coeff[k] = new double[Nk];
                N_coeff2[k] = new double[Nk];
                double[] window = DSP.hammingWindowDouble(Nk);
                for (int n = 0; n < Nk; ++n) {
                    N_coeff[k][n] = Math.cos(Math.PI * -2 * n * Q / Nk) * window[n];
                    N_coeff2[k][n] = Math.sin(Math.PI * -2 * n * Q / Nk) * window[n];
                }
            }
        }
        for (int i = 0; i < 12; ++i) noteindices[i] = i;
        noteindices = ReOrderCircleOfFifthsIndices(noteindices);        
        log.debug("init(): done initializing key detector");
    }
    
    public double[] ComputeQTransform(double[] wavedata) {
        double[] transform = new double[K];
        for (int k = 0; k < K; ++k) {
            int Nk = N_samples[k];
            if (Nk > 0) {
                double real = 0.0;
                double imag = 0.0;
                for (int n = 0; n < Nk; ++n) {
                    real += wavedata[n] * N_coeff[k][n];
                    imag += wavedata[n] * N_coeff2[k][n];
                }
                real /= Nk;
                imag /= Nk;
                transform[k] = Math.sqrt(real * real + imag * imag);
            } else {
                transform[k] = 0;
            }
        }
        return transform;
    }
    
    private static Semaphore detectfromfilesem = new Semaphore(1);
    public DetectedKey detectKeyFromFile(String strFilename) {
      DetectedKey returnval = null;
      log.debug("detectKeyFromFile(): detecting key from file=" + strFilename);
      Date startime = new Date();      
      try {
        detectfromfilesem.acquire();
        HashMap keyresults = new HashMap();
        double totalseconds = AudioLib.get_track_time(strFilename);
        File file = FileUtil.getFileObject(strFilename);
        // open the initial audio input stream from the file
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        // get the source audio format
        AudioFormat sourceFormat = audioInputStream.getFormat();
        // set the decoded format to PCM_SIGNED
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);
        // get the decoded audio input stream
        AudioInputStream decodedInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
        AudioFormat audioFormat = decodedInputStream.getFormat();
        
        init(audioFormat.getSampleRate());
        float bytespersecond = audioFormat.getSampleRate() * audioFormat.getChannels() * audioFormat.getSampleSizeInBits() / 8;
        int sample_bytesize = audioFormat.getSampleSizeInBits() / 8;      
        double max_frequency = audioFormat.getSampleRate() / 2;
        int bytesize = audioFormat.getSampleSizeInBits() / 8;        
        
        int total_cqts = (int)Math.ceil(Math.min(totalseconds, max_process_length) * audioFormat.getSampleRate() / step_size);
        double[][] cqts = new double[total_cqts][];
        int cqt_iter = 0;
        
        // create a temporary buffer to read bytes from the decoded audio format
        byte[] abData = new byte[READBUFFERSIZE * sample_bytesize * audioFormat.getChannels()];
        long nBytesRead = 0;
        int totalBytesRead = 0;      
        // create a fixed length process buffer which will be used to analyze the audio bytes
        byte[] readBuffer = new byte[abData.length];
        int readCount = 0;
        // create an array holder for wavedata
        double[] combined_wavedata = new double[N_samples[2]];
        int combined_index = 0;
        boolean past_process_length = false;
        // initiailze skiprate variables--at highest quality, no samples are skipped
        // while there are still bytes in the audio input stream
        while ((nBytesRead != -1) && !waitingtocancel && !past_process_length && !RapidEvolution.instance.terminatesignal) {
          // read the bytes into the temporary buffer
          try { nBytesRead = decodedInputStream.read(abData, 0, abData.length); } catch (IOException e) { log.error("detectKeyFromFile(): error", e); }
          for (int i = 0; i < nBytesRead; ++i) {
            // add the bytes to the process buffer
            readBuffer[readCount++] = abData[i];
            // when the process buffer is full, send for processing
            if ((readCount == readBuffer.length) && !past_process_length) {
                
                int count = 0;
                while ((count < readBuffer.length) && !past_process_length) {
                  combined_wavedata[combined_index] = 0;
                  for (int channel = 0; channel < audioFormat.getChannels(); ++channel) {
                      combined_wavedata[combined_index] += ByteToSampleReader.getSampleFromBytes(readBuffer, count, bytesize, audioFormat.isBigEndian());
                      count += bytesize;
                  }
                  if (combined_index >= 0) {
                      combined_wavedata[combined_index] /= audioFormat.getChannels();
                  }
                  combined_index++;                  
                  if (combined_index >= combined_wavedata.length) {
                      
                      double[] cqt = ComputeQTransform(combined_wavedata);                
                      cqts[cqt_iter++] = cqt;
                      	log.debug("detectKeyFromFile(): computed cqt transform: " + cqt_iter);                      	
                      	//for (int ct = 0; ct < cqts[cqt_iter - 1].length; ++ct) {
                          //    log.trace("detectKeyFromFile(): \tindex: " + ct + ", value: " + cqts[cqt_iter - 1][ct]);
                          	//}
                      
                      double audiotime = ((double)totalBytesRead) / bytespersecond;
      	            	updateUIresults(returnval);
      	            	updateUIstatus(audiotime, totalseconds);
	                      
                      if (audiotime >= max_process_length) {
                          past_process_length = true;
                      }
      	            	
                      // slide window step_size forward...
                      if (step_size < combined_wavedata.length) {
                          for (int s = step_size; s < combined_wavedata.length; ++s) {
                              combined_wavedata[s - step_size] = combined_wavedata[s];
                          }                                                
                      }
                      combined_index -= step_size;
                  }
                }
                if (combined_index > 0) {
                    for (int r = combined_index; r < combined_wavedata.length; ++r) {
                        combined_wavedata[r] = 0;
                    }
                    double[] cqt = ComputeQTransform(combined_wavedata);                
                    cqts[cqt_iter++] = cqt;
                }
                                	            
	            readCount = 0;
            }
          }
          totalBytesRead += nBytesRead;
        }

        decodedInputStream.close();
        audioInputStream.close();
        
            log.debug("detectKeyFromFile(): cqts used: " + cqt_iter + ", total: " + total_cqts);

        int[] master_tuning_bin = new int[10];
        for (int c = 0; c < cqt_iter; ++c) {
            ArrayList peaks = determinePeaks(cqts[c]);
            if (peaks.size() >= num_peaks_threshold) {
                double[] tuning_bin = new double[10];
                Iterator peak_iter = peaks.iterator();
                while (peak_iter.hasNext()) {
                    int peak_index = ((Integer)peak_iter.next()).intValue();
                    int bin = peak_index;
                    while (bin >= 10) bin -= 10;
                    tuning_bin[bin] += cqts[c][peak_index];
                }
                int max_tuning_index = 0;
                double total_energy = tuning_bin[0];
                for (int m = 1; m < tuning_bin.length; ++m) {
                    total_energy += tuning_bin[m];
                    if (tuning_bin[m] > tuning_bin[max_tuning_index])
                        max_tuning_index = m;
                }
                if (total_energy != 0) {
                    if ((tuning_bin[max_tuning_index] / total_energy) >= tuning_peak_threshold) {
                        master_tuning_bin[max_tuning_index]++;
                    } else {
                        // discard
                    }
                }
            } else {
                // discard
            }
        }
        int master_tuning = 0;
        for (int t = 1; t < master_tuning_bin.length; ++t) {
            if (master_tuning_bin[t] > master_tuning_bin[master_tuning])
                master_tuning = t;
        }
        log.debug("detectKeyFromFile(): master tuning: " + master_tuning);
        
        int num_note_partials = (int)Math.floor((K - master_tuning) / 10);

        log.debug("detectKeyFromFile(): # note partials: " + num_note_partials);
        
        KeyProbability prob = new KeyProbability(rapid_evolution.audio.KeyDetector.analyzechunksize / decodedInputStream.getFormat().getSampleRate() * 1000);
        int[] notecount = new int[12];
        double time = 0;
        double total_time = 0;
        for (int c = 0; c < cqt_iter; ++c) {
            double segment_time = ((double)step_size) / audioFormat.getSampleRate();
            time += segment_time;
            double[] note_partials = new double[num_note_partials];
            int t_iter = master_tuning;
            int partial_index = 0;
            while (t_iter < K) {
                note_partials[partial_index] = cqts[c][t_iter];
                // gather +/- 25cents from tuning
                for (int r = t_iter - 1; r >= Math.max(0, t_iter - 2); --r) {
                    note_partials[partial_index] += cqts[c][r];
                }
                for (int r = t_iter + 1; r < Math.min(K, t_iter + 3); ++r) {
                    note_partials[partial_index] += cqts[c][r];
                }
                t_iter += 10;
                partial_index++;
            }
            log.debug("detectKeyFromFile(): note partial: " + c);
            for (int ct = 0; ct < note_partials.length; ++ct) {
                log.debug("detectKeyFromFile(): \tindex: " + ct + ", value: " + note_partials[ct]);
            }
            
            ArrayList consonance_indices = new ArrayList();
            boolean[] used_partials = new boolean[num_note_partials];
            boolean done = false;
            while (!done && passesConsonanceCheck(consonance_indices) && (consonance_indices.size() < max_consonance_partials)) {                
                int max_partial_index = getMaxPartial(note_partials, used_partials);
                if (max_partial_index == -1) done = true;
                else consonance_indices.add(new Integer(max_partial_index));
            }
            if (!done && (consonance_indices.size() < max_consonance_partials)) {
                consonance_indices.remove(consonance_indices.size() - 1);
            }
            log.debug("detectKeyFromFile(): note partial: " + c + ", consonance indices: " + consonance_indices);
            
            Iterator notes = consonance_indices.iterator();
            while (notes.hasNext()) {
                int note = ((Integer)notes.next()).intValue();
                while (note >= 12) note -= 12;
                notecount[note]++;
            }

            double[] keycount = new double[12];
            for (int n = 0; n < note_partials.length; ++n) {
                int note = n;
                while (note >= 12) note-=12;
                keycount[note] += note_partials[n];
            }
            prob.add(keycount, segment_time);            
            
            if (time >= key_window_seconds) {                                
                if (totals == null)
                    totals = new KeyProbability(rapid_evolution.audio.KeyDetector.analyzechunksize / decodedInputStream.getFormat().getSampleRate() * 1000);
                totals.add(notecount, time);
                for (int i = 0; i < 12; ++i) {
                    keyenergy[i] += notecount[i];
                }
                log.debug("detectKeyFromFile(): window processed: " + total_time + " -> " + (total_time + key_window_seconds));
                log.debug("detectKeyFromFile(): notecount:");
                    for (int i = 0; i < 12; ++i) {
                        log.debug("detectKeyFromFile(): \tnote: " + getNote(i) + ", value: " + notecount[i]);
                    }                    
                double[] pitchprofile = ReOrderCircleOfFifths(notecount);
                log.debug("detectKeyFromFile(): pitchprofile: ");
                    for (int i = 0; i < 12; ++i) {
                        log.debug("detectKeyFromFile(): \tnote: " + getNote(noteindices[i]) + ", value: " + pitchprofile[i]);
                    }                    
                
                // filter for minor scale variation (removes prominent G# in Am for example)
                for (int i = 0; i < 12; ++i) {
                    int c1 = i + 1;
                    while (c1 >= 12) c1 -= 12;
                    int c2 = i + 2;
                    while (c2 >= 12) c2 -= 12;
                    int c3 = i - 1;
                    while (c3 < 0) c3 += 12;
                    if ((pitchprofile[i] > c1) &&
                        (pitchprofile[i] > c2) &&
                        (pitchprofile[i] > c3)) {
                        int c4 = i + 4;
                        while (c4 >= 12) c4 -= 12;
                        int c5 = i - 4;
                        while (c5 < 0) c5 += 12;
                        int c6 = i - 5;
                        while (c6 < 0) c6 += 12;
                        if ((pitchprofile[i] < c4) &&
                            (pitchprofile[i] < c5) &&
                            (pitchprofile[i] < c6)) {
                            pitchprofile[i] = 0;
                        }
                                
                    }
                }
                
                ArrayList[] pitchregions = new ArrayList[12];
                boolean[] continuous = new boolean[12];
                for (int r = 0; r < 12; ++r) {
                    int range = r + 1;
                    pitchregions[r] = findPitchRange(pitchprofile, range);
                    if (r > 0) {
                        continuous[r] = isContinuous(pitchregions[r], pitchregions[r - 1]);                        
                    }
                        if (r > 0) {
                            log.debug("detectKeyFromFile(): pitch region " + r + ": " + pitchregions[r] + ", continuous: " + continuous[r]);
                        } else {
                            log.debug("detectKeyFromFile(): pitch region " + r + ": " + pitchregions[r]);
                        }
                }

                if (continuous[6] && continuous[7]) {
                    int p7_start = ((Integer)pitchregions[6].get(0)).intValue();
                    int root_key = p7_start + 1;
                    while (root_key >= 12) root_key -= 12;                    
                    
                    log.debug("detectKeyFromFile(): scale selected, root: " + getNote(noteindices[root_key]));
                    
                    int type = 0; //undefined
                    if (continuous[1] && continuous[2]) {
                        int p2_start = ((Integer)pitchregions[1].get(0)).intValue();
                        if (p2_start == root_key) {
                            type = 1; // major
                        } else {
                            int minor_root = root_key + 3;
                            while (minor_root >= 12) minor_root -= 12;
                            if (p2_start == minor_root) {
                                type = 2; // minor
                                root_key = minor_root;
                            }                                
                        }
                    }                                            
                    
                    if (master_tuning > 5) {
                        root_key ++;
                        while (root_key >= 12) root_key -= 12;                    
                        master_tuning -= 10;
                    }
                    int western_note = 120 + noteindices[root_key] * 10;
                    double frequency = freq[western_note];
                    String keystr = "";
                    if (type == 1) {                                                
                        keystr = getNote(noteindices[root_key]);
                        if (master_tuning > 0) keystr += "+" + master_tuning * 10;
                        else if (master_tuning < 0) keystr += master_tuning * 10;
                    } else if (type == 2) {
                        keystr = getNote(noteindices[root_key]) + "m";
                        if (master_tuning > 0) keystr += "+" + master_tuning * 10;
                        else if (master_tuning < 0) keystr += master_tuning * 10;
                    }
                    if (!keystr.equals("")) {
                        log.debug("detectKeyFromFile(): key identified: " + keystr);                            
                        if (keyresults.containsKey(keystr)) {
                            double weight = ((Double)keyresults.get(keystr)).doubleValue();
                            weight += key_window_seconds;
                            keyresults.put(keystr, new Double(weight));
                        } else {
                            double weight = key_window_seconds;
                            keyresults.put(keystr, new Double(weight));
                        }
                    } else {
                        log.debug("detectKeyFromFile(): no key identified");
                    }
                                        
                } else {
                    log.debug("detectKeyFromFile(): no scale selected");
                }
                
                time = 0;                    
                total_time += key_window_slide_seconds;
                if (total_time >= max_process_length) {
                    c = cqt_iter; // stop
                } else {                    
                    c -= ((key_window_seconds - key_window_slide_seconds) * audioFormat.getSampleRate() / step_size);
                }
                for (int i = 0; i < 12; ++i) notecount[i] = 0;                
            }
            
        }        
        
        cleanupUI();
        if (!(waitingtocancel || RapidEvolution.instance.terminatesignal)) {
          //float totalaudiotime = ((float)totalBytesRead) / bytespersecond;
            Set resultset = keyresults.entrySet();
            Object maxkey = null;
            double max = 0;
            double total = 0.0;
            if (resultset != null) {
                Iterator i = resultset.iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry)i.next();
                    double value = ((Double)entry.getValue()).doubleValue();
                    if ((maxkey == null) || (value > max)) {
                        maxkey = entry.getKey();
                        max = value;
                    }         
                    total += value;
                }                
            }
            if (maxkey != null) {
                returnval = new DetectedKey((String)maxkey, "", max / total);
            }
          updateUIresults(returnval);
          Date endtime = new Date();
          long seconds = (endtime.getTime() - startime.getTime()) / 1000;
          log.debug("detectKeyFromFile(): detected key=" + returnval + ", in " + seconds + " seconds");
          returnval = prob.getDetectedKey(true);
          log.debug("detectKeyFromFile(): using method 2, key=" + returnval);
              DetectedKey m2key = determineKey(totals, keyenergy);
              if (m2key != null) {
                  log.debug("detectKeyFromFile(): \tmethod2: " + m2key.getStartKey() + ", accuracy: " + m2key.getAccuracy());
                  if (returnval == null) returnval = m2key;
              }
        }
      } catch (java.lang.OutOfMemoryError e) { log.error("detectKeyFromFile(): error", e); new OutOfMemoryThread().start(); }
      catch (Exception e) { log.error("detectKeyFromFile(): error", e);  }
      detectfromfilesem.release();
      return returnval;
    }
        
    private void readWaveData(byte[] readBuffer, double[][] wavedata, AudioFormat audioFormat) {
        try {
          int num_channels_to_use = 1;
          long totalsamples = wavedata[0].length;
          int count = 0;
          int waveindex = 0;
          int bytesize = audioFormat.getSampleSizeInBits() / 8;
          while (count < readBuffer.length) {
            for (int channel = 0; channel < audioFormat.getChannels(); ++channel) {
                if (channel < num_channels_to_use) {
                    wavedata[channel][waveindex] = ByteToSampleReader.getSampleFromBytes(readBuffer, count, bytesize, audioFormat.isBigEndian());
                }
                count += bytesize;
            }
            waveindex++;
          }

        } catch (Exception e) { log.error("readWaveData(): error", e); }
      }    
    
    private ArrayList determinePeaks(double[] cqt) {
        ArrayList peaks = new ArrayList();
        for (int i = 0; i < cqt.length; ++i) {
            boolean local_maxima = true;
            if (i + 1 < cqt.length) {
                if (cqt[i] < cqt[i + 1]) local_maxima = false;
            } else if (i - 1 >= 0) {
                if (cqt[i] < cqt[i - 1]) local_maxima = false;
            }
            if (local_maxima) {
                boolean pass_right = true;
                int r = i + 1;
                while (pass_right && (r < Math.min(cqt.length, i + 8))) {
                    if (cqt[r] != 0.0) {
                        if (cqt[i] / cqt[r] < peak_neighbor_ratio)
                            pass_right = false;
                    }
                    ++r;
                }
                boolean pass_left = true;
                int l = i - 1;
                while (pass_right && pass_left && (l >= Math.max(0, i - 7))) {
                    if (cqt[l] != 0.0) {
                        if (cqt[i] / cqt[l] < peak_neighbor_ratio)
                            pass_left = false;
                    }
                    --l;
                }
                if (pass_right && pass_left) {
                    // local maxima found
                    peaks.add(new Integer(i));
                }
            }
        }
        return peaks;
    }
    
    private int getMaxPartial(double[] partials, boolean[] used) {
        int index = -1;
        for (int i = 0; i < partials.length; ++i) {
            if (!used[i]) {
                if ((index == -1) || (partials[i] > partials[index])) {
                    index = i;
                }
            }
        }
        if (index >= 0)
            used[index] = true;
        return index;
    }
    
    private boolean passesConsonanceCheck(ArrayList indices) {
        if (indices.size() == 0) return true;
        boolean[] pass = { true, true, true, true, true, true, true, true, true, true, true, true };        
        for (int i = 0; i < indices.size(); ++i) {
            int compare = ((Integer)indices.get(i)).intValue();
            if (!checkRoot(0, compare)) pass[0] = false;
            if (!checkRoot(1, compare)) pass[1] = false;
            if (!checkRoot(2, compare)) pass[2] = false;
            if (!checkRoot(3, compare)) pass[3] = false;
            if (!checkRoot(4, compare)) pass[4] = false;
            if (!checkRoot(5, compare)) pass[5] = false;
            if (!checkRoot(6, compare)) pass[6] = false;
            if (!checkRoot(7, compare)) pass[7] = false;
            if (!checkRoot(8, compare)) pass[8] = false;
            if (!checkRoot(9, compare)) pass[9] = false;
            if (!checkRoot(10, compare)) pass[10] = false;
            if (!checkRoot(11, compare)) pass[11] = false;
        }
        for (int i = 0; i < 12; ++i) if (pass[i]) return true;
        return false;
    }
    
    private boolean checkRoot(int reference, int compare) {
        int diff = compare - reference;
        while (diff >= 12) diff -= 12;
        while (diff < 0) diff += 12;
        if (diff == 0) return true;
        if (diff == 2) return true;
        if (diff == 4) return true;
        if (diff == 5) return true;
        //if (diff == 6) return true; // melodic/harmonic minor, not diatonic
        if (diff == 7) return true;
        //if (diff == 8) return true; // melodic/harmonic minor, not diatonic
        if (diff == 9) return true;
        if (diff == 11) return true;
        return false;
    }
    
    private double[] ReOrderCircleOfFifths(int[] notes) {
        int total = 0;
        for (int i = 0; i < 12; ++i) total += notes[i];        
        double[] circle = new double[12];
        int c = 0;
        for (int i = 0; i < 12; ++i) {
            circle[i] = ((double)notes[c]) / total;
            c += 7;
            while (c >= 12) c -= 12;
        }
        return circle;
    }
    
    private int[] ReOrderCircleOfFifthsIndices(int[] notes) {
        int[] circle = new int[12];
        int c = 0;
        for (int i = 0; i < 12; ++i) {
            circle[i] = notes[c];
            c += 7;
            while (c >= 12) c -= 12;
        }
        return circle;
    }    
    private ArrayList findPitchRange(double[] pitchprofile, int range) {
        double max = 0;
        int max_index = 0;
        int max_end_index = max_index + range - 1;
        for (int r = 0; r < range; ++r) {
            max += pitchprofile[max_index + r];
        }
        for (int i = 1; i < pitchprofile.length; ++i) {
            double sum = 0;
            for (int r = i; r < i + range; ++r) {
                int safe_r = r;
                while (safe_r >= 12) safe_r -= 12;
                sum += pitchprofile[safe_r];
            }
            if (sum > max) {
                max = sum;
                max_index = i;
                max_end_index = max_index + range - 1;
                while (max_end_index >= 12) max_end_index -= 12;
            }
        }
        ArrayList array = new ArrayList();
        array.add(new Integer(max_index));
        array.add(new Integer(max_end_index));
        return array;
    }
    
    boolean isContinuous(ArrayList pr1, ArrayList pr2) {
        int s1 = ((Integer)pr1.get(0)).intValue();
        int s2 = ((Integer)pr2.get(0)).intValue();
        int e1 = ((Integer)pr1.get(1)).intValue();
        int e2 = ((Integer)pr2.get(1)).intValue();
        return ((s1 == s2) || (e1 == e2));
    }

    
    public static int UI_EDITDIALOG = 1;
    public static int UI_ADDDIALOG = 2;    
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
    private void cleanupUI() {
        if (uimode == UI_EDITDIALOG) {
          AudioEngine.instance.editdetectingfromfile = false;
          EditSongKeyProgressUI.instance.setVisible(false);
        } else if (uimode == UI_ADDDIALOG) {
          AudioEngine.instance.adddetectingfromfile = false;
          AddSongKeyProgressUI.instance.Hide();
        }
    }
    public void cancel() { waitingtocancel = true; }

    private static int safeindex(int val) {
        while (val > 11) val -= 12;
        return val;
      }
           
    public static DetectedKey determineKey(KeyProbability totals, int[] keyenergy) throws Exception {
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
            double std_deviation = Math.sqrt(variance);
            //double max_std_deviation = Math.sqrt((((combinedtotals[max] - combinedtotals[min]) / 2) * ((combinedtotals[max] - combinedtotals[min]) / 2)) * 24);          
            double range = maxkeyenergy - minkeyenergy;
            double deviation = maxkeyenergy - average;
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
    
    String getNote(int i) {
        if (i == 0) return "A";
        else if (i == 1) return "A#";
        else if (i == 2) return "B";
        else if (i == 3) return "C";
        else if (i == 4) return "C#";
        else if (i == 5) return "D";
        else if (i == 6) return "D#";
        else if (i == 7) return "E";
        else if (i == 8) return "F";
        else if (i == 9) return "F#";
        else if (i == 10) return "G";
        else if (i == 11) return "G#";
        return "";
    }
}

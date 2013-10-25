package rapid_evolution.audio;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.threads.OutOfMemoryThread;
import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.audio.AudioBuffer;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.CodecConstants;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoderFactory;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;
import com.mixshare.rapid_evolution.thread.Task;

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
public class Bpm {
    
    private static Logger log = Logger.getLogger(Bpm.class);
    
  public Bpm() { }
  
  private static final int EXTERNAL_BUFFER_SIZE = 128000;
  private static int analyzechunksize = 8192;

  static public Semaphore DetectingBPMSem = null; //new Semaphore(1);
  
  // config variables
  static public double minbpm = 80.0;  // minimum bpm to search (lower bound cap)
  static public double maxbpm = 160.0; // maximum bpm to search (upper bound cap)
  
  static public DetectedBpm GetBpmFromFile(String filename, double measurescale, Task task) {

    // process variables
    SubBandSeperator subband = null;
    DetectedBpm detectedbpm = null;

    AudioDecoder decoder = null;
    
    try {
      if (DetectingBPMSem != null) DetectingBPMSem.acquire();
      log.debug("GetBpmFromFile(): detecting bpm from file=" + filename);
      
      // portable music mode translation
      File file = FileUtil.getFileObject(filename);      
      AudioEngine.instance.detectbpmoutputfile = file; // TODO: ugly, should pass parameter instead

      decoder = AudioDecoderFactory.getAudioDecoder(file.getAbsolutePath());
      if (decoder != null) {
          double seconds = decoder.getTotalSeconds();
          if (RapidEvolution.instance.terminatesignal || task.isCancelled()) throw new Exception();
       
          // read WAV data and merge to a single channel, then seperate channel into frequency bands
          if (subband == null) subband = new SubBandSeperator((float)decoder.getSampleRate(), minbpm, maxbpm, seconds, task);

	      PaceMaker pacer = new PaceMaker();          
          double[] wavearray = new double[analyzechunksize];
          long frames_read = decoder.readFrames(wavearray.length);
          boolean done = false;
          boolean aborted = false;
          int total_frames_read = 0;          
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
        	  log.debug("GetBpmFromFile(): " + total_frames_read + " frames read");
              detectedbpm = subband.getBpm(decoder);
          }
          
      }

    } catch (java.io.FileNotFoundException e) {
        log.warn("GetBpmFromFile(): file not found=" + filename);
    } catch (java.lang.OutOfMemoryError e) {
      new OutOfMemoryThread().start();
    } catch (Exception e) { log.error("GetBpmFromFile(): error Exception", e); }
    
    if (decoder != null) decoder.close();    
    log.debug("GetBpmFromFile(): Detected bpm: " + String.valueOf(detectedbpm));
    if (DetectingBPMSem != null) DetectingBPMSem.release();
    return detectedbpm;
  }

  static public double GetBpmFromFile2(AudioDecoder decoder, double minbpm1,
            double maxbpm1, double minbpm2, double maxbpm2, double minbpm3,
            double maxbpm3, Task task) {
        double detectedbpm = 0.0;
        try {
            double[] newdata = new double[CodecConstants.DEFAULT_FRAME_BUFFER_SIZE];
            int measures = 1;
            // determines maximum block size for selectiv descent:
            int maxpower = 6; 
            // lower quality level #s mean higher quality and more time
            int qualitylevel = maxpower
                    - (OptionsUI.instance.bpmdetectionquality.getValue() - 1);
            if (qualitylevel < 0)
                qualitylevel = 0;
            if (qualitylevel > maxpower)
                qualitylevel = maxpower;
            double[] combinedAudio = null;
            boolean[] usedarray = new boolean[5]; // 1 2 4 8 16 32
            for (int i = 0; i < usedarray.length; i++)
                usedarray[i] = true;
            BPMComb comb = new BPMComb(usedarray);
            try {
                if (RapidEvolution.instance.terminatesignal || task.isCancelled())
                    throw new Exception();
                log.debug("GetBpmFromFile2(): sub-detecting bpm...");
                log.debug("GetBpmFromFile2(): -- range 1 -- min: "
                        + String.valueOf(minbpm1) + ", max: "
                        + String.valueOf(maxbpm1));
                log.debug("GetBpmFromFile2(): -- range 2 -- min: "
                        + String.valueOf(minbpm2) + ", max: "
                        + String.valueOf(maxbpm2));
                log.debug("GetBpmFromFile2(): -- range 3 -- min: "
                        + String.valueOf(minbpm3) + ", max: "
                        + String.valueOf(maxbpm3));

                float samplescale = (float) decoder.getSampleRate() / 44100.0f;
                if (samplescale > 1.0f)
                    while ((samplescale / 2.0f) >= 1.0f) {
                        samplescale /= 2.0f;
                        maxpower++;
                        qualitylevel++;
                    }
                else if (samplescale < 1.0f)
                    while ((samplescale * 2.0f) <= 1.0f) {
                        samplescale *= 2.0f;
                        maxpower--;
                        qualitylevel--;
                    }
                if (qualitylevel < 0)
                    qualitylevel = 0;

                Vector inspect = new Vector();
                int chunkscale = (int) Math.pow(2.0, maxpower);
                double effectivesamplerate = decoder.getSampleRate()
                        / ((double) chunkscale);
                double bpm = 60.0 * effectivesamplerate * measures;
                int blocksize = 1;
                while (bpm >= minbpm * 0.75) {
                    if (bpm <= maxbpm * 1.25) {
                        if ((bpm >= minbpm1) && (bpm <= maxbpm1))
                            inspect.add(new Integer(blocksize));
                        if ((bpm >= minbpm2) && (bpm <= maxbpm2))
                            inspect.add(new Integer(blocksize));
                        if ((bpm >= minbpm3) && (bpm <= maxbpm3))
                            inspect.add(new Integer(blocksize));
                    }
                    blocksize++;
                    bpm = 60.0 / ((double) blocksize) * effectivesamplerate
                            * measures;
                }
                for (int m = maxpower; m >= qualitylevel; m--) {

                    if (RapidEvolution.instance.terminatesignal || task.isCancelled())
                        throw new Exception();

                    decoder.reset();
                    
                    chunkscale = (int) Math.pow(2.0, m);
                    effectivesamplerate = decoder.getSampleRate()
                            / ((double) chunkscale);
                    comb.reset(usedarray);
                    double previouslastvalue = 0.0;
                    double avgbpm = 0;
                    for (int i = 0; i < inspect.size(); ++i) {
                        int isize = ((Integer) inspect.get(i)).intValue();
                        avgbpm += 60.0 / ((double) isize) * effectivesamplerate
                                * measures;
                        comb.addBlock(isize);
                    }
                    avgbpm /= (double) inspect.size();
                    boolean keepgoing = false;
                    for (int i = 0; i < inspect.size(); ++i) {
                        int isize = ((Integer) inspect.get(i)).intValue();
                        double ibpm = 60.0 / ((double) isize)
                                * effectivesamplerate * measures;
                        if (Math.abs(ibpm - avgbpm) > 1.0)
                            keepgoing = true;
                    }
                    if (!keepgoing) {
                        m = 0;
                        break;
                    }
                    log.debug("GetBpmFromFile2(): m = " + String.valueOf(m));
                    double[] tempbits = new double[0];
                    AudioBuffer buffer = decoder.getAudioBuffer();
                    double[][] data = buffer.getSampleData();
                    if ((combinedAudio == null)
                            || (combinedAudio.length != analyzechunksize))
                        combinedAudio = new double[analyzechunksize];
                    int tempbitindex = 0;
                    int newdatasize = 0;
                    int index = 0;
                    try {
                        double timeinterval = (double) analyzechunksize
                                / decoder.getSampleRate();
                        boolean done = false;
                        boolean aborted = false;
                        PaceMaker pacer = new PaceMaker();
                        while (!done) {
                            pacer.startInterval();
                            if (RapidEvolution.instance.terminatesignal || task.isCancelled()) {
                                done = true;
                                aborted = true;
                                throw new Exception();
                            } else {
                                long frames_read = decoder
                                        .readFrames(analyzechunksize);
                                if (frames_read == 0) {
                                    done = true;
                                } else if (frames_read == analyzechunksize) {
                                    for (int f = 0; f < analyzechunksize; ++f) {
                                        double val = 0.0;
                                        for (int c = 0; c < buffer
                                                .getNumChannels(); ++c) {
                                            val += data[c][f];
                                        }
                                        combinedAudio[f] = val;
                                    }

                                    newdatasize = (int) Math
                                            .floor(((double) (combinedAudio.length + tempbits.length))
                                                    / chunkscale) + 1;
                                    int newdataindex = 0;
                                    index = 0;
                                    int wavecount = 0;

                                    if (chunkscale > 1) {
                                        if (newdata.length < newdatasize) {
                                            newdata = new double[newdatasize];
                                        }
                                        while (wavecount < combinedAudio.length) {
                                            if (tempbits != null) {
                                                if ((tempbits.length + (combinedAudio.length - wavecount)) >= chunkscale) {
                                                    double avg = 0.0;
                                                    for (int b = 0; b < tempbits.length; ++b)
                                                        avg += Math
                                                                .abs(tempbits[b]);
                                                    for (int b = tempbits.length; b < chunkscale; ++b) {
                                                        avg += Math
                                                                .abs(combinedAudio[wavecount++]);
                                                    }
                                                    newdata[index++] = avg;
                                                    tempbits = null;
                                                } else {
                                                    while (wavecount < combinedAudio.length)
                                                        tempbits[tempbitindex++] = combinedAudio[wavecount++];
                                                }
                                            } else if ((wavecount + chunkscale) < combinedAudio.length) {
                                                double avg = 0.0;
                                                for (int b = 0; b < chunkscale; ++b) {
                                                    avg += Math
                                                            .abs(combinedAudio[wavecount++]);
                                                }
                                                newdata[index++] = avg;
                                            } else {
                                                int tempbitsize = combinedAudio.length
                                                        - wavecount;
                                                while (tempbitsize
                                                        + combinedAudio.length < chunkscale) {
                                                    tempbitsize += combinedAudio.length;
                                                }
                                                tempbits = new double[tempbitsize];
                                                tempbitindex = 0;
                                                while (wavecount < combinedAudio.length)
                                                    tempbits[tempbitindex++] = combinedAudio[wavecount++];
                                            }
                                        }
                                        if (index > 0) comb.pushData(newdata, index);
                                    } else {
                                        comb.pushData(combinedAudio,
                                                combinedAudio.length);
                                    }

                                }
                            }
                            pacer.endInterval();
                        }
                    } catch (Exception e) {
                        log.error("GetBpmFromFile2(): error", e);
                        log.debug("GetBpmFromFile2(): newdatasize: "
                                + newdatasize);
                    }

                    comb.normalize();

                    double min = 0;
                    for (int i = 0; i < usedarray.length; i++)
                        usedarray[i] = false;
                    double avgcount = 0;
                    for (int i = 0; i < comb.bpmfilters.size(); ++i) {
                        BPMFilter filter = (BPMFilter) comb.bpmfilters.get(i);
                        double blockbpm = 60.0 / ((double) filter.blocksize)
                                * effectivesamplerate * measures;
                        double blockdiff = 1.0;
                        double blockbpm2 = 60.0
                                / ((double) (filter.blocksize + 1))
                                * effectivesamplerate * measures;
                        blockdiff = Math.abs(blockbpm - blockbpm2);
                        blockbpm2 = 60.0 / ((double) (filter.blocksize - 1))
                                * effectivesamplerate * measures;
                        double blockdiff2 = Math.abs(blockbpm - blockbpm2);
                        if (blockdiff2 < blockdiff)
                            blockdiff = blockdiff2;
                        int minindex = -1;
                        double mindiff = 0.0;
                        for (int z = 0; z < filter.use.length; ++z) {
                            if ((filter.totaldiff[z] != 0)
                                    && ((filter.totaldiff[z] < mindiff) || (mindiff == 0))) {
                                mindiff = filter.totaldiff[z];
                                minindex = (int) Math.pow(2.0, z);
                                usedarray[z] = true;
                            }
                        }
                        avgcount += mindiff;
                        if ((mindiff < min) || (min == 0)) {
                            detectedbpm = round(blockbpm,
                                    extractDecimalPlaces(String
                                            .valueOf(blockdiff)));
                            min = mindiff;
                        }
                        log.debug("GetBpmFromFile2(): bpm: "
                                + String.valueOf(blockbpm) + ", count: "
                                + String.valueOf(mindiff) + ", measures: "
                                + minindex);
                    }
                    avgcount /= (double) comb.bpmfilters.size();
                    if (avgcount == 0) {
                        m = maxpower;
                        break;
                    }
                    log.debug("GetBpmFromFile2(): avg count: "
                            + String.valueOf(avgcount));
                    inspect = new Vector();
                    String inspecting = new String("selecting: ");
                    for (int i = 0; i < comb.bpmfilters.size(); ++i) {
                        BPMFilter filter = (BPMFilter) comb.bpmfilters.get(i);
                        double blockbpm = 60.0 / ((double) filter.blocksize)
                                * effectivesamplerate * measures;
                        boolean islessthan = false;
                        for (int z = 0; z < filter.use.length; ++z)
                            if (filter.totaldiff[z] != 0.0
                                    && (filter.totaldiff[z] < avgcount))
                                islessthan = true;
                        if (islessthan) {
                            inspecting += String.valueOf(blockbpm) + "  ";
                            //              if ((i > 0) && (((BPMFilter)comb.bpmfilters.get(i - 1)).blocksize == (filter.blocksize - 1))) {
                            //                BPMFilter filter2 = (BPMFilter)comb.bpmfilters.get(i - 1);
                            //                boolean islessthan2 = false;
                            //                for (int z = 0; z < filter2.use.length; ++z) if (filter2.totaldiff[z] != 0.0 && (filter2.totaldiff[z] < avgcount)) islessthan2 = true;
                            //                if (islessthan2) inspect.add(new Integer((filter.blocksize * 2) - 1));
                            //              } else inspect.add(new Integer((filter.blocksize * 2) - 1));
                            inspect
                                    .add(new Integer((filter.blocksize * 2) - 1));
                            inspect.add(new Integer(filter.blocksize * 2));
                            //              if (((i + 1) < comb.bpmfilters.size()) && (((BPMFilter)comb.bpmfilters.get(i + 1)).blocksize == (filter.blocksize + 1))) {
                            //                BPMFilter filter2 = (BPMFilter)comb.bpmfilters.get(i + 1);
                            //                boolean islessthan2 = false;
                            //                for (int z = 0; z < filter2.use.length; ++z) if (filter2.totaldiff[z] != 0.0 && (filter2.totaldiff[z] < avgcount)) islessthan2 = true;
                            //                if (islessthan2) inspect.add(new Integer((filter.blocksize * 2) + 1));
                            //              } else inspect.add(new Integer((filter.blocksize * 2) + 1));
                            inspect
                                    .add(new Integer((filter.blocksize * 2) + 1));
                        }
                    }
                    log.debug("GetBpmFromFile2(): " + inspecting);
                    log.debug("GetBpmFromFile2(): est. bpm: "
                            + String.valueOf(detectedbpm));
                    
                }
            } catch (java.lang.OutOfMemoryError e) {
                new OutOfMemoryThread().start();
            } catch (Exception e) {
                log.error("GetBpmFromFile2(): error", e);
            }
            if (detectedbpm != 0.0) {
                while (detectedbpm < minbpm)
                    detectedbpm *= 2.0;
                while (detectedbpm > maxbpm)
                    detectedbpm /= 2.0;
            }
        } catch (Exception e) {
        }
        return detectedbpm;
    }

  public static double round(double val, int places) {
      long factor = (long)Math.pow(10,places);

      // Shift the decimal the correct number of places
      // to the right.
      val = val * factor;

      // Round to the nearest integer.
      long tmp = Math.round(val);

      // Shift the decimal the correct number of places
      // back to the left.
      return (double)tmp / factor;
  }

  static public int extractDecimalPlaces(String input) {
    int returnval = 0;
    boolean pastcomma = false;
    for (int i = 0; 0 < input.length(); ++i) {
      if (input.charAt(i) == '.') pastcomma = true;
      else if (pastcomma) {
        if (input.charAt(i) != '0') return returnval + 1;
        else returnval++;
      } else {
        if (input.charAt(i) != '0') return returnval;
      }
    }
    return returnval;
  }
}

package rapid_evolution.audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import rapid_evolution.ui.OptionsUI;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import rapid_evolution.audio.ByteToSampleReader;
import rapid_evolution.ui.main.TimePitchShiftProgressUI;
import rapid_evolution.audio.ResampleLib;
import rapid_evolution.audio.Resampler;
import rapid_evolution.FileUtil;
import rapid_evolution.util.intHolder;

import org.apache.log4j.Logger;

import rapid_evolution.audio.Normalizer;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.*;
import com.mixshare.rapid_evolution.audio.AudioBuffer;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoderFactory;
import com.mixshare.rapid_evolution.io.FileLockManager;

public class PitchShift {
    
    private static Logger log = Logger.getLogger(PitchShift.class);
    
  public PitchShift() { }

  static float M_PI = 3.14159265358979323846f;
  static int MAX_FRAME_LENGTH = 8192;

  private static Semaphore shiftingfilesem = new Semaphore(1);
  public Normalizer pitchShiftFile(String inputFilename, String outputFilename, float semitoneshift, double timescale, boolean willnormalize) {
    int bytespersample = 1;
    normalizemode = willnormalize;
    if (inputFilename.toLowerCase().equals(outputFilename.toLowerCase())) {
        IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
            SkinManager.instance.getDialogMessageText("time_pitch_shift_file_error"),
            SkinManager.instance.getDialogMessageTitle("time_pitch_shift_file_error"),
            IOptionPane.ERROR_MESSAGE);
      return null;
    }
    AudioDecoder decoder = null;
    log.debug("pitchShiftFile(): pitch/time shifting file=" + inputFilename);
    try {
      shiftingfilesem.acquire();
      FileLockManager.startFileRead(inputFilename);
      File file = FileUtil.getFileObject(inputFilename);
      decoder = AudioDecoderFactory.getAudioDecoder(file.getAbsolutePath());
      AudioBuffer audiobuffer = decoder.getAudioBuffer();
      if (decoder != null) {
          double totalseconds = decoder.getTotalSeconds();
          resampler_unused = new float[audiobuffer.getNumChannels()][];
          
          float bytespersecond = (float)decoder.getSampleRate() * audiobuffer.getNumChannels() * decoder.getAudioFormat().getSampleSizeInBits() / 8;
          int totalbytes = (int)Math.floor(timescale * totalseconds * bytespersecond) + decoder.getAudioFormat().getSampleSizeInBits() / 8  * audiobuffer.getNumChannels();
          
          float projectedpitchsftt = pitchsftt * ((float)decoder.getFrameRate() / 44100);
          int actualpitchsftt = (int)pitchsftt;
          while (actualpitchsftt > projectedpitchsftt) {
            actualpitchsftt /= 2;
          }
          while (actualpitchsftt < projectedpitchsftt) {
            actualpitchsftt *= 2;
          }
          if ((window == null) || (window.length != actualpitchsftt)) {
              window = new float[actualpitchsftt];
              for (int k = 0; k < actualpitchsftt; k++) window[k] = -0.5f*(float)Math.cos(2.0f*M_PI*(float)k/(float)actualpitchsftt)+0.5f;
          }
          int quality = OptionsUI.instance.timepitchshiftquality.getValue() + 4;
          if (quality > actualpitchsftt) quality = (int)actualpitchsftt;
          
          VectorInputStream vis = new VectorInputStream(decoder.getDecodedInputStream(), decoder.getAudioFormat(), totalbytes, actualpitchsftt, quality, timescale);

          pitchShift = (float)Math.pow(2.0f, semitoneshift/12.0f);

          // save to new file
          AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
          if (!outputFilename.toLowerCase().endsWith(".wav")) outputFilename = outputFilename + ".wav";
          File outfile = new File(outputFilename);
          AudioInputStream encodedAudioInputStream = new AudioInputStream(vis, decoder.getAudioFormat(), vis.available());
          int	nWrittenFrames = 0;
          try
          {
                  nWrittenFrames = AudioSystem.write(encodedAudioInputStream, fileType, outfile);
                  log.debug("pitchShiftFile(): saving shifted file: " + outputFilename);
          }
          catch (IOException e) {
            error = true;
            log.error("pitchShiftFile(): error", e);
          }
          vis.close();
          
      }      
           
    } catch (Exception e) { error = true; log.error("pitchShiftFile(): error", e); }
    if (decoder != null) decoder.close();
    FileLockManager.endFileRead(inputFilename);
    shiftingfilesem.release();
    TimePitchShiftProgressUI.instance.setVisible(false);
    if (!error) return new Normalizer((float)(1.0 / highestval), (int)(Math.floor(timescale * totalbytestoread) + 1));
    else return null;
  }

  int pitchsftt = 2048;
  float[] window = null;
  float pitchShift = 0.0f;

  int totalbytestoread = 0;
  double highestval = 0.0;
  boolean normalizemode = false;

  AudioInputStream audioInputStream;

  private static int analyzechunksize = 8192;

  float[][] gInFIFO;
  float[][] gOutFIFO;
  float[][] gFFTworksp;
  float[][] gLastPhase;
  float[][] gSumPhase;
  float[][] gOutputAccum;
  float[][] gAnaFreq;
  float[][] gAnaMagn;
  float[][] gSynFreq;
  float[][] gSynMagn;

  public boolean stopshifting = false;
  boolean error = false;

  class VectorInputStream extends InputStream {
    public VectorInputStream(InputStream instream, AudioFormat format, int totalbytecount, int fft_FrameSize, int oversampling, double timescale) {
      decodedInputStream = instream;
      audioFormat = format;
      channels = audioFormat.getChannels();
      timescalefactor = timescale;
      resampler = new Resampler(channels, timescalefactor);
//      resampler2 = new ResampleLib[channels];
//      for (int i = 0; i < channels; ++i) resampler2[i] = new ResampleLib(1, timescalefactor);

      ratio = 1.0 / timescale;
      bytespersample = audioFormat.getSampleSizeInBits() / 8;
      bigendian = audioFormat.isBigEndian();
      totalbytes= totalbytecount;
      float seconds = 1.0f;
      chunksize =  (int)(audioFormat.getSampleRate() * seconds / pitchsftt * audioFormat.getChannels() * bytespersample) * pitchsftt;
      // create a fixed length process buffer which will be used to analyze the audio bytes
      readBuffer = new byte[chunksize];
      abData = new byte[chunksize];

      fftFrameSize = fft_FrameSize;
      osamp = oversampling;
      fadeZoneLen = fftFrameSize/2;
      fftFrameSize2 = fftFrameSize/2;
      stepSize = fftFrameSize/osamp;
      freqPerBin = audioFormat.getSampleRate()/fftFrameSize;
      expct = 2.0f*M_PI*stepSize/fftFrameSize;
      inFifoLatency = fftFrameSize-stepSize;
      if (gRover == null) {
        gRover = new int[channels];
        for (int i = 0; i < channels; ++i) gRover[i] = inFifoLatency;
      }
      gInFIFO = new float[channels][MAX_FRAME_LENGTH];
      gOutFIFO = new float[channels][MAX_FRAME_LENGTH];
      gFFTworksp = new float[channels][2*MAX_FRAME_LENGTH];
      gLastPhase = new float[channels][MAX_FRAME_LENGTH/2+1];
      gSumPhase = new float[channels][MAX_FRAME_LENGTH/2+1];
      gOutputAccum = new float[channels][2*MAX_FRAME_LENGTH];
      gAnaFreq = new float[channels][MAX_FRAME_LENGTH];
      gAnaMagn = new float[channels][MAX_FRAME_LENGTH];
      gSynFreq = new float[channels][MAX_FRAME_LENGTH];
      gSynMagn = new float[channels][MAX_FRAME_LENGTH];
    }

    InputStream decodedInputStream;
    AudioFormat audioFormat;
    // create a temporary buffer to read bytes from the decoded audio format
    byte[] abData;
    long nBytesRead = 0;
    int totalBytesRead = 0;
    byte[] readBuffer = null;
    int readCount = 0;
    int chunksize;

    public void close() { try {
        audioInputStream.close();
        decodedInputStream.close();
      } catch (Exception e) { } }

    float[] lastsamples = null;
    double position = 0.0;
    double timescalefactor;
    double ratio;

    int resamplerinuse = 0;
    Resampler resampler;
    ResampleLib[] resampler2;

    float getNextSample() throws IOException {
      return getSample();
/*
      float returnval;
      if (lastsamples == null) {
        lastsamples = new float[4];
        for (int i = 0; i < lastsamples.length; ++i) lastsamples[i] = getSample();
      }

      if (position < 1.0) {
        returnval = lastsamples[0];
      } else {
        returnval = AudioLib.hermite2((float)(position - Math.floor(position)), lastsamples[0], lastsamples[1], lastsamples[2], lastsamples[3]);
//          float delta = (float)(position - Math.floor(position));
//          returnval = delta * lastsamples[1] + (1.0f - delta) *lastsamples[2];
      }

      position += ratio;
      while (position >= 2.0) {
//        position -= ratio;
        position -= 1.0;
        lastsamples[0] = lastsamples[1];
        lastsamples[1] = lastsamples[2];
        lastsamples[2] = lastsamples[3];
        lastsamples[3] = getSample();
      }
      return returnval;
*/
    }

    float getSample() throws IOException {
      if (stopshifting || RapidEvolution.instance.terminatesignal) { error = true;  throw new IOException(); }
      if ((samples == null) || (samples[0].length == nextsample)) {
        nextsample = 0;
        nextchannel = 0;
        boolean done = false;
        // while there are still bytes in the audio input stream
        while ((nBytesRead != -1) && !done) {
          // read the bytes into the temporary buffer
          try { nBytesRead = decodedInputStream.read(abData, 0, abData.length);
          totalbytestoread += nBytesRead;
            for (int i = 0; i < nBytesRead; ++i) {
              // add the bytes to the process buffer
              readBuffer[readCount++] = abData[i];
              // when the process buffer is full, send for processing
              if (readCount == readBuffer.length) {
                // process read buffer
                shiftSegment(readBuffer, channels, bytespersample);
                done = true;
                readCount = 0;
              }
            }
            totalBytesRead += nBytesRead;
          } catch (IOException e) { error = true;  log.error("getSample(): error", e); }
        }
        if (!done && (readCount > 0)) {
          while (readCount < readBuffer.length) readBuffer[readCount++] = 0;
          shiftSegment(readBuffer, channels, bytespersample);
          readCount = 0;
        }
      }
      float returnval = samples[nextchannel++][nextsample];
      if (nextchannel >= channels) {
        nextchannel = 0;
        nextsample++;
      }
      return returnval;
    }

    int nextchannel = 0;
    int nextsample = 0;

    float[][] samples = null;

    byte[] output = null;
    int outputpos = 0;
    public int read() throws IOException {
      if (stopshifting || RapidEvolution.instance.terminatesignal) { error = true; throw new IOException(); }
      if ((output == null) || (output.length == outputpos)) {
        for (int c = 0; c < channels; ++c) {

            if (pitchShift == 1.0f) {
              int offset = 0;
              if (c == 0) {
                output = new byte[bytespersample * channels];
                outputpos = 0;
              } else offset = c * bytespersample;

              float sample = (float)getNextSample();
              ByteToSampleReader.setBytesFromNormalizedSample(sample, output, offset, bytespersample, bigendian);
              if (Math.abs(sample) > highestval) highestval = Math.abs(sample);

            } else {

              /* As long as we have not yet collected enough data just read in */
              gInFIFO[c][gRover[c]] = getNextSample();

              int offset = 0;
              if (c == 0) {
                output = new byte[bytespersample * channels];
                outputpos = 0;
              } else offset = c * bytespersample;

              float sample = (float)(gOutFIFO[c][gRover[c]-inFifoLatency]);
              ByteToSampleReader.setBytesFromNormalizedSample(sample, output, offset, bytespersample, bigendian);
              if (Math.abs(sample) > highestval) highestval = Math.abs(sample);

              gRover[c]++;

              /* now we have enough data for processing */
              if (gRover[c] >= fftFrameSize) {
                      gRover[c] = inFifoLatency;

                      /* do windowing and re,im interleave */
                      for (k = 0; k < fftFrameSize;k++) {
                              gFFTworksp[c][2*k] = gInFIFO[c][k] * window[k];
                              gFFTworksp[c][2*k+1] = 0.0f;
                      }


                      /* ***************** ANALYSIS ******************* */
                      /* do transform */
                      smbFft(gFFTworksp[c], fftFrameSize, -1);

                      float[] old_gFFTworksp = new float[fftFrameSize];
                      for (k = 0; k < fftFrameSize; ++k) old_gFFTworksp[k] = gFFTworksp[c][k];

                      /* this is the analysis step */
                      for (k = 0; k <= fftFrameSize2; k++) {

                              /* de-interlace FFT buffer */
                              real = gFFTworksp[c][2*k];
                              imag = gFFTworksp[c][2*k+1];

                              /* compute magnitude and phase */
                              magn = 2.0f*(float)Math.sqrt(real*real + imag*imag);
                              phase = (float)smbatan2(imag,real);

                              /* compute phase difference */
                              tmp = phase - gLastPhase[c][k];
                              gLastPhase[c][k] = phase;

                              /* subtract expected phase difference */
                              tmp -= (double)k*expct;

                              /* map delta phase into +/- Pi interval */
                              qpd = (int)(tmp/M_PI);
                              if (qpd >= 0) qpd += qpd&1;
                              else qpd -= qpd&1;
                              tmp -= M_PI*(double)qpd;

                              /* get deviation from bin frequency from the +/- Pi interval */
                              tmp = osamp*tmp/(2.0f*M_PI);

                              /* compute the k-th partials' true frequency */
                              tmp = (float)k*freqPerBin + tmp*freqPerBin;

                              /* store magnitude and true frequency in analysis arrays */
                              gAnaMagn[c][k] = magn;
                              gAnaFreq[c][k] = tmp;

                      }



                      /* ***************** PROCESSING ******************* */
                      /* this does the actual pitch shifting */
                      for (int n = 0; n < fftFrameSize; ++n) gSynMagn[c][n] = 0.0f;
                      for (int n = 0; n < fftFrameSize; ++n) gSynFreq[c][n] = 0.0f;
                      for (k = 0; k <= fftFrameSize2; k++) {
                              index = (int)(k/pitchShift);
                              if (index <= fftFrameSize2) {
                                      gSynMagn[c][k] += gAnaMagn[c][index];
                                      gSynFreq[c][k] = gAnaFreq[c][index] * pitchShift;
                              }
                      }

                      // formant correction would have to be done here?
//                      for (k = 0; k <= fftFrameSize2; k++) {
//                          gSynMagn[c][k] = gAnaMagn[c][k];
//                          gSynFreq[c][k] = gAnaFreq[c][k]; //(gSynFreq[c][k] * gAnaFreq[c][k]);
//                      }

                      /* ***************** SYNTHESIS ******************* */
                      /* this is the synthesis step */
                      for (k = 0; k <= fftFrameSize2; k++) {

                              /* get magnitude and true frequency from synthesis arrays */
                              magn = gSynMagn[c][k];
                              tmp = gSynFreq[c][k];

                              /* subtract bin mid frequency */
                              tmp -= (double)k*freqPerBin;

                              /* get bin deviation from freq deviation */
                              tmp /= freqPerBin;

                              /* take osamp into account */
                              tmp = 2.0f*M_PI*tmp/osamp;

                              /* add the overlap phase advance back in */
                              tmp += (double)k*expct;

                              /* accumulate delta phase to get bin phase */
                              gSumPhase[c][k] += tmp;
                              phase = gSumPhase[c][k];

                              /* get real and imag part and re-interleave */
                              gFFTworksp[c][2*k] = magn*(float)Math.cos(phase);
                              gFFTworksp[c][2*k+1] = magn*(float)Math.sin(phase);
                      }

                      /* zero negative frequencies */
                      for (k = fftFrameSize+2; k < 2*fftFrameSize; k++) gFFTworksp[c][k] = 0.0f;

                      // ...or would formant correction be done here?
//                      for (k = 0; k < fftFrameSize; k++) {
//                          gFFTworksp[c][k] = (gFFTworksp[c][k] + old_gFFTworksp[k]) / 2;
//                      }

                      /* do inverse transform */
                      smbFft(gFFTworksp[c], fftFrameSize, 1);

                      /* do windowing and add to output accumulator */
                      for(k=0; k < fftFrameSize; k++) {
//                              window = -0.5f*(float)Math.cos(2.0f*M_PI*(float)k/(float)fftFrameSize)+0.5f;
                              gOutputAccum[c][k] += 2.0f*window[k]*gFFTworksp[c][2*k]/(fftFrameSize2*osamp);
                      }
                      for (k = 0; k < stepSize; k++) gOutFIFO[c][k] = gOutputAccum[c][k];

                      /* shift accumulator */
                      for (int n = 0; n < fftFrameSize; ++n) {
                        gOutputAccum[c][n] = gOutputAccum[c][n+stepSize];
                      }

                      /* move input FIFO */
                      for (k = 0; k < inFifoLatency; k++) gInFIFO[c][k] = gInFIFO[c][k+stepSize];
              }
            }
        }
      }

      bytesread++;
      TimePitchShiftProgressUI.instance.progressbar2.setValue((int)(100.0f * bytesread/totalbytes));
      return output[outputpos++];
    }

    public int read(byte[] b) throws IOException {
      int i = 0;
      while ((i < b.length) && (available() > 0)) {
        b[i++] = (byte)read();
      }
      return i;
    }

    public int read(byte[] b, int off, int len) throws IOException {
      int i = 0;
      while ((i < len) && (available() > 0)) {
        b[off + i++] = (byte)read();
      }
      return i;
    }

    public int available() {
      return totalbytes - bytesread;
    }

    public long skip(long n) throws IOException {
      int i = 0;
      while ((i < n) && (available() > 0)) {
        read();
        ++i;
      }
      return i;
    }

    public boolean markSupported() { return false; }

    public void	reset() {
      bytesread = 0;
      nextsample = 0;
      nextchannel = 0;
      position = 0.0;
      bytevector = null;
      try {
            decodedInputStream.reset();
      } catch (Exception e) { error = true;  }
    }

    byte[] bytevector = null;

    boolean bigendian;
    int channels;
    int bytespersample;
    int bytesread = 0;
    int totalbytes;

    int[] gRover = null;
    float magn, phase, tmp, real, imag;
    float freqPerBin, expct;
    int i,k, qpd, index, inFifoLatency, stepSize, fftFrameSize2, fadeZoneLen;
    int fftFrameSize;
    int osamp;

    private void shiftSegment(byte[] readBuffer, int num_channels, int bytesize) {
      try {
        int totalsamples = readBuffer.length / num_channels / bytesize;
        int extrasamples = 0;
        if (resampleimmediately) {
          if (resampler_unused[0] != null) extrasamples = resampler_unused[0].length;
        }
        samples = new float[num_channels][totalsamples + extrasamples];
        int samplecount = 0;
        if (extrasamples > 0) {
          while (samplecount < extrasamples) {
            for (int i = 0; i < num_channels; ++i) samples[i][samplecount] = resampler_unused[i][samplecount];
            samplecount++;
          }
        }
        int count = 0;
        while (count < readBuffer.length) {
          for (int channel = 0; channel < num_channels; ++channel) {
              double sample = ByteToSampleReader.getSampleFromBytesFactored(readBuffer, count, bytesize, audioFormat.isBigEndian(), normalizemode ? 100.0 : 1.5);
              samples[channel][samplecount] = (float)sample;
              count += bytesize;
          }
          samplecount++;
        }
        if (resampleimmediately) {
          if (resamplerinuse == 0) {
            //libsample
            for (int i = 0; i < num_channels; ++i) {
              float[] output = new float[ (int) Math.floor(timescalefactor *
                  samples[i].length) + 1];
              intHolder holder = new intHolder(0);
              int frames_generated = resampler.resample_process(resampler.
                  processor[i], timescalefactor,
                  samples[i], samples[i].length,
                  0, holder,
                  output, output.length);
              int srcused = holder.value;

              for (int j = 0; j < frames_generated; j++) {
                if (output[j] <= -1.0f)
                  output[j] = -1.0f;
                else if (output[j] >= 1.0f)
                  output[j] = 1.0f;
              }

              if ( (samples[i].length - srcused) > 0) {
                resampler_unused[i] = new float[samples[i].length - srcused];
                for (int j = srcused; j < samples[i].length; ++j)
                    resampler_unused[i][j - srcused] = samples[i][j];
              }
              else resampler_unused[i] = null;
              if (output.length != frames_generated) {
                samples[i] = new float[frames_generated];
                for (int j = 0; j < frames_generated; ++j) samples[i][j] =
                    output[j];
              }
              else samples[i] = output;
            }
          } else if (resamplerinuse == 1) {
            // libsamplerate - secret rabbit code
            for (int i = 0; i < num_channels; ++i) {
              resampler2[i].processSamples(samples[i]);
              if ( (samples[i].length - resampler2[i].frames_used) > 0) {
                resampler_unused[i] = new float[samples[i].length -
                    resampler2[i].frames_used];
                for (int j = resampler2[i].frames_used; j < samples[i].length;
                     ++j) resampler_unused[i][j -
                    resampler2[i].frames_used] = samples[i][j];
              }
              else resampler_unused[i] = null;
              if (resampler2[i].processed_frames.length !=
                  resampler2[i].frames_generated) {
                samples[i] = new float[resampler2[i].frames_generated];
                for (int j = 0; j < resampler2[i].frames_generated; ++j)
                    samples[i][j] = resampler2[i].processed_frames[j];
              }
              else samples[i] = resampler2[i].processed_frames;
            }

          }
        }
      } catch (Exception e) { error = true;  log.error("shiftSegment(): error", e); }
    }
  }

  boolean resampleimmediately = true;
  float[][] resampler_unused;

  static void smbFft(float[] fftBuffer, long fftFrameSize, long sign)
  /*
          FFT routine, (C)1996 S.M.Bernsee. Sign = -1 is FFT, 1 is iFFT (inverse)
          Fills fftBuffer[0...2*fftFrameSize-1] with the Fourier transform of the
          time domain data in fftBuffer[0...2*fftFrameSize-1]. The FFT array takes
          and returns the cosine and sine parts in an interleaved manner, ie.
          fftBuffer[0] = cosPart[0], fftBuffer[1] = sinPart[0], asf. fftFrameSize
          must be a power of 2. It expects a complex input signal (see footnote 2),
          ie. when working with 'common' audio signals our input signal has to be
          passed as {in[0],0.,in[1],0.,in[2],0.,...} asf. In that case, the transform
          of the frequencies of interest is in fftBuffer[0...fftFrameSize].
  */
  {
          float wr, wi, arg;
          int p1, p2;
          float tr, ti, ur, ui, temp;
          int p1r, p1i, p2r, p2i;
          int i, bitm, j, le, le2, k;

          for (i = 2; i < 2*fftFrameSize-2; i += 2) {
                  for (bitm = 2, j = 0; bitm < 2*fftFrameSize; bitm <<= 1) {
                          if ((i & bitm) != 0) j++;
                          j <<= 1;
                  }
                  if (i < j) {
                          p1 = i; p2 = j;
                          temp = fftBuffer[p1]; fftBuffer[p1++] = fftBuffer[p2];
                          fftBuffer[p2++] = temp; temp = fftBuffer[p1];
                          fftBuffer[p1] = fftBuffer[p2]; fftBuffer[p2] = temp;
                  }
          }
          for (k = 0, le = 2; k < (long)(Math.log(fftFrameSize)/Math.log(2.)); k++) {
                  le <<= 1;
                  le2 = le>>1;
                  ur = 1.0f;
                  ui = 0.0f;
                  arg = M_PI / (le2>>1);
                  wr = (float)Math.cos(arg);
                  wi = sign*(float)Math.sin(arg);
                  for (j = 0; j < le2; j += 2) {
                          p1r = j; p1i = p1r+1;
                          p2r = p1r+le2; p2i = p2r+1;
                          for (i = j; i < 2*fftFrameSize; i += le) {
                                  tr = fftBuffer[p2r] * ur - fftBuffer[p2i] * ui;
                                  ti = fftBuffer[p2r] * ui + fftBuffer[p2i] * ur;
                                  fftBuffer[p2r] = fftBuffer[p1r] - tr; fftBuffer[p2i] = fftBuffer[p1i] - ti;
                                  fftBuffer[p1r] += tr; fftBuffer[p1i] += ti;
                                  p1r += le; p1i += le;
                                  p2r += le; p2i += le;
                          }
                          tr = ur*wr - ui*wi;
                          ui = ur*wi + ui*wr;
                          ur = tr;
                  }
          }
  }

  static double smbatan2(double x, double y) {
    double signx;
    if (x > 0.) signx = 1.;
    else signx = -1.;
    if (x == 0.) return 0.;
    if (y == 0.) return signx * M_PI / 2.;
    return Math.atan2(x, y);
  }

}

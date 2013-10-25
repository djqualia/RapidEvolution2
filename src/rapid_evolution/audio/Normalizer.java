package rapid_evolution.audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import rapid_evolution.audio.ByteToSampleReader;
import rapid_evolution.ui.main.NormalizationProgressUI;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.*;
import com.mixshare.rapid_evolution.audio.AudioBuffer;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoder;
import com.mixshare.rapid_evolution.audio.codecs.AudioDecoderFactory;
import com.mixshare.rapid_evolution.io.FileLockManager;

public class Normalizer {

    private static Logger log = Logger.getLogger(Normalizer.class);
    
  public Normalizer() { }
  public Normalizer(float factor) { normalize_factor = factor; }
  public Normalizer(float factor, int numbytes) {
      normalize_factor = factor;
      totalbytes = numbytes;
   }

  float normalize_factor = 0.0f;
  int totalbytes = 0;
  
  private static Semaphore normalizesem = new Semaphore(1);
  public void normalizeFile(String inputFilename, String outputFilename) {
    if (inputFilename.toLowerCase().equals(outputFilename.toLowerCase())) {
        IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
         SkinManager.instance.getDialogMessageText("normalization_file_error"),
        SkinManager.instance.getDialogMessageTitle("normalization_file_error"),
        IOptionPane.ERROR_MESSAGE);
      return;
    }
    log.debug("normalizeFile(): normalizing file=" + inputFilename);    
    AudioDecoder decoder = null;
    
    try {
      normalizesem.acquire();
      FileLockManager.startFileRead(inputFilename);
      File file = new File (inputFilename);
      decoder = AudioDecoderFactory.getAudioDecoder(file.getAbsolutePath());
      AudioBuffer audiobuffer = decoder.getAudioBuffer();
      if (decoder != null) {
          double totalseconds = decoder.getTotalSeconds();
          float bytespersecond = (float)decoder.getSampleRate() * audiobuffer.getNumChannels() * decoder.getAudioFormat().getSampleSizeInBits() / 8;
          if (totalbytes == 0) totalbytes = (int)Math.floor(totalseconds* bytespersecond) + decoder.getAudioFormat().getSampleSizeInBits() / 8 * audiobuffer.getNumChannels();
          int bytespersample = decoder.getAudioFormat().getSampleSizeInBits() / 8;

          if (normalize_factor == 0.0f) {
            int nBytesRead = 0;
            int framesize =  (int)(decoder.getSampleRate() * 1.0);
            long frames_read = decoder.readNormalizedFrames(framesize);
            while (frames_read > 0) {
                readSegment(audiobuffer, frames_read);
                frames_read = decoder.readNormalizedFrames(framesize);
            }
            normalize_factor = 1.0f / maxsampleval;
            decoder.reset();
          }

          if (log.isDebugEnabled()) log.debug("normalizeFile(): normalize factor=" + normalize_factor + ", totalbytes=" + totalbytes);
          
          VectorInputStream vis = new VectorInputStream(decoder.getDecodedInputStream(), decoder.getAudioFormat(), totalbytes, normalize_factor);

          // save to new file
          AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
          if (!outputFilename.toLowerCase().endsWith(".wav")) outputFilename = outputFilename + ".wav";
          File outfile = new File(outputFilename);
          AudioInputStream encodedAudioInputStream = new AudioInputStream(vis, decoder.getAudioFormat(), vis.available());
          int	nWrittenFrames = 0;
          try
          {
                  nWrittenFrames = AudioSystem.write(encodedAudioInputStream, fileType, outfile);
                  log.debug("normalizeFile(): saved normalized file=" + outputFilename);
                  NormalizationProgressUI.instance.Hide();
          }
          catch (IOException e)
          {
                  log.error("normalizeFile(): error", e);
          }
          vis.close();
      }      
    } catch (Exception e) { log.error("normalizeFile(): error", e); }
    if (decoder != null) decoder.close();
    FileLockManager.endFileRead(inputFilename);    
    normalizesem.release();
    NormalizationProgressUI.instance.Hide();
  }

  AudioInputStream audioInputStream;

  private static int analyzechunksize = 8192;

  public boolean stopnormalizing = false;

  class VectorInputStream extends InputStream {
    public VectorInputStream(InputStream instream, AudioFormat format, int totalbytecount, double normfactor) {
      decodedInputStream = instream;
      audioFormat = format;
      channels = audioFormat.getChannels();
      bytespersample = audioFormat.getSampleSizeInBits() / 8;
      bigendian = audioFormat.isBigEndian();
      totalbytes= totalbytecount;
      factor = (float)normfactor;
      float seconds = 1.0f;
      chunksize =  (int)(audioFormat.getSampleRate() * seconds * audioFormat.getChannels() * bytespersample);
      // create a fixed length process buffer which will be used to analyze the audio bytes
      readBuffer = new byte[chunksize];
      abData = new byte[chunksize];
    }
    float factor;

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

    double ratio;

    float getNextSample() throws IOException {
      return getSample();
    }

    float getSample() throws IOException {
      if (stopnormalizing || RapidEvolution.instance.terminatesignal) throw new IOException();
      if ((samples == null) || (samples[0].length == nextsample)) {
        nextsample = 0;
        nextchannel = 0;
        boolean done = false;
        // while there are still bytes in the audio input stream
        while ((nBytesRead != -1) && !done) {
          // read the bytes into the temporary buffer
          try { nBytesRead = decodedInputStream.read(abData, 0, abData.length);
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
          } catch (IOException e) { log.error("getSample(): error", e); }
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
      return returnval * factor;
    }

    int nextchannel = 0;
    int nextsample = 0;

    float[][] samples = null;

    byte[] output = null;
    int outputpos = 0;
    public int read() throws IOException {
      if (stopnormalizing || RapidEvolution.instance.terminatesignal) throw new IOException();
      if ((output == null) || (output.length == outputpos)) {
        int offset = 0;
        for (int c = 0; c < channels; ++c) {
          if (c == 0) {
            output = new byte[bytespersample * channels];
            outputpos = 0;
          } else offset = c * bytespersample;
          ByteToSampleReader.setBytesFromNormalizedSample((float)getNextSample(), output, offset, bytespersample, bigendian);
        }
      }
      bytesread++;
      NormalizationProgressUI.instance.progressbar2.setValue((int)(100.0f * bytesread/totalbytes));
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
      bytevector = null;
      try {
            decodedInputStream.reset();
      } catch (Exception e) { }
    }

    byte[] bytevector = null;

    boolean bigendian;
    int channels;
    int bytespersample;
    int bytesread = 0;
    int totalbytes;

    private void shiftSegment(byte[] readBuffer, int num_channels, int bytesize) {
      try {
        int totalsamples = readBuffer.length / num_channels / bytesize;
        samples = new float[num_channels][totalsamples];
        int samplecount = 0;
        int count = 0;
        while (count < readBuffer.length) {
          for (int channel = 0; channel < num_channels; ++channel) {
              double sample = ByteToSampleReader.getSampleFromBytesNormalized(readBuffer, count, bytesize, audioFormat.isBigEndian());
              samples[channel][samplecount] = (float)sample;
              count += bytesize;
          }
          samplecount++;
        }
      } catch (Exception e) { log.error("shiftSegment(): error", e); }
    }
  }

  float maxsampleval = 0.0f;
  private void readSegment(AudioBuffer buffer, long frames_read) {
    double[][] sample_data = buffer.getSampleData();
    for (int f = 0; f < frames_read; ++f) {
        for (int c = 0; c < buffer.getNumChannels(); ++c) {
            double sample = sample_data[c][f];
            if (Math.abs(sample) > maxsampleval) maxsampleval = (float)(Math.abs(sample));                
        }
    }
  int samplecount = 0;
  int count = 0;
  }
}

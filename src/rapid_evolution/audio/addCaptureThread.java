package rapid_evolution.audio;

import java.util.Vector;
import java.io.ByteArrayOutputStream;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Logger;

import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.audio.KeyDetector;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.tags.WriteBatchTags;
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

public class addCaptureThread extends Thread {
    
    private static Logger log = Logger.getLogger(addCaptureThread.class);
    
    private double[] norm_keycount = new double[] { 0,0,0,0,0,0,0,0,0,0,0,0 };
    
    public addCaptureThread() {
    	setPriority(Thread.NORM_PRIORITY - 1);
    }
    
  public void run() {
    try {
      AudioEngine.instance.addTargetDataLine.open(AudioEngine.instance.mainAudioFormat);
      AudioEngine.instance.addTargetDataLine.start();
      AudioEngine.instance.addAIS = new AudioInputStream(AudioEngine.instance.addTargetDataLine);
      AudioEngine.instance.addBaos = new ByteArrayOutputStream();
      int nBufferSize = AudioEngine.instance.BUFFER_LENGTH * AudioEngine.instance.mainAudioFormat.getFrameSize();
      byte[] abBuffer = new byte[nBufferSize];
      AudioEngine.instance.addtotalcount = new KeyProbability(rapid_evolution.audio.KeyDetector.analyzechunksize / AudioEngine.instance.mainAudioFormat.getSampleRate() * 1000);
      AudioEngine.instance.addkeyenergycount = new double[12];
      for (int k = 0; k < 12; k++) AudioEngine.instance.addkeyenergycount[k] = 0;
      int bytesize = AudioEngine.instance.mainAudioFormat.getSampleSizeInBits() / 8;
      double[][] wavedata = null;
      int num_channels_to_use = 1;

      while (AudioEngine.instance.adddetecting) {
        int nBytesRead = AudioEngine.instance.addAIS.read(abBuffer);
        if (nBytesRead == -1)
          break;
        AudioEngine.instance.addBaos.write(abBuffer, 0, nBytesRead);
        if (AudioEngine.instance.addBaos.size() > 50000) {
          try {
            byte[] abAudioData = AudioEngine.instance.addBaos.toByteArray();
            int count = 0;
            if (wavedata == null) wavedata = new double[AudioEngine.instance.mainAudioFormat.getChannels()][abAudioData.length / AudioEngine.instance.mainAudioFormat.getChannels() / bytesize];
            int totalsamples = wavedata[0].length;
            int wavecount = 0;
            while (count < abAudioData.length) {
              for (int channel = 0; channel < AudioEngine.instance.mainAudioFormat.getChannels();
                   ++channel) {
                  if (channel < num_channels_to_use) {
                      wavedata[channel][wavecount] = ByteToSampleReader.getSampleFromBytes(abAudioData, count, bytesize, AudioEngine.instance.mainAudioFormat.isBigEndian());
                  }
                  count += bytesize;
              }
              wavecount++;
            }

            long amt = KeyDetector.analyzechunksize; //((60.0 / double(m_startbpm)) * double(WaveFormat.nSamplesPerSec)) / 4.0;
            long icount = 0;
            int maxfrequency = (int) AudioEngine.instance.mainAudioFormat.getSampleRate() / 2;
            double timeinterval = (double) amt / maxfrequency;

            while ( (icount + amt) <= totalsamples) {
                for (int channel = 0; channel < Math.min(num_channels_to_use, AudioEngine.instance.mainAudioFormat.getChannels()); ++channel) {
                    for (int i = 0; i < norm_keycount.length; ++i)
                        norm_keycount[i] = 0.0;
                    KeyDetector.countKeyProbabilities( wavedata[channel], icount, amt, timeinterval,
                            maxfrequency, AudioEngine.instance.addtotalcount, AudioEngine.instance.addkeyenergycount, norm_keycount);
                }
              icount += amt;
            }

            String keystr = KeyDetector.determineKey(AudioEngine.instance.addtotalcount, AudioEngine.instance.addkeyenergycount).getStartKey();
            AddSongsUI.instance.addsongsstartkeyfield.setText(keystr);
//              mainAIS = new AudioInputStream(mainTargetDataLine);
            AudioEngine.instance.addBaos = new ByteArrayOutputStream();
          } catch (Exception e) {
            AudioEngine.instance.adddetecting = false;
            AudioEngine.instance.addTargetDataLine.stop();
            AudioEngine.instance.addTargetDataLine.close();
            AddSongsUI.instance.addsongsdetectkeybutton.setText(SkinManager.instance.getTextFor(AddSongsUI.instance.addsongsdetectkeybutton));
            log.error("run(): error", e);
          }
        }
      }

    }
    catch (Exception e) { log.error("run(): error", e); }
    AudioEngine.instance.adddetecting = false;
  }
}

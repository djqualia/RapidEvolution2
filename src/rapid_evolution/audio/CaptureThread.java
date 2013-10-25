package rapid_evolution.audio;

import java.io.ByteArrayOutputStream;
import java.util.Vector;
import rapid_evolution.RapidEvolution;
import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Logger;

import rapid_evolution.threads.ErrorMessageThread;
import rapid_evolution.ui.main.SearchPane;
import com.mixshare.rapid_evolution.music.Key;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.KeyDetector;
import rapid_evolution.ui.RapidEvolutionUI;
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

public class CaptureThread extends Thread {
    
    private static Logger log = Logger.getLogger(CaptureThread.class);
    
    public CaptureThread() {
    	setPriority(Thread.NORM_PRIORITY - 1);
    }
    
    private double[] norm_keycount = new double[] { 0,0,0,0,0,0,0,0,0,0,0,0 };
    
  public void run() {
    try {
      AudioEngine.instance.mainTargetDataLine.open(AudioEngine.instance.mainAudioFormat);
      AudioEngine.instance.mainTargetDataLine.start();
      AudioEngine.instance.mainAIS = new AudioInputStream(AudioEngine.instance.mainTargetDataLine);
      AudioEngine.instance.mainBaos = new ByteArrayOutputStream();
      int nBufferSize = AudioEngine.instance.BUFFER_LENGTH * AudioEngine.instance.mainAudioFormat.getFrameSize();
      byte[] abBuffer = new byte[nBufferSize];
      AudioEngine.instance.maintotalcount = new KeyProbability(rapid_evolution.audio.KeyDetector.analyzechunksize / AudioEngine.instance.mainAudioFormat.getSampleRate() * 1000);
      AudioEngine.instance.mainkeyenergycount = new double[12];
      for (int k = 0; k < 12; k++) AudioEngine.instance.mainkeyenergycount[k] = 0;
      double[][] wavedata = null;
      int num_channels_to_use = 1;
//      int minanalyzesize = KeyDetector.analyzechunksize * AudioEngine.instance.mainAudioFormat.getChannels() * (AudioEngine.instance.mainAudioFormat.getSampleSizeInBits() / 8);

      while (AudioEngine.instance.detecting) {
        int nBytesRead = AudioEngine.instance.mainAIS.read(abBuffer);
        if (nBytesRead == -1)
          break;
        AudioEngine.instance.mainBaos.write(abBuffer, 0, nBytesRead);
        if (AudioEngine.instance.mainBaos.size() > 50000) {
          try {
            byte[] abAudioData = AudioEngine.instance.mainBaos.toByteArray();

            long totalsamples = abAudioData.length / AudioEngine.instance.mainAudioFormat.getChannels() /
                (AudioEngine.instance.mainAudioFormat.getSampleSizeInBits() / 8);
            int count = 0;
            int wavecount = 0;
            int bytesize = AudioEngine.instance.mainAudioFormat.getSampleSizeInBits() / 8;
            if (wavedata == null) wavedata = new double[AudioEngine.instance.mainAudioFormat.getChannels()][abAudioData.length / AudioEngine.instance.mainAudioFormat.getChannels() / bytesize];
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
                            maxfrequency, AudioEngine.instance.maintotalcount, AudioEngine.instance.mainkeyenergycount, norm_keycount);
                }
              icount += amt;
            }

            String keystr = KeyDetector.determineKey(AudioEngine.instance.maintotalcount, AudioEngine.instance.mainkeyenergycount).getStartKey();
            SearchPane.instance.keyfield.setText(keystr);
//              mainAIS = new AudioInputStream(mainTargetDataLine);
            AudioEngine.instance.mainBaos = new ByteArrayOutputStream();
          } catch (Exception e) { log.error("run(): error", e); }
        }
      }
      String keystr = KeyDetector.determineKey(AudioEngine.instance.maintotalcount, AudioEngine.instance.mainkeyenergycount).getStartKey();
      SearchPane.instance.keyfield.setText(keystr);
      if (!SearchPane.instance.keyfield.getText().equals("")) {
          RapidEvolutionUI.instance.setCurrentKey(Key.getKey(SearchPane.instance.keyfield.getText()));
        RapidEvolutionUI.instance.UpdateRoutine();
//          UpdateThread ut = new UpdateThread();
//          ut.start();
      }
    }
    catch (Exception e) {
      AudioEngine.instance.detecting = false;
      AudioEngine.instance.mainTargetDataLine.stop();
      AudioEngine.instance.mainTargetDataLine.close();
      SearchPane.instance.detectbutton.setText(SkinManager.instance.getTextFor(SearchPane.instance.detectbutton));
      log.error("run(): error", e);
      new ErrorMessageThread(SkinManager.instance.getDialogMessageTitle("live_key_detection_error"), SkinManager.instance.getDialogMessageText("live_key_detection_error") + e.getMessage()).start();                      
    }
    AudioEngine.instance.detecting = false;
  }
}

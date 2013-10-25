package rapid_evolution.audio;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import javax.sound.sampled.LineUnavailableException;
import rapid_evolution.ui.main.SearchPane;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.Vector;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongLinkedList;

import javax.sound.sampled.AudioFileFormat;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongUI;
import java.io.ByteArrayOutputStream;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.threads.*;

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
public class AudioEngine {
    
    private static Logger log = Logger.getLogger(AudioEngine.class);
    
    public AudioEngine() {
        instance = this;
    }

    public static AudioEngine instance = null;

    //key detection
    public int wavechunksize = 8192;
    public TargetDataLine mainTargetDataLine;
    public TargetDataLine editTargetDataLine;
    public TargetDataLine addTargetDataLine;
    public AudioInputStream mainAIS;
    public AudioInputStream editAIS;
    public AudioInputStream addAIS;
    public boolean detecting = false;
    public boolean editdetecting = false;
    public boolean adddetectingfromfile = false;
    public boolean adddetecting = false;
    public AudioFormat mainAudioFormat = getAudioFormat();
    public int BUFFER_LENGTH = 1024;
    public ByteArrayOutputStream mainBaos;
    public ByteArrayOutputStream editBaos;
    public ByteArrayOutputStream addBaos;
    public KeyProbability maintotalcount;
    public KeyProbability edittotalcount;
    public KeyProbability addtotalcount;
    public double[] mainkeyenergycount;
    public double[] editkeyenergycount;
    public double[] addkeyenergycount;
    public boolean editdetectingfromfile = false;
    public File addtempoutputFile = null;
    // file playing
    public boolean EditSongStopPlayback = false;
    public boolean AddSongStopPlayback = false;
    Semaphore DetectingKeyFromFileSem = new Semaphore(1);
    public File edittempoutputFile = null;
    public Semaphore DetectingBPMSem = new Semaphore(1);
    public File detectbpmoutputfile = null;
    public boolean editsongsplaying = false;
    public boolean addsongsplaying = false;

    public static InputStream openInput(String fileName) throws IOException {
      File file = new File(fileName);
      InputStream fileIn = new FileInputStream(file);
      BufferedInputStream buffIn = new BufferedInputStream(fileIn);
      return buffIn;
    }

    private void AddSongplayAudio(String filename) {
      try{
        File soundFile = new File(filename);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
        AddSongStopPlayback = false;
        new PlayThread(sourceDataLine, 1, audioFormat, audioInputStream).start();
      } catch (Exception e) {
          log.error("AddSongplayAudio(): error", e);
        AddSongStopPlayback = true;
        AddSongsUI.instance.addsongsplaybutton.setEnabled(true);
      }
    }

    private void EditSongplayAudio(String filename) {
      try{
        File soundFile = new File(filename);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
        EditSongStopPlayback = false;
        new PlayThread(sourceDataLine, 0, audioFormat, audioInputStream).start();
      } catch (Exception e) {
          log.error("EditSongplayAudio(): error", e);
        EditSongStopPlayback = true;
        EditSongUI.instance.editsongsplaybutton.setEnabled(true);
      }
    }

    public void addCaptureAudio(){
      try{
        if (mainAudioFormat == null) mainAudioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, mainAudioFormat);
        addTargetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        new addCaptureThread().start();
      } catch (Exception e) {
      	if (mainAudioFormat != null) {          	    
      	    if (mainAudioFormat.getChannels() == 1) {
      	        log.debug("addCaptureAudio(): failed to get mono input device, trying stereo");
      	        mainAudioFormat = getStereoAudioFormatBigEndian();
      	      addCaptureAudio();
      	        return;
      	    }
      	}          
        log.error("addCaptureAudio(): error", e);
        new ErrorMessageThread(SkinManager.instance.getDialogMessageTitle("live_key_detection_error"), SkinManager.instance.getDialogMessageText("live_key_detection_error") + e.getMessage()).start();                        
        adddetecting = false;
        addTargetDataLine.stop();
        addTargetDataLine.close();
        AddSongsUI.instance.addsongsdetectkeybutton.setText(SkinManager.instance.getTextFor(AddSongsUI.instance.addsongsdetectkeybutton));
      }
    }

    public void editCaptureAudio(){
      try{
        if (mainAudioFormat == null) mainAudioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, mainAudioFormat);
        editTargetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        new editCaptureThread().start();
      } catch (Exception e) {
        	if (mainAudioFormat != null) {          	    
          	    if (mainAudioFormat.getChannels() == 1) {
          	        log.debug("editCaptureAudio(): failed to get mono input device, trying stereo");
          	        mainAudioFormat = getStereoAudioFormatBigEndian();
          	      editCaptureAudio();
          	        return;
          	    }
          	}
        	log.error("editCaptureAudio(): error", e);
        new ErrorMessageThread(SkinManager.instance.getDialogMessageTitle("live_key_detection_error"), SkinManager.instance.getDialogMessageText("live_key_detection_error") + e.getMessage()).start();                        
        editdetecting = false;
        editTargetDataLine.stop();
        editTargetDataLine.close();
        EditSongUI.instance.editsongsdetectkeybutton.setText(SkinManager.instance.getTextFor(EditSongUI.instance.editsongsdetectkeybutton));
      }
    }

    public void captureAudio(){
      try{
        if (mainAudioFormat == null) mainAudioFormat = getAudioFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, mainAudioFormat);
        mainTargetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        new CaptureThread().start();
      } catch (Exception e) {
          	if (mainAudioFormat != null) {          	    
          	    if (mainAudioFormat.getChannels() == 1) {
          	        log.debug("captureAudio(): failed to get mono input device, trying stereo");
          	        mainAudioFormat = getStereoAudioFormatBigEndian();
          	        captureAudio();
          	        return;
          	    }
          	}
          new ErrorMessageThread(SkinManager.instance.getDialogMessageTitle("live_key_detection_error"), SkinManager.instance.getDialogMessageText("live_key_detection_error") + e.getMessage()).start();                
          log.error("captureAudio(): error", e);
        detecting = false;
        mainTargetDataLine.stop();
        mainTargetDataLine.close();
        SearchPane.instance.detectbutton.setText(SkinManager.instance.getTextFor(SearchPane.instance.detectbutton));
      }
    }

    private AudioFormat getAudioFormat(){
      float sampleRate = 44100.0F;
      //8000,11025,16000,22050,44100
      int sampleSizeInBits = 16;
      //8,16
      int channels = 1;
      boolean signed = true;
      boolean bigEndian = false;
      return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
//      return new AudioFormat(44100.0F, 16, 1, true, false);
    }
    
    private AudioFormat getStereoAudioFormat() {
        float sampleRate = 44100.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
//        return new AudioFormat(44100.0F, 16, 1, true, false);
    }
    
    private AudioFormat getStereoAudioFormatBigEndian() {
        float sampleRate = 44100.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
//        return new AudioFormat(44100.0F, 16, 1, true, false);
    }

    public void playAudioFile( String filename ) {
      new StartEditPlayThread(filename).start();
    }

    public void addPlayAudioFile( String filename ) {
      new StartAddPlayThread(filename).start();
    }

    public void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException
    {
      byte[] data = new byte[4096];
      SourceDataLine line = getLine(targetFormat);
      if (line != null)
      {
        // Start
        line.start();
        int nBytesRead = 0, nBytesWritten = 0;
        while ((nBytesRead != -1) && (!EditSongStopPlayback))
        {
            nBytesRead = din.read(data, 0, data.length);
            if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
        }
        // Stop
        line.drain();
        line.stop();
        line.close();
        din.close();
        EditSongUI.instance.editsongsplaybutton.setText(SkinManager.instance.getTextFor(EditSongUI.instance.editsongsplaybutton));
        editsongsplaying = false;
        EditSongStopPlayback = true;
        mainAudioFormat = null;
      }
    }

    public SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
    {
      SourceDataLine res = null;
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
      res = (SourceDataLine) AudioSystem.getLine(info);
      res.open(audioFormat);
      return res;
    }

    public void addrawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException
    {
      byte[] data = new byte[4096];
      SourceDataLine line = getLine(targetFormat);
      if (line != null)
      {
        // Start
        line.start();
        int nBytesRead = 0, nBytesWritten = 0;
        while ((nBytesRead != -1) && (!AddSongStopPlayback))
        {
            nBytesRead = din.read(data, 0, data.length);
            if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
        }
        // Stop
        line.drain();
        line.stop();
        line.close();
        din.close();
        AddSongsUI.instance.addsongsplaybutton.setText(SkinManager.instance.getTextFor(AddSongsUI.instance.addsongsplaybutton));
        addsongsplaying = false;
        AddSongStopPlayback = true;
        mainAudioFormat = null;
      }
    }

    public void DetectBatchKeys(SongLinkedList[] songs) {
      new DetectBatchKeysThread(songs).start();
    }
    
    public void DetectRGAs(SongLinkedList[] songs) {
        new DetectBatchRGAsThread(songs).start();
    }

}

package rapid_evolution.audio;

import java.io.File;
import java.io.FileInputStream;
import java.util.BitSet;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;

import org.apache.log4j.Logger;
import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.sound.spi.FlacAudioFileReader;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import java.util.Iterator;

import com.mixshare.rapid_evolution.audio.tags.readers.mp4.DefaultMP4TagReader;
import com.mixshare.rapid_evolution.audio.tags.readers.mp4.JAudioMP4TagReader;
import com.mixshare.rapid_evolution.audio.codecs.decoders.QTAudioDecoder;
import com.mixshare.rapid_evolution.qt.QTUtil;
import davaguine.jmac.info.APEInfo;

public class AudioLib {
    
    private static Logger log = Logger.getLogger(AudioLib.class);
    
    private AudioLib() { }

    public static double readFlacTime(String filename) {
        try {
          File file = new File(filename);
          FlacAudioFileReader reader = new FlacAudioFileReader();
          FLACDecoder decoder = new FLACDecoder(reader.getAudioInputStream(file));          
          StreamInfo streaminfo = decoder.readStreamInfo();
          double seconds = ((double)streaminfo.getTotalSamples()) / streaminfo.getSampleRate();
          return seconds;
        } catch (Exception e) { log.error("readFlacTime(): error", e); }
        return 0.0;        
    }
    
    public static double get_track_time(String filename) {
        if (log.isDebugEnabled()) log.debug("get_track_time(): filename=" + filename);
        if (filename == null)
        	return 0.0;
        double result = 0.0;
      try {          
          String filenameLC = filename.toLowerCase();
        if (filenameLC.endsWith(".ogg")) result = readOggTime(filename);
        else if (filenameLC.endsWith(".flac")) result = readFlacTime(filename);
        else if (filenameLC.endsWith(".mp4") || filename.toLowerCase().endsWith(".m4a")) {
            result = readMP4Time(filename);
        } else if (filename.toLowerCase().endsWith(".ape")) {
            APEInfo info = new APEInfo(new File(filename));
            return ((double)info.getApeInfoLengthMs()) / 1000.0;
        } else if (filename.toLowerCase().endsWith(".mp3")) {
          double time = 0.0;
          File f = new File(filename);
          AudioInputStream in = null;          
          try {
            AudioFileFormat baseFileFormat = null;
            AudioFormat baseFormat = null;
            in = AudioSystem.getAudioInputStream(f);            
            baseFileFormat = AudioSystem.getAudioFileFormat(in);
            baseFormat = baseFileFormat.getFormat();
             if (baseFileFormat instanceof TAudioFileFormat)
             {
                 Map properties = ((TAudioFileFormat)baseFileFormat).properties();
                 String key = "duration";
                 Long val = (Long) properties.get(key);
                 time = val.doubleValue() / 1000000.0;
             }
             if (time != 0.0) result = time;                        
            in.close();
          } catch (Exception e) { if (in != null) in.close(); }
          if (result == 0.0) {
              FileInputStream inputStream = new FileInputStream(f);
	          Bitstream m_bitstream = new Bitstream(inputStream);
	          Header m_header = m_bitstream.readFrame();
	          double mediaLength = f.length();
	          double nTotalMS = 0.0;
	          if (mediaLength != AudioSystem.NOT_SPECIFIED) { nTotalMS = m_header.total_ms((int)mediaLength); }
	          time = nTotalMS / 1000.0;
	          if (time != 0.0) result = time;
              inputStream.close();
          }
        }
        if (result == 0.0) {
	        File file = new File(filename);
	        AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
	        if (log.isDebugEnabled()) log.debug("get_track_time(): aff=" + aff);
	        AudioInputStream in = AudioSystem.getAudioInputStream(file);
	        AudioFormat baseFormat = in.getFormat();
	        if (log.isDebugEnabled()) log.debug("get_track_time(): baseFormat=" + baseFormat);
	        double seconds = aff.getFrameLength() / baseFormat.getFrameRate();
	        if (log.isDebugEnabled()) log.debug("get_track_time(): seconds=" + seconds);
	        in.close();
	        if (seconds != 0.0) result = seconds;
        }
      } catch (Exception e) { log.error("get_track_time(): error", e); }
      if (result == 0.0) {
          try {
              if (QTUtil.isQuickTimeSupported()) {
                  result = QTUtil.getTotalSeconds(filename);
              }
          } catch (Exception e) { }
      }            
      if (log.isDebugEnabled()) log.debug("get_track_time(): result=" + result);
      return result;
    }

    public static double readMP4Time(String file_in) {
        try {
            DefaultMP4TagReader reader = new DefaultMP4TagReader(file_in);
            return reader.getTimeInSeconds();
        } catch (Exception e) {
            log.error("readMP4Time(): error", e);
        }
        return 0.0;
    }
    
    public static double readOggTime(String file_in) {
      if (!file_in.toLowerCase().endsWith(".ogg")) return 0;
      try {
          JAudioMP4TagReader tagReader = new JAudioMP4TagReader(file_in);
          double result = tagReader.getTimeInSeconds();
          if (result != 0.0)
              return result;          
          
          File file = new File(file_in);
        // Get AudioFileFormat from given file.
        AudioInputStream in = AudioSystem.getAudioInputStream(file);            
        AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(in);
        log.debug("readOggTime(): baseFileFormat=" + baseFileFormat + ", properties=" + baseFileFormat.properties());
        if (baseFileFormat instanceof TAudioFileFormat) {
            Map props = ((TAudioFileFormat)baseFileFormat).properties();
            if (props != null) {
                Iterator propsIter = props.entrySet().iterator();
                while (propsIter.hasNext()) {
                  Map.Entry entry = (Map.Entry)propsIter.next();
                  String id = (String)entry.getKey();
                  Object value = entry.getValue();
                  log.debug("readOggTime(): id=" + id + ", value=" + value);
                }                
                // Length in seconds
                return ((((Long)props.get("duration")).doubleValue())/1000000.0);
            }
        }
        in.close();
      } catch (Exception e) { log.error("readOggTime(): error", e); }
      return 0.0;
    }


    // Returns a bitset containing the values in bytes.
    // The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
    public static BitSet getBitSetfromByte(byte thisbyte) {
        BitSet bits = new BitSet();
        for (int i=0; i<8; i++) {
            if ((thisbyte&(1<<(i%8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }
    
    // Returns a byte array of at least length 1.
    // The most significant bit in the result is guaranteed not to be a 1
    // (since BitSet does not support sign extension).
    // The byte-ordering of the result is big-endian which means the most significant bit is in element 0.
    // The bit at index 0 of the bit set is assumed to be the least significant bit.
    public static byte getBytefromBitSet(BitSet bits) {
        byte thisbyte = 0;
        for (int i=0; i<bits.length(); i++) {
            if (bits.get(i)) {
                thisbyte |= 1<<(i%8);
            }
        }
        return thisbyte;
    }

    static public float hermite2(float x, float y0, float y1, float y2, float y3)
    {
        // 4-point, 3rd-order Hermite (x-form)
        float c0 = y1;
        float c1 = 0.5f * (y2 - y0);
        float c3 = 1.5f * (y1 - y2) + 0.5f * (y3 - y0);
        float c2 = y0 - y1 + c1 - c3;

        return ((c3 * x + c2) * x + c1) * x + c0;
    }

    // xm1 ---> x[n-1]  x0 ---> x[n]  x1 ---> x[n+1]   x2 ---> x[n+2]   fractional position stands for a fraction between 0 and 1 to interpolate
    static public float hermite4(float frac_pos, float xm1, float x0, float x1, float x2)
    {
       float c     = (x1 - xm1) * 0.5f;
       float v     = x0 - x1;
       float w     = c + v;
       float a     = w + v + (x2 - x0) * 0.5f;
       float b_neg = w + a;
       return ((((a * frac_pos) - b_neg) * frac_pos + c) * frac_pos + x0);
    }
}

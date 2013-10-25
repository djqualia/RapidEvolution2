package rapid_evolution.audio;

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;

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

public class FFTComb {
    
    private static Logger log = Logger.getLogger(FFTComb.class);
    
  public FFTComb() { }
  public FFTComb(int in_threshold) { threshold = in_threshold; }
  public Vector datastream = new Vector();
  public void pushData(Vector data) {
    for (int i = 0; i < data.size(); ++i) datastream.add(data.get(i));
    if (datastream.size() > threshold) {
//        int power = 1;
//        double val = Math.pow(2.0, power);
//        while (val <= threshold) {
//          val = Math.pow(2.0, ++power);
//        }
//        power+=1;
//        int size = (int)Math.pow(2.0, power);
//        int buffersize = (size - threshold) / 2;
//       float[] processdata = new float[size];
//       for (int i = 0; i < buffersize; ++i) processdata[i] = 0.0f;
//       for (int i = 0; i < threshold; ++i) {
//         processdata[i + buffersize] = (float)((Double)datastream.get(i)).doubleValue();
//       }
//       for (int i = buffersize + threshold; i < processdata.length; ++i) {
//         processdata[i] = 0.0f;
//       }
//       for (int i = 0; i < threshold; ++i) {
//         processdata[i + buffersize] = (float)((Double)datastream.get(i)).doubleValue();
//       }
     float[] processdata = new float[threshold];
     for (int i = 0; i < threshold; ++i) {
       processdata[i] = (float)((Double)datastream.get(i)).doubleValue();
     }
     FastFourierTransform fft = new FastFourierTransform();
     fft.doFFT(processdata);
     float bpm = 0.0f;
     int block = 1;
     float maxvalue = 0.0f;
     float maxbpm = 0.0f;

     log.debug("pushData(): processdatalength: " + String.valueOf(processdata.length));
//       while (block < processdata.length / 2) {
     while (bpm < 300.0f) {
       bpm = ((float)block) / ((float)processdata.length) * 44100.0f / 16.0f * 60.0f;
       float value = processdata[block++];
       log.debug("pushData(): " + String.valueOf(block) + ", bpm " + bpm + ": " + String.valueOf(value));
       if ((Math.abs(value) > maxvalue) && (bpm > 20.0f)) {
         maxvalue = Math.abs(value);
         maxbpm = bpm;
       }
     }
     log.debug("pushData(): detected bpm: " + String.valueOf(maxbpm));
//       for (int i = 0; i <
     Vector newdata = new Vector();
     for (int i = threshold; i < datastream.size(); ++i) newdata.add(datastream.get(i));
     datastream = newdata;
    }
  }
  public void reset() { datastream = new Vector(); }
  int threshold = 131072;
}

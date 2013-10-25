package rapid_evolution.audio;

import java.util.Vector;
import rapid_evolution.RapidEvolution;
import rapid_evolution.audio.AudioEngine;

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
public class DSP {
    public DSP() { }

    float[] hamming = hammingWindow(AudioEngine.instance.wavechunksize);
    public static float[] hammingWindow(int i)
    {
        float af[] = new float[i];
        for(int j = 0; j < i; j++)
        {
            af[j] = 0.5F - 0.5F * (float)Math.cos((6.2831853071795862D * (double)j) / (double)i);
        }

        return af;
    }

    public static double[] hammingWindowDouble(int i)
    {
        double af[] = new double[i];
        for(int j = 0; j < i; j++)
        {
            af[j] = 0.5 - 0.5 * (float)Math.cos((6.2831853071795862D * (double)j) / (double)i);
        }

        return af;
    }    
/*
    void countkeynotesFFT(Vector wavedata, long icount, long amt, double[] keycount, double time, int maxfreq) {
      float[] a = new float[(int)amt];
      for (int i = 0; i < amt; ++i) a[i] = ((Double)wavedata.get((int)(icount + i))).floatValue() * hamming[i];
      FastFourierTransform fft = new FastFourierTransform();
      a = fft.doFFT(a);

      double[] basefrequency = new double[12];

      //a
      basefrequency[0] = 55.0;
      //a#
      basefrequency[1] = 58.27046875;
      //b
      basefrequency[2] = 61.73546875;
      //c
      basefrequency[3] = 65.40640625;
      //c#
      basefrequency[4] = 69.295625;
      //d
      basefrequency[5] = 73.41625;
      //d#
      basefrequency[6] = 77.78125;
      //e
      basefrequency[7] = 82.406875;
      //f
      basefrequency[8] = 87.3071875;
      //f#
      basefrequency[9] = 92.49875;
      //g
      basefrequency[10] = 97.99875;
      //g#
      basefrequency[11] = 103.82625;

      double[] keycount2 = new double[12];
      for (int p = 0; p < 12; ++p) { basefrequency[p] *= AudioEngine.instance.startscale; keycount2[p] = 1.0; }

      for (int p = 0; p < AudioEngine.instance.maxoctaves; p++) {
        for (int i = 0; i < 12; ++i) {
          double top = (double)(basefrequency[i] / (double)maxfreq);
          double bottom = ((double)amt) / 2.0;
          int index = (int)(top * bottom);
          keycount[i] += Math.abs(a[index]);
        }
        for (p = 0; p < 12; ++p) basefrequency[p] *= 2.0;
      }

//    for (int p = 0; p < 12; ++p) { keycount[p] += keycount2[p]; }
    }
*/
    void countkeynotesTest(Vector wavedata, long icount, long amt, double[] keycount, double time, int maxfreq) {
      double s = 1.0;
      double k = 0.2;
      double val = time / (double)amt / Math.sqrt(s);
      double total = 0.0;
      for (int i = 0; i < (int)amt; ++i) {
        double t = (i - k * (double)amt) / s * time / (double)amt;
        total += ((Double)wavedata.get((int)(icount + i))).floatValue() * compute_gabor(0.25, t, 110);
      }
      keycount[0] += Math.abs(total * val);
    }

    double compute_gabor(double w, double t, double n) {
      double real = 1.0 / w * Math.exp(-Math.PI * (t / w) * (t / w)) * Math.cos(2.0 * Math.PI * n * t / w);
      double imag = 1.0 / w * Math.exp(-Math.PI * (t / w) * (t / w)) * Math.sin(2.0 * Math.PI * n * t / w);
      double magnitude = Math.sqrt(real * real + imag * imag);
      return Math.abs(real); //magnitude;
    }

    void countkeynotesSubband(Vector wavedata, long icount, long amt, double[] keycount, double time, int maxfreq) {
      double[] highpass = new double[(int)amt / 2];
      double[] lowpass = new double[(int)amt / 2];
      int i = 0;
      for (int j = 0; j < (int)amt; j += 2) {
        lowpass[i] = (((Double)(wavedata.get(j + (int)icount))).doubleValue() + ((Double)wavedata.get(j + (int)icount + 1)).doubleValue()) / 2.0;
        highpass[i] = (((Double)(wavedata.get(j + (int)icount))).doubleValue() - ((Double)wavedata.get(j + (int)icount + 1)).doubleValue()) / 2.0;
        ++i;
      }
      recurseSubBand(lowpass, 0, ((double)maxfreq) / 2.0, keycount);
      recurseSubBand(highpass, ((double)maxfreq) / 2.0, (double)maxfreq, keycount);
    }

    double[] musicalfreq = { 16.35, 17.32, 18.35, 19.45, 20.60, 21.83, 23.12, 24.50, 25.96, 27.50, 29.14, 30.87, 32.70, 34.65, 36.71, 38.89, 41.20, 43.65, 46.25, 49.00, 51.91, 55.00 };
    void recurseSubBand(double[] data, double minfreq, double maxfreq, double[] keycount) {
      if (data.length == 1) {
//      System.out.println("freq: " + minfreq + " to " + maxfreq + " == " + myabs(data[0]));
            if (maxfreq < 55.0) return;
            double[] basefrequency = new double[13];
            //a
            basefrequency[0] = 55.0;
            //a#
            basefrequency[1] = 58.27046875;
            //b
            basefrequency[2] = 61.73546875;
            //c
            basefrequency[3] = 65.40640625;
            //c#
            basefrequency[4] = 69.295625;
            //d
            basefrequency[5] = 73.41625;
            //d#
            basefrequency[6] = 77.78125;
            //e
            basefrequency[7] = 82.406875;
            //f
            basefrequency[8] = 87.3071875;
            //f#
            basefrequency[9] = 92.49875;
            //g
            basefrequency[10] = 97.99875;
            //g#
            basefrequency[11] = 103.82625;
            //a
            basefrequency[12] = 110.0;

            boolean notfound = true;
            while (notfound) {
              for (int i = 0; i < 12; ++i) {
                if (notfound && (basefrequency[i + 1] >= minfreq)) {
                  if ((minfreq - basefrequency[i]) > (basefrequency[i + 1] - minfreq)) keycount[i] += Math.abs(data[0]);
                  else if ((i + 1) < 12) keycount[i + 1] += Math.abs(data[0]);
                  else keycount[0] += Math.abs(data[0]);
                  notfound = false;
                }
              }
              for (int i = 0; i < 13; ++i) basefrequency[i] *= 2.0;
            }

            //a
            basefrequency[0] = 55.0;
            //a#
            basefrequency[1] = 58.27046875;
            //b
            basefrequency[2] = 61.73546875;
            //c
            basefrequency[3] = 65.40640625;
            //c#
            basefrequency[4] = 69.295625;
            //d
            basefrequency[5] = 73.41625;
            //d#
            basefrequency[6] = 77.78125;
            //e
            basefrequency[7] = 82.406875;
            //f
            basefrequency[8] = 87.3071875;
            //f#
            basefrequency[9] = 92.49875;
            //g
            basefrequency[10] = 97.99875;
            //g#
            basefrequency[11] = 103.82625;
            //a
            basefrequency[12] = 110.0;

            notfound = true;
            while (notfound) {
              for (int i = 0; i < 12; ++i) {
                if (notfound && (basefrequency[i + 1] >= maxfreq)) {
                  if ((maxfreq - basefrequency[i]) < (basefrequency[i + 1] - maxfreq)) keycount[i] += Math.abs(data[0]);
                  else if ((i + 1) < 12) keycount[i + 1] += Math.abs(data[0]);
                  else keycount[0] += Math.abs(data[0]);
                  notfound = false;
                }
              }
              for (int i = 0; i < 13; ++i) basefrequency[i] *= 2.0;
            }


      } else {
        double[] highpass = new double[data.length / 2];
        double[] lowpass = new double[data.length / 2];
        int i = 0;
        for (int j = 0; j < data.length ; j += 2) {
          lowpass[i] = (data[j] + data[j + 1]) / 2.0;
          highpass[i] = (data[j] - data[j + 1]) / 2.0;
          ++i;
        }
        double freqdiff = (maxfreq - minfreq) / 2.0;
        recurseSubBand(lowpass, minfreq, freqdiff + minfreq, keycount);
        recurseSubBand(highpass, maxfreq - freqdiff, maxfreq, keycount);
      }
    }

    /*
      void creatematrix() {
        {
          double waveletwidth = 1.0;
          double coeff2 = (1.0 / waveletwidth);
          double[] frequencyparam = new double[12];
          double[] basefrequency = new double[12];
          //a
          basefrequency[0] = 55.0;
          //a#
          basefrequency[1] = 58.27046875;
          //b
          basefrequency[2] = 61.73546875;
          //c
          basefrequency[3] = 65.40640625;
          //c#
          basefrequency[4] = 69.295625;
          //d
          basefrequency[5] = 73.41625;
          //d#
          basefrequency[6] = 77.78125;
          //e
          basefrequency[7] = 82.406875;
          //f
          basefrequency[8] = 87.3071875;
          //f#
          basefrequency[9] = 92.49875;
          //g
          basefrequency[10] = 97.99875;
          //g#
          basefrequency[11] = 103.82625;
          for (int p = 0; p < 12; ++p) {
                  basefrequency[p] *= startscale;
                  frequencyparam[p] = basefrequency[p] * waveletwidth;
          }
          vmatrixarray = new Vector[wavechunksize / 2];
          int index = 0;
          for (int p = 0; p < wavechunksize / 2; ++p) {
            int amt = p;
            double coeff1 = 1.0 / 44100.0;
            vmatrixarray[index] = new Vector();
             for (int s = 0; s < maxoctaves; ++s) {
                     double st = Math.pow(2, -s);
                     vmatrixarray[index].add(new Vector());
                     Vector vmatrix_s = (Vector)vmatrixarray[index].get(s);
                     int n = 0;
                     for (double ks = 0.5; ks <= 0.5; ks += 0.3) {
                             double k = ks * amt;
                             vmatrix_s.add(new Vector());
                             Vector vmatrix_s_n = (Vector)vmatrix_s.get(n);
                             for (int m = 0; m < amt; ++m) {
                                     double x = (((double)m) - k) / st * coeff1;
                                     double v1 = (x / waveletwidth);
                                     vmatrix_s_n.add(new Vector());
                                     Vector vmatrix_s_n_m = (Vector)vmatrix_s_n.get(m);
                                     for (int z = 0; z < 12; ++z) vmatrix_s_n_m.add(new Double(coeff2 * Math.exp(-3.141592653589793 * v1 * v1) * Math.cos(2.0 * 3.141592653589793 * frequencyparam[z] * v1) * coeff1 / st));
                             }
                             ++n;
                     }
             }
            ++index;
          }
          matrixformed = true;
        }
      }
    */

   Vector convolve(Vector signal, double[] impulse) {
     Vector newvector = new Vector();
     int Msize = signal.size();
     int Nsize = impulse.length;
//    for (int n = 0; n < Msize + Nsize - 1; ++n) {
     for (int n = 0; n < Msize; ++n) {
       double val = 0.0;
       for (int i = 0; i < impulse.length; ++i) {
         int sigindex = n - i;
         if ((sigindex < signal.size()) && (sigindex >= 0)) val += impulse[i] * ((Double)signal.get(sigindex)).doubleValue();
       }
       newvector.add(new Double(val));
     }
     return newvector;
   }

   Vector logdiff(Vector olddata, double previousvalue) {
//    if (true) return olddata;
     Vector newdata = new Vector();
     double avg = 0.0;
     Double d = new Double(Math.log(((Double)olddata.get(0)).doubleValue()) - Math.log(previousvalue));
     avg += d.doubleValue();
     newdata.add(d);
     for (int i = 1; i < olddata.size(); ++i) {
       d = new Double(Math.log(((Double)olddata.get(i)).doubleValue()) - Math.log(((Double)olddata.get(i - 1)).doubleValue()));
       avg += d.doubleValue();
       newdata.add(d);
     }
     avg /= olddata.size();
     double avgsqr = 0.0;
     for (int i = 0; i < olddata.size(); ++i) {
       d = (Double)newdata.get(i);
       avgsqr += (d.doubleValue() - avg) * (d.doubleValue() - avg);
     }
     avgsqr /= (olddata.size() - 1);
     avgsqr = Math.sqrt(avgsqr) * 1.5;
     Vector newerdata = new Vector();
     for (int i = 0; i < olddata.size(); ++i) {
       d = (Double)newdata.get(i);
       if (d.doubleValue() < avgsqr) {
         newerdata.add(new Double(0));
       } else {
         newerdata.add(newdata.get(i));
       }
     }
     return newerdata;
   }

   double[] rectifyandsmoothenvelope(double [] vals, double previousvalue, Hanning hwindow, int hanshift, double hanamplitude) {
//    if (true) return vals;
     double[] newvals = new double[vals.length];
     if (previousvalue < 0) previousvalue = 0;
     for (int i = 0; i < vals.length; ++i) if (vals[i] >= 0.0) newvals[i] = vals[i] * vals[i]; else newvals[i] = 0.0;
     for (int i = 0; i < vals.length; ++i) {
       if (newvals[i] > (hwindow.values[hanshift] * hanamplitude)) {
         hanshift = 0;
         hanamplitude = newvals[i];
       } else {
         newvals[i] = hwindow.values[hanshift++] * hanamplitude;
         if (hanshift == hwindow.values.length) hanshift--;
       }
     }
     return newvals;
   }

   double[] halfwaverectifyenvelope(double [] vals, double previousvalue) {
     double[] newvals = new double[vals.length];
     newvals[0] = Math.abs(previousvalue) - Math.abs(vals[0]);
     if (newvals[0] < 0) newvals[0] = 0;
     for (int i = 1; i < vals.length; ++i) {
       newvals[i] = Math.abs(vals[i - 1]) - Math.abs(vals[i]);
       if (newvals[i] < 0) newvals[i] = 0;
     }
     return newvals;
   }

}

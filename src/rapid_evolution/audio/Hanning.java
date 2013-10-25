package rapid_evolution.audio;

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

public class Hanning {
//    public Hanning(float samplerate) { Hanning((double)samplerate); }
  public Hanning(double samplerate) {
    int n = (int)(samplerate * 0.1);
    values = new double[n];
    for (int i = 0; i < n; ++i) {
      values[i] = 0.5 + 0.5 * Math.cos(2.0 * Math.PI * ((double)i) / ((double)(2 * n)));
    }
  }
  public double[] values = null;
  }

package rapid_evolution;

import rapid_evolution.SongUtil;

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

public class SongColor {
  static public int SongColorVersion = 1;
  public SongColor(int in_windowsize, float in_samplerate, double seconds, int bands) {
    windowsize = in_windowsize;
    samplerate = in_samplerate;
    tracktime = seconds;
    num_bands = bands;
    energy = new double[num_bands];
    avgcentroid = new float[num_bands];
    variancecentroid = new float[num_bands];
    avgrolloff = new int[num_bands];
    variancerolloff = new int[num_bands];
    avgflux = new float[num_bands];
    varianceflux = new float[num_bands];
    avgzerocrossings = new int[num_bands];
    variancezerocrossing = new int[num_bands];
    lowenergy = new float[num_bands];
    for (int i = 0; i < num_bands; ++i) {
      energy[i] = 0.0;
      avgcentroid[i] = 0.0f;
      variancecentroid[i] = 0.0f;
      avgrolloff[i] = 0;
      variancerolloff[i] = 0;
      avgflux[i] = 0.0f;
      varianceflux[i] = 0.0f;
      avgzerocrossings[i] = 0;
      variancezerocrossing[i] = 0;
      lowenergy[i] = 0.0f;
    }
  }

  public int num_bands = 0;

  // size of analyzed window
  public int windowsize = 0;
  // samplerate
  public float samplerate = 0.0f;
  // length of song in seconds
  public double tracktime = 0.0f;

// energy features:
  public double[] energy = null;

// surface features:
  // average spectral brightness
  public float[] avgcentroid = null;
  public float[] variancecentroid = null;

  // average spectral shape
  public int[] avgrolloff = null;
  public int[] variancerolloff = null;

  // average spectral change from window to window
  public float[] avgflux = null;
  public float[] varianceflux = null;

  // average time domain 0 crossings per window (measure of noise)
  public int[] avgzerocrossings = null;
  public int[] variancezerocrossing = null;

  // percentage of analyzed windows with energy less than the average window
  public float[] lowenergy = null;

// rhythm features:
  // primary detected bpm
  public double period0 = 0.0;

  // relative amplitude of primary bpm
  public double amplitude0 = 0.0f;

  // ratio of secondary bpm to primary
  public double ratioperiod1 = 0.0f;

  // relative amplitude of secondary bpm
  public double amplitude1 = 0.0f;

  public double ratioperiod2 = 0.0f;
  public double ratioperiod3 = 0.0f;
  public double amplitude2 = 0.0f;
  public double amplitude3 = 0.0f;

  public String toString() {
    String s = new String();
    s += new String("window size: " + String.valueOf(windowsize) + ", samplerate: " + String.valueOf(samplerate) + ", track time: " + String.valueOf(tracktime) + " seconds\n");
    for (int i = 0; i < num_bands; ++i) {
      if (i == 0) s += new String("lowpass (<200hz):\n");
      else if (i == 1)  s += new String("band1 (200-400hz):\n");
      else if (i == 2)  s += new String("band2 (400-800hz):\n");
      else if (i == 3)  s += new String("band3 (800-1600hz):\n");
      else if (i == 4)  s += new String("band4 (1600-3200hz):\n");
      else if (i == 5)  s += new String("highpass (>3200hz):\n");

      s += String.valueOf("--> energy: " + String.valueOf(energy[i]) + "%\n");

      s += new String("--> spectral brightness (mean-centroid): " + String.valueOf(avgcentroid[i]) + ", variance: " + String.valueOf(variancecentroid[i]) + "\n");
      s += new String("--> spectral shape (mean-rolloff): " + String.valueOf(avgrolloff[i]) + ", variance: " + String.valueOf(variancerolloff[i]) + "\n");
      s += new String("--> spectral change (mean-flux): " + String.valueOf(avgflux[i]) + ", variance: " + String.valueOf(varianceflux[i]) + "\n");
      s += new String("--> spectral noise (mean-zerocrossings): " + String.valueOf(avgzerocrossings[i]) + ", variance: " + String.valueOf(variancezerocrossing[i]) + "\n");
      s += new String("--> low energy percentage: " + String.valueOf(lowenergy[i]) + "%\n");
    }
    s += new String("period0 (primary bpm): " + String.valueOf(period0) + ", amplitude0: " + String.valueOf(amplitude0) + "%\n");
    s += new String("ratio period1: " + String.valueOf(ratioperiod1) + ", amplitude1: " + String.valueOf(amplitude1) + "%\n");
    s += new String("ratio period2: " + String.valueOf(ratioperiod2) + ", amplitude2: " + String.valueOf(amplitude2) + "%\n");
    s += new String("ratio period3: " + String.valueOf(ratioperiod3) + ", amplitude3: " + String.valueOf(amplitude3) + "%");
    return s;
  }

  double getDiff(SongColor color) {
    double similarity = 1.0;

    // track length similarity
    double maxtime = tracktime;
    if (color.tracktime > maxtime) maxtime = color.tracktime;
    double timediff = Math.abs(tracktime - color.tracktime);
    similarity *= (maxtime - timediff) / maxtime;

    for (int i = 0; i < num_bands; ++i) {
    float maxcentroid = avgcentroid[i];
    if (color.avgcentroid[i] > maxcentroid) maxcentroid = color.avgcentroid[i];
    float centroiddiff = Math.abs(avgcentroid[i] - color.avgcentroid[i]);
    similarity *= (maxcentroid - centroiddiff) / maxcentroid;

    maxcentroid = variancecentroid[i];
    if (color.variancecentroid[i] > maxcentroid) maxcentroid = color.variancecentroid[i];
    centroiddiff = Math.abs(variancecentroid[i] - color.variancecentroid[i]);
    similarity *= (maxcentroid - centroiddiff) / maxcentroid;

    int maxrolloff = avgrolloff[i];
    if (color.avgrolloff[i] > maxrolloff) maxrolloff = color.avgrolloff[i];
    int rolldiff = Math.abs(avgrolloff[i] - color.avgrolloff[i]);
    similarity *= ((double)(maxrolloff - rolldiff)) / ((double)maxrolloff);

    maxrolloff = variancerolloff[i];
    if (color.variancerolloff[i] > maxrolloff) maxrolloff = color.variancerolloff[i];
    rolldiff = Math.abs(variancerolloff[i] - color.variancerolloff[i]);
    similarity *= ((double)(maxrolloff - rolldiff)) / ((double)maxrolloff);

    float maxflux = avgflux[i];
    if (color.avgflux[i] > maxflux) maxflux = color.avgflux[i];
    float fluxdiff = Math.abs(avgflux[i] - color.avgflux[i]);
    similarity *= (maxflux - fluxdiff) / maxflux;

    maxflux = varianceflux[i];
    if (color.varianceflux[i] > maxflux) maxflux = color.varianceflux[i];
    fluxdiff = Math.abs(varianceflux[i] - color.varianceflux[i]);
    similarity *= (maxflux - fluxdiff) / maxflux;

    int maxzerocrossings = avgzerocrossings[i];
    if (color.avgzerocrossings[i] > maxzerocrossings) maxzerocrossings = color.avgzerocrossings[i];
    int zerodiff = Math.abs(avgzerocrossings[i] - color.avgzerocrossings[i]);
    similarity *= ((double)(maxzerocrossings - zerodiff)) / ((double)maxzerocrossings);

    maxzerocrossings = variancezerocrossing[i];
    if (color.variancezerocrossing[i] > maxzerocrossings) maxzerocrossings = color.variancezerocrossing[i];
    zerodiff = Math.abs(variancezerocrossing[i] - color.variancezerocrossing[i]);
    similarity *= ((double)(maxzerocrossings - zerodiff)) / ((double)maxzerocrossings);

    float maxlowenergy = lowenergy[i];
    if (color.lowenergy[i] > maxlowenergy) maxlowenergy = color.lowenergy[i];
    float lowdiff = Math.abs(lowenergy[i] - color.lowenergy[i]);
    similarity *= (maxlowenergy - lowdiff) / maxlowenergy;
    }

    similarity *= (1.0 - Math.abs(SongUtil.get_bpmdiff((float)period0, (float)color.period0)) / 100.0f);

    double maxamp0 = amplitude0;
    if (color.amplitude0 > maxamp0) maxamp0 = color.amplitude0;
    double ampdiff0 = Math.abs(amplitude0 - color.amplitude0);
    similarity *= (maxamp0 - ampdiff0) / maxamp0;

    double maxamp1 = amplitude1;
    if (color.amplitude1 > maxamp1) maxamp1 = color.amplitude1;
    double ampdiff1 = Math.abs(amplitude1 - color.amplitude1);
    similarity *= (maxamp1 - ampdiff1) / maxamp1;

    double maxamp2 = amplitude2;
    if (color.amplitude2 > maxamp2) maxamp2 = color.amplitude2;
    double ampdiff2 = Math.abs(amplitude2 - color.amplitude2);
    similarity *= (maxamp2 - ampdiff2) / maxamp2;

    double maxamp3 = amplitude3;
    if (color.amplitude3 > maxamp3) maxamp3 = color.amplitude3;
    double ampdiff3 = Math.abs(amplitude3 - color.amplitude3);
    similarity *= (maxamp3 - ampdiff3) / maxamp3;

    try {
    double maxratio1 = ratioperiod1;
    if (color.ratioperiod1 > maxratio1) maxratio1 = color.ratioperiod1;
    double ratiodiff1 = Math.abs(ratioperiod1 - color.ratioperiod1);
    similarity *= (maxratio1 - ratiodiff1) / maxratio1;

    double maxratio2 = ratioperiod2;
    if (color.ratioperiod2 > maxratio2) maxratio2 = color.ratioperiod2;
    double ratiodiff2 = Math.abs(ratioperiod2 - color.ratioperiod2);
    similarity *= (maxratio2 - ratiodiff2) / maxratio2;

    double maxratio3 = ratioperiod3;
    if (color.ratioperiod3 > maxratio3) maxratio3 = color.ratioperiod3;
    double ratiodiff3 = Math.abs(ratioperiod3 - color.ratioperiod3);
    similarity *= (maxratio3 - ratiodiff3) / maxratio3;
    } catch (Exception e) { }

    return similarity;
  }
}

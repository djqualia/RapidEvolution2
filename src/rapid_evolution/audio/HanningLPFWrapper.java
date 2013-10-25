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

class HanningLPFWrapper {
  public HanningLPFWrapper(float samplerate) {
    hwindow = new Hanning(samplerate);
  }
  Hanning hwindow = null;
  int hanshift = 0;
  double hamplitude = 0.0;
}

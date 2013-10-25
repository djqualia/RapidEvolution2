package rapid_evolution.audio;

import rapid_evolution.RapidEvolution;
import rapid_evolution.audio.BPMComb;

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
public class BPMFilter {
  public BPMFilter(int in_blocksize, boolean[] usearray, BPMComb mastercomb) {
    blocksize = in_blocksize;
    use = usearray;
    comb = mastercomb;
    datapointers = new int[use.length];
    totalcounted = new long[use.length];
    totaldiff = new double[use.length];
    for (int i = 0; i < use.length; ++i) {
      datapointers[i] = 0;
      totalcounted[i] = 0;
      totaldiff[i] = 0.0;
    }
  }
  public int blocksize;
  boolean[] use = null;
  public BPMComb comb = null;
  int[] datapointers = null;
  long[] totalcounted = null;
  double[] totaldiff = null;
  public void process() {
    for (int i = 0; i < use.length; ++i) {
      if (RapidEvolution.instance.terminatesignal) return;
      if (use[i]) {
        int power = (int)Math.pow(2.0, i);
        while ((datapointers[i] + (blocksize * 2 * power)) <= comb.datastream.size()) {
          totalcounted[i] += blocksize * power;
          for (int j = 0; j < blocksize * power; ++j) {
            int indexa = j + datapointers[i];
            int indexb = j + datapointers[i] + blocksize * power;
            totaldiff[i] += Math.abs(Math.abs(((Double)comb.datastream.get(indexa)).doubleValue()) - Math.abs(((Double)comb.datastream.get(indexb)).doubleValue()));
          }
          datapointers[i] += blocksize * power;
        }
      } else datapointers[i] = comb.datastream.size();
    }
  }
  }

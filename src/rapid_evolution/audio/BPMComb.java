package rapid_evolution.audio;

import java.util.Vector;
import rapid_evolution.RapidEvolution;
import rapid_evolution.audio.BPMFilter;

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
public class BPMComb {
  public BPMComb(boolean[] in_usearray) { usearray = in_usearray; }
  public boolean[] usearray = null;
  public void addBlock(int blocksize) {
    boolean alreadyexists = false;
    for (int i = 0; i < bpmfilters.size(); i++) {
      BPMFilter filter = (BPMFilter)bpmfilters.get(i);
      if (filter.blocksize == blocksize) alreadyexists = true;
    }
    if (!alreadyexists) bpmfilters.add(new BPMFilter(blocksize, usearray, this));
  }
  public Vector datastream = new Vector();
  public void pushData(double[] data, int size) {
    for (int j = 0; j < size; ++j) datastream.add(new Double(data[j]));
    if (RapidEvolution.instance.terminatesignal) return;
    int lowestpointer = -1;
    for (int i = 0; i < bpmfilters.size(); ++i) {
     if (RapidEvolution.instance.terminatesignal) return;
     BPMFilter filter = (BPMFilter)bpmfilters.get(i);
     filter.process();
     for (int z = 0; z < filter.use.length; ++z) {
       if ((lowestpointer == -1) || (filter.datapointers[z] < lowestpointer)) lowestpointer = filter.datapointers[z];
     }
    }
    if (lowestpointer > (datastream.size() / 2)) {
      Vector newdata = new Vector();
      for (int i = lowestpointer; i < datastream.size(); ++i) newdata.add(datastream.get(i));
      datastream = newdata;
      for (int i = 0; i < bpmfilters.size(); ++i) {
       BPMFilter filter = (BPMFilter)bpmfilters.get(i);
       for (int z = 0; z < filter.use.length; ++z) filter.datapointers[z] -= lowestpointer;
      }
    }
  }
  boolean normalized = false;
  public void normalize() {
    if (normalized) return;
    long max = -1;
    int maxindex = -1;
    for (int i = 0; i < bpmfilters.size(); ++i) {
      BPMFilter record = (BPMFilter)bpmfilters.get(i);
      for (int z = 0; z < record.use.length; z++) {
        if ((maxindex == -1) || (record.totalcounted[z] > max)) {
          max = record.totalcounted[z];
          maxindex = i;
        }
      }
    }
    for (int i = 0; i < bpmfilters.size(); ++i) {
      BPMFilter record = (BPMFilter)bpmfilters.get(i);
      for (int z = 0; z < record.use.length; z++) {
        double ratio = ((double)record.totalcounted[z]) / ((double)max);
        if (ratio != 0.0) record.totaldiff[z] = (long)(((double)record.totaldiff[z]) / ratio);
      }
    }
    normalized = true;
  }
  public void reset(boolean[] in_usearray) { bpmfilters = new Vector(); normalized = false; datastream = new Vector(); usearray = in_usearray; }
  Vector bpmfilters = new Vector();
}

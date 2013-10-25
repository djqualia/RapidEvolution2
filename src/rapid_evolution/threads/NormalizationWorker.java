package rapid_evolution.threads;

import rapid_evolution.audio.Normalizer;
import java.io.File;

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
public class NormalizationWorker extends Thread {
  public NormalizationWorker(Normalizer normalizerclass, String file, String outfile, File tFile) {
    normalizer = normalizerclass;
    filestr = file;
    outfilestr = outfile;
    tempFile = tFile;
    setPriority(Thread.NORM_PRIORITY - 1);
  }

  Normalizer normalizer;
  String filestr;
  String outfilestr;
  File tempFile = null;

  public void run() {
    if (normalizer != null) {
      if (tempFile != null) {
        normalizer.normalizeFile(tempFile.getAbsolutePath(), outfilestr);
        tempFile.delete();
      } else {
        normalizer.normalizeFile(filestr, outfilestr);
      }
    }
  }
}

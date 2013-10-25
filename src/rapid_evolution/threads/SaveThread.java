package rapid_evolution.threads;

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
public class SaveThread extends Thread {
  public SaveThread() {}

  public static boolean savethreadon = false;

  public void run() {
    if (savethreadon) return;
    savethreadon = true;
    RapidEvolution.instance.SaveRoutine(false);
    savethreadon = false;
  }
}

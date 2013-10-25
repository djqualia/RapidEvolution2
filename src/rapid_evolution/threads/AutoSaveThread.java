package rapid_evolution.threads;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SyncUI;
import rapid_evolution.SongDB;

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

public class AutoSaveThread extends Thread {

    private static Logger log = Logger.getLogger(AutoSaveThread.class);

    public AutoSaveThread() {
    	setPriority(Thread.NORM_PRIORITY - 1);
    }
    
  public void run() {
    while (true) {
      try {
        if (RapidEvolution.instance.terminatesignal) return;
        Thread.sleep(1800000);
        if (RapidEvolution.instance.terminatesignal) return;
        if (RapidEvolution.instance.loaded && SongDB.instance.dirtybit && !OptionsUI.instance.disableautosave.isSelected()) {
          log.debug("run(): autosaving...");
          try {
            RapidEvolution.instance.SaveRoutine(true);
          } catch (Exception e) { log.error("run(): error", e); }
        }
      } catch (Exception e) { return; }
    }
  }
}

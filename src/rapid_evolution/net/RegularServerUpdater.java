package rapid_evolution.net;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongDB;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.ui.OptionsUI;

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

public class RegularServerUpdater extends Thread {
    
    private static Logger log = Logger.getLogger(RegularServerUpdater.class);
    
  public static int regularthreads = 0;
  private static int throttle_seconds = 1000;
  public boolean stopthread = false;
  public void run() {
    if (regularthreads > 0) return;
    regularthreads++;
    while ((!RapidEvolution.instance.terminatesignal) && !stopthread) {
      try {
      if (!MixshareClient.instance.isConnected()) { regularthreads--; return; }
      SongLinkedList iter = SongDB.instance.SongLL;
      while ((iter != null) && !RapidEvolution.instance.terminatesignal && MixshareClient.instance.isConnected()) {
        if (!iter.servercached) {
          try {
              if (MixshareClient.instance.servercommand.sendSong(iter)) {
                iter.servercached = true;
                if (log.isDebugEnabled()) log.debug("run(): sent song=" + iter);                
              } else log.debug("run(): send song failed: " + iter.uniquestringid);
              Thread.sleep(throttle_seconds);
          }
          catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }
        }
        iter = iter.next;
      }
      if (!OptionsUI.instance.donotsharemixouts.isSelected()) {
	      iter = SongDB.instance.SongLL;
	      while ((iter != null) && MixshareClient.instance.isConnected() && !RapidEvolution.instance.terminatesignal) {
	        for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
	          try {
	            if (!iter.mixout_servercache[i]) {
	                if (MixshareClient.instance.servercommand.sendMixout(iter, i)) {
	                  iter.mixout_servercache[i] = true;
	                  if (log.isDebugEnabled()) log.debug("run(): sent mixout, " + SongDB.instance.NewGetSongPtr(iter.uniquesongid) + " -> " + SongDB.instance.NewGetSongPtr(iter.mixout_songs[i]));
	                } else {
	                  iter.servercached = false;
	                  SongDB.instance.NewGetSongPtr(iter.mixout_songs[i]).servercached = false;
	                  log.debug("run(): send mixout failed: " + iter.uniquestringid + " -> " + iter.getMixoutSongname(i));
	                }
	                Thread.sleep(throttle_seconds);
	            }
	          }
	          catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }
	        }
	        iter = iter.next;
	      }
      }
      if (RapidEvolution.getMixshareClient().isSyncing()) {
          boolean removeoldstuff = true;
          iter = SongDB.instance.SongLL;
          while ((iter != null) && removeoldstuff) {
              if (!iter.servercached) removeoldstuff = false;
              for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
                  if (!iter.mixout_servercache[i]) removeoldstuff = false;
              }
              iter = iter.next;
          }
          if (removeoldstuff) {
            MixshareClient.instance.servercommand.RemoveOldData();
            log.debug("run(): successfully synced with server");
          }
      }
      Thread.sleep(1000 * 60); // run every minute
      } catch (Exception e) { log.error("run(): error", e); }
    }
  }
}

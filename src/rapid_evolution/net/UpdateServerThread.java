package rapid_evolution.net;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.ui.OptionsUI;
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

public class UpdateServerThread extends Thread {
    
    private static Logger log = Logger.getLogger(UpdateServerThread.class);
    
  static Semaphore UpdateServerSem = new Semaphore(1);
  public UpdateServerThread() { song = null; }
  public UpdateServerThread(SongLinkedList in_song) { song = in_song; }
  public UpdateServerThread(SongLinkedList in_song, int in_mindex) { song = in_song; mindex = in_mindex; }
  SongLinkedList song;
  int mindex = -1;
  public void run() {
    try {
    UpdateServerSem.acquire();
    if (MixshareClient.instance.isConnected()) {
      if (song == null) {
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          if (RapidEvolution.instance.terminatesignal || !MixshareClient.instance.isConnected()) { UpdateServerSem.release(); return; }
          if (!iter.servercached) {
            try {
                if (MixshareClient.instance.servercommand.sendSong(iter)) iter.servercached = true;
            }
            catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }
          }
          iter = iter.next;
        }
        if (!OptionsUI.instance.donotsharemixouts.isSelected()) {        
	        iter = SongDB.instance.SongLL;
	        while (iter != null) {
	          if (RapidEvolution.instance.terminatesignal || !MixshareClient.instance.isConnected()) { UpdateServerSem.release(); return; }
	          for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
	            try {
	              if (!iter.mixout_servercache[i]) {
	                  if (MixshareClient.instance.servercommand.sendMixout(iter, i)) iter.mixout_servercache[i] = true;
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
            if (removeoldstuff) MixshareClient.instance.servercommand.RemoveOldData();
        }
      } else {
        //update specific song
        SongLinkedList iter = song;
        if (mindex == -1) {
          if (!iter.servercached) {
            try {
              if (MixshareClient.instance.servercommand.sendSong(iter)) iter.servercached = true;
            }
            catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }
          }
        } else {
          for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
            if ((mindex == -1) || (mindex == i)) {
              try {
                if (!iter.mixout_servercache[i]) {
                  if (MixshareClient.instance.servercommand.sendMixout(iter, i)) iter.mixout_servercache[i] = true;;
                }
              }
             catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }}
           }
        }
      }
    }
    } catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }
    UpdateServerSem.release();
  }
}

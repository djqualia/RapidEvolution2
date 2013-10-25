package rapid_evolution.net;

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.OldSongValues;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongLinkedList;
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

public class UpdateServerSong extends Thread {
    
    private static Logger log = Logger.getLogger(UpdateServerSong.class);
    
  private static Vector queue = new Vector();
  public static class Job {
      public Job(SongLinkedList ineditsong, OldSongValues oldsongvalues) {
          editsong = ineditsong;
          this.oldsongvalues = oldsongvalues;
      }
      public SongLinkedList editsong;
      public OldSongValues oldsongvalues;
  }
  public UpdateServerSong() {      
  }
  public static void addToQueue(SongLinkedList ineditsong, OldSongValues oldvalues) {
      queue.add(new Job(ineditsong, oldvalues));
      if (threadcount == 0) {
          new UpdateServerSong().start();
      }
  }
  private static Semaphore runsem = new Semaphore(1);
  private static int threadcount = 0;
  public void run() {
      if (threadcount > 0) return;
      try {
          runsem.acquire();
          threadcount++;
          while (queue.size() > 0) {
              Job job = (Job)queue.get(0);
              if (MixshareClient.instance.servercommand != null) MixshareClient.instance.servercommand.UpdateEditSong(job.editsong, job.oldsongvalues);
              queue.remove(0);
          }
      } catch (Exception e) {
          log.error("run(): error", e);
      }
      threadcount--;
      runsem.release();
  }
}

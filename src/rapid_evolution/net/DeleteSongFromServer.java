package rapid_evolution.net;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.ui.SuggestedMixesUI;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import java.util.Vector;

import org.apache.log4j.Logger;

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

public class DeleteSongFromServer extends Thread {
    
    private static Logger log = Logger.getLogger(DeleteSongFromServer.class);
    
  private static Vector queue = new Vector();
  public static class Job {
      public Job(SongLinkedList ineditsong) {
          editsong = ineditsong;
      }
      public SongLinkedList editsong;
  }
  public DeleteSongFromServer() {      
  }
  public static void addToQueue(SongLinkedList ineditsong) {
      queue.add(new Job(ineditsong));
      if (threadcount == 0) {
          new DeleteSongFromServer().start();
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
              if (MixshareClient.instance.servercommand != null) MixshareClient.instance.servercommand.removeSong(job.editsong); 
              queue.remove(0);
          }
      } catch (Exception e) {
          log.error("run(): error", e);
      }
      threadcount--;
      runsem.release();
  }
}

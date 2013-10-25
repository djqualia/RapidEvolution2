package rapid_evolution.net;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.SuggestedMixesUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.net.DeleteSongFromServer.Job;

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


public class GetNewServerMixesThread extends Thread {

    private static Logger log = Logger.getLogger(GetNewServerMixesThread.class);

    private static Vector queue = new Vector();
    public static class Job {
        public Job() {
        }
    }
    public GetNewServerMixesThread() {      
    }
    public static void addToQueue() {
        if (!MixshareClient.instance.isConnected()) return;
        queue.add(new Job());
        if (threadcount == 0) {
            new GetNewServerMixesThread().start();
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
                RapidEvolutionUI.instance.lastqueryresults = (MixshareClient.instance.servercommand2 == null) ? new Vector() : MixshareClient.instance.servercommand2.getMixouts(RapidEvolutionUI.instance.currentsong);
                SuggestedMixesUI.instance.DisplaySuggestedMixouts(RapidEvolutionUI.instance.lastqueryresults);                
                queue.remove(0);
                while (queue.size() > 1) queue.remove(0);
            }
        } catch (Exception e) {
            log.error("run(): error", e);
        }
        threadcount--;
        runsem.release();
    }
  }

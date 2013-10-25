package rapid_evolution.audio;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.audio.detect.AllDetectTask;
import com.mixshare.rapid_evolution.thread.Task;
import com.mixshare.rapid_evolution.thread.TaskRunner;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class DetectAll extends Thread {
    
    private static Logger log = Logger.getLogger(DetectAll.class);
    
  public DetectAll(SongLinkedList[] inputsongs) {
	  songs = inputsongs;
	  setPriority(Thread.NORM_PRIORITY - 1);
  }
  
  SongLinkedList[] songs;  
  private static Semaphore thread_sem = new Semaphore(1);
  boolean stopflag = false;
  public void cancel() {
      stopflag = true;
  }
  public void run() {
      try {
          thread_sem.acquire();
	    int n = IOptionPane.showConfirmDialog(SkinManager.instance
                    .getFrame("main_frame"), SkinManager.instance
                    .getDialogMessageText("detect_all"), SkinManager.instance
                    .getDialogMessageTitle("detect_all"),
                    IOptionPane.YES_NO_CANCEL_OPTION);
            boolean overwrite = false;
            if ((n == -1) || (n == 2)) {
                thread_sem.release();
                return;
            }
            if (n == 0)
                overwrite = true;

            long startTime = System.currentTimeMillis();            
            Task task = new AllDetectTask(songs, overwrite);
            TaskRunner.executeTask(task);              
            long endTime = System.currentTimeMillis();
            long timeInSeconds = (endTime - startTime) / 1000;
            log.debug("run(): total time for batch detection ~= " + timeInSeconds);
            
            
      } catch (Exception e) {
          log.error("run(): error", e);      }
      RapidEvolutionUI.instance.detectallbatchprogress_ui.Hide();
      thread_sem.release();
  }
}

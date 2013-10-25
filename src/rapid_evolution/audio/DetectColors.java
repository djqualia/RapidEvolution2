package rapid_evolution.audio;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.audio.detect.BeatIntensityDetectionTask;
import com.mixshare.rapid_evolution.thread.Task;
import com.mixshare.rapid_evolution.thread.TaskRunner;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class DetectColors extends Thread {
    
    private static Logger log = Logger.getLogger(DetectColors.class);
    
    private static Semaphore thread_sem = new Semaphore(1);
    
    private boolean beatintensity_only = false;
  public DetectColors(SongLinkedList[] inputsongs) {
	  songs = inputsongs;
	  setPriority(Thread.NORM_PRIORITY - 1);
  }
  
  public DetectColors(SongLinkedList[] inputsongs, boolean beatintensity_only) {
      songs = inputsongs;
      this.beatintensity_only = beatintensity_only;
  }

  SongLinkedList[] songs;
  public void run() {
        try {
            int n = IOptionPane
                    .showConfirmDialog(
                            SkinManager.instance.getFrame("main_frame"),
                            SkinManager.instance
                                    .getDialogMessageText(beatintensity_only ? "detect_beat_intensity"
                                            : "detect_colors"),
                            SkinManager.instance
                                    .getDialogMessageTitle(beatintensity_only ? "detect_beat_intensity"
                                            : "detect_colors"),
                            IOptionPane.YES_NO_CANCEL_OPTION);
            boolean overwrite = false;
            if ((n == -1) || (n == 2)) {
                thread_sem.release();
                return;
            }
            if (n == 0)
                overwrite = true;

            long startTime = System.currentTimeMillis();            
            Task task = new BeatIntensityDetectionTask(songs, overwrite);
            TaskRunner.executeTask(task);              
            long endTime = System.currentTimeMillis();
            long timeInSeconds = (endTime - startTime) / 1000;
            log.debug("run(): total time for batch detection ~= " + timeInSeconds);
            
        } catch (Exception e) {
            log.error("run(): error", e);
        }
        thread_sem.release();
    }
      
  
  private boolean stop = false;
  public void cancel() {
      stop = true;
  }
  
  public boolean isCancelled() {
      return stop;
  }
  
}

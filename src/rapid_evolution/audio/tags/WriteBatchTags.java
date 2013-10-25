package rapid_evolution.audio.tags;

import rapid_evolution.SongLinkedList;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.audio.tags.TagManager;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.*;

public class WriteBatchTags extends Thread {

    private static Logger log = Logger.getLogger(WriteBatchTags.class);

  public WriteBatchTags(SongLinkedList[] songvector) { songs = songvector; }
  SongLinkedList[] songs;
  public static boolean stop;
  public void run() {
    int n = IOptionPane.showConfirmDialog(
       SkinManager.instance.getFrame("main_frame"),
       SkinManager.instance.getDialogMessageText("write_song_tags_prompt"),
       SkinManager.instance.getDialogMessageTitle("write_song_tags_prompt"),
       IOptionPane.YES_NO_OPTION);
   if (n != 0) return;
   int successes = 0;
   RapidEvolutionUI.instance.writetagsprogress_ui.progressbar.setValue(0); // 0 to 100
   RapidEvolutionUI.instance.writetagsprogress_ui.Display();
   try {       
       
       WorkerThread worker = new WorkerThread(songs, 0);
       
       worker.start();
       
       int last_index = 0;
       int last_same_count = 0;
       int wait_ms = 500;
       int max_ms = 1000 * 120; // 2 minutes
       while (!worker.isDone()) {
           Thread.sleep(wait_ms);
           if (worker.getCurrentIndex() == last_index) {
               ++last_same_count;
           } else {
               last_index = worker.getCurrentIndex();
               last_same_count = 0;
           }
           if (last_same_count * wait_ms >= max_ms) {
               // write thread is stuck
               log.error("run(): the write tag worker thread is stuck on song=" + songs[worker.getCurrentIndex()] + ", filename=" + songs[worker.getCurrentIndex()].getFileName());
               int new_index = worker.getCurrentIndex() + 1;
               log.debug("run(): advancing to the next song, index=" + new_index);
               successes += worker.getSuccesses();
               worker.stop();
               //TagManager.releaseWritingSemaphore();
               worker = new WorkerThread(songs, new_index);
               worker.start();
           }
       }
       
       successes += worker.getSuccesses();
       
   } catch (Exception e) { log.error("run(): error", e); }   
   RapidEvolutionUI.instance.writetagsprogress_ui.Hide();   
   String dialog_text = SkinManager.instance.getDialogMessageText("write_batch_tags");
   dialog_text = dialog_text.replaceAll("%num_successful%", String.valueOf(successes));
   dialog_text = dialog_text.replaceAll("%total%", String.valueOf(songs.length));
   String dialog_title = SkinManager.instance.getDialogMessageTitle("write_batch_tags");
   IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"), dialog_text, dialog_title, IOptionPane.PLAIN_MESSAGE);   
  }
  
  private class WorkerThread extends Thread {
      
      SongLinkedList[] songs;
      int index;
      int successes = 0;
      
      public WorkerThread(SongLinkedList[] songs, int start_index) {
          this.songs = songs;
          index = start_index;
      }
      
      public int getCurrentIndex() { 
          return index;
      }
      
      public int getSuccesses() {
          return successes;
      }
      
      public boolean isDone() {
          return ((index >= songs.length) || stop);
      }
      
      public void run() {
          stop = false;
          while ((index < songs.length) && !stop) {
              log.debug("run(): writing tag for song=" + songs[index] + ", filename=" + songs[index].getFileName());
              if (TagManager.writeTags(songs[index])) {
                successes++;
                log.debug("run(): tag successfully written");
            } else {
                log.debug("run(): tag write failed");
            }
            int progress = (int)(((float)index + 1) / songs.length * 100.0f);
            RapidEvolutionUI.instance.writetagsprogress_ui.progressbar.setValue(progress);
            ++index;
        }
          
      }
  }
  
}

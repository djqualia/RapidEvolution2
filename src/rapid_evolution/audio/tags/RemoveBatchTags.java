package rapid_evolution.audio.tags;

import rapid_evolution.SongLinkedList;
import com.mixshare.rapid_evolution.audio.tags.TagManager;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.*;

public class RemoveBatchTags extends Thread {
  public RemoveBatchTags(SongLinkedList[] songvector) { songs = songvector; }
  SongLinkedList[] songs;
  public void run() {
    int n = IOptionPane.showConfirmDialog(
       SkinManager.instance.getFrame("main_frame"),
       SkinManager.instance.getDialogMessageText("remove_song_tags_prompt"),
       SkinManager.instance.getDialogMessageTitle("remove_song_tags_prompt"),
       IOptionPane.YES_NO_OPTION);
   if (n != 0) return;
   int successes = 0;
   try {       
     for (int i = 0; i < songs.length; ++i) {
         if (TagManager.removeTags(songs[i])) {
             successes++;
         }
     }
   } catch (Exception e) { }   
   
   String dialog_text = SkinManager.instance.getDialogMessageText("remove_batch_tags");
   dialog_text = dialog_text.replaceAll("%num_successful%", String.valueOf(successes));
   dialog_text = dialog_text.replaceAll("%total%", String.valueOf(songs.length));
   String dialog_title = SkinManager.instance.getDialogMessageTitle("remove_batch_tags");
   IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"), dialog_text, dialog_title, IOptionPane.PLAIN_MESSAGE);   

  }
}

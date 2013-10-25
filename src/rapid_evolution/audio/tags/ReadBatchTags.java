package rapid_evolution.audio.tags;

import rapid_evolution.SongLinkedList;

import rapid_evolution.OldSongValues;
import rapid_evolution.SongDB;
import com.mixshare.rapid_evolution.audio.tags.TagManager;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.*;

public class ReadBatchTags extends Thread {
    public static boolean stop;
  public ReadBatchTags(SongLinkedList[] songs) { this.songs = songs; }
  SongLinkedList[] songs;

  
  public void run() {
    int n = IOptionPane.showConfirmDialog(
        SkinManager.instance.getFrame("main_frame"),
        SkinManager.instance.getDialogMessageText("read_song_tags_prompt"),
        SkinManager.instance.getDialogMessageTitle("read_song_tags_prompt"),
        IOptionPane.YES_NO_CANCEL_OPTION);
    if ((n == -1) || (n == 2)) return;
    RapidEvolutionUI.instance.readtagsprogress_ui.progressbar.setValue(0); // 0 to 100
    RapidEvolutionUI.instance.readtagsprogress_ui.Display();
    boolean overwrite = false;
    if (n == 0) overwrite = true;
    stop = false;
    int i = 0;
    while (!stop && (i < songs.length)) {
      SongLinkedList editedsong = songs[i];
      OldSongValues old_values = new OldSongValues(editedsong);
      String filename = editedsong.getRealFileName();
      if ((filename != null) && !filename.equals("")) {
        SongLinkedList datasong = TagManager.readTags(filename);
        editedsong.copyValuesFrom(datasong, overwrite);
        SongDB.instance.UpdateSong(editedsong,old_values);
      }
      ++i;
      int progress = (int)(((float)i + 1) / songs.length * 100.0f);
      RapidEvolutionUI.instance.readtagsprogress_ui.progressbar.setValue(progress);      
    }
    RapidEvolutionUI.instance.readtagsprogress_ui.Hide();       
  }
}

package rapid_evolution.threads;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.OldSongValues;

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
public class UpdateSongThread extends Thread {
  SongLinkedList song;
  OldSongValues oldValues;
  public UpdateSongThread(SongLinkedList inputsong, OldSongValues old_values)  { song = inputsong; oldValues = old_values; }
  public void run()  {
    RapidEvolutionUI.instance.UpdateSongRoutine(song, oldValues);
  }
}

package rapid_evolution;

import rapid_evolution.RapidEvolution;
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

public class UpdateStyles extends Thread {
  public UpdateStyles() { song = null; }
  public UpdateStyles(SongLinkedList in) { song = in; }
  SongLinkedList song;
  public void run() {
    SongDB.instance.UpdateStylesRoutine(song);
    SongDB.instance.stylesdirtybit = 0;
  }
}

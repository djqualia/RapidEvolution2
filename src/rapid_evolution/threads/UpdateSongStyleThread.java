package rapid_evolution.threads;

import rapid_evolution.SongLinkedList;
import rapid_evolution.SongDB;
import rapid_evolution.Artist;

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
public class UpdateSongStyleThread extends Thread {
  public UpdateSongStyleThread(SongLinkedList inputsong) { addedsong = inputsong; }
  SongLinkedList addedsong;
  public void run() {
    SongDB.instance.UpdateSongStyleRoutine(addedsong);
    Artist.InsertToArtistList(addedsong);
  }
}

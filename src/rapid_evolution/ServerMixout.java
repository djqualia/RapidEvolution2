package rapid_evolution;

import java.util.Vector;

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

public class ServerMixout {
  public ServerMixout(SongLinkedList toSong, float in_bpmdiff, String in_comments, int in_rank, boolean in_addon) {
    this.toSong = toSong;
    bpmdiff = in_bpmdiff;
    comments = in_comments;
    rank = in_rank;
    addon = in_addon;
  }
  public SongLinkedList toSong;
  public float bpmdiff = 0.0f;
  public String comments = null;
  public int rank = 0;
  public boolean addon = false;
  public Vector mixoutdata = new Vector();

}

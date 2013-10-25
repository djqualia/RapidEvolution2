package rapid_evolution;

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

public class SongStack {
  public SongStack(long song, SongStack nextptr) {
    songid = song;
    next = nextptr;
  }
  public SongStack(long song, float bpmdiff, SongStack nextptr) {
    songid = song;
    diff = bpmdiff;
    next = nextptr;
  }
  public SongStack(long song, float bpmdiff, float keyclose, SongStack nextptr) {
    songid = song;
    diff = bpmdiff;
    keycloseness = keyclose;
    next = nextptr;
  }
  public int size() { return internalsize(0); }
  private int internalsize(int val) {
    if (next != null) return next.internalsize(val + 1);
    else return val + 1;
  }
  public SongStack getIndex(int index) {
    if (index == 0) return this;
    else return next.getIndex(index - 1);
  }
 public long songid;
 public float diff;
 public float keycloseness;
 public SongStack next;
}

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

public class SongList {
  public SongList(SongLinkedList addsong, SongList nextptr) { song = addsong; next = nextptr; }
  public SongList() { song = null; next = null; }
  public SongList insert(SongLinkedList insertsong) {
      return insert(insertsong, null);
  }
  public SongList insert(SongLinkedList insertsong, SongList end) {
      if (song == null) {
          song = insertsong;
          return this;
      } else {          
        SongList iter = null;
        if (end != null) {
            iter = end;
        } else {
            iter = this;
            while (iter.next != null) iter = iter.next;
        }
        iter.next = new SongList(insertsong, null);
        return iter.next;
      }
    }  
  public void sortedinsert(SongLinkedList insertsong) {
    //String cmpstring = insertsong.getSongIdShort();
    SongList iter = this;
    while (iter != null) {
      if (iter.song == null) {
        iter.song = insertsong;
        return;
      }
      else if (insertsong.compareTo(iter.song) < 0) {
        iter.next = new SongList(iter.song, iter.next);
        iter.song = insertsong;
        return;
      } else if (iter.next == null) {
        iter.next = new SongList(insertsong, null);
        return;
      }
      iter = iter.next;
    }
  }
  public SongLinkedList song;
  public SongList next;
}

package rapid_evolution;

import org.apache.log4j.Logger;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class SongQueue {
    
    private static Logger log = Logger.getLogger(SongQueue.class);
    
  public SongQueue(SongLinkedList sptr) { songptr = sptr; next = null; }
  Semaphore insertsongSem = new Semaphore(1);
  public void insertsong(SongLinkedList sptr) {
    try {
      insertsongSem.acquire();
    if (next != null) next.insertsong(sptr);
    else next = new SongQueue(sptr);
    } catch (Exception e) { log.error("insertsong(): error", e); }
    insertsongSem.release();
  }
  public void deletesong(int x) {
    try {
    insertsongSem.acquire();
    if (x == 1) {
      SongQueue tmp = next;
      next = next.next;
      tmp.next = null;
    } else next.deletesong(x - 1);
    } catch (Exception e) { }
    insertsongSem.release();
  }

  SongLinkedList getIndex(int x) {
      SongQueue titer = this;
      while (x != 0) {
        if ((titer != null) && (titer.next != null)) {
          titer = titer.next;
          --x;
        } else return null;
      }
      if (titer != null) return titer.songptr;
      else return null;
    }

    SongLinkedList songptr;
    SongQueue next;
}

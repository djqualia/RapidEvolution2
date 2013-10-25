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

public class Mixout {  
  public Mixout(String uniqueid1, String uniqueid2) {
    songuniqueid1 = uniqueid1.toLowerCase();
    songuniqueid2 = uniqueid2.toLowerCase();
  }
  public String songuniqueid1;
  public String songuniqueid2;
  public SongLinkedList tosong = null;
  public SongLinkedList fromsong = null;

}

package rapid_evolution.comparables;

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
public class myLength implements Comparable {
    public static myLength default_value = new myLength("");
  public myLength(String val) {
    data = val;
    if ((val == null) ||val.equals("")) seconds = 0;
    else {
      seconds = 0;
      int index1 = 0;
      while ((index1 < data.length() && Character.isDigit(data.charAt(index1)))) index1++;
      int minutes1 = 0;
      try { minutes1 = Integer.parseInt(data.substring(0, index1)); }
      catch (Exception e) { }
      int index2 = index1 + 1;
      try {
        while ( (index2 < data.length()) &&
               Character.isDigit(data.charAt(index2)))
          index2++;
        seconds = Integer.parseInt(data.substring(index1 + 1, index2)) +
            minutes1 * 60;
      } catch (Exception e) { }
    }
  }
  public int compareTo(myLength b) {
    //if ((b.seconds == 0) && (seconds == 0)) return 0;
    //if (b.seconds == 0) return -1;
    //if (seconds == 0) return 1;
    if (seconds < b.seconds) return -1;
    if (seconds > b.seconds) return 1;
    return 0;
  }
  public int compareTo(Object b1) {
    myLength b = (myLength)b1;
    return compareTo(b);
  }
  public int hashCode() { return data.hashCode(); }
  public boolean equals(Object b) {
      if (b instanceof myLength) {
          myLength mb = (myLength)b;
          return data.equals(mb.data);
      }      
      return false;
  }
  public String toString() { return data; }
  public int seconds;
  public String data;
};

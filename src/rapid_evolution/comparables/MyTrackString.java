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
public class MyTrackString implements Comparable {
    public static MyTrackString default_value = new MyTrackString("");
  public MyTrackString(String val) {
    if (val == null) data = new String(""); else data = val;
    String numstr = new String("");
    int index = 0;
    boolean shift = false;
    if ((data.length() > 0)  && Character.isDigit(data.charAt(0))) index = 0;
    else if ((data.length() > 1)  && Character.isDigit(data.charAt(1))) { index = 1; shift = true; }
    while ((index < data.length()) && Character.isDigit(data.charAt(index))) numstr += data.charAt(index++);
    try {
      codeval = Integer.parseInt(numstr);
      if (shift) codeval += Integer.MAX_VALUE / 2;
    } catch (Exception e) { codeval = Integer.MAX_VALUE; }
  }
  public int compareTo(MyTrackString b) {
    if (codeval < b.codeval) return -1;
    if (codeval > b.codeval) return 1;
    return (data.compareTo(b.data));
  }
  public int compareTo(Object b1) {
    MyTrackString b = (MyTrackString)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof MyTrackString) {
          MyTrackString mb = (MyTrackString)b;      
          if (data.equals(mb.data)) return true;
      }
    return false;
  }
  public int hashCode() {
      return data.hashCode();
  }
  public String toString() {
    return data;
  }
  public String data;
  int codeval;
};

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
public class myColorFloat implements Comparable {
    public static myColorFloat default_value = new myColorFloat(-1.0f);
  public myColorFloat(float val) { data = val; }
  public int compareTo(myColorFloat b) {
    if (data < b.data) return -1;
    if (data > b.data) return 1;
    return 0;
  }
  public int compareTo(Object b1) {
    myColorFloat b = (myColorFloat)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof myColorFloat) {
          myColorFloat mb = (myColorFloat)b;
          if (data == mb.data) return true;
      }    
    return false;
  }
  public String toString() { if (data == -1.0f) return new String("");
    return new String(String.valueOf(data * 100.0f) + "%");
  }
  public int hashCode() { return toString().hashCode(); }
  float data;
  }

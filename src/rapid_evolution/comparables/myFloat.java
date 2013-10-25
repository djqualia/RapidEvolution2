package rapid_evolution.comparables;

import rapid_evolution.StringUtil;

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
public class myFloat implements Comparable {
  public myFloat(float val) { data = val; }
  public int compareTo(myFloat b) {
    if (data < b.data) return -1;
    if (data > b.data) return 1;
    return 0;
  }
  public float getFloatValue() { return data; }
  public int compareTo(Object b1) {
    myFloat b = (myFloat)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof myFloat) {
          myFloat mb = (myFloat)b;
          if (data == mb.data) return true;
      }
    return false;
  }
  public int hashCode() { return toString().hashCode(); }
  public String toString() { if (data == 0.0) return new String(""); return StringUtil.getDecimalString(data); }
  public float data;
};

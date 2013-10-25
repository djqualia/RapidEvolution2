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
public class myPercentageInteger implements Comparable {
    
    public static myPercentageInteger default_value = new myPercentageInteger(0);
    
  public myPercentageInteger(int val) { data = val; }
  public int compareTo(myPercentageInteger b) {
    if (data < b.data) return -1;
    if (data > b.data) return 1;
    return 0;
  }
  public int compareTo(Object b1) {
      if (b1 instanceof myPercentageInteger) {
          myPercentageInteger b = (myPercentageInteger)b1;
          return compareTo(b);
      }
      return -1;
  }
  public boolean equals(Object b) {
      if (b instanceof myPercentageInteger) {
          myPercentageInteger mb = (myPercentageInteger)b;
          if (data == mb.data) return true;
      }    
    return false;
  }
  public String toString() {
      if (data == -1) return new String("");
  	return String.valueOf(data) + "%";
  }
  public int hashCode() {
      return toString().hashCode();
  }
  public int data;
}

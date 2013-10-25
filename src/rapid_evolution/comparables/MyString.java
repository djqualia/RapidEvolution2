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
public class MyString implements Comparable {
    
  public MyString(String val) {
    if (val == null) {
      data = "";
    }
    else {
      data = val;
    }
  }
  public int compareTo(MyString b) {
    if ((data.equals("")) && (b.data.equals(""))) return 0;
    if (b.data.equals("")) return -1;
    if (data.equals("")) return 1;
    return (data.compareToIgnoreCase(b.data));
  }
  public int compareTo(Object b1) {
      if (b1 instanceof MyString) {
          MyString b = (MyString)b1;
          return compareTo(b);
      }
      return data.compareToIgnoreCase(b1.toString());      
  }
  public int hashCode() {
      return data.toLowerCase().hashCode();
  }
  public boolean equals(Object b) {
      if (b instanceof MyString) {
          MyString mb = (MyString)b;
          if (data.equalsIgnoreCase(mb.data)) return true;
      }    
    return false;
  }
  public String toString() { return data; }
  public void setData(String data) {
      this.data = data;
  }
  String data;
};

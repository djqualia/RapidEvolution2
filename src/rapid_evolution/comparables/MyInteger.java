package rapid_evolution.comparables;

public class MyInteger implements Comparable {
    public static MyInteger default_value = new MyInteger(0);
  public MyInteger(int val) { data = val; }
  public int compareTo(MyInteger b) {
    if (data < b.data) return -1;
    if (data > b.data) return 1;
    return 0;
  }
  public int getValue() { return data; }
  public int compareTo(Object b1) {
    MyInteger b = (MyInteger)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof MyInteger) {
          MyInteger mb = (MyInteger)b;
          if (data == mb.data) return true;
      }
    return false;
  }
  public int hashCode() { return data; }
  public String toString() { if (data == 0) return new String(""); return String.valueOf(data); }
  public int data;
};

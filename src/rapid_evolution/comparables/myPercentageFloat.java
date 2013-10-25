package rapid_evolution.comparables;

public class myPercentageFloat implements Comparable {
    
    public static myPercentageFloat default_value = new myPercentageFloat(-1.0f);
    
  public myPercentageFloat(float val) { data = val; }
  public int compareTo(myPercentageFloat b) {
    if (data < b.data) return -1;
    if (data > b.data) return 1;
    return 0;
  }
  public float getFloatValue() { return data; }
  public int compareTo(Object b1) {
    myPercentageFloat b = (myPercentageFloat)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof myPercentageFloat) {
          myPercentageFloat mb = (myPercentageFloat)b;
          if (data == mb.data) return true;
      }    
      return false;
  }
  public String toString() { if (data == -1.0f) return new String("");
    if (data == 1.0f) return new String("100%");
    String str = String.valueOf(data * 100.0);
    int length = str.length();
    int max = 5;
    if (data < 0.1) max = 4;
    if (length > max) length = max;
    boolean perform = false;
    for (int i = 0; i < length; ++i) {
      if (str.charAt(i) == '.') perform = true;
    }
    if (perform) {
      while (str.charAt(length - 1) == '0') length--;
      if (str.charAt(length - 1) == '.') length--;
    }
    return new String(str.substring(0,length) + "%");
  }
  public int hashCode() {
      return toString().hashCode();
  }
  float data;
}

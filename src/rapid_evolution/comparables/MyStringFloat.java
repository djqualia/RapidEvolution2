package rapid_evolution.comparables;

public class MyStringFloat implements Comparable {
    
    public static MyStringFloat default_value = new MyStringFloat("");
    
  public MyStringFloat(String val) {
      if (val == null) {
          data = new String("");
          nan = true;
      }
      else {
          data = val;
          try {
              float_val = Float.parseFloat(val);
          } catch (Exception e) { nan = true; }          
      }
      cached_tostring = calculateToString();
  }
  public int compareTo(MyStringFloat b) {
    if ((data.equals("")) && (b.data.equals(""))) return 0;
    if (b.data.equals("")) return -1;
    if (data.equals("")) return 1;
    float floatdata1 = float_val;
    float floatdata2 = b.float_val;
    if (floatdata1 < floatdata2) return -1;
    if (floatdata1 > floatdata2) return 1;
    return 0;
  }
  public int compareTo(Object b1) {
    MyStringFloat b = (MyStringFloat)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof MyStringFloat) {
          MyStringFloat mb = (MyStringFloat)b;
          if (data.equals(mb.data)) return true;
      }    
    return false;
  }
  public int hashCode() {
      return toString().hashCode();
  }
  public String toString() { return cached_tostring; }
  
  public String calculateToString() {
      if (data.equals("")) return data;
      int length = data.length();
      boolean positive = false;
      if (float_val > 0) positive = true;
      int max = 5;
      if (positive) max = 4;
      if (length > max) length = max;
      if (float_val > 0) return new String("+" + data.substring(0, length) + "%");
      return data.substring(0,length) + "%";      
  }
  
  public float getFloatValue() { return float_val; }
  public boolean isNaN() { return nan; }
  
  private String cached_tostring;
  private String data;
  private float float_val = 0.0f;
  private boolean nan = false;
};

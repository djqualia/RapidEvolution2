package rapid_evolution.comparables;

import rapid_evolution.ui.OptionsUI;


public class MyUserObject implements Comparable {
    
    public static MyUserObject default1_value = new MyUserObject(1, "");
    public static MyUserObject default2_value = new MyUserObject(2, "");
    public static MyUserObject default3_value = new MyUserObject(3, "");
    public static MyUserObject default4_value = new MyUserObject(4, "");

    private int this_type;
  public MyUserObject(int this_type, String val) {
      if (val == null) {
          data = new String("");
      }else {
          data = val; 
      }
      this.this_type = this_type;
  }
  public int getSortType() {
      if (this_type == 1) return OptionsUI.instance.custom1sortas.getSelectedIndex();
      if (this_type == 2) return OptionsUI.instance.custom2sortas.getSelectedIndex();
      if (this_type == 3) return OptionsUI.instance.custom3sortas.getSelectedIndex();
      if (this_type == 4) return OptionsUI.instance.custom4sortas.getSelectedIndex();
      return -1;
  }
  public int compareTo(MyUserObject b) {
    if ((data.equals("")) && (b.data.equals(""))) return 0;
    if (b.data.equals("")) return -1;
    if (data.equals("")) return 1;
    if ((getSortType() == 0) || (getSortType() == 1)) {
        try {
            float floatdata1 = Float.parseFloat(data);
            float floatdata2 = Float.parseFloat(b.data);
            if (floatdata1 < floatdata2) return -1;
            if (floatdata1 > floatdata2) return 1;
            return 0;
          } catch (Exception e) { }        
    }
    return data.compareToIgnoreCase(b.data);
  }
  public int compareTo(Object b1) {
    MyUserObject b = (MyUserObject)b1;
    return compareTo(b);
  }
  public boolean equals(Object b) {
      if (b instanceof MyUserObject) {
          MyUserObject mb = (MyUserObject)b;
          if (data.equalsIgnoreCase(mb.data)) return true;
      }    
    return false;
  }
  public int hashCode() {
      return data.toLowerCase().hashCode();
  }
  public String toString() {
    return data;
  }
  protected String data;
};

package rapid_evolution.comparables;

public class MyCents implements Comparable {

    static public MyCents NO_CENTS = new MyCents(Float.NaN);
    
    private String data;
    private int val;
    
    public MyCents(float fval) {
        if (Float.isNaN(fval)) {
            data = "";
            val = Integer.MAX_VALUE;
        } else {
            val = (int) (fval * 100.0f);
            if (val > 0)
                data = "+" + val + " cents";
            else
                data = String.valueOf(val) + " cents";
        }
    }

    public int compareTo(MyCents b) {
        int data1 = val;
        int data2 = b.val;
        if (data1 < data2)
            return -1;
        if (data1 > data2)
            return 1;
        return 0;
    }

    public int compareTo(Object b1) {
        MyCents b = (MyCents) b1;
        return compareTo(b);
    }

    public boolean equals(Object b) {
        if (b instanceof MyCents) {
            MyCents mb = (MyCents) b;
            if (val == mb.val)
                return true;
        }
        return false;
    }

    public int hashCode() {
        return val;
    }

    public String toString() {
        return data;
    }

};

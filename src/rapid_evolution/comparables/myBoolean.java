package rapid_evolution.comparables;

import rapid_evolution.ui.SkinManager;

public class myBoolean implements Comparable {

    public static myBoolean default_value = new myBoolean(false);

    public boolean data;
    
    public myBoolean(boolean val) {
        data = val;
    }

    public int compareTo(myBoolean b) {
        if ((data == false) && (b.data == true))
            return -1;
        if ((data == true) && (b.data == false))
            return 1;
        return 0;
    }

    public int compareTo(Object b1) {
        if (b1 instanceof myBoolean) {
            myBoolean b = (myBoolean) b1;
            return compareTo(b);
        }
        return -1;
    }

    public boolean equals(Object b) {
        if (b instanceof myBoolean) {
            myBoolean mb = (myBoolean) b;
            if (data == mb.data)
                return true;
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        if (data == true)
            return SkinManager.instance.getMessageText("default_true_text");
        return SkinManager.instance.getMessageText("default_false_text");
    }

};

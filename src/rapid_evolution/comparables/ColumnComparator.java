package rapid_evolution.comparables;

import java.util.Comparator;
import java.util.Vector;

public class ColumnComparator implements Comparator {
    
    protected int index;
    protected boolean ascending;

    public ColumnComparator(int index, boolean ascending) {
        this.index = index;
        this.ascending = ascending;
    }

    public int compare(Object one, Object two) {
        if (one instanceof Vector && two instanceof Vector) {
            Vector vOne = (Vector) one;
            Vector vTwo = (Vector) two;
            Object oOne = vOne.elementAt(index);
            Object oTwo = vTwo.elementAt(index);
            if (oOne instanceof Comparable && oTwo instanceof Comparable) {
                Comparable cOne = (Comparable) oOne;
                Comparable cTwo = (Comparable) oTwo;
                if (ascending) {
                    try {
                        return cOne.compareTo(cTwo);
                    } catch (Exception e) {
                        try {
                            float f1 = Float.parseFloat(cOne.toString());
                            float f2 = Float.parseFloat(cTwo.toString());
                            if (f1 < f2)
                                return -1;
                            else if (f2 < f1)
                                return 1;
                            else
                                return 0;
                        } catch (Exception e2) {
                            return cOne.toString().compareTo(cTwo.toString());
                        }
                    }
                } else {
                    try {
                        return cTwo.compareTo(cOne);
                    } catch (Exception e) {
                        try {
                            float f1 = Float.parseFloat(cTwo.toString());
                            float f2 = Float.parseFloat(cOne.toString());
                            if (f1 < f2)
                                return -1;
                            else if (f2 < f1)
                                return 1;
                            else
                                return 0;
                        } catch (Exception e2) {
                            return cTwo.toString().compareTo(cOne.toString());
                        }
                    }
                }
            }
        }
        return 1;
    }
    
}
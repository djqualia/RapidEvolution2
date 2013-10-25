package rapid_evolution.util;

import javax.swing.DefaultListModel;

public class ListUtil {

    public static int sortedInsert(DefaultListModel dlm, Comparable value) {
        
        if (dlm.getSize() == 0) {
            dlm.insertElementAt(value, 0);
            return 0;
        }
        
        int start = 0;
        int end = dlm.getSize() - 1;
        int half_way = dlm.getSize() / 2;
        
        boolean done = false;
        while (!done) {
            if (end - start < 2) {
                for (int i = start; i <= end; ++i) {
                    Object compare = dlm.getElementAt(i);
                    if (value.compareTo(compare) < 0) {
                        dlm.insertElementAt(value, i);
                        return i;
                    } else if (value.compareTo(compare) == 0) {
                        return i;
                    }
                }
                dlm.insertElementAt(value, end + 1);
                return end + 1;                
            } else {
                Object compare = dlm.getElementAt(half_way);
                int cmp = value.compareTo(compare);
                if (cmp < 0) {
                    end = half_way - 1;
                } else if (cmp == 0) {
                    return half_way;
                } else {
                    start = half_way + 1;
                }
                half_way = (start + end) / 2;                
            }
        }        
        return -1;
        
    }
    
}

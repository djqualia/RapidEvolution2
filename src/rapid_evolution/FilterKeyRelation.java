package rapid_evolution;

import com.mixshare.rapid_evolution.music.*;

public class FilterKeyRelation extends KeyRelation implements Comparable {

    public FilterKeyRelation(KeyRelation keyRelation) {
        super(keyRelation.getDifference(), keyRelation.getRelationship());
    }
    
    public String toString() {
        String result = super.toString();
        if ((result == null) || result.equals(""))
            return Filter.noValueString;
        return result;
    }
    
    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }
    
    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());
    }
    
    public int hashCode() {
        return toString().toLowerCase().hashCode();
    }
}

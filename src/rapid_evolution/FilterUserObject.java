package rapid_evolution;

import rapid_evolution.comparables.*;

public class FilterUserObject extends MyUserObject {

    public FilterUserObject(MyUserObject input) {
        super(input.getSortType(), input.toString());        
    }
    
    public String toString() {
        String result = super.toString();
        if ((result == null) || result.equals(""))
            return Filter.noValueString;
        return result;
    }
    
}

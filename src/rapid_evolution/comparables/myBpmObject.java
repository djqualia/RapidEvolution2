package rapid_evolution.comparables;

import rapid_evolution.StringUtil;

public class myBpmObject implements Comparable {

    public float startbpm;
    public float endbpm;
    private boolean isEmpty = false;
    
    public myBpmObject(float bpmstart, float bpmend) {
        startbpm = bpmstart;
        endbpm = bpmend;
    }
    public myBpmObject() {
        // no value...
        isEmpty = true;
    }

    public int compareTo(myBpmObject b) {
        float bpm1;
        if (startbpm != 0.0)
            bpm1 = startbpm;
        else
            bpm1 = endbpm;
        float bpm2;
        if (b.startbpm != 0.0)
            bpm2 = b.startbpm;
        else
            bpm2 = b.endbpm;
        if (bpm1 < bpm2)
            return -1;
        if (bpm1 > bpm2)
            return 1;
        return 0;
    }

    public int compareTo(Object b1) {
        myBpmObject b = (myBpmObject) b1;
        return compareTo(b);
    }

    public boolean equals(Object b) {
        if (b instanceof myBpmObject) {
            myBpmObject mb = (myBpmObject) b;
            if ((startbpm == mb.startbpm) && (endbpm == mb.endbpm))
                return true;
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        if (isEmpty)
            return rapid_evolution.Filter.noValueString;
        String returnval = "";
        if (startbpm != 0.0) {
            returnval += StringUtil.getDecimalString(startbpm);
            if (endbpm != 0.0) {
                returnval += "->";
                returnval += StringUtil.getDecimalString(endbpm);
            }
            return returnval;
        }
        if (endbpm != 0.0)
            return StringUtil.getDecimalString(endbpm);
        return returnval;
    }

};

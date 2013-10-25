package rapid_evolution.comparables;


public class MySortObject implements Comparable {

    private Object object;
    private double sort_value;
    
    public MySortObject(Object object, double sort_value) {
        this.object = object;
        this.sort_value = sort_value;
    }
    
    public int compareTo(Object o) {
        if (o instanceof MySortObject) {
            MySortObject m_o = (MySortObject)o;
            if (sort_value < m_o.sort_value) return -1;
            else if (sort_value > m_o.sort_value) return 1;
            return 0;                
        }
        return -1;
    }    
    
    public Object getObject() { return object; }
    public double getValue() { return sort_value; }
    public String toString() { return object.toString(); }
}

package rapid_evolution.audio;

public class DetectedKey {
    private String startkey = "";
    private String endkey = "";
    private double accuracy;
    public DetectedKey() {
        
    }
    public DetectedKey(String _startkey, String _endkey, double _accuracy) {
        startkey = _startkey;
        endkey = _endkey;
        accuracy = _accuracy;
    }
    
    public String getStartKey() { return startkey; }
    public String getEndKey() { if (startkey.equals(endkey)) return ""; return endkey; }
    public void setStartKey(String key) { startkey = key; }
    public void setEndKey(String key) { endkey = key; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double value) { accuracy = value; }
    
    public String toString() {
        return "[key: " + startkey + "->" + endkey + ", accuracy: " + accuracy + "]";
    }
}

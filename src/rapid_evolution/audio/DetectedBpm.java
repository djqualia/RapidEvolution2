package rapid_evolution.audio;

public class DetectedBpm {
    private double bpm;
    private double accuracy;
    private int intensity;
    public DetectedBpm(double _bpm, double _accuracy, int _intensity) {
        bpm = _bpm;
        accuracy = _accuracy;
        intensity = _intensity;
    }
    
    public String toString() { return "[bpm=" + bpm + ", accuracy=" + accuracy + ", intensity=" + intensity + "]"; }
    
    public double getBpm() { return bpm; }
    public double getAccuracy() { return accuracy; }
    public int getBeatIntensity() { return intensity; }
}

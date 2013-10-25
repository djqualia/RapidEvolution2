package rapid_evolution.audio;

import java.util.Arrays;
import java.util.Vector;

import org.apache.log4j.Logger;
import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.audio.dsps.key.*;
import rapid_evolution.comparables.MySortObject;
import com.mixshare.rapid_evolution.music.spiral.*;

public class KeyProbability {
    
    private static Logger log = Logger.getLogger(KeyProbability.class);
    
    private Vector filters = new Vector();
    
    /*
    public KeyProbability() {        
        filters.add(new MultiKeyProbabilityFilter("major (ionian)",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 },
                new double[] { 0.31, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.25, 0.0, 0.19, 0.0, 0.0 }, // maj6
                new double[] { 0.31, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.19 }, // maj7
                new double[] { 0.22, 0.0, 0.14, 0.0, 0.18, 0.14, 0.0, 0.18, 0.0, 0.0, 0.0, 0.14 } // maj11
        } ));     
        filters.add(new MultiKeyProbabilityFilter("minor (aeolian)",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 }, // natural
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.0, 0.12 }, // harmonic
                new double[] { 0.31, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.19, 0.0 }, // min7
                new double[] { 0.26, 0.0, 0.16, 0.21, 0.0, 0.0, 0.0, 0.21, 0.0, 0.0, 0.16, 0.0 }, // min9
                new double[] { 0.22, 0.0, 0.14, 0.18, 0.0, 0.14, 0.0, 0.18, 0.0, 0.0, 0.14, 0.0 } // min11
        } ));        
        filters.add(new MultiKeyProbabilityFilter("minor (dorian+6)",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 },
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 }, // augmented
                new double[] { 0.31, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.25, 0.0, 0.19, 0.0, 0.0 }, // min6
                new double[] { 0.31, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.19, 0.0 } // min7
        } ));        
        filters.add(new MultiKeyProbabilityFilter("minor (phrygian-2)",				new double[][] {
                new double[] { 0.2, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 },
                new double[] { 0.31, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.19, 0.0 }, // min7
                new double[] { 0.26, 0.16, 0.0, 0.21, 0.0, 0.0, 0.0, 0.21, 0.0, 0.0, 0.16, 0.0 }, // min7b9
        } ));        
        filters.add(new MultiKeyProbabilityFilter("major (lydian+4)",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.12 },
                new double[] { 0.31, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.19 }, // maj7
                new double[] { 0.22, 0.0, 0.14, 0.0, 0.18, 0.0, 0.14, 0.18, 0.0, 0.0, 0.0, 0.14 }, // maj7#11
                new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.12, 0.0 } // flat-seven
        } ));        
        filters.add(new MultiKeyProbabilityFilter("major (mixolydian-7)",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 }, // scale notes
                new double[] { 0.31, 0.0, 0.0, 0.0, 0.25, 0.0, 0.0, 0.25, 0.0, 0.0, 0.19, 0.0 }, // dom7
                new double[] { 0.26, 0.0, 0.16, 0.0, 0.21, 0.0, 0.0, 0.21, 0.0, 0.0, 0.16, 0.0 }, // dom9
                new double[] { 0.22, 0.0, 0.14, 0.0, 0.18, 0.14, 0.0, 0.18, 0.0, 0.0, 0.14, 0.0 } // dom11
        } ));        
        filters.add(new MultiKeyProbabilityFilter("locrian",				new double[][] {
                new double[] { 0.2, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.12, 0.0 },
                new double[] { 0.31, 0.0, 0.0, 0.25, 0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 0.19, 0.0 }, // dim7
                new double[] { 0.26, 0.16, 0.0, 0.21, 0.0, 0.0, 0.21, 0.0, 0.0, 0.0, 0.16, 0.0 } // dim7b9
        } ));             
    }
    */

    public KeyProbability(double segmentTime) { 
        
        /*
        Vector minorNaturalPoints = new Vector();        
        Vector minorHarmonicPoints = new Vector(); // dorian       
        Vector majorPoints = new Vector();
        for (int i = 0; i < 12 * 3; ++i) {
            majorPoints.add(SpiralArray.getMajorKeyPoint(i));
            minorNaturalPoints.add(SpiralArray.getMinorKeyPoint(i, 0.0, 1.0));
            minorHarmonicPoints.add(SpiralArray.getMinorKeyPoint(i, 1.0, 1.0));
        }
        
        filters.add(new CompositeKeyProbabilityFilter(
                new SpiralKeyProbabilityFilter(new SpiralKey(false, "aeolian", minorNaturalPoints)),
                new MultiKeyProbabilityFilter(false, "aeolian",             new double[][] {
                        new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 },
                        new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.0, 0.12 } }),
                        0.4,
                        0.6));

        filters.add(new CompositeKeyProbabilityFilter(
                new SpiralKeyProbabilityFilter(new SpiralKey(false, "dorian", minorHarmonicPoints)),
                new MultiKeyProbabilityFilter(false, "dorian",              new double[][] {
                        new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 },
                        new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } }),
                        0.4,
                        0.6));
        
        filters.add(new CompositeKeyProbabilityFilter(
                new SpiralKeyProbabilityFilter(new SpiralKey(true, "ionian", majorPoints)),
                new SingleKeyProbabilityFilter(true, "ionian",          new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } ),
                        0.4,
                        0.6));
        */
        
        /*
        Vector minorNaturalPoints = new Vector();        
        Vector minorHarmonicPoints = new Vector(); // dorian       
        Vector minorMelodicPoints = new Vector();
        Vector minorPhrygianPoints = new Vector();
        Vector majorPoints = new Vector();
        Vector mixolydianPoints = new Vector();
        Vector lydianPoints = new Vector();
        for (int i = 0; i < 12 * 3; ++i) {
            majorPoints.add(SpiralArray.getMajorKeyPoint(i));
            minorNaturalPoints.add(SpiralArray.getMinorKeyPoint(i, 0.0, 1.0));
            minorHarmonicPoints.add(SpiralArray.getMinorKeyPoint(i, 1.0, 1.0));
            minorMelodicPoints.add(SpiralArray.getMinorKeyPoint(i, 0.5, 0.5));
            minorPhrygianPoints.add(SpiralArray.getPhrygianKeyPoint(i));
            mixolydianPoints.add(SpiralArray.getMixolydianKeyPoint(i));
            lydianPoints.add(SpiralArray.getLydianKeyPoint(i));
        }
        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey(false, "aeolian", minorNaturalPoints)));
        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey(false, "dorian", minorHarmonicPoints)));
        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey(false, "melodic", minorMelodicPoints)));
//        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey("minor phrygian", minorPhrygianPoints)));
        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey(true, "ionian", majorPoints)));
//        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey("major mixolydian", mixolydianPoints)));
//        filters.add(new SpiralKeyProbabilityFilter(new SpiralKey("major lydian", lydianPoints)));
*/
        
        
        filters.add(new SingleKeyProbabilityFilter(true, "ionian", 			new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter(true, "lydian",				new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter(true, "mixolydian",			new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 } ));
        filters.add(new MultiKeyProbabilityFilter(false, "aeolian",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 },
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.0, 0.12 } // harmonic
        } ));        
        filters.add(new MultiKeyProbabilityFilter(false, "dorian",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 },
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } // augmented
        } ));        
        filters.add(new SingleKeyProbabilityFilter(false, "phrygian",			new double[] { 0.2, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 } ));
    }
    
    /*
    public KeyProbability() {        
        filters.add(new SingleKeyProbabilityFilter("major (ionian)", 				new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (aeolian)",	 			new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (harmonic)", 				new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (augmented)",				new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (melodic)", 				new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.06, 0.06, 0.06, 0.06 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (jazz melodic)", 			new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (dorian+6)",				new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 } ));
        filters.add(new SingleKeyProbabilityFilter("minor (phrygian-2)",			new double[] { 0.2, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 } ));
        filters.add(new SingleKeyProbabilityFilter("major (lydian+4)",				new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.12 } ));
        filters.add(new SingleKeyProbabilityFilter("major (lydian+4 flat-seven)",	new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.12, 0.0 } ));
        filters.add(new SingleKeyProbabilityFilter("major (mixolydian-7)",			new double[] { 0.2, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.16, 0.0, 0.12, 0.12, 0.0 } ));
        filters.add(new SingleKeyProbabilityFilter("locrian",				new double[] { 0.2, 0.12, 0.0, 0.16, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.12, 0.0 } ));
        filters.add(new MultiKeyProbabilityFilter("minor (combined)",				new double[][] {
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.0, 0.12, 0.0, 0.12 },
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.0, 0.12 },
                new double[] { 0.2, 0.0, 0.12, 0.16, 0.0, 0.12, 0.0, 0.16, 0.12, 0.0, 0.12, 0.0 }
        } ));                        
//        filters.add(new SingleKeyProbabilityFilter("pentatonic",	 			new double[] { 0.26, 0.0, 0.16, 0.0, 0.21, 0.0, 0.0, 0.21, 0.0, 0.16, 0.0, 0.0 } ));
//        filters.add(new SingleKeyProbabilityFilter("pentatonic minor",		 	new double[] { 0.26, 0.0, 0.0, 0.21, 0.0, 0.16, 0.0, 0.21, 0.0, 0.0, 0.16, 0.0 } ));
//        filters.add(new SingleKeyProbabilityFilter("blues", 					new double[] { 0.2, 0.0, 0.0, 0.12, 0.16, 0.12, 0.16, 0.12, 0.0, 0.0, 0.12, 0.0 } ));
//        filters.add(new SingleKeyProbabilityFilter("whole tone",	 			new double[] { 0.23, 0.0, 0.14, 0.0, 0.17, 0.0, 0.14, 0.0, 0.18, 0.0, 0.14, 0.0 } ));                
    }
*/
  
    private static double analyze_segment_size = 0.1; // seconds;
    private double segment_size;
    private double[] segment_totals = new double[12];
    
    public void add(double[] totals, double time) {
        segment_size += time;
        for (int i = 0; i < totals.length; ++i) {
            segment_totals[i] += totals[i];
        }
        if (segment_size > analyze_segment_size) {
            processSegment();
        }
    }

    public void add(int[] totals, double time) {
        segment_size += time;
        for (int i = 0; i < totals.length; ++i) {
            segment_totals[i] += totals[i];
        }
        if (segment_size > analyze_segment_size) {
            processSegment();
        }
    }
    
    public void finish() {
        if (segment_size > 0) processSegment();        
    }
    
    private void processSegment() {
        //if (log.isTraceEnabled()) log.trace("processSegment(): segment_size=" + segment_size + "s");
        double total = 0;
        for (int i = 0; i < 12; ++i) total += segment_totals[i];
        if (total > 0) {
          for (int i = 0; i < 12; ++i) segment_totals[i] /= total;
        }
        for (int f = 0; f < filters.size(); ++f) {
            KeyProbabilityFilter filter = (KeyProbabilityFilter)filters.get(f);
            filter.add(segment_totals);
        }                    
        segment_size = 0;
        for (int i = 0; i < segment_totals.length; ++i) {
            segment_totals[i] = 0;
        }        
    }
    
    /*
    public DetectedKey getKey() {
        if (hasNoData()) return null;
        KeyProbabilityFilter max_filter = null;
        double best_filter_probability = 0.0;
        KeyProbabilityFilter best_filter = null;
        double max_probability = 0.0;
        double min_probability = Double.MAX_VALUE;
        double total_probability = 0.0;
        for (int f = 0; f < filters.size(); ++f) {
            KeyProbabilityFilter filter = (KeyProbabilityFilter)filters.get(f);
            double filter_probability = filter.getMaxProbability();
            if (filter_probability > max_probability) {
                max_probability = filter_probability;
                max_filter = filter;
            }
            double min_filter_probability = filter.getMinProbability();
            if (min_filter_probability < min_probability) {
                min_probability = min_filter_probability;
            }
            double total = filter.getTotalProbability();
            total_probability += total;
            if (total > best_filter_probability) {
                best_filter_probability = total;
                best_filter = filter;
            }
        }       
        double average = total_probability / (filters.size() * 12);
        double range = max_probability - min_probability;
        double accuracy = (max_probability - average) / range;
        if (accuracy < 0.0) accuracy = 0.0;
        if (accuracy > 1.0) accuracy = 1.0;
        return new DetectedKey(max_filter.getKey(), "", accuracy);
    }
    */
    
    public DetectedKey getDetectedKey(boolean print_details) {
        try {
            Vector results = new Vector();
            KeyProbabilityFilter max_filter = null;
            double best_filter_probability = 0.0;
            KeyProbabilityFilter best_filter = null;            
            double max_probability = 0.0;
            double min_probability = Double.MAX_VALUE;
            double total_probability = 0.0;                        
            for (int f = 0; f < filters.size(); ++f) {
                KeyProbabilityFilter filter = (KeyProbabilityFilter)filters.get(f);
                KeyProbabilitySet resultSet = filter.getNormalizedProbabilities();
                resultSet.addResults(results);
                double filter_probability = resultSet.getMaxProbability();
                if (filter_probability > max_probability) {
                    max_probability = filter_probability;
                    max_filter = filter;
                }
                double min_filter_probability = resultSet.getMinProbability();
                if (min_filter_probability < min_probability) {
                    min_probability = min_filter_probability;
                }
                double total = resultSet.getTotalProbability();
                total_probability += total;
                if (total > best_filter_probability) {
                    best_filter_probability = total;
                    best_filter = filter;
                }
            }          
            double average = total_probability / (filters.size() * 12);
            double range = max_probability - min_probability;
            double accuracy = (max_probability - average) / range;
            if (accuracy < 0.0) accuracy = 0.0;
            if (accuracy > 1.0) accuracy = 1.0;
            
            Object[] array = results.toArray();
            Arrays.sort(array);
            double[] buckets = new double[12];
            double minor_pts = 0.0;
            int num_minors = 0;
            double major_pts = 0.0;
            int num_majors = 0;
            for (int i = array.length - 1; i >= 0; --i) {
                MySortObject so = (MySortObject)array[i];
                String keycode = Key.getKey(so.getObject().toString()).getKeyCode().toString();
                if (print_details) log.debug("debugKeyScores(): key=" + so.getObject().toString() + ", score=" + so.getValue() + ", keycode=" + keycode);
                StringBuffer keycode_int = new StringBuffer();
                boolean minor = false;
                for (int c = 0; c < keycode.length(); ++c) {
                    if (Character.isDigit(keycode.charAt(c)))
                        keycode_int.append(keycode.charAt(c));                    
                    else if ((keycode.charAt(c) == 'A')) {
                        minor_pts += so.getValue();
                        ++num_minors;
                        minor = true;
                    } else if ((keycode.charAt(c) == 'B')) {
                        major_pts += so.getValue();
                        ++num_majors;
                    }
                }
                int index = Integer.parseInt(keycode_int.toString()) - 1;
                buckets[index] += so.getValue();
                /*
                if (so.getObject().toString().indexOf("minor (phrygian") > 0)
                    buckets[safeindex(index - 1) - 1] += so.getValue() / 2;
                else if (so.getObject().toString().indexOf("minor (dorian") > 0)
                    buckets[safeindex(index + 1) - 1] += so.getValue() / 2;
                else if (so.getObject().toString().indexOf("major (lydian") > 0)
                    buckets[safeindex(index + 1) - 1] += so.getValue() / 2;
                else if (so.getObject().toString().indexOf("major (mixolydian") > 0)
                    buckets[safeindex(index - 1) - 1] += so.getValue() / 2;
                else {
                    buckets[safeindex(index - 1) - 1] += so.getValue() / 2;
                    buckets[safeindex(index + 1) - 1] += so.getValue() / 2;
                }
                */
            }
            if (print_details) log.debug("debugKeyScores(): key region buckets=");
            double max_bucket = 0.0;
            int max_index = -1;
            for (int i = 0; i < 12; ++i) {
                if (print_details) log.debug("debugKeyScores(): \t" + (i + 1) + ": " + buckets[i]);
                if (buckets[i] > max_bucket) {
                    max_bucket = buckets[i];
                    max_index = i + 1;
                }
            }
            if (print_details) log.debug("debugKeyScores(): max region=" + max_index);
            //log.debug("debugKeyScores(): major score=" + major_pts);
            //log.debug("debugKeyScores(): minor score=" + minor_pts);
            for (int i = array.length - 1; i >= 0; --i) {
                MySortObject so = (MySortObject)array[i];
                if (so.getValue() == 0.0) return new DetectedKey("", "", 0.0);
                /*
                String keycode = new Key(so.getObject().toString()).getKeyCode();
                StringBuffer keycode_int = new StringBuffer();
                for (int c = 0; c < keycode.length(); ++c) {
                    if (Character.isDigit(keycode.charAt(c)))
                        keycode_int.append(keycode.charAt(c));                    
                }
                int index = Integer.parseInt(keycode_int.toString());
                */
//                if ((index == max_index) || (safeindex(index + 3) == max_index) || (safeindex(index - 3) == max_index)) {
/*
                if (so.getObject().toString().indexOf("minor (phrygian") > 0)
                    index -= 1;
                else if (so.getObject().toString().indexOf("minor (dorian") > 0)
                    index += 1;
                else if (so.getObject().toString().indexOf("major (lydian") > 0)                    
                    index -= 1;
                else if (so.getObject().toString().indexOf("major (mixolydian") > 0)
                    index += 1;                
*/
//                if (safeindex(index) == max_index) {
                    String predicted_key = so.getObject().toString();
                    if (print_details) log.debug("debugKeyScores(): predicted key=" + predicted_key);                    
                    return new DetectedKey(predicted_key, "", accuracy);
//                }
//                }
            }
        } catch (Exception e) {
            log.error("debugKeyScores(): error creating debug output", e);
        }    
        return null;
    }
    
    private int safeindex(int index) {
        while (index < 1) index += 12;
        while (index > 12) index -= 12;
        return index;
    }
    
    public boolean hasNoData() {
        boolean all_zeros = true;
        for (int f = 0; f < filters.size(); ++f) {
            KeyProbabilityFilter filter = (KeyProbabilityFilter)filters.get(f);
            if (!filter.isAllZeros()) all_zeros = false;
        }
        return all_zeros;
    }
}

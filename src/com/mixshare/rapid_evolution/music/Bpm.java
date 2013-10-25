package com.mixshare.rapid_evolution.music;


public class Bpm {

    /**
     * This method will determine the percentage difference between two BPM values.
     * Since some BPMs are equivalent (i.e. 50bpm == 100bpm), the space covering
     * all the powers of 2 must be searched for the lowest possible difference.
     * 
     * For example:
     *   where sourceBpm=50 and targetBpm=105, the BPM difference is +5(%)
     * 
     * @param sourceBpm
     * @param targetBpm
     * @return double the difference from sourceBpm to targetBpm
     */
    static public double getBpmDifference(double sourceBpm, double targetBpm) {
        if ((sourceBpm == 0.0) || (targetBpm == 0.0)) return 0.0;
        if (sourceBpm == targetBpm) return 0.0;
        double diff_before_shift = targetBpm / sourceBpm * 100.0; // avoids calculating twice
        double diff = diff_before_shift - 100.0;
        if (diff < 0.0) {
            double diff2 = diff_before_shift * 2.0 - 100.0;
            if (diff2 == 0.0) return 0.0;
            else if (diff2 > 0) {
                if (-diff < diff2) return diff;
                else return diff2;
            } else {
                return getBpmDifference(sourceBpm, targetBpm * 2.0);
            }
        } else { // diff > 0.0
            double diff2 = diff_before_shift / 2.0 - 100.0;
            if (diff2 == 0.0) return 0.0;
            else if (diff2 < 0) {
                if (diff < -diff2) return diff;
                else return diff2;
            } else {
                return getBpmDifference(sourceBpm, targetBpm / 2.0);
            }
        }
    }    
    
}

package rapid_evolution.comparables;

import rapid_evolution.*;

public class myLengthRange implements Comparable {
  public myLengthRange(int min_seconds, int max_seconds) {
    this.max_seconds = max_seconds;
    this.min_seconds = min_seconds;
  }
  public myLengthRange() { // no value
  }
  public int compareTo(myLengthRange b) {
      if (max_seconds < b.max_seconds) return -1;
      if (max_seconds > b.max_seconds) return 1;
      return 0;
  }
  public int compareTo(Object b1) {
      myLengthRange b = (myLengthRange)b1;
    return compareTo(b);
  }
  public int hashCode() { return max_seconds; }
  public boolean equals(Object b) {
      if (b instanceof myLengthRange) {
          myLengthRange mb = (myLengthRange)b;
          return (max_seconds == mb.max_seconds);
      }
      return false;
  }
  public String toString() {
      if ((min_seconds == 0) && (max_seconds == 0))
          return Filter.noValueString;
      if (min_seconds == 0) {
          return "0:00" + "->" + StringUtil.seconds_to_time(max_seconds) + "s";
      }
      return StringUtil.seconds_to_time(min_seconds) + "->" + StringUtil.seconds_to_time(max_seconds) + "s";
  }
  public int max_seconds;
  public int min_seconds;
};

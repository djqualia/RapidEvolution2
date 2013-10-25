package rapid_evolution;

import rapid_evolution.RapidEvolution;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.OptionsUI;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SongUtil {
  public SongUtil() {
  }

  static public String trim(String input) {
    input = input.toLowerCase();
    input = StringUtil.ReplaceString("the ", input, "");
    input = StringUtil.ReplaceString("and", input, "");
    input = StringUtil.ReplaceString(" n ", input, "");
    input = StringUtil.ReplaceString("'n'", input, "");
    input = StringUtil.ReplaceString("-n-", input, "");
    input = StringUtil.ReplaceString(" at ", input, "@");
    String returnval = new String("");
    for (int i = 0; i < input.length(); ++i) {
      if (Character.isDigit(input.charAt(i)) || Character.isLetter(input.charAt(i))) returnval += input.charAt(i);
    }
    returnval = StringUtil.ReplaceString("drumnbass", returnval, "db");
    returnval = StringUtil.ReplaceString("drumbass", returnval, "db");
    returnval = StringUtil.ReplaceString("ing", returnval, "in");
    return returnval;
  }

  private static float valid_range = 8.0f;
  public static float get_bpmdiff(float tobpm, float frombpm) {
      if ((tobpm == 0.0f) || (frombpm == 0.0f)) return Float.MAX_VALUE;
    float diff = frombpm / tobpm * 100.0f - 100.0f;
    if (diff == 0.0f) return 0.0f;
    else if (diff < 0) {
        float diff2 = frombpm * 2.0f / tobpm * 100.0f - 100.0f;
        if (diff2 == 0.0f) return 0.0f;
        else if (diff2 > 0) {
            if (-diff < diff2) return diff;
            else return diff2;
        } else {
            return get_bpmdiff(tobpm, frombpm * 2.0f);
        }
    } else { // diff > 0
        float diff2 = frombpm / 2.0f / tobpm * 100.0f - 100.0f;
        if (diff2 == 0.0f) return 0.0f;
        else if (diff2 < 0) {
            if (diff < -diff2) return diff;
            else return diff2;
        } else {
            return get_bpmdiff(tobpm, frombpm / 2.0f);
        }
    }
  }

  public static boolean isMemberOf(int index, int[] indexarray) {
    for (int i = 0; i < indexarray.length; ++i) if (indexarray[i] == index) return true;
    return false;
  }
}

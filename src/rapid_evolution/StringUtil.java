package rapid_evolution;

import rapid_evolution.comparables.MyString;

import java.util.StringTokenizer;
import java.util.Vector;
import com.mixshare.rapid_evolution.music.Key;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Point;

import org.apache.log4j.Logger;
import rapid_evolution.util.OSHelper;

public class StringUtil {
    
    private static Logger log = Logger.getLogger(StringUtil.class);
    
    public StringUtil() {
    }

    public static int countChar(String input, char c) {
        if (input == null) return 0;
        int count = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == c) count++;
        }
        return count;
    }
    
    public static String cleanString(String input) {
        //System.out.println("cleaning string: " + input);
        if (input == null) return "";
        StringBuffer output = new StringBuffer();
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            //System.out.println("\tcharacter: " + c + ", int value: " + (int)c);
            if (Character.isIdentifierIgnorable(c)) {
                // ignore
            } else {
                output.append(c);
            }                        
        }
        return stripNonValidXMLCharacters(output.toString().trim());
    }

    static private String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }  
    static public boolean substring(String sub, String main) {
      if ((main == null) || (sub == null)) return false;
      if (main.toLowerCase().indexOf(sub.toLowerCase()) >= 0) return true;
      return false;
    }

    static public boolean isNumeric(char val) {
      if ((val >= '0') && (val <= '9')) return true;
      return false;
    }

    static public String ReplaceString(String remove, String source, String replace) {
      if (remove.equals(replace)) return source;
      String returnval = new String(source);
      int fromindex = 0;
      int index = returnval.indexOf(remove);
      while (index >= 0) {
        returnval = returnval.substring(0, returnval.indexOf(remove, fromindex)) + replace + returnval.substring(returnval.indexOf(remove, fromindex) + remove.length(), returnval.length());
        fromindex = index + replace.length();
        index = returnval.indexOf(remove, fromindex);
      }
      return returnval;
    }

    static public MyString processcomments(String input) {
      String tmp = new String("");
      for (int i = 0; i < input.length(); ++i) {
       if (input.charAt(i) == '\n') tmp += "  ";
       else tmp += input.charAt(i);
      }
      return new MyString(tmp);
    }

    static public String processcomments2(String input) {
      String tmp = new String();
      for (int i = 0; i < input.length(); ++i) {
       if (input.charAt(i) == '\n') tmp += "  ";
       else tmp += input.charAt(i);
      }
      return tmp;
    }

    static public String makeProper(String input) {
      String output = new String("");
      boolean doneupper = false;
      for (int i = 0; i < input.length(); ++i) {
        if (!doneupper && Character.isLetter(input.charAt(i))) {
          doneupper = true;
          output += Character.toUpperCase(input.charAt(i));
        } else {
          if (Character.isLetter(input.charAt(i))) {
            output += Character.toLowerCase(input.charAt(i));
          } else {
            doneupper = false;
            output += input.charAt(i);
          }
        }
      }
      return output;
    }

    static public String XMLScrub(String val2) {
     String val = val2;
     if (val == null) return null;
     if (val.equals("")) return val;
      while ((val.length() > 0) && ((val.charAt(0) == ' ') || (val.charAt(0) == '\t'))) val = val.substring(1, val.length());
      while ((val.length() > 0) && ((val.charAt(val.length() - 1) == ' ') || (val.charAt(val.length() - 1) == '\t'))) val = val.substring(0, val.length() - 1);
      return val;
    }

    static public Vector ParseTabs(String input) {
      Vector x = new Vector();
      int index = 0;
      int startpos = 0;
      boolean done = false;
      while (!done) {
        while ((index < input.length()) && input.charAt(index) != '\t') index++;
        x.add(input.substring(startpos, index));
        startpos = index + 1;
        index++;
        if (index >= input.length()) return x;
      }
      return x;
    }

    static public Vector Parse(String input, char c) {
        Vector x = new Vector();
        try {	        
	        int index = 0;
	        int startpos = 0;
	        boolean done = false;
	        while (!done) {
	          while ((index < input.length()) && input.charAt(index) != c) index++;
	          x.add(input.substring(startpos, index));
	          startpos = index + 1;
	          index++;
	          if (index >= input.length()) return x;
	        }
	        return x;
        } catch (Exception e) {
            log.error("Parse(): error", e);
        }
        return x;
      }
    
    static public String validate_bpm(String input) {
      if ((input == null) || (input.equals(""))) return new String("");
      int seperator = 0;
      try {
        float num = Float.parseFloat(input);
        return input;
      } catch (Exception e) { return new String(""); }
    }

    static public String validate_time(String input) {
      if ((input == null) || (input.equals(""))) return new String("");
      int seperator = 0;
      while (((seperator < input.length()) && ((input.charAt(seperator) != ':') && (input.charAt(seperator) != '.') && (input.charAt(seperator) != '-')))) seperator++;
        if (seperator == input.length()) return new String("");
      try {
        int num = Integer.parseInt(input.substring(0, seperator));
        if ((input.length() - 3) != seperator) return new String("");
        int num2 = Integer.parseInt(input.substring(seperator + 1, input.length()));
        return input;
      } catch (Exception e) { log.error("validate_time(): error", e); return new String(""); }
    }

    static public String validate_timesig(String input) {
      if ((input == null) || (input.equals(""))) return new String("");
      if (input.equalsIgnoreCase("n/a")) return input;
      int seperator = 0;
      while (((seperator < input.length()) && ((input.charAt(seperator) != '/')))) seperator++;
        if (seperator == input.length()) return new String("");
      try {
        int num = Integer.parseInt(input.substring(0, seperator));
        int num2 = Integer.parseInt(input.substring(seperator + 1, input.length()));
        return input;
      } catch (Exception e) { return new String(""); }
    }

    static public String validate_keyformat(String input) {
      if ((input == null) || (input.equals(""))) return new String("");
      if (Key.isValid(input)) return input;
      return new String("");
    }

    static public String validate_rank(String input) {
      if ((input == null) || (input.equals(""))) return new String("");
      try {
        int rank = Integer.parseInt(input);
        if (rank < 0) return new String("0");
        if (rank > 100) return new String("100");
        return input;
      } catch (Exception e) { log.error("validate_rank(): error", e); }
      return new String("");
    }

    static public String validate_bpmdiff(String input) {
      if ((input == null) || (input.equals(""))) return new String("");
      try {
        float f = Float.parseFloat(input);
        return input;
      } catch (Exception e) { return new String(""); }
    }

    public static String track_increment(String input) {
      if (input == null || input.equals("")) return input;
      int startIndex = input.indexOf('-') + 1;
      int intindex = startIndex;
      while ((intindex < input.length()) && !((input.charAt(intindex) >= '0') && (input.charAt(intindex) <= '9'))) intindex++;
      String startstring = input.substring(0, intindex);
      if (intindex == input.length()) return new String("");
      int endindex = intindex;
      while ((endindex < input.length()) && ((input.charAt(endindex) >= '0') && (input.charAt(endindex) <= '9'))) endindex++;
      String endstring = new String("");
      if (endindex < input.length()) endstring += input.substring(endindex, input.length());
      try {
        input = input.substring(0, endindex);
        input = input.substring(intindex, input.length());
        int zeroindex = 0;
        while ((zeroindex < input.length()) && input.charAt(zeroindex) == '0') zeroindex++;
        String zerostr = new String("");
        boolean addzero = true;
        if ((zeroindex < input.length()) && (input.charAt(zeroindex) == '9')) addzero = false;
        while ((zeroindex > 0) && addzero) {
          zerostr += "0";
          zeroindex--;
        }
        int num = Integer.parseInt(input);
        num++;
        return new String(startstring + zerostr + String.valueOf(num) + endstring);
      } catch (Exception e) { log.error("track_increment(): error", e); return new String(""); }
    }

    public static String ProcessStyleName(String style) {
      StyleLinkedList siter = SongDB.instance.masterstylelist;
      while (siter != null) {
         if (siter.getName().equalsIgnoreCase(style)) return siter.getName();
         siter = siter.next;
      }
      style = SongUtil.trim(style);
      if (style.length() > 0) {
        siter = SongDB.instance.masterstylelist;
        while (siter != null) {
          String tname = SongUtil.trim(siter.getName());
          if (tname.length() > 0) {
            if (tname.equalsIgnoreCase(style)) return siter.getName();
            siter = siter.next;
          }
        }
      }
      return style;
    }
    
    
    public static String getline(String input, int line) {
      if (input == null) return null;
      if (input.equals("")) return null;
      int firstindex = -1;
      int count = 0;
      if (line == 0) firstindex = 0;
      int countedlines = 0;
      while (true) {
        if (input.charAt(count) == '\n') {
          if ((countedlines + 1) == line) firstindex = count + 1;
          if (countedlines == line) return input.substring(firstindex, count);
          countedlines++;
        }
        count++;
        if (input.length() == count) return input.substring(firstindex, count);
      }
    }

    public static String seconds_to_time(int seconds) {
      if (seconds == 0) return "";
      int minutes = 0;
      while (seconds >= 60) {
        minutes++;
        seconds -= 60;
      }
      String secs = new String("");
      if (seconds < 10) secs += "0";
      return new String(String.valueOf(minutes) + ":" + secs + String.valueOf(seconds));
    }

    public static String ScrubFileType(String input) {    
        String extension = FileUtil.getExtension(input);
        if ((extension != null) && (extension.length() > 0)) {
            input = input.substring(0, input.length() - extension.length() - 1);
        }
      return input;
    }

    public static int parseMeasure(String timesig) {
      try {
        String tmpstr = new String();
        int idx = 0;
        while (Character.isDigit(timesig.charAt(idx))) idx++;
        return Integer.parseInt(timesig.substring(0, idx));
      } catch (Exception e) { log.error("parseMeasure(): error", e); }
      return 4;
    }

    public static byte[] stringtobytearray(String input) {
      byte[] returnval = new byte[input.length()];
      for (int i = 0; i < input.length(); ++i) returnval[i] = (byte)input.charAt(i);
      return returnval;
    }

    public static int num_lines(String str){
      int count = 1;
      for (int i = 0; i < str.length(); ++i) if (str.charAt(i) == '\n') count++;
      return count;
    }

    public static int gettracktype(String input) {
      if (!(input.length() > 0)) return -1;
      char l = Character.toLowerCase(input.charAt(0));
      if ((l == 'a') || (l == 'b')) return 0;
      if ((l == 'c') || (l == 'd')) return 1;
      if ((l == 'e') || (l == 'f')) return 2;
      if ((l == 'g') || (l == 'h')) return 3;
      if ((l == 'i') || (l == 'j')) return 4;
      if ((l == 'k') || (l == 'l')) return 5;
      if ((l == 'm') || (l == 'n')) return 6;
      if ((l == 'o') || (l == 'p')) return 7;
      if ((l == 'q') || (l == 'r')) return 8;
      if ((l == 's') || (l == 't')) return 9;
      if ((l == 'u') || (l == 'v')) return 10;
      if ((l == 'w') || (l == 'x')) return 11;
      if ((l == 'y') || (l == 'z')) return 12;
      return -1;
    }

    public static String printboolean(boolean input) {
      if (input) return new String("1");
      else return new String("0");
    }

    public static String extract_date(String input) {
      try {
        int index = 0;
        while ( (index < input.length()) && (! (input.charAt(index) == ':'))) {
          index++;
        }
        index--;
        while ( (index >= 0) && (StringUtil.isNumeric(input.charAt(index)))) {
          index--;
        }
        index++;
        String returnval = input.substring(index, index + 5);
        if (returnval.charAt(0) == '0') return returnval.substring(1);
        else return returnval;
      } catch (Exception e) { }
      return new String("");
    }

    public static String remove_underscores(String input) {
      if (input.indexOf("_") < 0) return input; // there are no underscores
      else {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < input.length(); ++i) {
          if (input.charAt(i) == '_') sb.append(" ");
          else sb.append(input.charAt(i));
        }
        return sb.toString();
      }
    }

    public static String getBpmDiffText(float bpmdiff) {
      String bpmtext = String.valueOf(bpmdiff);
      if (bpmdiff > 0) bpmtext = new String("+" + bpmtext);
      int length = bpmtext.length();
      if (length > 6) length = 6;
      return bpmtext.substring(0,length);
    }

    public static String RemoveExtension(String filename) {
      if (filename.charAt(filename.length() -4) == '.') {
        return filename.substring(0, filename.length() - 4);
      }
      return filename;
    }

    public static String FilterKeyForFilename(String filename) {
      int pos = filename.indexOf("#");
      if (((pos + 1) < filename.length()) && (filename.charAt(pos+1) == 'm'))
        return ReplaceString("#", filename, "-sharp-");
      else return ReplaceString("#", filename, "-sharp");
    }

    static public Point parsePoint(String input) {
      Dimension d = parseDimension(input);
      Point p = new Point(d.width, d.height);
      return p;
    }

    static public Dimension parseDimension(String input) {
      try {
        int index1 = 1;
        int index2 = index1;
        while (Character.isDigit(input.charAt(index2)) || (input.charAt(index2) == '.') || (input.charAt(index2) == '-')) index2++;
        int val1 = (int)(Double.parseDouble(input.substring(index1, index2)));
        index1 = index2 + 1;
        index2 = index1;
        while  (Character.isDigit(input.charAt(index2)) || (input.charAt(index2) == '.') || (input.charAt(index2) == '-')) index2++;
        int val2 = (int)(Double.parseDouble(input.substring(index1, index2)));
        return new Dimension(val1, val2);
      } catch (Exception e) { }
      return null;
    }

    static public Color parseColor(String input) {
      if (input == null) return null;
      try {
        int index1 = 1;
        int index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int red = Integer.parseInt(input.substring(index1, index2));
        index1 = index2 + 1;
        index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int green = Integer.parseInt(input.substring(index1, index2));
        index1 = index2 + 1;
        index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int blue = Integer.parseInt(input.substring(index1, index2));
        return new Color(red, green, blue);
      } catch (Exception e) { }
      return null;
    }

    static public Insets parseInsets(String input) {
      try {
        int index1 = 1;
        int index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int val1 = Integer.parseInt(input.substring(index1, index2));
        index1 = index2 + 1;
        index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int val2 = Integer.parseInt(input.substring(index1, index2));
        index1 = index2 + 1;
        index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int val3 = Integer.parseInt(input.substring(index1, index2));
        index1 = index2 + 1;
        index2 = index1;
        while (Character.isDigit(input.charAt(index2))) index2++;
        int val4 = Integer.parseInt(input.substring(index1, index2));
        return new Insets(val1, val2, val3, val4);
      } catch (Exception e) { }
      return null;
    }
    static public boolean isTrue(String val) {
      if (val.equalsIgnoreCase("true")) return true;
      if (val.equalsIgnoreCase("yes")) return true;
      if (val.equalsIgnoreCase("1")) return true;
      if (val.equalsIgnoreCase("on")) return true;
      if (val.equalsIgnoreCase("selected")) return true;
      return false;
    }
    static public boolean isFalse(String val) {
      if (val.equalsIgnoreCase("false")) return true;
      if (val.equalsIgnoreCase("no")) return true;
      if (val.equalsIgnoreCase("0")) return true;
      if (val.equalsIgnoreCase("none")) return true;
      if (val.equalsIgnoreCase("off")) return true;
      if (val.equalsIgnoreCase("unselected")) return true;
      return false;
    }

    static public String getDecimalString(float val) {
      String returnval = String.valueOf(val);
      if (returnval.endsWith(".0")) return returnval.substring(0, returnval.length() - 2);
      return returnval;
    }

    // parses title (from remix) and return the original string if can't be parsed
     static public String parseTitle(String name) {
       while (name.endsWith(" ")) name = name.substring(0, name.length() - 1);
       int index = name.length() - 1;
       int endindex = -1;
       int startindex = -1;
       boolean done = false;
       while ((index >= 0) && !done) {
         if ((name.charAt(index) == ')') || (name.charAt(index) == ']')) endindex = index;
         if ((name.charAt(index) == '(') || (name.charAt(index) == '[')) {
             startindex = index;
             done = true;
         }
         index--;
       }
       if (done) {
         if (endindex == -1) endindex = name.length();
         startindex++;
         endindex--;
         if ((startindex < name.length()) && (endindex >= 0) && ((endindex == name.length() - 2) || (endindex == name.length() - 1)) && ((startindex - 2) >= 0)) {
           String songname = name.substring(0, startindex - 1);
           while (songname.endsWith(" ")) songname = songname.substring(0, songname.length() - 1);
           return songname;
         }
       }
       return name;
     }

     // parse remix from title field, returns "" if it can't be found
     static public String parseRemix(String name) {
       while (name.endsWith(" ")) name = name.substring(0, name.length() - 1);
       int index = name.length() - 1;
       int endindex = -1;
       int startindex = -1;
       boolean done = false;
       while ((index >= 0) && !done) {
         if ((name.charAt(index) == ')') || (name.charAt(index) == ']')) endindex = index;
         if ((name.charAt(index) == '(') || (name.charAt(index) == '[')) {
             startindex = index;
             done = true;
         }
         index--;
       }
       if (done) {
         if (endindex == -1) endindex = name.length();
         startindex++;
         endindex--;
         if ((startindex < name.length()) && (endindex >= 0) && ((endindex == name.length() - 2) || (endindex == name.length() - 1)) && ((startindex - 2) >= 0)) {
           String remix = name.substring(startindex, endindex + 1);
           return remix;
         }
       }
       return "";
     }

     static public String trim(String input) {
         if (input == null) return null;
         while (input.startsWith(" ")) input = input.substring(1, input.length());
         while (input.endsWith(" ")) input = input.substring(0, input.length() - 1);
         return input;
     }

     static public String valueOf(double value) {
         String result = String.valueOf(value);
         if (result.endsWith(".0")) return result.substring(0, result.length() - 2);
         return result;
     }

     
     public static String unescape(String s) { // decode UTF-8
         StringBuffer sbuf = new StringBuffer () ;
         int l  = s.length() ;
         int ch = -1 ;
         int b, sumb = 0;
         for (int i = 0, more = -1 ; i < l ; i++) {
           /* Get next byte b from URL segment s */
           switch (ch = s.charAt(i)) {
     	case '%':
     	  ch = s.charAt (++i) ;
     	  int hb = (Character.isDigit ((char) ch) 
     		    ? ch - '0'
     		    : 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
     	  ch = s.charAt (++i) ;
     	  int lb = (Character.isDigit ((char) ch)
     		    ? ch - '0'
     		    : 10+Character.toLowerCase ((char) ch)-'a') & 0xF ;
     	  b = (hb << 4) | lb ;
     	  break ;
     	case '+':
     	  b = ' ' ;
     	  break ;
     	default:
     	  b = ch ;
           }
           /* Decode byte b as UTF-8, sumb collects incomplete chars */
           if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
     	sumb = (sumb << 6) | (b & 0x3f) ;	// Add 6 bits to sumb
     	if (--more == 0) sbuf.append((char) sumb) ; // Add char to sbuf
           } else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
     	sbuf.append((char) b) ;			// Store in sbuf
           } else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
     	sumb = b & 0x1f;
     	more = 1;				// Expect 1 more byte
           } else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
     	sumb = b & 0x0f;
     	more = 2;				// Expect 2 more bytes
           } else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
     	sumb = b & 0x07;
     	more = 3;				// Expect 3 more bytes
           } else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
     	sumb = b & 0x03;
     	more = 4;				// Expect 4 more bytes
           } else /*if ((b & 0xfe) == 0xfc)*/ {	// 1111110x (yields 1 bit)
     	sumb = b & 0x01;
     	more = 5;				// Expect 5 more bytes
           }
           /* We don't test if the UTF-8 encoding is well-formed */
         }
         return sbuf.toString() ;
       }
     
     static public String encodeString(String text) {
         StringBuffer encoded = new StringBuffer();
         for (int iter = 0; iter < text.length(); ++iter) {
             char character = text.charAt(iter);
             if (Character.isLetterOrDigit(character))
                 encoded.append(character);
         }
         return encoded.toString().toLowerCase();
     }     
     
     static public boolean isLowerCase(String input) {
         return input.equals(input.toLowerCase());
     }
     
     public static boolean areStyleNamesEqual(String stylea, String styleb) {
         return stylea.equalsIgnoreCase(styleb);
     }
     
     public static String filterParentName(String name, String parent) {

         StringTokenizer display_tokens = new StringTokenizer(parent.toLowerCase(), ",");
         while (display_tokens.hasMoreTokens()) {
             String token = display_tokens.nextToken().trim();                
             String t1 = token + ", ";
             String t2 = ", " + token;
             if (name.startsWith(t1)) {
                 name = name.substring(t1.length());
             } else if (name.endsWith(t2)) {
                 name = name.substring(0, name.length() - t2.length());
             }
         }         
         
         return name;
     }

     public static boolean isValid(String value) {
         return ((value != null) && (value.trim().length() > 0));
     }    
     
     /*
     public static String makeValidFilename(String new_filename) {
         new_filename = StringUtil.ReplaceString("/", new_filename, "-");
         new_filename = StringUtil.ReplaceString("\\", new_filename, "-");
         new_filename = StringUtil.ReplaceString(":", new_filename, "-");
         new_filename = StringUtil.ReplaceString("*", new_filename, "");
         new_filename = StringUtil.ReplaceString("?", new_filename, "");
         new_filename = StringUtil.ReplaceString("\"", new_filename, "'");
         new_filename = StringUtil.ReplaceString("<", new_filename, "[");
         new_filename = StringUtil.ReplaceString(">", new_filename, "]");
         new_filename = StringUtil.ReplaceString("|", new_filename, "-");
         return new_filename;
     }
     */
     
     static private int MAX_FILENAME_CHARACTERS = OSHelper.isWindows() ? 255 : 1000; // for directories and filenames
     static private int MAX_PATH_LENGTH = OSHelper.isWindows() ? 259 : 1000; // for full paths, a windows kernel limitation according to wikipedia
     static private String[] prohibited = { "con", "prn", "aux", "clock$", "nul", "com0",
                                            	"com1", "com2", "com3", "com4", "com5", "com6", "com7",
                                            	"com8", "com9", "ltp0", "ltp1", "ltp2", "ltp3", "ltp4",
                                            	"ltp5", "ltp6", "ltp7", "ltp8", "ltp9" };
     public static String checkFullPathLength(String input) {
    	 if (input == null)
    		 return null;
    	 if (input.length() > MAX_PATH_LENGTH) {
    		 if (log.isDebugEnabled())
    			 log.debug("checkFullPathLength(): file path limit reached=" + input);
    		 int extensionIndex = input.lastIndexOf(".");
    		 int end = MAX_PATH_LENGTH;
    		 String extension = "";
    		 if (extensionIndex > 0) {
    			 extension = input.substring(extensionIndex);
    			 end -= extension.length();
    			 return input.substring(0, end) + extension;
    		 } else {
    			 return input.substring(0, end);
    		 }
    	 }
    	 return input;    	 
     }     
     
     /**
      * This method converts any text into a valid filename.  It also converts to lower case so that
      * the filename will be more compatible between Windows and Linux, where filename case sensitivy
      * differs.
      * 
      * NOTE: DO NOT CHANGE THIS METHOD.  It is used by the LocalProfileFetcher and any changes could
      * impact the ability to fetch existing profiles.
      */
     public static String makeValidFilename(String text) {
    	 if (text == null)
    		 return null;
    	 //text = removeAccents(text);
    	 int tlength = text.length();
    	 StringBuffer result = new StringBuffer(tlength);
    	 boolean done = false;
    	 int i = 0;
    	 while ((i < tlength) && !done) {
    		 char c = text.charAt(i);
    		 boolean ignore = false;
    		 if ((c == '.') && (result.length() == 0)) ignore = true; // remove preceding .'s
    		 if ((c == ' ') && (result.length() == 0)) ignore = true; // remove preceding spaces
    		 if ((c == '/') || (c == '\\') || (c == ':') || (c == '*') ||
    				 (c == '?') || (c == '%') || (c == '|'))
    			 c = '-';
    		 if (c == '\"') c = '\'';
    		 if (c == '<') c = '[';
    		 if (c == '>') c = ']';
    		 if ((c >= 0) && (c <= 31)) ignore = true; // windows kernel prevents characters 1-31 
    		 if (!ignore) {
    			 result.append(c);
        		 //if (result.length() >= MAX_FILENAME_CHARACTERS)
        			 //done = true;
    		 }
    		 ++i;
    	 }
    	 // remove trailing .'s and spaces
    	 while ((result.length() > 0) && ((result.charAt(result.length() - 1) == '.') || (result.charAt(result.length() - 1) == ' ')))
    		 result.deleteCharAt(result.length() - 1);
    	 if (result.length() > 0) {
    		 for (int s = 0; s < prohibited.length; ++s) {
    			 if ((result.toString().equals(prohibited[s])) || (result.toString().startsWith(prohibited[s] + ".")))
    				 result.insert(0, '_');
    		 }
    	 }
         return result.toString();
     }     
     
     public static Float parseNumericalPrefix(String input) {         
         Float result = null;
         try {
             if (input != null) {
                 int index = 0;
                 char c = input.charAt(index);
                 while (Character.isDigit(c) || (c == '.') || (c == '-') || (c == '+')) {
                     ++index;
                     if (index >= input.length())
                         c = '$';
                     else
                         c = input.charAt(index);                    
                 }
                 result = Float.parseFloat(input.substring(0, index));             
             }
         } catch (Exception e) { }
         return result;
     }
     
     static public Vector getAllInBetweens(String data, String prefix, String suffix) {
         Vector result = new Vector();
         int lastIndex = 0;
         int startIndex = data.indexOf(prefix, lastIndex);
         while (startIndex >= 0) {
             int endIndex = data.indexOf(suffix, startIndex + prefix.length());
             if (endIndex >= 0) {
                 String value = data.substring(startIndex + prefix.length(), endIndex);
                 result.add(value);
                 lastIndex = endIndex + suffix.length();
             }
             startIndex = data.indexOf(prefix, lastIndex);
         }
         return result;
     }
     
     static public String stripHtml(String input) {
         if (input != null)
             return input.replaceAll("\\<.*?>","").trim();         
         return null;
     }
     
     /*
     public static String toBitArray(byte b) {
         StringBuffer result = new StringBuffer();
         for (int i = 1; i <= 8; ++i) {
             byte a = 0;
             if ()
             result.append(b & )
         }
         return result.toString();
     }
     */
     
     static public Integer parseYear(String text) {
         if (text == null)
             return null;
         boolean isWithinNumber = false;
         int startIndex = -1;
         int endIndex = -1;
         for (int c = 0; c < text.length(); ++c) {
             if (Character.isDigit(text.charAt(c))) {
                 if (isWithinNumber) {
                     endIndex = c;
                     if ((endIndex - startIndex + 1) == 4)
                         return new Integer(Integer.parseInt(text.substring(startIndex, endIndex + 1)));
                 } else {
                     isWithinNumber = true;
                     startIndex = c;
                 }
             } else {
                 isWithinNumber = false;
                 startIndex = -1;
                 endIndex = -1;
             }
         }
         return null;
     }
     
     static public boolean isValidTime(String input) {
        if ((input == null) || input.equals(""))
            return false;
        input = validate_time(input);
        if ((input == null) || input.equals(""))
            return false;
        return true;
     }
     
}

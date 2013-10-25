package rapid_evolution;

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

public class MySortableSong implements Comparable {
  public MySortableSong(SongLinkedList in_song, boolean info) {
    song = in_song;
    artistlc = song.getArtist().toLowerCase();
    albumlc = song.getAlbum().toLowerCase();
    tracklc = song.getTrack().toLowerCase();
    songnamelc = song.getSongname().toLowerCase();
    remixerlc = song.getRemixer().toLowerCase();
    useinfo = info;
  }
  public int compareTo(MySortableSong b) {
    if (OptionsUI.instance.songdisplayartist.isSelected()) {
      if (b.artistlc.equals("") && !artistlc.equals("")) return -1;
      if (artistlc.equals("") && !b.artistlc.equals("")) return 1;
      if ((artistlc.compareTo(b.artistlc)) < 0) return -1;
      else if ((artistlc.compareTo(b.artistlc)) > 0) return 1;
      else {
        if (OptionsUI.instance.songdisplayalbum.isSelected()) {
          if (b.albumlc.equals("") && !albumlc.equals("")) return -1;
          if (albumlc.equals("") && !b.albumlc.equals("")) return 1;
          if ((albumlc.compareTo(b.albumlc)) < 0) return -1;
          else if ((albumlc.compareTo(b.albumlc)) > 0) return 1;
          else {
            if (OptionsUI.instance.songdisplaytrack.isSelected()) {
              int cmp = 0;
              if (b.tracklc.equals("") && !tracklc.equals("")) cmp = -1;
              else if (tracklc.equals("") && !b.tracklc.equals("")) cmp = 1;
              else if (((tracklc.length() > 0) && Character.isDigit(tracklc.charAt(0))) && ((b.tracklc.length() > 0) && Character.isDigit(b.tracklc.charAt(0)))) {
                String numtracklc = new String("");
                int index = 0;
                while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
                index = 0;
                String bnumtracklc = new String("");
                while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
                int num1 = Integer.parseInt(numtracklc);
                int num2 = Integer.parseInt(bnumtracklc);
                if (num1 < num2) cmp = -1;
                else if (num1 > num2) cmp = 1;
              } else if (((tracklc.length() > 1) && Character.isDigit(tracklc.charAt(1))) && ((b.tracklc.length() > 1) && Character.isDigit(b.tracklc.charAt(1)))) {
                String numtracklc = new String("");
                int index = 1;
                while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
                index = 1;
                String bnumtracklc = new String("");
                while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
                int num1 = Integer.parseInt(numtracklc);
                int num2 = Integer.parseInt(bnumtracklc);
                if (num1 < num2) cmp = -1;
                else if (num1 > num2) cmp = 1;
              } else {
                if ((tracklc.compareTo(b.tracklc)) < 0) cmp = -1;
                else if ((tracklc.compareTo(b.tracklc)) > 0) cmp = 1;
              }
              if (cmp < 0) return -1;
              else if (cmp > 0) return 1;
              else {
                if (OptionsUI.instance.songdisplaysongname.isSelected()) {
                  if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
                  if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
                  if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
                  else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
                  else {
                    if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                      if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                      if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                      if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                      else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                      else return 0;
                    } else return 0;
                  }
                } else {
                  if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                    if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                    if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                    if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                    else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                    else return 0;
                  } else return 0;
                }
              }
            } else {
              if (OptionsUI.instance.songdisplaysongname.isSelected()) {
                if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
                if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
                if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
                else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
                else {
                  if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                    if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                    if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                    if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                    else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                    else return 0;
                  } else return 0;
                }
              } else {
                if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                  if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                  if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                  if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                  else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                  else return 0;
                } else return 0;
              }
            }
          }
        } else {
          if (OptionsUI.instance.songdisplaytrack.isSelected()) {
            int cmp = 0;
            if (b.tracklc.equals("") && !tracklc.equals("")) cmp = -1;
            else if (tracklc.equals("") && !b.tracklc.equals("")) cmp = 1;
            else if (((tracklc.length() > 0) && Character.isDigit(tracklc.charAt(0))) && ((b.tracklc.length() > 0) && Character.isDigit(b.tracklc.charAt(0)))) {
              String numtracklc = new String("");
              int index = 0;
              while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
              index = 0;
              String bnumtracklc = new String("");
              while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
              int num1 = Integer.parseInt(numtracklc);
              int num2 = Integer.parseInt(bnumtracklc);
              if (num1 < num2) cmp = -1;
              else if (num1 > num2) cmp = 1;
            } else if (((tracklc.length() > 1) && Character.isDigit(tracklc.charAt(1))) && ((b.tracklc.length() > 1) && Character.isDigit(b.tracklc.charAt(1)))) {
              String numtracklc = new String("");
              int index = 1;
              while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
              index = 1;
              String bnumtracklc = new String("");
              while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
              int num1 = Integer.parseInt(numtracklc);
              int num2 = Integer.parseInt(bnumtracklc);
              if (num1 < num2) cmp = -1;
              else if (num1 > num2) cmp = 1;
            } else {
              if ((tracklc.compareTo(b.tracklc)) < 0) cmp = -1;
              else if ((tracklc.compareTo(b.tracklc)) > 0) cmp = 1;
            }
            if (cmp < 0) return -1;
            else if (cmp > 0) return 1;
            else {
              if (OptionsUI.instance.songdisplaysongname.isSelected()) {
                if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
                if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
                if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
                else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
                else {
                  if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                    if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                    if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                    if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                    else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                    else return 0;
                  } else return 0;
                }
              } else {
                if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                  if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                  if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                  if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                  else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                  else return 0;
                } else return 0;
              }
            }
          } else {
            if (OptionsUI.instance.songdisplaysongname.isSelected()) {
              if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
              if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
              if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
              else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
              else {
                if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                  if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                  if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                  if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                  else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                  else return 0;
                } else return 0;
              }
            } else {
              if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                else return 0;
              } else return 0;
            }
          }
        }
      }
    } else {
      if (OptionsUI.instance.songdisplayalbum.isSelected()) {
        if (b.albumlc.equals("") && !albumlc.equals("")) return -1;
        if (albumlc.equals("") && !b.albumlc.equals("")) return 1;
        if ((albumlc.compareTo(b.albumlc)) < 0) return -1;
        else if ((albumlc.compareTo(b.albumlc)) > 0) return 1;
        else {
          if (OptionsUI.instance.songdisplaytrack.isSelected()) {
            int cmp = 0;
            if (b.tracklc.equals("") && !tracklc.equals("")) cmp = -1;
            else if (tracklc.equals("") && !b.tracklc.equals("")) cmp = 1;
            else if (((tracklc.length() > 0) && Character.isDigit(tracklc.charAt(0))) && ((b.tracklc.length() > 0) && Character.isDigit(b.tracklc.charAt(0)))) {
              String numtracklc = new String("");
              int index = 0;
              while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
              index = 0;
              String bnumtracklc = new String("");
              while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
              int num1 = Integer.parseInt(numtracklc);
              int num2 = Integer.parseInt(bnumtracklc);
              if (num1 < num2) cmp = -1;
              else if (num1 > num2) cmp = 1;
            } else if (((tracklc.length() > 1) && Character.isDigit(tracklc.charAt(1))) && ((b.tracklc.length() > 1) && Character.isDigit(b.tracklc.charAt(1)))) {
              String numtracklc = new String("");
              int index = 1;
              while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
              index = 1;
              String bnumtracklc = new String("");
              while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
              int num1 = Integer.parseInt(numtracklc);
              int num2 = Integer.parseInt(bnumtracklc);
              if (num1 < num2) cmp = -1;
              else if (num1 > num2) cmp = 1;
            } else {
              if ((tracklc.compareTo(b.tracklc)) < 0) cmp = -1;
              else if ((tracklc.compareTo(b.tracklc)) > 0) cmp = 1;
            }
            if (cmp < 0) return -1;
            else if (cmp > 0) return 1;
            else {
              if (OptionsUI.instance.songdisplaysongname.isSelected()) {
                if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
                if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
                if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
                else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
                else {
                  if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                    if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                    if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                    if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                    else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                    else return 0;
                  } else return 0;
                }
              } else {
                if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                  if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                  if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                  if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                  else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                  else return 0;
                } else return 0;
              }
            }
          } else {
            if (OptionsUI.instance.songdisplaysongname.isSelected()) {
              if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
              if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
              if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
              else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
              else {
                if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                  if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                  if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                  if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                  else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                  else return 0;
                } else return 0;
              }
            } else {
              if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                else return 0;
              } else return 0;
            }
          }
        }
      } else {
        if (OptionsUI.instance.songdisplaytrack.isSelected()) {
          int cmp = 0;
          if (b.tracklc.equals("") && !tracklc.equals("")) cmp = -1;
          else if (tracklc.equals("") && !b.tracklc.equals("")) cmp = 1;
          else if (((tracklc.length() > 0) && Character.isDigit(tracklc.charAt(0))) && ((b.tracklc.length() > 0) && Character.isDigit(b.tracklc.charAt(0)))) {
            String numtracklc = new String("");
            int index = 0;
            while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
            index = 0;
            String bnumtracklc = new String("");
            while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
            int num1 = Integer.parseInt(numtracklc);
            int num2 = Integer.parseInt(bnumtracklc);
            if (num1 < num2) cmp = -1;
            else if (num1 > num2) cmp = 1;
          } else if (((tracklc.length() > 1) && Character.isDigit(tracklc.charAt(1))) && ((b.tracklc.length() > 1) && Character.isDigit(b.tracklc.charAt(1)))) {
            String numtracklc = new String("");
            int index = 1;
            while ((index < tracklc.length()) && Character.isDigit(tracklc.charAt(index))) numtracklc += tracklc.charAt(index++);
            index = 1;
            String bnumtracklc = new String("");
            while ((index < b.tracklc.length()) && Character.isDigit(b.tracklc.charAt(index))) bnumtracklc += b.tracklc.charAt(index++);
            int num1 = Integer.parseInt(numtracklc);
            int num2 = Integer.parseInt(bnumtracklc);
            if (num1 < num2) cmp = -1;
            else if (num1 > num2) cmp = 1;
          } else {
            if ((tracklc.compareTo(b.tracklc)) < 0) cmp = -1;
            else if ((tracklc.compareTo(b.tracklc)) > 0) cmp = 1;
          }
          if (cmp < 0) return -1;
          else if (cmp > 0) return 1;
          else {
            if (OptionsUI.instance.songdisplaysongname.isSelected()) {
              if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
              if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
              if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
              else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
              else {
                if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                  if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                  if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                  if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                  else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                  else return 0;
                } else return 0;
              }
            } else {
              if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                else return 0;
              } else return 0;
            }
          }
        } else {
          if (OptionsUI.instance.songdisplaysongname.isSelected()) {
            if (b.songnamelc.equals("") && !songnamelc.equals("")) return -1;
            if (songnamelc.equals("") && !b.songnamelc.equals("")) return 1;
            if ((songnamelc.compareTo(b.songnamelc)) < 0) return -1;
            else if ((songnamelc.compareTo(b.songnamelc)) > 0) return 1;
            else {
              if (OptionsUI.instance.songdisplayremixer.isSelected()) {
                if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
                if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
                if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
                else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
                else return 0;
              } else return 0;
            }
          } else {
            if (OptionsUI.instance.songdisplayremixer.isSelected()) {
              if (b.remixerlc.equals("") && !remixerlc.equals("")) return -1;
              if (remixerlc.equals("") && !b.remixerlc.equals("")) return 1;
              if ((remixerlc.compareTo(b.remixerlc)) < 0) return -1;
              else if ((remixerlc.compareTo(b.remixerlc)) > 0) return 1;
              else return 0;
            } else return 0;
          }
        }
      }
    }
  }
  public int compareTo(Object b1) {
    MySortableSong b = (MySortableSong)b1;
    return compareTo(b);
  }
  public boolean equals(MySortableSong b) {
    if (this.compareTo(b) == 0) return true;
    return false;
  }
  public String toString() { if (useinfo) return song.getSongId(); else return song.getSongIdShort(); }
  SongLinkedList song;
  String artistlc;
  String albumlc;
  String tracklc;
  String songnamelc;
  String remixerlc;
  boolean useinfo;
};

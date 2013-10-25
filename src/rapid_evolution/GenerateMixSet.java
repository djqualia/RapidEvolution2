package rapid_evolution;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.PaceMaker;

import rapid_evolution.SearchParser;
import rapid_evolution.SongStack;
import rapid_evolution.ui.MixGeneratorUI;
import rapid_evolution.ui.RapidEvolutionUI;

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

public class GenerateMixSet extends Thread {

    private static Logger log = Logger.getLogger(GenerateMixSet.class);
    
    public GenerateMixSet() {
    	setPriority(Thread.NORM_PRIORITY - 1);
    }
    
  public void run() {
    float[] rankings = new float[MixGeneratorUI.instance.max_mixes + 1];
    try {
      if (MixGeneratorUI.instance.waitingtocancel)
        throw new Exception();;
      int i;
      int minrank = 50;
      try { MixGeneratorUI.instance.numsongs = Integer.parseInt(MixGeneratorUI.instance.numsongsmixsetfield.getText()); }
      catch (Exception e) { }
      try { minrank = Integer.parseInt(MixGeneratorUI.instance.minrankfield.getText()); }
      catch (Exception e) { }
      if (!MixGeneratorUI.instance.generated) {
        SearchParser includetokens = new SearchParser(MixGeneratorUI.instance.includekeywordsfield.
            getText());
        SearchParser excludetokens = new SearchParser(MixGeneratorUI.instance.excludekeywordsfield.
            getText());
        SongLinkedList[] tempmix = new SongLinkedList[MixGeneratorUI.instance.numsongs];
        for (i = 0; i < MixGeneratorUI.instance.numsongs; ++i)
          tempmix[i] = null;
        int[] mixoutused = new int[MixGeneratorUI.instance.numsongs - 1];
        int[] toleranceused = new int[MixGeneratorUI.instance.numsongs];
        for (i = 0; i < MixGeneratorUI.instance.numsongs - 1; ++i) {
          mixoutused[i] = 0;
          toleranceused[i] = 0;
        }
        toleranceused[MixGeneratorUI.instance.numsongs - 1] = 0;
        MixGeneratorUI.instance.mixes = new SongLinkedList[MixGeneratorUI.instance.max_mixes + 1][];
        MixGeneratorUI.instance.num_mixes = 0;
        int tolerance = 0;
        int maxtolerated = 4 + (MixGeneratorUI.instance.numsongs - 1) * 7 + MixGeneratorUI.instance.numsongs;
        int tolerated;
        boolean runonce = true;
        while (((MixGeneratorUI.instance.num_mixes == 0) && MixGeneratorUI.instance.closestmatchcheckbox.isSelected() && (tolerance < maxtolerated)) || runonce) {
          SongLinkedList iter = SongDB.instance.SongLL;
          while ( (iter != null) && (MixGeneratorUI.instance.num_mixes <= MixGeneratorUI.instance.max_mixes) && MixGeneratorUI.instance.isgenerating) {
            if (MixGeneratorUI.instance.waitingtocancel)
              throw new Exception();
            if (! (MixGeneratorUI.instance.usestartswith && (iter != MixGeneratorUI.instance.startswith))) {
              tolerated = 0;
              toleranceused[0] = 0;
              boolean correctsongtype = true;
              if (MixGeneratorUI.instance.usenonvinylrb.isSelected() && iter.getVinylOnly())
                correctsongtype = false;
              if (MixGeneratorUI.instance.usevinylrb.isSelected() && iter.getNonVinylOnly())
                correctsongtype = false;
              if (iter.isDisabled())
                correctsongtype = false;
              boolean valid = ( (includetokens.num_rows > 0) &&
                               !MixGeneratorUI.instance.weakincludecb.isSelected()) ? false : true;
              if (correctsongtype) {
                if (!MixGeneratorUI.instance.weakincludecb.isSelected()) {
                  if (includetokens.getStatus(iter.getSongIdShort()) ||
                      includetokens.getStatus(iter.getComments()))
                    valid = true;
                  if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
                        (tolerated < tolerance)) && !valid) {
                    toleranceused[0]++;
                    valid = true;
                    ++tolerated;
                  }
                  if (valid && !excludetokens.isEmpty())
                    if (excludetokens.getStatus(iter.getSongIdShort()) ||
                        excludetokens.getStatus(iter.getComments()))
                      valid = false;
                  if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
                        (tolerated < tolerance)) && !valid) {
                    toleranceused[0]++;
                    valid = true;
                    ++tolerated;
                  }
                  if (valid && MixGeneratorUI.instance.avoidsongtrailcheckbox.isSelected() &&
                      (iter != MixGeneratorUI.instance.startswith)) {
                    SongStack stackiter = RapidEvolutionUI.instance.prevstack;
                    while ( (stackiter != null) && valid) {
                      if (stackiter.songid == iter.uniquesongid)
                        valid = false;
                      stackiter = stackiter.next;
                    }
                    if ( (RapidEvolutionUI.instance.currentsong != null) && (RapidEvolutionUI.instance.currentsong == iter))
                      valid = false;
                    stackiter = RapidEvolutionUI.instance.nextstack;
                    while ( (stackiter != null) && valid) {
                      if (stackiter.songid == iter.uniquesongid)
                        valid = false;
                      stackiter = stackiter.next;
                    }
                    if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
                          (tolerated < tolerance)) && !valid) {
                      toleranceused[0]++;
                      valid = true;
                      ++tolerated;
                    }
                  }
                  if (valid && !MixGeneratorUI.instance.mixsetstylestree.isSelectionEmpty()) {
                    valid = false;
                    if (MixGeneratorUI.instance.isMemberOfMixStyle(iter))
                      valid = true;
                  }
                  if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
                        (tolerated < tolerance)) && !valid) {
                    toleranceused[0]++;
                    valid = true;
                    ++tolerated;
                  }
                }
              }
              else
                valid = false;
              if (valid) {
                tempmix[0] = iter;
                int num_songs = 1;
                for (i = 0; i < MixGeneratorUI.instance.numsongs - 1; ++i)
                  mixoutused[i] = 0;
                boolean stillsearching = true;
        	      PaceMaker pacer = new PaceMaker();
                while (stillsearching && MixGeneratorUI.instance.isgenerating) {                    
                    if (MixGeneratorUI.instance.waitingtocancel) throw new Exception();
                  pacer.startInterval();
                  try {
	                  if (mixoutused[num_songs - 1] <
	                      tempmix[num_songs - 1].getNumMixoutSongs()) {
	                    valid = true;
	                    for (i = 0; i < num_songs; ++i) {
	                      if (tempmix[i].uniquesongid == tempmix[num_songs -
	                          1].mixout_songs[mixoutused[num_songs - 1]])
	                        valid = false;
	                    }
	                    if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                          (tolerated < tolerance)) && !valid) {
	                      toleranceused[num_songs]++;
	                      valid = true;
	                      ++tolerated;
	                    }
	                    if (valid) {
	                      boolean notdone = true;
	                      SongLinkedList siter = SongDB.instance.NewGetSongPtr(tempmix[num_songs -
	                          1].mixout_songs[mixoutused[num_songs - 1]]);
	                      if (siter != null) {
	                        valid = ( (includetokens.num_rows > 0) &&
	                                 !MixGeneratorUI.instance.weakincludecb.isSelected()) ? false : true;
	                        if (!MixGeneratorUI.instance.weakincludecb.isSelected())
	                          if (includetokens.getStatus(siter.getSongIdShort()) ||
	                              includetokens.getStatus(siter.getComments()))
	                            valid = true;
	                        if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                              (tolerated < tolerance)) && !valid) {
	                          toleranceused[num_songs]++;
	                          valid = true;
	                          ++tolerated;
	                        }
	                        if (valid && !excludetokens.isEmpty())
	                          if (excludetokens.getStatus(siter.getSongIdShort()) ||
	                              excludetokens.getStatus(siter.getComments()))
	                            valid = false;
	                        if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                              (tolerated < tolerance)) && !valid) {
	                          toleranceused[num_songs]++;
	                          valid = true;
	                          ++tolerated;
	                        }
	                        if (valid && MixGeneratorUI.instance.avoidsongtrailcheckbox.isSelected()) {
	                          SongStack stackiter = RapidEvolutionUI.instance.prevstack;
	                          while ( (stackiter != null) && valid) {
	                            if (stackiter.songid == siter.uniquesongid)
	                              valid = false;
	                            stackiter = stackiter.next;
	                          }
	                          if ( (RapidEvolutionUI.instance.currentsong != null) && (RapidEvolutionUI.instance.currentsong == siter))
	                            valid = false;
	                          stackiter = RapidEvolutionUI.instance.nextstack;
	                          while ( (stackiter != null) && valid) {
	                            if (stackiter.songid == siter.uniquesongid)
	                              valid = false;
	                            stackiter = stackiter.next;
	                          }
	                          if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                                (tolerated < tolerance)) && !valid) {
	                            toleranceused[num_songs]++;
	                            valid = true;
	                            ++tolerated;
	                          }
	                        }
	                        if (valid && !MixGeneratorUI.instance.mixsetstylestree.isSelectionEmpty()) {
	                          valid = false;
	                          if (MixGeneratorUI.instance.isMemberOfMixStyle(siter))
	                            valid = true;
	                        }
	                        if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                              (tolerated < tolerance)) && !valid) {
	                          toleranceused[num_songs]++;
	                          valid = true;
	                          ++tolerated;
	                        }
	                        if (MixGeneratorUI.instance.minrankcb.isSelected()) {
	                          boolean val = (tempmix[num_songs -
	                                         1].getMixoutRank(mixoutused[num_songs -
	                                         1]) >= minrank);
	                          if (!val)
	                            valid = false;
	                        }
	                        if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                              (tolerated < tolerance)) && !valid) {
	                          toleranceused[num_songs]++;
	                          valid = true;
	                          ++tolerated;
	                        }
	                        if (MixGeneratorUI.instance.usenonvinylrb.isSelected() && siter.getVinylOnly())
	                          valid = false;
	                        if (MixGeneratorUI.instance.usevinylrb.isSelected() && siter.getNonVinylOnly())
	                          valid = false;
	                        if (siter.isDisabled())
	                          valid = false;
	                        if (valid &&
	                            !tempmix[num_songs -
	                            1]._mixout_addons[mixoutused[num_songs - 1]]) {
	                          tempmix[num_songs] = siter;
	                          mixoutused[num_songs - 1]++;
	                          ++num_songs;
	                          if ( (num_songs == MixGeneratorUI.instance.numsongs) &&
	                              (MixGeneratorUI.instance.num_mixes <= MixGeneratorUI.instance.max_mixes)) {
	                            if (MixGeneratorUI.instance.weakincludecb.isSelected()) {
	                              boolean[] used_includes = new boolean[
	                                  includetokens.num_rows];
	                              for (i = 0; i < includetokens.num_rows; ++i) {
	                                used_includes[i] = false;
	                                for (int z = 0; z < MixGeneratorUI.instance.numsongs; ++z) {
	                                  if (includetokens.getRowStatus(tempmix[z].
	                                      getSongId(), i) ||
	                                      includetokens.getRowStatus(tempmix[z].
	                                      getComments(), i))
	                                    used_includes[i] = true;
	                                  if (!used_includes[i] && (z < MixGeneratorUI.instance.numsongs - 1)) {
	                                    for (int a = 0;
	                                         a < tempmix[z].getNumMixoutSongs();
	                                         ++a) {
	                                      if (tempmix[z].mixout_songs[a] ==
	                                          tempmix[z + 1].uniquesongid) {
	                                        if (includetokens.getRowStatus(
	                                            tempmix[
	                                            z]._mixout_comments[a], i))
	                                          used_includes[i] = true;
	                                      }
	                                    }
	                                  }
	                                }
	                                if (!used_includes[i])
	                                  valid = false;
	                                if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                                      (tolerated < tolerance)) && !valid) {
	                                  toleranceused[num_songs - 1]++;
	                                  valid = true;
	                                  ++tolerated;
	                                }
	                              }
	                            }
	                            if (MixGeneratorUI.instance.controlbpmrangecb.isSelected()) {
	                              float netbpmdiff = 0.0f;
	                              float compensation = 2.0f;
	                              float scale;
	                              for (int z = 0; z < MixGeneratorUI.instance.numsongs - 1; ++z) {
	                                scale = 1.0f;
	                                if ( (tempmix[z].getTime() != null) &&
	                                    !tempmix[z].getTime().equals("")) {
	                                  String data = tempmix[z].getTime();
	                                  float seconds = 0;
	                                  int index1 = 0;
	                                  while (Character.isDigit(data.charAt(index1)))
	                                    index1++;
	                                  int minutes1 = 0;
	                                  try {
	                                    minutes1 = Integer.parseInt(data.substring(
	                                        0, index1));
	                                  }
	                                  catch (Exception e) {}
	                                  int index2 = index1 + 1;
	                                  try {
	                                    while ( (index2 < data.length()) &&
	                                           Character.isDigit(data.charAt(
	                                        index2)))
	                                      index2++;
	                                    seconds = Integer.parseInt(data.substring(
	                                        index1 + 1, index2)) + minutes1 * 60;
	                                  }
	                                  catch (Exception e) {}
	                                  if (seconds != 0)
	                                    scale = seconds / 240.0f;
	                                }
	                                for (int a = 0; a < tempmix[z].getNumMixoutSongs();
	                                     ++a) {
	                                  if (tempmix[z].mixout_songs[a] ==
	                                      tempmix[
	                                      z + 1].uniquesongid) {
	                                    netbpmdiff += tempmix[z]._mixout_bpmdiff[a];
	                                    if (netbpmdiff > 0) {
	                                      netbpmdiff -= compensation * scale;
	                                      if (netbpmdiff < 0)
	                                        netbpmdiff = 0;
	                                    }
	                                    else {
	                                      netbpmdiff += compensation * scale;
	                                      if (netbpmdiff > 0)
	                                        netbpmdiff = 0;
	                                    }
	                                    if (Math.abs(netbpmdiff) > RapidEvolutionUI.instance.bpmscale)
	                                      valid = false;
	                                    if ( (MixGeneratorUI.instance.closestmatchcheckbox.isSelected() &&
	                                          (tolerated < tolerance)) && !valid) {
	                                      toleranceused[num_songs - 1]++;
	                                      valid = true;
	                                      ++tolerated;
	                                    }
	                                  }
	                                }
	                              }
	                            }
	                            if (valid) {
	                              if (MixGeneratorUI.instance.num_mixes < MixGeneratorUI.instance.max_mixes) {
	                                if (MixGeneratorUI.instance.mixes != null)
	                                  MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.num_mixes] = new SongLinkedList[
	                                      MixGeneratorUI.instance.numsongs];
	                                else
	                                  throw new Exception();
	                                ;
	                                for (i = 0; i < MixGeneratorUI.instance.numsongs; ++i)
	                                  MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.num_mixes][i] = tempmix[i];
	                                rankings[MixGeneratorUI.instance.num_mixes] = RankMix(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.num_mixes],
	                                    MixGeneratorUI.instance.numsongs);
	                                ++MixGeneratorUI.instance.num_mixes;
	                              }
	                              else {
	                                MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.num_mixes] = new SongLinkedList[
	                                    MixGeneratorUI.instance.numsongs];
	                                for (i = 0; i < MixGeneratorUI.instance.numsongs; ++i)
	                                  MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.num_mixes][i] = tempmix[i];
	                                rankings[MixGeneratorUI.instance.num_mixes] = RankMix(MixGeneratorUI.instance.mixes[MixGeneratorUI.instance.num_mixes],
	                                    MixGeneratorUI.instance.numsongs);
	                                if (MixGeneratorUI.instance.mixes == null)
	                                  throw new Exception();
	                                ;
	                                // replace lowest mix here
	                                boolean foundlowest = false;
	                                int lowestindex = -1;
	                                float lowestrank = 100;
	                                for (i = 0; i <= MixGeneratorUI.instance.num_mixes; ++i) {
	                                  if (!foundlowest) {
	                                    foundlowest = true;
	                                    lowestindex = i;
	                                    lowestrank = rankings[i];
	                                  }
	                                  else if (rankings[i] <= lowestrank) {
	                                    lowestindex = i;
	                                    lowestrank = rankings[i];
	                                  }
	                                }
	                                if (MixGeneratorUI.instance.mixes == null)throw new Exception();
	                                ;
	                                if ( (lowestindex < MixGeneratorUI.instance.max_mixes) &&
	                                    (lowestindex >= 0)) {
	                                  MixGeneratorUI.instance.mixes[lowestindex] = new SongLinkedList[
	                                      MixGeneratorUI.instance.numsongs];
	                                  for (i = 0; i < MixGeneratorUI.instance.numsongs; ++i)
	                                    MixGeneratorUI.instance.mixes[lowestindex][i] = tempmix[i];
	                                  rankings[lowestindex] = RankMix(MixGeneratorUI.instance.mixes[
	                                      lowestindex], MixGeneratorUI.instance.numsongs);
	                                }
	                              }
	                            }
	                          }
	                          if (num_songs != MixGeneratorUI.instance.numsongs)
	                            mixoutused[num_songs - 1] = 0;
	                          if ( (siter.getNumMixoutSongs() == 0) ||
	                              (num_songs == MixGeneratorUI.instance.numsongs)) {
	                            num_songs--;
	                            tolerated -= toleranceused[num_songs];
	                            toleranceused[num_songs] = 0;
	                            //mixoutused[num_songs - 1]++;
	                            if (num_songs == 0)
	                              stillsearching = false;
	                          }
	                        }
	                        else {
	                          mixoutused[num_songs - 1]++;
	                          tolerated -= toleranceused[num_songs];
	                          toleranceused[num_songs] = 0;
	                        }
	                      }
	                      else {
	                        mixoutused[num_songs - 1]++;
	                        tolerated -= toleranceused[num_songs];
	                        toleranceused[num_songs] = 0;
	                      }
	                    }
	                    else {
	                      mixoutused[num_songs - 1]++;
	                      tolerated -= toleranceused[num_songs];
	                      toleranceused[num_songs] = 0;
	                    }
	                  }
	                  else {
	                    mixoutused[num_songs - 1]++;
	                    tolerated -= toleranceused[num_songs];
	                    toleranceused[num_songs] = 0;
	                    num_songs--;
	                    if (num_songs == 0)
	                      stillsearching = false;
	                  }
                  } catch (Exception e) {
                      log.error("run(): error", e);
                  }
                  pacer.endInterval();
                }
              }
              iter = iter.next;
              if (MixGeneratorUI.instance.waitingtocancel)
                throw new Exception();
              ;
            }
            else iter = iter.next;
          }
          tolerance++;
          runonce = false;
        }
      }
    } catch (Exception  e) { if (!MixGeneratorUI.instance.waitingtocancel) log.error("run(): error", e); }
    if (MixGeneratorUI.instance.num_mixes > 1) {
      try {
        SortMixes(MixGeneratorUI.instance.mixes, rankings, 0, MixGeneratorUI.instance.num_mixes - 1, MixGeneratorUI.instance.numsongs);
      } catch (Exception e) { }
    }
    MixGeneratorUI.instance.generated = true;
    if (MixGeneratorUI.instance.num_mixes > 1) {
      MixGeneratorUI.instance.nextmixsetbutton.setEnabled(true);
      MixGeneratorUI.instance.fastnextmixsetbutton.setEnabled(true);
    }
    MixGeneratorUI.instance.DisplayMix(0);
    if (MixGeneratorUI.instance.num_mixes > 0) {
      MixGeneratorUI.instance.loadmixsetbutton.setEnabled(true);
      MixGeneratorUI.instance.savetexttrail.setEnabled(true);
      MixGeneratorUI.instance.savemixsetbutton.setEnabled(true);
      MixGeneratorUI.instance.appendtosongtrail.setEnabled(true);
      MixGeneratorUI.instance.exporttosongtrail.setEnabled(true);
      if (RapidEvolutionUI.instance.currentsong != null)
        MixGeneratorUI.instance.appendtosongtrail.setEnabled(true);
    }
    MixGeneratorUI.instance.isgenerating = false;

    MixGeneratorUI.instance.EnableMixGenUI();

    MixGeneratorUI.instance.waitingtocancel = false;
  }

  static float RankMix(SongLinkedList[] mix, int num_songs) {
    float score = 0.0f;
    for (int i = 0; i < num_songs; ++i) {
      if ((i + 1) < num_songs) {
        int z = 0;
        boolean notfound = true;
        while ((z < mix[i].getNumMixoutSongs()) && notfound) {
          if (mix[i].mixout_songs[z] == mix[i + 1].uniquesongid) {
            notfound = false;
            score += mix[i].getMixoutRank(z);
          }
          ++z;
        }
      }
    }
    return score;
  }

  static void SortMixes(SongLinkedList[] [] mix, float[] rankings, int startpos, int endpos, int num_songs) {
    int pivotpos = startpos;
    int original = startpos;
    ++startpos;
    while (startpos <= endpos) {
      if (rankings[pivotpos] < rankings[startpos]) {
        if ((startpos - pivotpos) == 1) {
          SongLinkedList[] tempmix = mix[pivotpos];
          float tempranking = rankings[pivotpos];
          mix[pivotpos] = mix[startpos];
          rankings[pivotpos] = rankings[startpos];
          mix[startpos] = tempmix;
          rankings[startpos] = tempranking;
          ++pivotpos;
          ++startpos;
        } else {
          SongLinkedList[] tempmix = mix[pivotpos + 1];
          float tempranking = rankings[pivotpos + 1];
          SongLinkedList[] tempmix2 = mix[pivotpos];
          float tempranking2 = rankings[pivotpos];
          mix[pivotpos] = mix[startpos];
          rankings[pivotpos] = rankings[startpos];
          mix[startpos] = tempmix;
          rankings[startpos] = tempranking;
          mix[pivotpos + 1] = tempmix2;
          rankings[pivotpos + 1] = tempranking2;
          ++pivotpos;
          ++startpos;
        }
      } else ++startpos;
    }
    if ((pivotpos - original - 1) >= 1) SortMixes(MixGeneratorUI.instance.mixes, rankings, original, pivotpos - 1, num_songs);
    if ((endpos - pivotpos - 1) >= 1) SortMixes(MixGeneratorUI.instance.mixes, rankings, pivotpos + 1, endpos, num_songs);
  }
}

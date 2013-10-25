package rapid_evolution.ui.main;

import rapid_evolution.SongLinkedList;
import rapid_evolution.SongStack;
import rapid_evolution.SongUtil;
import rapid_evolution.RapidEvolution;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.music.KeyRelation;
import com.mixshare.rapid_evolution.music.SongKeyRelation;
import rapid_evolution.net.GetNewServerMixesThread;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SyncUI;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.SkinManager;

/**
 * Since so many UI updates are made when changing the current song, this thread is invoked with SwingUtilities.invokeLater
 * for thread safety with UI events...
 */
public class changecurrentsongthread extends Thread {

    private static Logger log = Logger.getLogger(changecurrentsongthread.class);
    
    static Semaphore ChangeCurrentSongSem = new Semaphore(1);
  SongLinkedList newsong;
  float bpmdiff;
  boolean usebpmdiff;
  boolean dontchangesongtrail;
  public changecurrentsongthread(SongLinkedList in_newsong, float in_bpmdiff, boolean in_usebpmdiff, boolean in_dontchangesongtrail) {
    newsong = in_newsong;
    bpmdiff = in_bpmdiff;
    usebpmdiff = in_usebpmdiff;
    dontchangesongtrail = in_dontchangesongtrail;
  }
  public static boolean ischangingcurrentsong = false;
  public void run() {
//      if (RapidEvolution.debugmode) System.out.println("ChangeCurrentSongThread - Start");
      if (RapidEvolutionUI.instance.currentsong == newsong) return; 
      try {
          ChangeCurrentSongSem.acquire();
                    
          boolean dontincrementplaycount = false;
          ischangingcurrentsong = true;
    
          if ((!dontchangesongtrail) && (RapidEvolutionUI.instance.currentsong != null)) {
              if (RapidEvolutionUI.instance.nextstack != null) {
                  if (RapidEvolutionUI.instance.nextstack.songid == newsong.uniquesongid) {
                      dontincrementplaycount = true;
                      RapidEvolutionUI.instance.nextstack = RapidEvolutionUI.instance.nextstack.next;
                  } else {
                      RapidEvolutionUI.instance.nextstack = null;
                      
                  }
              }
              RapidEvolutionUI.instance.prevstack = new SongStack(RapidEvolutionUI.instance.currentsong.uniquesongid, RapidEvolutionUI.instance.prevstack);
              RapidEvolutionUI.instance.backbutton.setEnabled(true);
          }
          if ((RapidEvolutionUI.instance.currentsong != null) && (newsong.getStartbpm() != 0.0)) {
              float old_bpm = RapidEvolution.instance.getActualBpm();
              if (newsong.getEndbpm() != 0.0) RapidEvolution.instance.setCurrentBpm( newsong.getEndbpm());
              else if (newsong.getStartbpm() != 0.0) RapidEvolution.instance.setCurrentBpm( newsong.getStartbpm());
              if (!usebpmdiff) bpmdiff = SongUtil.get_bpmdiff(newsong.getStartbpm(), old_bpm);
              if (!OptionsUI.instance.lockpitchshift.isSelected()) {
                  SearchPane.instance.bpmslider.setValue((int)(-bpmdiff * 100.0f));
                  //RapidEvolution.instance.setActualBpm( ( ( (float) - SearchPane.instance.bpmslider.getValue() / 10000.0f) + 1.0f) * RapidEvolution.instance.getCurrentBpm());
                  SearchPane.instance.showSliderShiftValue();
              } else RapidEvolution.instance.setActualBpm( ( ( (float) - SearchPane.instance.bpmslider.getValue() / 10000.0f) + 1.0f) * RapidEvolution.instance.getCurrentBpm());
              String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
              int length = bpmtext.length();
              if (length > 6) length = 6;
              SearchPane.instance.bpmfield.setText(bpmtext.substring(0,length));
              if (!OptionsUI.instance.disablekeylockfunctionality.isSelected()) {
                  SongKeyRelation relation = Key.getClosestKeyRelation(newsong, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey());
                  if (relation.isCompatible()) {
                      if (relation.isBestRelationWithKeylock()) RapidEvolutionUI.instance.keylockcurrentsong.setSelected(true);
                      else RapidEvolutionUI.instance.keylockcurrentsong.setSelected(false);
                  }
              }
          } else {
              if (newsong.getEndbpm() != 0.0) RapidEvolution.instance.setCurrentBpm( newsong.getEndbpm());
              else if (newsong.getStartbpm() != 0.0) RapidEvolution.instance.setCurrentBpm( newsong.getStartbpm());
              if (RapidEvolution.instance.getCurrentBpm() != 0.0) {
                  RapidEvolution.instance.setActualBpm( ( ( (float) - SearchPane.instance.bpmslider.getValue() / 10000.0f) + 1.0f) * RapidEvolution.instance.getCurrentBpm());
                  String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
                  int length = bpmtext.length();
                  if (length > 6) length = 6;
                  SearchPane.instance.bpmfield.setText(bpmtext.substring(0,length));
              }
          }
          RapidEvolutionUI.instance.currentsong = newsong;
          if ((RapidEvolutionUI.instance.currentsong.next != null) || (RapidEvolutionUI.instance.nextstack != null)) RapidEvolutionUI.instance.nextbutton.setEnabled(true);
          else RapidEvolutionUI.instance.nextbutton.setEnabled(false);
          RapidEvolutionUI.instance.currentsongfield.setText(RapidEvolutionUI.instance.currentsong.toString());
          if (RapidEvolutionUI.instance.prevstack != null) {
              RapidEvolutionUI.instance.previoussong = SongDB.instance.NewGetSongPtr(RapidEvolutionUI.instance.prevstack.songid);
              RapidEvolutionUI.instance.previoussongfield.setText(RapidEvolutionUI.instance.previoussong.toString());
          } else {
              RapidEvolutionUI.instance.previoussong = null;
              RapidEvolutionUI.instance.previoussongfield.setText("");
          }
          RapidEvolutionUI.instance.setRatingFromCurrent();
          SearchListMouse.instance.findlinkselection.setEnabled(true);
          RapidEvolutionUI.instance.editcurrentsongbutton.setEnabled(true);
          SkinManager.instance.setEnabled("current_song_sync_button", true);
          SkinManager.instance.setEnabled("view_excludes_button", true);
          RapidEvolutionUI.instance.addexcludebutton.setEnabled(false);
          SyncUI.instance.addedsyncsongs = new Vector();
          RapidEvolutionUI.instance.keylockcurrentsong.setEnabled(true);
          if (RapidEvolutionUI.instance.currentsong.getStartKey().isValid() || RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) {
              if ((RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) || (RapidEvolutionUI.instance.currentsong.getStartbpm() != 0)) {
                  Key fromkey = RapidEvolutionUI.instance.currentsong.getStartKey();
                  if (RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) fromkey = RapidEvolutionUI.instance.currentsong.getEndKey();
                  Key fkey = fromkey;
                  float frombpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
                  if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) frombpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
                  float fromdiff = SongUtil.get_bpmdiff(frombpm, RapidEvolution.instance.getActualBpm());
                  if (!RapidEvolutionUI.instance.keylockcurrentsong.isSelected())
                      RapidEvolutionUI.instance.setCurrentKey(fkey.getShiftedKeyByBpmDifference(fromdiff));
                  else
                      RapidEvolutionUI.instance.setCurrentKey(fkey);
                      
                  SearchPane.instance.keyfield.setText(RapidEvolutionUI.instance.getCurrentKey().toString());
                  SearchPane.instance.keysearchbutton.setEnabled(true);
              } else SearchPane.instance.keysearchbutton.setEnabled(false);
          } else {
              RapidEvolutionUI.instance.setCurrentKey(null);
              SearchPane.instance.keyfield.setText("");
              SearchPane.instance.keysearchbutton.setEnabled(false);
          }
          if ((RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) || (RapidEvolutionUI.instance.currentsong.getStartbpm() != 0)) SearchPane.instance.bpmsearchbutton.setEnabled(true);
          else SearchPane.instance.bpmsearchbutton.setEnabled(false);
          if ((RapidEvolutionUI.instance.currentsong.next == null) && (RapidEvolutionUI.instance.nextstack == null)) RapidEvolutionUI.instance.nextbutton.setEnabled(false);
          else RapidEvolutionUI.instance.nextbutton.setEnabled(true);
          if (RapidEvolutionUI.instance.prevstack != null) RapidEvolutionUI.instance.backbutton.setEnabled(true);
          else RapidEvolutionUI.instance.backbutton.setEnabled(false);
          SkinManager.instance.setEnabled("current_song_mixes_button", false);
          SkinManager.instance.setEnabled("add_mixout_button", false);
          MixoutPane.instance.addoncheckbox.setSelected(false);
          MixoutPane.instance.addoncheckbox.setEnabled(false);
          MixoutPane.instance.mixoutcomments.setEnabled(false);
          MixoutPane.instance.mixoutcomments.setText("");
          MixoutPane.instance.mixoutscore.setEnabled(false);
          MixoutPane.instance.mixoutscore.setText("");
          MixoutPane.instance.bpmdifffield.setEnabled(false);
          MixoutPane.instance.bpmdifffield.setText("");
          MixoutPane.instance.calculatebpmdiffbutton.setEnabled(false);
          MixoutPane.instance.mixoutcommentslabel.setEnabled(false);
          MixoutPane.instance.scorefield.setEnabled(false);
          MixoutPane.instance.bpmdifflabel.setEnabled(false);
          if (OptionsUI.instance.clearsearchautomatically.isSelected()) SearchPane.instance.searchfield.setText(new String(""));
          MixoutPane.instance.RedrawMixoutTable();
          RapidEvolutionUI.instance.changecurrentroutine();
          RapidEvolutionUI.instance.UpdateRoutine(true);          
          if (OptionsUI.instance.autobpmkeysearch.isSelected()) {
              if (RapidEvolutionUI.instance.bpmsearched || RapidEvolutionUI.instance.keysearched) {
//                  javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {                   
                      RapidEvolutionUI.instance.SearchRoutine();
//                  } });
              }
          }
          if (!dontincrementplaycount && !dontchangesongtrail) newsong.setTimesPlayed(newsong.getTimesPlayed() + 1);
          OptionsUI.instance.filter1.changeCurrentSong();
          OptionsUI.instance.filter2.changeCurrentSong();
          OptionsUI.instance.filter3.changeCurrentSong();
          GetNewServerMixesThread.addToQueue();
      } catch (Exception e) { log.error("run(): error", e); }
      ischangingcurrentsong = false;
      ChangeCurrentSongSem.release();
  	}
}

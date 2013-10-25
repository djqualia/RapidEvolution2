package rapid_evolution;

import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.main.SearchListMouse;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.StylesPane;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.music.SongKeyRelation;
import com.mixshare.rapid_evolution.music.KeyRelation;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class FindLinkThread extends Thread {
  static public boolean findinglink = false;
  public FindLinkThread(SongLinkedList in_song, boolean in_onlymixouts) { findsong = in_song; onlymixouts = in_onlymixouts; }
  SongLinkedList findsong;
  boolean onlymixouts;
  public void run() {
    int count = 0;
    try {
    RapidEvolutionUI.instance.SearchingSemaphore.acquire();
    RapidEvolutionUI.instance.findsearched = false;
    RapidEvolutionUI.instance.keysearched = true;
    SearchListMouse.instance.findlinkselection.setEnabled(false);
    findinglink = true;
    SearchPane.instance.searchdisplaylist = new SongList();
    SongLinkedList siter = SongDB.instance.SongLL;
    while (siter != null) {
      boolean addsong = true;
      if (onlymixouts) {
        if ((siter != findsong) && (siter != RapidEvolutionUI.instance.currentsong)) {
          boolean found = false;
          int  i = 0;
          while (!found && (i < siter.getNumMixoutSongs())) {
            if (siter.mixout_songs[i] == findsong.uniquesongid) found = true;
            ++i;
          }
          i = 0;
          while (!found && (i < findsong.getNumMixoutSongs())) {
            if (findsong.mixout_songs[i] == siter.uniquesongid) found = true;
            ++i;
          }
          if (!found) addsong = false;
        } else addsong = false;
      }
      if ((SearchPane.instance.shownonvinylradio.isSelected()) && siter.getVinylOnly()) addsong = false;
      if ((SearchPane.instance.showvinylradio.isSelected()) && siter.getNonVinylOnly()) addsong = false;
      if (OptionsUI.instance.preventrepeats.isSelected()) {
        if (RapidEvolutionUI.instance.currentsong == siter) addsong = false;
        if (RapidEvolutionUI.instance.currentsong == findsong) addsong = false;
        SongStack ssiter = RapidEvolutionUI.instance.prevstack;
        while (addsong && (ssiter != null)) {
          // to do: optimize repeats to not do string comparisons...
          if (ssiter.songid == siter.uniquesongid) addsong = false;
          ssiter = ssiter.next;
        }
      }
      String keyword = siter.toString();
      if (addsong && siter.isDisabled() && OptionsUI.instance.donotshowdisabledosngs.isSelected()) addsong = false;
      if (addsong && (RapidEvolutionUI.instance.currentsong != null) && RapidEvolutionUI.instance.keysearched && OptionsUI.instance.excludesongsonsamerecord.isSelected()) {
        if ((siter.getAlbum().toLowerCase().equals(RapidEvolutionUI.instance.currentsong.getAlbum().toLowerCase())) && siter.getVinylOnly() && RapidEvolutionUI.instance.currentsong.getVinylOnly()) {
          int type1 = StringUtil.gettracktype(RapidEvolutionUI.instance.currentsong.getTrack());
          int type2 = StringUtil.gettracktype(siter.getTrack());
          if ((type1 >= 0) && (type2 >=0) && (type1 == type2)) addsong = false;
        }
      }
      if (addsong && (findsong != null) && RapidEvolutionUI.instance.keysearched && OptionsUI.instance.excludesongsonsamerecord.isSelected()) {
        if ((siter.getAlbum().toLowerCase().equals(findsong.getAlbum().toLowerCase())) && siter.getVinylOnly() && findsong.getVinylOnly()) {
          int type1 = StringUtil.gettracktype(findsong.getTrack());
          int type2 = StringUtil.gettracktype(siter.getTrack());
          if ((type1 >= 0) && (type2 >=0) && (type1 == type2)) addsong = false;
        }
      }
      if (addsong && (!(!OptionsUI.instance.searchwithinstyles.isSelected() && RapidEvolutionUI.instance.findsearched)))
        if (!StylesPane.instance.isMemberOfCurrentStyle(siter)) addsong = false;
      boolean cannotkeylock = false;
      boolean cankeylock = false;
      if (addsong && RapidEvolutionUI.instance.keysearched) {
        if ((siter.getStartbpm() == 0) || (!siter.getStartKey().isValid())) addsong = false;
        else {
          float bpmdiff = SongUtil.get_bpmdiff(siter.getStartbpm(), RapidEvolution.instance.getActualBpm());
          if (Math.abs(bpmdiff) > RapidEvolutionUI.instance.bpmscale) addsong = false;
          else {
              SongKeyRelation relation = Key.getClosestKeyRelation(siter, RapidEvolution.instance.getActualBpm(), RapidEvolutionUI.instance.getCurrentKey());              
              if (relation.isCompatibleWithKeylock() && !OptionsUI.instance.disablekeylockfunctionality.isSelected())
                  cankeylock = true;
              if (relation.isCompatibleWithoutKeylock() && !OptionsUI.instance.searchexcludenokeylock.isSelected())
                  cannotkeylock = true;
              if (!(cankeylock || cannotkeylock))
                  addsong = false;
          }
        }
      }


      if (addsong && (cankeylock || cannotkeylock)) {
        if ((findsong.getStartbpm() == 0) || (!findsong.getStartKey().isValid())) addsong = false;
        else {
          float bpmdiff = SongUtil.get_bpmdiff(findsong.getStartbpm(), RapidEvolution.instance.getActualBpm());
          if (Math.abs(bpmdiff) > (RapidEvolutionUI.instance.bpmscale * 2)) addsong = false;
          else {
              // TODO: this logic needs ot be fixed, is siter key locked or not?  what speed is it at??
              SongKeyRelation relation = Key.getClosestKeyRelation(findsong, siter);
              addsong = false;
              if (cankeylock && relation.isCompatibleWithKeylock() && !OptionsUI.instance.disablekeylockfunctionality.isSelected())
                  addsong = true;
              if (cannotkeylock && relation.isCompatibleWithoutKeylock() && !OptionsUI.instance.searchexcludenokeylock.isSelected())
                  addsong = true;
          }
        }
      } else addsong = false;

      if (addsong && !RapidEvolutionUI.instance.findsearched && (OptionsUI.instance.excludesongsondonottrylist.isSelected() && (RapidEvolutionUI.instance.currentsong != null))) {
        // to do: optimize exclude list to not involve string comparison
        for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumExcludeSongs(); ++i)
          if (RapidEvolutionUI.instance.currentsong.exclude_songs[i] == siter.uniquesongid) addsong = false;
      }
      if (addsong && !RapidEvolutionUI.instance.findsearched && (OptionsUI.instance.excludesongsondonottrylist.isSelected() && (findsong != null))) {
        // to do: optimize exclude list to not involve string comparison
        for (int i = 0; i < findsong.getNumExcludeSongs(); ++i)
          if (findsong.exclude_songs[i] == siter.uniquesongid) addsong = false;
      }
      if (addsong && (!RapidEvolutionUI.instance.findsearched && (RapidEvolutionUI.instance.currentsong != null) && (!SongLinkedList.determinetimesigcompatibility(siter, RapidEvolutionUI.instance.currentsong)))) addsong = false;
      if (addsong && (!RapidEvolutionUI.instance.findsearched && (findsong != null) && (!SongLinkedList.determinetimesigcompatibility(siter, findsong)))) addsong = false;

      if (addsong) {
        SearchPane.instance.searchdisplaylist.insert(siter);
        count++;
      }
      siter = siter.next;
    }
    } catch (Exception e) { count = 0; }
    RapidEvolutionUI.instance.SearchingSemaphore.release();
    if ((count == 0) && onlymixouts) new FindLinkThread(findsong, false).start();
    else {
      findinglink = false;
      if (SearchPane.instance.searchdisplaylist.song == null) SearchPane.instance.searchdisplaylist = null;
      SearchPane.instance.RedrawSearchTable();
    }
  }
}

package rapid_evolution.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import rapid_evolution.Mixout;
import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.ServerMixout;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.LoginPromptUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.music.Key;

public class MyConnectThread extends Thread {

    private static Logger log = Logger.getLogger(MyConnectThread.class);
    
    public static String clientversion = "1.2";

  static int connectedthreads = 0;
  static private Semaphore serverquerysem = new Semaphore(1);
  Socket sock = null;
  BufferedReader br = null;
  PrintStream ps = null;
  String line = null;
  OutputStream os = null;
  InputStream is = null;
  int connectionport = RapidEvolution.testservermode ? 8100 : 4200;
  public MyConnectThread() { MixshareClient.instance.servercommand = this; }
  public MyConnectThread(int port) { connectionport = port; }
  public void sendString(String str) { try { ps.println(str); } catch (Exception e) {
      MixshareClient.instance.DisconnectFromServer();
      log.error("sendString(): error", e);
  }}
  public String receiveString() {
     try {
       String returnval = StringUtil.cleanString(br.readLine());
       if (returnval != null) return returnval;
     } catch (Exception e) { log.error("receiveString(): error", e); }
     MixshareClient.instance.DisconnectFromServer();
     // part of the eproblem?
     return null;
   }
  public SongLinkedList receiveSong() {
    String artist = receiveString();
    String album = receiveString();
    String track = receiveString();
    String songname = receiveString();
    String remixer = receiveString();
    String tracktime = receiveString();
    String timesig = receiveString();
    float startbpm = 0.0f;
    float endbpm = 0.0f;
    try { startbpm = Float.parseFloat(receiveString()); } catch (Exception e) { }
    try { endbpm = Float.parseFloat(receiveString()); } catch (Exception e) { }
    int bpmaccuracy = 0;
    try { bpmaccuracy = Integer.parseInt(receiveString()); } catch (Exception e) { }
    String startkey = com.mixshare.rapid_evolution.music.Key.getPreferredKeyNotation(receiveString());
    String endkey = com.mixshare.rapid_evolution.music.Key.getPreferredKeyNotation(receiveString());
    int keyaccuracy = 0;
    try { keyaccuracy = Integer.parseInt(receiveString()); } catch (Exception e) { }
    int num_commentline = Integer.parseInt(receiveString());
    String comments = receiveString();
    for (int i = 1; i < num_commentline; ++i) comments += "\n" + receiveString();
    String[] styles = new String[Integer.parseInt(receiveString())];
    for (int i = 0; i < styles.length; ++i) styles[i] = receiveString();
    String user1 = receiveString();
    String user2 = receiveString();
    String user3 = receiveString();
    String user4 = receiveString();
    int beatIntensity = Integer.parseInt(receiveString());
    SongLinkedList returnval = new SongLinkedList(0, artist, album, track, songname, remixer, comments, false, false, startbpm, endbpm, 0, 0, Key.getKey(startkey), Key.getKey(endkey), new String(""), tracktime, timesig, false, true, new String(""), new String(""), new String(""), new String(""), null, styles);
    returnval.calculate_unique_id();
    returnval.calculateSongDisplayIds();
    returnval.setKeyAccuracy(keyaccuracy);
    returnval.setBpmAccuracy(bpmaccuracy);
    returnval.setUser1(user1);
    returnval.setUser2(user2);
    returnval.setUser3(user3);
    returnval.setUser4(user4);
    returnval.setBeatIntensity(beatIntensity);
    lastqueried = System.currentTimeMillis();
    return returnval;
  }
  public void UpdateEditSong(SongLinkedList editsong, OldSongValues old_values) {
      String olduniqueid = old_values.getUniqueStringId();
      log.debug("UpdateEditSong(): " + editsong + ", old id: " + olduniqueid);
    if (!MixshareClient.instance.isConnected()) {
      if (!editsong.uniquestringid.toLowerCase().equals(olduniqueid.toLowerCase())) RapidEvolution.getMixshareClient().setOutOfSync(true);
      return;
    }
    try {
      editsong.pendingEditRequests++;
      serverquerysem.acquire();
      sendString("4");
      sendString(old_values.getArtist());
      sendString(old_values.getAlbum());
      sendString(old_values.getTrack());
      sendString(old_values.getTitle());
      sendString(old_values.getRemix());
      sendSongINFO(editsong);
      if (receiveString().equals("1")) editsong.servercached = true;
    } catch (Exception e) { log.error("UpdateEditSong(): error", e); }
    lastqueried = System.currentTimeMillis();
    editsong.pendingEditRequests--;
    serverquerysem.release();
  }

  public String changeName(String newname) {
    String returnval = newname;
    try {
      serverquerysem.acquire();
      sendString("7");
      sendString(newname);
      returnval = receiveString();
    } catch (Exception e) { log.error("changeName(): error", e); }
    serverquerysem.release();
    return returnval;
  }

  public void restoreFromServer() {
/*
      Vector addsongs = new Vector();
    Vector mixouts = new Vector();
    try {
      serverquerysem.acquire();
      sendString("8");
      int numsongs = Integer.parseInt(receiveString());
      for (int i = 0; i < numsongs; ++i) { addsongs.add(receiveSong()); lastqueried = System.currentTimeMillis(); }
      int nummixouts = Integer.parseInt(receiveString());
      for (int i = 0; i < nummixouts; ++i) { mixouts.add(receiveMixout()); lastqueried = System.currentTimeMillis(); }
    } catch (Exception e) { if (RapidEvolution.debugmode) e.printStackTrace(); }
    serverquerysem.release();
    RapidEvolution.instance.loaded = false;
    for (int k = 0; k < addsongs.size(); ++k) {
      SongLinkedList tempsong = (SongLinkedList)addsongs.get(k);
      for (int st = 0; st < tempsong.stylelist.length; ++st) {
        StyleLinkedList siter = SongDB.instance.masterstylelist;
        boolean found = false;
        while (siter != null) {
            if (siter.name.toLowerCase().equals(tempsong.stylelist[st].toLowerCase())) found = true;
            siter = siter.next;
        }
        if (!found) SongDB.instance.addStyle(tempsong.stylelist[st], false);
      }
      SongLinkedList newsong = SongDB.instance.getSongLinkedList(tempsong, tempsong.getFileName());
      if (newsong != null) {
        long olduniqueid = newsong.uniquesongid;
        String oldname = newsong.uniquestringid;
        String oldartist = newsong.getArtist();
          if ((newsong.getSongname().equals(""))) newsong.setSongname(tempsong.getSongname());
          if ((newsong.getArtist().equals(""))) newsong.setArtist(tempsong.getArtist());
          if ((newsong.getAlbum().equals(""))) newsong.setAlbum(tempsong.getAlbum());
          if ((newsong.getTrack().equals(""))) newsong.setTrack(tempsong.getTrack());
          if ((newsong.getTime().equals(""))) newsong.setTime(tempsong.getTime());
          if ((newsong.getRemixer().equals(""))) newsong.setRemixer(tempsong.getRemixer());
          if ((newsong.getTimesig().equals(""))) newsong.setTimesig(tempsong.getTimesig());
          if ((newsong.getStartkey().equals(""))) newsong.setStartkey(rapid_evolution.util.Key.getPreferredKeyNotation(tempsong.getStartkey()));
          if ((newsong.getEndkey().equals(""))) newsong.setEndkey(rapid_evolution.util.Key.getPreferredKeyNotation(tempsong.getEndkey()));
          if ((tempsong.getStartbpm() == 0)) newsong.setStartbpm(tempsong.getStartbpm());
          if ((tempsong.getEndbpm() == 0)) newsong.setEndbpm(tempsong.getEndbpm());
          if ((newsong.getComments().equals(""))) newsong.setComments(tempsong.getComments());
          if ((newsong.getUser1().equals(""))) newsong.setUser1(tempsong.getUser1());
          if ((newsong.getUser2().equals(""))) newsong.setUser2(tempsong.getUser2());
          if ((newsong.getUser3().equals(""))) newsong.setUser3(tempsong.getUser3());
          if ((newsong.getUser4().equals(""))) newsong.setUser4(tempsong.getUser4());
          if ((newsong.getFileName().equals(""))) newsong.setFilename(tempsong.getFileName());
        SongDB.instance.UpdateSong(newsong, oldname, oldartist, olduniqueid);
        if (newsong == RapidEvolutionUI.instance.currentsong) {
          RapidEvolutionUI.instance.UpdateCurrentRoutine();
//            UpdateCurrentThread ut = new UpdateCurrentThread();
//            ut.start();
        }
        for (int st = 0; st < tempsong.stylelist.length; ++st) {
          StyleLinkedList siter = SongDB.instance.masterstylelist;
          while (siter != null) {
              if (siter.name.toLowerCase().equals(tempsong.stylelist[st].toLowerCase())) {
                if (!siter.contains(newsong)) {
                    siter.insertSong(newsong);
                }
              }
              siter = siter.next;
          }
        }

      } else {
        SongDB.instance.AddSingleSong(tempsong, tempsong.stylelist);
      }

    }
    for (int m = 0; m < mixouts.size(); ++m) {
      Mixout mixout = (Mixout)mixouts.get(m);
      SongLinkedList mixtosong = SongDB.instance.OldGetSongPtr(mixout.songuniqueid2);
      SongLinkedList mixfromsong = SongDB.instance.OldGetSongPtr(mixout.songuniqueid1);
      if ((mixtosong != null) && (mixfromsong != null)) {
        mixfromsong.insertMixOut(mixtosong, mixout.comments, mixout.bpmdiff, mixout.rank, mixout.addon);
        if (mixfromsong == RapidEvolutionUI.instance.currentsong) MixoutPane.instance.RedrawMixoutTable();
      }
    }
    RapidEvolution.instance.loaded = true;
    */
  }

  public void removeMixout(SongLinkedList from_song, SongLinkedList to_song) {
    if (!MixshareClient.instance.isConnected()) {
      RapidEvolution.getMixshareClient().setOutOfSync(true);
      return;
    }
    try {
      serverquerysem.acquire();
      sendString("-2");
      sendString(from_song.getArtist());
      sendString(from_song.getAlbum());
      sendString(from_song.getTrack());
      sendString(from_song.getSongname());
      sendString(from_song.getRemixer());
      sendString(to_song.getArtist());
      sendString(to_song.getAlbum());
      sendString(to_song.getTrack());
      sendString(to_song.getSongname());
      sendString(to_song.getRemixer());
    } catch (Exception e) { log.error("receiveMixout(): error", e); }
    lastqueried = System.currentTimeMillis();
    serverquerysem.release();
  }

  public void sendShortSongINFO(SongLinkedList song) {
    sendString(song.getArtist());
    sendString(song.getAlbum());
    sendString(song.getTrack());
    sendString(song.getSongname());
    sendString(song.getRemixer());
  }

  public boolean sendMixout(SongLinkedList song, int index) {
    boolean returnval = false;
    try {
      serverquerysem.acquire();
      sendString("2");
//        sendString(song.uniquesongid);
//        sendString(song.mixout_songs[index]);
      sendShortSongINFO(song);
      sendShortSongINFO(SongDB.instance.NewGetSongPtr(song.mixout_songs[index]));
      sendString(String.valueOf(song.getMixoutBpmdiff(index)));
      sendString(OptionsUI.instance.donotsharemixcomments.isSelected() ? "1" : String.valueOf(StringUtil.num_lines(song.getMixoutComments(index))));
      sendString(OptionsUI.instance.donotsharemixcomments.isSelected() ? "" : song.getMixoutComments(index));
      sendString(String.valueOf(song.getMixoutRank(index)));
      if (song.getMixoutAddon(index)) sendString("1");
      else sendString("0");
      // NULL pointer here:?
      if (receiveString().equals("1")) returnval = true;
      else returnval = false;
    } catch (Exception e) { log.error("sendMixout(): error", e); }
    serverquerysem.release();
    lastqueried = System.currentTimeMillis();
    if (!returnval) {
        MixshareClient.instance.servercommand.sendSong(song);
        SongLinkedList tosong = SongDB.instance.NewGetSongPtr(song.mixout_songs[index]);
        MixshareClient.instance.servercommand.sendSong(tosong);
        try {
          serverquerysem.acquire();
          sendString("2");
          sendShortSongINFO(song);
          sendShortSongINFO(tosong);
          sendString(String.valueOf(song.getMixoutBpmdiff(index)));
          sendString(String.valueOf(StringUtil.num_lines(song.getMixoutComments(index))));
          sendString(song.getMixoutComments(index));
          sendString(String.valueOf(song.getMixoutRank(index)));
          if (song.getMixoutAddon(index)) sendString("1");
          else sendString("0");
          // NULL pointer here
          if (receiveString().equals("1")) returnval = true;
          else returnval = false;
        } catch (Exception e) { log.error("sendMixout(): error", e); }
        serverquerysem.release();
        lastqueried = System.currentTimeMillis();
    }
    if (returnval) song.mixout_servercache[index] = true;
    return returnval;
  }
  public void sendSongINFO(SongLinkedList song) {
    sendString(song.getArtist());
    sendString(song.getAlbum());
    sendString(song.getTrack());
    sendString(song.getSongname());
    sendString(song.getRemixer());
    sendString(song.getTime());
    sendString(String.valueOf(song.getRatingInt()));
    sendString(song.getTimesig());
    sendString(String.valueOf(song.getStartbpm()));
    sendString(String.valueOf(song.getEndbpm()));
    sendString(String.valueOf(song.getBpmAccuracy()));
    sendString(song.getStartKey().getKeyNotationSharp(true));
    sendString(song.getEndKey().getKeyNotationSharp(true));
    sendString(String.valueOf(song.getKeyAccuracy()));
    sendString(String.valueOf(StringUtil.num_lines(song.getComments())));
    sendString(song.getComments());
    int stylecount = 0;
    for (int i = 0; i < song.getStyles().length; ++i) if (song.getStyle(i)) stylecount++;
    sendString(String.valueOf(stylecount));
    StyleLinkedList siter = SongDB.instance.masterstylelist;
    stylecount = 0;
    while (siter != null) {
      if (song.getStyle(stylecount++)) sendString(siter.getName());
      siter = siter.next;
    }
    sendString(song.getUser1());
    sendString(song.getUser2());
    sendString(song.getUser3());
    sendString(song.getUser4());
    sendString(String.valueOf(song.getBeatIntensity()));    
    lastqueried = System.currentTimeMillis();
  }
  public boolean removeSong(SongLinkedList song) {
      log.debug("removeSong(): song=" + song);
    if (!MixshareClient.instance.isConnected()) {
        RapidEvolution.getMixshareClient().setOutOfSync(true);
      return false;
    }
    boolean returnval = false;
    try {
      serverquerysem.acquire();
      sendString("-1");
      sendSongINFO(song);
      if (receiveString().equals("1"))
          returnval = true;
    } catch (Exception e) { log.error("removeSong(): error", e); }    
    serverquerysem.release();
    lastqueried = System.currentTimeMillis();
    return returnval;
  }
  public void RemoveOldData() {
    try {
      serverquerysem.acquire();
      sendString("10");
      RapidEvolution.getMixshareClient().setIsSyncing(false);
    } catch (Exception e) { log.error("RemoveOldData(): error", e); }
    serverquerysem.release();
  }

  public boolean sendSong(SongLinkedList song) {
    boolean returnval = false;
    try {
      while (song.pendingEditRequests > 0) {
        // wait until any pending edit requests complete, otherwise could cause duplicate/error on server
        try {
          Thread.sleep(5000);
        } catch (java.lang.InterruptedException ie) { }
      }
      serverquerysem.acquire();
      sendString("1");
      sendSongINFO(song);
      String result = receiveString();
      if (result != null) {
          if (result.equals("1")) {
              returnval = true;
              song.servercached = true;
          }
      }
    } catch (Exception e) { log.error("sendSong(): error", e); }
    serverquerysem.release();
    lastqueried = System.currentTimeMillis();
    return returnval;
  }

  long lastqueried;
  class CheckStatusThread extends Thread {
    public void run() {
      while (!RapidEvolution.instance.terminatesignal) {
        try {
          long diff = (System.currentTimeMillis() - lastqueried) / 1000;
          if (diff > 60) { CloseOut(); return; }
          else Thread.sleep(10000);
        } catch (Exception e) { log.error("run(): error", e);  }
      }
    }
  }

  void CloseOut() {
    try { if (is != null) is.close(); } catch (IOException ie) { log.error("CloseOut: error", ie); }
    try { if (os != null) os.close(); } catch (IOException ie) { log.error("CloseOut: error", ie); }
    try { if (sock != null) sock.close(); } catch (IOException ie) { log.error("CloseOut: error", ie); }
  }

  public void run() {
    if (connectedthreads > 0) return;
    connectedthreads++;
    while (!RapidEvolution.instance.terminatesignal) {
      try {
          if (OptionsUI.instance.disableserver.isSelected()) {
              connectedthreads--;
              return;            
          }
        String usernamestr = OptionsUI.instance.username.getText();
        String passwordstr = new String(OptionsUI.instance.password.getPassword());
        // prompt for username and pass if nonexistant
        if (usernamestr.equals("") || passwordstr.equals("")) {
          LoginPromptUI.instance.Display();
          connectedthreads--;
          return;
        }

        try {
          // setup connection
          sock = new Socket("mixshare.dyndns.org", connectionport);
          sock.setSoTimeout(15000);
          os = sock.getOutputStream();
          is = sock.getInputStream();
          ps = new PrintStream(os);
          br = new BufferedReader(new InputStreamReader(is));

          // hand made timer...
          lastqueried = System.currentTimeMillis();
          new CheckStatusThread().start();

          sendString(clientversion);
          sendString(RapidEvolution.versionString);
          sendString(usernamestr);
          sendString(passwordstr);
          sendString(OptionsUI.instance.emailaddress.getText());
          sendString(OptionsUI.instance.userwebsite.getText());
          sendString(OptionsUI.instance.customfieldtext1.getText());
          sendString(OptionsUI.instance.customfieldtext2.getText());
          sendString(OptionsUI.instance.customfieldtext3.getText());
          sendString(OptionsUI.instance.customfieldtext4.getText());          
          sendString(OptionsUI.instance.custom_field_1_tag_combo.getSelectedItem().toString());
          sendString(OptionsUI.instance.custom_field_2_tag_combo.getSelectedItem().toString());
          sendString(OptionsUI.instance.custom_field_3_tag_combo.getSelectedItem().toString());
          sendString(OptionsUI.instance.custom_field_4_tag_combo.getSelectedItem().toString());
          sendString("0"); // disabling the requirement that each re2 copy have  a unique user
          //if (MixshareClient.instance.neverconnectedtoserver) sendString("1");
          //else sendString("0");
          if (receiveString().equals("OK")) {
            MixshareClient.instance.neverconnectedtoserver = false;
            sendString("12");
            String shouldsync = receiveString();
            
            if ((shouldsync.equals("1") && !RapidEvolution.getMixshareClient().isSyncing()) || RapidEvolution.getMixshareClient().isOutOfSync()) {
                RapidEvolution.getMixshareClient().setIsSyncing(true);
                SongLinkedList iter = SongDB.instance.SongLL;
                while (iter != null) {
                    iter.servercached = false;
                    for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
                        iter.mixout_servercache[i] = false;
                    }
                    iter = iter.next;
                }
                sendString("11");
                RapidEvolution.getMixshareClient().setOutOfSync(false);
            }
            log.debug("run(): connected to mixshare (" + String.valueOf(connectionport) + ")");
            if (RapidEvolution.getMixshareClient().isSyncing()) log.debug("run(): is syncing with server");
            MixshareClient.instance.setThreadConnected(0);
            MixshareClient.instance.servercommand2 = new MyConnectQueryThread();
            MixshareClient.instance.servercommand2.start();
            // keep alive
            while (!RapidEvolution.instance.terminatesignal && sock.isConnected() && !sock.isClosed() && MixshareClient.instance.isConnected()) {
              try {
                serverquerysem.acquire();
                sendString("6");
                receiveString();
              } catch (Exception e) { serverquerysem.release(); MixshareClient.instance.DisconnectFromServer(); log.error("run(): error", e); }
              serverquerysem.release();
              lastqueried = System.currentTimeMillis();
              try { Thread.sleep(5000); } catch (Exception e) { }
            }
            sendString("CLOSE");
            MixshareClient.instance.DisconnectFromServer();
          } else {
              IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
              SkinManager.instance.getDialogMessageText("mixshare_bad_user_password"),
              SkinManager.instance.getDialogMessageTitle("mixshare_bad_user_password"),
              IOptionPane.ERROR_MESSAGE);

              LoginPromptUI.instance.Display();
            connectedthreads--;
            return;
          }
        } catch (Exception e) {
          String error = e.toString();
          if ((error.startsWith("java.net.ConnectException: Connection refused: connect"))) log.debug("run(): mixshare server is not responding or is unreachable");
          else if ((error.startsWith("java.net.ConnectException: Connection timed out: connect"))) log.debug("run(): connection to mixshare server timed out");
          else if ((error.startsWith("java.net.UnknownHostException"))) log.debug("run(): connection to mixshare failed: unknown host");
          else log.debug("run(): error", e);
          MixshareClient.instance.DisconnectFromServer();
        }
        CloseOut();
      } catch (Exception e) {log.error("run(): error", e); MixshareClient.instance.DisconnectFromServer(); }

      if (!RapidEvolution.instance.terminatesignal) {
        if (connectionport == (RapidEvolution.testservermode ? 4200 + 20 : 4200)) {
          SkinManager.instance.setEnabled("add_songs_query_server_button", false);
          OptionsUI.instance.restoreFromServer.setEnabled(false);
          OptionsUI.instance.username.setEnabled(false);
          SkinManager.instance.setEnabled("edit_song_query_server_button", false);
            SkinManager.instance.setEnabled("current_song_mixes_button", false);
//          if (RapidEvolution.debugmode) System.out.println("Waiting 60 seconds to connect...");
        }
        try { Thread.sleep(60000); }
        catch (Exception e) { log.error("run(): error", e); }
      }
    }
    connectedthreads--;
  }
}

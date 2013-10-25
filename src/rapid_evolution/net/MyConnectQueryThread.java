package rapid_evolution.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.ServerMixout;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.net.DataMinerHelper;
import com.mixshare.rapid_evolution.net.mining.DiscogsArtistJob;
import com.mixshare.rapid_evolution.net.mining.DiscogsLabelJob;
import com.mixshare.rapid_evolution.net.mining.LastfmAlbumJob;
import com.mixshare.rapid_evolution.net.mining.LastfmArtistJob;
import com.mixshare.rapid_evolution.net.mining.LastfmSongJob;
import com.mixshare.rapid_evolution.net.mining.MiningJobInterface;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class MyConnectQueryThread extends Thread {

    private static Logger log = Logger.getLogger(MyConnectQueryThread.class);
    
    static private boolean miningEnabled = false;
    static private Semaphore serverquerysem2 = new Semaphore(1);
    Socket sock = null;
    BufferedReader br = null;
    PrintStream ps = null;
    String line = null;
    OutputStream os = null;
    InputStream is = null;
    int connectionport = RapidEvolution.testservermode ? 8100 : 4199;
    long lastqueried;
    public void sendString(String str) { try { ps.println(str); } catch (Exception e) { MixshareClient.instance.DisconnectFromServer(); }}
    public String receiveString() {
      try {
        String returnval = StringUtil.cleanString(br.readLine());
        if (returnval != null) return returnval;
      } catch (Exception e) { }
      MixshareClient.instance.DisconnectFromServer();
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
        sendString(song.getStartKey().getShortKeyNotation());
        sendString(song.getEndKey().getShortKeyNotation());
        sendString(String.valueOf(song.getKeyAccuracy()));        
        sendString(String.valueOf(StringUtil.num_lines(song.getComments())));
        sendString(song.getComments());
        int stylecount = 0;
        for (int i = 0; i < song.getStyles().length; ++i) if (song.getStyle(i)) stylecount++;
        sendString(String.valueOf(stylecount));
        StyleLinkedList siter = SongDB.instance.masterstylelist;
        stylecount = 0;
        while (siter != null) {
            // TODO: send hierarchy info
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

    public Vector queryServer(SongLinkedList song) {
        log.debug("queryServer(): song=" + song);
      Vector returnval = new Vector();
      try {
        serverquerysem2.acquire();
        sendString("3");
        sendSongINFO(song);
        int numsongs = Integer.parseInt(receiveString());
        for (int i = 0; i < numsongs; ++i) returnval.add(receiveSong());
      } catch (Exception e) { }
      serverquerysem2.release();
      lastqueried = System.currentTimeMillis();
      for (int j = 0; j < returnval.size(); ++j) {
        SongLinkedList onesong = (SongLinkedList)returnval.get(j);
        for (int i = 0; i < onesong.stylelist.length; ++i) {
            onesong.stylelist[i] = StringUtil.ProcessStyleName(onesong.stylelist[i]);
        }
      }
      return returnval;
    }

    public Vector queryServerDetails(SongLinkedList song) {
        if (log.isDebugEnabled()) log.debug("queryServerDetails(): song=" + song);
      Vector returnval = new Vector();
      try {
        serverquerysem2.acquire();
        sendString("13");
        sendSongINFO(song);
        int numsongs = Integer.parseInt(receiveString());
        for (int i = 0; i < numsongs; ++i) returnval.add(receiveSong());
      } catch (Exception e) { log.error("queryServerDetails(): error", e); }
      serverquerysem2.release();
      lastqueried = System.currentTimeMillis();
      if (log.isTraceEnabled()) log.trace("queryServerDetails(): returnval=" + returnval);
      return returnval;
    }

    public Vector queryServerStrict(SongLinkedList song) {
      Vector returnval = new Vector();
      try {
        serverquerysem2.acquire();
        sendString("9");
        sendSongINFO(song);
        int numsongs = Integer.parseInt(receiveString());
        for (int i = 0; i < numsongs; ++i) returnval.add(receiveSong());
      } catch (Exception e) { }
      serverquerysem2.release();
      lastqueried = System.currentTimeMillis();
      for (int j = 0; j < returnval.size(); ++j) {
        SongLinkedList onesong = (SongLinkedList)returnval.get(j);
        for (int i = 0; i < onesong.stylelist.length; ++i) {
            onesong.stylelist[i] = StringUtil.ProcessStyleName(onesong.stylelist[i]);
        }
      }
      return returnval;
    }

    public Vector getMixouts(SongLinkedList song) {
        return getMixouts(song, true);
    }
    public Vector<ServerMixout> getMixouts(SongLinkedList song, boolean retry) {
        log.debug("getMixouts(): song=" + song);
        Vector returnval = new Vector();
        if (!OptionsUI.instance.donotsharemixouts.isSelected()) {
	      try {
	        serverquerysem2.acquire();
	        sendString("5");
	        sendString(song.getArtist());
	        sendString(song.getAlbum());
	        sendString(song.getTrack());
	        sendString(song.getSongname());
	        sendString(song.getRemixer());
	        int num_results = Integer.parseInt(receiveString());
	        if (num_results == -1) {
	            if (retry) {
		            log.debug("getMixouts(): song is missing, sending...");
		          serverquerysem2.release();
		          lastqueried = System.currentTimeMillis();
		          MixshareClient.instance.servercommand.sendSong(song);
		          return getMixouts(song, false);
	            }            
	            num_results = 0;
	        }
	        for (int i = 0; i < num_results; ++i) {
	        	ServerMixout mixout = receiveMixout();
	          //mixout.tosong = receiveSong();
	          returnval.add(mixout);
	        }
	      } catch (Exception e) { }
	      serverquerysem2.release();
	      lastqueried = System.currentTimeMillis();
        }        
      return returnval;
    }

    public ServerMixout receiveMixout() {
    	SongLinkedList toSong = receiveSong();
    	float bpmdiff = Float.parseFloat(receiveString());
    	int rank = Integer.parseInt(receiveString());
    	boolean addon = false;
    	if (receiveString().equals("1")) addon = true;
    	lastqueried = System.currentTimeMillis();
    	return new ServerMixout(toSong, bpmdiff, "", rank, addon);
    }

    class CheckStatusThread extends Thread {
      public void run() {
        while (!RapidEvolution.instance.terminatesignal) {
          try {
            long diff = (System.currentTimeMillis() - lastqueried) / 1000;
            if (diff > 60) { CloseOut(); return; }
            else Thread.sleep(10000);
          } catch (Exception e) { }
        }
      }
    }

    void CloseOut() {
      try { br.close(); is.close(); } catch (IOException ie) { }
      try { ps.close(); os.close(); } catch (IOException ie) { }
      try { if (sock != null) sock.close(); } catch (IOException ie) { }
    }

    public void run() {
        try {
            if (OptionsUI.instance.disableserver.isSelected()) {
                return;            
            }
          String usernamestr = OptionsUI.instance.username.getText();
          String passwordstr = new String(OptionsUI.instance.password.getPassword());

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

            sendString(MyConnectThread.clientversion);
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
            sendString("0");
            if (receiveString().equals("OK")) {

            	/*
            	// test code
                LastfmArtistJob job2 = new LastfmArtistJob("Thelonious Monk");
          	  DataMinerHelper.setCurrentJob(job2);
          	  ((Thread)job2).start();                  	
            	*/
            	
              MixshareClient.instance.neverconnectedtoserver = false;
              sendString("12");
              receiveString();
              MixshareClient.instance.setThreadConnected(1);
              // keep alive
              while (!RapidEvolution.instance.terminatesignal && sock.isConnected() && !sock.isClosed() && MixshareClient.instance.isConnected()) {
                Thread.sleep(10000);
                try {
                  serverquerysem2.acquire();
                  if (DataMinerHelper.hasResult()) {                	  
                	  MiningJobInterface finishedJob = DataMinerHelper.getCurrentJob();
                	  Object result = finishedJob.getResult();
                	  if (result != null)
            			  new ObjectSender(result).start();
                	  DataMinerHelper.clearCurrentJob();
                  }
                  if (DataMinerHelper.canAcceptJob()) {
                	  sendString("14");
                	  int jobType = Integer.parseInt(receiveString());
                	  if (jobType >= 0) {
                		  MiningJobInterface job = null;
                		  if (miningEnabled) {
	                		  if (jobType == 0) {
	                			  // discgos artist
	                			  String artistName = receiveString();
	                			  job = new DiscogsArtistJob(artistName);
	                    		  if (log.isTraceEnabled())
	                    			  log.trace("run(): received discogs artist job=" + artistName);
	                		  } else if (jobType == 1) {
	                			  // discogs label
	                			  String labelName = receiveString();
	                			  job = new DiscogsLabelJob(labelName);
	                    		  if (log.isTraceEnabled())
	                    			  log.trace("run(): received discogs label job=" + labelName);
	                		  } else if (jobType == 2) {
	                			  // lastfm artist
	                			  String artistName = receiveString();
	                			  job = new LastfmArtistJob(artistName);
	                    		  if (log.isTraceEnabled())
	                    			  log.trace("run(): received lastfm artist job=" + artistName);                			  
	                		  } else if (jobType == 3) {
	                			  // lastfm album
	                			  String artist = receiveString();
	                			  String album = receiveString();
	                			  job = new LastfmAlbumJob(artist, album);
	                    		  if (log.isTraceEnabled())
	                    			  log.trace("run(): received lastfm album job=" + artist + " - " + album);                			  
	                 		  } else if (jobType == 4) {
	                 			  // lastfm song
	                 			  String artist = receiveString();
	                 			  String song = receiveString();
	                 			  job = new LastfmSongJob(artist, song);
	                    		  if (log.isTraceEnabled())
	                    			  log.trace("run(): received lastfm track job=" + artist + " - " + song);                			  
	                 			  
	                 		  }
	                		  DataMinerHelper.setCurrentJob(job);
	                		  ((Thread)job).start();
                		  }
                	  }
                  } else {
                	  sendString("6");
                	  receiveString();
                  }
                } catch (Exception e) { serverquerysem2.release(); MixshareClient.instance.DisconnectFromServer(); }
                serverquerysem2.release();
                lastqueried = System.currentTimeMillis();
                try { Thread.sleep(5000); } catch (Exception e) { }
              }
              sendString("CLOSE");
              MixshareClient.instance.DisconnectFromServer();
            }
          } catch (Exception e) {
            String error = e.toString();
            if ((error.startsWith("java.net.ConnectException: Connection refused: connect"))) log.debug("run(): server is not responding or unreachable");
            else log.error("run(): error", e);
            MixshareClient.instance.DisconnectFromServer();
          }
          CloseOut();
        } catch (Exception e) { log.error("run(): error", e); MixshareClient.instance.DisconnectFromServer(); }

    }
};

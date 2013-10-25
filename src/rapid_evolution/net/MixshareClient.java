package rapid_evolution.net;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

public class MixshareClient {
    
    private static Logger log = Logger.getLogger(MixshareClient.class);
    
    public MixshareClient() {
        instance = this;
        ResetThreadsConnected();
      }

    public static MixshareClient instance = null;

    public MyConnectThread servercommand = null;
    public MyConnectQueryThread servercommand2 = null;

    public boolean neverconnectedtoserver = true; // obsolete
    int num_clientconnects = 0;
    public boolean dontconnectinoptions = false;
    Semaphore ClientConnectThreadSem = new Semaphore(1);

    public RegularServerUpdater serverupdater = new RegularServerUpdater();

    public boolean isConnected() { return connectedtoserver; }
    public void setThreadConnected(int thread) {
      connectedtoserver = true;
      threadsconnected[thread] = true;
      if (threadsconnected[0] && threadsconnected[1]) ConnectedToServer();
    }

    private boolean connectedtoserver = false;
    public void ConnectedToServer() {
      OptionsUI.instance.restoreFromServer.setEnabled(true);
      SkinManager.instance.setEnabled("add_songs_query_server_button", true);
      OptionsUI.instance.username.setEnabled(true);
      SkinManager.instance.setEnabled("edit_song_query_server_button", true);
      MixshareClient.instance.serverupdater = new RegularServerUpdater();
      MixshareClient.instance.serverupdater.start();
    }

    public void DisconnectFromServer() {
      connectedtoserver = false;
      OptionsUI.instance.restoreFromServer.setEnabled(false);
      SkinManager.instance.setEnabled("add_songs_query_server_button", false);
      OptionsUI.instance.username.setEnabled(false);
      SkinManager.instance.setEnabled("edit_song_query_server_button", false);
      SkinManager.instance.setEnabled("current_song_mixes_button", false);
      ResetThreadsConnected();
    }

    private boolean[] threadsconnected;
    private void ResetThreadsConnected() {
      threadsconnected = new boolean[2];
      threadsconnected[0] = false;
      threadsconnected[1] = false;
    }
    
    public void querySongDetails(SongLinkedList newsong, boolean detailsonly) {
        if (MixshareClient.instance.isConnected() && OptionsUI.instance.automaticallyquerywhenadding.isSelected()) {
            try {
                GetSongInfoThread.GetSongInfoSemaphore.acquire();
                Vector results = (MixshareClient.instance.servercommand2 == null) ? null : MixshareClient.instance.servercommand2.queryServerDetails(newsong);
                SongLinkedList onesong = null;
                if (results.size() == 1) {
                    onesong = (SongLinkedList)results.get(0);
                    if (OptionsUI.instance.donotquerycomments.isSelected()) onesong.setComments(new String(""));
                    if (OptionsUI.instance.donotquerystyles.isSelected()) onesong.stylelist = new String[0];

                    if (!detailsonly) {
                        if (SongDB.instance.OldGetSongPtr(onesong.uniquestringid) != null) {
                            detailsonly = true;
                        }
                    }
                    if (detailsonly) {
                        onesong.setArtist("");
                        onesong.setAlbum("");
                        onesong.setTrack("");
                        onesong.setSongname("");
                        onesong.setRemixer("");
                    }                    
                  
                  if (OptionsUI.instance.overwritewhenquerying.isSelected()) {
	                  if ((!onesong.getArtist().equals("")))
	                    newsong.setArtist(onesong.getArtist());
	                  if ( (!onesong.getAlbum().equals("")))
	                    newsong.setAlbum(onesong.getAlbum());
	                  if ( (!onesong.getTrack().equals("")))
	                    newsong.setTrack(onesong.getTrack());
	                  if ( (!onesong.getSongname().equals("")))
	                    newsong.setSongname(onesong.getSongname());
	                  if ( onesong.getStartKey().isValid()) newsong.setStartkey(onesong.getStartKey().toString());
	                  if ( onesong.getEndKey().isValid())
	                    newsong.setEndkey(onesong.getEndKey().toString());
	                  if ( (!onesong.getRemixer().equals("")))
	                    newsong.setRemixer(onesong.getRemixer());
	                  if ( (onesong.getStartbpm() != 0.0f))
	                    newsong.setStartbpm(onesong.getStartbpm());
	                  if ( (onesong.getEndbpm() != 0.0f))
	                    newsong.setEndbpm(onesong.getEndbpm());
	                  if (!onesong.getTimesig().equals("")) newsong.setTimesig(onesong.getTimesig());
	                  if (!onesong.getTime().equals("")) newsong.setTime(onesong.getTime());
	                  if (!onesong.getComments().equals("")) newsong.setComments(onesong.getComments());
	                  if (!onesong.getUser1().equals("")) newsong.setUser1(onesong.getUser1());
	                  if (!onesong.getUser2().equals("")) newsong.setUser2(onesong.getUser2());
	                  if (!onesong.getUser3().equals("")) newsong.setUser3(onesong.getUser3());
	                  if (!onesong.getUser4().equals("")) newsong.setUser4(onesong.getUser4());
	                  if (onesong.getBeatIntensity() != 0) newsong.setBeatIntensity(onesong.getBeatIntensity());
                  } else {
                    if ((newsong.getArtist().equals("")) &&
                        (!onesong.getArtist().equals("")))
                      newsong.setArtist(onesong.getArtist());
                    if ( (newsong.getAlbum().equals("")) && (!onesong.getAlbum().equals("")))
                      newsong.setAlbum(onesong.getAlbum());
                    if ( (newsong.getTrack().equals("")) && (!onesong.getTrack().equals("")))
                      newsong.setTrack(onesong.getTrack());
                    if ( (newsong.getSongname().equals("")) && (!onesong.getSongname().equals("")))
                      newsong.setSongname(onesong.getSongname());
                    if ( !newsong.getStartKey().isValid() &&
                        (onesong.getStartKey().isValid())) newsong.setStartkey(onesong.getStartKey());
                    if ( !newsong.getEndKey().isValid() &&
                        onesong.getEndKey().isValid())
                      newsong.setEndkey(onesong.getEndKey());
                    if ( (newsong.getRemixer().equals("")) &&
                        (!onesong.getRemixer().equals("")))
                      newsong.setRemixer(onesong.getRemixer());
                    if ( (newsong.getStartbpm() == 0.0f) && (onesong.getStartbpm() != 0.0f))
                      newsong.setStartbpm(onesong.getStartbpm());
                    if ( (newsong.getEndbpm() == 0.0f) && (onesong.getEndbpm() != 0.0f))
                      newsong.setEndbpm(onesong.getEndbpm());
                    if (newsong.getTimesig().equals("") && !onesong.getTimesig().equals("")) newsong.setTimesig(onesong.getTimesig());
                    if (newsong.getTime().equals("") && !onesong.getTime().equals("")) newsong.setTime(onesong.getTime());
                    if (newsong.getComments().equals("") && !onesong.getComments().equals("")) newsong.setComments(onesong.getComments());
                    if (newsong.getUser1().equals("") && !onesong.getUser1().equals("")) newsong.setUser1(onesong.getUser1());
                    if (newsong.getUser2().equals("") && !onesong.getUser2().equals("")) newsong.setUser2(onesong.getUser2());
                    if (newsong.getUser3().equals("") && !onesong.getUser3().equals("")) newsong.setUser3(onesong.getUser3());
                    if (newsong.getUser4().equals("") && !onesong.getUser4().equals("")) newsong.setUser4(onesong.getUser4());
                    if ((newsong.getBeatIntensity() == 0) && (onesong.getBeatIntensity() != 0)) newsong.setBeatIntensity(onesong.getBeatIntensity());
                  }
                  if (!OptionsUI.instance.donotquerystyles.isSelected()) {
                    Vector styles = new Vector();
                    if (onesong.stylelist != null) {                        
                        for (int h = 0; h < onesong.stylelist.length; ++h) {
                            String g = onesong.stylelist[h];

                            boolean found = false;
                            StyleLinkedList siter = SongDB.instance.masterstylelist;
                            while (siter != null) {
                                if (StringUtil.areStyleNamesEqual(siter.getName(), g)) {
                                    found = true;
                                }

                                siter = siter.next;
                            }
                            if (!found) {
                            	StyleLinkedList addedStyle = SongDB.instance.addStyle(g, false);
                            	addedStyle.insertSong(newsong);
                            } else {
                                styles.add(g);
                            }
                        }                    
                    }
                    if (newsong.stylelist != null) {
                        for (int i = 0; i < newsong.stylelist.length; ++i) {
                            boolean found = false;
                            int j = 0;
                            while (!found && (j < styles.size())) {
                                String cmpstyle = (String)styles.get(j);
                                if (cmpstyle.equalsIgnoreCase(newsong.stylelist[i])) found = true;
                                ++j;
                            }
                            if (!found) styles.add(newsong.stylelist[i]);
                        }
                    }
                    String[] newstyles = new String[styles.size()];
                    for (int i = 0; i < newstyles.length; ++i) {
                        newstyles[i] = (String)styles.get(i);
                    }
                    newsong.stylelist = newstyles; //onesong.stylelist;
                  }
                }                
            } catch (Exception e) {
                log.error("querySongDetails(): error", e);
            }
            GetSongInfoThread.GetSongInfoSemaphore.release();
          }        
    }
    
}

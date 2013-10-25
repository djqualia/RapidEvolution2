package rapid_evolution.net;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.net.MixshareClient;

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
public class SendSongToServer extends Thread {
    SongLinkedList song;
    public SendSongToServer(SongLinkedList insong) { song = insong; }
    public void run() {
      MixshareClient.instance.servercommand.sendSong(song);
    }
}

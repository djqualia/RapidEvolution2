package rapid_evolution.net;

import rapid_evolution.SongLinkedList;

public class RemoveMixoutFromServer extends Thread {
  SongLinkedList from_song;
  SongLinkedList to_song;
  public RemoveMixoutFromServer(SongLinkedList from_song, SongLinkedList to_song) {
      this.from_song = from_song;
      this.to_song = to_song;
  }
  public void run() {
    MixshareClient.instance.servercommand.removeMixout(from_song, to_song);
  }
}

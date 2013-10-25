package rapid_evolution.ui.dnd;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.AddMixoutUI;
import rapid_evolution.ui.RapidEvolutionUI;

public class RECurrentSongDropListener implements REDrop.Listener {

	private static Logger log = Logger.getLogger(RECurrentSongDropListener.class);

	public RECurrentSongDropListener() { }
	
	public void filesDropped(Vector<File> files) {
		if (log.isDebugEnabled())
			log.debug("filesDropped(): files=" + files);
		if ((RapidEvolutionUI.instance.lastdragsourceindex != 0)) {
			int size = files.size();
			boolean notfound = true;
			if (size == 1) {
				File file = files.get(0);
				SongLinkedList iter = SongDB.instance.SongLL;
				while (notfound && (iter != null)) {
					if ((iter.getFileName().equals(file.getAbsolutePath()))
							&& (iter != RapidEvolutionUI.instance.currentsong)) {
						notfound = false;
						SongLinkedList newcurrentsong = iter;
						RapidEvolutionUI.instance.change_current_song(newcurrentsong, 0.0f, false, false);
					}
					iter = iter.next;
				}
			}
		}
	}

	public void songsDropped(Vector<SongLinkedList> songs) {
		if (log.isDebugEnabled())
			log.debug("songsDropped(): songs=" + songs);
		if ((RapidEvolutionUI.instance.lastdragsourceindex != 0)) {
			if (songs.size() == 1) {
				SongLinkedList newcurrentsong = songs.get(0);
				RapidEvolutionUI.instance.change_current_song(newcurrentsong, 0.0f, false, false);				
			}
		}
	}

	
}
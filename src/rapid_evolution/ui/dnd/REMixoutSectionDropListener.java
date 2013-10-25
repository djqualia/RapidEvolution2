package rapid_evolution.ui.dnd;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.AddMixoutUI;
import rapid_evolution.ui.RapidEvolutionUI;

public class REMixoutSectionDropListener implements REDrop.Listener {

	private static Logger log = Logger.getLogger(REMixoutSectionDropListener.class);

	public REMixoutSectionDropListener() { }
	
	public void filesDropped(Vector<File> files) {
		if (log.isDebugEnabled())
			log.debug("filesDropped(): files=" + files);
		if (RapidEvolutionUI.instance.currentsong != null) {
			if ((RapidEvolutionUI.instance.lastdragsourceindex != 2)
					&& (RapidEvolutionUI.instance.lastdragsourceindex != 0)) {
				int size = files.size();
				boolean notfound = true;
				if (size == 1) {
					File file = files.get(0);
					SongLinkedList iter = SongDB.instance.SongLL;
					while (notfound && (iter != null)) {
						if ((iter.getFileName().equals(file.getAbsolutePath()))
								&& (iter != RapidEvolutionUI.instance.currentsong)) {
							notfound = false;
							SongLinkedList newmixout = iter;
							addMixout(newmixout);							
						}
						iter = iter.next;
					}
				}
			}
		}
	}

	public void songsDropped(Vector<SongLinkedList> songs) {
		if (log.isDebugEnabled())
			log.debug("songsDropped(): songs=" + songs);
		if ((RapidEvolutionUI.instance.lastdragsourceindex != 2)
				&& (RapidEvolutionUI.instance.lastdragsourceindex != 0)) {
			if (songs.size() == 1) {
				SongLinkedList newmixout = songs.get(0);		
				addMixout(newmixout);
			}
		}
	}
	
	static private void addMixout(SongLinkedList newmixout) {
		if (RapidEvolutionUI.instance.currentsong != null) {
			if (RapidEvolutionUI.instance.currentsong.uniquesongid == newmixout.uniquesongid)
				return;                    
			for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
				if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == newmixout.uniquesongid)
					return;
			}
			AddMixoutUI.instance.AddMixout(newmixout);
			AddMixoutUI.instance.Display();
		}
	}
	
}

package rapid_evolution.ui.dnd;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.main.MixoutPane;

public class RESearchSectionDropListener implements REDrop.Listener {

	private static Logger log = Logger.getLogger(RESearchSectionDropListener.class);

	public RESearchSectionDropListener() { }
	
	public void filesDropped(Vector<File> files) {
		if (log.isDebugEnabled())
			log.debug("filesDropped(): files=" + files);
		if (((RapidEvolutionUI.instance.lastdragsourceindex != 1)
				&& (RapidEvolutionUI.instance.lastdragsourceindex != 0) && (RapidEvolutionUI.instance.lastdragsourceindex != 3))) {
			try {
				if ((!AddSongsUI.instance.isVisible())) {
					int size = files.size();
					boolean notfound = true;
					if (size == 1) {
						File file = files.get(0);
						SongLinkedList iter = SongDB.instance.SongLL;
						while (notfound && (iter != null)) {
							if ((iter.getFileName().equals(file.getAbsolutePath()))
									&& (iter != RapidEvolutionUI.instance.currentsong)) {
								notfound = false;
								SongLinkedList editsong = iter;
								EditSongUI.instance.EditSong(editsong);
							}
							iter = iter.next;
						}
					}
					if (notfound && (size >= 1)) {
						File[] filesArry = new File[files.size()];
						for (int i = 0; i < files.size(); ++i)
							filesArry[i] = files.get(i);
						RapidEvolutionUI.instance.AcceptBrowseAddSongs(filesArry, true);
					}
				}
			} catch (Exception e) {
				log.error("filesDropped(): error", e);
			}
		} else {
			if (log.isDebugEnabled())
				log.debug("filesDropped(): skipped due to last drag source=" + RapidEvolutionUI.instance.lastdragsourceindex);
		}
	}

	public void songsDropped(Vector<SongLinkedList> songs) {
		if (log.isDebugEnabled())
			log.debug("songsDropped(): songs=" + songs);
		if (((RapidEvolutionUI.instance.lastdragsourceindex != 1)
				&& (RapidEvolutionUI.instance.lastdragsourceindex != 0) && (RapidEvolutionUI.instance.lastdragsourceindex != 3))) {
			if ((RapidEvolutionUI.instance.lastdragsourceindex == 2)
					&& (RapidEvolutionUI.instance.currentsong != null)) {
				// if the source was the mixout table, remove the selected mixout (only 1 can be selected)
				MixoutPane.instance.RemoveSelectedMixout();
			}
		}
	}

}

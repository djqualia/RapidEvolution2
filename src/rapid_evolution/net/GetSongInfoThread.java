package rapid_evolution.net;

import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;

import java.util.Vector;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.AddMatchQueryUI;
import rapid_evolution.ui.EditMatchQueryUI;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.SongDB;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.*;
import com.ibm.iwt.*;

public class GetSongInfoThread extends Thread {

	private static Logger log = Logger.getLogger(GetSongInfoThread.class);

	public static int gettinginfothreads = 0;
	public static Semaphore GetSongInfoSemaphore = new Semaphore(1);

	public GetSongInfoThread() {
		song = null;
	}

	public GetSongInfoThread(boolean in_disablemultiple) {
		song = null;
		disablemultiple = in_disablemultiple;
	}

	public GetSongInfoThread(boolean in_disablemultiple, boolean strict) {
		song = null;
		disablemultiple = in_disablemultiple;
		usestrict = strict;
	}

	boolean disablemultiple = false;

	public GetSongInfoThread(SongLinkedList in_song) {
		song = in_song;
	}

	SongLinkedList song;
	boolean usestrict = false;

	public void run() {
		GetSongInfoRoutine(song, disablemultiple, usestrict, false);
	}

	public static void GetSongInfoRoutine(SongLinkedList song,
			boolean disablemultiple, boolean usestrict, boolean onlydetails) {
		try {
			if (gettinginfothreads > 0)
				return;
			GetSongInfoSemaphore.acquire();
			gettinginfothreads++;
			SkinManager.instance.setEnabled("add_songs_query_server_button",
					false);
			SkinManager.instance.setEnabled("edit_song_query_server_button",
					false);
			if (song == null) {
				SongLinkedList qsong = new SongLinkedList();
				qsong.setArtist(AddSongsUI.instance.addsongsartistfield.getText());
				qsong.setAlbum(AddSongsUI.instance.addsongsalbumfield.getText());
				qsong.setTrack(AddSongsUI.instance.addsongstrackfield.getText());
				qsong.setSongname(AddSongsUI.instance.addsongstitlefield.getText());
				qsong.setRemixer(AddSongsUI.instance.addsongsremixerfield.getText());
				qsong.setTime(AddSongsUI.instance.addsongstimefield.getText());

				Vector results = (MixshareClient.instance.servercommand2 == null) ? null
						: (onlydetails ? MixshareClient.instance.servercommand2
								.queryServerDetails(qsong)
								: ((OptionsUI.instance.strictsearch
										.isSelected() || usestrict) ? MixshareClient.instance.servercommand2
										.queryServerStrict(qsong)
										: MixshareClient.instance.servercommand2
												.queryServer(qsong)));

				SongLinkedList onesong = null;
				if (results.size() == 1) {
					onesong = (SongLinkedList) results.get(0);
					log.debug("GetSongInfoRoutine(): 1 result=" + onesong);
					if (OptionsUI.instance.donotquerycomments.isSelected())
						onesong.setComments(new String(""));
					if (OptionsUI.instance.donotquerystyles.isSelected())
						onesong.stylelist = new String[0];

					if (!onlydetails) {
						if (SongDB.instance
								.OldGetSongPtr(onesong.uniquestringid) != null) {
							onlydetails = true;
						}
					}

					if (onlydetails) {
						onesong.setArtist("");
						onesong.setAlbum("");
						onesong.setTrack("");
						onesong.setSongname("");
						onesong.setRemixer("");
					}
					// s = getTrack(onesong);

					AddSongsUI.instance.PopulateAddSongDialog(onesong, OptionsUI.instance.overwritewhenquerying.isSelected(), !OptionsUI.instance.donotquerystyles.isSelected());

				} else if ((results.size() > 1) && !disablemultiple) {
					AddMatchQueryUI.instance.display_parameter = results;
					AddMatchQueryUI.instance.Display(results);
				} else if ((results.size() == 0) && !disablemultiple) {
					IOptionPane
							.showMessageDialog(
									AddSongsUI.instance.getDialog(),
									SkinManager.instance
											.getDialogMessageText("query_server_no_results"),
									SkinManager.instance
											.getDialogMessageTitle("query_server_no_results"),
									IOptionPane.INFORMATION_MESSAGE);
				}
			} else {

				SongLinkedList qsong = new SongLinkedList();
				qsong.setArtist(EditSongUI.instance.editsongsartistfield
						.getText());
				qsong.setAlbum(EditSongUI.instance.editsongsalbumfield
						.getText());
				qsong.setTrack(EditSongUI.instance.editsongstrackfield
						.getText());
				qsong.setSongname(EditSongUI.instance.editsongstitlefield
						.getText());
				qsong.setRemixer(EditSongUI.instance.editsongsremixerfield
						.getText());
				qsong.setTime(EditSongUI.instance.editsongstimefield.getText());

				Vector results = (MixshareClient.instance.servercommand2 == null) ? null
						: (onlydetails ? MixshareClient.instance.servercommand2
								.queryServerDetails(qsong)
								: ((OptionsUI.instance.strictsearch
										.isSelected() || usestrict) ? MixshareClient.instance.servercommand2
										.queryServerStrict(qsong)
										: MixshareClient.instance.servercommand2
												.queryServer(qsong)));
				SongLinkedList onesong = null;
				if (results.size() == 1) {
					onesong = (SongLinkedList) results.get(0);
					if (OptionsUI.instance.donotquerycomments.isSelected())
						onesong.setComments(new String(""));
					if (OptionsUI.instance.donotquerystyles.isSelected())
						onesong.stylelist = new String[0];

					if (!onlydetails) {
						if (SongDB.instance
								.OldGetSongPtr(onesong.uniquestringid) != null) {
							onlydetails = true;
						}
					}

					if (onlydetails) {
						onesong.setArtist("");
						onesong.setAlbum("");
						onesong.setTrack("");
						onesong.setSongname("");
						onesong.setRemixer("");
					}
					
					EditSongUI.instance.PopulateEditSongDialog(onesong, OptionsUI.instance.overwritewhenquerying.isSelected(), !OptionsUI.instance.donotquerystyles.isSelected());

				} else if ((results.size() > 1) && !disablemultiple) {
					EditMatchQueryUI.instance.display_parameter = results;
					EditMatchQueryUI.instance.Display();
				} else if ((results.size() == 0) && !disablemultiple) {
					IOptionPane
							.showMessageDialog(
									EditSongUI.instance.getDialog(),
									SkinManager.instance
											.getDialogMessageText("query_server_no_results"),
									SkinManager.instance
											.getDialogMessageTitle("query_server_no_results"),
									IOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (Exception e) {
			MixshareClient.instance.DisconnectFromServer();
			log.error("GetSongInfoRoutine(): error", e);
		}
		gettinginfothreads--;
		SkinManager.instance.setEnabled("add_songs_query_server_button", true);
		SkinManager.instance.setEnabled("edit_song_query_server_button", true);
		GetSongInfoSemaphore.release();
	}
}

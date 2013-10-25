package rapid_evolution.ui.main;

import java.util.Vector;

import javax.swing.event.ListSelectionListener;
import rapid_evolution.FindLinkThread;
import rapid_evolution.SongLinkedList;
import rapid_evolution.RapidEvolution;
import javax.swing.event.ListSelectionEvent;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.SearchListMouse;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import java.util.Vector;

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

public class SearchSelectionListener implements ListSelectionListener {
    private static Vector selected_files = new Vector();
    private static SongLinkedList[] songs;
    public static Vector getSelectedFiles() { return selected_files; }
    public static SongLinkedList[] getSelectedSongs() { return songs; }
    
    public void filenameChanged(SongLinkedList changed_song) {
        if (songs != null) {
            for (int song_iter = 0; song_iter < songs.length; ++song_iter) {
                SongLinkedList song = songs[song_iter];
                if (changed_song.uniquesongid == song.uniquesongid) {
                    selected_files.set(song_iter, song.getFile());
                    return;
                }
            }
        }
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int selectedRowCount = SearchPane.instance.searchtable.getSelectedRowCount();
        if ((selectedRowCount >= 1)
                && !SearchPane.instance.recreatingsearch) {
            SearchListMouse.instance.playselection.setEnabled(true);
            if (selectedRowCount == 1) {
                // one selected
                //edit
                // set current
                //  play
                //  generate playlist
                //  find link
                //  detect
                //  audio
                //  tag
                //  special functions
                //  delete song

                SearchListMouse.instance.generateplayselection.setEnabled(true);
                SearchListMouse.instance.pitchshift.setEnabled(true);
                SearchListMouse.instance.normalize.setEnabled(true);
                if ((RapidEvolutionUI.instance.currentsong != null)
                        && !FindLinkThread.findinglink)
                    SearchListMouse.instance.findlinkselection.setEnabled(true);
                else
                    SearchListMouse.instance.findlinkselection
                            .setEnabled(false);
                SearchListMouse.instance.tagsmenu.setText(SkinManager.instance
                        .getMessageText("menu_option_tags"));
                //SearchListMouse.instance.setfields.setText(SkinManager.instance.getMessageText("menu_option_set_field"));
                SearchListMouse.instance.deleteselected
                        .setText(SkinManager.instance
                                .getMessageText("menu_option_delete_song"));
                SearchListMouse.instance.audiomenu.setEnabled(true);
                SearchListMouse.instance.setcurrent.setEnabled(true);
                if (RapidEvolutionUI.instance.currentsong != null) {
                    SearchListMouse.instance.addmixout.setEnabled(true);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.edititem, 0);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.setcurrent, 1);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.addmixout, 2);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.generateplayselection, 4);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.findlinkselection, 5);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.audiomenu, 7);
                } else {
                    SearchListMouse.instance.addmixout.setEnabled(false);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.edititem, 0);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.setcurrent, 1);
                    SearchListMouse.instance.m_pmnPopup
                            .remove(SearchListMouse.instance.addmixout);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.generateplayselection, 3);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.findlinkselection, 4);
                    SearchListMouse.instance.m_pmnPopup.insert(
                            SearchListMouse.instance.audiomenu, 6);
                }

                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.fields);
                SearchListMouse.instance.styles.setEnabled(true);

                SearchPane.instance.searchselected.setText("1 selected");
                
            } else {
                // multiple selected
                //edit
                //play
                //detect
                //tag
                //set field
                //add to styles
                //set styles
                //special functions
                //delete song
                SearchListMouse.instance.findlinkselection.setEnabled(false);
                SearchListMouse.instance.generateplayselection
                        .setEnabled(false);
                SearchListMouse.instance.pitchshift.setEnabled(false);
                SearchListMouse.instance.normalize.setEnabled(false);
                SearchListMouse.instance.tagsmenu.setText(SkinManager.instance
                        .getMessageText("menu_option_tags"));
                //SearchListMouse.instance.setfields.setText(SkinManager.instance.getMessageText("menu_option_set_fields"));
                SearchListMouse.instance.deleteselected
                        .setText(SkinManager.instance
                                .getMessageText("menu_option_delete_songs"));
                SearchListMouse.instance.audiomenu.setEnabled(false);

                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.edititem);

                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.setcurrent);
                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.addmixout);
                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.generateplayselection);
                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.findlinkselection);
                SearchListMouse.instance.m_pmnPopup
                        .remove(SearchListMouse.instance.audiomenu);

                SearchListMouse.instance.m_pmnPopup.insert(
                        SearchListMouse.instance.fields, 3);
                
                SearchPane.instance.searchselected.setText(selectedRowCount + " selected");
                
            }
            SearchListMouse.instance.tagsmenu.setEnabled(true);
            SearchListMouse.instance.readtagsselected.setEnabled(true);
            SearchListMouse.instance.writetagsselected.setEnabled(true);
            SearchListMouse.instance.removetagsselected.setEnabled(true);
            SearchListMouse.instance.deleteselected.setEnabled(true);
            SearchListMouse.instance.detectkeys.setEnabled(true);
            SearchListMouse.instance.detectrgad.setEnabled(true);
            SearchListMouse.instance.detectbpms.setEnabled(true);
            SearchListMouse.instance.detectall.setEnabled(true);
            SearchListMouse.instance.detectcolors.setEnabled(true);
            SearchListMouse.instance.detectbeatintensity.setEnabled(true);
            SearchListMouse.instance.setstyles.setEnabled(true);
            SearchListMouse.instance.addtostyles.setEnabled(true);
            SearchListMouse.instance.removefromstyles.setEnabled(true);
            SearchListMouse.instance.styles.setEnabled(true);
            SearchListMouse.instance.detectmenu.setEnabled(true);
            SearchListMouse.instance.fields.setEnabled(true);
            SearchListMouse.instance.specialmenu.setEnabled(true);
            //        SearchPane.instance.searchScrollPane.scrollRectToVisible(SearchPane.instance.searchtable.getCellRect(SearchPane.instance.searchtable.getSelectedRow(),
            // SearchPane.instance.searchtable.getSelectedColumn(), true));

            // cache files for dragging and dropping (prevents autoscrolling bug, really annoying)
            selected_files.removeAllElements();
            songs = RapidEvolutionUI.getSelectedSearchSongs();
            for (int song_iter = 0; song_iter < songs.length; ++song_iter) {
                SongLinkedList song = songs[song_iter];
                if ((song.getFileName() != null) && !song.getFileName().equals("")) {
                    selected_files.add(song.getFile());
                }
            }
        
        } else {
            SearchListMouse.instance.findlinkselection.setEnabled(false);
            SearchListMouse.instance.generateplayselection.setEnabled(false);
            SearchListMouse.instance.playselection.setEnabled(false);
            SearchListMouse.instance.tagsmenu.setEnabled(false);
            SearchListMouse.instance.setcurrent.setEnabled(false);
            SearchListMouse.instance.addmixout.setEnabled(false);
            SearchListMouse.instance.readtagsselected.setEnabled(false);
            SearchListMouse.instance.removetagsselected.setEnabled(true);
            SearchListMouse.instance.writetagsselected.setEnabled(false);
            SearchListMouse.instance.deleteselected.setEnabled(false);
            SearchListMouse.instance.detectmenu.setEnabled(false);
            SearchListMouse.instance.detectkeys.setEnabled(false);
            SearchListMouse.instance.detectbpms.setEnabled(false);
            SearchListMouse.instance.detectall.setEnabled(false);
            SearchListMouse.instance.detectcolors.setEnabled(false);
            SearchListMouse.instance.detectbeatintensity.setEnabled(false);
            SearchListMouse.instance.addtostyles.setEnabled(false);
            SearchListMouse.instance.removefromstyles.setEnabled(false);
            SearchListMouse.instance.setstyles.setEnabled(false);
            SearchListMouse.instance.styles.setEnabled(false);
            SearchListMouse.instance.fields.setEnabled(false);
            SearchListMouse.instance.specialmenu.setEnabled(false);
            SearchPane.instance.searchselected.setText("");
        }
        if (SearchPane.instance.recreatingsearch) {
            SkinManager.instance.setEnabled("add_mixout_button", false);
            RapidEvolutionUI.instance.addexcludebutton.setEnabled(false);
            SearchListMouse.instance.edititem.setEnabled(false);
            return;
        }
        if (selectedRowCount != 1) {
            SkinManager.instance.setEnabled("add_mixout_button", false);
            SearchListMouse.instance.edititem.setEnabled(false);
            RapidEvolutionUI.instance.currentlyselectedsong = null;
            if (selectedRowCount > 0)
                RapidEvolutionUI.instance.addexcludebutton.setEnabled(true);
            else
                RapidEvolutionUI.instance.addexcludebutton.setEnabled(false);
        } else {
            SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                    .getModel().getValueAt(
                            SearchPane.instance.searchtable.getSelectedRow(),
                            SearchPane.instance.searchcolumnconfig.num_columns);
            RapidEvolutionUI.instance.currentlyselectedsong = song;
            if (RapidEvolutionUI.instance.currentsong != null) {
                boolean enable = true;
                if (song == RapidEvolutionUI.instance.currentsong)
                    enable = false;
                for (int i = 0; i < RapidEvolutionUI.instance.currentsong
                        .getNumMixoutSongs(); ++i) {
                    if (song.uniquesongid == RapidEvolutionUI.instance.currentsong.mixout_songs[i])
                        enable = false;
                }
                SkinManager.instance.setEnabled("add_mixout_button", enable);
                enable = true;
                if (song == RapidEvolutionUI.instance.currentsong)
                    enable = false;
                for (int i = 0; i < RapidEvolutionUI.instance.currentsong
                        .getNumExcludeSongs(); ++i) {
                    if (song.uniquesongid == RapidEvolutionUI.instance.currentsong.exclude_songs[i])
                        enable = false;
                }
                RapidEvolutionUI.instance.addexcludebutton.setEnabled(enable);
            }
            SearchListMouse.instance.edititem.setEnabled(true);
        }
        StylesPane.instance.styletree.repaint();
    }
}

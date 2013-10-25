package rapid_evolution.ui;

import javax.swing.DefaultListModel;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.music.Key;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongUtil;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;

import rapid_evolution.audio.AudioPlayer;

import rapid_evolution.OldSongValues;;

public class UpdateSongUIUpdateThread extends Thread {

    private static Logger log = Logger
            .getLogger(UpdateSongUIUpdateThread.class);

    private SongLinkedList song;
    private OldSongValues oldValues;
    
    private boolean redraw;

    public UpdateSongUIUpdateThread(SongLinkedList song, boolean redraw, OldSongValues old_values) {
        this.song = song;
        this.redraw = redraw;
        this.oldValues = old_values;
    }

    public void run() {
        try {
            SearchPane.instance.dontfiretablechange++;
            song.calculate_unique_id();
            song.calculateSongDisplayIds();

            if (song == RapidEvolutionUI.instance.currentsong) {
                UpdateCurrentRoutine();
                RapidEvolutionUI.instance.currentsongfield.setText(song
                        .getSongId());
                RapidEvolutionUI.instance.setRatingFromCurrent();
            }

            int searchindex = SearchPane.instance.getSearchViewIndex(song);
            if (searchindex >= 0) {
                for (int z = 0; z < SearchPane.instance.searchcolumnconfig.num_columns; ++z) {
                    SearchPane.instance.searchtable
                            .getModel()
                            .setValueAt(
                                    song
                                            .get_column_data(SearchPane.instance.searchcolumnconfig.columnindex[z]),
                                    searchindex, z);
                }
            }

            for (int i = 0; i < MixoutPane.instance.mixouttable.getRowCount(); ++i) {
                SongLinkedList stmp = (SongLinkedList) MixoutPane.instance.mixouttable
                        .getModel()
                        .getValueAt(
                                i,
                                MixoutPane.instance.mixoutcolumnconfig.num_columns);
                if (stmp == song) {
                    for (int z = 0; z < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++z)
                        MixoutPane.instance.mixouttable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_mixdata(MixoutPane.instance.mixoutcolumnconfig.columnindex[z]),
                                        i, z);
                }
            }

            if (RapidEvolutionUI.instance.isSyncButtonVisible()) {
                for (int i = 0; i < SyncUI.instance.synctable.getRowCount(); ++i) {
                    SongLinkedList stmp = (SongLinkedList) SyncUI.instance.synctable
                            .getModel()
                            .getValueAt(
                                    i,
                                    SyncUI.instance.synccolumnconfig.num_columns);
                    if (stmp == song) {
                        for (int z = 0; z < SyncUI.instance.synccolumnconfig.num_columns; ++z)
                            SyncUI.instance.synctable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(SyncUI.instance.synccolumnconfig.columnindex[z]),
                                            i, z);
                    }
                }
            }

            if (RapidEvolutionUI.instance.isRootsButtonVisible()) {
                for (int i = 0; i < RootsUI.instance.rootstable.getRowCount(); ++i) {
                    SongLinkedList stmp = (SongLinkedList) RootsUI.instance.rootstable
                            .getModel()
                            .getValueAt(
                                    i,
                                    RootsUI.instance.rootscolumnconfig.num_columns);
                    if (stmp == song) {
                        for (int z = 0; z < RootsUI.instance.rootscolumnconfig.num_columns; ++z)
                            RootsUI.instance.rootstable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(RootsUI.instance.rootscolumnconfig.columnindex[z]),
                                            i, z);
                    }
                }
            }

            if (RapidEvolutionUI.instance.isViewExcludesVisible()) {
                for (int i = 0; i < ExcludeUI.instance.excludetable
                        .getRowCount(); ++i) {
                    SongLinkedList stmp = (SongLinkedList) ExcludeUI.instance.excludetable
                            .getModel()
                            .getValueAt(
                                    i,
                                    ExcludeUI.instance.excludecolumnconfig.num_columns);
                    if (stmp == song) {
                        for (int z = 0; z < ExcludeUI.instance.excludecolumnconfig.num_columns; ++z)
                            ExcludeUI.instance.excludetable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(ExcludeUI.instance.excludecolumnconfig.columnindex[z]),
                                            i, z);
                    }
                }
            }

            if (RapidEvolutionUI.instance.isMixesButtonVisible()) {
                for (int i = 0; i < SuggestedMixesUI.instance.suggestedtable
                        .getRowCount(); ++i) {
                    SongLinkedList stmp = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                            .getModel()
                            .getValueAt(
                                    i,
                                    SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                    if (stmp == song) {
                        for (int z = 0; z < SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns; ++z)
                            SuggestedMixesUI.instance.suggestedtable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(SuggestedMixesUI.instance.suggestedcolumnconfig.columnindex[z]),
                                            i, z);
                    }
                }
            }

            if (BrokenLinkSongsUI.instance.isVisible()) {
                for (int i = 0; i < BrokenLinkSongsUI.instance.songvector
                        .size(); ++i) {
                    SongLinkedList songiter = (SongLinkedList) BrokenLinkSongsUI.instance.songvector
                            .get(i);
                    if (song.equals(songiter)) {
                        DefaultListModel dlm = (DefaultListModel) BrokenLinkSongsUI.instance.songlist
                                .getModel();
                        dlm.setElementAt(songiter.getSongIdShort(), i);
                    }
                }
            }

            if (redraw)
                SongTrailUI.instance.RedrawSongTrailRoutine();
            
            if (song == RapidEvolutionUI.instance.currentsong) {
                RapidEvolutionUI.instance.currentsongfield.setText(song.getSongId());
                RapidEvolutionUI.instance.setRatingFromCurrent();
                RapidEvolution.instance.setCurrentBpm(song.getStartbpm());
                if (song.getEndbpm() != 0) RapidEvolution.instance.setCurrentBpm(song.getEndbpm());
                if ((RapidEvolution.instance.getCurrentBpm() != 0.0)) {                                        
                    if ((song.getStartbpm() != oldValues.getStartBpm()) || (song.getEndbpm() != oldValues.getEndBpm())) {
                        // the bpm of the song has changed
                        RapidEvolution.instance.setActualBpm(RapidEvolution.instance.getCurrentBpm());
                        SearchPane.instance.bpmslider.setValue(0);//( ( (float) - bpmslider.getValue() / 10000.0f) + 1.0f) * currentbpm;
                    }                    
                  SearchPane.instance.bpmsearchbutton.setEnabled(true);
                  String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
                  int length = bpmtext.length();
                  if (length > 6) length = 6;
                  SearchPane.instance.bpmfield.setText(bpmtext.substring(0,length));
                  if (RapidEvolutionUI.instance.currentsong.getStartKey().isValid() || RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) {
                    if ((RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) || (RapidEvolutionUI.instance.currentsong.getStartbpm() != 0)) {
                      SearchPane.instance.keysearchbutton.setEnabled(true);
                      Key fromkey = RapidEvolutionUI.instance.currentsong.getStartKey();
                      if (RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) fromkey = RapidEvolutionUI.instance.currentsong.getEndKey();
                      float frombpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
                      if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) frombpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
                      float fromdiff = SongUtil.get_bpmdiff(frombpm, RapidEvolution.instance.getActualBpm());
                      if (!RapidEvolutionUI.instance.keylockcurrentsong.isSelected())
                          RapidEvolutionUI.instance.setCurrentKey(fromkey.getShiftedKeyByBpmDifference(fromdiff));
                      else
                          RapidEvolutionUI.instance.setCurrentKey(fromkey);
                          
                      SearchPane.instance.keyfield.setText(RapidEvolutionUI.instance.getCurrentKey().toString());
                      SearchPane.instance.searchtable.repaint();
                    }
                  }
                }
              }

            OptionsUI.instance.filter1.updatedSong(song, false);
            OptionsUI.instance.filter2.updatedSong(song, false);
            OptionsUI.instance.filter3.updatedSong(song, false);

            if (AudioPlayer.currentsongllplaying != null) {
                if (AudioPlayer.currentsongllplaying.equals(song))
                    AudioPlayer.updateDisplay();
            }
            
            
        } catch (Exception e) {
            log.error("run(): error Exception", e);
        } finally {
        	SearchPane.instance.dontfiretablechange--;
        }
    }

    public void UpdateCurrentRoutine() {
        try {
            if (!SearchPane.instance.recreatingsearch) {

                int bpmdiff_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_bpm_diff"));
                if (bpmdiff_index >= 0) {
                    for (int r = 0; r < SearchPane.instance.searchtable
                            .getRowCount(); ++r) {
                        SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                                .getModel()
                                .getValueAt(
                                        r,
                                        SearchPane.instance.searchcolumnconfig.num_columns);
                        SearchPane.instance.searchtable.getModel().setValueAt(
                                song.get_column_data(25), r, bpmdiff_index);
                    }
                }

                int keylock_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_key_lock"));
                if (keylock_index >= 0) {
                    for (int r = 0; r < SearchPane.instance.searchtable
                            .getRowCount(); ++r) {
                        SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                                .getModel()
                                .getValueAt(
                                        r,
                                        SearchPane.instance.searchcolumnconfig.num_columns);
                        SearchPane.instance.searchtable.getModel().setValueAt(
                                song.get_column_data(19), r, keylock_index);
                    }
                }

                int actualkey_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_actual_key"));
                if (actualkey_index >= 0) {
                    for (int r = 0; r < SearchPane.instance.searchtable
                            .getRowCount(); ++r) {
                        SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                                .getModel()
                                .getValueAt(
                                        r,
                                        SearchPane.instance.searchcolumnconfig.num_columns);
                        SearchPane.instance.searchtable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_data(ColumnConfig.COLUMN_ACTUAL_KEY),
                                        r, actualkey_index);
                    }
                }

                int actualkeycode_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_actual_key_code"));
                if (actualkeycode_index >= 0) {
                    for (int r = 0; r < SearchPane.instance.searchtable
                            .getRowCount(); ++r) {
                        SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                                .getModel()
                                .getValueAt(
                                        r,
                                        SearchPane.instance.searchcolumnconfig.num_columns);
                        SearchPane.instance.searchtable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_data(ColumnConfig.COLUMN_ACTUAL_KEY_CODE),
                                        r, actualkeycode_index);
                    }
                }

                int pitchshift_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_pitch_shift"));
                if (pitchshift_index >= 0) {
                    for (int r = 0; r < SearchPane.instance.searchtable
                            .getRowCount(); ++r) {
                        SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                                .getModel()
                                .getValueAt(
                                        r,
                                        SearchPane.instance.searchcolumnconfig.num_columns);
                        SearchPane.instance.searchtable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_data(ColumnConfig.COLUMN_PITCH_SHIFT),
                                        r, pitchshift_index);
                    }
                }

                int keytype_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_key_relation"));
                if (keytype_index >= 0) {
                    for (int r = 0; r < SearchPane.instance.searchtable
                            .getRowCount(); ++r) {
                        SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable
                                .getModel()
                                .getValueAt(
                                        r,
                                        SearchPane.instance.searchcolumnconfig.num_columns);
                        SearchPane.instance.searchtable.getModel().setValueAt(
                                song.get_column_data(26), r, keytype_index);
                    }
                }

                SearchPane.instance.searchtable.repaint();
            }
        } catch (Exception e) {
            log.error("UpdateCurrentRoutine(): error", e);
        }

        if (RapidEvolutionUI.instance.isViewExcludesVisible()) {
            try {
                if (!ExcludeUI.instance.recreatingexclude) {
                    for (int i = 0; i < ExcludeUI.instance.excludecolumnconfig.num_columns; ++i) {
                        if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_bpm_diff"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(25),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_lock"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(19),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_data(ColumnConfig.COLUMN_ACTUAL_KEY),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key_code"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_data(ColumnConfig.COLUMN_ACTUAL_KEY_CODE),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_pitch_shift"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_data(ColumnConfig.COLUMN_PITCH_SHIFT),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_relation"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(26),
                                                r, i);
                            }
                        }
                    }
                    ExcludeUI.instance.excludetable.repaint();
                }
            } catch (Exception e) {
                log.error("UpdateCurrentRoutine(): error", e);
            }
        }

        if (RapidEvolutionUI.instance.isRootsButtonVisible()) {
            try {
                if (!RootsUI.instance.recreatingroots) {
                    for (int i = 0; i < RootsUI.instance.rootscolumnconfig.num_columns; ++i) {
                        if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_bpm_diff"))) {
                            for (int r = 0; r < RootsUI.instance.rootstable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                RootsUI.instance.rootscolumnconfig.num_columns);
                                RootsUI.instance.rootstable.getModel()
                                        .setValueAt(
                                                song.get_column_rootdata(25),
                                                r, i);
                            }
                        } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_lock"))) {
                            for (int r = 0; r < RootsUI.instance.rootstable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                RootsUI.instance.rootscolumnconfig.num_columns);
                                RootsUI.instance.rootstable.getModel()
                                        .setValueAt(
                                                song.get_column_rootdata(19),
                                                r, i);
                            }
                        } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key"))) {
                            for (int r = 0; r < RootsUI.instance.rootstable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                RootsUI.instance.rootscolumnconfig.num_columns);
                                RootsUI.instance.rootstable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_rootdata(ColumnConfig.COLUMN_ACTUAL_KEY),
                                                r, i);
                            }
                        } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key_code"))) {
                            for (int r = 0; r < RootsUI.instance.rootstable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                RootsUI.instance.rootscolumnconfig.num_columns);
                                RootsUI.instance.rootstable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_rootdata(ColumnConfig.COLUMN_ACTUAL_KEY_CODE),
                                                r, i);
                            }
                        } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_pitch_shift"))) {
                            for (int r = 0; r < RootsUI.instance.rootstable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                RootsUI.instance.rootscolumnconfig.num_columns);
                                RootsUI.instance.rootstable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_rootdata(ColumnConfig.COLUMN_PITCH_SHIFT),
                                                r, i);
                            }
                        } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_relation"))) {
                            for (int r = 0; r < RootsUI.instance.rootstable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                RootsUI.instance.rootscolumnconfig.num_columns);
                                RootsUI.instance.rootstable.getModel()
                                        .setValueAt(
                                                song.get_column_rootdata(26),
                                                r, i);
                            }
                        }
                    }
                    RootsUI.instance.rootstable.repaint();
                }
            } catch (Exception e) {
                log.error("UpdateCurrentRoutine(): error", e);
            }
        }

        if (RapidEvolutionUI.instance.isSyncButtonVisible()) {
            try {
                if (!SyncUI.instance.recreatingsync) {
                    for (int i = 0; i < SyncUI.instance.synccolumnconfig.num_columns; ++i) {
                        if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_bpm_diff"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(25),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_lock"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(19),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_data(ColumnConfig.COLUMN_ACTUAL_KEY),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key_code"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_data(ColumnConfig.COLUMN_ACTUAL_KEY_CODE),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_pitch_shift"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_data(ColumnConfig.COLUMN_PITCH_SHIFT),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_relation"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(26),
                                                r, i);
                            }
                        }
                    }
                    SyncUI.instance.synctable.repaint();
                }
            } catch (Exception e) {
                log.error("UpdateCurrentRoutine(): error", e);
            }
        }
    }

}

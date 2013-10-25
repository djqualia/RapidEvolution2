package rapid_evolution.ui;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;

public class UpdateTempoUIThread extends Thread {

    private static Logger log = Logger.getLogger(UpdateTempoUIThread.class);

    private boolean changecurrent;

    public UpdateTempoUIThread(boolean changecurrent) {
        this.changecurrent = changecurrent;
    }

    public void run() {
        try {
        	SearchPane.instance.dontfiretablechange++;
            if (!SearchPane.instance.recreatingsearch) {

                int bpmshift_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_bpm_shift"));
                if (bpmshift_index >= 0) {
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
                                                .get_column_data(ColumnConfig.COLUMN_BPMSHIFT),
                                        r, bpmshift_index);
                    }
                }

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
                        SearchPane.instance.searchtable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_data(ColumnConfig.COLUMN_BPMDIFF),
                                        r, bpmdiff_index);
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
                        SearchPane.instance.searchtable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_data(ColumnConfig.COLUMN_KEYLOCK),
                                        r, keylock_index);
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
                        SearchPane.instance.searchtable
                                .getModel()
                                .setValueAt(
                                        song
                                                .get_column_data(ColumnConfig.COLUMN_KEYRELATION),
                                        r, keytype_index);
                    }
                }

                int stylesimilarity_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_style_similarity"));
                if (stylesimilarity_index >= 0) {
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
                                                .get_column_data(ColumnConfig.COLUMN_STYLESIMILARITY),
                                        r, stylesimilarity_index);
                    }
                }

                int artistsimilarity_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_artist_similarity"));
                if (artistsimilarity_index >= 0) {
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
                                                .get_column_data(ColumnConfig.COLUMN_ARTISTSIMILARITY),
                                        r, artistsimilarity_index);
                    }
                }

                int colorsimilarity_index = SearchPane.instance
                        .getSearchColumnModelIndex(SkinManager.instance
                                .getMessageText("column_title_color_similarity"));
                if (colorsimilarity_index >= 0) {
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
                                                .get_column_data(ColumnConfig.COLUMN_COLORSIMILARITY),
                                        r, colorsimilarity_index);
                    }
                }

                SearchPane.instance.searchtable.repaint();
            }
        } catch (Exception e) {
            log.error("UpdateRoutine(): error", e);
        } finally {
        	SearchPane.instance.dontfiretablechange--;
        }

        try {
        	SearchPane.instance.dontfiretablechange++;
            if (!MixoutPane.instance.recreatingmixouts && !changecurrent) {
                for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
                    if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_bpm_shift"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable.getModel()
                                    .setValueAt(song.get_column_mixdata(18), r,
                                            i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_key_lock"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable.getModel()
                                    .setValueAt(song.get_column_mixdata(19), r,
                                            i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_actual_key"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(ColumnConfig.COLUMN_ACTUAL_KEY),
                                            r, i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_actual_key_code"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(ColumnConfig.COLUMN_ACTUAL_KEY_CODE),
                                            r, i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_pitch_shift"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable
                                    .getModel()
                                    .setValueAt(
                                            song
                                                    .get_column_mixdata(ColumnConfig.COLUMN_PITCH_SHIFT),
                                            r, i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_key_relation"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable.getModel()
                                    .setValueAt(song.get_column_mixdata(26), r,
                                            i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_style_similarity"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable.getModel()
                                    .setValueAt(song.get_column_mixdata(34), r,
                                            i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_artist_similarity"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable.getModel()
                                    .setValueAt(song.get_column_mixdata(38), r,
                                            i);
                        }
                    } else if (MixoutPane.instance.mixoutcolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_color_similarity"))) {
                        for (int r = 0; r < MixoutPane.instance.mixouttable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            MixoutPane.instance.mixoutcolumnconfig.num_columns);
                            MixoutPane.instance.mixouttable.getModel()
                                    .setValueAt(song.get_column_mixdata(36), r,
                                            i);
                        }
                    }
                }
                MixoutPane.instance.mixouttable.repaint();
            }
        } catch (Exception e) {
            log.error("UpdateRoutine(): error", e);
        } finally {
        	SearchPane.instance.dontfiretablechange--;
        }

        if (RapidEvolutionUI.instance.isViewExcludesVisible()) {
            try {
            	SearchPane.instance.dontfiretablechange++;
                if (!ExcludeUI.instance.recreatingexclude && !changecurrent) {
                    for (int i = 0; i < ExcludeUI.instance.excludecolumnconfig.num_columns; ++i) {
                        if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_bpm_shift"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(18),
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
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_style_similarity"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(34),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_artist_similarity"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(38),
                                                r, i);
                            }
                        } else if (ExcludeUI.instance.excludecolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_color_similarity"))) {
                            for (int r = 0; r < ExcludeUI.instance.excludetable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) ExcludeUI.instance.excludetable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                ExcludeUI.instance.excludecolumnconfig.num_columns);
                                ExcludeUI.instance.excludetable.getModel()
                                        .setValueAt(song.get_column_data(36),
                                                r, i);
                            }
                        }
                    }
                    ExcludeUI.instance.excludetable.repaint();
                }
            } catch (Exception e) {
                log.error("UpdateRoutine(): error", e);
            } finally {
            	SearchPane.instance.dontfiretablechange--;
            }
        }

        if (RapidEvolutionUI.instance.isMixesButtonVisible()) {
            try {
            	SearchPane.instance.dontfiretablechange++;
                if (!SuggestedMixesUI.instance.recreatingsuggested
                        && !changecurrent) {
                    for (int i = 0; i < SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns; ++i) {
                        if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_bpm_shift"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel().setValueAt(
                                                song.get_column_rootdata(18),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_lock"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel().setValueAt(
                                                song.get_column_rootdata(19),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_rootdata(ColumnConfig.COLUMN_ACTUAL_KEY),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_actual_key_code"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_rootdata(ColumnConfig.COLUMN_ACTUAL_KEY_CODE),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_pitch_shift"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .setValueAt(
                                                song
                                                        .get_column_rootdata(ColumnConfig.COLUMN_PITCH_SHIFT),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_key_relation"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel().setValueAt(
                                                song.get_column_rootdata(26),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_style_similarity"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel().setValueAt(
                                                song.get_column_rootdata(34),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_artist_similarity"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel().setValueAt(
                                                song.get_column_rootdata(38),
                                                r, i);
                            }
                        } else if (SuggestedMixesUI.instance.suggestedcolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_color_similarity"))) {
                            for (int r = 0; r < SuggestedMixesUI.instance.suggestedtable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SuggestedMixesUI.instance.suggestedtable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns);
                                SuggestedMixesUI.instance.suggestedtable
                                        .getModel().setValueAt(
                                                song.get_column_rootdata(36),
                                                r, i);
                            }
                        }

                    }
                    SuggestedMixesUI.instance.suggestedtable.repaint();
                }
            } catch (Exception e) {
                log.error("UpdateRoutine(): error", e);
            } finally {
            	SearchPane.instance.dontfiretablechange--;
            }
        }

        try {
        	SearchPane.instance.dontfiretablechange++;
            if (!RootsUI.instance.recreatingroots && !changecurrent) {
                for (int i = 0; i < RootsUI.instance.rootscolumnconfig.num_columns; ++i) {
                    if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_bpm_shift"))) {
                        for (int r = 0; r < RootsUI.instance.rootstable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            RootsUI.instance.rootscolumnconfig.num_columns);
                            RootsUI.instance.rootstable.getModel().setValueAt(
                                    song.get_column_rootdata(18), r, i);
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
                            RootsUI.instance.rootstable.getModel().setValueAt(
                                    song.get_column_rootdata(19), r, i);
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
                            RootsUI.instance.rootstable.getModel().setValueAt(
                                    song.get_column_rootdata(26), r, i);
                        }
                    } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_style_similarity"))) {
                        for (int r = 0; r < RootsUI.instance.rootstable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            RootsUI.instance.rootscolumnconfig.num_columns);
                            RootsUI.instance.rootstable.getModel().setValueAt(
                                    song.get_column_rootdata(34), r, i);
                        }
                    } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_artist_similarity"))) {
                        for (int r = 0; r < RootsUI.instance.rootstable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            RootsUI.instance.rootscolumnconfig.num_columns);
                            RootsUI.instance.rootstable.getModel().setValueAt(
                                    song.get_column_rootdata(38), r, i);
                        }
                    } else if (RootsUI.instance.rootscolumnconfig.columntitles[i]
                            .equals(SkinManager.instance
                                    .getMessageText("column_title_color_similarity"))) {
                        for (int r = 0; r < RootsUI.instance.rootstable
                                .getRowCount(); ++r) {
                            SongLinkedList song = (SongLinkedList) RootsUI.instance.rootstable
                                    .getModel()
                                    .getValueAt(
                                            r,
                                            RootsUI.instance.rootscolumnconfig.num_columns);
                            RootsUI.instance.rootstable.getModel().setValueAt(
                                    song.get_column_rootdata(36), r, i);
                        }
                    }
                }
                RootsUI.instance.rootstable.repaint();
            }
        } catch (Exception e) {
            log.error("UpdateRoutine(): error", e);
        } finally {
        	SearchPane.instance.dontfiretablechange--;
        }

        if (RapidEvolutionUI.instance.isSyncButtonVisible()) {
            try {
            	SearchPane.instance.dontfiretablechange++;
                if (!SyncUI.instance.recreatingsync && !changecurrent) {
                    for (int i = 0; i < SyncUI.instance.synccolumnconfig.num_columns; ++i) {
                        if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_bpm_shift"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(18),
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
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_style_similarity"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(34),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_artist_similarity"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(38),
                                                r, i);
                            }
                        } else if (SyncUI.instance.synccolumnconfig.columntitles[i]
                                .equals(SkinManager.instance
                                        .getMessageText("column_title_color_similarity"))) {
                            for (int r = 0; r < SyncUI.instance.synctable
                                    .getRowCount(); ++r) {
                                SongLinkedList song = (SongLinkedList) SyncUI.instance.synctable
                                        .getModel()
                                        .getValueAt(
                                                r,
                                                SyncUI.instance.synccolumnconfig.num_columns);
                                SyncUI.instance.synctable.getModel()
                                        .setValueAt(song.get_column_data(36),
                                                r, i);
                            }
                        }

                    }
                    SyncUI.instance.synctable.repaint();
                }
            } catch (Exception e) {
                log.error("run(): error Exception", e);
            } finally {
            	SearchPane.instance.dontfiretablechange--;
            }
        }

    }
}
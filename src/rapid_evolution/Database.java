package rapid_evolution;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rapid_evolution.audio.Bpm;
import rapid_evolution.comparables.myImageIcon;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.ui.AddMatchQueryUI;
import rapid_evolution.ui.ColumnConfig;
import rapid_evolution.ui.EditMatchQueryUI;
import rapid_evolution.ui.ExcludeUI;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RenameFilesUI;
import rapid_evolution.ui.RootsUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SuggestedMixesUI;
import rapid_evolution.ui.SyncUI;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.util.OSHelper;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.util.timing.PaceMaker;
import com.mixshare.rapid_evolution.util.timing.Semaphore;

public class Database {

    private static Logger log = Logger.getLogger(Database.class);

    public static boolean loadDatabase() { return loadDatabase("music_database.xml"); } 
    
    static public float databaseVersion;

    public static boolean loadDatabase(String database_filename) {
        boolean success = false;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = null;
            try {
                document = builder.parse(OSHelper.getFileBackwardsCompatible(database_filename).toURI().toString());
            } catch (java.io.FileNotFoundException fnfe) {
                return true;
            }
            RapidEvolution.first_time_to_run = false;
            Map include_indices = new HashMap();
            Map exclude_indices = new HashMap();
            Element elem = document.getDocumentElement();
            float version = Float.parseFloat(elem.getAttribute("version"));
            databaseVersion = version;
            NodeList rootnodes = elem.getChildNodes();
            for (int rootnode_iter = 0; rootnode_iter < rootnodes.getLength(); ++rootnode_iter) {
                Node rootnode = rootnodes.item(rootnode_iter);
                if (rootnode.getNodeName().equalsIgnoreCase("config")) {
                    NodeList confignodes = rootnode.getChildNodes();
                    for (int confignode_iter = 0; confignode_iter < confignodes.getLength(); ++confignode_iter) {
                        Node confignode = confignodes.item(confignode_iter);
                        if (confignode.getNodeName().equalsIgnoreCase("settings")) {
                            NodeList settings = confignode.getChildNodes();
                            int searchHistoryCount = 1;
                            for (int setting_iter = 0; setting_iter < settings.getLength(); ++setting_iter) {
                                Node setting = settings.item(setting_iter);
                                if (setting.getNodeName().equalsIgnoreCase("sync_needed")) {
                                    RapidEvolution.getMixshareClient().setOutOfSync(false);
                                    if (getFirstValue(setting).equalsIgnoreCase("yes"))
                                        RapidEvolution.getMixshareClient().setOutOfSync(true);
                                } else if (setting.getNodeName().equalsIgnoreCase("sync_in_progress")) {
                                    RapidEvolution.getMixshareClient().setIsSyncing(false);
                                    if (getFirstValue(setting).equalsIgnoreCase("yes"))
                                        RapidEvolution.getMixshareClient().setIsSyncing(true);
                                } else if (setting.getNodeName().equalsIgnoreCase("id3_tag_library")) {
                                    OptionsUI.instance.id3writer.setSelectedIndex(0);
                                    OptionsUI.instance.id3reader.setSelectedIndex(0);
                                } else if (setting.getNodeName().equalsIgnoreCase("root_music_directory")) {
                                	OptionsUI.instance.rootMusicDirectory.setText(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("tag_library_writer")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.id3writer.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("tag_library_reader")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.id3reader.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("key_format")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.keyformatcombo.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("rename_file_pattern")) {
                                    RenameFilesUI.instance.renamepatternfield.setText(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_1_tag")) {
                                    OptionsUI.instance.custom_field_1_tag_combo.setSelectedItem(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_2_tag")) {
                                    OptionsUI.instance.custom_field_2_tag_combo.setSelectedItem(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_3_tag")) {
                                    OptionsUI.instance.custom_field_3_tag_combo.setSelectedItem(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_4_tag")) {
                                    OptionsUI.instance.custom_field_4_tag_combo.setSelectedItem(getFirstValue(setting));                                    
                                } else if (setting.getNodeName().equalsIgnoreCase("style_require_shortcut")) {
                                    OptionsUI.instance.style_require_shortcut_combobox.setSelectedItem(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("style_exclude_shortcut")) {
                                    OptionsUI.instance.style_exclude_shortcut_combobox.setSelectedItem(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("bpm_slider_tickmark")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    RapidEvolutionUI.instance.bpmslider_tickmark_index = value;                                    
                                } else if (setting.getNodeName().equalsIgnoreCase("bpm_slider_range")) {
                                    SearchPane.bpmrangespinner.getModel().setValue(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("os_media_player_path")) {
                                    OptionsUI.OSMediaPlayer = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("time_pitch_shift_quality")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.timepitchshiftquality.setValue(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("audio_processing_cpu_utilization")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.cpuutilization.setValue(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("bpm_detection_quality")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.bpmdetectionquality.setValue(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("key_detection_quality")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.keydetectionquality.setValue(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("bpm_detection_min_bpm")) {
                                    Bpm.minbpm = Double.parseDouble(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("bpm_detection_max_bpm")) {
                                    Bpm.maxbpm = Double.parseDouble(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("album_cover_thumbnail_size")) {
                                    myImageIcon.icon_size = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.albumcoverthumbwidth.setText(String.valueOf(myImageIcon.icon_size));
                                } else if (setting.getNodeName().equalsIgnoreCase("piano_octaves")) {
                                    Integer octaves = new Integer(Integer.parseInt(getFirstValue(setting)));
                                    ((SpinnerNumberModel)MIDIPiano.instance.octavesspinner).setValue(octaves);
                                    MIDIPiano.instance.setkeyboardshift(octaves.intValue());
                                    MIDIPiano.instance.num_keys = 12 * octaves.intValue();
                                    MIDIPiano.instance.keyspressed = new boolean[MIDIPiano.instance.num_keys];
                                    MIDIPiano.instance.keyspressed2 = new int[MIDIPiano.instance.num_keys];
                                    MIDIPiano.instance.masterkeyspressed = new int[MIDIPiano.instance.num_keys];
                                    for (int i = 0; i < MIDIPiano.instance.num_keys; ++i) {
                                      MIDIPiano.instance.keyspressed[i] = false;
                                      MIDIPiano.instance.keyspressed2[i] = 0;
                                      MIDIPiano.instance.masterkeyspressed[i] = 0;
                                    }                                    
                                } else if (setting.getNodeName().equalsIgnoreCase("piano_shift")) {
                                    ((SpinnerNumberModel)MIDIPiano.instance.shiftspinner).setValue(new Integer(Integer.parseInt(getFirstValue(setting))));
                                } else if (setting.getNodeName().equalsIgnoreCase("training_speed")) {
                                    ((SpinnerNumberModel)MIDIPiano.instance.speedspinner).setValue(new Integer(Integer.parseInt(getFirstValue(setting))));
                                } else if (setting.getNodeName().equalsIgnoreCase("piano_midi_device")) {
                                    MIDIPiano.instance.initialcombovalue = -1;
                                    MIDIPiano.instance.initialmidivalue = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("piano_row_1_chord_type")) {
                                    OptionsUI.keychordtype1 = Integer.parseInt(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("piano_row_2_chord_type")) {
                                    OptionsUI.keychordtype2 = Integer.parseInt(getFirstValue(setting));
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_1_label")) {
                                    String label = getFirstValue(setting);
                                    if (label.equalsIgnoreCase(SkinManager.instance.getMessageText("column_title_rating")))
                                        label = "old_" + label;
                                    OptionsUI.instance.customfieldtext1.setText(label);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_2_label")) {
                                    String label = getFirstValue(setting);
                                    if (label.equalsIgnoreCase(SkinManager.instance.getMessageText("column_title_rating")))
                                        label = "old_" + label;
                                    OptionsUI.instance.customfieldtext2.setText(label);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_3_label")) {
                                    String label = getFirstValue(setting);
                                    if (label.equalsIgnoreCase(SkinManager.instance.getMessageText("column_title_rating")))
                                        label = "old_" + label;
                                    OptionsUI.instance.customfieldtext3.setText(label);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_4_label")) {
                                    String label = getFirstValue(setting);
                                    if (label.equalsIgnoreCase(SkinManager.instance.getMessageText("column_title_rating")))
                                        label = "old_" + label;
                                    OptionsUI.instance.customfieldtext4.setText(label);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_1_sort_as")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.custom1sortas.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_2_sort_as")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.custom2sortas.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_3_sort_as")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.custom3sortas.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("custom_field_4_sort_as")) {
                                    int value = Integer.parseInt(getFirstValue(setting));
                                    OptionsUI.instance.custom4sortas.setSelectedIndex(value);
                                } else if (setting.getNodeName().equalsIgnoreCase("options_song_filter_1_combo")) {                                    
                                    OptionsUI.instance.filter1preset = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("options_song_filter_2_combo")) {                                    
                                    OptionsUI.instance.filter2preset = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("options_song_filter_3_combo")) {                                    
                                    OptionsUI.instance.filter3preset = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("options_organized_directory")) {
                                    rapid_evolution.ui.OrganizeFilesUI.organizedPersistDir = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("options_organized_backup_directory")) {
                                    rapid_evolution.ui.OrganizeFilesUI.organizedBackupPersistDir = getFirstValue(setting);
                                } else if (setting.getNodeName().equalsIgnoreCase("search_history_" + searchHistoryCount)) {
                                    SearchPane.instance.searchhistorybutton.getPreviousSearches().add(getFirstValue(setting));
                                    ++searchHistoryCount;
                                }
                                
                                
                            }
                            if (version < 1.03f) {
                                if (RapidEvolution.getMixshareClient().isSyncing()) {
                                    RapidEvolution.getMixshareClient().setIsSyncing(false);
                                    RapidEvolution.getMixshareClient().setOutOfSync(true);
                                }
                            }
                        } else if (confignode.getNodeName().equalsIgnoreCase("columns")) {
                            int total = Integer.parseInt(confignode.getAttributes().getNamedItem("total").getNodeValue());
                            NodeList column_nodelist = confignode.getChildNodes();
                            for (int columnnode_iter = 0; columnnode_iter < column_nodelist.getLength(); ++columnnode_iter) {
                                Node columnnode = column_nodelist.item(columnnode_iter);
                                if (columnnode.getNodeName().equalsIgnoreCase("default_order")) {
                                    NodeList order_nodelist = columnnode.getChildNodes();
                                    for (int order_iter = 0; order_iter < order_nodelist.getLength(); ++order_iter) {
                                        Node order = order_nodelist.item(order_iter);
                                        if (order.getNodeName().equalsIgnoreCase("search")) {
                                            int num_columns = Integer.parseInt(order.getAttributes().getNamedItem("num_columns").getNodeValue());
                                            NodeList searchindices = order.getChildNodes();                                            
                                            int[] columnindices = new int[num_columns];
                                            int cindex = 0;
                                            for (int c = 0; c < searchindices.getLength(); ++c) {
                                                Node searchindex = searchindices.item(c);
                                                if (searchindex.getNodeName().equalsIgnoreCase("index")) {
                                                    columnindices[cindex++] = Integer.parseInt(getFirstValue(searchindex));
                                                }                                                
                                            }
                                            if (columnindices.length == SearchPane.instance.defaultColumnIndices.length) SearchPane.instance.defaultColumnIndices = columnindices;
                                            else SearchPane.instance.defaultColumnIndices = SearchPane.instance.determineOrdering(SearchPane.instance.defaultColumnIndices, columnindices, false);
                                        } else if (order.getNodeName().equalsIgnoreCase("mixouts")) {
                                            int num_columns = Integer.parseInt(order.getAttributes().getNamedItem("num_columns").getNodeValue());
                                            NodeList mixoutsindices = order.getChildNodes();
                                            int[] columnindices = new int[num_columns];
                                            int cindex = 0;
                                            for (int c = 0; c < mixoutsindices.getLength(); ++c) {
                                                Node mixoutindex = mixoutsindices.item(c);
                                                if (mixoutindex.getNodeName().equalsIgnoreCase("index")) {
                                                    columnindices[cindex++] = Integer.parseInt(getFirstValue(mixoutindex));
                                                }
                                            }
                                            if (columnindices.length == MixoutPane.instance.defaultColumnIndices.length) MixoutPane.instance.defaultColumnIndices = columnindices;
                                            else MixoutPane.instance.defaultColumnIndices = MixoutPane.instance.determineOrdering(MixoutPane.instance.defaultColumnIndices, columnindices, false);
                                        }
                                    }                                    
                                } else if (columnnode.getNodeName().equalsIgnoreCase("column")) {
                                    int columnid = Integer.parseInt(columnnode.getAttributes().getNamedItem("id").getNodeValue());
                                    NodeList table_nodes = columnnode.getChildNodes();
                                    for (int t = 0; t < table_nodes.getLength(); ++t) {
                                        Node tablenode = table_nodes.item(t);
                                        if (tablenode.getNodeName().equalsIgnoreCase("search_width")) {
                                            SearchPane.instance.searchcolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("mixouts_width")) {
                                            MixoutPane.instance.mixoutcolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("excludes_width")) {
                                            ExcludeUI.instance.excludecolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("roots_width")) {
                                            RootsUI.instance.rootscolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("suggested_mixouts_width")) {
                                            SuggestedMixesUI.instance.suggestedcolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("sync_width")) {
                                            SyncUI.instance.synccolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("add_song_query_width")) {
                                            AddMatchQueryUI.instance.matchcolumnconfig.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        } else if (tablenode.getNodeName().equalsIgnoreCase("edit_song_query_width")) {
                                            EditMatchQueryUI.instance.matchcolumnconfig2.setDefaultWidth(columnid, Integer.parseInt(getFirstValue(tablenode)));
                                        }
                                    }
                                    
                                    
                                }
                            }
                        } else if (confignode.getNodeName().equalsIgnoreCase("tables")) {
                            NodeList tablenodes = confignode.getChildNodes();
                            for (int table_iter = 0; table_iter < tablenodes.getLength(); ++table_iter) {
                                Node tablenode = tablenodes.item(table_iter);
                                if (tablenode.getNodeName().equalsIgnoreCase("table")) {
                                    String name = tablenode.getAttributes().getNamedItem("name").getNodeValue();                                
                                    int num_columns = Integer.parseInt(tablenode.getAttributes().getNamedItem("num_columns").getNodeValue());                                                                        
                                    ColumnConfig columnconfig = null;
                                    if (name.equalsIgnoreCase("search")) columnconfig = SearchPane.instance.searchcolumnconfig;
                                    else if (name.equalsIgnoreCase("mixouts")) columnconfig = MixoutPane.instance.mixoutcolumnconfig;
                                    else if (name.equalsIgnoreCase("excludes")) columnconfig = ExcludeUI.instance.excludecolumnconfig;
                                    else if (name.equalsIgnoreCase("roots")) columnconfig = RootsUI.instance.rootscolumnconfig;
                                    else if (name.equalsIgnoreCase("suggested_mixouts")) columnconfig = SuggestedMixesUI.instance.suggestedcolumnconfig;
                                    else if (name.equalsIgnoreCase("sync")) columnconfig = SyncUI.instance.synccolumnconfig;
                                    else if (name.equalsIgnoreCase("add_song_query")) columnconfig = AddMatchQueryUI.instance.matchcolumnconfig;
                                    else if (name.equalsIgnoreCase("edit_song_query")) columnconfig = EditMatchQueryUI.instance.matchcolumnconfig2;                                
                                    columnconfig.num_columns = num_columns;
                                    columnconfig.columnindex = new int[columnconfig.num_columns];
                                    columnconfig.columntitles = new String[columnconfig.num_columns];
                                    columnconfig.setPreferredWidth(new int[columnconfig.num_columns]);                                                                    
                                    try {
                                        columnconfig.primary_sort_column = Integer.parseInt(tablenode.getAttributes().getNamedItem("primary_sort_column").getNodeValue());
                                        columnconfig.primary_sort_ascending = tablenode.getAttributes().getNamedItem("primary_sort_ascending").getNodeValue().equals("yes") ? true : false;
                                    } catch (Exception e) { }
                                    try {
                                        columnconfig.secondary_sort_column = Integer.parseInt(tablenode.getAttributes().getNamedItem("secondary_sort_column").getNodeValue());
                                        columnconfig.secondary_sort_ascending = tablenode.getAttributes().getNamedItem("secondary_sort_ascending").getNodeValue().equals("yes") ? true : false;
                                    } catch (Exception e) { }
                                    try {
                                        columnconfig.tertiary_sort_column = Integer.parseInt(tablenode.getAttributes().getNamedItem("tertiary_sort_column").getNodeValue());
                                        columnconfig.tertiary_sort_ascending = tablenode.getAttributes().getNamedItem("tertiary_sort_ascending").getNodeValue().equals("yes") ? true : false;
                                    } catch (Exception e) { }
                                    
                                    NodeList columns = tablenode.getChildNodes();
                                    int cindex = 0;
                                    for (int c = 0; c < columns.getLength(); ++c) {
                                        Node column = columns.item(c);
                                        if (column.getNodeName().equalsIgnoreCase("column")) {
                                            int id = Integer.parseInt(column.getAttributes().getNamedItem("column_id").getNodeValue());
                                            int width = Integer.parseInt(column.getAttributes().getNamedItem("width").getNodeValue());
                                            columnconfig.columnindex[cindex] = id;                                        
                                            columnconfig.setPreferredWidth(cindex, width);
                                            columnconfig.columntitles[cindex] = columnconfig.getKeyword(columnconfig.columnindex[cindex]);
                                            ++cindex;
                                        }                                    
                                    }
                                    if (columnconfig == SearchPane.instance.searchcolumnconfig) {
                                        for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
                                            int c = SearchPane.instance.searchcolumnconfig.columnindex[i];
                                            OptionsUI.instance.SetSelectedSearchColumn(c);
                                        }
                                    } else if (columnconfig == MixoutPane.instance.mixoutcolumnconfig) {
                                        for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
                                            int c = MixoutPane.instance.mixoutcolumnconfig.columnindex[i];
                                            OptionsUI.instance.SetSelectedMixoutColumn(c);
                                        }                                        
                                    }
                                }
                            }
                        } else if (confignode.getNodeName().equalsIgnoreCase("user")) {
                            NodeList usernodes = confignode.getChildNodes();
                            for (int u = 0; u < usernodes.getLength(); ++u) {
                                Node usernode = usernodes.item(u);
                                if (usernode.getNodeName().equalsIgnoreCase("name")) {
                                    OptionsUI.instance.username.setText(getFirstValue(usernode));                                    
                                    OptionsUI.instance.username.setEnabled(false);
                                } else if (usernode.getNodeName().equalsIgnoreCase("password")) {
                                    OptionsUI.instance.password.setText(getFirstValue(usernode));
                                } else if (usernode.getNodeName().equalsIgnoreCase("email")) {
                                    OptionsUI.instance.emailaddress.setText(getFirstValue(usernode));
                                } else if (usernode.getNodeName().equalsIgnoreCase("website")) {
                                    OptionsUI.instance.userwebsite.setText(getFirstValue(usernode));
                                }
                            }
                        }
                    }
                } else if (rootnode.getNodeName().equalsIgnoreCase("styles")) {
                    if (rootnode.getAttributes().getNamedItem("dirty").getNodeValue().equalsIgnoreCase("yes"))
                        SongDB.instance.stylesdirtybit = 1;
                    else SongDB.instance.stylesdirtybit = 0;
                    int num_styles = Integer.parseInt(rootnode.getAttributes().getNamedItem("num_styles").getNodeValue());
                    SongDB.instance.num_styles = num_styles;
                    StyleLinkedList lastaddedstyle = null;
                    int sindex = 0;
                    NodeList stylenodes = rootnode.getChildNodes();
                    for (int stylenode_iter = 0; stylenode_iter < stylenodes.getLength(); ++stylenode_iter) {
                        Node stylenode = stylenodes.item(stylenode_iter);
                        if (stylenode.getNodeName().equalsIgnoreCase("style")) {
                            String stylename = stylenode.getAttributes().getNamedItem("name").getNodeValue();
                            String description = "";
                            try { description = stylenode.getAttributes().getNamedItem("description").getNodeValue(); } catch (Exception e) { }
                            boolean categoryOnly = false;
                            try {
                                if (stylenode.getAttributes().getNamedItem("category_only").getNodeValue().equals("yes"))
                                    categoryOnly = true;
                            } catch (Exception e) { }
                            
                            int styleid = -1;
                            try { styleid = Integer.parseInt(stylenode.getAttributes().getNamedItem("id").getNodeValue()); } catch (Exception e) { styleid = StyleLinkedList.getNextStyleId(); }                            
                            if (SongDB.instance.masterstylelist == null) lastaddedstyle = SongDB.instance.masterstylelist = new StyleLinkedList(stylename, null, styleid);
                            else lastaddedstyle = lastaddedstyle.next = new StyleLinkedList(stylename, null, styleid);
                            lastaddedstyle.set_sindex(sindex++);
                            lastaddedstyle.setDescription(description);
                            lastaddedstyle.setCategoryOnly(categoryOnly);
                            String parent_styleids = null;
                            try { parent_styleids = stylenode.getAttributes().getNamedItem("parent_ids").getNodeValue(); } catch (Exception e) { }
                            if ((parent_styleids == null) || parent_styleids.equals("")) {
                                // no parents, make child of root
                                lastaddedstyle.addParentStyle(StyleLinkedList.root_style);
                            } else {
                                StringTokenizer tokenizer = new StringTokenizer(parent_styleids, ",");
                                while (tokenizer.hasMoreTokens()) {
                                    int parent_styleid = new Integer(tokenizer.nextToken()).intValue();
                                    lastaddedstyle.addParentStyle(parent_styleid);
                                }
                            }
                            String child_styleids = null;
                            try { child_styleids = stylenode.getAttributes().getNamedItem("child_ids").getNodeValue(); } catch (Exception e) { }
                            if ((child_styleids == null) || child_styleids.equals("")) {
                                // no children
                            } else {
                                StringTokenizer tokenizer = new StringTokenizer(child_styleids, ",");
                                while (tokenizer.hasMoreTokens()) {
                                    int child_styleid = new Integer(tokenizer.nextToken()).intValue();
                                    lastaddedstyle.addChildStyle(child_styleid);
                                }
                            }                            
                            
                            NodeList pnodes = stylenode.getChildNodes();
                            for (int p = 0; p < pnodes.getLength(); ++p) {
                                Node pnode = pnodes.item(p);
                                if (pnode.getNodeName().equalsIgnoreCase("include") || pnode.getNodeName().equalsIgnoreCase("exclude")) {
                                    boolean include = false;
                                    if (pnode.getNodeName().equalsIgnoreCase("include")) include = true;
                                    NodeList inodes = pnode.getChildNodes();
                                    for (int i = 0; i < inodes.getLength(); ++i) {
                                        Node inode = inodes.item(i);
                                        if (inode.getNodeName().equalsIgnoreCase("keywords")) {
                                            NodeList knodes = inode.getChildNodes();
                                            for (int k = 0; k < knodes.getLength(); ++k) {
                                                Node knode = knodes.item(k);
                                                if (knode.getNodeName().equalsIgnoreCase("keyword")) {
                                                    if (include)
                                                        lastaddedstyle.insertKeyword(getFirstValue(knode));
                                                    else lastaddedstyle.insertExcludeKeyword(getFirstValue(knode));
                                                }
                                            }                                                                                     
                                        } else if (inode.getNodeName().equalsIgnoreCase("songs")) {
                                            NodeList snodes = inode.getChildNodes();
                                            for (int s = 0; s < snodes.getLength(); ++s) {
                                                Node snode = snodes.item(s);
                                                if (snode.getNodeName().equalsIgnoreCase("song")) {
                                                    if (include)
                                                        lastaddedstyle.insertSong(Long.parseLong(getFirstValue(snode)));
                                                    else lastaddedstyle.insertExcludeSong(Long.parseLong(getFirstValue(snode)));
                                                }
                                            }                                            
                                        }
                                    }
                                } else if (pnode.getNodeName().equalsIgnoreCase("include_display") || pnode.getNodeName().equalsIgnoreCase("exclude_display")) {
                                    boolean include = false;
                                    if (pnode.getNodeName().equalsIgnoreCase("include_display")) include = true;
                                    NodeList enodes = pnode.getChildNodes();
                                    for (int e = 0; e < enodes.getLength(); ++e) {
                                        Node enode = enodes.item(e);
                                        if (enode.getNodeName().equalsIgnoreCase("entry")) {
                                            String display = enode.getAttributes().getNamedItem("display").getNodeValue();
                                            String value = enode.getAttributes().getNamedItem("value").getNodeValue();
                                            if (include) {
                                                lastaddedstyle.styleincludedisplayvector.add(display);
                                                try {
                                                    lastaddedstyle.styleincludevector.add(new Long(Long.parseLong(value)));
                                                } catch (Exception e2) {
                                                    lastaddedstyle.styleincludevector.add(value);
                                                }
                                            } else {
                                                lastaddedstyle.styleexcludedisplayvector.add(display);
                                                try {
                                                    lastaddedstyle.styleexcludevector.add(new Long(Long.parseLong(value)));
                                                } catch (Exception e2) {
                                                    lastaddedstyle.styleexcludevector.add(value);
                                                }                                                
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (rootnode.getNodeName().equalsIgnoreCase("songs")) {                   
                    SongLinkedList.minuniqueid = Integer.parseInt(rootnode.getAttributes().getNamedItem("max_unique_id").getNodeValue());
                    SongLinkedList.checkuniqueid = false;
                    if (rootnode.getAttributes().getNamedItem("check_unique_id").getNodeValue().equalsIgnoreCase("yes"))
                        SongLinkedList.checkuniqueid = true;
                    SongLinkedList AddPtr = null;
                    NodeList songnodes = rootnode.getChildNodes();
                    for (int song_iter = 0; song_iter < songnodes.getLength(); ++song_iter) {
                        Node songnode = songnodes.item(song_iter);
                        if (songnode.getNodeName().equalsIgnoreCase("song")) {
                            int uniqueid = 0;
                            String album = "";
                            String artist = "";
                            String track = "";
                            String title = "";
                            String remix = "";
                            String comments = "";
                            String time = "";
                            String timesig = "";
                            String user1 = "";
                            String user2 = "";
                            String user3 = "";
                            String user4 = "";
                            String filename = "";
                            boolean digital = false;
                            boolean analog = false;
                            boolean disabled = false;
                            boolean synced = false;
                            String startkey = "";
                            String endkey = "";
                            int numplays = 0;
                            String itunesid = "";
                            int keyaccuracy = 0;
                            int beatintensity = 0;
                            float startbpm = 0.0f;
                            float endbpm = 0.0f;
                            float replay_gain = Float.MAX_VALUE;
                            Boolean compilation = null;
                            int bpmaccuracy = 0;
                            java.util.Date dateadded = new java.util.Date();
                            String stylesbitmask = "";                            

                            String shortid = "";
                            String shortid_winfo = "";
                            String songid = "";
                            String songid_winfo = "";
                            String rating = "0";
                            
                            int num_mixouts = Integer.parseInt(songnode.getAttributes().getNamedItem("num_mixouts").getNodeValue());
                            int num_excludes = Integer.parseInt(songnode.getAttributes().getNamedItem("num_excludes").getNodeValue());
                        
                            NodeList songattr_nodelist = songnode.getChildNodes();
                            for (int attr_iter = 0; attr_iter < songattr_nodelist.getLength(); ++attr_iter) {
                                Node attr = songattr_nodelist.item(attr_iter);
                                if (attr.getNodeName().equalsIgnoreCase("unique_id")) {
                                    uniqueid = Integer.parseInt(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("artist")) {
                                    artist = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("album")) {
                                    album = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("track")) {
                                    track = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("title")) {
                                    title = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("remix")) {
                                    remix = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("comments")) {
                                    comments = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("time")) {
                                    time = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("time_signature")) {
                                    timesig = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("custom1")) {
                                    user1 = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("custom2")) {
                                    user2 = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("custom3")) {
                                    user3 = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("custom4")) {
                                    user4 = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("filename")) {
                                    filename = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("digital_only")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) digital = true;
                                } else if (attr.getNodeName().equalsIgnoreCase("analog_only")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) analog = true;
                                } else if (attr.getNodeName().equalsIgnoreCase("disabled")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) disabled = true;
                                } else if (attr.getNodeName().equalsIgnoreCase("synced")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) synced = true;
                                } else if (attr.getNodeName().equalsIgnoreCase("compilation")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) compilation = new Boolean(true);
                                    else if (getFirstValue(attr).equalsIgnoreCase("no")) compilation = new Boolean(false);
                                } else if (attr.getNodeName().equalsIgnoreCase("key_start")) {
                                    startkey = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("key_end")) {
                                    endkey = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("key_accuracy")) {
                                    keyaccuracy = Integer.parseInt(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("bpm_start")) {
                                    startbpm = Float.parseFloat(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("bpm_end")) {
                                    endbpm = Float.parseFloat(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("bpm_accuracy")) {
                                    bpmaccuracy = Integer.parseInt(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("beat_intensity")) {
                                    beatintensity = Integer.parseInt(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("num_plays")) {
                                    numplays = Integer.parseInt(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("replay_gain")) {
                                    replay_gain = Float.parseFloat(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("itunes_id")) {
                                    itunesid = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("date_added")) {
                                    dateadded = RapidEvolution.masterdateformat.parse(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("styles_bitmask")) {
                                    stylesbitmask = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("songid_winfo")) {
                                    songid_winfo = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("songid")) {
                                    songid = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("shortid")) {
                                    shortid = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("shortid_winfo")) {
                                    shortid_winfo = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("rating")) {
                                    rating = getFirstValue(attr);
                                }                                                                                                
                            }
                            if (AddPtr == null)
                                AddPtr = SongDB.instance.SongLL = new SongLinkedList(uniqueid, artist, album, track,
                                        title, remix, comments, analog, digital,
                                        startbpm, endbpm,
                                        num_mixouts, num_excludes, Key.getKey(startkey), Key.getKey(endkey), filename,
                                        time, timesig, disabled, synced, user1, user2,
                                        user3, user4, null);
                            else
                                AddPtr = AddPtr.next = new SongLinkedList(uniqueid, artist, album, track,
                                        title, remix, comments, analog, digital,
                                        startbpm, endbpm,
                                        num_mixouts, num_excludes, Key.getKey(startkey), Key.getKey(endkey), filename,
                                        time, timesig, disabled, synced, user1, user2,
                                        user3, user4, null);
                            
                            AddPtr.setDateAdded(dateadded);
                            AddPtr.itunes_id = itunesid;
                            AddPtr.setTimesPlayed(numplays);
                            AddPtr.setKeyAccuracy(keyaccuracy);
                            AddPtr.setBpmAccuracy(bpmaccuracy);
                            AddPtr.setBeatIntensity(beatintensity);
                            if (replay_gain != Float.MAX_VALUE) {
                                AddPtr.setRGA(replay_gain);
                            }
                            if (rating.equals("5")) AddPtr.setRating((char)5);
                            else if (rating.equals("4")) AddPtr.setRating((char)4);
                            else if (rating.equals("3")) AddPtr.setRating((char)3);
                            else if (rating.equals("2")) AddPtr.setRating((char)2);
                            else if (rating.equals("1")) AddPtr.setRating((char)1);
                            else AddPtr.setRating((char)0);
                            if (version >= 1.01f)
                                AddPtr.iscompilation = compilation;
                            AddPtr.setStyles(new boolean[SongDB.instance.num_styles]);
                            for (int z = 0; z < SongDB.instance.num_styles; ++z) {
                                if (stylesbitmask.charAt(z) == '1') AddPtr.setStyle(z, true);
                                else AddPtr.setStyle(z, false);
                            }
                        
                            AddPtr.calculateSongDisplayIds(true);
                            if (SongDB.instance.songmap.containsKey(new Long(AddPtr.uniquesongid))) {
                                AddPtr.uniquesongid = SongLinkedList.getNextUniqueID();
                                log.error("loadDatabase(): duplicate unique song ids detected!  please report this bug.");
                            }
                            SongDB.instance.songmap.put(new Long(AddPtr.uniquesongid), AddPtr);  
                            SongDB.instance.uniquesongstring_songmap.put(AddPtr.getUniqueStringId(), AddPtr);
                            
                            // init field indexes
                            RapidEvolution.getMusicDatabase().getArtistIndex().addItem(AddPtr.getArtist(), false);
                            RapidEvolution.getMusicDatabase().getAlbumIndex().addItem(AddPtr.getAlbum(), false);
                            RapidEvolution.getMusicDatabase().getCustom1Index().addItem(AddPtr.getUser1(), false);
                            RapidEvolution.getMusicDatabase().getCustom2Index().addItem(AddPtr.getUser2(), false);
                            RapidEvolution.getMusicDatabase().getCustom3Index().addItem(AddPtr.getUser3(), false);
                            RapidEvolution.getMusicDatabase().getCustom4Index().addItem(AddPtr.getUser4(), false);
                        }                                               
                    }
                } else if (rootnode.getNodeName().equalsIgnoreCase("mixouts")) {
                    NodeList mixoutnodes = rootnode.getChildNodes();
                    for (int mixout_iter = 0; mixout_iter < mixoutnodes.getLength(); ++mixout_iter) {
                        Node mixoutnode = mixoutnodes.item(mixout_iter);
                        if (mixoutnode.getNodeName().equalsIgnoreCase("mixout")) {
                            
                            long from_uniqueid = -1;
                            long to_uniqueid = -1;
                            float bpmdiff = 0.0f;
                            int rank = 0;
                            boolean synced = false;
                            String comments = "";
                            boolean addon = false;
                            
                            NodeList mixoutattr_nodelist = mixoutnode.getChildNodes();
                            for (int attr_iter = 0; attr_iter < mixoutattr_nodelist.getLength(); ++attr_iter) {
                                Node attr = mixoutattr_nodelist.item(attr_iter);
                                if (attr.getNodeName().equalsIgnoreCase("from_unique_id")) {
                                    from_uniqueid = Long.parseLong(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("to_unique_id")) {
                                    to_uniqueid = Long.parseLong(getFirstValue(attr));                                
                                } else if (attr.getNodeName().equalsIgnoreCase("bpm_diff")) {
                                    bpmdiff = Float.parseFloat(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("rank")) {
                                    rank = Integer.parseInt(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("synced")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) synced = true;
                                } else if (attr.getNodeName().equalsIgnoreCase("comments")) {
                                    comments = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("addon")) {
                                    if (getFirstValue(attr).equalsIgnoreCase("yes")) addon = true;
                                }
                            }
                            
                            SongLinkedList fromsong = SongDB.instance.NewGetSongPtr(from_uniqueid);
                            int index = 0;
                            Integer idx = (Integer)include_indices.get(new Long(from_uniqueid));
                            if (idx != null) index = idx.intValue();
                            if (fromsong != null) {
                                fromsong.mixout_songs[index] = to_uniqueid;
                                fromsong._mixout_ranks[index] = rank;
                                fromsong._mixout_comments[index] = comments;
                                fromsong._mixout_bpmdiff[index] =  bpmdiff;
                                fromsong.mixout_servercache[index] = synced;
                                fromsong._mixout_addons[index] = addon;
                                ++index;
                                include_indices.put(new Long(from_uniqueid), new Integer(index));
                            }
                                                        
                        }
                    }                    
                } else if (rootnode.getNodeName().equalsIgnoreCase("excludes")) {
                    NodeList excludenodes = rootnode.getChildNodes();
                    for (int exclude_iter = 0; exclude_iter < excludenodes.getLength(); ++exclude_iter) {
                        Node excludenode = excludenodes.item(exclude_iter);
                        if (excludenode.getNodeName().equalsIgnoreCase("exclude")) {
                            
                            long from_uniqueid = -1;
                            long to_uniqueid = -1;
                            
                            NodeList excludeattr_nodelist = excludenode.getChildNodes();
                            for (int attr_iter = 0; attr_iter < excludeattr_nodelist.getLength(); ++attr_iter) {
                                Node attr = excludeattr_nodelist.item(attr_iter);
                                if (attr.getNodeName().equalsIgnoreCase("from_unique_id")) {
                                    from_uniqueid = Long.parseLong(getFirstValue(attr));
                                } else if (attr.getNodeName().equalsIgnoreCase("to_unique_id")) {
                                    to_uniqueid = Long.parseLong(getFirstValue(attr));                                
                                }
                            }
                            
                            SongLinkedList fromsong = SongDB.instance.NewGetSongPtr(from_uniqueid);
                            int index = 0;
                            Integer idx = (Integer)exclude_indices.get(new Long(from_uniqueid));
                            if (idx != null) index = idx.intValue();
                            if (fromsong != null) {
                                fromsong.exclude_songs[index] = to_uniqueid;
                                ++index;
                                exclude_indices.put(new Long(from_uniqueid), new Integer(index));
                            }
                                                        
                        }
                    }                    
                } else if (rootnode.getNodeName().equalsIgnoreCase("artists")) {
                    NodeList artistnodes = rootnode.getChildNodes();
                    for (int artistnode_iter = 0; artistnode_iter < artistnodes.getLength(); ++artistnode_iter) {
                        Node artistnode = artistnodes.item(artistnode_iter);
                        if (artistnode.getNodeName().equalsIgnoreCase("artist")) {
                            
                            boolean styles_cached = false;
                            String searchname = "";
                            HashMap songs = new HashMap();
                            HashMap searchcount = new HashMap(); 
                            HashMap weights = new HashMap();                            
                            if (artistnode.getAttributes().getNamedItem("styles_cached").getNodeValue().equalsIgnoreCase("yes")) styles_cached = true;
                            
                            NodeList attrlist = artistnode.getChildNodes();
                            for (int attr_iter = 0; attr_iter < attrlist.getLength(); ++attr_iter) {
                                Node attr = attrlist.item(attr_iter);
                                if (attr.getNodeName().equalsIgnoreCase("search_name")) {
                                    searchname = getFirstValue(attr);
                                } else if (attr.getNodeName().equalsIgnoreCase("songs")) {
                                    NodeList songnodes = attr.getChildNodes();
                                    for (int siter = 0; siter < songnodes.getLength(); ++siter) {
                                        Node songnode = songnodes.item(siter);
                                        if (songnode.getNodeName().equalsIgnoreCase("song")) {
                                            long id = Long.parseLong(songnode.getAttributes().getNamedItem("id").getNodeValue());
                                            songs.put(new Long(id), new Long(id));
                                        }
                                    }
                                } else if (attr.getNodeName().equalsIgnoreCase("styles")) {
                                    NodeList stylenodes = attr.getChildNodes();
                                    for (int siter = 0; siter < stylenodes.getLength(); ++siter) {
                                        Node stylenode = stylenodes.item(siter);
                                        if (stylenode.getNodeName().equalsIgnoreCase("style")) {
                                            String name = stylenode.getAttributes().getNamedItem("name").getNodeValue();
                                            String song_count = stylenode.getAttributes().getNamedItem("song_count").getNodeValue();
                                            double weight = Double.parseDouble(stylenode.getAttributes().getNamedItem("weight").getNodeValue());                                            
                                            searchcount.put(name, new Integer(Integer.parseInt(song_count)));
                                            weights.put(name, new Double(weight));
                                        }
                                    }
                                }
                            }
                                                        
                            Artist artist = new Artist(searchname, songs, searchcount, styles_cached, weights);
                            Artist.masterartistlist.put(searchname, artist);                    
                            
                        }                        
                    }
                } else if (rootnode.getNodeName().equalsIgnoreCase("albumcovers")) {
                    NodeList albumcovernodes = rootnode.getChildNodes();
                    for (int albumcovernode_iter = 0; albumcovernode_iter < albumcovernodes.getLength(); ++albumcovernode_iter) {
                        Node albumcovernode = albumcovernodes.item(albumcovernode_iter);
                        if (albumcovernode.getNodeName().equalsIgnoreCase("albumcover")) {                            
                            String id = albumcovernode.getAttributes().getNamedItem("id").getNodeValue();
                            ImageSet imageset = new ImageSet(albumcovernode.getAttributes().getNamedItem("thumbnail").getNodeValue());                            
                            NodeList attrlist = albumcovernode.getChildNodes();
                            for (int attr_iter = 0; attr_iter < attrlist.getLength(); ++attr_iter) {
                                Node attr = attrlist.item(attr_iter);
                                if (attr.getNodeName().equalsIgnoreCase("image")) {
                                    imageset.addFile(attr.getAttributes().getNamedItem("filename").getNodeValue());
                                }
                            }                                 
                            SongDB.instance.albumcover_filenames.put(id, imageset);                            
                        }                        
                    }                
                }
            }
            
            success = true;
        } catch (Exception e) {
            log.error("loadDatabase(): error loading database filename=" + database_filename, e);
        }
        return success;
    }
        
    private static Semaphore SaveSem = new Semaphore(1);
    public static boolean saveDatabase(boolean use_pacer) { return saveDatabase("music_database.xml", use_pacer); }
    public static boolean saveDatabase(String save_filename, boolean use_pacer) {
        boolean success = false;
        try {
            SaveSem.acquire();              
            if (log.isDebugEnabled()) log.debug("saveDatabase(): filename=" + save_filename);
            if (log.isTraceEnabled()) log.trace("saveDatabase(): use_pacer=" + use_pacer);
                        
            int[] oldsearchorder = new int[SearchPane.instance.searchcolumnconfig.num_columns];
            int[] oldmixoutorder = new int[MixoutPane.instance.mixoutcolumnconfig.num_columns];

            for (int i = 0; i < Math.min(SearchPane.instance.searchcolumnconfig.num_columns, SearchPane.instance.searchtable.getColumnCount()); ++i) {
              SearchPane.instance.searchcolumnconfig.setPreferredWidth(i, SearchPane.instance.searchtable.getColumnModel().getColumn(i).getWidth());
              oldsearchorder[i] = SearchPane.instance.searchcolumnconfig.getIndex(SearchPane.instance.searchtable.getColumnName(i));
            }
            for (int i = 0; i < SyncUI.instance.synccolumnconfig.num_columns; ++i)
              SyncUI.instance.synccolumnconfig.setPreferredWidth(i, SyncUI.instance.synctable.getColumnModel().getColumn(i).getWidth());
            for (int i = 0; i < RootsUI.instance.rootscolumnconfig.num_columns; ++i)
              RootsUI.instance.rootscolumnconfig.setPreferredWidth(i, RootsUI.instance.rootstable.getColumnModel().getColumn(i).getWidth());
            for (int i = 0; i < ExcludeUI.instance.excludecolumnconfig.num_columns; ++i)
              ExcludeUI.instance.excludecolumnconfig.setPreferredWidth(i, ExcludeUI.instance.excludetable.getColumnModel().getColumn(i).getWidth());
            for (int i = 0; i < SuggestedMixesUI.instance.suggestedcolumnconfig.num_columns; ++i)
              SuggestedMixesUI.instance.suggestedcolumnconfig.setPreferredWidth(i, SuggestedMixesUI.instance.suggestedtable.getColumnModel().getColumn(i).getWidth());
            for (int i = 0; i < Math.min(MixoutPane.instance.mixouttable.getColumnCount(), MixoutPane.instance.mixoutcolumnconfig.num_columns); ++i) {
              MixoutPane.instance.mixoutcolumnconfig.setPreferredWidth(i, MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).getWidth());
              oldmixoutorder[i] = MixoutPane.instance.mixoutcolumnconfig.getIndex(MixoutPane.instance.mixouttable.getColumnName(i));
            }
            for (int i = 0; i < AddMatchQueryUI.instance.matchcolumnconfig.num_columns; ++i)
              AddMatchQueryUI.instance.matchcolumnconfig.setPreferredWidth(i, AddMatchQueryUI.instance.matchtable.getColumnModel().getColumn(i).getWidth());
            for (int i = 0; i < EditMatchQueryUI.instance.matchcolumnconfig2.num_columns; ++i)
              EditMatchQueryUI.instance.matchcolumnconfig2.setPreferredWidth(i, EditMatchQueryUI.instance.matchtable2.getColumnModel().getColumn(i).getWidth());

            SearchPane.instance.defaultColumnIndices = SearchPane.instance.determineOrdering(SearchPane.instance.defaultColumnIndices, oldsearchorder, false);
            MixoutPane.instance.defaultColumnIndices = MixoutPane.instance.determineOrdering(MixoutPane.instance.defaultColumnIndices, oldmixoutorder, false);
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element musicdatabase = document.createElement("music_database");
            musicdatabase.setAttribute("version", "1.04");
            Element config = document.createElement("config");
            musicdatabase.appendChild(config);
            Element styles = document.createElement("styles");
            musicdatabase.appendChild(styles);
            Element songs = document.createElement("songs");
            musicdatabase.appendChild(songs);
            Element mixouts = document.createElement("mixouts");
            musicdatabase.appendChild(mixouts);
            Element excludes = document.createElement("excludes");
            musicdatabase.appendChild(excludes);
            Element artists = document.createElement("artists");
            musicdatabase.appendChild(artists);
            Element albumcovers = document.createElement("albumcovers");
            musicdatabase.appendChild(albumcovers);
 						
            Element settings = document.createElement("settings");
            config.appendChild(settings);
            Element setting = null;

            setting = document.createElement("sync_needed");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(RapidEvolution.getMixshareClient().isOutOfSync() ? "yes" : "no"));

            setting = document.createElement("sync_in_progress");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(RapidEvolution.getMixshareClient().isSyncing() ? "yes" : "no"));

            setting = document.createElement("root_music_directory");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.rootMusicDirectory.getText())));
            
            setting = document.createElement("tag_library_writer");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.id3writer.getSelectedIndex())));

            setting = document.createElement("tag_library_reader");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.id3reader.getSelectedIndex())));
            
            setting = document.createElement("key_format");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.keyformatcombo.getSelectedIndex())));
            
            setting = document.createElement("rename_file_pattern");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(RenameFilesUI.instance.renamepatternfield.getText())));            
            
            setting = document.createElement("custom_field_1_tag");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom_field_1_tag_combo.getSelectedItem())));

            setting = document.createElement("custom_field_2_tag");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom_field_2_tag_combo.getSelectedItem())));

            setting = document.createElement("custom_field_3_tag");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom_field_3_tag_combo.getSelectedItem())));

            setting = document.createElement("custom_field_4_tag");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom_field_4_tag_combo.getSelectedItem())));

            setting = document.createElement("bpm_slider_tickmark");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(SearchPane.instance.bpmslider_tickmark_combo.getSelectedIndex())));

            setting = document.createElement("style_require_shortcut");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.style_require_shortcut_combobox.getSelectedItem())));

            setting = document.createElement("style_exclude_shortcut");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.style_exclude_shortcut_combobox.getSelectedItem())));
            
            setting = document.createElement("bpm_slider_range");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(((String)SearchPane.bpmrangespinner.getModel().getValue())));
            
            setting = document.createElement("os_media_player_path");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode((OptionsUI.OSMediaPlayer == null) ? "" : OptionsUI.OSMediaPlayer));
            
            setting = document.createElement("time_pitch_shift_quality");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.timepitchshiftquality.getValue())));

            setting = document.createElement("audio_processing_cpu_utilization");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.cpuutilization.getValue())));
            
            setting = document.createElement("bpm_detection_quality");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.bpmdetectionquality.getValue())));
            
            setting = document.createElement("key_detection_quality");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.keydetectionquality.getValue())));

            setting = document.createElement("bpm_detection_min_bpm");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(Bpm.minbpm)));

            setting = document.createElement("bpm_detection_max_bpm");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(Bpm.maxbpm)));

            setting = document.createElement("album_cover_thumbnail_size");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(myImageIcon.icon_size)));
            
            setting = document.createElement("piano_octaves");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(((SpinnerNumberModel)MIDIPiano.instance.octavesspinner).getNumber().intValue())));

            setting = document.createElement("training_speed");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(((SpinnerNumberModel)MIDIPiano.instance.speedspinner).getNumber().intValue())));
            
            setting = document.createElement("piano_shift");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(((SpinnerNumberModel)MIDIPiano.instance.shiftspinner).getNumber().intValue())));
            
            if (OptionsUI.instance.mididevicescombo.getSelectedIndex() >= 0) {
            	setting = document.createElement("piano_midi_device");
            	settings.appendChild(setting);
            	setting.appendChild(document.createTextNode(MIDIPiano.instance.deviceinfo[OptionsUI.instance.mididevicescombo.getSelectedIndex()].toString()));
            }
            
            setting = document.createElement("piano_row_1_chord_type");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.row1chordtype.getSelectedIndex())));

            setting = document.createElement("piano_row_2_chord_type");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.row2chordtype.getSelectedIndex())));
            
            setting = document.createElement("custom_field_1_label");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.customfieldtext1.getText()));

            setting = document.createElement("custom_field_1_sort_as");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom1sortas.getSelectedIndex())));
            
            setting = document.createElement("custom_field_2_label");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.customfieldtext2.getText()));

            setting = document.createElement("custom_field_2_sort_as");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom2sortas.getSelectedIndex())));
            
            setting = document.createElement("custom_field_3_label");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.customfieldtext3.getText()));
            
            setting = document.createElement("custom_field_3_sort_as");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom3sortas.getSelectedIndex())));
            
            setting = document.createElement("custom_field_4_label");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.customfieldtext4.getText()));
            
            setting = document.createElement("custom_field_4_sort_as");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(String.valueOf(OptionsUI.instance.custom4sortas.getSelectedIndex())));

            setting = document.createElement("options_song_filter_1_combo");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.filter1options.getSelectedItem().toString()));

            setting = document.createElement("options_song_filter_2_combo");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.filter2options.getSelectedItem().toString()));

            setting = document.createElement("options_song_filter_3_combo");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(OptionsUI.instance.filter3options.getSelectedItem().toString()));

            setting = document.createElement("options_organized_directory");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(rapid_evolution.ui.OrganizeFilesUI.organizedPersistDir));

            setting = document.createElement("options_organized_backup_directory");
            settings.appendChild(setting);
            setting.appendChild(document.createTextNode(rapid_evolution.ui.OrganizeFilesUI.organizedBackupPersistDir));

            // search history
            Iterator searchHistoryIterator = SearchPane.instance.searchhistorybutton.getPreviousSearches().iterator();
            int count = 1;
            while (searchHistoryIterator.hasNext()) {
                setting = document.createElement("search_history_" + count++);
                settings.appendChild(setting);
                setting.appendChild(document.createTextNode(searchHistoryIterator.next().toString()));                
            }            
            
            Element columns = document.createElement("columns");
            config.appendChild(columns);            
            columns.setAttribute("total", String.valueOf(ColumnConfig.total_unique_columns));
            
            Element default_column_order = document.createElement("default_order");
            columns.appendChild(default_column_order);
            
            Element search_order = document.createElement("search");
            default_column_order.appendChild(search_order);
            search_order.setAttribute("num_columns", String.valueOf(SearchPane.instance.defaultColumnIndices.length));
            for (int c = 0; c < SearchPane.instance.defaultColumnIndices.length; ++c) {
                Element index = document.createElement("index");
                search_order.appendChild(index);
                index.appendChild(document.createTextNode(String.valueOf(SearchPane.instance.defaultColumnIndices[c])));
            }

            Element mixouts_order = document.createElement("mixouts");
            default_column_order.appendChild(mixouts_order);            
            mixouts_order.setAttribute("num_columns", String.valueOf(MixoutPane.instance.defaultColumnIndices.length));
            for (int c = 0; c < MixoutPane.instance.defaultColumnIndices.length; ++c) {
                Element index = document.createElement("index");
                mixouts_order.appendChild(index);
                index.appendChild(document.createTextNode(String.valueOf(MixoutPane.instance.defaultColumnIndices[c])));
            }            
            
            for (int c = 0; c < ColumnConfig.total_unique_columns; ++c) {
                Element column = document.createElement("column");
                columns.appendChild(column);
                column.setAttribute("id", String.valueOf(c));

                Element width = document.createElement("search_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(SearchPane.instance.searchcolumnconfig.getDefaultWidth(c))));

                width = document.createElement("mixouts_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(MixoutPane.instance.mixoutcolumnconfig.getDefaultWidth(c))));
                
                width = document.createElement("excludes_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(ExcludeUI.instance.excludecolumnconfig.getDefaultWidth(c))));

                width = document.createElement("roots_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(RootsUI.instance.rootscolumnconfig.getDefaultWidth(c))));

                width = document.createElement("suggested_mixouts_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(SuggestedMixesUI.instance.suggestedcolumnconfig.getDefaultWidth(c))));

                width = document.createElement("sync_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(SyncUI.instance.synccolumnconfig.getDefaultWidth(c))));

                width = document.createElement("add_song_query_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(AddMatchQueryUI.instance.matchcolumnconfig.getDefaultWidth(c))));
                
                width = document.createElement("edit_song_query_width");
                column.appendChild(width);                
                width.appendChild(document.createTextNode(String.valueOf(EditMatchQueryUI.instance.matchcolumnconfig2.getDefaultWidth(c))));
            }            

            Element tables = document.createElement("tables");
            config.appendChild(tables);

            tables.appendChild(createTable(document, "search", SearchPane.instance.searchcolumnconfig));
            tables.appendChild(createTable(document, "mixouts", MixoutPane.instance.mixoutcolumnconfig));
            tables.appendChild(createTable(document, "excludes", ExcludeUI.instance.excludecolumnconfig));
            tables.appendChild(createTable(document, "roots", RootsUI.instance.rootscolumnconfig));            
            tables.appendChild(createTable(document, "suggested_mixouts", SuggestedMixesUI.instance.suggestedcolumnconfig));            
            tables.appendChild(createTable(document, "sync", SyncUI.instance.synccolumnconfig));            
            tables.appendChild(createTable(document, "add_song_query", AddMatchQueryUI.instance.matchcolumnconfig));            
            tables.appendChild(createTable(document, "edit_song_query", EditMatchQueryUI.instance.matchcolumnconfig2));                               
            
            // user info
            Element user = document.createElement("user");
            config.appendChild(user);
		    Element username = document.createElement("name");
		    username.appendChild(document.createTextNode(OptionsUI.instance.username.getText()));
		    user.appendChild(username);
		    Element password = document.createElement("password");
		    password.appendChild(document.createTextNode(new String(OptionsUI.instance.password.getPassword())));
		    user.appendChild(password);
		    Element email = document.createElement("email");
		    email.appendChild(document.createTextNode(OptionsUI.instance.emailaddress.getText()));
		    user.appendChild(email);
		    Element website = document.createElement("website");
		    website.appendChild(document.createTextNode(OptionsUI.instance.userwebsite.getText()));
		    user.appendChild(website);                        

            SongDB.instance.addsinglesongsem.acquire();
            SongDB.instance.UpdateSongStyleArraysSem.acquire();            
            
            // styles
            if (log.isDebugEnabled()) log.debug("saveDatabase(): saving styles...");
		    styles.setAttribute("num_styles", String.valueOf(SongDB.instance.num_styles));
            if (SongDB.instance.stylesdirtybit != 0) styles.setAttribute("dirty", "yes");
            else styles.setAttribute("dirty", "no");
            StyleLinkedList stiter = SongDB.instance.masterstylelist;
            PaceMaker pacer = new PaceMaker();
            while (stiter != null) {
                if (use_pacer) pacer.startInterval();
                Element style = document.createElement("style");
                styles.appendChild(style);
                boolean add_include = false;
                Element include = document.createElement("include");      
                boolean add_include_keywords = false;
                Element includekeywords = document.createElement("keywords");     
                boolean add_include_songs = false;
                Element includesongs = document.createElement("songs");                
                boolean add_exclude = false;
                Element exclude = document.createElement("exclude");         
                boolean add_exclude_keywords = false;
                Element excludekeywords = document.createElement("keywords");  
                boolean add_exclude_songs = false;
                Element excludesongs = document.createElement("songs");                
                
                style.setAttribute("name", stiter.getName());
                style.setAttribute("description", StringUtil.cleanString(stiter.getDescription()));
                style.setAttribute("category_only", stiter.isCategoryOnly() ? "yes" : "no");
                style.setAttribute("id", String.valueOf(stiter.getStyleId()));
                style.setAttribute("parent_ids", stiter.getParentIdsString());               
                style.setAttribute("child_ids", stiter.getChildIdsString());               
                
			    // include keywords
			    stiter.insertKeywordSem.acquire();			    
			    if (stiter.getNumKeywords() > 0) {
			        add_include = true;
			        add_include_keywords = true;
			    }
			    String[] ikeywords = stiter.getKeywords();
			    for (int k = 0; k < ikeywords.length; ++k) {
				    Element keyword = document.createElement("keyword");
				    keyword.appendChild(document.createTextNode(ikeywords[k]));
				    includekeywords.appendChild(keyword);
			    }			    
			    stiter.insertKeywordSem.release();
			    
			    // exclude keywords
			    stiter.insertExcludeKeywordSem.acquire();              
			    if (stiter.getNumExcludeKeywords() > 0) {
			        add_exclude = true;
			        add_exclude_keywords = true;
			    }
			    String[] ekeywords = stiter.getExcludeKeywords();
			    for (int k = 0; k < ekeywords.length; ++k) {
				    Element keyword = document.createElement("keyword");
				    keyword.appendChild(document.createTextNode(ekeywords[k]));
				    excludekeywords.appendChild(keyword);
			    }
			    stiter.insertExcludeKeywordSem.release();

			    // include songs
			    stiter.insertSongSem.acquire();			    
			    if (stiter.getNumSongs() > 0) {
			        add_include = true;
			        add_include_songs = true;
			    }
			    long[] isongs = stiter.getSongs();
			    for (int k = 0; k < isongs.length; ++k) {
				    Element song = document.createElement("song");
				    song.appendChild(document.createTextNode(String.valueOf(isongs[k])));
				    includesongs.appendChild(song);
			    }			    
			    stiter.insertSongSem.release();

			    // exclude songs
			    stiter.insertExcludeSongSem.acquire();
			    if (stiter.getNumExcludeSongs() > 0) {
			        add_exclude = true;
			        add_exclude_songs = true;
			    }
			    long[] esongs = stiter.getExcludeSongs();
			    for (int k = 0; k < esongs.length; ++k) {
				    Element song = document.createElement("song");
				    song.appendChild(document.createTextNode(String.valueOf(esongs[k])));
				    excludesongs.appendChild(song);
			    }
			    stiter.insertExcludeSongSem.release();

			    if (add_include) style.appendChild(include);
			    if (add_include_keywords) include.appendChild(includekeywords);
			    if (add_include_songs) include.appendChild(includesongs);
			    if (add_exclude) style.appendChild(exclude);
			    if (add_exclude_keywords) exclude.appendChild(excludekeywords);
			    if (add_exclude_songs) exclude.appendChild(excludesongs);
              
			    Element include_display = document.createElement("include_display");
			    if (stiter.styleincludedisplayvector.size() > 0) style.appendChild(include_display);
		        for (int i = 0; i < stiter.styleincludedisplayvector.size(); ++i) {
		            Element entry = document.createElement("entry");
		            include_display.appendChild(entry);		
		            String display = (String)stiter.styleincludedisplayvector.get(i);
		            entry.setAttribute("display", display);		            
		            entry.setAttribute("value", stiter.styleincludevector.get(i).toString());		            
		        }

			    Element exclude_display = document.createElement("exclude_display");
			    if (stiter.styleexcludedisplayvector.size() > 0) style.appendChild(exclude_display);
		        for (int i = 0; i < stiter.styleexcludedisplayvector.size(); ++i) {
		            Element entry = document.createElement("entry");
		            exclude_display.appendChild(entry);		            
		            String display = (String)stiter.styleexcludedisplayvector.get(i);
		            entry.setAttribute("display", display);
		            entry.setAttribute("value", stiter.styleexcludevector.get(i).toString());
		        }
			    
			    // next style...
			    stiter = stiter.next;
			    if (use_pacer) pacer.endInterval();
            }                        
            
            // songs
            if (log.isDebugEnabled()) log.debug("saveDatabase(): saving songs...");
			SongLinkedList iter = SongDB.instance.SongLL;
			songs.setAttribute("max_unique_id", String.valueOf(SongLinkedList.minuniqueid));
			songs.setAttribute("check_unique_id", SongLinkedList.checkuniqueid ? "yes" : "no");
            pacer.reset();
			while (iter != null) {
			    if (use_pacer) pacer.startInterval();
				Element songelement = document.createElement("song");

				songelement.setAttribute("num_mixouts", String.valueOf(iter.getNumMixoutSongs()));
				songelement.setAttribute("num_excludes", String.valueOf(iter.getNumExcludeSongs()));
				
				Element uniquesongid = document.createElement("unique_id");
				uniquesongid.appendChild(document.createTextNode(String.valueOf(iter.uniquesongid)));
				songelement.appendChild(uniquesongid);

				if (!iter.getSongId().equals("")) {
				    Element songidwinfo = document.createElement("songid_winfo");
				    songidwinfo.appendChild(document.createTextNode(iter.getSongId()));
				    songelement.appendChild(songidwinfo);
				}
				
				if (!iter.getSongIdShort().equals("")) {
				    Element songid = document.createElement("songid");
				    songid.appendChild(document.createTextNode(iter.getSongIdShort()));
				    songelement.appendChild(songid);
				}
				
				if (!iter.getShortId().equals("")) {
				    Element shortid = document.createElement("shortid");
				    shortid.appendChild(document.createTextNode(iter.getShortId()));
				    songelement.appendChild(shortid);
				}

				if (!iter.getShortIdWInfo().equals("")) {
				    Element shortidwinfo = document.createElement("shortid_winfo");
				    shortidwinfo.appendChild(document.createTextNode(iter.getShortIdWInfo()));
				    songelement.appendChild(shortidwinfo);
				}
				
				if (!iter.getArtist().equals("")) {
				    Element artist = document.createElement("artist");
				    artist.appendChild(document.createTextNode(iter.getArtist()));
				    songelement.appendChild(artist);
				}
				
				if (!iter.getAlbum().equals("")) {
				    Element album = document.createElement("album");
				    album.appendChild(document.createTextNode(iter.getAlbum()));
				    songelement.appendChild(album);
				}

				if (!iter.getTrack().equals("")) {
				    Element track = document.createElement("track");
				    track.appendChild(document.createTextNode(iter.getTrack()));
				    songelement.appendChild(track);
				}

				if (!iter.getSongname().equals("")) {
				    Element title = document.createElement("title");
				    title.appendChild(document.createTextNode(iter.getSongname()));
				    songelement.appendChild(title);
				}

				if (!iter.getRemixer().equals("")) {
				    Element remix = document.createElement("remix");
				    remix.appendChild(document.createTextNode(iter.getRemixer()));
				    songelement.appendChild(remix);
				}
				if (!iter.getComments().equals("")) {
				    Element comments = document.createElement("comments");
				    comments.appendChild(document.createTextNode(iter.getComments()));
				    songelement.appendChild(comments);
				}
				
				if (!iter.getUser1().equals("")) {				
				    Element custom1 = document.createElement("custom1");
				    custom1.appendChild(document.createTextNode(iter.getUser1()));
				    songelement.appendChild(custom1);
				}
				    
				if (!iter.getUser2().equals("")) {				
				    Element custom2 = document.createElement("custom2");
				    custom2.appendChild(document.createTextNode(iter.getUser2()));
				    songelement.appendChild(custom2);
				}

				if (!iter.getUser3().equals("")) {
				    Element custom3 = document.createElement("custom3");
				    custom3.appendChild(document.createTextNode(iter.getUser3()));
				    songelement.appendChild(custom3);
				}

				if (!iter.getUser4().equals("")) {
				    Element custom4 = document.createElement("custom4");
				    custom4.appendChild(document.createTextNode(iter.getUser4()));
				    songelement.appendChild(custom4);
				}

				if (!iter.getTime().equals("")) {
				    Element time = document.createElement("time");
				    time.appendChild(document.createTextNode(iter.getTime()));
				    songelement.appendChild(time);
				}
				
				if (!iter.getTimesig().equals("")) {
				    Element timesig = document.createElement("time_signature");
				    timesig.appendChild(document.createTextNode(iter.getTimesig()));
				    songelement.appendChild(timesig);
				}

				if (!iter.getFileName().equals("")) {
				    Element filename = document.createElement("filename");
				    filename.appendChild(document.createTextNode(iter.getFileName()));
				    songelement.appendChild(filename);
				}
				
				if (iter.getVinylOnly()) {
				    Element analog = document.createElement("analog_only");
				    analog.appendChild(document.createTextNode("yes"));
				    songelement.appendChild(analog);				    
				}

				if (iter.getNonVinylOnly()) {
				    Element digital = document.createElement("digital_only");
				    digital.appendChild(document.createTextNode("yes"));
				    songelement.appendChild(digital);				    
				}

				if (iter.isDisabled()) {
				    Element disabled = document.createElement("disabled");
				    disabled.appendChild(document.createTextNode("yes"));
				    songelement.appendChild(disabled);				    
				}

				if (iter.servercached) {
				    Element synced = document.createElement("synced");
				    synced.appendChild(document.createTextNode("yes"));
				    songelement.appendChild(synced);				    
				}

				if (iter.iscompilation != null) {
				    if (iter.iscompilation.booleanValue()) {
				        Element compilation = document.createElement("compilation");
				        compilation.appendChild(document.createTextNode("yes"));
				        songelement.appendChild(compilation);
				    } else {
				        Element compilation = document.createElement("compilation");
				        compilation.appendChild(document.createTextNode("no"));
				        songelement.appendChild(compilation);				        
				    }
				}
				
				if (iter.getStartKey().isValid()) {
				    Element startkey = document.createElement("key_start");
				    startkey.appendChild(document.createTextNode(iter.getStartKey().toStringExact()));
				    songelement.appendChild(startkey);
				}

				if (iter.getEndKey().isValid()) {
				    Element endkey = document.createElement("key_end");
				    endkey.appendChild(document.createTextNode(iter.getEndKey().toStringExact()));
				    songelement.appendChild(endkey);
				}

				if (iter.getKeyAccuracy() != 0) {
				    Element keyaccuracy = document.createElement("key_accuracy");
				    keyaccuracy.appendChild(document.createTextNode(String.valueOf(iter.getKeyAccuracy())));
				    songelement.appendChild(keyaccuracy);
				}

				if (iter.getStartbpm() != 0.0f) {
				    Element bpmstart = document.createElement("bpm_start");
				    bpmstart.appendChild(document.createTextNode(String.valueOf(iter.getStartbpm())));
				    songelement.appendChild(bpmstart);
				}

				if (iter.getEndbpm() != 0.0f) {
				    Element bpmend = document.createElement("bpm_end");
				    bpmend.appendChild(document.createTextNode(String.valueOf(iter.getEndbpm())));
				    songelement.appendChild(bpmend);
				}

				if (iter.getBpmAccuracy() != 0) {
				    Element bpmaccuracy = document.createElement("bpm_accuracy");
				    bpmaccuracy.appendChild(document.createTextNode(String.valueOf(iter.getBpmAccuracy())));
				    songelement.appendChild(bpmaccuracy);
				}

				if (iter.getBeatIntensity() != 0) {
				    Element beatintensity = document.createElement("beat_intensity");
				    beatintensity.appendChild(document.createTextNode(String.valueOf(iter.getBeatIntensity())));
				    songelement.appendChild(beatintensity);
				}
				
				if (iter.getTimesPlayed() != 0) {
				    Element timesplayed = document.createElement("num_plays");
				    timesplayed.appendChild(document.createTextNode(String.valueOf(iter.getTimesPlayed())));
				    songelement.appendChild(timesplayed);
				}
                
                if (iter.getRGA().isValid()) {
                    Element replaygain = document.createElement("replay_gain");
                    replaygain.appendChild(document.createTextNode(String.valueOf(iter.getRGA().getDifference())));
                    songelement.appendChild(replaygain);                    
                }

		        if ((iter.itunes_id != null) && !iter.itunes_id.equals("")) {
				    Element itunesid = document.createElement("itunes_id");
				    itunesid.appendChild(document.createTextNode(iter.itunes_id));
				    songelement.appendChild(itunesid);
		        }

			    Element rating_element = document.createElement("rating");
			    String rating = "0";
			    if (iter.getRating() == 5) rating = "5";
			    if (iter.getRating() == 4) rating = "4";
			    if (iter.getRating() == 3) rating = "3";
			    if (iter.getRating() == 2) rating = "2";
			    if (iter.getRating() == 1) rating = "1";
			    rating_element.appendChild(document.createTextNode(rating));
			    songelement.appendChild(rating_element);
		        
			    Element date_added = document.createElement("date_added");
			    date_added.appendChild(document.createTextNode(RapidEvolution.masterdateformat.format(iter.getDateAdded())));
			    songelement.appendChild(date_added);
				
		        String stylestring = new String("");
		        for (int g = 0; g < SongDB.instance.num_styles; ++g) {
		          if (iter.getStyle(g)) stylestring += "1";
		          else stylestring += "0";
		        }
			    Element styles_bitmask = document.createElement("styles_bitmask");
			    styles_bitmask.appendChild(document.createTextNode(stylestring));
			    songelement.appendChild(styles_bitmask);
			    
				songs.appendChild(songelement);
				
				// mixouts
		        iter.insertMixoutSem.acquire();
		        for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
		            
		            Element mixout = document.createElement("mixout");
		            
				    Element from_id = document.createElement("from_unique_id");
				    from_id.appendChild(document.createTextNode(String.valueOf(iter.uniquesongid)));
				    mixout.appendChild(from_id);
		            
				    Element to_id = document.createElement("to_unique_id");
				    to_id.appendChild(document.createTextNode(String.valueOf(iter.mixout_songs[i])));
				    mixout.appendChild(to_id);

				    Element bpmdiff = document.createElement("bpm_diff");
				    bpmdiff.appendChild(document.createTextNode(String.valueOf(iter._mixout_bpmdiff[i])));
				    mixout.appendChild(bpmdiff);

				    if (iter.getMixoutRank(i) != 0) {
				        Element rank = document.createElement("rank");
				        rank.appendChild(document.createTextNode(String.valueOf(iter.getMixoutRank(i))));
				        mixout.appendChild(rank);
				    }
				    
					if (iter.mixout_servercache[i]) {
					    Element synced = document.createElement("synced");
					    synced.appendChild(document.createTextNode("yes"));
					    mixout.appendChild(synced);				    
					}
					
				    if (!iter._mixout_comments[i].equals("")) {
				        Element comments = document.createElement("comments");
				        comments.appendChild(document.createTextNode(iter._mixout_comments[i]));
				        mixout.appendChild(comments);
				    }
				    
				    if (iter._mixout_addons[i]) {
					    Element addon = document.createElement("addon");
					    addon.appendChild(document.createTextNode("yes"));
					    mixout.appendChild(addon);				    				        
				    }
				    
		            mixouts.appendChild(mixout);
		            
		        }
		        iter.insertMixoutSem.release();
									
		        // excludes
		        iter.insertExcludeSem.acquire();
		        for (int i = 0; i < iter.getNumExcludeSongs(); ++i) {
		            Element exclude = document.createElement("exclude");
		            
				    Element from_id = document.createElement("from_unique_id");
				    from_id.appendChild(document.createTextNode(String.valueOf(iter.uniquesongid)));
				    exclude.appendChild(from_id);
		            
				    Element to_id = document.createElement("to_unique_id");
				    to_id.appendChild(document.createTextNode(String.valueOf(iter.exclude_songs[i])));
				    exclude.appendChild(to_id);
				    
				    excludes.appendChild(exclude);
		        }
		        iter.insertExcludeSem.release();       
		        
		        // next song...
		        iter = iter.next;
		        if (use_pacer) pacer.endInterval();
		    }			

	        // artists		       
            if (log.isDebugEnabled()) log.debug("saveDatabase(): saving artists...");
	        Collection coll = Artist.masterartistlist.values();
	        if (coll != null) {	            
		        pacer.reset();
		        Iterator artist_iter = coll.iterator();
		        while (artist_iter.hasNext()) {
		            if (use_pacer) pacer.startInterval();
		            Artist artist = (Artist)artist_iter.next();
		          
		            if (!artist.name.equals("")) {
			            Element artist_element = document.createElement("artist");		          		            
			            
			            artist_element.setAttribute("styles_cached", artist.stylescached ? "yes" : "no");			            
			            
					    Element name = document.createElement("search_name");
					    name.appendChild(document.createTextNode(artist.name));
					    artist_element.appendChild(name);
			          	
					    Element artist_songs = document.createElement("songs");
					    boolean added_song = false;	
					    Iterator songs_iter = artist.songs.values().iterator();
					    while (songs_iter.hasNext()) {
					        long songid = ((Long)songs_iter.next()).longValue();
					        if (SongDB.instance.NewGetSongPtr(songid) != null) {
					            Element artist_song = document.createElement("song");
					            artist_song.setAttribute("id", String.valueOf(songid));
					            added_song = true;
					            artist_songs.appendChild(artist_song);
					        }
					    }
					    if (added_song) {
					        artist_element.appendChild(artist_songs);
					        artists.appendChild(artist_element);
					    }
					    
					    Element artist_styles = document.createElement("styles");
					    artist_styles.setAttribute("cached", artist.stylescached ? "yes" : "no");
					    artist_element.appendChild(artist_styles);	
					    Iterator stylecount = artist.stylescount.entrySet().iterator();
					    while (stylecount.hasNext()) {
					        Map.Entry entryset = (Map.Entry)stylecount.next();
					        Element artist_style = document.createElement("style");
					        artist_styles.appendChild(artist_style);					        
					        artist_style.setAttribute("name", (String)entryset.getKey());
					        artist_style.setAttribute("song_count", String.valueOf(((Integer)entryset.getValue()).intValue()));					        
					        artist_style.setAttribute("weight", String.valueOf((Double)artist.profile.get(entryset.getKey())));					        
					    } 
					    
		            }
		            if (use_pacer) pacer.endInterval();
		        }
	        }
			
	        //albumcovers	        
            if (log.isDebugEnabled()) log.debug("saveDatabase(): saving album covers...");
	        coll = SongDB.instance.albumcover_filenames.entrySet();
	        if (coll != null) {
	            pacer.reset();	            
		        Iterator album_iter = coll.iterator();
		        while (album_iter.hasNext()) {
		            if (use_pacer) pacer.startInterval();
		            Map.Entry entry = (Map.Entry)album_iter.next();
		            String key = (String)entry.getKey();
		            ImageSet imageset = (ImageSet)entry.getValue();
		          
		            if ((imageset != null)&&(imageset.getNumFile() > 0)) {
			            Element albumcover_element = document.createElement("albumcover");		          		            
			            
			            albumcover_element.setAttribute("id", key);			            
			            albumcover_element.setAttribute("thumbnail", imageset.getThumbnailFilename());			            
			            			          	
					    String[] images = imageset.getFiles();
					    boolean added_image = false;
					    for (int i = 0; i < images.length; ++i) {
					        String filename =  images[i];
				            Element image_filename = document.createElement("image");
				            image_filename.setAttribute("filename", filename);
				            added_image = true;
				            albumcover_element.appendChild(image_filename);
					    }
					    if (added_image) {
					        albumcovers.appendChild(albumcover_element);
					    }					    
					    
		            }
		            if (use_pacer) pacer.endInterval();
		        }
	        }
	        
            SongDB.instance.addsinglesongsem.release();
            SongDB.instance.UpdateSongStyleArraysSem.release();            
            
            if (log.isDebugEnabled()) log.debug("saveDatabase(): writing to XML");
			document.appendChild(musicdatabase);
            writeXmlFile(document, OSHelper.getWorkingDirectory() + "/" + save_filename);
            
            SongDB.instance.dirtybit = false;
            success = true;
            if (log.isDebugEnabled()) log.debug("saveDatabase(): success");
        } catch (Exception e) {
            log.error("saveDatabase(): error saving database filename=" + save_filename, e);
            SongDB.instance.addsinglesongsem.release();
            SongDB.instance.UpdateSongStyleArraysSem.release();            
        }
        
        SaveSem.release();                
        return success;
    }
    
    // This method writes a DOM document to a file
    public static void writeXmlFile(Document doc, String filename) {
        try {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);    
            // Prepare the output file
            File file = new File(filename);
            Result result = new StreamResult(file);    
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");            
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            log.error("writeXmlFile(): error", e);
        } catch (TransformerException e) {
            log.error("writeXmlFile(): error", e);
        }
    }
    
    private static Element createTable(Document document, String name, ColumnConfig columnconfig) {
        Element table = document.createElement("table");    
        table.setAttribute("name", name);
        table.setAttribute("num_columns", String.valueOf(columnconfig.num_columns));        
        table.setAttribute("primary_sort_column", String.valueOf(columnconfig.primary_sort_column));
        table.setAttribute("secondary_sort_column", String.valueOf(columnconfig.secondary_sort_column));
        table.setAttribute("tertiary_sort_column", String.valueOf(columnconfig.tertiary_sort_column));
        table.setAttribute("primary_sort_ascending", columnconfig.primary_sort_ascending ? "yes" : "no");
        table.setAttribute("secondary_sort_ascending", columnconfig.secondary_sort_ascending ? "yes" : "no");
        table.setAttribute("tertiary_sort_ascending", columnconfig.tertiary_sort_ascending ? "yes" : "no");
        for (int c = 0; c < columnconfig.num_columns; ++c) {
            Element column = document.createElement("column");
            table.appendChild(column);                
            column.setAttribute("column_id", String.valueOf(columnconfig.columnindex[c]));
            column.setAttribute("width", String.valueOf(columnconfig.getPreferredWidth(c)));
        }  
        return table;
    }       
    
    private static String getFirstValue(Node node) {
        Node child = node.getFirstChild();
        if (child != null) {
            String value = child.getNodeValue();
            if (value != null) return value;
        }
        return "";
    }
    
}

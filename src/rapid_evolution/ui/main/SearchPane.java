package rapid_evolution.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import rapid_evolution.OldSongValues;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongList;
import rapid_evolution.SongUtil;
import rapid_evolution.StringUtil;
import rapid_evolution.audio.AudioEngine;
import rapid_evolution.audio.BPMTapper;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.threads.OutOfMemoryThread;
import rapid_evolution.threads.UpdateThread;
import rapid_evolution.ui.ColumnConfig;
import rapid_evolution.ui.DefaultSortTableModel;
import rapid_evolution.ui.JSortTable;
import rapid_evolution.ui.MySliderLabel;
import rapid_evolution.ui.MyTableColumnModelListener;
import rapid_evolution.ui.NewTableColumnModelListener;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.REComboBox;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SortTableModel;

import com.mixshare.rapid_evolution.music.Key;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.button.SearchHistoryButton;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.slider.BpmMediaSlider;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.button.RERadioButton;

import com.mixshare.rapid_evolution.ui.swing.spinner.RESpinner;

public class SearchPane implements ActionListener, ChangeListener, TableModelListener {

    private static Logger log = Logger.getLogger(SearchPane.class);
    
    public SearchPane() {
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    static public SearchPane instance = null;

    public JPanel mainpanel = new JPanel();
    public JTextField searchfield = new RETextField();
    public JButton bpmresetbutton = new REButton();
    public JButton tapbpmbutton = new REButton();
    public JTextField bpmfield = new RETextField();
    public JButton detectbutton = new REButton();
    public JTextField keyfield = new RETextField();
    public JButton keysearchbutton = new REButton();
    public JButton bpmsearchbutton = new REButton();
    public JButton findallbutton = new REButton();
    public static JRadioButton showallradio = new RERadioButton();
    public static JRadioButton showvinylradio = new RERadioButton();
    public static JRadioButton shownonvinylradio = new RERadioButton();
    static String[] bpmrangechoices = { "4%", "8%", "10%", "20%", "50%" };
    public static SpinnerListModel bpmRangeModel = new SpinnerListModel(bpmrangechoices);
    public static JSpinner bpmrangespinner = new RESpinner(bpmRangeModel);
    public JSlider bpmslider;
    public REComboBox bpmslider_tickmark_combo = new REComboBox();
    public JTextField bpmshiftfield = new RETextField();
    public RELabel resultslabel = new RELabel();
    public JButton clearresultsbutton = new REButton();
    public ColumnConfig searchcolumnconfig = new ColumnConfig();
    public DroppableSearchTable searchtable;
    ListSelectionModel searchlistMod;
    public DroppableSearchScrollPane searchScrollPane;
    public static SearchListMouse asearchlistmouse = new SearchListMouse();
    public SearchSelectionListener searchlistener;
    public JLabel searchselected = new RELabel();
    public SearchHistoryButton searchhistorybutton = new SearchHistoryButton();

    private void setupDialog() {
        bpmslider = new BpmMediaSlider();
        searchtable = new DroppableSearchTable();
        
                       
      searchfield.setDropTarget(null);
      SearchFieldKeyListener searchfieldlistener = new SearchFieldKeyListener();
      searchfield.addKeyListener(searchfieldlistener);
      searchfield.getDocument().addDocumentListener(searchfieldlistener);
      searchtable.setAutoscrolls(OptionsUI.instance.searchtableautoscroll.isSelected());
      searchtable.addKeyListener(new SearchKeyListener());
      searchlistener = new SearchSelectionListener();
      searchtable.getSelectionModel().addListSelectionListener(searchlistener);
      searchlistMod = searchtable.getSelectionModel();
      searchScrollPane = new DroppableSearchScrollPane(searchtable);
      bpmslider_tickmark_combo.addActionListener(this);
      SkinManager.addScroll(searchtable, searchScrollPane);
      
      searchtable.getColumnModel().addColumnModelListener(new MyTableColumnModelListener(searchcolumnconfig));
     
            bpmrangespinner.getModel().setValue("8%");
            bpmslider.setMajorTickSpacing(200);
            bpmslider.setMinorTickSpacing(100);

            /*
            Hashtable bpmlabelTable = new Hashtable();
            JLabel label1 = new RELabel(" 0"); label1.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label2 = new RELabel("-2"); label2.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label3 = new RELabel("+2"); label3.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label4 = new RELabel("-4"); label4.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label5 = new RELabel("+4"); label5.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label6 = new RELabel("-6"); label6.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label7 = new RELabel("+6"); label7.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label8 = new RELabel("-8"); label8.setForeground(SkinManager.instance.getColor("default_foregroud"));
            JLabel label9 = new RELabel("+8"); label9.setForeground(SkinManager.instance.getColor("default_foregroud"));
            bpmlabelTable.put(new Integer(0), label1);
            bpmlabelTable.put(new Integer(200), label2);
            bpmlabelTable.put(new Integer(-200), label3);
            bpmlabelTable.put(new Integer(400), label4);
            bpmlabelTable.put(new Integer(-400), label5);
            bpmlabelTable.put(new Integer(600), label6);
            bpmlabelTable.put(new Integer(-600), label7);
            bpmlabelTable.put(new Integer(800), label8);
            bpmlabelTable.put(new Integer(-800), label9);
            bpmslider.setLabelTable(bpmlabelTable);
*/
            
            //bpmshiftfield.setEditable(false);
            bpmshiftfield.addKeyListener(new BpmSliderFieldKeyListener());            
            bpmshiftfield.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    UpdateBpmSliderField();
                }});
            bpmsearchbutton.setEnabled(false);
            keysearchbutton.setEnabled(false);

            bpmfield.addKeyListener(new BpmFieldKeyListener());
            bpmfield.addFocusListener(new FocusAdapter() {
                  public void focusLost(FocusEvent e) {
                      UpdateBpmField();
                  }});

            keyfield.addKeyListener(new KeyFieldKeyListener());
            keyfield.addFocusListener(new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                UpdateKeyField();
              }});

            searchtable.addMouseListener(instance.asearchlistmouse);

            showSliderShiftValue();
                        
    }

    public void showSliderShiftValue() {
      bpmshiftfield.setText(bpmslider.toString());
    }
    
    public void UpdateBpmField() {
        String bpmusertext = StringUtil.validate_bpm(bpmfield.getText());
        if (bpmusertext.equals("")) {
          if (RapidEvolutionUI.instance.currentsong != null) {
            RapidEvolution.instance.setActualBpm( ( ( (float) - bpmslider.getValue() / 10000.0f) + 1.0f) * RapidEvolution.instance.getCurrentBpm());
            String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
            int length = bpmtext.length();
            if (length > 6) length = 6;
            bpmfield.setText(bpmtext.substring(0,length));
            bpmsearchbutton.setEnabled(true);
            if ((RapidEvolutionUI.instance.getCurrentKey() != null)) keysearchbutton.setEnabled(true);
            UpdateThread ut = new UpdateThread();
            ut.start();
          } else {
            RapidEvolution.instance.setActualBpm(0.0f);
            bpmfield.setText("");
            bpmsearchbutton.setEnabled(false);
            keysearchbutton.setEnabled(false);
            UpdateThread ut = new UpdateThread();
            ut.start();
          }
        } else {
          RapidEvolution.instance.setActualBpm(Float.parseFloat(bpmusertext));
          String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
          int length = bpmtext.length();
          if (length > 6) length = 6;
          bpmfield.setText(bpmtext.substring(0,length));
          bpmsearchbutton.setEnabled(true);
          bpmslider.setValue(0);
          showSliderShiftValue();
          if ((RapidEvolutionUI.instance.getCurrentKey() != null)) keysearchbutton.setEnabled(true);
          UpdateThread ut = new UpdateThread();
          ut.start();
      }
    }

    public void UpdateKeyField() {
        String validkey = StringUtil.validate_keyformat(keyfield.getText());
        if (validkey.equals("")) {
          if ((RapidEvolutionUI.instance.currentsong != null) && ((RapidEvolutionUI.instance.currentsong.getStartbpm() != 0.0f) || (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0.0f))) {
              Key fromkey = RapidEvolutionUI.instance.currentsong.getStartKey();
              if (RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) fromkey = RapidEvolutionUI.instance.currentsong.getEndKey();
              float frombpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
              if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) frombpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
              float fromdiff = SongUtil.get_bpmdiff(frombpm, RapidEvolution.instance.getActualBpm());
              if (!RapidEvolutionUI.instance.keylockcurrentsong.isSelected())
                  RapidEvolutionUI.instance.setCurrentKey(fromkey.getShiftedKeyByBpmDifference(fromdiff));
              else
                  RapidEvolutionUI.instance.setCurrentKey(fromkey);
                  
              keyfield.setText(RapidEvolutionUI.instance.getCurrentKey().toString());
              searchtable.repaint();
          } else {
            keyfield.setText("");
            RapidEvolutionUI.instance.setCurrentKey(null);
            keysearchbutton.setEnabled(false);
            searchtable.repaint();
          }
        } else {
            RapidEvolutionUI.instance.setCurrentKey(Key.getKey(validkey));
            keyfield.setText(RapidEvolutionUI.instance.getCurrentKey().toString());
            searchtable.repaint();
            if (RapidEvolution.instance.getActualBpm() != 0.0f) keysearchbutton.setEnabled(true);
            new UpdateThread().start();
        }
    }

    public static SongList searchdisplaylist = null;
    public static HashMap searchmodel_index = new HashMap();
    public int getSearchViewIndex(SongLinkedList song) {
        if (song == null) return -1;
        Integer val = (Integer)searchmodel_index.get(new Long(song.uniquesongid));
        if (val != null) {
            DefaultSortTableModel model = (DefaultSortTableModel)searchtable.getModel();
            int result = model.convertRowToViewIndex(val.intValue());
            if (log.isTraceEnabled()) log.trace("getSearchViewIndex(): model index=" + val.intValue() + ", view index=" + result + ", song=" + song);
            return result;
        }
        return -1;
    }
    
    public static HashMap searchmodel_column_index = new HashMap();
    public int getSearchColumnModelIndex(String column_name) {
        //if (RapidEvolution.debugmode) System.out.println("getSearchColumnViewIndex(): column_name: " + column_name);
        int result = -1;
        Integer model_index = (Integer)searchmodel_column_index.get(column_name);        
        if (model_index != null) {
            result = model_index.intValue();
        }
        //if (RapidEvolution.debugmode) System.out.println("getSearchColumnViewIndex(): view index: " + result);
        return result;
    }
    
    public SortTableModel makeModel(int columncount) {
      try {
        Vector data = new Vector();
        SongList soiter = searchdisplaylist;
        searchmodel_index.clear();
        searchmodel_column_index.clear();
        for (int c = 0; c < columncount; ++c) {
            searchmodel_column_index.put(SearchPane.instance.searchcolumnconfig.columntitles[c], new Integer(c));
        }
        int count = 0;
        while (soiter != null) {
          Vector row = new Vector();
          for (int i = 0; i < columncount; ++i)
            row.add(soiter.song.get_column_data(SearchPane.instance.searchcolumnconfig.columnindex[i]));
          row.add(soiter.song);
          data.add(row);
          searchmodel_index.put(new Long(soiter.song.uniquesongid), new Integer(count));
          count++;          
          soiter = soiter.next;
        }
        if (SkinManager.instance == null) SearchPane.instance.resultslabel.setText("");
        else SearchPane.instance.resultslabel.setText(String.valueOf(count) + " " + SkinManager.instance.getMessageText("search_results_suffix"));
        Vector columnames = new Vector();
        for (int i = 0; i < columncount; ++i) {
          columnames.add(SearchPane.instance.searchcolumnconfig.columntitles[i]);
        }
        columnames.add("id code");
        DefaultSortTableModel returnval = new DefaultSortTableModel(data, columnames) {
          public boolean isCellEditable(int rowIndex, int mColIndex) {
              if (searchcolumnconfig.columnindex[mColIndex] == ColumnConfig.COLUMN_RATING) return true;
            if (OptionsUI.instance.searchinplaceediting.isSelected()) return isEditableSearchColumn(mColIndex);
            else return false;
          }
          
        };
        return returnval;
      } catch (java.lang.OutOfMemoryError e) {
        new OutOfMemoryThread().start();
      }
      return null;
    }

    public boolean recreatingsearch = false;
    Semaphore RecreateSearchColumnsSem = new Semaphore(1);
    public void RecreateSearchColumns() {
      try {
      RecreateSearchColumnsSem.acquire();
      recreatingsearch = true;

      for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns - 1; ++i) SearchPane.instance.searchcolumnconfig.setDefaultWidth(SearchPane.instance.searchcolumnconfig.getIndex(SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().toString()), SearchPane.instance.searchtable.getColumnModel().getColumn(i).getWidth());
      boolean ascending = SearchPane.instance.searchtable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetSearchColumnCount();
      RecreateUsingSearch(SearchPane.instance.searchcolumnconfig, SearchPane.instance.searchtable, columncount);

      SkinManager.instance.getTableConfig(searchtable).setConfig(searchtable);
      SearchPane.instance.searchtable.setModel(makeModel(columncount));
      searchtable.getModel().addTableModelListener(this);
      if (searchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = searchtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	          column.setCellEditor(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      try {
          if (searchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_ALBUM_COVER)) {          
              searchtable.setRowHeight(Integer.parseInt(OptionsUI.instance.albumcoverthumbwidth.getText()) + 10);
          } else {
              searchtable.setRowHeight(SkinManager.instance.getTableRowHeight());
          }
      } catch (Exception e) { }
      SearchPane.instance.searchtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(SearchPane.instance.searchtable));
      SearchPane.instance.searchcolumnconfig.num_columns = columncount;
      for (int i = 0; i < columncount; ++i) {
        SearchPane.instance.searchtable.getColumnModel().getColumn(i).setPreferredWidth(SearchPane.instance.searchcolumnconfig.getPreferredWidth(i));
        SearchPane.instance.searchtable.getColumnModel().getColumn(i).setWidth(SearchPane.instance.searchcolumnconfig.getPreferredWidth(i));
      }
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setResizable(false);
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setMinWidth(0);
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setMaxWidth(0);
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setWidth(0);
      searchtable.sort(SearchPane.instance.searchcolumnconfig);
      recreatingsearch = false;
      } catch (Exception e) {  log.error("RecreateSearchColumns(): error", e);  }
      RecreateSearchColumnsSem.release();
    }

    Semaphore RedrawSearchTableSem = new Semaphore(1);
    public void RedrawSearchTable() {
      try {
      RedrawSearchTableSem.acquire();
      for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
        SearchPane.instance.searchcolumnconfig.columntitles[i] = SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().toString();
        SearchPane.instance.searchcolumnconfig.setIndex(i);
        SearchPane.instance.searchcolumnconfig.setPreferredWidth(i, SearchPane.instance.searchtable.getColumnModel().getColumn(i).getWidth());
      }

      int index = SearchPane.instance.searchtable.getSortedColumnIndex();
      boolean ascending = SearchPane.instance.searchtable.isSortedColumnAscending();
      int oldsortedindex = -1;
      if ((index >= 0) && (index < SearchPane.instance.searchcolumnconfig.num_columns))
        oldsortedindex = index;

      int columncount = SearchPane.instance.searchcolumnconfig.num_columns;

      SkinManager.instance.getTableConfig(searchtable).setConfig(searchtable);
      SearchPane.instance.searchtable.setModel(makeModel(columncount));
      searchtable.getModel().addTableModelListener(this);
      if (searchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = searchtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	          column.setCellEditor(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      SearchPane.instance.searchtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(SearchPane.instance.searchtable));
      for (int i = 0; i < columncount; ++i) {
        SearchPane.instance.searchtable.getColumnModel().getColumn(i).setPreferredWidth(SearchPane.instance.searchcolumnconfig.getPreferredWidth(i));
        SearchPane.instance.searchtable.getColumnModel().getColumn(i).setWidth(SearchPane.instance.searchcolumnconfig.getPreferredWidth(i));
      }
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setResizable(false);
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setMinWidth(0);
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setMaxWidth(0);
      SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setWidth(0);
      searchtable.sort(SearchPane.instance.searchcolumnconfig);
      } catch (Exception e) { log.error("RedrawSearchTable(): error", e); }
      RedrawSearchTableSem.release();
    }

    public void CreateSearchColumns() {
      if (SearchPane.instance.searchcolumnconfig.num_columns <= 0) {
        OptionsUI.instance.searchcolumn_songid.setSelected(true);
        OptionsUI.instance.searchcolumn_bpmdiff.setSelected(true);
        OptionsUI.instance.searchcolumn_bpm.setSelected(true);
        OptionsUI.instance.searchcolumn_key.setSelected(true);
        OptionsUI.instance.searchcolumn_keylock.setSelected(true);
        OptionsUI.instance.searchcolumn_length.setSelected(true);
        OptionsUI.instance.searchcolumn_comments.setSelected(true);
        OptionsUI.instance.searchcolumn_nummixouts.setSelected(true);
        RecreateSearchColumns();
        return;
      }
      if (SkinManager.instance.getTableConfig(searchtable) != null) {
        SkinManager.instance.getTableConfig(searchtable).setConfig(searchtable);
        SearchPane.instance.searchtable.setModel(makeModel(SearchPane.instance.searchcolumnconfig.num_columns));
        searchtable.getModel().addTableModelListener(this);
        if (searchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
  	      TableColumn column = searchtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
  	      if (column != null) {
  	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	          column.setCellEditor(RapidEvolutionUI.instance.getRatingCellRenderer());
  	      }
        }
        try {
            if (searchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_ALBUM_COVER)) {          
                searchtable.setRowHeight(Integer.parseInt(OptionsUI.instance.albumcoverthumbwidth.getText()) + 10);
            } else {
                searchtable.setRowHeight(SkinManager.instance.getTableRowHeight());
            }
        } catch (Exception e) { }        
        SearchPane.instance.searchtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(SearchPane.instance.searchtable));
        for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) {
          SearchPane.instance.searchtable.getColumnModel().getColumn(i).setPreferredWidth(SearchPane.instance.searchcolumnconfig.getPreferredWidth(i));
          SearchPane.instance.searchtable.getColumnModel().getColumn(i).setWidth(SearchPane.instance.searchcolumnconfig.getPreferredWidth(i));
        }
        SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setResizable(false);
        SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setMinWidth(0);
        SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setMaxWidth(0);
        SearchPane.instance.searchtable.getColumnModel().getColumn(SearchPane.instance.searchcolumnconfig.num_columns).setWidth(0);
        searchtable.sort(SearchPane.instance.searchcolumnconfig);
      }
    }

    private void setupActionListeners() {
        findallbutton.addActionListener(this);
        clearresultsbutton.addActionListener(this);
        showallradio.addActionListener(this);
        showvinylradio.addActionListener(this);
        shownonvinylradio.addActionListener(this);
        bpmresetbutton.addActionListener(this);
        tapbpmbutton.addActionListener(this);
        keysearchbutton.addActionListener(this);
        detectbutton.addActionListener(this);
        bpmsearchbutton.addActionListener(this);
        bpmrangespinner.addChangeListener(this);
        bpmslider.addChangeListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == findallbutton) {
              RapidEvolutionUI.instance.findsearched = true;
              RapidEvolutionUI.instance.bpmsearched = false;
              RapidEvolutionUI.instance.keysearched = false;
              RapidEvolutionUI.instance.Search();
      } else if (ae.getSource() == bpmsearchbutton) {
              RapidEvolutionUI.instance.findsearched = false;
              RapidEvolutionUI.instance.bpmsearched = true;
              RapidEvolutionUI.instance.keysearched = false;
              RapidEvolutionUI.instance.Search();
      } else if (ae.getSource() == keysearchbutton) {
              RapidEvolutionUI.instance.findsearched = false;
              RapidEvolutionUI.instance.bpmsearched = false;
              RapidEvolutionUI.instance.keysearched = true;
              RapidEvolutionUI.instance.Search();
      } else if (ae.getSource() == clearresultsbutton) {
          RapidEvolutionUI.instance.ClearSearch();
          clearresultsbutton.setEnabled(false);
          RapidEvolutionUI.instance.savelistbutton.setEnabled(false);
      } else if (ae.getSource() == bpmslider_tickmark_combo) {
          setBpmSliderRangeLabels();
      } else if (ae.getSource() == detectbutton) {
            if (AudioEngine.instance.detecting) {
              AudioEngine.instance.detecting = false;
              AudioEngine.instance.mainTargetDataLine.stop();
              AudioEngine.instance.mainTargetDataLine.close();
              detectbutton.setText(SkinManager.instance.getTextFor(detectbutton));
            } else {
                try {
                    if ((MIDIPiano.instance.synth != null) && (MIDIPiano.instance.synth.isOpen())) MIDIPiano.instance.synth.close();
                } catch (Exception e) { log.error("actionPerformed(): error", e); } 
              AudioEngine.instance.detecting = true;
              detectbutton.setText(SkinManager.instance.getAltTextFor(detectbutton));
              AudioEngine.instance.captureAudio();
            }
      } else if (ae.getSource() == bpmresetbutton) {
            BPMTapper.instance.m_numTicks = -1;
            BPMTapper.instance.ResetBpm();
      } else if (ae.getSource() == tapbpmbutton) {
            BPMTapper.instance.maintapped = true;
            BPMTapper.instance.addsongstartbpm1tapped = false;
            BPMTapper.instance.addsongendbpm1tapped = false;
            BPMTapper.instance.editsongstartbpm1tapped = false;
            BPMTapper.instance.editsongendbpm1tapped = false;
            BPMTapper.instance.TapBPM();
      } else if (ae.getSource() == showallradio) {
              showallradio.setSelected(true);
              showvinylradio.setSelected(false);
              shownonvinylradio.setSelected(false);
      } else if (ae.getSource() == showvinylradio) {
              showallradio.setSelected(false);
              showvinylradio.setSelected(true);
              shownonvinylradio.setSelected(false);
      } else if (ae.getSource() == shownonvinylradio) {
              showallradio.setSelected(false);
              showvinylradio.setSelected(false);
              shownonvinylradio.setSelected(true);
      }
    }

    public void stateChanged(ChangeEvent event) {
      if(event.getSource() == bpmslider) {
        RecalculateCurrentBpmKey();
      } else if (event.getSource() == bpmrangespinner) {
        setBpmSliderRangeLabels();
      }
    }

    public void UpdateBpmSliderField() {
        try {
            float shift = Float.parseFloat(SearchPane.instance.bpmshiftfield.getText());
            SearchPane.instance.bpmslider.setValue(-(int)(shift * 100));
            SearchPane.instance.RecalculateCurrentBpmKey(true);
        } catch (Exception e2) { }        
    }
    
    public void RecalculateCurrentBpmKey() {
        RecalculateCurrentBpmKey(false);
    }
    public void RecalculateCurrentBpmKey(boolean dontupdatesliderfield) {
      if ((!OptionsUI.instance.smoothbpmslider.isSelected() && bpmslider.getValueIsAdjusting())) { return; }
      if (changecurrentsongthread.ischangingcurrentsong) return;
      if (!dontupdatesliderfield) showSliderShiftValue();
      if (RapidEvolution.instance.getCurrentBpm() == 0.0) {
        try {
          RapidEvolution.instance.setCurrentBpm(Float.parseFloat(SearchPane.instance.bpmfield.getText()));
        } catch (Exception e) { }
      }
      if (RapidEvolution.instance.getCurrentBpm() != 0.0) {
        RapidEvolution.instance.setActualBpm(( ( (float) - bpmslider.getValue() / 10000.0f) + 1.0f) * RapidEvolution.instance.getCurrentBpm());
        String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
        int length = bpmtext.length();
        if (length > 6) length = 6;
        bpmfield.setText(bpmtext.substring(0,length));
        if (RapidEvolutionUI.instance.currentsong != null) {
          if (RapidEvolutionUI.instance.currentsong.
                                  getStartKey().isValid() ||
              RapidEvolutionUI.instance.currentsong.
                                  getEndKey().isValid()) {
            if ( (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) ||
                (RapidEvolutionUI.instance.currentsong.getStartbpm() != 0)) {
              Key fromkey = RapidEvolutionUI.instance.currentsong.getStartKey();
              if (RapidEvolutionUI.instance.currentsong.
                                      getEndKey().isValid()) fromkey =
                  RapidEvolutionUI.instance.currentsong.getEndKey();
              float frombpm = RapidEvolutionUI.instance.currentsong.
                  getStartbpm();
              if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0)
                  frombpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
              float fromdiff = SongUtil.get_bpmdiff(frombpm,
                  RapidEvolution.instance.getActualBpm());
              if (!RapidEvolutionUI.instance.keylockcurrentsong.isSelected())
                  RapidEvolutionUI.instance.setCurrentKey(fromkey.getShiftedKeyByBpmDifference(fromdiff));
              else
                  RapidEvolutionUI.instance.setCurrentKey(fromkey);
              keyfield.setText(RapidEvolutionUI.instance.getCurrentKey().toString());
              searchtable.repaint();
            }
          }
        }
        UpdateThread ut = new UpdateThread();
        ut.start();
      }

    }    

    public static int[] defaultColumnIndices = {
      21,33,0,32,1,2,52,3,4,27,5,46,18,25,9,7,8,42,50,12,47,10,11,19,26,35,49,41,48,38,43,34,36,6,28,29,30,31,37,45,40,51,20,23,13,15,16,17,14,44
    };
    public static int getIndexOf(int val) {
        for (int i = 0; i < defaultColumnIndices.length; ++i) if (defaultColumnIndices[i] == val) return i;
        return -1;
    }
    public static boolean columnIsBefore(int c1, int c2) { return (getIndexOf(c1) < getIndexOf(c2)); }
    public static int[] getDefaultColumnIndices() {
        Vector indices = new Vector();
        if (OptionsUI.instance.searchcolumn_songidwinfo.isSelected()) indices.add(new Integer(21));
        if (OptionsUI.instance.searchcolumn_shortidwinfo.isSelected()) indices.add(new Integer(33));
        if (OptionsUI.instance.searchcolumn_songid.isSelected()) indices.add(new Integer(0));
        if (OptionsUI.instance.searchcolumn_shortid.isSelected()) indices.add(new Integer(32));
        if (OptionsUI.instance.searchcolumn_artist.isSelected()) indices.add(new Integer(1));
        if (OptionsUI.instance.searchcolumn_album.isSelected()) indices.add(new Integer(2));
        if (OptionsUI.instance.searchcolumn_albumcover.isSelected()) indices.add(new Integer(52));
        if (OptionsUI.instance.searchcolumn_track.isSelected()) indices.add(new Integer(3));
        if (OptionsUI.instance.searchcolumn_title.isSelected()) indices.add(new Integer(4));
        if (OptionsUI.instance.searchcolumn_remixer.isSelected()) indices.add(new Integer(27));
        if (OptionsUI.instance.searchcolumn_length.isSelected()) indices.add(new Integer(5));
        if (OptionsUI.instance.searchcolumn_rating.isSelected()) indices.add(new Integer(46));
        if (OptionsUI.instance.searchcolumn_bpmdiff.isSelected()) indices.add(new Integer(18));
        if (OptionsUI.instance.searchcolumn_relbpmdiff.isSelected()) indices.add(new Integer(25));
        if (OptionsUI.instance.searchcolumn_bpm.isSelected()) indices.add(new Integer(9));
        if (OptionsUI.instance.searchcolumn_startbpm.isSelected()) indices.add(new Integer(7));
        if (OptionsUI.instance.searchcolumn_endbpm.isSelected()) indices.add(new Integer(8));
        if (OptionsUI.instance.searchcolumn_bpmaccuracy.isSelected()) indices.add(new Integer(42));
        if (OptionsUI.instance.searchcolumn_beatintensity.isSelected()) indices.add(new Integer(50));
        if (OptionsUI.instance.searchcolumn_key.isSelected()) indices.add(new Integer(12));
        if (OptionsUI.instance.searchcolumn_actualkey.isSelected()) indices.add(new Integer(47));
        if (OptionsUI.instance.searchcolumn_startkey.isSelected()) indices.add(new Integer(10));
        if (OptionsUI.instance.searchcolumn_endkey.isSelected()) indices.add(new Integer(11));
        if (OptionsUI.instance.searchcolumn_keylock.isSelected()) indices.add(new Integer(19));
        if (OptionsUI.instance.searchcolumn_keymode.isSelected()) indices.add(new Integer(26));
        if (OptionsUI.instance.searchcolumn_keycode.isSelected()) indices.add(new Integer(35));
        if (OptionsUI.instance.searchcolumn_actualkeycode.isSelected()) indices.add(new Integer(49));
        if (OptionsUI.instance.searchcolumn_keyaccuracy.isSelected()) indices.add(new Integer(41));
        if (OptionsUI.instance.searchcolumn_pitchshift.isSelected()) indices.add(new Integer(48));
        if (OptionsUI.instance.searchcolumn_artistsimilarity.isSelected()) indices.add(new Integer(38));
        if (OptionsUI.instance.searchcolumn_stylesimilarity.isSelected()) indices.add(new Integer(34));
        if (OptionsUI.instance.searchcolumn_styles.isSelected()) indices.add(new Integer(43));
        if (OptionsUI.instance.searchcolumn_colorsimilarity.isSelected()) indices.add(new Integer(36));
        if (OptionsUI.instance.searchcolumn_timesig.isSelected()) indices.add(new Integer(6));
        if (OptionsUI.instance.searchcolumn_user1.isSelected()) indices.add(new Integer(28));
        if (OptionsUI.instance.searchcolumn_user2.isSelected()) indices.add(new Integer(29));
        if (OptionsUI.instance.searchcolumn_user3.isSelected()) indices.add(new Integer(30));
        if (OptionsUI.instance.searchcolumn_user4.isSelected()) indices.add(new Integer(31));
        if (OptionsUI.instance.searchcolumn_dateadded.isSelected()) indices.add(new Integer(37));
        if (OptionsUI.instance.searchcolumn_lastmodified.isSelected()) indices.add(new Integer(45));
        if (OptionsUI.instance.searchcolumn_timesplayed.isSelected()) indices.add(new Integer(40));
        if (OptionsUI.instance.searchcolumn_replaygain.isSelected()) indices.add(new Integer(51));
        if (OptionsUI.instance.searchcolumn_nummixouts.isSelected()) indices.add(new Integer(20));
        if (OptionsUI.instance.searchcolumn_numaddons.isSelected()) indices.add(new Integer(23));
        if (OptionsUI.instance.searchcolumn_comments.isSelected()) indices.add(new Integer(13));
        if (OptionsUI.instance.searchcolumn_vinyl.isSelected()) indices.add(new Integer(15));
        if (OptionsUI.instance.searchcolumn_nonvinyl.isSelected()) indices.add(new Integer(16));
        if (OptionsUI.instance.searchcolumn_disabled.isSelected()) indices.add(new Integer(17));
        if (OptionsUI.instance.searchcolumn_filename.isSelected()) indices.add(new Integer(14));
        if (OptionsUI.instance.searchcolumn_hasalbumcover.isSelected()) indices.add(new Integer(44));
        int[] returnval = new int[indices.size()];
        for (int i = 0; i < indices.size(); ++i) returnval[i] = ((Integer)indices.get(i)).intValue();
        return returnval;
    }

    public static int[] determineOrdering(int[] defaultorder, int[] originalorder, boolean reset) {
      try {
        int[] returnval = new int[defaultorder.length];
        Vector temparray = new Vector();
        for (int i = 0; i < originalorder.length; ++i) temparray.add(new Integer(originalorder[i]));
        for (int i = 0; i < defaultorder.length; ++i) {
            boolean exists = false;
            int j = 0;
            while (!exists && (j < temparray.size())) {
                int val = ((Integer)temparray.get(j)).intValue();
                if (val == defaultorder[i]) exists = true;
                ++j;
            }
            if (!exists) {
                boolean inserted = false;
                j = 0;
                while (!inserted && (j < temparray.size())) {
                    int val = ((Integer)temparray.get(j)).intValue();
                    if (columnIsBefore(defaultorder[i], val)) {
                        temparray.insertElementAt(new Integer(defaultorder[i]), j);
                        inserted = true;
                    } else {
                        ++j;
                    }
                }
                if (!inserted) temparray.add(new Integer(defaultorder[i]));
            }
        }
        if (reset) {
          int[] newDefaultMatrix = new int[temparray.size()];
          for (int i = 0; i < temparray.size(); ++i) {
              int val = ((Integer)temparray.get(i)).intValue();
              newDefaultMatrix[i] = val;
          }
          defaultColumnIndices = determineOrdering(defaultColumnIndices, newDefaultMatrix, false);
        }
        for (int i = 0; i < temparray.size(); ++i) {
            int val = ((Integer)temparray.get(i)).intValue();
            boolean found = false;
            int j = 0;
            while (!found && (j < defaultorder.length)) {
                if (defaultorder[j] == val) found = true;
                ++j;
            }
            if (!found) temparray.removeElementAt(i--);
            else returnval[i] = val;
        }
        return returnval;
      } catch (Exception e) {
      }
      return new int[0];
    }

    public void RecreateUsingSearch(ColumnConfig columnconfig, JSortTable table, int columncount) {
        int[] defaultorder = getDefaultColumnIndices();
        int[] oldorder = new int[SearchPane.instance.searchcolumnconfig.num_columns];
        for (int i = 0; i < SearchPane.instance.searchcolumnconfig.num_columns; ++i) oldorder[i] = SearchPane.instance.searchcolumnconfig.getIndex(SearchPane.instance.searchtable.getColumnModel().getColumn(i).getHeaderValue().toString());
        int[] neworder = determineOrdering(defaultorder, oldorder, true);
        RecreateUsingSearch(columnconfig, table, columncount, neworder);
    }

    public void RecreateUsingSearch(ColumnConfig columnconfig, JSortTable table, int columncount, int[] columnorder) {

        int primary_sort_index_type = -1;
        if ((columnconfig.primary_sort_column < columnconfig.num_columns) && (columnconfig.primary_sort_column >= 0))
            primary_sort_index_type = columnconfig.columnindex[columnconfig.primary_sort_column];
        int secondary_sort_index_type = -1;
        if ((columnconfig.secondary_sort_column < columnconfig.num_columns) && (columnconfig.secondary_sort_column >= 0))
            secondary_sort_index_type = columnconfig.columnindex[columnconfig.secondary_sort_column];
        int tertiary_sort_index_type = -1;
        if ((columnconfig.tertiary_sort_column < columnconfig.num_columns) && (columnconfig.tertiary_sort_column >= 0))
            tertiary_sort_index_type = columnconfig.columnindex[columnconfig.tertiary_sort_column];
      int sortcolumn = -1;
      columnconfig.num_columns = columncount;
      columnconfig.columnindex = new int[columncount];
      columnconfig.columntitles = new String[columncount];
      columnconfig.setPreferredWidth(new int[columncount]);
      columncount = 0;
      for (int i = 0; i < columnorder.length; ++i) {
          columnconfig.columnindex[columncount] = columnorder[i];
          columnconfig.setPreferredWidth(columncount, columnconfig.getDefaultWidth(columnorder[i]));
          columnconfig.columntitles[columncount++] = columnconfig.getKeyword(columnorder[i]);
      }
      if (primary_sort_index_type != -1) {
          for (int i = 0; i < columnconfig.num_columns; ++i) {
              if (columnconfig.columnindex[i] == primary_sort_index_type)
                  columnconfig.primary_sort_column = i;
          }
      }
      if (secondary_sort_index_type != -1) {
          for (int i = 0; i < columnconfig.num_columns; ++i) {
              if (columnconfig.columnindex[i] == secondary_sort_index_type)
                  columnconfig.secondary_sort_column = i;
          }
      }
      if (tertiary_sort_index_type != -1) {
          for (int i = 0; i < columnconfig.num_columns; ++i) {
              if (columnconfig.columnindex[i] == tertiary_sort_index_type)
                  columnconfig.tertiary_sort_column = i;
          }
      }
    }

    boolean dontchange = false;
    public static int dontfiretablechange = 0;
    public void tableChanged(TableModelEvent e) {
      if ((e.getType() == TableModelEvent.INSERT) || (e.getType() == TableModelEvent.DELETE)) return;
      if (dontchange) return;
      if (dontfiretablechange > 0) return;
      dontchange = true;
      try {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String title = model.getColumnName(column);
        Object data = (Object)model.getValueAt(row, column);
        SongLinkedList song = (SongLinkedList)model.getValueAt(row, searchcolumnconfig.num_columns);
        CellUpdatesong(title, song, data);
      } catch (Exception e2) { }
        dontchange = false;
    }

    public void CellUpdatesong(String title, SongLinkedList song, Object data) {
      if (SongDB.instance.isupdatingsong) return;
      OldSongValues old_values = new OldSongValues(song);
      if (title.equals(SkinManager.instance.getMessageText("column_title_artist"))) {
        song.setArtist(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_album"))) {
        song.setAlbum(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_track"))) {
        song.setTrack(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_title"))) {
        song.setSongname(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_time"))) {
        String time = StringUtil.validate_time(data.toString());
        song.setTime(time);
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_time_signature"))) {
        String timesig = StringUtil.validate_timesig(data.toString());
        song.setTimesig(timesig);
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_bpm_start"))) {
        try {
           song.setStartbpm(Float.parseFloat(data.toString()));
        } catch (Exception e2) { }
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_bpm_end"))) {
        try {
           song.setEndbpm(Float.parseFloat(data.toString()));
        } catch (Exception e2) { }
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_key_start"))) {
        String key = StringUtil.validate_keyformat(data.toString());
        song.setStartkey(key);
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_key_end"))) {
        String key = StringUtil.validate_keyformat(data.toString());
        song.setEndkey(key);
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_song_comments"))) {
        song.setComments(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_filename"))) {
        song.setFilename(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_analog"))) {
        boolean doset = false;
        boolean flag = false;
        if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_true_text"))) { flag = true; doset = true; }
        else if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_false_text"))) doset = true;
        if (doset) {
          song.setVinylOnly( flag);
          if (flag) song.setNonVinylOnly( !flag);
        }
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_digital"))) {
        boolean doset = false;
        boolean flag = false;
        if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_true_text"))) { flag = true; doset = true; }
        else if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_false_text"))) doset = true;
        if (doset) {
          song.setNonVinylOnly( flag);
          if (flag) song.setVinylOnly( !flag);
        }
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_disabled"))) {
        boolean doset = false;
        boolean flag = false;
        if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_true_text"))) { flag = true; doset = true; }
        else if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_false_text"))) doset = true;
        if (doset) {
          song.setDisabled(flag);
          SongDB.instance.UpdateSong(song, old_values);
        }
      } else if (title.equals(SkinManager.instance.getMessageText("column_title_remix"))) {
        String olduniqueid = song.uniquestringid;
        long oldlongid = song.uniquesongid;
        song.setRemixer(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(OptionsUI.instance.customfieldtext1.getText())) {
        song.setUser1(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(OptionsUI.instance.customfieldtext2.getText())) {
        song.setUser2(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(OptionsUI.instance.customfieldtext3.getText())) {
        song.setUser3(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      } else if (title.equals(OptionsUI.instance.customfieldtext4.getText())) {
        song.setUser4(data.toString());
        SongDB.instance.UpdateSong(song, old_values);
      }
    }

    private boolean isEditableSearchColumn(int columnindex) {
      String title = searchtable.getColumnModel().getColumn(searchtable.convertColumnIndexToModel(columnindex)).getHeaderValue().toString();
      return checkEditableTitle(title);
    }

    public boolean checkEditableTitle(String title) {
      if (title.equals(SkinManager.instance.getMessageText("column_title_artist"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_album"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_track"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_title"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_time"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_time_signature"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_bpm_start"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_bpm_end"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_key_start"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_key_end"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_song_comments"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_filename"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_analog"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_digital"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_disabled"))) return true;
      if (title.equals(SkinManager.instance.getMessageText("column_title_remix"))) return true;
      if (title.equals(OptionsUI.instance.customfieldtext1.getText())) return true;
      if (title.equals(OptionsUI.instance.customfieldtext2.getText())) return true;
      if (title.equals(OptionsUI.instance.customfieldtext3.getText())) return true;
      if (title.equals(OptionsUI.instance.customfieldtext4.getText())) return true;
      return false;
    }
    
    public void setBpmSliderRangeLabels() {
        if (bpmslider_tickmark_combo != null) {
            if (bpmslider_tickmark_combo.getSelectedIndex() == 1) {
                setBpmSliderRange(4);
                return;
            } else if (bpmslider_tickmark_combo.getSelectedIndex() == 2) {
                setBpmSliderRange(5);
                return;
            } 
        }
        // use default slider range
        setBpmSliderRange(-1);
    }

    private void setBpmSliderRange(int notches) {
        int oldvalue = bpmslider.getValue();
        if (bpmrangespinner.getModel().getValue().equals("4%")) {
            RapidEvolutionUI.instance.bpmscale = 4;
            bpmslider.setMinimum(-400);
            bpmslider.setMaximum(400);
            if (notches == 5) {
                bpmslider.setMajorTickSpacing(80);
                bpmslider.setMinorTickSpacing(40);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("   0", bpmslider));
                bpmlabelTable.put(new Integer(80), new MySliderLabel("-0.8", bpmslider));
                bpmlabelTable.put(new Integer(-80), new MySliderLabel("+0.8", bpmslider));
                bpmlabelTable.put(new Integer(160), new MySliderLabel("-1.6", bpmslider));
                bpmlabelTable.put(new Integer(-160), new MySliderLabel("+1.6", bpmslider));
                bpmlabelTable.put(new Integer(240), new MySliderLabel("-2.4", bpmslider));
                bpmlabelTable.put(new Integer(-240), new MySliderLabel("+2.4", bpmslider));
                bpmlabelTable.put(new Integer(320), new MySliderLabel("-3.2", bpmslider));
                bpmlabelTable.put(new Integer(-320), new MySliderLabel("+3.2", bpmslider));
                bpmlabelTable.put(new Integer(400), new MySliderLabel("  -4", bpmslider));
                bpmlabelTable.put(new Integer(-400), new MySliderLabel("  +4", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);                
            } else { // default: (notches == 4)
                bpmslider.setMajorTickSpacing(100);
                bpmslider.setMinorTickSpacing(50);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel(" 0", bpmslider));
                bpmlabelTable.put(new Integer(100), new MySliderLabel("-1", bpmslider));
                bpmlabelTable.put(new Integer(-100), new MySliderLabel("+1", bpmslider));
                bpmlabelTable.put(new Integer(200), new MySliderLabel("-2", bpmslider));
                bpmlabelTable.put(new Integer(-200), new MySliderLabel("+2", bpmslider));
                bpmlabelTable.put(new Integer(300), new MySliderLabel("-3", bpmslider));
                bpmlabelTable.put(new Integer(-300), new MySliderLabel("+3", bpmslider));
                bpmlabelTable.put(new Integer(400), new MySliderLabel("-4", bpmslider));
                bpmlabelTable.put(new Integer(-400), new MySliderLabel("+4", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);
            }
            bpmslider.setValue(oldvalue);
            showSliderShiftValue();
        } else if (bpmrangespinner.getModel().getValue().equals("8%")) {
            RapidEvolutionUI.instance.bpmscale = 8;
            bpmslider.setMinimum(-800);
            bpmslider.setMaximum(800);
            if (notches == 5) {
                bpmslider.setMajorTickSpacing(160);
                bpmslider.setMinorTickSpacing(80);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("   0", bpmslider));
                bpmlabelTable.put(new Integer(160), new MySliderLabel("-1.6", bpmslider));
                bpmlabelTable.put(new Integer(-160), new MySliderLabel("+1.6", bpmslider));
                bpmlabelTable.put(new Integer(320), new MySliderLabel("-3.2", bpmslider));
                bpmlabelTable.put(new Integer(-320), new MySliderLabel("+3.2", bpmslider));
                bpmlabelTable.put(new Integer(480), new MySliderLabel("-4.8", bpmslider));
                bpmlabelTable.put(new Integer(-480), new MySliderLabel("+4.8", bpmslider));
                bpmlabelTable.put(new Integer(640), new MySliderLabel("-6.4", bpmslider));
                bpmlabelTable.put(new Integer(-640), new MySliderLabel("+6.4", bpmslider));
                bpmlabelTable.put(new Integer(800), new MySliderLabel("  -8", bpmslider));
                bpmlabelTable.put(new Integer(-800), new MySliderLabel("  +8", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);                
            } else { // default: (notches == 4)
                bpmslider.setMajorTickSpacing(200);
                bpmslider.setMinorTickSpacing(100);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel(" 0", bpmslider));
                bpmlabelTable.put(new Integer(200), new MySliderLabel("-2", bpmslider));
                bpmlabelTable.put(new Integer(-200), new MySliderLabel("+2", bpmslider));
                bpmlabelTable.put(new Integer(400), new MySliderLabel("-4", bpmslider));
                bpmlabelTable.put(new Integer(-400), new MySliderLabel("+4", bpmslider));
                bpmlabelTable.put(new Integer(600), new MySliderLabel("-6", bpmslider));
                bpmlabelTable.put(new Integer(-600), new MySliderLabel("+6", bpmslider));
                bpmlabelTable.put(new Integer(800), new MySliderLabel("-8", bpmslider));
                bpmlabelTable.put(new Integer(-800), new MySliderLabel("+8", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);
            }
            bpmslider.setValue(oldvalue);
            showSliderShiftValue();
        } else if (bpmrangespinner.getModel().getValue().equals("10%")) {
            RapidEvolutionUI.instance.bpmscale = 10;
            bpmslider.setMinimum(-1000);
            bpmslider.setMaximum(1000);
            if (notches ==4) {
                bpmslider.setMajorTickSpacing(250);
                bpmslider.setMinorTickSpacing(125);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("   0", bpmslider));
                bpmlabelTable.put(new Integer(250), new MySliderLabel("-2.5", bpmslider));
                bpmlabelTable.put(new Integer(-250), new MySliderLabel("+2.5", bpmslider));
                bpmlabelTable.put(new Integer(500), new MySliderLabel("-5.0", bpmslider));
                bpmlabelTable.put(new Integer(-500), new MySliderLabel("+5.0", bpmslider));
                bpmlabelTable.put(new Integer(750), new MySliderLabel("-7.5", bpmslider));
                bpmlabelTable.put(new Integer(-750), new MySliderLabel("+7.5", bpmslider));
                bpmlabelTable.put(new Integer(1000), new MySliderLabel(" -10", bpmslider));
                bpmlabelTable.put(new Integer(-1000), new MySliderLabel(" +10", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);                
            } else { // default: (notches == 5)
                bpmslider.setMajorTickSpacing(200);
                bpmslider.setMinorTickSpacing(100);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("  0", bpmslider));
                bpmlabelTable.put(new Integer(200), new MySliderLabel(" -2", bpmslider));
                bpmlabelTable.put(new Integer(-200), new MySliderLabel(" +2", bpmslider));
                bpmlabelTable.put(new Integer(400), new MySliderLabel(" -4", bpmslider));
                bpmlabelTable.put(new Integer(-400), new MySliderLabel(" +4", bpmslider));
                bpmlabelTable.put(new Integer(600), new MySliderLabel(" -6", bpmslider));
                bpmlabelTable.put(new Integer(-600), new MySliderLabel(" +6", bpmslider));
                bpmlabelTable.put(new Integer(800), new MySliderLabel(" -8", bpmslider));
                bpmlabelTable.put(new Integer(-800), new MySliderLabel(" +8", bpmslider));
                bpmlabelTable.put(new Integer(1000), new MySliderLabel("-10", bpmslider));
                bpmlabelTable.put(new Integer(-1000), new MySliderLabel("+10", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);
            }
            bpmslider.setValue(oldvalue);
            showSliderShiftValue();
        } else if (bpmrangespinner.getModel().getValue().equals("20%")) {
            RapidEvolutionUI.instance.bpmscale = 20;
            bpmslider.setMinimum(-2000);
            bpmslider.setMaximum(2000);
            if (notches == 4) {
                bpmslider.setMajorTickSpacing(500);
                bpmslider.setMinorTickSpacing(250);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("  0", bpmslider));
                bpmlabelTable.put(new Integer(500), new MySliderLabel(" -5", bpmslider));
                bpmlabelTable.put(new Integer(-500), new MySliderLabel(" +5", bpmslider));
                bpmlabelTable.put(new Integer(1000), new MySliderLabel("-10", bpmslider));
                bpmlabelTable.put(new Integer(-1000), new MySliderLabel("+10", bpmslider));
                bpmlabelTable.put(new Integer(1500), new MySliderLabel("-15", bpmslider));
                bpmlabelTable.put(new Integer(-1500), new MySliderLabel("+15", bpmslider));
                bpmlabelTable.put(new Integer(2000), new MySliderLabel("-20", bpmslider));
                bpmlabelTable.put(new Integer(-2000), new MySliderLabel("+20", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);
            } else { // default: (nothces == 5)
                bpmslider.setMajorTickSpacing(400);
                bpmslider.setMinorTickSpacing(200);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("  0", bpmslider));
                bpmlabelTable.put(new Integer(400), new MySliderLabel(" -4", bpmslider));
                bpmlabelTable.put(new Integer(-400), new MySliderLabel(" +4", bpmslider));
                bpmlabelTable.put(new Integer(800), new MySliderLabel(" -8", bpmslider));
                bpmlabelTable.put(new Integer(-800), new MySliderLabel(" +8", bpmslider));
                bpmlabelTable.put(new Integer(1200), new MySliderLabel("-12", bpmslider));
                bpmlabelTable.put(new Integer(-1200), new MySliderLabel("+12", bpmslider));
                bpmlabelTable.put(new Integer(1600), new MySliderLabel("-16", bpmslider));
                bpmlabelTable.put(new Integer(-1600), new MySliderLabel("+16", bpmslider));
                bpmlabelTable.put(new Integer(2000), new MySliderLabel("-20", bpmslider));
                bpmlabelTable.put(new Integer(-2000), new MySliderLabel("+20", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);                
            }
            bpmslider.setValue(oldvalue);
            showSliderShiftValue();
        } else if (bpmrangespinner.getModel().getValue().equals("50%")) {
            RapidEvolutionUI.instance.bpmscale = 50;
            bpmslider.setMinimum(-5000);
            bpmslider.setMaximum(5000);
            if (notches == 4) {
                bpmslider.setMajorTickSpacing(1250);
                bpmslider.setMinorTickSpacing(625);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("    0", bpmslider));
                bpmlabelTable.put(new Integer(1250), new MySliderLabel("-12.5", bpmslider));
                bpmlabelTable.put(new Integer(-1250), new MySliderLabel("+12.5", bpmslider));
                bpmlabelTable.put(new Integer(1250*2), new MySliderLabel("  -25", bpmslider));
                bpmlabelTable.put(new Integer(-1250*2), new MySliderLabel("  +25", bpmslider));
                bpmlabelTable.put(new Integer(1250*3), new MySliderLabel("-37.5", bpmslider));
                bpmlabelTable.put(new Integer(-1250*3), new MySliderLabel("+37.5", bpmslider));
                bpmlabelTable.put(new Integer(1250*4), new MySliderLabel("  -50", bpmslider));
                bpmlabelTable.put(new Integer(-1250*4), new MySliderLabel("  +50", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);
            } else { // default: (notches ==5)
                bpmslider.setMajorTickSpacing(1000);
                bpmslider.setMinorTickSpacing(500);
                Hashtable bpmlabelTable = new Hashtable();
                bpmlabelTable.put(new Integer(0), new MySliderLabel("  0", bpmslider));
                bpmlabelTable.put(new Integer(200*5), new MySliderLabel(" -10", bpmslider));
                bpmlabelTable.put(new Integer(-200*5), new MySliderLabel(" +10", bpmslider));
                bpmlabelTable.put(new Integer(400*5), new MySliderLabel(" -20", bpmslider));
                bpmlabelTable.put(new Integer(-400*5), new MySliderLabel(" +20", bpmslider));
                bpmlabelTable.put(new Integer(600*5), new MySliderLabel(" -30", bpmslider));
                bpmlabelTable.put(new Integer(-600*5), new MySliderLabel(" +30", bpmslider));
                bpmlabelTable.put(new Integer(800*5), new MySliderLabel(" -40", bpmslider));
                bpmlabelTable.put(new Integer(-800*5), new MySliderLabel(" +40", bpmslider));
                bpmlabelTable.put(new Integer(1000*5), new MySliderLabel("-50", bpmslider));
                bpmlabelTable.put(new Integer(-1000*5), new MySliderLabel("+50", bpmslider));
                bpmslider.setLabelTable(bpmlabelTable);                
            }
            bpmslider.setValue(oldvalue);
            showSliderShiftValue();
        }        
    }
    
}

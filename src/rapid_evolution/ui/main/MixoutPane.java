package rapid_evolution.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongStack;
import rapid_evolution.SongUtil;
import rapid_evolution.StringUtil;
import rapid_evolution.ui.AddMixoutUI;
import rapid_evolution.ui.ColumnConfig;
import rapid_evolution.ui.DefaultSortTableModel;
import rapid_evolution.ui.JSortTable;
import rapid_evolution.ui.MixGeneratorUI;
import rapid_evolution.ui.MyTableColumnModelListener;
import rapid_evolution.ui.NewTableColumnModelListener;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.RootsUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.SortTableModel;
import rapid_evolution.ui.SuggestedMixesUI;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class MixoutPane implements ActionListener, TableModelListener {

    private static Logger log = Logger.getLogger(MixoutPane.class);
    
    public MixoutPane() {
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    static public MixoutPane instance = null;

    public JPanel mixoutpanel = new JPanel();
    public JPanel mixdetailspanel = new JPanel();
    public ColumnConfig mixoutcolumnconfig = new ColumnConfig();
    public DroppableMixoutTable mixouttable;
    public JLabel mixoutcommentslabel = new RELabel();
    public JCheckBox addoncheckbox = new RECheckBox();
    public JTextArea mixoutcomments = new RETextArea();
    public JTextField mixoutscore = new RETextField();
    public JTextField bpmdifffield = new RETextField();
    public JButton calculatebpmdiffbutton = new REButton();
    MixListMouse amixlistmouse = new MixListMouse();
    public SongLinkedList selectedmixout = null;
    public DroppableMixoutScrollPane mixoutscrollpane;
    public JLabel scorefield = new RELabel();
    public JLabel bpmdifflabel = new RELabel();

    private void setupDialog() {
       mixouttable = new DroppableMixoutTable();
        mixouttable.setAutoscrolls(OptionsUI.instance.mixouttableautoscroll.isSelected());
        mixouttable.addKeyListener(new MixoutTableKeyListener());
        mixouttable.getSelectionModel().addListSelectionListener(new MixoutTableListListener());
        mixoutscrollpane = new DroppableMixoutScrollPane(mixouttable);
        SkinManager.addScroll(mixouttable, mixoutscrollpane);
//        mixsplit.setDividerLocation((int)(mixoutpanel.getSize().getHeight() - mixdetailspanel.getMinimumSize().getHeight()));
        addoncheckbox.setEnabled(false);
        mixoutcomments.setEnabled(false);
//    mixoutcomments.addKeyListener(new MixoutKeyListener());
        mixoutcomments.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
              if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) {
                RapidEvolutionUI.instance.currentsong.setMixoutComments(i, new String(mixoutcomments.getText()));
              }
            UpdateMixout();
          }
        });
        mixoutscore.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            mixoutscore.setText(StringUtil.validate_rank(mixoutscore.getText()));
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
              if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) {
                RapidEvolutionUI.instance.currentsong.setMixoutRank(i, Integer.parseInt(mixoutscore.getText()));
              }
            UpdateMixout();
          }
        });
        mixoutscore.setEnabled(false);
        bpmdifffield.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
           bpmdifffield.setText(StringUtil.validate_bpmdiff(bpmdifffield.getText()));
           for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
             if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) {
               try { RapidEvolutionUI.instance.currentsong.setMixoutBpmdiff(i, Float.parseFloat(bpmdifffield.getText())); }
               catch (Exception fe) { }
             }
           UpdateMixout();
         }
       });
        mixouttable.getColumnModel().addColumnModelListener(new MyTableColumnModelListener(mixoutcolumnconfig));
        
//    bpmdifffield.addKeyListener(new MixoutKeyListener());
       bpmdifffield.setEnabled(false);
       calculatebpmdiffbutton.setEnabled(false);
       mixoutcommentslabel.setEnabled(false);
       scorefield.setEnabled(false);
       bpmdifflabel.setEnabled(false);

       mixouttable.addMouseListener(amixlistmouse);
    }

    public void UpdateMixout() {
      SongLinkedList song = selectedmixout;
      int r = -1;
      for (int j = 0; j < mixouttable.getRowCount();  ++j) {
        SongLinkedList tsong = (SongLinkedList)mixouttable.getValueAt(j, mixoutcolumnconfig.num_columns);
        if (tsong == song) r = j;
      }
      for (int i = 0; i < mixoutcolumnconfig.num_columns; ++i) {
        if (mixoutcolumnconfig.columntitles[i].equals(SkinManager.instance.getMessageText("column_title_bpm_diff"))) {
          mixouttable.getModel().setValueAt(song.get_column_mixdata(25), r, i);
        }
        else if (mixoutcolumnconfig.columntitles[i].equals(SkinManager.instance.getMessageText("column_title_key_lock"))) {
          mixouttable.getModel().setValueAt(song.get_column_mixdata(19), r, i);
        }
        else if (mixoutcolumnconfig.columntitles[i].equals(SkinManager.instance.getMessageText("column_title_key_type"))) {
          mixouttable.getModel().setValueAt(song.get_column_mixdata(26), r, i);
        }
        else if (mixoutcolumnconfig.columntitles[i].equals(SkinManager.instance.getMessageText("column_title_bpm_shift"))) {
          mixouttable.getModel().setValueAt(song.get_column_mixdata(18), r, i);
        }
        else if (mixoutcolumnconfig.columntitles[i].equals(SkinManager.instance.getMessageText("column_title_rank"))) {
          mixouttable.getModel().setValueAt(song.get_column_mixdata(22), r, i);
        }
        else if (mixoutcolumnconfig.columntitles[i].equals(SkinManager.instance.getMessageText("column_title_addon"))) {
          mixouttable.getModel().setValueAt(song.get_column_mixdata(24), r, i);
        }
      }
      MixGeneratorUI.instance.DisplayMix(MixGeneratorUI.instance.current_mix);
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == song.uniquesongid) {
          RapidEvolutionUI.instance.currentsong.mixout_servercache[i] = false;
//        new SendMixoutThread(currentsong, i).start();
       }
      }
    }

    public void ConfirmAddMixout() {
      AddMixoutUI.instance.Hide();
      int num = 0;
      try { num = Integer.parseInt(AddMixoutUI.instance.addmixoutscorefield.getText()); }
      catch (Exception e) { }
      float diff = 0.0f;
      try { diff = Float.parseFloat(AddMixoutUI.instance.addmixoutbpmdifffield.getText()); }
      catch (Exception e) { }
      RapidEvolutionUI.instance.currentsong.insertMixOut(AddMixoutUI.instance.addmixoutsong, AddMixoutUI.instance.addmixoutcommentsfield.getText(), diff, num, AddMixoutUI.instance.addmixoutaddoncb.isSelected());
      RedrawMixoutTable();
      for (int i = 0; i < mixouttable.getRowCount(); ++i) {
        SongLinkedList song = (SongLinkedList)mixouttable.getValueAt(i, MixoutPane.instance.mixoutcolumnconfig.num_columns);
        if (song == AddMixoutUI.instance.addmixoutsong) mixouttable.setRowSelectionInterval(i, i);
      }
      if (AddMixoutUI.instance.source_id == 5) {
          javax.swing.SwingUtilities.invokeLater(new changecurrentsongthread(AddMixoutUI.instance.addmixoutsong, 0.0f, false, false));          
      }      
      if (mixouttable.getSelectedRow() == -1) return;
      SongLinkedList song = (SongLinkedList)mixouttable.getModel().getValueAt(mixouttable.getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns);
      selectedmixout = song;
      addoncheckbox.setEnabled(true);
      mixoutcomments.setEnabled(true);
      mixoutscore.setEnabled(true);
      bpmdifffield.setEnabled(true);
      calculatebpmdiffbutton.setEnabled(true);
      mixoutcommentslabel.setEnabled(true);
      scorefield.setEnabled(true);
      bpmdifflabel.setEnabled(true);
      for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
        if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == song.uniquesongid) {
          mixoutcomments.setText(RapidEvolutionUI.instance.currentsong.getMixoutComments(i));
          mixoutscore.setText(String.valueOf(RapidEvolutionUI.instance.currentsong.getMixoutRank(i)));
          String bpmtext = String.valueOf(RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i));
          int maxlen = 6;
          if (RapidEvolutionUI.instance.currentsong.getMixoutBpmdiff(i) > 0) bpmtext = new String("+" + bpmtext);
          if (bpmtext.length() < maxlen) maxlen = bpmtext.length();
          bpmdifffield.setText(bpmtext.substring(0, maxlen));
          if (RapidEvolutionUI.instance.currentsong.getMixoutAddon(i)) addoncheckbox.setSelected(true);
          else addoncheckbox.setSelected(false);
        }
      }

      if (AddMixoutUI.instance.source_id == 0) { SkinManager.instance.setEnabled("add_mixout_button", false); SearchPane.instance.searchtable.requestFocus(); }
      else if (AddMixoutUI.instance.source_id == 1) {
        SkinManager.instance.setEnabled("roots_add_mixout_button", false);
        RootsUI.instance.rootstable.requestFocus();
      }
      else {
        SkinManager.instance.setEnabled("suggested_mixouts_add_mixout_button", false);
        SuggestedMixesUI.instance.suggestedtable.requestFocus();
      }
      if (OptionsUI.instance.addmixoutchangesong.isSelected()) {
          new changecurrentsongthread(AddMixoutUI.instance.addmixoutsong, diff, true, false).start();
      }
      
    }

    public void RemoveSelectedMixout() {
      int n = IOptionPane.showConfirmDialog(
          SkinManager.instance.getFrame("main_frame"),
          SkinManager.instance.getDialogMessageText("delete_mixout_confirm"),
          SkinManager.instance.getDialogMessageTitle("delete_mixout_confirm"),
          IOptionPane.YES_NO_OPTION);
      if (n == 0) {
        SongLinkedList removesong = (SongLinkedList) mixouttable.getModel().
            getValueAt(mixouttable.getSelectedRow(),
                       MixoutPane.instance.mixoutcolumnconfig.num_columns);
        for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
          if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == removesong.uniquesongid) {
            RapidEvolutionUI.instance.currentsong.removeMixOut(i);
            --i;
          }
        }
        RedrawMixoutTable();
        MixGeneratorUI.instance.DisplayMix(MixGeneratorUI.instance.current_mix);
      }
    }

    public SortTableModel makeMixModel() {
      Vector data = new Vector();
      if (RapidEvolutionUI.instance.currentsong != null) {
        for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) {
          Vector row = new Vector();
          SongLinkedList song = SongDB.instance.NewGetSongPtr(RapidEvolutionUI.instance.currentsong.mixout_songs[i]);
          if (song != null) {
            boolean keep = true;
            if (OptionsUI.instance.preventrepeats.isSelected()) {
              SongStack siter = RapidEvolutionUI.instance.prevstack;
              while ((siter != null) && keep) {
                if (siter.songid == song.uniquesongid) keep = false;
                siter = siter.next;
              }
            }
            if (keep) {
              for (int j = 0; j < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++j)
                row.add(song.get_column_mixdata(MixoutPane.instance.mixoutcolumnconfig.columnindex[j]));
              row.add(song);
              data.add(row);
            }
          }
        }
      }
      Vector columnames = new Vector();
      for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i)
        columnames.add(MixoutPane.instance.mixoutcolumnconfig.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel returnval = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
            if (mixoutcolumnconfig.columnindex[mColIndex] == ColumnConfig.COLUMN_RATING) return true;
          if (OptionsUI.instance.mixoutinplaceediting.isSelected()) return isEditableMixoutColumn(mColIndex);
          else return false;
        }
      };
      return returnval;
    }

    boolean dontchange = false;
    public void tableChanged(TableModelEvent e) {
      if ((e.getType() == TableModelEvent.INSERT) || (e.getType() == TableModelEvent.DELETE)  || (e.getType() == TableModelEvent.HEADER_ROW)) return;
      if (dontchange) return;
      if (SearchPane.instance.dontfiretablechange > 0) return;
      dontchange = true;
      try {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = (Object)model.getValueAt(row, column);
        if (columnName.equals(SkinManager.instance.getMessageText("column_title_rank"))) {
          data = StringUtil.validate_rank(data.toString());
          int rank = 0;
          try {
            rank = Integer.parseInt(data.toString());
          } catch (Exception e2) { }
          for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
            if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) RapidEvolutionUI.instance.currentsong.setMixoutRank(i, rank);
        } else if (columnName.equals(SkinManager.instance.getMessageText("column_title_addon"))) {
          boolean doset = false;
          boolean addon = false;
          if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_true_text"))) { addon = true; doset = true; }
          else if (data.toString().equalsIgnoreCase(SkinManager.instance.getMessageText("default_false_text"))) doset = true;
          if (doset) {
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
              if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) RapidEvolutionUI.instance.currentsong.setMixoutAddon(i, addon);
          }
        } else if (columnName.equals(SkinManager.instance.getMessageText("column_title_bpm_diff"))) {
          try {
            float bpmdiff = Float.parseFloat(data.toString());
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
              if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) RapidEvolutionUI.instance.currentsong.setMixoutBpmdiff(i, bpmdiff);
          } catch (Exception e2) { }
        } else if (columnName.equals(SkinManager.instance.getMessageText("column_title_mixout_comments"))) {
          for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
            if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) RapidEvolutionUI.instance.currentsong.setMixoutComments(i, data.toString());
        } else {
          SearchPane.instance.CellUpdatesong(columnName, selectedmixout, data);
        }
      } catch (Exception e2) { }
        dontchange = false;
    }

    
    private boolean isEditableMixoutColumn(int columnindex) {
        String columnname = mixouttable.getColumnModel().getColumn(mixouttable.convertColumnIndexToModel(columnindex)).getHeaderValue().toString();
        log.trace("isEditableMixoutColumn(): columnindex=" + columnindex + ", columname=" + columnname);
      if (columnname.equals(SkinManager.instance.getMessageText("column_title_rank"))) return true;
      if (columnname.equals(SkinManager.instance.getMessageText("column_title_addon"))) return true;
      if (columnname.equals(SkinManager.instance.getMessageText("column_title_bpm_diff"))) return true;
      if (columnname.equals(SkinManager.instance.getMessageText("column_title_mixout_comments"))) return true;
      return SearchPane.instance.checkEditableTitle(columnname);
    }

    public boolean recreatingmixouts = false;
    Semaphore RecreateMixoutColumnsSem = new Semaphore(1);
    public void RecreateMixoutColumns() {
      try {
      RecreateMixoutColumnsSem.acquire();
      recreatingmixouts = true;

      for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns - 1; ++i) MixoutPane.instance.mixoutcolumnconfig.setDefaultWidth(MixoutPane.instance.mixoutcolumnconfig.getIndex(mixouttable.getColumnModel().getColumn(i).getHeaderValue().toString()), mixouttable.getColumnModel().getColumn(i).getWidth());

      boolean ascending = mixouttable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetMixoutColumnCount();
      RecreateUsingMixout(MixoutPane.instance.mixoutcolumnconfig, mixouttable, columncount);

      SkinManager.instance.getTableConfig(mixouttable).setConfig(mixouttable);
      mixouttable.setModel(makeMixModel());
      if (mixoutcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
          TableColumn column = mixouttable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
          if (column != null) {
              column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	          column.setCellEditor(RapidEvolutionUI.instance.getRatingCellRenderer());
          }
      }
      try {
          if (mixoutcolumnconfig.containsColumnType(ColumnConfig.COLUMN_ALBUM_COVER)) {          
              mixouttable.setRowHeight(Integer.parseInt(OptionsUI.instance.albumcoverthumbwidth.getText()) + 10);
          } else {
              mixouttable.setRowHeight(SkinManager.instance.getTableRowHeight());
          }
      } catch (Exception e) { }      
      mixouttable.getModel().addTableModelListener(this);
      mixouttable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(mixouttable));
      for (int i = 0; i < columncount; ++i) {
        mixouttable.getColumnModel().getColumn(i).setPreferredWidth(MixoutPane.instance.mixoutcolumnconfig.getPreferredWidth(i));
        mixouttable.getColumnModel().getColumn(i).setWidth(MixoutPane.instance.mixoutcolumnconfig.getPreferredWidth(i));
      }
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setResizable(false);
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setMinWidth(0);
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setMaxWidth(0);
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setWidth(0);
      mixouttable.sort(mixoutcolumnconfig);
      addoncheckbox.setSelected(false);
      addoncheckbox.setEnabled(false);
      mixoutcomments.setEnabled(false);
      mixoutcomments.setText("");
      mixoutscore.setEnabled(false);
      mixoutscore.setText("");
      bpmdifffield.setEnabled(false);
      bpmdifffield.setText("");
      calculatebpmdiffbutton.setEnabled(false);
      mixoutcommentslabel.setEnabled(false);
      scorefield.setEnabled(false);
      bpmdifflabel.setEnabled(false);
      recreatingmixouts = false;
      } catch (Exception e) { log.error("RecreateMixoutColumns(): error", e); }
      RecreateMixoutColumnsSem.release();
    }

    public void CreateMixoutColumns() {
      if (MixoutPane.instance.mixoutcolumnconfig.num_columns <= 0) {
        OptionsUI.instance.mixoutcolumn_songid.setSelected(true);
        OptionsUI.instance.mixoutcolumn_bpmdiff.setSelected(true);
        OptionsUI.instance.mixoutcolumn_rank.setSelected(true);
        OptionsUI.instance.mixoutcolumn_bpm.setSelected(true);
        OptionsUI.instance.mixoutcolumn_key.setSelected(true);
        OptionsUI.instance.mixoutcolumn_keylock.setSelected(true);
        OptionsUI.instance.mixoutcolumn_length.setSelected(true);
        OptionsUI.instance.mixoutcolumn_comments.setSelected(true);
        OptionsUI.instance.mixoutcolumn_nummixouts.setSelected(true);
        RecreateMixoutColumns();
        return;
      }

      if (SkinManager.instance.getTableConfig(mixouttable) != null) {
        SkinManager.instance.getTableConfig(mixouttable).setConfig(mixouttable);
        mixouttable.setModel(makeMixModel());
        if (mixoutcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
            TableColumn column = mixouttable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
            if (column != null) {
                column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
  	          column.setCellEditor(RapidEvolutionUI.instance.getRatingCellRenderer());
            }
        }
        try {
            if (mixoutcolumnconfig.containsColumnType(ColumnConfig.COLUMN_ALBUM_COVER)) {          
                mixouttable.setRowHeight(Integer.parseInt(OptionsUI.instance.albumcoverthumbwidth.getText()) + 10);
            } else {
                mixouttable.setRowHeight(SkinManager.instance.getTableRowHeight());
            }
        } catch (Exception e) { }              
        mixouttable.getModel().addTableModelListener(this);
        mixouttable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(mixouttable));
        for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
          mixouttable.getColumnModel().getColumn(i).setPreferredWidth(MixoutPane.instance.mixoutcolumnconfig.getPreferredWidth(i));
          mixouttable.getColumnModel().getColumn(i).setWidth(MixoutPane.instance.mixoutcolumnconfig.getPreferredWidth(i));
        }
        mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setResizable(false);
        mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setMinWidth(0);
        mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setMaxWidth(0);
        mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setWidth(0);
        mixouttable.sort(mixoutcolumnconfig);
      }
    }

    Semaphore RedrawMixoutTableSem = new Semaphore(1);
    public void RedrawMixoutTable() {
      try {
      RedrawMixoutTableSem.acquire();
      for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) {
        MixoutPane.instance.mixoutcolumnconfig.columntitles[i] = mixouttable.getColumnModel().getColumn(i).getHeaderValue().toString();
        MixoutPane.instance.mixoutcolumnconfig.setIndex(i);
        MixoutPane.instance.mixoutcolumnconfig.setPreferredWidth(i, mixouttable.getColumnModel().getColumn(i).getWidth());
      }

      int columncount = MixoutPane.instance.mixoutcolumnconfig.num_columns;

      SkinManager.instance.getTableConfig(mixouttable).setConfig(mixouttable);
      mixouttable.setModel(makeMixModel());
      if (mixoutcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
          TableColumn column = mixouttable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
          if (column != null) {
              column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	          column.setCellEditor(RapidEvolutionUI.instance.getRatingCellRenderer());
          }
      }
      mixouttable.getModel().addTableModelListener(this);
      mixouttable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(mixouttable));
      for (int i = 0; i < columncount; ++i) {
        mixouttable.getColumnModel().getColumn(i).setPreferredWidth(MixoutPane.instance.mixoutcolumnconfig.getPreferredWidth(i));
        mixouttable.getColumnModel().getColumn(i).setWidth(MixoutPane.instance.mixoutcolumnconfig.getPreferredWidth(i));
      }
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setResizable(false);
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setMinWidth(0);
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setMaxWidth(0);
      mixouttable.getColumnModel().getColumn(MixoutPane.instance.mixoutcolumnconfig.num_columns).setWidth(0);
      mixouttable.sort(mixoutcolumnconfig);
      mixouttable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      } catch (Exception e) { log.error("RedrawMixoutTable(): error", e); }
      RedrawMixoutTableSem.release();
    }

    private void setupActionListeners() {
        addoncheckbox.addActionListener(this);
        calculatebpmdiffbutton.addActionListener(this);

    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == calculatebpmdiffbutton) {
         float usebpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
         if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) usebpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
         SongLinkedList sll = (SongLinkedList)mixouttable.getModel().getValueAt(mixouttable.getSelectedRow(), mixoutcolumnconfig.num_columns);
         if ((usebpm != 0.0f) && (sll.getStartbpm() != 0.0f)) {
             float bpmdiff = SongUtil.get_bpmdiff(sll.getStartbpm(), usebpm);
             String bpmtext = String.valueOf(bpmdiff);
             if (bpmdiff > 0) bpmtext = new String("+" + bpmtext);
             int length = bpmtext.length();
             if (length > 6) length = 6;
             bpmdifffield.setText(bpmtext.substring(0,length));
             for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
               if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) {
                 try { RapidEvolutionUI.instance.currentsong.setMixoutBpmdiff(i, Float.parseFloat(bpmdifffield.getText())); }
                 catch (Exception e) { }
               }
             UpdateMixout();
         }
      } else if (ae.getSource() == addoncheckbox) {
         for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i)
           if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedmixout.uniquesongid) {
             RapidEvolutionUI.instance.currentsong.setMixoutAddon(i, addoncheckbox.isSelected());
//          if (connectedtoserver) new UpdateServerThread(RapidEvolutionUI.instance.currentsong, i).start();
           }
         UpdateMixout();
      }
    }

    public static int[] defaultColumnIndices = {
      21,33,0,32,1,2,52,3,4,27,22,24,39,5,46,18,25,9,7,8,42,50,12,47,10,11,19,26,35,49,41,48,38,43,34,36,6,28,29,30,31,37,45,40,51,20,23,13,15,16,17,14,44
    };
    public static int getIndexOf(int val) {
        for (int i = 0; i < defaultColumnIndices.length; ++i) if (defaultColumnIndices[i] == val) return i;
        return -1;
    }
    public static boolean columnIsBefore(int c1, int c2) { return (getIndexOf(c1) < getIndexOf(c2)); }
    int[] getDefaultColumnIndices() {
        Vector indices = new Vector();
        if (OptionsUI.instance.mixoutcolumn_songidwinfo.isSelected()) indices.add(new Integer(21));
        if (OptionsUI.instance.mixoutcolumn_shortidwinfo.isSelected()) indices.add(new Integer(33));
        if (OptionsUI.instance.mixoutcolumn_songid.isSelected()) indices.add(new Integer(0));
        if (OptionsUI.instance.mixoutcolumn_shortid.isSelected()) indices.add(new Integer(32));
        if (OptionsUI.instance.mixoutcolumn_artist.isSelected()) indices.add(new Integer(1));
        if (OptionsUI.instance.mixoutcolumn_album.isSelected()) indices.add(new Integer(2));
        if (OptionsUI.instance.mixoutcolumn_albumcover.isSelected()) indices.add(new Integer(52));
        if (OptionsUI.instance.mixoutcolumn_track.isSelected()) indices.add(new Integer(3));
        if (OptionsUI.instance.mixoutcolumn_title.isSelected()) indices.add(new Integer(4));
        if (OptionsUI.instance.mixoutcolumn_remixer.isSelected()) indices.add(new Integer(27));
        if (OptionsUI.instance.mixoutcolumn_rank.isSelected()) indices.add(new Integer(22));
        if (OptionsUI.instance.mixoutcolumn_isaddon.isSelected()) indices.add(new Integer(24));
        if (OptionsUI.instance.mixoutcolumn_mixcomments.isSelected()) indices.add(new Integer(39));
        if (OptionsUI.instance.mixoutcolumn_length.isSelected()) indices.add(new Integer(5));
        if (OptionsUI.instance.mixoutcolumn_rating.isSelected()) indices.add(new Integer(46));
        if (OptionsUI.instance.mixoutcolumn_bpmdiff.isSelected()) indices.add(new Integer(18));
        if (OptionsUI.instance.mixoutcolumn_relbpmdiff.isSelected()) indices.add(new Integer(25));
        if (OptionsUI.instance.mixoutcolumn_bpm.isSelected()) indices.add(new Integer(9));
        if (OptionsUI.instance.mixoutcolumn_startbpm.isSelected()) indices.add(new Integer(7));
        if (OptionsUI.instance.mixoutcolumn_endbpm.isSelected()) indices.add(new Integer(8));
        if (OptionsUI.instance.mixoutcolumn_bpmaccuracy.isSelected()) indices.add(new Integer(42));        
        if (OptionsUI.instance.mixoutcolumn_beatintensity.isSelected()) indices.add(new Integer(50));        
        if (OptionsUI.instance.mixoutcolumn_key.isSelected()) indices.add(new Integer(12));
        if (OptionsUI.instance.mixoutcolumn_actualkey.isSelected()) indices.add(new Integer(47));
        if (OptionsUI.instance.mixoutcolumn_startkey.isSelected()) indices.add(new Integer(10));
        if (OptionsUI.instance.mixoutcolumn_endkey.isSelected()) indices.add(new Integer(11));
        if (OptionsUI.instance.mixoutcolumn_keylock.isSelected()) indices.add(new Integer(19));
        if (OptionsUI.instance.mixoutcolumn_keymode.isSelected()) indices.add(new Integer(26));
        if (OptionsUI.instance.mixoutcolumn_keycode.isSelected()) indices.add(new Integer(35));
        if (OptionsUI.instance.mixoutcolumn_actualkeycode.isSelected()) indices.add(new Integer(49));
        if (OptionsUI.instance.mixoutcolumn_keyaccuracy.isSelected()) indices.add(new Integer(41));                
        if (OptionsUI.instance.mixoutcolumn_pitchshift.isSelected()) indices.add(new Integer(48));
        if (OptionsUI.instance.mixoutcolumn_artistsimilarity.isSelected()) indices.add(new Integer(38));
        if (OptionsUI.instance.mixoutcolumn_styles.isSelected()) indices.add(new Integer(43));
        if (OptionsUI.instance.mixoutcolumn_stylesimilarity.isSelected()) indices.add(new Integer(34));
        if (OptionsUI.instance.mixoutcolumn_colorsimilarity.isSelected()) indices.add(new Integer(36));
        if (OptionsUI.instance.mixoutcolumn_timesig.isSelected()) indices.add(new Integer(6));
        if (OptionsUI.instance.mixoutcolumn_user1.isSelected()) indices.add(new Integer(28));
        if (OptionsUI.instance.mixoutcolumn_user2.isSelected()) indices.add(new Integer(29));
        if (OptionsUI.instance.mixoutcolumn_user3.isSelected()) indices.add(new Integer(30));
        if (OptionsUI.instance.mixoutcolumn_user4.isSelected()) indices.add(new Integer(31));
        if (OptionsUI.instance.mixoutcolumn_dateadded.isSelected()) indices.add(new Integer(37));
        if (OptionsUI.instance.mixoutcolumn_lastmodified.isSelected()) indices.add(new Integer(45));
        if (OptionsUI.instance.mixoutcolumn_timesplayed.isSelected()) indices.add(new Integer(40));
        if (OptionsUI.instance.mixoutcolumn_replaygain.isSelected()) indices.add(new Integer(51));
        if (OptionsUI.instance.mixoutcolumn_nummixouts.isSelected()) indices.add(new Integer(20));
        if (OptionsUI.instance.mixoutcolumn_numaddons.isSelected()) indices.add(new Integer(23));
        if (OptionsUI.instance.mixoutcolumn_comments.isSelected()) indices.add(new Integer(13));
        if (OptionsUI.instance.mixoutcolumn_vinyl.isSelected()) indices.add(new Integer(15));
        if (OptionsUI.instance.mixoutcolumn_nonvinyl.isSelected()) indices.add(new Integer(16));
        if (OptionsUI.instance.mixoutcolumn_disabled.isSelected()) indices.add(new Integer(17));
        if (OptionsUI.instance.mixoutcolumn_filename.isSelected()) indices.add(new Integer(14));
        if (OptionsUI.instance.mixoutcolumn_hasalbumcover.isSelected()) indices.add(new Integer(44));
        int[] returnval = new int[indices.size()];
        for (int i = 0; i < indices.size(); ++i) returnval[i] = ((Integer)indices.get(i)).intValue();
        return returnval;
    }

    public static int[] determineOrdering(int[] defaultorder, int[] originalorder, boolean reset) {
      try {
        int[] returnval = new int[defaultorder.length];
        Vector temparray = new Vector();
        for (int i = 0; i < originalorder.length; ++i) temparray.add(new
            Integer(originalorder[i]));
        for (int i = 0; i < defaultorder.length; ++i) {
          boolean exists = false;
          int j = 0;
          while (!exists && (j < temparray.size())) {
            int val = ( (Integer) temparray.get(j)).intValue();
            if (val == defaultorder[i]) exists = true;
            ++j;
          }
          if (!exists) {
            boolean inserted = false;
            j = 0;
            while (!inserted && (j < temparray.size())) {
              int val = ( (Integer) temparray.get(j)).intValue();
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
            int val = ( (Integer) temparray.get(i)).intValue();
            newDefaultMatrix[i] = val;
          }
          defaultColumnIndices = determineOrdering(defaultColumnIndices,
              newDefaultMatrix, false);
        }
        for (int i = 0; i < temparray.size(); ++i) {
          int val = ( (Integer) temparray.get(i)).intValue();
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

    public void RecreateUsingMixout(ColumnConfig columnconfig, JSortTable table, int columncount) {
        int[] defaultorder = getDefaultColumnIndices();
        int[] oldorder = new int[MixoutPane.instance.mixoutcolumnconfig.num_columns];
        for (int i = 0; i < MixoutPane.instance.mixoutcolumnconfig.num_columns; ++i) oldorder[i] = MixoutPane.instance.mixoutcolumnconfig.getIndex(MixoutPane.instance.mixouttable.getColumnModel().getColumn(i).getHeaderValue().toString());
        int[] neworder = determineOrdering(defaultorder, oldorder, true);
        RecreateUsingMixout(columnconfig, table, columncount, neworder);
    }

    public void RecreateUsingMixout(ColumnConfig columnconfig, JSortTable table, int columncount, int[] columnordering) {
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
      for (int i = 0; i < columnordering.length; ++i) {
        columnconfig.columnindex[columncount] = columnordering[i];
        columnconfig.setPreferredWidth(columncount, columnconfig.getDefaultWidth(columnordering[i]));
        columnconfig.columntitles[columncount++] = columnconfig.getKeyword(columnordering[i]);
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
    
    public SongLinkedList[] getSelectedMixoutSongs() {
        int[] selected = mixouttable.getSelectedRows();
        SongLinkedList[] songs = new SongLinkedList[selected.length];
      for (int i = 0; i < selected.length; ++i) {
        SongLinkedList song = (SongLinkedList)mixouttable.getModel().getValueAt(selected[i], mixoutcolumnconfig.num_columns);
        songs[i] = song;
      }
      return songs;
    }
    
}

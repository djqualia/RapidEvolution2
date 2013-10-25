package rapid_evolution.ui;

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import rapid_evolution.MixInfo;
import rapid_evolution.ServerMixout;
import rapid_evolution.RapidEvolution;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.main.MixoutPane;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class SuggestedMixesUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(SuggestedMixesUI.class);
    
    public SuggestedMixesUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static SuggestedMixesUI instance = null;
    public JButton suggestedokbutton = new REButton();
    public ColumnConfig suggestedcolumnconfig = new ColumnConfig();
    public DroppableSuggestedTable suggestedtable = new DroppableSuggestedTable();
    public JButton suggestedaddexclude = new REButton();
    public JCheckBox showallsuggested = new RECheckBox();

    private void setupDialog() {
            // suggested mixes dialog
            suggestedaddexclude.setEnabled(false);
            suggestedtable.getSelectionModel().addListSelectionListener(new SuggestedSelectionListener());
    }

    public void PostInit() {
      if (!RapidEvolution.instance.loaded) SkinManager.instance.setEnabled("suggested_mixouts_add_mixout_button", false);
    }

    public class DroppableSuggestedTable extends RETable implements DragSourceListener, DragGestureListener {
        DragSource dragSource = DragSource.getDefaultDragSource();
        public DroppableSuggestedTable() {
          dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
          setModel(makeSuggestedModel());
          if (suggestedcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
    	      TableColumn column = suggestedtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
    	      if (column != null) {
    	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
    	      }
          }
          getColumnModel().addColumnModelListener(new NewTableColumnModelListener(this));
        }
        public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
        public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
        public void dragExit(DragSourceEvent DragSourceEvent){}
        public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
        public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}
        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
          if (getSelectedRowCount() != 1) return;
          int selection = getSelectedRow();
          if ((selection < 0) || (selection >= getRowCount())) return;
          try {
            SongLinkedList song = ((SongLinkedList)getValueAt(getSelectedRow(), suggestedcolumnconfig.num_columns));
            if (song.getFileName() != null) {
              FileSelection transferable = new FileSelection(song.getFile(), song);
              dragGestureEvent.startDrag(
                    DragSource.DefaultCopyDrop,
                    transferable,
                    this);
            }
          } catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
        }
        public Component prepareRenderer(TableCellRenderer r, int row, int col) {
          Component c = null;
          try {
            c = super.prepareRenderer(r, row, col);
            SongLinkedList newsong = (SongLinkedList) suggestedtable.getModel().
                getValueAt(row, suggestedcolumnconfig.num_columns);
            SongLinkedList song = SongDB.instance.NewGetSongPtr(newsong.uniquesongid);
            if (song != null) {
              if (row == suggestedtable.getSelectedRow())
                return c;
              c.setBackground(song.getSuggestedColor());
              return c;
            }
          } catch (Exception e) { }
          return c;

        }
    }

    public class SuggestedSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
          if (suggestedtable.getSelectedRowCount() == 1) {
            long selectedsongid = ((SongLinkedList)suggestedtable.getValueAt(suggestedtable.getSelectedRow(), suggestedcolumnconfig.num_columns)).uniquesongid;;
            boolean mixoutfound = false;
            boolean excludefound = false;
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedsongid) mixoutfound = true;
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumExcludeSongs(); ++i) if (RapidEvolutionUI.instance.currentsong.exclude_songs[i] == selectedsongid) excludefound = true;
            if (!mixoutfound) SkinManager.instance.setEnabled("suggested_mixouts_add_mixout_button", true);
            else SkinManager.instance.setEnabled("suggested_mixouts_add_mixout_button", false);
            if (!excludefound) suggestedaddexclude.setEnabled(true);
            else suggestedaddexclude.setEnabled(false);
          } else {
            SkinManager.instance.setEnabled("suggested_mixouts_add_mixout_button", false);
            suggestedaddexclude.setEnabled(false);
          }
        }
    }

    SortTableModel makeSuggestedModel() {
      Vector data = new Vector();
      if ((RapidEvolutionUI.instance.currentsong != null) && (RapidEvolutionUI.instance.suggestedsongs != null)) {
        for (int i = 0; i < RapidEvolutionUI.instance.suggestedsongs.size(); ++i) {
          SongLinkedList iter = (SongLinkedList)RapidEvolutionUI.instance.suggestedsongs.get(i);
          Vector row = new Vector();
          for (int j = 0; j < suggestedcolumnconfig.num_columns; ++j)
            row.add(iter.get_suggestedcolumn_data(suggestedcolumnconfig.columnindex[j]));
          row.add(iter);
          data.add(row);
        }
      }
      Vector columnames = new Vector();
      for (int i = 0; i < suggestedcolumnconfig.num_columns; ++i)
        columnames.add(suggestedcolumnconfig.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
          return false;
        }
      };
      return result;
    }

    public boolean recreatingsuggested = false;
    Semaphore RecreateSuggestedColumnsSem = new Semaphore(1);
    public void RecreateSuggestedColumns() {
      try {
      RecreateSuggestedColumnsSem.acquire();
      recreatingsuggested = true;

      for (int i = 0; i < suggestedcolumnconfig.num_columns - 1; ++i) suggestedcolumnconfig.setDefaultWidth(suggestedcolumnconfig.getIndex(suggestedtable.getColumnModel().getColumn(i).getHeaderValue().toString()), suggestedtable.getColumnModel().getColumn(i).getWidth());

      boolean ascending = suggestedtable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetMixoutColumnCount();
      MixoutPane.instance.RecreateUsingMixout(suggestedcolumnconfig, suggestedtable, columncount);

      SkinManager.instance.getTableConfig(suggestedtable).setConfig(suggestedtable);
      suggestedtable.setModel(makeSuggestedModel());
      if (suggestedcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = suggestedtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      suggestedtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(suggestedtable));
      for (int i = 0; i < columncount; ++i) {
        suggestedtable.getColumnModel().getColumn(i).setPreferredWidth(suggestedcolumnconfig.getPreferredWidth(i));
        suggestedtable.getColumnModel().getColumn(i).setWidth(suggestedcolumnconfig.getPreferredWidth(i));
      }
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setResizable(false);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setMinWidth(0);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setMaxWidth(0);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setWidth(0);
      
      suggestedtable.sort(suggestedcolumnconfig);

      recreatingsuggested = false;
      } catch (Exception e) { log.error("RecreateSuggestedColumns(): error", e); }
      RecreateSuggestedColumnsSem.release();
    }

    Semaphore RedrawSuggestedSem = new Semaphore(1);
    public void RedrawSuggested() {
      try {
      RedrawSuggestedSem.acquire();

      int columncount = suggestedcolumnconfig.num_columns;

      for (int i = 0; i < suggestedcolumnconfig.num_columns; ++i) {
        suggestedcolumnconfig.columntitles[i] = suggestedtable.getColumnModel().getColumn(i).getHeaderValue().toString();
        suggestedcolumnconfig.setIndex(i);
        suggestedcolumnconfig.setPreferredWidth(i, suggestedtable.getColumnModel().
            getColumn(i).getWidth());
      }

      SkinManager.instance.getTableConfig(suggestedtable).setConfig(suggestedtable);
      suggestedtable.setModel(makeSuggestedModel());
      if (suggestedcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = suggestedtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      suggestedtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(suggestedtable));
      for (int i = 0; i < columncount; ++i) {
        suggestedtable.getColumnModel().getColumn(i).setPreferredWidth(suggestedcolumnconfig.getPreferredWidth(i));
        suggestedtable.getColumnModel().getColumn(i).setWidth(suggestedcolumnconfig.getPreferredWidth(i));
      }
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setResizable(false);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setMinWidth(0);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setMaxWidth(0);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setWidth(0);
      suggestedtable.sort(suggestedcolumnconfig);
      } catch (Exception e) { log.error("RedrawSuggested(): error", e); }
      RedrawSuggestedSem.release();
    }

    public void CreateSuggestedColumns() {
      if (suggestedcolumnconfig.num_columns <= 0) { RecreateSuggestedColumns(); return; }
      SkinManager.instance.getTableConfig(suggestedtable).setConfig(suggestedtable);
      suggestedtable.setModel(makeSuggestedModel());
      if (suggestedcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = suggestedtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      suggestedtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(suggestedtable));
      for (int i = 0; i < suggestedcolumnconfig.num_columns; ++i) {
        suggestedtable.getColumnModel().getColumn(i).setPreferredWidth(suggestedcolumnconfig.getPreferredWidth(i));
        suggestedtable.getColumnModel().getColumn(i).setWidth(suggestedcolumnconfig.getPreferredWidth(i));
      }
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setResizable(false);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setMinWidth(0);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setMaxWidth(0);
      suggestedtable.getColumnModel().getColumn(suggestedcolumnconfig.num_columns).setWidth(0);
      suggestedtable.sort(suggestedcolumnconfig);
    }

    public void DisplaySuggestedMixouts(Vector lastqueryresults) {
      RapidEvolutionUI.instance.suggestedsongs = new Vector();
      RapidEvolutionUI.instance.suggestedinfo = new Vector();
      for (int i = 0; i < lastqueryresults.size(); ++i) {
        ServerMixout mixout = (ServerMixout)lastqueryresults.get(i);
        try {
        	String matchArtist = mixout.toSong.getArtist().toLowerCase();
        	String matchTitle = mixout.toSong.getSongname().toLowerCase();
        	SongLinkedList iter = SongDB.instance.SongLL;
        	SongLinkedList song = null;
        	while ((iter != null) && (song == null)) {
        		if (matchArtist.startsWith(iter.getArtist().toLowerCase())) {
        			String titleWithRemix = iter.getSongname();
        			if (iter.getRemixer().length() > 0)
        				titleWithRemix = titleWithRemix + " (" + iter.getRemixer() + ")";
        			if (matchTitle.equalsIgnoreCase(titleWithRemix)) {
        				song = iter;
        			}
        		}        		
        		iter = iter.next;
        	}        
        boolean valid = true;
        boolean notfound = true;
        if ((song == null) && showallsuggested.isSelected()) song = mixout.toSong;
        if (song != null) {
          if (song == RapidEvolutionUI.instance.currentsong) valid = false;
          else { for (int z = 0; z < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++z) if (RapidEvolutionUI.instance.currentsong.mixout_songs[z] == song.uniquesongid) { valid = false; notfound = false; } }
        } else valid = false;
        if (showallsuggested.isSelected() && notfound) SkinManager.instance.setEnabled("current_song_mixes_button", true);
        if (valid) {
//              if (debugmode) System.out.println("-- mix found -- : " + song.songid);
          SkinManager.instance.setEnabled("current_song_mixes_button", true);
          RapidEvolutionUI.instance.suggestedsongs.add(song);

//              float usebpm = currentsong.bpm;
//              if (currentsong.endbpm != 0) usebpm = currentsong.endbpm;
//              String bpmdiff = new String("");
//              SongLinkedList sll = song;
//              if ((sll.bpm != 0) && (usebpm != 0)) bpmdiff = String.valueOf(-SongUtil.get_bpmdiff(sll.bpm, usebpm));

          RapidEvolutionUI.instance.suggestedinfo.add(new MixInfo(mixout.comments, mixout.rank, mixout.addon, String.valueOf(mixout.bpmdiff)));
        }
        } catch (Exception e) { log.error("DisplaySuggestedMixouts(): error", e); }
      }
      RedrawSuggested();
    }

    private void setupActionListeners() {
        suggestedokbutton.addActionListener(this);
        suggestedaddexclude.addActionListener(this);
        showallsuggested.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == suggestedaddexclude) {
        if (!AddExcludeUI.instance.isVisible()) {
          RapidEvolutionUI.instance.excludesong = new Vector();
          SongLinkedList excludedsong = (SongLinkedList) (suggestedtable.getModel().getValueAt(suggestedtable.getSelectedRow(), suggestedcolumnconfig.num_columns));
          RapidEvolutionUI.instance.excludesong.add(excludedsong);
          if (RapidEvolutionUI.instance.excludesong != null) {
            RapidEvolutionUI.instance.rootsinvoked = 3;
            AddExcludeUI.instance.addreverseexcludebt.setSelected(false);
            AddExcludeUI.instance.excludefromfield.setText(RapidEvolutionUI.instance.currentsong.getSongIdShort());
            AddExcludeUI.instance.excludetofield.setText(excludedsong.getSongIdShort());
            AddExcludeUI.instance.addreverseexcludebt.setSelected(false);
            AddExcludeUI.instance.Display(suggestedaddexclude);
          }
        } else AddExcludeUI.instance.requestFocus();
      } else if (ae.getSource() == suggestedokbutton) {
            setVisible(false);
      } else if (ae.getSource() == showallsuggested) {
           DisplaySuggestedMixouts(RapidEvolutionUI.instance.lastqueryresults);
      }
    }
}

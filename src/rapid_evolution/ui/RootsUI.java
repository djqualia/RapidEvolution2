package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import rapid_evolution.SongLinkedList;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DnDConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.dnd.DragSourceListener;
import java.io.File;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.Component;
import java.awt.dnd.DragSourceDropEvent;
import java.util.Vector;
import rapid_evolution.SongStack;
import rapid_evolution.ui.OptionsUI;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.SongDB;
import rapid_evolution.ui.REDialog;
import javax.swing.JViewport;

import org.apache.log4j.Logger;

import rapid_evolution.ui.RETable;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class RootsUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(RootsUI.class);
    
    public RootsUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static RootsUI instance = null;
    public JButton rootsokbutton = new REButton();
    public JButton rootsaddexclude  = new REButton();
    public ColumnConfig rootscolumnconfig = new ColumnConfig();
    public DroppableRootsTable rootstable = new DroppableRootsTable();

    private void setupDialog() {
       rootstable.addMouseListener(new RootsMouse());
       rootstable.getSelectionModel().addListSelectionListener(new RootsSelectionListener());
       rootsaddexclude.setEnabled(false);
    }

    public void PostInit() {
      if (!RapidEvolution.instance.loaded) SkinManager.instance.setEnabled("roots_add_mixout_button", false);
    }

    public class DroppableRootsTable extends RETable implements DragSourceListener, DragGestureListener {
        DragSource dragSource = DragSource.getDefaultDragSource();
        public DroppableRootsTable() {
          dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
          setModel(makeRootsModel());
          if (rootscolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
    	      TableColumn column = rootstable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
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
            SongLinkedList song = ((SongLinkedList)getValueAt(getSelectedRow(), rootscolumnconfig.num_columns));
            if (song.getFileName() != null) {
              FileSelection transferable = new FileSelection(song.getFile(), song);
              dragGestureEvent.startDrag(
                    DragSource.DefaultCopyDrop,
                    transferable,
                    this);
              RapidEvolutionUI.instance.lastdragsourceindex = 3;
            }
          } catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
        }
        public Component prepareRenderer(TableCellRenderer r, int row, int col) {
          Component c = null;
          try {
            c = super.prepareRenderer(r, row, col);
            SongLinkedList song = (SongLinkedList) rootstable.getModel().
                getValueAt(row, rootscolumnconfig.num_columns);
            if (song != null) {
              if (row == rootstable.getSelectedRow())
                return c;
              c.setBackground(song.getRootColor());
              return c;
            }
          } catch (Exception e) { }
          return c;

        }
    }

    SortTableModel makeRootsModel() {
      int count = 0;
      Vector data = new Vector();
      if (RapidEvolutionUI.instance.currentsong != null) {
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          for (int i = 0; i < iter.getNumMixoutSongs(); ++i) {
            if (iter.mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) {
              boolean valid = true;
              for (int j = 0; j < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++j) {
                //if (RapidEvolutionUI.instance.currentsong.mixout_songs[j] == iter.uniquesongid) valid = false;
              }
              for (int j = 0; j < RapidEvolutionUI.instance.currentsong.getNumExcludeSongs(); ++j) {
                if (RapidEvolutionUI.instance.currentsong.exclude_songs[j] == iter.uniquesongid) valid = false;
              }
              if (OptionsUI.instance.preventrepeats.isSelected()) {
                SongStack ssiter = RapidEvolutionUI.instance.prevstack;
                while (valid && (ssiter != null)) {
                  if (ssiter.songid == iter.uniquesongid) valid = false;
                  ssiter = ssiter.next;
                }
              }
              if (valid) {
                count++;
                Vector row = new Vector();
                for (int j = 0; j < rootscolumnconfig.num_columns; ++j)
                  row.add(iter.get_column_rootdata(rootscolumnconfig.columnindex[j]));
                row.add(iter);
                data.add(row);
              }
            }
          }
          iter = iter.next;
        }
      }
      if (SkinManager.instance != null) {
        if (count > 0) SkinManager.instance.setEnabled("current_song_roots_button", true);
        else SkinManager.instance.setEnabled("current_song_roots_button", false);
      }
      Vector columnames = new Vector();
      for (int i = 0; i < rootscolumnconfig.num_columns; ++i)
        columnames.add(rootscolumnconfig.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
          return false;
        }
      };
      return result;
    }

    public class RootsSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
          if (rootstable.getSelectedRowCount() == 1) {
            long selectedsongid = ((SongLinkedList)rootstable.getValueAt(rootstable.getSelectedRow(), rootscolumnconfig.num_columns)).uniquesongid;;
            boolean mixoutfound = false;
            boolean excludefound = false;
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumMixoutSongs(); ++i) if (RapidEvolutionUI.instance.currentsong.mixout_songs[i] == selectedsongid) mixoutfound = true;
            for (int i = 0; i < RapidEvolutionUI.instance.currentsong.getNumExcludeSongs(); ++i) if (RapidEvolutionUI.instance.currentsong.exclude_songs[i] == selectedsongid) excludefound = true;
            if (!mixoutfound) SkinManager.instance.setEnabled("roots_add_mixout_button", true);
            else SkinManager.instance.setEnabled("roots_add_mixout_button", false);
            if (!excludefound) rootsaddexclude.setEnabled(true);
            else rootsaddexclude.setEnabled(false);
          } else {
            SkinManager.instance.setEnabled("roots_add_mixout_button", false);
            rootsaddexclude.setEnabled(false);
          }
        }
    }

    class RootsMouse extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          if (rootstable.getSelectedRowCount() == 1) {
            SongLinkedList selectedsongid = ((SongLinkedList)rootstable.getValueAt(rootstable.getSelectedRow(), rootscolumnconfig.num_columns));
            RapidEvolutionUI.instance.change_current_song(selectedsongid, 0.0f, false, false);
          }
        }

      }
    }

    public boolean recreatingroots = false;
    Semaphore RecreateRootsColumnsSem = new Semaphore(1);
    public void RecreateRootsColumns() {
      try {
      RecreateRootsColumnsSem.acquire();
      recreatingroots = true;

      for (int i = 0; i < rootscolumnconfig.num_columns - 1; ++i) rootscolumnconfig.setDefaultWidth(rootscolumnconfig.getIndex(rootstable.getColumnModel().getColumn(i).getHeaderValue().toString()), rootstable.getColumnModel().getColumn(i).getWidth());

      boolean ascending = rootstable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetMixoutColumnCount();
      MixoutPane.instance.RecreateUsingMixout(rootscolumnconfig, rootstable, columncount);

      SkinManager.instance.getTableConfig(rootstable).setConfig(rootstable);
      rootstable.setModel(makeRootsModel());
      if (rootscolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = rootstable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      rootstable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(rootstable));
      for (int i = 0; i < columncount; ++i) {
        rootstable.getColumnModel().getColumn(i).setPreferredWidth(rootscolumnconfig.getPreferredWidth(i));
        rootstable.getColumnModel().getColumn(i).setWidth(rootscolumnconfig.getPreferredWidth(i));
      }
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setResizable(false);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setMinWidth(0);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setMaxWidth(0);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setWidth(0);

	rootstable.sort(rootscolumnconfig);
      
      recreatingroots = false;
      } catch (Exception e) { log.error("RecreateRootsColumns(): error", e); }
      RecreateRootsColumnsSem.release();
    }

    Semaphore RedrawRootsSem = new Semaphore(1);
    public void RedrawRoots() {
      try {
      RedrawRootsSem.acquire();
      int columncount = rootscolumnconfig.num_columns;
      for (int i = 0; i < rootscolumnconfig.num_columns; ++i) {
        rootscolumnconfig.columntitles[i] = rootstable.getColumnModel().getColumn(i).getHeaderValue().toString();
        rootscolumnconfig.setIndex(i);
        rootscolumnconfig.setPreferredWidth(i, rootstable.getColumnModel().
            getColumn(i).getWidth());
      }
      SkinManager.instance.getTableConfig(rootstable).setConfig(rootstable);
      rootstable.setModel(makeRootsModel());
      if (rootscolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = rootstable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      rootstable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(rootstable));
      for (int i = 0; i < columncount; ++i) {
        rootstable.getColumnModel().getColumn(i).setPreferredWidth(rootscolumnconfig.getPreferredWidth(i));
        rootstable.getColumnModel().getColumn(i).setWidth(rootscolumnconfig.getPreferredWidth(i));
      }
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setResizable(false);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setMinWidth(0);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setMaxWidth(0);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setWidth(0);
  	rootstable.sort(rootscolumnconfig);
      } catch (Exception e) { log.error("RecreateRootsColumns(): error", e); }
      RedrawRootsSem.release();
    }

    public void CreateRootsColumns() {
      if (rootscolumnconfig.num_columns <= 0) { RecreateRootsColumns(); return; }
      SkinManager.instance.getTableConfig(rootstable).setConfig(rootstable);
      rootstable.setModel(makeRootsModel());
      if (rootscolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = rootstable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      rootstable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(rootstable));
      for (int i = 0; i < rootscolumnconfig.num_columns; ++i) {
        rootstable.getColumnModel().getColumn(i).setPreferredWidth(rootscolumnconfig.getPreferredWidth(i));
        rootstable.getColumnModel().getColumn(i).setWidth(rootscolumnconfig.getPreferredWidth(i));
      }
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setResizable(false);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setMinWidth(0);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setMaxWidth(0);
      rootstable.getColumnModel().getColumn(rootscolumnconfig.num_columns).setWidth(0);
  	rootstable.sort(rootscolumnconfig);
    }

    private void setupActionListeners() {

       rootsokbutton.addActionListener(this);
       rootsaddexclude.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == rootsaddexclude) {
        if (!AddExcludeUI.instance.isVisible()) {
          RapidEvolutionUI.instance.rootsinvoked = 1;
          AddExcludeUI.instance.addreverseexcludebt.setSelected(false);
          AddExcludeUI.instance.excludefromfield.setText(RapidEvolutionUI.instance.currentsong.getSongIdShort());
          RapidEvolutionUI.instance.excludesong = new Vector();
          SongLinkedList excludedsong = (SongLinkedList) (rootstable.getModel().getValueAt(rootstable.getSelectedRow(), rootscolumnconfig.num_columns));
          RapidEvolutionUI.instance.excludesong.add(excludedsong);
          AddExcludeUI.instance.excludetofield.setText(excludedsong.getSongIdShort());
          AddExcludeUI.instance.addreverseexcludebt.setSelected(false);
          AddExcludeUI.instance.Display();
        } else AddExcludeUI.instance.requestFocus();
      } else if (ae.getSource() == rootsokbutton) {
            setVisible(false);
      }
    }
}

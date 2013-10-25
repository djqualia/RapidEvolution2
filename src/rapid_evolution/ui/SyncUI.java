package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import rapid_evolution.RapidEvolution;
import javax.swing.JButton;
import rapid_evolution.SongLinkedList;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetEvent;
import java.io.File;
import java.util.Iterator;
import java.awt.dnd.DropTarget;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDragEvent;
import java.util.Vector;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import rapid_evolution.ui.NewTableColumnModelListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.DefaultListModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.REDialog;
import javax.swing.JViewport;

import org.apache.log4j.Logger;

import rapid_evolution.ui.RETable;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class SyncUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(SyncUI.class);
    
    public SyncUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
        addsyncsongs_ui = new AddSyncSongsUI("add_sync_songs_dialog");
    }

    public static SyncUI instance = null;
    public JButton synccancelbutton = new REButton();
    public JButton syncsongsbutton = new REButton();
    public JButton syncremovebutton = new REButton();
    public ColumnConfig synccolumnconfig = new ColumnConfig();
    public DroppableSyncTable synctable = new DroppableSyncTable();
    public Vector addedsyncsongs = null;
    public DroppableSyncScrollPane syncscroll;
    public AddSyncSongsUI addsyncsongs_ui;

    private void setupDialog() {
        syncscroll = new DroppableSyncScrollPane(synctable);
        SkinManager.addScroll(synctable, syncscroll);
        synctable.getSelectionModel().addListSelectionListener(new SyncSelectionListener());
        syncsongsbutton.setEnabled(false);
        syncremovebutton.setEnabled(false);
    }

    public class SyncSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
          if (!recreatingsync && (synctable.getSelectedRowCount() >= 1)) {
            syncremovebutton.setEnabled(true);
          } else {
            syncremovebutton.setEnabled(false);
          }
        }
    }

    public class DroppableSyncTable extends RETable implements DropTargetListener
    {
        public DroppableSyncTable() {
          setModel(makeSyncModel());
          if (synccolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
    	      TableColumn column = synctable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
    	      if (column != null) {
    	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
    	      }
          }
          getColumnModel().addColumnModelListener(new NewTableColumnModelListener(this));
        }

        DropTarget dropTarget = new DropTarget (this, this);
        public void dragEnter (DropTargetDragEvent dropTargetDragEvent) { dropTargetDragEvent.acceptDrag (DnDConstants.ACTION_COPY); }
        public void dragExit (DropTargetEvent dropTargetEvent) {}
        public void dragOver (DropTargetDragEvent dropTargetDragEvent) {}
        public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent){}
        public void drop (DropTargetDropEvent dropTargetDropEvent) {
          dropSong(dropTargetDropEvent);
        }

        public Component prepareRenderer(TableCellRenderer r, int row, int col) {
          Component c = null;
          try {
            c = super.prepareRenderer(r, row, col);;
            SongLinkedList song = (SongLinkedList) synctable.getModel().
                getValueAt(row, SearchPane.instance.searchcolumnconfig.num_columns);
            if (song == null)
              return c;
            int[] rowsel = synctable.getSelectedRows();
            for (int i = 0; i < rowsel.length; ++i) {
              if (rowsel[i] == row)
                return c;
            }
            c.setBackground(song.getSearchColor());
          } catch (Exception e) { }
          return c;
        }

    }

    private void dropSong(DropTargetDropEvent dropTargetDropEvent) {
      if ((RapidEvolutionUI.instance.lastdragsourceindex == 0) && dropTargetDropEvent.isLocalTransfer()) return;

      Transferable t = dropTargetDropEvent.getTransferable();
      boolean success = false;
      try {
          if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            if (RapidEvolutionUI.instance.currentsong != null) {
              Object x = t.getTransferData(DataFlavor.stringFlavor);
              if (x instanceof Vector) {
                  Vector re2selection = (Vector)x;
                  for (int i = 0; i < re2selection.size(); ++i) {
                      SongLinkedList addsong = SongDB.instance.NewGetSongPtr(((Long)re2selection.get(i)).longValue());                
                      if (RapidEvolutionUI.instance.currentsong.uniquesongid == addsong.uniquesongid) return;
                      boolean notfound = true;
                      for (int j = 0; j < addedsyncsongs.size(); ++j) {
                          if (addedsyncsongs.get(j) == addsong) notfound = false;
                      }
                      if (notfound) addedsyncsongs.add(addsong);
                  }
                  Redrawsync();
                  if (synctable.getRowCount() > 0) syncsongsbutton.setEnabled(true);
                  success = true;
              }
            }
          }
      } catch (Exception e) { }
      if (!success) {
         try {
         dropTargetDropEvent.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
         java.util.List fileList = (java.util.List)t.getTransferData(DataFlavor.javaFileListFlavor);
         Iterator iterator = fileList.iterator();
         File file = null;
         while (iterator.hasNext() && (RapidEvolutionUI.instance.currentsong != null))
         {
           file = (File)iterator.next();
           SongLinkedList iter = SongDB.instance.SongLL;
           boolean notfound = true;
           while (notfound && (iter != null)) {
             if ((iter.getFileName().equals(file.getAbsolutePath())) && (iter != RapidEvolutionUI.instance.currentsong)) {
               notfound = false;
                 SongLinkedList addsong = iter;
                 boolean notfound2 = true;
                 for (int j = 0; j < addedsyncsongs.size(); ++j) {
                   if (addedsyncsongs.get(j) == addsong) notfound2 = false;
                 }
                 if (notfound2) addedsyncsongs.add(addsong);
                 Redrawsync();
                 if (synctable.getRowCount() > 0) syncsongsbutton.setEnabled(true);
             }
             iter = iter.next;
           }
         }
         } catch (Exception e2) { log.error("dropSong(): error", e2); }
       }
      RapidEvolutionUI.instance.lastdragsourceindex = -1;
    }

    public class DroppableSyncScrollPane extends JScrollPane implements DropTargetListener {
      DropTarget dropTarget = new DropTarget (this, this);
      public DroppableSyncScrollPane(Component view) { super(view); }
      public void dragEnter (DropTargetDragEvent dropTargetDragEvent) { dropTargetDragEvent.acceptDrag (DnDConstants.ACTION_COPY); }
      public void dragExit (DropTargetEvent dropTargetEvent) {}
      public void dragOver (DropTargetDragEvent dropTargetDragEvent) {}
      public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent){}
      public void drop (DropTargetDropEvent dropTargetDropEvent) {
        dropSong(dropTargetDropEvent);
      }
    }

    public void CreateSyncColumns() {
      if (synccolumnconfig.num_columns <= 0) { RecreateSyncColumns(); return; }
      SkinManager.instance.getTableConfig(synctable).setConfig(synctable);
      synctable.setModel(makeSyncModel());
      if (synccolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = synctable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      synctable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(synctable));
      for (int i = 0; i < synccolumnconfig.num_columns; ++i) {
        synctable.getColumnModel().getColumn(i).setPreferredWidth(synccolumnconfig.getPreferredWidth(i));
        synctable.getColumnModel().getColumn(i).setWidth(synccolumnconfig.getPreferredWidth(i));
      }
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setResizable(false);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setMinWidth(0);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setMaxWidth(0);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setWidth(0);
      synctable.sort(synccolumnconfig);
    }

    Semaphore RedrawSyncSem = new Semaphore(1);
    public void Redrawsync() {
      try {
      RedrawSyncSem.acquire();

      int columncount = synccolumnconfig.num_columns;

      for (int i = 0; i < synccolumnconfig.num_columns; ++i) {
        synccolumnconfig.columntitles[i] = synctable.getColumnModel().getColumn(i).getHeaderValue().toString();
        synccolumnconfig.setIndex(i);
        synccolumnconfig.setPreferredWidth(i, synctable.getColumnModel().
            getColumn(i).getWidth());
      }
      SkinManager.instance.getTableConfig(synctable).setConfig(synctable);
      synctable.setModel(makeSyncModel());
      if (synccolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = synctable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      synctable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(synctable));
      for (int i = 0; i < columncount; ++i) {
        synctable.getColumnModel().getColumn(i).setPreferredWidth(synccolumnconfig.getPreferredWidth(i));
        synctable.getColumnModel().getColumn(i).setWidth(synccolumnconfig.getPreferredWidth(i));
      }
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setResizable(false);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setMinWidth(0);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setMaxWidth(0);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setWidth(0);
      synctable.sort(synccolumnconfig);
      } catch (Exception e) { log.error("Redrawsync(): error", e); }
      RedrawSyncSem.release();
    }

    public SortTableModel makeSyncModel() {
      Vector data = new Vector();
      if (RapidEvolutionUI.instance.currentsong != null) {
        SongLinkedList iter = SongDB.instance.SongLL;
        while (iter != null) {
          if (iter != RapidEvolutionUI.instance.currentsong) {
            for (int j = 0; j < addedsyncsongs.size(); ++j) {
              if (iter.uniquesongid == ((SongLinkedList)addedsyncsongs.get(j)).uniquesongid) {
                Vector row = new Vector();
                for (int i = 0; i < synccolumnconfig.num_columns; ++i)
                  row.add(iter.get_column_data(synccolumnconfig.columnindex[i]));
                row.add(iter);
                data.add(row);
              }
            }
          }
          iter = iter.next;
        }
      }
      Vector columnames = new Vector();
      for (int i = 0; i < synccolumnconfig.num_columns; ++i)
        columnames.add(synccolumnconfig.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
          return false;
        }
      };
      return result;
    }

    public boolean recreatingsync = false;
    Semaphore RecreateSyncColumnsSem = new Semaphore(1);
    public void RecreateSyncColumns() {
      try {
      RecreateSyncColumnsSem.acquire();
      recreatingsync = true;

      for (int i = 0; i < synccolumnconfig.num_columns - 1; ++i) synccolumnconfig.setDefaultWidth(synccolumnconfig.getIndex(synctable.getColumnModel().getColumn(i).getHeaderValue().toString()), synctable.getColumnModel().getColumn(i).getWidth());

      boolean ascending = synctable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetSearchColumnCount();
      SearchPane.instance.RecreateUsingSearch(synccolumnconfig, synctable, columncount);
      SkinManager.instance.getTableConfig(synctable).setConfig(synctable);
      synctable.setModel(makeSyncModel());
      if (synccolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = synctable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      synctable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(synctable));
      for (int i = 0; i < columncount; ++i) {
        synctable.getColumnModel().getColumn(i).setPreferredWidth(synccolumnconfig.getPreferredWidth(i));
        synctable.getColumnModel().getColumn(i).setWidth(synccolumnconfig.getPreferredWidth(i));
      }
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setResizable(false);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setMinWidth(0);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setMaxWidth(0);
      synctable.getColumnModel().getColumn(synccolumnconfig.num_columns).setWidth(0);

      synctable.sort(synccolumnconfig);
      
      recreatingsync = false;
      } catch (Exception e) { log.error("RecreateSyncColumns(): error", e); }
      RecreateSyncColumnsSem.release();
    }

    private void setupActionListeners() {
       syncsongsbutton.addActionListener(this);
       synccancelbutton.addActionListener(this);
       syncremovebutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == syncsongsbutton) {
         Vector synclist = new Vector();
         for (int i = 0; i < synctable.getRowCount(); ++i) {
           SongLinkedList song = (SongLinkedList)synctable.getModel().getValueAt(i, synccolumnconfig.num_columns);
           if (song != RapidEvolutionUI.instance.currentsong) synclist.add(song);
         }
         SongDB.instance.SyncSongs(RapidEvolutionUI.instance.currentsong, synclist);
      } else if (ae.getSource() == syncremovebutton) {
            DefaultSortTableModel dstm = (DefaultSortTableModel)synctable.getModel();
            while (synctable.getSelectedRow() >= 0) {
              SongLinkedList song = (SongLinkedList)synctable.getModel().getValueAt(synctable.getSelectedRow(), synccolumnconfig.num_columns);
              for (int i = 0; i < addedsyncsongs.size(); ++i) {
                if (song == addedsyncsongs.get(i)) addedsyncsongs.remove(i--);
              }
              dstm.removeRow(synctable.getSelectedRow());
            }
            if (synctable.getRowCount() == 0) syncsongsbutton.setEnabled(false);
      } else if (ae.getSource() == synccancelbutton) {
         setVisible(false);
      }
    }

    public boolean PreDisplay() {
      if (synctable.getRowCount() > 0) syncsongsbutton.setEnabled(true);
      else syncsongsbutton.setEnabled(false);
      return true;
    }

}

package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import rapid_evolution.RapidEvolution;
import java.awt.Component;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import rapid_evolution.SongLinkedList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import java.util.Vector;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.SongDB;
import rapid_evolution.ui.REDialog;
import javax.swing.JViewport;

import org.apache.log4j.Logger;

import rapid_evolution.ui.RETable;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class ExcludeUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(ExcludeUI.class);
    
    public ExcludeUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static ExcludeUI instance = null;
    public JButton excludeokbutton = new REButton();
    public JButton removeexcludebutton = new REButton();

    public ColumnConfig excludecolumnconfig = new ColumnConfig();
    public JSortTable excludetable = new RETable() {
      public Component prepareRenderer(TableCellRenderer r, int row, int col) {
        Component c = null;
        try {
          c = c = super.prepareRenderer(r, row, col);
          SongLinkedList song = (SongLinkedList) excludetable.getModel().
              getValueAt(row, excludecolumnconfig.num_columns);
          if (song == null)
            return c;
          int[] rowsel = excludetable.getSelectedRows();
          for (int i = 0; i < rowsel.length; ++i) {
            if (rowsel[i] == row)
              return c;
          }
          c.setBackground(song.getSearchColor());
        } catch (Exception e) { }
        return c;
      }
    };

    private void setupDialog() {
            // view exclude dialog
            removeexcludebutton.setEnabled(false);
            excludetable.getSelectionModel().addListSelectionListener(new ExcludeSelectionListener());
    }

    public class ExcludeSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
          if ((RapidEvolutionUI.instance.currentsong != null) && !recreatingexclude) {
            if (excludetable.getSelectedRowCount() >= 1) removeexcludebutton.setEnabled(true);
            else removeexcludebutton.setEnabled(false);
          }
        }
    }

    public boolean recreatingexclude = false;
    Semaphore RecreateExcludeColumnsSem = new Semaphore(1);
    public void RecreateExcludeColumns() {
      try {
      RecreateExcludeColumnsSem.acquire();
      recreatingexclude = true;

      for (int i = 0; i < excludecolumnconfig.num_columns - 1; ++i) excludecolumnconfig.setDefaultWidth(excludecolumnconfig.getIndex(excludetable.getColumnModel().getColumn(i).getHeaderValue().toString()), excludetable.getColumnModel().getColumn(i).getWidth());

      boolean ascending = excludetable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetSearchColumnCount();
      SearchPane.instance.RecreateUsingSearch(excludecolumnconfig, excludetable, columncount);
      SkinManager.instance.getTableConfig(excludetable).setConfig(excludetable);
      excludetable.setModel(makeExcludeModel());
      if (excludecolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = excludetable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      excludetable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(excludetable));
      for (int i = 0; i < columncount; ++i) {
        excludetable.getColumnModel().getColumn(i).setPreferredWidth(excludecolumnconfig.getPreferredWidth(i));
        excludetable.getColumnModel().getColumn(i).setWidth(excludecolumnconfig.getPreferredWidth(i));
      }
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setResizable(false);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setMinWidth(0);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setMaxWidth(0);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setWidth(0);

      excludetable.sort(excludecolumnconfig);
      
      recreatingexclude = false;
      } catch (Exception e) { log.error("RecreateExcludeColumns(): error", e); }
      RecreateExcludeColumnsSem.release();
    }

    public SortTableModel makeExcludeModel() {
      Vector data = new Vector();
      if (RapidEvolutionUI.instance.currentsong != null) {
        for (int j = 0; j < RapidEvolutionUI.instance.currentsong.getNumExcludeSongs(); ++j) {
          SongLinkedList soiter = SongDB.instance.NewGetSongPtr(RapidEvolutionUI.instance.currentsong.exclude_songs[j]);
          Vector row = new Vector();
          for (int i = 0; i < excludecolumnconfig.num_columns; ++i)
            row.add(soiter.get_column_data(excludecolumnconfig.columnindex[i]));
          row.add(soiter);
          data.add(row);
        }
      }
      Vector columnames = new Vector();
      for (int i = 0; i < excludecolumnconfig.num_columns; ++i)
        columnames.add(excludecolumnconfig.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
          return false;
        }
      };
      return result;
    }

    Semaphore RedrawExcludeSem = new Semaphore(1);
    public void Redrawexclude() {
      try {
      RedrawExcludeSem.acquire();

      int columncount = excludecolumnconfig.num_columns;

      for (int i = 0; i < excludecolumnconfig.num_columns; ++i) {
        excludecolumnconfig.columntitles[i] = excludetable.getColumnModel().getColumn(i).getHeaderValue().toString();
        excludecolumnconfig.setIndex(i);
        excludecolumnconfig.setPreferredWidth(i, excludetable.getColumnModel().
            getColumn(i).getWidth());
      }
      SkinManager.instance.getTableConfig(excludetable).setConfig(excludetable);
      excludetable.setModel(makeExcludeModel());
      if (excludecolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = excludetable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      excludetable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(excludetable));
      for (int i = 0; i < columncount; ++i) {
        excludetable.getColumnModel().getColumn(i).setPreferredWidth(excludecolumnconfig.getPreferredWidth(i));
        excludetable.getColumnModel().getColumn(i).setWidth(excludecolumnconfig.getPreferredWidth(i));
      }
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setResizable(false);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setMinWidth(0);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setMaxWidth(0);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setWidth(0);
      excludetable.sort(excludecolumnconfig);
      } catch (Exception e) { log.error("Redrawexclude(): error", e); }
      RedrawExcludeSem.release();
    }

    public void CreateExcludeColumns() {
      if (excludecolumnconfig.num_columns <= 0) { RecreateExcludeColumns(); return; }
      SkinManager.instance.getTableConfig(excludetable).setConfig(excludetable);
      excludetable.setModel(makeExcludeModel());
      if (excludecolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = excludetable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      excludetable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(excludetable));
      for (int i = 0; i < excludecolumnconfig.num_columns; ++i) {
        excludetable.getColumnModel().getColumn(i).setPreferredWidth(excludecolumnconfig.getPreferredWidth(i));
        excludetable.getColumnModel().getColumn(i).setWidth(excludecolumnconfig.getPreferredWidth(i));
      }
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setResizable(false);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setMinWidth(0);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setMaxWidth(0);
      excludetable.getColumnModel().getColumn(excludecolumnconfig.num_columns).setWidth(0);
      excludetable.sort(excludecolumnconfig);
    }

    private void setupActionListeners() {
        removeexcludebutton.addActionListener(this);
        excludeokbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == excludeokbutton) {
        setVisible(false);
      } else if (ae.getSource() == removeexcludebutton) {
        for (int i = 0; i < excludetable.getRowCount(); ++i)
          if (excludetable.isRowSelected(i)) {
            SongLinkedList removesong = (SongLinkedList)excludetable.getModel().getValueAt(i, excludecolumnconfig.num_columns);
            RapidEvolutionUI.instance.RemoveExclude(removesong);
          }
        RecreateExcludeColumns();
        removeexcludebutton.setEnabled(false);
      }
    }
}


package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import rapid_evolution.RapidEvolution;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.util.Vector;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.REDialog;
import rapid_evolution.net.GetSongInfoThread;
import rapid_evolution.net.MixshareClient;
import javax.swing.JViewport;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import java.awt.Component;
import rapid_evolution.ui.RETable;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class EditMatchQueryUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(EditMatchQueryUI.class);
    
    public EditMatchQueryUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static EditMatchQueryUI instance = null;

    public JButton matchsongsokbutton2 = new REButton();
    public JButton matchsongscancelbutton2 = new REButton();
    public ColumnConfig matchcolumnconfig2 = new ColumnConfig();
    public JSortTable matchtable2 = new RETable();

    private void setupDialog() {
      //    match dialog
      matchtable2.addMouseListener(new MatchServerMouse2());
    }

    class MatchServerMouse2 extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          RapidEvolutionUI.instance.MatchSelected(true);
        }
      }
    }

    public Vector addedmatchsongs2 = null;
    Semaphore RedrawMatchSem2 = new Semaphore(1);
    public void Redrawmatch2() {
      try {
      RedrawMatchSem2.acquire();

      int columncount = matchcolumnconfig2.num_columns;

      for (int i = 0; i < matchcolumnconfig2.num_columns; ++i) {
        matchcolumnconfig2.columntitles[i] = matchtable2.getColumnName(i);
        matchcolumnconfig2.setIndex(i);
        matchcolumnconfig2.setPreferredWidth(i, matchtable2.getColumnModel().
            getColumn(i).getWidth());
      }
      SkinManager.instance.getTableConfig(matchtable2).setConfig(matchtable2);
      matchtable2.setModel(makeMatchModel2());
      if (matchcolumnconfig2.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = matchtable2.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      matchtable2.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(matchtable2));
      for (int i = 0; i < columncount; ++i) {
        matchtable2.getColumnModel().getColumn(i).setPreferredWidth(matchcolumnconfig2.getPreferredWidth(i));
        matchtable2.getColumnModel().getColumn(i).setWidth(matchcolumnconfig2.getPreferredWidth(i));
      }
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setResizable(false);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setMinWidth(0);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setMaxWidth(0);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setWidth(0);
      matchtable2.sort(matchcolumnconfig2);
      } catch (Exception e) { log.error("Redrawmatch2(): error", e); }
      RedrawMatchSem2.release();
    }

    public Vector matchesvector2 = new Vector();
    SortTableModel makeMatchModel2() {
      Vector data = new Vector();

       for (int j = 0; j < matchesvector2.size(); ++j) {
         SongLinkedList iter = (SongLinkedList)matchesvector2.get(j);
          Vector row = new Vector();
          for (int i = 0; i < matchcolumnconfig2.num_columns; ++i)
            row.add(iter.get_column_data(matchcolumnconfig2.columnindex[i]));
          row.add(iter);
          data.add(row);
        }
      Vector columnames = new Vector();
      for (int i = 0; i < matchcolumnconfig2.num_columns; ++i)
        columnames.add(matchcolumnconfig2.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
          return false;
        }
      };
      return result;
      
    }

    boolean recreatingmatch2 = false;
    Semaphore RecreateMatchColumnsSem2 = new Semaphore(1);
    public void RecreateMatchColumns2() {
      try {
      RecreateMatchColumnsSem2.acquire();
      recreatingmatch2 = true;

      for (int i = 0; i < matchcolumnconfig2.num_columns - 1; ++i) matchcolumnconfig2.setDefaultWidth(matchcolumnconfig2.getIndex(matchtable2.getColumnName(i)), matchtable2.getColumnModel().getColumn(i).getWidth());

      boolean ascending = matchtable2.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetSearchColumnCount();
      SearchPane.instance.RecreateUsingSearch(matchcolumnconfig2, matchtable2, columncount);
      SkinManager.instance.getTableConfig(matchtable2).setConfig(matchtable2);
      matchtable2.setModel(makeMatchModel2());
      if (matchcolumnconfig2.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = matchtable2.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      matchtable2.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(matchtable2));
      for (int i = 0; i < columncount; ++i) {
        matchtable2.getColumnModel().getColumn(i).setPreferredWidth(matchcolumnconfig2.getPreferredWidth(i));
        matchtable2.getColumnModel().getColumn(i).setWidth(matchcolumnconfig2.getPreferredWidth(i));
      }
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setResizable(false);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setMinWidth(0);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setMaxWidth(0);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setWidth(0);
      
      matchtable2.sort(matchcolumnconfig2);

      recreatingmatch2 = false;
      } catch (Exception e) { log.error("RecreateMatchColumns2(): error", e); }
      RecreateMatchColumnsSem2.release();
    }

    public void CreateMatchColumns2() {
      if (matchcolumnconfig2.num_columns <= 0) { RecreateMatchColumns2(); return; }
      SkinManager.instance.getTableConfig(matchtable2).setConfig(matchtable2);
      matchtable2.setModel(makeMatchModel2());
      if (matchcolumnconfig2.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = matchtable2.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      matchtable2.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(matchtable2));
      for (int i = 0; i < matchcolumnconfig2.num_columns; ++i) {
        matchtable2.getColumnModel().getColumn(i).setPreferredWidth(matchcolumnconfig2.getPreferredWidth(i));
        matchtable2.getColumnModel().getColumn(i).setWidth(matchcolumnconfig2.getPreferredWidth(i));
      }
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setResizable(false);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setMinWidth(0);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setMaxWidth(0);
      matchtable2.getColumnModel().getColumn(matchcolumnconfig2.num_columns).setWidth(0);
      matchtable2.sort(matchcolumnconfig2);
    }

    private void setupActionListeners() {
        matchsongsokbutton2.addActionListener(this);
        matchsongscancelbutton2.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == matchsongsokbutton2) {
         RapidEvolutionUI.instance.MatchSelected(true);
      } else if (ae.getSource() == matchsongscancelbutton2) {
         setVisible(false);
      }
    }

    public boolean PreDisplay(Object source) {
      if (display_parameter == null) {
        if (MixshareClient.instance.isConnected())
            new GetSongInfoThread(EditSongUI.instance.getEditedSong()).start();
          return false;
      }
      matchesvector2 = (Vector)display_parameter;
      display_parameter = null;
      Redrawmatch2();
      return true;
    }
}

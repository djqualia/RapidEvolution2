package rapid_evolution.ui;

import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JButton;
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
import rapid_evolution.ui.styles.StylesUI;
import rapid_evolution.ui.REDialog;
import rapid_evolution.net.GetSongInfoThread;
import rapid_evolution.net.MixshareClient;
import javax.swing.JViewport;
import java.awt.Component;
import rapid_evolution.ui.RETable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class AddMatchQueryUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(AddMatchQueryUI.class);
    
    public AddMatchQueryUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static AddMatchQueryUI instance = null;

    public JButton matchsongsokbutton = new REButton();
    public JButton matchsongscancelbutton = new REButton();
    public ColumnConfig matchcolumnconfig = new ColumnConfig();
    public JSortTable matchtable = new RETable();

    private void setupDialog() {
        //    match dialog
      matchtable.addMouseListener(new MatchServerMouse());
    }

    Vector addedmatchsongs = null;
    Semaphore RedrawMatchSem = new Semaphore(1);
    public void Redrawmatch() {
      try {
      RedrawMatchSem.acquire();

      int columncount = matchcolumnconfig.num_columns;

      for (int i = 0; i < matchcolumnconfig.num_columns; ++i) {
        matchcolumnconfig.columntitles[i] = matchtable.getColumnName(i);
        matchcolumnconfig.setIndex(i);
        matchcolumnconfig.setPreferredWidth(i, matchtable.getColumnModel().
            getColumn(i).getWidth());
      }
      SkinManager.instance.getTableConfig(matchtable).setConfig(matchtable);
      matchtable.setModel(makeMatchModel());
      if (matchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = matchtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      matchtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(matchtable));
      for (int i = 0; i < columncount; ++i) {
        matchtable.getColumnModel().getColumn(i).setPreferredWidth(matchcolumnconfig.getPreferredWidth(i));
        matchtable.getColumnModel().getColumn(i).setWidth(matchcolumnconfig.getPreferredWidth(i));
      }
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setResizable(false);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setMinWidth(0);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setMaxWidth(0);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setWidth(0);
      matchtable.sort(matchcolumnconfig);
      } catch (Exception e) { log.error("Redrawmatch(): error", e); }
      RedrawMatchSem.release();
    }

    public boolean recreatingmatch = false;
    Semaphore RecreateMatchColumnsSem = new Semaphore(1);
    public void RecreateMatchColumns() {
      try {
      RecreateMatchColumnsSem.acquire();
      recreatingmatch = true;

      for (int i = 0; i < matchcolumnconfig.num_columns - 1; ++i) matchcolumnconfig.setDefaultWidth(matchcolumnconfig.getIndex(matchtable.getColumnName(i)), matchtable.getColumnModel().getColumn(i).getWidth());

      boolean ascending = matchtable.isSortedColumnAscending();
      int columncount = OptionsUI.instance.GetSearchColumnCount();
      SearchPane.instance.RecreateUsingSearch(matchcolumnconfig, matchtable, columncount);
      SkinManager.instance.getTableConfig(matchtable).setConfig(matchtable);
      matchtable.setModel(makeMatchModel());
      if (matchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = matchtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      matchtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(matchtable));
      for (int i = 0; i < columncount; ++i) {
        matchtable.getColumnModel().getColumn(i).setPreferredWidth(matchcolumnconfig.getPreferredWidth(i));
        matchtable.getColumnModel().getColumn(i).setWidth(matchcolumnconfig.getPreferredWidth(i));
      }
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setResizable(false);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setMinWidth(0);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setMaxWidth(0);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setWidth(0);

      matchtable.sort(matchcolumnconfig);
      
      recreatingmatch = false;
      } catch (Exception e) { log.error("RecreateMatchColumns(): error", e); }
      RecreateMatchColumnsSem.release();
    }

    public void CreateMatchColumns() {
      if (matchcolumnconfig.num_columns <= 0) { RecreateMatchColumns(); return; }
      SkinManager.instance.getTableConfig(matchtable).setConfig(matchtable);
      matchtable.setModel(makeMatchModel());
      if (matchcolumnconfig.containsColumnType(ColumnConfig.COLUMN_RATING)) {
	      TableColumn column = matchtable.getColumn(SkinManager.instance.getMessageText("column_title_rating"));
	      if (column != null) {
	          column.setCellRenderer(RapidEvolutionUI.instance.getRatingCellRenderer());
	      }
      }
      matchtable.getColumnModel().addColumnModelListener(new NewTableColumnModelListener(matchtable));
      for (int i = 0; i < matchcolumnconfig.num_columns; ++i) {
        matchtable.getColumnModel().getColumn(i).setPreferredWidth(matchcolumnconfig.getPreferredWidth(i));
        matchtable.getColumnModel().getColumn(i).setWidth(matchcolumnconfig.getPreferredWidth(i));
      }
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setResizable(false);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setMinWidth(0);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setMaxWidth(0);
      matchtable.getColumnModel().getColumn(matchcolumnconfig.num_columns).setWidth(0);
      matchtable.sort(matchcolumnconfig);
    }

    public Vector matchesvector = new Vector();
    public SortTableModel makeMatchModel() {
      Vector data = new Vector();

       for (int j = 0; j < matchesvector.size(); ++j) {
         SongLinkedList iter = (SongLinkedList)matchesvector.get(j);
          Vector row = new Vector();
          for (int i = 0; i < matchcolumnconfig.num_columns; ++i)
            row.add(iter.get_column_data(matchcolumnconfig.columnindex[i]));
          row.add(iter);
          data.add(row);
        }
      Vector columnames = new Vector();
      for (int i = 0; i < matchcolumnconfig.num_columns; ++i)
        columnames.add(matchcolumnconfig.columntitles[i]);
      columnames.add("id code");
      DefaultSortTableModel result = new DefaultSortTableModel(data, columnames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
          return false;
        }
      };
      return result;
    }

    class MatchServerMouse extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          RapidEvolutionUI.instance.MatchSelected(false);
        }
      }
    }

    private void setupActionListeners() {
        matchsongsokbutton.addActionListener(this);
        matchsongscancelbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == matchsongsokbutton) {
        RapidEvolutionUI.instance.MatchSelected(false);
      } else if (ae.getSource() == matchsongscancelbutton) {
         Hide();
      }
    }

    public boolean PreDisplay() {
      if (display_parameter == null) {
        if (MixshareClient.instance.isConnected()) {
          new GetSongInfoThread().start();
        }
        return false;
      } else {
        matchesvector = (Vector)display_parameter;
        display_parameter = null;
        Redrawmatch();
        return true;
      }
    }
}

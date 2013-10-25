package rapid_evolution.ui.main;

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.FileSelection;
import rapid_evolution.ui.NewTableColumnModelListener;
import rapid_evolution.ui.RETable;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.dnd.REDrop;
import rapid_evolution.ui.dnd.RESearchSectionDropListener;

public class DroppableSearchTable extends RETable implements DragSourceListener, DragGestureListener {

    private static Logger log = Logger.getLogger(DroppableSearchTable.class);
    
    DragSource dragSource = DragSource.getDefaultDragSource();
    public DroppableSearchTable() {
      dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
      setModel(SearchPane.instance.makeModel(SearchPane.instance.searchcolumnconfig.num_columns));
      getColumnModel().addColumnModelListener(new NewTableColumnModelListener(this));
      this.sort(SearchPane.instance.searchcolumnconfig);
	  new REDrop(this, new RESearchSectionDropListener()); 
    }
    public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
    public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
    public void dragExit(DragSourceEvent DragSourceEvent){}
    public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
    public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}
    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {        
      if (getSelectedRowCount() < 1) return;
      try {
          FileSelection transferable = new FileSelection(SearchSelectionListener.getSelectedFiles(), SearchSelectionListener.getSelectedSongs());
          RapidEvolutionUI.instance.lastdragsourceindex = 1;              
          dragGestureEvent.startDrag(
                DragSource.DefaultCopyDrop,
                transferable,
                this);
      } catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }

    }
    
    
    public Component prepareRenderer(TableCellRenderer r, int row, int col) {
      Component c = null;
      try {
        c = super.prepareRenderer(r, row, col);
        if ((row < SearchPane.instance.searchtable.getRowCount()) && (c != null)) {
            SongLinkedList song = (SongLinkedList) SearchPane.instance.searchtable.getModel().
            	getValueAt(row, SearchPane.instance.searchcolumnconfig.num_columns);
            if (song == null)
                return c;
            if (SearchPane.instance.searchtable.isRowSelected(row)) 
                return c;
            c.setBackground(song.getSearchColor());
        }
      } catch (Exception e) {
          log.error("prepareRenderer(): error Exception", e);
      }
      return c;
    }
    
}

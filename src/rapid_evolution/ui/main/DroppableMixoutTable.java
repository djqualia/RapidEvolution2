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
import rapid_evolution.ui.dnd.REMixoutSectionDropListener;


public class DroppableMixoutTable extends RETable
    implements DragSourceListener, DragGestureListener
{
  
    private static Logger log = Logger.getLogger(DroppableMixoutTable.class);
    
    DragSource dragSource = DragSource.getDefaultDragSource();

    public DroppableMixoutTable()
    {
      dragSource.createDefaultDragGestureRecognizer(
          this, DnDConstants.ACTION_COPY, this);
      setModel(MixoutPane.instance.makeMixModel());
      getColumnModel().addColumnModelListener(new NewTableColumnModelListener(this));
	  new REDrop(this, new REMixoutSectionDropListener());       
    }

    public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
    public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
    public void dragExit(DragSourceEvent DragSourceEvent){}
    public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
    public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}

    public void dragGestureRecognized(DragGestureEvent dragGestureEvent)
    {
      if (getSelectedRowCount() != 1) return;
      int selection = getSelectedRow();
      if ((selection < 0) || (selection >= getRowCount())) return;
      try {
        SongLinkedList song = ((SongLinkedList)getValueAt(getSelectedRow(), MixoutPane.instance.mixoutcolumnconfig.num_columns));
        
          FileSelection transferable = new FileSelection(((song.getFileName() != null) && !song.getFileName().equals("")) ? song.getFile() : null, song);
          RapidEvolutionUI.instance.lastdragsourceindex = 2;
          dragGestureEvent.startDrag(
                DragSource.DefaultCopyDrop,
                transferable,
                this);
        
      } catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
    }
    
    public Component prepareRenderer(TableCellRenderer r, int row, int col) {
      Component c = null;
      try {
        c = super.prepareRenderer(r, row, col);;
        SongLinkedList song = (SongLinkedList) MixoutPane.instance.mixouttable.getModel().
            getValueAt(row, MixoutPane.instance.mixoutcolumnconfig.num_columns);
        if (song != null) {
          if (row == MixoutPane.instance.mixouttable.getSelectedRow())
            return c;
          c.setBackground(song.getMixColor());
          return c;
        }
      } catch (Exception e) { }
      return c;
    }
}

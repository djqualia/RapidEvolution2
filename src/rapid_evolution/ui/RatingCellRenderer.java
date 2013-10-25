package rapid_evolution.ui;

import java.awt.Component;
import java.awt.Color;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import rapid_evolution.SongLinkedList;
import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;

import org.apache.log4j.Logger;

public class RatingCellRenderer extends JToolBar implements TableCellRenderer, TableCellEditor {

    private static Logger log = Logger.getLogger(RatingCellRenderer.class);
    
    public RatingCellRenderer() {
    }
    
    
    public Component getTableCellRendererComponent(JTable table, 
            Object value, boolean isSelected, boolean hasFocus, int row,  int column) {

        //log.trace("getTableCellRendererComponent(): row=" + row + ", column=" + column);
        
        ColumnConfig config = RapidEvolutionUI.instance.getTableConfig(table);
        
        if (config != null) {
	        SongLinkedList song = (SongLinkedList)table.getValueAt(row, config.num_columns);
	        if (song != null) {
		        RatingToolBar toolbar = song.getRatingBar();
	            if (log.isTraceEnabled()) log.trace("getTableCellRendererComponent(): toolbar=" + toolbar);
		        if (toolbar != null) {
			        if (isSelected) {            
			            toolbar.getComponent().setForeground(table.getSelectionForeground());
			            toolbar.getComponent().setBackground(table.getSelectionBackground());    
			          } else {
			              toolbar.getComponent().setForeground(table.getForeground());
			              toolbar.getComponent().setBackground(table.getBackground());
			          } 	
			        return toolbar.getComponent();
		        }
	        }
        }
        return null;
        
  }     
    
    public Component getTableCellEditorComponent(JTable table, Object value, 
                        boolean isSelected, int row, int column) {
        ColumnConfig config = RapidEvolutionUI.instance.getTableConfig(table);
        if (config != null) {
	        SongLinkedList song = (SongLinkedList)table.getValueAt(row, config.num_columns);
	        if (song != null) {
	            if (log.isTraceEnabled()) log.trace("getTableCellEditorComponent(): song=" + song + ", editing_toolbar=" + editing_toolbar);
		        RatingToolBar toolbar = song.getRatingBar();
		        if (toolbar != null) {
		            if (editing_toolbar == null) editing_toolbar = new RatingToolBarFlyWeight((char)0);
		            editing_toolbar.setBackground(table.getSelectionBackground());    
		            editing_toolbar.setRating(toolbar.getComponent().getRating());
		            RatingToolBarFlyWeight.setEditedSong(song);
			        return editing_toolbar;
		        }
	        }
        }
        return null;

    } 
    
    private static RatingToolBarFlyWeight editing_toolbar = null;
    
    public boolean isCellEditable(EventObject event) {
        return true;
      }     

    public boolean shouldSelectCell(EventObject event) {
        return true;
      }     
    
    public void addCellEditorListener(CellEditorListener listener) {
        listenerList.add(CellEditorListener.class, listener);
      } 

      public void removeCellEditorListener(CellEditorListener listener) {
        listenerList.remove(CellEditorListener.class, listener);
      } 

      protected void fireEditingStopped() {
        CellEditorListener listener;
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
          if (listeners[i] == CellEditorListener.class) {
            listener = (CellEditorListener) listeners[i + 1];
            listener.editingStopped(changeEvent);
          } 
        } 
      } 

      protected void fireEditingCanceled() {
        CellEditorListener listener;
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
          if (listeners[i] == CellEditorListener.class) {
            listener = (CellEditorListener) listeners[i + 1];
            listener.editingCanceled(changeEvent);
          } 
        } 
      } 

      public void cancelCellEditing() {
        fireEditingCanceled();
      } 

      public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
      }     
      
      protected EventListenerList listenerList = new EventListenerList();
      protected ChangeEvent changeEvent = new ChangeEvent(this);
      
      public Object getCellEditorValue() {
          return null;
        }       
      
}

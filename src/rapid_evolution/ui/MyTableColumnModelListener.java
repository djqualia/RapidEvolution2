package rapid_evolution.ui;

import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

import org.apache.log4j.Logger;

public class MyTableColumnModelListener implements TableColumnModelListener {

    private static Logger log = Logger.getLogger(MyTableColumnModelListener.class);
    
    private ColumnConfig config;
    
    public MyTableColumnModelListener(ColumnConfig config) {
        this.config = config;
    }
    
    public void columnAdded(TableColumnModelEvent e) {
        
    }
    
    public void 	columnMarginChanged(ChangeEvent e) {
        
    }
    
    public void 	columnMoved(TableColumnModelEvent e) {
        try {
	        int from_index = e.getFromIndex();
	        int to_index = e.getToIndex();
	        if (from_index != to_index) {
	            if (log.isTraceEnabled()) log.trace("columnMoved(): from_index=" + from_index + ", to_index=" + to_index);
	            if (to_index < from_index) {
	                if ((config.primary_sort_column >= to_index) && (config.primary_sort_column < from_index))
	                    config.primary_sort_column++;
	                else if (config.primary_sort_column == from_index)
	                    config.primary_sort_column = to_index;
	                if ((config.secondary_sort_column >= to_index) && (config.secondary_sort_column < from_index))
	                    config.secondary_sort_column++;
	                else if (config.secondary_sort_column == from_index)
	                    config.secondary_sort_column = to_index;
	                if ((config.tertiary_sort_column >= to_index) && (config.tertiary_sort_column < from_index))
	                    config.tertiary_sort_column++;
	                else if (config.tertiary_sort_column == from_index)
	                    config.tertiary_sort_column = to_index;                
	            } else {
	                // to_index > from_index
	                if ((config.primary_sort_column > from_index) && (config.primary_sort_column <= to_index))
	                    config.primary_sort_column--;
	                else if (config.primary_sort_column == from_index)
	                    config.primary_sort_column = to_index;
	                if ((config.secondary_sort_column > from_index) && (config.secondary_sort_column <= to_index))
	                    config.secondary_sort_column--;
	                else if (config.secondary_sort_column == from_index)
	                    config.secondary_sort_column = to_index;
	                if ((config.tertiary_sort_column > from_index) && (config.tertiary_sort_column <= to_index))
	                    config.tertiary_sort_column--;
	                else if (config.tertiary_sort_column == from_index)
	                    config.tertiary_sort_column = to_index;                                
	            }
	        }        
        } catch (Exception e2) {
            log.error("columnMoved(): error Exception", e2);
        }
    }
    
    public void 	columnRemoved(TableColumnModelEvent e) {
    }
    public void 	columnSelectionChanged(ListSelectionEvent e) {
    
    }
}

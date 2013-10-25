package rapid_evolution.ui.main;

import rapid_evolution.Artist;
import rapid_evolution.SongLinkedList;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import javax.swing.JList;

import org.apache.log4j.Logger;

import java.awt.dnd.DragGestureEvent;
import rapid_evolution.ui.FileSelection;
import java.awt.dnd.DnDConstants;
import rapid_evolution.RapidEvolution;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

// really a draggable song field (currentsong/previous song)?

public class MyStyleList extends JList implements DropTargetListener {
    
    private static Logger log = Logger.getLogger(MyStyleList.class);
    
    public MyStyleList() {
      super();
    }
    DropTarget dropTarget = new DropTarget (this, this);
    public void dragEnter (DropTargetDragEvent dropTargetDragEvent) { dropTargetDragEvent.acceptDrag (DnDConstants.ACTION_COPY); }
    public void dragExit (DropTargetEvent dropTargetEvent) {}
    public void dragOver (DropTargetDragEvent dropTargetDragEvent) {}
    public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent){}
    public void drop (DropTargetDropEvent dropTargetDropEvent) {
       if (((RapidEvolutionUI.instance.lastdragsourceindex != 0)) || !dropTargetDropEvent.isLocalTransfer()) {

           Transferable t = dropTargetDropEvent.getTransferable();
           boolean success = false;
           try {
               if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                   Object x = t.getTransferData(DataFlavor.stringFlavor);
                   if (x instanceof Vector) {
                       Vector re2selection = (Vector)x;
                       SongLinkedList newcurrentsong = (SongLinkedList)re2selection.get(0);                
                       RapidEvolutionUI.instance.change_current_song(newcurrentsong, 0.0f, false, false);
                       success = true;
                   }
               }
           } catch (Exception e) { }

           if (!success) {
               try {
            dropTargetDropEvent.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
            java.util.List fileList = (java.util.List)t.getTransferData(DataFlavor.javaFileListFlavor);
            Iterator iterator = fileList.iterator();
            int size = 0;
            while (iterator.hasNext())
            {
              size++;
              File file = (File)iterator.next();
            }
            if (size == 1) {
              iterator = fileList.iterator();
              while (iterator.hasNext())
              {
                File file = (File)iterator.next();
                SongLinkedList iter = SongDB.instance.SongLL;
                boolean notdone = true;
                while (notdone && (iter != null)) {
                  if (iter.getFileName().equals(file.getAbsolutePath())){ RapidEvolutionUI.instance.change_current_song(iter, 0.0f, true, false); notdone = false; }
                  iter = iter.next;
                }
              }
            }
          } catch (Exception e2) { log.error("drop(): error", e2); }
        }
      }
      RapidEvolutionUI.instance.lastdragsourceindex = -1;
    }

}

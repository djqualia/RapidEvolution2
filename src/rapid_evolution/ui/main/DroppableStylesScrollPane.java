package rapid_evolution.ui.main;

import rapid_evolution.SongLinkedList;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.Component;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetEvent;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import java.awt.dnd.DropTarget;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDragEvent;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.*;

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

public class DroppableStylesScrollPane extends JScrollPane implements DropTargetListener {

    private static Logger log = Logger.getLogger(DroppableStylesScrollPane.class);

    DropTarget dropTarget = new DropTarget (this, this);
  private RETree tree;
  public DroppableStylesScrollPane(Component view) {
      super(view);
      tree = (RETree)view; }
  public void dragEnter (DropTargetDragEvent dropTargetDragEvent) { dropTargetDragEvent.acceptDrag (DnDConstants.ACTION_COPY); }
  public void dragExit (DropTargetEvent dropTargetEvent) {}
  public void dragOver (DropTargetDragEvent dropTargetDragEvent) {}
  public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent){}
  public void drop (DropTargetDropEvent dropTargetDropEvent) {
      log.debug("drop(): dropping on scroll pane");
      tree.drop(dropTargetDropEvent, true);
    }
}

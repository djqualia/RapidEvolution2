package rapid_evolution.ui.main;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import org.apache.log4j.Logger;

import rapid_evolution.ui.FileSelection;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.dnd.RECurrentSongDropListener;
import rapid_evolution.ui.dnd.REDrop;

import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;


// really a draggable song field (currentsong/previous song)?

public class DragTextField extends RETextField implements DragSourceListener, DragGestureListener {

    private static Logger log = Logger.getLogger(DragTextField.class);

    public DragTextField(int n) {
      super(n);
      dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
	  new REDrop(this, new RECurrentSongDropListener());       
    }
    DragSource dragSource = DragSource.getDefaultDragSource();
    public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
    public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
    public void dragExit(DragSourceEvent DragSourceEvent){}
    public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
    public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}
    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
      if ((RapidEvolutionUI.instance.currentsong == null) || (RapidEvolutionUI.instance.currentsong.getFileName().equals(""))) return;
      FileSelection transferable = new FileSelection(RapidEvolutionUI.instance.currentsong.getFile(), RapidEvolutionUI.instance.currentsong);
      try { RapidEvolutionUI.instance.lastdragsourceindex = 0; dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
      catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
    }

}

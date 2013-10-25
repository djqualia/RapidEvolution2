package rapid_evolution.ui.main;

import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import java.awt.dnd.DragGestureEvent;
import rapid_evolution.ui.FileSelection;
import rapid_evolution.RapidEvolution;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSourceListener;
import java.io.File;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.FileUtil;

import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;


public class DragTextFieldPrev extends RETextField implements DragSourceListener, DragGestureListener {

    private static Logger log = Logger.getLogger(DragTextFieldPrev.class);

    public DragTextFieldPrev(int n) {
      super(n);
      dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }
    DragSource dragSource = DragSource.getDefaultDragSource();
    public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
    public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
    public void dragExit(DragSourceEvent DragSourceEvent){}
    public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
    public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}
    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
      if ((RapidEvolutionUI.instance.previoussong == null) || (RapidEvolutionUI.instance.previoussong.getFileName().equals(""))) return;
      FileSelection transferable = new FileSelection(RapidEvolutionUI.instance.previoussong.getFile(), RapidEvolutionUI.instance.previoussong);
      try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); RapidEvolutionUI.instance.lastdragsourceindex = 0; }
      catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
    }
}

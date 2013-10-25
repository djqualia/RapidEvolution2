package rapid_evolution.ui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.main.DroppableStylesScrollPane;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.styles.AddProcessSetStyles;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.StylesUI;

import com.ibm.iwt.IOptionPane;

/**
 * This should be "REStylesTree" as its not a generalized tree...
 */
public class RETree extends JTree implements Autoscroll, TreeSelectionListener, DragGestureListener, DropTargetListener, DragSourceListener  {

    private static Logger log = Logger.getLogger(RETree.class);
        
    public RETree(DefaultMutableTreeNode node) {
        super(node);
        cellrenderer = new RETreeCellRenderer(this);
        this.setOpaque(true);
        init();                               
    }
    
    public RETree() {
        super();
        cellrenderer = new RETreeCellRenderer(this);
        this.setOpaque(true);
        init();                       
    }
    
    private RETreeCellRenderer cellrenderer = null;
    private DroppableStylesScrollPane scrollpane;
    
    private REBasicTreeUI ui;
    
    public Insets getAutoscrollInsets() {
        return new Insets(100,100,100,100);
     }
    
    public void clearSelection() { 
        super.clearSelection();
        for (int r = this.getRowCount() - 1; r >= 0; --r) {
            this.collapseRow(r);
        }
    }
      
     public void autoscroll(Point p) {
        Point topLeft = new Point(p.x - 10,p.y - 10);
        scrollRectToVisible(new Rectangle(topLeft,new Dimension(20,20)));
     }
     
     protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g;
         if (rapid_evolution.RapidEvolution.aaEnabled)
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         super.paintComponent(g);
     }     
               
    private void init() {
        //setModel(new DefaultTreeModel());
//        ui = new REBasicTreeUI();
//        setUI(ui);
        setEditable(false);
        setRootVisible(false);
        setToggleClickCount(-1);
        setVisibleRowCount(-1);        
        setExpandsSelectedPaths(true);
               
        setShowsRootHandles(true);
        setSelectionModel(new ToggleTreeSelectionModel(this));           
        setCellRenderer(cellrenderer);    
        setDragEnabled(false);        
        
        scrollpane = new DroppableStylesScrollPane(this);
        SkinManager.addScroll(this, scrollpane);        
        

        // NON OF THIS WORKS!@?!
//        this.addMouseListener(new RowSelectionListener(this));
//        Container container = this.getParent();
//        container.addMouseListener(new RowSelectionListener(this));        
//        if (container instanceof JPanel)
//            log.trace("init(): tree parent is JPanel");
//        container.getParent().addMouseListener(new RowSelectionListener(this));        
        
        addTreeSelectionListener(this);
        addTreeWillExpandListener(new TreeWillExpandListener() 
                {
                  public void treeWillExpand(TreeExpansionEvent evt)
                         throws ExpandVetoException {
                      if (!getSelectionModel().isPathSelected(evt.getPath())) {
//                          throw new ExpandVetoException(evt); 
                      }
                  } 

                  public void treeWillCollapse(TreeExpansionEvent evt)
                         throws ExpandVetoException {
                      if (getSelectionModel().isPathSelected(evt.getPath())) {
//                          throw new ExpandVetoException(evt); 
                      }
                     DefaultMutableTreeNode thisNode = 
                       (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
                     boolean isAChildSelected = checkIsChildSelected(thisNode);
                      if (isAChildSelected) throw new ExpandVetoException(evt); 
                  } 
                }) ;

		/* ********************** CHANGED ********************** */
        dragSource = DragSource.getDefaultDragSource() ;
    		/* ****************** END OF CHANGE ******************** */
        
        DragGestureRecognizer dgr = 
          dragSource.createDefaultDragGestureRecognizer(
            this,                             //DragSource
            DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
            this                              //DragGestureListener
          );

        /* Eliminates right mouse clicks as valid actions - useful especially
         * if you implement a JPopupMenu for the JTree
         */
        dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);

        /* First argument:  Component to associate the target with
         * Second argument: DropTargetListener 
        */
        DropTarget dropTarget = new DropTarget(this, this);
        
        setDropTarget(dropTarget);

    }
    
    private int indent_size = 20;
    public void setTotalIndent(int indent) {
        indent_size = indent;
    }
    protected void processMouseEvent(MouseEvent e) {
        //log.trace("processMouseEvent(): x: " + e.getX() + ", y: " + e.getY());
        if (SwingUtilities.isRightMouseButton(e)) {
            if (e.isPopupTrigger()) {
                if (SongDB.instance.num_styles >= 1) {
                    if ( EditStyleUI.instance.isVisible())  EditStyleUI.instance.setVisible(false);
                    else {
                        try {
                            Point pt = e.getPoint();
                            DefaultTreeModel dlm = (DefaultTreeModel) getModel();              
                            int row = getRowForLocation(e.getX(), e.getY());
                            if (row >= 0) {
                                TreePath path = getPathForRow(row);
                                MyMutableStyleNode node = (MyMutableStyleNode)path.getLastPathComponent();
                                EditStyleUI.instance.display_parameter =  node.getStyle();
                                EditStyleUI.instance.Display();
                            }
                        } catch (Exception e2) {
                            log.error("processMouseEvent(): error", e2);
                        }
                    }      
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
/*            TreeSelectionListener[] listeners = getTreeSelectionListeners();
            for (int i = 0; i < listeners.length; ++i) {
                listeners[i].
            }
            */
            int row = getRowForLocation(e.getX(), e.getY());
            if (row >= 0) {
                TreePath path = getPathForRow(row);

//                boolean is_expand = ui.isLocationInExpandControl(path, e.getX() - getX(), e.getY() - getY());
                                
                ToggleTreeSelectionModel selmodel = (ToggleTreeSelectionModel)getSelectionModel();
                selmodel.setAllowsSelection(true);
                if (this == StylesPane.instance.styletree) {
	                boolean require = false;
	                boolean exclude = false;
	                if (e.isControlDown()) {
	                    if (OptionsUI.instance.style_require_shortcut_combobox.getSelectedItem().equals("CTRL")) {
	                        require = true;
	                    } else if (OptionsUI.instance.style_exclude_shortcut_combobox.getSelectedItem().equals("CTRL")) {
	                        exclude = true;
	                    }
	                }
	                if (e.isAltDown()) {
	                    if (OptionsUI.instance.style_require_shortcut_combobox.getSelectedItem().equals("ALT")) {
	                        require = true;
	                    } else if (OptionsUI.instance.style_exclude_shortcut_combobox.getSelectedItem().equals("ALT")) {
	                        exclude = true;
	                    }                    
	                }
	                if (e.isShiftDown()) {
	                    if (OptionsUI.instance.style_require_shortcut_combobox.getSelectedItem().equals("SHIFT")) {
	                        require = true;
	                    } else if (OptionsUI.instance.style_exclude_shortcut_combobox.getSelectedItem().equals("SHIFT")) {
	                        exclude = true;
	                    }                    
	                }
	                MyMutableStyleNode node = (MyMutableStyleNode)path.getLastPathComponent();
	                StyleLinkedList style = node.getStyle();
	                if (!require && !exclude) {
	                    style.setRequired(false);
	                    style.setExcluded(false);
	                    super.processMouseEvent(e);
	                } else {
	                    if (require) {
	                        style.setRequired(!style.isRequired());
	                        if (style.isRequired())
	                            style.setExcluded(false);
	                    }
	                    if (exclude) {
	                        style.setExcluded(!style.isExcluded());
	                        if (style.isExcluded())
	                            style.setRequired(false);
	                    }
	                    selmodel.removeSelectionPath(path);
	                    this.repaint();
	                    boolean required_or_excluded_exists = false;
	                    StyleLinkedList siter = SongDB.instance.masterstylelist;
	                    while ((siter != null) && !required_or_excluded_exists) {
	                        if (siter.isRequired() || siter.isExcluded()) required_or_excluded_exists = true;
	                        siter = siter.next;
	                    }
	                    if (required_or_excluded_exists) StylesPane.instance.clearstylesbutton.setEnabled(true);
	                    else {
	                        if (this.getSelectionCount() == 0) StylesPane.instance.clearstylesbutton.setEnabled(false);
	                        else StylesPane.instance.clearstylesbutton.setEnabled(true);
	                    }
	                }
                } else {
                    super.processMouseEvent(e);
                }
                selmodel.setAllowsSelection(false);             
            } else {
                row = getRowForLocation(e.getX() + indent_size, e.getY());
                if (row >= 0) {
                    // expansion click
                    log.trace("processMouseEvent(): expansion click detected");
                    TreePath path = getPathForRow(row);
                    if (isExpanded(path)) {
                        collapsePath(path);
                    } else {
                        if (e.isControlDown()) {
                            // recursive expand
                            expandPath(path);
                            MyMutableStyleNode node = (MyMutableStyleNode)path.getLastPathComponent();
                            recursiveExpand(node);
                        } else {
                            expandPath(path);                        
                        }
                    }
                }
            }
        }
    }    
    
    private void recursiveExpand(MyMutableStyleNode node) {
        for (int i = 0; i < node.getChildCount(); ++i) {
            MyMutableStyleNode child = (MyMutableStyleNode)node.getChildAt(i);
            expandPath(new TreePath(child.getPath()));
            recursiveExpand(child);
        }
    }
    
    private boolean checkIsChildSelected(DefaultMutableTreeNode node) {
        for (int i = 0; i < node.getChildCount(); ++i) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
            TreePath tp = new TreePath(child.getPath());
            if (getSelectionModel().isPathSelected(tp)) return true;
            if ((child.getChildCount() > 0) && checkIsChildSelected(child)) return true;
        }
        return false;
    }
    
    public String getToolTipText (MouseEvent e)
    {
        return super.getToolTipText(e);

    }
    
    /** Variables needed for DnD */
    private DragSource dragSource = null;
    private DragSourceContext dragSourceContext = null;
    
    /** Stores the selected node info */
    protected TreePath SelectedTreePath = null;
    protected MyMutableStyleNode SelectedNode = null;    

    ///////////////////////// Interface stuff ////////////////////
    /** DragGestureListener interface method */
    public void dragGestureRecognized(DragGestureEvent e) {

        //Get the selected node
        
      MyMutableStyleNode dragNode = (MyMutableStyleNode)getPathForLocation((int)e.getDragOrigin().getX(), (int)e.getDragOrigin().getY()).getLastPathComponent();
//      MyMutableStyleNode dragNode = (MyMutableStyleNode)SelectedNode;
      	log.trace("dragGestureRecognized(): " + dragNode);
      if (dragNode != null) {

        //Get the Transferable Object
        Transferable transferable = (Transferable) dragNode.getStyle();
  			/* ********************** CHANGED ********************** */

        //Select the appropriate cursor;
        Cursor cursor = DragSource.DefaultCopyDrop;
        int action = e.getDragAction();
        if (action == DnDConstants.ACTION_MOVE) 
          cursor = DragSource.DefaultMoveDrop;
          
          
        //In fact the cursor is set to NoDrop because once an action is rejected
        // by a dropTarget, the dragSourceListener are no more invoked.
        // Setting the cursor to no drop by default is so more logical, because 
        // when the drop is accepted by a component, then the cursor is changed by the
        // dropActionChanged of the default DragSource.
  			/* ****************** END OF CHANGE ******************** */
     
        SelectedNode = dragNode;
        SelectedTreePath = new TreePath(dragNode.getPath());
        
        //begin the drag
        dragSource.startDrag(e, cursor, transferable, this);
      }
    }

    /** DragSourceListener interface method */
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dragEnter(DragSourceDragEvent dsde) {
  		/* ********************** CHANGED ********************** */
  		/* ****************** END OF CHANGE ******************** */
    }

    public void dragOver(DragSourceDragEvent dsde) {

        
    }
    
    

    /** DragSourceListener interface method */
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dragExit(DragSourceEvent dsde) {
    }

    
    public void drop(DropTargetDropEvent e, boolean drop_on_root) {
      	log.trace("drop(): dropping, root?: " + drop_on_root);
        try {
          Transferable tr = e.getTransferable();

          if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
              log.trace("drop(): detected song(s)");
              Point loc = e.getLocation();
              TreePath destinationPath = getPathForLocation(loc.x, loc.y);
              DefaultMutableTreeNode newParent = null;
              if (destinationPath != null) newParent = (DefaultMutableTreeNode) destinationPath.getLastPathComponent();
              MyMutableStyleNode myNewParent = null;
              if (newParent instanceof MyMutableStyleNode) {
                  myNewParent = (MyMutableStyleNode)newParent;
                  StyleLinkedList style = myNewParent.getStyle();
                  log.trace("drop(): dropping on style: " + style);
                                    
                  Object x = tr.getTransferData(DataFlavor.stringFlavor);
                  if (x instanceof Vector) {

                      String text = SkinManager.instance.getDialogMessageText("add_songs_to_style");
                      text = text.replaceAll("%style%", style.getName());
                      int n = IOptionPane.showConfirmDialog(
                              SkinManager.instance.getFrame("main_frame"),
                              text,
                              SkinManager.instance.getDialogMessageTitle("add_songs_to_style"),
                              IOptionPane.YES_NO_OPTION);
                          if (n != 0) {
                            return;
                          }
                      
                      Vector re2selection = (Vector)x;
                      SongLinkedList[] songs = new SongLinkedList[re2selection.size()];
                      for (int i = 0; i < re2selection.size(); ++i) {
                          SongLinkedList song = SongDB.instance.NewGetSongPtr(((Long)re2selection.get(i)).longValue());                
                          log.trace("drop(): dropping song: " + song);
                          songs[i] = song;
                      }
                      javax.swing.SwingUtilities.invokeLater(new AddProcessSetStyles(new StyleLinkedList[] { style }, songs));

                  }
                  
              }
              return;
              
              
          }          
          
          //flavor not supported, reject drop
          if (!tr.isDataFlavorSupported( StyleLinkedList.INFO_FLAVOR)) e.rejectDrop();

        	log.trace("drop(): flavor supported");

          Object item = tr.getTransferData( StyleLinkedList.INFO_FLAVOR );        
          
          StyleLinkedList childInfo = StyleLinkedList.getStyle(Integer.parseInt((String)item));

        	log.trace("drop(): dropping song: " + childInfo + "selectedtreepath: " + SelectedTreePath);
          
          //get new parent node
          Point loc = e.getLocation();
          TreePath destinationPath = getPathForLocation(loc.x, loc.y);

          
          final String msg = testDropTarget(destinationPath, SelectedTreePath);
          if (msg != null) {
            e.rejectDrop();

            log.debug("drop(): " + msg);

            return;
          }


          DefaultMutableTreeNode newParent = null;
          if (destinationPath != null) newParent = (DefaultMutableTreeNode) destinationPath.getLastPathComponent();

      	log.trace("drop(): new parent: " + newParent);
                           
          //get old parent node
          DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) SelectedNode.getParent();

      	log.trace("drop(): old parent: " + oldParent);

          int action = e.getDropAction();
          boolean copyAction = (action == DnDConstants.ACTION_COPY);
          
          MyMutableStyleNode myNewParent = null;
          if (newParent instanceof MyMutableStyleNode) {
              myNewParent = (MyMutableStyleNode)newParent;
              if (myNewParent.getStyle().equals(childInfo)) {
                  e.rejectDrop();
                  return;
              }
          }
          
          //make new child node
          MyMutableStyleNode newChild = new MyMutableStyleNode(childInfo, StylesUI.getHierarchyName(childInfo, myNewParent));
          
          try {
              String text = SkinManager.instance.getDialogMessageText("drop_style_node");
              String title = SkinManager.instance.getDialogMessageTitle("drop_style_node");  
              if (copyAction) title = title.replaceAll("%action%", "copy");
              else title = title.replaceAll("%action%", "move");
              
              StyleLinkedList parentStyle = null;
              if (newParent instanceof MyMutableStyleNode) {
                  MyMutableStyleNode myparent = (MyMutableStyleNode)newParent;
                  parentStyle = myparent.getStyle();
              }
              StyleLinkedList childStyle = null;
              if (newChild instanceof MyMutableStyleNode) {
                  MyMutableStyleNode mychild = (MyMutableStyleNode)newChild;
                  childStyle = mychild.getStyle();
              }
              StyleLinkedList OldParentStyle = null;
              if (oldParent instanceof MyMutableStyleNode) {
                  MyMutableStyleNode myparent = (MyMutableStyleNode)oldParent;
                  OldParentStyle = myparent.getStyle();
              }
              
              if ((OldParentStyle != null) && (parentStyle != null)) {
                  if (OldParentStyle.equals(parentStyle)) {
                      e.rejectDrop();
                      return;
                  }
              }
              
              if (OldParentStyle == parentStyle) {
                  e.rejectDrop();
                  return;
              }
              
              if (parentStyle != null) text = text.replaceAll("%parent_style%", parentStyle.getName());
              else text = text.replaceAll("%parent_style%", "root");
              
              text = text.replaceAll("%child_style%", childStyle.getName());
              
              int n = IOptionPane.showConfirmDialog(
                      SkinManager.instance.getFrame("main_frame"),
                      text,
                      title,
                      IOptionPane.YES_NO_OPTION);
              
              if (n == 0) {

                  try { 

                      StylesUI.updateHierarchy(OldParentStyle, parentStyle, childStyle, copyAction);
                                      
                      if (copyAction) e.acceptDrop (DnDConstants.ACTION_COPY);
                      else e.acceptDrop (DnDConstants.ACTION_MOVE);

                      if (newParent != null) {
                          TreePath parentPath = new TreePath(newParent.getPath());
                          expandPath(parentPath);
                      }
                  }
                  catch (java.lang.IllegalStateException ils) {
                      e.rejectDrop();
                  }
                  
                  e.getDropTargetContext().dropComplete(true);
                  
              }                       
              
          } catch (Exception e2) {
              log.error("drop(): error", e2);
          }
        }
        catch (IOException io) {
            log.error("drop(): error", io);
            e.rejectDrop(); }
        catch (UnsupportedFlavorException ufe) {
            log.error("drop(): error", ufe);
            e.rejectDrop();}        
    }
    
    /** DropTargetListener interface method - What we do when drag is released */
    public void drop(DropTargetDropEvent e) {
        drop(e, false);

    } //end of method


    /** DropTaregetListener interface method */
    public void dragEnter(DropTargetDragEvent e) {
    }

    /** DropTaregetListener interface method */
    public void dragExit(DropTargetEvent e) { 
    }

    
    private class dragTimedListener implements ActionListener {
        public dragTimedListener(TreePath path, RETree tree) {
            this.path = path;
            this.tree = tree;
        }
        TreePath path;
        RETree tree;
        public void actionPerformed( ActionEvent ae )
        {

            Point loc = latest_dtde.getLocation();
            // expansion click
            TreePath samePath = getPathForLocation(loc.x, loc.y);
            
            if (samePath != null) {

                if( path.equals( samePath ) )
                {
                  if( !isExpanded( path ) )
                  {
                    tree.expandPath( path );
                  }
                }                
            }

            mouseOnPath = null;
   
        }        
    }
    
    /** DragSourceListener interface method */
    private TreePath mouseOnPath = null;
    private DropTargetDragEvent latest_dtde;

    
    /** DropTaregetListener interface method */
    public void dragOver(DropTargetDragEvent e) {

        DataFlavor[] flavors = e.getCurrentDataFlavors();
        boolean style = false;
        log.trace("dragOver(): # current flavors=" + flavors.length);
        for (int f = 0; f < flavors.length; ++f) {
            log.trace("dragOver(): flavor " + f + "=" + flavors[f]);
            // don't reject songs            
            if (flavors[f].getClass().equals(StyleLinkedList.class)) style = true;
        }
        if (!style) return;
        
        /* ********************** CHANGED ********************** */
      //set cursor location. Needed in setCursor method
      Point cursorLocationBis = e.getLocation();
          TreePath destinationPath = 
        getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

//          if (SelectedTreePath == null) 
//              SelectedTreePath = new TreePath(SelectedNode.getPath());
          
      // if destination path is okay accept drop...
      String dropTest = testDropTarget(destinationPath, SelectedTreePath);
      if (dropTest == null){
      	e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE ) ;
      }
      // ...otherwise reject drop
      else {
      	log.trace("dragOver(): dropTest failed: " + dropTest);
      	e.rejectDrag() ;
      }
  		/* ****************** END OF CHANGE ******************** */
      
		/* ********************** CHANGED ********************** */
		/* ****************** END OF CHANGE ******************** */
      
      latest_dtde = e;
      if( mouseOnPath != null )
      {
        return;
      }
   
      if (destinationPath != null) {
          mouseOnPath = destinationPath;
          Timer t2 = new Timer( 1000, new dragTimedListener(destinationPath, this));
          t2.setRepeats( false );
          t2.start();
      }      
    }

    /** DropTaregetListener interface method */
    public void dropActionChanged(DropTargetDragEvent e) {
    }


    /** TreeSelectionListener - sets selected node */
    public void valueChanged(TreeSelectionEvent evt) {
      SelectedTreePath = evt.getNewLeadSelectionPath();
      if (SelectedTreePath == null) SelectedTreePath = evt.getPath();
      if (SelectedTreePath == null) {
//        SelectedNode = null;
        return;
      }
      SelectedNode = 
        (MyMutableStyleNode)SelectedTreePath.getLastPathComponent();
    }

    /** Convenience method to test whether drop location is valid
    @param destination The destination path 
    @param dropper The path for the node to be dropped
    @return null if no problems, otherwise an explanation
    */
    private String testDropTarget(TreePath destination, TreePath dropper) {
      //Typical Tests for dropping
        
        if (destination == null) {
            if (dropper != null) {
//                MyMutableStyleNode child_node = (MyMutableStyleNode)dropper.getLastPathComponent();        
//                if (!child_node.getStyle().isRootStyle()) return null;
                return null;
            }
            return "Can't drag nothing...";
        }
        MyMutableStyleNode parent_node = (MyMutableStyleNode)destination.getLastPathComponent();
        if (dropper == null) return "Dropper is null";
        MyMutableStyleNode child_node = (MyMutableStyleNode)dropper.getLastPathComponent();        
    	log.trace("testDropTarget(): destination=" + parent_node + ", dropper=" + child_node);

        //Test 1.
      boolean destinationPathIsNull = destination == null;
      if (destinationPathIsNull) 
        return "Invalid drop location.";

      //Test 2.
      MyMutableStyleNode node = parent_node;
      if ( !node.getAllowsChildren() )
        return "This node does not allow children";

      if (isChildOfSelf(parent_node.getStyle(), child_node.getStyle()) || 
              isParentOfSelf(child_node.getStyle(), parent_node.getStyle())) {
      	log.trace("testDropTarget(): recursive loop detected");
          return "Causes recursive loop";          
      }
      
      if (parent_node.equals(child_node))
        return "Destination cannot be same as source";

      if (dropper != null) {
          //Test 3.
          if ( dropper.isDescendant(destination)) 
              return "Destination node cannot be a descendant.";

          //Test 4.
          if ( dropper.getParentPath().equals(destination)) 
              return "Destination node cannot be a parent.";
      }
      
      return null;
    }
    
    // MISTAKE, CHECK VIA STYLES NOT NODES
    
    boolean isChildOfSelf(StyleLinkedList parent_style, StyleLinkedList new_child_style) {
        if (new_child_style.equals(parent_style)) return true;
        // search for parent_style under new_child_style
        StyleLinkedList[] child_styles = new_child_style.getChildStyles();
        for (int i = 0; i < child_styles.length; ++i) {
            if (child_styles[i].equals(parent_style)) return true;
            if (isChildOfSelf(parent_style, child_styles[i])) return true;
        }
        return false;
    }
    boolean isParentOfSelf(StyleLinkedList child_style, StyleLinkedList new_parent_style) {
        if (child_style.equals(new_parent_style)) return true;
        // search for child_style over new_parent_style
        StyleLinkedList[] parent_styles = new_parent_style.getParentStyles();
        for (int i = 0; i < parent_styles.length; ++i) {
            if (parent_styles[i].equals(child_style)) return true;
            if (isParentOfSelf(child_style, parent_styles[i])) return true;
        }
        return false;
    }
    
    public TreePath[] getSelectionPaths() {
        TreePath[] paths = super.getSelectionPaths();
        if (paths != null) return paths;
        return new TreePath[0];
    }
    
    public void addSelectionPath(TreePath path) {
        SwingUtilities.invokeLater(new addSelectionClass(path));
    }
    
    protected class addSelectionClass extends Thread {
        private TreePath path;
        public addSelectionClass(TreePath path) {
            this.path = path;
        }
        public void run() {
            ToggleTreeSelectionModel selmodel = (ToggleTreeSelectionModel)getSelectionModel();
            selmodel.setAllowsSelection(true);
            selmodel.addSelectionPath(path);
            selmodel.setAllowsSelection(false);
            //super.addSelectionPath(path);            
        }
    }

    public boolean isADirectSelectedStyle(StyleLinkedList style) {
        Map map = StylesUI.getMap(this);
        Vector nodes = (Vector)map.get(style);
        if (nodes != null) {
            for (int j = 0; j < nodes.size(); ++j) {
                MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(j);
                if (getSelectionModel().isPathSelected(new TreePath(node.getPath())))
                    return true;
            }
        }        
        return false;
    }

    public boolean isALogicalSelectedStyle(StyleLinkedList style) {
        Map map = StylesUI.getMap(this);
        Vector nodes = (Vector)map.get(style);
        if (nodes != null) {
            for (int j = 0; j < nodes.size(); ++j) {
                MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(j);
                if (getSelectionModel().isPathSelected(new TreePath(node.getPath())))
                    return true;
            }
        }        
        return false;
    }
    
    // collapses nodes where no children are selected
    public void minimizeTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); ++i) {
            MyMutableStyleNode node = (MyMutableStyleNode)root.getChildAt(i);
            if (!hasSelectedChildren(node)) collapsePath(new TreePath(node.getPath()));
        }
    }
    
    private boolean hasSelectedChildren(MyMutableStyleNode root) {
        boolean hasSelected = false;
        for (int i = 0; i < root.getChildCount(); ++i) {
            MyMutableStyleNode node = (MyMutableStyleNode)root.getChildAt(i);
            TreePath path = new TreePath(node.getPath());
            if (!hasSelectedChildren(node)) collapsePath(path);
            else hasSelected = true;
            if (!hasSelected && getSelectionModel().isPathSelected(path)) hasSelected = true;
        }
        return hasSelected;
    }

  	public String getRootSelection() {
  	    HashMap style_map = new HashMap();
  	    StyleLinkedList siter = SongDB.instance.masterstylelist;
  	    while (siter != null) {
  	        if (this.isALogicalSelectedStyle(siter)) {
                addStyleCount(style_map, siter);
                siter.countParentStyles(style_map);
  	        }
  	        siter = siter.next;
  	    }
  	    int max_count = 0;
  	  StyleLinkedList max_style = null;
  	    Iterator iter = style_map.entrySet().iterator();
  	    while (iter.hasNext()) {
  	        Entry entry = (Entry)iter.next();
  	      StyleLinkedList style = (StyleLinkedList)entry.getKey();
  	      if (!style.equals(StyleLinkedList.root_style)) {
  	      int count = ((Integer)entry.getValue()).intValue();
  	        if ((max_style == null) || (count > max_count)) {
  	            max_style = style;
  	            max_count = count;
  	        } else if (count == max_count) {
  	            if (style.isRootStyle()) {
  	                max_style = style;
  	                max_count = count;
  	            }
  	        }
  	      }
  	    }
  	    if (max_style != null)
  	        return max_style.getName();
  	    return null;
  	}    
    
  	private void addStyleCount(HashMap style_map, StyleLinkedList style) {
  	    Integer count = (Integer)style_map.get(style);
  	    if (count != null) {
  	        count = new Integer(count.intValue() + 1);
  	        style_map.put(style, count);
  	    } else {
  	        style_map.put(style, new Integer(1));
  	    }
  	}
  	
    private class RowSelectionListener extends MouseAdapter {
        private RETree tree;
        public RowSelectionListener(RETree tree) {
            this.tree = tree;
        }
        public void mousePressed(MouseEvent e) {
            if (log.isTraceEnabled())
                log.trace("mousePressed(): e=" + e);
            if (!tree.isEnabled())
                return;
            TreePath closestPath = tree.getClosestPathForLocation(e.getX(), e
                    .getY());
            if (closestPath == null)
                return;
            if (log.isTraceEnabled())
                log.trace("mousePressed(): closestPath=" + closestPath);
        }
    }
    
}

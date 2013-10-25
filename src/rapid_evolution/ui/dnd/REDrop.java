package rapid_evolution.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.ui.RapidEvolutionUI;

/**
 * This class makes it easy to drag and drop files from the operating
 * system to a Java program. Any <tt>java.awt.Component</tt> can be
 * dropped onto, but only <tt>javax.swing.JComponent</tt>s will indicate
 * the drop event with a changed border.
 * <p/>
 * To use this class, construct a new <tt>FileDrop</tt> by passing
 * it the target component and a <tt>Listener</tt> to receive notification
 * when file(s) have been dropped. Here is an example:
 * <p/>
 * <code><pre>
 *      JPanel myPanel = new JPanel();
 *      new REDrop( myPanel, new FileDrop.Listener()
 *      {   public void filesDropped( java.io.File[] files )
 *          {   
 *              // handle file drop
 *              ...
 *          }   // end filesDropped
 *      }); // end FileDrop.Listener
 * </pre></code>
 * <p/>
 * You can specify the border that will appear when files are being dragged by
 * calling the constructor with a <tt>javax.swing.border.Border</tt>. Only
 * <tt>JComponent</tt>s will show any indication with a border.
 * <p/>
 * You can turn on some debugging features by passing a <tt>PrintStream</tt>
 * object (such as <tt>System.out</tt>) into the full constructor. A <tt>null</tt>
 * value will result in no extra debugging information being output.
 * <p/>
 *
 * <p>I'm releasing this code into the Public Domain. Enjoy.
 * </p>
 * <p><em>Original author: Robert Harder, rharder@usa.net</em></p>
 * <p>2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.</p>
 *
 * @author  Robert Harder
 * @author  rharder@users.sf.net
 * @version 1.0.1
 */
public class REDrop
{
    static private Logger log = Logger.getLogger(REDrop.class);
    
    static public int DROP_TYPE_INVALID = 0;
    static public int DROP_TYPE_SONG = 1;
    static public int DROP_TYPE_FILE = 2;
	
    static private boolean useBorder = false;
    private transient javax.swing.border.Border normalBorder;
    private transient java.awt.dnd.DropTargetListener dropListener;    
    
    private static Boolean supportsDnD; // indicates if JVM supports DnD
    
    private static java.awt.Color defaultBorderColor = new java.awt.Color( 0f, 0f, 1f, 0.25f );
    
    public static interface Listener {       
        public abstract void filesDropped(Vector<File> files);
        public abstract void songsDropped(Vector<SongLinkedList> songs);
        
    }
    
    /**
     * Constructs a {@link FileDrop} with a default light-blue border
     * and, if <var>c</var> is a {@link java.awt.Container}, recursively
     * sets all elements contained within as drop targets, though only
     * the top level container will change borders.
     *
     * @param c Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.awt.Component c,
    final Listener listener )
    {   this( null,  // Logging stream
              c,     // Drop target
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), // Drag border
              true, // Recursive
              listener );
    }   // end constructor
    
    
    
    
    /**
     * Constructor with a default border and the option to recursively set drop targets.
     * If your component is a <tt>java.awt.Container</tt>, then each of its children
     * components will also listen for drops, though only the parent will change borders.
     *
     * @param c Component on which files will be dropped.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.awt.Component c,
    final boolean recursive,
    final Listener listener )
    {   this( null,  // Logging stream
              c,     // Drop target
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), // Drag border
              recursive, // Recursive
              listener );
    }   // end constructor
    
    
    /**
     * Constructor with a default border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out PrintStream to record debugging info or null for no debugging.
     * @param out 
     * @param c Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final Listener listener )
    {   this( out,  // Logging stream
              c,    // Drop target
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), 
              false, // Recursive
              listener );
    }   // end constructor
    
        
    
    /**
     * Constructor with a default border, debugging optionally turned on
     * and the option to recursively set drop targets.
     * If your component is a <tt>java.awt.Container</tt>, then each of its children
     * components will also listen for drops, though only the parent will change borders.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out PrintStream to record debugging info or null for no debugging.
     * @param out 
     * @param c Component on which files will be dropped.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final boolean recursive,
    final Listener listener)
    {   this( out,  // Logging stream
              c,    // Drop target
              javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 2, defaultBorderColor ), // Drag border
              recursive, // Recursive
              listener );
    }   // end constructor
    
    
    
    
    /**
     * Constructor with a specified border 
     *
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final Listener listener) 
    {   this(
            null,   // Logging stream
            c,      // Drop target
            dragBorder, // Drag border
            false,  // Recursive
            listener );
    }   // end constructor
    
    
        
    
    /**
     * Constructor with a specified border and the option to recursively set drop targets.
     * If your component is a <tt>java.awt.Container</tt>, then each of its children
     * components will also listen for drops, though only the parent will change borders.
     *
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final boolean recursive,
    final Listener listener) 
    {   this(
            null,
            c,
            dragBorder,
            recursive,
            listener );
    }   // end constructor
    
            
    
    /**
     * Constructor with a specified border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out PrintStream to record debugging info or null for no debugging.
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final Listener listener) 
    {   this(
            out,    // Logging stream
            c,      // Drop target
            dragBorder, // Drag border
            false,  // Recursive
            listener );
    }   // end constructor
    
    
    
    
    
    /**
     * Full constructor with a specified border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out PrintStream to record debugging info or null for no debugging.
     * @param c Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive Recursively set children as drop targets.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    public REDrop(
    final java.io.PrintStream out,
    final java.awt.Component c,
    final javax.swing.border.Border dragBorder,
    final boolean recursive,
    final Listener listener) 
    {   
        
        if( supportsDnD() )
        {   // Make a drop listener
            dropListener = new java.awt.dnd.DropTargetListener()
            {   
            	public void dragEnter( java.awt.dnd.DropTargetDragEvent evt ) {
            		if (log.isTraceEnabled())
            			log.trace("dragEnter(): called");

                    // Is this an acceptable drag event?
                    if( isDragOk( evt ) )
                    {
                        // If it's a Swing component, set its border
                        if (useBorder && (c instanceof javax.swing.JComponent)) {
                        	javax.swing.JComponent jc = (javax.swing.JComponent) c;
                            normalBorder = jc.getBorder();
                    		if (log.isTraceEnabled())
                    			log.trace("dragEnter(): normal border saved.");
                            jc.setBorder( dragBorder );
                    		if (log.isTraceEnabled())
                    			log.trace("dragEnter(): drag border set");
                        }   

                        // Acknowledge that it's okay to enter
                        //evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                        evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY );
                		if (log.isTraceEnabled())
                			log.trace("dragEnter(): event accepted");
                    }   // end if: drag ok
                    else 
                    {   // Reject the drag event
                        evt.rejectDrag();
                		if (log.isTraceEnabled())
                			log.trace("dragEnter(): event rejected");
                    }   // end else: drag not ok
                }   // end dragEnter

                public void dragOver( java.awt.dnd.DropTargetDragEvent evt ) 
                {   // This is called continually as long as the mouse is
                    // over the drag target.
                }   // end dragOver

                public void drop( java.awt.dnd.DropTargetDropEvent evt ) {   
            		if (log.isTraceEnabled())
            			log.trace("drop(): called");
                    try {                    		
                        java.awt.datatransfer.Transferable tr = evt.getTransferable();
                        if (tr.isDataFlavorSupported (java.awt.datatransfer.DataFlavor.stringFlavor) && evt.isLocalTransfer()) { // is it a song?
                            evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY );
                    		if (log.isTraceEnabled())
                    			log.trace("drop(): song(s) accepted");                        	
                            try {
                        	  Object x = tr.getTransferData(DataFlavor.stringFlavor);
                        	  if (x instanceof Vector) {
                        		  Vector re2selection = (Vector)x;
                        		  Vector<SongLinkedList> songs = new Vector<SongLinkedList>(re2selection.size());
                        		  for (int i = 0; i < re2selection.size(); ++i) {
                        			  SongLinkedList song = SongDB.instance.NewGetSongPtr(((Long)re2selection.get(i)).longValue());
                        			  songs.add(song);
                        		  }
                                  if( listener != null )
                                      listener.songsDropped(songs);
                        	  }
                            } catch (Exception e) {
                            	log.error("drop(): error", e);
                            }
                            evt.getDropTargetContext().dropComplete(true);
                    		if (log.isTraceEnabled())
                    			log.trace("drop(): drop complete");                        	
                        } else if (tr.isDataFlavorSupported (java.awt.datatransfer.DataFlavor.javaFileListFlavor)) { // is it a file list?
                            evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY );
                    		if (log.isTraceEnabled())
                    			log.trace("drop(): file list accepted");

                            // Get a useful list
                            java.util.List fileList = (java.util.List) 
                                tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                            java.util.Iterator iterator = fileList.iterator();

                            // Convert list to vector
                            Vector<File> files = new Vector<File>(fileList.size());
                            for (int i = 0; i < fileList.size(); ++i) {
                            	files.add((File)fileList.get(i));
                            }

                            // Alert listener to drop.
                            if( listener != null )
                                listener.filesDropped( files );

                            // Mark that drop is completed.
                            evt.getDropTargetContext().dropComplete(true);
                    		if (log.isTraceEnabled())
                    			log.trace("drop(): drop complete");
                        } else { // is it a reader? (linux file list)
                            DataFlavor[] flavors = tr.getTransferDataFlavors();
                            boolean handled = false;
                            for (int zz = 0; zz < flavors.length; zz++) {
                                if (flavors[zz].isRepresentationClassReader()) {
                                    evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);

                            		if (log.isTraceEnabled())
                            			log.trace("drop(): reader accepted");

                                    Reader reader = flavors[zz].getReaderForText(tr);

                                    BufferedReader br = new BufferedReader(reader);
                                    
                                    if(listener != null)
                                        listener.filesDropped(createFileVector(br, out));
                                    
                                    // Mark that drop is completed.
                                    evt.getDropTargetContext().dropComplete(true);
                            		if (log.isTraceEnabled())
                            			log.trace("drop(): drop complete");
                                    handled = true;
                                    break;
                                }
                            }
                            if(!handled){
                        		if (log.isTraceEnabled())
                        			log.trace("drop(): not a file list or reader, song?");                            	
                                evt.rejectDrop();
                            }
                            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                        }   // end else: not a file list
                    } catch ( java.io.IOException io) {
                    	log.error("drop(): IOException", io);
                        evt.rejectDrop();
                    } catch (java.awt.datatransfer.UnsupportedFlavorException ufe) {
                    	log.error("drop(): UnsupportedFlavorException", ufe);
                        evt.rejectDrop();
                    } finally {
                        if (c instanceof javax.swing.JComponent) {
                        	javax.swing.JComponent jc = (javax.swing.JComponent)c;
                        	if (useBorder) {
                        		jc.setBorder(normalBorder);
                        		if (log.isTraceEnabled())
                        			log.trace("drop(): normal border restored");
                        	}
                        }
                        RapidEvolutionUI.instance.lastdragsourceindex = -1;
                    }
                }

                public void dragExit( java.awt.dnd.DropTargetEvent evt ) {
            		if (log.isTraceEnabled())
            			log.trace("dragExit(): called");                            	                	
                    if (c instanceof javax.swing.JComponent) {   
                    	javax.swing.JComponent jc = (javax.swing.JComponent) c;
                    	if (useBorder) {
                    		jc.setBorder( normalBorder );
                    		if (log.isTraceEnabled())
                    			log.trace("dragExit(): normal border restored");
                    	}
                    }
                }

                public void dropActionChanged( java.awt.dnd.DropTargetDragEvent evt ) {
            		if (log.isTraceEnabled())
            			log.trace("dropActionChanged(): called");                            	
                    if (isDragOk(evt)) {
                        evt.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
                		if (log.isTraceEnabled())
                			log.trace("dropActionChanged(): event accepted");                            	
                    } else {
                    	evt.rejectDrag();
                    	if (log.isTraceEnabled())
                    		log.trace("dropActionChanged(): event rejected");                            	
                    }
                }
            };

            // Make the component (and possibly children) drop targets
            makeDropTarget( out, c, recursive );
        }   // end if: supports dnd
        else {
    		if (log.isTraceEnabled())
    			log.trace("REDrop(): drag and drop is not supported with this JVM");                            	
        }   // end else: does not support DnD
    }   // end constructor

    
    private static boolean supportsDnD()
    {   // Static Boolean
        if( supportsDnD == null )
        {   
            boolean support = false;
            try
            {   Class arbitraryDndClass = Class.forName( "java.awt.dnd.DnDConstants" );
                support = true;
            }   // end try
            catch( Exception e )
            {   support = false;
            }   // end catch
            supportsDnD = new Boolean( support );
        }   // end if: first time through
        return supportsDnD.booleanValue();
    }   // end supportsDnD
    
    
     // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
     private static String ZERO_CHAR_STRING = "" + (char)0;
     private static Vector<File> createFileVector(BufferedReader bReader, PrintStream out)
     {
        try { 
            java.util.List list = new java.util.ArrayList();
            java.lang.String line = null;
            while ((line = bReader.readLine()) != null) {
                try {
                    // kde seems to append a 0 char to the end of the reader
                    if(ZERO_CHAR_STRING.equals(line)) continue; 
                    
                    java.io.File file = new java.io.File(new java.net.URI(line));
                    list.add(file);
                } catch (Exception ex) {
                	log.error("createFileArray(): error with " + line, ex);
                }
            }

            Vector<File> files = new Vector<File>(list.size());
            for (int i = 0; i < list.size(); ++i) {
            	files.add((File)list.get(i));
            }
        } catch (IOException ex) {
        	log.error("createFileArray(): IOException", ex);
        }
        return new Vector<File>(0);
     }
     // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
     
    
    private void makeDropTarget( final java.io.PrintStream out, final java.awt.Component c, boolean recursive )
    {
        // Make drop target
        final java.awt.dnd.DropTarget dt = new java.awt.dnd.DropTarget();
        try {   
        	dt.addDropTargetListener( dropListener );
        } catch(java.util.TooManyListenersException e) {
        	log.error("makeDropTarget(): TooManyListenersException: Drop will not work due to previous error. Do you have another listener attached?", e);
        }
        
        // Listen for hierarchy changes and remove the drop target when the parent gets cleared out.
        c.addHierarchyListener( new java.awt.event.HierarchyListener() {
        	public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
        		if (log.isTraceEnabled())
        			log.trace("hierarchyChanged(): called");                            	
                java.awt.Component parent = c.getParent();
                if (parent == null) {   
                	c.setDropTarget( null );
        			if (log.isTraceEnabled())
        				log.trace("hierarchyChanged(): drop target cleared from component");                            	
                } else {
                	new java.awt.dnd.DropTarget(c, dropListener);
                	if (log.isTraceEnabled())
                		log.trace("hierarchyChanged(): drop target added to component");                            	
                }
            }
        });        
        
        if( c.getParent() != null )
            new java.awt.dnd.DropTarget(c, dropListener);
        
        if( recursive && (c instanceof java.awt.Container ) )
        {   
            // Get the container
            java.awt.Container cont = (java.awt.Container) c;
            
            // Get it's components
            java.awt.Component[] comps = cont.getComponents();
            
            // Set it's components as listeners also
            for( int i = 0; i < comps.length; i++ )
                makeDropTarget( out, comps[i], recursive );
        }   // end if: recursively set components as listener
    }   // end dropListener
    
    
    
    /** Determine if the dragged data is a file list. */
    private boolean isDragOk(final java.awt.dnd.DropTargetDragEvent evt) {       	
    	if (log.isTraceEnabled())
    		log.trace("isDragOk(): called");    	
    	int dropType = DROP_TYPE_INVALID;
        if (evt.getTransferable().isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
        	dropType = DROP_TYPE_SONG;
        } else {
        	java.awt.datatransfer.DataFlavor[] flavors = evt.getCurrentDataFlavors();        
        	int i = 0;
        	while((dropType == DROP_TYPE_INVALID) && (i < flavors.length)) {   
        		final DataFlavor curFlavor = flavors[i];
        		if (log.isTraceEnabled())
        			log.trace("isDragOk(): current flavor " + i + "=" + curFlavor.toString());
        		if (curFlavor.equals(java.awt.datatransfer.DataFlavor.javaFileListFlavor) || curFlavor.isRepresentationClassReader()) {
        			dropType = DROP_TYPE_FILE;                
        		}
        		++i;
        	}
        }
        if (log.isTraceEnabled())
        	log.trace("isDragOk(): determined type=" + dropType);        
        return (dropType != DROP_TYPE_INVALID);
    }
        
    /**
     * Removes the drag-and-drop hooks from the component and optionally
     * from the all children. You should call this if you add and remove
     * components after you've set up the drag-and-drop.
     * This will recursively unregister all components contained within
     * <var>c</var> if <var>c</var> is a {@link java.awt.Container}.
     *
     * @param c The component to unregister as a drop target
     * @since 1.0
     */
    public static boolean remove( java.awt.Component c)
    {   return remove( null, c, true );
    }   // end remove
    
    
    
    /**
     * Removes the drag-and-drop hooks from the component and optionally
     * from the all children. You should call this if you add and remove
     * components after you've set up the drag-and-drop.
     *
     * @param out Optional {@link java.io.PrintStream} for logging drag and drop messages
     * @param c The component to unregister
     * @param recursive Recursively unregister components within a container
     * @since 1.0
     */
    public static boolean remove( java.io.PrintStream out, java.awt.Component c, boolean recursive )
    {   // Make sure we support dnd.
        if (supportsDnD()) {
    		if (log.isTraceEnabled())
    			log.trace("remove(): removing drag-and-drop hooks");                            	
            c.setDropTarget( null );
            if( recursive && ( c instanceof java.awt.Container ) )
            {   java.awt.Component[] comps = ((java.awt.Container)c).getComponents();
                for( int i = 0; i < comps.length; i++ )
                    remove( out, comps[i], recursive );
                return true;
            }   // end if: recursive
            else return false;
        }   // end if: supports DnD
        else return false;
    }   // end remove
    
    

    
    
    
/* ********  I N N E R   C L A S S  ******** */    
    
    
    /**
     * This is the event that is passed to the
     * {@link FileDropListener#filesDropped filesDropped(...)} method in
     * your {@link FileDropListener} when files are dropped onto
     * a registered drop target.
     *
     * <p>I'm releasing this code into the Public Domain. Enjoy.</p>
     * 
     * @author  Robert Harder
     * @author  rob@iharder.net
     * @version 1.2
     */
    public static class Event extends java.util.EventObject {

        private java.io.File[] files;

        /**
         * Constructs an {@link Event} with the array
         * of files that were dropped and the
         * {@link FileDrop} that initiated the event.
         *
         * @param files The array of files that were dropped
         * @source The event source
         * @since 1.1
         */
        public Event( java.io.File[] files, Object source ) {
            super( source );
            this.files = files;
        }   // end constructor

        /**
         * Returns an array of files that were dropped on a
         * registered drop target.
         *
         * @return array of files that were dropped
         * @since 1.1
         */
        public java.io.File[] getFiles() {
            return files;
        }   // end getFiles
    
    }   // end inner class Event
    
    
    
/* ********  I N N E R   C L A S S  ******** */
    

    /**
     * At last an easy way to encapsulate your custom objects for dragging and dropping
     * in your Java programs!
     * When you need to create a {@link java.awt.datatransfer.Transferable} object,
     * use this class to wrap your object.
     * For example:
     * <pre><code>
     *      ...
     *      MyCoolClass myObj = new MyCoolClass();
     *      Transferable xfer = new TransferableObject( myObj );
     *      ...
     * </code></pre>
     * Or if you need to know when the data was actually dropped, like when you're
     * moving data out of a list, say, you can use the {@link TransferableObject.Fetcher}
     * inner class to return your object Just in Time.
     * For example:
     * <pre><code>
     *      ...
     *      final MyCoolClass myObj = new MyCoolClass();
     *
     *      TransferableObject.Fetcher fetcher = new TransferableObject.Fetcher()
     *      {   public Object getObject(){ return myObj; }
     *      }; // end fetcher
     *
     *      Transferable xfer = new TransferableObject( fetcher );
     *      ...
     * </code></pre>
     *
     * The {@link java.awt.datatransfer.DataFlavor} associated with 
     * {@link TransferableObject} has the representation class
     * <tt>net.iharder.dnd.TransferableObject.class</tt> and MIME type
     * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
     * This data flavor is accessible via the static
     * {@link #DATA_FLAVOR} property.
     *
     *
     * <p>I'm releasing this code into the Public Domain. Enjoy.</p>
     * 
     * @author  Robert Harder
     * @author  rob@iharder.net
     * @version 1.2
     */
    public static class TransferableObject implements java.awt.datatransfer.Transferable
    {
        /**
         * The MIME type for {@link #DATA_FLAVOR} is 
         * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
         *
         * @since 1.1
         */
        public final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";


        /**
         * The default {@link java.awt.datatransfer.DataFlavor} for
         * {@link TransferableObject} has the representation class
         * <tt>net.iharder.dnd.TransferableObject.class</tt>
         * and the MIME type 
         * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
         *
         * @since 1.1
         */
        public final static java.awt.datatransfer.DataFlavor DATA_FLAVOR = 
            new java.awt.datatransfer.DataFlavor( REDrop.TransferableObject.class, MIME_TYPE );


        private Fetcher fetcher;
        private Object data;

        private java.awt.datatransfer.DataFlavor customFlavor; 



        /**
         * Creates a new {@link TransferableObject} that wraps <var>data</var>.
         * Along with the {@link #DATA_FLAVOR} associated with this class,
         * this creates a custom data flavor with a representation class 
         * determined from <code>data.getClass()</code> and the MIME type
         * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
         *
         * @param data The data to transfer
         * @since 1.1
         */
        public TransferableObject( Object data )
        {   this.data = data;
            this.customFlavor = new java.awt.datatransfer.DataFlavor( data.getClass(), MIME_TYPE );
        }   // end constructor



        /**
         * Creates a new {@link TransferableObject} that will return the
         * object that is returned by <var>fetcher</var>.
         * No custom data flavor is set other than the default
         * {@link #DATA_FLAVOR}.
         *
         * @see Fetcher
         * @param fetcher The {@link Fetcher} that will return the data object
         * @since 1.1
         */
        public TransferableObject( Fetcher fetcher )
        {   this.fetcher = fetcher;
        }   // end constructor



        /**
         * Creates a new {@link TransferableObject} that will return the
         * object that is returned by <var>fetcher</var>.
         * Along with the {@link #DATA_FLAVOR} associated with this class,
         * this creates a custom data flavor with a representation class <var>dataClass</var>
         * and the MIME type
         * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
         *
         * @see Fetcher
         * @param dataClass The {@link java.lang.Class} to use in the custom data flavor
         * @param fetcher The {@link Fetcher} that will return the data object
         * @since 1.1
         */
        public TransferableObject( Class dataClass, Fetcher fetcher )
        {   this.fetcher = fetcher;
            this.customFlavor = new java.awt.datatransfer.DataFlavor( dataClass, MIME_TYPE );
        }   // end constructor

        /**
         * Returns the custom {@link java.awt.datatransfer.DataFlavor} associated
         * with the encapsulated object or <tt>null</tt> if the {@link Fetcher}
         * constructor was used without passing a {@link java.lang.Class}.
         *
         * @return The custom data flavor for the encapsulated object
         * @since 1.1
         */
        public java.awt.datatransfer.DataFlavor getCustomDataFlavor()
        {   return customFlavor;
        }   // end getCustomDataFlavor


    /* ********  T R A N S F E R A B L E   M E T H O D S  ******** */    


        /**
         * Returns a two- or three-element array containing first
         * the custom data flavor, if one was created in the constructors,
         * second the default {@link #DATA_FLAVOR} associated with
         * {@link TransferableObject}, and third the
         * {@link java.awt.datatransfer.DataFlavor.stringFlavor}.
         *
         * @return An array of supported data flavors
         * @since 1.1
         */
        public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() 
        {   
            if( customFlavor != null )
                return new java.awt.datatransfer.DataFlavor[]
                {   customFlavor,
                    DATA_FLAVOR,
                    java.awt.datatransfer.DataFlavor.stringFlavor
                };  // end flavors array
            else
                return new java.awt.datatransfer.DataFlavor[]
                {   DATA_FLAVOR,
                    java.awt.datatransfer.DataFlavor.stringFlavor
                };  // end flavors array
        }   // end getTransferDataFlavors



        /**
         * Returns the data encapsulated in this {@link TransferableObject}.
         * If the {@link Fetcher} constructor was used, then this is when
         * the {@link Fetcher#getObject getObject()} method will be called.
         * If the requested data flavor is not supported, then the
         * {@link Fetcher#getObject getObject()} method will not be called.
         *
         * @param flavor The data flavor for the data to return
         * @return The dropped data
         * @since 1.1
         */
        public Object getTransferData( java.awt.datatransfer.DataFlavor flavor )
        throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException 
        {   
            // Native object
            if( flavor.equals( DATA_FLAVOR ) )
                return fetcher == null ? data : fetcher.getObject();

            // String
            if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) )
                return fetcher == null ? data.toString() : fetcher.getObject().toString();

            // We can't do anything else
            throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
        }   // end getTransferData




        /**
         * Returns <tt>true</tt> if <var>flavor</var> is one of the supported
         * flavors. Flavors are supported using the <code>equals(...)</code> method.
         *
         * @param flavor The data flavor to check
         * @return Whether or not the flavor is supported
         * @since 1.1
         */
        public boolean isDataFlavorSupported( java.awt.datatransfer.DataFlavor flavor ) 
        {
            // Native object
            if( flavor.equals( DATA_FLAVOR ) )
                return true;

            // String
            if( flavor.equals( java.awt.datatransfer.DataFlavor.stringFlavor ) )
                return true;

            // We can't do anything else
            return false;
        }   // end isDataFlavorSupported


    /* ********  I N N E R   I N T E R F A C E   F E T C H E R  ******** */    

        /**
         * Instead of passing your data directly to the {@link TransferableObject}
         * constructor, you may want to know exactly when your data was received
         * in case you need to remove it from its source (or do anyting else to it).
         * When the {@link #getTransferData getTransferData(...)} method is called
         * on the {@link TransferableObject}, the {@link Fetcher}'s
         * {@link #getObject getObject()} method will be called.
         *
         * @author Robert Harder
         * @copyright 2001
         * @version 1.1
         * @since 1.1
         */
        public static interface Fetcher
        {
            /**
             * Return the object being encapsulated in the
             * {@link TransferableObject}.
             *
             * @return The dropped object
             * @since 1.1
             */
            public abstract Object getObject();
        }   // end inner interface Fetcher



    }   // end class TransferableObject

    
    
    
    
}

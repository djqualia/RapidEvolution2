package rapid_evolution.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import java.awt.Point;

import org.apache.log4j.Logger;

import rapid_evolution.comparables.myImageIcon;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

public class REList extends JList {

    private static Logger log = Logger.getLogger(REList.class);

    private REListCellRenderer cellrenderer = null;

    boolean icons = false;

    public REList() {
        init();
    }

    public REList(boolean icons) {
        this.icons = icons;
        init();
    }

    private void init() {
        if (icons)
            cellrenderer = new REListCellRenderer(true);
        else
            cellrenderer = new REListCellRenderer();
        this.setCellRenderer(cellrenderer);
        // ToolTipManager.sharedInstance().registerComponent(this);
        if (icons) {
            registerComponent(this);
        }
    }

    static MouseHandler MOUSE_HANDLER = new MouseHandler();

    // implementation of MouseHandler
    static class MouseHandler extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            if (log.isTraceEnabled())
                log.trace("mouseEntered(): e=" + e);
            JComponent c = (JComponent) e.getComponent();
            Action action = c.getActionMap().get("postTip");
            // it is also possible to use own Timer to display
            // ToolTip with custom delay, but here we just
            // display it immediately
            if (action != null) {
                javax.swing.SwingUtilities.invokeLater(new ToolTipPosterThread(c, action));
            }
        }
    }
    
    static public class ToolTipPosterThread extends Thread {
        private Action action;
        private JComponent c;
        public ToolTipPosterThread(JComponent c, Action action) {
            this.c = c;
            this.action = action;
        }
        public void run() {
            action.actionPerformed(new ActionEvent(c,
                    ActionEvent.ACTION_PERFORMED, "postTip"));            
        }
    }

    public static void registerComponent(JComponent c) {
        if (log.isTraceEnabled())
            log.trace("registerComponent(): registering list for immediate tooltips");
        // ensure InputMap and ActionMap are created
        InputMap imap = c.getInputMap();
        ActionMap amap = c.getActionMap();
        // put dummy KeyStroke into InputMap if is empty:
        boolean removeKeyStroke = false;
        KeyStroke[] ks = imap.keys();
        if (ks == null || ks.length == 0) {
            imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0),
                    "backSlash");
            removeKeyStroke = true;
        }
        // now we can register by ToolTipManager
        ToolTipManager.sharedInstance().registerComponent(c);
        // and remove dummy KeyStroke
        if (removeKeyStroke) {
            imap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0));
        }
        // now last part - add appropriate MouseListener and
        // hear to mouseEntered events
        c.addMouseListener(MOUSE_HANDLER);
    }

    public void setIconMode(boolean mode) {
        this.icons = mode;
        cellrenderer.setIconMode(mode);
        if (icons) {
            registerComponent(this);
        }
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (rapid_evolution.RapidEvolution.aaEnabled)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
    }

    public String getToolTipText(MouseEvent e) {
        if (log.isTraceEnabled())
            log.trace("getToolTipText(): called");

        if (!icons)
            return super.getToolTipText(e);

        // Convert the mouse coordinates where the left mouse button was
        // pressed to a TTList item index.

        int index = locationToIndex(e.getPoint());
        
        // If the left mouse button was clicked and the mouse cursor was
        // not over a list item, index is set to -1.

        if ((index > -1) && getCellBounds(index, index).contains(e.getPoint())) {
            // Extract the ListModel.

            
            ListModel lm = (ListModel) getModel();

            // Get the ToolTipLink associated with the TTList item
            // index.

            link = (myImageIcon) lm.getElementAt(index);

            // Return the ToolTipLink's ToolTip text.
            if (log.isTraceEnabled())
                log.trace("getToolTipText(): returning="
                        + link.getToolTipText());

            return link.getToolTipText();
        } else {
            link = null;
            return null;
        }
    }
    
    private myImageIcon link = null;
    
    public JToolTip createToolTip() {
        if (link != null)
            return link.createToolTip();
        return
            super.createToolTip();
    }    


}

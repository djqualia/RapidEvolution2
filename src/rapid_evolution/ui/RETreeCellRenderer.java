package rapid_evolution.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import rapid_evolution.RapidEvolution;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.ui.swing.lookfeel.LookAndFeelManager;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import java.awt.Dimension;

public class RETreeCellRenderer extends RELabel implements TreeCellRenderer {

    private static Logger log = Logger.getLogger(RETreeCellRenderer.class);
    
    static public Color backgroundlistcolor = null;
    
    private RETree tree;
    
    public RETreeCellRenderer(RETree tree) {
        setOpaque(true);
        this.tree = tree;
    }

    static public int rowSelectionFixBuffer = 500;
    
    public Dimension getPreferredSize() {        
        Dimension size = super.getPreferredSize();
//        if (log.isTraceEnabled())
//            log.trace("getPreferredSize(): width=" + size.getWidth() + ", tree width=" + tree.getWidth());
        // this is a hack needed to allow tree selections for the entire row...  couldn't figure out a better way!
        // moust listeners on tree and background did not fire "mousePressed" for any background events, only when on
        // the style label exactly.
        if (LookAndFeelManager.isSubstanceLAF())
            size.setSize(size.getWidth() + rowSelectionFixBuffer, size.getHeight());
        return size;
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        if (!RapidEvolution.instance.loaded) {
            return this;
        }      
        
        setText(value.toString());
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

        /*
        if (selected) {
            if (SkinManager.instance.colormap.get("tree_cell_selection_background") != null)
                setBackground(SkinManager.instance.getColor("tree_cell_selection_background"));
            else if (SkinManager.instance.colormap.get("default_selected_background") != null)
                setBackground(SkinManager.instance.getColor("default_selected_background"));
            else setBackground(UIManager.getColor("Tree.selectionBackground"));
            if (SkinManager.instance.colormap.get("tree_cell_selection_foreground") != null)
                setForeground(SkinManager.instance.getColor("tree_cell_selection_foreground"));
            else if (SkinManager.instance.colormap.get("default_selected_foreground") != null)
                setForeground(SkinManager.instance.getColor("default_selected_foreground"));
            else setForeground(UIManager.getColor("Tree.selectionForeground"));
          } else {
            if (SkinManager.instance.colormap.get("tree_cell_background") != null)
                setBackground(SkinManager.instance.getColor("tree_cell_background"));
            else if (SkinManager.instance.colormap.get("default_background") != null)
                setBackground(SkinManager.instance.getColor("default_background"));
            else setBackground(UIManager.getColor("Tree.background"));
            if (SkinManager.instance.colormap.get("tree_cell_foreground") != null)
                setForeground(SkinManager.instance.getColor("tree_cell_foreground"));
            else if (SkinManager.instance.colormap.get("default_foreground") != null)
                setForeground(SkinManager.instance.getColor("default_foreground"));
            else setForeground(UIManager.getColor("Tree.foreground"));
          }
        */
        if (selected) {
            setBackground(SkinManager.instance.getColor("style_background_selected"));
            Color foreground = (Color)SkinManager.instance.colormap.get("style_foreground_selected");
            if (foreground == null)
                foreground = UIManager.getColor("Tree.selectionForeground");
            setForeground(foreground);
        } else {
            setBackground(SkinManager.instance.getColor("style_background_default"));
            setForeground(UIManager.getColor("Tree.Foreground"));
        }            

        if (SkinManager.instance.colormap.get("tree_cell_font") != null)
            setFont(SkinManager.instance.getFont("tree_cell_font"));
        else if (SkinManager.instance.colormap.get("tree_font") != null)
            setFont(SkinManager.instance.getFont("tree_font"));
        else if (SkinManager.instance.colormap.get("default_font") != null)
            setFont(SkinManager.instance.getFont("default_font"));
        else setFont(UIManager.getFont("Tree.font"));
                
        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            if (node.isLeaf()) {
                setIcon(null);
            } else {
                if (tree.isExpanded(path)) {
                    // expanded icon
                    setIcon(null);//SkinManager.instance.getIcon("tree_expanded_icon"));
                } else {
                    // collapsed icon
                    setIcon(null);//SkinManager.instance.getIcon("tree_collapsed_icon"));                    
                }
            }

        }
//        setIcon
        
        return this;
   }
    
    public void paintComponent(Graphics g) {
        if (LookAndFeelManager.isSubstanceLAF())
            super.paintComponent(g);
        else
            super.paintComponentSuper(g);
    }    
    
    
}
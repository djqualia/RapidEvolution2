package rapid_evolution.ui.styles;

import java.awt.Color;
import java.awt.Component;

import rapid_evolution.RapidEvolution;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.*;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import javax.swing.tree.DefaultMutableTreeNode;

import javax.swing.tree.TreePath;

public class MyStyleTreeRenderer extends RETreeCellRenderer implements TreeCellRenderer {

//  static public Color foregroundlistcolor = null;
//  static public Color foregroundlistnonselectedcolor = null;
  static public Color backgroundlistcolor = null;

  public MyStyleTreeRenderer(RETree tree) {
        super(tree);
        setOpaque(true);
  }
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      if (!RapidEvolution.instance.loaded) {
          return this;
      }      
      super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
//      setText(value.toString());
      if (OptionsUI.instance.autohighlightstyles.isSelected()) {
          TreePath path = tree.getPathForRow(row);
          if (path != null) {
              DefaultMutableTreeNode defnode = (DefaultMutableTreeNode)tree.getPathForRow(row).getLastPathComponent();          
              boolean isStyleofSelected = false;
              if (defnode instanceof MyMutableStyleNode) {
                  MyMutableStyleNode mynode = (MyMutableStyleNode)defnode;          
                  isStyleofSelected = RapidEvolutionUI.instance.isStyleOfSelected(mynode.getStyle());
                  StyleLinkedList style = mynode.getStyle();
                  if (style.isExcluded()) {
                      setBackground(SkinManager.instance.getColor("style_background_excluded"));
                      Color foreground = (Color)SkinManager.instance.colormap.get("style_foreground_selected");
                      if (foreground == null)
                          foreground = UIManager.getColor("Tree.selectionForeground");
                      setForeground(foreground);
                  } else if (style.isRequired()) {
                      setBackground(SkinManager.instance.getColor("style_background_required"));
                      Color foreground = (Color)SkinManager.instance.colormap.get("style_foreground_selected");
                      if (foreground == null)
                          foreground = UIManager.getColor("Tree.selectionForeground");
                      setForeground(foreground);
                  } else if (selected && isStyleofSelected) {
                      setBackground(SkinManager.instance.getColor("style_background_of_current_song"));
                      Color foreground = (Color)SkinManager.instance.colormap.get("style_foreground_selected");
                      if (foreground == null)
                          foreground = UIManager.getColor("Tree.selectionForeground");
                      setForeground(foreground);
                  } else if (selected) {
                      setBackground(SkinManager.instance.getColor("style_background_selected"));
                      Color foreground = (Color)SkinManager.instance.colormap.get("style_foreground_selected");
                      if (foreground == null)
                          foreground = UIManager.getColor("Tree.selectionForeground");
                      setForeground(foreground);
                  } else if (isStyleofSelected) {
                      setBackground(SkinManager.instance.getColor("style_background_of_selected_song"));
                      setForeground(UIManager.getColor("Tree.Foreground"));
                  } else {
                      setBackground(SkinManager.instance.getColor("style_background_default"));
                      setForeground(UIManager.getColor("Tree.Foreground"));
                  }
              } else {                  
                  if (selected && isStyleofSelected) {
                      setBackground(SkinManager.instance.getColor("style_background_of_current_song"));
                      setForeground(UIManager.getColor("Tree.selectionForeground"));
                  } else if (selected) {
                      setBackground(SkinManager.instance.getColor("style_background_selected"));
                      setForeground(UIManager.getColor("Tree.selectionForeground"));
                  } else if (isStyleofSelected) {
                      setBackground(SkinManager.instance.getColor("style_background_of_selected_song"));
                      setForeground(UIManager.getColor("Tree.Foreground"));
                  } else {
                      setBackground(SkinManager.instance.getColor("style_background_default"));
                      setForeground(UIManager.getColor("Tree.Foreground"));
                  }
              }
          } else {
              if (selected) {
                  setBackground(backgroundlistcolor);
                  Color foreground = (Color)SkinManager.instance.colormap.get("style_foreground_selected");
                  if (foreground == null)
                      foreground = UIManager.getColor("Tree.selectionForeground");
                  setForeground(foreground);
              } else {
                  setBackground(SkinManager.instance.getColor("style_background_default"));
                  setForeground(UIManager.getColor("Tree.Foreground"));
              }
          }
//        if (iss) {
//            setBorder(BorderFactory.createLineBorder(
//              Color.blue, 2));
//        } else {
//            setBorder(BorderFactory.createLineBorder(
//             list.getBackground(), 2));
//        }
      }

      return this;
  }
  
}

package rapid_evolution.ui;

import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.UIManager;
import javax.swing.ListCellRenderer;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;

public class REListCellRenderer extends JLabel implements ListCellRenderer {

    private boolean icon = false;
    public REListCellRenderer(boolean icon) {
        this.icon = icon;
        setOpaque(true);
  }

    public void setIconMode(boolean icon) {
        this.icon = icon;
    }
    public REListCellRenderer() {
          setOpaque(true);
    }
    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = null;
        if (!icon) {
            setText(value.toString());
            if (isSelected) {
              if (SkinManager.instance.colormap.get("list_cell_selection_background") != null)
                setBackground(SkinManager.instance.getColor("list_cell_selection_background"));
              else if (SkinManager.instance.colormap.get("default_selected_background") != null)
                setBackground(SkinManager.instance.getColor("default_selected_background"));
              else setBackground(UIManager.getColor("List.selectionBackground"));
              if (SkinManager.instance.colormap.get("list_cell_selection_foreground") != null)
                setForeground(SkinManager.instance.getColor("list_cell_selection_foreground"));
              else if (SkinManager.instance.colormap.get("default_selected_foreground") != null)
                setForeground(SkinManager.instance.getColor("default_selected_foreground"));
              else setForeground(UIManager.getColor("List.selectionForeground"));
            } else {
              if (SkinManager.instance.colormap.get("list_cell_background") != null)
                setBackground(SkinManager.instance.getColor("list_cell_background"));
              else if (SkinManager.instance.colormap.get("default_background") != null)
                setBackground(SkinManager.instance.getColor("default_background"));
              else setBackground(UIManager.getColor("List.background"));
              if (SkinManager.instance.colormap.get("list_cell_foreground") != null)
                setForeground(SkinManager.instance.getColor("list_cell_foreground"));
              else if (SkinManager.instance.colormap.get("default_foreground") != null)
                setForeground(SkinManager.instance.getColor("default_foreground"));
              else setForeground(UIManager.getColor("List.foreground"));
            }
            if (SkinManager.instance.colormap.get("list_cell_font") != null)
              setFont(SkinManager.instance.getFont("list_cell_font"));
            else if (SkinManager.instance.colormap.get("list_font") != null)
              setFont(SkinManager.instance.getFont("list_font"));
            else if (SkinManager.instance.colormap.get("default_font") != null)
              setFont(SkinManager.instance.getFont("default_font"));
            else setFont(UIManager.getFont("List.font"));
            return this;
        } else {
            component = (Component)value;
            if (isSelected) {
                if (SkinManager.instance.colormap.get("list_cell_selection_background") != null)
                    component.setBackground(SkinManager.instance.getColor("list_cell_selection_background"));
                  else if (SkinManager.instance.colormap.get("default_selected_background") != null)
                      component.setBackground(SkinManager.instance.getColor("default_selected_background"));
                  else component.setBackground(UIManager.getColor("List.selectionBackground"));
                  if (SkinManager.instance.colormap.get("list_cell_selection_foreground") != null)
                      component.setForeground(SkinManager.instance.getColor("list_cell_selection_foreground"));
                  else if (SkinManager.instance.colormap.get("default_selected_foreground") != null)
                      component.setForeground(SkinManager.instance.getColor("default_selected_foreground"));
                  else component.setForeground(UIManager.getColor("List.selectionForeground"));
            } else {
                if (SkinManager.instance.colormap.get("list_cell_background") != null)
                    component.setBackground(SkinManager.instance.getColor("list_cell_background"));
                  else if (SkinManager.instance.colormap.get("default_background") != null)
                      component.setBackground(SkinManager.instance.getColor("default_background"));
                  else component.setBackground(UIManager.getColor("List.background"));
                  if (SkinManager.instance.colormap.get("list_cell_foreground") != null)
                      component.setForeground(SkinManager.instance.getColor("list_cell_foreground"));
                  else if (SkinManager.instance.colormap.get("default_foreground") != null)
                      component.setForeground(SkinManager.instance.getColor("default_foreground"));
                  else component.setForeground(UIManager.getColor("List.foreground"));                
            }
            return component;
        }
    }
    
    
}

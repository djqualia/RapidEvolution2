package rapid_evolution.ui;

import javax.swing.*;
import java.awt.Component;
import java.util.*;
import rapid_evolution.ui.REDialogAbstract;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.ui.RapidEvolutionUI;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Point;

public class REDialog extends REDialogAbstract {

  static public REDialog getREDialog(String id) { return (REDialog)instances.get(id); }
  static public HashMap instances = new HashMap();

  public REDialog(String id) {
    dialog_id = id;
    instances.put(id, this);
  }

  public Object display_parameter = null;

  private String dialog_id = null;
  private boolean displayed = false;

  public void reset() {
    displayed = false;
  }

  protected boolean PreDisplay() { return true; }
  protected void PostDisplay() { }
  protected boolean PreDisplay(Object source) { return true; }
  protected void PostDisplay(Object source) { }
  public void PostInit() { }

  public Component getParentComponent() {
    try {
      Component component = (Component)SkinManager.instance.getParent(getDialog());
      if (component != null) return component;
    } catch (Exception e) { }
    return null;
  }

  public void Display() { Display(null); }
  public void Display(Object source) {
    JDialog dialog = getDialog();
    if (dialog == null) return;
    JDialog parent = null;
    try {
      parent = (JDialog)SkinManager.instance.getParent(dialog);
    } catch (Exception e) { }
    if (dialog.isVisible()) dialog.requestFocus();
    else {
      if (!PreDisplay()) return;
      if (!PreDisplay(source)) return;
      Point point = dialog.getLocation();
      if (!displayed && ((int)point.getX() == 0) && ((int)point.getY() == 0)) {
        if (parent == null) RapidEvolutionUI.instance.CenterComponent(dialog);
        else RapidEvolutionUI.instance.CenterComponent(dialog, parent);
        displayed = true;
      }
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension scrnsize = toolkit.getScreenSize();
      if (dialog.getLocation().getX() < 0) dialog.setLocation(0, (int)dialog.getLocation().getY());
      if (dialog.getLocation().getX() + dialog.getSize().getWidth() > scrnsize.width) dialog.setLocation((int)(scrnsize.width - dialog.getSize().getWidth()), (int)dialog.getLocation().getY());
      if (dialog.getLocation().getY() < 0) dialog.setLocation((int)dialog.getLocation().getX(), 0);
      if (dialog.getLocation().getY() + dialog.getSize().getHeight() > scrnsize.height) dialog.setLocation((int)dialog.getLocation().getX(), (int)(scrnsize.height - dialog.getSize().getHeight()));
      dialog.setVisible(true);
      PostDisplay();
      PostDisplay(source);
    }
    display_parameter = null;
  }

  public JDialog getDialog() {
    return (JDialog)SkinManager.instance.getObject(dialog_id);
  }

  public void Hide() {
    JDialog dialog = getDialog();
    if (dialog != null) dialog.setVisible(false);
  }

  public boolean isVisible() {
    JDialog dialog = getDialog();
    if (dialog != null) return dialog.isVisible();
    return false;
  }

  public void requestFocus() {
    JDialog dialog = getDialog();
    if (dialog != null) dialog.requestFocus();
  }

  public void setVisible(boolean visible) {
    JDialog dialog = getDialog();
    if (dialog != null) dialog.setVisible(visible);
  }

  public void repaint() {
    JDialog dialog = getDialog();
    if (dialog != null) dialog.repaint();
  }

  public void addWindowListener(WindowAdapter adapter) {
    JDialog dialog = getDialog();
    if (dialog != null) {
      dialog.addWindowListener(adapter);
    }
  }

  public void addKeyListener(KeyListener listener) {
    JDialog dialog = getDialog();
    if (dialog != null) {
      dialog.addKeyListener(listener);
    }
  }

  public void setTitle(String text) {
    JDialog dialog = getDialog();
    if (dialog != null) dialog.setTitle(text);
  }

  public void setSize(int x, int y) {
    JDialog dialog = getDialog();
    if (dialog != null) dialog.setSize(x,y);
  }

  public void validate() {
      JDialog dialog = getDialog();
      if (dialog != null) dialog.validate();
    }

  public int getHeight() {
    JDialog dialog = getDialog();
    if (dialog != null) return (int)(dialog.getSize().getHeight());
    return 0;

  }

  public int getWidth() {
    JDialog dialog = getDialog();
    if (dialog != null) return (int)(dialog.getSize().getWidth());
    return 0;

  }

}

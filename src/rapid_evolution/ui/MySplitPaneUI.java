package rapid_evolution.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import rapid_evolution.ui.MySplitPaneDivider;

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

public class MySplitPaneUI extends BasicSplitPaneUI {
  public MySplitPaneUI() {
    super();
  }
  
  public MySplitPaneUI(int _divider_size, boolean use_empty_splitpanel_dividers) {
    super();
    divider_size = _divider_size;
    this.use_empty_splitpanel_dividers = use_empty_splitpanel_dividers;
  }

  int divider_size = -1;
  boolean use_empty_splitpanel_dividers = false;

  public BasicSplitPaneDivider createDefaultDivider() {
    return new MySplitPaneDivider( this , divider_size, use_empty_splitpanel_dividers);
  }
  
}

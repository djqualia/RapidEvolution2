package rapid_evolution.ui;

import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

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
public class MySplitPaneDivider extends BasicSplitPaneDivider {
  public MySplitPaneDivider(BasicSplitPaneUI ui, int _divider_size, boolean use_empty_splitpanel_dividers) {
    super(ui);
    setLayout(new BorderLayout());
    JPanel p = new JPanel();
    Color background = SkinManager.instance.getAspectColor(ui.getSplitPane(), ".background", null);
    if (background != null) p.setBackground(background);
    divider_size = _divider_size;
//    p.setBorder(new LineBorder(Color.pink));
    add(p);
//    setBorder(new LineBorder(Color.yellow));
    this.invisible = use_empty_splitpanel_dividers;
  }
  private boolean invisible = false;

  public void paint(Graphics g) {
    if (!invisible) super.paint(g);
  }

  int divider_size = -1;
  public int getDividerSize() {
    if (divider_size != -1) return divider_size;
    return super.getDividerSize();
  }
  
}

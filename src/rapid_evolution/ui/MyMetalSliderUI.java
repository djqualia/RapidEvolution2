package rapid_evolution.ui;

import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.Graphics;

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
public class MyMetalSliderUI extends MetalSliderUI {
  public MyMetalSliderUI() {
    super();
  }

  protected  final String SLIDER_FILL = null;

  public void paintThumb(Graphics g) {
    filledSlider = false;
    super.paintThumb(g);
  }

  public void paintTrack(Graphics g) {
    filledSlider = false;
    super.paintTrack(g);
  }
}

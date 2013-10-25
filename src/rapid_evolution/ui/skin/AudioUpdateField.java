package rapid_evolution.ui.skin;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.audio.Bpm;

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
public class AudioUpdateField implements DocumentListener {
  public AudioUpdateField(String _id) {
    id = _id;
  }

  String id;

  public void changedUpdate(DocumentEvent e) { update(); }
  public void insertUpdate(DocumentEvent e) { update(); }
  public void removeUpdate(DocumentEvent e) { update(); }

  private void update() {
    try {
    if (id.equalsIgnoreCase("options_bpm_detection_range_start")) {
      double val = Double.parseDouble(SkinManager.instance.getFieldText(
          "options_bpm_detection_range_start"));
      if (val != 0.0)  Bpm.minbpm = val;
    }
    if (id.equalsIgnoreCase("options_bpm_detection_range_end")) {
      double val = Double.parseDouble(SkinManager.instance.getFieldText(
          "options_bpm_detection_range_end"));
      if (val != 0.0) Bpm.maxbpm = val;
    }
    } catch (Exception exception) { }
  }
}

package rapid_evolution.ui.styles;

import rapid_evolution.ui.main.SearchSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import rapid_evolution.ui.styles.ListStyleSongMouse;
import rapid_evolution.ui.styles.ListStyleSongsUI;
import javax.swing.DefaultListModel;

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
public class StyleSongSelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
        if (ListStyleSongsUI.instance.liststylesongslist.getSelectedIndices().length > 1) {
            ListStyleSongMouse.instance.edititem.setEnabled(false);
            ListStyleSongMouse.instance.playselection.setEnabled(true);
            ListStyleSongMouse.instance.removeselection.setEnabled(true);
        } else if (ListStyleSongsUI.instance.liststylesongslist.getSelectedIndices().length == 1) {
            ListStyleSongMouse.instance.edititem.setEnabled(true);
            ListStyleSongMouse.instance.playselection.setEnabled(true);
            ListStyleSongMouse.instance.removeselection.setEnabled(true);
        } else {
            ListStyleSongMouse.instance.edititem.setEnabled(false);
            ListStyleSongMouse.instance.playselection.setEnabled(false);
            ListStyleSongMouse.instance.removeselection.setEnabled(false);
        }
        DefaultListModel dlm = (DefaultListModel) ListStyleSongsUI.instance.liststylesongslist.getModel();
        if (dlm.getSize() > 0) ListStyleSongsUI.instance.removesongfromstylebt.setEnabled(true);
        else ListStyleSongsUI.instance.removesongfromstylebt.setEnabled(false);
    }
}

package rapid_evolution.ui.styles;

import java.util.Vector;

import javax.swing.DefaultListModel;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

public class RemoveSongsFromStyle extends Thread {
    private static Logger log = Logger.getLogger(RemoveSongsFromStyle.class);
    
  public RemoveSongsFromStyle() { }
  public void run() {
    int n = IOptionPane.showConfirmDialog(ListStyleSongsUI.instance.getDialog(),
              SkinManager.instance.getDialogMessageText("remove_songs_from_style"),
              SkinManager.instance.getDialogMessageTitle("remove_songs_from_style"),
              IOptionPane.YES_NO_OPTION);
    if (n != 0) return;
    ListStyleSongsUI.instance.removesongfromstylebt.setEnabled(false);
    DefaultListModel dlm = (DefaultListModel) ListStyleSongsUI.instance.liststylesongslist.getModel();
    int[] selectedindices = ListStyleSongsUI.instance.liststylesongslist.getSelectedIndices();
    StyleLinkedList originaledited = EditStyleUI.instance.editedstyle;
    Vector processsongs = new Vector();
    for (int i = 0; i < selectedindices.length; ++i) {
      dlm.removeElementAt(selectedindices[i] - i);
      processsongs.add(ListStyleSongsUI.instance.stylesonglist.get(selectedindices[i] - i));
      ListStyleSongsUI.instance.stylesonglist.removeElementAt(selectedindices[i] - i);

    }
    ListStyleSongsUI.instance.removesongfromstylebt.setEnabled(true);
    for (int j = 0; j < processsongs.size(); ++j) {
      SongLinkedList iter = (SongLinkedList)processsongs.get(j);
      try {
        if (originaledited.containsDirect(iter)) originaledited.insertExcludeSong(iter);
      } catch (Exception e) { log.error("run(): error", e); }
    }
  }
}

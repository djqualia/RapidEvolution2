package rapid_evolution.ui.main;

import rapid_evolution.RapidEvolution;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.SongDB;
import rapid_evolution.ui.SkinManager;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.ui.styles.*;

import rapid_evolution.StyleLinkedList;

public class StyleTreeSelectionHandler implements TreeSelectionListener {

    private static Logger log = Logger.getLogger(StyleTreeSelectionHandler.class);
    
    public void valueChanged(TreeSelectionEvent e) {
        try {
            int num_selectedstyles = 0;
            TreePath[] treepaths = StylesPane.instance.styletree.getSelectionPaths();
            if ((treepaths != null) && (treepaths.length == 1)) SkinManager.instance.setEnabled("style_edit_button", true);
            else SkinManager.instance.setEnabled("style_edit_button", false);
            boolean required_or_excluded_exists = false;
            StyleLinkedList siter = SongDB.instance.masterstylelist;
            while ((siter != null) && !required_or_excluded_exists) {
                if (siter.isRequired() || siter.isExcluded()) required_or_excluded_exists = true;
                siter = siter.next;
            }
            if ((treepaths != null) && (treepaths.length >= 1)) {
                StylesPane.instance.clearstylesbutton.setEnabled(true);
                StylesPane.instance.deletestylebutton.setEnabled(true);
            }
            else {
                if (required_or_excluded_exists)
                    StylesPane.instance.clearstylesbutton.setEnabled(true);
                else
                    StylesPane.instance.clearstylesbutton.setEnabled(false);
                StylesPane.instance.deletestylebutton.setEnabled(false);
            }
            if (OptionsUI.instance.useselectedstylesimilarity.isSelected()) {
                RapidEvolutionUI.instance.UpdateStyleSimilarityRoutine();
            }
            if (OptionsUI.instance.filterusestyles.isSelected() && !SongDB.instance.isAddingSong) {
                // test this with the fila brazillia scenario -> clear styles, shoiuld add albums
                OptionsUI.instance.filter1.updateList(true);
                //OptionsUI.instance.filter1.valueChanged(e);
            }
        } catch (Exception e2) {
            log.error("valueChanged(): error", e2);
        }
    }
}

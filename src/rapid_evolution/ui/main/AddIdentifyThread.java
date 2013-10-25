package rapid_evolution.ui.main;

import java.util.Vector;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.audio.AudioLib;
import rapid_evolution.net.GetSongInfoThread;
import rapid_evolution.net.MixshareClient;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.AddStyleRunnable;
import rapid_evolution.ui.MyMutableStyleNode;
import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.audio.tags.TagManager;

public class AddIdentifyThread extends Thread {

    private static Logger log = Logger.getLogger(AddIdentifyThread.class);

    static public int addseqnum = 0;

    static public int addidentifythreadcount = 0;

    public AddIdentifyThread(int in_myseqnum) {
        myseqnum = in_myseqnum;
    }

    int myseqnum;

    public void run() {
        addidentifythreadcount++;
        if (!OptionsUI.instance.disableautotagreading.isSelected()) {
            try {
                AddSongsUI.instance.addsongsreadtagbutton.setEnabled(false);
                // System.out.println(addsongsfilenamefield.getText());
                SongLinkedList datasong = TagManager
                        .readTags(AddSongsUI.instance.addsongsfilenamefield
                                .getText());
                if (addseqnum == myseqnum) {
                    AddSongsUI.instance.PopulateAddSongDialog(datasong, true, OptionsUI.instance.createstylesfromgenretags.isSelected());
                    if (datasong.stylelist != null) {
                        for (int s = 0; s < datasong.stylelist.length; ++s) {

                            String stylename = datasong.stylelist[s];

                            boolean found = false;

                            StyleLinkedList siter = SongDB.instance.masterstylelist;
                            while (siter != null) {
                                if (StringUtil.areStyleNamesEqual(siter
                                        .getName(), stylename)) {
                                    found = true;
                                    Vector nodes = (Vector) AddSongsUI.instance.style_tree_map
                                            .get(siter);
                                    if (nodes != null) {
                                        for (int i = 0; i < nodes.size(); ++i) {
                                            MyMutableStyleNode node = (MyMutableStyleNode) nodes
                                                    .get(i);
                                            AddSongsUI.instance.addsongstylestree
                                                    .addSelectionPath(new TreePath(
                                                            node.getPath()));
                                        }
                                    }
                                }
                                siter = siter.next;
                            }

                            if (!found
                                    && OptionsUI.instance.createstylesfromgenretags
                                            .isSelected()) {
                                siter = SongDB.instance.addStyle(stylename,
                                        false, new SelectStyleRunnable());
                            }

                        }
                    }
                }
            } catch (Exception e) {
            }
            AddSongsUI.instance.addsongsreadtagbutton.setEnabled(true);
            if (formatTrack(AddSongsUI.instance.addsongstrackfield.getText())) {
                char firstcharacter = AddSongsUI.instance.addsongstrackfield
                        .getText().charAt(0);
                if ((firstcharacter >= '1') && (firstcharacter <= '9')) {
                    AddSongsUI.instance.addsongstrackfield.setText("0"
                            + AddSongsUI.instance.addsongstrackfield.getText());
                }
            }
        }
        if (AddSongsUI.instance.addsongstitlefield.getText().equals("")) {
            if (OptionsUI.instance.usefilenameastag.isSelected())
                AddSongsUI.instance.addsongstitlefield
                        .setText(StringUtil
                                .remove_underscores(StringUtil
                                        .ScrubFileType(AddSongsUI.instance.addsongsfilenamefield
                                                .getText())));
        }

        try {
            if (addseqnum == myseqnum) {
                if (!AddSongsUI.instance.addsongsfilenamefield.getText()
                        .equals("")
                        && AddSongsUI.instance.addsongstimefield.getText().equals("")) {
                    int seconds = (int) AudioLib
                            .get_track_time(AddSongsUI.instance.addsongsfilenamefield
                                    .getText());
                    if (seconds != 0)
                        AddSongsUI.instance.addsongstimefield
                                .setText(StringUtil.seconds_to_time(seconds));
                }
            }
        } catch (Exception e) {
            log.error("run(): error", e);
        }        
        try {
            if (addseqnum == myseqnum)
                if (OptionsUI.instance.automaticallyquerywhenadding
                        .isSelected()
                        && MixshareClient.instance.isConnected()) {
                    GetSongInfoThread
                            .GetSongInfoRoutine(null, true, true, true);
                    // new GetSongInfoThread(true, true).start();
                }
        } catch (Exception e) {
            log.error("run(): error", e);
        }
        try {
            if (addseqnum == myseqnum) {
                if (formatTrack(AddSongsUI.instance.addsongstrackfield
                        .getText())) {
                    char firstcharacter = AddSongsUI.instance.addsongstrackfield
                            .getText().charAt(0);
                    if ((firstcharacter >= '1') && (firstcharacter <= '9')) {
                        AddSongsUI.instance.addsongstrackfield.setText("0"
                                + AddSongsUI.instance.addsongstrackfield
                                        .getText());
                    }
                }
            }
        } catch (Exception e) {
            log.error("AddIdentifyThread(): error", e);
        }
        addidentifythreadcount--;
    }

    public class SelectStyleRunnable extends AddStyleRunnable {
        public void run() {
        	if (log.isDebugEnabled())
        		log.debug("SelectStyleRunnable.run(): starting=" + style.getName());
            Vector nodes = (Vector) AddSongsUI.instance.style_tree_map
            .get(style);
    if (nodes != null) {
        for (int i = 0; i < nodes.size(); ++i) {
            MyMutableStyleNode node = (MyMutableStyleNode) nodes
                    .get(i);
            AddSongsUI.instance.addsongstylestree
                    .addSelectionPath(new TreePath(
                            node.getPath()));
        }
    }
        }
    }
    
    static public boolean formatTrack(String track) {
        if ((track == null) || (track.length() == 0))
            return false;
        if (track.indexOf("-") >= 0)
            return false;
        int index = 0;
        while ((index < track.length())
                && Character.isDigit(track.charAt(index))) {
            ++index;
        }
        if (index == 1) {
            if ((track.length() == 3) && Character.isDigit(track.charAt(2)))
                return false;
            return true;
        }
        return false;
    }
}

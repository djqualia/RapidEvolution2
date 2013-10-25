package rapid_evolution.ui.styles;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

public class AddProcessSetStyles extends Thread {

    private static Logger log = Logger.getLogger(AddProcessSetStyles.class);
    
    private StyleLinkedList[] styles;
    private SongLinkedList[] songs;
    public AddProcessSetStyles(StyleLinkedList[] styles, SongLinkedList[] songs) { this.styles = styles; this.songs = songs; }
    public void run() {
        try {
            log.trace("run(): starting");
      StylesUI.processsetcount++;
      SkinManager.instance.setEnabled("style_add_button", false);
      AddStyleUI.instance.addstyleokbutton.setEnabled(false);
            
      for (int j = 0; j < songs.length; ++j) {
        try {
          SongLinkedList editedsong = songs[j];
          boolean changed = false;

          for (int p = 0; p < styles.length; ++p) {
              StyleLinkedList styleiter = styles[p];
              if (!styleiter.containsDirect(editedsong)) {
                  if (styleiter.containsExcludeSong(editedsong.uniquesongid))
                    styleiter.removeExcludeSong(editedsong);
                  String[] ekeywords = styleiter.getExcludeKeywords();
                for (int i = 0; i < ekeywords.length; ++i) {
                  if (styleiter.matchesExcludeKeywords(editedsong ,ekeywords[i])) {
                    String display = SkinManager.instance.getDialogMessageText("delete_style_exclude_keyword");
                    display = StringUtil.ReplaceString("%style%", display, styleiter.getName());
                    display = StringUtil.ReplaceString("%keyword%", display, ekeywords[i]);
                    display = StringUtil.ReplaceString("%songid%", display, editedsong.getSongIdShort());
                    int n = IOptionPane.showConfirmDialog(
                        SkinManager.instance.getFrame("main_frame"),
                        display,
                        SkinManager.instance.getDialogMessageTitle("delete_style_exclude_keyword"),
                        IOptionPane.YES_NO_OPTION);
                    if (n == 0) {
                      styleiter.removeExcludeKeyword(ekeywords[i]);
                      --i;
                    }
                  }
                }
                changed = true;
                styleiter.insertSong(editedsong);
              }
              
          }
          
          if (changed && OptionsUI.instance.enablefilter.isSelected()) {
              OptionsUI.instance.filter1.updatedSong(editedsong, false);
              OptionsUI.instance.filter2.updatedSong(editedsong, false);
              OptionsUI.instance.filter3.updatedSong(editedsong, false);
          }            
        } catch (Exception e) { }
      }
      StylesUI.processsetcount--;
      if (StylesUI.processsetcount == 0) {
        SkinManager.instance.setEnabled("style_add_button", true);
        AddStyleUI.instance.addstyleokbutton.setEnabled(true);
      }
        } catch (Exception e) {
            log.error("run(): error", e);
        }
    }
  }

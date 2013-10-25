package rapid_evolution.ui;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import rapid_evolution.MixoutObject;
import com.mixshare.rapid_evolution.util.timing.Semaphore;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.SongStack;
import rapid_evolution.StringUtil;
import rapid_evolution.comparables.MyStringFloat;
import rapid_evolution.filefilters.MixFileFilter;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.SearchPane;

import com.ibm.iwt.IOptionPane;
import com.mixshare.rapid_evolution.mix.SongTrail;
import com.mixshare.rapid_evolution.mix.SongTrailManager;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class SongTrailUI extends REDialog implements ActionListener {

    private static Logger log = Logger.getLogger(SongTrailUI.class);
    
    public SongTrailUI(String id) {
      super(id);
        instance = this;
        setupDialog();
        setupActionListeners();
    }

    public static SongTrailUI instance = null;
    public JButton songtrailokbutton = new REButton();
    public JButton songtrailsaveasbt = new REButton();
    public JButton loadsongtrailbutton = new REButton();
    public DragSongTrailList songtraillist = new DragSongTrailList();
    public JButton clearsongtrailbutton = new REButton();
    public JButton clearallsongtrailbutton = new REButton();

    private void setupDialog() {
            // song trail dialog
            songtraillist.setModel(new DefaultListModel());
            songtraillist.addMouseListener(new SongTrailListListener());
    }

    class SongTrailListListener extends MouseAdapter {
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
          Point pt = e.getPoint();
          DefaultListModel dlm = (DefaultListModel) songtraillist.getModel();
          int index = songtraillist.locationToIndex(pt);
          if (index < 0) return;
          SongStack newprevstack = null;
          SongStack newnextstack = null;
          SongLinkedList nextcurrentsong = null;
          int line = 0;
          SongStack tmpstack = null;
          SongStack siter = RapidEvolutionUI.instance.prevstack;
          while (siter != null) {
            tmpstack = new SongStack(siter.songid, tmpstack);
            siter = siter.next;
          }
          boolean addtonext = false;
          SongLinkedList lastsong = null;
          siter = tmpstack;
          boolean entered;
          while (siter != null) {
            SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
            entered = false;
            if (lastsong != null) {
              for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
                if (lastsong.mixout_songs[i] == siter.songid) {
                  entered = true;
                  if ((line == index) || ((line + 1) == index)) {
                    nextcurrentsong = addsong;
                    addtonext = true;
                  } else {
                    if (addtonext) newnextstack = new SongStack(addsong.uniquesongid, newnextstack);
                    else newprevstack = new SongStack(addsong.uniquesongid, newprevstack);
                  }
                  line++;
                }
              }
            }
            if (!entered) {
                if (line == index) {
                  nextcurrentsong = addsong;
                  addtonext = true;
                } else {
                  if (addtonext) newnextstack = new SongStack(addsong.uniquesongid, newnextstack);
                  else newprevstack = new SongStack(addsong.uniquesongid, newprevstack);
                }

            }
            line++;
            lastsong = addsong;
            siter = siter.next;
          }
          if (RapidEvolutionUI.instance.currentsong != null) {
            entered = false;
            if (lastsong != null) {
              for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
                if (lastsong.mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) {
                  entered = true;
                  if ((line == index) || ((line + 1) == index)) {
                    nextcurrentsong = RapidEvolutionUI.instance.currentsong;
                    addtonext = true;
                  } else {
                    if (addtonext) newnextstack = new SongStack(RapidEvolutionUI.instance.currentsong.uniquesongid, newnextstack);
                    else newprevstack = new SongStack(RapidEvolutionUI.instance.currentsong.uniquesongid, newprevstack);
                  }
                  line++;
                }
              }
            }
            if (!entered) {
              if (line == index) {
                nextcurrentsong = RapidEvolutionUI.instance.currentsong;
                addtonext = true;
              } else {
                if (addtonext) newnextstack = new SongStack(RapidEvolutionUI.instance.currentsong.uniquesongid, newnextstack);
                else newprevstack = new SongStack(RapidEvolutionUI.instance.currentsong.uniquesongid, newprevstack);
              }
            }
            line++;
            lastsong = RapidEvolutionUI.instance.currentsong;
          }
          siter = RapidEvolutionUI.instance.nextstack;
          while (siter != null) {
            entered = false;
            SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
            if (lastsong != null) {
              for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
                if (lastsong.mixout_songs[i] == siter.songid) {
                  entered = true;
                  if ((line == index) || ((line + 1) == index)) {
                    nextcurrentsong = addsong;
                    addtonext = true;
                  } else {
                    if (addtonext) newnextstack = new SongStack(addsong.uniquesongid, newnextstack);
                    else newprevstack = new SongStack(addsong.uniquesongid, newprevstack);
                  }
                  line++;
                }
              }
            }
            if (!entered) {
              if (line == index) {
                nextcurrentsong = addsong;
                addtonext = true;
              } else {
                if (addtonext) newnextstack = new SongStack(addsong.uniquesongid, newnextstack);
                else newprevstack = new SongStack(addsong.uniquesongid, newprevstack);
              }
            }
            line++;
            lastsong = addsong;
            siter = siter.next;
          }
          tmpstack = null;
          siter = newnextstack;
          while (siter != null) {
            tmpstack = new SongStack(siter.songid, tmpstack);
            siter = siter.next;
          }
          newnextstack = tmpstack;
          RapidEvolutionUI.instance.nextstack = newnextstack;
          RapidEvolutionUI.instance.prevstack = newprevstack;
          SearchPane.instance.bpmslider.setValue(0);
          RapidEvolutionUI.instance.change_current_song(nextcurrentsong, 0, true, true);
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
          Point pt = e.getPoint();
          DefaultListModel dlm = (DefaultListModel) songtraillist.getModel();
          int index = songtraillist.locationToIndex(pt);
          if (index < 0) return;
          EditSongTrail(index);
        }
      }
    }

    public void EditSongTrail(int index) {
      Integer i = (Integer)songtraildatatype.get(index);
      if (i == null) return;
      int val = i.intValue();
      if (val == 0) {
        EditSongUI.instance.EditSong((SongLinkedList)songtraildata.get(index));
      } else try {
        EditMixoutSongTrailUI.instance.editedmixout = (MixoutObject)songtraildata.get(index);
        if (!EditMixoutSongTrailUI.instance.isVisible()) {
          EditMixoutSongTrailUI.instance.editmixoutfromsong = EditMixoutSongTrailUI.instance.editedmixout.song;
          EditMixoutSongTrailUI.instance.editmixouttosong = SongDB.instance.NewGetSongPtr(EditMixoutSongTrailUI.instance.editedmixout.song.mixout_songs[EditMixoutSongTrailUI.instance.editedmixout.index]);
          EditMixoutSongTrailUI.instance.editmixoutfromfield.setText(EditMixoutSongTrailUI.instance.editmixoutfromsong.getSongIdShort());
          EditMixoutSongTrailUI.instance.editmixouttofield.setText(EditMixoutSongTrailUI.instance.editmixouttosong.getSongIdShort());
          EditMixoutSongTrailUI.instance.editmixoutcommentsfield.setText(EditMixoutSongTrailUI.instance.editedmixout.song.getMixoutComments(EditMixoutSongTrailUI.instance.editedmixout.index));
          EditMixoutSongTrailUI.instance.editmixoutbpmdifffield.setText(String.valueOf(EditMixoutSongTrailUI.instance.editedmixout.song.getMixoutBpmdiff(EditMixoutSongTrailUI.instance.editedmixout.index)));
          EditMixoutSongTrailUI.instance.editmixoutscorefield.setText(String.valueOf(EditMixoutSongTrailUI.instance.editedmixout.song.getMixoutRank(EditMixoutSongTrailUI.instance.editedmixout.index)));
          EditMixoutSongTrailUI.instance.editmixoutaddoncb.setSelected(EditMixoutSongTrailUI.instance.editedmixout.song.getMixoutAddon(EditMixoutSongTrailUI.instance.editedmixout.index));
          EditMixoutSongTrailUI.instance.Display();
        } else EditMixoutSongTrailUI.instance.requestFocus();
      } catch (Exception e) { log.error("EditSongTrail(): error", e); }
    }

    public class DragSongTrailList extends REList implements DragSourceListener, DragGestureListener {
        public DragSongTrailList() {
          super();
          dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
        }
        DragSource dragSource = DragSource.getDefaultDragSource();
        public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent){}
        public void dragEnter(DragSourceDragEvent DragSourceDragEvent){}
        public void dragExit(DragSourceEvent DragSourceEvent){}
        public void dragOver(DragSourceDragEvent DragSourceDragEvent){}
        public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent){}
        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
          int index = songtraillist.getSelectedIndex();
          if (index < 0) return;
          int line = 0;
          SongStack tmpstack = null;
          SongStack siter = RapidEvolutionUI.instance.prevstack;
          while (siter != null) {
            tmpstack = new SongStack(siter.songid, tmpstack);
            siter = siter.next;
          }
          SongLinkedList lastsong = null;
          siter = tmpstack;
          boolean entered;
          while (siter != null) {
            SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
            entered = false;
            if (lastsong != null) {
              for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
                if (lastsong.mixout_songs[i] == siter.songid) {
                  entered = true;
                  if ((line == index) || ((line + 1) == index)) {
                    FileSelection transferable = new FileSelection(addsong.getFile(), addsong);
                    try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                    catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
                    return;
                  }
                  line++;
                }
              }
            }
            if (!entered) {
                if (line == index) {
                  FileSelection transferable = new FileSelection(addsong.getFile(), addsong);
                  try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                  catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
                  return;
                }
            }
            line++;
            lastsong = addsong;
            siter = siter.next;
          }
          if (RapidEvolutionUI.instance.currentsong != null) {
            entered = false;
            if (lastsong != null) {
              for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
                if (lastsong.mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) {
                  entered = true;
                  if ((line == index) || ((line + 1) == index)) {
                    FileSelection transferable = new FileSelection(RapidEvolutionUI.instance.currentsong.getFile(), RapidEvolutionUI.instance.currentsong);
                    try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                    catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
                    return;
                  }
                  line++;
                }
              }
            }
            if (!entered) {
              if (line == index) {
                FileSelection transferable = new FileSelection(RapidEvolutionUI.instance.currentsong.getFile(), RapidEvolutionUI.instance.currentsong);
                try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                catch (Exception e) { log.error("dragGestureRecognized(): error", e);return; }
                return;
              }
            }
            line++;
            lastsong = RapidEvolutionUI.instance.currentsong;
          }
          siter = RapidEvolutionUI.instance.nextstack;
          while (siter != null) {
            entered = false;
            SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
            if (lastsong != null) {
              for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
                if (lastsong.mixout_songs[i] == siter.songid) {
                  entered = true;
                  if ((line == index) || ((line + 1) == index)) {
                    FileSelection transferable = new FileSelection(addsong.getFile(), addsong);
                    try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                    catch (Exception e) { log.error("dragGestureRecognized(): error", e); return; }
                    return;
                  }
                  line++;
                }
              }
            }
            if (!entered) {
              if (line == index) {
                FileSelection transferable = new FileSelection(addsong.getFile(), addsong);
                try { dragGestureEvent.startDrag(DragSource.DefaultCopyDrop, transferable, this); }
                catch (Exception e) {log.error("dragGestureRecognized(): error", e); return; }
                return;
              }
            }
            line++;
            lastsong = addsong;
            siter = siter.next;
          }
        }
    }

    public void StartRedrawSongTrailThread() {
      new RedrawSongTrailThread().start();
    }

    int songtrailsize = 0;
    Semaphore RedrawSongTrailSem = new Semaphore(1);
    public class RedrawSongTrailThread extends Thread {
      public RedrawSongTrailThread()  { }
      public void run()  {
        RedrawSongTrailRoutine();
      }
    }

    public Vector songtraildata = null;
    public Vector songtraildatatype = null;

    public void RedrawSongTrailRoutine() {
      try {
      RedrawSongTrailSem.acquire();
      songtraildata = new Vector();
      songtraildatatype = new Vector();
      DefaultListModel dlm = (DefaultListModel) songtraillist.getModel();
      dlm.clear();
      int line = 0;
      int songs = 0;
      SongStack tmpstack = null;
      SongStack siter = RapidEvolutionUI.instance.prevstack;
      while (siter != null) {
        tmpstack = new SongStack(siter.songid, tmpstack);
        siter = siter.next;
      }
      SongLinkedList lastsong = null;
      siter = tmpstack;
      while (siter != null) {
        SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
        if (lastsong != null) {
          for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
            if (lastsong.mixout_songs[i] == siter.songid) {
              String mixstr = new String();
              mixstr += "     [";
              mixstr += String.valueOf(lastsong.getMixoutRank(i)) + "]   (";
              mixstr += new MyStringFloat(String.valueOf(lastsong.getMixoutBpmdiff(i))).toString();
              mixstr += ")   ";
              mixstr += lastsong.getMixoutComments(i);
              songtraildata.add(new MixoutObject(lastsong, i));
              songtraildatatype.add(new Integer(1));
              dlm.add(line++, mixstr);
            }
          }
        }
        songtraildata.add(addsong);
        songtraildatatype.add(new Integer(0));
        dlm.add(line++, addsong);
        songs++;
        lastsong = addsong;
        siter = siter.next;
      }
      if (RapidEvolutionUI.instance.currentsong != null) {
        if (lastsong != null) {
          for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
            if (lastsong.mixout_songs[i] == RapidEvolutionUI.instance.currentsong.uniquesongid) {
              String mixstr = new String();
              mixstr += "     [";
              mixstr += String.valueOf(lastsong.getMixoutRank(i)) + "]   (";
              mixstr += new MyStringFloat(String.valueOf(lastsong.getMixoutBpmdiff(i))).toString();
              mixstr += ")   ";
              mixstr += lastsong.getMixoutComments(i);
              songtraildata.add(new MixoutObject(lastsong, i));
              songtraildatatype.add(new Integer(1));
              dlm.add(line++, mixstr);
            }
          }
        }
        songtraildata.add(RapidEvolutionUI.instance.currentsong);
        songtraildatatype.add(new Integer(0));
        dlm.add(line++, RapidEvolutionUI.instance.currentsong);
        songs++;
        lastsong = RapidEvolutionUI.instance.currentsong;
      }
      siter = RapidEvolutionUI.instance.nextstack;
      while (siter != null) {
        SongLinkedList addsong = SongDB.instance.NewGetSongPtr(siter.songid);
        if (lastsong != null) {
          for (int i = 0; i < lastsong.getNumMixoutSongs(); ++i) {
            if (lastsong.mixout_songs[i] == siter.songid) {
              String mixstr = new String();
              mixstr += "     [";
              mixstr += String.valueOf(lastsong.getMixoutRank(i)) + "]   (";
              mixstr += new MyStringFloat(String.valueOf(lastsong.getMixoutBpmdiff(i))).toString();
              mixstr += ")   ";
              mixstr += lastsong.getMixoutComments(i);
              songtraildata.add(new MixoutObject(lastsong, i));
              songtraildatatype.add(new Integer(1));
              dlm.add(line++, mixstr);
            }
          }
        }
        songtraildata.add(addsong);
        songtraildatatype.add(new Integer(0));
        dlm.add(line++, addsong);
        songs++;
        lastsong = addsong;
        siter = siter.next;
      }
      songtrailsize = songs;
      } catch (Exception e) { }
      RedrawSongTrailSem.release();

    }

    private void setupActionListeners() {
        songtrailokbutton.addActionListener(this);
        songtrailsaveasbt.addActionListener(this);
        loadsongtrailbutton.addActionListener(this);
       clearsongtrailbutton.addActionListener(this);
       clearallsongtrailbutton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == songtrailokbutton) {
        setVisible(false);
      } else if (ae.getSource() == clearallsongtrailbutton) {
            RapidEvolutionUI.instance.nextstack = null;
            RapidEvolutionUI.instance.prevstack = null;
            RapidEvolutionUI.instance.ClearCurrentSong();
            RapidEvolutionUI.instance.suggestedsongs = new Vector();
            ExcludeUI.instance.Redrawexclude();
            RootsUI.instance.RedrawRoots();
            SuggestedMixesUI.instance.RedrawSuggested();
            SyncUI.instance.Redrawsync();
            RedrawSongTrailThread rstt = new RedrawSongTrailThread();
            rstt.start();
      } else if (ae.getSource() == songtrailsaveasbt) {
            if (songtraillist.getModel().getSize() <= 0) return;
            JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
            if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
            fc.addChoosableFileFilter(new MixFileFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showSaveDialog(getDialog());
            File tmp = fc.getSelectedFile();
            if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
              String filestr = (String)tmp.getAbsolutePath();
              SongTrailManager.saveSongTrail(SongTrailManager.getCurrentSongTrail(), filestr);              
            }
      } else if (ae.getSource() == loadsongtrailbutton) {
            JFileChooser fc = new JFileChooser();
            if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
            fc.addChoosableFileFilter(new MixFileFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);
            int returnVal = fc.showOpenDialog(getDialog());
            File tmp = fc.getSelectedFile();
            if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
              String filestr = (String)tmp.getAbsolutePath();
              try {
                  SongTrail songTrail = SongTrailManager.loadSongTrail(filestr);
                  if (songTrail != null) {
                      replacevector = new Vector();
                      for (int i = 0; i < songTrail.getNumSongs(); ++i) replacevector.insertElementAt(songTrail.getSong(i).uniquestringid, 0);
                      songtrailoadsize = songTrail.getNumSongs();
                      ScrubSongTrail(songTrail.getNumSongs() - 1);                      
                  }
              } catch (Exception e) { log.error("actionPerformed(): error", e); }
            }
      } else if (ae.getSource() == clearsongtrailbutton) {
          RapidEvolutionUI.instance.nextstack = null;
          RapidEvolutionUI.instance.prevstack = null;
          RapidEvolutionUI.instance.backbutton.setEnabled(false);
          if ((RapidEvolutionUI.instance.currentsong != null) && (RapidEvolutionUI.instance.currentsong.next != null)) RapidEvolutionUI.instance.nextbutton.
                  setEnabled(true);
          else RapidEvolutionUI.instance.nextbutton.setEnabled(false);
          RedrawSongTrailThread rstt = new RedrawSongTrailThread();
          rstt.start();
          MixoutPane.instance.RedrawMixoutTable();
      }
    }

    public int replacesongmode = 0;
    public void ReplaceMissingSong(int index) {
      if (replacesongmode == 0) {
        replacevector.set(replaceindex, ((SongLinkedList)SelectMissingSongUI.instance.selectmixxingsongsvector.get(index)).uniquestringid);
        ScrubSongTrail(replaceindex - 1);
      } else {
        //replacevector.set(replaceindex, ((SongLinkedList)selectmixxingsongsvector.get(index)).uniquesongid);
        MixGeneratorUI.instance.mixes[0][replaceindex2] = ((SongLinkedList)SelectMissingSongUI.instance.selectmixxingsongsvector.get(index));
        MixGeneratorUI.instance.ScrubSongTrail2(replacevector2, replaceindex2 + 1);
      }
    }

    public Vector replacevector = null;
    public Vector replacevector2 = null;
    public int replaceindex = -1;
    public int replaceindex2 = -1;
    public int songtrailoadsize = 0;

    public void ScrubSongTrail(int startval) {
      replacesongmode = 0;
      for (int i = startval; i >= 0; --i)
        if (SongDB.instance.OldGetSongPtr((String)replacevector.get(i)) == null) {
        replaceindex = i;
        if (SongDB.instance.OldGetSongPtr(((String)replacevector.get(i)).toLowerCase()) != null)  {
          replacevector.set(i, ((String)replacevector.get(i)).toLowerCase());
        } else {
          String missingsong = StringUtil.ReplaceString("%songid%", SkinManager.instance.getDialogMessageText("load_song_trail_missing_song"), (String)replacevector.get(i));
          int n = IOptionPane.showConfirmDialog(
              SkinManager.instance.getFrame("main_frame"),
              missingsong,
              SkinManager.instance.getDialogMessageTitle("load_song_trail_missing_song"),
              IOptionPane.YES_NO_OPTION);
          if (n != 0) return;
          SelectMissingSongUI.instance.selectmissingsongsfield.setText("");
          DefaultListModel dlm = (DefaultListModel) SelectMissingSongUI.instance.selectmissingsonglist.getModel();
          dlm.removeAllElements();
          SelectMissingSongUI.instance.selectmixxingsongsvector.removeAllElements();
          SongLinkedList iter = SongDB.instance.SongLL;
          while (iter != null) {
            dlm.addElement(iter.getSongId());
            SelectMissingSongUI.instance.selectmixxingsongsvector.add(iter);
            iter = iter.next;
          }
          SelectMissingSongUI.instance.Display();
          return;
        }
      }
      int num_songs = songtrailoadsize;
      for (int i = 0; i < num_songs; ++i) if (SongDB.instance.OldGetSongPtr((String)replacevector.get(i)) == null) return;
      RapidEvolutionUI.instance.nextstack = null;
      RapidEvolutionUI.instance.prevstack = null;
      RapidEvolutionUI.instance.currentsong = null;
      RapidEvolutionUI.instance.keylockcurrentsong.setEnabled(false);
      SearchPane.instance.bpmslider.setValue(0);
      for (int i = 0; i < num_songs - 1; ++i) RapidEvolutionUI.instance.nextstack = new SongStack(SongDB.instance.OldGetSongPtr((String)replacevector.get(i)).uniquesongid, RapidEvolutionUI.instance.nextstack);
      RapidEvolutionUI.instance.change_current_song(SongDB.instance.OldGetSongPtr((String)replacevector.get(num_songs - 1)), 0, true, true);
      RapidEvolutionUI.instance.songtrail_ui.StartRedrawSongTrailThread();
      RapidEvolutionUI.instance.backbutton.setEnabled(false);
    }
}

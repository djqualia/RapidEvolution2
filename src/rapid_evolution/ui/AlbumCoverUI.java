package rapid_evolution.ui;

import rapid_evolution.filefilters.ImageFileFilter;
import rapid_evolution.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import java.io.File;

import java.util.Iterator;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;

import com.mixshare.rapid_evolution.ui.swing.filechooser.ImagePreviewFileChooser;

public class AlbumCoverUI extends REDialog implements ActionListener {
    
    private static Logger log = Logger.getLogger(AlbumCoverUI.class);
    
    public AlbumCoverUI(String id) {
        super(id);
        _id = id;
          setupDialog();
          setupActionListeners();          
      }
    
    private String _id;
    
    public JButton okbutton = new REButton();
    public JButton retrievebutton = new REButton();
    public JButton changebutton = new REButton();
    public JPanel imagepanel = new JPanel();
        
    private void setupDialog() {
        
    }
    
    private void setupActionListeners() {
        okbutton.addActionListener(this);
        retrievebutton.addActionListener(this);
        changebutton.addActionListener(this);        
    }
    
    String previousfilepath = "";
    public boolean pathisknown = false;
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == okbutton) {
            Hide();
        } else if (ae.getSource() == changebutton) {
            JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
            ImagePreviewFileChooser preview = new ImagePreviewFileChooser(fc);
            fc.addPropertyChangeListener(preview);
            fc.setAccessory(preview);
            if (!previousfilepath.equals(""))
                fc.setCurrentDirectory(new File(previousfilepath));
            fc.addChoosableFileFilter(new ImageFileFilter());
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.setMultiSelectionEnabled(true);
            int returnVal = fc.showOpenDialog(getDialog());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File tmp = fc.getSelectedFile();
                if (tmp != null) previousfilepath = tmp.getAbsolutePath();
                File[] files = fc.getSelectedFiles();
                ImageSet imageset = new ImageSet();
                for (int i = 0; i < files.length; ++i) {
                    imageset.addFile(files[i].getAbsolutePath());
                }
                imageset.computeThumbnailFilename();
                SongDB.instance.setAlbumCoverFilename(artistname, albumname, imageset);
            }
            
            resetDisplay();       
            this.validate();
            
        } else if (ae.getSource() == retrievebutton) {
            new AlbumCoverRetrieverThread(this, artistname, albumname, pathisknown ? previousfilepath : null, true).start();
        }
    }
    
    public class AlbumCoverRetrieverThread extends Thread {
        private AlbumCoverUI ui;
        private String artistname;
        private String albumname;
        private String lookindir;
        private boolean albumcover_only;        
        public AlbumCoverRetrieverThread(AlbumCoverUI ui, String artistname, String albumname, String lookindir, boolean albumcover_only) {
            this.ui = ui;
            this.artistname = artistname;
            this.albumname = albumname;
            this.lookindir = lookindir;
            this.albumcover_only = albumcover_only;
        }        
        public void run() {
            try {
                ui.retrievebutton.setEnabled(false);
                DataRetriever.updateInfo(artistname, albumname, pathisknown ? previousfilepath : null, true);
                resetDisplay();
                ui.validate();            
            } catch (Exception e) {
                log.error("run(): error Exception", e);
            }
            ui.retrievebutton.setEnabled(true);
        }
    }
    
    private String albumname;
    private String artistname;
    
    public void showAlbum(String albumname, String artistname) {
        if (isVisible()) return;
        this.albumname = albumname;
        this.artistname = artistname;
        resetDisplay();
    }
    
    private void resetDisplay() {
        ImageSet albumcover_imageset = SongDB.instance.getAlbumCoverImageSet(artistname, albumname);
        
        imagepanel.removeAll();
        //RelativeLayout layout = (RelativeLayout)imagepanel.getLayout();

        if (albumcover_imageset != null) {
            String[] filenames = albumcover_imageset.getFiles();
        String lastname = null;
        for (int i = 0; i < filenames.length; ++i) {
            String albumcover_filename = filenames[i];
            if (albumcover_filename != null) {
                ImageIcon icon = new ImageIcon(albumcover_filename) ;        
                JLabel imagelabel = new JLabel(icon);
                imagelabel.setToolTipText(albumcover_filename);
//                if (lastname == null) {
                    //imagepanel.add(imagelabel, albumcover_filename);
                    //layout.addConstraint(albumcover_filename, AttributeType.HORIZONTAL_CENTER, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, 0));
                    //layout.addConstraint(albumcover_filename, AttributeType.TOP, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.TOP, 15));
                //} else {
                    //imagepanel.add(imagelabel, albumcover_filename);
                    //layout.addConstraint(albumcover_filename, AttributeType.HORIZONTAL_CENTER, new AttributeConstraint(DependencyManager.ROOT_NAME, AttributeType.HORIZONTAL_CENTER, 0));
                    //layout.addConstraint(albumcover_filename, AttributeType.TOP, new AttributeConstraint(lastname, AttributeType.BOTTOM, 100));                    
                //}
                imagepanel.add(imagelabel);
                lastname = albumcover_filename;
            }
        }                
        }
        
        
        //int width = icon.getIconWidth() + 85;
        //if (width < 100) width = 150;
        //int height = icon.getIconHeight() + 115;
        //if (height < 100) height = 150;                       
        //imagepanel.setSize(600, 6000);
        
        String albumid = SongDB.getAlbumID(artistname, albumname);
        if (StringUtil.isLowerCase(artistname) && StringUtil.isLowerCase(albumname)) {
            albumid = albumid.toLowerCase();
        }
        this.setTitle(albumid);
        this.Display();        
    }
      
      
}

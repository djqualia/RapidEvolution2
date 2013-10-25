package rapid_evolution;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.filefilters.ImageFileFilter;
import rapid_evolution.threads.ErrorMessageThread;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.util.OSHelper;
import rapid_evolution.util.URLUtil;

import com.chango.data.discogs.common.LabelInstance;
import com.mixshare.rapid_evolution.data.retriever.discogs.DiscogsAPIWrapper;
import com.mixshare.rapid_evolution.data.retriever.discogs.DiscogsRelease;

public class DataRetriever {

    private static Logger log = Logger.getLogger(DataRetriever.class);

    public static void updateInfo(String artist, String album, String lookindir, boolean albumcover_only) {
        updateInfo(null, artist, album, lookindir, albumcover_only);
    }

    public static void updateInfo(SongLinkedList song, boolean albumcover_only) {
        updateInfo(song, song.getArtist(), song.getAlbum(), FileUtil.getDirectoryFromFilename(song.getRealFileName()), albumcover_only);
    }

    private static void updateInfo(SongLinkedList song, String artist, String album, String lookindir, boolean albumcover_only) {
        if (log.isDebugEnabled())
            log.debug("updateInfo(): " + song + ", artist: " + artist
                    + ", album: " + album);
        try {
            artist = artist.toLowerCase();
            album = album.toLowerCase();
            if ((artist == null) || artist.equals(""))
                artist = "Various ";
            if (artist.equals("") || album.equals(""))
                return;
            ImageSet imageset = SongDB.instance.getAlbumCoverImageSet(artist, album);
            if (imageset != null) {
                if (!imageset.isValid()) {
                    if (log.isDebugEnabled())
                        log.debug("updateInfo(): detected missing album cover information, removing");
                    SongDB.instance.removeAlbumCoverImageSet(artist, album);
                    imageset = null;
                }
            }
            if (imageset == null) {
                try {
                    if (lookindir != null) {
                        imageset = new ImageSet();
                        String directory = lookindir;
                        if (directory != null) {
                            File dir = new File(directory);
                            File files[] = dir.listFiles();
                            files = FileUtil.sortfiles(files);
                            for (int i = 0; i < files.length; ++i) {
                                File file = files[i];
                                String filename = file.getAbsolutePath();
                                if (ImageFileFilter.isImage(filename)) {
                                    imageset.addFile(filename);
                                }
                            }
                        }
                        if (imageset.getNumFile() > 0) {
                            imageset.computeThumbnailFilename();
                            SongDB.instance.setAlbumCoverFilename(artist,
                                    album, imageset);
                        } else {
                            imageset = null;
                        }
                    }

                } catch (Exception e2) {
                    log.error("updateInfo(): error", e2);
                }
            }

            if (!(albumcover_only && (imageset != null))) {

                String searchArtist = null;
                String searchReleaseTitle = null;                
                if (SongLinkedList.isCompilationAlbum(artist, album)) {
                	searchArtist = "Various";
                	searchReleaseTitle = album;
                } else {
                	searchArtist = artist;
                	searchReleaseTitle = album;
                }
                String search_string = searchArtist + " " + searchReleaseTitle;
                Vector strip_list = new Vector();
                strip_list.add(" ep");
                strip_list.add(" lp");
                strip_list.add(" cds");
                strip_list.add(" - disc 1");
                strip_list.add(" - disc 2");
                strip_list.add(" - disc 3");
                strip_list.add(" - disc 4");
                strip_list.add(" - cd1");
                strip_list.add(" - cd2");
                strip_list.add(" - cd3");
                strip_list.add(" - cd4");
                for (int i = 0; i < strip_list.size(); ++i) {
                    String remove = (String) strip_list.get(i);
                    if (searchReleaseTitle.toLowerCase().endsWith(remove)) {
                    	searchReleaseTitle = searchReleaseTitle.substring(0,
                    			searchReleaseTitle.length() - remove.length());
                    }
                }
                
                // insert new code here...
                if (log.isDebugEnabled())
                	log.debug("updateInfo(): searchArtist=" + searchArtist + ", searchReleaseTitle=" + searchReleaseTitle);
                Integer releaseID = DiscogsAPIWrapper.searchForReleaseId(searchArtist, searchReleaseTitle);
                if (releaseID != null) {
                	DiscogsRelease release = DiscogsAPIWrapper.getRelease(releaseID);
                	
                    OldSongValues old_values = new OldSongValues(song);
                    if (SongDB.instance.getAlbumCoverImageSet(artist, album) == null) {
                        if (!release.getPrimaryImageURL().equals("")) {
                            String albumcover_filename = StringUtil
                                    .encodeString(search_string)
                                    + "_thumbnail";
                            if (!albumcover_filename.equals("")) {
                                String extension = FileUtil
                                        .getExtension(release.getPrimaryImageURL());
                                int index = 1;
                                String filename = OSHelper.getWorkingDirectory() + "/albumcovers/"
                                        + albumcover_filename + "." + extension;
                                while (FileUtil.fileAlreadyExists(filename)) {
                                    index++;
                                    filename = OSHelper.getWorkingDirectory() + "/albumcovers/"
                                            + albumcover_filename + index + "."
                                            + extension;
                                }
                                imageset = new ImageSet(filename);
                                if (URLUtil.saveImage(release.getPrimaryImageURL(),
                                        filename)) {
                                    SongDB.instance.setAlbumCoverFilename(
                                            artist, album, imageset);
                                    readMoreImages(
                                            release,
                                            imageset, search_string);
                                }
                            }
                        }
                    }
                    if (song != null) {
                        if (!albumcover_only) {
                            if (release.getLabels().size() > 0) {
                                if (OptionsUI.instance.custom_field_1_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_publisher"))) {
                                    if (song.getUser1().equals(""))
                                        song.setUser1((String)release.getLabels().get(0));
                                }
                                if (OptionsUI.instance.custom_field_2_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_publisher"))) {
                                    if (song.getUser2().equals(""))
                                        song.setUser2((String)release.getLabels().get(0));
                                }
                                if (OptionsUI.instance.custom_field_3_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_publisher"))) {
                                    if (song.getUser3().equals(""))
                                        song.setUser3((String)release.getLabels().get(0));
                                }
                                if (OptionsUI.instance.custom_field_4_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_publisher"))) {
                                    if (song.getUser4().equals(""))
                                        song.setUser4((String)release.getLabels().get(0));
                                }
                            }
                            if (release.getLabelInstances().size() > 0) {
                                if (OptionsUI.instance.custom_field_1_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_catalogid"))) {
                                    if (song.getUser1().equals(""))
                                        song.setUser1(((LabelInstance)release.getLabelInstances().get(0)).getCatalogId());
                                }
                                if (OptionsUI.instance.custom_field_2_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_catalogid"))) {
                                    if (song.getUser2().equals(""))
                                        song.setUser2(((LabelInstance)release.getLabelInstances().get(0)).getCatalogId());
                                }
                                if (OptionsUI.instance.custom_field_3_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_catalogid"))) {
                                    if (song.getUser3().equals(""))
                                        song.setUser3(((LabelInstance)release.getLabelInstances().get(0)).getCatalogId());
                                }
                                if (OptionsUI.instance.custom_field_4_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_catalogid"))) {
                                    if (song.getUser4().equals(""))
                                        song.setUser4(((LabelInstance)release.getLabelInstances().get(0)).getCatalogId());
                                }
                            }
                            if (release.getYearReleased() != null) {
                                if (OptionsUI.instance.custom_field_1_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_year"))) {
                                    if (song.getUser1().equals(""))
                                        song
                                                .setUser1(FormatDate(String.valueOf(release.getYearReleased())));
                                }
                                if (OptionsUI.instance.custom_field_2_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_year"))) {
                                    if (song.getUser2().equals(""))
                                        song
                                                .setUser2(FormatDate(String.valueOf(release.getYearReleased())));
                                }
                                if (OptionsUI.instance.custom_field_3_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_year"))) {
                                    if (song.getUser3().equals(""))
                                        song
                                                .setUser3(FormatDate(String.valueOf(release.getYearReleased())));
                                }
                                if (OptionsUI.instance.custom_field_4_tag_combo
                                        .getSelectedItem()
                                        .equals(
                                                SkinManager.instance
                                                        .getMessageText("custom_field_id3_tag_year"))) {
                                    if (song.getUser4().equals(""))
                                        song
                                                .setUser4(FormatDate(String.valueOf(release.getYearReleased())));
                                }
                            }
                            Vector uniqueStyles = release.getUniqueStylesAndGenres();
                            if (uniqueStyles.size() > 0) {
                                for (int i = 0; i < uniqueStyles.size(); ++i) {
                                    String stylename = (String)uniqueStyles.get(i);
                                    StyleLinkedList styleiter = SongDB.instance.masterstylelist;
                                    boolean found = false;
                                    while (styleiter != null) {
                                        if (StringUtil.areStyleNamesEqual(
                                                styleiter.getName(), stylename)) {
                                            found = true;
                                            if (!styleiter.containsDirect(song)) {
                                                styleiter.insertSong(song);
                                            }
                                        }
                                        styleiter = styleiter.next;
                                    }
                                    if (!found) {
                                        styleiter = SongDB.instance.addStyle(
                                                stylename, false);
                                        styleiter.insertSong(song);
                                    }
                                }
                            }
                        }
                        SongDB.instance.UpdateSong(song, old_values);
                    }
                	
                }
                
            }
        } catch (Exception e) {
            log.error("updateInfo(): error", e);
            new ErrorMessageThread(
                    SkinManager.instance
                            .getDialogMessageTitle("album_cover_retrieval_error"),
                    SkinManager.instance
                            .getDialogMessageText("album_cover_retrieval_error")
                            + e.getMessage()).start();
        }
    }



    private static void readMoreImages(DiscogsRelease release, ImageSet imageset, String search_string) {
        try {
        	for (int i = 0; i < release.getImageURLs().size(); ++i) {
                String imageurl = (String)release.getImageURLs().get(i);
                if (!imageurl.equals(release.getPrimaryImageURL())) {
	                log.debug("readMoreImages(): additional album cover url="
	                                + imageurl);
	                String albumcover_filename = StringUtil
	                        .encodeString(search_string);
	                if (!albumcover_filename.equals("")) {
	                    String extension = FileUtil
	                            .getExtension(imageurl);
	                    int index2 = 1;
	                    String filename = OSHelper.getWorkingDirectory() + "/albumcovers/"
	                            + albumcover_filename + "."
	                            + extension;
	                    while (FileUtil.fileAlreadyExists(filename)) {
	                        index2++;
	                        filename = OSHelper.getWorkingDirectory() + "/albumcovers/"
	                                + albumcover_filename + index2
	                                + "." + extension;
	                    }
	                    if (URLUtil.saveImage(imageurl, filename)) {
	                        imageset.addFile(filename);
	                    }
	                }
                }
        	}
        } catch (Exception e) {
            log.error("readMoreImages(): error", e);
        }
    }

    private static String FormatDate(String input) {
        if (input == null)
            return null;
        if (input.length() > 4)
            return input.substring(input.length() - 4, input.length());
        return input;
    }

}
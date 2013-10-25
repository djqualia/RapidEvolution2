package com.mixshare.rapid_evolution.audio.tags.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import rapid_evolution.ImageSet;
import rapid_evolution.SongDB;
import rapid_evolution.StringUtil;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.audio.AudioLib;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.audio.tags.TagReader;
import com.mixshare.rapid_evolution.audio.tags.TagReaderFactory;
import com.mixshare.rapid_evolution.music.Key;

public class TagReaderThread extends Thread {

    private static Logger log = Logger.getLogger(TagReaderThread.class);    
    
    private String filename = null;
    private SongLinkedList result = null;
    
    public TagReaderThread(String filename) {
        this.filename = filename;
    }
    
    public void run() {
        SongLinkedList song = new SongLinkedList();
        try {            
            TagReader tag_reader = TagReaderFactory.getTagReader(filename);                        
            if (tag_reader != null) {
                
                // album:
                try {
                    song.setAlbum(StringUtil.cleanString(tag_reader.getAlbum()));
                } catch (Exception e) {
                    log.error("readTags(): error reading album", e);
                }
                                
                // artist:
                try {
                    song.setArtist(StringUtil.cleanString(tag_reader.getArtist()));
                } catch (Exception e) {
                    log.error("readTags(): error reading artist", e);
                }
                
                // beat intensity:
                try {
                    Integer intensity = tag_reader.getBeatIntensity();
                    if (intensity != null) song.setBeatIntensity(intensity.intValue());
                } catch (Exception e) {
                    log.error("readTags(): error reading beat intensity", e);
                }
                
                // bpm accurcy:
                try {
                    Integer accuracy = tag_reader.getBpmAccuracy();
                    if (accuracy != null) song.setBpmAccuracy(accuracy.intValue());
                } catch (Exception e) {
                    log.error("readTags(): error reading bpm accuracy", e);
                }
                
                // bpm start:
                try {
                    Float bpm_start = tag_reader.getBpmStart();
                    if (bpm_start != null) song.setStartbpm(bpm_start.floatValue());
                } catch (Exception e) {
                    log.error("readTags(): error reading start bpm", e);
                }

                // bpm end:
                try {
                    Float bpm_end = tag_reader.getBpmEnd();
                    if (bpm_end != null) song.setEndbpm(bpm_end.floatValue());
                } catch (Exception e) {
                    log.error("readTags(): error reading end bpm", e);
                }
                
                // comments
                try {
                    String comments = tag_reader.getComments();
                    if (OptionsUI.instance.writekeytocomments.isSelected() && (comments != null)) {
                        int index = comments.indexOf("-");
                        if (index > 0) {
                            String before = comments.substring(0, index).trim();
                            int index2 = before.indexOf(",");
                            if (index2 > 0) {
                                String before1 = before.substring(0, index2).trim();
                                String before2 = before.substring(index2 + 1).trim();
                                if (Key.isValid(before1) && Key.isValid(before2)) {
                                    comments = comments.substring(index + 1).trim();
                                    if (!song.getStartKey().isValid())
                                        song.setStartkey(Key.getPreferredKeyNotation(before1));
                                }                               
                            }
                        } else {
                            int index2 = comments.indexOf(",");
                            if (index2 > 0) {
                                String before1 = comments.substring(0, index2).trim();
                                String before2 = comments.substring(index2 + 1).trim();
                                if (Key.isValid(before1) && Key.isValid(before2)) {
                                    comments = "";
                                    if (!song.getStartKey().isValid())
                                        song.setStartkey(Key.getPreferredKeyNotation(before1));
                                }                               
                            }                           
                        }
                    }                    
                    song.setComments(StringUtil.cleanString(comments));
                } catch (Exception e) {
                    log.error("readTags(): error reading comments", e);
                }
                
                // key accurcy:
                try {
                    Integer accuracy = tag_reader.getKeyAccuracy();
                    if (accuracy != null) song.setKeyAccuracy(accuracy.intValue());
                } catch (Exception e) {
                    log.error("readTags(): error reading key accuracy", e);
                }
                
                // start key:
                try {
                    String key = Key.getPreferredKeyNotation(StringUtil.cleanString(tag_reader.getKeyStart()));
                    if (!key.equals(""))
                        song.setStartkey(key);
                } catch (Exception e) {
                    log.error("readTags(): error reading start key", e);
                }
                
                // end key:
                try {
                    song.setEndkey(Key.getPreferredKeyNotation(StringUtil.cleanString(tag_reader.getKeyEnd())));   
                } catch (Exception e) {
                    log.error("readTags(): error reading end key", e);
                }
                
                // genre:
                HashMap style_map = new HashMap();                
                try {
                    String genre = StringUtil.cleanString(tag_reader.getGenre());
                    if (genre != null) {
                        if (!OptionsUI.instance.createstylesfromgenretags.isSelected()) {
                            // only use existing styles
                            boolean exists = false;
                            StyleLinkedList siter = SongDB.instance.masterstylelist;
                            while (!exists && (siter != null)) {
                                if (genre.equalsIgnoreCase(siter.getName())) {
                                    exists = true;
                                    style_map.put(genre.toLowerCase(), genre);
                                }
                                siter = siter.next;
                            }                           
                        } else {
                            style_map.put(genre.toLowerCase(), genre);
                        }
                    }
                } catch (Exception e) {
                    log.error("readTags(): error reading genre", e);
                }                 
                
                // rating:
                try {
                    Integer rating = tag_reader.getRating();
                    if (rating != null)
                        song.setRating((char)rating.intValue());
                } catch (Exception e) {
                    log.error("readTags(): error reading rating", e);
                }
                
                // remix:
                try {
                    song.setRemixer(StringUtil.cleanString(tag_reader.getRemix()));
                } catch (Exception e) {
                    log.error("readTags(): error reading remix", e);
                }                    
                
                // replay gain:
                try {
                    Float replayGain = tag_reader.getReplayGain();
                    if (replayGain != null) {
                        song.setRGA(replayGain.floatValue());
                    }
                } catch (Exception e) {
                    log.error("readTags(): error reading replay gain", e);
                }                    

                // styles:
                try {
                    if (!OptionsUI.instance.createstylesfromgenretags.isSelected()) {
                        // only use existing styles
                        String[] styles = tag_reader.getStyles();
                        if (styles != null) {
                            // clear genre if styles exist
                            style_map.clear();
                            for (int s = 0; s < styles.length; ++s) {
                                String style = StringUtil.cleanString(styles[s]);
                                boolean exists = false;
                                StyleLinkedList siter = SongDB.instance.masterstylelist;
                                while (!exists && (siter != null)) {
                                    if (style.equalsIgnoreCase(siter.getName())) {
                                        exists = true;
                                        style_map.put(style.toLowerCase(), style);
                                    }
                                    siter = siter.next;
                                }
                            }
                        }
                    } else {
                        String[] styles = tag_reader.getStyles();
                        if (styles != null) {
                            // clear genre if styles exist
                            //style_map.clear();
                            for (int s = 0; s < styles.length; ++s) {
                                String style = StringUtil.cleanString(styles[s]);
                                style_map.put(style.toLowerCase(), style);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("readTags(): error reading styles", e);
                }          

                Collection styles = style_map.values();
                if (styles != null) {
                    song.stylelist = new String[styles.size()];
                    Iterator iter = styles.iterator();
                    int count = 0;
                    while (iter.hasNext()) {
                        song.stylelist[count++] = (String)iter.next();
                    }
                }
                
                // time
                try {
                    String time = StringUtil.cleanString(tag_reader.getTime());
                    if (!StringUtil.isValid(time)) {
                        double track_time = AudioLib.get_track_time(filename);
                        time = StringUtil.seconds_to_time((int)track_time);
                    }
                    song.setTime(time);
                } catch (Exception e) {
                    log.error("readTags(): error reading time", e);
                }                                
                
                // time signature
                try {
                    String time_sig = StringUtil.cleanString(tag_reader.getTimeSignature());
                    song.setTimesig(time_sig);
                } catch (Exception e) {
                    log.error("readTags(): error reading time signature", e);
                }
                
                // track:
                try {
                    String track = StringUtil.cleanString(tag_reader.getTrack());
                    if ((track.length() == 1) && ((track.charAt(0) >= '1') && (track.charAt(0) <= '9'))) {
                        track = "0" + track;
                    }
                    Integer total_tracks = tag_reader.getTotalTracks();
                    if ((total_tracks != null) && (total_tracks.intValue() > 0)) {
                        if (!track.endsWith("/" + String.valueOf(total_tracks))) {
                            if ((total_tracks.intValue() >= 1) && (total_tracks.intValue() <= 9))
                                track += "/0" + String.valueOf(total_tracks);
                            else
                                track += "/" + String.valueOf(total_tracks);
                        }
                    }
                    song.setTrack(track);
                } catch (Exception e) {
                    log.error("readTags(): error reading track", e);
                }
                
                // title:
                try {
                    String title = StringUtil.cleanString(tag_reader.getTitle());
                    if (OptionsUI.instance.automatically_parse_title_field.isSelected() || OptionsUI.instance.writekeytotitle.isSelected()) {
                        int index = title.indexOf("-");
                        if (index > 0) {
                            String before = title.substring(0, index).trim();
                            int before_int = -1;
                            try {
                                before_int = Integer.parseInt(before);
                            } catch (Exception e) { }
                            if (before_int > 0) {
                                title = title.substring(index + 1).trim();
                                if (song.getTrack().equals(""))
                                    song.setTrack(before);
                            }
                            if (Key.isWellFormed(before)) {
                                title = title.substring(index + 1).trim();
                                if (!song.getStartKey().isValid())
                                    song.setStartkey(before);
                            }
                        }
                    }
                    song.setSongname(title);
                } catch (Exception e) {
                    log.error("readTags(): error reading title", e);
                }                                    
                
                // user1:
                try {
                    song.setUser1(StringUtil.cleanString(readCustomField(tag_reader, (String)OptionsUI.instance.custom_field_1_tag_combo.getSelectedItem(), 1)));
                } catch (Exception e) {
                    log.error("readTags(): error reading user1", e);
                }

                // user2:
                try {
                    song.setUser2(StringUtil.cleanString(readCustomField(tag_reader, (String)OptionsUI.instance.custom_field_2_tag_combo.getSelectedItem(), 2)));
                } catch (Exception e) {
                    log.error("readTags(): error reading user2", e);
                }
                    
                // user3:
                try {
                    song.setUser3(StringUtil.cleanString(readCustomField(tag_reader, (String)OptionsUI.instance.custom_field_3_tag_combo.getSelectedItem(), 3)));
                } catch (Exception e) {
                    log.error("readTags(): error reading user3", e);
                }
                    
                // user4:
                try {
                    song.setUser4(StringUtil.cleanString(readCustomField(tag_reader, (String)OptionsUI.instance.custom_field_4_tag_combo.getSelectedItem(), 4)));
                } catch (Exception e) {
                    log.error("readTags(): error reading user4", e);
                }
                
                // album cover:
                try {
                    String albumcover_filename = tag_reader.getAlbumCoverFilename();
                    log.trace("readTags() read album cover=" + albumcover_filename);
                    if (albumcover_filename != null) {
                        ImageSet image_set = SongDB.instance.getAlbumCoverImageSet(song.getArtist(), song.getAlbum());
                        log.trace("readTags() existing image set=" + image_set);
                        if (image_set == null) {
                            image_set = new ImageSet();
                            image_set.addFile(albumcover_filename);
                            image_set.setThumbnailFilename(albumcover_filename);
                            SongDB.instance.setAlbumCoverFilename(song.getArtist(), song.getAlbum(), image_set);
                        } else {                            
                            //image_set.addFile(albumcover_filename);
                        }
                    }
                } catch (Exception e) {
                    log.error("readTags(): error reading album cover", e);
                }                
            }

            if (song.getRemixer().equals("") && !song.getSongname().equals("")) {
                song.setRemixer(StringUtil.parseRemix(song.getSongname()));
                song.setSongname(StringUtil.parseTitle(song.getSongname()));
            }
            
            if (OptionsUI.instance.usefilenameastag.isSelected()) {
                File file = new File(filename);
                String scrubbed_filename = StringUtil.remove_underscores(StringUtil.ScrubFileType(file.getName()));
                if (song.getSongname().equals(""))
                    song.setSongname(scrubbed_filename);
            }
            
            if (OptionsUI.instance.automatically_parse_title_field.isSelected()) {
                String scrubbed_filename = song.getSongname();
                String artist = "";
                String album = "";
                String title = "";
                String remix = "";
                int divider_index = scrubbed_filename.indexOf(" - ");
                if (divider_index > 0) {
                    artist = scrubbed_filename.substring(0, divider_index);
                    title = scrubbed_filename.substring(divider_index + 3);
                } else {
                    title = scrubbed_filename;
                }
                scrubbed_filename = title;
                divider_index = scrubbed_filename.indexOf(" - ");
                if (divider_index > 0) {
                    album = scrubbed_filename.substring(0, divider_index);
                    title = scrubbed_filename.substring(divider_index + 3);
                }
                remix = StringUtil.parseRemix(title);
                title = StringUtil.parseTitle(title);
                boolean replaced_artist = false;
                boolean replaced_album = false;
                boolean replaced_remixer = false;
                if (song.getArtist().equals("") && !artist.equals("")) {
                    replaced_artist = true;
                    song.setArtist(artist);
                }
                if (song.getAlbum().equals("") && !album.equals("")) {
                    replaced_album = true;
                    song.setAlbum(album);
                }
                if (song.getRemixer().equals("") && !remix.equals("")) {
                    replaced_remixer = true;
                    song.setRemixer(remix);                                    
                }
                if (replaced_artist && replaced_album && replaced_remixer) {
                    song.setSongname(title);                        
                } else if (replaced_artist && replaced_album && !replaced_remixer) {
                    if (!remix.equals(""))
                        song.setSongname(title + " (" + remix + ")");
                    else if (song.getRemixer().equalsIgnoreCase(remix))
                        song.setSongname(title);
                } else if (replaced_artist && !replaced_album && replaced_remixer) {
                    if (!album.equals(""))
                        song.setSongname(album + " - " + title);
                    else if (song.getAlbum().equalsIgnoreCase(album))
                        song.setSongname(title);
                } else if (!replaced_artist && replaced_album && replaced_remixer) {
                    if (!artist.equals(""))
                        song.setSongname(artist + " - " + title);
                    else if (song.getArtist().equalsIgnoreCase(artist))
                        song.setSongname(title);
                } else if (replaced_artist && !replaced_album && !replaced_remixer) {
                    if (!album.equals("") && !remix.equals(""))
                        song.setSongname(album + " - " + title + " (" + remix + ")");
                    else if (album.equals("") && !remix.equals(""))
                        song.setSongname(title + " (" + remix + ")");
                   else if (!album.equals("") && remix.equals(""))
                       song.setSongname(album + " - " + title);
                   else
                       song.setSongname(title);                            
                } else if (!replaced_artist && !replaced_album && replaced_remixer) {
                    if (!album.equals("") && !artist.equals(""))
                        song.setSongname(artist + " - " + album + " - " + title);
                    else if (album.equals("") && !artist.equals(""))
                        song.setSongname(artist + " - " + title);
                   else if (!album.equals("") && artist.equals(""))
                       song.setSongname(album + " - " + title);
                   else
                       song.setSongname(title);
                } else if (!replaced_artist && replaced_album && !replaced_remixer) {
                    if (!artist.equals("") && !remix.equals(""))
                        song.setSongname(artist + " - " + title + " (" + remix + ")");
                    else if (artist.equals("") && !remix.equals(""))
                        song.setSongname(title + " (" + remix + ")");
                   else if (!artist.equals("") && remix.equals(""))
                       song.setSongname(artist + " - " + title);
                   else
                       song.setSongname(title);                            
                } else {
                    // didn't replace anything
                }
                if (title.startsWith("[")) {
                    int endIndex = title.indexOf("] - ");
                    if (endIndex > 0) {
                        String track = title.substring(1, endIndex);
                        title = title.substring(endIndex + 4);
                        song.setTrack(track);
                        song.setSongname(title);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("run(): error Exception", e);
        }
        result = song;
    }
    
    public SongLinkedList getResult() { return result; }
    
    private static String readCustomField(TagReader tag_reader, String customType, int custom_field) {
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_none"))) {
            if (custom_field == 1) return tag_reader.getUser1();
            else if (custom_field == 2) return tag_reader.getUser2();
            else if (custom_field == 3) return tag_reader.getUser3();
            else if (custom_field == 4) return tag_reader.getUser4();
            else {
                log.error("readCustomField(): unknown custom field=" + custom_field);
            }            
        }
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_content_group_description"))) return tag_reader.getContentGroupDescription();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_content_type"))) return tag_reader.getContentType();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_encoded_by"))) return tag_reader.getEncodedBy();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_file_type"))) return tag_reader.getFileType();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_languages"))) return tag_reader.getLanguages();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_lyrics"))) return tag_reader.getLyrics();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_publisher"))) return tag_reader.getPublisher();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_catalogid"))) return tag_reader.getCatalogId();
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_byte_size"))) {
            Integer size = tag_reader.getSizeInBytes();
            if (size != null) return String.valueOf(size.intValue());
        }
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_year"))) return tag_reader.getYear();
        return null;        
    }
    
}

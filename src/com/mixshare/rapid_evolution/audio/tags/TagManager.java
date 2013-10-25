package com.mixshare.rapid_evolution.audio.tags;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.ImageSet;
import rapid_evolution.SongDB;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StyleLinkedList;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;

import com.mixshare.rapid_evolution.audio.tags.util.TagReaderThread;
import com.mixshare.rapid_evolution.io.FileLockManager;
import com.mixshare.rapid_evolution.music.Key;

public class TagManager {
    
    private static Logger log = Logger.getLogger(TagManager.class);
                    
    private static long readTagMaxTime = 1000 * 15; // 15 seconds
    
    public static SongLinkedList readTags(String filename) {
        SongLinkedList song = null;
        try {
        	FileLockManager.startFileRead(filename);
            TagReaderThread readerThread = new TagReaderThread(filename);
            readerThread.start();
            boolean done = false;
            song = readerThread.getResult();
            long startTime = System.currentTimeMillis();            
            while (!done && (song == null)) {
                Thread.sleep(250);
                song = readerThread.getResult();
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > readTagMaxTime)
                    done = true;
            }
        } catch (java.lang.OutOfMemoryError e) {
            log.error("readTags(): error out of memory", e);
        } catch (Exception e) {
            log.error("readTags(): error Exception", e);
        }
        FileLockManager.endFileRead(filename);
        if (song == null)
            song = new SongLinkedList();                            
        return song;    
    }

    public static boolean writeTags(SongLinkedList song) {
        return writeTags(song, null, null, null);
    }    
    public static boolean writeTags(SongLinkedList song, String filename, String[] song_styles, String genre) {
        boolean success = false;
        try {
            if (log.isDebugEnabled())
                log.debug("writeTags(): song=" + song + ", filename=" + filename);            
            if (filename == null) filename = song.getRealFileName();
        	FileLockManager.startFileWrite(filename);
            TagWriter tag_writer = TagWriterFactory.getTagWriter(filename);
            if (tag_writer != null) {

                if (log.isDebugEnabled())
                    log.debug("writeTags(): tag_writer_class=" + tag_writer.getClass());
                
                // album:
                try {
                    if (OptionsUI.instance.tagwritealbum.isSelected() && (!song.getAlbum().equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setAlbum(song.getAlbum());
                } catch (Exception e) {
                    log.debug("writeTags(): error writing album", e);
                }
                
                // album cover:
                try {
                    if (OptionsUI.instance.tagwritingalbumcover.isSelected() && (!song.getAlbum().equals("") || OptionsUI.instance.tagswriteempty.isSelected())) {
                        ImageSet imageset = SongDB.instance.getAlbumCoverImageSet(song);
                        if (imageset != null) {
                            String image_filename = imageset.getThumbnailFilename();
                            File file = new File(image_filename);
                            if (file.exists()) {
                                tag_writer.setAlbumCover(image_filename, song.getAlbum());
                            }                    
                        }
                    }                
                } catch (Exception e) {
                    log.debug("writeTags(): error writing album cover", e);
                }                    
                
                // artist:
                try {
                    if (OptionsUI.instance.tagwriteartist.isSelected() && (!song.getArtist().equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setArtist(song.getArtist());
                } catch (Exception e) {
                    log.debug("writeTags(): error writing artist", e);
                }
                
                // beat intensity:
                try {
                    if (OptionsUI.instance.tagwritebpm.isSelected() && ((song.getBeatIntensity() != 0) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setBeatIntensity(song.getBeatIntensity());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing beat intensity", e);
                }
                
                // bpm:
                try {
                    int bpm = (int)song.getStartbpm();
                    //if (bpm == 0) bpm = (int)song.getEndbpm();
                    if (OptionsUI.instance.tagwritebpm.isSelected() && ((bpm != 0) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setBpm(bpm);
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing bpm", e);
                }
                
                // bpm float
                if (OptionsUI.instance.tagswritebpmdecimals.isSelected()) {
                    float bpm = song.getStartbpm();
                    if (OptionsUI.instance.tagwritebpm.isSelected() && ((bpm != 0.0f) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setBpmFloat(bpm);
                    }                    
                }
                
                // bpm accuracy:
                try {
                    if (OptionsUI.instance.tagwritebpm.isSelected() && ((song.getBpmAccuracy() != 0) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setBpmAccuracy(song.getBpmAccuracy());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing bpm accuracy", e);
                }
                
                // bpm start:
                try {
                    if (OptionsUI.instance.tagwritebpm.isSelected() && ((song.getStartbpm() != 0) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setBpmStart(song.getStartbpm());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing start bpm", e);
                }

                // bpm end:
                try {
                    if (OptionsUI.instance.tagwritebpm.isSelected() && ((song.getEndbpm() != 0) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setBpmEnd(song.getEndbpm());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing end bpm", e);
                }
                
                // comments:
                try {
                    String comments = song.getComments();
                    if (OptionsUI.instance.writekeytocomments.isSelected()) {
                        String key_comments = Key.getKeyDescription(song);
                        if (key_comments.length() > 0) {
                            if (!song.getComments().equals(""))
                                comments = key_comments + " - " + song.getComments();
                            else
                                comments = key_comments;    
                        }
                    }
                    if (OptionsUI.instance.tagwritecomments.isSelected() && (!comments.equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setComments(comments);
                } catch (Exception e) {
                    log.debug("writeTags(): error writing comments", e);
                }
                
                // genre:
                try {
                    if (genre == null) genre = song.getGenre();
                    if (genre != null) {
                        if (OptionsUI.instance.tagwritegenre.isSelected() && (!genre.equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                            tag_writer.setGenre(genre);
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing genre", e);
                }
                
                // group tag:
                try {
                    String group_key_tag = song.getStartKey().getGroupSSLString();
                    if (OptionsUI.instance.tagwritekeytogroupingtag.isSelected() && (!group_key_tag.equals("") || OptionsUI.instance.tagswriteempty.isSelected())) {
                        String group_key_end_tag = song.getEndKey().getGroupSSLString();
                        if (!group_key_end_tag.equals("") && !group_key_end_tag.equals(group_key_tag))
                        	tag_writer.setContentGroupDescription(group_key_tag + " -> " + group_key_end_tag);
                        else 
                        	tag_writer.setContentGroupDescription(group_key_tag);
                    }
                    
                } catch (Exception e) {
                    log.debug("writeTags(): error writing group tag", e);
                }
                
                // key:
                try {
                    Key key = song.getStartKey();
                    String keyStr = key.getID3SafeKeyNotation();
                    if (OptionsUI.instance.writekeycodes.isSelected())
                        keyStr = key.getKeyCode().toString();
                    //if (keyStr.equals("")) keyStr = new Key(song.getEndkey()).getID3SafeKeyNotation();
                    if (OptionsUI.instance.tagwritekey.isSelected() && (!keyStr.equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setKey(keyStr);
                } catch (Exception e) {
                    log.debug("writeTags(): error writing key", e);
                }
                
                // key accuracy:
                try {
                    if (OptionsUI.instance.tagwritekey.isSelected() && ((song.getKeyAccuracy() != 0) || OptionsUI.instance.tagswriteempty.isSelected())) {
                        tag_writer.setKeyAccuracy(song.getKeyAccuracy());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing key accuracy", e);
                }
                
                // key start:
                try {
                    String keyStr = null;
                    if (OptionsUI.instance.writekeycodes.isSelected()) {
                        keyStr = song.getStartKey().getKeyCode().toString();
                    } else {
                        keyStr = song.getStartKey().getPreferredKeyNotation();
                    }                        
                    if (OptionsUI.instance.tagwritekey.isSelected() && (!keyStr.equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setKeyStart(keyStr);
                } catch (Exception e) {
                    log.debug("writeTags(): error writing start key", e);
                }

                // key end:
                try {
                    String keyStr = null;
                    if (OptionsUI.instance.writekeycodes.isSelected()) {
                        keyStr = song.getEndKey().getKeyCode().toString();
                    } else {
                        keyStr = song.getEndKey().getPreferredKeyNotation();
                    }                        
                    if (OptionsUI.instance.tagwritekey.isSelected() && (!keyStr.equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setKeyEnd(keyStr);
                } catch (Exception e) {
                    log.debug("writeTags(): error writing end key", e);
                }
                
                // rating:
                try {
                    Integer rating = null;
                    if (song.getRating() == (char)1) rating = new Integer(1);
                    else if (song.getRating() == (char)2) rating = new Integer(2);
                    else if (song.getRating() == (char)3) rating = new Integer(3);
                    else if (song.getRating() == (char)4) rating = new Integer(4);
                    else if (song.getRating() == (char)5) rating = new Integer(5);
                    if (rating != null) {
                        if (OptionsUI.instance.tagwriterating.isSelected() && ((rating.intValue() > 0) || OptionsUI.instance.tagswriteempty.isSelected()))
                            tag_writer.setRating(rating.intValue());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing rating", e);
                }
                
                // remix:
                try {
                    if (!OptionsUI.instance.writeremixertotitle.isSelected()) {
                        if (OptionsUI.instance.tagwriteremixer.isSelected() && (!song.getRemixer().equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                            tag_writer.setRemix(song.getRemixer());
                    } else {
                        if (OptionsUI.instance.tagwriteremixer.isSelected() && OptionsUI.instance.tagswriteempty.isSelected())
                            tag_writer.setRemix("");
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing remix", e);
                }
                
                // replay gain
                try {
                    if (OptionsUI.instance.tagwriterga.isSelected()) {
                        if (song.getRGA().isValid())
                            tag_writer.setReplayGain(song.getRGA().getDifference());
                    }
                } catch (Exception e) {
                    log.debug("writeTags(): error writing replay gain", e);
                }                
                
                // styles:
                try {
                    String[] styles = null;
                    if (song_styles == null) {
	                    StyleLinkedList siter = SongDB.instance.masterstylelist;
	                    Vector styles_vector = new Vector();
	                    while (siter != null) {
	                        if (siter.containsDirect(song)) {
	                            styles_vector.add(siter.getName());
	                        }
	                        siter = siter.next;
	                    }                    
	                    styles = new String[styles_vector.size()];                    
	                    for (int s = 0; s < styles.length; ++s) {
	                        styles[s] = (String)styles_vector.get(s);
	                    }
                    } else {
                        styles = song_styles;
                    }
                    java.util.Arrays.sort(styles);                    
                    if (OptionsUI.instance.tagwritingstyles.isSelected() && ((styles.length > 0) || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setStyles(styles);                    
                } catch (Exception e) {
                    log.debug("writeTags(): error writing styles", e);
                }
                
                // time:
                try {
                    if (OptionsUI.instance.tagwritetime.isSelected() && (!song.getTime().equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setTime(song.getTime());
                } catch (Exception e) {
                    log.debug("writeTags(): error writing time", e);
                }                
                
                // time sig:
                try {
                    if (OptionsUI.instance.tagwritetimesig.isSelected() && (!song.getTimesig().equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setTimeSignature(song.getTimesig());
                } catch (Exception e) {
                    log.debug("writeTags(): error writing time signature", e);
                }                
                
                // title:
                try {
                    String title = song.getSongname();
                    if (OptionsUI.instance.writeremixertotitle.isSelected() && !song.getRemixer().trim().equals("")) {
                        if (!title.equals(""))
                            title = title + " (" + song.getRemixer() + ")";
                        else title = "(" + song.getRemixer() + ")";                    
                    }
                    if (OptionsUI.instance.writekeytotitle.isSelected()) {
                        String key_comments = song.getStartKey().getPreferredKeyNotation();
                        if (key_comments.length() > 0)
                            title = key_comments + " - " + title;
                    }
                    if (OptionsUI.instance.tagwritesongname.isSelected() && (!title.equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setTitle(title);
                } catch (Exception e) {
                    log.debug("writeTags(): error writing title", e);
                }                    

                // track:
                try {
                    String track = song.getTrack();
                    Integer total_tracks = null;
                    int seperator = 0;
                    while ((seperator < track.length()) && ((track.charAt(seperator) != '/') && (track.charAt(seperator) != '.') && (track.charAt(seperator) != ',') && (track.charAt(seperator) != '-')))
                        seperator++;
                    if ((seperator > 0) && (seperator != track.length())) {
                        try {
                            total_tracks = new Integer(Integer.parseInt(track.substring(seperator + 1, track.length())));
                            track = track.substring(0, seperator);
                        } catch (java.lang.NumberFormatException e) {
                            if (log.isDebugEnabled()) log.debug("writeTags(): number format exception=" + track, e);
                        }
                    }
                    if (OptionsUI.instance.tagwritetrack.isSelected() && (!song.getTrack().equals("") || OptionsUI.instance.tagswriteempty.isSelected()))
                        tag_writer.setTrack(track, total_tracks);
                } catch (Exception e) {
                    log.debug("writeTags(): error writing track", e);
                }
                
                if (OptionsUI.instance.tagwritingcustomfields.isSelected()) {
	                // user1:
	                try {
                		writeCustomField(tag_writer, (String)OptionsUI.instance.custom_field_1_tag_combo.getSelectedItem(), song.getUser1(), 1);
	                } catch (Exception e) {
	                    log.error("writeTags(): error writing custom user field 1", e);
	                }
	                
	                // user2:
                    try {                
                		writeCustomField(tag_writer, (String)OptionsUI.instance.custom_field_2_tag_combo.getSelectedItem(), song.getUser2(), 2);
                    } catch (Exception e) {
                        log.error("writeTags(): error writing custom user field 2", e);
                    }
	
	                // user3:
                    try {         
                		writeCustomField(tag_writer, (String)OptionsUI.instance.custom_field_3_tag_combo.getSelectedItem(), song.getUser3(), 3);
                    } catch (Exception e) {
                        log.error("writeTags(): error writing custom user field 3", e);
                    }
	
	                // user4:
                    try {    
                		writeCustomField(tag_writer, (String)OptionsUI.instance.custom_field_4_tag_combo.getSelectedItem(), song.getUser4(), 4);
                    } catch (Exception e) {
                        log.error("writeTags(): error writing custom user field 4", e);
                    }
                }
                
                // save the tags
                success = tag_writer.save();
                if (log.isDebugEnabled()) {
                    if (success) log.debug("writeTags(): success");
                    else log.debug("writeTags(): failed");
                }

            }
        } catch (java.lang.OutOfMemoryError e) {
            log.error("writeTags(): error out of memory", e);
        } catch (Exception e) {
            log.error("writeTags(): error Exception", e);
        }
        FileLockManager.endFileWrite(filename);
        return success;
    }
    
    public static boolean removeTags(SongLinkedList song) {
        boolean success = false;
        try {
        	FileLockManager.startFileWrite(song.getRealFileName());
            TagRemover tag_remover = TagRemoverFactory.getTagRemover(song.getRealFileName());
            if (tag_remover != null) {
                tag_remover.removeTags();
            }
        } catch (Exception e) {
            log.error("removeTags(): error Exception", e);
        }
        FileLockManager.endFileWrite(song.getRealFileName());
        return success;
    }    
    
    private static void writeCustomField(TagWriter tag_writer, String customType, String value, int custom_field) {
        if (((value == null) || value.equals("")) && !OptionsUI.instance.tagswriteempty.isSelected())
            return;
        if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_none"))) {
            if (custom_field == 1) tag_writer.setUser1(value);
            else if (custom_field == 2) tag_writer.setUser2(value);
            else if (custom_field == 3) tag_writer.setUser3(value);
            else if (custom_field == 4) tag_writer.setUser4(value);
            else {
                log.error("writeCustomField(): unknown custom field=" + custom_field);
            }        
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_content_group_description"))) {
            tag_writer.setContentGroupDescription(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_content_type"))) {
            tag_writer.setContentType(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_encoded_by"))) {
            tag_writer.setEncodedBy(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_file_type"))) {
            tag_writer.setFileType(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_languages"))) {
            tag_writer.setLanguages(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_lyrics"))) {
            tag_writer.setLyrics(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_catalogid"))) {
            tag_writer.setCatalogId(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_publisher"))) {
            tag_writer.setPublisher(value);
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_byte_size"))) {
            tag_writer.setSizeInBytes(Integer.parseInt(value));
        }
        else if (customType.equals(SkinManager.instance.getMessageText("custom_field_id3_tag_year"))) {
            tag_writer.setYear(value);
        }
        else {
            log.error("writeCustomField(): unknown custom field type=" + customType);
        }
    }

}

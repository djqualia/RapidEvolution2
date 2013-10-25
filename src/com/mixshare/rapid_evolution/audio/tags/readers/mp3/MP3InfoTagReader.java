package com.mixshare.rapid_evolution.audio.tags.readers.mp3;

import java.io.RandomAccessFile;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.StringUtil;

import com.mixshare.rapid_evolution.audio.tags.readers.BaseTagReader;
import com.mixshare.rapid_evolution.audio.tags.util.AlbumCoverUtil;
import com.mixshare.rapid_evolution.audio.tags.util.TagUtil;

import de.ueberdosis.mp3info.ID3Reader;
import de.ueberdosis.mp3info.ID3Tag;
import de.ueberdosis.mp3info.id3v2.FrameAPIC;
import de.ueberdosis.mp3info.id3v2.FrameCOMM;
import de.ueberdosis.mp3info.id3v2.FrameTALB;
import de.ueberdosis.mp3info.id3v2.FrameTBPM;
import de.ueberdosis.mp3info.id3v2.FrameTCOM;
import de.ueberdosis.mp3info.id3v2.FrameTCON;
import de.ueberdosis.mp3info.id3v2.FrameTENC;
import de.ueberdosis.mp3info.id3v2.FrameTFLT;
import de.ueberdosis.mp3info.id3v2.FrameTIME;
import de.ueberdosis.mp3info.id3v2.FrameTIT1;
import de.ueberdosis.mp3info.id3v2.FrameTIT2;
import de.ueberdosis.mp3info.id3v2.FrameTKEY;
import de.ueberdosis.mp3info.id3v2.FrameTLAN;
import de.ueberdosis.mp3info.id3v2.FrameTLEN;
import de.ueberdosis.mp3info.id3v2.FrameTOPE;
import de.ueberdosis.mp3info.id3v2.FrameTPE1;
import de.ueberdosis.mp3info.id3v2.FrameTPE4;
import de.ueberdosis.mp3info.id3v2.FrameTPUB;
import de.ueberdosis.mp3info.id3v2.FrameTRCK;
import de.ueberdosis.mp3info.id3v2.FrameTSIZ;
import de.ueberdosis.mp3info.id3v2.FrameTXXX;
import de.ueberdosis.mp3info.id3v2.FrameTYER;
import de.ueberdosis.mp3info.id3v2.ID3V2Frame;
import de.ueberdosis.mp3info.id3v2.ID3V2Tag;

public class MP3InfoTagReader extends BaseTagReader {

    private static Logger log = Logger.getLogger(MP3InfoTagReader.class);
            
    private String filename = null;
    private ID3Tag v1tag = null;
    private ID3V2Tag v2tag = null;
    private Vector v2frames = null;
    
    public MP3InfoTagReader(String filename) {
        try {        
            this.filename = filename;
            if (log.isDebugEnabled()) log.debug("MP3InfoTagReader(): filename=" + filename);            
            RandomAccessFile in = new RandomAccessFile(filename, "r");
            if (in != null) {
                try {
                    ID3Reader id3 = new ID3Reader(filename);
                } catch (Exception e) {
                    log.error("MP3InfoTagReader(): exception calling ID3Reader", e);
                }
                v1tag = ID3Reader.readTag(in);
                if (log.isDebugEnabled()) log.debug("MP3InfoTagReader(): v1tag=" + v1tag);
                v2tag = ID3Reader.getV2Tag();
                if (log.isDebugEnabled()) log.debug("MP3InfoTagReader(): v2tag=" + v2tag);
                if (v2tag != null) {
                    v2frames = v2tag.getFrames();
                    if ((v2frames != null) && log.isDebugEnabled()) log.debug("MP3InfoTagReader(): # frames=" + v2frames.size());                    
                }                
            }
        } catch (java.io.FileNotFoundException e) {
            log.error("MP3InfoTagReader(): error file not found=" + filename, e);
        } catch (java.io.IOException e) {
            log.error("MP3InfoTagReader(): error IO exception, filename=" + filename, e);
        }
    }
    
    public String getAlbum() {
        String album = null;
        FrameTALB frame = (FrameTALB)getFrame(FRAME_ALBUM);
        if (frame != null) {
            album = frame.getText();
        }
        if (StringUtil.isValid(album)) return album;
        if (v1tag != null)
            album = v1tag.getAlbum();
        return album;
    }
    
    public String getAlbumCoverFilename() {
        String album_cover_filename = null;
        FrameAPIC frame = (FrameAPIC)getFrame(FRAME_ALBUM_COVER);
        if (frame != null) {
            album_cover_filename = AlbumCoverUtil.saveAlbumCover(this, frame.getDescription(), frame.getMimeType(), frame.getPictureData());
        }
        return album_cover_filename;        
    }    
    
    public String getArtist() {
        String artist = null;
        // lead performer
        FrameTPE1 tpe1_frame = (FrameTPE1)getFrame(FRAME_LEAD_PERFORMER);
        if (tpe1_frame != null) {
            artist = tpe1_frame.getText();
        }
        if (StringUtil.isValid(artist)) return artist;
        if (v1tag != null)
            artist = v1tag.getArtist();
        if (StringUtil.isValid(artist)) return artist;
        // composer
        FrameTCOM tcom_frame = (FrameTCOM)getFrame(FRAME_COMPOSER);
        if (tcom_frame != null) {
            artist = tcom_frame.getText();
        }
        if (StringUtil.isValid(artist)) return artist;
        // original performer
        FrameTOPE tope_frame = (FrameTOPE)getFrame(FRAME_ORIGINAL_PERFORMER);
        if (tope_frame != null) {
            artist = tope_frame.getText();
        }
        return artist;
    }    

    public Integer getBeatIntensity() {
        Integer intensity = null;        
        String txxx_intensity = getTXXXFrameValue(TXXX_BEAT_INTENSITY);
        if (txxx_intensity != null) {
            try {
                intensity = new Integer(Integer.parseInt(txxx_intensity));
            } catch (java.lang.NumberFormatException e) {
                log.error("getBeatIntensity(): number format exception beat_intensity=" + txxx_intensity);
            }            
        }
        return intensity;        
    }
    
    public Integer getBpmAccuracy() {
        Integer accuracy = null;        
        String txxx_accuracy = getTXXXFrameValue(TXXX_BPM_ACCURACY);
        if (txxx_accuracy != null) {
            try {
                accuracy = new Integer(Integer.parseInt(txxx_accuracy));
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmAccuracy(): number format exception bpm_accuracy=" + txxx_accuracy);
            }            
        }
        return accuracy;
    }
    
    public Float getBpmStart() {
        Float start_bpm = null;
        // txxx start bpm
        String txxx_bpm = getTXXXFrameValue(TXXX_BPM_START);
        if (txxx_bpm != null) {
            try {
                float bpm = Float.parseFloat(txxx_bpm);
                if (bpm != 0.0f) start_bpm = new Float(bpm);
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmStart(): number format exception start bpm txxx=" + txxx_bpm);
            }            
        }
        if (start_bpm != null) return start_bpm;
        // tbpm
        FrameTBPM frame = (FrameTBPM)getFrame(FRAME_BPM);
        if (frame != null) {
            try {
                float bpm = Float.parseFloat(frame.getText());
                if (bpm != 0.0f) start_bpm = new Float(bpm);
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmStart(): number format exception tbpm=" + frame.getText());
            }
        }
        if (start_bpm != null) return start_bpm;
        // txxx bpm/tempo/fbpm
        txxx_bpm = getTXXXFrameValue(TXXX_BPM);
        if (txxx_bpm != null) {
            try {
                float bpm = Float.parseFloat(txxx_bpm);
                if (bpm != 0.0f) start_bpm = new Float(bpm);
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmStart(): number format exception bpm txxx=" + txxx_bpm);
            }            
        }
        if (start_bpm != null) return start_bpm;
        txxx_bpm = getTXXXFrameValue(TXXX_TEMPO);
        if (txxx_bpm != null) {
            try {
                float bpm = Float.parseFloat(txxx_bpm);
                if (bpm != 0.0f) start_bpm = new Float(bpm);
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmStart(): number format exception tempo txxx=" + txxx_bpm);
            }            
        }
        if (start_bpm != null) return start_bpm;        
        String fBPM = getTXXXFrameValue(TXXX_BPM_FINALSCRATCH);
        if (fBPM != null) {
            try {
                float bpm = Float.parseFloat(fBPM);
                if (bpm != 0.0f) start_bpm = new Float(bpm);
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmStart(): number format exception fbpm txxx=" + fBPM);
            }            
        }
        return start_bpm;
    }

    public Float getBpmEnd() {
        Float end_bpm = null;        
        String txxx_bpm = getTXXXFrameValue(TXXX_BPM_END);
        if (txxx_bpm != null) {
            try {
                float bpm = Float.parseFloat(txxx_bpm);
                if (bpm != 0.0f) end_bpm = new Float(bpm);
            } catch (java.lang.NumberFormatException e) {
                log.error("getBpmEnd(): number format exception=" + txxx_bpm, e);
            }            
        }
        return end_bpm;
    }    
    
    public String getComments() {
        String comments = null;
        FrameCOMM frame = (FrameCOMM)getFrame(FRAME_COMMENTS);
        if (frame != null) {
            // TODO: there's some bug truncating first character of comments...
            comments = frame.getText();
        }
        if (StringUtil.isValid(comments)) return comments;
        if (v1tag != null)
            comments = v1tag.getComment();
        return comments;
    }
    
    public String getContentGroupDescription() {
        String content_group_description = null;
        FrameTIT1 frame = (FrameTIT1)getFrame(FRAME_CONTENT_GROUP_DESCRIPTION);
        if (frame != null) {
            content_group_description = frame.getText();
        }
        return content_group_description;
    }
    
    public String getContentType() {
        String content_type = null;
        FrameTCON frame = (FrameTCON)getFrame(FRAME_CONTENT_TYPE);
        if (frame != null) {
            content_type = frame.getText();
        }
        return content_type;
    }
    
    public String getEncodedBy() {
        String encoded_by = null;
        FrameTENC frame = (FrameTENC)getFrame(FRAME_ENCODED_BY);
        if (frame != null) {
            encoded_by = frame.getText();
        }
        return encoded_by;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getFileType() {
        String file_type = null;
        FrameTFLT frame = (FrameTFLT)getFrame(FRAME_FILE_TYPE);
        if (frame != null) {
            file_type = frame.getText();
        }
        return file_type;
    }

    public String getGenre() {
        String genre = null;
        FrameTCON tcon_frame = (FrameTCON)getFrame(FRAME_GENRE);
        if (tcon_frame != null) {
            genre = tcon_frame.getText();
        }
        if (StringUtil.isValid(genre)) return genre;
        if (v1tag != null) {
            genre = v1tag.getGenreS();
        }        
        return genre;
    }
    
    public Integer getKeyAccuracy() {
        Integer accuracy = null;        
        String txxx_accuracy = getTXXXFrameValue(TXXX_KEY_ACCURACY);
        if (txxx_accuracy != null) {
            try {
                accuracy = new Integer(Integer.parseInt(txxx_accuracy));
            } catch (java.lang.NumberFormatException e) {
                log.error("getKeyAccuracy(): number format exception key_accuracy=" + txxx_accuracy);
            }            
        }
        return accuracy;
    }
    
    public String getKeyStart() {
        String start_key = null;
        // txxx start key
        start_key = getTXXXFrameValue(TXXX_KEY_START);
        if (StringUtil.isValid(start_key)) return start_key;
        // tkey
        FrameTKEY tkey_frame = (FrameTKEY)getFrame(FRAME_KEY);
        if (tkey_frame != null) {
            start_key = tkey_frame.getText();
        }
        if (StringUtil.isValid(start_key)) return start_key;
        // txxx key/initialkey/fkey
        start_key = getTXXXFrameValue(TXXX_KEY);
        if (StringUtil.isValid(start_key)) return start_key;
        start_key = getTXXXFrameValue(TXXX_KEY_INITIAL);
        if (StringUtil.isValid(start_key)) return start_key;        
        ID3V2Frame fkey_frame = getFrame(TXXX_KEY_FINALSCRATCH);
        if (fkey_frame != null) {
            byte[] data = fkey_frame.getData();
            if (data != null) {
              start_key = new String(data);
            }
        }
        return start_key;
    }
    
    public String getKeyEnd() {
        String end_key = null;
        end_key = getTXXXFrameValue(TXXX_KEY_END);
        return end_key;
    }
    
    public String getLanguages() {
        String languages = null;
        FrameTLAN frame = (FrameTLAN)getFrame(FRAME_LANGUAGES);
        if (frame != null) {
            languages = frame.getText();
        }
        return languages;
    }

    public String getPublisher() {
        String publisher = null;
        FrameTPUB frame = (FrameTPUB)getFrame(FRAME_PUBLISHER);
        if (frame != null) {
            publisher = frame.getText();
        }
        return publisher;
    }
    
    public Integer getRating() {
        Integer rating = null;
        String txxx_rating = getTXXXFrameValue(TXXX_RATING);
        if (txxx_rating != null) {
            try {
                int ratingInt = Integer.parseInt(txxx_rating);
                if (ratingInt != 0) rating = new Integer(ratingInt);
            } catch (java.lang.NumberFormatException e) {
                log.error("getRating(): number format exception txxx rating=" + txxx_rating);
            }            
        }        
        return rating;
    }    
    
    public String getRemix() {
        String remix = null;
        FrameTPE4 frame = (FrameTPE4)getFrame(FRAME_REMIXER);
        if (frame != null) {
            remix = frame.getText();
        }
        return remix;
    }
        
    public Integer getSizeInBytes() {
        Integer size_in_bytes = null;
        FrameTSIZ frame = (FrameTSIZ)getFrame(FRAME_SIZE);
        if (frame != null) {
            try {
                size_in_bytes = new Integer(Integer.parseInt(frame.getText()));
            } catch (java.lang.NumberFormatException e) {
                log.error("getSizeInBytes(): number format exception=" + frame.getText(), e);
            }
        }
        return size_in_bytes;
    }
    
    public String[] getStyles() {
        Vector styles = new Vector();
        int style_count = 1;
        String style = getStyle(style_count);
        while (style != null) {
            styles.add(style);
            style = getStyle(++style_count);
        }
        return TagUtil.getStyleStringArray(styles);
    }  
        
    public String getTime() {
        String time = null;
        time = getTXXXFrameValue(TXXX_TIME);
        if (StringUtil.isValid(time)) return time;
        FrameTLEN tlen_frame = (FrameTLEN)getFrame(FRAME_LENGTH);
        if (tlen_frame != null) {
            try {
                int milliseconds = Integer.parseInt(tlen_frame.getText());
                time = StringUtil.seconds_to_time(milliseconds / 1000);
            } catch (Exception e) {
                log.error("getTime(): error getting length=", e);
            }
        }        
        if (StringUtil.isValid(time)) return time;
        FrameTIME time_frame = (FrameTIME)getFrame(FRAME_TIME);
        if (time_frame != null) {
            time = time_frame.getText();
        }        
        return time;
    }
    
    public String getTimeSignature() {
        return getTXXXFrameValue(TXXX_TIME_SIGNATURE);        
    }
    
    public String getTitle() {
        String title = null;
        FrameTIT2 tit2_frame = (FrameTIT2)getFrame(FRAME_TITLE);
        if (tit2_frame != null) {
            title = tit2_frame.getText();
        }
        if (StringUtil.isValid(title)) return title;
        if (v1tag != null)
            title = v1tag.getTitle();
        return title;
    }
    
    public String getTrack() {
        String track = null;
        FrameTRCK frame = (FrameTRCK)getFrame(FRAME_TRACK);
        if (frame != null) {
            track = frame.getText();
        }
        if (StringUtil.isValid(track)) return track;
        if (v1tag != null) {
            track = v1tag.getTrackS();
            if ((track != null) && track.equals("0"))
                track = null;
        }
        return track;
    }

    public String getUser1() {
        return getTXXXFrameValue(TagUtil.getUser1TagId());
    }

    public String getUser2() {
        return getTXXXFrameValue(TagUtil.getUser2TagId());
    }

    public String getUser3() {
        return getTXXXFrameValue(TagUtil.getUser3TagId());
    }

    public String getUser4() {
        return getTXXXFrameValue(TagUtil.getUser4TagId());
    }
    
    public String getYear() {
        String year = null;
        FrameTYER frame = (FrameTYER)getFrame(FRAME_YEAR);
        if (frame != null) {
            year = frame.getText();
        }
        if (StringUtil.isValid(year)) return year;
        if (v1tag != null)
            year = v1tag.getYear();
        return year;
    }
    
    private ID3V2Frame getFrame(String frame_id) {
        if (v2frames != null) {
            for (int f = 0; f < v2frames.size(); ++f) {
                ID3V2Frame frame = (ID3V2Frame)v2frames.get(f);
                if (frame.containsData() && frame.getFrameID().equals(frame_id))
                    return frame;
            }
        }
        return null;
    }
    
    private String getTXXXFrameValue(String frame_description) {
        //log.trace("getTXXXFrameValue(): frame_description=" + frame_description);
        if (v2frames != null) {
            for (int f = 0; f < v2frames.size(); ++f) {
                ID3V2Frame frame = (ID3V2Frame)v2frames.get(f);
                if (frame.containsData() && frame.getFrameID().equals(FRAME_TXXX)) {                    
                    FrameTXXX txxx_frame = (FrameTXXX)frame;
                    log.trace("getTXXXFrameValue(): found txxx frame=" + frame);
                    String frameStr = frame.toString();
                    String desc = StringUtil.getline(frameStr, 1).substring(12, StringUtil.getline(frameStr, 1).length()).trim();
                    log.trace("getTXXXFrameValue(): description=" + desc);                    
                    if (desc.equals(frame_description)) {
                      String strValue = StringUtil.getline(frameStr, 2).substring(12, StringUtil.getline(frameStr, 2).length()).trim();
                      log.trace("getTXXXFrameValue(): value=" + strValue);                    
                      return strValue;
                    }

                }
            }
        }
        return null;        
    }
 
    private String getStyle(int style_number) {
        String identifier = TagUtil.getStyleTagId(style_number);
        return getTXXXFrameValue(identifier);
    }
    
}

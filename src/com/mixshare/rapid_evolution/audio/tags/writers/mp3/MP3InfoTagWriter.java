package com.mixshare.rapid_evolution.audio.tags.writers.mp3;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.StringUtil;

import com.mixshare.rapid_evolution.audio.tags.util.TagUtil;
import com.mixshare.rapid_evolution.audio.tags.util.mp3info.TXXXDataSource;
import com.mixshare.rapid_evolution.audio.tags.util.mp3info.TextDataSource;
import com.mixshare.rapid_evolution.audio.tags.writers.BaseTagWriter;

import de.ueberdosis.mp3info.ID3Reader;
import de.ueberdosis.mp3info.ID3Tag;
import de.ueberdosis.mp3info.ID3Writer;
import de.ueberdosis.mp3info.id3v2.FrameAPIC;
import de.ueberdosis.mp3info.id3v2.FrameCOMM;
import de.ueberdosis.mp3info.id3v2.FrameTALB;
import de.ueberdosis.mp3info.id3v2.FrameTBPM;
import de.ueberdosis.mp3info.id3v2.FrameTCOM;
import de.ueberdosis.mp3info.id3v2.FrameTCON;
import de.ueberdosis.mp3info.id3v2.FrameTENC;
import de.ueberdosis.mp3info.id3v2.FrameTFLT;
import de.ueberdosis.mp3info.id3v2.FrameTIT1;
import de.ueberdosis.mp3info.id3v2.FrameTIT2;
import de.ueberdosis.mp3info.id3v2.FrameTKEY;
import de.ueberdosis.mp3info.id3v2.FrameTLAN;
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
import de.ueberdosis.mp3info.id3v2.ID3V2Writer;
import de.ueberdosis.mp3info.id3v2.SeekPastEndException;

/**
 * Old and not maintained since early 2008
 */
public class MP3InfoTagWriter extends BaseTagWriter {

    private static Logger log = Logger.getLogger(MP3InfoTagWriter.class);

    private String filename = null;
    private ID3Tag v1tag = null;
    private ID3V2Tag v2tag = null;
    private Vector v2frames = null;
    
    public MP3InfoTagWriter(String filename, int mode) {
        try {
            this.filename = filename;
            if (log.isDebugEnabled()) log.debug("MP3InfoTagWriter(): filename=" + filename);                        
            RandomAccessFile in = new RandomAccessFile(filename, "r");
        	if (in != null) {
                ID3Reader id3 = new ID3Reader(filename);        	    
        	    v1tag = (mode == TAG_MODE_UPDATE) ? ID3Reader.readTag(in) : new ID3Tag();
                if (log.isDebugEnabled()) log.debug("MP3InfoTagWriter(): v1tag=" + v1tag);        	    
        	    v2tag = (mode == TAG_MODE_UPDATE) ? ID3Reader.getV2Tag() : new ID3V2Tag();
                if (log.isDebugEnabled()) log.debug("MP3InfoTagWriter(): v2tag=" + v2tag);        	    
            	in.close();            	
            	if (v1tag == null) v1tag = new ID3Tag();
            	if (v2tag == null) v2tag = new ID3V2Tag();               	
            	v2frames = v2tag.getFrames();
                if ((v2frames != null) && log.isDebugEnabled()) log.debug("MP3InfoTagWriter(): # frames=" + v2frames.size());
        	}
        } catch (java.io.FileNotFoundException e) {
            log.error("MP3InfoTagReader(): error file not found=" + filename, e);
        } catch (java.io.IOException e) {
            log.error("MP3InfoTagReader(): error IO exception, filename=" + filename, e);
        }    
    }
    
    public boolean save() {
        boolean success = false;
        try {
            if (v2tag != null) {
                ID3V2Writer.writeTag(new File(filename), v2tag);
            }
            if (v1tag != null) {
                RandomAccessFile v1file = new RandomAccessFile(filename, "rw");
                ID3Writer.writeTag(v1file, v1tag);
                v1file.close();
            }
            success = true;
            if (log.isDebugEnabled()) log.debug("save(): tag successfully written to filename=" + filename);
        } catch (Exception e) {
            log.error("save(): could not save tag to filename=" + filename, e);
        }
        return success;
    }
    
    public void setAlbum(String album) {
        if (v1tag != null)
            v1tag.setAlbum(album);
        if (v2tag != null) {
            FrameTALB frame = (FrameTALB)getFrame(FRAME_ALBUM);
            if (frame == null) {
                frame = new FrameTALB();
                v2tag.addFrame(frame);
            }
            frame.setText(album);
        }
    }
    
    public void setAlbumCover(String filename, String album) {
        if (v2tag != null) {
            try {
	            File file = new File(filename);
	            byte[] buffer = new byte[32 * 1024];
	            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
	            ByteArrayOutputStream imageBinary = new ByteArrayOutputStream();
	            int bytes_read = bufferedInputStream.read(buffer);
	            while (bytes_read != -1) {
	                imageBinary.write(buffer, 0, bytes_read);
	                bytes_read = bufferedInputStream.read(buffer);
	            }
	            String mimeType = "image/" + FileUtil.getExtension(file);           
	            FrameAPIC frame = (FrameAPIC)getFrame(FRAME_ALBUM_COVER);
	            if (frame != null) {
	                v2tag.removeFrame(frame);
	            }
	            frame = new FrameAPIC(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_ALBUM_COVER), mimeType, album, imageBinary.toByteArray());
	            v2tag.addFrame(frame);
            } catch (Exception e) {
                log.error("setAlbumCover(): error Exception", e);
            }
        }
    }
    
    public void setArtist(String artist) {
        if (v1tag != null)
            v1tag.setArtist(artist); 
        if (v2tag != null) {
            FrameTPE1 tpe1_frame = (FrameTPE1)getFrame(FRAME_LEAD_PERFORMER);
            if (tpe1_frame == null) {
                tpe1_frame = new FrameTPE1();
                v2tag.addFrame(tpe1_frame);
            }
            tpe1_frame.setText(artist);
            FrameTCOM tcom_frame = (FrameTCOM)getFrame(FRAME_COMPOSER);
            if (tcom_frame == null) {
                tcom_frame = new FrameTCOM();
                v2tag.addFrame(tcom_frame);
            }
            tcom_frame.setText(artist);
            FrameTOPE tope_frame = (FrameTOPE)getFrame(FRAME_ORIGINAL_PERFORMER);
            if (tope_frame == null) {
                tope_frame = new FrameTOPE();
                v2tag.addFrame(tope_frame);
            }
            tope_frame.setText(artist);
        }        
    }
    
    public void setBeatIntensity(int beat_intensity) {
        if (v2tag != null) {
	        try {
	            FrameTXXX txxx_frame = getTXXXFrame(TXXX_BEAT_INTENSITY);
	            if (txxx_frame != null) {
	                v2tag.removeFrame(txxx_frame);
	            }
	            TXXXDataSource datasource = new TXXXDataSource(TXXX_BEAT_INTENSITY, String.valueOf(beat_intensity));
	            txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
	            v2tag.addFrame(txxx_frame);	            
	        } catch (SeekPastEndException e) {
	            log.error("setBeatIntensity(): seek past end exception", e);
	        }            
        }        
    }
    
    public void setBpm(int bpm) {
        if (v2tag != null) {
            FrameTBPM frame = (FrameTBPM)getFrame(FRAME_BPM);
            if (frame == null) {
                frame = new FrameTBPM(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_BPM));
                v2tag.addFrame(frame);
            }
            frame.setText(String.valueOf(bpm));
        }        
    }
    
    public void setBpmFloat(float bpm) {        
        if (v2tag != null) {
            FrameTBPM frame = (FrameTBPM)getFrame(FRAME_BPM);
            if (frame == null) {
                frame = new FrameTBPM(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_BPM));
                v2tag.addFrame(frame);
            }
            frame.setText(String.valueOf(bpm));
        }        
    }
    
    public void setBpmAccuracy(int accuracy) {
        if (v2tag != null) {
	        try {
	            FrameTXXX txxx_frame = getTXXXFrame(TXXX_BPM_ACCURACY);
	            if (txxx_frame != null) {
	                v2tag.removeFrame(txxx_frame);
	            }
	            TXXXDataSource datasource = new TXXXDataSource(TXXX_BPM_ACCURACY, String.valueOf(accuracy));
	            txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
	            v2tag.addFrame(txxx_frame);	            
	        } catch (SeekPastEndException e) {
	            log.error("setBpmAccuracy(): seek past end exception", e);
	        }            
        }        
    }
    
    public void setBpmStart(float start_bpm) {
        if (v2tag != null) {
	        try {
	            FrameTXXX txxx_frame = getTXXXFrame(TXXX_BPM_START);
	            if (txxx_frame != null) {
	                v2tag.removeFrame(txxx_frame);
	            }
	            TXXXDataSource datasource = new TXXXDataSource(TXXX_BPM_START, String.valueOf(start_bpm));
	            txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
	            v2tag.addFrame(txxx_frame);	            
	        } catch (SeekPastEndException e) {
	            log.error("setBpmEnd(): seek past end exception", e);
	        }            
        }        
    }
    
    public void setBpmEnd(float end_bpm) {
        if (v2tag != null) {
	        try {
	            FrameTXXX txxx_frame = getTXXXFrame(TXXX_BPM_END);
	            if (txxx_frame != null) {
	                v2tag.removeFrame(txxx_frame);
	            }
	            TXXXDataSource datasource = new TXXXDataSource(TXXX_BPM_END, String.valueOf(end_bpm));
	            txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
	            v2tag.addFrame(txxx_frame);	            
	        } catch (SeekPastEndException e) {
	            log.error("setBpmEnd(): seek past end exception", e);
	        }
        }
    }
    
    public void setComments(String comments) {
        if (v1tag != null)
            v1tag.setComment(comments);
        if (v2tag != null) {
            FrameCOMM frame = (FrameCOMM)getFrame(FRAME_COMMENTS);
            if (frame == null) {
                frame = new FrameCOMM();
                v2tag.addFrame(frame);
            }
            frame.setText(comments);
        }        
    }
    
    public void setContentGroupDescription(String content_group_description) {
        if (v2tag != null) {
            FrameTIT1 frame = (FrameTIT1)getFrame(FRAME_CONTENT_GROUP_DESCRIPTION);
            if (frame == null) {
                frame = new FrameTIT1(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_CONTENT_GROUP_DESCRIPTION));
                v2tag.addFrame(frame);
            }
            frame.setText(content_group_description);
        }        
    }
    
    public void setContentType(String content_type) {
        if (v2tag != null) {
            FrameTCON frame = (FrameTCON)getFrame(FRAME_CONTENT_TYPE);
            if (frame == null) {
                frame = new FrameTCON(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_CONTENT_TYPE));
                v2tag.addFrame(frame);
            }
            frame.setText(content_type);
        }        
    }
    
    public void setEncodedBy(String encoded_by) {
        if (v2tag != null) {
            FrameTENC frame = (FrameTENC)getFrame(FRAME_ENCODED_BY);
            if (frame == null) {
                frame = new FrameTENC(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_ENCODED_BY));
                v2tag.addFrame(frame);
            }
            frame.setText(encoded_by);
        }
    }
    
    public void setFileType(String file_type) {
        if (v2tag != null) {
            FrameTFLT frame = (FrameTFLT)getFrame(FRAME_FILE_TYPE);
            if (frame == null) {
                frame = new FrameTFLT(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_FILE_TYPE));
                v2tag.addFrame(frame);
            }
            frame.setText(file_type);
        }
    }

    public void setGenre(String genre) {
        if (v2tag != null) {
            FrameTCON frame = (FrameTCON)getFrame(FRAME_CONTENT_TYPE);
            if (frame == null) {
                frame = new FrameTCON();
                v2tag.addFrame(frame);
            }
            frame.setText(genre);
        }
        if (v1tag != null) {
            //TODO: would need to see if genre maps to a predefined V1 genre
            //v1tag.setGenre(...);
        }        
    }
    
    public void setKey(String key) {
        if (v2tag != null) {
            try {
                FrameTKEY tkey_frame = (FrameTKEY)getFrame(FRAME_KEY);
                if (tkey_frame == null) {
                    tkey_frame = new FrameTKEY(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_KEY), new TextDataSource(""));
                    v2tag.addFrame(tkey_frame);
                }
                tkey_frame.setText(key);
            } catch (SeekPastEndException e) {
                log.debug("setKeyStart(): error seek past end exception=" + e);
            }
        }        
    }
    
    public void setKeyAccuracy(int accuracy) {
        if (v2tag != null) {
	        try {
	            FrameTXXX txxx_frame = getTXXXFrame(TXXX_KEY_ACCURACY);
	            if (txxx_frame != null) {
	                v2tag.removeFrame(txxx_frame);
	            }
	            TXXXDataSource datasource = new TXXXDataSource(TXXX_KEY_ACCURACY, String.valueOf(accuracy));
	            txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
	            v2tag.addFrame(txxx_frame);	            
	        } catch (SeekPastEndException e) {
	            log.error("setKeyAccuracy(): seek past end exception", e);
	        }            
        }        
    }
    
    public void setKeyStart(String start_key) {
        if (v2tag != null) {
	        try {
	            FrameTXXX txxx_frame = getTXXXFrame(TXXX_KEY_START);
	            if (txxx_frame != null) {
	                v2tag.removeFrame(txxx_frame);
	            }
	            TXXXDataSource datasource = new TXXXDataSource(TXXX_KEY_START, start_key);
	            txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
	            v2tag.addFrame(txxx_frame);	            
	        } catch (SeekPastEndException e) {
	            log.error("setKeyStart(): seek past end exception", e);
	        }
        }        
    }
    
    public void setKeyEnd(String end_key) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TXXX_KEY_END);
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TXXX_KEY_END, end_key);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setKeyStart(): seek past end exception", e);
            }
        }
    }
    
    public void setLanguages(String languages) {
        if (v2tag != null) {
            FrameTLAN frame = (FrameTLAN)getFrame(FRAME_LANGUAGES);
            if (frame == null) {
                frame = new FrameTLAN(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_LANGUAGES));
                v2tag.addFrame(frame);
            }
            frame.setText(languages);
        }
    }
    
    public void setPublisher(String publisher) {
        if (v2tag != null) {
            FrameTPUB frame = (FrameTPUB)getFrame(FRAME_PUBLISHER);
            if (frame == null) {
                frame = new FrameTPUB(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_PUBLISHER));
                v2tag.addFrame(frame);
            }
            frame.setText(publisher);
        }
    }
    
    public void setRating(int rating) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TXXX_RATING);
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TXXX_RATING, String.valueOf(rating));
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setRating(): seek past end exception", e);
            }
        }
    }
    
    public void setRemix(String remix) {
        if (v2tag != null) {
            FrameTPE4 frame = (FrameTPE4)getFrame(FRAME_REMIXER);
            if (frame == null) {
                frame = new FrameTPE4(new ID3V2Frame(FRAME_REMIXER));
                v2tag.addFrame(frame);
            }
            frame.setText(remix);
        }
    }
    
    public void setSizeInBytes(int size) {
        if (v2tag != null) {
            FrameTSIZ frame = (FrameTSIZ)getFrame(FRAME_SIZE);
            if (frame == null) {
                frame = new FrameTSIZ(new ID3V2Frame(FRAME_SIZE));
                v2tag.addFrame(frame);
            }
            frame.setText(String.valueOf(size));
        }
    }
    
    public void setStyles(String[] styles) {
        removeStyles();
        if (styles != null) {
            for (int s = 0; s < styles.length; ++s) {
                setStyle(styles[s], s + 1);
            }
        }        
    }  
    
    public void setTime(String time) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TXXX_TIME);
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TXXX_TIME, time);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setTime(): seek past end exception", e);
            }        
        }
    }
    
    public void setTimeSignature(String time_sig) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TXXX_TIME_SIGNATURE);
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TXXX_TIME_SIGNATURE, time_sig);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setTimeSignature(): seek past end exception", e);
            }        
        }        
    }    
    
    public void setTitle(String title) {
        if (v2tag != null) {
            FrameTIT2 tit2_frame = (FrameTIT2)getFrame(FRAME_TITLE);
            if (tit2_frame == null) {
                tit2_frame = new FrameTIT2();
                v2tag.addFrame(tit2_frame);
            }
            tit2_frame.setText(title);
        }
        if (v1tag != null) {
            v1tag.setTitle(title);
        }
    }
    
    public void setTrack(String track, Integer total_tracks) {
        if (v2tag != null) {
            FrameTRCK frame = (FrameTRCK)getFrame(FRAME_TRACK);
            if (frame == null) {
                frame = new FrameTRCK();
                v2tag.addFrame(frame);
            }
            if (total_tracks != null)
                frame.setText(track + "/" + total_tracks);
            else
                frame.setText(track);
        }
        if (v1tag != null) {
            try {
                int track_int = Integer.parseInt(track);
                v1tag.setTrack(track_int);
            } catch (java.lang.NumberFormatException e) {
                log.debug("setTrack(): could not parse track=" + track);
            }
        }
    }
    
    public void setUser1(String value) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TagUtil.getUser1TagId());
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TagUtil.getUser1TagId(), value);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setUser1(): seek past end exception", e);
            }        
        }
    }
    
    public void setUser2(String value) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TagUtil.getUser2TagId());
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TagUtil.getUser2TagId(), value);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setUser2(): seek past end exception", e);
            }        
        }
    }   
    
    public void setUser3(String value) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TagUtil.getUser3TagId());
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TagUtil.getUser3TagId(), value);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setUser3(): seek past end exception", e);
            }        
        }
    }   
    
    public void setUser4(String value) {
        if (v2tag != null) {
            try {
                FrameTXXX txxx_frame = getTXXXFrame(TagUtil.getUser4TagId());
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(TagUtil.getUser4TagId(), value);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setUser4(): seek past end exception", e);
            }        
        }
    }       
        
    public void setYear(String year) {
        if (v2tag != null) {
            FrameTYER frame = (FrameTYER)getFrame(FRAME_YEAR);
            if (frame == null) {
                frame = new FrameTYER();
                v2tag.addFrame(frame);
            }
            frame.setText(year);
        }
        if (v1tag != null) {
            v1tag.setYear(year);
        }
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
    
    private FrameTXXX getTXXXFrame(String frame_description) {
        if (v2frames != null) {
            for (int f = 0; f < v2frames.size(); ++f) {
                ID3V2Frame frame = (ID3V2Frame)v2frames.get(f);
                if (frame.containsData() && frame.getFrameID().equals(FRAME_TXXX)) {
                    String frameStr = frame.toString();
                    String desc = StringUtil.getline(frameStr, 1).substring(14, StringUtil.getline(frameStr, 1).length());
                    if (desc.equals(frame_description)) {
                        return (FrameTXXX)frame;
                    }
                }
            }
        }
        return null;        
    }
    
    private void removeStyles() {
        if (v2frames != null) {
            Vector remove_frames = new Vector();
            for (int f = 0; f < v2frames.size(); ++f) {
                ID3V2Frame frame = (ID3V2Frame)v2frames.get(f);
                if (frame.containsData() && frame.getFrameID().equals(FRAME_TXXX)) {
                    String frameStr = frame.toString();
                    String desc = StringUtil.getline(frameStr, 1).substring(14, StringUtil.getline(frameStr, 1).length());
                    if (desc.startsWith(TXXX_STYLES_PREFIX)) {
                        remove_frames.add(frame);
                    }
                }
            }
            for (int f = 0; f < remove_frames.size(); ++f) {
                v2tag.removeFrame((ID3V2Frame)remove_frames.get(f));
            }            
        }    
    }    
    
    private void setStyle(String style, int style_number) {
        if (v2tag != null) {
            String identifier = TagUtil.getStyleTagId(style_number);
            try {
                FrameTXXX txxx_frame = getTXXXFrame(identifier);
                if (txxx_frame != null) {
                    v2tag.removeFrame(txxx_frame);
                }
                TXXXDataSource datasource = new TXXXDataSource(identifier, style);
                txxx_frame = new FrameTXXX(new de.ueberdosis.mp3info.id3v2.ID3V2Frame(FRAME_TXXX), datasource);
                v2tag.addFrame(txxx_frame);	            
            } catch (SeekPastEndException e) {
                log.error("setStyle(): seek past end exception", e);
            }        
        } 
    }    
    
}

package com.mixshare.rapid_evolution.audio.tags.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.StringUtil;

import com.mixshare.rapid_evolution.audio.tags.TagReader;
import rapid_evolution.util.OSHelper;

public class AlbumCoverUtil {
    
    private static Logger log = Logger.getLogger(AlbumCoverUtil.class);
    
    public static String saveAlbumCover(TagReader tag_reader, BufferedImage image) {
        String filename = null;
        try {
            if (image != null) {
                filename = "" + tag_reader.getArtist() + tag_reader.getAlbum();
                if (filename.equals("")) filename = "unknown";
                filename = StringUtil.cleanString(filename);
                filename = StringUtil.makeValidFilename(filename);
                filename = OSHelper.getWorkingDirectory() + "/albumcovers/" + filename + ".jpg";
                File file = new File(filename);
                ImageIO.write(image, "jpg", file);
            }
        } catch (Exception e) {
            log.error("saveAlbumCover(): error Exception", e);
            filename = null;
        }
        return filename;
    }
    
    public static String saveAlbumCover(TagReader tag_reader, String description, String mime_type, byte[] data) {
        String filename = null;
        try {
            if (log.isTraceEnabled()) 
            	log.trace("saveAlbumCover(): description=" + description + ", mime_type=" + mime_type);            
            String name = null;
            String artist = tag_reader.getArtist();
            String album = tag_reader.getAlbum();
            if ((artist != null) && !artist.equals("") && (album != null) && !album.equals("")) {
                name = artist + "_" + album;
            } else if ((description != null) && !description.equals("")){
                name = description;
            } else {
                name = FileUtil.getFilenameMinusDirectory(filename);
                name = name.substring(0, name.indexOf(FileUtil.getExtension(filename))) + "_albumcover";
            }
            if (log.isTraceEnabled()) log.trace("saveAlbumCover(): name=" + name);
            if (mime_type.length() > 0) {
                int index = mime_type.indexOf("/");
                String extension = null;
                if (index >= 0)
                    extension = mime_type.substring(index + 1, mime_type.length());
                else
                    extension = mime_type;

                File dirCheck = new File(OSHelper.getWorkingDirectory() + "/albumcovers/");
                if (!dirCheck.exists())
                	dirCheck.mkdir();
                
                String apic_filename = StringUtil.cleanString(OSHelper.getWorkingDirectory() + "/albumcovers/" + StringUtil.makeValidFilename(name) + "." + extension);
	            File file = new File(apic_filename);
	            int increment = 1;
                while (file.exists()) {
                    apic_filename = StringUtil.cleanString(OSHelper.getWorkingDirectory() + "/albumcovers/" + StringUtil.makeValidFilename(name) + "_" + String.valueOf(increment) + "." + extension);
                    file = new File(apic_filename);
                    ++increment;
                }
                FileOutputStream outputstream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputstream);
                bufferedOutputStream.write(data);
                bufferedOutputStream.close();
                outputstream.close();                
	            filename = apic_filename;
            }            
        } catch (Exception e) {
            log.error("saveAlbumCover(): error Exception", e);
        }
        return filename;
    }
    
}

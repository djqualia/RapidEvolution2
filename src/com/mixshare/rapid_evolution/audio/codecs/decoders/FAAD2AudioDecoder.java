package com.mixshare.rapid_evolution.audio.codecs.decoders;

import java.io.File;

import org.apache.log4j.Logger;

import rapid_evolution.FileUtil;
import rapid_evolution.RapidEvolution;
import rapid_evolution.util.OSHelper;

import com.mixshare.rapid_evolution.audio.exceptions.UnsupportedFileException;
import com.mixshare.rapid_evolution.util.io.StreamGobbler;

// decodes mp4/aac files
public class FAAD2AudioDecoder extends DefaultAudioDecoder {

    private static Logger log = Logger.getLogger(FAAD2AudioDecoder.class);
    
    private String wav_filename = null;
    
    public FAAD2AudioDecoder(String filename) throws UnsupportedFileException {
        super();
        try {
            if (log.isDebugEnabled()) log.debug("FAAD2AudioDecoder(): filename=" + filename);
            String extension = FileUtil.getExtension(filename);
            String new_filename = FileUtil.getFilenameMinusDirectory(filename);
            if (extension.length() > 0) {
                new_filename = new_filename.substring(0, new_filename.length() - extension.length() - 1);
            }
            File dir = new File(OSHelper.getWorkingDirectory() + "/temp/");
            if (!dir.exists())
            	dir.mkdir();
            wav_filename = OSHelper.getWorkingDirectory() + "/temp/" +  new_filename + ".wav";
            if (log.isDebugEnabled()) log.debug("FAAD2AudioDecoder(): temporary wav filename=" + wav_filename);
            // convert aac/mp4 to wav using faad
            String[] command = new String[4];
            if (RapidEvolution.instance.osName.toLowerCase().equals("mac os x")) command[0] = "faad_OSX";
            else if (RapidEvolution.instance.osName.toLowerCase().equals("linux")) command[0] = "faad";
            else command[0] = "faad.exe";
            command[1] = "-o";
            command[2] = wav_filename;
            command[3] = filename;                        
            Process proc = Runtime.getRuntime().exec(command);
            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), log);
            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), log);
            // kick them off
            errorGobbler.start();
            outputGobbler.start();                                    
            // any error???
            int exitVal = proc.waitFor();
            if (log.isDebugEnabled()) log.debug("FAAD2AudioDecoder(): done converting to wav, exit value=" + exitVal);
            init(wav_filename);
        } catch (Exception e) {
            log.error("FAAD2AudioDecoder(): error Exception", e);
            throw new UnsupportedFileException();
        }
    }
    
    public void close() {
        try {
            super.close();
            File file = new File(wav_filename);
            if (!file.delete())
            	file.deleteOnExit();
        } catch (Exception e) {
            log.error("close(): error Exception", e);
        }
    }
}

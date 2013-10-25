package com.mixshare.rapid_evolution.util.io;

import java.io.FileWriter;
import java.io.BufferedWriter;

import org.apache.log4j.Logger;

public class TextFileWriter {
    
    private static Logger log = Logger.getLogger(TextFileWriter.class);
    
    private String filename;
    private FileWriter outputstream;
    private BufferedWriter outputbuffer;
    
    public TextFileWriter(String _filename) {
        filename = _filename;
        try {
            outputstream = new FileWriter(filename);
            outputbuffer = new BufferedWriter(outputstream);
        } catch (java.io.IOException e) {
            log.error("TextFileWriter(): error creating outputstream for filename: " + filename);
        }
    }
    
    public void writeLine(String text) {
        try {
            outputbuffer.write(text);
            outputbuffer.newLine();
        } catch (java.io.IOException e) {
            log.error("writeLine(): error writing line to outputstream: " + text);
        }
    }
    
    public void close() {
        try {
            outputbuffer.close();
            outputstream.close();
        } catch (java.io.IOException e) {
            log.error("close(): error closing output buffer/stream");
        }
    }
    
}
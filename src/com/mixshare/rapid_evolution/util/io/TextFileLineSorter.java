package com.mixshare.rapid_evolution.util.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * This is a helper class which will read a text file from the file system
 * and return it as a more usable String.
 */
public class TextFileLineSorter {

    static private Logger log = Logger.getLogger(TextFileReader.class);

    private String text;
    
    public TextFileLineSorter(String filename) { sort(filename); }
    
    private void sort(String filename) {
        try {
            File file = new File(filename);
            if (file.exists()) {
                Vector lines = new Vector();
                StringBuffer textBuffer = new StringBuffer();
                FileReader inputstream = new FileReader(filename);
                BufferedReader inputbuffer = new BufferedReader(inputstream);
                String line;
                do {
                    line = inputbuffer.readLine();
                    if (line != null) {
                        lines.add(line);
                        textBuffer.append(line);
                        textBuffer.append("\n");
                    }
                } while (line != null);
                text = textBuffer.toString();
                inputbuffer.close();
                inputstream.close();
                java.util.Collections.sort(lines);
                TextFileWriter writer = new TextFileWriter(filename);
                for (int l = 0; l < lines.size(); ++l)
                    writer.writeLine(lines.get(l).toString());
                writer.close();
            }
        } catch (Exception e) {
            log.error("sort(): error Exception", e);
        }        
    }
    
    /**
     * Returns the contents of the text file as a String.
     */
    public String getText() { return text; }
    
    public static void main(String[] args) {
        new TextFileLineSorter("textBundle.properties");
    }
    
}

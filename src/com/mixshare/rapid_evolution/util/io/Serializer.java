package com.mixshare.rapid_evolution.util.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/**
 * This is a helper class to serialize Objects to/from the file system.
 */
public class Serializer {

    static private Logger log = Logger.getLogger(Serializer.class);   
    
    /**
     * Saves a serializable Object to a location on the file system.
     * 
     * @return boolean true if successful
     */
    static public boolean saveData(Object data, String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream streamOut = new ObjectOutputStream(fileOut);
            streamOut.writeObject(data);
            streamOut.close();
            fileOut.close();
            return true;
        } catch (Exception e) {
            log.error("saveData(): error Exception", e);           
        }
        return false;
    }
    
    /**
     * Reads a serialized Object from a location on the file system.
     * 
     * @return Object if read successfully, null otherwise
     */
    static public Object readData(String filePath) {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream in = new DecompressibleInputStream(fileIn);
            Object data = in.readObject();
            in.close();
            fileIn.close();
            return data;
        } catch (Exception e) {
            log.error("readData(): error Exception", e);
        }
        return null;
    }
        
}

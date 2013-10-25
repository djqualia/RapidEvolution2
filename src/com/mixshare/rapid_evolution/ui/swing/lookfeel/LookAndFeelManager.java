package com.mixshare.rapid_evolution.ui.swing.lookfeel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.SkinManager;
import rapid_evolution.util.OSHelper;

public class LookAndFeelManager {

    private static Logger log = Logger.getLogger(LookAndFeelManager.class);
    
    static public void setLookAndFeel(String lookAndFeelName) {
        setLookAndFeel(lookAndFeelName, true);
    }
    static public void setLookAndFeel(String lookAndFeelName, boolean updateUI) {
        try {
//            Toolkit.getDefaultToolkit().setDynamicLayout(false);
//            System.setProperty("sun.awt.noerasebackground","true");
            LookAndFeelInfo[] inf = UIManager.getInstalledLookAndFeels();
            for (int i = 0; i < inf.length; ++i) {
                if (inf[i].getName().equals(lookAndFeelName)) {
                    UIManager.setLookAndFeel(inf[i].getClassName());
                    if (SkinManager.instance != null)
                        SkinManager.instance.populateColorTypeIdMap();
                    if (isSubstanceLAF()) {                           
                    } else {
                    }
                }
            }
//            Toolkit.getDefaultToolkit().setDynamicLayout(true);
//            System.setProperty("sun.awt.noerasebackground","true");
            if (updateUI) {
                try {
                    SwingUtilities.updateComponentTreeUI(SkinManager.instance.getFrame("main_frame"));
                } catch (Exception e) {
                    log.trace("setLookAndFeel(): error Exception", e);
                }
                SkinManager.instance.Reset();
            }
        } catch (Exception e) {
            log.error("setLookAndFeel(): error Exception", e);
            // attempt to default to "Metal" L&F if there are any problems
            if (!lookAndFeelName.equals("Metal"))
                setLookAndFeel("Metal", updateUI);
        }
    }

    static public void saveCurrentLookAndFeel() {
        saveLookAndFeel(OptionsUI.instance.lookandfeelcombo.getSelectedItem().toString());
    }

    static public void saveLookAndFeel(String looknfeelName) {
        try {
            FileWriter outputstream = new FileWriter(OSHelper.getWorkingDirectory() + "/" + "lookfeel.cfg");
            BufferedWriter outputbuffer = new BufferedWriter(outputstream);
            outputbuffer.write(looknfeelName);
            outputbuffer.newLine();
            outputbuffer.write(SkinManager.instance.current_skin_filename);
            outputbuffer.newLine();
            outputbuffer.write((SkinManager.lastSkinName == null) ? "" : SkinManager.lastSkinName);
            outputbuffer.close();
            outputstream.close();
        } catch (Exception e) {
            log.error("SaveLookAndFeel(): error", e);
        }
    }
    
    static public String loadLookAndFeel() {
        String looknfeel = null;
        try {
            String looknfeelFilename = "lookfeel.cfg";
            File file = OSHelper.getFileBackwardsCompatible(looknfeelFilename);
            if (file.exists()) {
                FileReader inputstream = new FileReader(file.getAbsoluteFile());
                BufferedReader inputbuffer = new BufferedReader(inputstream);
                looknfeel = inputbuffer.readLine();
                String skinname = inputbuffer.readLine();
                if (skinname != null) {
                    SkinManager.instance.current_skin_filename = skinname;          
                    SkinManager.lastSkinName = inputbuffer.readLine();
                }
                inputbuffer.close();
                inputstream.close();
                setLookAndFeel(looknfeel, false);
            }   
        } catch (Exception e) {
            log.error("loadLookAndFeel(): error", e);
        }
        return looknfeel;
    }
    
    static public String getLookAndFeelName() { return UIManager.getLookAndFeel().getName(); }

    static public boolean isSubstanceLAF() { return (getLookAndFeelName().indexOf("Substance") >= 0); }
    
}

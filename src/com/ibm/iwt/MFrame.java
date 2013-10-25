package com.ibm.iwt;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.event.WindowChangeEvent;
import com.mixshare.rapid_evolution.audio.tags.TagWriteJobBuffer;

public class MFrame extends JFrame implements WindowListener {

    private static Logger log = Logger.getLogger(MFrame.class);
	
        /**
         * Creates an IFrame.
         */
        public MFrame()
        {
                super();
                setDefaultCloseOperation(IDialog.DO_NOTHING_ON_CLOSE);
                addWindowListener(this);
        }

        public void windowClosed(WindowChangeEvent e)
        {
        }

        /**
         * Does nothing.
         * @param e the WindowEvent
         */
        public void windowClosing(WindowEvent e)
        {
          if (SkinManager.instance.isChanging()) return;
          String addonText = TagWriteJobBuffer.hasWorkRemaining() ? (" (" + TagWriteJobBuffer.getSize() + " " + SkinManager.instance.getMessageText("tags_still_being_written") + ")") : "";                    
          int n = IOptionPane.showConfirmDialog(
              this,
              SkinManager.instance.getDialogMessageText("exit_confirm") + addonText,
              SkinManager.instance.getDialogMessageTitle("exit_confirm"),
              IOptionPane.YES_NO_OPTION);
          if (n == 0) {
            setVisible(false);
          }
        }

        /**
         * Does nothing.
         * @param e the WindowEvent
         */
        public void windowClosed(WindowEvent e)
        {
        }

        public void windowActivated(WindowEvent e)
        {
        }

        public void windowIconified(WindowEvent e)
        {
        }

        public void windowDeiconified(WindowEvent e)
        {
        }

        public void windowOpened(WindowEvent e)
        {
        }

        public void windowDeactivated(WindowEvent e)
        {
        }
        
}

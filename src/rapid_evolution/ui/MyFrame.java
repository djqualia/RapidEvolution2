package rapid_evolution.ui;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;

public class MyFrame extends JFrame implements java.awt.event.WindowListener {

    private static Logger log = Logger.getLogger(MyFrame.class);
    
    public MyFrame() {
        super();
//        addWindowListener(this);
    }
    public void handleQuit() {
        RapidEvolution.instance.handleQuit();
    }
    
	public void windowActivated(java.awt.event.WindowEvent e) {};
	public void windowClosed(java.awt.event.WindowEvent e) {};
	public void windowClosing(java.awt.event.WindowEvent e) {};
	public void windowDeactivated(java.awt.event.WindowEvent e) {
	    if (log.isTraceEnabled()) log.trace("windowDeactivated(): " + e);
	};
	public void windowDeiconified(java.awt.event.WindowEvent e) {};
	public void windowIconified(java.awt.event.WindowEvent e) {};
	public void windowOpened(java.awt.event.WindowEvent e) {};
}

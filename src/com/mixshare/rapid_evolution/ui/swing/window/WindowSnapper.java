package com.mixshare.rapid_evolution.ui.swing.window;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class WindowSnapper extends ComponentAdapter {

    public WindowSnapper() { }
    
    private boolean locked = false;
    private int sd = 5;
    
    public void componentMoved(ComponentEvent evt) {
        if(locked) return;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int nx = evt.getComponent().getX();
        int ny = evt.getComponent().getY();
        // top
        if(ny < 0+sd) {
            ny = 0;
        }
        // left
        if(nx < 0+sd) {
            nx = 0;
        }
        // right
        if(nx > size.getWidth()-evt.getComponent().getWidth()-sd) {
            nx = (int)size.getWidth()-evt.getComponent().getWidth();
        }
        // bottom
        if(ny > size.getHeight()-evt.getComponent().getHeight()-sd) {
            ny = (int)size.getHeight()-evt.getComponent().getHeight();
        }
        // make sure we don't get into a recursive loop when the
        // set location generates more events
        locked = true;
        evt.getComponent().setLocation(nx,ny);
        locked = false;
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hack #123: Window Snapping");
        JLabel label = new JLabel("Move this window's title bar to demonstrate screen edge snapping.");
        frame.getContentPane().add(label);
        frame.pack();
        
        frame.addComponentListener(new WindowSnapper());
        frame.setVisible(true);
    }
}

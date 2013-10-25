package rapid_evolution.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import rapid_evolution.SongLinkedList;

import com.mixshare.rapid_evolution.ui.swing.checkbox.RECheckBox;

public class RatingToolBarFlyWeight extends JPanel {

    private static Logger log = Logger.getLogger(RatingToolBarFlyWeight.class);
        
    private static ImageIcon selected_icon = null;
    private static ImageIcon icon = null;
    
    public JCheckBox rating1checkbox = new RECheckBox();
    public JCheckBox rating2checkbox = new RECheckBox();
    public JCheckBox rating3checkbox = new RECheckBox();
    public JCheckBox rating4checkbox = new RECheckBox();
    public JCheckBox rating5checkbox = new RECheckBox();  
    private char rating;
    public char getRating() { return rating; }
    
    private static SongLinkedList song = null;
    public static SongLinkedList getEditedSong() { return song; }
    public static void setEditedSong(SongLinkedList edited_song) {
        song = edited_song;
    }    
        
    public static HashMap all_flyweights = new HashMap();
    
    public String toString() {
        String result = "";
        if (rating == 1) result = "1";
        else if (rating == 2) result = "2";
        else if (rating == 3) result = "3";
        else if (rating == 4) result = "4";
        else if (rating == 5) result = "5";
        return result + " [" + super.toString() + "]";
    }
    
    public RatingToolBarFlyWeight(char rating) {
        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);
//        setOrientation(JToolBar.HORIZONTAL);
        add(rating1checkbox);
        add(rating2checkbox);
        add(rating3checkbox);
        add(rating4checkbox);
        add(rating5checkbox);
//        setMargin(new Insets(0,0,0,0));
        rating1checkbox.setMargin(new Insets(0,0,0,0));
        rating2checkbox.setMargin(new Insets(0,0,0,0));
        rating3checkbox.setMargin(new Insets(0,0,0,0));
        rating4checkbox.setMargin(new Insets(0,0,0,0));
        rating5checkbox.setMargin(new Insets(0,0,0,0));
        rating1checkbox.setEnabled(false);
        rating2checkbox.setEnabled(false);
        rating3checkbox.setEnabled(false);
        rating4checkbox.setEnabled(false);
        rating5checkbox.setEnabled(false);
        rating1checkbox.addMouseListener(new RatingToolBarMouse((char)1, this));
        rating2checkbox.addMouseListener(new RatingToolBarMouse((char)2, this));
        rating3checkbox.addMouseListener(new RatingToolBarMouse((char)3, this));
        rating4checkbox.addMouseListener(new RatingToolBarMouse((char)4, this));
        rating5checkbox.addMouseListener(new RatingToolBarMouse((char)5, this));
        setIcons();
        setRating(rating);
        all_flyweights.put(this, null);
    }
    
    public void setForeground(Color color) {
        super.setForeground(color);
        if (rating1checkbox != null) {
	        rating1checkbox.setForeground(color);
	        rating2checkbox.setForeground(color);
	        rating3checkbox.setForeground(color);
	        rating4checkbox.setForeground(color);
	        rating5checkbox.setForeground(color);
        }
    }
    
    public void setBackground(Color color) {
        super.setBackground(color);
        if (rating1checkbox != null) {
	        rating1checkbox.setBackground(color);
	        rating2checkbox.setBackground(color);
	        rating3checkbox.setBackground(color);
	        rating4checkbox.setBackground(color);
	        rating5checkbox.setBackground(color);
        }
    }
    
    public static void resetIcons() {
        selected_icon = null;
        icon = null;
        
        log.debug("resetIcons(): # flyweights=" + all_flyweights.keySet().size());
        Iterator weights = all_flyweights.keySet().iterator();
        while (weights.hasNext()) {
            RatingToolBarFlyWeight weight = (RatingToolBarFlyWeight)weights.next();
            weight.setIcons();
        }
        
    }
    public void setIcons() {        
        if (selected_icon == null) {
            selected_icon = SkinManager.instance.getIcon("table_star_selected_icon");
            log.debug("setIcons(): selected_icon=" + selected_icon);
        }
        if (icon == null) {
            icon = SkinManager.instance.getIcon("table_star_icon");
            log.debug("setIcons(): icon=" + icon);
        }
        rating1checkbox.setSelectedIcon(selected_icon);
        rating1checkbox.setIcon(icon);
        rating1checkbox.setDisabledIcon(icon);
        rating1checkbox.setDisabledSelectedIcon(selected_icon);
        rating1checkbox.setPressedIcon(selected_icon);
        rating2checkbox.setSelectedIcon(selected_icon);
        rating2checkbox.setIcon(icon);
        rating2checkbox.setDisabledIcon(icon);
        rating2checkbox.setDisabledSelectedIcon(selected_icon);
        rating2checkbox.setPressedIcon(selected_icon);
        rating3checkbox.setSelectedIcon(selected_icon);
        rating3checkbox.setIcon(icon);
        rating3checkbox.setDisabledIcon(icon);
        rating3checkbox.setDisabledSelectedIcon(selected_icon);
        rating3checkbox.setPressedIcon(selected_icon);
        rating4checkbox.setSelectedIcon(selected_icon);
        rating4checkbox.setIcon(icon);
        rating4checkbox.setDisabledIcon(icon);
        rating4checkbox.setDisabledSelectedIcon(selected_icon);
        rating4checkbox.setPressedIcon(selected_icon);
        rating5checkbox.setSelectedIcon(selected_icon);
        rating5checkbox.setIcon(icon);
        rating5checkbox.setDisabledIcon(icon);
        rating5checkbox.setDisabledSelectedIcon(selected_icon);
        rating5checkbox.setPressedIcon(selected_icon);        
        int width = Math.max(icon.getIconWidth() + 1, selected_icon.getIconWidth() + 1);
        int height = Math.max(icon.getIconHeight() + 1, selected_icon.getIconHeight() + 1);
        rating1checkbox.setMaximumSize(new Dimension(width, height));
        rating2checkbox.setMaximumSize(new Dimension(width, height));
        rating3checkbox.setMaximumSize(new Dimension(width, height));
        rating4checkbox.setMaximumSize(new Dimension(width, height));
        rating5checkbox.setMaximumSize(new Dimension(width, height));
        rating1checkbox.setMinimumSize(new Dimension(width, height));
        rating2checkbox.setMinimumSize(new Dimension(width, height));
        rating3checkbox.setMinimumSize(new Dimension(width, height));
        rating4checkbox.setMinimumSize(new Dimension(width, height));
        rating5checkbox.setMinimumSize(new Dimension(width, height));
        rating1checkbox.setPreferredSize(new Dimension(width, height));
        rating2checkbox.setPreferredSize(new Dimension(width, height));
        rating3checkbox.setPreferredSize(new Dimension(width, height));
        rating4checkbox.setPreferredSize(new Dimension(width, height));
        rating5checkbox.setPreferredSize(new Dimension(width, height));
    }
        
    public void setRating(char value) {
        rating = value;
        if (value == 0) {
            rating1checkbox.setSelected(false);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);
        } else if (value == 1) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(false);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);            
        } else if (value == 2) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(false);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);            
        } else if (value == 3) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(false);
            rating5checkbox.setSelected(false);            
        } else if (value == 4) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(true);
            rating5checkbox.setSelected(false);            
        } else if (value == 5) {
            rating1checkbox.setSelected(true);
            rating2checkbox.setSelected(true);
            rating3checkbox.setSelected(true);
            rating4checkbox.setSelected(true);
            rating5checkbox.setSelected(true);            
        }                 
    }
    
   	
}
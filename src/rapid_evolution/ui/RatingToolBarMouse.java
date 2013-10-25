package rapid_evolution.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import rapid_evolution.*;


public class RatingToolBarMouse extends MouseAdapter {
        
    private static Logger log = Logger.getLogger(RatingToolBarMouse.class);
    
    int rating = 0;
    RatingToolBarFlyWeight toolbar = null;
    public RatingToolBarMouse(char rating, RatingToolBarFlyWeight toolbar) {
        this.toolbar = toolbar;
        this.rating = rating;
    }
    
  public void mouseClicked(MouseEvent e) {
    if (rating == 1) {
          if (toolbar.rating2checkbox.isSelected() || toolbar.rating3checkbox.isSelected()
                  || toolbar.rating4checkbox.isSelected()
                  || toolbar.rating5checkbox.isSelected())
              toolbar.rating1checkbox.setSelected(true);
          else toolbar.rating1checkbox.setSelected(!toolbar.rating1checkbox.isSelected());
          toolbar.rating2checkbox.setSelected(false);
          toolbar.rating3checkbox.setSelected(false);
          toolbar.rating4checkbox.setSelected(false);
          toolbar.rating5checkbox.setSelected(false);
    } else if (rating == 2) {
          if (toolbar.rating3checkbox.isSelected() || toolbar.rating4checkbox.isSelected()
                  || toolbar.rating5checkbox.isSelected())
              toolbar.rating2checkbox.setSelected(true);
          else toolbar.rating2checkbox.setSelected(!toolbar.rating2checkbox.isSelected());
          toolbar.rating3checkbox.setSelected(false);
          toolbar.rating4checkbox.setSelected(false);
          toolbar.rating5checkbox.setSelected(false);
          if (toolbar.rating2checkbox.isSelected()) {
              toolbar.rating1checkbox.setSelected(true);
          } else {
              toolbar.rating1checkbox.setSelected(false);
          }
      } else if (rating == 3) {
          if (toolbar.rating4checkbox.isSelected() || toolbar.rating5checkbox.isSelected())
              toolbar.rating3checkbox.setSelected(true);
          else toolbar.rating3checkbox.setSelected(!toolbar.rating3checkbox.isSelected());
          toolbar.rating4checkbox.setSelected(false);
          toolbar.rating5checkbox.setSelected(false);
          if (toolbar.rating3checkbox.isSelected()) {
              toolbar.rating1checkbox.setSelected(true);
              toolbar.rating2checkbox.setSelected(true);
          } else {
              toolbar.rating1checkbox.setSelected(false);
              toolbar.rating2checkbox.setSelected(false);                  
          }
      } else if (rating == 4) {
          if (toolbar.rating5checkbox.isSelected())
              toolbar.rating4checkbox.setSelected(true);
          else toolbar.rating4checkbox.setSelected(!toolbar.rating4checkbox.isSelected());
          toolbar.rating5checkbox.setSelected(false);
          if (toolbar.rating4checkbox.isSelected()) {
              toolbar.rating1checkbox.setSelected(true);
              toolbar.rating2checkbox.setSelected(true);
              toolbar.rating3checkbox.setSelected(true);
          } else {
              toolbar.rating1checkbox.setSelected(false);
              toolbar.rating2checkbox.setSelected(false);
              toolbar.rating3checkbox.setSelected(false);                  
          }
      } else if (rating == 5) {
          if (toolbar.rating5checkbox.isSelected()) {
              toolbar.rating1checkbox.setSelected(false);
              toolbar.rating2checkbox.setSelected(false);
              toolbar.rating3checkbox.setSelected(false);
              toolbar.rating4checkbox.setSelected(false);
              toolbar.rating5checkbox.setSelected(false);
          } else {
              toolbar.rating1checkbox.setSelected(true);
              toolbar.rating2checkbox.setSelected(true);
              toolbar.rating3checkbox.setSelected(true);
              toolbar.rating4checkbox.setSelected(true);
              toolbar.rating5checkbox.setSelected(true);
          }         
      }      
      char selected_rating = 0;
      if (toolbar.rating5checkbox.isSelected()) selected_rating = 5;
      else if (toolbar.rating4checkbox.isSelected()) selected_rating = 4;
      else if (toolbar.rating3checkbox.isSelected()) selected_rating = 3;
      else if (toolbar.rating2checkbox.isSelected()) selected_rating = 2;
      else if (toolbar.rating1checkbox.isSelected()) selected_rating = 1;          
      SongLinkedList song = RatingToolBarFlyWeight.getEditedSong();
      if (log.isDebugEnabled()) log.debug("mouseClicked(): selected rating=" + ((int)selected_rating) + ", toolbar=" + song.getRatingBar());
      song.setRating(selected_rating);
      SongDB.instance.UpdateSong(song, new OldSongValues(song));
  }

  public void mousePressed(MouseEvent e) {
  }
  public void mouseReleased(MouseEvent e) {
  }

}
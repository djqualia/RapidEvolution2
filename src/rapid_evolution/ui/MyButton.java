package rapid_evolution.ui;

import javax.swing.JButton;
import java.awt.Graphics;

import java.awt.geom.RoundRectangle2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;

import com.mixshare.rapid_evolution.ui.swing.button.REButton;

public class MyButton extends REButton {

    public MyButton() {
        super();
    }
    
    String label = "";
    public MyButton(String label) {
        super(label);
        this.label = label;
    }

    /**
     * gets the label
     * @see setLabel
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * sets the label
     * @see getLabel
     */
    public void setLabel(String label) {
        super.setLabel(label);
        this.label = label;
    }    
    
    public void paintComponent(Graphics g) {
        if (this.getBorder() instanceof PillBorder) {
            // draw the label centered in the button
            g.fillRect(this.getLocation().x, this.getLocation().y, getSize().width, getSize().height);
            /*Font f = getFont();
            if(f != null) {
      	  FontMetrics fm = getFontMetrics(getFont());      	  
      	  g.setColor(new Color(255,255,255));//getForeground());      	      	  
      	  g.drawString(label,
      	        (getSize().width - 1)/2 - fm.stringWidth(label)/2,
      	      (getSize().height - 1)/2 + fm.getMaxDescent());
            }*/
        } else {
            super.paintComponent(g);
        }
    }
    
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
    }
}

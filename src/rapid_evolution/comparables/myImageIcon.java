package rapid_evolution.comparables;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import java.awt.Component;
import java.awt.Graphics;

import rapid_evolution.*;
import rapid_evolution.ui.*;

import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

public class myImageIcon extends JPanel implements Comparable {
    public static int icon_size = 64;

    private MyIcon icon;
    
    public myImageIcon(String filename, String cmpstr, String tooltip) {
        super();
        icon = new MyIcon(filename);
        JLabel label = new JLabel(icon);
        label.setToolTipText(tooltip);
        this.tooltipText = tooltip;
        this.add(label);
        this.cmpstr = cmpstr;
        icon.setSize(icon_size, icon_size);
        this.setToolTipText(tooltip);
        SkinManager.instance.readSharedComponentSettings(this, null);
        SkinManager.instance.readSharedJComponentSettings(this, null);
    }
    
    public int getImageLoadStatus() {
        return icon.getImageLoadStatus();
    }

    private String tooltipText;

    public String getToolTipText() {
        if ((tooltipText != null) && tooltipText.equals(""))
            return null;
        return tooltipText;
    }

    public int compareTo(Object b1) {
        if (b1 instanceof myImageIcon) {
            myImageIcon icon = (myImageIcon) b1;
            return cmpstr.compareToIgnoreCase(icon.cmpstr);
        }
        return -1;
    }

    public boolean equals(Object b) {
        if (b instanceof myImageIcon) {
            myImageIcon icon = (myImageIcon) b;
            return cmpstr.equalsIgnoreCase(icon.cmpstr);
        }
        return super.equals(b);
    }

    public int hashCode() {
        return cmpstr.hashCode();
        // super.hashCode();
    }

    public String toString() {
        return super.toString();
    }

    public String cmpstr;

    class MyIcon extends ImageIcon {

        int width, height;

        public MyIcon(String fileName) {
            super(fileName);
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (getImage() != null) {
                g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(),
                        c);
            }
        }

        public void setSize(int w, int h) {
            width = w;
            height = h;
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }
    }

    static public String formAlbumCoverToolTip(String artist, String album) {
        if ((album == null) || album.equals(""))
            return "<<no album>>";
        StringBuffer result = new StringBuffer();
        if ((artist != null) && !artist.equals("")) {
            result.append(artist);
            result.append(" - ");
        }
        result.append(album);
        return result.toString();
    }
    
    private JToolTip tooltip;

    public JToolTip createToolTip() {
        if (SkinManager.instance.use_custom_tooltips) {
            if (tooltip == null) {
                tooltip = new CustomToolTip();
                tooltip.setComponent(this);
            }
            return tooltip;
        } else {
            return super.createToolTip();
        }
    }
    
};

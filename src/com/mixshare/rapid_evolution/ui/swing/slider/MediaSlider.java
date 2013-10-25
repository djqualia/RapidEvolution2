package com.mixshare.rapid_evolution.ui.swing.slider;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;

import org.apache.log4j.Logger;

import com.mixshare.rapid_evolution.ui.swing.tooltip.CustomToolTip;

import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.SkinManager;

/**
 * This is a skinnable JSlider. It uses a custom UI that takes a series of
 * images and paints the jSlider. The track is made up of three images, a left,
 * center and right. In most cases, the center will be a single pixel wide or
 * tall depending on the orientation of the slider. This is to conserve space
 * and speed up processing. The center image will be stretched by the UI to
 * paint the entire track in between the left and right images of the track.
 * 
 * The thumb has two images, pressed and unpressed. If pressed is null, the
 * default is unpressed. If any image besides pressed is null, the default
 * component will be painted instead.
 */
public class MediaSlider extends JSlider implements ChangeListener {

    private static Logger log = Logger.getLogger(MediaSlider.class);

    /**
     * This represents the left end of the jslider track
     */
    private String LEFT_TRACK;

    /**
     * This represents the right end of the jslider track
     */
    private String RIGHT_TRACK;

    /**
     * This represents the center of the jslider track. This image should only
     * be 1 pixel wide for speed & space saving if the track remains the same
     * across the entire JSlider
     */
    private String CENTER_TRACK;

    /**
     * The thumb image
     */
    private String THUMB;

    /**
     * Optional value, this represents the thumb when the thumb is pressed with
     * the mouse
     */
    private String THUMB_PRESSED;

    private BufferedImage leftTrackImage;

    private BufferedImage rightTrackImage;

    private BufferedImage centerTrackImage;

    private BufferedImage thumbImage;

    private BufferedImage thumbPressedImage;

    public MediaSlider(int orientation, int min, int max, int value, boolean showTooltip) {
        super(orientation, min, max, value);
        init();
        this.showTooltip = showTooltip;
    }
    
    public MediaSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
        init();
    }
    
    public MediaSlider(String leftTrackName, String centerTrackName,
            String rightTrackName, String thumbName, String thumbPressedName) {

        LEFT_TRACK = leftTrackName;
        RIGHT_TRACK = rightTrackName;
        CENTER_TRACK = centerTrackName;
        THUMB = thumbName;
        THUMB_PRESSED = thumbPressedName;
        
        init();
    }
    
    private void init() {
        this.setFocusable(false);
        
        setImages();

//        setUI(new MediaSliderUI(this));

        this.addChangeListener(this);

    }

    /**
     * This only allows UIs that are subclassed from our own MediaSliderUI
     */
//    @Override
    public void setUI(SliderUI sliderUI) {
        if (sliderUI instanceof MediaSliderUI)
            super.setUI(sliderUI);
    }

    /**
     * Loads the images to be painted. This gets called anytime the theme
     * changes and the images need to be updated.
     */
    protected void setImages() {
        //setLeftTrackImage(convertIconToImage(SkinManager.instance.getIcon("slider_left_track_icon")));
        //setRightTrackImage(convertIconToImage(SkinManager.instance.getIcon("slider_right_track_icon")));
        //setCenterTrackImage(convertIconToImage(SkinManager.instance.getIcon("slider_center_track_icon")));
        //setThumbImage(convertIconToImage(SkinManager.instance.getIcon("slider_thumb_icon")));
        //setThumbPressedImage(convertIconToImage(SkinManager.instance.getIcon("slider_thumb_pressed_icon")));
    }

    /**
     * When the theme changes, load the new images and repaint the buffered
     * track image
     */
    public void updateTheme() {
        setImages();

        ((MediaSliderUI) getUI()).setDirty(true);
    }

    /**
     * Converts the image stored in an ImageIcon into a BufferedImage.
     */
    public static BufferedImage convertIconToImage(ImageIcon icon) {

        Image iconImage = icon.getImage();
        BufferedImage image = new BufferedImage(iconImage.getWidth(null),
                iconImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bufImageGraphics = image.createGraphics();
        bufImageGraphics.drawImage(icon.getImage(), 0, 0, null);

        bufImageGraphics.dispose();

        return image;
    }

    public BufferedImage getLeftTrackImage() {
        if ((SkinManager.instance != null) && (leftTrackImage == null)) setLeftTrackImage(convertIconToImage(SkinManager.instance.getIcon((this.getOrientation() == JSlider.HORIZONTAL) ? "horizontal_slider_left_track_icon" : "vertical_slider_left_track_icon")));        
        return leftTrackImage;
    }

    public BufferedImage getCenterTrackImage() {
        if ((SkinManager.instance != null) && (centerTrackImage == null)) setCenterTrackImage(convertIconToImage(SkinManager.instance.getIcon((this.getOrientation() == JSlider.HORIZONTAL) ? "horizontal_slider_center_track_icon" : "vertical_slider_center_track_icon")));
        return centerTrackImage;
    }

    public BufferedImage getRightTrackImage() {
        if ((SkinManager.instance != null) && (rightTrackImage == null)) setRightTrackImage(convertIconToImage(SkinManager.instance.getIcon((this.getOrientation() == JSlider.HORIZONTAL) ? "horizontal_slider_right_track_icon" : "vertical_slider_right_track_icon")));
        return rightTrackImage;
    }

    public BufferedImage getThumbImage() {
        if ((SkinManager.instance != null) && (thumbImage == null)) setThumbImage(convertIconToImage(SkinManager.instance.getIcon((this.getOrientation() == JSlider.HORIZONTAL) ? "horizontal_slider_thumb_icon" : "vertical_slider_thumb_icon")));
        return thumbImage;
    }

    public BufferedImage getThumbPressedImage() {
        if ((SkinManager.instance != null) && (thumbPressedImage == null)) setThumbPressedImage(convertIconToImage(SkinManager.instance.getIcon((this.getOrientation() == JSlider.HORIZONTAL) ? "horizontal_slider_thumb_pressed_icon" : "vertical_slider_thumb_pressed_icon")));
        return thumbPressedImage;
    }

    public void setLeftTrackImage(Image image) {
        leftTrackImage = (BufferedImage) image;
    }
    public void setLeftTrackImage(ImageIcon imageIcon) {
        leftTrackImage = convertIconToImage(imageIcon);
    }

    public void setCenterTrackImage(Image image) {
        centerTrackImage = (BufferedImage) image;
    }
    public void setCenterTrackImage(ImageIcon imageIcon) {
        centerTrackImage = convertIconToImage(imageIcon);
    }

    public void setRightTrackImage(Image image) {
        rightTrackImage = (BufferedImage) image;
    }
    public void setRightTrackImage(ImageIcon imageIcon) {
        rightTrackImage = convertIconToImage(imageIcon);
    }

    public void setThumbImage(Image image) {
        thumbImage = (BufferedImage) image;
    }
    public void setThumbImage(ImageIcon imageIcon) {
        thumbImage = convertIconToImage(imageIcon);
    }

    public void setThumbPressedImage(Image image) {
        thumbPressedImage = (BufferedImage) image;
    }
    public void setThumbPressedImage(ImageIcon imageIcon) {
        thumbPressedImage = convertIconToImage(imageIcon);
    }

    public void stateChanged(ChangeEvent e) {
        if (showTooltip)
            this.setToolTipText(this.toString());        
    }
   
    private boolean showTooltip = true;
    public void setShowTooltip(boolean show) {
        this.showTooltip = show;
    }
    
    public String toString() {
        return Integer.toString(getValue());
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
    
}
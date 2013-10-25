/*
 * Copyright (c) 2005-2007 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jvnet.substance;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSliderUI;

import org.apache.log4j.Logger;
import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.border.SubstanceBorderPainter;
import org.jvnet.substance.button.BaseButtonShaper;
import org.jvnet.substance.color.ColorScheme;
import org.jvnet.substance.painter.ClassicGradientPainter;
import org.jvnet.substance.painter.SubstanceGradientPainter;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.icon.SubstanceIconFactory;

import rapid_evolution.ui.SkinManager;

/**
 * UI for sliders in <b>Substance</b> look and feel.
 * 
 * RE2 FIXES:
    1)         if (this.substanceFadeStateListener != null) {
            this.substanceFadeStateListener.unregisterListeners();
            this.substanceFadeStateListener = null;
        }


    2)         this.slider = slider;
                scrollTimer = new Timer(0, null);

                .. in constructor
 *  
 *  3)     public void uninstallUI(JComponent c) {
        try {
            super.uninstallUI(c);
        } catch (Exception e) {
            log.trace("uninstallUI(): error Exception", e);
        }
    }
    
    public void installUI(JComponent c) {
        try {
            super.installUI(c);
        } catch (Exception e) {
            log.trace("installUI(): error Exception", e);
        }
    }


    4) 1 more just diff it
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceSliderUI extends BasicSliderUI implements Trackable {

    private static Logger log = Logger.getLogger(SubstanceSliderUI.class);
    
    /**
     * Background delegate.
     */
    private static SubstanceFillBackgroundDelegate bgDelegate = new SubstanceFillBackgroundDelegate();

    /**
     * Surrogate button model for tracking the thumb transitions.
     */
    private ButtonModel thumbModel;

    /**
     * Listener for fade animations.
     */
    private RolloverControlListener substanceRolloverListener;

    /**
     * Listener on property change events.
     */
    private PropertyChangeListener substancePropertyChangeListener;

    /**
     * Listener for fade animations.
     */
    protected FadeStateListener substanceFadeStateListener;

    // private MouseMotionListener substanceMouseMotionListener;

    /**
     * Icon for horizontal sliders.
     */
    protected Icon horizontalIcon;

    /**
     * Icon for sliders without labels and ticks.
     */
    protected Icon roundIcon;

    /**
     * Icon for vertical sliders.
     */
    protected Icon verticalIcon;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
     */
    public static ComponentUI createUI(JComponent c) {
        return new SubstanceSliderUI((JSlider) c);
    }

    /**
     * Simple constructor.
     * 
     * @param slider
     *            Slider.
     */
    public SubstanceSliderUI(JSlider slider) {
        super(null);
        this.thumbModel = new DefaultButtonModel();
        // {
        // @Override
        // public void setRollover(boolean b) {
        // super.setRollover(b);
        // System.out.println("Rollover -> " + b);
        // }
        // };
        this.thumbModel.setArmed(false);
        this.thumbModel.setSelected(false);
        this.thumbModel.setPressed(false);
        this.thumbModel.setRollover(false);
        this.thumbModel.setEnabled(slider.isEnabled());
        this.slider = slider;
        scrollTimer = new Timer(0, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#calculateTrackRect()
     */
    @Override
    protected void calculateTrackRect() {
        super.calculateTrackRect();
        if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
            this.trackRect.y = 3
                    + (int) Math.ceil(SubstanceSizeUtils
                            .getFocusStrokeWidth(SubstanceSizeUtils
                                    .getComponentFontSize(this.slider)))
                    + this.insetCache.top;
        }
    }

    /**
     * Returns the rectangle of track for painting.
     * 
     * @return The rectangle of track for painting.
     */
    private Rectangle getPaintTrackRect() {
        int trackLeft = 0, trackRight = 0, trackTop = 0, trackBottom = 0;
        if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
            trackTop = 3 + this.insetCache.top + 2 * this.focusInsets.top;
            trackBottom = trackTop + this.getTrackWidth() - 1;
            trackRight = this.trackRect.width;
            return new Rectangle(this.trackRect.x + trackLeft, trackTop,
                    trackRight - trackLeft, trackBottom - trackTop);
        } else {
            if (this.slider.getComponentOrientation().isLeftToRight()) {
                trackLeft = 6 + this.insetCache.left + this.focusInsets.left;
                trackRight = trackLeft + this.getTrackWidth() - 1;
            } else {
                trackRight = this.slider.getWidth() - 8 - this.insetCache.right;
                trackLeft = trackRight - this.getTrackWidth() + 1;
            }
            trackBottom = this.trackRect.height - 1;
            return new Rectangle(trackLeft, this.trackRect.y + trackTop,
                    trackRight - trackLeft, trackBottom - trackTop);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintTrack(java.awt.Graphics)
     */
    @Override
    public void paintTrack(Graphics g) {
        Graphics2D graphics = (Graphics2D) g.create();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        boolean drawInverted = this.drawInverted();

        // Translate to the origin of the painting rectangle
        Rectangle paintRect = this.getPaintTrackRect();
        // System.out.println(paintRect);
        graphics.translate(paintRect.x, paintRect.y);

        // Width and height of the painting rectangle.
        int width = paintRect.width;
        int height = paintRect.height;

        ComponentState currState = ComponentState.getState(this.thumbModel,
                this.slider);
        ComponentState prevState = SubstanceCoreUtilities
                .getPrevComponentState(this.slider);

        SubstanceTheme trackTheme = SubstanceCoreUtilities.getTheme(
                this.slider, true);
        if (this.slider.isEnabled())
            trackTheme = trackTheme.getDefaultTheme();
        else
            trackTheme = trackTheme.getDisabledTheme();

        // trackTheme = trackTheme.getDefaultTheme();
        // if (!this.slider.isEnabled()) {
        // graphics.setComposite(TransitionLayout.getAlphaComposite(
        // this.slider, 0.7f, g));
        // }
        // System.out.println("Painting track " + width + ":" + height);
        this.paintSliderTrack(graphics, drawInverted, trackTheme, width,
                height, (this.slider.getOrientation() == JSlider.VERTICAL));
        // graphics.setComposite(TransitionLayout.getAlphaComposite(
        // this.slider, g));

        SubstanceTheme theme = SubstanceCoreUtilities.getTheme(this.slider,
                currState, true, true);
        FadeState fadeState = SubstanceFadeUtilities.getFadeState(this.slider,
                FadeKind.ROLLOVER, FadeKind.SELECTION, FadeKind.PRESS);
        if (fadeState != null) {
            SubstanceTheme prevTheme = SubstanceCoreUtilities.getTheme(
                    this.slider, prevState, true, true);
            float cyclePos = fadeState.getFadePosition();
            if (!fadeState.isFadingIn())
                cyclePos = 10.0f - cyclePos;

            // System.out.println(prevTheme.getDisplayName() + " -> "
            // + theme.getDisplayName() + " at " + cyclePos);
            if (prevState != ComponentState.DEFAULT) {
                graphics.setComposite(TransitionLayout.getAlphaComposite(
                        this.slider, 1.0f - cyclePos / 10.0f, g));
                this.paintSliderTrackSelected(graphics, drawInverted,
                        paintRect, prevTheme, width, height);
            }
            if (currState != ComponentState.DEFAULT) {
                graphics.setComposite(TransitionLayout.getAlphaComposite(
                        this.slider, cyclePos / 10.0f, g));
                this.paintSliderTrackSelected(graphics, drawInverted,
                        paintRect, theme, width, height);
            }
        } else {
            boolean hasFill = currState.isKindActive(FadeKind.ROLLOVER)
                    || currState.isKindActive(FadeKind.PRESS)
                    || SubstanceCoreUtilities.isControlAlwaysPaintedActive(
                            this.slider, true);
            if (hasFill) {
                this.paintSliderTrackSelected(graphics, drawInverted,
                        paintRect, theme, width, height);
            }
        }

        // graphics.setColor(Color.blue);
        // graphics.translate(-paintRect.x, -paintRect.y);
        // graphics.draw(this.getPaintTrackRect());
        // graphics.setColor(Color.green);
        // graphics.draw(this.trackRect);
        graphics.dispose();
    }

    /**
     * Paints the slider track.
     * 
     * @param graphics
     *            Graphics.
     * @param drawInverted
     *            Indicates whether the value-range shown for the slider is
     *            reversed.
     * @param theme
     *            Theme.
     * @param width
     *            Track width.
     * @param height
     *            Track height.
     * @param toRotate
     *            Indicates whether the slider track image should be rotated (<code>true</code>
     *            if the slider is vertical).
     */
    private void paintSliderTrack(Graphics2D graphics, boolean drawInverted,
            SubstanceTheme theme, int width, int height, boolean toRotate) {
        // ColorScheme colorScheme = theme.getColorScheme();

        SubstanceGradientPainter gradientPainter = new ClassicGradientPainter();
        ColorScheme fillColorScheme = theme.getColorScheme();
        SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
                .getBorderPainter(this.slider, gradientPainter);

        // some strange bug in clipping a rotated Graphics removes the
        // last pixel row during the border painting. So, for vertical sliders
        // we create a temporary image, paint the slider there and then rotate
        // the image
        if (toRotate) {
            BufferedImage image = SubstanceCoreUtilities.getBlankImage(
                    height + 1, width + 1);
            this.paintSliderTrack((Graphics2D) image.getGraphics(),
                    drawInverted, theme, height, width, false);
            BufferedImage track = SubstanceImageCreator.getRotated(image, 3);
            graphics.drawImage(track, 0, 0, null);
            return;
        }

        int borderDelta = (int) Math.floor(SubstanceSizeUtils
                .getBorderStrokeWidth(SubstanceSizeUtils
                        .getComponentFontSize(slider)) / 2.0);
        float radius = SubstanceSizeUtils
                .getClassicButtonCornerRadius(SubstanceSizeUtils
                        .getComponentFontSize(slider)) / 2.0f;
        Shape contour = BaseButtonShaper.getBaseOutline(width + 1, height + 1,
                radius, null, borderDelta);

        BufferedImage fillTrackImage = gradientPainter.getContourBackground(
                width, height, contour, false, fillColorScheme,
                fillColorScheme, 0, false, false);
        graphics.drawImage(fillTrackImage, 0, 0, null);

        ColorScheme borderScheme = theme.getBorderTheme().getColorScheme();
        int borderThickness = (int) SubstanceSizeUtils
                .getBorderStrokeWidth(SubstanceSizeUtils
                        .getComponentFontSize(this.slider));
        GeneralPath contourInner = BaseButtonShaper.getBaseOutline(width + 1,
                height + 1, radius, null, borderThickness + borderDelta);
        borderPainter.paintBorder(graphics, slider, width + 1, height + 1,
                contour, contourInner, borderScheme, borderScheme, 0, false);
    }

    /**
     * Paints the selected part of the slider track.
     * 
     * @param graphics
     *            Graphics.
     * @param drawInverted
     *            Indicates whether the value-range shown for the slider is
     *            reversed.
     * @param paintRect
     *            Selected portion.
     * @param theme
     *            Theme.
     * @param width
     *            Track width.
     * @param height
     *            Track height.
     */
    private void paintSliderTrackSelected(Graphics2D graphics,
            boolean drawInverted, Rectangle paintRect, SubstanceTheme theme,
            int width, int height) {
        Insets insets = this.slider.getInsets();
        insets.top /= 2;
        insets.left /= 2;
        insets.bottom /= 2;
        insets.right /= 2;

        // boolean isDark = SubstanceCoreUtilities.isThemeDark(theme);
        // ColorScheme colorScheme = theme.getColorScheme();
        //
        // int radius = 2;
        // Color fillColor1 = isDark ? colorScheme.getUltraLightColor()
        // : colorScheme.getUltraLightColor();
        // Color fillColor2 = isDark ? colorScheme.getLightColor() : colorScheme
        // .getLightColor();

        SubstanceGradientPainter gp = SubstanceCoreUtilities
                .getGradientPainter(this.slider);
        SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
                .getBorderPainter(this.slider, gp);
        float radius = SubstanceSizeUtils
                .getClassicButtonCornerRadius(SubstanceSizeUtils
                        .getComponentFontSize(slider)) / 2.0f;
        int borderDelta = (int) Math.floor(SubstanceSizeUtils
                .getBorderStrokeWidth(SubstanceSizeUtils
                        .getComponentFontSize(slider)) / 2.0);

        // fill selected portion
        if (this.slider.isEnabled()) {
            if (this.slider.getOrientation() == SwingConstants.HORIZONTAL) {
                int middleOfThumb = this.thumbRect.x
                        + (this.thumbRect.width / 2) - paintRect.x;
                int fillMinX;
                int fillMaxX;

                // graphics.setPaint(new GradientPaint(0, 1, fillColor1, 0,
                // height - 1, fillColor2));

                if (drawInverted) {
                    fillMinX = middleOfThumb;
                    fillMaxX = width;
                } else {
                    fillMinX = 0;
                    fillMaxX = middleOfThumb;
                }

                int fillWidth = fillMaxX - fillMinX;
                int fillHeight = height + 1;
                if ((fillWidth > 0) && (fillHeight > 0)) {
                    Shape contour = BaseButtonShaper.getBaseOutline(fillWidth,
                            fillHeight, radius, null, borderDelta);
                    BufferedImage im = gp.getContourBackground(fillWidth,
                            fillHeight, contour, false, theme.getColorScheme(),
                            theme.getColorScheme(), 0.0f, false, false);
                    borderPainter.paintBorder(im.getGraphics(), this.slider,
                            fillWidth, fillHeight, contour, null, theme
                                    .getColorScheme(), theme.getColorScheme(),
                            0.0f, false);
                    graphics.drawImage(im, fillMinX, 0, null);
                }
            } else {
                int middleOfThumb = this.thumbRect.y
                        + (this.thumbRect.height / 2) - paintRect.y;
                int fillMinY;
                int fillMaxY;

                if (this.drawInverted()) {
                    fillMinY = 0;
                    fillMaxY = middleOfThumb;
                } else {
                    fillMinY = middleOfThumb;
                    fillMaxY = height + 1;
                }

                int fillWidth = fillMaxY - fillMinY;
                int fillHeight = width + 1;
                if ((fillWidth > 0) && (fillHeight > 0)) {
                    Shape contour = BaseButtonShaper.getBaseOutline(fillWidth,
                            fillHeight, radius, null, borderDelta);
                    BufferedImage im = gp.getContourBackground(fillWidth,
                            fillHeight, contour, false, theme.getColorScheme(),
                            theme.getColorScheme(), 0.0f, false, false);
                    borderPainter.paintBorder(im.getGraphics(), this.slider,
                            fillWidth, fillHeight, contour, null, theme
                                    .getColorScheme(), theme.getColorScheme(),
                            0.0f, false);
                    im = SubstanceImageCreator.getRotated(im, 1);
                    graphics.drawImage(im, 0, fillMinY, null);
                }
            }
        }
    }

    @Override
    protected Dimension getThumbSize() {
        Icon thumbIcon = this.getIcon();
        // if (slider.getOrientation() == JSlider.HORIZONTAL) {
        // thumbIcon = UIManager.getIcon("Slider.horizontalThumbIcon");
        // } else {
        // thumbIcon = UIManager.getIcon("Slider.verticalThumbIcon");
        // }
        // System.out.println(thumbIcon.getIconWidth() + ":" +
        // thumbIcon.getIconHeight());
        return new Dimension(thumbIcon.getIconWidth(), thumbIcon
                .getIconHeight());
    }

    /**
     * Returns the thumb icon for the associated slider.
     * 
     * @return The thumb icon for the associated slider.
     */
    protected Icon getIcon() {
        if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
            if (this.slider.getPaintTicks() || this.slider.getPaintLabels())
                return this.horizontalIcon;
            else
                return this.roundIcon;
        } else {
            if (this.slider.getPaintTicks() || this.slider.getPaintLabels())
                return this.verticalIcon;
            else
                return this.roundIcon;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintThumb(java.awt.Graphics)
     */
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D graphics = (Graphics2D) g.create();
        // graphics.setComposite(TransitionLayout.getAlphaComposite(slider));
        Rectangle knobBounds = this.thumbRect;
        // System.out.println(thumbRect);

        graphics.translate(knobBounds.x, knobBounds.y);

        Icon icon = this.getIcon();
        if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
            int yOffset = 0;
            // if (this.slider.getPaintTicks()) {
            // yOffset = 1;
            // } else {
            // yOffset = this.slider.getPaintLabels() ? 1 : -1;
            // }
            if (icon != null)
                icon.paintIcon(this.slider, graphics, -1, yOffset);
        } else {
            if (this.slider.getComponentOrientation().isLeftToRight()) {
                int xOffset = 0;
                // if (this.slider.getPaintTicks())
                // xOffset = 4;
                // else
                // xOffset = this.slider.getPaintLabels() ? 2 : 0;
                if (icon != null)
                    icon.paintIcon(this.slider, graphics, xOffset, -1);
            } else {
                int xOffset = 0;
                // if (this.slider.getPaintTicks())
                // xOffset = -1;
                // else
                // xOffset = this.slider.getPaintLabels() ? 2 : 2;
                if (icon != null)
                    icon.paintIcon(this.slider, graphics, xOffset, 1);
            }
        }

        // graphics.translate(-knobBounds.x, -knobBounds.y);
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics,
     *      javax.swing.JComponent)
     */
    @Override
    public synchronized void paint(Graphics g, JComponent c) {
        Graphics2D graphics = (Graphics2D) g.create();
        ComponentState currState = ComponentState.getState(this.thumbModel,
                this.slider);
        float alpha = SubstanceCoreUtilities.getTheme(this.slider, true)
                .getThemeAlpha(this.slider, currState);
        graphics.setComposite(TransitionLayout.getAlphaComposite(this.slider,
                alpha, g));

        SubstanceCoreUtilities.workaroundBug6576507(graphics);

        // important to synchronize on the slider as we are
        // about to fiddle with its opaqueness
        synchronized (c) {
            SubstanceSliderUI.bgDelegate.updateIfOpaque(graphics, c);
            // remove opaqueness
            boolean isOpaque = c.isOpaque();
            c.setOpaque(false);
            super.paint(graphics, c);
            // restore opaqueness
            c.setOpaque(isOpaque);
        }

        if (!this.slider.hasFocus()) {
            if (FadeTracker.getInstance().isTracked(c, FadeKind.FOCUS))
                this.paintFocus(graphics);
        }

        graphics.dispose();
        // g.setColor(Color.green);
        // ((Graphics2D) g).drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);
        // g.setColor(Color.blue);
        // ((Graphics2D) g).draw(this.thumbRect);
        // g.setColor(Color.red);
        // ((Graphics2D) g).draw(this.trackRect);
        // g.setColor(Color.green);
        // ((Graphics2D) g).draw(this.tickRect);
        // g.setColor(Color.magenta);
        // ((Graphics2D) g).draw(this.labelRect);
    }

    /**
     * Returns the button model for tracking the thumb transitions.
     * 
     * @return Button model for tracking the thumb transitions.
     */
    public ButtonModel getButtonModel() {
        return this.thumbModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvnet.substance.Trackable#isInside(java.awt.event.MouseEvent)
     */
    public boolean isInside(MouseEvent me) {
        Rectangle thumbB = this.thumbRect;
        if (thumbB == null)
            return false;
        return thumbB.contains(me.getX(), me.getY());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#installDefaults(javax.swing.JSlider)
     */
    @Override
    protected void installDefaults(JSlider slider) {
        super.installDefaults(slider);
        Font f = slider.getFont();
        if (f == null || f instanceof UIResource) {
            slider.setFont(new FontUIResource(SubstanceLookAndFeel
                    .getFontPolicy().getFontSet("Substance", null)
                    .getControlFont()));
        }
        int size = SubstanceSizeUtils.getSliderIconSize(SubstanceSizeUtils
                .getComponentFontSize(slider));
        // System.out.println("Slider size : " + size);
        this.horizontalIcon = SubstanceIconFactory.getSliderHorizontalIcon(
                size, false);
        this.roundIcon = SubstanceIconFactory.getSliderRoundIcon(size);
        this.verticalIcon = SubstanceIconFactory.getSliderVerticalIcon(size,
                false);

        int focusIns = (int) Math.ceil(2.0 * SubstanceSizeUtils
                .getFocusStrokeWidth(SubstanceSizeUtils
                        .getComponentFontSize(slider)));
        this.focusInsets = new Insets(focusIns, focusIns, focusIns, focusIns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#installListeners(javax.swing.JSlider)
     */
    @Override
    protected void installListeners(final JSlider slider) {
        super.installListeners(slider);

        // fix for defect 109 - memory leak on changing theme
        this.substanceRolloverListener = new RolloverControlListener(this,
                this.thumbModel);
        slider.addMouseListener(this.substanceRolloverListener);
        slider.addMouseMotionListener(this.substanceRolloverListener);

        this.substancePropertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("enabled".equals(evt.getPropertyName())) {
                    SubstanceSliderUI.this.thumbModel.setEnabled(slider
                            .isEnabled());
                }
                if ("font".equals(evt.getPropertyName())) {
                    if (scrollTimer == null) scrollTimer = new Timer(0, null);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {                            
                            slider.updateUI();
                        }
                    });
                }
            }
        };
        this.slider
                .addPropertyChangeListener(this.substancePropertyChangeListener);

        this.substanceFadeStateListener = new FadeStateListener(this.slider,
                this.thumbModel, SubstanceCoreUtilities.getFadeCallback(
                        this.slider, this.thumbModel, false));
        this.substanceFadeStateListener.registerListeners(false);

        //
        // this.substanceMouseMotionListener = new MouseMotionAdapter() {
        // @Override
        // public void mouseMoved(MouseEvent e) {
        // calculateThumbSize();
        // //System.out.println("-->" + thumbRect);
        // }
        // };
        // this.slider.addMouseMotionListener(this.substanceMouseMotionListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#uninstallListeners(javax.swing.JSlider)
     */
    @Override
    protected void uninstallListeners(JSlider slider) {
        super.uninstallListeners(slider);

        // fix for defect 109 - memory leak on changing theme
        slider.removeMouseListener(this.substanceRolloverListener);
        slider.removeMouseMotionListener(this.substanceRolloverListener);
        this.substanceRolloverListener = null;

        slider
                .removePropertyChangeListener(this.substancePropertyChangeListener);
        this.substancePropertyChangeListener = null;

        if (this.substanceFadeStateListener != null) {
            this.substanceFadeStateListener.unregisterListeners();
            this.substanceFadeStateListener = null;
        }
        //
        // slider.removeMouseMotionListener(this.substanceMouseMotionListener);
        // this.substanceMouseMotionListener = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintFocus(java.awt.Graphics)
     */
    @Override
    public void paintFocus(Graphics g) {
        SubstanceCoreUtilities.paintFocus(g, this.slider, this.slider, null,
                null, 1.0f, (int) Math.ceil(SubstanceSizeUtils
                        .getFocusStrokeWidth(SubstanceSizeUtils
                                .getComponentFontSize(this.slider))) / 2);
    }

    /**
     * Returns the amount that the thumb goes past the slide bar.
     * 
     * @return Amount that the thumb goes past the slide bar.
     */
    protected int getThumbOverhang() {
        return (int) (this.getThumbSize().getHeight() - this.getTrackWidth()) / 2;
    }

    /**
     * Returns the shorter dimension of the track.
     * 
     * @return Shorter dimension of the track.
     */
    protected int getTrackWidth() {
        return SubstanceSizeUtils.getSliderTrackSize(SubstanceSizeUtils
                .getComponentFontSize(this.slider));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#getTickLength()
     */
    @Override
    protected int getTickLength() {
        return SubstanceSizeUtils.getSliderTickSize(SubstanceSizeUtils
                .getComponentFontSize(this.slider));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintTicks(java.awt.Graphics)
     */
    @Override
    public void paintTicks(Graphics g) {
        Rectangle tickBounds = this.tickRect;
        if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
            g.translate(0, tickBounds.y);

            int value = this.slider.getMinimum()
                    + this.slider.getMinorTickSpacing();
            int xPos = 0;

            if ((this.slider.getMinorTickSpacing() > 0)
                    && (this.slider.getMajorTickSpacing() > 0)) {
                while (value < this.slider.getMaximum()) {
                    int delta = value - this.slider.getMinimum();
                    if (delta % this.slider.getMajorTickSpacing() != 0) {
                        xPos = this.xPositionForValue(value);
                        this.paintMinorTickForHorizSlider(g, tickBounds, xPos);
                    }
                    value += this.slider.getMinorTickSpacing();
                }
            }

            if (this.slider.getMajorTickSpacing() > 0) {
                value = this.slider.getMinimum()
                        + this.slider.getMajorTickSpacing();

                while (value < this.slider.getMaximum()) {
                    xPos = this.xPositionForValue(value);
                    this.paintMajorTickForHorizSlider(g, tickBounds, xPos);
                    value += this.slider.getMajorTickSpacing();
                }
            }

            g.translate(0, -tickBounds.y);
        } else {
            g.translate(tickBounds.x, 0);

            int value = this.slider.getMinimum()
                    + this.slider.getMinorTickSpacing();
            int yPos = 0;

            boolean ltr = this.slider.getComponentOrientation().isLeftToRight();
            if (this.slider.getMinorTickSpacing() > 0) {
                int offset = 0;
                if (!ltr) {
                    offset = tickBounds.width - tickBounds.width / 2;
                    g.translate(offset, 0);
                }

                while (value < this.slider.getMaximum()) {
                    yPos = this.yPositionForValue(value);
                    this.paintMinorTickForVertSlider(g, tickBounds, yPos);
                    value += this.slider.getMinorTickSpacing();
                }

                if (!ltr) {
                    g.translate(-offset, 0);
                }
            }

            if (this.slider.getMajorTickSpacing() > 0) {
                value = this.slider.getMinimum()
                        + this.slider.getMajorTickSpacing();
                if (!ltr) {
                    g.translate(2, 0);
                }

                while (value < this.slider.getMaximum()) {
                    yPos = this.yPositionForValue(value);
                    this.paintMajorTickForVertSlider(g, tickBounds, yPos);
                    value += this.slider.getMajorTickSpacing();
                }

                if (!ltr) {
                    g.translate(-2, 0);
                }
            }
            g.translate(-tickBounds.x, 0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintMajorTickForHorizSlider(java.awt.Graphics,
     *      java.awt.Rectangle, int)
     */
    @Override
    protected void paintMajorTickForHorizSlider(Graphics g,
            Rectangle tickBounds, int x) {
        boolean isDark = SubstanceCoreUtilities
                .isThemeDark(SubstanceLookAndFeel.getTheme());
        ColorScheme colorScheme = this.slider.isEnabled() ? SubstanceCoreUtilities
                .getDefaultScheme(this.slider)
                : SubstanceCoreUtilities.getDisabledScheme(this.slider);
        // Color color = isDark ? colorScheme.getUltraLightColor() : colorScheme
        // .getDarkColor();
        // Color icolor = isDark ? colorScheme.getDarkColor() : colorScheme
        // .getUltraLightColor();

        Graphics2D graphics = (Graphics2D) g.create();
        graphics.translate(x - 1, 0);
        SubstanceCoreUtilities.paintSeparator(this.slider, graphics,
                colorScheme, isDark, 0, tickBounds.height, JSeparator.VERTICAL,
                true, 0, 4);
        //      
        // graphics.setPaint(new GradientPaint(x - 1, 0, color, x - 1,
        // tickBounds.height, SubstanceColorUtilities.getAlphaColor(color,
        // 96)));
        // graphics.drawLine(x - 1, 0, x - 1, tickBounds.height - 1);
        // graphics.setPaint(new GradientPaint(x, 0, icolor, x,
        // tickBounds.height,
        // SubstanceColorUtilities.getAlphaColor(icolor, 96)));
        // graphics.drawLine(x, 0, x, tickBounds.height - 1);
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintMajorTickForVertSlider(java.awt.Graphics,
     *      java.awt.Rectangle, int)
     */
    @Override
    protected void paintMajorTickForVertSlider(Graphics g,
            Rectangle tickBounds, int y) {
        boolean isDark = SubstanceCoreUtilities
                .isThemeDark(SubstanceLookAndFeel.getTheme());
        ColorScheme colorScheme = this.slider.isEnabled() ? SubstanceCoreUtilities
                .getDefaultScheme(this.slider)
                : SubstanceCoreUtilities.getDisabledScheme(this.slider);
        // Color color = isDark ? colorScheme.getUltraLightColor() : colorScheme
        // .getDarkColor();
        // Color icolor = isDark ? colorScheme.getDarkColor() : colorScheme
        // .getUltraLightColor();

        Graphics2D graphics = (Graphics2D) g.create();
        graphics.translate(0, y);
        SubstanceCoreUtilities.paintSeparator(this.slider, graphics,
                colorScheme, isDark, tickBounds.width - 1, 0,
                JSeparator.HORIZONTAL, true, this.slider
                        .getComponentOrientation().isLeftToRight() ? 0 : 4,
                this.slider.getComponentOrientation().isLeftToRight() ? 4 : 0);
        // if (this.slider.getComponentOrientation().isLeftToRight()) {
        // graphics.setPaint(new GradientPaint(0, y, color,
        // tickBounds.width - 1, y, SubstanceColorUtilities
        // .getAlphaColor(color, 96)));
        // } else {
        // graphics.setPaint(new GradientPaint(0, y, SubstanceColorUtilities
        // .getAlphaColor(color, 96), tickBounds.width - 1, y, color));
        // }
        // graphics.drawLine(0, y, tickBounds.width - 1, y);
        // if (this.slider.getComponentOrientation().isLeftToRight()) {
        // graphics.setPaint(new GradientPaint(0, y + 1, icolor,
        // tickBounds.width - 1, y + 1, SubstanceColorUtilities
        // .getAlphaColor(icolor, 96)));
        // } else {
        // graphics.setPaint(new GradientPaint(0, y + 1,
        // SubstanceColorUtilities.getAlphaColor(icolor, 96),
        // tickBounds.width - 1, y + 1, icolor));
        // }
        // graphics.drawLine(0, y + 1, tickBounds.width - 1, y + 1);
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintMinorTickForHorizSlider(java.awt.Graphics,
     *      java.awt.Rectangle, int)
     */
    @Override
    protected void paintMinorTickForHorizSlider(Graphics g,
            Rectangle tickBounds, int x) {
        boolean isDark = SubstanceCoreUtilities
                .isThemeDark(SubstanceLookAndFeel.getTheme());
        ColorScheme colorScheme = this.slider.isEnabled() ? SubstanceCoreUtilities
                .getDefaultScheme(this.slider)
                : SubstanceCoreUtilities.getDisabledScheme(this.slider);
        // Color color = isDark ? colorScheme.getUltraLightColor() : colorScheme
        // .getDarkColor();
        // Color icolor = isDark ? colorScheme.getDarkColor() : colorScheme
        // .getUltraLightColor();

        Graphics2D graphics = (Graphics2D) g.create();
        graphics.translate(x - 1, 0);
        SubstanceCoreUtilities.paintSeparator(this.slider, graphics,
                colorScheme, isDark, 0, tickBounds.height / 2,
                JSeparator.VERTICAL, true, 0, 4);
        // graphics.setPaint(new GradientPaint(x - 1, 0, color, x - 1,
        // tickBounds.height / 2, SubstanceColorUtilities.getAlphaColor(
        // color, 96)));
        // graphics.drawLine(x - 1, 0, x - 1, tickBounds.height / 2);
        // graphics.setPaint(new GradientPaint(x, 0, icolor, x,
        // tickBounds.height / 2, SubstanceColorUtilities.getAlphaColor(
        // icolor, 96)));
        // graphics.drawLine(x, 0, x, tickBounds.height / 2);
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintMinorTickForVertSlider(java.awt.Graphics,
     *      java.awt.Rectangle, int)
     */
    @Override
    protected void paintMinorTickForVertSlider(Graphics g,
            Rectangle tickBounds, int y) {
        boolean isDark = SubstanceCoreUtilities
                .isThemeDark(SubstanceLookAndFeel.getTheme());
        ColorScheme colorScheme = this.slider.isEnabled() ? SubstanceCoreUtilities
                .getDefaultScheme(this.slider)
                : SubstanceCoreUtilities.getDisabledScheme(this.slider);
        // Color color = isDark ? colorScheme.getUltraLightColor() : colorScheme
        // .getDarkColor();
        // Color icolor = isDark ? colorScheme.getDarkColor() : colorScheme
        // .getUltraLightColor();

        Graphics2D graphics = (Graphics2D) g.create();
        graphics.translate(0, y);
        SubstanceCoreUtilities.paintSeparator(this.slider, graphics,
                colorScheme, isDark, tickBounds.width / 2, 0,
                JSeparator.HORIZONTAL, true, this.slider
                        .getComponentOrientation().isLeftToRight() ? 0 : 4,
                this.slider.getComponentOrientation().isLeftToRight() ? 4 : 0);
        // if (this.slider.getComponentOrientation().isLeftToRight()) {
        // graphics.setPaint(new GradientPaint(0, y, color,
        // tickBounds.width / 2, y, SubstanceColorUtilities
        // .getAlphaColor(color, 96)));
        // graphics.drawLine(0, y, tickBounds.width / 2, y);
        // } else {
        // graphics.setPaint(new GradientPaint(1, y, SubstanceColorUtilities
        // .getAlphaColor(color, 96), tickBounds.width / 2 + 1, y,
        // color));
        // graphics.drawLine(1, y, tickBounds.width / 2 + 1, y);
        // }
        // if (this.slider.getComponentOrientation().isLeftToRight()) {
        // graphics.setPaint(new GradientPaint(0, y + 1, icolor,
        // tickBounds.width / 2, y + 1, SubstanceColorUtilities
        // .getAlphaColor(icolor, 96)));
        // graphics.drawLine(0, y + 1, tickBounds.width / 2, y + 1);
        // } else {
        // graphics.setPaint(new GradientPaint(1, y + 1,
        // SubstanceColorUtilities.getAlphaColor(icolor, 96),
        // tickBounds.width / 2 + 1, y + 1, icolor));
        // graphics.drawLine(1, y + 1, tickBounds.width / 2 + 1, y + 1);
        // }
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#calculateTickRect()
     */
    @Override
    protected void calculateTickRect() {
        if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
            this.tickRect.x = this.trackRect.x;
            this.tickRect.y = this.trackRect.y + this.trackRect.height;
            this.tickRect.width = this.trackRect.width;
            this.tickRect.height = (this.slider.getPaintTicks()) ? this
                    .getTickLength() : 0;
        } else {
            this.tickRect.width = (this.slider.getPaintTicks()) ? this
                    .getTickLength() : 0;
            if (this.slider.getComponentOrientation().isLeftToRight()) {
                this.tickRect.x = this.trackRect.x + this.trackRect.width;
            } else {
                this.tickRect.x = this.trackRect.x - this.tickRect.width;
            }
            this.tickRect.y = this.trackRect.y;
            this.tickRect.height = this.trackRect.height;
        }

        if (this.slider.getPaintTicks()) {
            if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
                this.tickRect.y -= 3;
            } else {
                if (this.slider.getComponentOrientation().isLeftToRight()) {
                    this.tickRect.x -= 2;
                } else {
                    this.tickRect.x += 2;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#calculateLabelRect()
     */
    @Override
    protected void calculateLabelRect() {
        super.calculateLabelRect();
        if (this.slider.getOrientation() == JSlider.HORIZONTAL) {
            if (!this.slider.getPaintTicks()) {
                // labelRect.y += 1;
            }
        } else {
            if (!this.slider.getPaintTicks()) {
                if (this.slider.getComponentOrientation().isLeftToRight()) {
                    this.labelRect.x += 3;
                } else {
                    // labelRect.x -= 2;
                }

            }
        }
    }

    @Override
    protected void calculateThumbLocation() {
        try {
            super.calculateThumbLocation();
        } catch (Exception e) {
            log.trace("calculateThumbLocation(): error Exception", e);
        }
        Rectangle trackRect = this.getPaintTrackRect();
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int valuePosition = xPositionForValue(slider.getValue());

            double centerY = trackRect.y + trackRect.height / 2.0;
            thumbRect.y = (int) (centerY - thumbRect.height / 2.0) + 1;

            thumbRect.x = valuePosition - thumbRect.width / 2;
        } else {
            int valuePosition = yPositionForValue(slider.getValue());

            double centerX = trackRect.x + trackRect.width / 2.0;
            thumbRect.x = (int) (centerX - thumbRect.width / 2.0) + 1;

            thumbRect.y = valuePosition - (thumbRect.height / 2);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintHorizontalLabel(java.awt.Graphics,
     *      int, java.awt.Component)
     */
    @Override
    protected void paintHorizontalLabel(Graphics g, int value, Component label) {
        label.setEnabled(this.slider.isEnabled());
        super.paintHorizontalLabel(g, value, label);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#paintVerticalLabel(java.awt.Graphics,
     *      int, java.awt.Component)
     */
    @Override
    protected void paintVerticalLabel(Graphics g, int value, Component label) {
        label.setEnabled(this.slider.isEnabled());
        super.paintVerticalLabel(g, value, label);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#getPreferredSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        this.recalculateIfInsetsChanged();
        Dimension d;
        if (this.slider.getOrientation() == JSlider.VERTICAL) {
            d = new Dimension(this.getPreferredVerticalSize());
            d.width = this.insetCache.left + this.insetCache.right;
            d.width += this.focusInsets.left + this.focusInsets.right;
            d.width += this.trackRect.width; /* + tickRect.width */
            // + this.labelRect.width;
            // d.width -= 6;
            if (this.slider.getPaintTicks())
                d.width += getTickLength();
            if (this.slider.getPaintLabels())
                d.width += getWidthOfWidestLabel();
            d.width += 3;
            // d.width += 6;
        } else {
            d = new Dimension(this.getPreferredHorizontalSize());
            d.height = this.insetCache.top + this.insetCache.bottom;
            d.height += this.focusInsets.top + this.focusInsets.bottom;
            d.height += this.trackRect.height;/* + tickRect.height */
            // + this.labelRect.height;
            // d.height -= 6;
            if (this.slider.getPaintTicks())
                d.height += getTickLength();
            if (this.slider.getPaintLabels())
                d.height += getHeightOfTallestLabel();
            d.height += 3;
        }

        return d;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#setThumbLocation(int, int)
     */
    @Override
    public void setThumbLocation(int x, int y) {
        super.setThumbLocation(x, y);
        this.slider.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#getPreferredHorizontalSize()
     */
    @Override
    public Dimension getPreferredHorizontalSize() {
        return new Dimension(SubstanceSizeUtils.getAdjustedSize(
                SubstanceSizeUtils.getComponentFontSize(this.slider), 200, 1,
                20, false), 21);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicSliderUI#getPreferredVerticalSize()
     */
    @Override
    public Dimension getPreferredVerticalSize() {
        return new Dimension(21, SubstanceSizeUtils.getAdjustedSize(
                SubstanceSizeUtils.getComponentFontSize(this.slider), 200, 1,
                20, false));
    }

    public void uninstallUI(JComponent c) {
        try {
            super.uninstallUI(c);
        } catch (Exception e) {
            log.trace("uninstallUI(): error Exception", e);
        }
    }
    
    public void installUI(JComponent c) {
        try {
            super.installUI(c);
        } catch (Exception e) {
            log.trace("installUI(): error Exception", e);
        }
    }
    
    
}

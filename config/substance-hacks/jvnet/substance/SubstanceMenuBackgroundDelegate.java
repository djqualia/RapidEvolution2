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

import javax.swing.*;

import org.jvnet.lafwidget.animation.FadeKind;
import org.jvnet.lafwidget.animation.FadeState;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.substance.color.ColorScheme;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.utils.*;
import org.jvnet.substance.utils.SubstanceConstants.MenuGutterFillKind;

/**
 * Delegate for painting background of menu items.
 * 
 * 
 * FIX FOR RE2:
 * 
 *              if (!(menuItem.getParent() instanceof JMenuBar || menuItem.getParent() instanceof JPopupMenu))
            if (TransitionLayout.isOpaque(menuItem)) {
                this.fillBackgroundDelegate.update(graphics, menuItem, menuItem
                        .getParent() instanceof JMenuBar);
            }

 * 
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceMenuBackgroundDelegate {
    /**
     * Delegate for painting gradient background.
     */
    private static SubstanceGradientBackgroundDelegate activeBackgroundDelegate = new SubstanceGradientBackgroundDelegate();

    /**
     * Delegate for painting fill background.
     */
    private SubstanceFillBackgroundDelegate fillBackgroundDelegate;

    /**
     * Creates a new background delegate for menu items.
     * 
     * @param fillAlpha
     *            Alphs attribute for the fill.
     */
    public SubstanceMenuBackgroundDelegate(float fillAlpha) {
        this.fillBackgroundDelegate = new SubstanceFillBackgroundDelegate(
                fillAlpha);
    }

    /**
     * Updates the specified menu item with the background that matches the
     * provided parameters.
     * 
     * @param g
     *            Graphic context.
     * @param menuItem
     *            Menu item.
     * @param width
     *            Background width.
     * @param height
     *            Background height.
     * @param theme
     *            Theme for the background.
     * @param borderAlpha
     *            Border alpha.
     */
    private void paintBackground(Graphics g, JMenuItem menuItem, int width,
            int height, SubstanceTheme theme, float borderAlpha) {
        SubstanceMenuBackgroundDelegate.activeBackgroundDelegate.update(g,
                menuItem, width, height, theme, borderAlpha);
    }

    /**
     * Updates the specified menu item with the background that matches the
     * provided parameters.
     * 
     * @param g
     *            Graphic context.
     * @param menuItem
     *            Menu item.
     * @param bgColor
     *            Current background color.
     * @param borderAlpha
     *            Border alpha.
     * @param textOffset
     *            The offset of the menu item text.
     */
    public void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor,
            float borderAlpha, int textOffset) {
        
        if (!menuItem.isShowing())
            return;
        ButtonModel model = menuItem.getModel();
        int menuWidth = menuItem.getWidth();
        int menuHeight = menuItem.getHeight();

        // fix for defect 103 - no rollover effects on menu items
        // that are not in the selected menu path
        MenuElement[] selectedMenuPath = MenuSelectionManager.defaultManager()
                .getSelectedPath();
        boolean isRollover = (selectedMenuPath.length == 0);
        for (MenuElement elem : selectedMenuPath) {
            if (elem == menuItem) {
                isRollover = true;
                break;
            }
        }
        isRollover = isRollover && model.isRollover();

        Graphics2D graphics = (Graphics2D) g.create();

        if (TransitionLayout.isOpaque(menuItem)) {
            // menu item is opaque and selected (or armed) -
            // use background color of the item (with watermark)
            graphics.setColor(menuItem.getBackground());
            Component comp = menuItem.getParent();
            // if (!(comp instanceof JMenuBar)) {
            // // System.out.println("Filling " + menuItem.getText() + " with "
            // // + graphics.getColor().getRed() + ":"
            // // + graphics.getColor().getGreen() + ":"
            // // + graphics.getColor().getBlue());
            // // graphics.setColor(Color.red);
            // graphics.setComposite(TransitionLayout.getAlphaComposite(
            // menuItem, 0.7f, g));
            // }
            graphics.fillRect(0, 0, menuWidth, menuHeight);
            // System.out.println(menuItem.getText());
            this.fillBackgroundDelegate.setAlphaComposite(0.4f);
            while (comp != null) {
                // System.out.println("\t" + comp.getClass().getName());
                if (comp instanceof JMenuItem) {
                    break;
                }
                if (comp instanceof JMenuBar) {
                    // top-level
                    this.fillBackgroundDelegate.setAlphaComposite(1.0f);
                    break;
                }
                comp = comp.getParent();
            }
            

             if (!(menuItem.getParent() instanceof JMenuBar || menuItem.getParent() instanceof JPopupMenu))
            if (TransitionLayout.isOpaque(menuItem)) {
                this.fillBackgroundDelegate.update(graphics, menuItem, menuItem
                        .getParent() instanceof JMenuBar);
            }
            if (menuItem.getParent() instanceof JPopupMenu) {
                if (menuItem.getComponentOrientation().isLeftToRight()) {
                    MenuGutterFillKind fillKind = SubstanceCoreUtilities
                            .getMenuGutterFillKind();
                    if (fillKind != MenuGutterFillKind.NONE) {
                        ColorScheme scheme = SubstanceCoreUtilities
                                .getActiveScheme(menuItem);
                        Color leftColor = ((fillKind == MenuGutterFillKind.SOFT_FILL) || (fillKind == MenuGutterFillKind.HARD)) ? scheme
                                .getUltraLightColor()
                                : scheme.getLightColor();
                        Color rightColor = ((fillKind == MenuGutterFillKind.SOFT_FILL) || (fillKind == MenuGutterFillKind.SOFT)) ? scheme
                                .getUltraLightColor()
                                : scheme.getLightColor();
                        GradientPaint gp = new GradientPaint(0, 0, leftColor,
                                textOffset, 0, rightColor);
                        graphics.setComposite(TransitionLayout
                                .getAlphaComposite(menuItem, 0.7f, g));
                        graphics.setPaint(gp);
                        graphics.fillRect(0, 0, textOffset - 2, menuHeight);
                    }
                } else {
                    // fix for defect 125 - support of RTL menus
                    MenuGutterFillKind fillKind = SubstanceCoreUtilities
                            .getMenuGutterFillKind();
                    if (fillKind != MenuGutterFillKind.NONE) {
                        ColorScheme scheme = SubstanceCoreUtilities
                                .getActiveScheme(menuItem);
                        Color leftColor = ((fillKind == MenuGutterFillKind.HARD_FILL) || (fillKind == MenuGutterFillKind.HARD)) ? scheme
                                .getLightColor()
                                : scheme.getUltraLightColor();
                        Color rightColor = ((fillKind == MenuGutterFillKind.HARD_FILL) || (fillKind == MenuGutterFillKind.SOFT)) ? scheme
                                .getLightColor()
                                : scheme.getUltraLightColor();
                        GradientPaint gp = new GradientPaint(menuWidth
                                - textOffset, 0, leftColor, menuWidth, 0,
                                rightColor);
                        graphics.setComposite(TransitionLayout
                                .getAlphaComposite(menuItem, 0.7f, g));
                        graphics.setPaint(gp);
                        graphics.fillRect(menuWidth - textOffset - 2, 0,
                                menuWidth, menuHeight);
                    }
                }
            }
        }

        ComponentState prevState = SubstanceCoreUtilities
                .getPrevComponentState(menuItem);
        ComponentState currState = ComponentState.getState(model, menuItem,
                !(menuItem instanceof JMenu));
        float alphaForPrevBackground = 0.0f;

        // Compute the alpha values for the animation (the highlights
        // have separate alpha channels, so that rollover animation starts
        // from 0.0 alpha and goes to 0.4 alpha on non-selected cells,
        // but on selected cells goes from 0.7 to 0.9). We need to respect
        // these border values for proper visual transitions
        float startAlpha = SubstanceCoreUtilities.getHighlightAlpha(menuItem,
                prevState, true);
        float endAlpha = SubstanceCoreUtilities.getHighlightAlpha(menuItem,
                currState, true);
        float alphaForCurrBackground = endAlpha;

        SubstanceTheme prevTheme = SubstanceCoreUtilities.getHighlightTheme(
                menuItem, prevState, true, false);
        SubstanceTheme currTheme = SubstanceCoreUtilities.getHighlightTheme(
                menuItem, currState, true, false);

        FadeState state = SubstanceFadeUtilities.getFadeState(menuItem,
                FadeKind.SELECTION, FadeKind.ARM, FadeKind.ROLLOVER);
        if (state != null) {
            float fadeCoef = state.getFadePosition();

            // compute the total alpha of the overlays.
            float totalAlpha = 0.0f;
            if (state.isFadingIn()) {
                totalAlpha = startAlpha + (endAlpha - startAlpha) * fadeCoef
                        / 10.0f;
            } else {
                totalAlpha = startAlpha + (endAlpha - startAlpha)
                        * (10.0f - fadeCoef) / 10.0f;
            }

            if (state.isFadingIn())
                fadeCoef = 10.0f - fadeCoef;

            // compute the alpha for each one of the animation overlays
            alphaForPrevBackground = totalAlpha * fadeCoef / 10.0f;
            alphaForCurrBackground = totalAlpha * (10.0f - fadeCoef) / 10.0f;

            // System.out.println(menuItem.getText() + " from " +
            // prevState.name()
            // + "[" + alphaForPrevBackground + "] to " + currState.name()
            // + "[" + alphaForCurrBackground + "] at " + fadeCoef);
            // System.out.println("From " + prevTheme.getDisplayName() + " to :"
            // + currTheme.getDisplayName());

        }

        // System.out.println(menuItem.getText() + "[" + currState.name() + "]:"
        // + prevTheme.getDisplayName() + "[" + alphaForPrevBackground
        // + "]:" + currTheme.getDisplayName() + "["
        // + alphaForCurrBackground + "]");
        // System.out.println("ARM:" + menuItem.getModel().isArmed() + ", ENA:"
        // + menuItem.getModel().isEnabled() + ", PRE:"
        // + menuItem.getModel().isPressed() + ", ROL:"
        // + menuItem.getModel().isRollover() + ", SEL:"
        // + menuItem.getModel().isSelected());

        boolean hasHighlight = (state != null) || model.isArmed() || isRollover
                || ((menuItem instanceof JMenu) && model.isSelected());

        if (hasHighlight && (alphaForPrevBackground > 0.0f)) {
            graphics.setComposite(TransitionLayout.getAlphaComposite(menuItem,
                    alphaForPrevBackground, g));
            this.paintBackground(graphics, menuItem, menuWidth, menuHeight,
                    prevTheme, borderAlpha);
            graphics.setComposite(TransitionLayout.getAlphaComposite(menuItem,
                    g));
            // System.out.println("Painted " + prevTheme.getDisplayName()
            // + " with " + alphaForPrevBackground);
        }
        if (hasHighlight && (alphaForCurrBackground > 0.0f)) {
            graphics.setComposite(TransitionLayout.getAlphaComposite(menuItem,
                    alphaForCurrBackground, g));
            this.paintBackground(graphics, menuItem, menuWidth, menuHeight,
                    currTheme, borderAlpha);
            graphics.setComposite(TransitionLayout.getAlphaComposite(menuItem,
                    g));
            // System.out.println("Painted " + currTheme.getDisplayName()
            // + " with " + alphaForCurrBackground);
        }

        graphics.dispose();
    }
}

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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;

import org.apache.log4j.Logger;
import org.jvnet.lafwidget.animation.*;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.lafwidget.utils.LookUtils;
import org.jvnet.substance.theme.SubstanceTheme;
import org.jvnet.substance.utils.*;

import rapid_evolution.Filter;

/**
 * UI for lists in <b>Substance</b> look and feel.
 * 
 * FIXED FOR RE2: found code with component.getBackground()
 * and changed to draw entire row with background color:
 * 
 *             g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    0.5f));
            if (this.tree.getComponentOrientation().isLeftToRight()) {
                g2d.fillRect(0, bounds.y, clipBounds.width + bounds.x,
                        bounds.height);
            } else {
                g2d.fillRect(0, bounds.y, bounds.width + clipBounds.x,
                        bounds.height);
            }
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    currFactor));            




 * 
 * 
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceTreeUI extends BasicTreeUI {
    
    private static Logger log = Logger.getLogger(SubstanceTreeUI.class);
    
    /**
     * Holds the list of currently selected paths.
     */
    protected Map<TreePathId, Object> selectedPaths;

    /**
     * Holds the currently rolled-over path or <code>null</code> if none such.
     */
    protected TreePathId currRolloverPathId;

    /**
     * Delegate for painting the background.
     */
    private static SubstanceGradientBackgroundDelegate backgroundDelegate = new SubstanceGradientBackgroundDelegate();

    /**
     * Listener that listens to changes on
     * {@link SubstanceLookAndFeel#WATERMARK_TO_BLEED} property.
     */
    protected PropertyChangeListener substancePropertyChangeListener;

    /**
     * Listener for selection animations.
     */
    protected TreeSelectionListener substanceSelectionFadeListener;

    /**
     * Listener for fade animations on tree rollovers.
     */
    protected RolloverFadeListener substanceFadeRolloverListener;

    /**
     * Listener for selection of an entire row.
     */
    protected MouseListener substanceRowSelectionListener;

    /**
     * If <code>true</code>, the mouse pointer is in the bounds of the
     * associated tree.
     */
    private boolean isInside = false;

    /**
     * Map of previous fade states (for state-aware theme transitions).
     */
    private Map<TreePathId, ComponentState> prevStateMap;

    /**
     * Map of next fade states (for state-aware theme transitions).
     */
    private Map<TreePathId, ComponentState> nextStateMap;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
     */
    public static ComponentUI createUI(JComponent tree) {
        return new SubstanceTreeUI();
    }

    /**
     * Creates a UI delegate for tree.
     */
    public SubstanceTreeUI() {
        super();
        this.selectedPaths = new HashMap<TreePathId, Object>();
        this.prevStateMap = new HashMap<TreePathId, ComponentState>();
        this.nextStateMap = new HashMap<TreePathId, ComponentState>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#installDefaults()
     */
    @Override
    protected void installDefaults() {
        super.installDefaults();
        if (SubstanceCoreUtilities.toBleedWatermark(this.tree))
            this.tree.setOpaque(false);

        // Map<TreePathId, Object> selected = new HashMap<TreePathId, Object>();
        if (this.tree.getSelectionPaths() != null) {
            for (TreePath selectionPath : this.tree.getSelectionPaths()) {
                TreePathId pathId = new TreePathId(selectionPath);
                selectedPaths.put(pathId, selectionPath.getLastPathComponent());
                prevStateMap.put(pathId, ComponentState.SELECTED);
            }
        }

        SubstanceTheme theme = SubstanceCoreUtilities.getTheme(this.tree, true);
        setExpandedIcon(new IconUIResource(new ImageIcon(SubstanceImageCreator
                .getTreeIcon(
                        SubstanceSizeUtils.getComponentFontSize(this.tree),
                        this.tree, theme.getDefaultTheme().getColorScheme(),
                        SubstanceCoreUtilities.isThemeDark(theme), false))));
        setCollapsedIcon(new IconUIResource(new ImageIcon(SubstanceImageCreator
                .getTreeIcon(
                        SubstanceSizeUtils.getComponentFontSize(this.tree),
                        this.tree, theme.getDefaultTheme().getColorScheme(),
                        SubstanceCoreUtilities.isThemeDark(theme), true))));

        // this.tree.putClientProperty(SELECTED_INDICES, selected);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#uninstallDefaults()
     */
    @Override
    protected void uninstallDefaults() {
        // this.tree.putClientProperty(SELECTED_INDICES, null);
        this.selectedPaths.clear();
        super.uninstallDefaults();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#paintRow(java.awt.Graphics,
     *      java.awt.Rectangle, java.awt.Insets, java.awt.Rectangle,
     *      javax.swing.tree.TreePath, int, boolean, boolean, boolean)
     */
    @Override
    protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
            Rectangle bounds, TreePath path, int row, boolean isExpanded,
            boolean hasBeenExpanded, boolean isLeaf) {
        // Don't paint the renderer if editing this row.
        if ((this.editingComponent != null) && (this.editingRow == row))
            return;

        int leadIndex;

        if (this.tree.hasFocus()) {
            TreePath leadPath = this.tree.getLeadSelectionPath();
            leadIndex = this.getRowForPath(this.tree, leadPath);
        } else {
            leadIndex = -1;
        }

        Component component;

        component = this.currentCellRenderer.getTreeCellRendererComponent(
                this.tree, path.getLastPathComponent(), this.tree
                        .isRowSelected(row), isExpanded, isLeaf, row,
                (leadIndex == row));

        // second part - fix for defect 214 (rollover effects on non-opaque
        // trees resulted in inconsistent behaviour)
        boolean isWatermarkBleed = SubstanceCoreUtilities
                .toBleedWatermark(tree)
                || !tree.isOpaque();

        TreePathId pathId = new TreePathId(path);
        // boolean isSelectedAnim =
        // FadeTracker.getInstance().isTracked(this.tree,
        // pathId, FadeKind.SELECTION);
        // boolean isRolloverAnim =
        // FadeTracker.getInstance().isTracked(this.tree,
        // pathId, FadeKind.ROLLOVER);
        // TreePathId currRoPath = (TreePathId) tree
        // .getClientProperty(ROLLED_OVER_INDEX);
        // boolean isRollover = ((currRolloverPathId != null) &&
        // (currRolloverPathId.path
        // .equals(path)));

        // Respect the current composite set on the graphics - for
        // JXPanel alpah channel
        float currFactor = 1.0f;
        Composite currComposite = ((Graphics2D) g).getComposite();
        if (currComposite instanceof AlphaComposite) {
            AlphaComposite ac = (AlphaComposite) currComposite;
            if (ac.getRule() == AlphaComposite.SRC_OVER)
                currFactor = ac.getAlpha();
        }

        Graphics2D g2d = (Graphics2D) g.create();
        // fix for issue 183 - passing the original Graphics context
        // to compute the alpha composite. If the tree is in a JXPanel
        // (component from SwingX) and it has custom alpha value set,
        // then the original graphics context will have a SRC_OVER
        // alpha composite applied to it.
        g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                currFactor));
        if ((!isWatermarkBleed) && (component.getBackground() != null)) {
            g2d.setColor(component.getBackground());
//            Dimension size = component.getPreferredSize();
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    0.5f));
            if (this.tree.getComponentOrientation().isLeftToRight()) {
                g2d.fillRect(0, bounds.y, clipBounds.width + bounds.x,
                        bounds.height);
            } else {
                g2d.fillRect(0, bounds.y, bounds.width + clipBounds.x,
                        bounds.height);
            }
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    currFactor));            
            /*
            if (this.tree.getComponentOrientation().isLeftToRight()) {
                g2d.fillRect(bounds.x, bounds.y, size.width,
                        size.height);
            } else {
                g2d.fillRect(clipBounds.x, bounds.y, size.width,
                        size.height);
            }
            */            
        }
        // float alphaForBackground = 0.0f;
        //
        // // Support for selection animations (from 3.1)
        // if (this.tree.isRowSelected(row) || isSelectedAnim) {
        // if (isSelectedAnim) {
        // // set the alpha for selection animation
        // float fadeCoef = FadeTracker.getInstance().getFade10(this.tree,
        // pathId, FadeKind.SELECTION);
        // alphaForBackground = 0.7f * fadeCoef / 10.0f;
        // } else {
        // alphaForBackground = 0.7f;
        // }
        // }
        // // Support for rollover animations (from 3.1)
        // if (isRolloverAnim) {
        // // set the alpha for rollover animation
        // float fadeCoef = FadeTracker.getInstance().getFade10(this.tree,
        // pathId, FadeKind.ROLLOVER);
        // // System.out.println("Has rollover anim on " + row + "["
        // // + fadeCoef + "] : " + cx + ":" + cy + "-" + cw + ":"
        // // + ch);
        // alphaForBackground = Math.max(alphaForBackground,
        // 0.4f * fadeCoef / 10.0f);
        // } else {
        // if (isRollover) {
        // alphaForBackground = Math.max(alphaForBackground, 0.4f);
        // }
        // }

        ComponentState prevState = this.getPrevPathState(pathId);
        ComponentState currState = this.getPathState(pathId);
        float alphaForPrevBackground = 0.0f;

        // Compute the alpha values for the animation (the highlights
        // have separate alpha channels, so that rollover animation starts
        // from 0.0 alpha and goes to 0.4 alpha on non-selected cells,
        // but on selected cells goes from 0.7 to 0.9). We need to respect
        // these border values for proper visual transitions
        float startAlpha = SubstanceCoreUtilities.getHighlightAlpha(this.tree,
                prevState, true);
        float endAlpha = SubstanceCoreUtilities.getHighlightAlpha(this.tree,
                currState, true);
        float alphaForCurrBackground = endAlpha;

        FadeState state = SubstanceFadeUtilities.getFadeState(this.tree,
                pathId, FadeKind.SELECTION, FadeKind.ROLLOVER);
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
        }

        SubstanceTheme prevTheme = SubstanceCoreUtilities.getHighlightTheme(
                this.tree, prevState, true, false);
        SubstanceTheme currTheme = SubstanceCoreUtilities.getHighlightTheme(
                this.tree, currState, true, false);

        // System.out.println(row + ":" + prevTheme.getDisplayName() + "["
        // + alphaForPrevBackground + "]:" + currTheme.getDisplayName()
        // + "[" + alphaForCurrBackground + "]");

        // The DefaultTreeCellRenderer overrides the isOpaque method
        // so that there is no point in trying to make it non-opaque.
        // Fix for defect 181.
        boolean canHaveSubstanceEffects = !(component instanceof DefaultTreeCellRenderer);
        if (canHaveSubstanceEffects && (alphaForPrevBackground > 0.0f)) {
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    currFactor * alphaForPrevBackground, g));
            // Fix for defect 180 (old code in comments) - painting the
            // highlight beneath the entire row
            backgroundDelegate.update(g2d, component, new Rectangle(this.tree
                    .getInsets().left /* bounds.x */, bounds.y,
                    this.tree.getWidth() - this.tree.getInsets().right
                            - this.tree.getInsets().left/* bounds.x */,
                    bounds.height), prevTheme, 0.8f);
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    currFactor));
        }

        if (canHaveSubstanceEffects && (alphaForCurrBackground > 0.0f)) {
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    currFactor * alphaForCurrBackground, g));
            // Fix for defect 180 (old code in comments) - painting the
            // highlight beneath the entire row
            backgroundDelegate.update(g2d, component, new Rectangle(this.tree
                    .getInsets().left /* bounds.x */, bounds.y,
                    this.tree.getWidth() - this.tree.getInsets().right
                            - this.tree.getInsets().left/* bounds.x */,
                    bounds.height), currTheme, 0.8f);
            g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
                    currFactor));
        }

        // if (canHaveSubstanceEffects && (alphaForBackground > 0.0f)) {
        // g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree,
        // alphaForBackground, g));
        // // Fix for defect 180 (old code in comments) - painting the
        // // highlight beneath the entire row
        // backgroundDelegate.update(g2d, component, new Rectangle(this.tree
        // .getInsets().left /* bounds.x */, bounds.y,
        // this.tree.getWidth() - this.tree.getInsets().right
        // - this.tree.getInsets().left/* bounds.x */,
        // bounds.height), SubstanceCoreUtilities.getHighlightTheme(
        // this.tree, ComponentState.ACTIVE, true, true), true);
        // g2d.setComposite(TransitionLayout.getAlphaComposite(this.tree, g));
        // }

        if (component instanceof JComponent) {
            // Play with opacity to make our own gradient background
            // on selected elements to show.
            JComponent jRenderer = (JComponent) component;
            synchronized (jRenderer) {
                boolean newOpaque = !this.tree.isRowSelected(row);
                if (SubstanceCoreUtilities.toBleedWatermark(this.tree))
                    newOpaque = false;

                // fix for defect 181 - no highlight on renderers
                // that extend DefaultTreeCellRenderer
                newOpaque = newOpaque && canHaveSubstanceEffects;

                Map<Component, Boolean> opacity = new HashMap<Component, Boolean>();
                if (!newOpaque)
                    SubstanceCoreUtilities.makeNonOpaque(jRenderer, opacity);
                this.rendererPane.paintComponent(g2d, component, this.tree,
                        bounds.x, bounds.y, Math.max(this.tree.getWidth()
                                - this.tree.getInsets().right
                                - this.tree.getInsets().left - bounds.x,
                                bounds.width), bounds.height, true);
                if (!newOpaque)
                    SubstanceCoreUtilities.restoreOpaque(jRenderer, opacity);
            }
        } else {
            this.rendererPane.paintComponent(g2d, component, this.tree,
                    bounds.x, bounds.y, Math
                            .max(clipBounds.width, bounds.width),
                    bounds.height, true);
        }

        // Paint the expand control once again since it has been overlayed
        // by the highlight background on selected and rolled over rows.
        if (shouldPaintExpandControl(path, row, isExpanded, hasBeenExpanded,
                isLeaf)) {
            if (!this.tree.getComponentOrientation().isLeftToRight()
                    && LookUtils.IS_JAVA_5) {
                bounds.x -= 4;
            }
            paintExpandControl(g2d, clipBounds, insets, bounds, path, row,
                    isExpanded, hasBeenExpanded, isLeaf);
        }

        // g2d.setColor(Color.blue);
        // g2d.draw(bounds);
        g2d.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#paintExpandControl(java.awt.Graphics,
     *      java.awt.Rectangle, java.awt.Insets, java.awt.Rectangle,
     *      javax.swing.tree.TreePath, int, boolean, boolean, boolean)
     */
    @Override
    protected void paintExpandControl(Graphics g, Rectangle clipBounds,
            Insets insets, Rectangle bounds, TreePath path, int row,
            boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
        boolean toPaint = (!this.tree.isEnabled())
                || this.isInside
                || !FadeConfigurationManager.getInstance().fadeAllowed(
                        SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND,
                        tree);
        if (FadeTracker.getInstance().isTracked(this.tree,
                SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND)) {
            Graphics2D graphics = (Graphics2D) g.create();
            graphics
                    .setComposite(TransitionLayout
                            .getAlphaComposite(
                                    this.tree,
                                    FadeTracker
                                            .getInstance()
                                            .getFade10(
                                                    this.tree,
                                                    SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND) / 10.0f,
                                    g));
            super.paintExpandControl(graphics, clipBounds, insets, bounds,
                    path, row, isExpanded, hasBeenExpanded, isLeaf);
            graphics.dispose();
        } else if (toPaint) {
            super.paintExpandControl(g, clipBounds, insets, bounds, path, row,
                    isExpanded, hasBeenExpanded, isLeaf);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#paintHorizontalPartOfLeg(java.awt.Graphics,
     *      java.awt.Rectangle, java.awt.Insets, java.awt.Rectangle,
     *      javax.swing.tree.TreePath, int, boolean, boolean, boolean)
     */
    @Override
    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, Rectangle bounds, TreePath path, int row,
            boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
        Graphics2D graphics = (Graphics2D) g.create();
        float strokeWidth = SubstanceSizeUtils
                .getBorderStrokeWidth(SubstanceSizeUtils
                        .getComponentFontSize(this.tree));
        graphics.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_BEVEL));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        boolean toPaint = (!this.tree.isEnabled())
                || this.isInside
                || !FadeConfigurationManager.getInstance().fadeAllowed(
                        SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND,
                        tree);
        if (FadeTracker.getInstance().isTracked(this.tree,
                SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND)) {
            graphics
                    .setComposite(TransitionLayout
                            .getAlphaComposite(
                                    this.tree,
                                    FadeTracker
                                            .getInstance()
                                            .getFade10(
                                                    this.tree,
                                                    SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND) / 10.0f,
                                    g));
            super.paintHorizontalPartOfLeg(graphics, clipBounds, insets,
                    bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
        } else if (toPaint) {
            super.paintHorizontalPartOfLeg(graphics, clipBounds, insets,
                    bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
        }
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#paintVerticalPartOfLeg(java.awt.Graphics,
     *      java.awt.Rectangle, java.awt.Insets, javax.swing.tree.TreePath)
     */
    @Override
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, TreePath path) {
        Graphics2D graphics = (Graphics2D) g.create();
        float strokeWidth = SubstanceSizeUtils
                .getBorderStrokeWidth(SubstanceSizeUtils
                        .getComponentFontSize(this.tree));
        graphics.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        boolean toPaint = (!this.tree.isEnabled())
                || this.isInside
                || !FadeConfigurationManager.getInstance().fadeAllowed(
                        SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND,
                        tree);
        if (FadeTracker.getInstance().isTracked(this.tree,
                SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND)) {
            graphics
                    .setComposite(TransitionLayout
                            .getAlphaComposite(
                                    this.tree,
                                    FadeTracker
                                            .getInstance()
                                            .getFade10(
                                                    this.tree,
                                                    SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND) / 10.0f,
                                    g));
            super.paintVerticalPartOfLeg(graphics, clipBounds, insets, path);
        } else if (toPaint) {
            super.paintVerticalPartOfLeg(graphics, clipBounds, insets, path);
        }
        graphics.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#createDefaultCellRenderer()
     */
    @Override
    protected TreeCellRenderer createDefaultCellRenderer() {
        return new SubstanceDefaultTreeCellRenderer();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#installListeners()
     */
    @Override
    protected void installListeners() {
        super.installListeners();
        this.substancePropertyChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (SubstanceLookAndFeel.WATERMARK_TO_BLEED.equals(evt
                        .getPropertyName())) {
                    tree.setOpaque(!SubstanceCoreUtilities
                            .toBleedWatermark(tree));
                }
                if ("font".equals(evt.getPropertyName())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            tree.updateUI();
                        }
                    });
                }
            }
        };
        this.tree
                .addPropertyChangeListener(this.substancePropertyChangeListener);

        this.substanceSelectionFadeListener = new MyTreeSelectionListener();
        this.tree.getSelectionModel().addTreeSelectionListener(
                this.substanceSelectionFadeListener);

        this.substanceRowSelectionListener = new RowSelectionListener();
        this.tree.addMouseListener(this.substanceRowSelectionListener);
        
        // Add listener for the fade animation
        this.substanceFadeRolloverListener = new RolloverFadeListener();
        this.tree.addMouseMotionListener(this.substanceFadeRolloverListener);
        this.tree.addMouseListener(this.substanceFadeRolloverListener);
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicTreeUI#uninstallListeners()
     */
    @Override
    protected void uninstallListeners() {
        this.tree.removeMouseListener(this.substanceRowSelectionListener);
        this.substanceRowSelectionListener = null;

        this.tree.getSelectionModel().removeTreeSelectionListener(
                this.substanceSelectionFadeListener);
        this.substanceSelectionFadeListener = null;

        this.tree
                .removePropertyChangeListener(this.substancePropertyChangeListener);
        this.substancePropertyChangeListener = null;

        // Remove listener for the fade animation
        this.tree.removeMouseMotionListener(this.substanceFadeRolloverListener);
        this.tree.removeMouseListener(this.substanceFadeRolloverListener);
        this.substanceFadeRolloverListener = null;
        
        super.uninstallListeners();
    }

    /**
     * ID of a single tree path.
     * 
     * @author Kirill Grouchnikov
     */
    @SuppressWarnings("unchecked")
    protected static class TreePathId implements Comparable {
        /**
         * Tree path.
         */
        protected TreePath path;

        /**
         * Creates a tree path ID.
         * 
         * @param path
         *            Tree path.
         */
        public TreePathId(TreePath path) {
            this.path = path;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            if (o instanceof TreePathId) {
                TreePathId otherId = (TreePathId) o;
                if ((this.path == null) && (otherId.path != null))
                    return 1;
                if ((otherId.path == null) && (this.path != null))
                    return -1;
                Object[] path1Objs = this.path.getPath();
                Object[] path2Objs = otherId.path.getPath();
                if (path1Objs.length != path2Objs.length)
                    return 1;
                for (int i = 0; i < path1Objs.length; i++)
                    if (!path1Objs[i].equals(path2Objs[i]))
                        return 1;
                return 0;
            }
            return -1;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return this.compareTo(obj) == 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (this.path == null)
                return 0;
            Object[] pathObjs = this.path.getPath();
            int result = pathObjs[0].hashCode();
            for (int i = 1; i < pathObjs.length; i++)
                result = result ^ pathObjs[i].hashCode();
            return result;
        }
    }

    /**
     * Selection listener for selection animation effects.
     * 
     * @author Kirill Grouchnikov
     */
    protected class MyTreeSelectionListener implements TreeSelectionListener {
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        @SuppressWarnings("unchecked")
        public void valueChanged(TreeSelectionEvent e) {
            // Map<TreePathId, Object> currSelected = (Map<TreePathId, Object>)
            // tree
            // .getClientProperty(SELECTED_INDICES);
            if (tree.getSelectionPaths() != null) {
                for (TreePath selectionPath : tree.getSelectionPaths()) {
                    TreePathId pathId = new TreePathId(selectionPath);

                    // check if was selected before
                    if (!selectedPaths.containsKey(pathId)) {
                        // start fading in
                        // System.out.println("Fade in on index " + i);
                        FadeTracker.getInstance().trackFadeIn(
                                FadeKind.SELECTION, tree, pathId, false,
                                new PathRepaintCallback(tree, selectionPath));
                        selectedPaths.put(pathId, selectionPath
                                .getLastPathComponent());
                    }
                }
            }

            for (Iterator<Map.Entry<TreePathId, Object>> it = selectedPaths
                    .entrySet().iterator(); it.hasNext();) {
                Map.Entry<TreePathId, Object> entry = it.next();
                if (tree.getSelectionModel()
                        .isPathSelected(entry.getKey().path))
                    continue;
                // fade out for deselected path
                FadeTracker.getInstance().trackFadeOut(FadeKind.SELECTION,
                        tree, entry.getKey(), false,
                        new PathRepaintCallback(tree, entry.getKey().path));
                it.remove();
            }

            //
            //
            //
            // // check if was selected before and still points to
            // // the same element
            // if (currSelected.containsKey(cellId)) {
            // if (currSelected.get(cellId).equals(
            // table.getValueAt(i, j))) {
            // // start fading out
            // // System.out.println("Fade out on index " + i
            // // + ":" + j);
            // FadeTracker.getInstance().trackFadeOut(
            // FadeKind.SELECTION, table, cellId,
            // false,
            // new CellRepaintCallback(table, i, j));
            // }
            // currSelected.remove(cellId);
            // }
            // }
            // }
            // }
        }
    }

    /**
     * Repaints a single path during the fade animation cycle.
     * 
     * @author Kirill Grouchnikov
     */
    protected class PathRepaintCallback extends FadeTrackerAdapter {
        /**
         * Associated tree.
         */
        protected JTree tree;

        /**
         * Associated (animated) path.
         */
        protected TreePath treePath;

        /**
         * Creates a new animation repaint callback.
         * 
         * @param tree
         *            Associated tree.
         * @param treePath
         *            Associated (animated) path.
         */
        public PathRepaintCallback(JTree tree, TreePath treePath) {
            super();
            this.tree = tree;
            this.treePath = treePath;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadeEnded(org.jvnet.lafwidget.utils.FadeTracker.FadeKind)
         */
        @Override
        public void fadeEnded(FadeKind fadeKind) {
            if (SubstanceTreeUI.this.tree == tree) {
                TreePathId pathId = new TreePathId(treePath);
                ComponentState currState = getPathState(pathId);
                if (currState == ComponentState.DEFAULT) {
                    prevStateMap.remove(pathId);
                    nextStateMap.remove(pathId);
                } else {
                    prevStateMap.put(pathId, currState);
                    nextStateMap.put(pathId, currState);
                }
                // System.out.println(tabIndex + "->"
                // + prevStateMap.get(tabIndex).name());
            }
            this.repaintPath();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.lafwidget.utils.FadeTracker$FadeTrackerCallback#fadePerformed(org.jvnet.lafwidget.utils.FadeTracker.FadeKind,
         *      float)
         */
        @Override
        public void fadePerformed(FadeKind fadeKind, float fade10) {
            if (SubstanceTreeUI.this.tree == tree) {
                TreePathId pathId = new TreePathId(treePath);
                nextStateMap.put(pathId, getPathState(pathId));
            }
            this.repaintPath();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.lafwidget.animation.FadeTrackerAdapter#fadeReversed(org.jvnet.lafwidget.animation.FadeKind,
         *      boolean, float)
         */
        @Override
        public void fadeReversed(FadeKind fadeKind, boolean isFadingIn,
                float fadeCycle10) {
            if (SubstanceTreeUI.this.tree == tree) {
                TreePathId pathId = new TreePathId(treePath);
                ComponentState nextState = nextStateMap.get(pathId);
                if (nextState == null) {
                    prevStateMap.remove(pathId);
                } else {
                    prevStateMap.put(pathId, nextState);
                }
                // System.out.println(tabIndex + "->"
                // + prevStateMap.get(tabIndex).name());
            }
            this.repaintPath();
        }

        /**
         * Repaints the associated path.
         */
        private void repaintPath() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (SubstanceTreeUI.this.tree == null) {
                        // may happen if the LAF was switched in the meantime
                        return;
                    }

                    Rectangle boundsBuffer = new Rectangle();
                    Rectangle bounds = treeState.getBounds(treePath,
                            boundsBuffer);

                    if (bounds != null) {
                        // still visible

                        // fix for defect 180 - refresh the entire row
                        bounds.x = 0;
                        bounds.width = tree.getWidth();

                        // fix for defect 188 - rollover effects for trees
                        // with insets
                        Insets insets = tree.getInsets();
                        bounds.x += insets.left;
                        bounds.y += insets.top;

                        tree.repaint(bounds);
                    }
                }
            });
        }
    }

    public void fadeOutRollover() {
        this.substanceFadeRolloverListener.performExit();
    }
    
    /**
     * Listener for rollover animation effects.
     * 
     * @author Kirill Grouchnikov
     */
    private class RolloverFadeListener implements MouseListener,
            MouseMotionListener {

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            if (log.isTraceEnabled())
                log.trace("mouseEntered(): e=" + e);
            if (!tree.isEnabled())
                return;
            isInside = true;
            if (FadeConfigurationManager.getInstance().fadeAllowed(
                    SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND, tree)) {
                FadeTracker.getInstance().trackFadeIn(
                        SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND,
                        tree, false, null);
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            // if (SubstanceCoreUtilities.toBleedWatermark(list))
            // return;
            if (log.isTraceEnabled())
                log.trace("mouseExited(): e=" + e);
            
            performExit();
        }
        
        public void performExit() {
            if (!tree.isEnabled())
                return;
            isInside = false;
            if (FadeConfigurationManager.getInstance().fadeAllowed(
                    SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND, tree)) {
                FadeTracker.getInstance().trackFadeOut(
                        SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND,
                        tree, false, null);
            }
            this.fadeOut();
            // System.out.println("Nulling RO index");
            currRolloverPathId = null;
            // tree.putClientProperty(ROLLED_OVER_INDEX, null);            
        }

        public void mouseMoved(MouseEvent e) {
            if (log.isTraceEnabled())
                log.trace("mouseMoved(): e=" + e);
            // if (SubstanceCoreUtilities.toBleedWatermark(list))
            // return;

            if (!tree.isEnabled())
                return;
            isInside = true;
            handleMove(e);
        }

        public void mouseDragged(MouseEvent e) {
            // if (SubstanceCoreUtilities.toBleedWatermark(list))
            // return;

            if (!tree.isEnabled())
                return;
            handleMove(e);
        }

        /**
         * Handles various mouse move events and initiates the fade animation if
         * necessary.
         * 
         * @param e
         *            Mouse event.
         */
        private void handleMove(MouseEvent e) {
            TreePath closestPath = tree.getClosestPathForLocation(e.getX(), e
                    .getY());
            Rectangle bounds = tree.getPathBounds(closestPath);
            if (bounds == null) {
                this.fadeOut();
                currRolloverPathId = null;
                // tree.putClientProperty(ROLLED_OVER_INDEX, null);
                return;
            }
            if ((e.getY() < bounds.y)
                    || (e.getY() > (bounds.y + bounds.height))) {
                this.fadeOut();
                currRolloverPathId = null;
                // tree.putClientProperty(ROLLED_OVER_INDEX, null);
                return;
            }
            // check if this is the same index
            TreePathId newPathId = new TreePathId(closestPath);
            // TreePathId currPathId = (TreePathId) tree
            // .getClientProperty(ROLLED_OVER_INDEX);
            if ((currRolloverPathId != null)
                    && newPathId.equals(currRolloverPathId)) {
                // System.out.println("Same location " +
                // System.currentTimeMillis());
                // System.out.print("Current : ");
                // for (Object o1 : currPathId.path.getPath()) {
                // System.out.print(o1);
                // }
                // System.out.println("");
                // System.out.print("Closest : ");
                // for (Object o2 : newPathId.path.getPath()) {
                // System.out.print(o2);
                // }
                // System.out.println("");
                return;
            }

            this.fadeOut();
            FadeTracker.getInstance().trackFadeIn(FadeKind.ROLLOVER, tree,
                    newPathId, false,
                    new PathRepaintCallback(tree, closestPath));
            // System.out.println("Setting RO index to " + roIndex);
            currRolloverPathId = newPathId;
            // tree.putClientProperty(ROLLED_OVER_INDEX, newPathId);
        }

        /**
         * Initiates the fade out effect.
         */
        protected void fadeOut() {
            // TreePathId prevRoPath = (TreePathId) tree
            // .getClientProperty(ROLLED_OVER_INDEX);
            if (currRolloverPathId == null)
                return;

            FadeTracker.getInstance().trackFadeOut(FadeKind.ROLLOVER, tree,
                    currRolloverPathId, false,
                    new PathRepaintCallback(tree, currRolloverPathId.path));
        }
    }

    /**
     * Listener for selecting the entire rows.
     * 
     * @author Kirill Grouchnikov
     */
    private class RowSelectionListener extends MouseAdapter {
        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if (log.isTraceEnabled())
                log.trace("mousePressed(): e=" + e);
            if (!tree.isEnabled())
                return;
            TreePath closestPath = tree.getClosestPathForLocation(e.getX(), e
                    .getY());
            if (closestPath == null)
                return;
            Rectangle bounds = tree.getPathBounds(closestPath);
            // Process events outside the immediate bounds - fix for defect
            // 19 on substance-netbeans. This properly handles Ctrl and Shift
            // selections on trees.
            if ((e.getY() >= bounds.y)
                    && (e.getY() < (bounds.y + bounds.height))
                    && ((e.getX() < bounds.x) || (e.getX() > (bounds.x + bounds.width)))) {
                // tree.setSelectionPath(closestPath);

                // fix - don't select a node if the click was on the
                // expand control
                if (isLocationInExpandControl(closestPath, e.getX(), e.getY()))
                    return;
                selectPathForEvent(closestPath, e);
            }
        }
    }

    /**
     * Returns the pivot X for the cells rendered in the specified area. Used
     * for the smart tree scroll ({@link SubstanceLookAndFeel#TREE_SMART_SCROLL_ANIMATION_KIND}).
     * 
     * @param paintBounds
     *            Area bounds.
     * @return Pivot X for the cells rendered in the specified area
     */
    public int getPivotRendererX(Rectangle paintBounds) {
        TreePath initialPath = getClosestPathForLocation(tree, 0, paintBounds.y);
        Enumeration<?> paintingEnumerator = treeState
                .getVisiblePathsFrom(initialPath);
        int endY = paintBounds.y + paintBounds.height;

        int totalY = 0;
        int count = 0;

        if (initialPath != null && paintingEnumerator != null) {
            boolean done = false;
            Rectangle boundsBuffer = new Rectangle();
            Rectangle bounds;
            TreePath path;
            Insets insets = tree.getInsets();

            while (!done && paintingEnumerator.hasMoreElements()) {
                path = (TreePath) paintingEnumerator.nextElement();
                if (path != null) {
                    bounds = treeState.getBounds(path, boundsBuffer);
                    bounds.x += insets.left;
                    bounds.y += insets.top;

                    int currMedianX = bounds.x;// + bounds.width / 2;
                    totalY += currMedianX;
                    count++;
                    if ((bounds.y + bounds.height) >= endY)
                        done = true;
                } else {
                    done = true;
                }
            }
        }
        if (count == 0)
            return -1;
        return totalY / count - 2
                * SubstanceSizeUtils.getTreeIconSize(tree.getFont().getSize());
    }

    /**
     * Returns the previous state for the specified path.
     * 
     * @param pathId
     *            Path index.
     * @return The previous state for the specified path.
     */
    public ComponentState getPrevPathState(TreePathId pathId) {
        if (this.prevStateMap.containsKey(pathId))
            return this.prevStateMap.get(pathId);
        return ComponentState.DEFAULT;
    }

    /**
     * Returns the current state for the specified path.
     * 
     * @param pathId
     *            Path index.
     * @return The current state for the specified path.
     */
    public ComponentState getPathState(TreePathId pathId) {
        ButtonModel synthModel = new DefaultButtonModel();
        synthModel.setEnabled(this.tree.isEnabled());
        synthModel.setRollover((this.currRolloverPathId != null)
                && pathId.equals(this.currRolloverPathId));
        int rowIndex = this.tree.getRowForPath(pathId.path);
        synthModel.setSelected(this.tree.isRowSelected(rowIndex));
        return ComponentState.getState(synthModel, null);
    }
}
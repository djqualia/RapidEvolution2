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
package org.jvnet.substance.utils;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.LafWidgetUtilities;
import org.jvnet.lafwidget.animation.effects.GhostPaintingUtils;
import org.jvnet.lafwidget.layout.TransitionLayout;
import org.jvnet.lafwidget.utils.TrackableThread;
import org.jvnet.lafwidget.utils.LafConstants.AnimationKind;
import org.jvnet.substance.*;
import org.jvnet.substance.border.BorderPainterChangeListener;
import org.jvnet.substance.border.BorderPainterInfo;
import org.jvnet.substance.button.ButtonShaperChangeListener;
import org.jvnet.substance.button.ButtonShaperInfo;
import org.jvnet.substance.color.ColorScheme;
import org.jvnet.substance.painter.GradientPainterChangeListener;
import org.jvnet.substance.painter.GradientPainterInfo;
import org.jvnet.substance.skin.SkinInfo;
import org.jvnet.substance.theme.*;
import org.jvnet.substance.theme.SubstanceTheme.ThemeKind;
import org.jvnet.substance.title.*;
import org.jvnet.substance.utils.SubstanceConstants.FocusKind;
import org.jvnet.substance.utils.icon.SubstanceIconFactory;
import org.jvnet.substance.utils.icon.TransitionAwareIcon;
import org.jvnet.substance.utils.menu.TraitMenuHandler;
import org.jvnet.substance.watermark.*;

/**
 * Title pane for <b>Substance</b> look and feel.
 * 
 * 
 * RE2 FIXES: Commented out setting tooltips on frame buttons due to conflicts with custom tooltips
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceTitlePane extends JComponent {
    /**
     * Name for the client property that is set on a root pane to specify that
     * the heap status panel should be displayed permanently.
     */
    public static final String HEAP_STATUS_PANEL_PERMANENT = "substancelaf.internal.heapStatusPanelPermanent";

    // /**
    // * Max icon height for the title icon.
    // */
    // public static final int IMAGE_HEIGHT = 16;
    //
    // /**
    // * Max icon width for the title icon.
    // */
    // public static final int IMAGE_WIDTH = 16;
    //
    /**
     * PropertyChangeListener added to the JRootPane.
     */
    private PropertyChangeListener propertyChangeListener;

    /**
     * JMenuBar, typically renders the system menu items.
     */
    private JMenuBar menuBar;

    /**
     * Action used to close the Window.
     */
    private Action closeAction;

    /**
     * Action used to iconify the Frame.
     */
    private Action iconifyAction;

    /**
     * Action to restore the Frame size.
     */
    private Action restoreAction;

    /**
     * Action to restore the Frame size.
     */
    private Action maximizeAction;

    /**
     * Button used to maximize or restore the frame.
     */
    private JButton toggleButton;

    /**
     * Button used to minimize the frame
     */
    private JButton minimizeButton;

    /**
     * Button used to close the frame.
     */
    private JButton closeButton;

    /**
     * Listens for changes in the state of the Window listener to update the
     * state of the widgets.
     */
    private WindowListener windowListener;

    /**
     * Window we're currently in.
     */
    private Window window;

    /**
     * JRootPane rendering for.
     */
    private JRootPane rootPane;

    /**
     * Buffered Frame.state property. As state isn't bound, this is kept to
     * determine when to avoid updating widgets.
     */
    private int state;

    /**
     * SubstanceRootPaneUI that created us.
     */
    private SubstanceRootPaneUI rootPaneUI;

    /**
     * Indication whether any title pane can have heap status panel.
     */
    protected static boolean canHaveHeapStatusPanel;

    /**
     * The logfile name for the heap status panel. Can be <code>null</code> -
     * in this case the {@link HeapStatusThread} will not write heap
     * information.
     */
    protected static String heapStatusLogfileName;

    /**
     * The heap status panel of <code>this</code> title pane.
     */
    protected HeapStatusPanel heapStatusPanel;

    /**
     * The heap status toggle menu item of <code>this</code> title pane.
     */
    protected JCheckBoxMenuItem heapStatusMenuItem;

    /**
     * Listens on changes to <code>componentOrientation</code> and
     * {@link SubstanceLookAndFeel#WINDOW_MODIFIED} properties.
     */
    protected PropertyChangeListener propertyListener;

    protected TraitMenuHandler themeMenuHandler;

    protected ThemeChangeListener themeChangeListener;

    protected TraitMenuHandler watermarkMenuHandler;

    protected WatermarkChangeListener watermarkChangeListener;

    protected TraitMenuHandler buttonShaperMenuHandler;

    protected ButtonShaperChangeListener buttonShaperChangeListener;

    protected TraitMenuHandler gradientPainterMenuHandler;

    protected GradientPainterChangeListener gradientPainterChangeListener;

    protected TraitMenuHandler titlePainterMenuHandler;

    protected TitlePainterChangeListener titlePainterChangeListener;

    protected TraitMenuHandler borderPainterMenuHandler;

    protected BorderPainterChangeListener borderPainterChangeListener;

    protected MouseListener substanceDebugUiListener;

    public static final String HAS_BEEN_UNINSTALLED = "substancelaf.internal.titlePane.hasBeenUninstalled";

    /**
     * Panel that shows heap status and allows running the garbage collector.
     * 
     * @author Kirill Grouchnikov
     */
    public static class HeapStatusPanel extends JPanel {
        /**
         * The current heap size in kilobytes.
         */
        private int currHeapSizeKB;

        /**
         * The current used portion of heap in kilobytes.
         */
        private int currTakenHeapSizeKB;

        /**
         * History of used heap portion (in percents). Each value is in 0.0-1.0
         * range.
         */
        private LinkedList<Double> graphValues;

        /**
         * Creates new heap status panel.
         */
        public HeapStatusPanel() {
            this.graphValues = new LinkedList<Double>();
            HeapStatusThread.getInstance();
        }

        /**
         * Updates the values for <code>this</code> heap status panel.
         * 
         * @param currHeapSizeKB
         *            The current heap size in kilobytes.
         * @param currTakenHeapSizeKB
         *            The current used portion of heap in kilobytes.
         */
        public synchronized void updateStatus(int currHeapSizeKB,
                int currTakenHeapSizeKB) {
            this.currHeapSizeKB = currHeapSizeKB;
            this.currTakenHeapSizeKB = currTakenHeapSizeKB;
            double newGraphValue = (double) currTakenHeapSizeKB
                    / (double) currHeapSizeKB;
            this.graphValues.addLast(newGraphValue);
            this.repaint();
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        @Override
        public synchronized void paint(Graphics g) {
            Graphics2D graphics = (Graphics2D) g.create();

            ColorScheme scheme = SubstanceCoreUtilities.getActiveScheme(this
                    .getRootPane());

            // special case for mixed dark themes
            // if (scheme instanceof MixDarkBiColorScheme)
            // scheme = ((MixDarkBiColorScheme) scheme).getOrigDarkScheme();

            graphics.setColor(scheme.getDarkColor());
            int w = this.getWidth();
            int h = this.getHeight();

            graphics.drawRect(0, 0, w - 1, h - 1);

            graphics.setColor(scheme.getExtraLightColor());
            graphics.fillRect(1, 1, w - 2, h - 2);

            while (this.graphValues.size() > (w - 2))
                this.graphValues.removeFirst();

            int xOff = w - this.graphValues.size() - 1;
            graphics.setColor(scheme.getMidColor());
            int count = 0;
            for (double value : this.graphValues) {
                int valueH = (int) (value * (h - 2));
                graphics.drawLine(xOff + count, h - 1 - valueH, xOff + count,
                        h - 2);
                count++;
            }

            graphics.setFont(UIManager.getFont("Panel.font"));
            FontMetrics fm = graphics.getFontMetrics();

            StringBuffer longFormat = new StringBuffer();
            Formatter longFormatter = new Formatter(longFormat);
            longFormatter.format("%.1fMB / %.1fMB",
                    this.currTakenHeapSizeKB / 1024.f,
                    this.currHeapSizeKB / 1024.f);
            int strW = fm.stringWidth(longFormat.toString());
            int strH = fm.getAscent() + fm.getDescent();

            graphics.setColor(scheme.getForegroundColor());
            if (strW < (w - 5)) {
                graphics.drawString(longFormat.toString(), (w - strW) / 2,
                        (h + strH) / 2 - 2);
            } else {
                String shortFormat = (this.currTakenHeapSizeKB / 1024)
                        + "MB / " + (this.currHeapSizeKB / 1024) + "MB";
                strW = fm.stringWidth(shortFormat);
                graphics.drawString(shortFormat, (w - strW) / 2,
                        (h + strH) / 2 - 2);
            }

            graphics.dispose();
        }
    }

    /**
     * Thread for heap status panel.
     */
    public static class HeapStatusThread extends TrackableThread {
        /**
         * Current heap size in kilobytes.
         */
        private int heapSizeKB;

        /**
         * Current used portion of heap in kilobytes.
         */
        private int takenHeapSizeKB;

        /**
         * All heap status panels.
         */
        private static Set<WeakReference<HeapStatusPanel>> panels = new HashSet<WeakReference<HeapStatusPanel>>();

        /**
         * Single instance of <code>this</code> thread.
         */
        private static HeapStatusThread instance;

        /**
         * Formatter object (for logfile).
         */
        private SimpleDateFormat format;

        /**
         * Signifies whether a stop request has been issued on <code>this</code>
         * thread using the {@link #requestStop()} call.
         */
        private boolean isStopRequested;

        /**
         * Simple constructor. Defined private for singleton.
         * 
         * @see #getInstance()
         */
        private HeapStatusThread() {
            this.format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
            this.isStopRequested = false;
            this.setName("Substance heap status");
        }

        /**
         * Gets singleton instance of <code>this</code> thread.
         * 
         * @return Singleton instance of <code>this</code> thread.
         */
        public synchronized static HeapStatusThread getInstance() {
            if (HeapStatusThread.instance == null) {
                HeapStatusThread.instance = new HeapStatusThread();
                HeapStatusThread.instance.start();
            }
            return HeapStatusThread.instance;
        }

        /**
         * Registers new heap status panel with <code>this</code> thread.
         * 
         * @param panel
         *            Heap statuc panel.
         */
        public static synchronized void registerPanel(HeapStatusPanel panel) {
            panels.add(new WeakReference<HeapStatusPanel>(panel));
        }

        /**
         * Unregisters new heap status panel from <code>this</code> thread.
         * 
         * @param panel
         *            Heap statuc panel.
         */
        public static synchronized void unregisterPanel(HeapStatusPanel panel) {
            for (Iterator<WeakReference<HeapStatusPanel>> it = panels
                    .iterator(); it.hasNext();) {
                WeakReference<HeapStatusPanel> ref = it.next();
                HeapStatusPanel currPanel = ref.get();
                if (panel == currPanel) {
                    it.remove();
                    return;
                }
            }
        }

        /**
         * Updates the values of heap status.
         */
        private synchronized void updateHeapCounts() {
            long heapSize = Runtime.getRuntime().totalMemory();
            long heapFreeSize = Runtime.getRuntime().freeMemory();

            this.heapSizeKB = (int) (heapSize / 1024);
            this.takenHeapSizeKB = (int) ((heapSize - heapFreeSize) / 1024);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            while (!this.isStopRequested) {
                try {
                    // update every 0.5 seconds
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                }
                this.updateHeapCounts();
                for (Iterator<WeakReference<HeapStatusPanel>> it = panels
                        .iterator(); it.hasNext();) {
                    WeakReference<HeapStatusPanel> refPanel = it.next();
                    HeapStatusPanel panel = refPanel.get();
                    if (panel == null) {
                        // prune
                        panels.remove(it);
                        continue;
                    }

                    panel.updateStatus(this.heapSizeKB, this.takenHeapSizeKB);
                }
                // see if need to put info in log file
                if (SubstanceTitlePane.heapStatusLogfileName != null) {
                    PrintWriter pw = null;
                    try {
                        pw = new PrintWriter(new FileWriter(
                                SubstanceTitlePane.heapStatusLogfileName, true));
                        pw.println(this.format.format(new Date()) + " "
                                + this.takenHeapSizeKB + "KB / "
                                + this.heapSizeKB + "KB");
                    } catch (IOException ioe) {

                    } finally {
                        if (pw != null) {
                            pw.close();
                        }
                    }
                }
            }
        }

        @Override
        protected void requestStop() {
            this.isStopRequested = true;
            HeapStatusThread.instance = null;
        }
    }

    /**
     * Creates a new title pane.
     * 
     * @param root
     *            Root pane.
     * @param ui
     *            Root pane UI.
     */
    public SubstanceTitlePane(JRootPane root, SubstanceRootPaneUI ui) {
        this.rootPane = root;
        this.rootPaneUI = ui;

        this.state = -1;

        this.installSubcomponents();
        this.installDefaults();

        this.setLayout(this.createLayout());

        this.setToolTipText(this.getTitle());
    }

    /**
     * Uninstalls the necessary state.
     */
    public void uninstall() {
        this.uninstallListeners();
        this.window = null;
        // Swing bug (?) - the updateComponentTree never gets to the
        // system menu (and in our case we have radio menu items with
        // rollover listeners). Fix for defect 109 - memory leak on theme
        // switch
        if ((this.menuBar != null) && (this.menuBar.getMenuCount() > 0)) {
            this.menuBar.getUI().uninstallUI(this.menuBar);
            SubstanceCoreUtilities.uninstallMenu(this.menuBar.getMenu(0));
        }

        if (SubstanceTitlePane.canHaveHeapStatusPanel) {
            if (this.heapStatusPanel != null) {
                for (MouseListener listener : this.heapStatusPanel
                        .getMouseListeners())
                    this.heapStatusPanel.removeMouseListener(listener);
                HeapStatusThread.unregisterPanel(this.heapStatusPanel);
                this.remove(this.heapStatusPanel);
            }
        }

        if (this.menuBar != null)
            this.menuBar.removeAll();
        this.removeAll();

        SubstanceLookAndFeel
                .unregisterThemeChangeListener(this.themeChangeListener);
        this.themeChangeListener = null;
        SubstanceLookAndFeel
                .unregisterWatermarkChangeListener(this.watermarkChangeListener);
        this.watermarkChangeListener = null;
        SubstanceLookAndFeel
                .unregisterButtonShaperChangeListener(this.buttonShaperChangeListener);
        this.buttonShaperChangeListener = null;
        SubstanceLookAndFeel
                .unregisterGradientPainterChangeListener(this.gradientPainterChangeListener);
        this.gradientPainterChangeListener = null;
        SubstanceLookAndFeel
                .unregisterTitlePainterChangeListener(this.titlePainterChangeListener);
        this.titlePainterChangeListener = null;
        SubstanceLookAndFeel
                .unregisterBorderPainterChangeListener(this.borderPainterChangeListener);
        this.borderPainterChangeListener = null;
    }

    /**
     * Installs the necessary listeners.
     */
    private void installListeners() {
        if (this.window != null) {
            this.windowListener = new WindowHandler();
            this.window.addWindowListener(this.windowListener);
            this.propertyChangeListener = new PropertyChangeHandler();
            this.window.addPropertyChangeListener(this.propertyChangeListener);
        }

        // Property change listener for pulsating close button
        // when window has been marked as changed.
        // Fix for defect 109 - memory leak on theme change.
        this.propertyListener = new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                if (SubstanceLookAndFeel.WINDOW_MODIFIED.equals(evt
                        .getPropertyName())) {
                    syncCloseButtonTooltip();
                    // if (Boolean.TRUE.equals(evt.getNewValue())) {
                    // SubstanceTitlePane.this.closeButton
                    // .setToolTipText(SubstanceLookAndFeel
                    // .getLabelBundle().getString(
                    // "SystemMenu.close")
                    // + " ["
                    // + SubstanceLookAndFeel
                    // .getLabelBundle()
                    // .getString(
                    // "Tooltip.contentsNotSaved")
                    // + "]");
                    // } else {
                    // SubstanceTitlePane.this.closeButton
                    // .setToolTipText(SubstanceLookAndFeel
                    // .getLabelBundle().getString(
                    // "SystemMenu.close"));
                    // }
                    // SubstanceTitlePane.this.closeButton.repaint();
                }

                if ("componentOrientation".equals(evt.getPropertyName())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (SubstanceTitlePane.this.menuBar != null) {
                                SubstanceTitlePane.this.menuBar
                                        .applyComponentOrientation((ComponentOrientation) evt
                                                .getNewValue());
                            }
                        }
                    });
                }
            }
        };
        // Wire it on the frame itself and its root pane.
        this.rootPane.addPropertyChangeListener(this.propertyListener);
        if (this.getFrame() != null)
            this.getFrame().addPropertyChangeListener(this.propertyListener);

        if (SubstanceLookAndFeel.isDebugUiMode()) {
            this.substanceDebugUiListener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    process(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    process(e);
                }

                protected void process(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        JPopupMenu popup = new JPopupMenu();
                        JMenu cbMenu = new JMenu("Color blindness");
                        JMenuItem protanopiaCurrent = new JMenuItem(
                                "Protanopia current");
                        protanopiaCurrent.addActionListener(new ThemeChanger(
                                SubstanceLookAndFeel.getTheme().protanopia()));
                        cbMenu.add(protanopiaCurrent);
                        JMenuItem deuteranopiaCurrent = new JMenuItem(
                                "Deuteranopia current");
                        deuteranopiaCurrent
                                .addActionListener(new ThemeChanger(
                                        SubstanceLookAndFeel.getTheme()
                                                .deuteranopia()));
                        cbMenu.add(deuteranopiaCurrent);
                        JMenuItem tritanopiaCurrent = new JMenuItem(
                                "Tritanopia current");
                        tritanopiaCurrent.addActionListener(new ThemeChanger(
                                SubstanceLookAndFeel.getTheme().tritanopia()));
                        cbMenu.add(tritanopiaCurrent);

                        cbMenu.addSeparator();

                        JMenuItem restoreOriginal = new JMenuItem(
                                "Restore original");
                        if (SubstanceLookAndFeel.getTheme() instanceof SubstanceColorBlindTheme) {
                            restoreOriginal
                                    .addActionListener(new ThemeChanger(
                                            ((SubstanceColorBlindTheme) SubstanceLookAndFeel
                                                    .getTheme())
                                                    .getOriginalTheme()));
                        } else {
                            restoreOriginal.setEnabled(false);
                        }
                        cbMenu.add(restoreOriginal);

                        popup.add(cbMenu);

                        JMenu animMenu = new JMenu("Animation rate");
                        JMenuItem debugNone = new JMenuItem("None");
                        debugNone.addActionListener(new AnimationChanger(
                                AnimationKind.NONE));
                        animMenu.add(debugNone);
                        JMenuItem debugAnim = new JMenuItem(
                                "Debug rate (extra slow)");
                        debugAnim.addActionListener(new AnimationChanger(
                                AnimationKind.DEBUG));
                        animMenu.add(debugAnim);
                        JMenuItem debugAnimFast = new JMenuItem(
                                "Debug rate (faster)");
                        debugAnimFast.addActionListener(new AnimationChanger(
                                AnimationKind.DEBUG_FAST));
                        animMenu.add(debugAnimFast);
                        JMenuItem debugSlow = new JMenuItem("Slow rate");
                        debugSlow.addActionListener(new AnimationChanger(
                                AnimationKind.SLOW));
                        animMenu.add(debugSlow);
                        JMenuItem debugRegular = new JMenuItem("Regular rate");
                        debugRegular.addActionListener(new AnimationChanger(
                                AnimationKind.REGULAR));
                        animMenu.add(debugRegular);
                        JMenuItem debugFast = new JMenuItem("Fast rate");
                        debugFast.addActionListener(new AnimationChanger(
                                AnimationKind.FAST));
                        animMenu.add(debugFast);

                        popup.add(animMenu);

                        JMenu focusMenu = new JMenu("Focus kind");
                        for (FocusKind fKind : FocusKind.values()) {
                            JMenuItem focusMenuItem = new JMenuItem(fKind
                                    .name().toLowerCase());
                            focusMenuItem
                                    .addActionListener(new FocusKindChanger(
                                            fKind));
                            focusMenu.add(focusMenuItem);
                        }
                        popup.add(focusMenu);

                        JMenuItem dumpHierarchy = new JMenuItem(
                                "Dump hierarchy");
                        dumpHierarchy.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                dump(rootPane, 0);
                            }
                        });
                        popup.add(dumpHierarchy);

                        final JCheckBoxMenuItem ltrChange = new JCheckBoxMenuItem(
                                "Is left-to-right");
                        ltrChange.setSelected(rootPane
                                .getComponentOrientation().isLeftToRight());
                        ltrChange.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        rootPane
                                                .applyComponentOrientation(ltrChange
                                                        .isSelected() ? ComponentOrientation.LEFT_TO_RIGHT
                                                        : ComponentOrientation.RIGHT_TO_LEFT);
                                    }
                                });
                            }
                        });
                        popup.add(ltrChange);

                        final JCheckBoxMenuItem useThemedIcons = new JCheckBoxMenuItem(
                                "Use themed icons");
                        useThemedIcons.setSelected(SubstanceCoreUtilities
                                .useThemedDefaultIcon(null));
                        useThemedIcons.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        UIManager
                                                .put(
                                                        SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS,
                                                        useThemedIcons
                                                                .isSelected() ? Boolean.TRUE
                                                                : null);
                                        rootPane.repaint();
                                    }
                                });
                            }
                        });
                        popup.add(useThemedIcons);

                        final JCheckBoxMenuItem ghostDebugMode = new JCheckBoxMenuItem(
                                "Ghost debug mode");
                        ghostDebugMode.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        ghostDebugMode.setEnabled(false);
                                        GhostPaintingUtils.MAX_ICON_GHOSTING_ALPHA = 0.8f;
                                        GhostPaintingUtils.MIN_ICON_GHOSTING_ALPHA = 0.6f;
                                        GhostPaintingUtils.MAX_PRESS_GHOSTING_ALPHA = 0.8f;
                                        GhostPaintingUtils.MIN_PRESS_GHOSTING_ALPHA = 0.6f;
                                        GhostPaintingUtils.DECAY_FACTOR = 0.7f;
                                    }
                                });
                            }
                        });
                        popup.add(ghostDebugMode);

                        popup.show(SubstanceTitlePane.this, e.getX(), e.getY());
                    }
                }
            };
            this.addMouseListener(this.substanceDebugUiListener);
        }
    }

    /**
     * Uninstalls the necessary listeners.
     */
    private void uninstallListeners() {
        if (this.window != null) {
            this.window.removeWindowListener(this.windowListener);
            this.windowListener = null;
            this.window
                    .removePropertyChangeListener(this.propertyChangeListener);
            this.propertyChangeListener = null;
        }

        // Fix for defect 109 - memory leak on theme change.
        this.rootPane.removePropertyChangeListener(this.propertyListener);
        if (this.getFrame() != null)
            this.getFrame().removePropertyChangeListener(this.propertyListener);
        this.propertyListener = null;

        if (this.substanceDebugUiListener != null) {
            this.removeMouseListener(this.substanceDebugUiListener);
            this.substanceDebugUiListener = null;
        }
    }

    /**
     * Returns the <code>JRootPane</code> this was created for.
     */
    @Override
    public JRootPane getRootPane() {
        return this.rootPane;
    }

    /**
     * Returns the decoration style of the <code>JRootPane</code>.
     * 
     * @return Decoration style of the <code>JRootPane</code>.
     */
    private int getWindowDecorationStyle() {
        return this.getRootPane().getWindowDecorationStyle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#addNotify()
     */
    @Override
    public void addNotify() {
        super.addNotify();

        this.uninstallListeners();

        this.window = SwingUtilities.getWindowAncestor(this);
        if (this.window != null) {
            this.setActive(this.window.isActive());
            if (Boolean.TRUE.equals(SwingUtilities.getRootPane(this)
                    .getClientProperty(HAS_BEEN_UNINSTALLED))) {
                // System.out.println("Reinstalling");
                this.installSubcomponents();
                this.installDefaults();
                this.setLayout(this.createLayout());
                this.setToolTipText(this.getTitle());
            }
            if (this.window instanceof Frame) {
                this.setState(((Frame) this.window).getExtendedState());
            } else {
                this.setState(0);
            }
            this.installListeners();
        }
        this.setToolTipText(this.getTitle());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#removeNotify()
     */
    @Override
    public void removeNotify() {
        // System.out.println("Uninstalling");
        SwingUtilities.getRootPane(this).putClientProperty(
                HAS_BEEN_UNINSTALLED, Boolean.TRUE);
        super.removeNotify();

        this.uninstall();
        this.window = null;
        //
        // // Fix for defect 189 - memory leak on disposed frames
        // if (menuBar != null) {
        // MenuBarUI menuBarUI = menuBar.getUI();
        // if (menuBarUI instanceof SubstanceMenuBarUI) {
        // ((SubstanceMenuBarUI) menuBarUI).uninstallUI(menuBar);
        // }
        // }
    }

    /**
     * Adds any sub-Components contained in the <code>SubstanceTitlePane</code>.
     */
    private void installSubcomponents() {
        int decorationStyle = this.getWindowDecorationStyle();
        if (decorationStyle == JRootPane.FRAME) {
            this.createActions();
            this.menuBar = this.createMenuBar();
            this.add(this.menuBar);
            this.createButtons();
            this.add(this.minimizeButton);
            this.add(this.toggleButton);
            this.add(this.closeButton);

            if (SubstanceTitlePane.canHaveHeapStatusPanel) {
                this.heapStatusPanel = new HeapStatusPanel();
                this.add(this.heapStatusPanel);
                boolean isHeapStatusPanelShowing = Boolean.TRUE
                        .equals(this.rootPane
                                .getClientProperty(SubstanceLookAndFeel.HEAP_STATUS_PANEL));
                this.heapStatusPanel.setVisible(isHeapStatusPanelShowing);
                this.heapStatusPanel.setPreferredSize(new Dimension(80, this
                        .getPreferredSize().height));
                this.heapStatusPanel.setToolTipText(SubstanceCoreUtilities
                        .getResourceBundle(rootPane).getString(
                                "Tooltip.heapStatusPanel"));
                this.heapStatusPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.gc();
                    }
                });

                HeapStatusThread.registerPanel(this.heapStatusPanel);
            }
        } else {
            if ((decorationStyle == JRootPane.PLAIN_DIALOG)
                    || (decorationStyle == JRootPane.INFORMATION_DIALOG)
                    || (decorationStyle == JRootPane.ERROR_DIALOG)
                    || (decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG)
                    || (decorationStyle == JRootPane.FILE_CHOOSER_DIALOG)
                    || (decorationStyle == JRootPane.QUESTION_DIALOG)
                    || (decorationStyle == JRootPane.WARNING_DIALOG)) {
                this.createActions();
                this.createButtons();
                this.add(this.closeButton);
            }
        }
    }

    /**
     * Installs the fonts and necessary properties.
     */
    private void installDefaults() {
        this.setFont(UIManager.getFont("InternalFrame.titleFont", this
                .getLocale()));
    }

    /**
     * Returns the <code>JMenuBar</code> displaying the appropriate system
     * menu items.
     * 
     * @return <code>JMenuBar</code> displaying the appropriate system menu
     *         items.
     */
    protected JMenuBar createMenuBar() {
        this.menuBar = new SubstanceMenuBar();
        this.menuBar.setFocusable(false);
        this.menuBar.setBorderPainted(true);
        this.menuBar.add(this.createMenu());
        this.menuBar.setOpaque(false);
        // support for RTL
        this.menuBar.applyComponentOrientation(this.rootPane
                .getComponentOrientation());
        return this.menuBar;
    }

    /**
     * Create the <code>Action</code>s that get associated with the buttons
     * and menu items.
     */
    private void createActions() {
        this.closeAction = new CloseAction();
        if (this.getWindowDecorationStyle() == JRootPane.FRAME) {
            this.iconifyAction = new IconifyAction();
            this.restoreAction = new RestoreAction();
            this.maximizeAction = new MaximizeAction();
        }
    }

    /**
     * Returns the <code>JMenu</code> displaying the appropriate menu items
     * for manipulating the Frame.
     * 
     * @return <code>JMenu</code> displaying the appropriate menu items for
     *         manipulating the Frame.
     */
    private JMenu createMenu() {
        JMenu menu = new JMenu("");
        menu.setOpaque(false);
        menu.setBackground(null);
        if (this.getWindowDecorationStyle() == JRootPane.FRAME) {
            this.addMenuItems(menu);
        }
        return menu;
    }

    /**
     * Adds the necessary <code>JMenuItem</code>s to the specified menu.
     * 
     * @param menu
     *            Menu.
     */
    private void addMenuItems(JMenu menu) {
        menu.add(this.restoreAction);

        menu.add(this.iconifyAction);

        if (Toolkit.getDefaultToolkit().isFrameStateSupported(
                Frame.MAXIMIZED_BOTH)) {
            menu.add(this.maximizeAction);
        }

        if (SubstanceLookAndFeel.toShowExtraElements()) {
            menu.addSeparator();
            JMenu skinMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString("SystemMenu.skins"));
            Map<String, SkinInfo> allSkins = SubstanceLookAndFeel.getAllSkins();
            for (Map.Entry<String, SkinInfo> skinEntry : allSkins.entrySet()) {
                final String skinClassName = skinEntry.getValue()
                        .getClassName();
                JMenuItem jmiSkin = new JMenuItem(skinEntry.getKey());
                jmiSkin.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                SubstanceLookAndFeel.setSkin(skinClassName);
                            }
                        });
                    }
                });

                // try {
                // SubstanceSkin skin = ((SubstanceSkin) Class.forName(
                // skinClassName).newInstance());
                // Container cont = rootPane.getParent();
                // if (cont instanceof Frame) {
                // Image icon = ((Frame) cont).getIconImage();
                // if (icon != null) {
                // jmiSkin.setIcon(new ImageIcon(SubstanceImageCreator
                // .getThemeImage(new ImageIcon(icon), skin
                // .getTheme())));
                // }
                // }
                // } catch (Exception exc) {
                // }
                //
                skinMenu.add(jmiSkin);
            }
            menu.add(skinMenu);

            JMenu themeMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString("SystemMenu.themes"));
            ButtonGroup bgTheme = new ButtonGroup();
            JMenu brightThemes = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.themesBright"));
            JMenu coldThemes = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.themesCold"));
            JMenu darkThemes = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.themesDark"));
            JMenu invertedThemes = SubstanceLookAndFeel
                    .toEnableInvertedThemes() ? new JMenu(
                    SubstanceCoreUtilities.getResourceBundle(rootPane)
                            .getString("SystemMenu.themesInverted")) : null;
            JMenu negatedThemes = SubstanceLookAndFeel.toEnableNegatedThemes() ? new JMenu(
                    SubstanceCoreUtilities.getResourceBundle(rootPane)
                            .getString("SystemMenu.themesNegated"))
                    : null;
            JMenu mixedThemes = SubstanceLookAndFeel.hasMixedThemes() ? new JMenu(
                    SubstanceCoreUtilities.getResourceBundle(rootPane)
                            .getString("SystemMenu.themesMixed"))
                    : null;

            themeMenu.add(brightThemes);
            themeMenu.add(coldThemes);
            themeMenu.add(darkThemes);
            if (invertedThemes != null)
                themeMenu.add(invertedThemes);
            if (negatedThemes != null)
                themeMenu.add(negatedThemes);
            if (mixedThemes != null)
                themeMenu.add(mixedThemes);

            this.themeMenuHandler = new TraitMenuHandler();
            Map<String, ThemeInfo> allThemes = SubstanceLookAndFeel
                    .getAllThemes();
            for (Map.Entry<String, ThemeInfo> themeEntry : allThemes.entrySet()) {
                final ThemeInfo themeInfo = themeEntry.getValue();
                final String themeClassName = themeInfo.getClassName();
                JRadioButtonMenuItem jmiTheme = new JRadioButtonMenuItem(
                        themeEntry.getKey());
                this.themeMenuHandler.addTraitButton(
                        themeInfo.getDisplayName(), jmiTheme);

                jmiTheme.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                SubstanceLookAndFeel.setCurrentTheme(themeInfo);
                                // update all existing root panes
                                for (Frame frame : Frame.getFrames()) {
                                    SwingUtilities.updateComponentTreeUI(frame);
                                }
                            }
                        });
                    }
                });
                try {
                    switch (themeInfo.getThemeKind()) {
                    case MIXED:
                        MixedThemeInfo mixedThemeInfo = (MixedThemeInfo) themeInfo;
                        String[] themeClassNames = mixedThemeInfo
                                .getThemeClassNames();
                        SubstanceTheme[] themeInstances = new SubstanceTheme[themeClassNames.length];
                        for (int i = 0; i < themeClassNames.length; i++) {
                            Class<?> themeClass = Class
                                    .forName(themeClassNames[i]);
                            themeInstances[i] = (SubstanceTheme) themeClass
                                    .newInstance();
                        }

                        SubstanceTheme mixTheme = new SubstanceMixTheme(
                                themeInstances);
                        jmiTheme.setIcon(SubstanceImageCreator
                                .getThemeIcon(mixTheme));
                        break;
                    default:
                        Class<?> themeClass = Class.forName(themeClassName);
                        SubstanceTheme theme = (SubstanceTheme) themeClass
                                .newInstance();
                        if (themeInfo.getThemeKind() == ThemeKind.INVERTED)
                            theme = new SubstanceInvertedTheme(theme);
                        if (themeInfo.getThemeKind() == ThemeKind.NEGATED)
                            theme = new SubstanceNegatedTheme(theme);
                        jmiTheme.setIcon(SubstanceImageCreator
                                .getThemeIcon(theme));
                    }
                } catch (Exception exc) {
                    continue;
                }
                if (themeEntry.getKey().equals(
                        SubstanceLookAndFeel.getCurrentThemeName())) {
                    jmiTheme.setSelected(true);
                }
                switch (themeEntry.getValue().getThemeKind()) {
                case BRIGHT:
                    brightThemes.add(jmiTheme);
                    break;
                case COLD:
                    coldThemes.add(jmiTheme);
                    break;
                case DARK:
                    darkThemes.add(jmiTheme);
                    break;
                case INVERTED:
                    invertedThemes.add(jmiTheme);
                    break;
                case NEGATED:
                    negatedThemes.add(jmiTheme);
                    break;
                case MIXED:
                    mixedThemes.add(jmiTheme);
                    break;
                }
                bgTheme.add(jmiTheme);
            }

            themeMenu.setIcon(SubstanceImageCreator.getThemeIcon(null));
            menu.add(themeMenu);

            this.themeChangeListener = new ThemeChangeListener() {
                public void themeChanged() {
                    themeMenuHandler.selectTraitButton(SubstanceLookAndFeel
                            .getTheme());
                }
            };
            SubstanceLookAndFeel
                    .registerThemeChangeListener(this.themeChangeListener);

            JMenu watermarkMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.watermarks"));
            ButtonGroup bgWatermark = new ButtonGroup();
            this.watermarkMenuHandler = new TraitMenuHandler();
            Map<String, WatermarkInfo> allWatermarks = SubstanceLookAndFeel
                    .getAllWatermarks();
            for (Map.Entry<String, WatermarkInfo> watermarkEntry : allWatermarks
                    .entrySet()) {
                final String watermarkClassName = watermarkEntry.getValue()
                        .getClassName();
                JRadioButtonMenuItem jmiWatermark = new JRadioButtonMenuItem(
                        watermarkEntry.getKey());
                this.watermarkMenuHandler.addTraitButton(watermarkEntry
                        .getValue().getDisplayName(), jmiWatermark);
                jmiWatermark.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                SubstanceLookAndFeel
                                        .setCurrentWatermark(watermarkClassName);
                                // update all existing root panes
                                for (Frame frame : Frame.getFrames()) {
                                    SwingUtilities.updateComponentTreeUI(frame);
                                }
                            }
                        });
                    }
                });
                if (watermarkEntry.getKey().equals(
                        SubstanceLookAndFeel.getCurrentWatermarkName())) {
                    jmiWatermark.setSelected(true);
                }

                try {
                    Class<?> watermarkClass = Class.forName(watermarkClassName);
                    SubstanceWatermark watermark = (SubstanceWatermark) watermarkClass
                            .newInstance();
                    jmiWatermark.setIcon(SubstanceImageCreator
                            .getWatermarkIcon(watermark));
                } catch (Exception exc) {
                }

                bgWatermark.add(jmiWatermark);
                watermarkMenu.add(jmiWatermark);
            }
            menu.add(watermarkMenu);
            this.watermarkChangeListener = new WatermarkChangeListener() {
                public void watermarkChanged() {
                    watermarkMenuHandler.selectTraitButton(SubstanceLookAndFeel
                            .getCurrentWatermark());
                }
            };
            SubstanceLookAndFeel
                    .registerWatermarkChangeListener(this.watermarkChangeListener);

            JMenu buttonShaperMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.buttonShapers"));
            ButtonGroup bgButtonShaper = new ButtonGroup();
            this.buttonShaperMenuHandler = new TraitMenuHandler();
            Map<String, ButtonShaperInfo> allButtonShapers = SubstanceLookAndFeel
                    .getAllButtonShapers();
            for (Map.Entry<String, ButtonShaperInfo> buttonShaperEntry : allButtonShapers
                    .entrySet()) {
                final String buttonShaperClassName = buttonShaperEntry
                        .getValue().getClassName();
                JRadioButtonMenuItem jmiButtonShaper = new JRadioButtonMenuItem(
                        buttonShaperEntry.getKey());
                this.buttonShaperMenuHandler.addTraitButton(buttonShaperEntry
                        .getValue().getDisplayName(), jmiButtonShaper);
                jmiButtonShaper.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                SubstanceLookAndFeel
                                        .setCurrentButtonShaper(buttonShaperClassName);
                                // update all existing root panes
                                for (Frame frame : Frame.getFrames()) {
                                    SwingUtilities.updateComponentTreeUI(frame);
                                }
                            }
                        });
                    }
                });
                if (buttonShaperEntry.getKey().equals(
                        SubstanceLookAndFeel.getCurrentButtonShaperName())) {
                    jmiButtonShaper.setSelected(true);
                }
                bgButtonShaper.add(jmiButtonShaper);
                buttonShaperMenu.add(jmiButtonShaper);
            }
            menu.add(buttonShaperMenu);
            this.buttonShaperChangeListener = new ButtonShaperChangeListener() {
                public void buttonShaperChanged() {
                    buttonShaperMenuHandler
                            .selectTraitButton(SubstanceLookAndFeel
                                    .getCurrentButtonShaper());
                }
            };
            SubstanceLookAndFeel
                    .registerButtonShaperChangeListener(this.buttonShaperChangeListener);

            JMenu borderPainterMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.borderPainters"));
            ButtonGroup bgBorderPainter = new ButtonGroup();
            this.borderPainterMenuHandler = new TraitMenuHandler();
            Map<String, BorderPainterInfo> allBorderPainters = SubstanceLookAndFeel
                    .getAllBorderPainters();
            for (Map.Entry<String, BorderPainterInfo> borderPainterEntry : allBorderPainters
                    .entrySet()) {
                final String borderPainterClassName = borderPainterEntry
                        .getValue().getClassName();
                JRadioButtonMenuItem jmiBorderPainter = new JRadioButtonMenuItem(
                        borderPainterEntry.getKey());
                this.borderPainterMenuHandler.addTraitButton(borderPainterEntry
                        .getValue().getDisplayName(), jmiBorderPainter);
                jmiBorderPainter.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {

                                SubstanceLookAndFeel
                                        .setCurrentBorderPainter(borderPainterClassName);
                                // update all existing root panes
                                for (Frame frame : Frame.getFrames()) {
                                    SwingUtilities.updateComponentTreeUI(frame);
                                }
                            }
                        });
                    }
                });
                if (borderPainterEntry.getKey().equals(
                        SubstanceLookAndFeel.getCurrentBorderPainterName())) {
                    jmiBorderPainter.setSelected(true);
                }
                bgBorderPainter.add(jmiBorderPainter);
                borderPainterMenu.add(jmiBorderPainter);
            }
            menu.add(borderPainterMenu);
            this.borderPainterChangeListener = new BorderPainterChangeListener() {
                public void borderPainterChanged() {
                    borderPainterMenuHandler
                            .selectTraitButton(SubstanceLookAndFeel
                                    .getCurrentBorderPainter());
                }
            };
            SubstanceLookAndFeel
                    .registerBorderPainterChangeListener(this.borderPainterChangeListener);

            JMenu gradientPainterMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.gradientPainters"));
            ButtonGroup bgGradientPainter = new ButtonGroup();
            this.gradientPainterMenuHandler = new TraitMenuHandler();
            Map<String, GradientPainterInfo> allGradientPainters = SubstanceLookAndFeel
                    .getAllGradientPainters();
            for (Map.Entry<String, GradientPainterInfo> gradientPainterEntry : allGradientPainters
                    .entrySet()) {
                final String gradientPainterClassName = gradientPainterEntry
                        .getValue().getClassName();
                JRadioButtonMenuItem jmiGradientPainter = new JRadioButtonMenuItem(
                        gradientPainterEntry.getKey());
                this.gradientPainterMenuHandler.addTraitButton(
                        gradientPainterEntry.getValue().getDisplayName(),
                        jmiGradientPainter);
                jmiGradientPainter.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                SubstanceLookAndFeel
                                        .setCurrentGradientPainter(gradientPainterClassName);
                                // update all existing root panes
                                for (Frame frame : Frame.getFrames()) {
                                    SwingUtilities.updateComponentTreeUI(frame);
                                }
                            }
                        });
                    }
                });
                if (gradientPainterEntry.getKey().equals(
                        SubstanceLookAndFeel.getCurrentGradientPainterName())) {
                    jmiGradientPainter.setSelected(true);
                }
                bgGradientPainter.add(jmiGradientPainter);
                gradientPainterMenu.add(jmiGradientPainter);
            }
            menu.add(gradientPainterMenu);
            this.gradientPainterChangeListener = new GradientPainterChangeListener() {
                public void gradientPainterChanged() {
                    gradientPainterMenuHandler
                            .selectTraitButton(SubstanceLookAndFeel
                                    .getCurrentGradientPainter());
                }
            };
            SubstanceLookAndFeel
                    .registerGradientPainterChangeListener(this.gradientPainterChangeListener);

            JMenu titlePainterMenu = new JMenu(SubstanceCoreUtilities
                    .getResourceBundle(rootPane).getString(
                            "SystemMenu.titlePainters"));
            ButtonGroup bgTitlePainter = new ButtonGroup();
            this.titlePainterMenuHandler = new TraitMenuHandler();
            Map<String, TitlePainterInfo> allTitlePainters = SubstanceLookAndFeel
                    .getAllTitlePainters();
            for (Map.Entry<String, TitlePainterInfo> titlePainterEntry : allTitlePainters
                    .entrySet()) {
                final String titlePainterClassName = titlePainterEntry
                        .getValue().getClassName();
                JRadioButtonMenuItem jmiTitlePainter = new JRadioButtonMenuItem(
                        titlePainterEntry.getKey());
                this.titlePainterMenuHandler.addTraitButton(titlePainterEntry
                        .getValue().getDisplayName(), jmiTitlePainter);
                jmiTitlePainter.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {

                                SubstanceLookAndFeel
                                        .setCurrentTitlePainter(titlePainterClassName);
                                // update all existing root panes
                                for (Frame frame : Frame.getFrames()) {
                                    SwingUtilities.updateComponentTreeUI(frame);
                                }
                            }
                        });
                    }
                });
                if (titlePainterEntry.getKey().equals(
                        SubstanceLookAndFeel.getCurrentTitlePainterName())) {
                    jmiTitlePainter.setSelected(true);
                }
                bgTitlePainter.add(jmiTitlePainter);
                titlePainterMenu.add(jmiTitlePainter);
            }
            menu.add(titlePainterMenu);
            this.titlePainterChangeListener = new TitlePainterChangeListener() {
                public void titlePainterChanged() {
                    titlePainterMenuHandler
                            .selectTraitButton(SubstanceLookAndFeel
                                    .getCurrentTitlePainter());
                }
            };
            SubstanceLookAndFeel
                    .registerTitlePainterChangeListener(this.titlePainterChangeListener);

            if (SubstanceTitlePane.canHaveHeapStatusPanel
                    && (!(this.rootPane
                            .getClientProperty(SubstanceTitlePane.HEAP_STATUS_PANEL_PERMANENT) instanceof Boolean))) {
                this.heapStatusMenuItem = new JCheckBoxMenuItem(
                        SubstanceCoreUtilities.getResourceBundle(rootPane)
                                .getString("SystemMenu.showHeapStatus"));
                boolean isHeapStatusPanelShowing = Boolean.TRUE
                        .equals(this.rootPane
                                .getClientProperty(SubstanceLookAndFeel.HEAP_STATUS_PANEL));
                this.heapStatusMenuItem.setSelected(isHeapStatusPanelShowing);
                this.heapStatusMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SubstanceTitlePane.this.heapStatusPanel
                                .setVisible(!SubstanceTitlePane.this.heapStatusPanel
                                        .isVisible());
                        SubstanceTitlePane.this.rootPane
                                .putClientProperty(
                                        SubstanceLookAndFeel.HEAP_STATUS_PANEL,
                                        Boolean
                                                .valueOf(SubstanceTitlePane.this.heapStatusPanel
                                                        .isVisible()));
                    }
                });
                menu.add(this.heapStatusMenuItem);
            }
        }

        menu.addSeparator();

        menu.add(this.closeAction);
    }

    /**
     * Returns a <code>JButton</code> appropriate for placement on the
     * TitlePane.
     * 
     * @return Title button.
     */
    private JButton createTitleButton() {
        JButton button = new SubstanceTitleButton();

        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(true);

        return button;
    }

    /**
     * Creates the Buttons that will be placed on the TitlePane.
     */
    private void createButtons() {
        SubstanceTheme iconTheme = SubstanceLookAndFeel.getTheme()
                .getActiveTitlePaneTheme();

        this.closeButton = this.createTitleButton();
        this.closeButton.setAction(this.closeAction);
        this.closeButton.setText(null);
        this.closeButton.putClientProperty("paintActive", Boolean.TRUE);
        this.closeButton.setBorder(null);
        // this.closeButton.setToolTipText(SubstanceLookAndFeel
        // .getLabelBundle().getString(
        // "SystemMenu.close"));

        Icon closeIcon = new TransitionAwareIcon(closeButton,
                new TransitionAwareIcon.Delegate() {
                    public Icon getThemeIcon(SubstanceTheme theme) {
                        return SubstanceIconFactory.getTitlePaneIcon(
                                SubstanceIconFactory.IconKind.CLOSE, theme);
                    }
                });
        this.closeButton.setIcon(closeIcon);

        this.closeButton.setFocusable(false);
        this.closeButton.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY,
                Boolean.TRUE);

        this.closeButton.putClientProperty(
                SubstanceButtonUI.IS_TITLE_CLOSE_BUTTON, Boolean.TRUE);

        if (this.getWindowDecorationStyle() == JRootPane.FRAME) {
            // Icon maximizeIcon = SubstanceIconFactory.getTitlePaneIcon(
            // SubstanceIconFactory.IconKind.MAXIMIZE, iconTheme);

            // Icon restoreSizeIcon = SubstanceIconFactory.getTitlePaneIcon(
            // SubstanceIconFactory.IconKind.RESTORE, SubstanceLookAndFeel
            // .getColorScheme());

            this.minimizeButton = this.createTitleButton();
            this.minimizeButton.setAction(this.iconifyAction);
            this.minimizeButton.setText(null);
            this.minimizeButton.putClientProperty("paintActive", Boolean.TRUE);
            this.minimizeButton.setBorder(null);

            Icon minIcon = new TransitionAwareIcon(this.minimizeButton,
                    new TransitionAwareIcon.Delegate() {
                        public Icon getThemeIcon(SubstanceTheme theme) {
                            return SubstanceIconFactory.getTitlePaneIcon(
                                    SubstanceIconFactory.IconKind.MINIMIZE,
                                    theme);
                        }
                    });
            this.minimizeButton.setIcon(minIcon);

            this.minimizeButton.setFocusable(false);
            this.minimizeButton.putClientProperty(
                    SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
//            this.minimizeButton.setToolTipText(SubstanceCoreUtilities
//                    .getResourceBundle(rootPane)
//                    .getString("SystemMenu.iconify"));

            this.toggleButton = this.createTitleButton();
            this.toggleButton.setAction(this.restoreAction);
            this.toggleButton.putClientProperty("paintActive", Boolean.TRUE);
            this.toggleButton.setBorder(null);
            this.toggleButton.setText(null);

            Icon maxIcon = new TransitionAwareIcon(this.toggleButton,
                    new TransitionAwareIcon.Delegate() {
                        public Icon getThemeIcon(SubstanceTheme theme) {
                            return SubstanceIconFactory.getTitlePaneIcon(
                                    SubstanceIconFactory.IconKind.MAXIMIZE,
                                    theme);
                        }
                    });
            this.toggleButton.setIcon(maxIcon);

//            this.toggleButton.setToolTipText(SubstanceCoreUtilities
//                    .getResourceBundle(rootPane).getString(
//                            "SystemMenu.maximize"));
            this.toggleButton.setFocusable(false);
            this.toggleButton.putClientProperty(
                    SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);

        }
        syncCloseButtonTooltip();
    }

    /**
     * Returns the <code>LayoutManager</code> that should be installed on the
     * <code>SubstanceTitlePane</code>.
     * 
     * @return Layout manager.
     */
    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    /**
     * Updates state dependant upon the Window's active state.
     * 
     * @param isActive
     *            if <code>true</code>, the window is in active state.
     */
    private void setActive(boolean isActive) {
        Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

        if (this.getWindowDecorationStyle() == JRootPane.FRAME) {
            this.closeButton.putClientProperty("paintActive", activeB);
            this.minimizeButton.putClientProperty("paintActive", activeB);
            this.toggleButton.putClientProperty("paintActive", activeB);
        }
        this.getRootPane().repaint();
    }

    /**
     * Sets the state of the Window.
     * 
     * @param state
     *            Window state.
     */
    private void setState(int state) {
        this.setState(state, false);
    }

    /**
     * Sets the state of the window. If <code>updateRegardless</code> is true
     * and the state has not changed, this will update anyway.
     * 
     * @param state
     *            Window state.
     * @param updateRegardless
     *            if <code>true</code>, the update is done in any case.
     */
    private void setState(int state, boolean updateRegardless) {
        Window w = this.getWindow();

        if ((w != null) && (this.getWindowDecorationStyle() == JRootPane.FRAME)) {
            if ((this.state == state) && !updateRegardless) {
                return;
            }
            Frame frame = this.getFrame();

            if (frame != null) {
                JRootPane rootPane = this.getRootPane();

                if (((state & Frame.MAXIMIZED_BOTH) != 0)
                        && ((rootPane.getBorder() == null) || (rootPane
                                .getBorder() instanceof UIResource))
                        && frame.isShowing()) {
                    rootPane.setBorder(null);
                } else {
                    if ((state & Frame.MAXIMIZED_BOTH) == 0) {
                        // This is a croak, if state becomes bound, this can
                        // be nuked.
                        this.rootPaneUI.installBorder(rootPane);
                    }
                }
                if (frame.isResizable()) {
                    // special handling of mixed dark themes
                    SubstanceTheme iconTheme = SubstanceLookAndFeel.getTheme()
                            .getActiveTitlePaneTheme();
                    // if (iconTheme.getKind() == ThemeKind.DARK_MIXED)
                    // iconTheme = ((SubstanceMixDarkBiTheme) iconTheme)
                    // .getOriginalDarkTheme();

                    if ((state & Frame.MAXIMIZED_BOTH) != 0) {
                        Icon restoreIcon = new TransitionAwareIcon(
                                this.toggleButton,
                                new TransitionAwareIcon.Delegate() {
                                    public Icon getThemeIcon(
                                            SubstanceTheme theme) {
                                        return SubstanceIconFactory
                                                .getTitlePaneIcon(
                                                        SubstanceIconFactory.IconKind.RESTORE,
                                                        theme);
                                    }
                                });
                        this
                                .updateToggleButton(this.restoreAction,
                                        restoreIcon);
//                        this.toggleButton.setToolTipText(SubstanceCoreUtilities
//                                .getResourceBundle(rootPane).getString(
//                                        "SystemMenu.restore"));
                        this.maximizeAction.setEnabled(false);
                        this.restoreAction.setEnabled(true);
                    } else {
                        Icon maxIcon = new TransitionAwareIcon(
                                this.toggleButton,
                                new TransitionAwareIcon.Delegate() {
                                    public Icon getThemeIcon(
                                            SubstanceTheme theme) {
                                        return SubstanceIconFactory
                                                .getTitlePaneIcon(
                                                        SubstanceIconFactory.IconKind.MAXIMIZE,
                                                        theme);
                                    }
                                });
                        this.updateToggleButton(this.maximizeAction, maxIcon);
//                        this.toggleButton.setToolTipText(SubstanceCoreUtilities
//                                .getResourceBundle(rootPane).getString(
//                                        "SystemMenu.maximize"));
                        this.maximizeAction.setEnabled(true);
                        this.restoreAction.setEnabled(false);
                    }
                    if ((this.toggleButton.getParent() == null)
                            || (this.minimizeButton.getParent() == null)) {
                        this.add(this.toggleButton);
                        this.add(this.minimizeButton);
                        this.revalidate();
                        this.repaint();
                    }
                    this.toggleButton.setText(null);
                } else {
                    this.maximizeAction.setEnabled(false);
                    this.restoreAction.setEnabled(false);
                    if (this.toggleButton.getParent() != null) {
                        this.remove(this.toggleButton);
                        this.revalidate();
                        this.repaint();
                    }
                }
            } else {
                // Not contained in a Frame
                this.maximizeAction.setEnabled(false);
                this.restoreAction.setEnabled(false);
                this.iconifyAction.setEnabled(false);
                this.remove(this.toggleButton);
                this.remove(this.minimizeButton);
                this.revalidate();
                this.repaint();
            }
            this.closeAction.setEnabled(true);
            this.state = state;
        }
    }

    /**
     * Updates the toggle button to contain the Icon <code>icon</code>, and
     * Action <code>action</code>.
     * 
     * @param action
     *            Action.
     * @param icon
     *            Icon.
     */
    private void updateToggleButton(Action action, Icon icon) {
        this.toggleButton.setAction(action);
        this.toggleButton.setIcon(icon);
        this.toggleButton.setText(null);
    }

    /**
     * Returns the Frame rendering in. This will return null if the
     * <code>JRootPane</code> is not contained in a <code>Frame</code>.
     * 
     * @return Frame.
     */
    private Frame getFrame() {
        Window window = this.getWindow();

        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
    }

    /**
     * Returns the <code>Window</code> the <code>JRootPane</code> is
     * contained in. This will return null if there is no parent ancestor of the
     * <code>JRootPane</code>.
     * 
     * @return Window.
     */
    private Window getWindow() {
        return this.window;
    }

    /**
     * Returns the String to display as the title.
     * 
     * @return Display title.
     */
    private String getTitle() {
        Window w = this.getWindow();

        if (w instanceof Frame) {
            return ((Frame) w).getTitle();
        }
        if (w instanceof Dialog) {
            return ((Dialog) w).getTitle();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        // long start = System.nanoTime();
        // As state isn't bound, we need a convenience place to check
        // if it has changed. Changing the state typically changes the
        if (this.getFrame() != null) {
            this.setState(this.getFrame().getExtendedState());
        }
        JRootPane rootPane = this.getRootPane();
        Window window = this.getWindow();
        boolean leftToRight = (window == null) ? rootPane
                .getComponentOrientation().isLeftToRight() : window
                .getComponentOrientation().isLeftToRight();
        boolean isSelected = (window == null) ? true : window.isActive();
        int width = this.getWidth();
        int height = this.getHeight();

        // ColorScheme colorScheme = isSelected ? SubstanceCoreUtilities
        // .getActiveScheme(rootPane) : SubstanceCoreUtilities
        // .getDefaultScheme(rootPane);
        //
        // // special case for complex themes
        // if (isSelected
        // && (SubstanceLookAndFeel.getTheme() instanceof
        // SubstanceComplexTheme)) {
        // colorScheme = ((SubstanceComplexTheme) SubstanceLookAndFeel
        // .getTheme()).getActiveTitlePaneTheme().getColorScheme();
        // }
        SubstanceTheme theme = isSelected ? SubstanceLookAndFeel.getTheme()
                .getActiveTitlePaneTheme() : SubstanceLookAndFeel.getTheme()
                .getDefaultTitlePaneTheme();

        int xOffset = 0;
        String theTitle = this.getTitle();
        int leftEnd;
        int rightEnd;
        if (leftToRight) {
            // offset of border
            xOffset = 5;

            leftEnd = (this.menuBar == null) ? 0
                    : (this.menuBar.getWidth() + 10);
            xOffset += leftEnd;

            // find the leftmost button for the right end
            AbstractButton leftmostButton = null;

            if ((this.minimizeButton != null)
                    && (this.minimizeButton.getParent() != null)
                    && (this.minimizeButton.getBounds().width != 0)) {
                leftmostButton = this.minimizeButton;
            } else {
                if ((this.toggleButton != null)
                        && (this.toggleButton.getParent() != null)
                        && (this.toggleButton.getBounds().width != 0)) {
                    leftmostButton = this.toggleButton;
                } else {
                    if ((this.closeButton != null)
                            && (this.closeButton.getParent() != null)) {
                        leftmostButton = this.closeButton;
                    }
                }
            }

            rightEnd = this.getWidth();
            if (leftmostButton != null) {
                Rectangle rect = leftmostButton.getBounds();
                rightEnd = rect.getBounds().x - 5;
                if ((this.heapStatusPanel != null)
                        && (this.heapStatusPanel.isVisible()))
                    rightEnd = this.heapStatusPanel.getBounds().x - 5;
                rightEnd--;
            }

            if (theTitle != null) {
                FontMetrics fm = rootPane.getFontMetrics(g.getFont());
                int titleWidth = rightEnd - leftEnd - 20;
                String clippedTitle = SubstanceCoreUtilities.clipString(fm,
                        titleWidth, theTitle);
                // show tooltip with full title only if necessary
                if (theTitle.equals(clippedTitle))
                    this.setToolTipText(null);
                else
                    this.setToolTipText(theTitle);
                theTitle = clippedTitle;
            }
        } else {
            // RTL support

            xOffset = width - 5;

            rightEnd = (this.menuBar == null) ? width - 5 : width - 5
                    - this.menuBar.getWidth() - 10;

            // find the rightmost button for the left transition band
            AbstractButton rightmostButton = null;

            if ((this.minimizeButton != null)
                    && (this.minimizeButton.getParent() != null)
                    && (this.minimizeButton.getBounds().width != 0)) {
                rightmostButton = this.minimizeButton;
            } else {
                if ((this.toggleButton != null)
                        && (this.toggleButton.getParent() != null)
                        && (this.toggleButton.getBounds().width != 0)) {
                    rightmostButton = this.toggleButton;
                } else {
                    if ((this.closeButton != null)
                            && (this.closeButton.getParent() != null)) {
                        rightmostButton = this.closeButton;
                    }
                }
            }

            leftEnd = 5;
            if (rightmostButton != null) {
                Rectangle rect = rightmostButton.getBounds();
                leftEnd = (int) rect.getBounds().getMaxX() + 5;
                if ((this.heapStatusPanel != null)
                        && (this.heapStatusPanel.isVisible()))
                    leftEnd = (int) this.heapStatusPanel.getBounds().getMaxX() + 5;
                leftEnd++;
            }

            if (theTitle != null) {
                FontMetrics fm = rootPane.getFontMetrics(g.getFont());
                int titleWidth = rightEnd - leftEnd - 20;
                String clippedTitle = SubstanceCoreUtilities.clipString(fm,
                        titleWidth, theTitle);
                // show tooltip with full title only if necessary
                if (theTitle.equals(clippedTitle)) {
                    this.setToolTipText(null);
                } else {
                    this.setToolTipText(theTitle);
                }
                theTitle = clippedTitle;
                xOffset = rightEnd - fm.stringWidth(theTitle);
            }
        }

        SubstanceTitlePainter titlePainter = SubstanceCoreUtilities
                .getTitlePainter(rootPane);

        Graphics2D graphics = (Graphics2D) g.create();
        Font font = SubstanceLookAndFeel.getFontPolicy().getFontSet(
                "Substance", null).getWindowTitleFont();
        graphics.setFont(font);
        titlePainter.paintTitleBackground(graphics, rootPane, width + 1,
                height, leftEnd, rightEnd, theme, 0.0f);
        //
        // graphics, 0, 0, width,
        // height + 1, colorScheme, false, false);

        if (SubstanceCoreUtilities.toDrawWatermark(this)) {
            // paint the watermark over the title pane
            SubstanceLookAndFeel.getCurrentWatermark().drawWatermarkImage(
                    graphics, this, 0, 0, width, height);

            // paint the background second time with 50% translucency, making
            // the watermark 'bleed' through.
            Composite oldComp = graphics.getComposite();
            graphics.setComposite(TransitionLayout.getAlphaComposite(
                    this.rootPane, 0.5f));
            titlePainter.paintTitleBackground(graphics, rootPane, width + 1,
                    height, leftEnd, rightEnd, theme, 0.0f);
            // SubstanceImageCreator.paintRectangularBackground(graphics, 0, 0,
            // width,
            // height + 1, colorScheme, false, false);
            graphics.setComposite(oldComp);
        }

        // draw the title (if needed)
        if (theTitle != null) {
            FontMetrics fm = rootPane.getFontMetrics(graphics.getFont());
            int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();

            SubstanceCoreUtilities.paintTextWithDropShadow(this, graphics,
                    theme.getForegroundColor(), theTitle, width, height,
                    xOffset, yOffset);

            // Object oldAAValue = graphics
            // .getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            // graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            // RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            //
            // Color foregroundColor =
            // theme.getForegroundColor();//getTitlePaneForegroundColor(theme);
            //
            // // blur the text shadow
            // BufferedImage blurred =
            // SubstanceCoreUtilities.getBlankImage(width,
            // height);
            // Graphics2D gBlurred = (Graphics2D) blurred.getGraphics();
            // gBlurred.setFont(graphics.getFont());
            // gBlurred.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            // RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            // gBlurred.setColor(SubstanceColorUtilities
            // .getNegativeColor(foregroundColor));
            // ConvolveOp convolve = new ConvolveOp(new Kernel(3, 3, new float[]
            // {
            // .0f, .02f, .05f, .02f, .0f, .05f, .05f, .05f, .05f }),
            // ConvolveOp.EDGE_NO_OP, null);
            // gBlurred.drawString(theTitle, xOffset + 1, yOffset + 1);
            // blurred = convolve.filter(blurred, null);
            //
            // graphics.drawImage(blurred, 0, 0, null);
            //
            // graphics.setColor(foregroundColor);
            // RenderingUtils.installDesktopHints(graphics);
            // graphics.drawString(theTitle, xOffset, yOffset);
            // graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            // oldAAValue);
        }

        GhostPaintingUtils.paintGhostImages(this, graphics);

        // long end = System.nanoTime();
        // System.out.println(end - start);
        graphics.dispose();
    }

    // public Color getTitlePaneForegroundColor(SubstanceTheme theme) {
    // return SubstanceCoreUtilities.isThemeDark(theme) ? theme
    // .getColorScheme().getForegroundColor()
    // : new Color(
    // SubstanceColorUtilities
    // .getInterpolatedRGB(theme.getColorScheme()
    // .getUltraDarkColor(), theme
    // .getColorScheme()
    // .getForegroundColor(), 0.5));
    // }

    /**
     * Actions used to <code>close</code> the <code>Window</code>.
     */
    private class CloseAction extends AbstractAction {
        /**
         * Creates a new close action.
         */
        public CloseAction() {
            super(SubstanceCoreUtilities.getResourceBundle(rootPane).getString(
                    "SystemMenu.close"), SubstanceImageCreator
                    .getCloseIcon(SubstanceLookAndFeel.getTheme()));
        }

        public void actionPerformed(ActionEvent e) {
            Window window = SubstanceTitlePane.this.getWindow();

            if (window != null) {
                window.dispatchEvent(new WindowEvent(window,
                        WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    /**
     * Actions used to <code>iconfiy</code> the <code>Frame</code>.
     */
    private class IconifyAction extends AbstractAction {
        /**
         * Creates a new iconify action.
         */
        public IconifyAction() {
            super(SubstanceCoreUtilities.getResourceBundle(rootPane).getString(
                    "SystemMenu.iconify"), SubstanceImageCreator
                    .getMinimizeIcon(SubstanceLookAndFeel.getTheme()));
        }

        public void actionPerformed(ActionEvent e) {
            Frame frame = SubstanceTitlePane.this.getFrame();
            if (frame != null) {
                frame.setExtendedState(SubstanceTitlePane.this.state
                        | Frame.ICONIFIED);
            }
        }
    }

    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class RestoreAction extends AbstractAction {
        /**
         * Creates a new restore action.
         */
        public RestoreAction() {
            super(SubstanceCoreUtilities.getResourceBundle(rootPane).getString(
                    "SystemMenu.restore"), SubstanceImageCreator
                    .getRestoreIcon(SubstanceLookAndFeel.getTheme()));
        }

        public void actionPerformed(ActionEvent e) {
            Frame frame = SubstanceTitlePane.this.getFrame();

            if (frame == null) {
                return;
            }

            if ((SubstanceTitlePane.this.state & Frame.ICONIFIED) != 0) {
                frame.setExtendedState(SubstanceTitlePane.this.state
                        & ~Frame.ICONIFIED);
            } else {
                frame.setExtendedState(SubstanceTitlePane.this.state
                        & ~Frame.MAXIMIZED_BOTH);
            }
        }
    }

    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class MaximizeAction extends AbstractAction {
        /**
         * Creates a new maximize action.
         */
        public MaximizeAction() {
            super(SubstanceCoreUtilities.getResourceBundle(rootPane).getString(
                    "SystemMenu.maximize"), SubstanceImageCreator
                    .getMaximizeIcon(SubstanceLookAndFeel.getTheme()));
        }

        public void actionPerformed(ActionEvent e) {
            Frame frame = SubstanceTitlePane.this.getFrame();
            if (frame != null) {
                frame.setExtendedState(SubstanceTitlePane.this.state
                        | Frame.MAXIMIZED_BOTH);
            }
        }
    }

    /**
     * Class responsible for drawing the system menu. Looks up the image to draw
     * from the Frame associated with the <code>JRootPane</code>.
     */
    public class SubstanceMenuBar extends JMenuBar {
        @Override
        public void paint(Graphics g) {
            Frame frame = SubstanceTitlePane.this.getFrame();

            Image image = (frame != null) ? frame.getIconImage() : null;

            if (image != null) {
                int iSize = SubstanceSizeUtils.getTitlePaneIconSize();
                double coef = Math.max((double) iSize
                        / (double) image.getWidth(null), (double) iSize
                        / (double) image.getHeight(null));
                if (coef < 1.0) {
                    // fix for defect 255 - large icons need to be properly
                    // scaled down. While we can use
                    // RenderingHints.VALUE_INTERPOLATION_BILINEAR, it will not
                    // work good on large icons (such as 128*128), resulting in
                    // bad images. Here, we use multi-step scaling by Romain
                    // Guy.
                    BufferedImage bi = SubstanceCoreUtilities.getBlankImage(
                            image.getWidth(null), image.getHeight(null));
                    bi.getGraphics().drawImage(image, 0, 0, null);
                    g.drawImage(LafWidgetUtilities.createThumbnail(bi, iSize),
                            0, 0, null);
                } else
                    g.drawImage(image, 0, 0, null);
            } else {
                Icon icon = UIManager.getIcon("InternalFrame.icon");

                if (icon != null) {
                    icon.paintIcon(this, g, 0, 0);
                }
            }
        }

        @Override
        public Dimension getMinimumSize() {
            return this.getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();

            int iSize = SubstanceSizeUtils.getTitlePaneIconSize();
            return new Dimension(Math.max(iSize, size.width), Math.max(
                    size.height, iSize));
        }
    }

    /**
     * Layout manager for the title pane.
     * 
     * @author Kirill Graphics
     */
    private class TitlePaneLayout implements LayoutManager {
        /*
         * (non-Javadoc)
         * 
         * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
         *      java.awt.Component)
         */
        public void addLayoutComponent(String name, Component c) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
         */
        public void removeLayoutComponent(Component c) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
         */
        public Dimension preferredLayoutSize(Container c) {
            int height = this.computeHeight();
            return new Dimension(height, height);
        }

        /**
         * Computes title pane height.
         * 
         * @return Title pane height.
         */
        private int computeHeight() {
            FontMetrics fm = SubstanceTitlePane.this.rootPane
                    .getFontMetrics(SubstanceTitlePane.this.getFont());
            int fontHeight = fm.getHeight();
            fontHeight += 7;
            int iconHeight = 0;
            if (SubstanceTitlePane.this.getWindowDecorationStyle() == JRootPane.FRAME) {
                iconHeight = SubstanceSizeUtils.getTitlePaneIconSize();
            }

            int finalHeight = Math.max(fontHeight, iconHeight);
            return finalHeight;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
         */
        public Dimension minimumLayoutSize(Container c) {
            return this.preferredLayoutSize(c);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
         */
        public void layoutContainer(Container c) {
            boolean leftToRight = (SubstanceTitlePane.this.window == null) ? SubstanceTitlePane.this
                    .getRootPane().getComponentOrientation().isLeftToRight()
                    : SubstanceTitlePane.this.window.getComponentOrientation()
                            .isLeftToRight();

            int w = SubstanceTitlePane.this.getWidth();
            int x;
            int y = 3;
            int spacing;
            int buttonHeight;
            int buttonWidth;

            if ((SubstanceTitlePane.this.closeButton != null)
                    && (SubstanceTitlePane.this.closeButton.getIcon() != null)) {
                buttonHeight = SubstanceTitlePane.this.closeButton.getIcon()
                        .getIconHeight();
                buttonWidth = SubstanceTitlePane.this.closeButton.getIcon()
                        .getIconWidth();
            } else {
                buttonHeight = SubstanceSizeUtils.getTitlePaneIconSize();
                buttonWidth = SubstanceSizeUtils.getTitlePaneIconSize();
            }

            y = (getHeight() - buttonHeight) / 2;

            // assumes all buttons have the same dimensions
            // these dimensions include the borders

            x = leftToRight ? w : 0;

            spacing = 5;
            x = leftToRight ? spacing : w - buttonWidth - spacing;
            if (SubstanceTitlePane.this.menuBar != null) {
                SubstanceTitlePane.this.menuBar.setBounds(x, y, buttonWidth,
                        buttonHeight);
            }

            x = leftToRight ? w : 0;
            spacing = 4;
            x += leftToRight ? -spacing - buttonWidth : spacing;
            if (SubstanceTitlePane.this.closeButton != null) {
                SubstanceTitlePane.this.closeButton.setBounds(x, y,
                        buttonWidth, buttonHeight);
            }

            if (!leftToRight)
                x += buttonWidth;

            if (SubstanceTitlePane.this.getWindowDecorationStyle() == JRootPane.FRAME) {
                if (Toolkit.getDefaultToolkit().isFrameStateSupported(
                        Frame.MAXIMIZED_BOTH)) {
                    if (SubstanceTitlePane.this.toggleButton.getParent() != null) {
                        spacing = 10;
                        x += leftToRight ? -spacing - buttonWidth : spacing;
                        SubstanceTitlePane.this.toggleButton.setBounds(x, y,
                                buttonWidth, buttonHeight);
                        if (!leftToRight) {
                            x += buttonWidth;
                        }
                    }
                }

                if ((SubstanceTitlePane.this.minimizeButton != null)
                        && (SubstanceTitlePane.this.minimizeButton.getParent() != null)) {
                    spacing = 2;
                    x += leftToRight ? -spacing - buttonWidth : spacing;
                    SubstanceTitlePane.this.minimizeButton.setBounds(x, y,
                            buttonWidth, buttonHeight);
                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }

                if ((SubstanceTitlePane.this.heapStatusPanel != null)
                        && SubstanceTitlePane.this.heapStatusPanel.isVisible()) {
                    spacing = 5;
                    x += leftToRight ? (-spacing - SubstanceTitlePane.this.heapStatusPanel
                            .getPreferredSize().width)
                            : spacing;
                    SubstanceTitlePane.this.heapStatusPanel.setBounds(x, 1,
                            SubstanceTitlePane.this.heapStatusPanel
                                    .getPreferredSize().width,
                            SubstanceTitlePane.this.getHeight() - 3);
                }
            }
            // buttonsWidth = leftToRight ? w - x : x;
        }

    }

    /**
     * PropertyChangeListener installed on the Window. Updates the necessary
     * state as the state of the Window changes.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();

            // Frame.state isn't currently bound.
            if ("resizable".equals(name) || "state".equals(name)) {
                Frame frame = SubstanceTitlePane.this.getFrame();

                if (frame != null) {
                    SubstanceTitlePane.this.setState(frame.getExtendedState(),
                            true);
                }
                if ("resizable".equals(name)) {
                    SubstanceTitlePane.this.getRootPane().repaint();
                }
            } else {
                if ("title".equals(name)) {
                    SubstanceTitlePane.this.repaint();
                    SubstanceTitlePane.this.setToolTipText((String) pce
                            .getNewValue());
                } else {
                    if ("componentOrientation".equals(name)
                            || "iconImage".equals(name)) {
                        SubstanceTitlePane.this.revalidate();
                        SubstanceTitlePane.this.repaint();
                    }
                }
            }
        }
    }

    /**
     * WindowListener installed on the Window, updates the state as necessary.
     */
    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent ev) {
            SubstanceTitlePane.this.setActive(true);
        }

        @Override
        public void windowDeactivated(WindowEvent ev) {
            SubstanceTitlePane.this.setActive(false);
        }
    }

    /**
     * Sets indication whether frame title panes can show the heap status panel.
     * 
     * @param canHaveHeapStatusPanel
     *            if <code>true</code>, title panes can show the heap status
     *            panel.
     */
    public static void setCanHaveHeapStatusPanel(boolean canHaveHeapStatusPanel) {
        SubstanceTitlePane.canHaveHeapStatusPanel = canHaveHeapStatusPanel;
    }

    /**
     * Returns indication whether frame title panes can show the heap status
     * panel.
     * 
     * @return <code>true</code> if the frame title panes can show the heap
     *         status panel, <code>false</code> otherwise.
     */
    public static boolean getCanHaveHeapStatusPanel() {
        return SubstanceTitlePane.canHaveHeapStatusPanel;
    }

    /**
     * Sets location for heap status logfile. Relevant if
     * {@link #setCanHaveHeapStatusPanel(boolean)} was called with
     * <code>true</code>.
     * 
     * @param heapStatusLogfileName
     *            Logfile for the heap status panel.
     */
    public static void setHeapStatusLogfileName(String heapStatusLogfileName) {
        SubstanceTitlePane.heapStatusLogfileName = heapStatusLogfileName;
    }

    /**
     * Makes the heap status panel appear / disappear permanently on the
     * associated title pane and removes the corresponding check box menu items
     * from the system menu.
     * 
     * @param isVisible
     *            if <code>true</code>, the heap status panel will be
     *            permanently shown, if <code>false</code>, the heap status
     *            panel will be permanently hidden.
     */
    public void setHeapStatusPanePermanentVisibility(boolean isVisible) {
        if (!(this.rootPane
                .getClientProperty(SubstanceTitlePane.HEAP_STATUS_PANEL_PERMANENT) instanceof Boolean)) {
            this.menuBar.getMenu(0).remove(this.heapStatusMenuItem);
            this.heapStatusMenuItem = null;
        }
        this.heapStatusPanel.setVisible(isVisible);
        this.rootPane.putClientProperty(
                SubstanceTitlePane.HEAP_STATUS_PANEL_PERMANENT, Boolean
                        .valueOf(isVisible));
        this.rootPane.putClientProperty(SubstanceLookAndFeel.HEAP_STATUS_PANEL,
                Boolean.valueOf(isVisible));
        if (!isVisible) {
            HeapStatusThread.unregisterPanel(this.heapStatusPanel);
        }
        this.repaint();
    }

    protected static class ThemeChanger implements ActionListener {
        protected SubstanceTheme newTheme;

        public ThemeChanger(SubstanceTheme newTheme) {
            super();
            this.newTheme = newTheme;
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SubstanceLookAndFeel.setCurrentTheme(newTheme);
                    // update all existing root panes
                    for (Frame frame : Frame.getFrames()) {
                        SwingUtilities.updateComponentTreeUI(frame);
                    }
                }
            });
        }
    }

    protected static class AnimationChanger implements ActionListener {
        protected AnimationKind newAnimationKind;

        public AnimationChanger(AnimationKind newAnimationKind) {
            super();
            this.newAnimationKind = newAnimationKind;
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UIManager.put(LafWidget.ANIMATION_KIND, newAnimationKind);
                }
            });
        }
    }

    protected static class FocusKindChanger implements ActionListener {
        protected FocusKind newFocusKind;

        public FocusKindChanger(FocusKind newFocusKind) {
            super();
            this.newFocusKind = newFocusKind;
        }

        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UIManager
                            .put(SubstanceLookAndFeel.FOCUS_KIND, newFocusKind);
                }
            });
        }
    }

    /**
     * Synchronizes the tooltip of the close button.
     */
    protected void syncCloseButtonTooltip() {
        if (SubstanceCoreUtilities.isRootPaneModified(this.getRootPane())) {
//            this.closeButton.setToolTipText(SubstanceCoreUtilities
//                    .getResourceBundle(rootPane).getString("SystemMenu.close")
//                    + " ["
//                    + SubstanceCoreUtilities.getResourceBundle(rootPane)
//                            .getString("Tooltip.contentsNotSaved") + "]");
        } else {
//            this.closeButton.setToolTipText(SubstanceCoreUtilities
//                    .getResourceBundle(rootPane).getString("SystemMenu.close"));
        }
        this.closeButton.repaint();
    }

    public static void dump(Component comp, int level) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < level; i++)
            sb.append("  ");
        sb.append(comp.toString());
        System.out.println(sb);
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                dump(cont.getComponent(i), level + 1);
            }
        }
    }
}

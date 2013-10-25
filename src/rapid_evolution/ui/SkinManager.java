package rapid_evolution.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;
import rapid_evolution.StringUtil;
import rapid_evolution.audio.AudioPlayer;
import rapid_evolution.filefilters.SkinFileFilter;
import rapid_evolution.piano.MIDIPiano;
import rapid_evolution.ui.main.EditMixoutUI;
import rapid_evolution.ui.main.MixListMouse;
import rapid_evolution.ui.main.MixoutPane;
import rapid_evolution.ui.main.NormalizationProgressUI;
import rapid_evolution.ui.main.PitchTimeShiftUI;
import rapid_evolution.ui.main.SearchListMouse;
import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.ui.main.StylesPane;
import rapid_evolution.ui.main.TimePitchShiftProgressUI;
import rapid_evolution.ui.skin.AudioUpdateField;
import rapid_evolution.ui.styles.AddStyleExcludeKeywordUI;
import rapid_evolution.ui.styles.AddStyleKeywordUI;
import rapid_evolution.ui.styles.AddStyleSongsUI;
import rapid_evolution.ui.styles.AddStyleUI;
import rapid_evolution.ui.styles.AddToStylesUI;
import rapid_evolution.ui.styles.EditStyleUI;
import rapid_evolution.ui.styles.ExcludeStyleSongsUI;
import rapid_evolution.ui.styles.ListStyleSongMouse;
import rapid_evolution.ui.styles.ListStyleSongsUI;
import rapid_evolution.ui.styles.RemoveFromStyles;
import rapid_evolution.ui.styles.SetStylesUI;
import rapid_evolution.ui.styles.StyleExcludeMouse;
import rapid_evolution.ui.styles.StyleIncludeMouse;
import rapid_evolution.ui.styles.StylesUI;
import rapid_evolution.util.OSHelper;

import com.brunchboy.util.swing.relativelayout.AttributeConstraint;
import com.brunchboy.util.swing.relativelayout.AttributeType;
import com.brunchboy.util.swing.relativelayout.DependencyManager;
import com.brunchboy.util.swing.relativelayout.RelativeLayout;
import com.ibm.iwt.IDialog;
import com.ibm.iwt.IFrame;
import com.ibm.iwt.IOptionPane;
import com.ibm.iwt.MFrame;
import com.mixshare.rapid_evolution.ui.swing.button.REButton;
import com.mixshare.rapid_evolution.ui.swing.label.RELabel;
import com.mixshare.rapid_evolution.ui.swing.lookfeel.LookAndFeelManager;
import com.mixshare.rapid_evolution.ui.swing.panel.MoreInfoPanel;
import com.mixshare.rapid_evolution.ui.swing.panel.RESplitPanel;
import com.mixshare.rapid_evolution.ui.swing.slider.MediaSlider;
import com.mixshare.rapid_evolution.ui.swing.slider.SliderFixer;
import com.mixshare.rapid_evolution.ui.swing.spinner.RESpinner;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextArea;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;
import com.mixshare.rapid_evolution.ui.swing.viewport.ScrollPaneWatermark;
import com.mixshare.rapid_evolution.util.io.TextFileWriter;
import com.toedter.calendar.JDateChooser;

public class SkinManager {

    static private boolean createTextBundle = false;
    static private boolean removeTextValues = false;
    
    static private ResourceBundle bundle = PropertyResourceBundle.getBundle("translations");
    
    private static Logger log = Logger.getLogger(SkinManager.class);
    
  static public String current_skin_filename = "./skins/mixshare/mixshare-substance-skin.xml";

  static public String lastSkinName = null;
  
  public boolean setdefaults = true;

  HashMap templates = new HashMap();

  HashMap tableconfigmap = new HashMap();
  public TableConfig getTableConfig(JTable table) {
    TableConfig returnval = (TableConfig)tableconfigmap.get(table);
    if (returnval == null) {
      returnval = new TableConfig();
      tableconfigmap.put(table, returnval);
    }
    return returnval;
  }

  Insets default_field_margin = null;

  public boolean use_custom_tooltips = true;
  
  public HashMap sizemap = new HashMap();
  private HashMap errormap = new HashMap();
  public HashMap iconmap = new HashMap();
  public HashMap bordermap = new HashMap();
  public HashMap colormap = new HashMap();
  public HashMap marginmap = new HashMap();
  public HashMap moreinfomap = new HashMap();
  public HashMap fontmap = new HashMap();
  private HashMap messagemap = new HashMap();
  private Dimension default_dialog_size = new Dimension(100,100);
  private Dimension default_frame_size = new Dimension(100,100);
  private HashMap altmap = new HashMap();
  private HashMap textmap = new HashMap();
  private HashMap buttonmap = new HashMap();
  private HashMap parentmap = new HashMap();
  static HashMap scrollhash = new HashMap();
  private String skinname;
  private String author;
  private String look_feel;
  private String locale;
  private String description;
  NodeList root_nodelist = null;
  private HashMap imap = null;
  public static SkinManager instance;
  Vector conditions = new Vector();
  Vector recursiveconditions = new Vector();
  private HashMap conditionActions = new HashMap();
  private HashMap conditionIndex = new HashMap();
  private HashMap recursiveConditionIndex = new HashMap();
  HashMap splitpanel_hash = new HashMap();
  HashMap checkbox_hash = new HashMap();
  HashMap dialog_hash = new HashMap();
  HashMap radiobutton_hash = new HashMap();
  HashMap frame_hash = new HashMap();
  HashMap combobox_hash = new HashMap();


  public class MyRelativeLayout extends RelativeLayout {
    boolean errored = false;
    public void layoutContainer(Container container) {
      try {
        super.layoutContainer(container);
      } catch (IllegalStateException e) {
        if (errored) return;
        errored = true;
        IOptionPane.showMessageDialog(getFrame("main_frame"),
           e.getMessage(),
          "skin error",
          IOptionPane.ERROR_MESSAGE);
        log.error("layoutContainer(): error", e);
        throw new IllegalStateException(e.getMessage());
      }
    }

    public Dimension preferredLayoutSize(Container c) {
      try {
        return super.preferredLayoutSize(c);
      } catch (IllegalStateException e) {
        if (errored) return null;
        errored = true;
        IOptionPane.showMessageDialog(getFrame("main_frame"),
           e.getMessage(),
          "skin error",
          IOptionPane.ERROR_MESSAGE);
        log.error("preferredLayoutSize(): error", e);
        throw new IllegalStateException(e.getMessage());
      }
    }
  }

  private int randomid = 1;
  private String getFullId(String namespace, String id) {
    if (id == null) return (namespace + String.valueOf(randomid++));
    if (id.equalsIgnoreCase("root")) return id;
    if ((namespace == null) || (namespace.equals(""))) return id;
    if (id.equals("")) return namespace;
    return namespace + "_" + id;
  }

  class Error {
    public Error(String _id, String _title, String _text, String _option1, String _option2, String _option3) {
      id = _id;
      title = _title;
      text = _text;
      option_1 = _option1;
      option_2 = _option2;
      option_3 = _option3;
    }
    private String id;
    private String title;
    private String text;
    private String option_1;
    private String option_2;
    private String option_3;
    public String getID() { return id; }
    public String getTitle() { if (title == null) return ""; return title; }
    public String getText() {  if (text == null) return "";  return text; }
    public String getOption1() {  if (option_1 == null) return ""; return option_1; }
    public String getOption2() {  if (option_2 == null) return ""; return option_2; }
    public String getOption3() {  if (option_3 == null) return ""; return option_3; }
  }

  public String getDialogMessageTitle(String id) {
    return ((Error)errormap.get(id)).getTitle();
  }
  public String getDialogMessageText(String id) {
    return ((Error)errormap.get(id)).getText();
  }
  public String getDialogOption(String id, int num) {
    Error e = (Error) errormap.get(id);
    if (num == 1) return e.getOption1();
    if (num == 2) return e.getOption2();
    if (num == 3) return e.getOption3();
    return null;
  }

  private HashMap colorTypeIdMap = new HashMap();  
  public void populateColorTypeIdMap() {
      colorTypeIdMap.clear();
      UIDefaults defaults = UIManager.getLookAndFeel().getDefaults();
      if (log.isTraceEnabled())
          log.trace("populateColorTypeIdMap(): ui defaults=" + defaults);
      colorTypeIdMap.put("%DEFAULT_TABLE_SELECTION_BACKGROUND%", defaults.get("Table.selectionBackground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Table.background"));
      colorTypeIdMap.put("%DEFAULT_BUTTON_FOREGROUND%", defaults.get("Button.foreground"));
      colorTypeIdMap.put("%DEFAULT_LABEL_FOREGROUND%", defaults.get("Label.foreground"));

      /*
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Field.disabled"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("ScrollPane.foreground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Table.foreground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Dialog.background"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Frame.foreground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Table.background"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("ScrollPane.background"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Frame.background"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Spinner.foreground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Table.selectionForeground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Dialog.foreground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Label.background"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Table.selectionBackground"));
      colorTypeIdMap.put("%DEFAULT_TABLE_BACKGROUND%", defaults.get("Table.selectionBackground"));
       */

  
  }
  
  public Color getRandomColor() {
      return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
  }
  
  public Color getColor(String id) {
      Color color = null;
      Object obj = colormap.get(id);
      if (obj instanceof Color)
          color = (Color)obj;
      else if (obj instanceof String) {
          String colorId = (String)obj;
          color = (Color)colorTypeIdMap.get(colorId);
      } else {
          color = (Color)colorTypeIdMap.get(id);
      }
      if (color == null)
          return Color.white;
      return color;
  }

  public Font getFont(String id) {
    Font font = (Font)fontmap.get(id);
    return font;
  }

  public void setColor(String id, Color color) {
	  //if (log.isDebugEnabled())
		  log.info("setColor(): id=" + id + ", color=" + color);
	  colormap.put(id, color);
  }

  public void setFont(String id, Font font) {
    fontmap.put(id, font);
  }

  public void setMessageText(String id, String value) {
      messagemap.put(id, value);
  }
  
  public String getMessageText(String id) {
    String returnval = (String)messagemap.get(id.toLowerCase());
    if ((returnval == null) || returnval.equals("")) {
      if (id.equalsIgnoreCase("file_filter_skin")) return "RE2 Skin Files";
      if (id.equalsIgnoreCase("default_custom_field_1_1abel")) return "field 1";
      if (id.equalsIgnoreCase("default_custom_field_2_1abel")) return "field 2";
      if (id.equalsIgnoreCase("default_custom_field_3_1abel")) return "field 3";
      if (id.equalsIgnoreCase("default_custom_field_4_1abel")) return "field 4";
      return "";
    }
    else return returnval;
  }

  // BUG: random dot related to tool tips
  // TO DO: border on popup dialogs
  // BUG; mouse over border of non resizable window still shows cursor...
  // TO DO: must seperate MVC fully before releasing
  // TO DO: should reference rcombobox, rlist, rtable etc in this
  // TO DO: standardize resolution of colors/fonts/etc (i.e. genrealization)
  // BUG: changing skins breaks color chooser
  // FEATURE: allow duplicate appearences of same items?
  // POSSIBLE FEATURE: support for objects to load in specific locations?
  // DEFERRED FIX: toolbar alignment recursive

  // type = ".background", ".selectionForeground", etc
  public Color getAspectColor(Component component, String aspect, String explicit) {
    if (explicit != null) return StringUtil.parseColor(explicit);
    String type = determineId(component, aspect);
    String colortype = (String)uiTypeMap.get(type);
    if (colormap.get(colortype) != null) return (Color)colormap.get(colortype);
    String defaulttype = null;
    if (colortype != null)  {
      defaulttype = "default" + colortype.substring(colortype.indexOf("_"), colortype.length());
      if (colormap.get(defaulttype) != null) return (Color)colormap.get(defaulttype);
    }
    if (!aspect.equalsIgnoreCase(".selectionBackground")) {
      if (aspect.endsWith("Background")) defaulttype = "default_background";
      if (aspect.endsWith("Foreground")) defaulttype = "default_foreground";
      if (colormap.get(defaulttype) != null) return (Color)colormap.get(defaulttype);
    }
    return UIManager.getColor(type);
  }

  private Vector textcomponent_map = new Vector();
  private void readSharedTextComponentSettings(String id, JTextComponent component, NamedNodeMap attribute_map) {

    textcomponent_map.add(component);

    Color caretcolor = getAspectColor(component, ".caretForeground", getAttribute(attribute_map, "caret_color"));
    if (caretcolor == null) caretcolor = component.getForeground();
    if (caretcolor != null) {
      Backup(old_caret_color, component, component.getCaretColor());
      component.setCaretColor(caretcolor);
    }

    try {
        String default_value = getAttribute(attribute_map, "default_value");
        if (default_value == null) default_value = getAttribute(attribute_map, "value");
        if (default_value == null) default_value = getAttribute(attribute_map, "text");
        if (setdefaults && (default_value != null)) component.setText(default_value);
    } catch (Exception e) {
        log.trace("readSharedTextComponentSettings(): error", e);
    }

    try {
        String editable = getAttribute(attribute_map, "editable");
        if ((editable != null) && (StringUtil.isFalse(editable))) component.setEditable(false);
        else component.setEditable(true);
    } catch (Exception e) {
        log.trace("SkinManager(): error Exception", e);
    }

    Color selected_foreground = getAspectColor(component, ".selectionForeground", getAttribute(attribute_map, "selection_foreground"));
    Color selected_background = getAspectColor(component, ".selectionBackground", getAttribute(attribute_map, "selection_background"));
    if ((selected_foreground == null)) selected_foreground = component.getForeground();
    if (selected_foreground != null) {
//      log.trace("readSharedTextComponentSettings(): text selected foreground: " + selected_foreground.toString());
      Backup(old_textcomponent_selection_foreground, component, component.getSelectedTextColor());
      component.setSelectedTextColor(selected_foreground);
    }
    if ((selected_background == null)) selected_background = component.getBackground();
    if (selected_background != null) {
//      log.trace("readSharedTextComponentSettings(): text selected background: " + selected_background.toString());
      Backup(old_textcomponent_selection_background, component, component.getSelectionColor());
      component.setSelectionColor(selected_background);
    }

    String margin = getAttribute(attribute_map, "margin");
    if (margin != null) component.setMargin(StringUtil.parseInsets(margin));
    else component.setMargin((default_field_margin == null) ? RapidEvolutionUI.stdinsetsize : default_field_margin);

    String disabled_color = getAttribute(attribute_map, "disabled_color");
    Color disabledcolor = getAspectColor(component, ".disabled", disabled_color);
    if (disabledcolor == null) disabledcolor = getAspectColor(component, ".disabledText", disabled_color);
    if (disabledcolor == null) disabledcolor = getAspectColor(component, ".disabledForeground", disabled_color);
    if (disabledcolor != null) {
      Backup(old_textcomponent_disabled, component, component.getDisabledTextColor());
      component.setDisabledTextColor(disabledcolor);
    }
  }

  public ImageIcon getIcon(String id) {
      return getImageIcon(skinpath + (String)iconmap.get(id));
  }
  
  private void readSharedAbstractButtonSettings(AbstractButton button, NamedNodeMap attribute_map) {

    String setBorderPainted = getAttribute(attribute_map, "border_painted");
    if ((setBorderPainted != null) && StringUtil.isFalse(setBorderPainted)) button.setBorderPainted(false);
    else button.setBorderPainted(true);

    String content_area_fill = getAttribute(attribute_map, "content_area_filled");
    if ((content_area_fill != null) && StringUtil.isFalse(content_area_fill)) button.setContentAreaFilled(false);
    else button.setContentAreaFilled(true);

    if (!is_processing_condition) {
        String default_value = getAttribute(attribute_map, "default_value");
        if (default_value == null) default_value = getAttribute(attribute_map, "value");
        if (default_value == null) default_value = getAttribute(attribute_map, "selected");
        if (setdefaults && (default_value != null)) {
            if (StringUtil.isTrue(default_value)) button.setSelected(true);
            else if (StringUtil.isFalse(default_value)) button.setSelected(false);
        }
    }

    String margin = getAttribute(attribute_map, "margin");
    if (margin != null) button.setMargin(StringUtil.parseInsets(margin));
    else button.setMargin(RapidEvolutionUI.stdinsetsize);

    String text = getAttribute(attribute_map, "text");
    if (text != null) button.setText(text);
    else button.setText("");

    String mnemonic = getAttribute(attribute_map, "mnemonic");
    if (mnemonic != null) {
      if (mnemonic.equalsIgnoreCase("A")) button.setMnemonic(KeyEvent.VK_A);
      else if (mnemonic.equalsIgnoreCase("B")) button.setMnemonic(KeyEvent.VK_B);
      else if (mnemonic.equalsIgnoreCase("C")) button.setMnemonic(KeyEvent.VK_C);
      else if (mnemonic.equalsIgnoreCase("D")) button.setMnemonic(KeyEvent.VK_D);
      else if (mnemonic.equalsIgnoreCase("E")) button.setMnemonic(KeyEvent.VK_E);
      else if (mnemonic.equalsIgnoreCase("F")) button.setMnemonic(KeyEvent.VK_F);
      else if (mnemonic.equalsIgnoreCase("G")) button.setMnemonic(KeyEvent.VK_G);
      else if (mnemonic.equalsIgnoreCase("H")) button.setMnemonic(KeyEvent.VK_H);
      else if (mnemonic.equalsIgnoreCase("I")) button.setMnemonic(KeyEvent.VK_I);
      else if (mnemonic.equalsIgnoreCase("J")) button.setMnemonic(KeyEvent.VK_J);
      else if (mnemonic.equalsIgnoreCase("K")) button.setMnemonic(KeyEvent.VK_K);
      else if (mnemonic.equalsIgnoreCase("L")) button.setMnemonic(KeyEvent.VK_L);
      else if (mnemonic.equalsIgnoreCase("M")) button.setMnemonic(KeyEvent.VK_M);
      else if (mnemonic.equalsIgnoreCase("N")) button.setMnemonic(KeyEvent.VK_N);
      else if (mnemonic.equalsIgnoreCase("O")) button.setMnemonic(KeyEvent.VK_O);
      else if (mnemonic.equalsIgnoreCase("P")) button.setMnemonic(KeyEvent.VK_P);
      else if (mnemonic.equalsIgnoreCase("Q")) button.setMnemonic(KeyEvent.VK_Q);
      else if (mnemonic.equalsIgnoreCase("R")) button.setMnemonic(KeyEvent.VK_R);
      else if (mnemonic.equalsIgnoreCase("S")) button.setMnemonic(KeyEvent.VK_S);
      else if (mnemonic.equalsIgnoreCase("T")) button.setMnemonic(KeyEvent.VK_T);
      else if (mnemonic.equalsIgnoreCase("U")) button.setMnemonic(KeyEvent.VK_U);
      else if (mnemonic.equalsIgnoreCase("V")) button.setMnemonic(KeyEvent.VK_V);
      else if (mnemonic.equalsIgnoreCase("W")) button.setMnemonic(KeyEvent.VK_W);
      else if (mnemonic.equalsIgnoreCase("X")) button.setMnemonic(KeyEvent.VK_X);
      else if (mnemonic.equalsIgnoreCase("Y")) button.setMnemonic(KeyEvent.VK_Y);
      else if (mnemonic.equalsIgnoreCase("Z")) button.setMnemonic(KeyEvent.VK_Z);
      else if (mnemonic.equalsIgnoreCase("0")) button.setMnemonic(KeyEvent.VK_0);
      else if (mnemonic.equalsIgnoreCase("1")) button.setMnemonic(KeyEvent.VK_1);
      else if (mnemonic.equalsIgnoreCase("2")) button.setMnemonic(KeyEvent.VK_2);
      else if (mnemonic.equalsIgnoreCase("3")) button.setMnemonic(KeyEvent.VK_3);
      else if (mnemonic.equalsIgnoreCase("4")) button.setMnemonic(KeyEvent.VK_4);
      else if (mnemonic.equalsIgnoreCase("5")) button.setMnemonic(KeyEvent.VK_5);
      else if (mnemonic.equalsIgnoreCase("6")) button.setMnemonic(KeyEvent.VK_6);
      else if (mnemonic.equalsIgnoreCase("7")) button.setMnemonic(KeyEvent.VK_7);
      else if (mnemonic.equalsIgnoreCase("8")) button.setMnemonic(KeyEvent.VK_8);
      else if (mnemonic.equalsIgnoreCase("9")) button.setMnemonic(KeyEvent.VK_9);
    }

    String rolloverenabled = getAttribute(attribute_map, "rollover_enabled");
    if ((rolloverenabled != null) && StringUtil.isTrue(rolloverenabled))
      button.setRolloverEnabled(true);
    else
      button.setRolloverEnabled(false);

    String iconstr = getAttribute(attribute_map, "icon");
    String disablediconstr = getAttribute(attribute_map, "disabled_icon");
    String selectedselectediconstr = getAttribute(attribute_map, "selected_icon");
    String disabledselectediconstr = getAttribute(attribute_map, "disabled_selected_icon");
    String pressediconstr = getAttribute(attribute_map, "pressed_icon");
    String rollovericonstr = getAttribute(attribute_map, "rollover_icon");
    if (iconstr == null) {
      if ((button instanceof JCheckBox)) {
        iconstr = (String) iconmap.get("checkbox");
        if (iconstr == null) iconstr = (String) iconmap.get("checkbox_icon");
      }
      if ((button instanceof JRadioButton)) iconstr = (String) iconmap.get("radiobutton_icon");
      if ((button instanceof JButton)) iconstr = (String) iconmap.get("button_icon");
    }
    if (iconstr != null) {
      try {
        ImageIcon icon = getImageIcon(skinpath + iconstr, (text == null) ? button.getText() : text);
        button.setIcon(icon);
      } catch (Exception e) { log.error("readSharedAbstractButtonSettings(): error", e); }
    } else button.setIcon(null);
    if (pressediconstr == null) {
      if ((button instanceof JCheckBox)) {
        pressediconstr = (String) iconmap.get("checkbox_pressed");
        if (pressediconstr == null) pressediconstr = (String) iconmap.get("checkbox_pressed_icon");
      }
      if ((button instanceof JRadioButton)) pressediconstr = (String) iconmap.get("radiobutton_pressed_icon");
      if ((button instanceof JButton)) pressediconstr = (String) iconmap.get("button_pressed_icon");
    }
    if (pressediconstr != null) {
      try {
        ImageIcon pressedicon = getImageIcon(skinpath + pressediconstr, (text == null) ? button.getText() : text);
        button.setPressedIcon(pressedicon);
      } catch (Exception e) { log.error("readSharedAbstractButtonSettings(): error", e); }
    } else button.setPressedIcon(null);
    if (rollovericonstr == null) {
      if ((button instanceof JCheckBox)) {
        rollovericonstr = (String) iconmap.get("checkbox_rollover");
        if (rollovericonstr == null) rollovericonstr = (String) iconmap.get("checkbox_rollover_icon");
      }
      if ((button instanceof JRadioButton)) rollovericonstr = (String) iconmap.get("radiobutton_rollover_icon");
      if ((button instanceof JButton)) rollovericonstr = (String) iconmap.get("button_rollover_icon");
    }
    if (rollovericonstr != null) {
      button.setRolloverEnabled(true);
      try {
        ImageIcon rollovericon = getImageIcon(skinpath + rollovericonstr, (text == null) ? button.getText() : text);
        button.setRolloverIcon(rollovericon);
      } catch (Exception e) { log.error("readSharedAbstractButtonSettings(): error", e); }
    } else button.setRolloverIcon(null);
    if (disablediconstr == null) {
      if ((button instanceof JCheckBox)) {
        disablediconstr = (String) iconmap.get("checkbox_disabled");
        if (disablediconstr == null) disablediconstr = (String) iconmap.get("checkbox_disabled_icon");
      }
      if ((button instanceof JRadioButton)) disablediconstr = (String) iconmap.get("radiobutton_disabled_icon");
      if ((button instanceof JButton)) disablediconstr = (String) iconmap.get("button_disabled_icon");
    }
    if (disablediconstr != null) {
      try {
        ImageIcon icon = getImageIcon(skinpath + disablediconstr, (text == null) ? button.getText() : text);
        button.setDisabledIcon(icon);
      } catch (Exception e) { log.error("readSharedAbstractButtonSettings(): error", e); }
    } else button.setDisabledIcon(null);
    if (disabledselectediconstr == null) {
      if ((button instanceof JCheckBox)) {
        disabledselectediconstr = (String) iconmap.get("checkbox_disabled_selected");
        if (disabledselectediconstr == null) disabledselectediconstr = (String) iconmap.get("checkbox_disabled_selected_icon");
      }
      if ((button instanceof JRadioButton)) disabledselectediconstr = (String) iconmap.get("radiobutton_disabled_selected_icon");
      if ((button instanceof JButton)) disabledselectediconstr = (String) iconmap.get("button_disabled_selected_icon");
    }
    if (disabledselectediconstr != null) {
      try {
        ImageIcon icon = getImageIcon(skinpath + disabledselectediconstr, (text == null) ? button.getText() : text);
        button.setDisabledSelectedIcon(icon);
      } catch (Exception e) { log.error("readSharedAbstractButtonSettings(): error", e); }
    } else button.setDisabledSelectedIcon(null);
    if (selectedselectediconstr == null) {
      if ((button instanceof JCheckBox)) {
        selectedselectediconstr = (String) iconmap.get("checkbox_selected");
        if (selectedselectediconstr == null) selectedselectediconstr = (String) iconmap.get("checkbox_selected_icon");
      }
      if ((button instanceof JRadioButton)) selectedselectediconstr = (String) iconmap.get("radiobutton_selected_icon");
      if ((button instanceof JButton)) selectedselectediconstr = (String) iconmap.get("button_selected_icon");
    }
    if (selectedselectediconstr != null) {
      try {
        ImageIcon icon = getImageIcon(skinpath + selectedselectediconstr, (text == null) ? button.getText() : text);
        button.setSelectedIcon(icon);
      } catch (Exception e) { log.error("readSharedAbstractButtonSettings(): error", e); }
    } else button.setSelectedIcon(null);

    String horizontal_alignment = getAttribute(attribute_map, "horizontal_alignment");
    String vertical_alignment = getAttribute(attribute_map, "vertical_alignment");
    String horizontal_position = getAttribute(attribute_map, "horizontal_position");
    String vertical_position = getAttribute(attribute_map, "vertical_position");

    if (horizontal_alignment != null) {
      if (horizontal_alignment.equalsIgnoreCase("left")) button.setHorizontalAlignment(JLabel.LEFT);
      else if (horizontal_alignment.equalsIgnoreCase("center")) button.setHorizontalAlignment(JLabel.CENTER);
      else if (horizontal_alignment.equalsIgnoreCase("right")) button.setHorizontalAlignment(JLabel.RIGHT);
      else if (horizontal_alignment.equalsIgnoreCase("leading")) button.setHorizontalAlignment(JLabel.LEADING);
      else if (horizontal_alignment.equalsIgnoreCase("trailing")) button.setHorizontalAlignment(JLabel.TRAILING);
    } else {
     button.setHorizontalAlignment(JLabel.CENTER);
    }
    if (vertical_alignment != null) {
      if (vertical_alignment.equalsIgnoreCase("top")) button.setVerticalAlignment(JLabel.TOP);
      else if (vertical_alignment.equalsIgnoreCase("center")) button.setVerticalAlignment(JLabel.CENTER);
      else if (vertical_alignment.equalsIgnoreCase("bottom")) button.setVerticalAlignment(JLabel.BOTTOM);
    } else {
      button.setVerticalAlignment(JLabel.CENTER);
    }
    if (horizontal_position != null) {
      if (horizontal_position.equalsIgnoreCase("left")) button.setHorizontalTextPosition(JLabel.LEFT);
      else if (horizontal_position.equalsIgnoreCase("center")) button.setHorizontalTextPosition(JLabel.CENTER);
      else if (horizontal_position.equalsIgnoreCase("right")) button.setHorizontalTextPosition(JLabel.RIGHT);
      else if (horizontal_position.equalsIgnoreCase("leading")) button.setHorizontalTextPosition(JLabel.LEADING);
      else if (horizontal_position.equalsIgnoreCase("trailing")) button.setHorizontalTextPosition(JLabel.TRAILING);
    } else {
      button.setHorizontalTextPosition(JLabel.TRAILING);
    }
    if (vertical_position != null) {
      if (vertical_position.equalsIgnoreCase("top")) button.setVerticalTextPosition(JLabel.TOP);
      else if (vertical_position.equalsIgnoreCase("center")) button.setVerticalTextPosition(JLabel.CENTER);
      else if (vertical_position.equalsIgnoreCase("bottom")) button.setVerticalTextPosition(JLabel.BOTTOM);
    } else {
      button.setVerticalTextPosition(JLabel.CENTER);
    }
  }

  private Border getBorder(Component component, NamedNodeMap attribute_map) {
      String border = getAttribute(attribute_map, "border");
      if (border == null) {
        if (component instanceof JDialog) {
          if (bordermap.get("dialog") != null) attribute_map = (NamedNodeMap)bordermap.get("dialog");
          if (bordermap.get("dialog_border") != null) attribute_map = (NamedNodeMap)bordermap.get("dialog");
        }
        if (component instanceof JFrame) {
          if (bordermap.get("frame") != null) attribute_map = (NamedNodeMap)bordermap.get("frame");
          if (bordermap.get("frame_border") != null) attribute_map = (NamedNodeMap)bordermap.get("frame");
        }
        border = getAttribute(attribute_map, "border_style");
        if (border == null) return null;
      }
      Border newborder = null;
      if (border.equalsIgnoreCase("line")) {
        String bordercolorstr = getAttribute(attribute_map, "border_color");
        Color bordercolor = (bordercolorstr != null) ? StringUtil.parseColor(bordercolorstr) : component.getForeground();
        String thicknessstr = getAttribute(attribute_map, "border_thickness");
        if (thicknessstr != null) {
          newborder = BorderFactory.createLineBorder(bordercolor, Integer.parseInt(thicknessstr));
        } else newborder = BorderFactory.createLineBorder(bordercolor);
      } else if (border.equalsIgnoreCase("etched")) {
        Color highlight = null;
        Color shadow = null;
        int type = EtchedBorder.LOWERED;
        String typestr = getAttribute(attribute_map, "border_type");
        if (typestr != null && (typestr.equalsIgnoreCase("raised"))) type = EtchedBorder.RAISED;
        highlight = StringUtil.parseColor(getAttribute(attribute_map, "border_highlight"));
        shadow = StringUtil.parseColor(getAttribute(attribute_map, "border_shadow"));
        if ((highlight != null) && (shadow != null)) newborder = BorderFactory.createEtchedBorder(type, highlight, shadow);
        else newborder = BorderFactory.createEtchedBorder(type);
      } else if (border.equalsIgnoreCase("curved")) {          
          Color wall_color = null;
          wall_color = StringUtil.parseColor(getAttribute(attribute_map, "border_wall_color"));
          int sink_level = -1;
          String sink_str = getAttribute(attribute_map, "border_sink_level");
          try {
              sink_level = Integer.parseInt(sink_str);
          } catch (Exception e) { }
          if ((wall_color != null) && (sink_level != -1)) {
              newborder = new CurvedBorder(sink_level, wall_color);
          } else if (wall_color != null) {
              newborder = new CurvedBorder(wall_color);
          } else if (sink_level != -1) {
              newborder = new CurvedBorder(sink_level);
          } else {
              newborder = new CurvedBorder();
          }
      } else if (border.equalsIgnoreCase("pill")) {
          Color wall_color = null;
          wall_color = StringUtil.parseColor(getAttribute(attribute_map, "border_wall_color"));
          if (wall_color != null) {
              newborder = new PillBorder(wall_color);
          } else {
              newborder = new PillBorder();
          }
          String level_str = getAttribute(attribute_map, "border_inset_level");
          try {
              ((PillBorder)newborder).setInsetLevel(Integer.parseInt(level_str));
          } catch (Exception e) { }
          String border_fill = getAttribute(attribute_map, "border_fill");
          if (border_fill != null) {
              if (StringUtil.isTrue(border_fill)) {
                  ((PillBorder)newborder).setBorderFill(true);
              } else if (StringUtil.isFalse(border_fill)) {
                  ((PillBorder)newborder).setBorderFill(false);
              }              
          }          
      } else if (border.equalsIgnoreCase("rounded")) {          
          Color wall_color = null;
          String colorStr = getAttribute(attribute_map, "border_wall_color");
          if (!colorStr.startsWith("%")) {
              wall_color = StringUtil.parseColor(colorStr);
              colorStr = null;
          }
          int sink_level = -1;
          String sink_str = getAttribute(attribute_map, "border_sink_level");
          try {
              sink_level = Integer.parseInt(sink_str);
          } catch (Exception e) { }          
          if ((wall_color != null) && (sink_level != -1)) {
              newborder = new RoundedBorder(sink_level, wall_color);
          } else if (wall_color != null) {
              newborder = new RoundedBorder(wall_color);
          } else if (sink_level != -1) {
              newborder = new RoundedBorder(sink_level);
          } else {
              newborder = new RoundedBorder();
          }
          if (colorStr != null)
              ((RoundedBorder)newborder).setWallColorId(colorStr);
          String level_str = getAttribute(attribute_map, "border_inset_level");
          try {
              ((RoundedBorder)newborder).setInsetLevel(Integer.parseInt(level_str));
          } catch (Exception e) { }
          String border_fill = getAttribute(attribute_map, "border_fill");
          if (border_fill != null) {
              if (StringUtil.isTrue(border_fill)) {
                  ((RoundedBorder)newborder).setBorderFill(true);
              } else if (StringUtil.isFalse(border_fill)) {
                  ((RoundedBorder)newborder).setBorderFill(false);
              }              
          }
        } else if (border.equalsIgnoreCase("bevel")) {
        int type = BevelBorder.LOWERED;
        Color highlight = null;
        Color shadow = null;
        Color outerhighlight = null;
        Color outershadow = null;
        String typestr = getAttribute(attribute_map, "border_type");
        if (typestr != null && (typestr.equalsIgnoreCase("raised"))) type = BevelBorder.RAISED;
        highlight = StringUtil.parseColor(getAttribute(attribute_map, "border_highlight"));
        shadow = StringUtil.parseColor(getAttribute(attribute_map, "border_shadow"));
        if (highlight == null) highlight = StringUtil.parseColor(getAttribute(attribute_map, "border_highlight_inner"));
        if (shadow == null) shadow = StringUtil.parseColor(getAttribute(attribute_map, "border_shadow_inner"));
        outerhighlight = StringUtil.parseColor(getAttribute(attribute_map, "border_highlight_outer"));
        outershadow = StringUtil.parseColor(getAttribute(attribute_map, "border_shadow_outer"));
        if ((outershadow != null) && (outerhighlight != null) && (shadow != null) && (highlight != null)) newborder = BorderFactory.createBevelBorder(type, outerhighlight, highlight, outershadow, shadow);
        else if ((shadow != null) && (highlight != null)) newborder = BorderFactory.createBevelBorder(type, highlight, shadow);
        else newborder = BorderFactory.createBevelBorder(type);
      } else if ((border.equalsIgnoreCase("empty")) || (border.equalsIgnoreCase("none")) || (border.equalsIgnoreCase("null"))){
        String top = getAttribute(attribute_map, "border_top");
        String bottom = getAttribute(attribute_map, "border_bottom");
        String left = getAttribute(attribute_map, "border_left");
        String right = getAttribute(attribute_map, "border_right");
        if ((top != null) && (bottom != null) && (left != null) && (right != null))
          newborder = BorderFactory.createEmptyBorder(Integer.parseInt(top), Integer.parseInt(left), Integer.parseInt(bottom), Integer.parseInt(right));
        else newborder = BorderFactory.createEmptyBorder();
      } else if (border.equalsIgnoreCase("matte")) {
        String top = getAttribute(attribute_map, "border_top");
        String bottom = getAttribute(attribute_map, "border_bottom");
        String left = getAttribute(attribute_map, "border_left");
        String right = getAttribute(attribute_map, "border_right");
        Color color = StringUtil.parseColor(getAttribute(attribute_map, "border_color"));
        if (color == null) color = component.getForeground();
        String iconstr = getAttribute(attribute_map, "border_icon");
        if (iconstr != null) {
          ImageIcon icon = getImageIcon(skinpath + iconstr);
          if ((top != null) && (bottom != null) && (left != null) && (right != null))
            newborder = BorderFactory.createMatteBorder(Integer.parseInt(top), Integer.parseInt(left), Integer.parseInt(bottom), Integer.parseInt(right), icon);
        } else {
          if ((top != null) && (bottom != null) && (left != null) && (right != null))
            newborder = BorderFactory.createMatteBorder(Integer.parseInt(top), Integer.parseInt(left), Integer.parseInt(bottom), Integer.parseInt(right), color);
        }
      }
      String btitle = getAttribute(attribute_map, "border_title");
      if (btitle != null) {
        if (newborder != null) {
          String justificationstr = getAttribute(attribute_map, "border_title_justification");
          int justification = TitledBorder.DEFAULT_JUSTIFICATION;
          if ((justificationstr != null) && (justificationstr.equalsIgnoreCase("left"))) justification = TitledBorder.LEFT;
          if ((justificationstr != null) && (justificationstr.equalsIgnoreCase("center"))) justification = TitledBorder.CENTER;
          if ((justificationstr != null) && (justificationstr.equalsIgnoreCase("right"))) justification = TitledBorder.RIGHT;
          if ((justificationstr != null) && (justificationstr.equalsIgnoreCase("leading"))) justification = TitledBorder.LEADING;
          if ((justificationstr != null) && (justificationstr.equalsIgnoreCase("trailing"))) justification = TitledBorder.TRAILING;
          if ((justificationstr != null) && (justificationstr.equalsIgnoreCase("default_justification"))) justification = TitledBorder.DEFAULT_JUSTIFICATION;
          String positionstr = getAttribute(attribute_map, "border_title_position");
          int position = TitledBorder.DEFAULT_POSITION;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("above_top"))) position = TitledBorder.ABOVE_TOP;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("top"))) position = TitledBorder.TOP;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("below_top"))) position = TitledBorder.BELOW_TOP;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("above_bottom"))) position = TitledBorder.ABOVE_BOTTOM;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("bottom"))) position = TitledBorder.BOTTOM;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("below_bottom"))) position = TitledBorder.BELOW_BOTTOM;
          if ((positionstr != null) && (positionstr.equalsIgnoreCase("default_position"))) position = TitledBorder.DEFAULT_POSITION;
          String font = getAttribute(attribute_map, "border_font");
          String font_size = getAttribute(attribute_map, "border_font_size");
          String font_style = getAttribute(attribute_map, "border_font_style");
          Font thisfont = ((font != null) && (font_size != null) && (font_style != null)) ? new Font(font, Integer.parseInt(font_size), Integer.parseInt(font_style)) : null;
          Color color = StringUtil.parseColor(getAttribute(attribute_map, "border_title_color"));
          if (color == null) color = StringUtil.parseColor(getAttribute(attribute_map, "border_color"));
          if (color == null) color = component.getForeground();
          if ((color != null) && (font != null) && (justificationstr != null) && (positionstr != null))
            newborder = BorderFactory.createTitledBorder(newborder, btitle, justification, position, thisfont, color);
          else if ((font != null) && (justificationstr != null) && (positionstr != null))
            newborder = BorderFactory.createTitledBorder(newborder, btitle, justification, position, thisfont);
          else if ((justificationstr != null) && (positionstr != null))
            newborder = BorderFactory.createTitledBorder(newborder, btitle, justification, position);
          else
            newborder = BorderFactory.createTitledBorder(newborder, btitle);
        } else {
          newborder = BorderFactory.createTitledBorder(btitle);
        }
    }
    return newborder;
  }

  public void readSharedJComponentSettings(JComponent component, NamedNodeMap attribute_map) {

    setAlignmentX(component, getAttribute(attribute_map, "align_x"));
    setAlignmentY(component, getAttribute(attribute_map, "align_y"));

    String autoscrolls = getAttribute(attribute_map, "autoscrolls");
    if ((autoscrolls != null) && StringUtil.isFalse(autoscrolls)) component.setAutoscrolls(false);
    component.setAutoscrolls(true);

    Border oldborder = component.getBorder();
    Border newborder = getBorder(component, attribute_map);
    if (newborder != null) {
      if (oldborder != null) Backup(old_borders, component, oldborder);
      component.setBorder(newborder);
    }

    String locale = getAttribute(attribute_map, "locale");
    if (locale != null) component.setDefaultLocale(new Locale(locale));
    else component.setDefaultLocale(Locale.getDefault());

    String minsizestr = getAttribute(attribute_map, "minimum_size");
    if (minsizestr != null) {
      Backup(old_minimum_size, component, component.getMinimumSize());
      component.setMinimumSize(StringUtil.parseDimension(minsizestr));
    }
    String maxsizestr = getAttribute(attribute_map, "maximum_size");
    if (maxsizestr != null) {
      Backup(old_maximum_size, component, component.getMaximumSize());
      component.setMaximumSize(StringUtil.parseDimension(maxsizestr));
    }
    String preferredsizestr = getAttribute(attribute_map, "preferred_size");
    if (preferredsizestr != null) {
      Backup(old_preferred_size, component, component.getPreferredSize());
      component.setPreferredSize(StringUtil.parseDimension(preferredsizestr));
    }
    String opaque = getAttribute(attribute_map, "opaque");
    if ((opaque != null)) {
      Backup(old_opaquevalues, component, new Boolean(component.isOpaque()));
      if (StringUtil.isTrue(opaque)) component.setOpaque(true);
      else if (StringUtil.isFalse(opaque)) component.setOpaque(false);
    }

    String tooltip = getAttribute(attribute_map, "tooltip");
    if ((tooltip != null) && !tooltip.equals("")) component.setToolTipText(tooltip);
    else component.setToolTipText(null);    
  }

  public void readSharedComponentSettings(Component component, NamedNodeMap attribute_map) {

    String sizestr = getAttribute(attribute_map, "size");
    if (sizestr != null) component.setSize(StringUtil.parseDimension(sizestr));
    else {
      if (component instanceof JDialog) component.setSize(default_dialog_size);
      else if (component instanceof JFrame) component.setSize(default_frame_size);
    }

    String background = getAttribute(attribute_map, "background");
    setBackground(component, background);
    
    String foreground = getAttribute(attribute_map, "foreground");
    setForeground(component, foreground);

    try {
        String componentorientation = getAttribute(attribute_map, "component_orientation");
        if (!(LookAndFeelManager.isSubstanceLAF() && component instanceof JColorChooser)) {
            if ((componentorientation != null) && (componentorientation.equalsIgnoreCase("right_to_left")))
                component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            else
                component.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
    } catch (Exception e) {
        log.trace("readSharedComponentSettings(): error Exception", e);
    }

    String cursor = getAttribute(attribute_map, "cursor");
    boolean cursorset = false;
    if (cursor != null) {
      cursorset = true;
      if (cursor.equalsIgnoreCase("wait")) component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      else if (cursor.equalsIgnoreCase("w_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("text")) component.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
      else if (cursor.equalsIgnoreCase("s_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("sw_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("se_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("n_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("nw_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("ne_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("move")) component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      else if (cursor.equalsIgnoreCase("hand")) component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      else if (cursor.equalsIgnoreCase("e_resize")) component.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
      else if (cursor.equalsIgnoreCase("default")) component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//      if (cursor.equalsIgnoreCase("custom")) component.setCursor(Cursor.getPredefinedCursor(Cursor.CUSTOM_CURSOR));
      else if (cursor.equalsIgnoreCase("crosshair")) component.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      else cursorset = false;
    }
    if (!cursorset) {
      component.setCursor(Cursor.getDefaultCursor());
    }
    String focusable = getAttribute(attribute_map, "focusable");
    if ((component instanceof JLabel) || (component instanceof JPanel)) {
      if ((focusable != null) && StringUtil.isTrue(focusable)) component.setFocusable(true);
      else component.setFocusable(false);
    } else {
      if ((focusable != null) && StringUtil.isFalse(focusable)) component.setFocusable(false);
      else component.setFocusable(true);
    }

    String fontid = determineId(component, ".font");
    String fonttype = (String)uiTypeMap.get(fontid);
    Font currentfont = null;
    if (fontmap.get(fonttype) != null) currentfont = (Font)fontmap.get(fonttype);
    if ((currentfont == null) && (fontmap.get(fontid) != null)) currentfont = (Font)fontmap.get(fontid);
    if (currentfont == null) {
      if (fonttype != null) {
        fonttype = "default" + fonttype.substring(fonttype.indexOf("_"), fonttype.length());
        if (fontmap.get(fonttype) != null) currentfont = (Font)fontmap.get(fonttype);
      }
    }
    if (currentfont == null) currentfont = UIManager.getFont("default_font");
    if (currentfont == null) currentfont = UIManager.getFont(fontid);
    if (currentfont != null) {
      String font = getAttribute(attribute_map, "font");
      if (font != null) currentfont = new Font(font, currentfont.getStyle(), currentfont.getSize());
      String font_size = getAttribute(attribute_map, "font_size");
      if (font_size != null) currentfont = new Font(currentfont.getName(), currentfont.getStyle(), Integer.parseInt(font_size));
      String font_style = getAttribute(attribute_map, "font_style");
      if (font_style != null) {
        if (font_style.equalsIgnoreCase("plain")) currentfont = new Font(currentfont.getName(), Font.PLAIN, currentfont.getSize());
        if (font_style.equalsIgnoreCase("bold")) currentfont = new Font(currentfont.getName(), Font.BOLD, currentfont.getSize());
        if (font_style.equalsIgnoreCase("italic")) currentfont = new Font(currentfont.getName(), Font.ITALIC, currentfont.getSize());
        if (font_style.equalsIgnoreCase("bold+italic")) currentfont = new Font(currentfont.getName(), Font.BOLD + Font.ITALIC, currentfont.getSize());
      }
      setFontHelper(component, currentfont);
    } else {
        log.trace("readSharedComponentSettings(): font not set for: " + fontid);
    }

    String locale = getAttribute(attribute_map, "locale");
    if (locale != null) component.setLocale(new Locale(locale));
    else component.setLocale(Locale.getDefault());

  }

  private Object getUIObject(String type) {
    Object object = UIManager.getColor(type);
    if (object != null) return object;
    object = UIManager.getFont(type);
    return object;
  }

  private String determineId(Component component, String aspect) {
    String type = component.getClass().getName().substring(component.getClass().getPackage().getName().length() + 2);
    type = type + aspect;
    String oritinalid = type;
    Object object = getUIObject(type);
    Class current_class = component.getClass().getSuperclass();
    while ((object == null) && (current_class != null)) {
      type = current_class.getName().substring(current_class.getPackage().getName().length() + 2);
      type = type + aspect;
      object = getUIObject(type);
      current_class = current_class.getSuperclass();
    }
    if (object == null) return oritinalid;
    return type;
  }

  private HashMap uiTypeMap = new HashMap();
  private HashMap uiReverseMap = new HashMap();
  public void addToUImap(Object key, Object value) {
    uiTypeMap.put(key, value);
    uiReverseMap.put(value, key);
  }


  public void setBackground(Component component) {
      Color color = getAspectColor(component, ".background", null);
      setBackgroundHelper(component, color);      
  }
  private void setBackground(Component component, String background) {
    Color color = getAspectColor(component, ".background", background);
    setBackgroundHelper(component, color);
  }
  
  public void setForeground(Component component) {
      Color color = getAspectColor(component, ".foreground", null);
      setForegroundHelper(component, color);      
  }

  private void setForeground(Component component, String foreground) {
    Color color = getAspectColor(component, ".foreground", foreground);
    setForegroundHelper(component, color);
  }

  private void setFontHelper(Component component, Font font) {
    if (component instanceof JSpinner) {
      JSpinner spinner = (JSpinner) component;
      spinner.setFont(font);
      JFormattedTextField tf = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
      tf.setFont(font);
    } else if (component instanceof IFrame) {
      IFrame iframe = (IFrame) component;
      iframe.setFont(font);
      iframe.getTitleBar().setTitleFont(font);
    } else if (component instanceof IDialog) {
      IDialog idialog = (IDialog) component;
      idialog.setFont(font);
      idialog.getTitleBar().setTitleFont(font);
    } else if (component instanceof JTable) {
      JTable table = (JTable)component;
      table.setFont(font);
      Font headerfont = null;
      if (fontmap.get("table_header_font") != null) headerfont = (Font)fontmap.get("tableheader_font");
      if ((headerfont == null) && (fontmap.get("default_font") != null)) headerfont = (Font)fontmap.get("default_font");
      if (headerfont == null) headerfont = UIManager.getFont("TableHeader.font");
      if (headerfont == null) headerfont = font;
      table.getTableHeader().setFont(headerfont);
    } else if (component instanceof JList) {
      JList list = (JList)component;
      list.setFont(font);

    } else if (component instanceof JSlider) {
      JSlider slider = (JSlider)component;
      slider.setFont(font);
      if (slider.getLabelTable() != null) {
        Enumeration labels = slider.getLabelTable().elements();
        while (labels.hasMoreElements()) {
          JLabel label = (JLabel)labels.nextElement();
          label.setFont(font);
        }
      }
    } else {
      component.setFont(font);
    }
  }

//  private void isType(Component component, Class classtype) {


//  }

  private void setBackgroundHelper(Component component, Color color) {
    if (color == null) return;
//    if (component.getParent() != null) component.getParent().setBackground(color);
    if (component instanceof IFrame) {
      IFrame frame = (IFrame)component;
      frame.getIContentPane().setBackground(color);
      frame.setBackground(color);
      Color titlecolor = null;
      if (colormap.get("frame_title_background") != null) titlecolor = (Color)colormap.get("frame_title_background");
      if (titlecolor != null) frame.getTitleBar().setBackground(titlecolor);
      //      frame.getBor
    } else if (component instanceof JFrame) {
      JFrame frame = (JFrame)component;
      frame.getContentPane().setBackground(color);
      frame.setBackground(color);
//      frame.getBor
    } else if (component instanceof JSpinner) {
      JSpinner spinner = (JSpinner) component;
      spinner.setBackground(color);
      JFormattedTextField tf = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
      tf.setBackground(color);
    } else if (component instanceof JSlider) {
      JSlider slider = (JSlider)component;
      slider.setBackground(color);
      if (slider.getLabelTable() != null) {
        Enumeration labels = slider.getLabelTable().elements();
        while (labels.hasMoreElements()) {
          JLabel label = (JLabel)labels.nextElement();
          Color thiscolor = getAspectColor(label, ".background", null);
          label.setBackground(thiscolor);
        }
      }
    } else if (component instanceof JSplitPane) {
      JSplitPane splitpane = (JSplitPane)component;
      splitpane.setBackground(color);

    } else if (component instanceof JScrollPane) {
      JScrollPane scrollpane = (JScrollPane)component;
      scrollpane.setBackground(color);
      Color background = getAspectColor(scrollpane.getHorizontalScrollBar(), ".background", null);
      if (background != null) {
        scrollpane.getHorizontalScrollBar().setBackground(background);
        scrollpane.getVerticalScrollBar().setBackground(background);
      }
    } else if (component instanceof JPanel) {
      JPanel panel = (JPanel) component;
      panel.setBackground(color);
      if (panel.getParent() != null) panel.getParent().setBackground(color);
    } else if (component instanceof JTable) {
      JTable table = (JTable)component;
      table.setBackground(color);
      // set background of scrollpane
      if (scrollhash.get(table) != null) {
        JScrollPane scrollpane = (JScrollPane)scrollhash.get(table);
        Color background = getAspectColor(scrollpane.getHorizontalScrollBar(), ".background", null);
        if (background == null) background = component.getBackground();
        if (background != null) {
          scrollpane.getHorizontalScrollBar().setBackground(background);
          scrollpane.getVerticalScrollBar().setBackground(background);
        }
        scrollpane.setBackground(color);
      }
      if (table.getParent() != null) table.getParent().setBackground(color);
      // set column header colors
      Color headercolor = getAspectColor(table.getTableHeader(), ".background", null);
      if (headercolor == null) headercolor = color;
      table.getTableHeader().setBackground(headercolor);
      // set selected color
      Color selectbackgroundcolor = getAspectColor(table, ".selectionBackground", null);
      table.setSelectionBackground(selectbackgroundcolor);
    } else if (component instanceof IDialog) {
      IDialog dialog = (IDialog)component;
      dialog.getIContentPane().setBackground(color);
      dialog.setBackground(color);
      Color titlecolor = null;
      if (colormap.get("dialog_title_background") != null) titlecolor = (Color)colormap.get("dialog_title_background");
      if (titlecolor != null) dialog.getTitleBar().setBackground(titlecolor);
    } else if (component instanceof JDialog) {
      JDialog dialog = (JDialog)component;
      dialog.getContentPane().setBackground(color);
      dialog.setBackground(color);
    } else if (component instanceof JList) {
      JList list = (JList)component;
      list.setBackground(color);
      if (list.getParent() != null) list.getParent().setBackground(color);
    } else if (component instanceof JTextArea) {
      JTextArea textarea = (JTextArea)component;
      textarea.setBackground(color);
      if (textarea.getParent() != null) textarea.getParent().setBackground(color);
    } else if (component instanceof JColorChooser) {
      JColorChooser chooser = (JColorChooser) component;
      setColorChooserBackground(chooser, color);
    } else if (component instanceof JComboBox) {
      JComboBox combobox = (JComboBox) component;
      combobox.setBackground(color);
//      combobox.getEditor().getEditorComponent().setBackground(color);
    } else {
      component.setBackground(color);
    }
  }

  private void SetBackgroundRecursive(Component component, Color color) {
    setBackgroundHelper(component, color);
    if (component instanceof JPanel) {
      JPanel panel = (JPanel)component;
      for (int j = 0; j < panel.getComponents().length; ++j) {
        SetBackgroundRecursive(panel.getComponent(j), color);
      }
    }
  }

    public void setColorChooserBackground(JColorChooser chooser, Color color) {
        chooser.setBackground(color);
        try {
            if (!LookAndFeelManager.isSubstanceLAF())
                chooser.getPreviewPanel().setBackground(color);
        } catch (Exception e) {
            // Substance skins errors...
            log.trace("setColorChooserBackground(): error Exception", e);
        }
        for (int i = 0; i < chooser.getChooserPanels().length; ++i) {
          chooser.getChooserPanels()[i].setBackground(color);
          chooser.getChooserPanels()[i].getParent().setBackground(color);
          for (int j = 0; j < chooser.getChooserPanels()[i].getComponents().length; ++j) {
            SetBackgroundRecursive(chooser.getChooserPanels()[i].getComponent(j), color);
          }
        }       
    }
    

    public void setColorChooserForeground(JColorChooser chooser, Color color) {
        chooser.setForeground(color);
        try {
            if (!LookAndFeelManager.isSubstanceLAF())
                chooser.getPreviewPanel().setForeground(color);
        } catch (Exception e) {
            // Substance skins errors...
            log.trace("setColorChooserForeground(): error Exception", e);            
        }
        for (int i = 0; i < chooser.getChooserPanels().length; ++i) {
          chooser.getChooserPanels()[i].setForeground(color);
          chooser.getChooserPanels()[i].getParent().setForeground(color);
          for (int j = 0; j < chooser.getChooserPanels()[i].getComponents().length; ++j) {
            SetForegroundRecursive(chooser.getChooserPanels()[i].getComponent(j), color);
          }
        }       
    }   
  
  private void SetForegroundRecursive(Component component, Color color) {
    setForegroundHelper(component, color);
    if (component instanceof JPanel) {
      JPanel panel = (JPanel) component;
      for (int j = 0; j < panel.getComponents().length; ++j) {
        SetForegroundRecursive(panel.getComponent(j), color);
      }
    }
  }

  private void setForegroundHelper(Component component, Color color) {
    if (component instanceof IFrame) {
      IFrame frame = (IFrame) component;
      frame.getIContentPane().setForeground(color);
      frame.setForeground(color);
      Color titlecolor = color;
      if (colormap.get("frame_title_foreground") != null) titlecolor = (Color)colormap.get("frame_title_foreground");
      frame.getTitleBar().setTitleForeground(titlecolor);
      JComponent comp = frame.getLayeredPane();
      for(int i=0, ub=comp.getComponentCount(); i<ub; ++i) {
          comp.getComponent(i).setForeground(color);
       }
    }
    else if (component instanceof JFrame) {
      JFrame frame = (JFrame) component;
      frame.getContentPane().setForeground(color);
      frame.setForeground(color);
      JComponent comp = frame.getLayeredPane();
      for(int i=0, ub=comp.getComponentCount(); i<ub; ++i) {
          comp.getComponent(i).setForeground(color);
       }
    }
    else if (component instanceof IDialog) {
      IDialog dialog = (IDialog) component;
      dialog.getIContentPane().setForeground(color);
      dialog.setForeground(color);
      Color titlecolor = color;
      if (colormap.get("dialog_title_foreground") != null) titlecolor = (Color)colormap.get("dialog_title_foreground");
      dialog.getTitleBar().setTitleForeground(titlecolor);
    }
    else if (component instanceof JDialog) {
      JDialog dialog = (JDialog) component;
      dialog.getContentPane().setForeground(color);
      dialog.setForeground(color);

    }
    else if (component instanceof JTable) {
      JTable table = (JTable) component;
      table.setForeground(color);
      Color headercolor = getAspectColor(table.getTableHeader(), ".foreground", null);
      if (headercolor == null) headercolor = color;
      table.getTableHeader().setForeground(headercolor);
      // set selected color
      Color selectforegroundcolor = getAspectColor(table, ".selectionForeground", null);
      table.setSelectionForeground(selectforegroundcolor);
    } else if (component instanceof JSlider) {
      JSlider slider = (JSlider)component;
      slider.setForeground(color);
      if (slider.getLabelTable() != null) {
        Enumeration labels = slider.getLabelTable().elements();
        while (labels.hasMoreElements()) {
          JLabel label = (JLabel)labels.nextElement();
          Color thiscolor = getAspectColor(label, ".foreground", null);
          label.setForeground(thiscolor);
        }
      }
    } else if (component instanceof JSpinner) {
      JSpinner spinner = (JSpinner) component;
      spinner.setForeground(Color.red);
      JFormattedTextField tf = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
      tf.setForeground(color);
    } else if (component instanceof JColorChooser) {
      JColorChooser chooser = (JColorChooser) component;
      setColorChooserForeground(chooser, color);
    } else if (component instanceof JComboBox) {
      JComboBox combobox = (JComboBox) component;
      combobox.setForeground(color);
//      combobox.getEditor().getEditorComponent().setForeground(color);
    } else {
      component.setForeground(color);
    }
  }

  private HashMap condition_map = new HashMap();
  public boolean use_button_fixer = true;
  
  private void RecurseLoad(NodeList nl, Component parentComponent, int position, String namespace, boolean remove, boolean traverse_only) throws Exception {
    if (nl == null) return;
    if (remove) unset = true;
    for (int i = 0; i < nl.getLength(); ++i) {
      Node node = nl.item(i);
      Component thisComponent = null;
      if (node.getNodeName().equalsIgnoreCase("settings")) {
//        if (!remove) {
          NodeList settings = node.getChildNodes();
          for (int s = 0; s < settings.getLength(); ++s) {
            Node setting = settings.item(s);
            NamedNodeMap attribute_map = setting.getAttributes();
            if (setting.getNodeName().equalsIgnoreCase("margin")) {
              String id = getAttribute(attribute_map, "id");
              Insets margins = null;
              try {
                int left = Integer.parseInt(getAttribute(attribute_map, "left"));
                int top = Integer.parseInt(getAttribute(attribute_map, "top"));
                int bottom = Integer.parseInt(getAttribute(attribute_map,
                    "bottom"));
                int right = Integer.parseInt(getAttribute(attribute_map,
                    "right"));
                margins = new Insets(top, left, bottom, right);
              }
              catch (Exception e) {}
              if (margins == null) {
              try {
                margins = StringUtil.parseInsets(getAttribute(attribute_map, "value"));
              }
              catch (Exception e) {}
              }
              if (margins != null) {
                marginmap.put(id, margins);
                if (id.equalsIgnoreCase("default_margin")) RapidEvolutionUI.stdinsetsize = margins;
                else if (id.equalsIgnoreCase("field_margin")) default_field_margin = margins;
              }
            }
            else if (setting.getNodeName().equalsIgnoreCase("dialog-message")) {
                String id = getAttribute(attribute_map, "id");
                String titleKey = "DIALOG_MESSAGE_" + id.toUpperCase() + "_TITLE";
                String textKey = "DIALOG_MESSAGE_" + id.toUpperCase() + "_TEXT";
                String options1Key = "DIALOG_MESSAGE_" + id.toUpperCase() + "_OPTION1";
                String options2Key = "DIALOG_MESSAGE_" + id.toUpperCase() + "_OPTION2";
                String options3Key = "DIALOG_MESSAGE_" + id.toUpperCase() + "_OPTION3";
              String title = getString(titleKey);
              String text = getString(textKey);
              String option_1 = getString(options1Key);
              String option_2 = getString(options2Key);
              String option_3 = getString(options3Key);
              if (removeTextValues) {
                  removeAttribute(attribute_map, "title");
                  removeAttribute(attribute_map, "text");
                  removeAttribute(attribute_map, "option_1");
                  removeAttribute(attribute_map, "option_2");
                  removeAttribute(attribute_map, "option_3");
              }
              if (writer != null) {
                  writer.writeLine(titleKey + "=" + title);
                  writer.writeLine(textKey + "=" + text);
                  if (option_1 != null)
                      writer.writeLine(options1Key + "=" + option_1);
                  if (option_2 != null)
                      writer.writeLine(options2Key + "=" + option_2);
                  if (option_3 != null)
                      writer.writeLine(options3Key + "=" + option_3);
              }
              errormap.put(id,
                           new Error(id, title, text, option_1, option_2, option_3));
            }
            else if (setting.getNodeName().equalsIgnoreCase("message")) {
              String id = getAttribute(attribute_map, "id");
              String messageKey = "MESSAGE_" + id.toUpperCase();
              String text = getString(messageKey);
              messagemap.put(id.toLowerCase(), text);
              if (removeTextValues) {
                  removeAttribute(attribute_map, "text");
              }              
              if (writer != null) {
                  writer.writeLine(messageKey + "=" + text);
              }
            }
            else if (setting.getNodeName().equalsIgnoreCase("boolean")) {
              String id = getAttribute(attribute_map, "id");
              String value = getAttribute(attribute_map, "value");
              if (id.equalsIgnoreCase("use_default_window_borders")) {
                if (StringUtil.isTrue(value)) usedefaultborders = true;
                else if (StringUtil.isFalse(value)) usedefaultborders = false;
              } else if (id.equalsIgnoreCase("use_default_splitpanel_dividers")) {
                if (StringUtil.isTrue(value)) use_default_splitpanel_dividers = true;
                else if (StringUtil.isFalse(value)) use_default_splitpanel_dividers = false;
              } else if (id.equalsIgnoreCase("use_button_fixer")) {
                  if (StringUtil.isTrue(value)) use_button_fixer = true;
                  else if (StringUtil.isFalse(value)) use_button_fixer = false;                  
              } else if (id.equalsIgnoreCase("use_empty_splitpanel_dividers")) {
                  if (StringUtil.isTrue(value)) use_empty_splitpanel_dividers = true;
                  else if (StringUtil.isFalse(value)) use_empty_splitpanel_dividers = false;                  
              } else if (id.equalsIgnoreCase("use_liquid_buttons")) {
                  if (StringUtil.isTrue(value)) {
                      use_liquid_buttons = true;
                      REButton.setCurrentButtonType(REButton.BUTTON_TYPE_LIQUID);
                  }
                  else if (StringUtil.isFalse(value)) {
                      use_liquid_buttons = false;                  
                      REButton.setCurrentButtonType(REButton.BUTTON_TYPE_NORMAL);
                  }
              }
            }
            else if (setting.getNodeName().equalsIgnoreCase("color")) {
              String id = getAttribute(attribute_map, "id");
              String value = getAttribute(attribute_map, "value");
              if (value.startsWith("%"))
                  colormap.put(id, value);
              else
                  colormap.put(id, StringUtil.parseColor(value));
            }
            else if (setting.getNodeName().equalsIgnoreCase("icon")) {
              String id = getAttribute(attribute_map, "id");
              String value = getAttribute(attribute_map, "value");
              iconmap.put(id, value);
            }
            else if (setting.getNodeName().equalsIgnoreCase("border")) {
              String id = getAttribute(attribute_map, "id");
              bordermap.put(id, attribute_map);
            }
            else if (setting.getNodeName().equalsIgnoreCase("size")) {
              String id = getAttribute(attribute_map, "id");
              String value = getAttribute(attribute_map, "value");
              sizemap.put(id, value);
            }
            else if (setting.getNodeName().equalsIgnoreCase("font")) {
              String id = getAttribute(attribute_map, "id");
              String name = getAttribute(attribute_map, "name");
              String size = getAttribute(attribute_map, "size");
              String style = getAttribute(attribute_map, "style");
              if (name == null) name = default_font_name;
              int styleint = default_font_style;
              if (style != null) {
                if (style.equalsIgnoreCase("plain")) styleint = Font.PLAIN;
                if (style.equalsIgnoreCase("bold")) styleint = Font.BOLD;
                if (style.equalsIgnoreCase("italic")) styleint = Font.ITALIC;
                if (style.equalsIgnoreCase("bold+italic")) styleint = Font.BOLD +
                    Font.ITALIC;
              }
//            Font f = new Font(Font.d
              if (id.equalsIgnoreCase("default.font")) id = "default_font";
              if (id.equalsIgnoreCase("default")) id = "default_font";
              Font font = new Font(name, styleint, Integer.parseInt(size));
              fontmap.put(id, font);
            }
          }
  //      }
        setDefaults(remove);
      } else if (node.getNodeName().equalsIgnoreCase("template-definitions")) {
        NodeList settings = node.getChildNodes();
        for (int s = 0; s < settings.getLength(); ++s) {
          Node setting = settings.item(s);
          NamedNodeMap attribute_map = setting.getAttributes();
          if (setting.getNodeName().equalsIgnoreCase("template")) {
            String id = getAttribute(attribute_map, "id");
            templates.put(id, setting.getChildNodes());
          }
        }
      } else if (node.getNodeName().equalsIgnoreCase("condition")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getAttribute(attribute_map, "id");
        if (!remove && !checkbox_hash.containsKey(id)) recursiveconditions.add(new RecursiveCondition(id, node, parentComponent, position, namespace, condition_map));                  
        if (remove) log.debug("RecurseLoad(): removing condition: " + id);
        else if (traverse_only) log.debug("RecurseLoad(): traversing condition: " + id);
        else log.debug("RecurseLoad(): adding condition: " + id); 
        NodeList childnodes = node.getChildNodes();
        for (int cn = 0; cn < childnodes.getLength(); ++cn) {
          Node childnode = childnodes.item(cn);
          if (checkbox_hash.containsKey(id)) {
            JCheckBox checkbox = (JCheckBox)checkbox_hash.get(id);
            condition_map.put(id, new Boolean(checkbox.isSelected()));
            if (!checkbox.isSelected() && StringUtil.isFalse(childnode.getNodeName()))  {
              RecurseLoad(childnode.getChildNodes(), parentComponent, position, namespace, remove, traverse_only);              
            } else if (checkbox.isSelected() && StringUtil.isTrue(childnode.getNodeName())) {
              RecurseLoad(childnode.getChildNodes(), parentComponent, position, namespace, remove, traverse_only);
            } else if (!checkbox.isSelected() && StringUtil.isTrue(childnode.getNodeName())) {
                RecurseLoad(childnode.getChildNodes(), parentComponent, position, namespace, remove, true);
            } else if (checkbox.isSelected() && StringUtil.isFalse(childnode.getNodeName())) {
                RecurseLoad(childnode.getChildNodes(), parentComponent, position, namespace, remove, true);
            }
            condition_map.remove(id);
          
          } else {              
              if (StringUtil.isFalse(childnode.getNodeName()))  {
                  condition_map.put(id, new Boolean(false));
                  RecurseLoad(childnode.getChildNodes(), parentComponent, position, namespace, remove, traverse_only);
                  condition_map.remove(id);
              } else {
                  condition_map.put(id, new Boolean(true));
                  RecurseLoad(childnode.getChildNodes(), parentComponent, position, namespace, remove, true);
                  condition_map.remove(id);
              }              
          }
        }
      } else if (node.getNodeName().equalsIgnoreCase("template")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getAttribute(attribute_map, "id");
        String newnamespace = getFullId(namespace, getAttribute(attribute_map, "namespace"));
        if (id != null) {
          NodeList childnodes = (NodeList)templates.get(id);
          if (childnodes != null) RecurseLoad(childnodes, parentComponent, position, newnamespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("frame")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JFrame frame = (JFrame)getInstance(id, usedefaultborders ? MFrame.class : IFrame.class, null);
          thisComponent = (Component)frame;
          if (remove) {              
              frame.setVisible(false);
              imap.remove(id);
          } else if (!traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_FRAME"))
                  idText = idText.substring(0, idText.length() - 6);              
              String titleKey = "FRAME_" + idText + "_TITLE";
              // this code was needed to fix a bug switching between skins
              // TODO: investigate why
              if (usedefaultborders) frame = new MFrame();
              else frame = new IFrame();
              thisComponent = (Component)frame;   
              imap.put(id, frame);
              
            String title = getString(titleKey);
            String logo = getAttribute(attribute_map, "icon");
            String resizable = getAttribute(attribute_map, "resizable");
            if (removeTextValues) {
                removeAttribute(attribute_map, "title");
            }              
            if (writer != null) {
                writer.writeLine(titleKey + "=" + title);
            }            
            readSharedComponentSettings((Component)frame, attribute_map);
            frame_hash.put(id, frame);
            
            if (usedefaultborders || (!(frame instanceof IFrame))) frame.getContentPane().setLayout(new MyRelativeLayout());
            else ((IFrame)frame).getIContentPane().setLayout(new MyRelativeLayout());

            
            if ((resizable != null) && (StringUtil.isFalse(resizable))) frame.setResizable(false);
            else frame.setResizable(true);
            if (title != null) frame.setTitle(title);
            else frame.setTitle("");
            if (logo != null) {
              try {
                Image icon = Toolkit.getDefaultToolkit().getImage(skinpath + logo);
                frame.setIconImage(icon);
//                if (frame instanceof IFrame) {
//                  IFrame iframe = (IFrame)frame;
//                  iframe.setIcon
//                }
              } catch (Exception e) { log.error("RecurseLoad(): error", e); }
            } else frame.setIconImage(null);
            Border newborder = getBorder(frame, attribute_map);
            if (newborder != null) {
              if (!usedefaultborders && (frame instanceof IFrame)) ((IFrame)frame).setIContentPaneBorder(newborder);
            }
            if (sizemap.get("frame_titlebar_height") != null) {
              int size = Integer.parseInt((String)sizemap.get("frame_titlebar_height"));
              if (frame instanceof IFrame) {
                IFrame iframe = (IFrame)frame;
                iframe.setTitleBarHeight(size);
              }
            }
          }
        }
      } else if (node.getNodeName().equalsIgnoreCase("dialog")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JDialog dialog = (JDialog)getInstance(id, usedefaultborders ? JDialog.class : IDialog.class, parentComponent);
          thisComponent = (Component)dialog;
          if (remove) {
              imap.remove(id);
          }
          else if (!traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_DIALOG"))
                  idText = idText.substring(0, idText.length() - 7);                
              String titleKey = "DIALOG_" + idText + "_TITLE";
            String title = getString(titleKey);
            String resizable = getAttribute(attribute_map, "resizable");
            String triggers = getAttribute(attribute_map, "trigger");
            String close_triggers = getAttribute(attribute_map, "close_trigger");
            if (removeTextValues) {
                removeAttribute(attribute_map, "title");
            }              
            if (writer != null) {
                if (title != null)
                    writer.writeLine(titleKey + "=" + title);
            }            
            readSharedComponentSettings((Component)dialog, attribute_map);
            dialog_hash.put(id, dialog);
            if (usedefaultborders || (!(dialog instanceof IDialog))) dialog.getContentPane().setLayout(new MyRelativeLayout());
            else ((IDialog)dialog).getIContentPane().setLayout(new MyRelativeLayout());
            if (title != null) dialog.setTitle(title);
            else dialog.setTitle("");
            if ((resizable != null) && (StringUtil.isFalse(resizable))) dialog.setResizable(false);
            else dialog.setResizable(true);
            if (triggers != null) {
              StringTokenizer tokens = new StringTokenizer(triggers, ",");
              while (tokens.hasMoreTokens()) {
                String this_trigger = tokens.nextToken();
                JButton button = (JButton)getInstance(this_trigger, REButton.class, null);
                button.addActionListener(new DialogOpenAction(id));
              }
            }
            if (sizemap.get("dialog_titlebar_height") != null) {
              int size = Integer.parseInt((String)sizemap.get("dialog_titlebar_height"));
              if (dialog instanceof IDialog) {
                IDialog idialog = (IDialog)dialog;
                idialog.setTitleBarHeight(size);
              }
            }
            if (close_triggers != null) {
              StringTokenizer tokens = new StringTokenizer(close_triggers, ",");
              while (tokens.hasMoreTokens()) {
                String this_trigger = (String)tokens.nextToken();
                JButton button = (JButton)getInstance(this_trigger, REButton.class, null);
                button.addActionListener(new DialogCloseAction(id));
              }
            }
            Border newborder = getBorder(dialog, attribute_map);
            if (newborder != null) {
              if (!usedefaultborders && (dialog instanceof IDialog)) ((IDialog)dialog).setIContentPaneBorder(newborder);
            }
          }
        }
      } else if (node.getNodeName().equalsIgnoreCase("moreinfo_panel")) {
          NamedNodeMap attribute_map = node.getAttributes();
          String id = getFullId(namespace, getAttribute(attribute_map, "id"));
          if (id != null) {
            MoreInfoPanel panel = (MoreInfoPanel)getInstance(id, MoreInfoPanel.class, null);            
            thisComponent = (Component)panel;
            String spinColor = getAttribute(attribute_map, "spin_color");
            String spinColorHovering = getAttribute(attribute_map, "spin_color_hovering");
            String boxAxis = getAttribute(attribute_map, "box_axis");
            if (remove) {
                //...
            } else if (!traverse_only){
                moreinfomap.put(id, panel);
              readSharedComponentSettings((Component)panel, attribute_map);
              if (panel instanceof JComponent) readSharedJComponentSettings((JComponent)panel, attribute_map);
              if (spinColor != null) {
                  panel.setSpinColor(StringUtil.parseColor(spinColor));
              }
              if (spinColorHovering != null) {
                  panel.setSpinColorHovering(StringUtil.parseColor(spinColorHovering));
              }
              if (boxAxis != null) {
                  if (boxAxis.toLowerCase().startsWith("y"))
                      panel.setBoxLayoutAxis(BoxLayout.Y_AXIS);
                  else
                      panel.setBoxLayoutAxis(BoxLayout.X_AXIS);
              }
            }
            setConstraints(node, id, (Component)panel, parentComponent, position, namespace, remove, traverse_only);
          }
          
      } else if (node.getNodeName().equalsIgnoreCase("panel")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JPanel panel = (JPanel)getInstance(id, JPanel.class, null);
          thisComponent = (Component)panel;
          if (remove) {
              if (!id.equalsIgnoreCase("midi_keyboard_piano_panel"))
                  imap.remove(id);
          } else if (!traverse_only){
            readSharedComponentSettings((Component)panel, attribute_map);
            if (panel instanceof JComponent) readSharedJComponentSettings((JComponent)panel, attribute_map);
            panel.setLayout(new MyRelativeLayout());
          }
          setConstraints(node, id, (Component)panel, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("scrollpanel")) {     
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JScrollPane scrollpane = (JScrollPane)getInstance(id, JScrollPane.class, new JPanel());                    
          JPanel panel = getParentPanel(scrollpane);
          String watermarkPath = getAttribute(attribute_map, "watermark");
          String waterMarkTile = getAttribute(attribute_map, "watermark_tile");
          
          if (watermarkPath != null) {
              try {
                  ScrollPaneWatermark watermark = new ScrollPaneWatermark();
                  watermark.setBackgroundTexture(new File(skinpath + watermarkPath).toURL());
                  watermark.setView(panel);
                  scrollpane.setViewport(watermark); 
                  if (waterMarkTile != null) {
                      watermark.setTile(StringUtil.isTrue(waterMarkTile));
                  }
              } catch (Exception e) { 
                  log.error("RecurseLoad(): error setting watermark", e);
              }          
          }
          
          if (remove) imap.remove(id);
          else if (!traverse_only) {
            readSharedComponentSettings((Component)scrollpane, attribute_map);
            //if (scrollpane instanceof JComponent) readSharedJComponentSettings((JComponent)scrollpane, attribute_map);
            scrollpane.setWheelScrollingEnabled(true);
            panel.setLayout(new MyRelativeLayout());
            readSharedComponentSettings((Component)panel, attribute_map);
            if (panel instanceof JComponent) readSharedJComponentSettings((JComponent)panel, attribute_map);
          }
          thisComponent = (Component)panel;
          setConstraints(node, id, (Component)scrollpane, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("toolbar")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        JPanel panel = (JPanel)getInstance(id, JPanel.class, null);
        thisComponent = (Component)panel;
        if (id != null) {
          if (remove) imap.remove(id);
          else if (!traverse_only) {
            readSharedComponentSettings((Component)panel, attribute_map);
            if (panel instanceof JComponent) readSharedJComponentSettings((JComponent)panel, attribute_map);
            String layout = getAttribute(attribute_map, "layout");
            if ((layout == null) || (layout.toLowerCase().startsWith("x"))) panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            else panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
          }
          setConstraints(node, id, (Component)panel, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("label")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JLabel label = (JLabel)getInstance(id, RELabel.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_LABEL"))
                  idText = idText.substring(0, idText.length() - 6);                
              String textKey = "LABEL_" + idText;
              String tooltipKey = "LABEL_" + idText + "_TOOLTIP";
            String text = getString(textKey);
            String iconstr = getAttribute(attribute_map, "icon");
            String disablediconstr = getAttribute(attribute_map, "disabled_icon");
            String horizontal_alignment = getAttribute(attribute_map, "horizontal_alignment");
            String vertical_alignment = getAttribute(attribute_map, "vertical_alignment");
            String horizontal_position = getAttribute(attribute_map, "horizontal_position");
            String vertical_position = getAttribute(attribute_map, "vertical_position");
            String icontextgap = getAttribute(attribute_map, "icon_text_gap");           
            readSharedComponentSettings((Component)label, attribute_map);
            if (label instanceof JComponent) readSharedJComponentSettings((JComponent)label, attribute_map);
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                label.setToolTipText(tooltip);
            else
                label.setToolTipText(null);
            if (!id.equals("search_results_field")) {
                if (text != null) label.setText(text);
                else label.setText("");
            }
            if (iconstr != null) {
              try {
                ImageIcon icon = getImageIcon(skinpath + iconstr, (text == null) ? label.getText() : text);
                label.setIcon(icon);
              } catch (Exception e) { log.error("RecurseLoad(): error", e); }
            } else label.setIcon(null);
            if (disablediconstr != null) {
              try {
                ImageIcon icon = getImageIcon(skinpath + disablediconstr, (text == null) ? label.getText() : text);
                label.setDisabledIcon(icon);
              } catch (Exception e) { log.error("RecurseLoad(): error", e); }
            } else label.setDisabledIcon(null);
            if (horizontal_alignment != null) {
              if (horizontal_alignment.equalsIgnoreCase("left")) label.setHorizontalAlignment(JLabel.LEFT);
              else if (horizontal_alignment.equalsIgnoreCase("center")) label.setHorizontalAlignment(JLabel.CENTER);
              else if (horizontal_alignment.equalsIgnoreCase("right")) label.setHorizontalAlignment(JLabel.RIGHT);
              else if (horizontal_alignment.equalsIgnoreCase("leading")) label.setHorizontalAlignment(JLabel.LEADING);
              else if (horizontal_alignment.equalsIgnoreCase("trailing")) label.setHorizontalAlignment(JLabel.TRAILING);
            } else {
              if (text != null) label.setHorizontalAlignment(JLabel.LEADING);
              else label.setHorizontalAlignment(JLabel.CENTER);
            }
            if (vertical_alignment != null) {
              if (vertical_alignment.equalsIgnoreCase("top")) label.setVerticalAlignment(JLabel.TOP);
              else if (vertical_alignment.equalsIgnoreCase("center")) label.setVerticalAlignment(JLabel.CENTER);
              else if (vertical_alignment.equalsIgnoreCase("bottom")) label.setVerticalAlignment(JLabel.BOTTOM);
            } else {
              label.setVerticalAlignment(JLabel.CENTER);
            }
            if (horizontal_position != null) {
              if (horizontal_position.equalsIgnoreCase("left")) label.setHorizontalTextPosition(JLabel.LEFT);
              else if (horizontal_position.equalsIgnoreCase("center")) label.setHorizontalTextPosition(JLabel.CENTER);
              else if (horizontal_position.equalsIgnoreCase("right")) label.setHorizontalTextPosition(JLabel.RIGHT);
              else if (horizontal_position.equalsIgnoreCase("leading")) label.setHorizontalTextPosition(JLabel.LEADING);
              else if (horizontal_position.equalsIgnoreCase("trailing")) label.setHorizontalTextPosition(JLabel.TRAILING);
            } else {
              if (text != null) label.setHorizontalTextPosition(JLabel.LEADING);
              else label.setHorizontalTextPosition(JLabel.CENTER);
            }
            if (vertical_position != null) {
              if (vertical_position.equalsIgnoreCase("top")) label.setVerticalTextPosition(JLabel.TOP);
              else if (vertical_position.equalsIgnoreCase("center")) label.setVerticalTextPosition(JLabel.CENTER);
              else if (vertical_position.equalsIgnoreCase("bottom")) label.setVerticalTextPosition(JLabel.BOTTOM);
            } else {
              label.setVerticalTextPosition(JLabel.CENTER);
            }
            if (icontextgap != null) label.setIconTextGap(Integer.parseInt(icontextgap));
            else label.setIconTextGap(4);
            if (removeTextValues) {
                removeAttribute(attribute_map, "text");
                removeAttribute(attribute_map, "tooltip");
            }              
            if (writer != null) {
                if (text != null)
                    writer.writeLine(textKey + "=" + text);
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }            
          }
          setConstraints(node, id, (Component)label, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("field") || node.getNodeName().equalsIgnoreCase("passwordfield")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JTextField field = (JTextField)getInstance(id, RETextField.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_FIELD"))
                  idText = idText.substring(0, idText.length() - 6);                
              String tooltipKey = "FIELD_" + idText + "_TOOLTIP";              
            String columns = getAttribute(attribute_map, "columns");
            String horizontal_alignment = getAttribute(attribute_map, "horizontal_alignment");
            if (horizontal_alignment == null) horizontal_alignment = getAttribute(attribute_map, "align");
            String scroll_offset = getAttribute(attribute_map, "scroll_offset");
            if (columns == null) columns = getAttribute(attribute_map, "width");
            readSharedComponentSettings((Component)field, attribute_map);
            if (field instanceof JComponent) readSharedJComponentSettings((JComponent)field, attribute_map);
            if (field instanceof JTextComponent) readSharedTextComponentSettings(id, (JTextComponent)field, attribute_map);
            if (scroll_offset != null) {
              Backup(old_field_scroll_offsets, field, String.valueOf(field.getScrollOffset()));
              field.setScrollOffset(Integer.parseInt(scroll_offset));
            }
            if (horizontal_alignment != null) {
              if (horizontal_alignment.equalsIgnoreCase("left")) field.setHorizontalAlignment(JTextField.LEFT);
              else if (horizontal_alignment.equalsIgnoreCase("center")) field.setHorizontalAlignment(JTextField.CENTER);
              else if (horizontal_alignment.equalsIgnoreCase("right")) field.setHorizontalAlignment(JTextField.RIGHT);
              else if (horizontal_alignment.equalsIgnoreCase("leading")) field.setHorizontalAlignment(JTextField.LEADING);
              else if (horizontal_alignment.equalsIgnoreCase("trailing")) field.setHorizontalAlignment(JTextField.TRAILING);
            } else {
              field.setHorizontalAlignment(JTextField.LEADING);
            }
            if (columns != null) field.setColumns(Integer.parseInt(columns));
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                field.setToolTipText(tooltip);
            else
                field.setToolTipText(null);
            if (removeTextValues) {
                removeAttribute(attribute_map, "tooltip");
            }            
            if (writer != null) {
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }                        
          }
          setConstraints(node, id, (Component)field, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("checkbox")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JCheckBox checkbox = (JCheckBox)getInstance(id, JCheckBox.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_CHECKBOX"))
                  idText = idText.substring(0, idText.length() - 9);                
              String textKey = "CHECKBOX_" + idText;
              String tooltipKey = "CHECKBOX_" + idText + "_TOOLTIP";
            String border_flat = getAttribute(attribute_map, "border_flat");
            String setBorderPainted = getAttribute(attribute_map, "border_painted");
            readSharedComponentSettings((Component)checkbox, attribute_map);
            if (checkbox instanceof JComponent) readSharedJComponentSettings((JComponent)checkbox, attribute_map);
            if (checkbox instanceof AbstractButton) readSharedAbstractButtonSettings((AbstractButton)checkbox, attribute_map);
            checkbox_hash.put(id, checkbox);
            if ((setBorderPainted != null) && StringUtil.isTrue(setBorderPainted)) checkbox.setBorderPainted(true);
            else checkbox.setBorderPainted(false);
            if ((border_flat != null) && StringUtil.isTrue(border_flat)) checkbox.setBorderPaintedFlat(true);
            else checkbox.setBorderPaintedFlat(false);
            String text = getString(textKey);
            if (text != null)
                checkbox.setText(text);
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                checkbox.setToolTipText(tooltip);
            else
                checkbox.setToolTipText(null);
            if (removeTextValues) {
                removeAttribute(attribute_map, "text");
                removeAttribute(attribute_map, "tooltip");
            }              
            if (writer != null) {
                if (!checkbox.getText().equals(""))
                    writer.writeLine(textKey + "=" + checkbox.getText());
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }            
          }
          setConstraints(node, id, (Component)checkbox, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("colorchooser")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JColorChooser colorchooser = (JColorChooser)getInstance(id, JColorChooser.class, null);
          if (!remove && !traverse_only) {
            readSharedComponentSettings((Component)colorchooser, attribute_map);
            if (colorchooser instanceof JComponent) readSharedJComponentSettings((JComponent)colorchooser, attribute_map);
          }
          setConstraints(node, id, (Component)colorchooser, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("datechooser")) {
          NamedNodeMap attribute_map = node.getAttributes();
          String id = getFullId(namespace, getAttribute(attribute_map, "id"));
          if (id != null) {
            JDateChooser datechooser = (JDateChooser)getInstance(id, JDateChooser.class, null);
            if (!remove && !traverse_only) {
              readSharedComponentSettings((Component)datechooser, attribute_map);
              if (datechooser instanceof JComponent) readSharedJComponentSettings((JComponent)datechooser, attribute_map);
            }
            setConstraints(node, id, (Component)datechooser, parentComponent, position, namespace, remove, traverse_only);
          }
      } else if (node.getNodeName().equalsIgnoreCase("slider")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JSlider slider = (JSlider)getInstance(id, MediaSlider.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_SLIDER"))
                  idText = idText.substring(0, idText.length() - 7);
              String tooltipKey = "SLIDER_" + idText + "_TOOLTIP";              
              if (slider instanceof MediaSlider) {
                  MediaSlider mslider = (MediaSlider)slider;
                  try {
                      String leftTrackIcon = getAttribute(attribute_map, "left_track_icon");
                      String rightTrackIcon = getAttribute(attribute_map, "right_track_icon");
                      String centerTrackIcon = getAttribute(attribute_map, "center_track_icon");
                      String thumbIcon = getAttribute(attribute_map, "thumb_icon");
                      String thumbPressedIcon = getAttribute(attribute_map, "thumb_pressed_icon");
                      if (leftTrackIcon != null) {
                          ImageIcon icon = getImageIcon(skinpath + leftTrackIcon);
                          mslider.setLeftTrackImage(icon);
                      }
                      if (rightTrackIcon != null) {
                          ImageIcon icon = getImageIcon(skinpath + rightTrackIcon);
                          mslider.setRightTrackImage(icon);
                      }
                      if (centerTrackIcon != null) {
                          ImageIcon icon = getImageIcon(skinpath + centerTrackIcon);
                          mslider.setCenterTrackImage(icon);
                      }
                      if (thumbIcon != null) {
                          ImageIcon icon = getImageIcon(skinpath + thumbIcon);
                          mslider.setThumbImage(icon);
                      }
                      if (thumbPressedIcon != null) {
                          ImageIcon icon = getImageIcon(skinpath + thumbPressedIcon);
                          mslider.setThumbPressedImage(icon);
                      }                                    
                      slider.setUI(new com.mixshare.rapid_evolution.ui.swing.slider.MediaSliderUI(mslider));
                  } catch (Exception e) {
                      log.error("RecurseLoad(): error Exception", e);
                      throw new Exception("Error setting up slider=" + id);                      
                  }
                  new SliderFixer(slider);
              }
//              slider.setUI(new com.mixshare.rapid_evolution.ui.swing.slider.MediaSliderUI(slider));
//            if (slider.getUI() instanceof MetalSliderUI) slider.setUI(new CustomIconSliderUI());
//              slider.putClientProperty("Slider.horizontalThumbIcon", this.getIcon("bpm_slider_thumb_icon"));
            String orientation = getAttribute(attribute_map, "orientation");
            String paint_ticks = getAttribute(attribute_map, "paint_ticks");
            String paint_track = getAttribute(attribute_map, "paint_track");
            String pain_labels = getAttribute(attribute_map, "paint_labels");
            String snap_to_ticks = getAttribute(attribute_map, "snap_to_ticks");
            String extent = getAttribute(attribute_map, "extent");
            String inverted = getAttribute(attribute_map, "inverted");
            String filled = getAttribute(attribute_map, "filled");
            //String horizontal_thumb_icon = getAttribute(attribute_map, "horizontal_thumb_icon");
            readSharedComponentSettings((Component)slider, attribute_map);
            if (slider instanceof JComponent) readSharedJComponentSettings((JComponent)slider, attribute_map);
            if (extent != null) {
              Backup(old_slider_extents, slider, String.valueOf(slider.getExtent()));
              slider.setExtent(Integer.parseInt(extent));
            }
            if ((inverted != null) && StringUtil.isTrue(inverted)) slider.setInverted(true);
            else slider.setInverted(false);
            if ((paint_track != null) && StringUtil.isFalse(paint_track)) slider.setPaintTrack(false);
            else slider.setPaintTrack(true);
            if ((orientation == null)  || (orientation.equalsIgnoreCase("vertical"))) slider.setOrientation(JSlider.VERTICAL);
            else slider.setOrientation(JSlider.HORIZONTAL);
            if ((paint_ticks != null) && (StringUtil.isFalse(paint_ticks))) slider.setPaintTicks(false);
            else slider.setPaintTicks(true);
            if ((pain_labels != null) && (StringUtil.isFalse(pain_labels))) slider.setPaintLabels(false);
            else slider.setPaintLabels(true);                       
            if ((snap_to_ticks != null) && (StringUtil.isTrue(snap_to_ticks))) slider.setSnapToTicks(true);
            else slider.setSnapToTicks(false);
            if ((filled != null) && (StringUtil.isTrue(filled))) {
                slider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
            } else {
                slider.putClientProperty("JSlider.isFilled", Boolean.FALSE);
            }
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                slider.setToolTipText(tooltip);
            else
                slider.setToolTipText(null);
            if (removeTextValues) {
                removeAttribute(attribute_map, "tooltip");
            }            
            if (writer != null) {
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }                                    
          }
          setConstraints(node, id, (Component)slider, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("textarea")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JTextArea textarea = (JTextArea)getInstance(id, RETextArea.class, null);
          JScrollPane scrollpane = (JScrollPane)scrollhash.get(textarea);
          if (scrollpane == null) {
            scrollpane = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollhash.put(textarea, scrollpane);
          }
          if (!remove && !traverse_only) {
            String columns = getAttribute(attribute_map, "columns");
            String vertical_scrollbar = getAttribute(attribute_map, "vertical_scrollbar");
            String horizontal_scrollbar = getAttribute(attribute_map, "horizontal_scrollbar");
            String linewrap = getAttribute(attribute_map, "line_wrap");
            String wrapwordstyle = getAttribute(attribute_map, "wrap_word_style");
            String rows = getAttribute(attribute_map, "rows");
            String tab_size = getAttribute(attribute_map, "tab_size");
            readSharedComponentSettings((Component)textarea, attribute_map);
            if (textarea instanceof JComponent) readSharedJComponentSettings((JComponent)textarea, attribute_map);
            if (textarea instanceof JTextComponent) readSharedTextComponentSettings(id, (JTextComponent)textarea, attribute_map);
            try {
                if ((wrapwordstyle != null) && StringUtil.isFalse(wrapwordstyle)) textarea.setWrapStyleWord(false);
                else textarea.setWrapStyleWord(true);
            } catch (Exception e) { }
            if (columns != null) {
              Backup(old_textarea_columns, textarea, String.valueOf(textarea.getColumns()));
              textarea.setColumns(Integer.parseInt(columns));
            }
            readSharedComponentSettings((Component)scrollpane, attribute_map);
            if (scrollpane instanceof JComponent) readSharedJComponentSettings((JComponent)scrollpane, attribute_map);
            if (vertical_scrollbar != null) {
              if (vertical_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              else if (vertical_scrollbar.equalsIgnoreCase("always")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
              else if (vertical_scrollbar.equalsIgnoreCase("never")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            } else scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            if (horizontal_scrollbar != null) {
              if (horizontal_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              else if (horizontal_scrollbar.equalsIgnoreCase("always")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
              else if (horizontal_scrollbar.equalsIgnoreCase("never")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            } else scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            if (rows != null) {
              Backup(old_textarea_rows, textarea, String.valueOf(textarea.getRows()));
              textarea.setRows(Integer.parseInt(rows));
            }
            if (tab_size != null) {
              Backup(old_textarea_tab_sizes, textarea, String.valueOf(textarea.getTabSize()));
              textarea.setTabSize(Integer.parseInt(tab_size));
            }
            try {
                if (linewrap != null && StringUtil.isFalse(linewrap)) textarea.setLineWrap(false);
                else textarea.setLineWrap(true);
            } catch (Exception e) {
                // TODO: this was needed to catch an exception thrown when using Substance L&F skins...
                if (log.isTraceEnabled())
                    log.trace("RecurseLoad(): an exception occurred=" + e);
            }
          }
          setConstraints(node, id, (Component)scrollpane, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("list")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JList list = (JList)getInstance(id, JList.class, null);
          JScrollPane scrollpane = (JScrollPane)scrollhash.get(list);
          if (scrollpane == null) {
            scrollpane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollhash.put(list, scrollpane);
          }
          if (!remove && !traverse_only) {
            String vertical_scrollbar = getAttribute(attribute_map, "vertical_scrollbar");
            String horizontal_scrollbar = getAttribute(attribute_map, "horizontal_scrollbar");
            String selection_mode = getAttribute(attribute_map, "selection_mode");
            String orientation = getAttribute(attribute_map, "orientation");
            String focusable = getAttribute(attribute_map, "focusable");
            String cell_height = getAttribute(attribute_map, "cell_height");
            String cell_width = getAttribute(attribute_map, "cell_width");
            String visiblerowcount = getAttribute(attribute_map, "visible_row_count");
            readSharedComponentSettings((Component)list, attribute_map);
            if (list instanceof JComponent) readSharedJComponentSettings((JComponent)list, attribute_map);
            if ((cell_height == null) && (sizemap.get("list_cell_height") != null)) cell_height = (String)sizemap.get("list_cell_height");
            if (cell_height != null) {
              if (old_list_cell_heights != null) Backup(old_list_cell_heights, list, String.valueOf(list.getFixedCellHeight()));
              list.setFixedCellHeight(Integer.parseInt(cell_height));
            }
            if (cell_width != null) {
              if (old_list_cell_widths != null) Backup(old_list_cell_widths, list, String.valueOf(list.getFixedCellWidth()));
              list.setFixedCellWidth(Integer.parseInt(cell_width));
            }
            Color selected_foreground = getAspectColor(list, ".selectionForeground", getAttribute(attribute_map, "selection_foreground"));
            Color selected_background = getAspectColor(list, ".selectionBackground", getAttribute(attribute_map, "selection_background"));
            if ((selected_foreground == null)) selected_foreground = list.getForeground();
            if (selected_foreground != null) list.setSelectionForeground(selected_foreground);
            if ((selected_background == null)) selected_background = list.getBackground();
            if (selected_background != null) list.setSelectionBackground(selected_background);
            if (visiblerowcount != null) {
              Backup(old_list_visible_rows, list, String.valueOf(list.getVisibleRowCount()));
              list.setVisibleRowCount(Integer.parseInt(visiblerowcount));
            }
            readSharedComponentSettings((Component)scrollpane, attribute_map);
            if (scrollpane instanceof JComponent) readSharedJComponentSettings((JComponent)scrollpane, attribute_map);
            if (vertical_scrollbar != null) {
              if (vertical_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              else if (vertical_scrollbar.equalsIgnoreCase("always")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
              else if (vertical_scrollbar.equalsIgnoreCase("never")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            } else scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            if (horizontal_scrollbar != null) {
              if (horizontal_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              else if (horizontal_scrollbar.equalsIgnoreCase("always")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
              else if (horizontal_scrollbar.equalsIgnoreCase("never")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            } else scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            if (orientation != null) {
              if (orientation.equalsIgnoreCase("vertical")) list.setLayoutOrientation(JList.VERTICAL);
              else if (orientation.equalsIgnoreCase("horizontal")) list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
              else if (orientation.equalsIgnoreCase("vertical_wrap")) list.setLayoutOrientation(JList.VERTICAL_WRAP);
            } else list.setLayoutOrientation(JList.VERTICAL);
            if (focusable != null) {
              if (StringUtil.isTrue(focusable)) {
                scrollpane.setFocusable(true);
                scrollpane.getHorizontalScrollBar().setFocusable(true);
                scrollpane.getVerticalScrollBar().setFocusable(true);
              } else if (StringUtil.isFalse(focusable)) {
                scrollpane.setFocusable(false);
                scrollpane.getHorizontalScrollBar().setFocusable(false);
                scrollpane.getVerticalScrollBar().setFocusable(false);
              }
            }
          }
          setConstraints(node, id, (Component)scrollpane, parentComponent, position, namespace, remove, traverse_only);
        }

      } else if (node.getNodeName().equalsIgnoreCase("tree")) {
          NamedNodeMap attribute_map = node.getAttributes();
          String id = getFullId(namespace, getAttribute(attribute_map, "id"));
          if (id != null) {
            JTree tree = (JTree)getInstance(id, JTree.class, null);
            JScrollPane scrollpane = (JScrollPane)scrollhash.get(tree);
            if (scrollpane == null) {
              scrollpane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              scrollhash.put(tree, scrollpane);
            }
            if (!remove && !traverse_only) {

                /*
                if (LookAndFeelManager.isSubstanceLAF())
                    tree.setUI(new RESubstanceTreeUI());
                else
                    tree.setUI((TreeUI)Class.forName((String)UIManager.getLookAndFeel().getDefaults().get("TreeUI")).newInstance());
                */
                
                String vertical_scrollbar = getAttribute(attribute_map, "vertical_scrollbar");
              String horizontal_scrollbar = getAttribute(attribute_map, "horizontal_scrollbar");
              String selection_mode = getAttribute(attribute_map, "selection_mode");
              String orientation = getAttribute(attribute_map, "orientation");
              String focusable = getAttribute(attribute_map, "focusable");
              readSharedComponentSettings((Component)tree, attribute_map);
              
//              tree.putClientProperty(LafWidget.ANIMATION_KIND, AnimationKind.NONE);
              
              int total_indent = 0;
              ComponentUI ui = tree.getUI();
              if (ui instanceof BasicTreeUI) { 
                  String leftIndent = (String)sizemap.get("tree_left_child_indent");
                  if (leftIndent != null) {
                      try {
                          int indent = Integer.parseInt(leftIndent);
                          total_indent += indent;
                          if (!LookAndFeelManager.isSubstanceLAF())
                              ((BasicTreeUI)ui).setLeftChildIndent(indent);
                      } catch (Exception e) { log.trace("RecurseLoad(): error", e);  }
                  } else {
                      try {
                          int indent= UIManager.getInt("Tree.leftChildIndent");
                          total_indent += indent;
                          ((BasicTreeUI)ui).setLeftChildIndent(indent);
                      } catch (Exception e) { log.error("RecurseLoad(): error", e); }                          
                  }
                  String rightIndent = (String)sizemap.get("tree_right_child_indent");
                  if (rightIndent != null) {
                      try {
                          int indent = Integer.parseInt(rightIndent);
                          total_indent += indent;
                          if (!LookAndFeelManager.isSubstanceLAF())
                              ((BasicTreeUI)ui).setRightChildIndent(indent);
                      } catch (Exception e) { log.trace("RecurseLoad(): error", e);  }
                  } else {
                      try {
                          int indent = UIManager.getInt("Tree.rightChildIndent");
                          total_indent += indent;
                          ((BasicTreeUI)ui).setRightChildIndent(indent);
                      } catch (Exception e) { log.error("RecurseLoad(): error", e);  }
                  }
             }  
              if (tree instanceof RETree) {
                  ((RETree)tree).setTotalIndent(total_indent);
              }
              
              String row_height = (String)sizemap.get("tree_row_height");
              if (row_height != null) {
                  try {
                      tree.setRowHeight(Integer.parseInt(row_height));
                  } catch (Exception e) { }
              } else {
                  try {
                      tree.setRowHeight(UIManager.getInt("Tree.rowHeight"));
                  } catch (Exception e) { }
              }
              
              if (tree instanceof JComponent) readSharedJComponentSettings((JComponent)tree, attribute_map);
              Color selected_foreground = getAspectColor(tree, ".selectionForeground", getAttribute(attribute_map, "selection_foreground"));
              Color selected_background = getAspectColor(tree, ".selectionBackground", getAttribute(attribute_map, "selection_background"));
              readSharedComponentSettings((Component)scrollpane, attribute_map);
              if (scrollpane instanceof JComponent) readSharedJComponentSettings((JComponent)scrollpane, attribute_map);
              if (vertical_scrollbar != null) {
                if (vertical_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                else if (vertical_scrollbar.equalsIgnoreCase("always")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                else if (vertical_scrollbar.equalsIgnoreCase("never")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
              } else scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              if (horizontal_scrollbar != null) {
                if (horizontal_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                else if (horizontal_scrollbar.equalsIgnoreCase("always")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                else if (horizontal_scrollbar.equalsIgnoreCase("never")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
              } else scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              if (focusable != null) {
                if (StringUtil.isTrue(focusable)) {
                  scrollpane.setFocusable(true);
                  scrollpane.getHorizontalScrollBar().setFocusable(true);
                  scrollpane.getVerticalScrollBar().setFocusable(true);
                } else if (StringUtil.isFalse(focusable)) {
                  scrollpane.setFocusable(false);
                  scrollpane.getHorizontalScrollBar().setFocusable(false);
                  scrollpane.getVerticalScrollBar().setFocusable(false);
                }
              }
            }
            setConstraints(node, id, (Component)scrollpane, parentComponent, position, namespace, remove, traverse_only);
          }      
      
      } else if (node.getNodeName().equalsIgnoreCase("table")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          RETable table = (RETable)getInstance(id, RETable.class, null);
          JScrollPane scrollpane = (JScrollPane)scrollhash.get(table);
          if (scrollpane == null) {
            scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollhash.put(table, scrollpane);
          }
          if (!remove && !traverse_only) {
            String vertical_scrollbar = getAttribute(attribute_map, "vertical_scrollbar");
            String horizontal_scrollbar = getAttribute(attribute_map, "horizontal_scrollbar");
            String selection_mode = getAttribute(attribute_map, "selection_mode");
            String rowselection = getAttribute(attribute_map, "row_selection");
            String columnselection = getAttribute(attribute_map, "column_selection");
            String cellselection = getAttribute(attribute_map, "cell_selection");
            String focusable = getAttribute(attribute_map, "focusable");
            String autoresize = getAttribute(attribute_map, "auto_resize");
            String grid_color = getAttribute(attribute_map, "grid_color");
            String row_height = getAttribute(attribute_map, "row_height");
            String row_margin = getAttribute(attribute_map, "row_margin");
            String show_grid = getAttribute(attribute_map, "show_grid");
            String show_horizontal_lines = getAttribute(attribute_map, "show_horizontal_lines");
            String show_vertical_lines = getAttribute(attribute_map, "show_vertical_lines");
            Dimension intercellspacing = StringUtil.parseDimension(getAttribute(attribute_map, "cell_spacing"));
            ToolTipManager.sharedInstance().unregisterComponent(table);
            ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());
            readSharedComponentSettings((Component)table, attribute_map);
            Color gridcolor = StringUtil.parseColor(grid_color);
            if ((gridcolor == null) && (colormap.get("table_grid") != null)) gridcolor = getColor("table_grid");
            if ((gridcolor == null) && (colormap.get("default_foreground") != null)) gridcolor = getColor("default_foreground");
            if (gridcolor == null) gridcolor = UIManager.getColor(determineId(table, ".gridColor"));
            table.setGridColor(gridcolor);
            if (intercellspacing != null) {
              Backup(old_table_intercell_dimensions, table, String.valueOf(table.getIntercellSpacing()));
              table.setIntercellSpacing(intercellspacing);
            }
            if (show_grid != null) table.setShowGrid(!StringUtil.isFalse(show_grid));
            if (show_vertical_lines != null) table.setShowVerticalLines(!StringUtil.isFalse(show_vertical_lines));
            if (show_horizontal_lines != null) table.setShowHorizontalLines(!StringUtil.isFalse(show_horizontal_lines));
            if (row_height == null) row_height = (String)sizemap.get("table_row_height");
            if (row_height != null) {
              if (old_table_row_heights != null) Backup(old_table_row_heights, table, String.valueOf(table.getRowHeight()));
              tableRowHeight = Integer.parseInt(row_height);
              //table.setRowHeight(tableRowHeight);
            }
            if (row_margin != null) {
              if (old_table_row_margins != null) Backup(old_table_row_margins, table, String.valueOf(table.getRowMargin()));
              table.setRowMargin(Integer.parseInt(row_margin));
            }
            Color selected_foreground = getAspectColor(table, ".selectionForeground", getAttribute(attribute_map, "selection_foreground"));
            Color selected_background = getAspectColor(table, ".selectionBackground", getAttribute(attribute_map, "selection_background"));
            if (selected_foreground == null) selected_foreground = table.getForeground();
            if (selected_foreground != null) table.setSelectionForeground(selected_foreground);
            if ((selected_background == null)) selected_background = table.getBackground();
            if (selected_background != null) table.setSelectionBackground(selected_background);
            if (table instanceof JComponent) readSharedJComponentSettings((JComponent)table, attribute_map);
            TableConfig tableconfig = new TableConfig();
            readSharedComponentSettings((Component)scrollpane, attribute_map);
            if (scrollpane instanceof JComponent) readSharedJComponentSettings((JComponent)scrollpane, attribute_map);
            if (vertical_scrollbar != null) {
              if (vertical_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
              else if (vertical_scrollbar.equalsIgnoreCase("always")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
              else if (vertical_scrollbar.equalsIgnoreCase("never")) scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            } else scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            if (horizontal_scrollbar != null) {
              if (horizontal_scrollbar.equalsIgnoreCase("as needed")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
              else if (horizontal_scrollbar.equalsIgnoreCase("always")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
              else if (horizontal_scrollbar.equalsIgnoreCase("never")) scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            } else scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            if (rowselection != null) {
              if (StringUtil.isTrue(rowselection)) {
                table.setRowSelectionAllowed(true);
                tableconfig.rowselection = true;
              } else if (StringUtil.isFalse(rowselection)) {
                table.setRowSelectionAllowed(false);
                tableconfig.rowselection = false;
              }
            } else {
  //            table.setRowSelectionAllowed(true);
  //            tableconfig.rowselection = true;
            }
            if (cellselection != null) {
              if (StringUtil.isTrue(cellselection)) {
                table.setCellSelectionEnabled(true);
                tableconfig.cellselection = true;
              } else if (StringUtil.isFalse(cellselection)) {
                table.setCellSelectionEnabled(false);
                tableconfig.cellselection = false;
              }
            } else {
  //            table.setCellSelectionEnabled(false);
  //            tableconfig.cellselection = false;
            }
            if (columnselection != null) {
              if (StringUtil.isTrue(columnselection)) {
                table.setColumnSelectionAllowed(true);
                tableconfig.columnselection = true;
              } else if (StringUtil.isFalse(columnselection)) {
                table.setColumnSelectionAllowed(false);
                tableconfig.columnselection = false;
              }
            } else {
  //            table.setcolumnselectionEnabled(false);
  //            tableconfig.columnselection = false;
            }
            if (autoresize != null) {
              if (StringUtil.isFalse(autoresize)) table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
              else if (autoresize.equalsIgnoreCase("all_columns")) table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
              else if (autoresize.equalsIgnoreCase("last_column")) table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
              else if (autoresize.equalsIgnoreCase("next_column")) table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
              else if (autoresize.equalsIgnoreCase("subsequent_columns")) table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            } else table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            if (focusable != null) {
              if (StringUtil.isTrue(focusable)) {
                scrollpane.setFocusable(true);
                scrollpane.getHorizontalScrollBar().setFocusable(true);
                scrollpane.getVerticalScrollBar().setFocusable(true);
              } else if (StringUtil.isFalse(focusable)) {
                scrollpane.setFocusable(false);
                scrollpane.getHorizontalScrollBar().setFocusable(false);
                scrollpane.getVerticalScrollBar().setFocusable(false);
              }
            }
            tableconfig.selectionmode = table.getSelectionModel().getSelectionMode();
            tableconfig.autoresize = table.getAutoResizeMode();
            tableconfigmap.put(table, tableconfig);
          }
          setConstraints(node, id, (Component)scrollpane, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("spinner")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JSpinner spinner = (JSpinner)getInstance(id, RESpinner.class, null);
          if (!remove && !traverse_only) {
            readSharedComponentSettings((Component)spinner, attribute_map);
            if (spinner instanceof JComponent) readSharedJComponentSettings((JComponent)spinner, attribute_map);
            JFormattedTextField tf = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
            if (tf instanceof JTextComponent) readSharedTextComponentSettings(id, (JTextComponent)tf, attribute_map);
          }
          setConstraints(node, id, (Component)spinner, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("combobox")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JComboBox combobox = (JComboBox)getInstance(id, REComboBox.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_COMBOBOX"))
                  idText = idText.substring(0, idText.length() - 9);
              String tooltipKey = "COMBOBOX_" + idText + "_TOOLTIP";              
            String editable = getAttribute(attribute_map, "editable");
            String popup_enabled = getAttribute(attribute_map, "popup_enabled");
            String selection_foreground = getAttribute(attribute_map, "selection_foreground");
            String selection_background = getAttribute(attribute_map, "selection_background");
            readSharedComponentSettings((Component)combobox, attribute_map);
            if (combobox instanceof JComponent) readSharedJComponentSettings((JComponent)combobox, attribute_map);
            if ((editable != null) && (StringUtil.isTrue(editable))) combobox.setEditable(true);
            else combobox.setEditable(false);

            
            /*
             * these lines caused comboboxes to be fucked up when moving from substance to default skins, not sure if the lines
             * are even needed...
//          ComboBoxEditor tf = (combobox.getEditor())getTextField();
            Color sforeground = getAspectColor(combobox, ".selectionForeground", selection_foreground);
            if (sforeground != null) {
                try {
                    combobox.getEditor().getEditorComponent().setForeground(sforeground);
                } catch (Exception e) { }
            }
            Color sbackground = getAspectColor(combobox, ".selectionBackground", selection_background);
            if (sbackground != null) {
                try {
                    combobox.getEditor().getEditorComponent().setBackground(sbackground);
                } catch (Exception e) { }
            }
            */
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                combobox.setToolTipText(tooltip);
            else
                combobox.setToolTipText(null);
            
            if ((popup_enabled != null) && (StringUtil.isFalse(popup_enabled))) combobox.setLightWeightPopupEnabled(false);
            else combobox.setLightWeightPopupEnabled(true);
            combobox_hash.put(id, combobox);
            if (removeTextValues) {
                removeAttribute(attribute_map, "tooltip");
            }            
            if (writer != null) {
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }                                                
          }
          setConstraints(node, id, (Component)combobox, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("progressbar")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JProgressBar progressBar = (JProgressBar)getInstance(id, JProgressBar.class, null);
          if (!remove && !traverse_only) {
            String border_painted = getAttribute(attribute_map, "border_painted");
            String orientation = getAttribute(attribute_map, "orientation");
            String string = getAttribute(attribute_map, "string");
            String string_painted = getAttribute(attribute_map, "string_painted");
            readSharedComponentSettings((Component)progressBar, attribute_map);
            if (progressBar instanceof JComponent) readSharedJComponentSettings((JComponent)progressBar, attribute_map);
            if ((border_painted != null) && StringUtil.isFalse(border_painted)) progressBar.setBorderPainted(false);
            else progressBar.setBorderPainted(true);
            if ((orientation != null) && orientation.equalsIgnoreCase("vertical")) progressBar.setOrientation(JProgressBar.VERTICAL);
            else progressBar.setOrientation(JProgressBar.HORIZONTAL);
            if (string != null) progressBar.setString(string);
            else progressBar.setString("");
            if ((string_painted != null) && (StringUtil.isTrue(string_painted))) progressBar.setStringPainted(true);
            else progressBar.setStringPainted(false);
          }
          setConstraints(node, id, (Component)progressBar, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("button")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JButton button = (JButton)getInstance(id, REButton.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_BUTTON"))
                  idText = idText.substring(0, idText.length() - 7);
              String textKey = "BUTTON_" + idText + "_TEXT";
              String altTextKey = "BUTTON_" + idText + "_ALTTEXT";
              String tooltipKey = "BUTTON_" + idText + "_TOOLTIP";
            String alttext = getString(altTextKey);
            String text = getString(textKey);
            
            buttonmap.put(id, button);
            if (REButton.getCurrentButtonType() == REButton.BUTTON_TYPE_LIQUID)
                button.setBorder(BorderFactory.createEmptyBorder());
            else
                button.setBorder(new JButton().getBorder());
            readSharedComponentSettings((Component)button, attribute_map);
            if (button instanceof JComponent) readSharedJComponentSettings((JComponent)button, attribute_map);
            if (button instanceof AbstractButton) readSharedAbstractButtonSettings((AbstractButton)button, attribute_map);
            if (alttext != null) {
              altmap.put(button, alttext);
              textmap.put(button, text);
            }
            if (text != null)
                button.setText(text);
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                button.setToolTipText(tooltip);
            else
                button.setToolTipText(null);
            if (removeTextValues) {
                removeAttribute(attribute_map, "text");
                removeAttribute(attribute_map, "alt_text");
                removeAttribute(attribute_map, "tooltip");
            }              
            if (writer != null) {
                if (text != null)
                    writer.writeLine(textKey + "=" + text);
                if (alttext != null) 
                    writer.writeLine(altTextKey + "=" + alttext);
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }            
            
            //button.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
          }
          setConstraints(node, id, (Component)button, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("radiobutton")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JRadioButton button = (JRadioButton)getInstance(id, JRadioButton.class, null);
          if (!remove && !traverse_only) {
              String idText = id.toUpperCase();
              if (idText.endsWith("_RADIOBUTTON"))
                  idText = idText.substring(0, idText.length() - 12);
              String textKey = "RADIOBUTTON_" + idText + "_TEXT";
              String tooltipKey = "RADIOBUTTON_" + idText + "_TOOLTIP";
            String setBorderPainted = getAttribute(attribute_map, "border_painted");
            readSharedComponentSettings((Component)button, attribute_map);
            if (button instanceof JComponent) readSharedJComponentSettings((JComponent)button, attribute_map);
            if (button instanceof AbstractButton) readSharedAbstractButtonSettings((AbstractButton)button, attribute_map);
            if ((setBorderPainted != null) && StringUtil.isTrue(setBorderPainted)) button.setBorderPainted(true);
            else button.setBorderPainted(false);        
            String text = getString(textKey);
            if (text != null)
                button.setText(text);            
            String tooltip = getString(tooltipKey);
            if ((tooltip != null) && !tooltip.equals(""))
                button.setToolTipText(tooltip);
            else
                button.setToolTipText(null);
            if (removeTextValues) {
                removeAttribute(attribute_map, "text");
                removeAttribute(attribute_map, "tooltip");
            }              
            if (writer != null) {
                if (button.getText() != null)
                    writer.writeLine(textKey + "=" + button.getText());
                if ((tooltip != null) && !tooltip.equals(""))
                    writer.writeLine(tooltipKey + "=" + tooltip);
            }            
            
            radiobutton_hash.put(id, button);
          }
          setConstraints(node, id, (Component)button, parentComponent, position, namespace, remove, traverse_only);
        }
      } else if (node.getNodeName().equalsIgnoreCase("tabbedpanel")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        if (id != null) {
          JTabbedPane tabbedpanel = (JTabbedPane)getInstance(id, JTabbedPane.class, null);
          if (!remove && !traverse_only) {
            String default_index = getAttribute(attribute_map, "default_index");
            String tab_placement = getAttribute(attribute_map, "tab_placement");
            readSharedComponentSettings((Component)tabbedpanel, attribute_map);
            if (tabbedpanel instanceof JComponent) readSharedJComponentSettings((JComponent)tabbedpanel, attribute_map);
            if (tab_placement != null) {
              if (tab_placement.equalsIgnoreCase("top")) tabbedpanel.setTabPlacement(JTabbedPane.TOP);
              if (tab_placement.equalsIgnoreCase("bottom")) tabbedpanel.setTabPlacement(JTabbedPane.BOTTOM);
              if (tab_placement.equalsIgnoreCase("right")) tabbedpanel.setTabPlacement(JTabbedPane.RIGHT);
              if (tab_placement.equalsIgnoreCase("left")) tabbedpanel.setTabPlacement(JTabbedPane.LEFT);
            } else tabbedpanel.setTabPlacement(JTabbedPane.TOP);
            if (default_index != null) {
              try {
                tabbedpanel.setSelectedIndex(Integer.parseInt(default_index));
              } catch (Exception e) { }
            }
          }
          setConstraints(node, id, (Component)tabbedpanel, parentComponent, position, namespace, remove, traverse_only);
          NodeList childnodes = node.getChildNodes();
          int tabindex = 0;
          for (int cn = 0; cn < childnodes.getLength(); ++cn) {
            Node childnode = childnodes.item(cn);
            if (childnode.getNodeName().equalsIgnoreCase("tab")) {
              JPanel tab_panel;
              if (remove) {
                tab_panel = (JPanel)tabbedpanel.getComponentAt(0);
                tabbedpanel.removeTabAt(0);
              } else {
                NamedNodeMap child_map = childnode.getAttributes();
                String tab_id = getAttribute(child_map, "id");
                String tabKey = "TAB_" + tab_id.toUpperCase();
                String tab_title = getString(tabKey);
                tab_panel = new JPanel();
                if (removeTextValues) {
                    removeAttribute(child_map, "title");
                }              
                if (writer != null) {
                    writer.writeLine(tabKey + "=" + tab_title);
                }                            
                tab_panel.setLayout(new MyRelativeLayout());
                readSharedComponentSettings((Component)tab_panel, child_map);
                if (tab_panel instanceof JComponent) readSharedJComponentSettings((JComponent)tab_panel, child_map);
                tabbedpanel.addTab(tab_title, tab_panel);
                String tooltip = getAttribute(child_map, "tooltip");
                if ((tooltip != null) && !tooltip.equals("")) tabbedpanel.setToolTipTextAt(tabindex, tooltip);                
                else tabbedpanel.setToolTipTextAt(tabindex, null);
                String iconstr = getAttribute(child_map, "icon");
                if (iconstr != null) {
                  try {
                    ImageIcon icon = getImageIcon(skinpath + iconstr, (tab_title == null) ? "" : tab_title);
                    tabbedpanel.setIconAt(tabindex,icon);
                  } catch (Exception e) { log.error("RecurseLoad(): error", e); }
                } else tabbedpanel.setIconAt(tabindex,null);
                String disablediconstr = getAttribute(child_map, "disabled_icon");
                if (disablediconstr != null) {
                  try {
                    ImageIcon disabledicon = getImageIcon(skinpath + disablediconstr, (tab_title == null) ? "" : tab_title);
                    tabbedpanel.setDisabledIconAt(tabindex,disabledicon);
                  } catch (Exception e) { log.error("RecurseLoad(): error", e); }
                } else tabbedpanel.setDisabledIconAt(tabindex,null);
                String mnemonic = getAttribute(child_map, "mnemonic_index");
                if (mnemonic != null) tabbedpanel.setDisplayedMnemonicIndexAt(tabindex, Integer.parseInt(mnemonic));
                else tabbedpanel.setDisplayedMnemonicIndexAt(tabindex, -1);
                tabindex++;
              }
              NodeList splitnodes = childnode.getChildNodes();
              RecurseLoad(splitnodes, (Component)tab_panel, Position.NONE, namespace, remove, traverse_only);
            }
          }
        }
      } else if (node.getNodeName().equalsIgnoreCase("splitpanel")) {
        NamedNodeMap attribute_map = node.getAttributes();
        String id = getFullId(namespace, getAttribute(attribute_map, "id"));
        String divider_location_str = getAttribute(attribute_map, "divider_location");
        if (id != null) {
            if (remove) log.debug("RecurseLoad(): removing splitpanel: " + id);
            else log.debug("RecurseLoad(): adding splitpanel: " + id);            
          JSplitPane splitpanel = (JSplitPane)getInstance(id, RESplitPanel.class, null);
          if (remove && !traverse_only) {
//            imap.remove(id);
          } else  {
            String resize_weight = getAttribute(attribute_map, "resize_weight");
            String one_touch_expand = getAttribute(attribute_map, "one_touch_expand");
            String continuous_layout = getAttribute(attribute_map, "continuous_layout");
            readSharedComponentSettings((Component)splitpanel, attribute_map);
            if (splitpanel instanceof JComponent) readSharedJComponentSettings((JComponent)splitpanel, attribute_map);
            if ((continuous_layout != null) && StringUtil.isFalse(continuous_layout)) splitpanel.setContinuousLayout(false);
            else splitpanel.setContinuousLayout(true);

//            uidefaults.put("SplitPane.background", new ColorUIResource(Color.GREEN));
//            uidefaults.put("SplitPane.shadow", new ColorUIResource(Color.RED));
//            uidefaults.put("SplitPane.darkShadow", new ColorUIResource(Color.BLUE));
//            uidefaults.put("SplitPane.highlight", new ColorUIResource(Color.WHITE));

            splitpanel_hash.put(id, splitpanel);
            if (resize_weight != null) splitpanel.setResizeWeight(Double.parseDouble(resize_weight));
            else splitpanel.setResizeWeight(0.0);
            if ((one_touch_expand != null) && (StringUtil.isTrue(one_touch_expand))) splitpanel.setOneTouchExpandable(true);
            else splitpanel.setOneTouchExpandable(false);
          }
          setConstraints(node, id, (Component)splitpanel, parentComponent, position, namespace, remove, traverse_only);
          boolean vertical = false;
          NodeList childnodes = node.getChildNodes();
          for (int cn = 0; cn < childnodes.getLength(); ++cn) {
            Node childnode = childnodes.item(cn);
            if (childnode.getNodeName().equalsIgnoreCase("split-top")) {
              NodeList splitnodes = childnode.getChildNodes();              
              RecurseLoad(splitnodes, (Component)splitpanel, Position.TOP, namespace, remove, traverse_only);
              splitpanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
              vertical = true;
            } else if (childnode.getNodeName().equalsIgnoreCase("split-bottom")) {
              NodeList splitnodes = childnode.getChildNodes();
              RecurseLoad(splitnodes, (Component)splitpanel, Position.BOTTOM, namespace, remove, traverse_only);
              splitpanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
              vertical = true;
            } else if (childnode.getNodeName().equalsIgnoreCase("split-left")) {
              NodeList splitnodes = childnode.getChildNodes();
              RecurseLoad(splitnodes, (Component)splitpanel, Position.LEFT, namespace, remove, traverse_only);
              splitpanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            } else if (childnode.getNodeName().equalsIgnoreCase("split-right")) {
              NodeList splitnodes = childnode.getChildNodes();
              RecurseLoad(splitnodes, (Component)splitpanel, Position.RIGHT, namespace, remove, traverse_only);
              splitpanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            }
          }
          if (!remove) {
            if (divider_location_str != null) {
              int divide_val = Integer.parseInt(divider_location_str);
//            if (divide_val < 0) {
//              if (vertical) divide_val = (int)(Math.abs(splitpanel.getHeight() + divide_val));
//              else divide_val = (int)(Math.abs(splitpanel.getWidth() + divide_val));
//            }
              splitpanel.setDividerLocation(divide_val);
              splitpanelDividers.put(splitpanel, new Integer(divide_val));
            }
          }
        }
      }
      if (thisComponent != null) RecurseLoad(node.getChildNodes(), thisComponent, Position.NONE, namespace, remove, traverse_only);
    }
  }

  public String getTextFor(Object obj) { return (String)textmap.get(obj); }
  public String getAltTextFor(Object obj) { return (String)altmap.get(obj); }

  private void setAlignmentX(JComponent component, String align_x) {
    if (component == null) return;
    if (align_x != null) {
      float align_val = -1.0f;
      if (align_x.equalsIgnoreCase("left")) align_val = Component.LEFT_ALIGNMENT;
      else if (align_x.equalsIgnoreCase("center")) align_val = Component.CENTER_ALIGNMENT;
      else if (align_x.equalsIgnoreCase("right")) align_val = Component.RIGHT_ALIGNMENT;
      else {
        try {
          float val = Float.parseFloat(align_x);
          if ((val >= 0.0) && (val <= 1.0)) align_val = val;
        } catch (Exception e) { }
      }
      if (align_val != -1.0f) {
        if (component instanceof JScrollPane) {
          JScrollPane scrollpane = (JScrollPane)component;
          JPanel panel = getParentPanel(scrollpane);
          panel.setAlignmentX(align_val);
        }  else if (component instanceof JPanel) {
          JPanel panel = (JPanel)component;
          panel.setAlignmentX(align_val);
        }  else if (component instanceof JCheckBox) {
          JCheckBox checkbox = (JCheckBox)component;
          checkbox.setAlignmentX(align_val);
        }
      }
    }
  }

  private void setAlignmentY(JComponent component, String align_y) {
    if (component == null) return;
    if (align_y != null) {
      float align_val = -1.0f;
      if (align_y.equalsIgnoreCase("top")) align_val = Component.TOP_ALIGNMENT;
      else if (align_y.equalsIgnoreCase("center")) align_val = Component.CENTER_ALIGNMENT;
      else if (align_y.equalsIgnoreCase("bottom")) align_val = Component.BOTTOM_ALIGNMENT;
      else {
        try {
          float val = Float.parseFloat(align_y);
          if ((val >= 0.0) && (val <= 1.0)) align_val = val;
        } catch (Exception e) { }
      }
      if (align_val != -1.0f) {
        if (component instanceof JScrollPane) {
          JScrollPane scrollpane = (JScrollPane)component;
          JPanel panel = getParentPanel(scrollpane);
          panel.setAlignmentY(align_val);
        }  else if (component instanceof JPanel) {
          JPanel panel = (JPanel)component;
          panel.setAlignmentY(align_val);
        }  else if (component instanceof JCheckBox) {
          JCheckBox checkbox = (JCheckBox)component;
          checkbox.setAlignmentY(align_val);
        }
      }
    }
  }

  static class Position {
    static public int NONE = 0;
    static public int LEFT = 1;
    static public int TOP = 2;
    static public int RIGHT = 3;
    static public int BOTTOM = 4;
  };

  public Object getInstance(String id, Class classtype, Component parent) {
    try {
      Object obj = imap.get(id);
      if (obj != null) return obj;
      if (classtype.equals(JScrollPane.class)) {
        JScrollPane sp = new JScrollPane(parent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollhash.put(sp, parent);
        obj = sp;
      } else if ((classtype.equals(IDialog.class)) || (classtype.equals(JDialog.class))) {
        JDialog dialog = null;
        if (parent instanceof JFrame) {
          if (usedefaultborders)
            dialog = new JDialog((JFrame)parent, false);
         else dialog = new IDialog((JFrame)parent, false);
        } else {
          if (usedefaultborders)
           dialog = new JDialog((JDialog)parent, false);
         else dialog = new IDialog((JDialog)parent, false);
        }
        if (REDialog.getREDialog(id) == null) REDialog.instances.put(id, new REDialog(id));
        parentmap.put(dialog, parent);
        obj = dialog;
      } else obj = classtype.newInstance();
      imap.put(id, obj);
      return obj;
    } catch (Exception e) { log.error("getInstance(): error", e); }
    return null;
  }

  public Object getParent(Object child) { return parentmap.get(child); }

  private String getAttribute(NamedNodeMap nodemap, String attribute_name) {
      if (nodemap != null) {
          Node attribute_node = nodemap.getNamedItem(attribute_name);
          return (attribute_node == null) ? null : attribute_node.getNodeValue();
      }
      return null;
  }
  private void removeAttribute(NamedNodeMap nodemap, String attribute_name) {
      try {
          nodemap.removeNamedItem(attribute_name);
      } catch (Exception e) { }      
  }
  
  private boolean setConstraints(Node node, String id, Component component, Component parent, int position, String namespace, boolean remove, boolean traverse_only) {
      if (traverse_only) return true;
    boolean returnval = false;
    if (remove) {
      removeFromParent(id, component, parent, position);
      return true;
    }
    addToParent(id, component, parent, position);
    NodeList childnodes = node.getChildNodes();
    if (childnodes == null) return false;
    for (int cn = 0; cn < childnodes.getLength(); ++cn) {
      Node childnode = childnodes.item(cn);
      if (childnode.getNodeName().equalsIgnoreCase("condition")) {
        conditions.add(new Condition(childnode, id, component, parent, position, namespace));
        removeFromParent(component, parent);
        if (setdefaults) {
          NodeList conditionnodes = childnode.getChildNodes();
          for (int cndn = 0; cndn < conditionnodes.getLength(); ++cndn) {
            Node conditionnode = conditionnodes.item(cndn);
            if (StringUtil.isFalse(conditionnode.getNodeName()))  {
              setConstraints(conditionnode, id, component, parent, position, namespace, false, traverse_only);
            }
          }
        }
      } else if (childnode.getNodeName().equalsIgnoreCase("location")) {
        NodeList constraints = childnode.getChildNodes();
        for (int i = 0; i < constraints.getLength(); ++i) {
          Node constraint = constraints.item(i);
          if (constraint.getNodeName().equalsIgnoreCase("constraint")) {
            NamedNodeMap attr = constraint.getAttributes();
            String attribute = getAttribute(attr, "attribute");
            String relative_id = getFullId(namespace, getAttribute(attr, "relative_id"));
            if ((relative_id == null) || (relative_id.equalsIgnoreCase("root"))) relative_id = DependencyManager.ROOT_NAME;
            String relative_attribute = getAttribute(attr, "relative_attribute");
            String relative_position = getAttribute(attr, "relative_position");
            getLayout(parent).addConstraint(id, translateAttribute(attribute), new AttributeConstraint(relative_id, translateAttribute(relative_attribute), Integer.parseInt(relative_position)));
            returnval = true;
          }
        }
      }
    }
    return returnval;
  }

  private void addToParent(String id, Component component, Component parent, int position) {
    if (parent instanceof IFrame) {
      IFrame parentFrame = (IFrame)parent;
      parentFrame.getIContentPane().add(component, id);
    } else if (parent instanceof IDialog) {
      IDialog parentDialog = (IDialog)parent;
      parentDialog.getIContentPane().add(component, id);
    } else if (parent instanceof JFrame) {
      JFrame parentFrame = (JFrame)parent;
      parentFrame.getContentPane().add(component, id);
    } else if (parent instanceof JDialog) {
      JDialog parentDialog = (JDialog)parent;
      parentDialog.getContentPane().add(component, id);
    } else if (parent instanceof JSplitPane) {
      JSplitPane parentSplit = (JSplitPane)parent;
      if (position == Position.LEFT){
          log.debug("addToParent(): setting left split panel component, id: " + id);
          parentSplit.setLeftComponent(component);
      }
      else if (position == Position.RIGHT) {
          log.debug("addToParent(): setting right split panel component, id: " + id);
          parentSplit.setRightComponent(component);
      }
      else if (position == Position.TOP) {
          log.debug("addToParent(): setting top split panel component, id: " + id);
          parentSplit.setTopComponent(component);
      }
      else if (position == Position.BOTTOM) {
          log.debug("addToParent(): setting bottom split panel component, id: " + id);
          parentSplit.setBottomComponent(component);
      }
    } else if (parent instanceof MoreInfoPanel) {
        MoreInfoPanel parentPanel = (MoreInfoPanel)parent;
        parentPanel.setBottomComponent(component);        
    } else if (parent instanceof JPanel) {
      JPanel parentPanel = (JPanel)parent;
      parentPanel.add(component, id);
    } else if (parent instanceof JScrollPane) {
      JScrollPane scrollpane = (JScrollPane)parent;
      JPanel panel = getParentPanel(scrollpane);
      panel.add(component, id);
    }
  }

  private void removeFromParent(String id, Component component, Component parent, int position) {
    if (parent instanceof IFrame) {
      IFrame parentFrame = (IFrame)parent;
      parentFrame.getIContentPane().remove(component);
    } else if (parent instanceof IDialog) {
      IDialog parentDialog = (IDialog)parent;
      parentDialog.getIContentPane().remove(component);
    } else if (parent instanceof JFrame) {
      JFrame parentFrame = (JFrame)parent;
      parentFrame.getContentPane().remove(component);
    } else if (parent instanceof JDialog) {
      JDialog parentDialog = (JDialog)parent;
      parentDialog.getContentPane().remove(component);
    } else if (parent instanceof JSplitPane) {
      JSplitPane parentSplit = (JSplitPane)parent;
      if (position == Position.LEFT) {
          log.debug("removeFromParent(): removing left split panel component, id: " + id);
          parentSplit.setLeftComponent(null);
      }
      else if (position == Position.RIGHT) {
          log.debug("removeFromParent(): removing right split panel component, id: " + id);
          parentSplit.setRightComponent(null);
      }
      else if (position == Position.TOP) {
          log.debug("removeFromParent(): removing top split panel component, id: " + id);
          parentSplit.setTopComponent(null);
      }
      else if (position == Position.BOTTOM) {
          log.debug("removeFromParent(): removing bottom split panel component, id: " + id);
          parentSplit.setBottomComponent(null);
      }
      parentSplit.remove(component);
    } else if (parent instanceof MoreInfoPanel) {
        MoreInfoPanel parentPanel = (MoreInfoPanel)parent;
        parentPanel.removeBottomComponent(component);
    } else if (parent instanceof JPanel) {
      JPanel parentPanel = (JPanel)parent;
      parentPanel.remove(component);
    } else if (parent instanceof JScrollPane) {
      JScrollPane scrollpane = (JScrollPane)parent;
      JPanel panel = getParentPanel(scrollpane);
      panel.remove(component);
    }
  }

  private void removeFromParent(Component component, Component parent) {
    if (parent instanceof IFrame) {
      IFrame parentFrame = (IFrame)parent;
      parentFrame.getIContentPane().remove(component);
    } else if (parent instanceof IDialog) {
      IDialog parentDialog = (IDialog)parent;
      parentDialog.getIContentPane().remove(component);
    } else if (parent instanceof JFrame) {
      JFrame parentFrame = (JFrame)parent;
      parentFrame.getContentPane().remove(component);
    } else if (parent instanceof JDialog) {
      JDialog parentDialog = (JDialog)parent;
      parentDialog.getContentPane().remove(component);
    } else if (parent instanceof JSplitPane) {
      JSplitPane parentSplit = (JSplitPane)parent;
      parentSplit.remove(component);
    } else if (parent instanceof MoreInfoPanel) {
      MoreInfoPanel parentPanel = (MoreInfoPanel)parent;
      parentPanel.removeBottomComponent(component);
    } else if (parent instanceof JPanel) {
      JPanel parentPanel = (JPanel)parent;
      parentPanel.remove(component);
    } else if (parent instanceof JScrollPane) {
      JScrollPane scrollpane = (JScrollPane)parent;
      JPanel panel = getParentPanel(scrollpane);
      panel.remove(component);
    }
  }

  private MyRelativeLayout getLayout(Component component) {
    if (component instanceof IFrame) {
      IFrame parentFrame = (IFrame)component;
      MyRelativeLayout layout = (MyRelativeLayout)parentFrame.getIContentPane().getLayout();
      return layout;
    } else if (component instanceof JFrame) {
      JFrame parentFrame = (JFrame)component;
      MyRelativeLayout layout = (MyRelativeLayout)parentFrame.getContentPane().getLayout();
      return layout;
    } else if (component instanceof JSplitPane) {
      JSplitPane parentSplit = (JSplitPane)component;
      MyRelativeLayout layout = (MyRelativeLayout)parentSplit.getLayout();
      return layout;
    } else if (component instanceof MoreInfoPanel) {
        return null;
    } else if (component instanceof JPanel) {
      JPanel parentPanel = (JPanel)component;
      MyRelativeLayout layout = (MyRelativeLayout)parentPanel.getLayout();
      return layout;
    } else if (component instanceof IDialog) {
      IDialog parentDialog = (IDialog)component;
      MyRelativeLayout layout = (MyRelativeLayout)parentDialog.getIContentPane().getLayout();
      return layout;
    } else if (component instanceof JDialog) {
      JDialog parentDialog = (JDialog)component;
      MyRelativeLayout layout = (MyRelativeLayout)parentDialog.getContentPane().getLayout();
      return layout;
    } else if (component instanceof JScrollPane) {
      JScrollPane scrollPane = (JScrollPane)component;
      JPanel panel = getParentPanel(scrollPane);
      MyRelativeLayout layout = (MyRelativeLayout)panel.getLayout();
      if (layout == null) {
        layout = new MyRelativeLayout();
        panel.setLayout(layout);
      }
      return layout;
    }
    return null;
  }

  private JPanel getParentPanel(JScrollPane scroll) {
    return (JPanel)scrollhash.get(scroll);
  }
  public JScrollPane getScrollPanel(JTable table) {
    return (JScrollPane)scrollhash.get(table);
  }
  static public void addScroll(Object obj, JScrollPane scrollpane) {
    scrollhash.put(obj, scrollpane);
  }

  private AttributeType translateAttribute(String val) {
    if (val == null) return null;
    if (val.equalsIgnoreCase("top")) return AttributeType.TOP;
    if (val.equalsIgnoreCase("left")) return AttributeType.LEFT;
    if (val.equalsIgnoreCase("right")) return AttributeType.RIGHT;
    if (val.equalsIgnoreCase("bottom")) return AttributeType.BOTTOM;
    if (val.equalsIgnoreCase("horizontal_center")) return AttributeType.HORIZONTAL_CENTER;
    if (val.equalsIgnoreCase("vertical_center")) return AttributeType.VERTICAL_CENTER;
    return null;
  }

  public boolean loadSkin() {
    return loadSkin(current_skin_filename);
  }

  private File skinfile = null;
  private String skinpath = null;
  private boolean storedefaults;

  public boolean isChanging() {
    return ischanging;
  }

  boolean ischanging = false;

  private Map splitpanelDividers = new HashMap();
  
  static private TextFileWriter writer = null;
  
  public boolean loadSkin(String filename) {
    boolean returnval = false;
    try {
        if (createTextBundle) {
            writer = new TextFileWriter("textBundle.properties");
        }
      storedefaults = true;

      splitpanelDividers.clear();      
      
      // the custom popup menu ui shadows were kind of lame and substance l&fs provide shaded ones
      // so it became obsolete..
      //UIManager.put("PopupMenuUI", "com.mixshare.rapid_evolution.ui.swing.popupmenu.CustomPopupMenuUI");
            
      current_skin_filename = filename;
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      skinfile = new File(filename);
      skinpath = skinfile.getAbsolutePath().substring(0, skinfile.getAbsolutePath().indexOf(skinfile.getName()));
      log.debug("loadSkin(): skin path: " + skinpath);
      Document document = builder.parse(skinfile);
      Element elem = document.getDocumentElement();
      String redirectPath = elem.getAttribute("redirect");
      if ((redirectPath != null) && !redirectPath.equals("")) {
          return loadSkin(redirectPath);
      }      
      skinname = elem.getAttribute("title");
      author = elem.getAttribute("author");
      locale = elem.getAttribute("locale");
      look_feel = elem.getAttribute("look_feel");
      if (locale == null) locale = "en";
      description = elem.getAttribute("description");
      log.info("Loading Skin: " + skinname + ", Author: " + author + ", Locale: " + locale);
      Locale.setDefault(new Locale(locale));
      root_nodelist = elem.getChildNodes();
      RecurseLoad(root_nodelist, null, Position.NONE, "", false, false);
      
      if (removeTextValues)
          rapid_evolution.Database.writeXmlFile(document, filename + ".modified.xml");
      
      OptionsUI.instance.skin_title_field.setText(skinname);
      OptionsUI.instance.skin_author_field.setText(author);
      OptionsUI.instance.skin_locale_field.setText(locale);
      OptionsUI.instance.skin_description_field.setText(description);
      OptionsUI.instance.version.setText("rapid evolution " + RapidEvolution.versionString);
      OptionsUI.instance.programmedby.setText("created by jesse bickmore");
      OptionsUI.instance.email.setText("contact: qualia@mixshare.com");
      OptionsUI.instance.web.setText("www.mixshare.com");
      
      if ((look_feel != null) && !look_feel.equals("") && !skinname.equals(lastSkinName)) {
          if (log.isTraceEnabled())
              log.trace("loadSkin(): look and feel set in skin=" + look_feel + ", skinname=" + skinname + ", lastSkinname=" + lastSkinName);
          LookAndFeelManager.setLookAndFeel(look_feel, false);
          OptionsUI.instance.lookandfeelcombo.removeActionListener(OptionsUI.instance);
          for (int i = 0; i < OptionsUI.instance.lookandfeelcombo.getItemCount(); ++i) {
              String comboItem = OptionsUI.instance.lookandfeelcombo.getItemAt(i).toString();
              if (comboItem.equals(look_feel)) {
                  OptionsUI.instance.lookandfeelcombo.setSelectedIndex(i);
                  if (log.isTraceEnabled())
                      log.trace("loadSkin(): setting selected index=" + i);
              }
          }
          OptionsUI.instance.lookandfeelcombo.addActionListener(OptionsUI.instance);
//          OptionsUI.instance.lookandfeelcombo.setEnabled(false);
          LookAndFeelManager.saveLookAndFeel(look_feel);
      } else {
//          OptionsUI.instance.lookandfeelcombo.setEnabled(true);
      }
      
      populateColorTypeIdMap();
      updateComponents();
      
      storedefaults = false;
            
      RatingToolBarFlyWeight.resetIcons();      
      
      Iterator splits = splitpanelDividers.entrySet().iterator();
      while (splits.hasNext()) {
          Entry split = (Entry)splits.next();
          JSplitPane spane = (JSplitPane)split.getKey();
          Integer val = (Integer)split.getValue();
          spane.setDividerLocation(val.intValue());
      }
                  
      lastSkinName = skinname;
      
      if (writer != null) {
          writer.close();
      }
      
      returnval = true;
    } catch (Exception x) {
      // Error generated during parsing
          log.error("loadSkin(): error", x);
       JOptionPane.showMessageDialog(null,
          x.getMessage(),
         "error loading skin",
         JOptionPane.ERROR_MESSAGE);
      // browse
      JFileChooser fc = new com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser();
      fc.setDialogTitle("Select a skin");
      fc.setCurrentDirectory(new File(".\\skins"));
      fc.addChoosableFileFilter(new SkinFileFilter());
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.setMultiSelectionEnabled(false);
      int returnVal = fc.showOpenDialog(null);
      File tmp = fc.getSelectedFile();
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        String filestr = (String)tmp.getAbsolutePath();
        return loadSkin(filestr);
      } else {
        RapidEvolution.instance.Exit();
      }
    }

    return returnval;
  }

  private void updateMenuItems(Object object) {
    Field[] fields = object.getClass().getFields();
    for (int i = 0; i < fields.length; ++i) {
      try {
        Object field = fields[i].get(object);
        if (field instanceof JMenuItem) {
          JMenuItem menuitem = (JMenuItem) field;
          menuitem.setFont(UIManager.getFont("MenuItem.font"));
          menuitem.setBackground(UIManager.getColor("MenuItem.background"));
          menuitem.setForeground(UIManager.getColor("MenuItem.foreground"));
          SwingUtilities.updateComponentTreeUI(menuitem);
        }
        else if (field instanceof JMenu) {
          JMenu menu = (JMenu) field;
          menu.setFont(UIManager.getFont("Menu.font"));
          menu.setBackground(UIManager.getColor("Menu.background"));
          menu.setForeground(UIManager.getColor("Menu.foreground"));
          SwingUtilities.updateComponentTreeUI(menu);
        }
        else if (field instanceof JPopupMenu) {
          JPopupMenu popupmenu = (JPopupMenu) field;
/*          Color color = null;
          if (colormap.containsKey("menu_border")) color = getColor("menu_border");
          if ((color == null) && colormap.containsKey("menu_accelerator_foreground")) color = getColor("menu_accelerator_foreground");
          if (color == null) color = getColor("Menu.acceleratorForeground");
          if (color != null) {
            if (popupmenu.getBorder() instanceof LineBorder) {
              LineBorder b = (LineBorder) popupmenu.getBorder();
              popupmenu.setBorder(new LineBorder(color, b.getThickness(), b.getRoundedCorners()));
            } else if (popupmenu.getBorder() instanceof MatteBorder) {
              MatteBorder b = (MatteBorder) popupmenu.getBorder();
              if (b.getTileIcon() == null)
                 popupmenu.setBorder(new MatteBorder(b.getBorderInsets(), color));
             } else if (popupmenu.getBorder() instanceof PopupMenuBorder) {
               PopupMenuBorder b = (PopupMenuBorder) popupmenu.getBorder();
               popupmenu.setBorder(new PopupMenuBorder());
             }
          }
 */
          popupmenu.setFont(UIManager.getFont("PopupMenu.font"));
          popupmenu.setBackground(UIManager.getColor("PopupMenu.background"));
          popupmenu.setForeground(UIManager.getColor("PopupMenu.foreground"));
          SwingUtilities.updateComponentTreeUI(popupmenu);
        }
      } catch (Exception e) {
        log.error("updateMenuItems(): error", e);
      }
    }
  }

  public Object getObject(String id) { return imap.get(id); }

  private String default_font_name;
  private int default_font_style;
  private int default_font_size;

  public SkinManager() {
    instance = this;
    initUIDefaults();
    setImap();

    Font defaultfont = UIManager.getFont("TextField.font");
    default_font_name = defaultfont.getName();
    default_font_style = defaultfont.getStyle();
    default_font_size = defaultfont.getSize();

    addToUImap("Frame.background", "frame_background");
    addToUImap("Frame.foreground", "frame_foreground");
    addToUImap("Dialog.background", "dialog_background");
    addToUImap("Dialog.foreground", "dialog_foreground");
    addToUImap("controlLtHighlight", "control_light_highlight");
    addToUImap("controlHighlight", "control_highlight");

    // colors
    convertAndAddTypeMap("TextField.background");
    convertAndAddTypeMap("TextField.foreground");
    convertAndAddTypeMap("SplitPane.background");
    convertAndAddTypeMap("SplitPane.foreground");
    convertAndAddTypeMap("OptionPane.questionDialog.border.background");
    convertAndAddTypeMap("OptionPane.errorDialog.titlePane.foreground");
    convertAndAddTypeMap("ScrollBar.highlight");
    convertAndAddTypeMap("ToggleButton.highlight");
    convertAndAddTypeMap("EditorPane.background");
    convertAndAddTypeMap("RadioButton.light");
    convertAndAddTypeMap("Button.highlight");
    convertAndAddTypeMap("FormattedTextField.background");
    convertAndAddTypeMap("TextField.light");
    convertAndAddTypeMap("Tree.background");
    convertAndAddTypeMap("TabbedPane.selectHighlight");
    convertAndAddTypeMap("ToggleButton.light");
    convertAndAddTypeMap("Button.light");
    convertAndAddTypeMap("TextPane.background");
    convertAndAddTypeMap("TextField.highlight");
    convertAndAddTypeMap("ToolBar.highlight");
    convertAndAddTypeMap("TextArea.background");
    convertAndAddTypeMap("Separator.highlight");
    convertAndAddTypeMap("PasswordField.background");
    convertAndAddTypeMap("MenuBar.highlight");
    convertAndAddTypeMap("SplitPane.highlight");
    convertAndAddTypeMap("window");
    convertAndAddTypeMap("Table.focusCellBackground");
    convertAndAddTypeMap("Slider.highlight");
    convertAndAddTypeMap("Tree.textBackground");
    convertAndAddTypeMap("TabbedPane.highlight");
    convertAndAddTypeMap("List.background");
    convertAndAddTypeMap("InternalFrame.borderHighlight");
    convertAndAddTypeMap("RadioButton.highlight");
    convertAndAddTypeMap("ComboBox.buttonHighlight");
    convertAndAddTypeMap("ToolBar.light");
    convertAndAddTypeMap("Table.background");
    convertAndAddTypeMap("text");
    convertAndAddTypeMap("Separator.background");
    convertAndAddTypeMap("InternalFrame.borderLight");
    convertAndAddTypeMap("OptionPane.warningDialog.titlePane.background");
    convertAndAddTypeMap("OptionPane.errorDialog.titlePane.background");
    convertAndAddTypeMap("Tree.selectionBackground");
    convertAndAddTypeMap("ToolBar.floatingForeground");
    convertAndAddTypeMap("TextArea.selectionBackground");
    convertAndAddTypeMap("List.selectionBackground");
    convertAndAddTypeMap("textHighlight");
    convertAndAddTypeMap("Tree.line");
    convertAndAddTypeMap("PasswordField.selectionBackground");
    convertAndAddTypeMap("FormattedTextField.selectionBackground");
    convertAndAddTypeMap("info");
    convertAndAddTypeMap("TextPane.selectionBackground");
    convertAndAddTypeMap("ScrollBar.thumbHighlight");
    convertAndAddTypeMap("Tree.hash");
    convertAndAddTypeMap("EditorPane.selectionBackground");
    convertAndAddTypeMap("InternalFrame.activeTitleBackground");
    convertAndAddTypeMap("activeCaption");
    convertAndAddTypeMap("TextField.selectionBackground");
    convertAndAddTypeMap("Table.selectionBackground");
    convertAndAddTypeMap("ToolTip.background");
    convertAndAddTypeMap("DesktopIcon.background");
    convertAndAddTypeMap("InternalFrame.borderColor");
    convertAndAddTypeMap("ComboBox.disabledBackground");
    convertAndAddTypeMap("FormattedTextField.inactiveBackground");
    convertAndAddTypeMap("InternalFrame.inactiveTitleBackground");
    convertAndAddTypeMap("Button.background");
    convertAndAddTypeMap("TabbedPane.light");
    convertAndAddTypeMap("ToolTip.backgroundInactive");
    convertAndAddTypeMap("ToolBar.dockingBackground");
    convertAndAddTypeMap("ToggleButton.background");
    convertAndAddTypeMap("PopupMenu.background");
    convertAndAddTypeMap("CheckBoxMenuItem.background");
    convertAndAddTypeMap("Viewport.background");
    convertAndAddTypeMap("MenuItem.background");
    convertAndAddTypeMap("TabbedPane.tabAreaBackground");
    convertAndAddTypeMap("TextField.inactiveBackground");
    convertAndAddTypeMap("control");
    convertAndAddTypeMap("menu");
    convertAndAddTypeMap("windowBorder");
    convertAndAddTypeMap("Panel.background");
    convertAndAddTypeMap("RadioButtonMenuItem.background");
    convertAndAddTypeMap("ScrollBar.foreground");
    convertAndAddTypeMap("Spinner.foreground");
    convertAndAddTypeMap("MenuBar.background");
    convertAndAddTypeMap("CheckBox.background");
    convertAndAddTypeMap("RadioButton.background");
    convertAndAddTypeMap("ScrollBar.background");
    convertAndAddTypeMap("Spinner.background");
    convertAndAddTypeMap("ScrollBar.track");
    convertAndAddTypeMap("TabbedPane.selected");
    convertAndAddTypeMap("ColorChooser.swatchesDefaultRecentColor");
    convertAndAddTypeMap("ProgressBar.background");
    convertAndAddTypeMap("Menu.background");
    convertAndAddTypeMap("PasswordField.inactiveBackground");
    convertAndAddTypeMap("TableHeader.background");
    convertAndAddTypeMap("scrollbar");
    convertAndAddTypeMap("OptionPane.background");
    convertAndAddTypeMap("ComboBox.buttonBackground");
    convertAndAddTypeMap("ColorChooser.background");
    convertAndAddTypeMap("inactiveCaption");
    convertAndAddTypeMap("ProgressBar.selectionForeground");
    convertAndAddTypeMap("ToolBar.background");
    convertAndAddTypeMap("Slider.background");
    convertAndAddTypeMap("ScrollPane.background");
    convertAndAddTypeMap("Label.background");
    convertAndAddTypeMap("ToolBar.floatingBackground");
    convertAndAddTypeMap("ComboBox.background");
    convertAndAddTypeMap("OptionPane.warningDialog.titlePane.shadow");
    convertAndAddTypeMap("OptionPane.errorDialog.titlePane.shadow");
    convertAndAddTypeMap("OptionPane.errorDialog.border.background");
    convertAndAddTypeMap("OptionPane.questionDialog.titlePane.background");
    convertAndAddTypeMap("activeCaptionBorder");
    convertAndAddTypeMap("ToggleButton.focus");
    convertAndAddTypeMap("CheckBox.focus");
    convertAndAddTypeMap("Tree.selectionBorderColor");
    convertAndAddTypeMap("Slider.focus");
    convertAndAddTypeMap("Button.focus");
    convertAndAddTypeMap("CheckBoxMenuItem.selectionBackground");
    convertAndAddTypeMap("Menu.selectionBackground");
    convertAndAddTypeMap("ScrollBar.thumb");
    convertAndAddTypeMap("ComboBox.selectionBackground");
    convertAndAddTypeMap("MenuItem.selectionBackground");
    convertAndAddTypeMap("ProgressBar.foreground");
    convertAndAddTypeMap("Desktop.background");
    convertAndAddTypeMap("desktop");
    convertAndAddTypeMap("Slider.foreground");
    convertAndAddTypeMap("RadioButton.focus");
    convertAndAddTypeMap("RadioButtonMenuItem.selectionBackground");
    convertAndAddTypeMap("ToggleButton.shadow");
    convertAndAddTypeMap("EditorPane.inactiveForeground");
    convertAndAddTypeMap("ToggleButton.disabledText");
    convertAndAddTypeMap("TabbedPane.background");
    convertAndAddTypeMap("ScrollBar.shadow");
    convertAndAddTypeMap("inactiveCaptionBorder");
    convertAndAddTypeMap("textInactiveText");
    convertAndAddTypeMap("SplitPane.shadow");
    convertAndAddTypeMap("RadioButton.shadow");
    convertAndAddTypeMap("Menu.disabledForeground");
    convertAndAddTypeMap("Table.gridColor");
    convertAndAddTypeMap("ToolBar.shadow");
    convertAndAddTypeMap("ToggleButton.select");
    convertAndAddTypeMap("TextArea.inactiveForeground");
    convertAndAddTypeMap("Slider.shadow");
    convertAndAddTypeMap("RadioButton.select");
    convertAndAddTypeMap("CheckBox.disabledText");
    convertAndAddTypeMap("TextPane.inactiveForeground");
    convertAndAddTypeMap("Button.shadow");
    convertAndAddTypeMap("controlShadow");
    convertAndAddTypeMap("MenuItem.disabledForeground");
    convertAndAddTypeMap("CheckBoxMenuItem.disabledForeground");
    convertAndAddTypeMap("Label.disabledShadow");
    convertAndAddTypeMap("Checkbox.select");
    convertAndAddTypeMap("ComboBox.buttonShadow");
    convertAndAddTypeMap("InternalFrame.borderShadow");
    convertAndAddTypeMap("Button.select");
    convertAndAddTypeMap("TextField.shadow");
    convertAndAddTypeMap("Separator.shadow");
    convertAndAddTypeMap("Button.disabledText");
    convertAndAddTypeMap("Label.disabledForeground");
    convertAndAddTypeMap("PasswordField.inactiveForeground");
    convertAndAddTypeMap("TabbedPane.shadow");
    convertAndAddTypeMap("RadioButtonMenuItem.disabledForeground");
    convertAndAddTypeMap("ComboBox.disabledForeground");
    convertAndAddTypeMap("FormattedTextField.inactiveForeground");
    convertAndAddTypeMap("RadioButton.disabledText");
    convertAndAddTypeMap("MenuBar.shadow");
    convertAndAddTypeMap("TextField.inactiveForeground");
    convertAndAddTypeMap("OptionPane.warningDialog.border.background");
    convertAndAddTypeMap("OptionPane.warningDialog.titlePane.foreground");
    convertAndAddTypeMap("OptionPane.questionDialog.titlePane.shadow");
    convertAndAddTypeMap("Separator.foreground");
    convertAndAddTypeMap("RadioButtonMenuItem.acceleratorForeground");
    convertAndAddTypeMap("MenuItem.acceleratorForeground");
    convertAndAddTypeMap("Menu.acceleratorForeground");
    convertAndAddTypeMap("ScrollBar.thumbShadow");
    convertAndAddTypeMap("CheckBoxMenuItem.acceleratorForeground");
    convertAndAddTypeMap("ProgressBar.selectionBackground");
    convertAndAddTypeMap("ToolBar.dockingForeground");
    convertAndAddTypeMap("TabbedPane.focus");
    convertAndAddTypeMap("InternalFrame.borderDarkShadow");
    convertAndAddTypeMap("RadioButton.darkShadow");
    convertAndAddTypeMap("ToggleButton.darkShadow");
    convertAndAddTypeMap("TabbedPane.darkShadow");
    convertAndAddTypeMap("SplitPane.darkShadow");
    convertAndAddTypeMap("ScrollBar.thumbDarkShadow");
    convertAndAddTypeMap("TextField.darkShadow");
    convertAndAddTypeMap("ScrollBar.darkShadow");
    convertAndAddTypeMap("controlDkShadow");
    convertAndAddTypeMap("ComboBox.buttonDarkShadow");
    convertAndAddTypeMap("ToolTip.foregroundInactive");
    convertAndAddTypeMap("ScrollBar.trackHighlight");
    convertAndAddTypeMap("ToolBar.darkShadow");
    convertAndAddTypeMap("Button.darkShadow");
    convertAndAddTypeMap("OptionPane.questionDialog.titlePane.foreground");
    convertAndAddTypeMap("windowText");
    convertAndAddTypeMap("Table.selectionForeground");
    convertAndAddTypeMap("Label.foreground");
    convertAndAddTypeMap("FormattedTextField.caretForeground");
    convertAndAddTypeMap("RadioButtonMenuItem.selectionForeground");
    convertAndAddTypeMap("RadioButtonMenuItem.foreground");
    convertAndAddTypeMap("TextArea.caretForeground");
    convertAndAddTypeMap("inactiveCaptionText");
    convertAndAddTypeMap("PasswordField.caretForeground");
    convertAndAddTypeMap("ToolTip.foreground");
    convertAndAddTypeMap("ComboBox.foreground");
    convertAndAddTypeMap("TextPane.foreground");
    convertAndAddTypeMap("Tree.selectionForeground");
    convertAndAddTypeMap("InternalFrame.inactiveTitleForeground");
    convertAndAddTypeMap("textText");
    convertAndAddTypeMap("RadioButton.foreground");
    convertAndAddTypeMap("EditorPane.foreground");
    convertAndAddTypeMap("ToggleButton.foreground");
    convertAndAddTypeMap("TabbedPane.foreground");
    convertAndAddTypeMap("TextPane.caretForeground");
    convertAndAddTypeMap("Viewport.foreground");
    convertAndAddTypeMap("infoText");
    convertAndAddTypeMap("MenuItem.acceleratorSelectionForeground");
    convertAndAddTypeMap("MenuItem.foreground");
    convertAndAddTypeMap("menuText");
    convertAndAddTypeMap("Panel.foreground");
    convertAndAddTypeMap("textHighlightText");
    convertAndAddTypeMap("PasswordField.foreground");
    convertAndAddTypeMap("activeCaptionText");
    convertAndAddTypeMap("MenuBar.foreground");
    convertAndAddTypeMap("Menu.foreground");
    convertAndAddTypeMap("TextPane.selectionForeground");
    convertAndAddTypeMap("CheckBox.foreground");
    convertAndAddTypeMap("TextField.caretForeground");
    convertAndAddTypeMap("OptionPane.messageForeground");
    convertAndAddTypeMap("TextArea.selectionForeground");
    convertAndAddTypeMap("Menu.acceleratorSelectionForeground");
    convertAndAddTypeMap("TextField.foreground");
    convertAndAddTypeMap("MenuItem.selectionForeground");
    convertAndAddTypeMap("OptionPane.foreground");
    convertAndAddTypeMap("controlText");
    convertAndAddTypeMap("Tree.textForeground");
    convertAndAddTypeMap("ComboBox.selectionForeground");
    convertAndAddTypeMap("ToolBar.foreground");
    convertAndAddTypeMap("List.selectionForeground");
    convertAndAddTypeMap("InternalFrame.activeTitleForeground");
    convertAndAddTypeMap("TextArea.foreground");
    convertAndAddTypeMap("PasswordField.selectionForeground");
    convertAndAddTypeMap("TextField.selectionForeground");
    convertAndAddTypeMap("FormattedTextField.selectionForeground");
    convertAndAddTypeMap("Table.foreground");
    convertAndAddTypeMap("RadioButtonMenuItem.acceleratorSelectionForeground");
    convertAndAddTypeMap("TitledBorder.titleColor");
    convertAndAddTypeMap("TableHeader.foreground");
    convertAndAddTypeMap("DesktopIcon.foreground");
    convertAndAddTypeMap("Table.focusCellForeground");
    convertAndAddTypeMap("ColorChooser.foreground");
    convertAndAddTypeMap("EditorPane.selectionForeground");
    convertAndAddTypeMap("CheckBoxMenuItem.acceleratorSelectionForeground");
    convertAndAddTypeMap("Button.foreground");
    convertAndAddTypeMap("List.foreground");
    convertAndAddTypeMap("PopupMenu.foreground");
    convertAndAddTypeMap("CheckBoxMenuItem.foreground");
    convertAndAddTypeMap("EditorPane.caretForeground");
    convertAndAddTypeMap("CheckBoxMenuItem.selectionForeground");
    convertAndAddTypeMap("ScrollPane.foreground");
    convertAndAddTypeMap("Menu.selectionForeground");
    convertAndAddTypeMap("Tree.foreground");

    // FONTS
    convertAndAddTypeMap("Dialog.font");
    convertAndAddTypeMap("Frame.font");
    convertAndAddTypeMap("Button.font");
    convertAndAddTypeMap("CheckBox.font");
    convertAndAddTypeMap("Slider.font");
    convertAndAddTypeMap("CheckBoxMenuItem.acceleratorFont");
    convertAndAddTypeMap("CheckBoxMenuItem.font");
    convertAndAddTypeMap("ColorChooser.font");
    convertAndAddTypeMap("ComboBox.font");
    convertAndAddTypeMap("DesktopIcon.font");
    convertAndAddTypeMap("EditorPane.font");
    convertAndAddTypeMap("FormattedTextField.font");
    convertAndAddTypeMap("InternalFrame.titleFont");
    convertAndAddTypeMap("Label.font");
    convertAndAddTypeMap("List.font");
    convertAndAddTypeMap("Menu.acceleratorFont");
    convertAndAddTypeMap("Menu.font");
    convertAndAddTypeMap("MenuBar.font");
    convertAndAddTypeMap("MenuItem.acceleratorFont");
    convertAndAddTypeMap("MenuItem.font");
    convertAndAddTypeMap("OptionPane.font");
    convertAndAddTypeMap("Panel.font");
    convertAndAddTypeMap("PasswordField.font");
    convertAndAddTypeMap("PopupMenu.font");
    convertAndAddTypeMap("ProgressBar.font");
    convertAndAddTypeMap("RadioButton.font");
    convertAndAddTypeMap("RadioButtonMenuItem.acceleratorFont");
    convertAndAddTypeMap("RadioButtonMenuItem.font");
    convertAndAddTypeMap("ScrollPane.font");
    convertAndAddTypeMap("Spinner.font");
    convertAndAddTypeMap("TabbedPane.font");
    convertAndAddTypeMap("Table.font");
    convertAndAddTypeMap("TableHeader.font");
    convertAndAddTypeMap("TextArea.font");
    convertAndAddTypeMap("TextField.font");
    convertAndAddTypeMap("TextPane.font");
    convertAndAddTypeMap("TitledBorder.font");
    convertAndAddTypeMap("ToggleButton.font");
    convertAndAddTypeMap("ToolBar.font");
    convertAndAddTypeMap("ToolTip.font");
    convertAndAddTypeMap("Tree.font");
    convertAndAddTypeMap("Viewport.font");

    // icons
    convertAndAddTypeMap("OptionPane.questionIcon");
    convertAndAddTypeMap("OptionPane.errorIcon");
    convertAndAddTypeMap("OptionPane.warningIcon");
    convertAndAddTypeMap("OptionPane.informationIcon");
    convertAndAddTypeMap("CheckBox.icon");
    convertAndAddTypeMap("CheckBoxMenuItem.checkIcon");
    convertAndAddTypeMap("FileChooser.detailsViewIcon");
    convertAndAddTypeMap("FileChooser.homeFolderIcon");
    convertAndAddTypeMap("FileChooser.listViewIcon");
    convertAndAddTypeMap("FileChooser.newFolderIcon");
    convertAndAddTypeMap("FileChooser.upFolderIcon");
    convertAndAddTypeMap("Slider.horizontalThumbIcon");
    convertAndAddTypeMap("InternalFrame.minimizeIcon");
    convertAndAddTypeMap("InternalFrame.closeIcon");
    convertAndAddTypeMap("InternalFrame.icon");
    convertAndAddTypeMap("InternalFrame.maximizeIcon");
    convertAndAddTypeMap("InternalFrame.iconifyIcon");
    convertAndAddTypeMap("Menu.arrowIcon");
    convertAndAddTypeMap("RadioButtonMenuItem.arrowIcon");
    convertAndAddTypeMap("MenuItem.arrowIcon");
    convertAndAddTypeMap("CheckBoxMenuItem.arrowIcon");
    convertAndAddTypeMap("InternalFrame.paletteCloseIcon");
    convertAndAddTypeMap("RadioButton.icon");
    convertAndAddTypeMap("RadioButtonMenuItem.checkIcon");
    convertAndAddTypeMap("FileView.computerIcon");
    convertAndAddTypeMap("Tree.expandedIcon");
    convertAndAddTypeMap("Tree.collapsedIcon");
    convertAndAddTypeMap("FileView.floppyDriveIcon");
    convertAndAddTypeMap("Tree.openIcon");
    convertAndAddTypeMap("FileView.directoryIcon");
    convertAndAddTypeMap("Tree.closedIcon");
    convertAndAddTypeMap("FileView.hardDriveIcon");
    convertAndAddTypeMap("Tree.leafIcon");
    convertAndAddTypeMap("FileView.fileIcon");
    convertAndAddTypeMap("Slider.verticalThumbIcon");

    // margins / insets
    convertAndAddTypeMap("PasswordField.margin");
    convertAndAddTypeMap("FormattedTextField.margin");
    convertAndAddTypeMap("Spinner.arrowButtonInsets");
    convertAndAddTypeMap("TextArea.margin");
    convertAndAddTypeMap("TextField.margin");
    convertAndAddTypeMap("Slider.focusInsets");
    convertAndAddTypeMap("TabbedPane.tabInsets");
    convertAndAddTypeMap("Button.margin");
    convertAndAddTypeMap("ToggleButton.margin");
    convertAndAddTypeMap("TabbedPane.selectedTabPadInsets");
    convertAndAddTypeMap("RadioButton.margin");
    convertAndAddTypeMap("CheckBox.margin");
    convertAndAddTypeMap("MenuItem.margin");
    convertAndAddTypeMap("Menu.margin");
    convertAndAddTypeMap("CheckBoxMenuItem.margin");
    convertAndAddTypeMap("RadioButtonMenuItem.margin");
    convertAndAddTypeMap("EditorPane.margin");
    convertAndAddTypeMap("TextPane.margin");

    // borders
    convertAndAddTypeMap("ToggleButton.border");
    convertAndAddTypeMap("DesktopIcon.border");
    convertAndAddTypeMap("RadioButton.border");
    convertAndAddTypeMap("Spinner.arrowButtonBorder");
    convertAndAddTypeMap("Button.border");
    convertAndAddTypeMap("CheckBox.border");
    convertAndAddTypeMap("TextField.border");
    convertAndAddTypeMap("PasswordField.border");
    convertAndAddTypeMap("Spinner.border");
    convertAndAddTypeMap("FormattedTextField.border");
    convertAndAddTypeMap("OptionPane.messageAreaBorder");
    convertAndAddTypeMap("OptionPane.border");
    convertAndAddTypeMap("OptionPane.buttonAreaBorder");
    convertAndAddTypeMap("ProgressBar.border");
    convertAndAddTypeMap("ToolTip.borderInactive");
    convertAndAddTypeMap("Tree.editorBorder");
    convertAndAddTypeMap("List.focusCellHighlightBorder");
    convertAndAddTypeMap("Table.focusCellHighlightBorder");
    convertAndAddTypeMap("TitledBorder.border");
    convertAndAddTypeMap("ToolTip.border");
    convertAndAddTypeMap("RootPane.plainDialogBorder");
    convertAndAddTypeMap("RootPane.informationDialogBorder");
    convertAndAddTypeMap("RootPane.errorDialogBorder");
    convertAndAddTypeMap("RootPane.frameBorder");
    convertAndAddTypeMap("InternalFrame.border");
    convertAndAddTypeMap("MenuBar.border");
    convertAndAddTypeMap("Menu.border");
    convertAndAddTypeMap("CheckBoxMenuItem.border");
    convertAndAddTypeMap("MenuItem.border");
    convertAndAddTypeMap("RadioButtonMenuItem.border");
    convertAndAddTypeMap("InternalFrame.optionDialogBorder");
    convertAndAddTypeMap("InternalFrame.paletteBorder");
    convertAndAddTypeMap("PopupMenu.border");
    convertAndAddTypeMap("RootPane.colorChooserDialogBorder");
    convertAndAddTypeMap("RootPane.fileChooserDialogBorder");
    convertAndAddTypeMap("RootPane.questionDialogBorder");
    convertAndAddTypeMap("ScrollPane.border");
    convertAndAddTypeMap("Table.scrollPaneBorder");
    convertAndAddTypeMap("TableHeader.cellBorder");
    convertAndAddTypeMap("ToolBar.border");
    convertAndAddTypeMap("RootPane.warningDialogBorder");

    // sizes
    convertAndAddTypeMap("Menu.submenuPopupOffsetY");
    convertAndAddTypeMap("Menu.submenuPopupOffsetX");
    convertAndAddTypeMap("ToggleButton.textShiftOffset");
    convertAndAddTypeMap("RadioButton.textShiftOffset");
    convertAndAddTypeMap("Button.textShiftOffset");
    convertAndAddTypeMap("CheckBox.textShiftOffset");
    convertAndAddTypeMap("Menu.menuPopupOffsetY");
    convertAndAddTypeMap("Tree.rowHeight");
    convertAndAddTypeMap("Menu.menuPopupOffsetX");
    convertAndAddTypeMap("ProgressBar.cellSpacing");
    convertAndAddTypeMap("ProgressBar.cellLength");
    convertAndAddTypeMap("SplitPane.dividerSize");
    convertAndAddTypeMap("List.timeFactor");
    convertAndAddTypeMap("ComboBox.timeFactor");
    convertAndAddTypeMap("Tree.timeFactor");
    convertAndAddTypeMap("InternalFrame.paletteTitleHeight");
    convertAndAddTypeMap("Tree.rightChildIndent");
    convertAndAddTypeMap("DesktopIcon.width");
    convertAndAddTypeMap("ScrollBar.width");
    convertAndAddTypeMap("TabbedPane.tabRunOverlay");
    convertAndAddTypeMap("ProgressBar.cycleTime");
    convertAndAddTypeMap("Button.textIconGap");
    convertAndAddTypeMap("TabbedPane.textIconGap");
    convertAndAddTypeMap("CheckBox.textIconGap");
    convertAndAddTypeMap("ToggleButton.textIconGap");
    convertAndAddTypeMap("RadioButton.textIconGap");
    convertAndAddTypeMap("ProgressBar.repaintInterval");
    convertAndAddTypeMap("FormattedTextField.caretBlinkRate");
    convertAndAddTypeMap("TextArea.caretBlinkRate");
    convertAndAddTypeMap("TextField.caretBlinkRate");
    convertAndAddTypeMap("TextPane.caretBlinkRate");
    convertAndAddTypeMap("PasswordField.caretBlinkRate");
    convertAndAddTypeMap("EditorPane.caretBlinkRate");
    convertAndAddTypeMap("OptionPane.buttonClickThreshhold");
    convertAndAddTypeMap("Slider.majorTickLength");
    convertAndAddTypeMap("Tree.leftChildIndent");
    convertAndAddTypeMap("Slider.trackWidth");

    // added, but don't seem to do anything
    addToUImap("Button.highlight", "button_highlight");
    addToUImap("Button.light", "button_light");
    addToUImap("Button.select", "button_select");
    addToUImap("Button.shadow", "button_shadow");
    addToUImap("Button.darkShadow", "button_darkshadow");
    addToUImap("Button.focus", "button_focus");
    addToUImap("Button.disabledText", "button_disabledtext");
    addToUImap("ScrollPane.background", "scrollpanel_background");
    addToUImap("ScrollPane.foreground", "scrollpanel_foreground");
    try {
      jbInit();
    }
    catch (Exception ex) {
        log.error("SkinManager(): error", ex);
    }

  }

  private void convertAndAddTypeMap(String value) {
    boolean past_period = false;
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < value.length(); ++i) {
      if (value.charAt(i) == '.') {
        past_period = true;
      }
      if (!past_period) {
        result.append(Character.toLowerCase(value.charAt(i)));
      } else {
        if (value.charAt(i) == '.') result.append('_');
        else {
          if (Character.isUpperCase(value.charAt(i))) result.append('_');
          result.append(Character.toLowerCase(value.charAt(i)));
        }
      }
    }

    String resultstr = result.toString();
    resultstr = StringUtil.ReplaceString("textfield", resultstr, "field");
    // so as not to cause "panell"
    resultstr = StringUtil.ReplaceString("panel", resultstr, "pane");
    resultstr = StringUtil.ReplaceString("pane", resultstr, "panel");
    addToUImap(value, resultstr);
  }

  private void initUIDefaults() {
      uidefaults = UIManager.getDefaults();
    uidefaults.put("Spinner.background", uidefaults.get("TextField.background"));
    uidefaults.put("Spinner.foreground", uidefaults.get("TextField.foreground"));
    uidefaults.put("Table.background", uidefaults.get("Panel.background"));
    uidefaults.put("Table.foreground", uidefaults.get("Panel.foreground"));
    uidefaults.put("ScrollPane.background", uidefaults.get("Panel.background"));
    uidefaults.put("ScrollPane.foreground", uidefaults.get("Panel.foreground"));
    uidefaults.put("Frame.background", uidefaults.get("Panel.background"));
    uidefaults.put("Frame.foreground", uidefaults.get("Panel.foreground"));
    uidefaults.put("Dialog.background", uidefaults.get("Panel.background"));
    uidefaults.put("Dialog.foreground", uidefaults.get("Panel.foreground"));
    uidefaults.put("Frame.font", uidefaults.get("Panel.font"));
    uidefaults.put("Dialog.font", uidefaults.get("Panel.font"));
    uidefaults.put("Field.disabled", (new JTextField()).getDisabledTextColor());
    uidefaults.put("Table.selectionBackground", (new JTable()).getSelectionBackground());
    uidefaults.put("Table.selectionForeground", (new JTable()).getSelectionForeground());
    defaultBlinkRate = (new JTextField()).getCaret().getBlinkRate();
    initSelectionModes();
  }

  public void initSelectionModes() {
    MixStartsWithUI.instance.mixstartswithlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    SelectMissingSongUI.instance.selectmissingsonglist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    AddStyleSongsUI.instance.addsongincludelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    ExcludeStyleSongsUI.instance.addsongexcludelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    if (OptionsUI.instance.disable_multiple_delect.isSelected()) 
        SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    else SearchPane.instance.searchtable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);                
    StylesUI.resetSelectionModes();
//    StylesPane.instance.stylelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    AddMatchQueryUI.instance.matchtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    EditMatchQueryUI.instance.matchtable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    EditStyleUI.instance.editstylekeywordslist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    EditStyleUI.instance.styleexcludelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    ExcludeUI.instance.excludetable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    PlayListUI.instance.playlistlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    OptionsUI.instance.available_skins_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    RootsUI.instance.rootstable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    SongTrailUI.instance.songtraillist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    SuggestedMixesUI.instance.suggestedtable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    SyncUI.instance.synctable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    AddSyncSongsUI.instance.addsyncsongslist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    ListStyleSongsUI.instance.liststylesongslist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  }

  public void setImap() {
    imap = new HashMap(820);
    imap.put("current_song_field", RapidEvolutionUI.instance.currentsongfield);
    imap.put("previous_song_field", RapidEvolutionUI.instance.previoussongfield);
    imap.put("current_song_key_lock_checkbox", RapidEvolutionUI.instance.keylockcurrentsong);
    imap.put("current_song_edit_button", RapidEvolutionUI.instance.editcurrentsongbutton);
    imap.put("current_song_back_button", RapidEvolutionUI.instance.backbutton);
    imap.put("current_song_next_button", RapidEvolutionUI.instance.nextbutton);
    imap.put("search_panel", RapidEvolutionUI.instance.searchpanel.mainpanel);
    imap.put("styles_panel", RapidEvolutionUI.instance.stylespanel.stylepanel);
    imap.put("import_button", RapidEvolutionUI.instance.importbutton);
    imap.put("export_button", RapidEvolutionUI.instance.exportbutton);
    imap.put("add_excludes_button", RapidEvolutionUI.instance.addexcludebutton);
    imap.put("save_list_button", RapidEvolutionUI.instance.savelistbutton);
    imap.put("search_field", SearchPane.instance.searchfield);
    imap.put("search_history_button", SearchPane.instance.searchhistorybutton);
    imap.put("search_bpm_reset_button", SearchPane.instance.bpmresetbutton);
    imap.put("search_bpm_tapper_button", SearchPane.instance.tapbpmbutton);
    imap.put("search_clear_results_button", SearchPane.instance.clearresultsbutton);
    imap.put("search_bpm_slider_tickmark_combo", SearchPane.instance.bpmslider_tickmark_combo);
    imap.put("search_style_require_shortcut_combobox", OptionsUI.instance.style_require_shortcut_combobox);
    imap.put("search_style_exclude_shortcut_combobox", OptionsUI.instance.style_exclude_shortcut_combobox);
    imap.put("options_color_chooser_button", OptionsUI.instance.colorChooserButton);        
    imap.put("options_song_tag_write_key_to_title_checkbox", OptionsUI.instance.writekeytotitle);
    imap.put("options_display_always_on_top", OptionsUI.instance.alwaysontop);
    imap.put("options_song_tag_write_key_to_comments_checkbox", OptionsUI.instance.writekeytocomments);
    imap.put("options_add_songs_automatically_parse_title_checkbox", OptionsUI.instance.automatically_parse_title_field);
    imap.put("options_about_system_info", OptionsUI.instance.system_info_button);
    imap.put("options_audio_detect_start_and_end_keys_checkbox", OptionsUI.instance.detect_start_and_end_keys);
    imap.put("options_audio_detect_advanced_keys_checkbox", OptionsUI.instance.detect_advanced_keys);
    imap.put("options_audio_detect_disable_multithreaded_checkbox", OptionsUI.instance.detect_disable_multithreaded);
    imap.put("options_show_advanced_key_information_checkbox", OptionsUI.instance.show_advanced_key_information);    
    imap.put("search_bpm_field", SearchPane.instance.bpmfield);
    imap.put("search_bpm_shift_slider", SearchPane.instance.bpmslider);
    imap.put("search_key_detect_button", SearchPane.instance.detectbutton);
    imap.put("search_key_field", SearchPane.instance.keyfield);
    imap.put("key_search_button", SearchPane.instance.keysearchbutton);
    imap.put("bpm_search_button", SearchPane.instance.bpmsearchbutton);
    imap.put("find_all_button", SearchPane.instance.findallbutton);
    imap.put("search_bpm_slider_range_spinner", SearchPane.instance.bpmrangespinner);
    imap.put("search_selected_results_label", SearchPane.instance.searchselected);
    imap.put("search_bpm_shift_field", SearchPane.instance.bpmshiftfield);
    imap.put("search_filter_all", SearchPane.instance.showallradio);
    imap.put("search_filter_analog", SearchPane.instance.showvinylradio);
    imap.put("search_filter_digital", SearchPane.instance.shownonvinylradio);
    imap.put("search_table", SearchPane.instance.searchtable);
    imap.put("search_results_field", SearchPane.instance.resultslabel);
    imap.put("mixout_table", MixoutPane.instance.mixouttable);
    imap.put("mixout_comments_label", MixoutPane.instance.mixoutcommentslabel);
    imap.put("mixout_comments_field", MixoutPane.instance.mixoutcomments);
    imap.put("mixout_rank_field", MixoutPane.instance.mixoutscore);
    imap.put("mixout_addon_checkbox", MixoutPane.instance.addoncheckbox);
    imap.put("mixout_calculate_bpm_diff_button", MixoutPane.instance.calculatebpmdiffbutton);
    imap.put("mixout_bpm_diff_field", MixoutPane.instance.bpmdifffield);
    imap.put("mixout_rank_label", MixoutPane.instance.scorefield);
    imap.put("mixout_bpm_diff_label", MixoutPane.instance.bpmdifflabel);
    imap.put("mixoutdetails_panel", MixoutPane.instance.mixdetailspanel);
    imap.put("styles_tree", StylesPane.instance.styletree);
    imap.put("styles_dynamic_checkbox", StylesPane.instance.dynamixstylescheckbox);
    imap.put("styles_clear_button", StylesPane.instance.clearstylesbutton);
    imap.put("styles_delete_button", StylesPane.instance.deletestylebutton);
    imap.put("add_exclude_no_button", AddExcludeUI.instance.addexcludenobutton);
    imap.put("add_exclude_yes_button", AddExcludeUI.instance.addexcludeyesbutton);
    imap.put("add_exclude_from_song_field", AddExcludeUI.instance.excludefromfield);
    imap.put("add_exclude_song_field", AddExcludeUI.instance.excludetofield);
    imap.put("add_exclude_reverse_checkbox", AddExcludeUI.instance.addreverseexcludebt);
    imap.put("add_songs_rating_1_checkbox", AddSongsUI.instance.rating1checkbox);
    imap.put("add_songs_rating_2_checkbox", AddSongsUI.instance.rating2checkbox);
    imap.put("add_songs_rating_3_checkbox", AddSongsUI.instance.rating3checkbox);
    imap.put("add_songs_rating_4_checkbox", AddSongsUI.instance.rating4checkbox);
    imap.put("add_songs_rating_5_checkbox", AddSongsUI.instance.rating5checkbox);    
    imap.put("current_song_rating_1_checkbox", RapidEvolutionUI.instance.rating1checkbox);
    imap.put("current_song_rating_2_checkbox", RapidEvolutionUI.instance.rating2checkbox);
    imap.put("current_song_rating_3_checkbox", RapidEvolutionUI.instance.rating3checkbox);
    imap.put("current_song_rating_4_checkbox", RapidEvolutionUI.instance.rating4checkbox);
    imap.put("current_song_rating_5_checkbox", RapidEvolutionUI.instance.rating5checkbox);   
    imap.put("filter_search_minimum_ratings_include_unrated_checkbox", RapidEvolutionUI.instance.filter_include_unrated);
    imap.put("filter_search_minimum_rating_1_checkbox", RapidEvolutionUI.instance.minrating1checkbox);
    imap.put("filter_search_minimum_rating_2_checkbox", RapidEvolutionUI.instance.minrating2checkbox);
    imap.put("filter_search_minimum_rating_3_checkbox", RapidEvolutionUI.instance.minrating3checkbox);
    imap.put("filter_search_minimum_rating_4_checkbox", RapidEvolutionUI.instance.minrating4checkbox);
    imap.put("filter_search_minimum_rating_5_checkbox", RapidEvolutionUI.instance.minrating5checkbox);
    imap.put("filter_search_date_added_since_chooser", RapidEvolutionUI.instance.dateAddedsince);
    imap.put("add_songs_album_cover_toolbar", AddSongsUI.instance.addsongsalbumcover_ui.imagepanel);
    imap.put("add_songs_album_cover_ok_button", AddSongsUI.instance.addsongsalbumcover_ui.okbutton);
    imap.put("add_songs_album_cover_change_button", AddSongsUI.instance.addsongsalbumcover_ui.changebutton);
    imap.put("add_songs_album_cover_retrieve_button", AddSongsUI.instance.addsongsalbumcover_ui.retrievebutton);
    imap.put("edit_song_album_cover_toolbar", EditSongUI.instance.editsongalbumcover_ui.imagepanel);
    imap.put("edit_song_album_cover_ok_button", EditSongUI.instance.editsongalbumcover_ui.okbutton);
    imap.put("edit_song_album_cover_change_button", EditSongUI.instance.editsongalbumcover_ui.changebutton);
    imap.put("edit_song_album_cover_retrieve_button", EditSongUI.instance.editsongalbumcover_ui.retrievebutton);    
    imap.put("add_songs_query_results_table", AddMatchQueryUI.instance.matchtable);
    imap.put("add_songs_query_results_cancel_button", AddMatchQueryUI.instance.matchsongscancelbutton);
    imap.put("add_songs_query_results_ok_button", AddMatchQueryUI.instance.matchsongsokbutton);
    imap.put("add_mixout_cancel_button", AddMixoutUI.instance.addmixoutcancelbutton);
    imap.put("add_mixout_ok_button", AddMixoutUI.instance.addmixoutokbutton);
    imap.put("add_mixout_from_field", AddMixoutUI.instance.addmixoutfromfield);
    imap.put("add_mixout_to_field", AddMixoutUI.instance.addmixouttofield);
    imap.put("add_mixout_comments_textarea", AddMixoutUI.instance.addmixoutcommentsfield);
    imap.put("add_mixout_bpm_diff_field", AddMixoutUI.instance.addmixoutbpmdifffield);
    imap.put("add_mixout_calculate_bpm_diff_button", AddMixoutUI.instance.addmixoutcalcbpmdiffbutton);
    imap.put("add_mixout_rank_field", AddMixoutUI.instance.addmixoutscorefield);
    imap.put("add_mixout_addon_checkbox", AddMixoutUI.instance.addmixoutaddoncb);
    imap.put("edit_mixout_cancel_button", EditMixoutUI.instance.editmixoutcancelbutton);
    imap.put("edit_mixout_ok_button", EditMixoutUI.instance.editmixoutokbutton);
    imap.put("edit_mixout_from_field", EditMixoutUI.instance.editmixoutfromfield);
    imap.put("edit_mixout_to_field", EditMixoutUI.instance.editmixouttofield);
    imap.put("edit_mixout_comments_textarea", EditMixoutUI.instance.editmixoutcommentsfield);
    imap.put("edit_mixout_bpm_diff_field", EditMixoutUI.instance.editmixoutbpmdifffield);
    imap.put("edit_mixout_calculate_bpm_diff_button", EditMixoutUI.instance.editmixoutcalcbpmdiffbutton);
    imap.put("edit_mixout_rank_field", EditMixoutUI.instance.editmixoutscorefield);
    imap.put("edit_mixout_addon_checkbox", EditMixoutUI.instance.editmixoutaddoncb);
    imap.put("normalization_progress_bar", NormalizationProgressUI.instance.progressbar2);
    imap.put("normalization_cancel_button", NormalizationProgressUI.instance.cancelbutton);
    imap.put("time_pitch_shift_process_progress_bar", TimePitchShiftProgressUI.instance.progressbar2);
    imap.put("time_pitch_shift_process_cancel_button", TimePitchShiftProgressUI.instance.cancelbutton);
    imap.put("add_songs_detect_key_progress_bar", AddSongKeyProgressUI.instance.progressbar2);
    imap.put("add_songs_detect_key_cancel_button", AddSongKeyProgressUI.instance.addkeycancelbutton);
    imap.put("edit_song_detect_key_progress_bar", EditSongKeyProgressUI.instance.progressbar);
    imap.put("edit_song_detect_key_cancel_button", EditSongKeyProgressUI.instance.editkeycancelbutton);
    imap.put("detect_key_batch_progress_bar", DetectKeyBatchProgressUI.instance.progressbar);
    imap.put("detect_key_batch_cancel_button", DetectKeyBatchProgressUI.instance.cancelbutton);
    imap.put("detect_rga_batch_progress_bar", DetectRGABatchProgressUI.instance.progressbar);
    imap.put("detect_rga_batch_cancel_button", DetectRGABatchProgressUI.instance.cancelbutton);
    imap.put("detect_beat_intensity_batch_progress_bar", DetectBeatIntensityBatchProgressUI.instance.progressbar);
    imap.put("detect_beat_intensity_batch_cancel_button", DetectBeatIntensityBatchProgressUI.instance.cancelbutton);
    imap.put("write_tags_progress_bar", WriteTagsProgressUI.instance.progressbar);
    imap.put("write_tags_cancel_button", WriteTagsProgressUI.instance.cancelbutton);    
    imap.put("query_server_progress_bar", QueryServerProgressUI.instance.progressbar);
    imap.put("query_server_cancel_button", QueryServerProgressUI.instance.cancelbutton);    
    imap.put("organize_songs_progress_bar", OrganizeFilesProgressUI.instance.progressbar);
    imap.put("organize_songs_cancel_button", OrganizeFilesProgressUI.instance.cancelbutton);    
    imap.put("read_tags_progress_bar", ReadTagsProgressUI.instance.progressbar);
    imap.put("read_tags_cancel_button", ReadTagsProgressUI.instance.cancelbutton);    
    imap.put("import_progress_bar", ImportProgressUI.instance.progressbar);
    imap.put("import_cancel_button", ImportProgressUI.instance.cancelbutton);    
    imap.put("export_progress_bar", ExportProgressUI.instance.progressbar);
    imap.put("export_cancel_button", ExportProgressUI.instance.cancelbutton);    
    imap.put("retrieve_album_cover_progress_bar", RetrieveAlbumCoversProgressUI.instance.progressbar);
    imap.put("retrieve_album_cover_cancel_button", RetrieveAlbumCoversProgressUI.instance.cancelbutton);
    imap.put("renaming_files_progress_bar", RenameFilesProgressUI.instance.progressbar);
    imap.put("renaming_files_cancel_button", RenameFilesProgressUI.instance.cancelbutton);
    imap.put("detect_bpm_batch_progress_bar", DetectBpmBatchProgressUI.instance.progressbar);
    imap.put("detect_bpm_batch_cancel_button", DetectBpmBatchProgressUI.instance.cancelbutton);
    imap.put("detect_all_batch_progress_bar", DetectAllBatchProgressUI.instance.progressbar);
    imap.put("detect_all_batch_cancel_button", DetectAllBatchProgressUI.instance.cancelbutton);        
    imap.put("add_songs_album_cover_button", AddSongsUI.instance.albumcover_btn);
    imap.put("add_songs_bpm_detection_accuracy_slider", AddSongsUI.instance.addsongs_bpmaccuracy);
    imap.put("add_songs_key_detection_accuracy_slider", AddSongsUI.instance.addsongs_keyaccuracy);
    imap.put("add_songs_skip_button", AddSongsUI.instance.addsongsskipbutton);
    imap.put("add_songs_styles_tree", AddSongsUI.instance.addsongstylestree);
    imap.put("add_songs_styles_clear_button", AddSongsUI.instance.addsongsclearstylesbt);
    imap.put("add_songs_style_edit_button", AddSongsUI.instance.addsongseditstylebt);
    imap.put("style_edit_button", StylesPane.instance.editstylebutton);
    imap.put("add_songs_style_add_button", AddSongsUI.instance.addsongsaddstylebt);
    imap.put("add_songs_styles_delete_button", AddSongsUI.instance.addsongsdeletestylebt);
    imap.put("add_songs_ok_button", AddSongsUI.instance.addsongsokbutton);
    imap.put("add_songs_cancel_button", AddSongsUI.instance.addsongscancelbutton);

    imap.put("edit_song_back_button", EditSongUI.instance.backbutton);
    imap.put("edit_song_next_button", EditSongUI.instance.nextbutton);
    imap.put("edit_song_clear_fields_button", EditSongUI.instance.cleareditsongfields);
    
    imap.put("add_songs_more_button", AddSongsUI.instance.addsongsmorebutton);
    imap.put("add_songs_add_all_button", AddSongsUI.instance.addallbutton);
    imap.put("add_songs_artist_field", AddSongsUI.instance.addsongsartistfield);
    imap.put("add_songs_album_field", AddSongsUI.instance.addsongsalbumfield);
    imap.put("add_songs_track_field", AddSongsUI.instance.addsongstrackfield);
    imap.put("add_songs_time_field", AddSongsUI.instance.addsongstimefield);
    imap.put("add_songs_time_signature_field", AddSongsUI.instance.addsongstimesignaturefield);
    imap.put("add_songs_remix_field", AddSongsUI.instance.addsongsremixerfield);
    imap.put("add_songs_title_field", AddSongsUI.instance.addsongstitlefield);
    imap.put("add_songs_clear_fields_button", AddSongsUI.instance.clearaddsongfields);
    imap.put("add_songs_bpm_start_field", AddSongsUI.instance.addsongsstartbpmfield);
    imap.put("add_songs_bpm_start_tapper_button", AddSongsUI.instance.addsongsstartbpmtapbutton);
    imap.put("add_songs_bpm_start_reset_button", AddSongsUI.instance.addsongsstartbpmresetbutton);
    imap.put("add_songs_bpm_end_field", AddSongsUI.instance.addsongsendbpmfield);
    imap.put("add_songs_bpm_end_tapper_button", AddSongsUI.instance.addsongsendbpmtapbutton);
    imap.put("add_songs_bpm_end_reset_button", AddSongsUI.instance.addsongsendbpmresetbutton);
    imap.put("add_songs_key_start_field", AddSongsUI.instance.addsongsstartkeyfield);
    imap.put("add_songs_key_end_field", AddSongsUI.instance.addsongsendkeyfield);
    imap.put("add_songs_key_detect_button", AddSongsUI.instance.addsongsdetectkeybutton);
    imap.put("add_songs_comments_textarea", AddSongsUI.instance.addsongscommentsfield);
    imap.put("add_songs_custom_label_1", AddSongsUI.instance.addcustomfieldlabel1);
    imap.put("add_songs_custom_field_1", AddSongsUI.instance.addcustomfieldtext1);
    imap.put("add_songs_custom_label_2", AddSongsUI.instance.addcustomfieldlabel2);
    imap.put("add_songs_custom_field_2", AddSongsUI.instance.addcustomfieldtext2);
    imap.put("add_songs_custom_label_3", AddSongsUI.instance.addcustomfieldlabel3);
    imap.put("add_songs_custom_field_3", AddSongsUI.instance.addcustomfieldtext3);
    imap.put("add_songs_custom_label_4", AddSongsUI.instance.addcustomfieldlabel4);
    imap.put("add_songs_custom_field_4", AddSongsUI.instance.addcustomfieldtext4);
    imap.put("add_songs_filename_field", AddSongsUI.instance.addsongsfilenamefield);
    imap.put("add_songs_browse_button", AddSongsUI.instance.addsongsbrowsebutton);
    imap.put("add_songs_tag_write_button", AddSongsUI.instance.addsongswritetagbutton);
    imap.put("add_songs_rename_button", AddSongsUI.instance.addsongsrenamebutton);
    imap.put("add_songs_tag_read_button", AddSongsUI.instance.addsongsreadtagbutton);
    imap.put("add_songs_play_button", AddSongsUI.instance.addsongsplaybutton);
    imap.put("add_songs_analog_checkbox", AddSongsUI.instance.addsongsvinylonly);
    imap.put("add_songs_digital_checkbox", AddSongsUI.instance.addsongsnonvinylonly);
    imap.put("add_songs_disabled_checkbox", AddSongsUI.instance.addsongsdisabled);
    imap.put("add_sync_songs_list", AddSyncSongsUI.instance.addsyncsongslist);
    imap.put("add_sync_songs_search_button", AddSyncSongsUI.instance.addsyncsongssearchbutton);
    imap.put("add_sync_songs_cancel_button", AddSyncSongsUI.instance.addsyncsongscancelbutton);
    imap.put("add_sync_songs_ok_button", AddSyncSongsUI.instance.addsyncsongsokbutton);
    imap.put("add_sync_songs_search_field", AddSyncSongsUI.instance.addsyncsongssearchfield);
    imap.put("edit_song_query_results_table", EditMatchQueryUI.instance.matchtable2);
    imap.put("edit_song_query_results_cancel_button", EditMatchQueryUI.instance.matchsongscancelbutton2);
    imap.put("edit_song_query_results_ok_button", EditMatchQueryUI.instance.matchsongsokbutton2);
    imap.put("mixgen_edit_mixout_cancel_button", EditMixoutMixGenUI.instance.editmixout2cancelbutton);
    imap.put("mixgen_edit_mixout_ok_button", EditMixoutMixGenUI.instance.editmixout2okbutton);
    imap.put("mixgen_edit_mixout_from_field", EditMixoutMixGenUI.instance.editmixout2fromfield);
    imap.put("mixgen_edit_mixout_to_field", EditMixoutMixGenUI.instance.editmixout2tofield);
    imap.put("mixgen_edit_mixout_comments_textarea", EditMixoutMixGenUI.instance.editmixout2commentsfield);
    imap.put("mixgen_edit_mixout_bpm_diff_field", EditMixoutMixGenUI.instance.editmixout2bpmdifffield);
    imap.put("mixgen_edit_mixout_calculate_bpm_diff_button", EditMixoutMixGenUI.instance.editmixout2calcbpmdiffbutton);
    imap.put("mixgen_edit_mixout_rank_field", EditMixoutMixGenUI.instance.editmixout2scorefield);
    imap.put("mixgen_edit_mixout_addon_checkbox", EditMixoutMixGenUI.instance.editmixout2addoncb);
    imap.put("songtrail_edit_mixout_cancel_button", EditMixoutSongTrailUI.instance.editmixoutcancelbutton);
    imap.put("songtrail_edit_mixout_ok_button", EditMixoutSongTrailUI.instance.editmixoutokbutton);
    imap.put("songtrail_edit_mixout_from_field", EditMixoutSongTrailUI.instance.editmixoutfromfield);
    imap.put("songtrail_edit_mixout_to_field", EditMixoutSongTrailUI.instance.editmixouttofield);
    imap.put("songtrail_edit_mixout_comments_textarea", EditMixoutSongTrailUI.instance.editmixoutcommentsfield);
    imap.put("songtrail_edit_mixout_bpm_diff_field", EditMixoutSongTrailUI.instance.editmixoutbpmdifffield);
    imap.put("songtrail_edit_mixout_calculate_bpm_diff_button", EditMixoutSongTrailUI.instance.editmixoutcalcbpmdiffbutton);
    imap.put("songtrail_edit_mixout_rank_field", EditMixoutSongTrailUI.instance.editmixoutscorefield);
    imap.put("songtrail_edit_mixout_addon_checkbox", EditMixoutSongTrailUI.instance.editmixoutaddoncb);
    imap.put("edit_song_rating_1_checkbox", EditSongUI.instance.rating1checkbox);
    imap.put("edit_song_rating_2_checkbox", EditSongUI.instance.rating2checkbox);
    imap.put("edit_song_rating_3_checkbox", EditSongUI.instance.rating3checkbox);
    imap.put("edit_song_rating_4_checkbox", EditSongUI.instance.rating4checkbox);
    imap.put("edit_song_rating_5_checkbox", EditSongUI.instance.rating5checkbox);
    imap.put("edit_song_bpm_detection_accuracy_slider", EditSongUI.instance.editsong_bpmaccuracy);
    imap.put("edit_song_key_detection_accuracy_slider", EditSongUI.instance.editsong_keyaccuracy);        
    imap.put("edit_song_album_cover_button", EditSongUI.instance.albumcover_btn);
    imap.put("edit_song_styles_tree", EditSongUI.instance.editsongstylestree);
    imap.put("edit_song_styles_clear_button", EditSongUI.instance.editsongsclearstylesbt);
    imap.put("edit_song_style_add_button", EditSongUI.instance.editsongsaddstylebt);
    imap.put("edit_song_style_edit_button", EditSongUI.instance.editsongseditstylebt);
    imap.put("edit_song_styles_delete_button", EditSongUI.instance.editsongsdeletestylebt);
    imap.put("edit_song_ok_button", EditSongUI.instance.editsongsokbutton);
    imap.put("edit_song_cancel_button", EditSongUI.instance.editsongscancelbutton);
    imap.put("edit_song_artist_field", EditSongUI.instance.editsongsartistfield);
    imap.put("edit_song_album_field", EditSongUI.instance.editsongsalbumfield);
    imap.put("edit_song_track_field", EditSongUI.instance.editsongstrackfield);
    imap.put("edit_song_time_field", EditSongUI.instance.editsongstimefield);
    imap.put("edit_song_time_signature_field", EditSongUI.instance.editsongstimesignaturefield);
    imap.put("edit_song_remix_field", EditSongUI.instance.editsongsremixerfield);
    imap.put("edit_song_title_field", EditSongUI.instance.editsongstitlefield);
    imap.put("edit_song_bpm_start_field", EditSongUI.instance.editsongsstartbpmfield);
    imap.put("edit_song_bpm_start_tapper_button", EditSongUI.instance.editsongsstartbpmtapbutton);
    imap.put("edit_song_bpm_start_reset_button", EditSongUI.instance.editsongsstartbpmresetbutton);
    imap.put("edit_song_bpm_end_field", EditSongUI.instance.editsongsendbpmfield);
    imap.put("edit_song_bpm_end_tapper_button", EditSongUI.instance.editsongsendbpmtapbutton);
    imap.put("edit_song_bpm_end_reset_button", EditSongUI.instance.editsongsendbpmresetbutton);
    imap.put("edit_song_key_start_field", EditSongUI.instance.editsongsstartkeyfield);
    imap.put("edit_song_key_end_field", EditSongUI.instance.editsongsendkeyfield);
    imap.put("edit_song_key_detect_button", EditSongUI.instance.editsongsdetectkeybutton);
    imap.put("edit_song_comments_textarea", EditSongUI.instance.editsongscommentsfield);
    imap.put("edit_song_custom_label_1", EditSongUI.instance.editcustomfieldlabel1);
    imap.put("edit_song_custom_field_1", EditSongUI.instance.editcustomfieldtext1);
    imap.put("edit_song_custom_label_2", EditSongUI.instance.editcustomfieldlabel2);
    imap.put("edit_song_custom_field_2", EditSongUI.instance.editcustomfieldtext2);
    imap.put("edit_song_custom_label_3", EditSongUI.instance.editcustomfieldlabel3);
    imap.put("edit_song_custom_field_3", EditSongUI.instance.editcustomfieldtext3);
    imap.put("edit_song_custom_label_4", EditSongUI.instance.editcustomfieldlabel4);
    imap.put("edit_song_custom_field_4", EditSongUI.instance.editcustomfieldtext4);
    imap.put("edit_song_filename_field", EditSongUI.instance.editsongsfilenamefield);
    imap.put("edit_song_browse_button", EditSongUI.instance.editsongsbrowsebutton);
    imap.put("edit_song_tag_write_button", EditSongUI.instance.editsongswritetagbutton);
    imap.put("edit_song_rename_button", EditSongUI.instance.editsongsrenamebutton);
    imap.put("edit_song_tag_read_button", EditSongUI.instance.editsongsreadtagbutton);
    imap.put("edit_song_play_button", EditSongUI.instance.editsongsplaybutton);
    imap.put("edit_song_analog_checkbox", EditSongUI.instance.editsongsvinylonly);
    imap.put("edit_song_digital_checkbox", EditSongUI.instance.editsongsnonvinylonly);
    imap.put("edit_song_disabled_checkbox", EditSongUI.instance.editsongsdisabled);
    imap.put("exclude_songs_table", ExcludeUI.instance.excludetable);
    imap.put("exclude_songs_ok_button", ExcludeUI.instance.excludeokbutton);
    imap.put("exclude_songs_remove_button", ExcludeUI.instance.removeexcludebutton);

    imap.put("rename_files_pattern_field", RenameFilesUI.instance.renamepatternfield);
    imap.put("rename_files_ok_button", RenameFilesUI.instance.ok_button);
    imap.put("rename_files_cancel_button", RenameFilesUI.instance.cancel_button);    

    imap.put("generate_playlist_max_size_field", GeneratePlaylistUI.instance.numgeneratefield);
    imap.put("generate_playlist_ok_button", GeneratePlaylistUI.instance.numgenerateokbutton);
    imap.put("generate_playlist_cancel_button", GeneratePlaylistUI.instance.numgeneratecancelbutton);

    imap.put("organize_files_directory_field", OrganizeFilesUI.instance.organize_directory_field);
    imap.put("organize_files_backup_directory_field", OrganizeFilesUI.instance.backup_directory_field);
    imap.put("organize_files_directory_browse_button", OrganizeFilesUI.instance.organize_directory_browse_button);
    imap.put("organize_files_backup_directory_browse_button", OrganizeFilesUI.instance.backup_directory_browse_button);
    imap.put("organize_files_ok_button", OrganizeFilesUI.instance.ok_button);
    imap.put("organize_files_cancel_button", OrganizeFilesUI.instance.cancel_button);
    imap.put("organize_files_delete_old_files_and_directories_checkbox", OrganizeFilesUI.instance.delete_old_files);
    imap.put("organize_files_rename_files_checkbox", OrganizeFilesUI.instance.rename_files);
    imap.put("organize_files_write_tags_checkbox", OrganizeFilesUI.instance.write_tags);
    
    imap.put("options_library_root_directory_browse_button", OptionsUI.instance.browsemusicdirectory);
    imap.put("options_library_root_directory_field", OptionsUI.instance.rootMusicDirectory);
    imap.put("options_library_automatically_scan_for_new_songs", OptionsUI.instance.autoScanForNewMusic);
    imap.put("options_library_scan_now_button", OptionsUI.instance.scanNowButton);
    
    imap.put("format_bpm_values_decimal_size_field", FormatBpmsUI.instance.numdecimalplaces);
    imap.put("format_bpm_values_scale_field", FormatBpmsUI.instance.scale);
    imap.put("format_bpm_values_ok_button", FormatBpmsUI.instance.formatbpmsokbutton);
    imap.put("delete_songs_delete_files_checkbox", DeleteSongsUI.instance.deleteFilesCheckbox);
    imap.put("delete_songs_ok_button", DeleteSongsUI.instance.deleteokbutton);
    imap.put("import_mixmeister_artist_checkbox", ImportMixmeisterUI.instance.mixmeisterartist);
    imap.put("import_mixmeister_album_checkbox", ImportMixmeisterUI.instance.mixmeisteralbum);
    imap.put("import_mixmeister_title_checkbox", ImportMixmeisterUI.instance.mixmeistersongname);
    imap.put("import_mixmeister_time_checkbox", ImportMixmeisterUI.instance.mixmeistertime);
    imap.put("import_mixmeister_bpm_checkbox", ImportMixmeisterUI.instance.mixmeisterbpm);
    imap.put("import_mixmeister_key_checkbox", ImportMixmeisterUI.instance.mixmeisterkey);
    imap.put("import_mixmeister_genre_checkbox", ImportMixmeisterUI.instance.mixmeistergenre);
    imap.put("import_mixmeister_overwrite_checkbox", ImportMixmeisterUI.instance.mixmeister_ifprevious);
    imap.put("import_mixmeister_ok_button", ImportMixmeisterUI.instance.importmixmeisterokbutton);
    imap.put("import_mixmeister_cancel_button", ImportMixmeisterUI.instance.importmixmeistercancelbutton);
    
    imap.put("import_itunes_song_info_checkbox", ImportITunesUI.instance.import_songinfo);
    imap.put("import_itunes_time_checkbox", ImportITunesUI.instance.import_time);
    imap.put("import_itunes_bpm_checkbox", ImportITunesUI.instance.import_bpm);
    imap.put("import_itunes_genre_checkbox", ImportITunesUI.instance.import_genre);
    imap.put("import_itunes_rating_checkbox", ImportITunesUI.instance.import_rating);
    imap.put("import_itunes_comments_checkbox", ImportITunesUI.instance.import_comments);
    imap.put("import_itunes_update_only_checkbox", ImportITunesUI.instance.updateonly);
    imap.put("import_itunes_overwrite_checkbox", ImportITunesUI.instance.overwrite);
    imap.put("import_itunes_ok_button", ImportITunesUI.instance.okbutton);
    imap.put("import_itunes_cancel_button", ImportITunesUI.instance.cancelbutton);

    imap.put("import_traktor_song_info_checkbox", ImportTraktorUI.instance.import_songinfo);
    imap.put("import_traktor_time_checkbox", ImportTraktorUI.instance.import_time);
    imap.put("import_traktor_bpm_checkbox", ImportTraktorUI.instance.import_bpm);
    imap.put("import_traktor_key_checkbox", ImportTraktorUI.instance.import_key);
    imap.put("import_traktor_genre_checkbox", ImportTraktorUI.instance.import_genre);
    imap.put("import_traktor_comments_checkbox", ImportTraktorUI.instance.import_comments);
    imap.put("import_traktor_update_only_checkbox", ImportTraktorUI.instance.updateonly);
    imap.put("import_traktor_overwrite_checkbox", ImportTraktorUI.instance.overwrite);
    imap.put("import_traktor_ok_button", ImportTraktorUI.instance.okbutton);
    imap.put("import_traktor_cancel_button", ImportTraktorUI.instance.cancelbutton);
    
    imap.put("import_mixvibes_artist_checkbox", ImportMixvibesUI.instance.artist);
    imap.put("import_mixvibes_album_checkbox", ImportMixvibesUI.instance.album);
    imap.put("import_mixvibes_title_checkbox", ImportMixvibesUI.instance.title);
    imap.put("import_mixvibes_remix_checkbox", ImportMixvibesUI.instance.remix);
    imap.put("import_mixvibes_time_checkbox", ImportMixvibesUI.instance.time);
    imap.put("import_mixvibes_bpm_checkbox", ImportMixvibesUI.instance.bpm);
    imap.put("import_mixvibes_comments_checkbox", ImportMixvibesUI.instance.comment);
    imap.put("import_mixvibes_genre_checkbox", ImportMixvibesUI.instance.style);
    imap.put("import_mixvibes_overwrite_checkbox", ImportMixvibesUI.instance.overwrite_ifprevious);
    imap.put("import_mixvibes_ok_button", ImportMixvibesUI.instance.importmixvibesokbutton);
    imap.put("import_mixvibes_cancel_button", ImportMixvibesUI.instance.importmixvibescancelbutton);
    
    imap.put("import_spreadsheet_cancel_button", ImportSpreadsheetUI.instance.importcancelbutton);
    imap.put("import_spreadsheet_ok_button", ImportSpreadsheetUI.instance.importokbutton);
    imap.put("import_spreadsheet_artist_combobox", ImportSpreadsheetUI.instance.importexcelartistcombo);
    imap.put("import_spreadsheet_album_combobox", ImportSpreadsheetUI.instance.importexcelalbumcombo);
    imap.put("import_spreadsheet_track_combobox", ImportSpreadsheetUI.instance.importexceltrackcombo);
    imap.put("import_spreadsheet_title_combobox", ImportSpreadsheetUI.instance.importexceltitlecombo);
    imap.put("import_spreadsheet_remix_combobox", ImportSpreadsheetUI.instance.importexcelremixercombo);
    imap.put("import_spreadsheet_time_combobox", ImportSpreadsheetUI.instance.importexceltimecombo);
    imap.put("import_spreadsheet_time_signature_combobox", ImportSpreadsheetUI.instance.importexceltimesigcombo);
    imap.put("import_spreadsheet_bpm_start_combobox", ImportSpreadsheetUI.instance.importexcelstartbpmcombo);
    imap.put("import_spreadsheet_bpm_end_combobox", ImportSpreadsheetUI.instance.importexcelendbpmcombo);
    imap.put("import_spreadsheet_key_start_combobox", ImportSpreadsheetUI.instance.importexcelstartkeycombo);
    imap.put("import_spreadsheet_key_end_combobox", ImportSpreadsheetUI.instance.importexcelendkeycombo);
    imap.put("import_spreadsheet_comments_combobox", ImportSpreadsheetUI.instance.importexcelcommentscombo);
    imap.put("import_spreadsheet_custom_combobox_1", ImportSpreadsheetUI.instance.importexceluser1combo);
    imap.put("import_spreadsheet_custom_combobox_2", ImportSpreadsheetUI.instance.importexceluser2combo);
    imap.put("import_spreadsheet_custom_combobox_3", ImportSpreadsheetUI.instance.importexceluser3combo);
    imap.put("import_spreadsheet_custom_combobox_4", ImportSpreadsheetUI.instance.importexceluser4combo);
    imap.put("import_spreadsheet_custom_label_1", ImportSpreadsheetUI.instance.user1);
    imap.put("import_spreadsheet_custom_label_2", ImportSpreadsheetUI.instance.user2);
    imap.put("import_spreadsheet_custom_label_3", ImportSpreadsheetUI.instance.user3);
    imap.put("import_spreadsheet_custom_label_4", ImportSpreadsheetUI.instance.user4);
    imap.put("import_spreadsheet_filename_combobox", ImportSpreadsheetUI.instance.importexcelfilenamecombo);
    imap.put("key_format_csharp_radiobutton", KeyFormatUI.csharpoption);
    imap.put("key_format_dflat_radiobutton", KeyFormatUI.dflatoption);
    imap.put("key_format_dsharp_radiobutton", KeyFormatUI.dsharpoption);
    imap.put("key_format_eflat_radiobutton", KeyFormatUI.eflatoption);
    imap.put("key_format_fsharp_radiobutton", KeyFormatUI.fsharpoption);
    imap.put("key_format_gflat_radiobutton", KeyFormatUI.gflatoption);
    imap.put("key_format_gsharp_radiobutton", KeyFormatUI.gsharpoption);
    imap.put("key_format_aflat_radiobutton", KeyFormatUI.aflatoption);
    imap.put("key_format_asharp_radiobutton", KeyFormatUI.asharpoption);
    imap.put("key_format_bflat_radiobutton", KeyFormatUI.bflatoption);
    imap.put("key_format_ok_button", KeyFormatUI.instance.customizekeysbutton);
    imap.put("mixshare_login_username_field", LoginPromptUI.instance.initialusernameprompt);
    imap.put("mixshare_login_password_field", LoginPromptUI.instance.initialpasswordprompt);
    imap.put("mixshare_login_email_field", LoginPromptUI.instance.initialemailprompt);
    imap.put("mixshare_login_website_field", LoginPromptUI.instance.initialuserwebsite);
    imap.put("mixshare_login_ok_button", LoginPromptUI.instance.loginpromptokbutton);
    imap.put("media_player_current_song_field", MediaPlayerUI.instance.songplayingfield);
    imap.put("media_player_tracker", MediaPlayerUI.instance.songplayingtracker);
    imap.put("media_player_volume_slider", MediaPlayerUI.instance.songplayingvolume);
    imap.put("media_player_randomize_button", MediaPlayerUI.instance.randomizeplaylist);
    imap.put("media_player_pause_button", MediaPlayerUI.instance.songplayingpausebutton);
    imap.put("media_player_current_time_label", AudioPlayer.songplayingtime);
    imap.put("media_player_play_button", MediaPlayerUI.instance.songplayingplaybutton);
    imap.put("media_player_back_button", MediaPlayerUI.instance.songplayingbackbutton);
    imap.put("media_player_stop_button", MediaPlayerUI.instance.songplayingstopbutton);
    imap.put("media_player_next_button", MediaPlayerUI.instance.songplayingnextbutton);
    imap.put("media_player_ok_button", MediaPlayerUI.instance.songplayingokbutton);
    imap.put("mix_generator_results_list", MixGeneratorUI.instance.mixgenlist);
    imap.put("mix_generator_ok_button", MixGeneratorUI.instance.mixsetokbutton);
    imap.put("mix_generator_load_button", MixGeneratorUI.instance.loadmixsetbutton);
    imap.put("mix_generator_save_button", MixGeneratorUI.instance.savemixsetbutton);
    imap.put("mix_generator_append_button", MixGeneratorUI.instance.appendtosongtrail);
    imap.put("mix_generator_export_button", MixGeneratorUI.instance.exporttosongtrail);
    imap.put("mix_generator_save_text_button", MixGeneratorUI.instance.savetexttrail);
    imap.put("mix_generator_generate_button", MixGeneratorUI.instance.generatemixsetbutton);
    imap.put("mix_generator_cancel_button", MixGeneratorUI.instance.cancelmixsetbutton);
    imap.put("mix_generator_limit_bpm_range_checkbox", MixGeneratorUI.instance.controlbpmrangecb);
    imap.put("mix_generator_closest_match_checkbox", MixGeneratorUI.instance.closestmatchcheckbox);
    imap.put("mix_generator_avoid_song_trail_checkbox", MixGeneratorUI.instance.avoidsongtrailcheckbox);
    imap.put("mix_generator_digital_radiobutton", MixGeneratorUI.instance.usenonvinylrb);
    imap.put("mix_generator_analog_radiobutton", MixGeneratorUI.instance.usevinylrb);
    imap.put("mix_generator_all_radiobutton", MixGeneratorUI.instance.useallrb);
    imap.put("mix_generator_styles_clear_button", MixGeneratorUI.instance.clearmixstylesbutton);
    imap.put("mix_generator_styles_tree", MixGeneratorUI.instance.mixsetstylestree);
    imap.put("mix_generator_starts_with_clear_button", MixGeneratorUI.instance.clearstartswith);
    imap.put("mix_generator_starts_with_field", MixGeneratorUI.instance.startswithfield);
    imap.put("mix_generator_exclude_keywords_field", MixGeneratorUI.instance.excludekeywordsfield);
    imap.put("mix_generator_include_keywords_field", MixGeneratorUI.instance.includekeywordsfield);
    imap.put("mix_generator_weak_include_checkbox", MixGeneratorUI.instance.weakincludecb);
    imap.put("mix_generator_song_number_field", MixGeneratorUI.instance.numsongsmixsetfield);
    imap.put("mix_generator_minimum_rank_checkbox", MixGeneratorUI.instance.minrankcb);
    imap.put("mix_generator_minimum_rank_field", MixGeneratorUI.instance.minrankfield);
    imap.put("mix_generator_results_table", MixGeneratorUI.instance.mixgenlist);
    imap.put("mix_generator_fast_back_button", MixGeneratorUI.instance.fastprevmixsetbutton);
    imap.put("mix_generator_back_button", MixGeneratorUI.instance.prevmixsetbutton);
    imap.put("mix_generator_next_button", MixGeneratorUI.instance.nextmixsetbutton);
    imap.put("mix_generator_fast_next_button", MixGeneratorUI.instance.fastnextmixsetbutton);
    imap.put("mix_generator_max_results_field", MixGeneratorUI.instance.maxresultsfield);
    imap.put("mix_generator_average_rank_field", MixGeneratorUI.instance.avgmixscorefield);
    imap.put("mix_generator_average_rank_result_field", MixGeneratorUI.instance.mixrankresults);
    imap.put("mix_generator_starts_with_song_list", MixStartsWithUI.instance.mixstartswithlist);
    imap.put("mix_generator_starts_with_search_button", MixStartsWithUI.instance.mixstartsearchbutton);
    imap.put("mix_generator_starts_with_cancel_button", MixStartsWithUI.instance.mixstartcancelbutton);
    imap.put("mix_generator_starts_with_ok_button", MixStartsWithUI.instance.mixstartokbutton);
    imap.put("mix_generator_starts_with_search_field", MixStartsWithUI.instance.mixstartsearchfield);
    imap.put("options_ok_button", OptionsUI.instance.optionsokbutton);
    imap.put("options_look_and_feel_combobox", OptionsUI.instance.lookandfeelcombo);
    imap.put("option_bpm_blink_rate", OptionsUI.instance.option_bpm_blink_rate);
    imap.put("options_song_tag_write_empty_fields_checkbox", OptionsUI.instance.tagswriteempty);
    imap.put("options_song_tag_write_bpm_decimals_checkbox", OptionsUI.instance.tagswritebpmdecimals);    
    imap.put("options_disable_field_autocomplete_checkbox", OptionsUI.instance.disableautocomplete);
    imap.put("options_enable_debug_logging_checkbox", OptionsUI.instance.enabledebug);
    imap.put("options_full_styles_layout_checkbox", OptionsUI.instance.fullstyleslayout);
    imap.put("options_display_styles_on_left", OptionsUI.instance.displaystylesonleft);   
    imap.put("options_show_previous_song_checkbox", OptionsUI.instance.showprevioustrack);
    imap.put("options_key_format_combobox", OptionsUI.instance.keyformatcombo);
    imap.put("options_song_custom_field_1_tag_combo", OptionsUI.instance.custom_field_1_tag_combo);
    imap.put("options_song_custom_field_2_tag_combo", OptionsUI.instance.custom_field_2_tag_combo);
    imap.put("options_song_custom_field_3_tag_combo", OptionsUI.instance.custom_field_3_tag_combo);
    imap.put("options_song_custom_field_4_tag_combo", OptionsUI.instance.custom_field_4_tag_combo);        
    imap.put("options_prevent_repeats_checkbox", OptionsUI.instance.preventrepeats);
    imap.put("options_disable_keylock_checkbox", OptionsUI.instance.disablekeylockfunctionality);
    imap.put("options_os_media_player_checkbox", OptionsUI.instance.useosplayer);
    imap.put("options_browse_media_player_button", OptionsUI.instance.browsemediaplayer);
    imap.put("options_reset_os_media_player_button", OptionsUI.instance.resetmediaplayer);
    imap.put("options_smooth_bpm_slider_checkbox", OptionsUI.instance.smoothbpmslider);
    imap.put("options_auto_search_checkbox", OptionsUI.instance.autobpmkeysearch);
    imap.put("options_search_table_disable_multiple_select", OptionsUI.instance.disable_multiple_delect);
    imap.put("options_auto_highlight_styles_checkbox", OptionsUI.instance.autohighlightstyles);
    imap.put("options_selected_style_similarity_checkbox", OptionsUI.instance.useselectedstylesimilarity);
    imap.put("options_disable_tooltips_checkbox", OptionsUI.instance.disabletooltips);
    imap.put("options_disable_autosave_checkbox", OptionsUI.instance.disableautosave);
    imap.put("options_portable_music_checkbox", OptionsUI.instance.portablemusicmode);
    imap.put("options_auto_update_song_paths_checkbox", OptionsUI.instance.autoupdatepaths);
    imap.put("options_add_mixout_on_double_click", OptionsUI.instance.addmixoutondoubleclick);
    imap.put("options_search_strict_styles_match_checkbox", OptionsUI.instance.strictstylessearch);
    imap.put("options_add_mixout_change_current_song", OptionsUI.instance.addmixoutchangesong);
    imap.put("filter_1_values_list", OptionsUI.instance.filter1list);
    imap.put("filter_2_values_list", OptionsUI.instance.filter2list);
    imap.put("filter_3_values_list", OptionsUI.instance.filter3list);
    imap.put("options_song_filter_use_styles", OptionsUI.instance.filterusestyles);
    imap.put("options_song_filter_affected_by_search", OptionsUI.instance.filteraffectedbysearch);
    imap.put("options_song_filter_single_selection_mode", OptionsUI.instance.filtersingleselectmode);    
    imap.put("options_enable_filter", OptionsUI.instance.enablefilter);
    imap.put("search_use_filtering", OptionsUI.instance.usefiltering);
    imap.put("options_song_filter_auto_search", OptionsUI.instance.filterautosearch);    
    imap.put("options_song_filter_2_disable", OptionsUI.instance.filter2disable);
    imap.put("options_song_filter_3_disable", OptionsUI.instance.filter3disable);    
    imap.put("options_album_cover_thumbnail_width_field", OptionsUI.instance.albumcoverthumbwidth);    
    imap.put("options_search_column_actual_key_checkbox", OptionsUI.instance.searchcolumn_actualkey);
    imap.put("options_search_column_actual_key_code_checkbox", OptionsUI.instance.searchcolumn_actualkeycode);
    imap.put("options_search_column_pitch_shift_checkbox", OptionsUI.instance.searchcolumn_pitchshift);
    imap.put("options_search_column_key_accuracy_checkbox", OptionsUI.instance.searchcolumn_keyaccuracy);
    imap.put("options_search_column_bpm_accuracy_checkbox", OptionsUI.instance.searchcolumn_bpmaccuracy);
    imap.put("options_search_column_beat_intensity_checkbox", OptionsUI.instance.searchcolumn_beatintensity);
    imap.put("options_search_column_song_id_with_info_checkbox", OptionsUI.instance.searchcolumn_songidwinfo);
    imap.put("options_search_column_song_id_checkbox", OptionsUI.instance.searchcolumn_songid);
    imap.put("options_search_column_short_id_with_info_checkbox", OptionsUI.instance.searchcolumn_shortidwinfo);
    imap.put("options_search_column_short_id_checkbox", OptionsUI.instance.searchcolumn_shortid);
    imap.put("options_search_column_artist_checkbox", OptionsUI.instance.searchcolumn_artist);
    imap.put("options_search_column_album_checkbox", OptionsUI.instance.searchcolumn_album);
    imap.put("options_search_column_track_checkbox", OptionsUI.instance.searchcolumn_track);
    imap.put("options_search_column_title_checkbox", OptionsUI.instance.searchcolumn_title);
    imap.put("options_search_column_remix_checkbox", OptionsUI.instance.searchcolumn_remixer);
    imap.put("options_search_column_time_checkbox", OptionsUI.instance.searchcolumn_length);
    imap.put("options_search_column_rating_checkbox", OptionsUI.instance.searchcolumn_rating);
    imap.put("options_search_column_bpm_shift_checkbox", OptionsUI.instance.searchcolumn_bpmdiff);
    imap.put("options_search_column_bpm_diff_checkbox", OptionsUI.instance.searchcolumn_relbpmdiff);
    imap.put("options_search_column_bpm_checkbox", OptionsUI.instance.searchcolumn_bpm);
    imap.put("options_search_column_bpm_start_checkbox", OptionsUI.instance.searchcolumn_startbpm);
    imap.put("options_search_column_bpm_end_checkbox", OptionsUI.instance.searchcolumn_endbpm);
    imap.put("options_search_column_key_checkbox", OptionsUI.instance.searchcolumn_key);
    imap.put("options_search_column_key_start_checkbox", OptionsUI.instance.searchcolumn_startkey);
    imap.put("options_search_column_key_end_checkbox", OptionsUI.instance.searchcolumn_endkey);
    imap.put("options_search_column_key_lock_checkbox", OptionsUI.instance.searchcolumn_keylock);
    imap.put("options_search_column_key_relation_checkbox", OptionsUI.instance.searchcolumn_keymode);
    imap.put("options_search_column_key_code_checkbox", OptionsUI.instance.searchcolumn_keycode);
    imap.put("options_search_column_artist_similarity_checkbox", OptionsUI.instance.searchcolumn_artistsimilarity);
    imap.put("options_search_column_style_similarity_checkbox", OptionsUI.instance.searchcolumn_stylesimilarity);
    imap.put("options_search_column_color_similarity_checkbox", OptionsUI.instance.searchcolumn_colorsimilarity);
    imap.put("options_search_column_time_signature_checkbox", OptionsUI.instance.searchcolumn_timesig);
    imap.put("options_search_column_custom_field_1_checkbox", OptionsUI.instance.searchcolumn_user1);
    imap.put("options_search_column_custom_field_2_checkbox", OptionsUI.instance.searchcolumn_user2);
    imap.put("options_search_column_custom_field_3_checkbox", OptionsUI.instance.searchcolumn_user3);
    imap.put("options_search_column_custom_field_4_checkbox", OptionsUI.instance.searchcolumn_user4);
    imap.put("options_search_column_date_added_checkbox", OptionsUI.instance.searchcolumn_dateadded);
    imap.put("options_search_column_last_modified_checkbox", OptionsUI.instance.searchcolumn_lastmodified);
    imap.put("options_search_column_num_plays_checkbox", OptionsUI.instance.searchcolumn_timesplayed);
    imap.put("options_search_column_replay_gain_checkbox", OptionsUI.instance.searchcolumn_replaygain);
    imap.put("options_search_column_num_mixouts_checkbox", OptionsUI.instance.searchcolumn_nummixouts);
    imap.put("options_search_column_num_addons_checkbox", OptionsUI.instance.searchcolumn_numaddons);
    imap.put("options_search_column_comments_checkbox", OptionsUI.instance.searchcolumn_comments);
    imap.put("options_search_column_styles_checkbox", OptionsUI.instance.searchcolumn_styles);
    imap.put("options_search_column_analog_checkbox", OptionsUI.instance.searchcolumn_vinyl);
    imap.put("options_search_column_digital_checkbox", OptionsUI.instance.searchcolumn_nonvinyl);
    imap.put("options_search_column_disabled_checkbox", OptionsUI.instance.searchcolumn_disabled);
    imap.put("options_search_column_filename_checkbox", OptionsUI.instance.searchcolumn_filename);
    imap.put("options_search_column_has_album_cover_checkbox", OptionsUI.instance.searchcolumn_hasalbumcover);
    imap.put("options_search_column_album_cover_checkbox", OptionsUI.instance.searchcolumn_albumcover);
    imap.put("options_search_columns_apply_button", OptionsUI.instance.applysearchcolumns);
    imap.put("options_search_columns_reset_button", OptionsUI.instance.defaultsearchcolumns);
    imap.put("options_search_auto_scroll_checkbox", OptionsUI.instance.searchtableautoscroll);
    imap.put("options_search_auto_scroll_checkbox", OptionsUI.instance.searchtableautoscroll);
    imap.put("options_search_in_place_editing_checkbox", OptionsUI.instance.searchinplaceediting);
    imap.put("options_search_enforce_excludes_checkbox", OptionsUI.instance.excludesongsondonottrylist);
    imap.put("options_search_do_not_repeat_checkbox", OptionsUI.instance.findallpreventrepeats);
    imap.put("options_search_within_styles_checkbox", OptionsUI.instance.searchwithinstyles);
    imap.put("options_search_bpm_multiples_checkbox", OptionsUI.instance.evenbpmmultiples);
    imap.put("options_search_loose_key_match_checkbox", OptionsUI.instance.ignoremajorminor);
    imap.put("options_search_auto_clear_checkbox", OptionsUI.instance.clearsearchautomatically);
    imap.put("options_automatically_search_on_user_input", OptionsUI.instance.automaticsearchonuserinput);
    imap.put("options_search_exclude_same_vinyl_checkbox", OptionsUI.instance.excludesongsonsamerecord);
    imap.put("options_search_exclude_no_keylock_checkbox", OptionsUI.instance.searchexcludenokeylock);
    imap.put("options_search_hide_disabled_checkbox", OptionsUI.instance.donotshowdisabledosngs);
    imap.put("options_search_lock_pitch_shift_checkbox", OptionsUI.instance.lockpitchshift);
    imap.put("options_song_filter_1_combo", OptionsUI.instance.filter1options);
    imap.put("options_song_filter_2_combo", OptionsUI.instance.filter2options);
    imap.put("options_song_filter_3_combo", OptionsUI.instance.filter3options);
    imap.put("filter_1_clear_button", OptionsUI.instance.filter1clear);
    imap.put("filter_2_clear_button", OptionsUI.instance.filter2clear);
    imap.put("filter_3_clear_button", OptionsUI.instance.filter3clear);
    imap.put("filter_1_selectall_button", OptionsUI.instance.filter1selectall);
    imap.put("filter_2_selectall_button", OptionsUI.instance.filter2selectall);
    imap.put("filter_3_selectall_button", OptionsUI.instance.filter3selectall);
    imap.put("options_mixout_column_actual_key_checkbox", OptionsUI.instance.mixoutcolumn_actualkey);
    imap.put("options_mixout_column_actual_key_code_checkbox", OptionsUI.instance.mixoutcolumn_actualkeycode);
    imap.put("options_mixout_column_pitch_shift_checkbox", OptionsUI.instance.mixoutcolumn_pitchshift);
    imap.put("options_mixout_column_key_accuracy_checkbox", OptionsUI.instance.mixoutcolumn_keyaccuracy);
    imap.put("options_mixout_column_bpm_accuracy_checkbox", OptionsUI.instance.mixoutcolumn_bpmaccuracy);
    imap.put("options_mixout_column_beat_intensity_checkbox", OptionsUI.instance.mixoutcolumn_beatintensity);
    imap.put("options_mixout_column_song_id_with_info_checkbox", OptionsUI.instance.mixoutcolumn_songidwinfo);
    imap.put("options_mixout_column_song_id_checkbox", OptionsUI.instance.mixoutcolumn_songid);
    imap.put("options_mixout_column_short_id_with_info_checkbox", OptionsUI.instance.mixoutcolumn_shortidwinfo);
    imap.put("options_mixout_column_short_id_checkbox", OptionsUI.instance.mixoutcolumn_shortid);
    imap.put("options_mixout_column_artist_checkbox", OptionsUI.instance.mixoutcolumn_artist);
    imap.put("options_mixout_column_album_checkbox", OptionsUI.instance.mixoutcolumn_album);
    imap.put("options_mixout_column_track_checkbox", OptionsUI.instance.mixoutcolumn_track);
    imap.put("options_mixout_column_title_checkbox", OptionsUI.instance.mixoutcolumn_title);
    imap.put("options_mixout_column_remix_checkbox", OptionsUI.instance.mixoutcolumn_remixer);
    imap.put("options_mixout_column_rank_checkbox", OptionsUI.instance.mixoutcolumn_rank);
    imap.put("options_mixout_column_addon_checkbox", OptionsUI.instance.mixoutcolumn_isaddon);
    imap.put("options_mixout_column_mix_comments_checkbox", OptionsUI.instance.mixoutcolumn_mixcomments);
    imap.put("options_mixout_column_time_checkbox", OptionsUI.instance.mixoutcolumn_length);
    imap.put("options_mixout_column_rating_checkbox", OptionsUI.instance.mixoutcolumn_rating);
    imap.put("options_mixout_column_bpm_shift_checkbox", OptionsUI.instance.mixoutcolumn_bpmdiff);
    imap.put("options_mixout_column_bpm_diff_checkbox", OptionsUI.instance.mixoutcolumn_relbpmdiff);
    imap.put("options_mixout_column_bpm_checkbox", OptionsUI.instance.mixoutcolumn_bpm);
    imap.put("options_mixout_column_bpm_start_checkbox", OptionsUI.instance.mixoutcolumn_startbpm);
    imap.put("options_mixout_column_bpm_end_checkbox", OptionsUI.instance.mixoutcolumn_endbpm);
    imap.put("options_mixout_column_key_checkbox", OptionsUI.instance.mixoutcolumn_key);
    imap.put("options_mixout_column_key_start_checkbox", OptionsUI.instance.mixoutcolumn_startkey);
    imap.put("options_mixout_column_key_end_checkbox", OptionsUI.instance.mixoutcolumn_endkey);
    imap.put("options_mixout_column_key_lock_checkbox", OptionsUI.instance.mixoutcolumn_keylock);
    imap.put("options_mixout_column_key_relation_checkbox", OptionsUI.instance.mixoutcolumn_keymode);
    imap.put("options_mixout_column_key_code_checkbox", OptionsUI.instance.mixoutcolumn_keycode);
    imap.put("options_mixout_column_artist_similarity_checkbox", OptionsUI.instance.mixoutcolumn_artistsimilarity);
    imap.put("options_mixout_column_style_similarity_checkbox", OptionsUI.instance.mixoutcolumn_stylesimilarity);
    imap.put("options_mixout_column_color_similarity_checkbox", OptionsUI.instance.mixoutcolumn_colorsimilarity);
    imap.put("options_mixout_column_time_signature_checkbox", OptionsUI.instance.mixoutcolumn_timesig);
    imap.put("options_mixout_column_custom_field_1_checkbox", OptionsUI.instance.mixoutcolumn_user1);
    imap.put("options_mixout_column_custom_field_2_checkbox", OptionsUI.instance.mixoutcolumn_user2);
    imap.put("options_mixout_column_custom_field_3_checkbox", OptionsUI.instance.mixoutcolumn_user3);
    imap.put("options_mixout_column_custom_field_4_checkbox", OptionsUI.instance.mixoutcolumn_user4);
    imap.put("options_mixout_column_date_added_checkbox", OptionsUI.instance.mixoutcolumn_dateadded);
    imap.put("options_mixout_column_last_modified_checkbox", OptionsUI.instance.mixoutcolumn_lastmodified);
    imap.put("options_mixout_column_num_plays_checkbox", OptionsUI.instance.mixoutcolumn_timesplayed);
    imap.put("options_mixout_column_replay_gain_checkbox", OptionsUI.instance.mixoutcolumn_replaygain);    
    imap.put("options_mixout_column_num_mixouts_checkbox", OptionsUI.instance.mixoutcolumn_nummixouts);
    imap.put("options_mixout_column_num_addons_checkbox", OptionsUI.instance.mixoutcolumn_numaddons);
    imap.put("options_mixout_column_comments_checkbox", OptionsUI.instance.mixoutcolumn_comments);
    imap.put("options_mixout_column_styles_checkbox", OptionsUI.instance.mixoutcolumn_styles);   
    imap.put("options_mixout_column_analog_checkbox", OptionsUI.instance.mixoutcolumn_vinyl);
    imap.put("options_mixout_column_digital_checkbox", OptionsUI.instance.mixoutcolumn_nonvinyl);
    imap.put("options_mixout_column_disabled_checkbox", OptionsUI.instance.mixoutcolumn_disabled);
    imap.put("options_mixout_column_filename_checkbox", OptionsUI.instance.mixoutcolumn_filename);
    imap.put("options_mixout_column_has_album_cover_checkbox", OptionsUI.instance.mixoutcolumn_hasalbumcover);
    imap.put("options_mixout_column_album_cover_checkbox", OptionsUI.instance.mixoutcolumn_albumcover);
    imap.put("options_mixout_columns_apply_button", OptionsUI.instance.applymixoutcolumns);
    imap.put("options_mixout_columns_reset_button", OptionsUI.instance.defaultmixoutcolumns);
    imap.put("options_mixout_auto_scroll_checkbox", OptionsUI.instance.mixouttableautoscroll);
    imap.put("options_mixout_in_place_editing_checkbox", OptionsUI.instance.mixoutinplaceediting);
    imap.put("options_mixout_hide_details_checkbox", OptionsUI.instance.mixouttablehide);
    imap.put("options_display_blur_disabled_buttons_checkbox", OptionsUI.instance.blurDisabledButtons);
    imap.put("options_hide_advanced_features_checkbox", OptionsUI.instance.hideAdvancedFeatures);
    imap.put("options_audio_key_detect_quality_slider", OptionsUI.instance.keydetectionquality);
    imap.put("options_audio_bpm_detect_quality_slider", OptionsUI.instance.bpmdetectionquality);
    imap.put("options_audio_time_pitch_shift_quality_slider", OptionsUI.instance.timepitchshiftquality);
    imap.put("options_audio_processing_cpu_utilization_slider", OptionsUI.instance.cpuutilization);
    imap.put("options_audio_midi_device_combobox", OptionsUI.instance.mididevicescombo);
    imap.put("options_audio_midi_keyboard_row1_chord_type_combobox", OptionsUI.instance.row1chordtype);
    imap.put("options_audio_midi_keyboard_row2_chord_type_combobox", OptionsUI.instance.row2chordtype);
    imap.put("options_song_display_artist_checkbox", OptionsUI.instance.songdisplayartist);
    imap.put("options_song_display_album_checkbox", OptionsUI.instance.songdisplayalbum);
    imap.put("options_song_display_track_checkbox", OptionsUI.instance.songdisplaytrack);
    imap.put("options_song_display_title_checkbox", OptionsUI.instance.songdisplaysongname);
    imap.put("options_song_display_remix_checkbox", OptionsUI.instance.songdisplayremixer);
    imap.put("options_song_display_bpm_start_checkbox", OptionsUI.instance.songdisplaystartbpm);
    imap.put("options_song_display_bpm_end_checkbox", OptionsUI.instance.songdisplayendbpm);
    imap.put("options_song_display_key_start_checkbox", OptionsUI.instance.songdisplaystartkey);
    imap.put("options_song_display_key_end_checkbox", OptionsUI.instance.songdisplayendkey);
    imap.put("options_song_display_time_checkbox", OptionsUI.instance.songdisplaytracktime);
    imap.put("options_song_display_time_signature_checkbox", OptionsUI.instance.songdisplaytimesig);
    imap.put("options_song_display_custom_field_1_checkbox", OptionsUI.instance.songdisplayfield1);
    imap.put("options_song_display_custom_field_2_checkbox", OptionsUI.instance.songdisplayfield2);
    imap.put("options_song_display_custom_field_3_checkbox", OptionsUI.instance.songdisplayfield3);
    imap.put("options_song_display_custom_field_4_checkbox", OptionsUI.instance.songdisplayfield4);
    imap.put("options_song_custom_field_1_label_field", OptionsUI.instance.customfieldtext1);
    imap.put("options_song_custom_field_2_label_field", OptionsUI.instance.customfieldtext2);
    imap.put("options_song_custom_field_3_label_field", OptionsUI.instance.customfieldtext3);
    imap.put("options_song_custom_field_4_label_field", OptionsUI.instance.customfieldtext4);
    imap.put("options_song_custom_field_1_persistent_checkbox", OptionsUI.instance.donotclearuserfield1whenadding);
    imap.put("options_song_custom_field_2_persistent_checkbox", OptionsUI.instance.donotclearuserfield2whenadding);
    imap.put("options_song_custom_field_3_persistent_checkbox", OptionsUI.instance.donotclearuserfield3whenadding);
    imap.put("options_song_custom_field_4_persistent_checkbox", OptionsUI.instance.donotclearuserfield4whenadding);
    imap.put("options_song_custom_field_1_sort_combo", OptionsUI.instance.custom1sortas);
    imap.put("options_song_custom_field_2_sort_combo", OptionsUI.instance.custom2sortas);
    imap.put("options_song_custom_field_3_sort_combo", OptionsUI.instance.custom3sortas);
    imap.put("options_song_custom_field_4_sort_combo", OptionsUI.instance.custom4sortas);
    imap.put("options_add_songs_album_mode_checkbox", OptionsUI.instance.addsongalbumstyle);
    imap.put("options_add_songs_use_filename_as_title_checkbox", OptionsUI.instance.usefilenameastag);
    imap.put("options_song_tag_disable_automatic_reading_checkbox", OptionsUI.instance.disableautotagreading);
    imap.put("options_song_tag_automatic_updates_checkbox", OptionsUI.instance.tagautoupdate);
    imap.put("options_song_tag_write_id3_version_23", OptionsUI.instance.prefer_id3_v23);
    imap.put("options_song_tag_use_key_codes_checkbox", OptionsUI.instance.writekeycodes);
    imap.put("options_song_tag_write_remix_to_title_checkbox", OptionsUI.instance.writeremixertotitle);
    imap.put("options_song_tag_write_artist_checkbox", OptionsUI.instance.tagwriteartist);
    imap.put("options_song_tag_write_album_checkbox", OptionsUI.instance.tagwritealbum);
    imap.put("options_song_tag_write_track_checkbox", OptionsUI.instance.tagwritetrack);
    imap.put("options_song_tag_write_key_to_group_checkbox", OptionsUI.instance.tagwritekeytogroupingtag);    
    imap.put("options_song_tag_write_title_checkbox", OptionsUI.instance.tagwritesongname);
    imap.put("options_song_tag_write_remix_checkbox", OptionsUI.instance.tagwriteremixer);
    imap.put("options_song_tag_write_replay_gain_checkbox", OptionsUI.instance.tagwriterga);
    imap.put("options_song_tag_write_bpm_checkbox", OptionsUI.instance.tagwritebpm);
    imap.put("options_song_tag_write_key_checkbox", OptionsUI.instance.tagwritekey);
    imap.put("options_server_disable", OptionsUI.instance.disableserver);
    imap.put("options_song_tag_write_comments_checkbox", OptionsUI.instance.tagwritecomments);
    imap.put("options_song_tag_write_genre_checkbox", OptionsUI.instance.tagwritegenre);
    imap.put("options_song_tag_write_time_checkbox", OptionsUI.instance.tagwritetime);
    imap.put("options_song_tag_write_time_signature_checkbox", OptionsUI.instance.tagwritetimesig);
    imap.put("options_song_tag_write_rating_checkbox", OptionsUI.instance.tagwriterating);
    imap.put("options_song_tag_write_styles_checkbox", OptionsUI.instance.tagwritingstyles);
    imap.put("options_song_tag_write_album_cover_checkbox", OptionsUI.instance.tagwritingalbumcover);
    imap.put("options_song_tag_write_custom_fields_checkbox", OptionsUI.instance.tagwritingcustomfields);    
    imap.put("options_song_tag_create_styles_automatic_checkbox", OptionsUI.instance.createstylesfromgenretags);        
    //imap.put("options_server_create_styles_automatic_checkbox", OptionsUI.instance.servercreatestyles);
    imap.put("options_server_username_field", OptionsUI.instance.username);
    imap.put("options_server_password_field", OptionsUI.instance.password);
    imap.put("options_server_email_field", OptionsUI.instance.emailaddress);
    imap.put("options_server_website_field", OptionsUI.instance.userwebsite);
    imap.put("options_server_automatic_query_checkbox", OptionsUI.instance.automaticallyquerywhenadding);
    imap.put("options_server_query_overwrite_checkbox", OptionsUI.instance.overwritewhenquerying);
    imap.put("options_server_do_not_query_comments_checkbox", OptionsUI.instance.donotquerycomments);
    imap.put("options_server_do_not_query_styles_checkbox", OptionsUI.instance.donotquerystyles);
    imap.put("options_server_strict_query_checkbox", OptionsUI.instance.strictsearch);
    imap.put("options_server_do_not_share_mix_details_checkbox", OptionsUI.instance.donotsharemixcomments);
    imap.put("options_server_do_not_share_mixouts_checkbox", OptionsUI.instance.donotsharemixouts);
    imap.put("options_color_type_selection_combobox", OptionsUI.instance.colorselectionscombo);
    imap.put("options_color_chooser", OptionsUI.instance.colorchooser);
    imap.put("options_about_version_label", OptionsUI.instance.version);
    imap.put("options_about_created_by_label", OptionsUI.instance.programmedby);
    imap.put("options_about_contact_label", OptionsUI.instance.email);
    imap.put("options_about_website_label", OptionsUI.instance.web);
    imap.put("options_song_id3writer_combobox", OptionsUI.instance.id3writer);
    imap.put("options_song_id3reader_combobox", OptionsUI.instance.id3reader);
    imap.put("media_playlist_song_list", PlayListUI.instance.playlistlist);
    imap.put("media_playlist_ok_button", PlayListUI.instance.playlistokbutton);
    imap.put("roots_song_table", RootsUI.instance.rootstable);
    imap.put("roots_ok_button", RootsUI.instance.rootsokbutton);
    imap.put("roots_add_exclude_button", RootsUI.instance.rootsaddexclude);
    imap.put("playlist_select_missing_song_song_list", SelectMissingSongUI.instance.selectmissingsonglist);
    imap.put("playlist_select_missing_song_search_button", SelectMissingSongUI.instance.selectmissingsongsearchbutton);
    imap.put("playlist_select_missing_song_cancel_button", SelectMissingSongUI.instance.selectmissingsongcancel);
    imap.put("playlist_select_missing_song_ok_button", SelectMissingSongUI.instance.selectmissingsongsokbutton);
    imap.put("playlist_select_missing_song_search_field", SelectMissingSongUI.instance.selectmissingsongsfield);
    imap.put("set_fields_input_field", SetFieldsUI.instance.setfieldfield);
    imap.put("set_fields_ok_button", SetFieldsUI.instance.setfieldokbutton);
    imap.put("set_fields_cancel_button", SetFieldsUI.instance.setfieldcancelbutton);
    imap.put("copy_fields_from_combobox", CopyFieldsUI.instance.copyfieldfrom);
    imap.put("copy_fields_to_combobox", CopyFieldsUI.instance.copyfieldto);
    imap.put("copy_fields_ok_button", CopyFieldsUI.instance.copyfieldokbutton);
    imap.put("copy_fields_cancel_button", CopyFieldsUI.instance.copyfieldcancelbutton);
    imap.put("set_flag_analog_checkbox", SetFlagsUI.instance.setflaganalogonly);
    imap.put("set_flag_digital_checkbox", SetFlagsUI.instance.setflagdigitalonly);
    imap.put("set_flag_disabled_checkbox", SetFlagsUI.instance.setflagdisabled);
    imap.put("set_flags_ok_button", SetFlagsUI.instance.setflagsokbutton);
    imap.put("set_flags_cancel_button", SetFlagsUI.instance.setflagscancelbutton);
    imap.put("song_trail_list", SongTrailUI.instance.songtraillist);
    imap.put("song_trail_ok_button", SongTrailUI.instance.songtrailokbutton);
    imap.put("song_trail_save_button", SongTrailUI.instance.songtrailsaveasbt);
    imap.put("song_trail_load_button", SongTrailUI.instance.loadsongtrailbutton);
    imap.put("song_trail_clear_button", SongTrailUI.instance.clearsongtrailbutton);
    imap.put("song_trail_clear_all_button", SongTrailUI.instance.clearallsongtrailbutton);
    imap.put("suggested_mixouts_table", SuggestedMixesUI.instance.suggestedtable);
    imap.put("suggested_mixouts_ok_button", SuggestedMixesUI.instance.suggestedokbutton);
    imap.put("suggested_mixouts_add_exclude_button", SuggestedMixesUI.instance.suggestedaddexclude);
    imap.put("suggested_mixouts_show_all_checkbox", SuggestedMixesUI.instance.showallsuggested);
    imap.put("sync_songs_table", SyncUI.instance.synctable);
    imap.put("sync_songs_button", SyncUI.instance.syncsongsbutton);
    imap.put("sync_cancel_button", SyncUI.instance.synccancelbutton);
    imap.put("sync_songs_remove_button", SyncUI.instance.syncremovebutton);
    imap.put("style_exclude_add_keyword_field", AddStyleExcludeKeywordUI.instance.addexcludekeywordfield);
    imap.put("style_exclude_add_keyword_more_button", AddStyleExcludeKeywordUI.instance.addexcludekeywordmorebutton);
    imap.put("style_exclude_add_keyword_ok_button", AddStyleExcludeKeywordUI.instance.addexcludekeywordokbutton);
    imap.put("style_exclude_add_keyword_cancel_button", AddStyleExcludeKeywordUI.instance.addexcludekeywordcancelbutton);
    imap.put("style_include_add_keyword_field", AddStyleKeywordUI.instance.addkeywordfield);
    imap.put("style_include_add_keyword_more_button", AddStyleKeywordUI.instance.addkeywordmorebutton);
    imap.put("style_include_add_keyword_ok_button", AddStyleKeywordUI.instance.addkeywordokbutton);
    imap.put("style_include_add_keyword_cancel_button", AddStyleKeywordUI.instance.addkeywordcancelbutton);
    imap.put("style_include_add_songs_song_list", AddStyleSongsUI.instance.addsongincludelist);
    imap.put("style_include_add_songs_search_button", AddStyleSongsUI.instance.addstyleincludebutton);
    imap.put("style_include_add_songs_cancel_button", AddStyleSongsUI.instance.addstyleincludesong);
    imap.put("style_include_add_songs_ok_button", AddStyleSongsUI.instance.addstyleincludeokbutton);
    imap.put("style_include_add_songs_search_field", AddStyleSongsUI.instance.addstyleincludesearchfield);
    imap.put("style_exclude_add_songs_song_list", ExcludeStyleSongsUI.instance.addsongexcludelist);
    imap.put("style_exclude_add_songs_search_button", ExcludeStyleSongsUI.instance.addstyleexcludebutton);
    imap.put("style_exclude_add_songs_cancel_button", ExcludeStyleSongsUI.instance.addstyleexcludesong);
    imap.put("style_exclude_add_songs_ok_button", ExcludeStyleSongsUI.instance.addstyleexcludeokbutton);
    imap.put("style_exclude_add_songs_search_field", ExcludeStyleSongsUI.instance.addstyleexcludesearchfield);
    imap.put("add_style_name_field", AddStyleUI.instance.stylenamefield);
    imap.put("add_style_ok_button", AddStyleUI.instance.addstyleokbutton);
    imap.put("add_style_cancel_button", AddStyleUI.instance.addstylecancelbutton);
    imap.put("add_to_styles_tree", AddToStylesUI.instance.addtostylestree);
    imap.put("add_to_styles_clear_button", AddToStylesUI.instance.addtostylesclearbt);
    imap.put("add_to_styles_ok_button", AddToStylesUI.instance.addtostylesokbutton);
    imap.put("add_to_styles_cancel_button", AddToStylesUI.instance.addtostylescancelbutton);
    imap.put("remove_from_styles_tree", RemoveFromStyles.instance.removefromstylestree);
    imap.put("remove_from_styles_clear_button", RemoveFromStyles.instance.removefromstylesclearbt);
    imap.put("remove_from_styles_ok_button", RemoveFromStyles.instance.removefromstylesokbutton);
    imap.put("remove_from_styles_cancel_button", RemoveFromStyles.instance.removefromstylescancelbutton);
    imap.put("set_styles_tree", SetStylesUI.instance.setstylestree);
    imap.put("set_styles_clear_button", SetStylesUI.instance.setstylesclearbt);
    imap.put("set_styles_ok_button", SetStylesUI.instance.setstylesokbutton);
    imap.put("set_styles_cancel_button", SetStylesUI.instance.setstylescancelbutton);
    imap.put("edit_style_ok_button", EditStyleUI.instance.editstyleokbutton);
    imap.put("edit_style_name_field", EditStyleUI.instance.editstylenamefield);
    imap.put("edit_style_description_field", EditStyleUI.instance.styledescription);
    imap.put("edit_style_category_only_checkbox", EditStyleUI.instance.categoryOnlyCheckBox);
    imap.put("edit_style_show_exclude_checkbox", EditStyleUI.instance.showexcludekeywords);
    imap.put("edit_style_include_list", EditStyleUI.instance.editstylekeywordslist);
    imap.put("edit_style_include_remove_button", EditStyleUI.instance.editstyleremovekeywordbutton);
    imap.put("edit_style_exclude_list", EditStyleUI.instance.styleexcludelist);
    imap.put("edit_style_exclude_remove_button", EditStyleUI.instance.removestyleexclude);
    imap.put("edit_style_song_list", ListStyleSongsUI.instance.liststylesongslist);
    imap.put("edit_style_song_list_remove_button", ListStyleSongsUI.instance.removesongfromstylebt);
    imap.put("edit_style_song_list_ok_button", ListStyleSongsUI.instance.liststylesongsokbutton);
    imap.put("broken_file_links_list", BrokenLinkSongsUI.instance.songlist);
    imap.put("broken_file_links_delete_button", BrokenLinkSongsUI.instance.deletesongs);
    imap.put("broken_file_links_ok_button", BrokenLinkSongsUI.instance.okbutton);
    imap.put("midi_keyboard_training_checkbox", MIDIPiano.instance.training);
    imap.put("midi_keyboard_training_mode_combobox", MIDIPiano.instance.training_modes);
    imap.put("midi_keyboard_training_note_combobox", MIDIPiano.instance.training_note);
    imap.put("midi_keyboard_training_start_stop_button", MIDIPiano.instance.training_start_stop);
    imap.put("midi_keyboard_pitch_bend_slider", MIDIPiano.instance.pitchbend);
    imap.put("midi_keyboard_piano_panel", MIDIPiano.instance.pianopnl);
    imap.put("midi_keyboard_volume_slider", MIDIPiano.instance.midivolume);
    imap.put("midi_keyboard_training_speed_spinner", MIDIPiano.instance.spdspinner);    
    imap.put("midi_keyboard_training_speed_label", MIDIPiano.instance.spdspinnerlabel);        
    imap.put("midi_keyboard_program_combobox", MIDIPiano.instance.midiprogramList);
    imap.put("midi_keyboard_active_notes_label", MIDIPiano.instance.keyboardnotelabel);
    imap.put("midi_keyboard_ok_button", MIDIPiano.instance.pianookbutton);
    imap.put("midi_keyboard_chord_combobox", MIDIPiano.instance.chordlist);
    imap.put("midi_keyboard_num_octaves_spinner", MIDIPiano.instance.octspinner);
    imap.put("midi_keyboard_shift_spinner", MIDIPiano.instance.shiftsspinner);
    imap.put("time_pitch_shift_input_bpm_field", PitchTimeShiftUI.instance.startbpm);
    imap.put("time_pitch_shift_output_bpm_field", PitchTimeShiftUI.instance.endbpm);
    imap.put("time_pitch_shift_input_key_field", PitchTimeShiftUI.instance.startkey);
    imap.put("time_pitch_shift_output_key_field", PitchTimeShiftUI.instance.endkey);
    imap.put("time_pitch_shift_normalize_checkbox", PitchTimeShiftUI.instance.normalize);
    imap.put("time_pitch_shift_ok_button", PitchTimeShiftUI.instance.shiftokbutton);
    imap.put("time_pitch_shift_cancel_button", PitchTimeShiftUI.instance.shiftcancelbutton);
    imap.put("skin_title_field", OptionsUI.instance.skin_title_field);
    imap.put("skin_author_field", OptionsUI.instance.skin_author_field);
    imap.put("skin_locale_field", OptionsUI.instance.skin_locale_field);
    imap.put("skin_description_field", OptionsUI.instance.skin_description_field);
    imap.put("skin_reset_button", OptionsUI.instance.skin_reset_button);
    imap.put("available_skins_table", OptionsUI.instance.available_skins_table);
    imap.put("load_skin_button", OptionsUI.instance.load_skin_button);
    imap.put("refresh_available_skins_button", OptionsUI.instance.refresh_available_skins_button);
  }

  class Condition {
    public Condition(Node _node, String _id, Component _component, Component _parent, int _position, String _namespace) {
      node = _node;
      id = _id;
      component = _component;
      parent = _parent;
      position = _position;
      namespace = _namespace;
    }
    public Node node;
    public String id;
    public Component component;
    public Component parent;
    public int position;
    public String namespace;
  }

  class RecursiveCondition {
    public RecursiveCondition(String id, Node _node, Component _parent, int _position, String _namespace, HashMap conditions) {
        this.id = id;
      node = _node;
      parent = _parent;
      position = _position;
      namespace = _namespace;
      this.conditions = new HashMap();
      Collection coll = conditions.entrySet();
      log.debug("RecursiveCondition(): adding recursive condition: " + id);
      if (coll != null) {
          Iterator iter = coll.iterator();
          while (iter.hasNext()) {
              Map.Entry entry = (Map.Entry)iter.next();
              this.conditions.put(entry.getKey(), entry.getValue());
              log.debug("RecursiveCondition(): \tcheckbox: " + entry.getKey() + ", value: " + entry.getValue());
          }
      }      
      
    }
    public boolean satisfiesConditions() {
        log.debug("satisfiesConditions(): testing recursive condition prereqs: " + id);
        Collection coll = conditions.entrySet();
        if (coll != null) {
            Iterator iter = coll.iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                JCheckBox checkbox = (JCheckBox)checkbox_hash.get(entry.getKey());
                boolean value = ((Boolean)entry.getValue()).booleanValue();
                log.debug("satisfiesConditions(): \tchecking: " + entry.getKey() + ", value: " + value);
                if (checkbox.isSelected() != value) return false;
            }
        }
        log.debug("satisfiesConditions(): \tpassed!");
        return true;
    }
    public String id;
    public Node node;
    public Component parent;
    public int position;
    public String namespace;
    public HashMap conditions;
  }

  class DialogOpenAction implements ActionListener {
    public DialogOpenAction(String _id) { id = _id; }
    public void actionPerformed(ActionEvent ae) { REDialog dialog = (REDialog)REDialog.getREDialog(id); dialog.Display(ae.getSource()); }
    public String id;
  }

  class DialogCloseAction implements ActionListener {
    public DialogCloseAction(String _id) { id = _id; }
    public void actionPerformed(ActionEvent ae) { REDialog dialog = (REDialog)REDialog.getREDialog(id); dialog.setVisible(false); }
    public String id;
  }

  class ConditionAction implements ActionListener {
    public ConditionAction(String _id) { id = _id; }
    public void actionPerformed(ActionEvent ae) { ProcessCondition(id); }
    public String id;
  }
  public void ProcessAllConditions() {
      log.debug("ProcessAllConditions(): starting");
    for (int i = 0; i < conditions.size(); ++i) {
      Condition condition = (Condition)conditions.get(i);
      NamedNodeMap attribute_map = condition.node.getAttributes();
      String id = getAttribute(attribute_map, "id");
      if (id != null) {
        JCheckBox condition_checkbox = (JCheckBox)imap.get(id);
        if (condition_checkbox != null) {
          if (conditionActions.get(id) == null) {
            ConditionAction caction = new ConditionAction(id);
            condition_checkbox.addActionListener(caction);
            conditionActions.put(id, caction);
          }
          Vector similarconditions = (Vector)conditionIndex.get(id);
          if (similarconditions == null) {
            similarconditions = new Vector();
            conditionIndex.put(id, similarconditions);
          }
          similarconditions.add(condition);
        }
      }
    }
    for (int i = 0; i < recursiveconditions.size(); ++i) {
      RecursiveCondition condition = (RecursiveCondition)recursiveconditions.get(i);
      NamedNodeMap attribute_map = condition.node.getAttributes();
      String id = getAttribute(attribute_map, "id");
      Vector similarconditions = (Vector)recursiveConditionIndex.get(id);
      if (similarconditions == null) {
        similarconditions = new Vector();
        recursiveConditionIndex.put(id, similarconditions);
      }
      similarconditions.add(condition);
      //if (condition.satisfiesConditions()) {
      if (id != null) {
        JCheckBox condition_checkbox = (JCheckBox)imap.get(id);
        if (condition_checkbox != null) {
          if (conditionActions.get(id) == null) {
            ConditionAction caction = new ConditionAction(id);
            condition_checkbox.addActionListener(caction);
            conditionActions.put(id, caction);            
          }
        }
      }
      //}
    }
  }

  private static boolean is_processing_condition = false;
  public void ProcessCondition(String process_id) {
      try {          
          is_processing_condition = true;
          log.debug("ProcessCondition(): process_id=" + process_id);
    Vector parents = new Vector();
    Vector process_conditions = (Vector)conditionIndex.get(process_id);
    if (process_conditions != null) {
      for (int i = 0; i < process_conditions.size(); ++i) {
        Condition condition = (Condition)process_conditions.get(i);
        NamedNodeMap attribute_map = condition.node.getAttributes();
  //      String id = getAttribute(attribute_map, "id");
  //      if ((id != null) && (id.equalsIgnoreCase(process_id))) {
          JCheckBox condition_checkbox = (JCheckBox)imap.get(process_id);
          if (condition_checkbox != null) {
            NodeList childnodes = condition.node.getChildNodes();
            boolean addorremoved = false;
            for (int cn = 0; cn < childnodes.getLength(); ++cn) {
              Node childnode = childnodes.item(cn);
              if ((StringUtil.isTrue(childnode.getNodeName()) && condition_checkbox.isSelected()) || (StringUtil.isFalse(childnode.getNodeName()) && !condition_checkbox.isSelected()))  {
                addorremoved = true;
                if (!setConstraints(childnode, condition.id, condition.component, condition.parent, condition.position, condition.namespace, false, false)) {
                  getLayout(condition.parent).removeLayoutComponent(condition.component);
                  removeFromParent(condition.component, condition.parent);
                }
              }
            }
            if (!addorremoved) {
              getLayout(condition.parent).removeLayoutComponent(condition.component);
              removeFromParent(condition.component, condition.parent);
            }
            boolean found = false;
            int c = 0;
            while (!found && (c < parents.size())) {
              if (parents.get(c).equals(condition.parent)) found = true;
              ++c;
            }
            if (!found) parents.add(condition.parent);
          }
  //      }
      }
      for (int i = 0; i < parents.size(); ++i) {
        Component c = (Component)parents.get(i);
        c.validate();
      }
    }
    Vector recursive_conditions = (Vector)recursiveConditionIndex.get(process_id);
    JCheckBox condition_checkbox = (JCheckBox)imap.get(process_id);
    if (recursive_conditions != null) {
        for (int i = 0; i < recursive_conditions.size(); ++i) {
        RecursiveCondition condition = (RecursiveCondition)recursive_conditions.get(i);
        if (condition.satisfiesConditions()) {        
        NamedNodeMap attribute_map = condition.node.getAttributes();
        if (condition_checkbox != null) {
            log.debug("ProcessCondition(): \tprocessing recursive condition: " + 1);
          NodeList childnodes = condition.node.getChildNodes();
          for (int cn = 0; cn < childnodes.getLength(); ++cn) {
            Node childnode = childnodes.item(cn);
            if ((StringUtil.isFalse(childnode.getNodeName()) && condition_checkbox.isSelected()) || (StringUtil.isTrue(childnode.getNodeName()) && !condition_checkbox.isSelected()))  {
              RecurseLoad(childnode.getChildNodes(), condition.parent, condition.position, condition.namespace, true, false);
            }
          }
          childnodes = condition.node.getChildNodes();
          for (int cn = 0; cn < childnodes.getLength(); ++cn) {
            Node childnode = childnodes.item(cn);
            if ((StringUtil.isTrue(childnode.getNodeName()) && condition_checkbox.isSelected()) || (StringUtil.isFalse(childnode.getNodeName()) && !condition_checkbox.isSelected()))  {
              RecurseLoad(childnode.getChildNodes(), condition.parent, condition.position, condition.namespace, false, false);
            }
          }
          condition.parent.validate();
        }
        }
      }
    }    
    if (OptionsUI.instance.enablefilter.isSelected()) {
        setDynamicValues();
    }
      } catch (Exception e) {
          log.error("ProcessCondition(): error", e);
      }
      is_processing_condition = false;
  }

  public void setDynamicValues() {
      if ((OptionsUI.instance.filter1options != null) && (OptionsUI.instance.filter1options.getSelectedItem() != null)) {
          SkinManager.instance.setText("filter_1_values_label", OptionsUI.instance.filter1options.getSelectedItem().toString());
          SkinManager.instance.setText("filter_2_values_label", OptionsUI.instance.filter2options.getSelectedItem().toString());
          SkinManager.instance.setText("filter_3_values_label", OptionsUI.instance.filter3options.getSelectedItem().toString());
          OptionsUI.instance.filter1.changedSkins();
          OptionsUI.instance.filter2.changedSkins();
          OptionsUI.instance.filter3.changedSkins();
          OptionsUI.instance.UpdateCustomFieldLabels();
      }
  }
  
  public void setEnabled(String id, boolean val) {
    Component c = (Component)imap.get(id);
    if (c != null) c.setEnabled(val);
  }

  public void setVisible(String id, boolean val) {
    Component c = (Component)imap.get(id);
    if (c != null) c.setVisible(val);
  }

  // for saving loading mainly:

  private void writeLine(BufferedWriter outputbuffer, String text) {
    try {
      outputbuffer.write(text);
      outputbuffer.newLine();
    } catch (Exception e) {
        log.error("writeLine(): error", e);
    }
  }

  private static HashMap excludeSaveIds = new HashMap();
  static {
      excludeSaveIds.put("options_look_and_feel_combobox", null);
      // TODO: consider what others need to be excluded like this
//      excludeSaveIds.put("options_song_custom_field_1_tag_combo", null);
//      excludeSaveIds.put("options_song_custom_field_2_tag_combo", null);
//      excludeSaveIds.put("options_song_custom_field_3_tag_combo", null);
//      excludeSaveIds.put("options_song_custom_field_4_tag_combo", null);
  }
  
  public boolean Save(String filename) {
    try {
        LookAndFeelManager.saveCurrentLookAndFeel();
      if ((filename == null) || filename.equals("")) filename = "skin.dat";
      FileWriter outputstream = new FileWriter(OSHelper.getWorkingDirectory() + "/" + filename);
      BufferedWriter outputbuffer = new BufferedWriter(outputstream);
      writeLine(outputbuffer, "1.0");

      // checkboxes
      writeLine(outputbuffer, String.valueOf(checkbox_hash.size()));
      Iterator checkboxes = checkbox_hash.entrySet().iterator();
      while (checkboxes.hasNext()) {
        Map.Entry entry = (Map.Entry)checkboxes.next();
        String id = (String)entry.getKey();
        JCheckBox checkbox = (JCheckBox)entry.getValue();
        writeLine(outputbuffer, id);
        writeLine(outputbuffer, StringUtil.printboolean(checkbox.isSelected()));
      }

      // radiobuttones
      writeLine(outputbuffer, String.valueOf(radiobutton_hash.size()));
      Iterator radiobuttones = radiobutton_hash.entrySet().iterator();
      while (radiobuttones.hasNext()) {
        Map.Entry entry = (Map.Entry)radiobuttones.next();
        String id = (String)entry.getKey();
        JRadioButton radiobutton = (JRadioButton)entry.getValue();
        writeLine(outputbuffer, id);
        writeLine(outputbuffer, StringUtil.printboolean(radiobutton.isSelected()));
      }

      // frames
      writeLine(outputbuffer, String.valueOf(frame_hash.size()));
      Iterator frames = frame_hash.entrySet().iterator();
      while (frames.hasNext()) {
        Map.Entry entry = (Map.Entry)frames.next();
        String id = (String)entry.getKey();
        JFrame frame = (JFrame)entry.getValue();
        writeLine(outputbuffer, id);
        writeLine(outputbuffer, String.valueOf("(" + frame.getSize().getWidth() + "," + frame.getSize().getHeight() + ")"));
        writeLine(outputbuffer, String.valueOf("(" + frame.getLocation().getX() + "," + frame.getLocation().getY() + ")"));
      }

      // dialogs
      writeLine(outputbuffer, String.valueOf(dialog_hash.size()));
      Iterator dialogs = dialog_hash.entrySet().iterator();
      while (dialogs.hasNext()) {
        Map.Entry entry = (Map.Entry)dialogs.next();
        String id = (String)entry.getKey();
        JDialog dialog = (JDialog)entry.getValue();
        writeLine(outputbuffer, id);
        writeLine(outputbuffer, String.valueOf("(" + dialog.getSize().getWidth() + "," + dialog.getSize().getHeight() + ")"));
        writeLine(outputbuffer, String.valueOf("(" + dialog.getLocation().getX() + "," + dialog.getLocation().getY() + ")"));
      }

      // split panes
      writeLine(outputbuffer, String.valueOf(splitpanel_hash.size()));
      Iterator splitpanes = splitpanel_hash.entrySet().iterator();
      while (splitpanes.hasNext()) {
        Map.Entry entry = (Map.Entry)splitpanes.next();
        String id = (String)entry.getKey();
        JSplitPane splitpane = (JSplitPane)entry.getValue();
        writeLine(outputbuffer, id);
        double divider_location = (double)splitpane.getDividerLocation();
        boolean vertical = (splitpane.getOrientation() == JSplitPane.VERTICAL_SPLIT);
        double split_height = splitpane.getHeight();
        double split_width = splitpane.getWidth();
        double divider_percent = vertical ? divider_location / split_height : divider_location / split_width;
        writeLine(outputbuffer, String.valueOf((int)divider_location));
      }

      // colors
      writeLine(outputbuffer, String.valueOf(colormap.size()));
      Iterator colors = colormap.entrySet().iterator();
      while (colors.hasNext()) {
        Map.Entry entry = (Map.Entry)colors.next();
        String id = (String)entry.getKey();
        Object value = entry.getValue();
        if (value instanceof Color) {
            Color color = (Color)value;
            writeLine(outputbuffer, id);
            writeLine(outputbuffer, String.valueOf("(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")"));
        } else {
            writeLine(outputbuffer, id);
            writeLine(outputbuffer, value.toString());
        }
        
      }

      // comboboxes
      writeLine(outputbuffer, String.valueOf(combobox_hash.size()));
      Iterator comboboxes = combobox_hash.entrySet().iterator();
      while (comboboxes.hasNext()) {
        Map.Entry entry = (Map.Entry)comboboxes.next();
        String id = (String)entry.getKey();
        JComboBox combobox = (JComboBox)entry.getValue();
        writeLine(outputbuffer, id);
        writeLine(outputbuffer, String.valueOf(combobox.getSelectedIndex()));
      }

      // moreinfo panels
      writeLine(outputbuffer, String.valueOf(moreinfomap.size()));
      Iterator moreinfoPanels = moreinfomap.entrySet().iterator();
      while (moreinfoPanels.hasNext()) {
        Map.Entry entry = (Map.Entry)moreinfoPanels.next();
        String id = (String)entry.getKey();
        MoreInfoPanel moreInfoPanel = (MoreInfoPanel)entry.getValue();
        writeLine(outputbuffer, id);
        writeLine(outputbuffer, StringUtil.printboolean(moreInfoPanel.isBottomShowing()));
      }
            
      outputbuffer.close();
      outputstream.close();
      return true;
    } catch (Exception e) {
        log.error("Save(): error", e);
    }
    return false;
  }

  public void Load(String filename) {
    Load(filename, false);
  }

  public void Load(String filename, boolean changeonly) {

    Iterator splitpanes = splitpanel_hash.entrySet().iterator();
    while (splitpanes.hasNext()) {
      Map.Entry entry = (Map.Entry)splitpanes.next();
      String id = (String)entry.getKey();
      JSplitPane splitpane = (JSplitPane)entry.getValue();

      String divider_size = null;
      if (sizemap.get("divider_size") != null)
        divider_size = (String)sizemap.get("divider_size");
      if (divider_size != null) {
        Backup(old_divider_sizes, splitpane, String.valueOf(splitpane.getDividerSize()));
        splitpane.setDividerSize(Integer.parseInt(divider_size));
      }
      if (!use_default_splitpanel_dividers) splitpane.setUI(new MySplitPaneUI(splitpane.getDividerSize(), use_empty_splitpanel_dividers));
    }

    try {
      FileReader inputstream = new FileReader(OSHelper.getFileBackwardsCompatible(filename));
      BufferedReader inputbuffer = new BufferedReader(inputstream);
      float version = Float.parseFloat(inputbuffer.readLine());

      // checkboxes
      int num_check_boxes = Integer.parseInt(inputbuffer.readLine());
      for (int i = 0; i < num_check_boxes; ++i) {
        String id = inputbuffer.readLine();
        try {
            JCheckBox checkbox = (JCheckBox)imap.get(id);
            if (checkbox != null) {
                boolean oldvalue = checkbox.isSelected();
                boolean newvalue = StringUtil.isTrue(inputbuffer.readLine());
                if (oldvalue != newvalue) {
                    checkbox.setSelected(newvalue);
                    if (!changeonly) if (isCondition(id)) triggerAction(checkbox);
                }
            } else {
                inputbuffer.readLine();
            }
        } catch (Exception e) { }
      }
            
      // radiobuttons
      int num_radio_buttons = Integer.parseInt(inputbuffer.readLine());
      for (int i = 0; i < num_radio_buttons; ++i) {
        String id = inputbuffer.readLine();
        try {
        JRadioButton radiobutton = (JRadioButton)imap.get(id);
        if (radiobutton != null) {
          boolean oldvalue = radiobutton.isSelected();
          boolean newvalue = StringUtil.isTrue(inputbuffer.readLine());
          if (oldvalue != newvalue) {
            radiobutton.setSelected(newvalue);
          }
        } else {
            inputbuffer.readLine();
        }
        } catch (Exception e) { }
      }

      // frames
      int num_frames = Integer.parseInt(inputbuffer.readLine());
      for (int i = 0; i < num_frames; ++i) {
        String id = inputbuffer.readLine();
        try {
        JFrame frame = (JFrame)imap.get(id);
        if (frame != null) {
          Dimension dimension = StringUtil.parseDimension(inputbuffer.readLine());
          if (frame.isResizable()) frame.setSize(dimension);
          Point point = StringUtil.parsePoint(inputbuffer.readLine());
          frame.setLocation(point);
        } else {
            inputbuffer.readLine();
            inputbuffer.readLine();
        }
        } catch (Exception e) { }
      }

      // dialogs
      int num_dialogs = Integer.parseInt(inputbuffer.readLine());
      for (int i = 0; i < num_dialogs; ++i) {
        String id = inputbuffer.readLine();
        try {
        JDialog dialog = (JDialog)imap.get(id);
        if (dialog != null) {
          Dimension dimension = StringUtil.parseDimension(inputbuffer.readLine());
          if (dialog.isResizable()) dialog.setSize(dimension);
          Point point = StringUtil.parsePoint(inputbuffer.readLine());
          dialog.setLocation(point);
        } else {
            inputbuffer.readLine();
            inputbuffer.readLine();
        }
        } catch (Exception e) { }
      }

      // split panes
      int num_split_panes = Integer.parseInt(inputbuffer.readLine());
      for (int i = 0; i < num_split_panes; ++i) {
        String id = inputbuffer.readLine();
        try {
        JSplitPane splitpane = (JSplitPane)imap.get(id);
        double divider_location = Double.parseDouble(inputbuffer.readLine());
        if (!changeonly && (splitpane != null)) {
          splitpane.setDividerLocation(((int)divider_location));
        }
        } catch (Exception e) { }
      }

      // colors
      int num_colors = Integer.parseInt(inputbuffer.readLine());
      for (int i = 0; i < num_colors; ++i) {
        String id = inputbuffer.readLine();
        try {
        	String colorstr = inputbuffer.readLine();
        	if (!changeonly && (colormap.get(id) != null)) {
        		if (colorstr.startsWith("%"))
        			colormap.put(id, colorstr);
        		else
        			setColor(id, StringUtil.parseColor(colorstr));
        	}
        } catch (Exception e) {
        	//log.error("Load(): error", e);
        }
      }
      
      String nextline = inputbuffer.readLine();
      if (nextline != null) {
          // combo boxes
          int num_combo_boxes = Integer.parseInt(nextline);
          for (int i = 0; i < num_combo_boxes; ++i) {
              String id = inputbuffer.readLine();
              try {
              JComboBox combobox = (JComboBox)imap.get(id);
              if (combobox != null) {
                int oldvalue = combobox.getSelectedIndex();
                int newvalue = Integer.parseInt(inputbuffer.readLine());
                if (!excludeSaveIds.containsKey(id)) {
                    if (oldvalue != newvalue) {
                        combobox.setSelectedIndex(newvalue);
                    }
                }
              } else {
                  inputbuffer.readLine();
              }
              } catch (Exception e) { }
          }
      }
      
      nextline = inputbuffer.readLine();
      if (nextline != null) {
          // more info panels
          int num_more_info_panels = Integer.parseInt(nextline);
          for (int i = 0; i < num_more_info_panels; ++i) {
              String id = inputbuffer.readLine();
              try {
                  MoreInfoPanel moreInfo = (MoreInfoPanel)imap.get(id);
              if (moreInfo != null) {
                boolean oldvalue = moreInfo.isBottomShowing();
                boolean newvalue = StringUtil.isTrue(inputbuffer.readLine());
                if (oldvalue != newvalue) {
//                    moreInfo.showBottom(newvalue);
                }
              } else {
                  inputbuffer.readLine();
              }
              } catch (Exception e) { }
          }
      }
      
      inputbuffer.close();
      inputstream.close();
                  
    } catch (java.io.FileNotFoundException fnfe) {
        log.debug("Load(): " + filename + " does not exist, will use defaults");
    } catch (Exception e) {
        log.error("Load(): error loading filename=" + filename, e);
    }
    
  }

  private boolean isCondition(String id) {
    Vector conditionVector = (Vector)conditionIndex.get(id);
    if (conditionVector != null) return true;
    conditionVector = (Vector)recursiveConditionIndex.get(id);
    if (conditionVector != null) return true;
    if (conditionActions.get(id) != null) return true;
    return false;
  }

  public void triggerAction(JCheckBox checkbox) {
    ActionListener[] listeners = checkbox.getActionListeners();
    if ((listeners != null) && (listeners.length > 0)) {
      listeners[0].actionPerformed(new ActionEvent(checkbox, 0, ""));
    }
  }

  public void Reset() {
      log.debug("Reset(): resetting skin defaults");
      //lastSkinName = null; this line caused bugs
     ChangeSkins(new File(current_skin_filename));
  }

  private void Reinitialize() {
    usedefaultborders = false;
    use_default_splitpanel_dividers = false;
    setdefaults = true;
    templates.clear();
    tableconfigmap.clear();
    default_field_margin = null;
    errormap.clear();
    sizemap.clear();
    colormap.clear();
    marginmap.clear();
    moreinfomap.clear();
    ImageIconMap.clear();
    iconmap.clear();
    bordermap.clear();
    fontmap.clear();
    messagemap.clear();
    RapidEvolutionUI.stdinsetsize = null;
    default_field_margin = null;
    default_dialog_size = new Dimension(100,100);
    default_frame_size = new Dimension(100,100);
    altmap.clear();
    textmap.clear();
    parentmap.clear();
    textcomponent_map.clear();
    root_nodelist = null;
    look_feel = null;

    Iterator old_textcomponent_disabled_iter = old_textcomponent_disabled.entrySet().iterator();
    while (old_textcomponent_disabled_iter.hasNext()) {
      Map.Entry entry = (Map.Entry)old_textcomponent_disabled_iter.next();
      JTextComponent component = (JTextComponent)entry.getKey();
      Color color = (Color)entry.getValue();
      component.setDisabledTextColor(color);
    }
    old_textcomponent_disabled = new HashMap();

      Iterator oldtextcomponent_selection_foreground = old_textcomponent_selection_foreground.entrySet().iterator();
      while (oldtextcomponent_selection_foreground.hasNext()) {
        Map.Entry entry = (Map.Entry)oldtextcomponent_selection_foreground.next();
        JTextComponent component = (JTextComponent)entry.getKey();
        Color color = (Color)entry.getValue();
        component.setSelectedTextColor(color);
      }
      old_textcomponent_selection_foreground = new HashMap();

      Iterator oldtextcomponent_selection_background = old_textcomponent_selection_background.entrySet().iterator();
      while (oldtextcomponent_selection_background.hasNext()) {
        Map.Entry entry = (Map.Entry)oldtextcomponent_selection_background.next();
        JTextComponent component = (JTextComponent)entry.getKey();
        Color color = (Color)entry.getValue();
        component.setSelectionColor(color);
      }
      old_textcomponent_selection_background = new HashMap();

    Iterator oldopaquevalues = old_opaquevalues.entrySet().iterator();
    while (oldopaquevalues.hasNext()) {
      Map.Entry entry = (Map.Entry)oldopaquevalues.next();
      JComponent component = (JComponent)entry.getKey();
      boolean opaque = ((Boolean)entry.getValue()).booleanValue();
      component.setOpaque(opaque);
    }
    old_opaquevalues = new HashMap();

      Iterator old_caret_color_iter = old_caret_color.entrySet().iterator();
      while (old_caret_color_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_caret_color_iter.next();
        JTextComponent textc = (JTextComponent)entry.getKey();
        Color caretcolor = (Color)entry.getValue();
        textc.setCaretColor(caretcolor);
      }
      old_caret_color = new HashMap();

      Iterator old_list_visible_rows_iter = old_list_visible_rows.entrySet().iterator();
      while (old_list_visible_rows_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_list_visible_rows_iter.next();
        JList list = (JList)entry.getKey();
        String rows = (String)entry.getValue();
        list.setVisibleRowCount(Integer.parseInt(rows));
      }
      old_list_visible_rows = new HashMap();

      Iterator old_textarea_rows_iter = old_textarea_rows.entrySet().iterator();
      while (old_textarea_rows_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_textarea_rows_iter.next();
        JTextArea textarea = (JTextArea)entry.getKey();
        String rows = (String)entry.getValue();
        textarea.setRows(Integer.parseInt(rows));
      }
      old_textarea_rows = new HashMap();

      Iterator old_textarea_tab_sizes_iter = old_textarea_tab_sizes.entrySet().iterator();
      while (old_textarea_tab_sizes_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_textarea_tab_sizes_iter.next();
        JTextArea textarea = (JTextArea)entry.getKey();
        String tab_sizes = (String)entry.getValue();
        textarea.setTabSize(Integer.parseInt(tab_sizes));
      }
      old_textarea_tab_sizes = new HashMap();

      Iterator old_textarea_columns_iter = old_textarea_columns.entrySet().iterator();
      while (old_textarea_columns_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_textarea_columns_iter.next();
        JTextArea textarea = (JTextArea)entry.getKey();
        String columns = (String)entry.getValue();
        textarea.setColumns(Integer.parseInt(columns));
      }
      old_textarea_columns = new HashMap();

      Iterator old_slider_extents_iter = old_slider_extents.entrySet().iterator();
      while (old_slider_extents_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_slider_extents_iter.next();
        JSlider slider = (JSlider)entry.getKey();
        String extent = (String)entry.getValue();
        slider.setExtent(Integer.parseInt(extent));
      }
      old_slider_extents = new HashMap();

      Iterator old_field_scroll_offsets_iter = old_field_scroll_offsets.entrySet().iterator();
      while (old_field_scroll_offsets_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_field_scroll_offsets_iter.next();
        JTextField textfield = (JTextField)entry.getKey();
        String offset = (String)entry.getValue();
        textfield.setScrollOffset(Integer.parseInt(offset));
      }
      old_field_scroll_offsets = new HashMap();

    Iterator old_divider_sizes_iter = old_divider_sizes.entrySet().iterator();
    while (old_divider_sizes_iter.hasNext()) {
      Map.Entry entry = (Map.Entry)old_divider_sizes_iter.next();
      JSplitPane splitpane = (JSplitPane)entry.getKey();
      String dividersize = (String)entry.getValue();
      splitpane.setDividerSize(Integer.parseInt(dividersize));
    }
    old_divider_sizes = new HashMap();

      Iterator old_table_intercell_iter = old_table_intercell_dimensions.entrySet().iterator();
      while (old_table_intercell_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_table_intercell_iter.next();
        JTable table = (JTable)entry.getKey();
        Dimension dimension = (Dimension)entry.getValue();
        table.setIntercellSpacing(dimension);
      }
      old_table_intercell_dimensions = new HashMap();

      Iterator old_maximum_size_iter = old_maximum_size.entrySet().iterator();
      while (old_maximum_size_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_maximum_size_iter.next();
        JComponent component = (JComponent)entry.getKey();
        Dimension dimension = (Dimension)entry.getValue();
        component.setMaximumSize(dimension);
      }
      old_maximum_size = new HashMap();

      Iterator old_minimum_size_iter = old_minimum_size.entrySet().iterator();
      while (old_minimum_size_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_minimum_size_iter.next();
        JComponent component = (JComponent)entry.getKey();
        Dimension dimension = (Dimension)entry.getValue();
        component.setMinimumSize(dimension);
      }
      old_minimum_size = new HashMap();

      Iterator old_preferred_size_iter = old_preferred_size.entrySet().iterator();
      while (old_preferred_size_iter.hasNext()) {
        Map.Entry entry = (Map.Entry)old_preferred_size_iter.next();
        JComponent component = (JComponent)entry.getKey();
        Dimension dimension = (Dimension)entry.getValue();
        component.setPreferredSize(dimension);
      }
      old_preferred_size = new HashMap();

    Iterator old_table_row_heights_iter = old_table_row_heights.entrySet().iterator();
    while (old_table_row_heights_iter.hasNext()) {
      Map.Entry entry = (Map.Entry)old_table_row_heights_iter.next();
      JTable table = (JTable)entry.getKey();
      int height = Integer.parseInt((String)entry.getValue());
      table.setRowHeight(height);
    }
    old_table_row_heights = new HashMap();

    Iterator old_table_row_margins_iter = old_table_row_margins.entrySet().iterator();
    while (old_table_row_margins_iter.hasNext()) {
      Map.Entry entry = (Map.Entry)old_table_row_margins_iter.next();
      JTable table = (JTable)entry.getKey();
      int margins = Integer.parseInt((String)entry.getValue());
      table.setRowMargin(margins);
    }
    old_table_row_margins = new HashMap();

    Iterator old_list_cell_heights_iter = old_list_cell_heights.entrySet().iterator();
    while (old_list_cell_heights_iter.hasNext()) {
      Map.Entry entry = (Map.Entry)old_list_cell_heights_iter.next();
      JList list = (JList)entry.getKey();
      int height = Integer.parseInt((String)entry.getValue());
      list.setFixedCellHeight(height);
    }
    old_list_cell_heights = new HashMap();

    Iterator old_list_cell_widths_iter = old_list_cell_widths.entrySet().iterator();
    while (old_list_cell_widths_iter.hasNext()) {
      Map.Entry entry = (Map.Entry)old_list_cell_widths_iter.next();
      JList list = (JList)entry.getKey();
      int width = Integer.parseInt((String)entry.getValue());
      list.setFixedCellWidth(width);
    }
    old_list_cell_widths = new HashMap();

    Iterator oldborders = old_borders.entrySet().iterator();
    while (oldborders.hasNext()) {
      Map.Entry entry = (Map.Entry)oldborders.next();
      JComponent component = (JComponent)entry.getKey();
      Border border = (Border)entry.getValue();
      component.setBorder(border);
    }
    old_borders = new HashMap();

    Iterator checkboxes = checkbox_hash.entrySet().iterator();
    while (checkboxes.hasNext()) {
      Map.Entry entry = (Map.Entry)checkboxes.next();
      String id = (String)entry.getKey();
      JCheckBox checkbox = (JCheckBox)entry.getValue();
      ActionListener[] listeners = checkbox.getActionListeners();
      for (int i = 0; i < listeners.length; ++i) {
        if ((listeners[i] instanceof ConditionAction)) {
          checkbox.removeActionListener(listeners[i]);
        }
      }
    }
    conditions = new Vector();
    recursiveconditions = new Vector();
    conditionActions = new HashMap();
    conditionIndex = new HashMap();
    recursiveConditionIndex = new HashMap();
    splitpanel_hash = new HashMap();
    checkbox_hash = new HashMap();
    dialog_hash = new HashMap();
    radiobutton_hash = new HashMap();
    combobox_hash = new HashMap();
    frame_hash = new HashMap();
    initUIDefaults();
//    imap = null;
//    setImap();

    Iterator buttons = buttonmap.entrySet().iterator();
    while (buttons.hasNext()) {
      Map.Entry entry = (Map.Entry)buttons.next();
      String id = (String)entry.getKey();
      JButton button = (JButton)entry.getValue();
      ActionListener[] listeners = button.getActionListeners();
      for (int i = 0; i < listeners.length; ++i) {
        if ((listeners[i] instanceof DialogOpenAction) ||
           (listeners[i] instanceof DialogCloseAction)) {
          button.removeActionListener(listeners[i]);
        }
      }
    }
    buttonmap = new HashMap();
  }

  Vector opendialogs = null;
  private void getOpenDialogs() {
    opendialogs = new Vector();
    Iterator dialogs = dialog_hash.entrySet().iterator();
    while (dialogs.hasNext()) {
      Map.Entry entry = (Map.Entry)dialogs.next();
      String id = (String)entry.getKey();
      JDialog dialog = (JDialog)entry.getValue();
      if (dialog.isVisible()) {
        opendialogs.add(id);
        dialog.setVisible(false);
      }
      REDialog.getREDialog(id).reset();
    }
  }
  private void setOpenDialogs() {
    if (opendialogs == null) return;
    for (int i = 0; i < opendialogs.size(); ++i) {
      String id = (String)opendialogs.get(i);
      try {
        REDialog redialog = REDialog.getREDialog(id);
        if (redialog != null) {
          redialog.Display();
        } else {
          JDialog dialog = (JDialog)imap.get(id);
          dialog.setVisible(true);
        }
      } catch (Exception e) { }
    }
  }

  public static boolean changingSkin = false;
  public void ChangeSkins(File file) {
    ischanging = true;
    try {
        log.debug("ChangeSkins(): changing skin to: " + file.getAbsolutePath());

    Save("skin.dat");
    getOpenDialogs();
    unset = true;
    RecurseLoad(root_nodelist, null, Position.NONE, "", true, false);
    Reinitialize();

    loadSkin(file.getAbsolutePath());
    ProcessAllConditions();

    Load("skin.dat", true);

    Save("skin.dat");

    initSelectionModes();
    // checkboxes
    Iterator checkboxes = checkbox_hash.entrySet().iterator();
    while (checkboxes.hasNext()) {
      Map.Entry entry = (Map.Entry)checkboxes.next();
      String id = (String)entry.getKey();
      JCheckBox checkbox = (JCheckBox)entry.getValue();
      if (isCondition(id)) triggerAction(checkbox);
    }
//    RapidEvolutionUI.instance.mainWindow.validate();
    setOpenDialogs();
    
    getFrame("main_frame").setVisible(true);

    SearchPane.instance.setBpmSliderRangeLabels();

    setDynamicValues();
    
    populateColorTypeIdMap();
        
    SkinManager.instance.setColorChooserBackground(OptionsUI.instance.colorchooser,OptionsUI.instance.colorchooser.getBackground());
    SkinManager.instance.setColorChooserForeground(OptionsUI.instance.colorchooser,OptionsUI.instance.colorchooser.getForeground());
    SongLinkedList.resetColorCache();
    //colorchooser.setPreviewPanel(colorchooser_preview_panel);        
    
    } catch (Exception e) { }
    ischanging = false;

  }

  private HashMap old_textcomponent_disabled = new HashMap();
  private HashMap old_textcomponent_selection_foreground = new HashMap();
  private HashMap old_textcomponent_selection_background = new HashMap();
  private HashMap old_maximum_size = new HashMap();
  private HashMap old_minimum_size = new HashMap();
  private HashMap old_preferred_size = new HashMap();
  private HashMap old_caret_color = new HashMap();
  private HashMap old_list_visible_rows = new HashMap();
  private HashMap old_textarea_tab_sizes = new HashMap();
  private HashMap old_textarea_rows = new HashMap();
  private HashMap old_textarea_columns = new HashMap();
  private HashMap old_slider_extents = new HashMap();
  private HashMap old_field_scroll_offsets = new HashMap();
  private HashMap old_divider_sizes = new HashMap();
  private HashMap old_table_intercell_dimensions = new HashMap();
  private HashMap old_table_row_heights = new HashMap();
  private HashMap old_table_row_margins = new HashMap();
  private HashMap old_list_cell_heights = new HashMap();
  private HashMap old_list_cell_widths = new HashMap();
  private HashMap old_borders = new HashMap();
  private HashMap old_opaquevalues = new HashMap();
  private HashMap old_defaults = new HashMap();
  private void setDefaultValue(String variable, Object value) {
    if (unset) {
      if (old_defaults.get(variable) == null) uidefaults.remove(variable);
      else {
        if (old_defaults.get(variable) instanceof ColorUIResource) {
          ColorUIResource resource = (ColorUIResource) old_defaults.get(variable);
          uidefaults.put(variable, new Color(resource.getRed(), resource.getGreen(), resource.getBlue()));
        } else if (old_defaults.get(variable) instanceof FontUIResource) {
          FontUIResource resource = (FontUIResource) old_defaults.get(variable);
          uidefaults.put(variable, new Font(resource.getName(), resource.getStyle(), resource.getSize()));
        }
        else uidefaults.put(variable, old_defaults.get(variable));
      }
      old_defaults.remove(variable);
    } else {
      old_defaults.put(variable, uidefaults.get(variable));
      uidefaults.put(variable, value);
    }
  }

  private void Backup(HashMap map, Component key, Object value) {
    if (storedefaults && (map.get(key) == null)) map.put(key, value);
  }

  UIDefaults uidefaults = null;
  boolean unset = false;

  private void setDefaults(boolean original) {
    try {
      if (original) unset = true;
      else unset = false;
      if (!unset) {
        if (colormap.get("song_background_default") == null) {
          Color color = null;
          if (colormap.get("table_cell_background") != null) color = (Color)getColor("table_cell_background");
          if ((color == null) && (colormap.get("default_background") != null)) color = (Color)getColor("default_background");
//          if (color == null) color = Color.white;
          if (color != null)
              colormap.put("song_background_default", color);
        }
        if (colormap.get("song_background_disabled") == null) colormap.put("song_background_disabled", new Color(255,0,0));
        if (colormap.get("song_background_in_key") == null) colormap.put("song_background_in_key", new Color(0,255,0));
        if (colormap.get("song_background_in_key_with_mixouts") == null) colormap.put("song_background_in_key_with_mixouts", new Color(0,255,255));
        if (colormap.get("song_background_with_mixouts") == null) colormap.put("song_background_with_mixouts", new Color(255,255,0));
        if (colormap.get("mixout_background_default") == null) {
          Color color = null;
          if (colormap.get("table_cell_background") != null) color = (Color)getColor("table_cell_background");
          if ((color == null) && (colormap.get("default_background") != null)) color = (Color)getColor("default_background");
          if (color == null) color = Color.white;
          colormap.put("mixout_background_default", color);
        }
        if (colormap.get("mixout_background_disabled") == null) colormap.put("mixout_background_disabled", new Color(255,0,0));
        if (colormap.get("mixout_background_ranked") == null) colormap.put("mixout_background_ranked", new Color(0,255,0));
        if (colormap.get("mixout_background_ranked_with_mixouts") == null) colormap.put("mixout_background_ranked_with_mixouts", new Color(0,255,255));
        if (colormap.get("mixout_background_with_mixouts") == null) colormap.put("mixout_background_with_mixouts", new Color(255,255,0));
        if (colormap.get("mixout_background_addon") == null) colormap.put("mixout_background_addon", new Color(128,128,255));
        if (colormap.get("mixout_background_addon_ranked") == null) colormap.put("mixout_background_addon_ranked", new Color(255,128,255));
        if (colormap.get("style_background_default") == null) {
          Color color = null;
          if (colormap.get("list_cell_background") != null) color = (Color)getColor("list_cell_background");
          if ((color == null) && (colormap.get("default_background") != null)) color = (Color)getColor("default_background");
          if (color == null) color = Color.white;
          colormap.put("style_background_default", color);
        }
        if (colormap.get("style_background_selected") == null) {
          Color color = null;
          if (colormap.get("list_selection_background") != null) color = (Color)getColor("list_selection_background");
          if (color == null) color = UIManager.getColor("List.selectionBackground");
          colormap.put("style_background_selected", color);
        }
        if (colormap.get("style_background_of_selected_song") == null) colormap.put("style_background_of_selected_song", new Color(225,255,225));
        if (colormap.get("style_background_of_current_song") == null) colormap.put("style_background_of_current_song", new Color(49,106,197));
      }
      if (colormap.get("default_selection_background") != null) {
        Color color = getColor("default_selection_background");
        setDefaultValue("Tree.selectionBackground", color);
        setDefaultValue("TextPane.selectionBackground", color);
        setDefaultValue("TextField.selectionBackground", color);
        setDefaultValue("TextArea.selectionBackground", color);
        setDefaultValue("Table.selectionBackground", color);
        setDefaultValue("RadioButtonMenuItem.selectionBackground", color);
        setDefaultValue("ProgressBar.selectionBackground", color);
        setDefaultValue("PasswordField.selectionBackground", color);
        setDefaultValue("MenuItem.selectionBackground", color);
        setDefaultValue("Menu.selectionBackground", color);
        setDefaultValue("List.selectionBackground", color);
        setDefaultValue("FormattedTextField.selectionBackground", color);
        setDefaultValue("EditorPane.selectionBackground", color);
        setDefaultValue("ComboBox.selectionBackground", color);
        setDefaultValue("CheckBoxMenuItem.selectionBackground", color);
      }
      if (colormap.get("default_selection_foreground") != null) {
        Color color = getColor("default_selection_foreground");
        setDefaultValue("Tree.selectionForeground", color);
        setDefaultValue("ProgressBar.selectionForeground", color);
        setDefaultValue("TextPane.selectionForeground", color);
        setDefaultValue("TextField.selectionForeground", color);
        setDefaultValue("TextArea.selectionForeground", color);
        setDefaultValue("Table.selectionForeground", color);
        setDefaultValue("RadioButtonMenuItem.selectionForeground", color);
        setDefaultValue("PasswordField.selectionForeground", color);
        setDefaultValue("MenuItem.selectionForeground", color);
        setDefaultValue("Menu.selectionForeground", color);
        setDefaultValue("List.selectionForeground", color);
        setDefaultValue("FormattedTextField.selectionForeground", color);
        setDefaultValue("EditorPane.selectionForeground", color);
        setDefaultValue("ComboBox.selectionForeground", color);
        setDefaultValue("CheckBoxMenuItem.selectionForeground", color);
        setDefaultValue("RadioButtonMenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("MenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("Menu.acceleratorSelectionForeground", color);
        setDefaultValue("CheckBoxMenuItem.acceleratorSelectionForeground", color);
      }
      if (colormap.get("default_accelerator_selection_foreground") != null) {
        Color color = getColor("default_accelerator_selection_foreground");
        setDefaultValue("RadioButtonMenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("MenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("Menu.acceleratorSelectionForeground", color);
        setDefaultValue("CheckBoxMenuItem.acceleratorSelectionForeground", color);
      }
      if ((colormap.get("default_foreground") != null)) { // || unset) {
        Color color = getColor("default_foreground");
        setDefaultValue("windowText", color);
        setDefaultValue("Table.selectionForeground", color);
        setDefaultValue("Label.foreground", color);
        setDefaultValue("Separator.foreground", color);
        setDefaultValue("RadioButtonMenuItem.acceleratorForeground", color);
        setDefaultValue("MenuItem.acceleratorForeground", color);
        setDefaultValue("Menu.acceleratorForeground", color);
        setDefaultValue("ToolBar.dockingForeground", color);
        setDefaultValue("CheckBoxMenuItem.acceleratorForeground", color);
        setDefaultValue("FormattedTextField.caretForeground", color);
        setDefaultValue("RadioButtonMenuItem.selectionForeground", color);
        setDefaultValue("RadioButtonMenuItem.foreground", color);
        setDefaultValue("TextArea.caretForeground", color);
//        setDefaultValue("activeCaptionText", color);
//        setDefaultValue("inactiveCaptionText", color);
        setDefaultValue("PasswordField.caretForeground", color);
        setDefaultValue("ToolTip.foreground", color);
        setDefaultValue("ComboBox.foreground", color);
        setDefaultValue("TextPane.foreground", color);
        setDefaultValue("Tree.selectionForeground", color);
        setDefaultValue("InternalFrame.inactiveTitleForeground", color);
        setDefaultValue("textText", color);
        setDefaultValue("RadioButton.foreground", color);
        setDefaultValue("EditorPane.foreground", color);
        setDefaultValue("ToggleButton.foreground", color);
        setDefaultValue("TabbedPane.foreground", color);
        setDefaultValue("TextPane.caretForeground", color);
        setDefaultValue("FormattedTextField.foreground", color);
        setDefaultValue("ScrollBar.foreground", color);
        setDefaultValue("Viewport.foreground", color);
        setDefaultValue("infoText", color);
        setDefaultValue("MenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("MenuItem.foreground", color);
        setDefaultValue("menuText", color);
        setDefaultValue("Panel.foreground", color);
        setDefaultValue("textHighlightText", color);
        setDefaultValue("PasswordField.foreground", color);
        setDefaultValue("MenuBar.foreground", color);
        setDefaultValue("Menu.foreground", color);
        setDefaultValue("TextPane.selectionForeground", color);
        setDefaultValue("CheckBox.foreground", color);
        setDefaultValue("ComboBox.buttonForeground", color);
        setDefaultValue("TextField.caretForeground", color);
        setDefaultValue("OptionPane.messageForeground", color);
        setDefaultValue("TextArea.selectionForeground", color);
        setDefaultValue("Menu.acceleratorSelectionForeground", color);
        setDefaultValue("TextField.foreground", color);
        setDefaultValue("MenuItem.selectionForeground", color);
        setDefaultValue("OptionPane.foreground", color);
        setDefaultValue("controlText", color);
        setDefaultValue("Tree.textForeground", color);
        setDefaultValue("ComboBox.selectionForeground", color);
        setDefaultValue("ToolBar.foreground", color);
        setDefaultValue("List.selectionForeground", color);
        setDefaultValue("InternalFrame.activeTitleForeground", color);
        setDefaultValue("TextArea.foreground", color);
        setDefaultValue("PasswordField.selectionForeground", color);
        setDefaultValue("TextField.selectionForeground", color);
        setDefaultValue("FormattedTextField.selectionForeground", color);
        setDefaultValue("Table.foreground", color);


        setDefaultValue("RadioButtonMenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("TitledBorder.titleColor", color);
        setDefaultValue("TableHeader.foreground", color);
        setDefaultValue("DesktopIcon.foreground", color);
        setDefaultValue("Table.focusCellForeground", color);
        setDefaultValue("ColorChooser.foreground", color);
        setDefaultValue("EditorPane.selectionForeground", color);
        setDefaultValue("CheckBoxMenuItem.acceleratorSelectionForeground", color);
        setDefaultValue("Button.foreground", color);
        setDefaultValue("List.foreground", color);
        setDefaultValue("PopupMenu.foreground", color);
        setDefaultValue("CheckBoxMenuItem.foreground", color);
        setDefaultValue("EditorPane.caretForeground", color);
        setDefaultValue("CheckBoxMenuItem.selectionForeground", color);
        setDefaultValue("ScrollPane.foreground", color);
        setDefaultValue("Menu.selectionForeground", color);
        setDefaultValue("Tree.foreground", color);
      }
      if ((colormap.get("default_focus") != null)) { // || unset) {
        Color color = getColor("default_focus");
        setDefaultValue("Button.focus", color);
        setDefaultValue("Table.focusCellForeground", color);
        setDefaultValue("ToggleButton.focus", color);
        setDefaultValue("TabbedPane.focus", color);
        setDefaultValue("Slider.focus", color);
        setDefaultValue("RadioButton.focus", color);
        setDefaultValue("CheckBox.focus", color);

      }
      if ((colormap.get("default_select") != null)) {
        Color color = getColor("default_select");
        setDefaultValue("ToggleButton.select", color);
        setDefaultValue("RadioButton.select", color);
        setDefaultValue("Checkbox.select", color);
        setDefaultValue("Button.select", color);
      }
      if ((colormap.get("default_background") != null)) { // || unset) {
        Color color = getColor("default_background");

        setDefaultValue("DesktopIcon.background", color);
        setDefaultValue("Table.focusCellBackground", color);
        setDefaultValue("InternalFrame.borderColor", color);
        setDefaultValue("ComboBox.disabledBackground", color);
        setDefaultValue("FormattedTextField.inactiveBackground", color);
        setDefaultValue("InternalFrame.inactiveTitleBackground", color);
        setDefaultValue("Button.background", color);
        setDefaultValue("TabbedPane.light", color);
        setDefaultValue("ToolTip.backgroundInactive", color);
        setDefaultValue("ToolTip.background", color);
        setDefaultValue("ToolBar.dockingBackground", color);
        setDefaultValue("ToggleButton.background", color);
        setDefaultValue("PopupMenu.background", color);
        setDefaultValue("CheckBoxMenuItem.background", color);
        setDefaultValue("ComboBox.buttonBackground", color);
        setDefaultValue("Viewport.background", color);
        setDefaultValue("MenuItem.background", color);
        setDefaultValue("TabbedPane.tabAreaBackground", color);
        setDefaultValue("TextField.inactiveBackground", color);
//        setDefaultValue("control", color);
        setDefaultValue("SplitPane.background", color);
        setDefaultValue("menu", color);
        setDefaultValue("windowBorder", color);
        setDefaultValue("Panel.background", color);

        setDefaultValue("RadioButtonMenuItem.background", color);
        setDefaultValue("MenuBar.background", color);
        setDefaultValue("CheckBox.background", color);
        setDefaultValue("RadioButton.background", color);
        setDefaultValue("ScrollBar.background", color);
        setDefaultValue("Spinner.background", color);
        setDefaultValue("ScrollBar.track", color);
        setDefaultValue("TabbedPane.selected", color);
        setDefaultValue("ColorChooser.swatchesDefaultRecentColor", color);
        setDefaultValue("ProgressBar.background", color);
        setDefaultValue("Menu.background", color);
        setDefaultValue("PasswordField.inactiveBackground", color);
        setDefaultValue("TableHeader.background", color);
        setDefaultValue("List.background", color);
        setDefaultValue("Table.background", color);
        setDefaultValue("SplitPane.background", color);
        setDefaultValue("scrollbar", color);
        setDefaultValue("OptionPane.background", color);
        setDefaultValue("ComboBox.buttonBackground", color);
        setDefaultValue("ColorChooser.background", color);
//        setDefaultValue("inactiveCaption", color);
        setDefaultValue("ToolBar.background", color);
        setDefaultValue("Slider.background", color);
        setDefaultValue("ScrollPane.background", color);
        setDefaultValue("Label.background", color);
        setDefaultValue("ToolBar.floatingBackground", color);
        setDefaultValue("ComboBox.background", color);
      }
      if ((fontmap.get("default_font") != null)) { // || unset) {
        Font font = getFont("default_font");
        setDefaultValue("Button.font", font);
        setDefaultValue("CheckBox.font", font);
        setDefaultValue("CheckBoxMenuItem.acceleratorFont", font);
        setDefaultValue("CheckBoxMenuItem.font", font);
        setDefaultValue("ColorChooser.font", font);
        setDefaultValue("ComboBox.font", font);
        setDefaultValue("DesktopIcon.font", font);
        setDefaultValue("EditorPane.font", font);
        setDefaultValue("FormattedTextField.font", font);
        setDefaultValue("InternalFrame.titleFont", font);
        setDefaultValue("Label.font", font);
        setDefaultValue("List.font", font);
        setDefaultValue("Menu.acceleratorFont", font);
        setDefaultValue("Menu.font", font);
        setDefaultValue("MenuBar.font", font);
        setDefaultValue("MenuItem.acceleratorFont", font);
        setDefaultValue("MenuItem.font", font);
        setDefaultValue("OptionPane.font", font);
        setDefaultValue("Panel.font", font);
        setDefaultValue("PasswordField.font", font);
        setDefaultValue("PopupMenu.font", font);
        setDefaultValue("ProgressBar.font", font);
        setDefaultValue("RadioButton.font", font);
        setDefaultValue("RadioButtonMenuItem.acceleratorFont", font);
        setDefaultValue("RadioButtonMenuItem.font", font);
        setDefaultValue("ScrollPane.font", font);
        setDefaultValue("Slider.font", font);
        setDefaultValue("Spinner.font", font);
        setDefaultValue("TabbedPane.font", font);
        setDefaultValue("Table.font", font);
        setDefaultValue("TableHeader.font", font);
        setDefaultValue("TextArea.font", font);
        setDefaultValue("TextArea.font", font);
        setDefaultValue("TextField.font", font);
        setDefaultValue("TextPane.font", font);
        setDefaultValue("TitledBorder.font", font);
        setDefaultValue("ToggleButton.font", font);
        setDefaultValue("ToolBar.font", font);
        setDefaultValue("ToolTip.font", font);
        setDefaultValue("Tree.font", font);
        setDefaultValue("Viewport.font", font);
      }

      Iterator citer = colormap.keySet().iterator();
      while (citer.hasNext()) {
        String id = (String)citer.next();
        if (uiReverseMap.get(id) != null) {
          String newid = (String) uiReverseMap.get(id);
          setDefaultValue(newid, colormap.get(id));
        }
      }
      Iterator fiter = fontmap.keySet().iterator();
      while (fiter.hasNext()) {
        String id = (String)fiter.next();
        if (uiReverseMap.get(id) != null) {
          String newid = (String) uiReverseMap.get(id);
          setDefaultValue(newid, fontmap.get(id));
        }
      }
      Iterator iiter = iconmap.keySet().iterator();
      while (iiter.hasNext()) {
        String id = (String)iiter.next();
        if (uiReverseMap.get(id) != null) {
          String newid = (String) uiReverseMap.get(id);
          String iconstr = (String)iconmap.get(id);
          ImageIcon icon = getImageIcon(skinpath + iconstr);
          setDefaultValue(newid, icon);
        }
      }
      iiter = sizemap.keySet().iterator();
      while (iiter.hasNext()) {
        String id = (String)iiter.next();
        if (uiReverseMap.get(id) != null) {
          String newid = (String) uiReverseMap.get(id);
          Integer value = new Integer((String)sizemap.get(id));
          setDefaultValue(newid, value);
        }
      }

      Iterator miter = marginmap.keySet().iterator();
      while (miter.hasNext()) {
        String id = (String)miter.next();
        if (uiReverseMap.get(id) != null) {
          String newid = (String) uiReverseMap.get(id);
          Insets value = (Insets)marginmap.get(id);
          setDefaultValue(newid, value);
        }
      }

      Iterator biter = bordermap.keySet().iterator();
      while (biter.hasNext()) {
        String id = (String)biter.next();
        if (uiReverseMap.get(id) != null) {
          String newid = (String) uiReverseMap.get(id);
          setDefaultValue(newid, getBorder(null, (NamedNodeMap)bordermap.get(id)));
        }
      }

      if ((colormap.get("scrollbar_thumb") != null) &&
          (colormap.get("scrollbar_thumb_highlight") != null) &&
          (colormap.get("scrollbar_thumb_shadow") != null)) {
        ArrayList arraylist = new ArrayList();
        arraylist.add(new Double(0.3));
        arraylist.add(new Double(0.0));
        arraylist.add(getColor("scrollbar_thumb_shadow"));
        arraylist.add(getColor("scrollbar_thumb_highlight"));
        arraylist.add(getColor("scrollbar_thumb"));
        setDefaultValue("ScrollBar.gradient", arraylist);
      }
    } catch (Exception e) {
        log.error("setDefaults(): error", e);
    }
    if (colormap.get("song_background_default") == null) {
      if (colormap.get("table_cell_background") != null) colormap.put("song_background_default", colormap.get("table_cell_background"));
      else if (colormap.get("default_background") != null) colormap.put("song_background_default", colormap.get("default_background"));
      else colormap.put("song_background_default", UIManager.getColor("Table.background"));
    }
    if (colormap.get("mixout_background_default") == null) {
      if (colormap.get("table_cell_background") != null) colormap.put("mixout_background_default", colormap.get("table_cell_background"));
      else if (colormap.get("default_background") != null) colormap.put("mixout_background_default", colormap.get("default_background"));
      else colormap.put("mixout_background_default", UIManager.getColor("Table.background"));
    }
    if (colormap.get("style_background_default") == null) {
      if (colormap.get("list_cell_background") != null) colormap.put("style_background_default", colormap.get("list_cell_background"));
      else if (colormap.get("default_background") != null) colormap.put("style_background_default", colormap.get("default_background"));
      else colormap.put("style_background_default", UIManager.getColor("List.background"));
    }

  }

  public int defaultBlinkRate;
  public void setBlinkRate(int rate) {
    if (rate == 0) rate = defaultBlinkRate;
    for (int i = 0; i < textcomponent_map.size(); ++i) {
      JTextComponent textcomponent = (JTextComponent)textcomponent_map.get(i);
      textcomponent.getCaret().setBlinkRate(rate);
    }
  }
  public void resetBlinkRate() {
    setBlinkRate(defaultBlinkRate);
  }

  private void updateComponents() {
    Collection dialogs = REDialog.instances.values();
    Iterator dialogiter = dialogs.iterator();
    while (dialogiter.hasNext()) {
      REDialog dialog = (REDialog)dialogiter.next();
      dialog.PostInit();
      try {
          SwingUtilities.updateComponentTreeUI(dialog.getDialog());
      } catch (Exception e) {
          // this was needed for Substance skins errors...
          log.trace("updateComponents(): error updating component tree ui", e);
      }
      if (dialog.isVisible()) dialog.repaint();
//        dialog.getDialog().invalidate();
    }

    updateMenuItems(SearchListMouse.instance);
    updateMenuItems(StyleIncludeMouse.instance);
    updateMenuItems(StyleExcludeMouse.instance);
    updateMenuItems(ListStyleSongMouse.instance);
    updateMenuItems(MixListMouse.instance);
    updateMenuItems(BrokenLinkSongsUI.instance.listmouse);
    
    SwingUtilities.updateComponentTreeUI(getFrame("main_frame"));
  }

  public boolean usedefaultborders = false;
  private boolean use_default_splitpanel_dividers = false;
  private boolean use_empty_splitpanel_dividers = false;
  private boolean use_liquid_buttons = true;
  
  public JFrame getFrame(String id) {
    JFrame frame = (JFrame)getInstance(id, usedefaultborders ? MyFrame.class : IFrame.class, null);
    return frame;
  }

  HashMap ImageIconMap = new HashMap();

  public ImageIcon getImageIcon(String path) {
    if (ImageIconMap.get(path) == null)
      ImageIconMap.put(path, new ImageIcon(path));
    return (ImageIcon)ImageIconMap.get(path);
  }
  
  private ImageIcon getImageIcon(String path, String text) {
    // right now we're ignoring the text portion of image icons to save memory
    return getImageIcon(path);
  }

  private void jbInit() throws Exception {
  }

  public void printTypeMaps() {
      log.info("TYPE MAPS:");
    Iterator typeiter = uiTypeMap.entrySet().iterator();
    String[] array = new String[uiTypeMap.size()];
    int count = 0;
    while (typeiter.hasNext()) {
      Map.Entry entry = (Map.Entry)typeiter.next();
      String id = (String)entry.getKey();
      String value = (String)entry.getValue();
      array[count++] = value;
    }
    Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < array.length; ++i) log.info(array[i]);
  }

  public void printMessageMaps() {
    log.info("MESSAGE MAPS:");
    Iterator typeiter = messagemap.entrySet().iterator();
    String[] array = new String[messagemap.size()];
    int count = 0;
    while (typeiter.hasNext()) {
      Map.Entry entry = (Map.Entry)typeiter.next();
      String id = (String)entry.getKey();
      String value = (String)entry.getValue();
      array[count++] = id;
    }
    Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < array.length; ++i) log.info(array[i]);
  }

  public void printDialogMessageMaps() {
    log.info("MESSAGE MAPS:");
    Iterator typeiter = errormap.entrySet().iterator();
    String[] array = new String[errormap.size()];
    int count = 0;
    while (typeiter.hasNext()) {
      Map.Entry entry = (Map.Entry)typeiter.next();
      String id = (String)entry.getKey();
      Error value = (Error)entry.getValue();
      array[count++] = value.getID();
    }
    Arrays.sort(array, String.CASE_INSENSITIVE_ORDER);
    for (int i = 0; i < array.length; ++i) log.info(array[i]);
  }

  public void setText(String id, String text) {
    if (imap.get(id) instanceof JTextField) {
      JTextField field = (JTextField)imap.get(id);
      field.setText(text);
    } else if (imap.get(id) instanceof JLabel) {
        JLabel label = (JLabel)imap.get(id);
        label.setText(text);        
    }
  }

  public void addChangeListener(String id, Object listener) {
    if ((imap.get(id) instanceof JTextField) && (listener instanceof DocumentListener)){
      JTextField field = (JTextField)imap.get(id);
      field.getDocument().addDocumentListener((DocumentListener)listener);
    }
  }

  public void enableChangeListener(String id) {
    if ((imap.get(id) instanceof JTextField)){
      JTextField field = (JTextField)imap.get(id);
      field.getDocument().addDocumentListener(new AudioUpdateField(id));
    }
  }

  public int getSize(String id) {
      return Integer.parseInt((String)sizemap.get(id));
  }
  
  public String getFieldText(String id) {
    if ((imap.get(id) instanceof JTextField)){
     JTextField field = (JTextField)imap.get(id);
     return field.getText();
   }
   return null;
  }
  
  public JList getList(String id) {
      if ((imap.get(id) instanceof JList)){
          JList list = (JList)imap.get(id);
          return list;
        }
      REList list = new REList();
      imap.put(id, list);
      return list;
  }
  
  public String getString(String key) {
      try {
          return bundle.getString(key);
      } catch (Exception e) {
          // missing resource
      }
      return null;
  }
  
  private int tableRowHeight = 14;
  public int getTableRowHeight() { return tableRowHeight; }

}


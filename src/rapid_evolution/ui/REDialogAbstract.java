package rapid_evolution.ui;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
abstract class REDialogAbstract {
  abstract boolean PreDisplay(Object source);
  abstract boolean PreDisplay();
  abstract void Display(Object source);
  abstract void PostDisplay(Object source);
  abstract void PostDisplay();
  abstract void PostInit();
  abstract void reset();
}

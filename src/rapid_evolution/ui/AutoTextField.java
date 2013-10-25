package rapid_evolution.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import rapid_evolution.FieldIndex;
import com.mixshare.rapid_evolution.ui.swing.textfield.RETextField;

public class AutoTextField extends RETextField implements KeyListener {
    private static Logger log = Logger.getLogger(AutoTextField.class);

    class AutoDocument extends PlainDocument {        
     
       private boolean autoEnabled = true;
       
       public void setAutoEnabled(boolean val) {
           autoEnabled = val;
       }
        
    public void replace(int i, int j, String s, AttributeSet attributeset)
        throws BadLocationException {
        if (log.isTraceEnabled()) log.trace("replace(): i=" + i + ", j=" + j + ", s=" + s + ", attributeset=" + attributeset);
      super.remove(i, j);      
      insertString(i, s, attributeset);
    }

    public void insertString(int i, String s, AttributeSet attributeset)
        throws BadLocationException {
        if (log.isTraceEnabled()) log.trace("insertString(): i=" + i + ", s=" + s + ", attributeset=" + attributeset);
        if (!autoEnabled || ( i < getLength())) {
            super.insertString(i, s, attributeset);
            return;            
        }
      if (s == null || "".equals(s))
        return;
      String s1 = getText(0, i);
      String s2 = getMatch(s1 + s);
      int j = (i + s.length()) - 1;
      if (isStrict && s2 == null) {
        s2 = getMatch(s1);
        j--;
      } else if (!isStrict && s2 == null) {
        super.insertString(i, s, attributeset);
        return;
      }
      super.remove(0, getLength());
      super.insertString(0, s2, attributeset);
      setSelectionStart(j + 1);
      setSelectionEnd(getLength());
    }

    protected void removeUpdate(AbstractDocument.DefaultDocumentEvent chng) {
        boolean before_autoenabled = autoEnabled;
        autoEnabled = false;
        super.removeUpdate(chng);
        autoEnabled = before_autoenabled;
    }
    
    public void remove(int i, int j) throws BadLocationException {
        if (log.isTraceEnabled()) log.trace("remove(): i=" + i + ", j=" + j);

        int k = getSelectionStart();
//      if (k > 0)
//        k--;

      super.remove(i, j);      
      /*
      String s = getMatch(getText(0, k));
      if (!isStrict && s == null) {
        super.remove(i, j);
      } else {
        super.remove(0, getLength());
        super.insertString(0, s, null);
      }
      */
      
      try {
          //setSelectionStart(k);
        //setSelectionEnd(getLength());
      } catch (Exception exception) {
          log.error("remove(): error Exception", exception);
      }
      
    }

  }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
    
  public AutoTextField() {
    isCaseSensitive = false;
    isStrict = false;
    setDocument(new AutoDocument());
    addKeyListener(this);
  }

  public void setText(String value) {
      if (log.isTraceEnabled()) log.trace("setText(): value=" + value);
      AutoDocument autodocument = (AutoDocument) getDocument();
      autodocument.setAutoEnabled(false);
      super.setText(value);
      autodocument.setAutoEnabled(true);
      if (log.isTraceEnabled()) log.trace("setText(): end, value=" + getText());
  }
  
  private String getMatch(String s) {
      if (index != null) {
          return index.getMatch(s);
      }
      return null;
  }

  public void replaceSelection(String s) {
      if (log.isTraceEnabled()) log.trace("replaceSelection(): s=" + s);
    AutoDocument _lb = (AutoDocument) getDocument();
    if (_lb != null)
      try {
        int i = Math.min(getCaret().getDot(), getCaret().getMark());
        int j = Math.max(getCaret().getDot(), getCaret().getMark());
        _lb.replace(i, j - i, s, null);
      } catch (Exception exception) {
      }
  }

  public boolean isCaseSensitive() {
    return isCaseSensitive;
  }

  public void setCaseSensitive(boolean flag) {
    isCaseSensitive = flag;
  }

  public boolean isStrict() {
    return isStrict;
  }

  public void setStrict(boolean flag) {
    isStrict = flag;
  }

  public void setFieldIndex(FieldIndex index) {
      this.index = index;
  }
  
  private FieldIndex index;

  private boolean isCaseSensitive;

  private boolean isStrict;

}
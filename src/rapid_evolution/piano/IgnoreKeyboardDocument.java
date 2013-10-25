package rapid_evolution.piano;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

public class IgnoreKeyboardDocument extends PlainDocument {

    public void insertString(int offs, String str, AttributeSet a) {
      // reject the insert but print a message on the console
    }       
}
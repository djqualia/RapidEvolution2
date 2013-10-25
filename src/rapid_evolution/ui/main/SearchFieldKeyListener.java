package rapid_evolution.ui.main;

import rapid_evolution.RapidEvolution;
import rapid_evolution.SongLinkedList;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

import rapid_evolution.ui.RapidEvolutionUI;

import rapid_evolution.ui.OptionsUI;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class SearchFieldKeyListener extends KeyAdapter implements DocumentListener {

    private static Logger log = Logger.getLogger(SearchFieldKeyListener.class);
    
  public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == e.VK_ENTER) {
      RapidEvolutionUI.instance.findsearched = true;
      RapidEvolutionUI.instance.bpmsearched = false;
      RapidEvolutionUI.instance.keysearched = false;
      RapidEvolutionUI.instance.Search();
    }
  }
  
  public void insertUpdate(DocumentEvent e) {
      textChanged();
  }
  public void removeUpdate(DocumentEvent e) {
      textChanged();  }
  public void changedUpdate(DocumentEvent e) {
      textChanged();  }  

  private String last_text = "";  
  private void textChanged() {
      if (log.isTraceEnabled()) log.trace("textChanged(): field value=" + SearchPane.instance.searchfield.getText());
      if (!SearchPane.instance.searchfield.getText().equals(last_text)) {
          if (OptionsUI.instance.automaticsearchonuserinput.isSelected()) {
              if (RapidEvolutionUI.instance.isSearching()) RapidEvolutionUI.instance.searchpending = true;
              else {
                  new Thread() { public void run() {
                      if (log.isTraceEnabled()) log.trace("textChanged(): initiating search");
                      RapidEvolutionUI.instance.Search();
                  }}.start();
              }
          }
          last_text = SearchPane.instance.searchfield.getText();      
      }
  }
  
}

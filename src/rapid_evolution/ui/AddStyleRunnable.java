package rapid_evolution.ui;

import rapid_evolution.StyleLinkedList;

abstract public class AddStyleRunnable implements Runnable {

    protected StyleLinkedList style = null;
    public void setStyle(StyleLinkedList style) {
        this.style = style;
    }
    
    abstract public void run();
    
}

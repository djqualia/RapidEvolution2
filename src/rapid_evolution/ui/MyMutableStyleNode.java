package rapid_evolution.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import rapid_evolution.*;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

public class MyMutableStyleNode extends DefaultMutableTreeNode {
    
    private static Logger log = Logger.getLogger(MyMutableStyleNode.class);
    
    private StyleLinkedList style;
    private String stylename;
    public MyMutableStyleNode(StyleLinkedList style, String name) {
        // TODO: filter parent style names out of name used in tree label
        super(name);       
        stylename = name;
        this.style = style;
    }
    public StyleLinkedList getStyle() {
        return style;
    }
    public boolean equals(Object o) {
        if (o instanceof MyMutableStyleNode) {
            return style.equals(((MyMutableStyleNode)o).getStyle());
        }
        return false;
    }
        
    public String toString() { return stylename; }
    
    public boolean containsStyle(StyleLinkedList child) {
        for (int c = 0; c < this.getChildCount(); ++c) {
            MyMutableStyleNode child_node = (MyMutableStyleNode)this.getChildAt(c);
            if (child_node.getStyle().equals(child)) return true;
        }
        return false;
    }
        
    
}

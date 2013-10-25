package rapid_evolution.ui;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import rapid_evolution.*;
import rapid_evolution.ui.styles.*;
import java.util.Vector;

import java.util.Map;

public class ToggleTreeSelectionModel extends DefaultTreeSelectionModel {

    private static boolean debug = false;
    
    private RETree tree;    
    
    public ToggleTreeSelectionModel(RETree tree) {
        this.tree = tree;
        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);        
    }
    
    public void setSelectionPath(TreePath path) {
        if (debug) System.out.println("setSelectionPath(): path=" + path);
        if(allowSelection()) {        
            if (debug) System.out.println("path: " + path);
            if (this.isPathSelected(path)) {
                if (debug) System.out.println("already selected, deselecting");
                MyMutableStyleNode node = (MyMutableStyleNode)path.getLastPathComponent();
                deselectAllStyles(node.getStyle());
            } else {
                if (debug) System.out.println("selecting");
                MyMutableStyleNode node = (MyMutableStyleNode)path.getLastPathComponent();
                selectAllStyles(node.getStyle());
            }
        }
    }

    public void selectAllStyles(StyleLinkedList style) {
        tree.setExpandsSelectedPaths(false);
        Vector nodes = (Vector)((Map)StylesUI.getMap(tree)).get(style);
        if (nodes != null) {
            for (int n = 0; n < nodes.size(); ++n) {
                MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(n);
                super.addSelectionPath(new TreePath(node.getPath()));
            }
        }
        tree.setExpandsSelectedPaths(true);
    }
    
    public void deselectAllStyles(StyleLinkedList style) {
        tree.setExpandsSelectedPaths(false);
        Vector nodes = (Vector)((Map)StylesUI.getMap(tree)).get(style);
        if (nodes != null) {
            for (int n = 0; n < nodes.size(); ++n) {
                MyMutableStyleNode node = (MyMutableStyleNode)nodes.get(n);
                super.removeSelectionPath(new TreePath(node.getPath()));
            }
        }        
        tree.setExpandsSelectedPaths(true);
    }
    
    public void addSelectionPaths(TreePath[] paths){
        if (debug) System.out.println("addSelectionPaths(): paths=" + paths);
        if(allowSelection()){
            super.addSelectionPaths(paths);
        }
    }
    
    public void addSelectionPath(TreePath path) {
        if (debug) System.out.println("addSelectionPath(): path=" + path);        
        if(allowSelection()){
            super.addSelectionPath(path);
        }
    }

    
    public void setSelectionPaths(TreePath[] paths){
        if (debug) System.out.println("setSelectionPaths(): paths=" + paths);
        if(allowSelection()){
            super.setSelectionPaths(paths);
        }
    }
        
    private boolean allows = false;
    public void setAllowsSelection(boolean allows) {
        this.allows = allows;
    }
    private boolean allowSelection(){
        return allows;
    }
    
}
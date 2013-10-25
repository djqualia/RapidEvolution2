package com.mixshare.rapid_evolution.data.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class Index {
    
    static private Logger log = Logger.getLogger(Index.class);
    
    private Map byId = new HashMap();
    private Map byStringId = new HashMap();
    
    public Iterator getIterator() {
        Collection values = byId.values();
        if (values != null)
            return values.iterator();
        return null;
    }
    
    public IndexItem getItem(int uniqueId) {
        return (IndexItem)byId.get(new Integer(uniqueId));
    }
    
    public IndexItem getItem(String uniqueStringId) {
        return (IndexItem)byStringId.get(uniqueStringId);
    }
    
    public void add(IndexItem item) {
        try {
            byId.put(new Integer(item.getUniqueId()), item);
            byStringId.put(item.getUniqueStringId(), item);
        } catch (Exception e) {
            log.error("add(): error Exception", e);
        }
    }        
    
    public void update(IndexItem item, String oldUniqueStringId) {
        try {
            if (!item.getUniqueStringId().equals(oldUniqueStringId)) {
                byStringId.remove(oldUniqueStringId);
                byStringId.put(item.getUniqueStringId(), item);
            }
        } catch (Exception e) {
            log.error("update(): error Exception", e);
        }            
    }
    
    public void remove(IndexItem item) {
        try {
            byId.remove(new Integer(item.getUniqueId()));
            byStringId.remove(item.getUniqueStringId());
        } catch (Exception e) {
            log.error("remove(): error Exception", e);
        }            
    }

}

package rapid_evolution;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class FieldIndex {

    private static Logger log = Logger.getLogger(FieldIndex.class);
    
    private HashMap map = new HashMap();
    private static Date initial_date = new Date();
    
    private class ItemInfo {
        private Date lastused;
        private int num_entries;
        public ItemInfo(Date lastused, int num_entries) {
            this.lastused = lastused;
            this.num_entries = num_entries;
        }
        public Date getLastUsed() { return lastused; }
        public void setLastUsed(Date date) { lastused = date; }
        public int getNumEntries() { return num_entries; }
        public void setNumEntries(int entries) { num_entries = entries; }
    }
        
    public void addItem(String item, boolean set_lastupdated) {
        ItemInfo info = (ItemInfo)map.get(item);
        if (info != null) {
            info.setNumEntries(info.getNumEntries() + 1);
            if (set_lastupdated) info.setLastUsed(new Date());
        } else {            
            map.put(item, new ItemInfo(set_lastupdated ? new Date() : initial_date, 1));
        }        
    }
    
    public void addItem(String item) {
        addItem(item, true);
    }
    
    public void removeItem(String item) {
        ItemInfo info = (ItemInfo)map.get(item);
        if (info != null) {
            int num_entries = info.getNumEntries();
            if (num_entries == 1) {
                map.remove(item);
            } else {
                info.setNumEntries(num_entries - 1);
            }
        }            
    }
    
    public String getMatch(String text) {
        if ((text == null) || text.equals("")) return null;
        try {
            Set entries = map.entrySet();
            if (entries != null) {                
                String best_entry = null;
                ItemInfo best_info = null;
                Iterator iter = entries.iterator();
                if (iter != null) {
                    while (iter.hasNext()) {
                        Entry entry = (Entry)iter.next();
                        String item = (String)entry.getKey();
                        ItemInfo info = (ItemInfo)entry.getValue();
                        if (item.toLowerCase().startsWith(text.toLowerCase())) {
                            if ((best_entry == null) || 
                               (info.getLastUsed().after(best_info.getLastUsed())) ||
                               (info.getLastUsed().equals(best_info.getLastUsed()) && (info.getNumEntries() > best_info.getNumEntries()))
                               ) {
                                best_entry = item;
                                best_info = info;
                            } 
                        }
                    }
                }
                if (best_info != null) {
                    best_info.setLastUsed(new Date());
                }
                if (best_entry != null)
                    return text + best_entry.substring(text.length());
                return best_entry;
            }
        } catch (Exception e) {
            log.error("getMatch(): error", e);
        }
        return null;
    }

    
}

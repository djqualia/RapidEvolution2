package com.mixshare.rapid_evolution.util.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class LRUCache {

    private static Logger log = Logger.getLogger(LRUCache.class);
    
    private int max_size;
    private Map cache = new HashMap();
    private Map cache_hits = new HashMap();
    
    public LRUCache(int max_size) {
        this.max_size = max_size;
    }
    
    public void add(Object key, Object value) {
        cache.put(key, value);
        cache_hits.put(key, new Date());
        if (cache.size() > max_size) removeLeastHitCache();
    }
    
    public Object get(Object key) {
        if (cache_hits.containsKey(key)) {
            cache_hits.put(key, new Date());
        }
        return cache.get(key);
    }
    
    private void removeLeastHitCache() {
        if (log.isTraceEnabled()) log.trace("removeLeastHitCache(): max cache size reached: " + max_size);
        Date min_date = null;
        Object min_key = null;
        Iterator iter = cache_hits.entrySet().iterator();
        while (iter.hasNext()) {
            Entry entry = (Entry)iter.next();
            Date hit_date = (Date)entry.getValue();
            if ((min_key == null) || (hit_date.before(min_date))) {
                min_key = entry.getKey();
                min_date = hit_date;
            }
        }
        cache_hits.remove(min_key);
        cache.remove(min_key);        
    }
    
}

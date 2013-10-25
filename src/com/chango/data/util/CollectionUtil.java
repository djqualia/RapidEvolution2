package com.chango.data.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class CollectionUtil {

    private static Logger log = Logger.getLogger(CollectionUtil.class);
	
    static public boolean areObjectsEqual(Object obj1, Object obj2) {
    	if ((obj1 == null) && (obj2 == null))
    		return true;
    	if (obj1 == null)
    		return false;
    	if (obj2 == null)
    		return false;
		return (obj1.equals(obj2));
    }
    
    static public boolean areCollectionsEqual(Collection col1, Collection col2) {
    	return areCollectionsEqual(col1, col2, false);
    }	
    static public boolean areCollectionsEqual(Collection col1, Collection col2, boolean debug) {
    	if ((col1 == null) && (col2 == null))
    		return true;
    	if (col1 == null)
    		return false;
    	if (col2 == null)
    		return false;
    	if (col1.size() != col2.size())
    		return false;
    	Iterator iter1 = col1.iterator();
    	Iterator iter2 = col2.iterator();
    	while (iter1.hasNext() && iter2.hasNext()) {
			Object value1 = iter1.next();
			Object value2 = iter2.next();
			if (!areObjectsEqual(value1, value2)) {
    			if (debug)
    				log.debug("areCollectionsEqual(): objects not equal, value1=" + value1 + ", value2=" + value2);    										
				return false;
			}
    	}    	    	
    	if (iter1.hasNext())
    		return false;
    	if (iter2.hasNext())
    		return false;
    	return true;
    }
    
    static public boolean areArraysEqual(Object[] col1, Object[] col2) {
    	return areArraysEqual(col1, col2, false);
    }	    
    static public boolean areArraysEqual(Object[] col1, Object[] col2, boolean debug) {
    	if ((col1 == null) && (col2 == null))
    		return true;
    	if (col1 == null)
    		return false;
    	if (col2 == null)
    		return false;
    	if (col1.length != col2.length)
    		return false;
    	int i1 = 0;
    	int i2 = 0;
    	while ((i1 < col1.length) && (i2 < col2.length)) {
			Object value1 = col1[i1++];
			Object value2 = col2[i2++];
			if (!areObjectsEqual(value1, value2)) {
    			if (debug)
    				log.debug("areCollectionsEqual(): objects not equal, value1=" + value1 + ", value2=" + value2);    										
				return false;
			}
    	}    	    	
    	return true;    	
    }
    
    static public boolean areShortArraysEqual(short[] col1, short[] col2) {
    	return areShortArraysEqual(col1, col2, false);
    }	    
    static public boolean areShortArraysEqual(short[] col1, short[] col2, boolean debug) {
    	if ((col1 == null) && (col2 == null))
    		return true;
    	if (col1 == null)
    		return false;
    	if (col2 == null)
    		return false;
    	if (col1.length != col2.length)
    		return false;
    	int i1 = 0;
    	int i2 = 0;
    	while ((i1 < col1.length) && (i2 < col2.length)) {
    		short value1 = col1[i1++];
    		short value2 = col2[i2++];
			if (value1 != value2) {
    			if (debug)
    				log.debug("areShortArraysEqual(): objects not equal, value1=" + value1 + ", value2=" + value2);    										
				return false;
			}
    	}    	    	
    	return true;    	
    }        
    
    static public boolean areIntArraysEqual(int[] col1, int[] col2) {
    	return areIntArraysEqual(col1, col2, false);
    }	    
    static public boolean areIntArraysEqual(int[] col1, int[] col2, boolean debug) {
    	if ((col1 == null) && (col2 == null))
    		return true;
    	if (col1 == null)
    		return false;
    	if (col2 == null)
    		return false;
    	if (col1.length != col2.length)
    		return false;
    	int i1 = 0;
    	int i2 = 0;
    	while ((i1 < col1.length) && (i2 < col2.length)) {
			int value1 = col1[i1++];
			int value2 = col2[i2++];
			if (value1 != value2) {
    			if (debug)
    				log.debug("areIntArraysEqual(): objects not equal, value1=" + value1 + ", value2=" + value2);    										
				return false;
			}
    	}    	    	
    	return true;    	
    }    
    
    static public boolean areFloatArraysEqual(float[] col1, float[] col2) {
    	return areFloatArraysEqual(col1, col2, false);
    }	    
    static public boolean areFloatArraysEqual(float[] col1, float[] col2, boolean debug) {
    	if ((col1 == null) && (col2 == null))
    		return true;
    	if (col1 == null)
    		return false;
    	if (col2 == null)
    		return false;
    	if (col1.length != col2.length)
    		return false;
    	int i1 = 0;
    	int i2 = 0;
    	while ((i1 < col1.length) && (i2 < col2.length)) {
    		float value1 = col1[i1++];
    		float value2 = col2[i2++];
			if (value1 != value2) {
    			if (debug)
    				log.debug("areFloatArraysEqual(): objects not equal, value1=" + value1 + ", value2=" + value2);    										
				return false;
			}
    	}    	    	
    	return true;    	
    }      
    
    static public boolean areMapsEqual(Map map1, Map map2) {
    	return areMapsEqual(map1, map2, false);
    }
    static public boolean areMapsEqual(Map map1, Map map2, boolean debug) {
    	if ((map1 == null) && (map2 == null))
    		return true;
    	if (map1 == null)
    		return false;
    	if (map2 == null)
    		return false;
    	if (map1.size() != map2.size()) {
    		if (debug)
    			log.debug("areMapsEqual(): sizes not equal, size1=" + map1.size() + ", size2=" + map2.size());
    		return false;
    	}
    	Iterator iter1 = map1.entrySet().iterator();
    	//Iterator iter2 = map2.entrySet().iterator();
    	while (iter1.hasNext()) {
    		Entry entry1 = (Entry)iter1.next();
    		Object key1 = entry1.getKey();
			Object value1 = entry1.getValue();
			Object value2 = map2.get(key1);
			if ((value1 instanceof Map) && (value2 instanceof Map)) {
				if (!areMapsEqual((Map)value1, (Map)value2, debug)) {
	    			if (debug)
	    				log.debug("areMapsEqual(): maps not equal, key=" + key1 + ", value1=" + value1 + ", value2=" + value2);    					
					return false;
				}
			} else if ((value1 instanceof Collection) && (value2 instanceof Collection)) {
				if (!areCollectionsEqual((Collection)value1, (Collection)value2, debug)) {
	    			if (debug)
	    				log.debug("areMapsEqual(): collections not equal, key=" + key1 + ", value1=" + value1 + ", value2=" + value2);    					
					return false;
				}
			} else {
				if (!areObjectsEqual(value1, value2)) {
	    			if (debug)
	    				log.debug("areMapsEqual(): values not equal, key=" + key1 + ", value1=" + value1 + ", value2=" + value2);    					
					return false;
				}
			}
    	}
    	return true;
    }
    
}

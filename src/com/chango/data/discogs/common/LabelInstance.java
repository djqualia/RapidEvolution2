package com.chango.data.discogs.common;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.chango.data.util.CollectionUtil;

/**
 * This class is referenced indirectly by the Discogs API Wrapper and related classes.  It should have been renamed to a different package within com.mixshare however changing it 
 * on the server side would have resulted in potential serialization problems.  So I'm just playing it safe and copying it over since its just a bean basically...
 */
public class LabelInstance implements Comparable, Serializable {

    static private final long serialVersionUID = 0L;    
    
    private String name;
    private String catno;
    
    public LabelInstance(String name, String catno) {
        this.name = name;
        this.catno = catno;
    } 
	
    public String getName() { return name; }
    public String getCatalogId() { return catno; }
    
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(name);
        if ((catno != null) && (catno.length() > 0)) {
            result.append(" (");
            result.append(catno);
            result.append(")");
        }
        return result.toString();
    }
    
	public boolean equals(Object o) {
		if (o instanceof LabelInstance) {
			LabelInstance oP = (LabelInstance)o;
			if (!CollectionUtil.areObjectsEqual(oP.name, name))
				return false;
			if (!CollectionUtil.areObjectsEqual(oP.catno, catno))
				return false;
			return true;
		}
		return false;
	}     

    public int hashCode() {
        return toString().hashCode();
    }
    
    public int compareTo(Object o) {
        return toString().compareToIgnoreCase(o.toString());        
    }
    
}

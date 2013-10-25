package com.mixshare.rapid_evolution.audio.tags.util;

import java.util.Vector;

import rapid_evolution.ui.OptionsUI;

import com.mixshare.rapid_evolution.audio.tags.TagConstants;

public class TagUtil {
    
    public static String getStyleTagId(int style_number) {
        String styleNumStr = String.valueOf(style_number);
        if (styleNumStr.length() == 1) styleNumStr = "0" + styleNumStr;
        String identifier = TagConstants.TXXX_STYLES_PREFIX + styleNumStr;            
        return identifier;
    }
    
    public static String[] getStyleStringArray(Vector styles) {
        String[] result = new String[styles.size()];
        for (int s = 0; s < result.length; ++s)
            result[s] = (String)styles.get(s);
        return result;        
    }
    
    public static String getUser1TagId() {
        String value = convertToValidTagId(OptionsUI.instance.customfieldtext1.getText());
        if (value.length() > 0) return value;
        return "CUSTOM_USER_FIELD_1";
    }
    
    public static String getUser2TagId() {
        String value = convertToValidTagId(OptionsUI.instance.customfieldtext2.getText());
        if (value.length() > 0) return value;
        return "CUSTOM_USER_FIELD_2";        
    }

    public static String getUser3TagId() {
        String value = convertToValidTagId(OptionsUI.instance.customfieldtext3.getText());
        if (value.length() > 0) return value;
        return "CUSTOM_USER_FIELD_3";        
    }

    public static String getUser4TagId() {
        String value = convertToValidTagId(OptionsUI.instance.customfieldtext4.getText());
        if (value.length() > 0) return value;
        return "CUSTOM_USER_FIELD_4";        
    }
    
    private static String convertToValidTagId(String value) {
    	// the line below fixes a problem with traktor ratings, traktor will not read "RATING_WMP"...
    	if ((value != null) && (value.equalsIgnoreCase("RATING WMP")))
    		return value;
        value = value.trim();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c == ' ') result.append("_");
            else result.append(c);
        }
        return result.toString();
    }
    
}

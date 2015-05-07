package com.petroldesigns.chatbot;

import java.util.HashMap;
import java.util.Vector;
import com.aol.acc.AccPreferencesHook;

/**
 * The class consisting of boilerplate code used for storing the AOL preferences
 * @author atrank
 */
public class PetrolBotPrefs extends AccPreferencesHook {
	
	HashMap<String, String> map;
	
	public PetrolBotPrefs() {
		map = new HashMap<String, String>();
	}
		
	public String getValue(String specifier) {
		return (map.get(specifier));
	}
	
	public String getDefaultValue(String specifier) {
		return null;
	}

	public void setValue(String specifier, String value) {
		map.put(specifier, value);
	}

	public void reset(String specifier) {
		map.put(specifier, null);
	}

	public String[] getChildSpecifiers(String specifier) {
		Vector<String> v = new Vector<String>();
		for(String s : map.keySet()){
			if(s.startsWith(specifier) && !s.equals(specifier)){
				v.add(s);
			}
		}
		if(v.size() > 0){
			return (String[])v.toArray(new String[0]);
		} else {
			return null;
		}
	}
}

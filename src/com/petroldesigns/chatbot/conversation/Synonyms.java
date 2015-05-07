package com.petroldesigns.chatbot.conversation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.petroldesigns.chatbot.hibernate.DAO;

/**
 * Synonyms stores a collection of configuration specified synonymous words
 * and phrases that can be used within a conversation
 * @author atrank
 *
 */
public class Synonyms {
	
	public static Logger logger = Logger.getLogger(Synonyms.class.getSimpleName());
	
	static Synonyms instance;
	private HashMap<String, ArrayList<String>> synStore;
	
	/**
	 * Private constructor used to create a new instance of the singleton Synonyms class
	 */
	private Synonyms(){
		logger.setLevel(Level.INFO);
		synStore = new HashMap<String, ArrayList<String>>();
		Synonyms.instance = this;
	}

	/**
	 * The public static method for getting an instance of the Synonyms class
	 * @return the Singleton instance of Synonyms
	 */
	public static Synonyms getInstance(){
		if (Synonyms.instance == null) {
			return new Synonyms();
		} else {
			return Synonyms.instance;
		}
	}
	
	/**
	 * load the specified key=value1,value2,...,valueN file containing the list of synonyms.
	 * @param file the path to the file containing the synonyms definition.
	 */
	public void load(String file){
		logger.debug("loading configuration file:" + file);
		Properties props = new Properties();
		try {
			FileInputStream in = new FileInputStream(file);
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			logger.fatal("The file \"" + file + "\" could not be found.");
			System.exit(-1);
		} catch (SecurityException f) {
			logger.fatal("The file \"" + file + "\" could not be read: Access denied.");
			System.exit(-1);
		} catch (IOException g) {
			logger.fatal(g);
			System.exit(-1);
		}
		if (!props.isEmpty()) {
			Enumeration<Object> e = props.keys();
			while (e.hasMoreElements()){
				String key = (String)e.nextElement();
				String value = props.getProperty(key);
				String[] values = value.split(",");
				ArrayList<String> valuesList = new ArrayList<String>(values.length);
				for (String val: values) {
					if (!val.trim().equals("")){
						valuesList.add(val.toLowerCase().trim());
					}
				}
				synStore.put(key.toLowerCase().trim(), valuesList);
			}
		}
	}
	
	/**
	 * Convenience method to check if a word is either synonymous with  "yes" or "no"
	 * @param wrd the word to be checked
	 * @return true if the word is either synonymous with "yes" or "no", returns false otherwise.
	 */
	public boolean isYesOrNo(String wrd) {
		if (meaningMatch("yes", wrd)) return true;
		if (meaningMatch("no", wrd)) return true;
		return false;
	}
	
	/**
	 * check if the specified word matches the semantic meaning of "yes"
	 * @param wrd The word to be checked
	 * @return True if the word means "yes", false otherwise
	 */
	public boolean isYes(String wrd) {
		return meaningMatch("yes", wrd);
	}
	
	/**
	 * check if the specified word matches the semantic meaning of "no"
	 * @param wrd The word to be checked
	 * @return True if the word means "no", false otherwise
	 */
	public boolean isNo(String wrd) {
		return meaningMatch("no", wrd);
	}
	
	/**
	 * Checks if the two specified words have the same semantic meaning as defined in 
	 * by the synonyms datastore.
	 * @param word1 The first word to be checked (must be lowercase)
	 * @param word2 The second word to be checked (must be lowercase)
	 * @return true if the word meanings match, false otherwise
	 */
	public boolean meaningMatch(String word1, String word2) {
		if (synStore.containsKey(word1)) {
			ArrayList<String> l = synStore.get(word1);
			if (l.contains(word2)){
				return true;
			} else {
				return false;
			}
		} else if (synStore.containsKey(word2)) {
			ArrayList<String> l = synStore.get(word2);
			if (l.contains(word1)){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/** 
	 * Gets the Semantic Root of the word specified
	 * @param in The input word to check for a "Semantic Root" (must be lowercase)
	 * @return The Base word if it exists, or "" otherwise
	 */
	public String getSemanticRoot(String in){
		if (in != null && !in.trim().equals("")){
			String[] inn = in.split(" ");
			for (String i: inn) {
				if (synStore.containsKey(i)) {
					return i;
				} 
				for (String rt : synStore.keySet()){
					if (synStore.get(rt).contains(i)) return rt;
				} 
			}
			return "";
		} else {
			return "";
		}
	}
	
	/**
	 * Checks if the specified word is defined as a keyword value in the sybStore datastore.
	 * @param word The word to check for.
	 * @return True if the word is a keyword, false otherwise
	 */
	public boolean containsKeyword(String word) {
		return synStore.containsKey(word);
	}
	
	// FUTURE methods
	// public void addKeyword(String wrd) {}
	// public void addKeywordMatch(String keyword, String wrd) {}
	// public void removeKeyword(String wrd) {}
	// public void removeKeywordMatch(String keyword, String wrd) {}
}

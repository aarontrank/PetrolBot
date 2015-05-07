package com.petroldesigns.chatbot;

import java.io.IOException;
import java.util.jar.Attributes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The PetrolBotConfig class is a singleton that loads and manages a bot configuration
 * from an xml file specified on the command line (or in a default location)
 * @author atrank
 */
public class PetrolBotConfig {
	
	public static Logger logger = Logger.getLogger(PetrolBotConfig.class.getSimpleName());
	
	static PetrolBotConfig instance;

	
	private String key;
	private String aolUsername;
	private String aolPassword;
	
	private String hibernatePath;
	
	private String conversationPath;
	private String synonymsPath;
	
	/**
	 * The default constructor creates a new instance of the class and
	 * sets the static variable "instance" to refer to the new instance
	 */
	private PetrolBotConfig(){
		PetrolBotConfig.instance = this;
	}
	
	/**
	 * Gets a static instance of the Config class, 
	 * if no instance has been created yet, then the constructor is called
	 * otherwise the existing instance is returned
	 * @return The PetrolBotConfig object
	 */
	public static PetrolBotConfig getInstance(){
		if (PetrolBotConfig.instance==null){
			return new PetrolBotConfig();
		} else {
			return PetrolBotConfig.instance;
		}
	}
	
	/**
	 * Loads the configuration object from the XML file specified
	 * @param path The path to the XML configuration file to be parsed
	 */
	public PetrolBotConfig load(String path){
		logger.setLevel(Level.INFO);
		try {
			logger.debug("loading configuration file:" + path);
			PetrolBotConfigParser handler = new PetrolBotConfigParser();
			XMLReader reader = XMLReaderFactory.createXMLReader();
	        reader.setContentHandler(handler);
	        reader.parse(path);
    	} catch (SAXException s) { 
    		logger.fatal("SAXException:" + s);
    		System.exit(-1);
    	} catch (IOException e) {
    		logger.fatal("IOException:" + e);
    		System.exit(-1);
    	}
    	return this;
	}
	
	/**
	 * Gets the AOL Bot Key
	 * @return The Bot Key
	 */
	public String getKey(){
		return this.key;
	}
	
	/**
	 * Gets the AOL Bot Username
	 * @return The Bot Username
	 */
	public String getAOLUsername(){
		return this.aolUsername;
	}
	
	/**
	 * Gets the AOL Bot Password
	 * @return The Bot Password
	 */
	public String getAOLPassword(){
		return this.aolPassword;
	}

	/**
	 * Gets the Path to the Hiberante config file
	 * @return The path to the Hiberante config file
	 */
	public String getHibernatePath(){
		return this.hibernatePath;
	}
	
	/**
	 * Gets the Path to the Synonyms definition config file
	 * @return The path to the Synonyms definition config file
	 */
	public String getSynonymsPath(){
		return this.synonymsPath;
	}
	
	/**
	 * Gets the Path to the Conversation Script xml config file
	 * @return The Path to the Conversation Script xml config file
	 */
	public String getConversationPath(){
		return this.conversationPath;
	}
	
	/**
	 * Sets the AOL Bot Key
	 * @param key Key to set
	 */
	public void setKey(String key){
		this.key = key;
		logger.debug("set key="+key);
	}
	
	/**
	 * Sets the AOL Bot Username
	 * @param uname Bot Username
	 */
	public void setAolUsername(String uname){
		this.aolUsername = uname;
		logger.debug("set aolUsername="+aolUsername);
	}
	
	/**
	 * Sets the AOL Bot Password
	 * @param pass AOL Bot Password
	 */
	public void setAolPassword(String pass){
		this.aolPassword = pass;
		logger.debug("set aolPassword="+aolPassword);
	}

	/** 
	 * Sets the Path to the Hibernate XML configuration file
	 * @param c Path to the Hibernate XML configuration file
	 */
	public void setHibernatePath(String c){
		this.hibernatePath = c;
		logger.debug("set hibernatePath="+hibernatePath);
	}
	
	/** 
	 * Sets the Path to the Conversation XML configuration file
	 * @param c Path to the Conversation XML configuration file
	 */
	public void setConversationPath(String c){
		this.conversationPath = c;
		logger.debug("set conversationPath="+conversationPath);
	}
	
	/**
	 * Sets the Path to the synonyms configuration file
	 * @param s Path to the synonyms configuration file
	 */
	public void setSynonymsPath(String s){
		this.synonymsPath = s;
		logger.debug("set synonymsPath="+synonymsPath);
	}
	
	/**
	 * The SAX parser for the bot configuration file
	 * @author atrank
	 *
	 */
	public class PetrolBotConfigParser extends DefaultHandler {
		PetrolBotConfig config;
	
		public PetrolBotConfigParser() {
			config = PetrolBotConfig.getInstance();
			logger.debug("PetrolBotConfigParser constructed");
		}
		
		@Override
		public void startElement(String uri, String localName, String name,
				org.xml.sax.Attributes attributes) throws SAXException {
			if (localName.equals("petrolbot")){
    			config.setKey((String)attributes.getValue("key"));
    		} else if (localName.equals("aolaccount")) {
    			config.setAolUsername((String)attributes.getValue("username"));
    			config.setAolPassword((String)attributes.getValue("password"));
    		} else if (localName.equals("synonyms")) {
    			config.setSynonymsPath((String)attributes.getValue("path"));
    		} else if (localName.equals("conversation")) {
    			config.setConversationPath((String)attributes.getValue("path"));
    		} else if (localName.equals("hibernate")) {
    			config.setHibernatePath((String)attributes.getValue("path"));
    		}
			
		}
	}
}

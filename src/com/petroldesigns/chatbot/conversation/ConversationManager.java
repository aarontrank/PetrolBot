package com.petroldesigns.chatbot.conversation;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ConversationManager {

	public static Logger logger = Logger.getLogger(ConversationManager.class.getSimpleName());
	
	private ConversationTree cTree;
	
	public ConversationManager(String path){
		cTree = load(path);
	}
	
	public String getText(String state){
		return cTree.getNodeById(state).getText();
	}
	
	public String getNextState (String state, String userReply){
		ConversationNode n = cTree.getNodeById(state).getResponses().get(userReply);
		if (n != null) return n.getId();
		else {
			n = cTree.getNodeById(state).getResponses().get("*");
			if (n != null) return n.getId();
			return null;
		}
	}
	
	public boolean hasNextState(String state){
		if (cTree.getNodeById(state).getResponses().size() == 0) return false;
		return true;
	}
	
	public boolean hasPatternMatch(String state){
		if (cTree.getNodeById(state).getResponses().get("*") == null) return false;
		return true;
	}
	
	public String getStartState(){
		return cTree.getStartId();
	}
	
	private ConversationTree load(String path) {
		ConversationParser handler = new ConversationParser();
		try {
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
    	return handler.tree;
	}
	/**
	 * The SAX parser for the conversation configuration file
	 * @author atrank
	 *
	 */
	public class ConversationParser extends DefaultHandler {
		ConversationTree tree = new ConversationTree();
		String currentId;
		ConversationNode currentNode;
		public void startElement(String uri, String localName,String qName, org.xml.sax.Attributes atts) {
    		if (localName.equals("node")){
    			currentId = (String)atts.getValue("id");
    			currentNode = tree.addNode(currentId);
    			currentNode.setText((String)atts.getValue("text"));
    			logger.debug("add node id="+currentId+ " text=" + (String)atts.getValue("text"));
    			if (((String)atts.getValue("start")) != null && ((String)atts.getValue("start")).equals("true")) {
    				tree.setStart(currentId);
    			}
    		} else if (localName.equals("response")) {
    			if (currentNode != null) {
    				currentNode.addResponse((String)atts.getValue("label"), tree.addNode((String)atts.getValue("action")));
    				logger.debug("add response node="+currentId+ " label=" + (String)atts.getValue("label") + "  action= " + (String)atts.getValue("action"));
    			}
    		}
    	}
	}
}

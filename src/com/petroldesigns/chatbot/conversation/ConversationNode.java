package com.petroldesigns.chatbot.conversation;

import java.util.HashMap;

/**
 * A Conversation Node represents a single interaction within a chat conversation where
 * the chatbot sends a text message associated with the node, the user responds with
 * one of several options (distilled to the simplest synonym -- see the synonyms class),
 * and the response coresponds to a link to the next conversation node within the conversation.
 * With this arrangement, a conversation need not flow linearly, but may contain loops based
 * upon user response
 * @author atrank
 */
public class ConversationNode {
	
	/**
	 * The string identifier for this node
	 */
	private String id;
	
	/**
	 * The text message assocaited to this node
	 */
	private String text;
	
	/**
	 * The map of response options, and coresponding conversation nodes
	 */
	private HashMap<String, ConversationNode> responses;
	
	/**
	 * default constructor
	 */
	public ConversationNode(){
		this.responses = new HashMap<String, ConversationNode>();
	}
	
	/**
	 * constructor with the node identifier specified
	 * @param id The Conversation Node identifier
	 */
	public ConversationNode(String id){
		this.responses = new HashMap<String, ConversationNode>();
		this.id = id;
	}
	
	/**
	 * Sets the id of the Node
	 * @param id The Conversation Node identifier
	 */
	public void setId(String id){
		this.id = id;
	}
	
	/**
	 * Sets the message text associated with this Conversation Node
	 * @param text The text message to associate with this Conversation Node
	 */
	public void setText(String text){
		this.text = text;
	}
	
	/**
	 * Adds a response option and a coresponding Conversation Node to the Responses map
	 * @param label
	 * @param next
	 */
	public void addResponse(String label, ConversationNode next){
		if (next != null) {
			responses.put(label, next);
		}
	}
	
	/**
	 * Gets the Conversation Node identifier
	 * @return The Conversation Node identifier
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Gets the Text Message for this Conversation Node
	 * @return The Text Message for this Conversation Node
	 */
	public String getText(){
		return text;
	}
	
	/** 
	 * Gets the Responses HashMap for this Conversation Node
	 * @return the HashMap of response options and Conversation Nodes for this Conversation Node
	 */
	public HashMap<String, ConversationNode> getResponses(){
		return responses;
	}
}

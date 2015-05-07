package com.petroldesigns.chatbot.conversation;

import java.util.HashMap;

/**
 * The Conversation Tree represents an entire Scripted conversation as a collection
 * of Conversation Nodes with a specific Node as the start Node.   
 * @author atrank
 */
public class ConversationTree {
	
	/**
	 * The String identifier of the Node that represents the beginning of the conversation
	 */
	private String startId;
	
	/**
	 * The Node representing the beginning of the conversation
	 */
	private ConversationNode startNode;
	
	/**
	 * A Map of all nodes in the Conversation Tree, identified by their String identifiers
	 */
	private HashMap<String, ConversationNode> nodeMap;
	
	/**
	 * The default constructor creating a new ConversationTree
	 */
	public ConversationTree() {
		this.startNode = null;
		this.nodeMap = new HashMap<String, ConversationNode>();
	}
	
	/**
	 * Gets the node specified by the given Identifier
	 * @param id The String identifier for the requested node
	 * @return The node cooresponding to the identifier
	 */
	public ConversationNode getNodeById(String id){
		if (!hasNode(id)) return null;
		return nodeMap.get(id);
	}
	
	/**
	 * Determines if the tree contains a node specified by the given id
	 * @param id The id of the node to locate
	 * @return True if the node exists, false otherwise
	 */
	public boolean hasNode(String id){
		return nodeMap.containsKey(id);
	}
	
	/**
	 * Adds a node to the ConversationTree if it doesn't exist
	 * @param id The identifier of the new Node to add
	 * @return The Node that was added, or the node identified by the specified id
	 */
	public ConversationNode addNode(String id){
		if (!hasNode(id)) {
			ConversationNode ret = new ConversationNode(id);
			nodeMap.put(id, ret);
			return ret;
		}
		return nodeMap.get(id);
	}
	
	/**
	 * Sets the node identified as the Start Node of the tree
	 * @param id The String identifier of the node to set as the start node
	 */
	public void setStart(String id){
		this.startId = id;
		this.startNode = addNode(id);
	}
	
	/**
	 * Gets the Start Node
	 * @return The Node that has been specified as being the start node
	 */
	public ConversationNode getStartNode(){
		return this.startNode;
	}
	
	/**
	 * Gets the State Node Id
	 * @return The id of the Start Node
	 */
	public String getStartId(){
		return this.startId;
	}
}

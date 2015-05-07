package com.petroldesigns.chatbot.hibernate;

import java.util.Date;

/**
 * The Session class is a POJO that is used by Hibernate to manage the User Sessions of the PetrolBot
 * @author atrank
 */
public class Session {
	private long id;
	private String username;
	private Date start;
	private Date last;
	private int total;
	private String state;
	
	/**
	 * Default constructor
	 */
	public Session(){}
	
	//SETTERS
	/**
	 * setId() - Sets the unique identifier that is used by the database to identify this session
	 * @param s The ID of the session
	 */
	public void setId(long s){
		this.id = s;
	}
	
	/**
	 * setUsername() - sets the name of the user who owns this session
	 * @param u Username
	 */
	public void setUsername(String u){
		this.username = u;
	}
	
	/**
	 * setStart() - Sets the start time of this session
	 * @param t Start time
	 */
	public void setStart(Date t){
		this.start = t;
	}
	
	/**
	 * setLast() - Sets the last time (Date) that the user interacted with this session
	 * @param t Last interaction time
	 */
	public void setLast(Date t){
		this.last = t;
	}
	
	/**
	 * setTotal() - Sets the total number of times that the user has interacted with this session
	 * @param i Total number of interactions
	 */
	public void setTotal(int i){
		this.total = i;
	}
	
	/**
	 * setState() - Sets the current state of the user conversation
	 * @param s Current conversation state
	 */
	public void setState(String s){
		this.state = s;
	}
	
	//GETTERS
	/**
	 * getId() - Returns the unique identifier that the database uses to identify this session
	 * @return The unique identifier for this session
	 */
	public long getId(){
		return this.id;
	}
	
	/**
	 * getUsername() - Returns the name of the user who owns this session
	 * @return The name of the user who owns this session
	 */
	public String getUsername(){
		return this.username;
	}
	
	/**
	 * getStart() - Returns the start time of this user session
	 * @return The start time of this session
	 */
	public Date getStart(){
		return this.start;
	}
	
	/**
	 * getLast() - Returns the last time that the user interacted with this session
	 * @return The last time that the user interacted with this session
	 */
	public Date getLast(){
		return this.last;
	}
	
	/**
	 * getTotal() - Returns the total number of interactions that the user has had with the bot during this session
	 * @return The total number of interactions that the user has had during this session
	 */
	public int getTotal(){
		return this.total;
	}
	
	/**
	 * getState() - Returns the current state of the conversation that the bot is having with the user
	 * @return The Conversation state
	 */
	public String getState(){
		return this.state;
	}
}


package com.petroldesigns.chatbot.hibernate;

/**
 * The User class is a POJO that is used by Hibernate to manage the Users of the PetrolBot
 * @author atrank
 */
public class User {
	
	private long id;
	private String username;
	private long activesession;
	private boolean optin;
	private boolean isbuddy;
	
	/**
	 * The default User constructor
	 */
	public User(){}
	
	/**
	 * User constructor for when the username is known at construction time
	 * @param uname - the AIM username of the user
	 */
	public User(String uname){
		this.username = uname;
		this.activesession = 0;
		this.optin = false;
		this.isbuddy = false;
	}
	
	//SETTERS
	/**
	 * setId() - Sets the id of the user that is used by the database as the Primary key
	 * @param id The id to set
	 */
	public void setId(long id){
		this.id = id;
	}
	
	/**
	 * setUsername() - Sets the name of the user for whom this object cooresponds
	 * @param uname - The name of the user
	 */
	public void setUsername(String uname){
		this.username = uname;
	}
	
	/**
	 * setActivesession() - Sets the ID of the session that is currently active for the user
	 * @param s - The session ID of the active session
	 */
	public void setActivesession(long s){
		this.activesession = s;
	}
	
	/**
	 * setOptin() - Sets the user as either opted in or not.   The precise meaning of what it means to 
	 * be opted in is specified elsewhere
	 * @param o - True of False (False by default)
	 */
	public void setOptin(boolean o){
		this.optin = o;
	}
	
	/**
	 * setIsbuddy() - Keeps tack of whether or not the user is in the bot's AIM buddy list
	 * @param i - True or false (false by default)
	 */
	public void setIsbuddy(boolean i){
		this.isbuddy = i;
	}
	
	//GETTERS
	/**
	 * Returns the ID of the User which is used by the database as the primary key for the record
	 * @return The Database key ID
	 */
	public long getId(){
		return this.id;
	}
	
	/**
	 * Returns the username cooresponding to this User object
	 * @return The username
	 */
	public String getUsername(){
		return this.username;
	}
	
	/**
	 * Returns the ID of the active session or 0 if there is no active session
	 * @return - the id of the active session or 0 if the user has no active session
	 */
	public long getActivesession(){
		return this.activesession;
	}
	
	/**
	 * Returns true if the user is "Opted in"
	 * @return true is "Opted in", false otherwise
	 */
	public boolean getOptin(){
		return this.optin;
	}
	
	/**
	 * Returns true if the user is in the AIM buddylist, and false otherwise
	 * @return true if the in the buddylist, false otherwise
	 */
	public boolean getIsbuddy(){
		return this.isbuddy;
	}
}

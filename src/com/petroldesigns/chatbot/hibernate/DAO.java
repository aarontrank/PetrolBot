package com.petroldesigns.chatbot.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;

/**
 * DAO - The data access object used for interacting with the User and Session objects that have been persisted via hibernate.
 * @author atrank
 *
 */
public class DAO {

	public static Logger logger = Logger.getLogger(DAO.class.getSimpleName());
	public static int MAXSTORE = 5000;
	private LinkedList<String> nameStore;
	private LinkedList<User> userStore;
	
	public DAO(){
		nameStore = new LinkedList<String>();
		userStore = new LinkedList<User>();
		logger.setLevel(Level.INFO);
	}
	
	/**
	 * addUser() - adds a user specified by the given username to the database and returns the new User object.  If the user already exists then 
	 * it will return the user object of the existing user by the specified username
	 * @param username The username of the User to create an object for
	 * @return The User object of the new (or previously existing) User
	 */
	public User addUser(String username){
		if (username == null || username.trim().equals("")) return null;
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
			org.hibernate.Session session = HHelper.getSession();
			Query q = session.createQuery("from User u where u.username= :username");
			q.setString("username", username);
			List results = q.list();
		
			User user = null;
			if (results.size() == 1) {
				user = (User) results.get(0);
			}
		    if (user==null){
		    	user = new User(username);
		    	session.save(user);
		    }
		    tx.commit();
		    if (nameStore.size() == MAXSTORE) {
		    	nameStore.remove(0);
		    	userStore.remove(0);
		    }
		    nameStore.add(username);
		    userStore.add(user);
			return user;
	      } catch (HibernateException e) {
	    	  logger.error(this.getClass().toString() + ":addUser(" + username+ "):HibernateException:" + e);
	    	  tx.rollback();
	    	  return null;
	      }
	}
	
	/**
	 * getUser() - Returns the User object for the user specified by the given username from the database.   If the user doesn't exist then it will return null.
	 * @param username The username of the User to return the object of
	 * @return The User object if it exists, otherwise null
	 */
	public User getUser(String username){
		if (username == null || username.trim().equals("")) return null;
		try {
			int userIndex = nameStore.indexOf(username);
	        if (userIndex > -1) {
	        	User user = userStore.get(userIndex);
	        	return user;
	        } else {
				org.hibernate.Session session = HHelper.getSession();
				
				Query q = session.createQuery("from User u where u.username= :username");
		        q.setString("username", username);
		        List results = q.list();
		
		        User user = null;
		        if (results.size() == 1) {
		           user = (User) results.get(0);
		        } if (user != null) {
			        if (nameStore.size() == MAXSTORE) {
				    	nameStore.remove(0);
				    	userStore.remove(0);
				    }
				    nameStore.add(username);
				    userStore.add(user);
		        }
		        return user;
	        }
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":getUser(" + username+ "):HibernateException:" + e);
        	return null;
		}
	}

	/**
	 * removeUser() - Removes the User object specified by the given username from the database.  If the user doesn't exist then id does nothing.
	 * @param username THe username of the User to remove from the database
	 */
	public void removeUser(String username){
		if (username == null || username.trim().equals("")) return;
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
			org.hibernate.Session session = HHelper.getSession();
			Query q = session.createQuery("from User u where u.username= :username");
	        q.setString("username", username);
	        List results = q.list();
	
	        User user = null;
	        if (results.size() == 1) {
	           user = (User) results.get(0);
	           session.delete(user);
	        }
	        tx.commit();
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":delete(" + username+ "):HibernateException:" + e);
			tx.rollback();
		}
	}
	
	/**
	 * removeUser() - Removes the User object from the database.  If the user doesn't exist then id does nothing.
	 * @param user THe User object to remove from the database
	 */
	public void removeUser(User user){
		if (user == null) return;
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
			HHelper.getSession().delete(user);
			tx.commit();
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":delete(" + user.getUsername()+ "):HibernateException:" + e);
			tx.rollback();
		}
	}
	
	/**
	 * commitUser() - Commits any changes to a User object so that they are persisted into the database
	 * @param user The User object to commit
	 */
	public void commitUser(User user){
		if (user == null) return;
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
			HHelper.getSession().update(user);
			tx.commit();
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":commit(" + user.getUsername()+ "):HibernateException:" + e);
			tx.rollback();
		}
	}
	
	/**
	 * commitSession() - Commits any changes to a Session object so that they are persisted into the database
	 * @param s The Session object to commit
	 */
	public void commitSession(Session s){
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
			HHelper.getSession().update(s);
			tx.commit();
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":commitSession(" + s.getUsername()+ "):HibernateException:" + e);
			tx.rollback();
        	return;
		}
	}
	
	/**
	 * getUserSession() - Returns the current Session object for the specified User or null if the specified User does not have a current Session
	 * @param user The User for whom to return the current Session
	 * @return The current Session for the specified user, or null the user has no active Session
	 */
	public Session getUserSession(User user){
		if (user == null || user.getActivesession() == 0) return null;
		try {
			org.hibernate.Session session = HHelper.getSession();
			Query q = session.createQuery("from Session s where s.id= :id");
	        q.setLong("id", user.getActivesession());
	        List results = q.list();
	
	        Session s = null;
	        if (results.size() == 1) {
	           s = (Session) results.get(0);
	        }
	
	        return s;
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":getUserSession(" + user.getUsername()+ "):HibernateException:" + e);
        	return null;
		}
	}
	
	/**
	 * createUserSession() - Creates a new Session for the specified user.   If the user already has an active session, that session is forced to expire and a new session is started
	 * @param user The User for whom to create a new session
	 * @return The newly created Session for the specified User
	 */
	public Session createUserSession(User user){
		if (user == null) return null;
		if (user.getActivesession() != 0) expireUserSession(user);
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
	         Session s = new Session();
	         s.setUsername(user.getUsername());
	         Date start = new Date();
	         s.setStart(start);
	         s.setLast(start);
	         org.hibernate.Session session = HHelper.getSession();
	         session.save(s);
	         user.setActivesession(s.getId());
	         session.update(user);
	         tx.commit();
	         return s;
	      } catch (HibernateException e) {
	    	  logger.error(this.getClass().toString() + ":createUserSession(" + user.getUsername()+ "):HibernateException:" + e);
	    	  tx.rollback();
	         return null;
	      }
	}
	
	/**
	 * expireUserSession() - Expires the Session for the specified User, if the User has no active Session then nothing is done
	 * @param user The User for whom to expire the current Session
	 */
	public void expireUserSession(User user){
		if (user == null || user.getActivesession() == 0) return;
		user.setActivesession(0);
		Transaction tx = HHelper.getSession().beginTransaction();
		try {
			HHelper.getSession().update(user);
			tx.commit();
		} catch (HibernateException e) {
			logger.error(this.getClass().toString() + ":expireUserSession(" + user.getUsername()+ "):HibernateException:" + e);
			tx.rollback();
        	return;
		}
	}
}

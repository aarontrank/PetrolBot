package com.petroldesigns.chatbot.test;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import com.petroldesigns.chatbot.hibernate.HHelper;
import com.petroldesigns.chatbot.hibernate.Session;
import com.petroldesigns.chatbot.hibernate.User;
import com.petroldesigns.chatbot.hibernate.DAO;

public class TestHibernate {

	public static Logger logger = Logger.getLogger("Hibernate");
	
	   public static void main(String[] args) {
		   BasicConfigurator.configure();
		   logger.info("Starting the Test application");
		      Transaction tx = HHelper.getSession().beginTransaction();
		      TestHibernate tc3 = new TestHibernate();
		      try {
		            tc3.populateDatabase();
		            tx.commit();
		            System.out.println("Test data created and committed");
		      } catch (org.hibernate.HibernateException e) {
		    	  logger.error("HibernateException:" + e);
		    	  tx.rollback();
		      }
		      logger.info("Ending the Test application");
	   }

		   public void populateDatabase() {
			   User u = udao.addUser("user1");
			   Session us = udao.getUserSession(u);
			   if (us == null) {
				   us = udao.createUserSession(u);
			   }
			   us.setState("purple");
			   udao.commitSession(us);
			   u.setIsbuddy(true);
			   udao.commitUser(u);
		   }

		   private final DAO udao = new DAO();
}

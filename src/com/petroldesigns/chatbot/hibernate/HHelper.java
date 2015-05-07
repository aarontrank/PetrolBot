package com.petroldesigns.chatbot.hibernate;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.petroldesigns.chatbot.PetrolBotConfig;

/**
 * HHelper is a singleton Hibernate helper class that ensures that only a single Hibernate session is activated per bot and that manages the construction of the session object
 * @author atrank
 */
public class HHelper {
	private static final ThreadLocal session = new ThreadLocal();
	private static final ThreadLocal transaction = new ThreadLocal();   
	private static SessionFactory sessionFactory = new  Configuration().configure(new File(PetrolBotConfig.getInstance().getHibernatePath())).buildSessionFactory();
	
	/**
	 * Default constructor (Note: use getSession() from a static context instead)
	 */
	private HHelper() {}

	/**
	 * Constructs and returns a single the Hibernate session.
	 * @return The hibernate session singleton
	 */
	public static Session getSession() {
		Session session = (Session)HHelper.session.get();
		if ( sessionFactory == null || sessionFactory.isClosed() ) {
			HHelper.sessionFactory = new Configuration().configure(new File(PetrolBotConfig.getInstance().getHibernatePath())).buildSessionFactory();
			session = sessionFactory.openSession();
			HHelper.session.set(session);
		} else {
			if( session == null) {
				session = sessionFactory.openSession();
				HHelper.session.set(session);
			} else if (!session.isOpen()) {
				session = sessionFactory.openSession();
				HHelper.session.set(session);
			} else if (!session.isConnected()) {
				session = sessionFactory.openSession();
				HHelper.session.set(session);
			}
		}
		return session;
	}
}

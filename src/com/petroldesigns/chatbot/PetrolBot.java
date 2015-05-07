package com.petroldesigns.chatbot;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.aol.acc.AccAlert;
import com.aol.acc.AccAvManager;
import com.aol.acc.AccAvManagerProp;
import com.aol.acc.AccAvSession;
import com.aol.acc.AccAvStreamType;
import com.aol.acc.AccBartItem;
import com.aol.acc.AccBartItemProp;
import com.aol.acc.AccBuddyList;
import com.aol.acc.AccBuddyListProp;
import com.aol.acc.AccClientInfo;
import com.aol.acc.AccCustomSession;
import com.aol.acc.AccDirEntry;
import com.aol.acc.AccEvents;
import com.aol.acc.AccException;
import com.aol.acc.AccFileSharingItem;
import com.aol.acc.AccFileSharingSession;
import com.aol.acc.AccFileXfer;
import com.aol.acc.AccFileXferSession;
import com.aol.acc.AccGroup;
import com.aol.acc.AccGroupProp;
import com.aol.acc.AccIm;
import com.aol.acc.AccImInputState;
import com.aol.acc.AccImSession;
import com.aol.acc.AccInstance;
import com.aol.acc.AccInstanceProp;
import com.aol.acc.AccParticipant;
import com.aol.acc.AccParticipantProp;
import com.aol.acc.AccPermissions;
import com.aol.acc.AccPluginInfo;
import com.aol.acc.AccPluginInfoProp;
import com.aol.acc.AccPreferences;
import com.aol.acc.AccRateState;
import com.aol.acc.AccResult;
import com.aol.acc.AccSecondarySession;
import com.aol.acc.AccSecondarySessionState;
import com.aol.acc.AccSession;
import com.aol.acc.AccSessionProp;
import com.aol.acc.AccSessionState;
import com.aol.acc.AccStream;
import com.aol.acc.AccUser;
import com.aol.acc.AccUserProp;
import com.aol.acc.AccVariant;
import com.petroldesigns.chatbot.conversation.ConversationManager;
import com.petroldesigns.chatbot.conversation.Synonyms;
import com.petroldesigns.chatbot.hibernate.DAO;
import com.petroldesigns.chatbot.hibernate.HHelper;
import com.petroldesigns.chatbot.hibernate.Session;
import com.petroldesigns.chatbot.hibernate.User;

public class PetrolBot implements AccEvents {

	public static Logger logger = Logger.getLogger(PetrolBot.class.getSimpleName());

	private int SESSION_LENGTH = 1000 * 60 * 5; //5 minute max idle time for session length TODO: test this
	AccSession session;
	AccClientInfo info;
	
	PetrolBotConfig config;
	AccPreferences prefs;
	
	DAO dao;
	ConversationManager conversationManager;
	
	boolean running;
	
	/**
	 * Run the Bot, but only if the user specifies a configuration file
	 * @param args The arguments to the application
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			//Set up the logging 
			PropertyConfigurator.configure("log4j.cfg");
			new PetrolBot(args[0]);
		} else {
			System.out.print("Usage: java PetrolBot config-file.xml");
			System.exit(-1);
		}
	}
	
	/**
	 * Construct a PetrolBot object via a String path to a configuration file
	 * @param filepath The path to the configuration file
	 */
	public PetrolBot(String filepath) {
		this(PetrolBotConfig.getInstance().load(filepath));
	}
	
	/**
	 * Construct a PetrolBot object via a preexisting PetrolBotConfig object
	 * @param cfg the PetrolBotConfiguration object
	 */
	public PetrolBot(PetrolBotConfig cfg) {
		logger.setLevel(Level.INFO);
		logger.info("Loading the Hibernate configuration");
		dao = new DAO();
		this.config = cfg;
		logger.info("Loading the Synonyms");
		Synonyms.getInstance().load(config.getSynonymsPath());
		logger.info("Loading the Conversation Script");
		conversationManager = new ConversationManager(config.getConversationPath());
		logger.info("Starting the PetrolBot application");
		try {
			logger.info("Creating an AccSession");
			//create the main session 
			session = new AccSession();
			logger.info("AccSession Created");
			// add this object as the event listener
			session.setEventListener(this);
			// get the client info and set the key for the bot
			info = session.getClientInfo();
			info.setDescription(config.getKey());
			//set the bot identity
			session.setIdentity(config.getAOLUsername());
			//set the preferences 
			session.setPrefsHook(new PetrolBotPrefs());
			prefs = session.getPrefs();
			prefs.setValue("aimcc.im.chat.permissions.buddies", AccPermissions.RejectAll);
			prefs.setValue("aimcc.im.chat.permissions.nonBuddies", AccPermissions.RejectAll);
			prefs.setValue("aimcc.im.direct.permissions.buddies", AccPermissions.RejectAll);
			prefs.setValue("aimcc.im.direct.permissions.nonBuddies", AccPermissions.RejectAll);
			prefs.setValue("aimcc.im.standard.permissions.buddies", AccPermissions.AcceptAll);
			prefs.setValue("aimcc.im.standard.permissions.nonBuddies", AccPermissions.AcceptAll);
			//Set the server for testing purposes
			//prefs.setValue("aimcc.connect.host.address", "mdalogin01.oscar.aol.com");
			//sign the bot onto aim
			session.signOn(config.getAOLPassword());
			//Create the hibernate session now so that it isn't slow to interact with the first user
			HHelper.getSession();
			running = true;
			//While the bot is connected and running "pump" the messages
			while(running){
				try {
					AccSession.pump(50);
		    	} catch (Exception e) {
		    		logger.warn("Exception caught during pump:" + e);
		    	}
		   		try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					logger.warn("InterruptedException caught during sleep:" + e);
				}
		    }
		} catch (AccException a) {
			logger.error("AccExcpetion encountered:" + a);
		} finally {
			// set info, session, and prefs to null so that the AOL JNI libs will properly cleanup 
	        info = null;
	        session = null;
	        prefs = null;
	        
	        logger.info("Exiting the PetrolBot application");
	        // "force" garbage collection
	        System.gc();
	        System.runFinalization();
	        System.exit(0);
		}
	}
	
	private String getConversationText(Session usession, String message){
		String rt = Synonyms.getInstance().getSemanticRoot(message);
		if (usession.getTotal() == 0 ) {
			String response = conversationManager.getText(usession.getState());
			return response;
		} else if (!rt.equals("")) {
			if (!conversationManager.hasNextState(usession.getState())) return "";
			if (conversationManager.getNextState(usession.getState(),rt) == null) return "I'm sorry, I didn't understand you.  Can you please try to say it a different way.";
			usession.setState(conversationManager.getNextState(usession.getState(),rt));
			String response = conversationManager.getText(usession.getState());
			if (response.contains("${1}")) {
				response = response.replace("${1}", message);
			}
			return response;
		} else if (conversationManager.hasPatternMatch(usession.getState()) && !message.equals("")){
			usession.setState(conversationManager.getNextState(usession.getState(),"*"));
			String response = conversationManager.getText(usession.getState());
			if (response.contains("${1}")) {
				response = response.replace("${1}", message);
			}
			return response;
		} else {
			if (!conversationManager.hasNextState(usession.getState())) return "";
			return "I'm sorry, I didn't understand you.  Can you please try to say it a different way.";
		}
	}
	/**
	 * The OnImReceived event handler handles all incoming text messages from any user in the AIM system
	 */
	public void OnImReceived(AccSession session, AccImSession imSession, AccParticipant participant, AccIm im) {
		try {
			String msg = im.getConvertedText("text/plain").trim().toLowerCase();
			String uname = participant.getName();
			User user = dao.getUser(uname);
			Session usession;
			// User send cancel request so cancel the users session and end the chat
			if (msg.equals("cancel")) {
				if (user != null) {
					dao.expireUserSession(user);
				}
				return;
			}
			// User sends an opt out request
			if (msg.equals("opt out") || msg.equals("optout")) {
				if (user != null){
					user.setOptin(false);
					dao.commitUser(user);
					im.setText("You have been opted out of receiving unsolicited messages.   \n If you would like to receive information about updates or new offers send me an \"Opt In\" message");
					im.convertToMimeType("application/xhtml+xml");				
					imSession.sendIm(im);
				}
				return;
			}
			// User sends an opt in request
			if (msg.equals("opt in") || msg.equals("optin")) {
				if (user == null) {
					user = dao.addUser(uname);
				}
				user.setOptin(true);
				dao.commitUser(user);
				im.setText("Thanks! If there are any new offers we will let you know.");
				im.convertToMimeType("application/xhtml+xml");				
				imSession.sendIm(im);
				return;
			}
			// If this is the first user experience ever then add the user if possible
			if (user == null) {
				user = dao.addUser(uname);
				usession = dao.createUserSession(user);
				usession.setState(conversationManager.getStartState());
			} else {
				usession = dao.getUserSession(user);
				// If this is NOT the first user experience, but the user does not have an active session
				if (usession == null) {
					usession = dao.createUserSession(user);
					usession.setState(conversationManager.getStartState());
				} else if ((new Date()).getTime() > (usession.getLast().getTime() + SESSION_LENGTH)){
					dao.expireUserSession(user);
					usession = dao.createUserSession(user);
					usession.setState(conversationManager.getStartState());
				}
			}
			String response = getConversationText(usession,msg);
			if (!response.equals("")) {
				im.setText(response);
				im.convertToMimeType("application/xhtml+xml");				
				imSession.sendIm(im);
				usession.setTotal(usession.getTotal() + 1);
				usession.setLast(new Date());
				dao.commitSession(usession);
			} else {
				usession.setTotal(usession.getTotal() + 1);
				usession.setLast(new Date());
				dao.commitSession(usession);
				dao.expireUserSession(user);
			}
		} catch (AccException a) {
			logger.error(a);
		}
	}
	
	/**
	 * The OnStateChange event handler is called when the Bot goes offline.   When this occurs
	 * the bot will terminate
	 */
	public void OnStateChange(AccSession arg0, AccSessionState arg1, AccResult arg2) {
		logger.info("OnStateChange called:" + arg1 + "" + arg2);
		if(arg1 == AccSessionState.Offline){
			logger.info("Running set to false");
			running = false;
		}
	}
	
	//Unused Event Handlers
	public void BeforeImReceived(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {}
	public void BeforeImSend(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {}
	public void OnAlertReceived(AccSession arg0, AccAlert arg1) {}
	public void OnAudioLevelChange(AccSession arg0, AccAvSession arg1, String arg2, int arg3) {}
	public void OnAvManagerChange(AccSession arg0, AccAvManager arg1, AccAvManagerProp arg2, AccResult arg3) {}
	public void OnAvStreamStateChange(AccSession arg0, AccAvSession arg1, String arg2, AccAvStreamType arg3, AccSecondarySessionState arg4, AccResult arg5) {}
	public void OnBartItemRequestPropertyResult(AccSession arg0, AccBartItem arg1, AccBartItemProp arg2, int arg3, AccResult arg4,AccVariant arg5) {}
	public void OnBuddyAdded(AccSession arg0, AccGroup arg1, AccUser arg2, int arg3, AccResult arg4) {}
	public void OnBuddyListChange(AccSession arg0, AccBuddyList arg1, AccBuddyListProp arg2) {}
	public void OnBuddyMoved(AccSession arg0, AccUser arg1, AccGroup arg2, int arg3, AccGroup arg4, int arg5, AccResult arg6) {}
	public void OnBuddyRemoved(AccSession arg0, AccGroup arg1, AccUser arg2, AccResult arg3) {}
	public void OnChangesBegin(AccSession arg0) {}
	public void OnChangesEnd(AccSession arg0) {}
	public void OnConfirmAccountResult(AccSession arg0, int arg1, AccResult arg2) {}
	public void OnCustomDataReceived(AccSession arg0, AccCustomSession arg1, AccParticipant arg2, AccIm arg3) {}
	public void OnCustomSendResult(AccSession arg0, AccCustomSession arg1, AccParticipant arg2, AccIm arg3, AccResult arg4) {}
	public void OnDeleteStoredImsResult(AccSession arg0, int arg1, AccResult arg2) {}
	public void OnDeliverStoredImsResult(AccSession arg0, int arg1, AccResult arg2) {}
	public void OnEjectResult(AccSession arg0, AccSecondarySession arg1, String arg2, int arg3, AccResult arg4) {}
	public void OnEmbedDownloadComplete(AccSession arg0, AccImSession arg1, AccIm arg2) {}
	public void OnEmbedDownloadProgress(AccSession arg0, AccImSession arg1, AccIm arg2, String arg3, AccStream arg4) {}
	public void OnEmbedUploadComplete(AccSession arg0, AccImSession arg1, AccIm arg2) {}
	public void OnEmbedUploadProgress(AccSession arg0, AccImSession arg1, AccIm arg2, String arg3, AccStream arg4) {}
	public void OnFileSharingRequestListingResult(AccSession arg0, AccFileSharingSession arg1, AccFileSharingItem arg2, int arg3, AccResult arg4) {}
	public void OnFileSharingRequestXferResult(AccSession arg0, AccFileSharingSession arg1, AccFileXferSession arg2, int arg3, AccFileXfer arg4) {}
	public void OnFileXferCollision(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {}
	public void OnFileXferComplete(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2, AccResult arg3) {}
	public void OnFileXferProgress(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {}
	public void OnFileXferSessionComplete(AccSession arg0, AccFileXferSession arg1, AccResult arg2) {}
	public void OnGroupAdded(AccSession arg0, AccGroup arg1, int arg2, AccResult arg3) {}
	public void OnGroupChange(AccSession arg0, AccGroup arg1, AccGroupProp arg2) {}
	public void OnGroupMoved(AccSession arg0, AccGroup arg1, int arg2, int arg3, AccResult arg4) {}
	public void OnGroupRemoved(AccSession arg0, AccGroup arg1, AccResult arg2) {}
	public void OnIdleStateChange(AccSession arg0, int arg1) {}
	public void OnImSendResult(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3, AccResult arg4) {}
	public void OnImSent(AccSession arg0, AccImSession arg1, AccParticipant arg2, AccIm arg3) {}
	public void OnInputStateChange(AccSession arg0, AccImSession arg1, String arg2, AccImInputState arg3) {}
	public void OnInstanceChange(AccSession arg0, AccInstance arg1, AccInstance arg2, AccInstanceProp arg3) { }
	public void OnInviteResult(AccSession arg0, AccSecondarySession arg1, String arg2, int arg3, AccResult arg4) {}
	public void OnLocalImReceived(AccSession arg0, AccImSession arg1, AccIm arg2) {}
	public void OnLookupUsersResult(AccSession arg0, String[] arg1, int arg2, AccResult arg3, AccUser[] arg4) {}
	public void OnNewFileXfer(AccSession arg0, AccFileXferSession arg1, AccFileXfer arg2) {}
	public void OnNewSecondarySession(AccSession arg0, AccSecondarySession arg1, int arg2) {}
	public void OnParticipantChange(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2, AccParticipant arg3, AccParticipantProp arg4) {}
	public void OnParticipantJoined(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2) {}
	public void OnParticipantLeft(AccSession arg0, AccSecondarySession arg1, AccParticipant arg2, AccResult arg3, String arg4, String arg5) {}
	public void OnPluginChange(AccSession arg0, AccPluginInfo arg1, AccPluginInfoProp arg2) {}
	public void OnPreferenceChange(AccSession arg0, String arg1, AccResult arg2) {}
	public void OnPreferenceInvalid(AccSession arg0, String arg1, AccResult arg2) {}
	public void OnPreferenceResult(AccSession arg0, String arg1, int arg2, String arg3, AccResult arg4) {}
	public void OnRateLimitStateChange(AccSession arg0, AccImSession arg1, AccRateState arg2) {}
	public void OnReportUserResult(AccSession arg0, AccUser arg1, int arg2, AccResult arg3, int arg4, int arg5) {}
	public void OnRequestServiceResult(AccSession arg0, int arg1, AccResult arg2, String arg3, int arg4, byte[] arg5) {}
	public void OnRequestSummariesResult(AccSession arg0, int arg1, AccResult arg2, AccVariant arg3) {}
	public void OnSearchDirectoryResult(AccSession arg0, int arg1, AccResult arg2, AccDirEntry arg3) {}
	public void OnSecondarySessionChange(AccSession arg0, AccSecondarySession arg1, int arg2) {}
	public void OnSecondarySessionStateChange(AccSession arg0, AccSecondarySession arg1, AccSecondarySessionState arg2, AccResult arg3) {}
	public void OnSendInviteMailResult(AccSession arg0, int arg1, AccResult arg2) {}
	public void OnSessionChange(AccSession arg0, AccSessionProp arg1) {}
	public void OnSoundEffectReceived(AccSession arg0, AccAvSession arg1, String arg2, String arg3) {}
	public void OnUserChange(AccSession arg0, AccUser arg1, AccUser arg2, AccUserProp arg3, AccResult arg4) {}
	public void OnUserRequestPropertyResult(AccSession arg0, AccUser arg1, AccUserProp arg2, int arg3, AccResult arg4, AccVariant arg5) {}
}

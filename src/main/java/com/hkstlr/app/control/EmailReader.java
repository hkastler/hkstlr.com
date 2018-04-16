package com.hkstlr.app.control;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

import com.sun.mail.imap.IMAPFolder;


public class EmailReader {

    Properties props = new Properties();
    Session session;
    Store store;
    Folder blogBox;

    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final static String EMAIL_PROTOCOL = "imaps";

    public EmailReader() {
        // no-arg constructor
    }

    public EmailReader(Properties props) {
        super();
        this.props = props;
        init();   	
    }
    
    @PostConstruct
    void init() {
    	    	
    	storeConnect();
    	
        try {
			this.blogBox = store.getFolder(props.getProperty(EmailReaderPropertyKey.FOLDER_NAME));
			this.blogBox.open(Folder.READ_ONLY);
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "error", e);
		}
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public int getMessageCount() {
        int msgCount = 0;
        if (!store.isConnected()) {
            init();
        }
        try {
            
            if (!blogBox.isOpen()) {
                blogBox.open(Folder.READ_ONLY);
            }

            msgCount = blogBox.getMessageCount();

        } catch (MessagingException e) {
            log.log(Level.WARNING, "", e);
        }
        return msgCount;
    }

    public Message[] getImapEmails() {
        
    	try {
            if ( !this.store.isConnected() ) {
            	init();
            }
            if(null == blogBox) {
            	this.blogBox = store.getFolder(props.getProperty(EmailReaderPropertyKey.FOLDER_NAME));
            }
            
            if (!this.blogBox.isOpen()) {
                this.blogBox.open(Folder.READ_ONLY);
                
            }

            try {
            	//int rangeStart = Math.max(1, getMessageCount());
                Message msgs[] = blogBox.getMessages(1,getMessageCount());
                FetchProfile fp = new FetchProfile();
                fp.add(IMAPFolder.FetchProfileItem.MESSAGE);            
                
                blogBox.fetch(msgs, fp);
                return msgs;

            } catch (MessagingException e) {
                log.log(Level.WARNING, "", e);
            }

        } catch (MessagingException e) {
            log.log(Level.WARNING, "", e);

        }
        return null;

    }
    
    public void storeConnect(){
    	this.session = Session.getDefaultInstance(this.props, null);
    	try {
			this.store = session.getStore(props.getProperty("mail.store.protocol", EMAIL_PROTOCOL));
		} catch (NoSuchProviderException e) {
			log.log(Level.SEVERE,"error",e);
		}
        try {
            store.connect(
            		props.getProperty(EmailReaderPropertyKey.MAIL_IMAP_HOST,"hostname"),
                    props.getProperty(EmailReaderPropertyKey.USERNAME,"username"),
                    props.getProperty(EmailReaderPropertyKey.PASSWORD,"password")
                    );
            
        } catch (MessagingException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    public void storeClose() {
    	
        try {
            this.store.close();
        } catch (MessagingException e) {
            log.log(Level.WARNING, "", e);
        }
    }

    static public void showUnreadMails(Folder folder) {
        try {

            // (3) create a search term for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, true);

            // (4) create a search term for all recent messages
            Flags recent = new Flags(Flags.Flag.RECENT);
            FlagTerm recentFlagTerm = new FlagTerm(recent, false);

            // (5) combine the search terms with a JavaMail AndTerm:
            // http://java.sun.com/developer/onlineTraining/JavaMail/contents.html#JavaMailFetching
            SearchTerm searchTerm = new AndTerm(unseenFlagTerm, recentFlagTerm);

            Message msgs[] = folder.search(searchTerm);
            FetchProfile fp = new FetchProfile();
            fp.add(FetchProfile.Item.ENVELOPE);
           

            folder.fetch(msgs, fp);
            // inbox.search(arg0, arg1)
            System.out.println("MAILS: " + msgs.length);
            // int counter = 0;

            Arrays.asList(msgs).stream().forEach(message -> {

                try {
                    System.out.println("DATE: " + message.getSentDate().toString());
                    // System.out.println("SUBJECT: "+message.getSubject().toString());
                    System.out.println("CONTENT TYPE: " + message.getContentType().toString());

                    System.out.println("CONTENT: " + message.getContent().getClass().getName().toString());

                    // Class<?> clazz = Class.forName(message.getContent().getClass().getName());
                    Multipart multipart = (Multipart) message.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        System.out.println("MULTIPART: " + multipart.getBodyPart(i).getContent().toString());
                    }
                } catch (MessagingException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // System.out.println("CONTENT: "+message.getContent().toString());
                System.out.println("HEADERS: ");
                @SuppressWarnings("unchecked")
                Enumeration<Header> allHeaders = null;
                try {
                    allHeaders = message.getAllHeaders();
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                StringBuilder hdrs = new StringBuilder();
                Collections.list(allHeaders).stream()
                        .forEach(h -> hdrs.append((h.getName().toString() + ": " + h.getValue().toString() + "\n")));
                // System.out.println(hdrs.toString());
                System.out.println("******************************************");
            });

        } catch (MessagingException e) {
            System.out.println(e.toString());
        }
    }
    public class EmailReaderPropertyKey{
    	
    	public final static String FOLDER_NAME = "folderName";
    	public final static String MAIL_IMAP_HOST = "mail.imap.host";
    	public final static String USERNAME = "username";
    	public final static String PASSWORD = "password";
    	
    	private EmailReaderPropertyKey() {
    		//strings
    	}
    }

}

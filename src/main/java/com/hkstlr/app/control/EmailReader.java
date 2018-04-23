package com.hkstlr.app.control;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.sun.mail.imap.IMAPFolder;

public class EmailReader {

    Properties props = new Properties();
    Session session;
    Store store;
    Folder blogBox;
    String mailhost;
    String username;
    String password;

    private final Logger log = Logger.getLogger(this.getClass().getName());
    public final static String EMAIL_PROTOCOL = "imaps";

    private EmailReader() {
        // no-arg constructor
    }

    public EmailReader(Properties props) {
        super();
        this.props = props;
        init();
    }

    @PostConstruct
    void init() {
        this.mailhost = props.getProperty(EmailReaderPropertyKey.MAIL_IMAP_HOST, "hostname");
        this.username = props.getProperty(EmailReaderPropertyKey.USERNAME, "username");
        this.password = props.getProperty(EmailReaderPropertyKey.PASSWORD, "password");

        try {
            storeConnect();
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
            if (!this.store.isConnected()) {
                init();
            }
            if (null == blogBox) {
                this.blogBox = store.getFolder(props.getProperty(EmailReaderPropertyKey.FOLDER_NAME));
            }

            if (!this.blogBox.isOpen()) {
                this.blogBox.open(Folder.READ_ONLY);

            }

            try {
                //int rangeStart = Math.max(1, getMessageCount());
                Message msgs[] = blogBox.getMessages(1, getMessageCount());
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

    public void storeConnect() {
        this.session = Session.getDefaultInstance(this.props, null);
        try {
            this.store = session.getStore(props.getProperty("mail.store.protocol", EMAIL_PROTOCOL));
        } catch (NoSuchProviderException e) {
            log.log(Level.SEVERE, "error", e);
        }
        try {
            store.connect(this.mailhost, this.username, this.password);

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

    public class EmailReaderPropertyKey {

        public final static String FOLDER_NAME = "SENT";
        public final static String MAIL_IMAP_HOST = "mail.imap.host";
        public final static String USERNAME = "username";
        public final static String PASSWORD = "password";

        private EmailReaderPropertyKey() {
            //strings
        }
    }

}

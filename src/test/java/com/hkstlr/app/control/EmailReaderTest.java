package com.hkstlr.app.control;

import java.util.Arrays;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
public class EmailReaderTest {
    
    EmailReader cut;
    Config config;
    Session session;
    Store store;
    Folder folder;
    
    
    public EmailReaderTest() {
    }
    
  
    @Before
    public void setUp() {
        config = new Config();
        config.getProps().put(EmailReader.EmailReaderPropertyKey.MAIL_IMAP_HOST, "localhost");
        config.getProps().put("password", "p");
        config.getProps().put("username", "u");
        config.getProps().put("mail.store.protocol", "imap");
    }

    /**
     * Test of init method, of class EmailReader.
     */
    @Test
    public void testInit() throws MessagingException {
        
        cut = new EmailReader(config.getProps());
        Arrays.asList(cut.store.getPersonalNamespaces())
                .stream()
                .forEach(f -> System.out.println("name:" + f.getName()));
          
        assertEquals("INBOX",cut.blogBox.getName());
            
    }

    /**
     * Test of getMessageCount method, of class EmailReader.
     */
    @Test
    public void testGetMessageCount() {
        cut = new EmailReader(config.getProps());
        assertTrue(cut.blogBox.isOpen());
        assertEquals(0,cut.getMessageCount());
    }

    /**
     * Test of getImapEmails method, of class EmailReader.
     */
    @Test
    public void testGetImapEmails() {
       cut = new EmailReader(config.getProps());
       assertEquals(0,cut.getImapEmails().length); 
    }

    /**
     * Test of storeConnect method, of class EmailReader.
     */
    @Test
    public void testStoreConnect() {
       cut = new EmailReader(config.getProps());
       assertTrue(cut.store.isConnected()); 
    }

    /**
     * Test of storeClose method, of class EmailReader.
     */
    @Test
    public void testStoreClose() {
        cut = new EmailReader(config.getProps());        
       assertTrue(cut.store.isConnected());
       cut.storeClose();
       assertTrue(!cut.store.isConnected());
    }

    
}

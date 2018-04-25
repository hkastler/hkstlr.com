
package com.hkstlr.app.entities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageTest {
    
    BlogMessage cut;
    MimeMessage message; 
    
    public BlogMessageTest() {
    }
    
    
    @Before
    public void setUp() throws IOException, MessagingException {
        Session session = Session.getDefaultInstance(System.getProperties(), null);;
        Path eml = Paths.get("src","test","resources","message.eml");
        
        try (InputStream is = Files.newInputStream(eml)) {
            message = new MimeMessage(session, is);
        }
        cut = new BlogMessage(message);
    }

    /**
     * Test of getMessageId method, of class BlogMessage.
     */
    @Test
    public void testGetMessageId() {
    }

    /**
     * Test of setMessageId method, of class BlogMessage.
     */
    @Test
    public void testSetMessageId() {
    }

    /**
     * Test of getMessageNumber method, of class BlogMessage.
     */
    @Test
    public void testGetMessageNumber() {
    }

    /**
     * Test of setMessageNumber method, of class BlogMessage.
     */
    @Test
    public void testSetMessageNumber() {
    }

    

    /**
     * Test of getSubject method, of class BlogMessage.
     */
    @Test
    public void testGetSubject() {
        assertEquals("blog img hello world",cut.getSubject());
    }

    /**
     * Test of setSubject method, of class BlogMessage.
     */
    @Test
    public void testSetSubject() {
    }

    /**
     * Test of getBody method, of class BlogMessage.
     */
    @Test
    public void testGetBody() {
    }

    /**
     * Test of setBody method, of class BlogMessage.
     */
    @Test
    public void testSetBody() {
    }

    /**
     * Test of getHref method, of class BlogMessage.
     */
    @Test
    public void testGetHref() {
    }

    /**
     * Test of setHref method, of class BlogMessage.
     */
    @Test
    public void testSetHref() {
    }

    /**
     * Test of getHeaders method, of class BlogMessage.
     */
    @Test
    public void testGetHeaders() {
    }

    /**
     * Test of setHeaders method, of class BlogMessage.
     */
    @Test
    public void testSetHeaders() {
    }
    
}

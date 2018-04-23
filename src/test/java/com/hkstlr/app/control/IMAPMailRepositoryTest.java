package com.hkstlr.app.control;

import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.MockFolder;

public class IMAPMailRepositoryTest {

    @Before
    public void setUp() throws Exception {
        final Session session = Session.getInstance(System.getProperties());
        MimeMessage msg = new MimeMessage(session);
        msg.setRecipients(Message.RecipientType.TO,"testuser@mockserver.com");
        msg.setSubject("Some Subject");
        msg.setText("sometext");
        Transport.send(msg);
        MockFolder mfolder;
    }
    
    @Test
    public void test() throws AddressException, MessagingException{
        List<Message> inbox = org.jvnet.mock_javamail.Mailbox.get("testuser@mockserver.com");
       
        assertFalse(inbox.isEmpty());

        Message message = inbox.get(0);
        assertEquals("Some Subject", message.getSubject());
    }
}

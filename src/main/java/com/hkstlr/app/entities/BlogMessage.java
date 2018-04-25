package com.hkstlr.app.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.validation.constraints.NotNull;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import com.hkstlr.app.control.DateFormatter;
import com.hkstlr.app.control.StringChanger;
import com.sun.mail.util.BASE64DecoderStream;

public class BlogMessage {

    private String messageId;
    private int messageNumber;
    private Date createDate;
    private String subject;
    private String body;
    private String href;
    private String headers;
    private static final Logger LOG = Logger.getLogger(BlogMessage.class.getName());

    public BlogMessage() {
        // no-arg
    }

    public BlogMessage(Date createDate, String subject, String body) {
        super();
        this.createDate = createDate;
        this.subject = subject;
        this.body = body;
        this.href = createHref(Integer.MAX_VALUE);
    }

    public BlogMessage(Message msg) throws MessagingException, IOException {
        super();
        this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0]).orElse(Double.toHexString(Math.random()));
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = msg.getSubject();
        this.body = processMultipart(msg);
        this.href = createHref(Integer.MAX_VALUE);
        this.headers = messageHeadersToKeyValue(msg);

    }

    public BlogMessage(Message msg, Integer numberOfWordsInUrl) throws MessagingException, IOException {
        super();
        this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0]).orElse(Double.toHexString(Math.random()));
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = msg.getSubject();
        this.body = processMultipart(msg);
        this.href = createHref(numberOfWordsInUrl);
        this.headers = messageHeadersToKeyValue(msg);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the messageNumber
     */
    public int getMessageNumber() {
        return messageNumber;
    }

    /**
     * @param messageNumber the messageNumber to set
     */
    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return the headers
     */
    public String getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(String headers) {
        this.headers = headers;
    }

    @SuppressWarnings("unchecked")
    private String messageHeadersToKeyValue(Message message) {

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
        return hdrs.toString();
    }

    private String processMultipart(Message msg) throws IOException, MessagingException {

        Multipart multipart = (Multipart) msg.getContent();
        StringBuilder content = new StringBuilder();
        BodyPart part;   
        Optional<BodyPart> textPart = Optional.empty();
        Optional<BodyPart> htmlPart = Optional.empty();
        Optional<String> imgStr = Optional.empty();
        
        for (int i = 0; i < multipart.getCount(); i++) {
        	       	
            part = multipart.getBodyPart(i);            

            if (part.getContentType().contains("text/plain")){            	
            	textPart = Optional.of(part); //(String) part.getContent();        
                
            }else if (part.getContentType().contains("text/html")){   
            	htmlPart = Optional.of(part);//processHtml((String) part.getContent());               

            }else if (part.getContentType().contains("multipart/alternative")){
            	
            	DataHandler mh = part.getDataHandler();            	
                MimeMultipart mm = (MimeMultipart) mh.getContent();
                
                for (int m = 0; m < mm.getCount(); m++) {                   
            	   BodyPart p = mm.getBodyPart(m);
            	   if(p.getContentType().contains("text/html")) {
            		   htmlPart = Optional.of(p);
            	   }
                }        
                
            }  
                            
            if (part.getContent() instanceof BASE64DecoderStream) {
            	
                DataHandler dh = part.getDataHandler();
                if(dh.getContentType().contains("image/")) {
                	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    dh.writeTo(baos);

                    byte[] attBytes = baos.toByteArray();
                    String imageString = Base64.getEncoder().encodeToString(attBytes);
                    baos.close();

                    String template = "<div><img src=\"data:{0};base64, {1} \" /></div>";
                    String imgTag = MessageFormat.format(template, new Object[]{dh.getContentType(), imageString});
                    imgStr = Optional.of(imgTag);
                }   
                
            }
        }
        
        if(htmlPart.isPresent()) {
        	content.append(processHtml((String) htmlPart.get().getContent()));
        }else if(textPart.isPresent()) {
        	content.append((String) textPart.get().getContent());
        }
        if(imgStr.isPresent()) {
        	content.append(imgStr.get());
        }
        

        return content.toString();
    }
    
    private String processHtml(String html) {
    	Document doc = Jsoup.parse(html);
        Element htmlBody = doc.body();

        Whitelist wl = Whitelist.relaxed();
        wl.addAttributes("div", "style");
        wl.addTags("font");
        wl.addAttributes("font", "size");
        wl.addAttributes("font", "color");

        String safe = Jsoup.clean(htmlBody.html(), wl);
        return StringUtil.normaliseWhitespace(safe);
    }

    /**
     * Create href for blog, based on title or text
     */
    private String createHref(@NotNull Integer numberOfWordsInUrl) {

        String TITLE_SEPARATOR = "-";
        // Use title (minus non-alphanumeric characters)
        StringBuilder base = new StringBuilder();
        if (!this.subject.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(this.subject, ' ').trim());
        }
        // If we still have no base, then try body (minus non-alphanumerics)
        if (base.length() == 0 && !this.body.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(this.body, ' ').trim());
        }

        if (base.length() > 0) {

            StringTokenizer toker = new StringTokenizer(base.toString());
            StringBuilder tmp = new StringBuilder();
            int count = 0;
            while (toker.hasMoreTokens() && count < numberOfWordsInUrl) {
                String s = toker.nextToken();
                s = s.toLowerCase();
                if (tmp.length() == 0) {
                    tmp.append(s);
                } else {

                    tmp.append(TITLE_SEPARATOR).append(s);
                }
                count++;
            }
            base = tmp;
        } // No title or text, so instead we will use the items date
        // in YYYYMMDD format as the base anchor
        else {

            base.append(new DateFormatter(this.createDate).format8chars());
        }

        return base.toString();
    }

}

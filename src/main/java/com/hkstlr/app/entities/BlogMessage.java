package com.hkstlr.app.entities;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.validation.constraints.NotNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import com.hkstlr.app.control.DateFormatter;
import com.hkstlr.app.control.StringChanger;

public class BlogMessage {

    private Date createDate;
    private String subject;
    private String body;
    private String href;    
    private String headers;

    public BlogMessage() {
        //no-arg
    }

    public BlogMessage(Date createDate, String subject, String body) {
        super();
        this.createDate = createDate;
        this.subject = subject;
        this.body = body;
    }

    public BlogMessage(Message msg) throws MessagingException, IOException {
        super();
        this.createDate = msg.getReceivedDate();
        this.subject = msg.getSubject();
        this.body = processMultipart(msg);
        this.href = createAnchorBase(Integer.MAX_VALUE);
        this.headers = messageHeadersToKeyValue(msg);
    }
    
    public BlogMessage(Message msg, Integer numberOfWordsInUrl) throws MessagingException, IOException {
        super();
        this.createDate = msg.getReceivedDate();
        this.subject = msg.getSubject();
        this.body = processMultipart(msg);
        this.href = createAnchorBase(numberOfWordsInUrl);
        this.headers = messageHeadersToKeyValue(msg);
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
        BodyPart clearTextPart = null;
        BodyPart htmlTextPart = null;
        String content = null;

        for (int i = 0; i < multipart.getCount(); i++) {

            BodyPart part = multipart.getBodyPart(i);

            if (part.isMimeType("text/plain")) {
                clearTextPart = part;

            } else if (part.isMimeType("text/html")) {
                htmlTextPart = part;
            }
        }
        if (htmlTextPart != null) {
            String html = (String) htmlTextPart.getContent();
            Document doc = Jsoup.parse(html);
            Element body = doc.body();

            Whitelist wl = Whitelist.relaxed();
            wl.addAttributes("div", "style");
            wl.addTags("font");
            wl.addAttributes("font", "size");
            wl.addAttributes("font", "color");
            String safe = Jsoup.clean(body.html(), wl);
            content = safe;

        } else if (clearTextPart != null) {
            content = (String) clearTextPart.getContent();
        }
        return content;
    }

    /**
     * Create anchor for blog, based on title or text
     */
    private String createAnchorBase(@NotNull Integer numberOfWordsInUrl) {

        String TITLE_SEPARATOR = "-"; 
        // Use title (minus non-alphanumeric characters)
        StringBuilder base = new StringBuilder();
        if (!this.subject.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(this.subject, ' ').trim());
        }
        // If we still have no base, then try text (minus non-alphanumerics)
        if (base.length() == 0 && !body.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(body, ' ').trim());
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

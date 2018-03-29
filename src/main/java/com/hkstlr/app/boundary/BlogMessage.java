package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class BlogMessage {

	private Date createDate;
	private String subject;
	private String body;
	
	
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
		Multipart multipart = (Multipart) msg.getContent();
		BodyPart clearTextPart = null;
        BodyPart htmlTextPart = null;
        //System.out.println("parts:" + multipart.getCount());
		for (int i = 0; i < multipart.getCount(); i++) {
			//System.out.println("MULTIPART: " + multipart.getBodyPart(i).getContent().toString());
			 BodyPart part =  multipart.getBodyPart(i);
			 //System.out.println("mime: " + part.getContentType());
             if(part.isMimeType("text/plain"))
             {
                 clearTextPart = part;
                 //break;
             }
             else if(part.isMimeType("text/html"))
             {
                 htmlTextPart = part;
             }
		}
		if (htmlTextPart!=null)
        {
            String html = (String) htmlTextPart.getContent();
            Document doc = Jsoup.parse(html);
            Element body = doc.body();
            //System.out.println("body:"+body.html());
            Whitelist wl = Whitelist.relaxed();
            wl.addAttributes("div", "style");
            wl.addTags("font");
            wl.addAttributes("font", "size");
            wl.addAttributes("font", "color");
            String safe = Jsoup.clean(body.html(),wl);

            this.body = safe;
        } else if(clearTextPart!=null)
        {
			this.body = (String) clearTextPart.getContent();
        }
         
		
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
	
	
	
}

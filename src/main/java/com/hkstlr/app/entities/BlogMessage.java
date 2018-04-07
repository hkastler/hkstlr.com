package com.hkstlr.app.entities;

import com.hkstlr.app.control.DateFormatter;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.validation.constraints.NotNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class BlogMessage {

    private Date createDate;
    private String subject;
    private String href;
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
        this.body = processMultipart(msg);
        this.href = createAnchorBase(Integer.MAX_VALUE);
    }
    
    public BlogMessage(Message msg, Integer numberOfWordsInUrl) throws MessagingException, IOException {
        super();
        this.createDate = msg.getReceivedDate();
        this.subject = msg.getSubject();
        this.body = processMultipart(msg);
        this.href = createAnchorBase(numberOfWordsInUrl);
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
            // Use only the first 7 words
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
            
            base.append(DateFormatter.format8chars.format(this.createDate));
        }

        return base.toString();
    }

    private static class StringChanger {

        public StringChanger() {
        }

        /**
         * Replaces occurrences of non-alphanumeric characters with a supplied
         * char.
         *
         * @param str
         * @param subst
         * @return
         */
        public static String replaceNonAlphanumeric(String str, char subst) {
            StringBuilder ret = new StringBuilder(str.length());
            char[] testChars = str.toCharArray();
            for (int i = 0; i < testChars.length; i++) {
                if (Character.isLetterOrDigit(testChars[i])) {
                    ret.append(testChars[i]);
                } else {
                    ret.append(subst);
                }
            }
            return ret.toString();
        }
    }

}

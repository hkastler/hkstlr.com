package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.hkstlr.app.control.Config;
import com.hkstlr.app.control.EmailReader;
import com.hkstlr.app.entities.BlogMessage;

@ApplicationScoped
@Named
@DependsOn(value = "config")
public class Index {

    private List<BlogMessage> msgs = new ArrayList<>();
    private Map<String,BlogMessage> msgMap = new LinkedHashMap<>();
    private static Logger log = Logger.getLogger(Index.class.getName());
    Setup setup;

    @Inject
    private Config config;

    public Index() {
        // no arg contructor
    }

    @PostConstruct
    void init() {

        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {
            log.log(Level.INFO, "fetching");
            fetchAndSetBlogMessages();
        }
    }

    public void fetchAndSetBlogMessages() {

        if (!config.isSetup()) {
            return;
        }

        EmailReader er = new EmailReader(config.getProps());

        for (Message msg : er.getImapEmails()) {
            try {
                BlogMessage bmsg = new BlogMessage(msg);
                msgs.add(bmsg);
                msgMap.put(bmsg.getHref(), bmsg);
            } catch (IOException | MessagingException e) {
                log.log(Level.WARNING, "", e);
            }
        }

        er.closeStore();

        log.log(Level.INFO, "sorting");
        Collections.sort(msgs, (BlogMessage o1, BlogMessage o2)
                -> o2.getCreateDate().compareTo(o1.getCreateDate()));
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<BlogMessage> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<BlogMessage> msgs) {
        this.msgs = msgs;
    }

    public Map<String, BlogMessage> getMsgMap() {
        return msgMap;
    }

    public void setMsgMap(Map<String, BlogMessage> msgMap) {
        this.msgMap = msgMap;
    }
    
    

    public Setup getSetup() {
        return setup;
    }

    public void setSetup(Setup setup) {
        this.setup = setup;
    }

    @Produces
    public String view() {

        String template = "view.xhtml";
        if (!config.isSetup()) {
            template = "setup/index.xhtml";
        }

        return template;
    }

}

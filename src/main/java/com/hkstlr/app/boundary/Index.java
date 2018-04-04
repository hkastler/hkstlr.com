package com.hkstlr.app.boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;

import com.hkstlr.app.control.Config;
import com.hkstlr.app.control.EmailReader;
import com.hkstlr.app.entities.BlogMessage;
import javax.ejb.DependsOn;

@ApplicationScoped
@Named
@DependsOn(value = "config")
public class Index {

    private List<BlogMessage> msgs = new ArrayList<>();
    private static Logger log = Logger.getLogger(Index.class.getName());
    Setup setup;
    
    @Inject
    private Config config;

    public Index() {
        // no arg contructor
    }

    @PostConstruct
    void init() {
    	
    	log.log(Level.INFO, "setup:" + config.isSetup());
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
                msgs.add(new BlogMessage(msg));
            } catch (Exception e) {
                log.log(Level.WARNING, "", e);
                continue;
            }
        }

        er.closeStore();

        log.log(Level.INFO, "sorting");
        Collections.sort(msgs, new Comparator<BlogMessage>() {
            public int compare(BlogMessage o1, BlogMessage o2) {
                return o2.getCreateDate().compareTo(o1.getCreateDate());
            }
        });
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

    public Setup getSetup() {
        return setup;
    }

    public void setSetup(Setup setup) {
        this.setup = setup;
    }
    
    @Produces
    public String view() {
    	
    	String template = "view.xhtml";
    	if(!config.isSetup()) {
    		template = "setup/index.xhtml";
    	}
    	
    	return template;
    }

}

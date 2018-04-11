package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.hkstlr.app.control.Config;
import com.hkstlr.app.control.DateFormatter;
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
   
    @Inject
    Event<String> event;
    
    

    public Index() {
        // no arg contructor
    }

    @PostConstruct
    void init() {

        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {
                    
            event.fire("fetch");
        }
    }

    @Asynchronous  
    public void fetchAndSetBlogMessages(){

        if (!config.isSetup()) {
            return;
        }
        
        log.log(Level.INFO, "fetching");    
		try {
			
			ArrayList<BlogMessage> fm = getBlogMessages().get();
			this.msgs.clear();
			this.msgs = fm;
		} catch (InterruptedException | ExecutionException e) {
			log.log(Level.SEVERE, "error",e);
		}
		this.msgMap.clear();
        this.msgs.forEach(bmsg ->
                this.msgMap.put(bmsg.getHref(), bmsg));
        
        
    }
    
    
    @Asynchronous 
    public Future<ArrayList<BlogMessage>> getBlogMessages() throws InterruptedException {
        CompletableFuture<ArrayList<BlogMessage>> completableFuture 
          = new CompletableFuture<>();
        ArrayList<BlogMessage> bmsgs = new ArrayList<>();
        
        EmailReader er = new EmailReader(config.getProps());
        er.storeConnect();
        
        for (Message msg : er.getImapEmails()) {
            try {
                BlogMessage bmsg = new BlogMessage(msg);
                bmsgs.add(bmsg);
                
            } catch (IOException | MessagingException e) {
                log.log(Level.WARNING, "", e);
            }
        }

        er.storeClose();
        
        Collections.sort(bmsgs, (BlogMessage o1, BlogMessage o2)
                -> o2.getCreateDate().compareTo(o1.getCreateDate()));
        
        completableFuture.complete(bmsgs);
        return completableFuture;
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
    
    @Produces
    public int listIndexByHref(String href) {
    	int index = 0;
    	
    	for ( Map.Entry<String,BlogMessage> e : this.msgMap.entrySet() ) {
    	    String key = e.getKey();
    	    //BlogMessage val = e.getValue();
    	    if(key.equals(href)) {
    	    	break;
    	    }
    	    index++;
    	}
    	return index;
    }
    

    public Setup getSetup() {
        return setup;
    }

    public void setSetup(Setup setup) {
        this.setup = setup;
    }
    
    
    public int min(int a, int b) {
    	return Math.min(a, b);
    }

    @Produces
    public String view() {

    	
        String template = "view.xhtml";
        if (!config.isSetup()) {
            template = "setup/index.xhtml";
        }

        return template;
    }
    
    public String jsFormat(Date date) {
    	
    	return new DateFormatter(date).formatjsFormat();
    }
    

    public void goFetch() {
    	 event.fire("fetch");
    }

}

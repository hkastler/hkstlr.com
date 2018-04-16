package com.hkstlr.app.boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.hkstlr.app.control.Config;
import com.hkstlr.app.control.DateFormatter;
import com.hkstlr.app.control.FetchEvent;
import com.hkstlr.app.control.IndexEvent;
import com.hkstlr.app.entities.BlogMessage;

@ApplicationScoped
@Named
@DependsOn(value = "config")
public class Index {
	
	private List<BlogMessage> msgs = new ArrayList<>();
    private Map<String,Integer> msgMap = new LinkedHashMap<>();
    private static Logger log = Logger.getLogger(Index.class.getName());
    Setup setup;

    @Inject
    private Config config;
   
    @Inject
    Event<FetchEvent> event;   
    
    @Inject
    Event<String> indexEvent;

    public Index() {
        // no arg contructor
    }

    @PostConstruct
    void init() {

        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {                    
            event.fire(new FetchEvent(this.getClass().getCanonicalName()
            		.concat(".init()")));
        }
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

    public Map<String, Integer> getMsgMap() {
        return msgMap;
    }

    public void setMsgMap(Map<String, Integer> msgMap) {
        this.msgMap = msgMap;
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
    
    public void setIndexMsgs(ArrayList<BlogMessage> fm) {
    	
    	Collections.sort(fm, (BlogMessage o1, BlogMessage o2)
                -> o2.getCreateDate().compareTo(o1.getCreateDate()));
    	getMsgs().clear();
		setMsgs(fm);
		getMsgMap().clear();
		AtomicInteger i = new AtomicInteger(0);
		getMsgs().forEach(bmsg ->
			getMsgMap().put(bmsg.getHref(), i.getAndIncrement()));
		
    }

    public void goFetch() {
    	event.fire(new FetchEvent(this.getClass().getCanonicalName()
    			.concat(".goFetch()")));
    }
    
    @Asynchronous
    public void processIndexEvent(@Observes IndexEvent event) {
    	
        log.log(Level.INFO, "{0} Event being processed by ".concat(this.getClass().getCanonicalName()), 
        		Thread.currentThread().getName());
        log.log(Level.INFO, "{0} Event ", event.toString());
        if("fetched".equals(event.getName())) {
        	
        	log.log(Level.INFO, "processIndexEvent Event ");
        	setIndexMsgs(event.getMsgs());
        }
 
    }

}

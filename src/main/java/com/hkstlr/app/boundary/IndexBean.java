package com.hkstlr.app.boundary;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.hkstlr.app.control.Config;
import com.hkstlr.app.control.DateFormatter;
import com.hkstlr.app.control.FetchEvent;
import com.hkstlr.app.control.Index;
import com.hkstlr.app.control.IndexEvent;
import com.hkstlr.app.entities.BlogMessage;

@RequestScoped
@Named("index")
public class IndexBean {
	
	
    private static Logger log = Logger.getLogger(IndexBean.class.getName());
        
    @Inject
    Index index;

    @Inject
    Config config;
   
    @Inject
    Event<String> indexEvent;

    public IndexBean() {
        // no arg contructor
    }


    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<BlogMessage> getMsgs() {
        return index.getMsgs();
    }

    public Map<String, Integer> getMsgMap() {
        return index.getMsgMap();
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
    
    @Asynchronous
    public void goFetch() {
    	index.getEvent().fire(new FetchEvent(this.getClass().getCanonicalName()
        		.concat(".init()")));
    }
   

    @Asynchronous
    public void processIndexEvent(@Observes IndexEvent event) {
    	
        log.log(Level.INFO, "{0} Event being processed by ".concat(this.getClass().getCanonicalName()), 
        		Thread.currentThread().getName());
        log.log(Level.INFO, "{0} Event ", event.toString());
        if("fetched".equals(event.getName())) {
        	
        	log.log(Level.INFO, "processIndexEvent Event ");
        	index.setIndexMsgs(event.getMsgs());
        }
 
    }

}

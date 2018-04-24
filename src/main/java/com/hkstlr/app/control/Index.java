package com.hkstlr.app.control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import com.hkstlr.app.entities.BlogMessage;

@ApplicationScoped
@DependsOn(value = "config")
@Startup
public class Index {
	
	private List<BlogMessage> msgs = new ArrayList<>();
    private Map<String,Integer> msgMap = new LinkedHashMap<>();
    
    private static Logger log = Logger.getLogger(Index.class.getName());
    
    @Inject
    Config config;
    
    @Inject
	private Event<FetchEvent> event;

    @PostConstruct
    void init() {

        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {                    
            getEvent().fire(new FetchEvent(this.getClass().getCanonicalName()
            		.concat(".init()")));
        }
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


	/**
	 * @return the event
	 */
	public Event<FetchEvent> getEvent() {
		return event;
	}


	/**
	 * @param event the event to set
	 */
	public void setEvent(Event<FetchEvent> event) {
		this.event = event;
	}
    
	 public void setIndexMsgs(ArrayList<BlogMessage> fm) {
	    	
	    	//Collections.sort(fm, (BlogMessage o1, BlogMessage o2)
	        //        -> o2.getCreateDate().compareTo(o1.getCreateDate()));
	    	List<BlogMessage> smsgs = fm.stream()
	    			.sorted(Comparator.comparing(BlogMessage::getMessageNumber).reversed())
	    			.collect(Collectors.toList());
	    	this.getMsgs().clear();
			this.setMsgs(smsgs);
			this.getMsgMap().clear();
			AtomicInteger i = new AtomicInteger(0);
		    this.getMsgs().forEach(bmsg ->
				getMsgMap().put(bmsg.getHref(), i.getAndIncrement()));
			
	    }
    

}



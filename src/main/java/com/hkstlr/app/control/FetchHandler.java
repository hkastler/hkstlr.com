package com.hkstlr.app.control;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.hkstlr.app.entities.BlogMessage;

@SuppressWarnings("serial")
@Stateless
public class FetchHandler implements Serializable {
 
    private static final Logger log = Logger.getLogger(FetchHandler.class.getCanonicalName());
    
    @Inject
    Config config;
     
    @Inject
    Event<IndexEvent> event;    
 
    @Asynchronous
    public void goFetch(@Observes FetchEvent event) {
 
        log.log(Level.INFO, "Thread {0} | Class {1}", 
        		new Object[] {Thread.currentThread().getName(),FetchHandler.class.getCanonicalName()});
        log.log(Level.INFO, "Event {0}", event.getEvent());
        if(config.isSetup()) {
        	fetchAndSetBlogMessages();
        }	
    }
    
    @AccessTimeout(value=60000)
    @Asynchronous  
    public void fetchAndSetBlogMessages(){
        
        log.log(Level.INFO, "fetching");    
        
		try {
			
			ArrayList<BlogMessage> fm = getBlogMessages().get();
			event.fire(new IndexEvent("fetched",fm));
			
		} catch (InterruptedException | ExecutionException e) {
			log.log(Level.SEVERE, "error",e);
		}
		
		
		log.log(Level.INFO, "fetched");
        
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
        
        completableFuture.complete(bmsgs);
        return completableFuture;
    }
}

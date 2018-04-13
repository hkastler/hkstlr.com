package com.hkstlr.app.control;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.hkstlr.app.boundary.Index;
import com.hkstlr.app.entities.BlogMessage;

@SuppressWarnings("serial")
@Stateless
public class FetchHandler implements Serializable {
 
    private static final Logger log = Logger.getLogger(FetchHandler.class.getCanonicalName());
 
    @Inject
    Index index;
    
 
    @Asynchronous
    public void goFetch(@Observes FetchEvent event) {
 
        log.log(Level.INFO, "{0} Event being processed by ".concat(FetchHandler.class.getCanonicalName()), Thread.currentThread().getName());
        log.log(Level.INFO, "{0} Event ", event.getEvent());
        fetchAndSetBlogMessages();
 
    }
    
    @AccessTimeout(value=60000)
    @Asynchronous  
    public void fetchAndSetBlogMessages(){

        if (!index.getConfig().isSetup()) {
            return;
        }
        
        log.log(Level.INFO, "fetching");    
        
		try {
			
			ArrayList<BlogMessage> fm = getBlogMessages().get();
			index.getMsgs().clear();
			index.setMsgs(fm);
			
		} catch (InterruptedException | ExecutionException e) {
			log.log(Level.SEVERE, "error",e);
		}
		index.getMsgMap().clear();
		AtomicInteger i = new AtomicInteger(0);
		index.getMsgs().forEach(bmsg ->
			{index.getMsgMap().put(bmsg.getHref(), i.getAndIncrement());});
		
		log.log(Level.INFO, "{0} fetched",i.getAndIncrement());
        
    }
    

    
    @Asynchronous 
    public Future<ArrayList<BlogMessage>> getBlogMessages() throws InterruptedException {
        CompletableFuture<ArrayList<BlogMessage>> completableFuture 
          = new CompletableFuture<>();
        ArrayList<BlogMessage> bmsgs = new ArrayList<>();
        
        EmailReader er = new EmailReader(index.getConfig().getProps());
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
}

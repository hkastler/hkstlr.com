package com.hkstlr.app.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 *
 * @author henry.kastler
 */
@Singleton
public class BlogMessageFetchScheduler {

    @Inject
    Event<FetchEvent> event;

    Logger log = Logger.getLogger(this.getClass().getName());

    @Schedule(second = "0", minute = "0", hour = "*/2", persistent = false)
    public void fetchMessages() {
        try {
            log.log(Level.INFO, "getting blog msgs");            
            event.fire(new FetchEvent(this.toString()));
            log.log(Level.INFO, "blog msgs retrieved");
            
        } catch (Exception ex) {
            log.log(Level.SEVERE, "error", ex);
        }
    }
    
    

}

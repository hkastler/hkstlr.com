package com.hkstlr.app.control;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.hkstlr.app.boundary.Index;

@SuppressWarnings("serial")
@Stateless
public class SetupHandler implements Serializable {
 
    private static final Logger logger = Logger.getLogger(SetupHandler.class.getCanonicalName());
 
    @Inject
    Index index;
    
 
    @Asynchronous
    public void onSetup(@Observes String event) {
 
        logger.log(Level.INFO, "{0} Event being processed by SetupHandler", Thread.currentThread().getName());
        //System.out.println(event);
        index.fetchAndSetBlogMessages();
 
    }
}

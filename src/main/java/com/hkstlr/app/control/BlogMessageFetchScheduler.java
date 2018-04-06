package com.hkstlr.app.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.hkstlr.app.boundary.Index;
import java.util.ArrayList;

/**
 *
 * @author henry.kastler
 */
@Singleton
public class BlogMessageFetchScheduler {

    @Inject
    Index index;

    @Inject
    Event<String> event;

    Logger log = Logger.getLogger(BlogMessageFetchScheduler.class.getName());

    @Schedule(second = "0", minute = "0", hour = "*/6", persistent = false)
    public void fetchMessages() {
        try {
            log.log(Level.INFO, "getting blog msgs");
            index.setMsgs(new ArrayList<>());
            index.fetchAndSetBlogMessages();
            log.log(Level.INFO, "blog msgs retrieved");
            event.fire("blog msgs");
        } catch (Exception ex) {
            log.log(Level.SEVERE, "error", ex);
        }
    }

}

package com.hkstlr.app.boundary;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hkstlr.app.control.EmailReader;
import com.hkstlr.app.control.Index;
import com.hkstlr.app.entities.BlogMessage;

@Path("/srvc")
public class BlogBoxService {

	@Inject
	Index index;
	    
    public BlogBoxService() { 
    	super();
    }
    
    @GET
    @Produces("application/json")
    @Path("/entry/{href}")
    public BlogMessage getHref(@PathParam("href") String href) {    	
        return index.getMsgs().get(index.getMsgMap().get(href));
    }
    
    @GET
    @Produces("application/json")
    @Path("/msgs")
    public List<BlogMessage> getMsgs() {    	
        return index.getMsgs();
    }
    
    @GET
    @Produces("application/json")
    @Path("/props")
    public Properties getProps() {
    	//new prop to remove password from view without removing it from app config
    	Properties rProps = new Properties();
    	rProps.putAll(index.getConfig().getProps());
    	rProps.remove(EmailReader.EmailReaderPropertyKey.PASSWORD);
        return rProps;
    }
}

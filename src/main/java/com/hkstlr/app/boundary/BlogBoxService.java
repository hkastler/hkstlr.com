package com.hkstlr.app.boundary;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hkstlr.app.entities.BlogMessage;

@Path("/service")
public class BlogBoxService {

	@Inject
	Index index;
    
    public BlogBoxService() { 
    	//constructor
    }
    
    @GET
    @Produces("application/json")
    @Path("/entry/{href}")
    public BlogMessage getEntryByHref(@PathParam("href") String href) {    	
        return index.getMsgs().get(index.getMsgMap().get(href));
    }
    
    @GET
    @Produces("application/json")
    @Path("/entries")
    public List<BlogMessage> getEntries() {    	
        return index.getMsgs();
    }
    
    @GET
    @Produces("application/json")
    @Path("/props")
    public Properties getProps() {
    	//new prop to remove password from view without removing it from app config
    	Properties rProps = new Properties();
    	rProps.putAll(index.getConfig().getProps());
    	rProps.remove("password");
        return rProps;
    }
}

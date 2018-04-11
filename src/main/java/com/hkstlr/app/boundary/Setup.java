package com.hkstlr.app.boundary;

import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import com.hkstlr.app.control.Config;
import com.hkstlr.app.entities.User;

@Model
public class Setup {

	private static final Logger logger = Logger.getLogger(Setup.class.getCanonicalName());
	
    private User user = new User();
    private String folderName;
    private String action = "create";

    @Inject
    Config config;

    @Inject
    Index index;
    
    @Inject
    private Event<String> event;
    
    

    public Setup() {
        // jee needed constructor
    }

    public void setup()  {
        config.getProps().put("password", this.user.getPassword());
        config.getProps().put("folderName", this.folderName);
        config.getProps().put("username", this.user.getUsername());
        
        event.fire("fetch");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext. getApplication()
                .getNavigationHandler().handleNavigation(facesContext, null, 
                        "index");
    }
    

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Produces
    public boolean isSetup() {
    	return config.isSetup();
    }

}

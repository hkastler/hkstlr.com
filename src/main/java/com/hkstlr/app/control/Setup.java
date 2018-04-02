package com.hkstlr.app.control;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.hkstlr.app.boundary.Index;

@Named
@RequestScoped
public class Setup {

    private User user;
    private String folderName;
    private String action;
    private Properties props;

    @Inject
    Index index;

    public Setup() {
        // jee needed constructor
    }

    public Setup(Properties props) {
        this.props = props;
    }

    @PostConstruct
    void init() {
        user = new User();
        action = "create";
        this.props = index.getProps();
        
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    @Produces
    public boolean isSetup() {
        try {
            return props.containsKey("username") && props.containsKey("password") && props.containsKey("folderName");
        } catch (Exception e) {
            return false;
        }
    }

    public void setup() {
        props.put("password", this.user.password);
        props.put("folderName", this.folderName);
        props.put("username", this.user.username);
        index.setProps(props);
        index.setSetup(this);
        index.fetchAndSetBlogMessages();
        //System.out.println(index.getMsgs().length);

    }

    public class User {

        private String username;
        private String password;

        public User() {

        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

}

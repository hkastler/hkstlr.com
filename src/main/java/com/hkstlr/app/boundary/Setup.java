package com.hkstlr.app.boundary;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;

import com.hkstlr.app.entities.User;


@Model
public class Setup {

	private User user = new User();
	private String folderName;
	private String action = "create";
	private Properties props = new Properties();


	public Setup() {
		// jee needed constructor
	}

	public Setup(Properties props) {
		this.props = props;
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
			return props.containsKey("username")
					&& props.containsKey("password") 
					&& props.containsKey("folderName");
		} catch (Exception e) {
			return false;
		}
	}

	public void setup() {
		props.put("password", this.user.getPassword());
		props.put("folderName", this.folderName);
		props.put("username", this.user.getUsername());
		setProps(props);
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

}

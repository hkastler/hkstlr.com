package com.hkstlr.app.boundary;

import com.hkstlr.app.control.Config;

import javax.enterprise.inject.Model;
import com.hkstlr.app.entities.User;
import javax.inject.Inject;

@Model
public class Setup {

	private User user = new User();
	private String folderName;
	private String action = "create";

	@Inject
	Config config;
	
	@Inject
	Index index;

	public Setup() {
		// jee needed constructor
	}

	

	public void setup() {
		config.getProps().put("password", this.user.getPassword());
		config.getProps().put("folderName", this.folderName);
		config.getProps().put("username", this.user.getUsername());
		index.fetchAndSetBlogMessages();
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

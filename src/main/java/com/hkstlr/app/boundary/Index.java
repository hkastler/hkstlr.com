package com.hkstlr.app.boundary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;

@ApplicationScoped
@Startup
@Named
public class Index {

	private Properties props;
		

	public Index() {
		//no arg contructor
	}
	
	@PostConstruct
	void init() {
		this.props = new Properties();
		props.setProperty("site.name", "hkstlr.com");
	}
	

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}
	
	
	
}

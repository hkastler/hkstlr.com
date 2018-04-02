package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.mail.Message;

import com.hkstlr.app.control.EmailReader;
import com.hkstlr.app.control.Setup;
import com.hkstlr.app.entities.BlogMessage;

@ApplicationScoped
@Startup
@Named
public class Index {

	private Properties props = new Properties();
	private BlogMessage[] msgs;
	private Setup setup;
		
	public Index() {
		//no arg contructor
	}
	
	@PostConstruct
	void init(){
				
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		setup = new Setup(props);
		if(setup.isSetup()) {
			fetchAndSetBlogMessages();
			//props.put("setup", 1);
		}
	}
	
	
	public void fetchAndSetBlogMessages() {
		
		if(!setup.isSetup()) {
			return;
		}
		
		EmailReader er = new EmailReader(props);
		int counter = 0;
		this.msgs = new BlogMessage[er.getMessageCount()];
		for(Message msg : er.getImapEmails()) {
			 
			try {
				BlogMessage blg = new BlogMessage(msg);
				msgs[counter] = blg;
				counter++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} 
			
		}
		er.closeStore();
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public BlogMessage[] getMsgs() {
		return msgs;
	}

	public void setMsgs(BlogMessage[] msgs) {
		this.msgs = msgs;
	}

	public Setup getSetup() {
		return setup;
	}

	public void setSetup(Setup setup) {
		this.setup = setup;
	}

	
	
	
}

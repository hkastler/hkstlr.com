package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;

@ApplicationScoped
@Startup
@Named
public class Index {

	private Properties props;
	private BlogMessage[] msgs;
		
	public Index() {
		//no arg contructor
	}
	
	@PostConstruct
	void init(){
		this.props = new Properties();
		
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();;
		}	
		Setup setup = new Setup(props);
		if(setup.isSetup()) {
			fetchAndSetBlogMessages();
		}
	}
	
	public void fetchAndSetBlogMessages() {
		EmailReader er = new EmailReader(props);
		int counter = 0;
		this.msgs = new BlogMessage[er.getMessageCount()];
		for(Message msg : er.getImapEmails()) {
			BlogMessage blg = new BlogMessage();
			try {
				blg = new BlogMessage(msg);
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

	
	
	
	
}

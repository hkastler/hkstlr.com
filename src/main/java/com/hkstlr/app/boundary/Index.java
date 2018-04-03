package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private List<BlogMessage> msgs = new ArrayList<>();
	private Setup setup;
	private static Logger log = Logger.getLogger(Index.class.getName());

	public Index() {
		// no arg contructor
	}

	@PostConstruct
	void init() {

		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));

		} catch (IOException e) {
			
			log.log(Level.SEVERE,null,e);
		}
		setup = new Setup(props);
		if (setup.isSetup()) {
			log.log(Level.INFO, "fetching");
			fetchAndSetBlogMessages();
		}
	}

	public void fetchAndSetBlogMessages() {

		if (!setup.isSetup()) {
			return;
		}

		EmailReader er = new EmailReader(props);

		for (Message msg : er.getImapEmails()) {
			try {
				msgs.add(new BlogMessage(msg));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
		
		er.closeStore();

		log.log(Level.INFO, "sorting");
		Collections.sort(msgs, new Comparator<BlogMessage>() {
			public int compare(BlogMessage o1, BlogMessage o2) {
				return o2.getCreateDate().compareTo(o1.getCreateDate());
			}
		});
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public List<BlogMessage> getMsgs() {
		return msgs;
	}

	public void setMsgs(List<BlogMessage> msgs) {
		this.msgs = msgs;
	}

	public Setup getSetup() {
		return setup;
	}

	public void setSetup(Setup setup) {
		this.setup = setup;
	}

}

package com.hkstlr.app.boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
	private List<BlogMessage> msgs = new ArrayList<>();
	private Setup setup;

	public Index() {
		// no arg contructor
	}

	@PostConstruct
	void init() {

		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setup = new Setup(props);
		if (setup.isSetup()) {
			fetchAndSetBlogMessages();
			// msgs.sort(Comparator.comparing(o -> o.getCreateDate()));
			// Collections.sort(msgs, Collections.reverseOrder());
			//System.out.println("sorting");
			Collections.sort(msgs, new Comparator<BlogMessage>() {
				public int compare(BlogMessage o1, BlogMessage o2) {
					return o1.getCreateDate().compareTo(o2.getCreateDate());
				}
			});
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

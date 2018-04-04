
package com.hkstlr.app.control;

import com.hkstlr.app.boundary.Setup;
import com.hkstlr.app.entities.User;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 *
 * @author henry.kastler
 */
@Startup
@ApplicationScoped
public class Config {
    
	private Properties props = new Properties();
        private Logger log = Logger.getLogger(this.getClass().getName());

	public Config() {
		// jee needed constructor
	}

	public Config(Properties props) {
		this.props = props;
	}
        
        @PostConstruct
	void init() {

		try {
                    props.load(this.getClass().getClassLoader().getResourceAsStream("app.properties"));

		} catch (IOException e) {
			
			log.log(Level.SEVERE,null,e);
		}
		
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
			return this.getProps().containsKey("username") && this.getProps().containsKey("password")
					&& this.getProps().containsKey("folderName");
		} catch (Exception e) {
			return false;
		}
	}

}

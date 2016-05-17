/*
 * Copyright 2016 Sebastian Schmidl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sas.systems.unveiled.server;

import java.util.Properties;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sas.systems.unveiled.server.util.PropertiesLoader;

/**
 * Application Lifecycle Listener implementation class SessionStarter
 * 
 * @author <a href="https://github.com/CodeLionX">CodeLionX</a>
 */
@WebListener
public class SessionStarter implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(SessionStarter.class);
	
	@EJB
	private SessionManager sm;
	
	private Properties properties;

    /**
     * Default constructor. 
     */
    public SessionStarter() {
    	// load properties from file containing host name, ports and the media location
    	this.properties = PropertiesLoader.loadPropertiesFile(PropertiesLoader.SESSIONS_PROPERTIES_FILE);
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  {
    	LOG.trace("Initializing session...");
    	// set resource location for videos etc
    	sm.setMediaLocation(properties.getProperty(PropertiesLoader.SessionProps.SYSTEM_PATH_TO_MEDIA));
    	boolean wasSuccessful = sm.initSessions(SessionManager.PAYLOAD_TYPE_H263);
    	
    	if(!wasSuccessful) {
    		LOG.error("Could not initialize RTP and RTSP sessions!");
    		throw new IllegalArgumentException("Configuration of RTP or RTSP session was incorrect, so it could not be started.");
    	}
    	
    	System.out.println("Sessions successfully created and initialized");
    }
    
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	if(sm != null) sm.terminateSession();
    }
	
}

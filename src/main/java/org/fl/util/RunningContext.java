/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.file.FilesUtils;
import org.fl.util.os.OperatingInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
# Properties for RunningContext 
#
runningContext.operatingInfo.log=true

 */

public class RunningContext {

	private final static String projectBuildPropertyFile = "project.properties" ;
	
	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS VV" ;
	
	private final String name;
	private final Logger pLog;
	private AdvancedProperties props;
	private PropertiesStorage propsStorage ;
	private LoggerManager logMgr ;
	private Instant initializationDate ;
	
	private JsonElement buildInformation ;
	
	/**
	 * @param name : normally named, using a hierarchical dot-separated namespace. 
	 * That name will be used to configure Logger, so it should normally be based on the application package name
	 * @param systemProperty : System property name containing the property file url
	 * @param defaultPropertyUri : Default property file url, if the system property containing the property file uri is null
	 *         The property file may denominated by :
     *  		- a well formed URI (for instance "http://my.server.org/myProps.properties" or "file:///my/dir/myProps.properties")
	 */
	public RunningContext(String name, String systemProperty, URI defaultPropertyUri) {
		
		this.name = name;
		pLog = Logger.getLogger(name);
		try {
			propsStorage = new PropertiesStorage(systemProperty, defaultPropertyUri);
			initRunningContext(name, systemProperty, null, null) ;
		} catch (Exception e) {
			pLog.log(Level.SEVERE, "Exception processing property file.  ", e);
			e.printStackTrace();
			props = new AdvancedProperties(pLog) ;
			
		}	
	}

	/**
	 * @param name : normally named, using a hierarchical dot-separated namespace. 
	 * That name will be used to configure Logger, so it should normally be based on the application package name
	 * @param systemProperty : System property name containing the property file url
	 * @param defaultPropertyPathName : Default property file path, if the system property containing the property file uri is null
	 *         The property file may denominated by :
	 *          - an absolute path (for instance on windows "C:/mydir1/mydir2/myProps.properties"
     *  		- a relative path ( for instance "mydir/myProps.properties"). 
     *    		  In this case, the file is searched in the user.dir (system property) first, then with the class loader.
	 */
	public RunningContext(String name, String systemProperty, String defaultPropertyPathName) {

		this.name = name;
		pLog = Logger.getLogger(name) ;
		try {
			Path defaultPropertyPath = Paths.get(defaultPropertyPathName) ;
			propsStorage = new PropertiesStorage(systemProperty, defaultPropertyPath);
			initRunningContext(name, systemProperty, null, null) ;
		} catch (Exception e) {
			pLog.log(Level.SEVERE, "Exception processing property file.  ", e);
			e.printStackTrace();
			props = new AdvancedProperties(pLog) ;

		}	
	}
	
	public String getName() {
		return name;
	}
	
	private void initRunningContext(String name, String systemProperty, String baseDir, Handler customLogHandler) {
		
		initializationDate = Instant.now();

		// get property file
		props = propsStorage.getAdvanced(pLog); 

		// Initialize logger
		logMgr = LoggerManager.builder()
				.logName(name)
				.rootDir(baseDir)
				.properties(props)
				.customHandler(customLogHandler)
				.build();

		// eventually get the property file generated by the build
		try {

			AdvancedProperties propsProject = findProjectProperties(name); 

			if (propsProject != null) {
				props.putAll(propsProject);

				Gson gson = new Gson() ;
				buildInformation = gson.toJsonTree(propsProject) ;
			} else {
				buildInformation = null ;
			}
		} catch (Exception e) {
			pLog.log(Level.WARNING, "No build information for " + name, e) ;
		}

		boolean logOperatingInfos = props.getBoolean("runningContext.operatingInfo.log", false) ;
		if (logOperatingInfos) {
			pLog.info(getOperatingInfos(true).toString()) ;
		}

	}
	
	private AdvancedProperties findProjectProperties(String name) {
		
		try {
			Path projectPropPath = Paths.get(name + "_" + projectBuildPropertyFile);
			PropertiesStorage propsProjectStorage = new PropertiesStorage(projectPropPath);
			AdvancedProperties propsProject = propsProjectStorage.getAdvanced(pLog);
			
			if (propsProject != null) {
				return propsProject;
			} else {
				
				pLog.info("Specific project property file not found: " + projectPropPath);
				projectPropPath = Paths.get(projectBuildPropertyFile);
				pLog.info("Project property file fallback: " + projectPropPath);
				propsProjectStorage = new PropertiesStorage(projectPropPath);
				propsProject = propsProjectStorage.getAdvanced(pLog);
				
				if (propsProject == null) {
					pLog.warning("No project properties (build information) found");
				}
				return propsProject;
			}
		} catch (Exception e) {
			pLog.log(Level.WARNING, "No build information for " + name, e);
			return null;
		}
	}

	public AdvancedProperties getProps() {
		return props;
	}
	
	public void addCustomLogHandler(Handler customLogHandler) {
		
		if (logMgr != null) {
			logMgr.addCustomHandler(customLogHandler);
		}
	}
	
	// Set the level of logger and log handlers
	public boolean setLogsLevel(JsonObject levelsJson) {
		
		if (logMgr != null) {
			return logMgr.setLogsLevels(levelsJson);
		} else {
			return false ;
		}
	}
	
	public JsonObject getLogsLevel() {
		
		JsonObject ret ;
		if (logMgr != null) {
			ret = logMgr.getLoggerLevels() ;
		} else {
			// No logger manager, return an empty JsonObject
			ret = new JsonObject() ;
		}
		return ret ;
	}
	
	public URL getPropertiesLocation() {
		return propsStorage.getPropertyLocation() ;
	}
	
	public StringBuilder getMemoryLogs() {
		return logMgr.getMemoryLogs() ;
	}
	
	public String deleteMemoryLogs() {
		return logMgr.deleteMemoryLogs() ;
	}

	public String deleteAndResizeMemoryLogs(int newsize) {
		return logMgr.deleteMemoryLogsAndResize(newsize) ;
	}
	
	public Instant getInitializationDate() {
		return initializationDate;
	}
	
	public String printInitializationDate() {
		return DateTimeFormatter.ofPattern(datePattern).format(ZonedDateTime.ofInstant(initializationDate, ZoneId.systemDefault()));
	}
	
	public JsonObject getOperatingInfos(boolean withIpLookup) {
		OperatingInfo operatingInfo = new OperatingInfo() ;
		return operatingInfo.getInfo(withIpLookup) ;
	}
	
	public JsonObject getApplicationInfo(boolean withIpLookup) {
		
		JsonObject applicationInfo = new JsonObject() ;
		String initDate = printInitializationDate() ;
		JsonObject operatingContext = getOperatingInfos(withIpLookup) ;
		
		URL propsLocation = getPropertiesLocation() ;
		if (propsLocation != null) {
			applicationInfo.addProperty("propertiesLocation", propsLocation.toString());
		} else {
			applicationInfo.addProperty("propertiesLocation", "No properties file location");
		}
		if (buildInformation != null) {
			applicationInfo.add("buildInformation", buildInformation);
		} else {
			applicationInfo.addProperty("buildInformation", "No build information") ;
		}
		applicationInfo.addProperty("initialisationDate", initDate);
		applicationInfo.add("applicationProperties", getPropertiesAsJson()) ;
		applicationInfo.add("operatingContext", operatingContext) ;
		applicationInfo.add("fileSystemsInformation", FilesUtils.getFileSystemsInformation(pLog)) ;
				
		return applicationInfo ;
	}
	
	public JsonArray getPropertiesAsJson() {
		
		Enumeration<Object> keys = props.keys() ;
		Vector<String> keyList = new Vector<String>();
		while (keys.hasMoreElements()) {
			keyList.add((String)keys.nextElement()) ;
		}
		Collections.sort(keyList);
		JsonArray res = new JsonArray() ;
		for (String key : keyList) {
			res.add(key + ":" + props.getProperty(key));
		}
		return res ;
	}

}

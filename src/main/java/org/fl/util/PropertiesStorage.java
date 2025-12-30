/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Frédéric Lefèvre
 *
 *	Get/Set a properties object from a remote URL or a file
 */
public class PropertiesStorage {

	private static final Logger PS_LOGGER = Logger.getLogger(PropertiesStorage.class.getName());
	
	private static final String USER_DIR_PROPERTY = "user.dir";
	
    // URL of storage
    private URL propUrl;
    
    // Advanced Properties
    private AdvancedProperties advancedProperties;
    
    private Logger psLogger;
    
 /**
     * Create a properties storage
     * 
 	 * @param systemProperty System property name containing the property file url
	 * @param propertyUri Property file url
	 *         The property file may denominated by :
     *  		- a relative URI ( for instance "mydir/myProps.properties"). 
     *    		  In this case, the file is searched in the user.dir (system property) or with classloader getResource
     *  		- a absolute URI (for instance "http://my.server.org/myProps.properties" or "file:///my/dir/myProps.properties")
     * @throws Exception if the URI or file cannot be opened
     */
	public PropertiesStorage(URI propertyUri) throws Exception {
		init(propertyUri, PS_LOGGER);
	}

	public PropertiesStorage(URI propertyUri, Logger logger) throws Exception {
		init(propertyUri, logger);
	}
	
	private void init(URI propertyUri, Logger logger) throws Exception {
		
		psLogger = logger;
		
		propUrl = null;
		try {
			// Get the URI of the properties			

			if (propertyUri.isAbsolute()) {
				
				propUrl = propertyUri.toURL();
			} else {

				String propPath = propertyUri.toString();
				propUrl = getUrlFromSystemProperty(USER_DIR_PROPERTY, propPath);

				if (propUrl == null) {
					// Still not found. Maybe inside the jar. Try class loader
					propUrl = PropertiesStorage.class.getClassLoader().getResource(propPath);
					
					// Check it is a regular file
					if ((propUrl != null) 		
						&& (! Files.isRegularFile(Paths.get(propUrl.toURI())))) {
						propUrl = null;		
					}
				}
			}
			
		} catch (Exception e) {
			// Trace file load error
			psLogger.log(Level.SEVERE, buildPropErrorMsg("Exception openning properties url", propertyUri), e);
			throw e;
		}
		
		// Finally get the advanced properties
		// load property from the property file		
		advancedProperties = new AdvancedProperties(null);

		if (propUrl != null) {
			
			try (InputStreamReader reader = new InputStreamReader(propUrl.openStream(), StandardCharsets.UTF_8)) {
				advancedProperties.load(reader);
			} catch (Exception e) {
				psLogger.log(Level.SEVERE, "Property file loading error for " + propUrl, e);
				// Invalid url
				advancedProperties = new AdvancedProperties(null);
			}
		} else {
			psLogger.severe("Property file has not been found. URI: \"" + Objects.toString(propertyUri) + "\"");
		}	
	
	}
	
	private URL getUrlFromSystemProperty(String systemProperty, String relativePath) {
		String directory = System.getProperty(systemProperty);
		if (directory != null) {
			Path propPath = Paths.get(directory);

			if (Files.exists(propPath)) {
				Path fullPath;
				if (Files.isDirectory(propPath)) {
					fullPath =  propPath.resolve(relativePath);
				} else {
					fullPath = propPath.getParent().resolve(relativePath);
				}
				if (Files.exists(fullPath) && Files.isRegularFile(fullPath)) {
					try {
						return fullPath.toUri().toURL();
					} catch (MalformedURLException e) {
						psLogger.log(Level.SEVERE, "MalformedURLException with systemProperty " + systemProperty + " and relative path " +  Objects.toString(relativePath), e);
					}
				} 
			}
		}
		return null;
	}
   
	private String buildPropErrorMsg(String msg, URI propertyUti) {

		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append(msg).append("\n");
		errorMsg.append("property uri: ").append(Objects.toString(propertyUti)).append("\n");
		errorMsg.append(USER_DIR_PROPERTY).append(": ").append(System.getProperty(USER_DIR_PROPERTY)).append("\n");
		return errorMsg.toString();
	}
	
	public AdvancedProperties getAdvancedProperties() {
		return advancedProperties;
	}
	
	public void save() throws IOException, URISyntaxException {
		save(propUrl.toURI());
	}
	
	/**
	 * Store the properties object in the storage
	 * @throws IOException if properties cannot be stored
	 * @throws URISyntaxException 
	 */
	public void save(URI propertyUri) throws IOException, URISyntaxException {

		if (propertyUri != null) {
			
			try (BufferedWriter propertyWriter = Files.newBufferedWriter(Paths.get(propertyUri), StandardCharsets.UTF_8)) {
				advancedProperties.store(propertyWriter, "");
			} catch (Exception e) {
				psLogger.log(Level.SEVERE, "Property file writing error for " + propertyUri, e);
			}
		}
	}
	
	public URL getPropertyLocation() {
		return propUrl;
	}

}

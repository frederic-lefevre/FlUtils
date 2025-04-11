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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.PropertiesStorage;

/**
 * @author Frédéric Lefèvre
 *
 *	Get/Set a properties object from a remote URL or a file
 */
public class PropertiesStorage {

	private static final Logger psLogger = Logger.getLogger(PropertiesStorage.class.getName());
	
	private static final String USER_DIR_PRPERTY = "user.dir";
	
    // URL of storage
    private URL propUrl;
    
    // Advanced Properties
    private AdvancedProperties advancedProperties;
    
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
	   
		propUrl = null;
		try {
			// Get the URI of the properties			

				if (propertyUri.isAbsolute()) {
					propUrl = propertyUri.toURL();
				} else {
					
					String propPath = propertyUri.toString();
					propUrl = getUrlFromSystemProperty(USER_DIR_PRPERTY, propPath);

					if (propUrl == null) {
						// Still not found. Maybe inside the jar. Try class loader
						propUrl = PropertiesStorage.class.getClassLoader().getResource(propPath);
					}
				}
			
		} catch (Exception e) {
			// Trace file load error
			psLogger.log(Level.SEVERE, buildPropErrorMsg("Exception openning properties url", propertyUri), e);
			throw e;
		}
		
		// Finally get the advanced properties
		// load property from the property file		
		advancedProperties = new AdvancedProperties(psLogger);

		if (propUrl != null) {
			try (InputStreamReader reader = new InputStreamReader(propUrl.openStream(), StandardCharsets.UTF_8)) {
				advancedProperties.load(reader);
			} catch (Exception e) {
				psLogger.log(Level.SEVERE, "Property file loading error for " + propUrl, e);
				// Invalid url
				propUrl = null;
			}
		} else {
			psLogger.warning("GetAdvanced properties while properties url is null");
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
				if (Files.exists(fullPath)) {
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
		errorMsg.append(USER_DIR_PRPERTY).append(": ").append(System.getProperty(USER_DIR_PRPERTY)).append("\n");
		return errorMsg.toString();
	}
	
	public AdvancedProperties getAdvancedProperties(Logger log) {
		return advancedProperties;
	}
	
	/**
	 * Store a properties object in the storage
	 * @param props properties to store
	 * @throws IOException if properties cannot be stored
	 */
	public void save(Properties props) throws IOException {

		if (propUrl != null) {
		    File outFile = new File(propUrl.getPath());
		    if ((outFile != null) && (!outFile.exists() || (outFile.canWrite() && outFile.delete()))) {
		        OutputStream outStream = getOutputFromUrl() ;
		        props.store(outStream, "");
		        outStream.flush();
		        outStream.close();
		    } else {
		        throw new IOException("Cannot write property file") ;
		    }
		}
	}
	
	
	/**
	 * Is this properties storage writable
	 * @return true if this properties storage writable, false otherwise
	 */
	public boolean isWritable() {
		return ((propUrl != null) &&
				propUrl.getProtocol().equals("file"));
	}
	
	/**
	 * Get an output file and Stream from an Url
	 * @return the outputStream 
	 * @throws FileNotFoundException
	 */
	private OutputStream getOutputFromUrl() throws FileNotFoundException {
	    
	    if (isWritable()) {
	       return new BufferedOutputStream(new FileOutputStream(new File(propUrl.getPath()))) ;
	    } else {
	        return null;
	    }
	}
	
	public URL getPropertyLocation() {
		return propUrl;
	}

}

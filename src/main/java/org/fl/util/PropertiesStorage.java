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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // URL of storage
    private URL propUrl;
    
 /**
     * Create a properties storage
     * 
 	 * @param systemProperty System property name containing the property file url
	 * @param defaultPropertyUrlName Default property file url, if the system property containing the property file uri is null
	 *         The property file may denominated by :
     *  		- a relative path ( for instance "mydir/myProps.properties"). 
     *    		  In this case, the file is searched in the user.dir (system property)
     *  		- a well formed URI (for instance "http://my.server.org/myProps.properties" or "file:///my/dir/myProps.properties")
     * @throws Exception if the URI or file cannot be opened
     */
	public PropertiesStorage(String systemProperty, URI defaultPropertyUri) throws Exception {
		initPropertiesStorage(systemProperty, defaultPropertyUri) ;
	}
    
	public PropertiesStorage(String systemProperty, Path defaultPropertyPath) throws Exception {
		initPropertiesStorage(systemProperty, defaultPropertyPath) ;
	}
	
    /**
     * Create a properties storage
     * 
		 * @param propertyUrlName Property file url
	 *         The property file may denominated by :
     *  		- a relative path ( for instance "mydir/myProps.properties"). 
     *    		  In this case, the file is searched in the user.dir (system property)
     *  		- a well formed URI (for instance "http://my.server.org/myProps.properties" or "file:///my/dir/myProps.properties")
     * @throws Exception if the URI or file cannot be opened
     */
   public PropertiesStorage(URI propertyUri) throws Exception {
	   initPropertiesStorage(null, propertyUri) ;		
	}
   
   public PropertiesStorage(Path propertyPath) throws Exception {
	   initPropertiesStorage(null, propertyPath) ;		
	}
   
   private void initPropertiesStorage(String systemProperty, URI defaultPropertyUri) throws Exception {
	   
		propUrl = null;
		try {
			// Get the URI of the properties
			URI propUri ;
			if (systemProperty != null) {
				String propUrlName = System.getProperty(systemProperty) ;
				if (propUrlName == null) {
					// if the url name is not found in the system property, take the default
					propUri = defaultPropertyUri ;
					System.out.println("System property " + systemProperty + " not found. Default config will be used instead: " + defaultPropertyUri) ;
				} else {
					propUri = new URI(propUrlName) ;
				}
			} else {
				// systemProperty is null, take defaultPropertyUrl
				propUri = defaultPropertyUri;
			}
			
			if (propUri != null) {	
				propUrl = propUri.toURL() ;	
			} else {
				System.out.println(buildPropErrorMsg("properties url is null", systemProperty, defaultPropertyUri)) ;
			}
			
		} catch (Exception e) {
			// Trace file load error
			System.out.println(buildPropErrorMsg("Exception openning properties url", systemProperty, defaultPropertyUri)) ;
			e.printStackTrace();
			throw e;
		}
   }
   
   private void initPropertiesStorage(String systemProperty, Path defaultPropertyPath) throws Exception {
	   
		propUrl = null;
		try {
			// Get the path of the properties
			Path propPath ;
			if (systemProperty != null) {
				String propPathName = System.getProperty(systemProperty) ;
				if (propPathName == null) {
					// if the path is not found in the system property, take the default
					propPath = defaultPropertyPath ;
					System.out.println("System property " + systemProperty + " not found. Default config will be used instead: " + defaultPropertyPath) ;
				} else {
					propPath = Paths.get(propPathName) ;
				}
			} else {
				// systemProperty is null, take defaultPropertyPath
				propPath = defaultPropertyPath;
			}
			
			if (propPath != null) {
	
				if (! propPath.isAbsolute()) {
					// It is a relative path
					// Assume it is relative to user.dir system property
					
					String userDir = System.getProperty("user.dir");
					if (userDir != null) {
						Path fullPath = Paths.get(userDir, propPath.toString()) ;
						if (Files.exists(fullPath)) {
							propUrl = fullPath.toUri().toURL() ;
						} 
					}
					if (propUrl == null) {
						// Still not found. Maybe inside the jar. Try class loader
						propUrl = PropertiesStorage.class.getClassLoader().getResource(propPath.toString());
					}
				} else {
					// path is absolute
					propUrl = propPath.toUri().toURL() ; ;
				}
			} else {
				System.out.println(buildPropErrorMsg("properties path is null", systemProperty, defaultPropertyPath)) ;
			}			
		} catch (Exception e) {
			// Trace file load error
			System.out.println(buildPropErrorMsg("Exception openning properties url", systemProperty, defaultPropertyPath)) ;
			e.printStackTrace();
			throw e;
		}
   }
   
   private String buildPropErrorMsg(String msg, String systemProperty, Object defaultProperty) {
	   
	   StringBuilder errorMsg = new StringBuilder() ;
	   errorMsg.append(msg).append("\n") ;
	   errorMsg.append( "System property: ").append(systemProperty).append("\n") ;
	   errorMsg.append( "defaultProperty: ").append(defaultProperty).append("\n") ;
	   errorMsg.append( "user.dir: ").append(System.getProperty("user.dir")).append("\n") ;
	   return errorMsg.toString() ;
   }
	
	public AdvancedProperties getAdvanced(Logger log) {
		
	    // load property from the property file		
	    AdvancedProperties props = new AdvancedProperties(log);
	    
	    if (propUrl != null) {
		    try (InputStreamReader reader = new InputStreamReader(propUrl.openStream(), StandardCharsets.UTF_8)) {
		        props.load(reader);
		    } catch (Exception e) {
		    	if (log != null) {
		    		log.log(Level.WARNING, "Properties not found " + propUrl, e) ;
		    	} else {
		    		 System.out.println("Property file loading error loading " + propUrl);
		    		 e.printStackTrace();
		    	}
		        props = null ;
		    }
	    } else {
	    	props = null ;
	    }
		return props ;
	}
	
	/**
	 * Store a properties object in the storage
	 * @param props properties to store
	 * @throws IOException if properties cannot be stored
	 */
	public void save(Properties props) throws IOException {

	    File outFile = new File(propUrl.getPath()) ;
	    if ((outFile != null) && (!outFile.exists() || (outFile.canWrite() && outFile.delete()))) {
	        OutputStream outStream = getOutputFromUrl() ;
	        props.store(outStream, "") ;
	        outStream.flush() ;
	        outStream.close() ;
	    } else {
	        throw new IOException("Cannot write property file") ;
	    }
	}
	
	
	/**
	 * Is this properties storage writable
	 * @return true if this properties storage writable, false otherwise
	 */
	public boolean isWritable() {
	    return (propUrl.getProtocol().equals("file")) ;
	}
	
	/**
	 * Get an output file and Stream from an Url
	 * @return the outputStream 
	 * @throws FileNotFoundException
	 */
	private OutputStream getOutputFromUrl() throws FileNotFoundException {
	    
	    if (propUrl.getProtocol().equals("file")) {
	       return new BufferedOutputStream(new FileOutputStream(new File(propUrl.getPath()))) ;
	    } else {
	        return null ;
	    }
	}
	
	public URL getPropertyLocation() {
		return propUrl;
	}

}

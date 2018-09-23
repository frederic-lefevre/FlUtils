package com.ibm.lge.fl.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Frédéric Lefèvre
 *
 *	Get/Set a properties object from a remote URL or a file
 */
public class PropertiesStorage {

    // URL of storage
    private URL propUrl;
    
    private String propertiesAsString ;
    
 /**
     * Create a properties storage
     * 
 	 * @param systemProperty System property name containing the property file url or file name
	 * @param defaultPropertyUrlName Default property file url or file name, if the system property containing the property file url is null
	 *         The property file may denominated by :
     *  		- its name, without any path ( for instance "myProps.properties"). 
     *    		  In this case, the file is searched in the class loader path
     *  		- a well formed URL (for instance "http://my.server.org/myProps.properties" or "file:///my/dir/myProps.properties")
     * @throws Exception if the URL or file cannot be opened
     */
    public PropertiesStorage(String systemProperty, String defaultPropertyUrlName) throws Exception {
        
        // Get the URL name of the properties
        String propUrlName ;
        if (systemProperty != null) {
            propUrlName = System.getProperty(systemProperty);
            if (propUrlName == null) {
                // if the url name is not found in the system property, take the default
                propUrlName = defaultPropertyUrlName ;
                System.out.println("System property " + systemProperty + " not found. Default config will be used instead: " + defaultPropertyUrlName);
            }
		} else {
		    propUrlName = defaultPropertyUrlName ;
		}
		
		try {			
			// try to find the URL locally
			propUrl = PropertiesStorage.class.getClassLoader().getResource(propUrlName);
			if (propUrl == null) {
			    // URL is not found locally. Assumed it is remote
			    propUrl = new URL(propUrlName) ;
			}
		
		} catch (Exception e) {
			// Trace file load error
			System.out.println("Property file URL open error: " + propUrlName);
			e.printStackTrace() ;
			propUrl = null ;
			throw e;
		}
	}
    
    /**
     * Create a properties storage
     * 
	 * @param propertyFile : the file is searched in the class loader path
     */
   public PropertiesStorage(String propertyFile) {
        		
		// try to find the URL locally
		propUrl = PropertiesStorage.class.getClassLoader().getResource(propertyFile);			
	}
   
	/**
	 * Get a AdvancedProperties object from the storage
	 * @return the AdvancedProperties object
	 */
	public AdvancedProperties getAdvanced() {
		return getAdvancedTry(null) ;
	}
	
	public AdvancedProperties getAdvancedTry(Logger log) {
		
	    // load property from the property file		
	    AdvancedProperties props = new AdvancedProperties();
	    
	    if (propUrl != null) {
		    try {
		        InputStream inStream = propUrl.openStream() ;
		        props.load(inStream) ;
		        propertiesAsString = getPropertiesFromStream(inStream) ;
		        inStream.close() ;
		    } catch (Exception e) {
		    	if (log != null) {
		    		log.warning("Properties not found " + propUrl.toString()) ;
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

	private String getPropertiesFromStream(InputStream is) throws Exception {
		
		StringBuilder propBuff = new StringBuilder() ;
		if (is != null) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1))) {
				
				String inputLine;						
				while ((inputLine = in.readLine()) != null) {
					
					propBuff.append(inputLine).append("\n") ;							
				}											
			} catch (Exception e1) {
				propBuff = null ;
				throw e1;
			}
		}
		return propBuff.toString() ;
	}

	public String getPropertiesAsString() {
		return propertiesAsString;
	}
	
	public AdvancedProperties changeProperties(String properties, Logger log) {
		
		propertiesAsString = properties ;
		
		 AdvancedProperties props = new AdvancedProperties();
		 
		 try {
			props.load(new StringReader(properties));
		} catch (IOException e) {
			log.log(Level.SEVERE, "IO Exception changing properties", e);
		}
		 
		 return props ;
	}
}

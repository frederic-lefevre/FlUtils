package com.ibm.lge.fl.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvancedProperties extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final char keySeparator = '.' ; 

	private Logger log  ;
	
	public AdvancedProperties() {
		super() ;
		log = Logger.getLogger("Default advanced property logger") ;
	}

	public AdvancedProperties(Properties properties) {
		super(properties);
		log = Logger.getLogger("Default advanced property logger") ;
	}

	public AdvancedProperties(Properties properties, Logger l) {
		super(properties);
		log = l ;
	}
	
	// Get all key elements following a root key
	// The root key includes the key element separator (so it ends with a separator)
	// The keys are sorted digits first sorted according to their numeric value 
	// and then letters sorted case sensitive (upper letter first)
	public ArrayList<String> getKeysElements(String rootKey) {
		
		ArrayList<String> res = new ArrayList<String>() ;
		Set<String> keys = stringPropertyNames() ;
		for (String ks : keys) {
			if (ks.startsWith(rootKey)) {
				String subKey = keyFirstElement(ks.substring(rootKey.length())) ;
				if ((subKey != null) && (! res.contains(subKey))) {
					res.add(subKey) ;
				}
			}
		}
		Collections.sort(res, Digits_Then_Letters) ;
		return res ;
	}
	
	private  Comparator<String> Digits_Then_Letters = new Comparator<String>() {
		 public int compare(String str1, String str2) {
			 
			    int i1 = -1 ;
			    int i2 = -1 ;
			    boolean str1IsNum = true ;
			    boolean str2IsNum = true ;
			    try {
			    	i1 = Integer.parseInt(str1) ;
			    } catch (Exception e) {
			    	str1IsNum = false ;
			    }
			    try {
			    	i2 = Integer.parseInt(str2) ;
			    } catch (Exception e) {
			    	str2IsNum =  false ;
			    }
			    
			    if (str1IsNum && str2IsNum) {
			    	if (i1 > i2) {
			    		return 1 ;
			    	} else if (i1 < i2) {
			    		return -1 ;
			    	} else {
			    		return str1.compareTo(str2) ;
			    	}			    	
			    } else if (str1IsNum) {
			    	return -1 ;
			    } else if (str2IsNum) {
			    	return 1 ;
			    } else {
			    	return str1.compareTo(str2) ;
			    }		        
		    }
	} ;
	
	public int getInt(String key, int defaultValue) {
		
		String pString =  getProperty(key) ;
		int res = defaultValue;
		if ((pString != null) && (pString.length() >0)) {
			try {
				res = Integer.parseInt(pString) ;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when parsing property (type int)" + key, e) ;
			}
		}
		return res ;
	}
	
	public long getLong(String key, long defaultValue) {
		
		String pString =  getProperty(key) ;
		long res = defaultValue;
		if ((pString != null) && (pString.length() >0)) {
			try {
				res = Long.parseLong(pString) ;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when parsing property (type long)" + key, e) ;
			}
		}
		return res ;
	}
	
	public double getDouble(String key, double defaultValue) {
		
		String pString =  getProperty(key) ;
		double res = defaultValue;
		if ((pString != null) && (pString.length() >0)) {
			try {
				res = Double.parseDouble(pString) ;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when parsing property (type double)" + key, e) ;
			}
		}
		return res ;
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		
		String pString =  getProperty(key) ;
		boolean res ;
		if ((pString != null) && (pString.length() >0)) {
			if (pString.equalsIgnoreCase("true")) {
				res = true ;
			} else {
				res =false ;
			}
		} else {
			res = defaultValue;
		}
		return res ;
	}
	
	public char getChar(String key, char defaultValue) {
		
		String prop =  getProperty(key) ;
		if ((prop != null) && (! prop.isEmpty())) {
			return prop.charAt(0) ;
		} else {
			return defaultValue ;
		}
	}
	
	public int[] getArrayOfInt(String key, String separator) {
		
		String prop =  getProperty(key) ;
		int[] result ;
		if ((prop != null) && (! prop.isEmpty())) {
			
			String[] intArrayStrin = prop.split(separator) ;
			try {
				result = parseIntArray(intArrayStrin) ;
			} catch (Exception e) {				
				result = null ;
				log.log(Level.SEVERE, "Exception when parsing a list of integer property: " + prop, e) ;
			}
		} else {
			result = null ;
		}
		return result ;
	}
	
	public String[] getArrayOfString(String key, String separator) {
		
		String prop =  getProperty(key) ;
		String[] result ;
		if ((prop != null) && (! prop.isEmpty())) {			
			result = prop.split(separator) ;
		} else {
			result = null ;
		}
		return result ;
	}
	
	public List<String> getListOfString(String key, String separator) {
		
		String prop =  getProperty(key) ;
		List<String> result;
		if ((prop != null) && (! prop.isEmpty())) {			
			result = Arrays.asList(prop.split(separator)) ;
		} else {
			result = null ;
		}
		return result ;
	}
	
	private int[] parseIntArray(String[] as) {

		int[] res ;
		if ((as != null) && (as.length > 0)) {
			res = new int[as.length];
			for (int i=0 ; i < as.length ; i++) {
				res[i] = Integer.parseInt(as[i]) ;
			}
		} else {
			return null ;
		}
		return res ;
	}
	
	// get the first key element
	private String keyFirstElement(String key) {
		
		if (key != null) { 
			int idx = key.indexOf(keySeparator) ;
			if (idx > -1) {
				return key.substring(0, idx) ;
			} else {
				return key ;
			}
		} else {
			return null ;
		}
	}

	public Path getPath(String key) {
		
		String pString =  getProperty(key) ;
		Path path = null ;
		if ((pString != null) && (pString.length() > 0)) {
			
			path = Paths.get(pString) ;
			if (! Files.exists(path)) {
				// if the path does not lead to a file, try to find it via the class loader
				try {
					URL url = AdvancedProperties.class.getClassLoader().getResource(pString) ;
					if (url != null) {
						path = Paths.get(url.toURI()) ;
					}
				} catch (URISyntaxException e) {
					log.log(Level.SEVERE, "Exception when getting file path " + pString + " (value of property " + key, e);
				}
			}
		} 
		return path ;
	}
	
	public Path getPathFromURI(String key) {
		
		String pString =  getProperty(key) ;
	
		if ((pString != null) && (pString.length() >0)) {
			return Paths.get(URI.create(pString)) ;
		} else {
			return null ;
		}
	}
	
	public String getFileContentFromURI(String key) {
		return getFileContentFromURI(key, Charset.defaultCharset()) ;
	}
	
	public String getFileContentFromURI(String key, Charset charset) {
		
		String pString =  getProperty(key) ;
		Path fPath ;
		if ((pString != null) && (pString.length() >0)) {
			 fPath = Paths.get(URI.create(pString)) ;
		} else {
			return "" ;
		}
		try {
			return new String(Files.readAllBytes(fPath), charset) ;
		} catch (IOException e) {
			log.log(Level.SEVERE, "IO Exception when reading file " + pString + " (value of property " + key, e);
			return "" ;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception when reading file " + pString + " (value of property " + key, e);
			return "" ;
		}
		
	}
	
	public String getFileContent(String key) {		
		return getFileContent(key, Charset.defaultCharset()) ;
	}
	
	public String getFileContent(String key, Charset charset) {
		
		String pString =  getProperty(key) ;
		String ret = "" ;
		if (pString != null) {
			try {
				Path f = Paths.get(pString) ;
				if (! Files.exists(f)) {
					// if the path does not lead to a file, try to find it via the class loader
					URL url = AdvancedProperties.class.getClassLoader().getResource(pString) ;
					if (url != null) {
						f = Paths.get(url.toURI()) ;
					}
				}
				ret = new String(Files.readAllBytes(f), charset) ;
			} catch (IOException e) {
				log.log(Level.SEVERE, "IO Exception when reading file " + pString + " (value of property " + key, e);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when reading file " + pString + " (value of property " + key, e);
			}
		} else {
			log.severe("getFileContent: Property " + pString + " does not exist");
		}
		return ret ;
	}
	
	public byte[] getFileBinaryContent(String key) {
		
		String pString =  getProperty(key) ;
		byte[] ret = {};
		if (pString != null) {
			try {
				Path f = Paths.get(pString) ;
				if (! Files.exists(f)) {
					// if the path does not lead to a file, try to find it via the class loader
					URL url = AdvancedProperties.class.getClassLoader().getResource(pString) ;
					if (url != null) {
						f = Paths.get(url.toURI()) ;
					}
				}
				ret = Files.readAllBytes(f) ;
			} catch (IOException e) {
				log.log(Level.SEVERE, "IO Exception when reading file " + pString + " (value of property " + key, e);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when reading file " + pString + " (value of property " + key, e);
			}
		} else {
			log.severe("getFileContent: Property " + pString + " does not exist");
		}
		return ret ;
	}
	
	public AdvancedProperties getPropertiesFromFile(String key) {
		
		String filePath =  getProperty(key) ;
		if (filePath != null) {
		
			PropertiesStorage propsProjectStorage = new PropertiesStorage(filePath);
			AdvancedProperties propsProject = propsProjectStorage.getAdvancedTry(log) ; 
			return propsProject ;
		
		} else { 
			return null ;
		}

	}
	
	/**
     * get the level 
     * @param level as a string
     * @return the level as an object of Level class
     */
    public Level getLevel(String key, Level defaultLevel) {
    	String level =  getProperty(key) ;
        if ((level == null) || (level.isEmpty())) {
            return defaultLevel;
        } else {
            try {
                return Level.parse(level) ;
            } catch (Exception e) {
                System.err.println("Invalid level (" + level + ") in config file: " + e);
                return defaultLevel;
            }
        }
    }
    
	public ArrayList<String> getArrayOfFileContent(String key, String separator) {
		return getArrayOfFileContent(key, separator,  Charset.defaultCharset()) ;
	}
	
	public ArrayList<String> getArrayOfFileContent(String key, String separator, Charset charset) {
		
		String prop =  getProperty(key) ;
		String[] fPaths ;
		ArrayList<String> result = new ArrayList<String>() ;
		if ((prop != null) && (! prop.isEmpty())) {	
			try {
				fPaths = prop.split(separator) ;
				for (String fPath : fPaths) {
					 result.add(new String(Files.readAllBytes(Paths.get(fPath)))) ;
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "IO Exception when reading a file in value of property " + key, e);
				return null ;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception when reading a file in value of property " + key, e);
				return null ;
			}
		} else {
			result = null ;
		}
		
		return result ;
	}
	
	public void setLog(Logger log) {
		this.log = log;
	}
	
	// Get all the properties as a string
	public String getPropertiesAsString() {
		StringWriter writer = new StringWriter();
	    list(new PrintWriter(writer));
		return writer.getBuffer().toString();
	}
}

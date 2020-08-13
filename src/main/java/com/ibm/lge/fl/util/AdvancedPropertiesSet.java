package com.ibm.lge.fl.util;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//
// Class to generate a list of AdvancedProperties from a set of files
//
public class AdvancedPropertiesSet {

	private List<AdvancedProperties> apList ;

	public AdvancedPropertiesSet(FileSet fs, Logger log) {
		super();
		
		apList = new ArrayList<>() ;
		if (fs != null) {
			
			List<Path> fpList = fs.getFileList() ;
			
			if (fpList != null) {
				
				try {
					for (Path p : fpList) {
						Properties prop = new Properties() ;
						prop.load(Files.newBufferedReader(p));
						apList.add(new AdvancedProperties(prop, log)) ;
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "IO Exception when loading properties files", e);					
				}
			}
		}
		
	}

	public List<AdvancedProperties> getApList() {
		return apList;
	}
	
}

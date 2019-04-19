package com.ibm.lge.fl.util.file;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileComparator {

	private Logger 	pLog ;
	private boolean onError ;
	
	public FileComparator(Logger l) {
		onError = false ;
		pLog  	= l ;	
	}

	// Compare the content of 2 files
	public boolean areTheSame(Path path1, Path path2) {

		boolean result = true ;
		onError = false ;

		if ((path1 != null) && (path2 != null)) {
			try {
				if (Files.size(path1) != Files.size(path2)) {
					result = false;
				} else {

					try (InputStream in1 = new BufferedInputStream(Files.newInputStream(path1)) ;
						 InputStream in2 = new BufferedInputStream(Files.newInputStream(path2)) ) {

						int value1 ;
						int value2 ;
						do{
							value1 = in1.read() ;
							value2 = in2.read() ;
							if(value1 != value2){
								result = false;
							}
						} while ((value1 >= 0) && (result)) ;
					} catch (Exception e) {
						onError = true ;
						result  = false ;
						pLog.log(Level.SEVERE, "Exception comparing 2 files content: " + path1.toString() + " and " + path2.toString(), e) ;					
					}
				}
			} catch (Exception e) {
				onError = true ;
				result  = false ;
				pLog.log(Level.SEVERE, "Exception comparing 2 files size: " + path1.toString() + " and " + path2.toString(), e) ;
			}
		} else {
			onError = true ;
			result  = false ;
			pLog.severe("Null parameter(s) when comparing 2 files") ;
		}
		return result ;
	}
	
	public boolean isOnError() {
		return onError ;
	}
}

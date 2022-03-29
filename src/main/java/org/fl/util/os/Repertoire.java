package org.fl.util.os;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repertoire {

	private Path repertoire ;
	private Logger rLog ;
	private boolean valid ;
	
	public Repertoire(String dirName, boolean createIfNonExistant, Logger l) {
		
		rLog = l ;
		try {
			repertoire = Paths.get(dirName) ;
			valid = true ;
			if ( createIfNonExistant && (! Files.exists(repertoire))) {
				try {
					Files.createDirectories(repertoire) ;
				} catch (IOException e1) {
					valid = false ;
					rLog.log(Level.SEVERE, "IOException when creating directories for path " + repertoire, e1) ;
				}
			}
			if (! Files.isDirectory(repertoire)) {
				rLog.severe(dirName + " is a valid path but is not a directory") ;
				valid = false ;
			}
		} catch (InvalidPathException e) {
			valid = false ;
			rLog.log(Level.SEVERE, "Invalid path definition for string :" + dirName, e) ;
		}	
	}

	public boolean isValid() {
		return valid;
	}

}

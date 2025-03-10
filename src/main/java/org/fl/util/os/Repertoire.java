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

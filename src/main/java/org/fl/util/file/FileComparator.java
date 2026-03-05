/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

package org.fl.util.file;

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
	// Returns true if the files have the same content
	// Returns false if there has been an error or the content is different
	public boolean haveSameContent(Path path1, Path path2) {

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

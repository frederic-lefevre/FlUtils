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

package org.fl.util.file ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConcatener {

	private Logger fcLog ;
	private long chunkSize ;
	
	public FileConcatener(Logger l) {
		super();
		fcLog = l ;

		chunkSize = 96000 ;

	}
	
	public FileConcatener(long cs, Logger l) {
		super();
		fcLog = l ;
		if (cs == 0) {
			chunkSize = 96000 ;
		} else {
			chunkSize = cs ;
		}
	}
	
	// Files concatenation using direct channel to channel copy
	public boolean concatenateFiles(Path destinationPath, ArrayList<Path> originPaths) {
		
		boolean success = true ;
		
		long tb = System.currentTimeMillis() ;
		File destinationFile = destinationPath.toFile() ;
		ArrayList<File> originFiles = new ArrayList<File>();
		
		// Create File object and compute expected lengths
		long expectedLength = destinationFile.length() ;
		for (Path originPath : originPaths) {
			File origin = originPath.toFile();
			expectedLength = expectedLength + origin.length() ;
			originFiles.add(origin) ;
		}
		
		
		try (FileOutputStream fOutputStream = new FileOutputStream(destinationFile, true); 
				FileChannel out = fOutputStream.getChannel()) {		

			for (File originFile : originFiles) {
				
				try (FileInputStream fInputStream = new FileInputStream(originFile); 
						FileChannel in = fInputStream.getChannel()) {
					
					long totalSizeToAppend = in.size() ;
					long sizeToAppend = totalSizeToAppend ;
					
					while (sizeToAppend > 0) {
					// apparently there is a limitation in the number of bytes that can be written at a time
					// around 2 GB at least on Windows with JVM 1.7
					// So it must be done in a loop
						
						long xferedLength ;
						if (sizeToAppend > chunkSize) {
							xferedLength = in.transferTo (totalSizeToAppend-sizeToAppend, chunkSize, out);
						} else {
							xferedLength = in.transferTo (totalSizeToAppend-sizeToAppend, sizeToAppend, out);
						}
						sizeToAppend = sizeToAppend - xferedLength ;
					}
				} catch (Exception e1) {
					success = false ;
					fcLog.log(Level.SEVERE, "Exception during file concatenation: ", e1) ;
				}
			}
		} catch (Exception e) {
			success = false ;
			fcLog.log(Level.SEVERE, "Exception during file concatenation: ", e) ;
		}
				
		if (destinationFile.length() != expectedLength) {
			StringBuffer buff = new StringBuffer() ;
			buff.append("Wrong size for concatenation to ").append(destinationPath) ;
			buff.append("\nof files: \n") ;
			for (Path originPath : originPaths) {
				buff.append(originPath).append('\n') ;
			}
			success = false ;
			fcLog.severe(buff.toString()) ;
		} else {
			// remove the source files
			for (Path sourceFile : originPaths) {
				try {
					boolean hasBeenDeleted = Files.deleteIfExists(sourceFile) ;
					if (! hasBeenDeleted) {
						success = false ;
						fcLog.severe("A file could not be deleted: " + sourceFile) ;
					}
				} catch (IOException e) {
					success = false ;
					fcLog.log(Level.SEVERE, "Exception during file delete for file " + sourceFile, e) ;
				}
			}
		}
			
		fcLog.fine(() -> { 
			long d = System.currentTimeMillis() - tb ;
			return "Concatenation duration for a file packet " +  + d;}) ;
		return success ;

	}
}

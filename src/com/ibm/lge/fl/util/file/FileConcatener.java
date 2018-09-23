package com.ibm.lge.fl.util.file ;

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
		
		
		try (FileChannel out = new FileOutputStream(destinationFile, true).getChannel()) {		

			for (File originFile : originFiles) {
				
				try (FileChannel in = new FileInputStream(originFile).getChannel()) {
					
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
		if (fcLog.isLoggable(Level.FINE)) {
			long d = System.currentTimeMillis() - tb ;
			fcLog.fine("Concatenation duration for a file packet " +  + d) ;
		}
		return success ;

	}
}

package com.ibm.lge.fl.util.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileExaminer {

	private Path   path ;
	private Logger fLog ;
	
	private FileChannel fileChannel ;
	private long fileSize ;
	
	public FileExaminer(Path p, Logger l) {
		
		fLog = l ;
		path = p ;
		
		if (path == null) {
			fLog.severe("Creating a FileExaminer with a null path") ;
		} else if (Files.exists(path)) {
		
			try {
				fileSize 	= Files.size(path) ;
				fileChannel = FileChannel.open(path, StandardOpenOption.READ) ;
			} catch (Exception e) {
				fLog.log(Level.SEVERE, "Exception creating FileChannel or getting size for " + path, e) ;
			}
		} else {
			fLog.severe("Creating a FileExaminer for a non existant file: " + path.toString()) ;
		}
	}

	public ByteBuffer getFileZoneContent(long position, int size) {
		
		ByteBuffer bb = ByteBuffer.allocate(size) ;
		int res ;
		try {
			res = fileChannel.read(bb, position) ;
		} catch (IOException e) {
			fLog.log(Level.SEVERE, "Exception reading FileChannel for " + path, e) ;
		}
		return bb ;
	}
}

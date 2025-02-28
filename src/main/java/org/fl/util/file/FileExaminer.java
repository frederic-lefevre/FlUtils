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

package org.fl.util.file;

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
		
		ByteBuffer bb ;
		if (position >= fileSize) {
			bb = null ;
			fLog.warning("getFileZoneContent called with a position=" + position + " outside the file=" + path + "( file length=" + fileSize + " )") ;
		} else {
			bb = ByteBuffer.allocate(size + 8) ;
			int res  = 0 ;
			int nbBytesRead = 0 ;
			try {
				while ((res != -1) && (nbBytesRead < size)) {
					res = fileChannel.read(bb, position) ;
					if (res != -1) {
						nbBytesRead++ ;
					}
				}
			} catch (Exception e) {
				long failOffset = position + nbBytesRead ;
				fLog.log(Level.SEVERE, "Exception reading FileChannel for " + path + " at file offset " + failOffset, e) ;
			}
			 // make buffer ready for read
			bb.flip() ;
		}
		return bb ;
	}
}

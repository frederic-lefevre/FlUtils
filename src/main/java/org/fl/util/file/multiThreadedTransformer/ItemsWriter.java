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

package org.fl.util.file.multiThreadedTransformer;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemsWriter extends Thread {

	// output queue containing items to be written in the file
	// Each item is seprated by a line feed
	private LinkedBlockingQueue<CharSequence> outputQ ;
	
	// File path where items are written
	private Path resultFilePath ;
	
	// Charset used to write in file
	private Charset charset ;
	
	private Logger logger ;
	
	private boolean endOfProcess ;
	
	private long nbElementWritten ;
	
	public ItemsWriter(LinkedBlockingQueue<CharSequence> oq, Path targetPath, Charset cs, Logger l) {
		
		outputQ 	   	 = oq ;
		resultFilePath 	 = targetPath ;
		charset		   	 = cs ;
		logger 		   	 = l ;
		endOfProcess   	 = false ;
		nbElementWritten = 0 ;
	}

	public void endProcess() {
		endOfProcess = true ;
	}
	
	@Override
	public void run() {

		CharSequence elem = null ;
		try ( BufferedWriter bw = Files.newBufferedWriter(resultFilePath, charset)) {
			
			do {
				// Get item from output queue waiting if necessary for one to become available
				elem = outputQ.poll( 10, TimeUnit.MILLISECONDS) ;
				if (elem != null) {
					bw.append(elem) ;
					bw.newLine();
					nbElementWritten++ ;
				}
			} while ((! endOfProcess) || (! outputQ.isEmpty())) ;
		
			bw.flush();
			bw.close();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception when writing target expertise file writing: " + elem, e) ;
			if (elem != null) {
				try {
					CharsetEncoder chEnc = charset.newEncoder() ;
					for (int i=0; i < elem.length(); i++) {
						if (! chEnc.canEncode(elem.charAt(i))) {
							logger.severe("Cannot encode char at position " + i + "\n char=" + elem.charAt(i));
						}
					}
				} catch (Exception e1) {
					logger.log(Level.SEVERE, "Exception in exception when trying to check if a string encodable", e1) ;
				}
			}
		}

	}

	public long getNbElementWritten() {
		return nbElementWritten;
	}
}

package com.ibm.lge.fl.util.file.multiThreadedTransformer;

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

package com.ibm.lge.fl.util.file.multiThreadedTransformer;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

public class ThreadedItemsProcess implements Callable<JsonObject>  {

	private LinkedBlockingQueue<ArrayList<String>> entries ;
	
	private LinkedBlockingQueue<CharSequence> outPutQ ;
	
	private Logger logger ;
	
	private ItemProcessor itemProcessor  ;
	
	public ThreadedItemsProcess(LinkedBlockingQueue<ArrayList<String>> iq, 
								LinkedBlockingQueue<CharSequence> 	   oq, 
								ItemProcessor						   it,
								Logger 								   l) {
		
		entries 	  = iq ;
		outPutQ 	  = oq ;
		itemProcessor = it ;
		logger		  = l ;
	}

	@Override
	public JsonObject call() throws Exception {

		// Loop
		long nbRecordProcessed	  = 0 ;
		ArrayList<String> currentEntry = null ;

		do {

			try {
				// Get item from input queue waiting if necessary for one to become available
				currentEntry = entries.take() ;

				// process item
				if (currentEntry.size() > 0) {
					
					// build out item
					StringBuilder outItem = itemProcessor.processItem(currentEntry) ;

					// put item in output queue
					outPutQ.put(outItem);

					nbRecordProcessed++ ;
				}

			} catch (Exception e) {
				String msg = null;
				if (currentEntry != null) {
					msg = currentEntry.get(0) ;
				}
				logger.log(Level.SEVERE, "Exception processing record=" + msg, e);
			}
		} while (currentEntry.size() > 0) ;

		JsonObject result = new JsonObject() ;
		result.addProperty("nbRecordsProcessed", nbRecordProcessed);
		return result ;
	}
}

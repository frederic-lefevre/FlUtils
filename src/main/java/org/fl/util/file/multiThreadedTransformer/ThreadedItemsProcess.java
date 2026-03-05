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

package org.fl.util.file.multiThreadedTransformer;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public class ThreadedItemsProcess implements Callable<ObjectNode>  {

	private final LinkedBlockingQueue<ArrayList<String>> entries;
	private final LinkedBlockingQueue<CharSequence> outPutQ;
	private final Logger logger;
	private final ItemProcessor itemProcessor;
	
	public ThreadedItemsProcess(LinkedBlockingQueue<ArrayList<String>> iq, LinkedBlockingQueue<CharSequence> oq,
			ItemProcessor it, Logger l) {

		entries = iq;
		outPutQ = oq;
		itemProcessor = it;
		logger = l;
	}

	@Override
	public ObjectNode call() throws Exception {

		// Loop
		long nbRecordProcessed = 0;
		ArrayList<String> currentEntry = null;

		do {

			try {
				// Get item from input queue waiting if necessary for one to become available
				currentEntry = entries.take();

				// process item
				if (currentEntry.size() > 0) {

					// build out item
					CharSequence outItem = itemProcessor.processItem(currentEntry);

					// put item in output queue
					outPutQ.put(outItem);

					nbRecordProcessed++;
				}

			} catch (Exception e) {
				String msg = null;
				if (currentEntry != null) {
					msg = currentEntry.get(0);
				}
				logger.log(Level.SEVERE, "Exception processing record=" + msg, e);
			}
		} while (currentEntry.size() > 0);

		return JsonNodeFactory.instance.objectNode().put("nbRecordsProcessed", nbRecordProcessed);
	}
}

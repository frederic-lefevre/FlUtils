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

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.json.JsonUtils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class ItemsExtractor {

	private static final int ENTRIES_Q_SIZE_DEFAULT = 100;
	private static final int OUTPUT_Q_SIZE_DEFAULT = 100;
	private static final int ELIMINATED_Q_SIZE_DEFAULT = 100;
	private static final int ATYPIC_Q_SIZE_DEFAULT = 100;
	private static final int NB_PROCESS_THREAD_DEFAULT = 10;
	private static final int NB_SUPPLEMENTARY_THREAD = 4;

	private int entriesQueueSize;
	private int outputQueueSize;
	private int eliminatedQueueSize;
	private int atypicQueueSize;

	private LinkedBlockingQueue<ArrayList<String>> entries;

	private LinkedBlockingQueue<CharSequence> outPutQ;

	protected LinkedBlockingQueue<CharSequence> eliminatedEntries;

	// entries without email or organization, last name....
	protected LinkedBlockingQueue<CharSequence> atypicEntries;

	private ExecutorService executorService;

	private Path inputFilePath;
	private Charset inputCharset;

	private Path outputFilePath;
	private Path atypicEntriesFilePath;
	private Path eliminatedEntriesFilePath;
	private Charset outputCharset;

	private int nbProcessThreads;

	protected Logger logger;

	public ItemsExtractor(Path inputFilePath, Charset inputCharset, Path outputFilePath, Charset outputCharset, Logger l) {

		init(inputFilePath, inputCharset, outputFilePath, outputCharset, null, null, l);
	}

	public ItemsExtractor(Path inputFilePath, Charset inputCharset, Path outputFilePath, Charset outputCharset, Path eliminatedEntriesFilePath, Path atypicEntriesFilePath, Logger l) {

		init(inputFilePath, inputCharset, outputFilePath, outputCharset, eliminatedEntriesFilePath, atypicEntriesFilePath, l);
	}

	private void init(Path ip, Charset ics, Path op, Charset ocs, Path ep, Path ap, Logger l) {

		inputFilePath = ip;
		inputCharset = ics;
		outputFilePath = op;
		outputCharset = ocs;
		atypicEntriesFilePath = ap;
		eliminatedEntriesFilePath = ep;
		logger = l;

		entriesQueueSize = ENTRIES_Q_SIZE_DEFAULT;
		outputQueueSize = OUTPUT_Q_SIZE_DEFAULT;
		eliminatedQueueSize = ELIMINATED_Q_SIZE_DEFAULT;
		atypicQueueSize = ATYPIC_Q_SIZE_DEFAULT;
		nbProcessThreads = NB_PROCESS_THREAD_DEFAULT;
	}
	
	public void extract(ItemProcessor itemProcessor) {

		// init queues
		entries = new LinkedBlockingQueue<ArrayList<String>>(entriesQueueSize);
		outPutQ = new LinkedBlockingQueue<CharSequence>(outputQueueSize);
		if (eliminatedEntriesFilePath != null) {
			eliminatedEntries = new LinkedBlockingQueue<CharSequence>(eliminatedQueueSize);
		} else {
			eliminatedEntries = null;
		}
		if (atypicEntriesFilePath != null) {
			atypicEntries = new LinkedBlockingQueue<CharSequence>(atypicQueueSize);
		} else {
			atypicEntries = null;
		}

		// Init executor service for multi threading
		executorService = Executors.newFixedThreadPool(nbProcessThreads + NB_SUPPLEMENTARY_THREAD);

		// Launch thread that writes result file
		ItemsWriter itemsWriter = new ItemsWriter(outPutQ, outputFilePath, outputCharset, logger);
		itemsWriter.start();

		// Launch thread that writes eliminated entries file
		ItemsWriter eliminatedWriter = null;
		if (eliminatedEntries != null) {
			eliminatedWriter = new ItemsWriter(eliminatedEntries, eliminatedEntriesFilePath, outputCharset, logger);
			eliminatedWriter.start();
		}

		// Launch thread that writes atypic entries file
		ItemsWriter atypicWriter = null;
		if (atypicEntries != null) {
			atypicWriter = new ItemsWriter(atypicEntries, atypicEntriesFilePath, outputCharset, logger);
			atypicWriter.start();
		}

		// launch entries processor threads
		ArrayList<Future<ObjectNode>> futureResponses = new ArrayList<Future<ObjectNode>>();
		for (int i = 0; i < nbProcessThreads; i++) {

			ItemProcessor ipClone = itemProcessor.getClone();
			ipClone.setAtypicEntries(atypicEntries);
			ThreadedItemsProcess processItemsThread = new ThreadedItemsProcess(entries, outPutQ, ipClone, logger);
			Future<ObjectNode> futureResp = executorService.submit(processItemsThread);
			futureResponses.add(futureResp);
		}

		String line = null;
		long nbElemRead = 0;
		long nbLine = 0;
		long nbElimi = 0;
		long now = System.currentTimeMillis();
		try (BufferedReader bf = Files.newBufferedReader(inputFilePath, inputCharset)) {

			ArrayList<String> currentEntry = null;

			// search first entry
			boolean firstNotReached = true;
			while (firstNotReached && ((line = bf.readLine()) != null)) {
				nbLine++;
				if (line != null) {
					if (isValidFirstLineEntry(line)) {
						firstNotReached = false;

						currentEntry = new ArrayList<String>();
						currentEntry.add(line);

					} else if (eliminatedEntries != null) {

						nbElimi++;
						eliminatedEntries.put(line);
					}
				}
			}

			// continue (algorithm is simpler like this for all subsequent inscription)
			while ((line = bf.readLine()) != null) {
				nbLine++;
				if (line != null) {

					if (belongsToCurrentEntry(currentEntry, line)) {
						// same entry as the current one : it is a secondary line
						currentEntry.add(line);
					} else {
						// different entry as the current one : it is a first line

						if (isValidFirstLineEntry(line)) {
							// is a valid line for an entry

							// put the previous inscription in the queue
							// this call waits for space to be available in the queue if necessary
							entries.put(currentEntry);
							nbElemRead++;

							currentEntry = new ArrayList<String>();
							currentEntry.add(line);

						} else if (eliminatedEntries != null) {
							// not a valid record
							nbElimi++;
							eliminatedEntries.put(line);
						}
					}

				}
			}

			// Put the last entry in the queue
			if (currentEntry != null) {
				entries.put(currentEntry);
				nbElemRead++;
			}

			// Signal the end of the process to the threads by putting an empty element in
			// the input queue
			int nbEndMark = nbProcessThreads;
			currentEntry = new ArrayList<String>();
			while (nbEndMark > 0) {
				entries.put(currentEntry);
				nbEndMark--;
			}

			// wait responses from expertise extractor threads
			long nbElemProcessed = 0;
			ObjectNode globalResult = JsonNodeFactory.instance.objectNode();
			ArrayNode threadsResults = JsonNodeFactory.instance.arrayNode();
			
			for (Future<ObjectNode> oneExtratorRes : futureResponses) {

				ObjectNode oneRes = oneExtratorRes.get();
				threadsResults.add(oneRes);
				nbElemProcessed = nbElemProcessed + oneRes.get("nbRecordsProcessed").asLong();
			}
			globalResult.set("nbRecordsProcessedByThreads", threadsResults);
			globalResult.put("nbRecordsProcessed", nbElemProcessed);

			// Wait item processor threads end
			terminateExecutor(executorService);

			// signal the end to the item writer and eliminated/atypic entries writer
			// threads
			if (eliminatedWriter != null) {
				eliminatedWriter.endProcess();
			}
			if (atypicWriter != null) {
				atypicWriter.endProcess();
			}
			itemsWriter.endProcess();

			// wait item writer and eliminated/atypic entries writer end
			while (itemsWriter.isAlive() || (!outPutQ.isEmpty())
					|| ((atypicWriter != null) && (atypicWriter.isAlive() || (!atypicEntries.isEmpty())))
					|| ((eliminatedWriter != null) && (eliminatedWriter.isAlive() || (!eliminatedEntries.isEmpty())))) {
				Thread.sleep(1000);
			}

			// Check number of elements processed. Log result eventually
			Level logLevel = null;
			if ((nbElemProcessed != itemsWriter.getNbElementWritten()) || (nbElemProcessed != nbElemRead)) {
				logLevel = Level.SEVERE;
			} else if (logger.isLoggable(Level.INFO)) {
				logLevel = Level.INFO;
			}
			if (logLevel != null) {

				globalResult.put("nbLinesRead", nbLine);
				globalResult.put("nbLinesEliminated", nbElimi);
				globalResult.put("nbRecordsRead", nbElemRead);
				globalResult.put("nbRecordsProcessed", nbElemProcessed);
				globalResult.put("nbRecordsWritten", itemsWriter.getNbElementWritten());
				if (atypicWriter != null) {
					globalResult.put("nbAtypicRecordsWritten", atypicWriter.getNbElementWritten());
				}
				if (eliminatedWriter != null) {
					globalResult.put("nbEliminatedRecordsWritten", eliminatedWriter.getNbElementWritten());
				}

				logger.log(logLevel, JsonUtils.jsonPrettyPrint(globalResult));
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"Exception reading file.\n  Line nb=" + nbLine + "\n  Line=" + line + "\n  File=" + inputFilePath,
					e);
		}

		long duration = System.currentTimeMillis() - now;
		long durationPerItem = 0;
		if (nbElemRead != 0) {
			durationPerItem = duration / nbElemRead;
		}
		logger.fine("End. Duration=" + duration + "\n Duration per item=" + durationPerItem + "\n Number of items="
				+ nbElemRead);

	}
	
	public void setEntriesQueueSize(int entriesQueueSize) {
		this.entriesQueueSize = entriesQueueSize;
	}

	public void setOutputQueueSize(int outputQueueSize) {
		this.outputQueueSize = outputQueueSize;
	}

	public void setEliminatedQueueSize(int eliminatedQueueSize) {
		this.eliminatedQueueSize = eliminatedQueueSize;
	}

	public void setAtypicQueueSize(int atypicQueueSize) {
		this.atypicQueueSize = atypicQueueSize;
	}

	public void setNbProcessThreads(int nbProcessThreads) {
		this.nbProcessThreads = nbProcessThreads;
	}

	private void terminateExecutor(ExecutorService execSvc) {

		execSvc.shutdown();
		try {
			// Wait a while for existing tasks to terminate
			if (!execSvc.awaitTermination(10, TimeUnit.SECONDS)) {
				// Cancel currently executing tasks
				execSvc.shutdownNow();
			}

			if (!execSvc.awaitTermination(5, TimeUnit.SECONDS)) {
				logger.severe("Executor not terminated " + execSvc.isTerminated());
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			logger.warning("Interrupted exception during threads shutdown");
			execSvc.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	// Is it a valid first line of an entry
	protected abstract boolean isValidFirstLineEntry(String line);

	protected abstract boolean belongsToCurrentEntry(ArrayList<String> currentEntry, String line);
}

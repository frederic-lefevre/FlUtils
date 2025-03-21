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

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.fasterxml.jackson.databind.JsonNode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MultiThreadedTransformerTest {

	@Test
	@Order(1)
	void testWithNoInputFile() throws URISyntaxException, IOException {
		
		Logger sampleExtractorLogger = Logger.getLogger(SampleExtractor.class.getName());
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(sampleExtractorLogger);
		
		TestFileManager testFileManager = new TestFileManager();
		
		assertThat(testFileManager.getInputTestPath()).doesNotExist();
		
		SampleExtractor sampleExtractor = new SampleExtractor(
				testFileManager.getInputTestPath(),
				testFileManager.getCharset(),
				testFileManager.getRegularLinesOutputPath(),
				testFileManager.getCharset(),
				testFileManager.getWrongLinesOutputPath(),
				testFileManager.getAtypicLinesOutputPath()
				);
		
		SampleItemProcessor sampleItemProcessor = new SampleItemProcessor("Line prefix: ");
		
		JsonNode result = sampleExtractor.extract(sampleItemProcessor);
		
		assertThat(result.get("error")).isNotNull();
		assertThat(result.get("error").asText()).startsWith("Exception reading file");

		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		
		assertThat(testFileManager.getRegularLinesOutputPath()).exists().isRegularFile().isEmptyFile();
		assertThat(testFileManager.getWrongLinesOutputPath()).exists().isRegularFile().isEmptyFile();
		assertThat(testFileManager.getAtypicLinesOutputPath()).exists().isRegularFile().isEmptyFile();
		
		testFileManager.deleAllTestFiles();
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	private static final int NB_EXTRACTOR_THREAD = 9;
	
	@Test
	@Order(2)
	void test() throws URISyntaxException, IOException {
		
		TestFileManager testFileManager = new TestFileManager();
		
		testFileManager.writeAllTestsFiles();
		assertThat(testFileManager.getInputTestPath()).exists().isRegularFile().isNotEmptyFile();
		
		SampleExtractor sampleExtractor = new SampleExtractor(
				testFileManager.getInputTestPath(),
				testFileManager.getCharset(),
				testFileManager.getRegularLinesOutputPath(),
				testFileManager.getCharset(),
				testFileManager.getWrongLinesOutputPath(),
				testFileManager.getAtypicLinesOutputPath()
				);
		
		sampleExtractor.setNbProcessThreads(NB_EXTRACTOR_THREAD);
		
		SampleItemProcessor sampleItemProcessor = new SampleItemProcessor("Line prefix: ");
		
		JsonNode result = sampleExtractor.extract(sampleItemProcessor);
		
		assertThat(testFileManager.getRegularLinesOutputPath()).exists().isRegularFile().isNotEmptyFile();
		assertThat(testFileManager.getWrongLinesOutputPath()).exists().isRegularFile().isNotEmptyFile();
		assertThat(testFileManager.getAtypicLinesOutputPath()).exists().isRegularFile().isNotEmptyFile();
		
		assertThat(result).isNotNull();
		
		assertThat(result.get("nbLinesEliminated")).isNotNull();
		assertThat(result.get("nbEliminatedRecordsWritten")).isNotNull();
		assertThat(result.get("nbEliminatedRecordsWritten").asLong())
			.isEqualTo(result.get("nbLinesEliminated").asLong())
			.isEqualTo(TestFileManager.NUMBER_OF_WRONG_LINE);
		
		assertThat(result.get("nbAtypicRecordsWritten")).isNotNull();
		assertThat(result.get("nbAtypicRecordsWritten").asLong()).isEqualTo(TestFileManager.NUMBER_OF_ATYPIC_LINE);
		
		assertThat(result.get("nbLinesRead")).isNotNull();
		assertThat(result.get("nbLinesRead").asLong()).isEqualTo(TestFileManager.NUMBER_OF_REGULAR_LINE + TestFileManager.NUMBER_OF_ATYPIC_LINE + TestFileManager.NUMBER_OF_WRONG_LINE);
		
		// Lines are grouped by pair in the sample
		assertThat(result.get("nbRecordsProcessed")).isNotNull();
		assertThat(result.get("nbRecordsRead")).isNotNull();
		assertThat(result.get("nbRecordsWritten")).isNotNull();
		assertThat(result.get("nbRecordsProcessed").asLong())
			.isEqualTo(result.get("nbRecordsRead").asLong())
			.isEqualTo(result.get("nbRecordsWritten").asLong())
			.isEqualTo((TestFileManager.NUMBER_OF_REGULAR_LINE + TestFileManager.NUMBER_OF_ATYPIC_LINE)/2);
		
		assertThat(result.get("nbRecordsProcessedByThreads")).isNotNull().hasSize(NB_EXTRACTOR_THREAD);
		assertThat(result.get("nbRecordsProcessedByThreads").isArray()).isTrue();
		
		long nbRecordsProcessedByThread = StreamSupport.stream(result.get("nbRecordsProcessedByThreads").spliterator(), false)
			.map(item -> item.get("nbRecordsProcessed").asLong())
			.reduce(0L, Long::sum);		
		assertThat(nbRecordsProcessedByThread).isEqualTo(result.get("nbRecordsProcessed").asLong());

		testFileManager.deleAllTestFiles();
	}
}

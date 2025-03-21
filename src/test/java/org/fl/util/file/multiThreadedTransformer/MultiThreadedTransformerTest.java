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

import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
		
		sampleExtractor.extract(sampleItemProcessor);

		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		
		assertThat(testFileManager.getRegularLinesOutputPath()).exists().isRegularFile().isEmptyFile();
		assertThat(testFileManager.getWrongLinesOutputPath()).exists().isRegularFile().isEmptyFile();
		assertThat(testFileManager.getAtypicLinesOutputPath()).exists().isRegularFile().isEmptyFile();
		
		testFileManager.deleAllTestFiles();
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
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
		
		SampleItemProcessor sampleItemProcessor = new SampleItemProcessor("Line prefix: ");
		
		sampleExtractor.extract(sampleItemProcessor);
		
		assertThat(testFileManager.getRegularLinesOutputPath()).exists().isRegularFile().isNotEmptyFile();
		assertThat(testFileManager.getWrongLinesOutputPath()).exists().isRegularFile().isNotEmptyFile();
		assertThat(testFileManager.getAtypicLinesOutputPath()).exists().isRegularFile().isNotEmptyFile();
		
		testFileManager.deleAllTestFiles();
	}
}

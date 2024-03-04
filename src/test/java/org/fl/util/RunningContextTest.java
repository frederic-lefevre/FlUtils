/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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

package org.fl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class RunningContextTest {
	
	private static final String LOGGER_NAME = "org.fl.util.test1";
	
	@Test
	void testRunningContextWithRelativePath() {
		
		RunningContext rc = new RunningContext(LOGGER_NAME, null, "test1.properties");
		
		assertThat(rc).isNotNull();
		
		Logger logger = Logger.getLogger(LOGGER_NAME);
		
		assertThat(logger).isNotNull();
		
		assertThat(logger.getHandlers()).hasSize(3);
		
		List<String> handlersClassName = Arrays.stream(logger.getHandlers())
			.map(handler -> handler.getClass().toString())
			.toList();
		
		assertThat(handlersClassName).hasSameElementsAs(
				List.of(
						"class java.util.logging.FileHandler", 
						"class java.util.logging.ConsoleHandler",
						"class org.fl.util.BufferLogHandler"));
		
		assertThat(rc.getName()).isEqualTo(LOGGER_NAME);
		
		assertThat(rc.getInitializationDate()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));

	}

	@Test
	void testRunningContextWithAbsolutePath() {
		
		RunningContext rc = new RunningContext(LOGGER_NAME, null, 
				"C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties");
		
		assertThat(rc).isNotNull();
		
		Logger logger = Logger.getLogger(LOGGER_NAME);
		
		assertThat(logger).isNotNull();
		
		assertThat(logger.getHandlers()).hasSize(3);
		
		List<String> handlersClassName = Arrays.stream(logger.getHandlers())
			.map(handler -> handler.getClass().toString())
			.toList();
		
		assertThat(handlersClassName).hasSameElementsAs(
				List.of(
						"class java.util.logging.FileHandler", 
						"class java.util.logging.ConsoleHandler",
						"class org.fl.util.BufferLogHandler"));
	}
	
	@Test
	void testBasicRunningContextWithURI() throws URISyntaxException {
		
		RunningContext rc = new RunningContext(LOGGER_NAME, null, 
				new URI("file:///C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		assertThat(rc).isNotNull();
		
		Logger logger = Logger.getLogger(LOGGER_NAME);
		
		assertThat(logger).isNotNull();
		
		assertThat(logger.getHandlers()).hasSize(3);
		
		List<String> handlersClassName = Arrays.stream(logger.getHandlers())
			.map(handler -> handler.getClass().toString())
			.toList();
		
		assertThat(handlersClassName).hasSameElementsAs(
				List.of(
						"class java.util.logging.FileHandler", 
						"class java.util.logging.ConsoleHandler",
						"class org.fl.util.BufferLogHandler"));
	}
}

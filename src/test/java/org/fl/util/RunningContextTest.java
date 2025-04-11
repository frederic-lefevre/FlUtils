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

package org.fl.util;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class RunningContextTest {
	
	private static final String LOGGER_NAME = "org.fl.util.Test1";

	@Test
	void testRunningContextWithNullUriParam() throws JsonProcessingException {
		testRunningContextWithNullParam(() -> new RunningContext(null, null));
	}
	
	private void testRunningContextWithNullParam(Supplier<RunningContext> rcSupplier) throws JsonProcessingException {
		
		LogRecordCounter runningContextLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger("org.fl"));
		
		LogRecordCounter rootLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		RunningContext rc = rcSupplier.get();
		
		assertThat(rc).isNotNull();
		assertThat(rc.getName()).isEqualTo("org.fl");
		
		AdvancedProperties advancedProperties = rc.getProps();
		assertThat(advancedProperties).isNotNull().isNotEmpty();
		
		assertThat(advancedProperties.get("buildOs"))
			.isInstanceOfSatisfying(String.class, buildOs -> buildOs.contains("windows"));
		
		JsonNode buildInformation = rc.getBuildInformationAsJson();
		assertThat(buildInformation).isNotNull();
		
		assertThat(buildInformation).isNotEmpty().hasSize(2)
		.satisfiesExactlyInAnyOrder(
				buildInfo -> { 
					assertThat(buildInfo.get("moduleName")).isNotNull();
					assertThat(buildInfo.get("moduleName").asText()).isEqualTo("org.fl");
					assertThat(buildInfo.get("buildInformation")).isNotNull();
					assertThat(buildInfo.get("buildInformation").asText()).isEqualTo("No build information");
				},
				buildInfo -> { 
					assertThat(buildInfo.get("moduleName")).isNotNull();
					assertThat(buildInfo.get("moduleName").asText()).isEqualTo("org.fl.util");
					assertThat(buildInfo.get("version")).isNotNull();
					assertThat(buildInfo.get("version").asText()).isNotEmpty();
				}
				);
		
		assertThat(rc.getInitializationDate()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));
		
		assertThat(rc.getCommonLogFormatter()).isInstanceOf(SimpleFormatter.class);
		
		assertThat(runningContextLogRecordCounter.getLogRecordCount()).isEqualTo(3);
		assertThat(runningContextLogRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(3);
		
		assertThat(rootLogRecordCounter.getLogRecordCount()).isEqualTo(2);
		assertThat(rootLogRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(rootLogRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		runningContextLogRecordCounter.stopLogCountAndFilter();
		rootLogRecordCounter.stopLogCountAndFilter();

	}
	
	@Test
	void testRunningContextWithRelativePath() throws JsonProcessingException {
		
		RunningContext rc = new RunningContext(LOGGER_NAME, URI.create("test1.properties"));
		
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

		assertThat(rc.getBuildInformation()).isNotNull().isNotEmpty().contains("version");
		
		assertThat(rc.getCommonLogFormatter()).isInstanceOf(PlainLogFormatter.class);
	}

	@Test
	void testRunningContextWithAbsolutePath() {
		
		RunningContext rc = new RunningContext(LOGGER_NAME, 
				URI.create("file:///C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
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
		
		RunningContext rc = new RunningContext(LOGGER_NAME,
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
	
	@Test
	void testRunningContextBuildInfo() throws URISyntaxException, JsonProcessingException {
		
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		assertThat(rc).isNotNull();
		
		String buildInformationString = rc.getBuildInformation();
		assertThat(buildInformationString).isNotNull();
		
		JsonNode buildInformation = rc.getBuildInformationAsJson();
		assertThat(buildInformation).isNotNull();
		
		assertThat(buildInformation).isNotEmpty().hasSize(2)
			.satisfiesExactlyInAnyOrder(
					buildInfo -> { 
						assertThat(buildInfo.get("moduleName")).isNotNull();
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo(LOGGER_NAME);
					},
					buildInfo -> { 
						assertThat(buildInfo.get("moduleName")).isNotNull();
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo("org.fl.util");
					}
					);
	}
	
	@Test
	void testRunningContextBuildInfo2() throws URISyntaxException, JsonProcessingException {
							
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(LOGGER_NAME));
		
		rc.addBuildInformation("does.not.exists");
		
		JsonNode buildInformation = rc.getBuildInformationAsJson();
		assertThat(buildInformation).isNotNull();
		
		assertThat(buildInformation).isNotEmpty().hasSize(3)
			.satisfiesExactlyInAnyOrder(
					buildInfo -> { 
						assertThat(buildInfo.get("moduleName")).isNotNull();
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo(LOGGER_NAME);
						assertThat(buildInfo.get("version")).isNotNull();
						assertThat(buildInfo.get("version").asText()).isNotEmpty();
					},
					buildInfo -> { 
						assertThat(buildInfo.get("moduleName")).isNotNull();
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo("org.fl.util");
						assertThat(buildInfo.get("version")).isNotNull();
						assertThat(buildInfo.get("version").asText()).isNotEmpty();
					},
					buildInfo -> { 
						assertThat(buildInfo.get("moduleName")).isNotNull();
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo("does.not.exists");
						assertThat(buildInfo.get("buildInformation")).isNotNull();
						assertThat(buildInfo.get("buildInformation").asText()).isEqualTo("No build information");
					}
					);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(3);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(3);
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@Test
	void testRunningContextLoggingInfo() throws URISyntaxException, JsonProcessingException {
		
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		JsonNode applicationInfos = rc.getApplicationInfo(false);
		
		assertThat(applicationInfos).isNotNull();
		
		JsonNode loggingInfos = applicationInfos.get("loggingInformation");
		assertThat(loggingInfos).isNotNull();
		
		assertThat(loggingInfos.get("handlers")).isNotNull();
		assertThat(loggingInfos.get("handlers").asText())
			.isEqualTo("java.util.logging.FileHandler,java.util.logging.ConsoleHandler");
	}
}

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
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

class RunningContextTest {
	
	private static final String LOGGER_NAME = "org.fl.util.Test1";
	private static final String LOGGER_NAME2 = "org.fl.util.Test7";

	@Test
	void testRunningContextWithNullUriParam() throws JsonProcessingException {
		testRunningContextWithNullParam(() -> new RunningContext(null, null));
	}
	
	private void testRunningContextWithNullParam(Supplier<RunningContext> rcSupplier) throws JsonProcessingException {
		
		LogRecordCounter runningContextLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(RunningContext.class.getName()));
		
		LogRecordCounter orgFlLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger("org.fl"));
		
		LogRecordCounter loggerManagerLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(LoggerManager.class.getName()));
		
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
				});
		
		assertThat(rc.getInitializationDate()).isCloseTo(Instant.now(), within(2, ChronoUnit.SECONDS));
		
		assertThat(rc.getCommonLogFormatter()).isInstanceOf(SimpleFormatter.class);
		
		assertThat(orgFlLogRecordCounter.getLogRecordCount()).isEqualTo(3);
		assertThat(orgFlLogRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(3);
		assertThat(orgFlLogRecordCounter.getLogRecords()).hasSize(3)
			.anySatisfy(logRecord -> assertThat(logRecord.getMessage()).isEqualTo("No project properties (build information) found"));
		
		assertThat(runningContextLogRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(runningContextLogRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(runningContextLogRecordCounter.getLogRecords()).singleElement()
			.satisfies(logRecord -> assertThat(logRecord.getMessage()).isEqualTo("Null application name passed in running context. PropertyUri=null"));
		
		assertThat(loggerManagerLogRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(loggerManagerLogRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		assertThat(loggerManagerLogRecordCounter.getLogRecords()).singleElement()
			.satisfies(logRecord -> assertThat(logRecord.getMessage()).isEqualTo("Logging properties file not found loggingProperties.properties"));
		
		orgFlLogRecordCounter.stopLogCountAndFilter();
		runningContextLogRecordCounter.stopLogCountAndFilter();
		loggerManagerLogRecordCounter.stopLogCountAndFilter();

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
				URI.create("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		assertThat(rc).isNotNull();
		
		JsonNode applicationInfo = rc.getApplicationInfo(false);
		assertThat(applicationInfo).isNotNull();
		
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
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
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
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		assertThat(rc).isNotNull();
		
		String buildInformationString = rc.getBuildInformation();
		assertThat(buildInformationString).isNotNull();
		
		JsonNode buildInformation = rc.getBuildInformationAsJson();
		assertThat(buildInformation).isNotNull();
		
		assertThat(buildInformation).isNotEmpty().hasSize(2)
			.satisfiesExactlyInAnyOrder(
					buildInfo -> assertModuleBuildInfo(buildInfo, LOGGER_NAME),
					buildInfo -> assertModuleBuildInfo(buildInfo, "org.fl.util")
				);
	}
	
	private void assertModuleBuildInfo(JsonNode buildInfo, String moduleName) {
		assertThat(buildInfo).hasSize(11);
		assertThat(buildInfo.get("moduleName")).isNotNull();
		assertThat(buildInfo.get("moduleName").asText()).isEqualTo(moduleName);
		assertThat(buildInfo.has("version")).isTrue();
		assertThat(buildInfo.has("buildtime")).isTrue();
		assertThat(buildInfo.has("builder")).isTrue();
		assertThat(buildInfo.has("buildhost")).isTrue();
		assertThat(buildInfo.has("buildOs")).isTrue();
		assertThat(buildInfo.has("gitBranch")).isTrue();
		assertThat(buildInfo.has("gitCommitId")).isTrue();
		assertThat(buildInfo.has("gitCommitUrl")).isTrue();
		assertThat(buildInfo.has("gitCommitTime")).isTrue();
		assertThat(buildInfo.has("gitDirty")).isTrue();
	}
	
	@Test
	void testRunningContextBuildInfo2() throws URISyntaxException, JsonProcessingException {
							
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
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
	void testRunningContextBuildInfo3() throws URISyntaxException, JsonProcessingException {
							
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		String addedModule = "org.fl.util.test2";
		rc.addBuildInformation(addedModule);
		
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
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo(addedModule);
						assertThat(buildInfo.get("version")).isNotNull();
						assertThat(buildInfo.get("version").asText()).isNotEmpty();
					}
					);
	}
	
	@Test
	void testRunningContextBuildInfoWithSpecificClassLoader() throws URISyntaxException, JsonProcessingException {
							
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		String addedModule = "org.fl.util.test2";
		rc.addBuildInformation(addedModule, JsonLogFormatter.class.getClassLoader());
		
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
						assertThat(buildInfo.get("moduleName").asText()).isEqualTo(addedModule);
						assertThat(buildInfo.get("version")).isNotNull();
						assertThat(buildInfo.get("version").asText()).isNotEmpty();
					}
					);
	}
	
	@Test
	void testRunningContextLoggingInfo() throws URISyntaxException, JsonProcessingException {
		
		RunningContext rc = new RunningContext(LOGGER_NAME,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties"));
		
		JsonNode applicationInfos = rc.getApplicationInfo(false);
		
		assertThat(applicationInfos).isNotNull();
		
		JsonNode loggingInfos = applicationInfos.get("loggingInformation");
		assertThat(loggingInfos).isNotNull();
		
		assertThat(loggingInfos.get("handlers")).isNotNull();
		assertThat(loggingInfos.get("handlers").asText())
			.isEqualTo("java.util.logging.FileHandler,java.util.logging.ConsoleHandler");
	}
	
	@Test
	void testRunningContextApplicationInfoLog() throws URISyntaxException, JsonProcessingException {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(LOGGER_NAME2));
		
		RunningContext rc = new RunningContext(LOGGER_NAME2,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test7.properties"));
		
		JsonNode applicationInfos = rc.getApplicationInfo(false);	
		assertThat(applicationInfos).isNotNull();
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
	
	@Test
	void testBufferLogHandlerForInit() throws URISyntaxException {
		
		String loggerName = "org.fl.util.Test9";
		
		RunningContext rc = new RunningContext(loggerName,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test9.properties"));
		
		BufferLogHandler bufferLogHandlerForInit = rc.getBufferLogHandlerForInit();
		assertThat(bufferLogHandlerForInit).isNotNull();
		Logger logger = Logger.getLogger(loggerName);
		String infoMessage = "un message à l'init";
		logger.info(infoMessage);
		
		assertThat(bufferLogHandlerForInit.getLogRecords()).isNotNull().singleElement()
			.satisfies(logRecord -> assertThat(logRecord.getMessage()).isEqualTo(infoMessage));
	}
	
	@Test
	void testRemoveBufferLogForInit() throws Exception {
		
		String loggerName = "org.fl.util.Test9";
		
		RunningContext rc = new RunningContext(loggerName,
				new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test9.properties"));
		
		BufferLogHandler bufferLogHandlerForInit = rc.getBufferLogHandlerForInit();
		assertThat(bufferLogHandlerForInit).isNotNull();
		Logger logger = Logger.getLogger(loggerName);
		String infoMessage = "un message à l'init";
		logger.info(infoMessage);
		
		BufferLogHandler bufferLogHandler = new BufferLogHandler("test handler", 10);
		List<LogRecord> removedInitLogRecords = rc.removeInitBufferLogHandlerAndDrainLogRecordsTo(bufferLogHandler);
		Collection<LogRecord> drainedLogRecords = bufferLogHandler.getLogRecords();
		
		assertThat(removedInitLogRecords).isNotNull()
			.hasSameElementsAs(drainedLogRecords)
			.singleElement()
			.satisfies(logRecord -> assertThat(logRecord.getMessage()).isEqualTo(infoMessage));
		
		assertThat(rc.getBufferLogHandlerForInit()).isNull();
		
	}
	
	@Test
	void nullPrefixAndProgramArgTest() {
		assertThat(RunningContext.getProgramArgWithPrefix(null, null)).isNull();
	}
	
	@Test
	void nullPrefixTest() {
		assertThat(RunningContext.getProgramArgWithPrefix(null, new String[] {"-props=P1"})).isNull();
	}
	
	@Test
	void nullProgramArgTest() {
		assertThat(RunningContext.getProgramArgWithPrefix("-prefix", null)).isNull();
	}
	
	@Test
	void notFoundPrefixTest() {
		assertThat(RunningContext.getProgramArgWithPrefix("-prefs", new String[] {"-propsP1", "prefix2 toto"})).isNull();
	}
	
	@Test
	void notFoundPrefixTest2() {
		assertThat(RunningContext.getProgramArgWithPrefix("-prefs", new String[] {"", "prefix2 toto", ""})).isNull();
	}
	
	@Test
	void notFoundPrefixTest3() {
		assertThat(RunningContext.getProgramArgWithPrefix("-prefs", new String[] {})).isNull();
	}
	
	@Test
	void foundPrefixTest() {
		assertThat(RunningContext.getProgramArgWithPrefix("-props=", new String[] {"-props=propertyFile.properties", "prefix2 toto"}))
			.isNotNull()
			.isEqualTo("propertyFile.properties");
	}
	
	@Test
	void emptyPrefixTest() {
		assertThat(RunningContext.getProgramArgWithPrefix("", new String[] {"param1", "param2"}))
			.isNotNull()
			.isEqualTo("param1");
	}
	
	@Test
	void emptyPrefixTest2() {
		assertThat(RunningContext.getProgramArgWithPrefix("", new String[] {}))
			.isNull();
	}
	
	@Test
	void foundPrefixTest2() {
		assertThat(RunningContext.getProgramArgWithPrefix("-props=", new String[] {"-props=", "prefix2 toto"}))
			.isNotNull()
			.isEqualTo("");
	}
	
	@Test
	void foundPrefixTest3() {
		assertThat(RunningContext.getProgramArgWithPrefix("-props=", new String[] {"-props=propertyFile.properties", "-props=propertyFile2.properties", "prefix2 toto"}))
			.isNotNull()
			.isEqualTo("propertyFile.properties");
	}
	
	@Test
	void foundPrefixTest4() {
		assertThat(RunningContext.getProgramArgWithPrefix("-props ", new String[] {"-props propertyFile.properties", "prefix2 toto"}))
			.isNotNull()
			.isEqualTo("propertyFile.properties");
	}
}

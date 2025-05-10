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

import java.io.IOException;
import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoggerManagerTest {
	
	@BeforeEach
	void resetLogManagerConfig() throws IOException {
		
		// Reset LogManager to initial JVM configuration
		LogManager logManager = LogManager.getLogManager();
		logManager.reset();
		logManager.updateConfiguration(null);
	}
	
	@Test
	@Order(1)
	void testDefaultLoggerConfiguration() {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		LoggerManager logMgr = LoggerManager.builder().build();
		assertThat(logMgr).isNotNull();
		
		// 1 warning is logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		logRecordCounter.stopLogCountAndFilter();
		
		Logger defaultLogger = Logger.getLogger("org.fl");
		
		assertThat(defaultLogger.getLevel()).isNull();
		
		Handler[] handlers = defaultLogger.getHandlers();
		
		assertThat(handlers).isEmpty();
		
		assertThat(logMgr.getLoggingProperties()).isNull();
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(SimpleFormatter.class);
	}

	@Test
	@Order(2)
	void testNamedLoggerBasicConfiguration() {
		
		String loggerName = LoggerManagerTest.class.getName() + ".1";
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		// 1 warning is logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		logRecordCounter.stopLogCountAndFilter();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isNull();
		
		Handler[] handlers = logger.getHandlers();
		
		assertThat(handlers).isEmpty();
		assertThat(logMgr.getLoggingProperties()).isNull();
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(SimpleFormatter.class);
	}
	
	@Test
	@Order(3)
	void testNamedLoggerNotPresentInLoggingConfiguration() throws Exception {
		
		String loggerName = "org.fl.util.notInConfig";
		
		String pathString = "file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/main/java/flUtilsSample.properties";
		
		PropertiesStorage ps = new PropertiesStorage(URI.create(pathString));
		
		AdvancedProperties props = ps.getAdvancedProperties();
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
				
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		assertThat(logMgr.getLoggingProperties()).isNotNull();
		
		// 2 warnings are logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(2);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(2);
		logRecordCounter.stopLogCountAndFilter();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isNull();
	}
	
	@Test
	@Order(4)
	void testNamedLoggerSampleConfiguration() throws Exception {
		
		String loggerName = "org.fl.util.SampleApp";
		
		String pathString = "file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/main/java/flUtilsSample.properties";
		
		PropertiesStorage ps = new PropertiesStorage(URI.create(pathString));
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		AdvancedProperties loggingProps = props.getPropertiesFromFile(LoggerManager.LOGMANAGER_PROPERTY_FILE_PROPERTY);
		
		assertThat(loggingProps.get("handlers")).isEqualTo("java.util.logging.FileHandler,java.util.logging.ConsoleHandler");
		
		assertThat(loggingProps.get("java.util.logging.ConsoleHandler.level")).isEqualTo("INFO");
		assertThat(loggingProps.get("java.util.logging.ConsoleHandler.formatter")).isEqualTo("java.util.logging.SimpleFormatter");
		assertThat(loggingProps.get("java.util.logging.ConsoleHandler.encoding")).isEqualTo("UTF-8");
		
		assertThat(loggingProps.get("java.util.logging.FileHandler.level")).isEqualTo("WARNING");
		assertThat(loggingProps.get("java.util.logging.FileHandler.formatter")).isEqualTo("org.fl.util.PlainLogFormatter");
		assertThat(loggingProps.get("java.util.logging.FileHandler.pattern")).isEqualTo("/ForTests/FlUtils/myLogDir/app%u_%g.log");
		assertThat(loggingProps.get("java.util.logging.FileHandler.limit")).isEqualTo("80000000");
		assertThat(loggingProps.get("java.util.logging.FileHandler.count")).isEqualTo("3");
		assertThat(loggingProps.get("java.util.logging.FileHandler.encoding")).isEqualTo("UTF-8");
		assertThat(loggingProps.get("java.util.logging.FileHandler.append")).isEqualTo("true");
		assertThat(loggingProps.get("java.util.logging.FileHandler.maxLocks")).isEqualTo("100");
		
		assertThat(loggingProps.get("java.util.logging.SimpleFormatter.format")).isEqualTo("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %3$S %2$s%n%4$s: %5$s%6$s%n");
		
		assertThat(loggingProps.get(loggerName + ".handlers")).isEqualTo("java.util.logging.FileHandler,java.util.logging.ConsoleHandler");
		assertThat(loggingProps.get(loggerName + ".useParentHandlers")).isEqualTo("false");
		assertThat(loggingProps.get(loggerName + ".level")).isEqualTo("WARNING");
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(PlainLogFormatter.class);
		
		assertThat(logMgr.getLoggingProperties()).isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(loggingProps);
		
		Logger rootLogger = Logger.getLogger("");
		
		assertThat(rootLogger.getLevel()).isEqualTo(Level.INFO);
		assertThat(rootLogger.getUseParentHandlers()).isTrue();		
		assertSamplePropertyHandlers(rootLogger.getHandlers());
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isEqualTo(Level.WARNING);		
		assertThat(logger.getUseParentHandlers()).isFalse();		
		assertSamplePropertyHandlers(logger.getHandlers());	
	}
	
	private void assertSamplePropertyHandlers(Handler[] handlers) {
		
		assertThat(handlers).hasSize(2)
		.satisfiesExactlyInAnyOrder(
				handler -> { 
					assertThat(handler).isInstanceOf(ConsoleHandler.class);
					assertThat(handler.getEncoding()).isEqualTo("UTF-8");
					assertThat(handler.getLevel()).isEqualTo(Level.INFO);
					assertThat(handler.getFormatter()).isNotNull()
						.isInstanceOf(SimpleFormatter.class);
					assertThat(handler.getFilter()).isNull();
					assertThat(handler.getErrorManager()).isNotNull();
				},
				handler -> { 
					assertThat(handler).isInstanceOf(FileHandler.class);
					assertThat(handler.getEncoding()).isEqualTo("UTF-8");
					assertThat(handler.getLevel()).isEqualTo(Level.WARNING);
					assertThat(handler.getFormatter()).isNotNull()
						.isInstanceOf(PlainLogFormatter.class);
					assertThat(handler.getFilter()).isNull();
					assertThat(handler.getErrorManager()).isNotNull();
				}
			);
	}
	
	@Test
	@Order(5)
	void testBufferLogHandler() throws Exception {
		
		String loggerName = "org.fl.util.Test2";		
		assertsForTest2(loggerName);
	}
	
	@Test
	@Order(6)
	void testWithPreviousLoggerCreation() throws Exception {
		
		String loggerName = "org.fl.util.Test2";
		assertInitialJVMConfig(loggerName);
		assertsForTest2(loggerName);
	}
	
	private void assertsForTest2(String loggerName) throws Exception {
		
		String pathString = "file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test2.properties";
		
		PropertiesStorage ps = new PropertiesStorage(URI.create(pathString));
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		Logger rootLogger = Logger.getLogger("");
		
		assertThat(logger.getLevel()).isEqualTo(Level.INFO);
		assertThat(rootLogger.getLevel()).isEqualTo(Level.WARNING);
		
		assertThat(props.get("logging.BufferLogHandler.bufferLength")).isEqualTo("100");
		assertThat(props.get("logging.BufferLogHandler.level")).isEqualTo("INFO");
		
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(PlainLogFormatter.class);
		
		Handler[] handlers = logger.getHandlers();
		
		assertThat(handlers).hasSize(3)
			.satisfiesExactlyInAnyOrder(
				handler -> assertTest2ConsoleHandler(handler),
				handler -> assertTest2FileHandler(handler),
				handler -> {
					assertThat(handler).isInstanceOf(BufferLogHandler.class);
					assertThat(handler).isInstanceOfSatisfying(BufferLogHandler.class, 
							bufferLogHandler -> { 
								assertThat(bufferLogHandler.getName()).isEqualTo("standard bufferLogHandler");
								assertThat(bufferLogHandler.getMaxMemoryLogRecord()).isEqualTo(100);
							});
					assertThat(handler.getLevel()).isEqualTo(Level.INFO);
				}
			);
		
		Handler[] rootHandlers = rootLogger.getHandlers();
		
		assertThat(rootHandlers).hasSize(2)
			.satisfiesExactlyInAnyOrder(
				handler -> assertTest2ConsoleHandler(handler),
				handler -> assertTest2FileHandler(handler)			
			);
		
		// Log a INFO message
		String logMessage = "essai de log dans le BufferLogHandler";
		logger.info(logMessage);
		
		// Check it is in memory log
		assertThat(logMgr.getMemoryLogs()).isNotNull().contains(logMessage);
		
		// Delete memory logs
		assertThat(logMgr.deleteMemoryLogs()).isNotNull().contains("1 log records removed");
		
		// Check memory logs is empty
		assertThat(logMgr.getMemoryLogs()).isNotNull().isEmpty();
		
		// Delete memory logs
		assertThat(logMgr.deleteMemoryLogsAndResize(105)).isNotNull()
			.contains("0 log records removed")
			.contains("Maximum number of records resized to 105");
		
		logger.fine("Should not be published");
		// Check memory log is empty
		assertThat(logMgr.getMemoryLogs()).isNotNull().isEmpty();
	}
	
	private void assertTest2ConsoleHandler(Handler handler) {

		assertThat(handler).isInstanceOf(ConsoleHandler.class);
		assertThat(handler.getLevel()).isEqualTo(Level.OFF);
		assertThat(handler.getFormatter()).isNotNull()
			.isInstanceOf(SimpleFormatter.class);
		assertThat(handler.getFilter()).isNull();
		assertThat(handler.getErrorManager()).isNotNull();
	}

	private void assertTest2FileHandler(Handler handler) {

		assertThat(handler).isInstanceOf(FileHandler.class);
		assertThat(handler.getLevel()).isEqualTo(Level.OFF);
		assertThat(handler.getFormatter()).isNotNull()
			.isInstanceOf(PlainLogFormatter.class);
		assertThat(handler.getFilter()).isNull();
		assertThat(handler.getErrorManager()).isNotNull();
	}
	
	private void assertInitialJVMConfig(String loggerName) {
		
		// Get loggers before calling LoggerManager
		Logger logger = Logger.getLogger(loggerName);
		Logger rootLogger = Logger.getLogger("");
		
		assertThat(logger).isNotNull();
		assertThat(logger.getLevel()).isNull();
		assertThat(logger.getHandlers()).isNotNull().isEmpty();
		
		assertThat(rootLogger).isNotNull();
		assertThat(rootLogger.getLevel()).isEqualTo(Level.INFO);
		assertThat(rootLogger.getHandlers()).singleElement()
		.satisfies(
			handler -> {		
				assertThat(handler).isInstanceOf(ConsoleHandler.class);
				assertThat(handler.getLevel()).isEqualTo(Level.INFO);
				assertThat(handler.getFormatter()).isNotNull()
					.isInstanceOf(SimpleFormatter.class);
				assertThat(handler.getFilter()).isNull();
				assertThat(handler.getErrorManager()).isNotNull();}		
		);
	}
	
	@Test
	@Order(7)
	void test3() throws Exception {
		
		String loggerName = "org.fl.util.Test3";		
		assertsForTest3(loggerName);
	}
	
	@Test
	@Order(8)
	void test3WithPreviousLoggerCreation() throws Exception {
		
		String loggerName = "org.fl.util.Test3";
		assertInitialJVMConfig(loggerName);
		assertsForTest3(loggerName);
	}
	
	private void assertsForTest3(String loggerName) throws Exception {
		
		String pathString = "file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test3.properties";
		
		PropertiesStorage ps = new PropertiesStorage(URI.create(pathString));
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		Logger rootLogger = Logger.getLogger("");
		
		assertThat(logger.getLevel()).isEqualTo(Level.CONFIG);
		assertThat(rootLogger.getLevel()).isEqualTo(Level.WARNING);
		
		assertThat(props.get("logging.BufferLogHandler.bufferLength")).isEqualTo("100");
		assertThat(props.get("logging.BufferLogHandler.level")).isEqualTo("INFO");
		
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(PlainLogFormatter.class);
		
		Handler[] handlers = logger.getHandlers();
		
		assertThat(handlers).hasSize(2)
			.satisfiesExactlyInAnyOrder(
				handler -> assertTest3FileHandler(handler),
				handler -> {
					assertThat(handler).isInstanceOf(BufferLogHandler.class);
					assertThat(handler).isInstanceOfSatisfying(BufferLogHandler.class, 
							bufferLogHandler -> { 
								assertThat(bufferLogHandler.getName()).isEqualTo("standard bufferLogHandler");
								assertThat(bufferLogHandler.getMaxMemoryLogRecord()).isEqualTo(100);
							});
					assertThat(handler.getLevel()).isEqualTo(Level.INFO);
				}
			);
		
		Handler[] rootHandlers = rootLogger.getHandlers();
		
		assertThat(rootHandlers).singleElement()
			.satisfies(
				handler -> assertTest3ConsoleHandler(handler)		
			);
	}
	
	private void assertTest3ConsoleHandler(Handler handler) {

		assertThat(handler).isInstanceOf(ConsoleHandler.class);
		assertThat(handler.getLevel()).isEqualTo(Level.FINE);
		assertThat(handler.getFormatter()).isNotNull()
			.isInstanceOf(JsonLogFormatter.class);
		assertThat(handler.getFilter()).isNull();
		assertThat(handler.getErrorManager()).isNotNull();
	}

	private void assertTest3FileHandler(Handler handler) {

		assertThat(handler).isInstanceOf(FileHandler.class);
		assertThat(handler.getLevel()).isEqualTo(Level.FINER);
		assertThat(handler.getFormatter()).isNotNull()
			.isInstanceOf(PlainLogFormatter.class);
		assertThat(handler.getFilter()).isNull();
		assertThat(handler.getErrorManager()).isNotNull();
	}
	
	@Test
	@Order(9)
	void warningWhenLoggingFileNamePropertyNotFound() throws Exception {
		
		String loggerName = "org.fl.util.SampleApp";
		
		String pathString = "file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test4.properties";		
	
		PropertiesStorage ps = new PropertiesStorage(URI.create(pathString));
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		// 1 warning is logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		logRecordCounter.stopLogCountAndFilter();
	}
	
	@Test
	@Order(10)
	void severeErrorWhenLoggingPropertyFileNotFound() throws Exception {
		
		String loggerName = "org.fl.util.SampleApp";
		
		String pathString = "file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test5.properties";		
		
		PropertiesStorage ps = new PropertiesStorage(URI.create(pathString));
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		// 1 warning is logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		
		logRecordCounter.stopLogCountAndFilter();
	}
}

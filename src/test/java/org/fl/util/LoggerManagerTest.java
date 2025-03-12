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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class LoggerManagerTest {
	
	@Test
	void testDefaultLoggerConfiguration() {
		
		LoggerManager logMgr = LoggerManager.builder().build();
		assertThat(logMgr).isNotNull();
		
		Logger defaultLogger = Logger.getLogger("org.fl");
		
		assertThat(defaultLogger.getLevel()).isNull();
		
		Handler[] handlers = defaultLogger.getHandlers();
		
		assertThat(handlers).isEmpty();
		
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(SimpleFormatter.class);
	}

	@Test
	void testNamedLoggerBasicConfiguration() {
		
		String loggerName = LoggerManagerTest.class.getName() + ".1";
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isNull();
		
		Handler[] handlers = logger.getHandlers();
		
		assertThat(handlers).isEmpty();
		assertThat(logMgr.getCommonFormatterInstance()).isInstanceOf(SimpleFormatter.class);
	}
	
	@Test
	void testNamedLoggerNotPresentInLoggingConfiguration() throws Exception {
		
		String loggerName = "org.fl.util.notInConfig";
		
		String pathString = "C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/main/java/flUtilsSample.properties";
		Path propertyPath = Paths.get(pathString);
		
		PropertiesStorage ps = new PropertiesStorage(null, propertyPath);
		
		AdvancedProperties props = ps.getAdvanced(null);
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
				
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		// 2 warnings are logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(2);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(2);
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isNull();
	}
	
	@Test
	void testNamedLoggerSampleConfiguration() throws Exception {
		
		String loggerName = "org.fl.util.SampleApp";
		
		String pathString = "C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/main/java/flUtilsSample.properties";
		Path propertyPath = Paths.get(pathString);
		
		PropertiesStorage ps = new PropertiesStorage(null, propertyPath);
		
		AdvancedProperties props = ps.getAdvanced(null);
		assertThat(props).isNotNull();
		
		AdvancedProperties loggingProps = props.getPropertiesFromFile(LoggerManager.LOGMANAGER_PROPERTY_FILE_PROPERTY);
		
		assertThat(loggingProps.get("handlers")).isEqualTo("java.util.logging.FileHandler,java.util.logging.ConsoleHandler");
		
		assertThat(loggingProps.get("java.util.logging.ConsoleHandler.level")).isEqualTo("INFO");
		assertThat(loggingProps.get("java.util.logging.ConsoleHandler.formatter")).isEqualTo("java.util.logging.SimpleFormatter");
		assertThat(loggingProps.get("java.util.logging.ConsoleHandler.encoding")).isEqualTo("UTF-8");
		
		assertThat(loggingProps.get("java.util.logging.FileHandler.level")).isEqualTo("WARNING");
		assertThat(loggingProps.get("java.util.logging.FileHandler.formatter")).isEqualTo("org.fl.util.PlainLogFormatter");
		assertThat(loggingProps.get("java.util.logging.FileHandler.pattern")).isEqualTo("/tmp/myLogDir/app%u_%g.log");
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
	void testBufferLogHandler() throws Exception {
		
		String loggerName = "org.fl.util.Test2";
		
		String pathString = "C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test2.properties";
		Path propertyPath = Paths.get(pathString);
		
		PropertiesStorage ps = new PropertiesStorage(null, propertyPath);
		
		AdvancedProperties props = ps.getAdvanced(null);
		assertThat(props).isNotNull();
		
		LoggerManager logMgr = LoggerManager.builder()
				.applicationRootLoggerName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isEqualTo(Level.INFO);
		
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
	
}

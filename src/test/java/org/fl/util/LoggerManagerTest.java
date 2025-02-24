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

import org.junit.jupiter.api.Test;

class LoggerManagerTest {
	
	@Test
	void testDefaultLoggerConfiguration() {
		
		LoggerManager logMgr = LoggerManager.builder().build();
		assertThat(logMgr).isNotNull();
		
		Logger defaultLogger = Logger.getLogger("org.fl");
		
		assertThat(defaultLogger.getLevel()).isEqualTo(Level.WARNING);
		
		Handler[] handlers = defaultLogger.getHandlers();
		
		assertThat(handlers).singleElement()
			.satisfies(handler -> assertThat(handler).isInstanceOf(ConsoleHandler.class));
	}

	@Test
	void testNamedLoggerBasicConfiguration() {
		
		String loggerName = LoggerManagerTest.class.getName() + ".1";
		
		LoggerManager logMgr = LoggerManager.builder()
				.logName(loggerName)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isEqualTo(Level.WARNING);
		
		Handler[] handlers = logger.getHandlers();
		
		assertThat(handlers).singleElement()
			.satisfies(handler -> assertThat(handler).isInstanceOf(ConsoleHandler.class));
	}
	
	@Test
	void testNamedLoggerSampleConfiguration() throws Exception {
		
		String loggerName = LoggerManagerTest.class.getName() + ".2";
		
		String pathString = "C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/main/java/flUtilsSample.properties";
		Path propertyPath = Paths.get(pathString);
		
		PropertiesStorage ps = new PropertiesStorage(null, propertyPath);
		
		AdvancedProperties props = ps.getAdvanced(null);
		assertThat(props).isNotNull();
		
		assertThat(props.get("logging.directory.name")).isEqualTo("/tmp/myLogDir/");
		assertThat(props.get("logging.logfile.name")).isEqualTo("journal%g.log");
		assertThat(props.get("logging.logfile.length")).isEqualTo("80000000");
		assertThat(props.get("logging.logfile.number")).isEqualTo("3");
				
		assertThat(props.get("logging.file.level")).isEqualTo("WARNING");
		assertThat(props.get("logging.console.level")).isEqualTo("INFO");
				
		assertThat(props.get("logging.console.encode")).isEqualTo("UTF-8");
		assertThat(props.get("logging.file.encode")).isEqualTo("UTF-8");
		
		assertThat(props.get("logging.root.file.level")).isEqualTo("INFO");
		assertThat(props.get("logging.rootLogfile.name")).isEqualTo("rootApp%g.log");
		
		assertThat(props.get("logging.simpleLogFormatter.format")).isEqualTo("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %3$S %2$s%n%4$s: %5$s%6$s%n");
		
		LoggerManager logMgr = LoggerManager.builder()
				.logName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isEqualTo(Level.INFO);
		
		assertThat(System.getProperty("java.util.logging.SimpleFormatter.format"))
			.isNotNull()
			.isEqualTo("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %3$S %2$s%n%4$s: %5$s%6$s%n");
		
		Handler[] handlers = logger.getHandlers();
		
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
							.isInstanceOf(SimpleFormatter.class);
						assertThat(handler.getFilter()).isNull();
						assertThat(handler.getErrorManager()).isNotNull();
					}
				);
	}
	
	@Test
	void testBufferLogHandler() throws Exception {
		
		String loggerName = LoggerManagerTest.class.getName()  + ".3";
		
		String pathString = "C:/FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test2.properties";
		Path propertyPath = Paths.get(pathString);
		
		PropertiesStorage ps = new PropertiesStorage(null, propertyPath);
		
		AdvancedProperties props = ps.getAdvanced(null);
		assertThat(props).isNotNull();
		
		LoggerManager logMgr = LoggerManager.builder()
				.logName(loggerName)
				.properties(props)
				.build();
		
		assertThat(logMgr).isNotNull();
		
		Logger logger = Logger.getLogger(loggerName);
		
		assertThat(logger.getLevel()).isEqualTo(Level.INFO);
		
		assertThat(props.get("logging.BufferLogHandler.bufferLength")).isEqualTo("100");
		assertThat(props.get("logging.BufferLogHandler.level")).isEqualTo("INFO");
		
		Handler[] handlers = logger.getHandlers();
		
		assertThat(handlers).hasSize(3)
			.satisfiesExactlyInAnyOrder(
				handler -> assertThat(handler).isInstanceOf(ConsoleHandler.class),
				handler -> assertThat(handler).isInstanceOf(FileHandler.class),
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
		
		// Log a message
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
	}
}

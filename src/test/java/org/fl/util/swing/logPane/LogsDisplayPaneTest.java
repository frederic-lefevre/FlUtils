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

package org.fl.util.swing.logPane;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.BufferLogHandler;
import org.fl.util.FilterCounter;
import org.fl.util.RunningContext;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.fl.util.PlainLogFormatter;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

class LogsDisplayPaneTest {

	private static final String LOGGER_NAME = "org.fl.util.Test1";
	private static final String LOGGER_NAME2 = "org.fl.util.test2";
	
	@Test
	void testCreatLogsDisplayPane() throws JsonProcessingException {
		
		RunningContext rc = new RunningContext(LOGGER_NAME, URI.create("test1.properties"));
		
		assertThat(rc).isNotNull();
		
		Logger logger = Logger.getLogger(LOGGER_NAME);
		
		assertThat(logger).isNotNull();
		assertThat(logger.getLevel()).isEqualTo(Level.INFO);
		
		LogsDisplayPane logsDisplayPane = new LogsDisplayPane(rc);
		
		assertThat(logsDisplayPane).isNotNull();
		
		assertThat(logger.getHandlers()).hasSize(4)
			.satisfiesExactlyInAnyOrder(
				handler ->	assertThat(handler).isInstanceOf(ConsoleHandler.class),
				handler ->	assertThat(handler).isInstanceOf(FileHandler.class),
				handler ->	assertThat(handler).isInstanceOf(BufferLogHandler.class),
				handler -> { 
					assertThat(handler).isInstanceOf(TextAreaLogHandler.class);
					assertThat(handler.getEncoding()).isNull();
					assertThat(handler.getLevel()).isEqualTo(logger.getLevel());
					assertThat(handler.getFormatter()).isNotNull()
						.isInstanceOf(PlainLogFormatter.class);
					assertThat(handler.getFilter()).isNull();
					assertThat(handler.getErrorManager()).isNotNull();
				}
			);
	}
	
	@Test
	void nullLevelForApplicationLoggerShouldRaiseError() throws JsonProcessingException {
		
		LogRecordCounter rootLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		RunningContext rc = new RunningContext(LOGGER_NAME2, URI.create("test6.properties"));
		
		// 1 warning is logged
		assertThat(rootLogRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(rootLogRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		assertThat(rc).isNotNull();
		
		Logger logger = Logger.getLogger(LOGGER_NAME2);
		
		assertThat(logger).isNotNull();
		assertThat(logger.getLevel()).isNull();
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(LOGGER_NAME2));
		
		LogsDisplayPane logsDisplayPane = new LogsDisplayPane(rc);
		
		assertThat(logsDisplayPane).isNotNull();
		
		// 1 warning is logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		assertThat(logger.getHandlers()).singleElement()
			.satisfies(
				handler -> { 
					assertThat(handler).isInstanceOf(TextAreaLogHandler.class);
					assertThat(handler.getEncoding()).isNull();
					assertThat(handler.getLevel()).isEqualTo(Level.ALL);
					assertThat(handler.getFormatter()).isNotNull()
						.isInstanceOf(PlainLogFormatter.class);
					assertThat(handler.getFilter()).isNull();
					assertThat(handler.getErrorManager()).isNotNull();
				}
			);
	}
}

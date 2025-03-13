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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.FilterCounter;
import org.fl.util.RunningContext;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

class LogsDisplayPaneTest {

	private static final String LOGGER_NAME = "org.fl.util.test1";
	
	@Test
	void nullLevelForApplicationLoggerShouldRaiseError() throws JsonProcessingException {
		
		LogRecordCounter rootLogRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(""));
		
		RunningContext rc = new RunningContext(LOGGER_NAME, null, "test6.properties");
		
		// 1 warning is logged
		assertThat(rootLogRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(rootLogRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
		
		assertThat(rc).isNotNull();
		
		Logger logger = Logger.getLogger(LOGGER_NAME);
		
		assertThat(logger).isNotNull();
		assertThat(logger.getLevel()).isNull();
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(LOGGER_NAME));
		
		LogsDisplayPane logsDisplayPane = new LogsDisplayPane(rc);
		
		assertThat(logsDisplayPane).isNotNull();
		
		// 1 warning is logged
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(1);
	}
}

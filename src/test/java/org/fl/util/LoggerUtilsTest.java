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

import java.util.List;
import java.util.function.IntFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class LoggerUtilsTest {

	@Test
	void testNullLoggerNames() {
			
		assertThat(LoggerUtils.getChildLoggerNames(null)).isNotNull().isEmpty();
	}
	
	@Test
	void testNameSpaceWithNoLoggers() {
			
		assertThat(LoggerUtils.getChildLoggerNames("org.fl.name.space.with.no.logger")).isNotNull().isEmpty();
	}
	
	@Test
	void shouldGetAllLoggerNames() {
		
		List<String> loggerNames = LoggerUtils.getChildLoggerNames("");
		
		assertThat(loggerNames).isNotNull().isNotEmpty()
			.contains("")
			.anyMatch(loggerName -> loggerName.startsWith("org.junit.platform"))
			.anyMatch(loggerName -> loggerName.startsWith("org.junit.jupiter"));
	}
	
	@Test	
	void shouldGetSpecificLoggerNames() {
		
		String specificNameSpace = "org.fl.specific.name.space";
		
		IntFunction<Logger> specificLoggerSupplier = loggerNumber -> Logger.getLogger(specificNameSpace + "." + loggerNumber);
		
		int nbSpecificLogger = 10;
		List<Logger> specificLoggers = IntStream.rangeClosed(1, nbSpecificLogger).mapToObj(specificLoggerSupplier).collect(Collectors.toList());		
		assertThat(specificLoggers).isNotNull().hasSize(nbSpecificLogger);

		List<String> loggerNames = LoggerUtils.getChildLoggerNames(specificNameSpace);
		
		assertThat(loggerNames).isNotNull().hasSize(nbSpecificLogger)
			.containsAll(specificLoggers.stream().map(l -> l.getName()).collect(Collectors.toList()));
	}
	
	@Test	
	void shouldGetLoggerNames() {
		
		String specificNameSpace = "org.fl.specific.name.space";
		
		IntFunction<Logger> specificLoggerSupplier = loggerNumber -> Logger.getLogger(specificNameSpace + "." + loggerNumber);
		
		int nbSpecificLogger = 10;
		List<Logger> specificLoggers = IntStream.rangeClosed(1, nbSpecificLogger).mapToObj(specificLoggerSupplier).collect(Collectors.toList());		
		assertThat(specificLoggers).isNotNull().hasSize(nbSpecificLogger);

		List<String> loggerNames = LoggerUtils.getChildLoggerNames("org");
		
		assertThat(loggerNames).isNotNull().hasSizeGreaterThan(nbSpecificLogger)
			.anyMatch(loggerName -> loggerName.startsWith("org.junit.platform"))
			.anyMatch(loggerName -> loggerName.startsWith("org.junit.jupiter"))
			.containsAll(specificLoggers.stream().map(l -> l.getName()).collect(Collectors.toList()));
	}
}

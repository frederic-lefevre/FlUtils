/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

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
	
	@Test	
	void getLoggerLevelsOfNullThrowsNPE() {
		
		assertThatNullPointerException().isThrownBy(() -> LoggerUtils.getLoggerLevels(null));
	}
	
	@Test	
	void getMinimalLoggerLevels() throws JacksonException {
		
		Logger logger = Logger.getLogger(LoggerUtilsTest.class.getName() + ".test1");
		assertThat(logger.getLevel()).isNull();
		assertThat(logger.getHandlers()).isNotNull().isEmpty();
		JsonNode loggerLevelsJson = LoggerUtils.getLoggerLevels(logger);
		
		assertThat(loggerLevelsJson.get(LoggerUtils.LOG_LEVEL)).isNull();
		assertThat(loggerLevelsJson.get(LoggerUtils.HANDLERS)).isNotNull().isEmpty();
	}
	
	private static final String APPLICATION_NAME = "org.fl.util.Test1";
	private static final String CONSOLE_HANDLER_NAME = "java.util.logging.ConsoleHandler";
	
	@Test	
	void getLoggerLevels() throws JacksonException {
		
		RunningContext rc = new RunningContext(APPLICATION_NAME,
				"file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties");
		assertThat(rc.getName()).isNotNull().isEqualTo(APPLICATION_NAME);
		
		Logger logger = Logger.getLogger(APPLICATION_NAME);
		assertThat(logger.getLevel()).isNotNull().isEqualTo(Level.INFO);
		assertThat(logger.getHandlers()).isNotNull().hasSize(3);
		JsonNode loggerLevelsJson = LoggerUtils.getLoggerLevels(logger);
		
		String fileHandlerName = "java.util.logging.FileHandler";
		String bufferHandlerName = "org.fl.util.BufferLogHandler";
		String plainLogFormatterName = "org.fl.util.PlainLogFormatter";
		String simpleFormatterName = "java.util.logging.SimpleFormatter";
		assertThat(loggerLevelsJson.get(LoggerUtils.LOG_LEVEL).asString()).isEqualTo(Level.INFO.getName());
		assertThat(loggerLevelsJson.get(LoggerUtils.HANDLERS)).isNotNull().hasSize(3)
			.satisfiesExactlyInAnyOrder(
					jsonHandler -> { 
						assertThat(jsonHandler.get(LoggerUtils.HANDLER_NAME).asString()).isEqualTo(CONSOLE_HANDLER_NAME);
						assertThat(jsonHandler.get(LoggerUtils.HANDLER_LEVEL).asString()).isEqualTo(Level.INFO.getName());
						assertThat(jsonHandler.get(LoggerUtils.FORMATTER).asString()).isEqualTo(simpleFormatterName);
					},
					jsonHandler -> { 
						assertThat(jsonHandler.get(LoggerUtils.HANDLER_NAME).asString()).isEqualTo(fileHandlerName);
						assertThat(jsonHandler.get(LoggerUtils.HANDLER_LEVEL).asString()).isEqualTo(Level.INFO.getName());
						assertThat(jsonHandler.get(LoggerUtils.FORMATTER).asString()).isEqualTo(plainLogFormatterName);
					},
					jsonHandler -> { 
						assertThat(jsonHandler.get(LoggerUtils.HANDLER_NAME).asString()).isEqualTo(bufferHandlerName);
						assertThat(jsonHandler.get(LoggerUtils.HANDLER_LEVEL).asString()).isEqualTo(Level.INFO.getName());
						assertThat(jsonHandler.get(LoggerUtils.FORMATTER)).isNull();
						assertThat(jsonHandler.get(LoggerUtils.MEMORY_BUF_SZ).asInt()).isEqualTo(100);
					}
				);
	}
	
	@Test	
	void setLoggerLevelsOfNullThrowsIllegalArgument() {
		
		assertThatIllegalArgumentException().isThrownBy(() -> LoggerUtils.setLoggerLevels(null, JsonNodeFactory.instance.objectNode()))
			.withMessage("Logger parameter must not be null");
	}
	
	@Test	
	void setLoggerLevelsWithNullJsonThrowsIllegalArgument() {
		
		assertThatIllegalArgumentException().isThrownBy(() -> LoggerUtils.setLoggerLevels(Logger.getLogger(APPLICATION_NAME), null))
			.withMessage("Level json parameter must not be null");
	}
	
	@Test	
	void setLoggerLevelsWhenNoHanlder() throws JacksonException {
		
		Logger logger = Logger.getLogger(LoggerUtilsTest.class.getName() + ".test1");
		assertThat(logger.getLevel()).isNull();
		assertThat(logger.getHandlers()).isNotNull().isEmpty();
		
		ObjectNode jsonLevels = JsonNodeFactory.instance.objectNode();
		
		Level newLogLevel = Level.FINE;
		Level newHandlerLevel = Level.FINER;
		jsonLevels.put(LoggerUtils.LOG_LEVEL, newLogLevel.getName());
		
		ObjectNode jsonHandler = JsonNodeFactory.instance.objectNode();
		jsonHandler.put(LoggerUtils.HANDLER_NAME, CONSOLE_HANDLER_NAME);
		jsonHandler.put(LoggerUtils.HANDLER_LEVEL, newHandlerLevel.getName());
		
		ArrayNode handlersJson = JsonNodeFactory.instance.arrayNode();
		handlersJson.add(jsonHandler);
		
		jsonLevels.set(LoggerUtils.HANDLERS, handlersJson);
		
		boolean success = LoggerUtils.setLoggerLevels(logger, jsonLevels);
		
		assertThat(success).isTrue();
		
		assertThat(logger.getLevel()).isEqualTo(newLogLevel);
		
		assertThat(Arrays.stream(logger.getHandlers())
			.filter(handler -> handler.getClass().getName().equals(CONSOLE_HANDLER_NAME))
			.findFirst()).isEmpty();
	}
	
	@Test	
	void setLoggerAndHandlerLevels() throws JacksonException {
		
		RunningContext rc = new RunningContext(APPLICATION_NAME,
				"file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties");
		assertThat(rc.getName()).isNotNull().isEqualTo(APPLICATION_NAME);
		
		Logger logger = Logger.getLogger(APPLICATION_NAME);
		assertThat(logger.getLevel()).isEqualTo(Level.INFO);
		assertThat(logger.getHandlers()).isNotNull().hasSize(3)
			.anySatisfy(handler -> { 
				assertThat(handler.getClass().getName()).isEqualTo(CONSOLE_HANDLER_NAME); 
				assertThat(handler.getLevel()).isEqualTo(Level.INFO);
			});
		
		ObjectNode jsonLevels = JsonNodeFactory.instance.objectNode();
		
		Level newLogLevel = Level.FINE;
		Level newHandlerLevel = Level.FINER;
		jsonLevels.put(LoggerUtils.LOG_LEVEL, newLogLevel.getName());
		
		ObjectNode jsonHandler = JsonNodeFactory.instance.objectNode();
		jsonHandler.put(LoggerUtils.HANDLER_NAME, CONSOLE_HANDLER_NAME);
		jsonHandler.put(LoggerUtils.HANDLER_LEVEL, newHandlerLevel.getName());
		
		ArrayNode handlersJson = JsonNodeFactory.instance.arrayNode();
		handlersJson.add(jsonHandler);
		
		jsonLevels.set(LoggerUtils.HANDLERS, handlersJson);
		
		boolean success = LoggerUtils.setLoggerLevels(logger, jsonLevels);
		
		assertThat(success).isTrue();
		
		assertThat(logger.getLevel()).isEqualTo(newLogLevel);
		
		assertThat(Arrays.stream(logger.getHandlers())
			.filter(handler -> handler.getClass().getName().equals(CONSOLE_HANDLER_NAME))
			.findFirst())
			.isNotEmpty()
			.hasValueSatisfying(consoleHandler -> 
				assertThat(consoleHandler.getLevel()).isEqualTo(newHandlerLevel));
	}
	
	@Test	
	void getLevelFromHierarchyOfNullShouldThrowException() {
		assertThatIllegalArgumentException().isThrownBy(() -> LoggerUtils.getLevelFromHierarchy(null))
			.withMessage("Logger parameter must not be null");
	}
	
	@Test	
	void getLevelFromHierarchyRootLogger() {
		
		Logger rootLogger = Logger.getLogger("");
		assertThat(LoggerUtils.getLevelFromHierarchy(rootLogger).getLevel()).isEqualTo(rootLogger.getLevel());
	}
	
	@Test	
	void getLevelFromHierarchyOfLoggerWithLevelShouldReturnThisLevel() {
		
		Logger logger = Logger.getLogger("my.logger");
		logger.setLevel(Level.FINER);
		
		assertThat(LoggerUtils.getLevelFromHierarchy(logger).getLevel()).isEqualTo(Level.FINER);
	}
	
	@Test	
	void getLevelFromHierarchyShouldReturnParentLevel() {
		
		Logger loggerWithoutLevel = Logger.getLogger("my.logger.without.level");
		Logger logger = Logger.getLogger("my.logger");
		logger.setLevel(Level.FINER);
		
		assertThat(LoggerUtils.getLevelFromHierarchy(loggerWithoutLevel).getLevel()).isEqualTo(Level.FINER);
	}
	
	@Test	
	void noLevelInHierarchyShouldReturnRootLevel() {
		
		Logger loggerWithoutLevel = Logger.getLogger("a.logger.without.level.in.its.hierarchy");
		Level rootLoggerLevel = Logger.getLogger("").getLevel();

		
		assertThat(LoggerUtils.getLevelFromHierarchy(loggerWithoutLevel).getLevel()).isEqualTo(rootLoggerLevel);
	}
}

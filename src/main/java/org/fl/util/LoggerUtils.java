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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoggerUtils {

	private LoggerUtils() {
		// Hide constructor
	}
	
	public static List<String> getChildLoggerNames(String loggerNameSpace) {

		List<String> childLoggerNames = new ArrayList<>();
		if (loggerNameSpace != null) {
			Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
			while (loggerNames.hasMoreElements()) {
				String loggerName = loggerNames.nextElement();
				if ((loggerName != null) && (loggerName.startsWith(loggerNameSpace))) {
					childLoggerNames.add(loggerName);
				}
			}
		}
		return childLoggerNames;
	}
	
	// Get a JsonObject representing the levels of a logger (and all levels of its
	// Handlers)
	public static final String LOG_LEVEL = "logLevel";
	public static final String HANDLERS = "handlers";
	public static final String HANDLER_LEVEL = "handlerLevel";
	public static final String HANDLER_NAME = "handlerName";
	public static final String FORMATTER = "formatter";
	public static final String MEMORY_BUF_SZ = "memoryBufferSize";
	
	// Get the logger level and the levels, formatter of all handlers
	public static JsonNode getLoggerLevels(Logger log) {
	    
		ObjectNode levelsJson = JsonNodeFactory.instance.objectNode();

		Level lLevel = log.getLevel();
		if (lLevel != null) {
			levelsJson.put(LOG_LEVEL, lLevel.getName());
		}
		
		Handler[] handlers = log.getHandlers() ;
		if (handlers != null) {
			
			ArrayNode handlerJsonArray = JsonNodeFactory.instance.arrayNode();
			for (Handler handler : handlers) {
				
				ObjectNode handlerJson = JsonNodeFactory.instance.objectNode();
				handlerJson.put(HANDLER_NAME,  handler.getClass().getName());
				handlerJson.put(HANDLER_LEVEL, handler.getLevel().getName());
				Formatter formatter = handler.getFormatter() ;
				String formatterName ;
				if (formatter != null) {
					formatterName = formatter.getClass().getName();
					handlerJson.put(FORMATTER, 	formatterName);
				}
				
				if (handler instanceof BufferLogHandler) {
					handlerJson.put(MEMORY_BUF_SZ, ((BufferLogHandler) handler).getMaxMemoryLogRecord()) ;
				}
				handlerJsonArray.add(handlerJson);
			}
			levelsJson.set(HANDLERS, handlerJsonArray);
		}		
		return levelsJson ;	    
	}
	
    // Set the levels of logger and handlers
    public static boolean setLogsLevels(Logger log, JsonNode levelsJson) {
    	
		boolean success = true;
		if (levelsJson != null) {

			try {
				// Log level
				JsonNode logLevelElem = levelsJson.get(LOG_LEVEL);
				if (logLevelElem != null) {
					String levelString = logLevelElem.asText();
					try {
						Level newLevel = Level.parse(levelString);
						log.setLevel(newLevel);
					} catch (IllegalArgumentException e) {
						// parse level exception
						log.log(Level.WARNING, "Bad level in setLogsLevel json\n " + levelsJson.toString(), e);
						success = false;
					}
				}
	    		
				// Handlers levels
				JsonNode handlersLevelsElem = levelsJson.get(HANDLERS);
				if ((handlersLevelsElem != null) && (handlersLevelsElem.isArray())) {

					HashMap<String, Level> handlers = new HashMap<String, Level>();
					for (JsonNode handlerElem : handlersLevelsElem) {

						String handlerName = handlerElem.get(HANDLER_NAME).asText();
						String handlerLevel = handlerElem.get(HANDLER_LEVEL).asText();
						try {
							Level newLevel = Level.parse(handlerLevel);
							handlers.put(handlerName, newLevel);
						} catch (IllegalArgumentException e) {
							// parse level exception
							log.log(Level.WARNING, "Bad handler level in setLogsLevel for handler " + handlerName
									+ "\nin json: " + levelsJson.toString(), e);
							success = false;
						}
					}

					Handler[] logHandlers = log.getHandlers();
					for (Handler handler : logHandlers) {
						String handlerName = handler.getClass().getName();
						Level newHandlerLevel = handlers.get(handlerName);
						if (newHandlerLevel != null) {
							handler.setLevel(newHandlerLevel);
						} else {
							// handler not found
							success = false;
						}
					}
				}
			} catch (Exception e1) {
				log.log(Level.WARNING, "Exception in setLogsLevel json\n " + levelsJson.toString(), e1);
				success = false;
			}
		} else {
			// no input
			success = false;
		}
		return success;

	}
    
	public static void flushAllHandlers(Logger log) {

		if (log != null) {
			Handler[] handlers = log.getHandlers();
			if (handlers != null) {
				for (Handler handler : handlers) {
					handler.flush();
				}
			}
		}
	}

}

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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoggerManager {

	private static final String DEFAULT_LOG_NAME = "org.fl";
	private static final String DEFAULT_LOG_FILE_DIR = "log";
	private static final int DEFAULT_LOG_FILE_LENGTH = 8000000;
	private static final int DEFAULT_LOG_FILE_NUMBER = 3;

	// associated Logger
	private final Logger log;

	// log file pattern and number
	private String logFilePattern;

	// formatter
	private Formatter formatter;

	// in memory logging handler
	private BufferLogHandler bufferLogHandler;

	private AdvancedProperties properties;

	private LoggerManager() {
		log = null;
	}

    
    /**
     * Init a logger
     * @param logName : name of the Logger
     * @param rootDir : a root directory for logging. The logging directory in the property file will be interpreted as relative to the root directory. 
     *        So the property logging.directory.relative.name will be used
     * @param props : a set of properties
     * @param customHandler : a custom handler (could be a bluemix cloudant handler for instance, or whatever)
     */
    private LoggerManager(String logName, String rootDir, AdvancedProperties props, Handler customHandler) {

    	// get or create the logger (it may already exist, with all its handlers set, in the case of a J2EE container)
   		log = Logger.getLogger(logName);
   		
    	if (props == null) {
    		properties = new AdvancedProperties(log);
    	} else {
    		properties = props;
    	}
    
		try {
			// if the logger was already existing (due to a previous deployement)
			removeAllHandlersExceptConsole() ;
		} catch (SecurityException e) {
			System.out.println("Security exception removing handlers in LoggerManager ") ;
			e.printStackTrace() ;
		} catch (Exception e) {
			System.out.println("Exception removing handlers in LoggerManager ") ;
			e.printStackTrace();
		}
		
		try {
			initHandlers(rootDir, customHandler);
		} catch (SecurityException e) {
			System.out.println("Security exception in handler intialisation, LoggerManager ") ;
			e.printStackTrace() ;
		} catch (IOException e) {
			System.out.println("IOException in handler intialisation, LoggerManager ") ;
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception in handler intialisation, LoggerManager ") ;
			e.printStackTrace();
		}
    }
    
    public static Builder builder() {
    	return new Builder();
    }
    
    public static class Builder {
    	
    	private String logName;
    	private String rootDir;
    	private AdvancedProperties props;
    	private Handler customHandler;
    	
    	private Builder() {
    		logName = DEFAULT_LOG_NAME;
    		rootDir = null;
    		props = null;
    		customHandler = null;
    	}
    	
    	public Builder logName(String logName) {
    		this.logName = logName;
    		return this;
    	}
    	
    	public Builder rootDir(String rootDir) {
    		this.rootDir = rootDir;
    		return this;
    	}
    	
    	public Builder properties(AdvancedProperties props) {
    		this.props = props;
    		return this;
    	}
    	
    	public Builder customHandler(Handler handler) {
    		this.customHandler = handler;
    		return this;
    	}
    	
    	public LoggerManager build() {
    		return new LoggerManager(logName, rootDir, props, customHandler);
    	}
    }
    
    
    private void initHandlers(String rootDir, Handler customHandler) throws SecurityException, IOException {
    	
		// Set custom format for SimpleFormatter
		String customFormat = properties.getProperty("logging.simpleLogFormatter.format");
		if ((customFormat != null) && (!customFormat.isEmpty())) {
			System.setProperty("java.util.logging.SimpleFormatter.format", customFormat);
		}

		// Set the formatter
		String formatterName = properties.getProperty("logging.formatter");
		if (formatterName == null) {
			formatter = new SimpleFormatter();
		} else if (formatterName.equals(JsonLogFormatter.class.getName())) {
			formatter = new JsonLogFormatter();
		} else if (formatterName.equals(PlainLogFormatter.class.getName())) {
			formatter = new PlainLogFormatter();
		} else {
			System.out.println("Unknown log formatter class (logging.formatter property): " + formatterName);
			formatter = new SimpleFormatter();
		}

		int logfileLength = properties.getInt("logging.logfile.length", DEFAULT_LOG_FILE_LENGTH);
		int logfileNumber = properties.getInt("logging.logfile.number", DEFAULT_LOG_FILE_NUMBER);
		
		String logDirName;
		if ((rootDir != null) && (!rootDir.isEmpty())) {
			logDirName = rootDir + properties.getProperty("logging.directory.relative.name", DEFAULT_LOG_FILE_DIR);
		} else {
			logDirName = properties.getProperty("logging.directory.name", DEFAULT_LOG_FILE_DIR);
		}

		// Root logger
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		if (rootLogger != null) {
			
			Level rootFileLevel = properties.getLevel("logging.root.file.level", null);
			if (rootFileLevel != null) {
				String rootLogFileName = properties.getProperty("logging.rootLogfile.name");
				if ((rootLogFileName != null) && (!rootLogFileName.isEmpty())) {
					String rootLogFile = logDirName + rootLogFileName;
					FileHandler rootFh = new FileHandler(rootLogFile, logfileLength, logfileNumber, true);
					rootFh.setFormatter(formatter);
					rootLogger.addHandler(rootFh);
					rootFh.setLevel(properties.getLevel("logging.root.file.level", Level.FINEST));
				}
			}
			Level rootConsoleLevel = properties.getLevel("logging.root.console.level", null);
			if (rootConsoleLevel != null) {
				ConsoleHandler chRoot = new ConsoleHandler();
				chRoot.setFormatter(formatter);
				rootLogger.addHandler(chRoot);
				chRoot.setLevel(properties.getLevel("logging.root.console.level", Level.FINEST));
			}
			rootLogger.setLevel(getHighestHandlerLevel(rootLogger));
		}
		
		// Console Handler : always have a console handler (level maybe set to OFF)
		configureConsoleHandler();
		
		log.setUseParentHandlers(false);
		
        // -----------------------------------
        // File handler		
		String logFileNamePattern = properties.getProperty("logging.logfile.name");
		if ((logFileNamePattern != null) && (!logFileNamePattern.isEmpty())) {
			// File handler requested

			logFilePattern = logDirName + logFileNamePattern;

			// verify that the directory exists. If not, create it.
			File logDir = new File(logDirName);
			if (!logDir.exists()) {
				logDir.mkdirs();
			}

			// encoding and level for file handler
			String encoding = properties.getProperty("logging.file.encode", Charset.defaultCharset().name());
			Level level = properties.getLevel("logging.file.level", Level.FINEST);

			FileHandler fh = new FileHandler(logFilePattern, logfileLength, logfileNumber, true);
			fh.setFormatter(formatter);
			fh.setEncoding(encoding);
			log.addHandler(fh);
			fh.setLevel(level);		
		}

		// Memory handler
		int bufferSize = properties.getInt("logging.BufferLogHandler.bufferLength", 0);
		if (bufferSize > 0) {

			bufferLogHandler = new BufferLogHandler("standard bufferLogHandler", bufferSize);
			bufferLogHandler.setLevel(properties.getLevel("logging.BufferLogHandler.level", Level.OFF));
			log.addHandler(bufferLogHandler);
		} else {
			bufferLogHandler = null;
		}

		// Custom Handler, if any
		addCustomHandler(customHandler);

		// Set the log level to the highest level of the handlers
		log.setLevel(getHighestHandlerLevel(log));
        
    }
    
    /**
     * Get the highest logging level of all logger handlers
     * @param logger
     * @return The highest logging level of all logger handlers
     */
	private Level getHighestHandlerLevel(Logger logger) {
		Handler handlers[] = logger.getHandlers();
		Level highestLevel = Level.OFF;
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i].getLevel().intValue() < highestLevel.intValue()) {
				highestLevel = handlers[i].getLevel();
			}
		}
		return highestLevel;
	}
    
	// Add a custom handler to the logger
	public void addCustomHandler(Handler customHandler) {

		// Custom Handler
		if (customHandler != null) {
			try {

				// Get custom handler class name to find associated properties
				String customHandlerName = customHandler.getClass().getSimpleName();

				// custom handler encoding and formatting
				String encoding = properties.getProperty("logging." + customHandlerName + ".encode",
						Charset.defaultCharset().name());
				customHandler.setFormatter(formatter);
				customHandler.setEncoding(encoding);

				// add the custom handler to the logger
				log.addHandler(customHandler);

				// Custom handler level
				customHandler.setLevel(properties.getLevel("logging." + customHandlerName + ".level", Level.OFF));

				if ((customHandler instanceof BufferLogHandler) && (bufferLogHandler != null)) {
					// if the custom handler has an (inherited) in-memory logging, there is no need
					// to have
					// the standard in-memory logging of this class, so suppress it if it was
					// enabled
					log.removeHandler(bufferLogHandler);
					bufferLogHandler.close();
					bufferLogHandler = null;
				}
			} catch (SecurityException | UnsupportedEncodingException e) {
				System.err.println("Unable to set encoding for the custom log handler: " + e);
			}
		}
	}
    
    /**
     * Get the log files
     * @return the log files
     */
	public File[] getLogFiles() {

		File[] logFiles = null;
		if (logFilePattern != null) {
			File logDir = (new File(logFilePattern)).getParentFile();
			if ((logDir != null) && (logDir.isDirectory())) {
				logFiles = logDir.listFiles(new logFilter());
			}
		}
		return logFiles;
	}

	private class logFilter implements FileFilter {

		public boolean accept(File pathname) {
			return (!pathname.getName().endsWith("lck"));
		}
	}

	// Get a JsonObject representing the levels of a logger (and all levels of its
	// Handlers)
	private static final String LOG_LEVEL = "logLevel";
	private static final String HANDLERS = "handlers";
	private static final String HANDLER_LEVEL = "handlerLevel";
	private static final String HANDLER_NAME = "handlerName";
	private static final String FORMATTER = "formatter";
	private static final String MEMORY_BUF_SZ = "memoryBufferSize";

	private static final String NO_FORMATTER = "No formatter";
	private static final String NO_MEMORY_LOGGING = " No in-memory logging";
	
	// Get the logger level and the levels, formatter of all handlers
	public JsonNode getLoggerLevels() {
	    
		ObjectNode levelsJson = JsonNodeFactory.instance.objectNode();

		Level lLevel = log.getLevel() ;
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
				if (formatter == null) {
					formatterName = NO_FORMATTER; 
				} else {
					formatterName = formatter.getClass().getName() ;
				}
				handlerJson.put(FORMATTER, 	formatterName);
				if (handler instanceof BufferLogHandler) {
					handlerJson.put(MEMORY_BUF_SZ, ((BufferLogHandler) handler).getMaxMemoryLogRecord()) ;
				} else {
					handlerJson.put(MEMORY_BUF_SZ, NO_MEMORY_LOGGING) ;
				}
				handlerJsonArray.add(handlerJson);
			}
			levelsJson.set(HANDLERS, handlerJsonArray);
		}
		
		return levelsJson ;	    
	}
	
    // Set the levels of logger and handlers
    public boolean setLogsLevels(JsonNode levelsJson) {
    	
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

	private void removeAllHandlersExceptConsole() {

		Handler[] handlers = log.getHandlers();
		if (handlers != null) {
			Stream.of(handlers)
			.filter(handler -> !(handler instanceof ConsoleHandler))
			.forEach(handler -> log.removeHandler(handler));
		}
	}

	private void configureConsoleHandler() throws SecurityException, UnsupportedEncodingException {
		
		ConsoleHandler consoleHandler = getConsoleHandler();
		if (consoleHandler == null) {
			consoleHandler = new ConsoleHandler();
			log.addHandler(consoleHandler);
		}
		
		consoleHandler.setFormatter(formatter);
		consoleHandler.setEncoding(properties.getProperty("logging.console.encode", Charset.defaultCharset().name()));
		consoleHandler.setLevel(properties.getLevel("logging.console.level", Level.WARNING));
	}
	
	private ConsoleHandler getConsoleHandler() {

		Handler[] handlers = log.getHandlers();
		if (handlers != null) {
			return Stream.of(handlers)
					.filter(handler -> (handler instanceof ConsoleHandler))
					.map(handler -> (ConsoleHandler)handler)
					.findFirst()
					.orElse(null);
		} else {
			return null;
		}	
	}
	
	// Get memory log (from a handler which has the largest in-memory buffer)
	public StringBuilder getMemoryLogs() {
		if (bufferLogHandler != null) {
			// standard in-memory handler exists, so it is the only one
			return bufferLogHandler.getMemoryLogs();
		} else {
			// search for in-memory handlers

			List<BufferLogHandler> inMemoryHandlers = getHandlersWithInMemoryLog();
			if ((inMemoryHandlers != null) && (!inMemoryHandlers.isEmpty())) {
				return inMemoryHandlers.get(0).getMemoryLogs();
			} else {
				return null;
			}
		}
	}

	// Delete all memory logs (i.e all logs stored in in-memory buffers of handlers)
	public String deleteMemoryLogs() {
		if (bufferLogHandler != null) {
			return bufferLogHandler.deleteMemoryLogs() + " log records removed from memory";
		} else {
			// search for in-memory handlers

			List<BufferLogHandler> inMemoryHandlers = getHandlersWithInMemoryLog();
			if (inMemoryHandlers != null) {

				StringBuilder msg = new StringBuilder();
				for (BufferLogHandler inMemoryHandler : inMemoryHandlers) {
					int nbRemove = inMemoryHandler.deleteMemoryLogs();
					String hName = inMemoryHandler.getName();
					String cName = inMemoryHandler.getClass().getSimpleName();
					msg.append(nbRemove).append(" log records removed from ").append(cName).append(" ").append(hName)
							.append("\n");
				}
				return msg.toString();
			} else {
				return "No in-memory log handlers found";
			}
		}
	}

	// Delete all memory logs (i.e all logs stored in in-memory buffers of handlers)
	// and resize thier buffer to a new size
	public String deleteMemoryLogsAndResize(int newSize) {

		StringBuilder msg = new StringBuilder(64);
		int nbRemove = 0;
		if (bufferLogHandler != null) {
			nbRemove = bufferLogHandler.deleteAndResizeMemoryLogs(newSize);
			msg.append(nbRemove).append(" log records removed from memory; ");
			msg.append("Maximum number of records resized to ").append(newSize);
		} else {
			// search for in-memory handlers

			List<BufferLogHandler> inMemoryHandlers = getHandlersWithInMemoryLog();
			if (inMemoryHandlers != null) {
				;
				for (BufferLogHandler inMemoryHandler : inMemoryHandlers) {
					nbRemove = inMemoryHandler.deleteAndResizeMemoryLogs(newSize);
					String hName = inMemoryHandler.getName();
					String cName = inMemoryHandler.getClass().getSimpleName();
					msg.append(nbRemove).append(" log records removed from ").append(cName).append(" ").append(hName)
							.append("\n");
				}
				msg.append("Maximum number of records resized to ").append(newSize);
			} else {
				msg.append("No in-memory log handlers found");
			}
		}
		return msg.toString();
	}

	// Get all handlers with in-memory logging, sorted by the size of their buffer
	private List<BufferLogHandler> getHandlersWithInMemoryLog() {

		List<BufferLogHandler> result = new ArrayList<>();
		Handler[] handlers = log.getHandlers();
		if (handlers != null) {
			for (Handler handler : handlers) {
				if (handler instanceof BufferLogHandler) {
					result.add((BufferLogHandler) handler);
				}
			}
			if (!result.isEmpty()) {
				InMemoryHandlerComparator inMemoryHandlerComparator = new InMemoryHandlerComparator();
				Collections.sort(result, inMemoryHandlerComparator);
			}
		}
		return result;
	}

	// BufferLogHandler comparator that compare the size of in-memory buffer
	private class InMemoryHandlerComparator implements Comparator<BufferLogHandler> {

		public int compare(BufferLogHandler blh1, BufferLogHandler blh2) {
			return (blh2.getMaxMemoryLogRecord() - blh1.getMaxMemoryLogRecord());
		}
	}
}

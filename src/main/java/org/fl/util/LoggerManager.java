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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import org.fl.util.swing.logPane.LogsDisplayPane;

import com.fasterxml.jackson.databind.JsonNode;

public class LoggerManager {

	private static final String DEFAULT_LOG_NAME = "org.fl";

	protected static final String LOGMANAGER_PROPERTY_FILE_PROPERTY = "logManager.properties.file";
	protected static final String BUFFERLOGHANDLER_BASE_PROPERTY = "logging.BufferLogHandler";
	protected static final String BUFFERLOGHANDLER_FOR_INIT_BASE_PROPERTY = "logging.BufferLogHandler";
	private static final String FILE_HANDLER_PATTERN_PROPERTY = "java.util.logging.FileHandler.pattern";
	
	// Root logger
	private static final Logger loggerManagertLogger = Logger.getLogger(LoggerManager.class.getName());
			
	// root Logger of the application
	private final Logger applicationRootLogger;

	// formatter
	private final String formatterName;

	// in memory logging handler
	private final BufferLogHandler bufferLogHandler;

	// in memory logging handler reserved for application initialization
	// Used before the GUI setup
	// Once the GUI is set up, the eventual log records will be displayed in the Log Display tab
	private final BufferLogHandler bufferLogHandlerForInit;
	
	private final AdvancedProperties properties;

	private final AdvancedProperties loggingProperties;
	
	// LoggerManager Builder
    public static Builder builder() {
    	return new Builder();
    }
    
    public static class Builder {
    	
    	private String applicationRootLoggerName;
    	private AdvancedProperties props;
    	
    	private Builder() {
    		applicationRootLoggerName = DEFAULT_LOG_NAME;
    		props = null;
    	}
    	
    	public Builder applicationRootLoggerName(String logName) {
    		this.applicationRootLoggerName = logName;
    		return this;
    	}
    	
    	public Builder properties(AdvancedProperties props) {
    		this.props = props;
    		return this;
    	}
    	
    	public LoggerManager build() {
    		return new LoggerManager(applicationRootLoggerName, props);
    	}
    }
  
    private LoggerManager(String logName, AdvancedProperties props) {
   		
    	if (props == null) {
    		properties = new AdvancedProperties(null);
    	} else {
    		properties = props;
    	}
    	
    	// Read java.util.logging.LogManager configuration
    	loggingProperties = initJavaUtilLogging(logName);
    		
    	// get or create the logger
   		applicationRootLogger = Logger.getLogger(logName);
   		
		// Set the formatter name for specific handlers
		formatterName = properties.getProperty("logging.formatter");
		
		bufferLogHandler = initBufferedLogHandler(BUFFERLOGHANDLER_BASE_PROPERTY, 0, Level.OFF);
		
		if (LogsDisplayPane.hasLogsDisplayPaneProperty(properties)) {
			bufferLogHandlerForInit = initBufferedLogHandler(BUFFERLOGHANDLER_FOR_INIT_BASE_PROPERTY, 50, Level.INFO);
		} else {
			bufferLogHandlerForInit = null;
		}

    }
    
    // Remapper for logging properties.
    // For all property key k, if the new property exists, take it, else keep the old one
    private Function<String, BiFunction<String,String,String>> loggingPrpertyRemapper = (k) -> ((o, n) -> n == null ? o : n);
    
    private AdvancedProperties initJavaUtilLogging(String applicationRootLoggerName) {
    	
    	AdvancedProperties loggingProperties = null;
    	String loggingPropertiesFileName = properties.getProperty(LOGMANAGER_PROPERTY_FILE_PROPERTY);
    	if ((loggingPropertiesFileName != null) && !loggingPropertiesFileName.isEmpty()) {
    				
    		URL loggingPropertiesUrl = RunningContext.class.getClassLoader().getResource(loggingPropertiesFileName);
    		
    		if (loggingPropertiesUrl != null) {
    			
    			loggingProperties = properties.getPropertiesFromFile(LOGMANAGER_PROPERTY_FILE_PROPERTY);
    			createFileHandlerPatternNonExistantFolders(loggingProperties);
   			
    			LogManager logManager = LogManager.getLogManager();
    			try (InputStream is = loggingPropertiesUrl.openStream()) {
    				logManager.reset();
    				LogManager.getLogManager().updateConfiguration(is, loggingPrpertyRemapper);
    			} catch (IOException e) {
    				loggerManagertLogger.log(Level.SEVERE, "IOException when LogManager loads logging properties file " + loggingPropertiesFileName, e);
				}
    			checkApplicationootLoggerConfig(loggingProperties, applicationRootLoggerName);
    		} else {
    			loggerManagertLogger.severe("Logging properties file not found " + loggingPropertiesFileName);
    		}
    	} else {
    		loggerManagertLogger.warning(LOGMANAGER_PROPERTY_FILE_PROPERTY + " property is not found in the application property file");
    	}
    	return loggingProperties;
    }
    
    private void checkApplicationootLoggerConfig(AdvancedProperties loggingProperties, String applicationRootLoggerName) {
    	
    	String applicationLogLevel = loggingProperties.getProperty(applicationRootLoggerName + ".level");
    	if (applicationLogLevel == null) {
    		loggerManagertLogger.warning("Application root logger level is not defined in the logging configuration properties");
    	}
    	
    	String applicationLogHandlers = loggingProperties.getProperty(applicationRootLoggerName + ".handlers");
    	if (applicationLogHandlers == null) {
    		loggerManagertLogger.warning("Application root logger handlers are not defined in the logging configuration properties");
    	}
    }
    
    private void createFileHandlerPatternNonExistantFolders(AdvancedProperties loggingProperties) {
    	
		String filePathPattern = loggingProperties.getProperty(FILE_HANDLER_PATTERN_PROPERTY);
		if ((filePathPattern != null) && !filePathPattern.startsWith("%t") && !filePathPattern.startsWith("%h")) {
			
			try {

				Path logFileFolderPath = Path.of(filePathPattern).toAbsolutePath().getParent();
				if (Files.notExists(logFileFolderPath)) {
					Files.createDirectories(logFileFolderPath);
				}
			} catch (InvalidPathException e) {
				loggerManagertLogger.log(Level.SEVERE, "InvalidPathException converting log file pattern " + filePathPattern, e);
			} catch (Exception e) {
				loggerManagertLogger.log(Level.SEVERE, "Exception creating unexistent parent folders in log file pattern " + filePathPattern, e);
			}
		}
    }
    
    private BufferLogHandler initBufferedLogHandler(String baseProperty, int defaultBufferSize, Level defaultLevel) {

		// Memory handler
    	BufferLogHandler bufferLogHandler = null;
		int bufferSize = properties.getInt(baseProperty + ".bufferLength", defaultBufferSize);
		if (bufferSize > 0) {

			bufferLogHandler = new BufferLogHandler(baseProperty, bufferSize);
			bufferLogHandler.setLevel(properties.getLevel(baseProperty  + ".level", defaultLevel));
			applicationRootLogger.addHandler(bufferLogHandler);
		} 
		return bufferLogHandler;
    }
    
	public Formatter getCommonFormatterInstance() {
		
		if (formatterName == null) {
			return  new SimpleFormatter();
		} else if (formatterName.equals(SimpleFormatter.class.getName())) {
			return  new SimpleFormatter();
		} else if (formatterName.equals(JsonLogFormatter.class.getName())) {
			return new JsonLogFormatter();
		} else if (formatterName.equals(PlainLogFormatter.class.getName())) {
			return new PlainLogFormatter();
		} else if (formatterName.equals(XMLFormatter.class.getName())) {
			return new XMLFormatter();
		} else {
			loggerManagertLogger.warning("Unknown log formatter class (logging.formatter property): " + formatterName);
			return new SimpleFormatter();
		}
	}
	
	public AdvancedProperties getLoggingProperties() {
		return loggingProperties;
	}

	public BufferLogHandler getBufferLogHandlerForInit() {
		return bufferLogHandlerForInit;
	}

	// Add a custom handler to the logger
	public void addCustomHandler(Handler customHandler) {

		// Custom Handler
		if (customHandler != null) {
			try {

				// Get custom handler class name to find associated properties
				String customHandlerName = customHandler.getClass().getSimpleName();

				// custom handler encoding and formatting
				String encoding = properties.getProperty("logging." + customHandlerName + ".encode",Charset.defaultCharset().name());
				customHandler.setFormatter(getCommonFormatterInstance());
				customHandler.setEncoding(encoding);

				// add the custom handler to the logger
				applicationRootLogger.addHandler(customHandler);

				// Custom handler level
				customHandler.setLevel(properties.getLevel("logging." + customHandlerName + ".level", Level.OFF));

			} catch (SecurityException | UnsupportedEncodingException e) {
				loggerManagertLogger.log(Level.SEVERE, "Unable to set encoding for the custom log handler", e);
			}
		}
	}

	// Get the logger level and the levels, formatter of all handlers
	public JsonNode getLoggerLevels() {
		return LoggerUtils.getLoggerLevels(applicationRootLogger);
	}
	
    // Set the levels of logger and handlers
    public boolean setLogsLevels(JsonNode levelsJson) {
    	return LoggerUtils.setLoggerLevels(applicationRootLogger, levelsJson);
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

			return searchAndDestroy(new StringBuilder(), (memBuf) -> memBuf.deleteMemoryLogs());
		}
	}

	// Delete all memory logs (i.e all logs stored in in-memory buffers of handlers)
	// and resize the buffer to a new size
	public String deleteMemoryLogsAndResize(int newSize) {

		StringBuilder msg = new StringBuilder(64);

		if (bufferLogHandler != null) {
			int nbRemove = bufferLogHandler.deleteAndResizeMemoryLogs(newSize);
			msg.append(nbRemove).append(" log records removed from memory; ");
			msg.append("Maximum number of records resized to ").append(newSize);
			return msg.toString();
		} else {
			// search for in-memory handlers

			return searchAndDestroy(msg, (memBuf) -> memBuf.deleteAndResizeMemoryLogs(newSize));
		}
	}

	private String searchAndDestroy(StringBuilder msg, Function<BufferLogHandler, Integer> deleteOp) {
		
		// search for in-memory handlers
		List<BufferLogHandler> inMemoryHandlers = getHandlersWithInMemoryLog();
		if (inMemoryHandlers != null) {

			for (BufferLogHandler inMemoryHandler : inMemoryHandlers) {
				int nbRemove = deleteOp.apply(inMemoryHandler);
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
	
	private static final InMemoryHandlerComparator inMemoryHandlerComparator = new InMemoryHandlerComparator();
	
	// Get all handlers with in-memory logging, sorted by the size of their buffer
	private List<BufferLogHandler> getHandlersWithInMemoryLog() {

		List<BufferLogHandler> result = new ArrayList<>();
		Handler[] handlers = applicationRootLogger.getHandlers();
		if (handlers != null) {
			for (Handler handler : handlers) {
				if (handler instanceof BufferLogHandler bufferLogHandler) {
					result.add(bufferLogHandler);
				}
			}
			if (!result.isEmpty()) {
				Collections.sort(result, inMemoryHandlerComparator);
			}
		}
		return result;
	}

	// BufferLogHandler comparator that compare the size of in-memory buffer
	private static class InMemoryHandlerComparator implements Comparator<BufferLogHandler> {

		public int compare(BufferLogHandler blh1, BufferLogHandler blh2) {
			return (blh2.getMaxMemoryLogRecord() - blh1.getMaxMemoryLogRecord());
		}
	}
}

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

package org.fl.util.response;

import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatusMessage {

    private static final Locale LOCALE_FOR_LOG =  Locale.US;
    private static final int TYPICAL_MESSAGE_TEXT_SIZE = 256;

    public enum MessageKind {
        ERROR,
        INFO,
        WARNING
    }

    // Ordered : "more general" categories first.
    public enum TargetClient {
        BUSINESS_CLIENT,
        TECHNICAL_CLIENT
    }
    
    private final MessageKeyInterface key;
    private final MessageKind kind;
    private final Object[] arguments;
    private final Throwable exception;

    private String loggedResponseId;

    public static StatusMessage newInfo(MessageKeyInterface key, Object... arguments) {
        return new StatusMessage(MessageKind.INFO, key, null, arguments);
    }

    public static StatusMessage newWarning(MessageKeyInterface key, Object... arguments) {
        return new StatusMessage(MessageKind.WARNING, key, null, arguments);
    }

    public static StatusMessage newError(MessageKeyInterface key, Object... arguments) {
        return new StatusMessage(MessageKind.ERROR, key, null, arguments);
    }

    public static StatusMessage newError(MessageKeyInterface key, Throwable exception, Object... arguments) {
        return new StatusMessage(MessageKind.ERROR, key, exception, arguments);
    }

    protected StatusMessage(MessageKind kind, MessageKeyInterface key, Throwable exception, Object... arguments) {
        this.key = key;
        this.kind = kind;
        this.exception = exception;
        this.arguments = arguments;
        this.loggedResponseId = null;
    }
    
    public String getMessageText(Locale locale) {
        String message ;
        if (key == null) {
            message = "The key of the StatusMessage is null";
        } else {
            message = key.getMessagesResourcesManager().getMessage(locale, key.toKeyName(), arguments);
        }
        if ((loggedResponseId != null) && (! loggedResponseId.isEmpty())) {
            return message + "\nmlResponseId=" + loggedResponseId;
        } else {
            return message;
        }
    }

    public MessageKind getKind() {
        return kind;
    }

    public String getMessageKey() {
        if (key == null) {
            return null;
        } else {
            return key.toKeyName();
        }
    }

    public Throwable getException() {
        return exception;
    }


    public TargetClient getTargetClient() {
        if (key == null) {
            return null;
        } else {
            return key.getTargetClient();
        }
    }

    // Log the StatusMessage if not already logged.
    // Return the responseId under which the StatusMessage has been logged if it was already logged
    //     else return null
    String logIfNotAlreadyLogged(String responseId, Set<String> correlatedMlResponseIds, Logger logger) {

        if (loggedResponseId == null) {
            loggedResponseId = responseId;
            logMessage(logger, correlatedMlResponseIds);
            return null;
        } else {
            return loggedResponseId;
        }
    }
    

    private void logMessage(Logger logger, Set<String> correlatedMlResponseIds) {

        String logMessage = getLogMessage(correlatedMlResponseIds);
        switch (getKind()) {
            case WARNING:
                if (exception != null) {
                    logger.log(Level.WARNING, logMessage, exception);
                } else {
                    logger.warning(logMessage);
                }
                break;
            case INFO:
                if (exception != null) {
                    logger.log(Level.INFO, logMessage, exception);
                } else {
                    logger.info(logMessage);
                }
                break;
            default:
                if (exception != null) {
                    logger.log(Level.SEVERE, logMessage, exception);
                } else {
                    logger.severe(logMessage);
                }
        }
    }
    
    private String getLogMessage(Set<String> correlatedMlResponseIds) {
        StringBuilder logMessage = new StringBuilder(TYPICAL_MESSAGE_TEXT_SIZE);
        logMessage.append(getMessageText(LOCALE_FOR_LOG));
        if (! correlatedMlResponseIds.isEmpty()) {
            logMessage.append("\nmlCorrelatedResponseIds=");
            correlatedMlResponseIds.forEach(responseId -> logMessage.append(responseId).append(" "));
        }
        return logMessage.toString();
    }
}

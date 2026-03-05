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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class RawResponse {

    protected boolean success;
    protected final Map<StatusMessage.TargetClient, List<StatusMessage>> statusMessages;
    private final String responseId;
    protected final Logger logger;

    private final Set<String> correlatedIds;
    
    // ----------
    // Constructors
    // ----------
    
	protected RawResponse(boolean success, StatusMessage statusMessage, Logger logger) {
        this.success = success;
        this.responseId = UUID.randomUUID().toString();
        this.correlatedIds = new HashSet<>();
        this.logger = logger;
        this.statusMessages = new EnumMap<>(StatusMessage.TargetClient.class);
        if (statusMessage != null) {
            addStatusMessage(statusMessage);
        }
	}

    protected RawResponse(RawResponse rawResponse) {
        this.success = rawResponse.success;
        this.responseId = UUID.randomUUID().toString();
        this.correlatedIds = rawResponse.correlatedIds;
        this.logger = rawResponse.logger;
        this.statusMessages = new EnumMap<>(StatusMessage.TargetClient.class);
        addStatusMessages(rawResponse.getStatusMessages());
    }
    
    // ----------
    // Getter, setter, supplier
    // ----------

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResponseId() {
        return responseId;
    }

    public List<StatusMessage> getStatusMessages() {

        List<StatusMessage> orderedMessages = new ArrayList<>();
        statusMessages.forEach((_,v) -> orderedMessages.addAll(v));
        return Collections.unmodifiableList(orderedMessages);
    }

    public String printStatusMessages(Locale locale) {
        StringBuilder msg = new StringBuilder();
        if (statusMessages != null) {
            statusMessages.values().forEach(messages -> messages.forEach(statusMessage -> msg.append(statusMessage.getMessageText(locale)).append("\n")));
        }
        return msg.toString();
    }
    
    // ----------
    // Fluent API
    // ----------

    public RawResponse addStatusMessage(StatusMessage statusMessage) {
        if (statusMessage != null) {
            StatusMessage.TargetClient targetClient = statusMessage.getTargetClient();
            statusMessages.computeIfAbsent(targetClient, _ -> new ArrayList<>());
            String previousLoggedResponseId = statusMessage.logIfNotAlreadyLogged(responseId, correlatedIds, logger);
            if (previousLoggedResponseId != null) {
                // the StatusMessage has already been logged under previousLoggedResponseId
                correlatedIds.add(previousLoggedResponseId);
            }
            statusMessages.get(targetClient).add(statusMessage);
        }
        return this;
    }

    public RawResponse addStatusMessages(List<StatusMessage> statusMessages) {
        if (statusMessages != null) {
            statusMessages.forEach(this::addStatusMessage);
        }
        return this;
    }

    public RawResponse addStatusMessages(StatusMessage ...statusMessages) {
        if (statusMessages != null) {
            addStatusMessages(Arrays.asList(statusMessages));
        }
        return this;
    }
}

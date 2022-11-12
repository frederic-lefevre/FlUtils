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
        statusMessages.forEach((k,v) -> orderedMessages.addAll(v));
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
            statusMessages.computeIfAbsent(targetClient, k -> new ArrayList<>());
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

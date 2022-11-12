package org.fl.util.response;

import java.util.Locale;

public interface MessageKeyInterface {

	String toKeyName();

	StatusMessage.TargetClient getTargetClient();

	MessagesResourcesManager getMessagesResourcesManager();

	default String getMessageText(Locale locale, Object... args) {
		return getMessagesResourcesManager().getMessage(locale, toKeyName(), args);
	}

	default String getMessageText(Object... args) {
		return getMessagesResourcesManager().getMessage(Locale.getDefault(), toKeyName(), args);
	}

}

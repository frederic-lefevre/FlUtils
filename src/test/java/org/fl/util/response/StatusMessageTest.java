package org.fl.util.response;

import java.util.Locale;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusMessageTest {

	private final static Logger LOGGER = Logger.getLogger(StatusMessageTest.class.getName());
	private static final MessagesResourcesManager messagesResourcesManager = new MessagesResourcesManager(TestKey.class, "i18n/test-messages", LOGGER);
	
    public enum TestKey implements  MessageKeyInterface {
        KEY_1("key1", StatusMessage.TargetClient.TECHNICAL_CLIENT),
        KEY_2("key2", StatusMessage.TargetClient.BUSINESS_CLIENT),
        KEY_3("key3", StatusMessage.TargetClient.BUSINESS_CLIENT),
        KEY_4("key4", StatusMessage.TargetClient.BUSINESS_CLIENT);

        private final String keyName;
        private final StatusMessage.TargetClient targetClient;

        TestKey(String keyName, StatusMessage.TargetClient targetClient){
            this.keyName = keyName;
            this.targetClient = targetClient;
        }

        public String toKeyName() {
            return keyName;
        }

        public StatusMessage.TargetClient getTargetClient() {
            return targetClient;
        }
       
        public MessagesResourcesManager getMessagesResourcesManager() { return messagesResourcesManager;}
    }

    @Test
    void testKeyWithoutParameter() {

    	Locale.setDefault(Locale.US);
        TestKey key = TestKey.KEY_2;
        String expectedMessage = "No parameter in this";
        String expectedMessage_fr = "Pas de paramètre pour celui-ci";
        StatusMessage statusMessage = StatusMessage.newError(key, "param1", "param2") ;
        assertThat(statusMessage).isNotNull();
        assertThat(key.toKeyName()).isEqualTo(statusMessage.getMessageKey());
        assertThat(statusMessage.getException()).isNull();
        assertThat(expectedMessage).isEqualTo(statusMessage.getMessageText(Locale.US));
        assertThat(expectedMessage_fr).isEqualTo(statusMessage.getMessageText(Locale.FRANCE));
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }

    @Test
    void testKeyWithParameter() {

    	Locale.setDefault(Locale.US);
        TestKey key = TestKey.KEY_1;
        String expectedMessage = "A message param1 with parameter param2";
        String expectedMessage_fr = "Un message param1 avec des paramètres param2";
        StatusMessage statusMessage = StatusMessage.newError(key, "param1", "param2");
        assertThat(statusMessage).isNotNull();
        assertThat(key.toKeyName()).isEqualTo(statusMessage.getMessageKey());
        assertThat(statusMessage.getException()).isNull();
        assertThat(expectedMessage).isEqualTo(statusMessage.getMessageText(Locale.US));
        assertThat(expectedMessage_fr).isEqualTo(statusMessage.getMessageText(Locale.FRANCE));
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }

    @Test
    void testKeyWithNullParameter() {

    	Locale.setDefault(Locale.US);
        TestKey key = TestKey.KEY_1;
        String expectedMessage = "A message null with parameter {1}";
        String expectedMessage_fr = "Un message null avec des paramètres {1}";
        StatusMessage statusMessage = StatusMessage.newError(key, (Object)null);
        assertThat(statusMessage).isNotNull();
        assertThat(key.toKeyName()).isEqualTo(statusMessage.getMessageKey());
        assertThat(statusMessage.getException()).isNull();
        assertThat(expectedMessage).isEqualTo(statusMessage.getMessageText(Locale.US));
        assertThat(expectedMessage_fr).isEqualTo(statusMessage.getMessageText(Locale.FRANCE));
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }

    @Test
    void testKeyWithNullException() {

    	Locale.setDefault(Locale.US);
        TestKey key = TestKey.KEY_1;
        String expectedMessage = "A message {0} with parameter {1}";
        String expectedMessage_fr = "Un message {0} avec des paramètres {1}";
        StatusMessage statusMessage = StatusMessage.newError(key, (Exception)null);
        assertThat(statusMessage).isNotNull();
        assertThat(key.toKeyName()).isEqualTo(statusMessage.getMessageKey());
        assertThat(statusMessage.getException()).isNull();
        assertThat(expectedMessage).isEqualTo(statusMessage.getMessageText(Locale.US));
        assertThat(expectedMessage_fr).isEqualTo(statusMessage.getMessageText(Locale.FRANCE));
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }


    @Test
    void testKeyFromEnum() {

    	Locale.setDefault(Locale.US);
        String expectedMessage = "A key from enum";
        String expectedMessage_fr = "Une clé d'un enum";
        StatusMessage statusMessage = StatusMessage.newError(TestKey.KEY_3, "param1", "param2") ;
        assertThat(statusMessage).isNotNull();
        assertThat(TestKey.KEY_3.toKeyName()).isEqualTo(statusMessage.getMessageKey());
        assertThat(statusMessage.getException()).isNull();
        assertThat(expectedMessage).isEqualTo(statusMessage.getMessageText(Locale.US));
        assertThat(expectedMessage_fr).isEqualTo(statusMessage.getMessageText(Locale.FRANCE));
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }

    @Test
    void testNullKey() {

    	Locale.setDefault(Locale.US);
        StatusMessage statusMessage = StatusMessage.newError( null) ;
        assertThat(statusMessage).isNotNull();
        assertThat(statusMessage.getMessageKey()).isNull();
        assertThat(statusMessage.getException()).isNull();
        assertThat(statusMessage.getMessageText(Locale.US)).isEqualTo("The key of the StatusMessage is null");
        assertThat(statusMessage.getMessageText(Locale.FRANCE)).isEqualTo("The key of the StatusMessage is null");
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }

    @Test
    void testNullParameters() {

    	Locale.setDefault(Locale.US);
        StatusMessage statusMessage = StatusMessage.newError(null, null, (Object)null) ;
        assertThat(statusMessage).isNotNull();
        assertThat(statusMessage.getMessageKey()).isNull();
        assertThat(statusMessage.getException()).isNull();
        assertThat(statusMessage.getMessageText(Locale.US)).isEqualTo("The key of the StatusMessage is null");
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }

    @Test
    void shouldCreateStatusMessageWithException() {

    	Locale.setDefault(Locale.US);
        final IllegalArgumentException an_exception = new IllegalArgumentException("An exception");

        String expectedMessage = "A message param1 with parameter param2";
        String expectedMessage_fr = "Un message param1 avec des paramètres param2";
        StatusMessage statusMessage = StatusMessage.newError(TestKey.KEY_1, an_exception, "param1", "param2") ;
        assertThat(statusMessage).isNotNull();
        assertThat(TestKey.KEY_1.toKeyName()).isEqualTo(statusMessage.getMessageKey());
        assertThat(statusMessage.getException())
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class)
                .isEqualTo(an_exception);
        assertThat(expectedMessage).isEqualTo(statusMessage.getMessageText(Locale.US));
        assertThat(expectedMessage_fr).isEqualTo(statusMessage.getMessageText(Locale.FRANCE));
        assertThat(statusMessage.getKind()).isEqualTo(StatusMessage.MessageKind.ERROR);
    }
}

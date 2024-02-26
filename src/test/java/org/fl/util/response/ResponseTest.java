/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.fl.util.LoggerCounter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ResponseTest {

	private final static Logger logger = Logger.getLogger(ResponseTest.class.getName());
	
    @Test
    void testNoStatusMessage() {

        Response<String> mlResponse = new Response<>(false, null, logger,null);
        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().isEmpty();
        assertThat(mlResponse.printStatusMessages(Locale.US)).isNotNull().isEmpty();
    }

    @Test
    void testAddStatusMessages() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        Response<Void> mlResponse = (new Response<>(false, null, noLog, (Void)null))
                .addStatusMessages(Arrays.stream(ResponseTest.TestMessageKey.values()).map(StatusMessage::newError).collect(Collectors.toList()))
                .addStatusMessage(StatusMessage.newWarning(ResponseTest.TestMessageKey.MALFORMED_AUTH_URL));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(ResponseTest.TestMessageKey.values().length + 1);
        
        assertThat(noLog.getErrorCount()).isEqualTo(ResponseTest.TestMessageKey.values().length + 1);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(ResponseTest.TestMessageKey.values().length);
        assertThat(noLog.getErrorCount(Level.WARNING)).isEqualTo(1);
    }

    @Test
    void testStatusMessageOrder() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        Response<Integer> mlResponse = new Response<>(false, null, noLog,0);
        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();

        Arrays.asList(TestMessageKey.values()).forEach(key -> mlResponse.addStatusMessage(StatusMessage.newError(key)));
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(TestMessageKey.values().length);

        List<StatusMessage> statusMessages = mlResponse.getStatusMessages();

        // Check messages are ordered, business client first, technical client last
        List<TestMessageKey> businessMessage = new ArrayList<>();
        List<TestMessageKey> technicalMessage = new ArrayList<>();
        Arrays.asList(TestMessageKey.values())
                .forEach(testMessageKey ->
                {
                    if (testMessageKey.targetClient.equals(StatusMessage.TargetClient.BUSINESS_CLIENT)) {
                        businessMessage.add(testMessageKey);
                    } else {
                        technicalMessage.add(testMessageKey);
                    }
                });

        // check order
        int currentIdx = 0;
        // business messages come first and in the same order as they were inserted
        for (TestMessageKey testMessageKey : businessMessage) {
            assertThat(testMessageKey.keyName).isEqualTo(statusMessages.get(currentIdx).getMessageKey());
            currentIdx++;
        }
        // then technical messages, in same order as they were inserted
        for (TestMessageKey testMessageKey : technicalMessage) {
            assertThat(testMessageKey.keyName).isEqualTo(statusMessages.get(currentIdx).getMessageKey());
            currentIdx++;
        }
        
        assertThat(noLog.getErrorCount()).isEqualTo(ResponseTest.TestMessageKey.values().length);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(ResponseTest.TestMessageKey.values().length);
    }

    @Test
    void basicTestForGoodResponse() {

        final String responseString = "the response";
        Response<String> goodResponse = goodStringResponse(responseString, logger);
        assertThat(goodResponse).isNotNull();
        assertThat(goodResponse.isSuccess()).isTrue();
        assertThat(goodResponse.getStatusMessages()).isNotNull().isEmpty();
        assertThat(goodResponse.getResponseObject()).isNotNull().isEqualTo(responseString);
    }

    @Test
    void basicTestForBadResponse() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	StatusMessage statusMessageForBadResponse = StatusMessage.newError(ResponseTest.TestMessageKey.MALFORMED_AUTH_URL);
        Response<String> badResponse = new Response<>(false, statusMessageForBadResponse, noLog, null);
        assertThat(badResponse).isNotNull();
        assertThat(badResponse.isSuccess()).isFalse();
        assertThat(badResponse.getStatusMessages()).isNotNull().singleElement().isEqualTo(statusMessageForBadResponse);
        assertThat(badResponse.getResponseObject()).isNull();
        
        assertThat(noLog.getErrorCount()).isEqualTo(1);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(1);
    }

    @Test
    void shouldProvideResponseId() {

        Response<String> aResponse = new Response<>(true, null, logger,null);
        Response<String> anotherResponse = new Response<>(true, null, logger,null);

        assertThat(aResponse.getResponseId()).isNotNull().isNotEqualTo(anotherResponse.getResponseId());
    }

    @Test
    void fluentApiGoodResponse() {

        final String WAS_NEVER_PROCESSED = "the response never processed";
        final String I_WAS_PROCESSED = "The response was processed";
        AtomicReference<String> shouldBeProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);

        final String firstString = "the response one";
        final String anotherString = "another string";
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);
        Response<List<String>> mlResponse = goodStringResponse(firstString, logger)
                .composeWithResponseIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return goodListOfStringResponse(logger, response, anotherString);
                })
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldNotBeAddedStatusMessage));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isTrue();
        assertThat(mlResponse.getStatusMessages()).isNotNull().isEmpty();
        assertThat(mlResponse.getResponseObject()).isNotNull().hasSize(2).contains(firstString, anotherString);
        assertThat(shouldBeProcessedString.get()).isEqualTo(I_WAS_PROCESSED);
    }

    @Test
    void fluentApiGoodResponseWithObjectCompose() {

        final String WAS_NEVER_PROCESSED = "the response never processed";
        final String I_WAS_PROCESSED = "The response was processed";
        AtomicReference<String> shouldBeProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);

        final String firstString = "the response one";
        final String anotherString = "another string";
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);
        Response<List<String>> mlResponse = goodStringResponse(firstString, logger)
                .composeIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return Arrays.asList(response, anotherString);
                })
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldNotBeAddedStatusMessage));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isTrue();
        assertThat(mlResponse.getStatusMessages()).isNotNull().isEmpty();
        assertThat(mlResponse.getResponseObject()).isNotNull().hasSize(2).contains(firstString, anotherString);
        assertThat(shouldBeProcessedString.get()).isEqualTo(I_WAS_PROCESSED);
    }

    @Test
    void fluentApiBadSecondResponse() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        final String firstString = "the response one";
        StatusMessage addedStatusMessageListElement = StatusMessage.newError(ResponseTest.TestMessageKey.UNREACHABLE_AUTH_URL);
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);

        StatusMessage statusMessageForBadListResponse = StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_API_KEY);
        Response<List<String>> badListOfStringResponse = new Response<>(false, statusMessageForBadListResponse, noLog, null);
        Response<List<String>> mlResponse = goodStringResponse(firstString, logger)
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldNotBeAddedStatusMessage))
                .composeWithResponseIfSuccess(response -> badListOfStringResponse)
                .addStatusMessagesWhenInError(() -> Collections.singletonList(addedStatusMessageListElement));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(2)
                .contains(statusMessageForBadListResponse, addedStatusMessageListElement)
                .doesNotContain(shouldNotBeAddedStatusMessage);
        assertThat(mlResponse.getResponseObject()).isNull();
        
        assertThat(noLog.getErrorCount()).isEqualTo(2);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(2);
    }

    @Test
    void fluentApiBadFirstResponse() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        final String WAS_NEVER_PROCESSED = "the response never processed";
        AtomicReference<String> neverProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);
        StatusMessage addedStatusMessageListElement = StatusMessage.newError(ResponseTest.TestMessageKey.UNREACHABLE_AUTH_URL);
        StatusMessage shouldBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);

        StatusMessage statusMessageForBadListResponse = StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_API_KEY);
        Response<List<String>> badListOfStringResponse = new Response<>(false, statusMessageForBadListResponse, noLog, null);
        Response<String> mlResponse = badListOfStringResponse
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldBeAddedStatusMessage))
                .composeWithResponseIfSuccess(response -> {
                    neverProcessedString.set("we should not pass here !");
                    return goodStringResponse(neverProcessedString.get(), logger);
                })
                .addStatusMessagesWhenInError(() -> Collections.singletonList(addedStatusMessageListElement));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(3)
                .contains(statusMessageForBadListResponse, addedStatusMessageListElement, shouldBeAddedStatusMessage);
        assertThat(mlResponse.getResponseObject()).isNull();
        assertThat(neverProcessedString.get()).isEqualTo(WAS_NEVER_PROCESSED);
        
        assertThat(noLog.getErrorCount()).isEqualTo(3);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(3);
    }

    @Test
    void fluentApiGoodResponseFinallyPutInError() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        final String WAS_NEVER_PROCESSED = "the response never processed";
        final String I_WAS_PROCESSED = "The response was processed";
        AtomicReference<String> shouldBeProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);

        final String firstString = "the response one";
        final String anotherString = "another string";
        final String notAdded = "I will not be added";
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);
        StatusMessage shouldBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_AUTH_URL_PATH);
        Response<List<String>> mlResponse = goodStringResponse(firstString, logger)
                .composeWithResponseIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return goodListOfStringResponse(noLog, response, anotherString);
                })
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldNotBeAddedStatusMessage))
                .putInErrorWithStatusMessage(() -> shouldBeAddedStatusMessage)
                .applyIfSuccess(mlListOfStringResponse -> fail("No, I should not have succeeded !"));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().singleElement().isEqualTo(shouldBeAddedStatusMessage);

        // Response object is unchanged, not nullify
        assertThat(mlResponse.getResponseObject()).isNotNull().hasSize(2).contains(firstString, anotherString).doesNotContain(notAdded);

        assertThat(shouldBeProcessedString.get()).isEqualTo(I_WAS_PROCESSED);

        // Now that should nullify object response
        Response<List<String>> mlResponse2 = mlResponse.composeWithResponseIfSuccess(listOfString -> {
            listOfString.add(notAdded);
            return goodListOfStringResponse(logger, listOfString.toArray(new String[0]));
        });

        assertThat(mlResponse2).isNotNull();
        assertThat(mlResponse2.isSuccess()).isFalse();
        assertThat(mlResponse2.getStatusMessages()).isNotNull().singleElement().isEqualTo(shouldBeAddedStatusMessage);
        assertThat(mlResponse2.getResponseObject()).isNull();
        
        assertThat(noLog.getErrorCount()).isEqualTo(1);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(1);
    }

    @Test
    void fluentApiGoodResponseFinallyPutInErrorWithSeveralMessages() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        final String WAS_NEVER_PROCESSED = "the response never processed";
        final String I_WAS_PROCESSED = "The response was processed";
        AtomicReference<String> shouldBeProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);

        final String firstString = "the response one";
        final String anotherString = "another string";
        final String notAdded = "I will not be added";
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);
        List<StatusMessage> shouldBeAddedStatusMessages = Arrays.asList(
                StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_AUTH_URL_PATH),
                StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_API_KEY));
        Response<List<String>> mlResponse = goodStringResponse(firstString, logger)
                .composeWithResponseIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return goodListOfStringResponse(noLog, response, anotherString);
                })
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldNotBeAddedStatusMessage))
                .putInErrorWithStatusMessages(() -> shouldBeAddedStatusMessages)
                .applyIfSuccess(mlListOfStringResponse -> fail("No, I should not have succeeded !"));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(2).allMatch(shouldBeAddedStatusMessages::contains);

        // Response object is unchanged, not nullify
        assertThat(mlResponse.getResponseObject()).isNotNull().hasSize(2).contains(firstString, anotherString).doesNotContain(notAdded);

        assertThat(shouldBeProcessedString.get()).isEqualTo(I_WAS_PROCESSED);

        assertThat(noLog.getErrorCount()).isEqualTo(2);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(2);
    }

    @Test
    void fluentApiComposeWhenInError() {

    	LoggerCounter noLog = LoggerCounter.getLogger();
    	
        String addToResponseWhenInError = "added by composeWhenInError";
        List<String> listOfString = new ArrayList<>();

        Response<List<String>> goodResponse = goodListOfStringResponse(logger)
                .composeWithResponseOnError(response -> {
                    listOfString.add(addToResponseWhenInError);
                    return response;
                });

        assertThat(goodResponse).isNotNull();
        assertThat(goodResponse.isSuccess()).isTrue();
        assertThat(goodResponse.getResponseObject()).isNotNull();
        assertThat(listOfString).isEmpty();

        Response<List<String>> badResponse = badListOfStringResponse(noLog)
                .composeWithResponseOnError(response -> {
                    listOfString.add(addToResponseWhenInError);
                    return response;
                });

        assertThat(badResponse).isNotNull();
        assertThat(badResponse.isSuccess()).isFalse();
        assertThat(badResponse.getResponseObject()).isNull();
        assertThat(listOfString).contains(addToResponseWhenInError);
        
        assertThat(noLog.getErrorCount()).isEqualTo(1);
        assertThat(noLog.getErrorCount(Level.SEVERE)).isEqualTo(1);
    }

    private Response<String> goodStringResponse(String response, Logger l) {
        return new Response<>(true, null, l, response);
    }

    private Response<List<String>> goodListOfStringResponse(Logger l, String... responses) {
        return new Response<>(true, null, l, Arrays.asList(responses));
    }

    private Response<List<String>> badListOfStringResponse(Logger l) {
        return new Response<>(false, StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_API_KEY), l, null);
    }


    public enum TestMessageKey implements MessageKeyInterface {

        AUTHENTICATION_SERVICE_EXCEPTION("AuthenticationServiceException", StatusMessage.TargetClient.BUSINESS_CLIENT),
        TOKEN_SERIALIZATION_FAILURE("TokenSerializationFailure", StatusMessage.TargetClient.TECHNICAL_CLIENT),
        UNREACHABLE_AUTH_URL("UnreachableAuthUrl", StatusMessage.TargetClient.TECHNICAL_CLIENT),
        WRONG_API_KEY("WrongApiKey", StatusMessage.TargetClient.TECHNICAL_CLIENT),
        WRONG_AUTH_URL_PATH("WrongAuthUrlPath", StatusMessage.TargetClient.BUSINESS_CLIENT),
        WRONG_AUTH_URL_HOST("WrongAuthUrlHost", StatusMessage.TargetClient.TECHNICAL_CLIENT),
        MALFORMED_AUTH_URL("MalformedAuthUrl", StatusMessage.TargetClient.BUSINESS_CLIENT),
        AUTHENTICATION_SERVICE_RESPONSE_EXCEPTION("AuthenticationServiceResponseException", StatusMessage.TargetClient.BUSINESS_CLIENT),
        AUTHENTICATION_SERVICE_OTHER_EXCEPTION("AuthenticationServiceOtherException", StatusMessage.TargetClient.TECHNICAL_CLIENT);

        private final String keyName;
        private final StatusMessage.TargetClient targetClient;

        TestMessageKey(String keyName, StatusMessage.TargetClient targetClient){
            this.keyName = keyName;
            this.targetClient = targetClient;
        }

        public String toKeyName() {
            return keyName;
        }

        public StatusMessage.TargetClient getTargetClient() {
            return targetClient;
        }

        private static final LoggerCounter noLog = LoggerCounter.getLogger();
        private static final MessagesResourcesManager messagesResourcesManager = new MessagesResourcesManager(TestMessageKey.class, "i18n/test-messages", noLog);
        public MessagesResourcesManager getMessagesResourcesManager() { return messagesResourcesManager;}
    }
}

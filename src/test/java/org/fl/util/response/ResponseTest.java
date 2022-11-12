package org.fl.util.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

        Response<Void> mlResponse = (new Response<>(false, null, logger, (Void)null))
                .addStatusMessages(Arrays.stream(ResponseTest.TestMessageKey.values()).map(StatusMessage::newError).collect(Collectors.toList()))
                .addStatusMessage(StatusMessage.newWarning(ResponseTest.TestMessageKey.MALFORMED_AUTH_URL));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(ResponseTest.TestMessageKey.values().length + 1);
    }

    @Test
    void testStatusMessageOrder() {

        Response<Integer> mlResponse = new Response<>(false, null, logger,0);
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
    }

    @Test
    void basicTestForGoodResponse() {

        final String responseString = "the response";
        Response<String> goodResponse = goodStringResponse(responseString);
        assertThat(goodResponse).isNotNull();
        assertThat(goodResponse.isSuccess()).isTrue();
        assertThat(goodResponse.getStatusMessages()).isNotNull().isEmpty();
        assertThat(goodResponse.getResponseObject()).isNotNull().isEqualTo(responseString);
    }

    @Test
    void basicTestForBadResponse() {

        Response<String> badResponse = badStringResponse();
        assertThat(badResponse).isNotNull();
        assertThat(badResponse.isSuccess()).isFalse();
        assertThat(badResponse.getStatusMessages()).isNotNull().singleElement().isEqualTo(statusMessageForBadResponse);
        assertThat(badResponse.getResponseObject()).isNull();
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
        Response<List<String>> mlResponse = goodStringResponse(firstString)
                .composeWithResponseIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return goodListOfStringResponse(response, anotherString);
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
        Response<List<String>> mlResponse = goodStringResponse(firstString)
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

        final String firstString = "the response one";
        StatusMessage addedStatusMessageListElement = StatusMessage.newError(ResponseTest.TestMessageKey.UNREACHABLE_AUTH_URL);
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);

        Response<List<String>> mlResponse = goodStringResponse(firstString)
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldNotBeAddedStatusMessage))
                .composeWithResponseIfSuccess(response -> badListOfStringResponse())
                .addStatusMessagesWhenInError(() -> Collections.singletonList(addedStatusMessageListElement));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(2)
                .contains(statusMessageForBadListResponse, addedStatusMessageListElement)
                .doesNotContain(statusMessageForBadResponse, shouldNotBeAddedStatusMessage);
        assertThat(mlResponse.getResponseObject()).isNull();
    }

    @Test
    void fluentApiBadFirstResponse() {

        final String WAS_NEVER_PROCESSED = "the response never processed";
        AtomicReference<String> neverProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);
        StatusMessage addedStatusMessageListElement = StatusMessage.newError(ResponseTest.TestMessageKey.UNREACHABLE_AUTH_URL);
        StatusMessage shouldBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);

        Response<String> mlResponse = badListOfStringResponse()
                .addStatusMessagesWhenInError(() -> Collections.singletonList(shouldBeAddedStatusMessage))
                .composeWithResponseIfSuccess(response -> {
                    neverProcessedString.set("we should not pass here !");
                    return goodStringResponse(neverProcessedString.get());
                })
                .addStatusMessagesWhenInError(() -> Collections.singletonList(addedStatusMessageListElement));

        assertThat(mlResponse).isNotNull();
        assertThat(mlResponse.isSuccess()).isFalse();
        assertThat(mlResponse.getStatusMessages()).isNotNull().hasSize(3)
                .contains(statusMessageForBadListResponse, addedStatusMessageListElement, shouldBeAddedStatusMessage)
                .doesNotContain(statusMessageForBadResponse);
        assertThat(mlResponse.getResponseObject()).isNull();
        assertThat(neverProcessedString.get()).isEqualTo(WAS_NEVER_PROCESSED);
    }

    @Test
    void fluentApiGoodResponseFinallyPutInError() {

        final String WAS_NEVER_PROCESSED = "the response never processed";
        final String I_WAS_PROCESSED = "The response was processed";
        AtomicReference<String> shouldBeProcessedString = new AtomicReference<>(WAS_NEVER_PROCESSED);

        final String firstString = "the response one";
        final String anotherString = "another string";
        final String notAdded = "I will not be added";
        StatusMessage shouldNotBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.AUTHENTICATION_SERVICE_EXCEPTION);
        StatusMessage shouldBeAddedStatusMessage = StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_AUTH_URL_PATH);
        Response<List<String>> mlResponse = goodStringResponse(firstString)
                .composeWithResponseIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return goodListOfStringResponse(response, anotherString);
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
            return goodListOfStringResponse(listOfString.toArray(new String[0]));
        });

        assertThat(mlResponse2).isNotNull();
        assertThat(mlResponse2.isSuccess()).isFalse();
        assertThat(mlResponse2.getStatusMessages()).isNotNull().singleElement().isEqualTo(shouldBeAddedStatusMessage);
        assertThat(mlResponse2.getResponseObject()).isNull();
    }

    @Test
    void fluentApiGoodResponseFinallyPutInErrorWithSeveralMessages() {

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
        Response<List<String>> mlResponse = goodStringResponse(firstString)
                .composeWithResponseIfSuccess(response -> {
                    shouldBeProcessedString.set(I_WAS_PROCESSED);
                    return goodListOfStringResponse(response, anotherString);
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

    }

    @Test
    void fluentApiComposeWhenInError() {

        String addToResponseWhenInError = "added by composeWhenInError";
        List<String> listOfString = new ArrayList<>();

        Response<List<String>> goodResponse = goodListOfStringResponse()
                .composeWithResponseOnError(response -> {
                    listOfString.add(addToResponseWhenInError);
                    return response;
                });

        assertThat(goodResponse).isNotNull();
        assertThat(goodResponse.isSuccess()).isTrue();
        assertThat(goodResponse.getResponseObject()).isNotNull();
        assertThat(listOfString).isEmpty();

        Response<List<String>> badResponse = badListOfStringResponse()
                .composeWithResponseOnError(response -> {
                    listOfString.add(addToResponseWhenInError);
                    return response;
                });

        assertThat(badResponse).isNotNull();
        assertThat(badResponse.isSuccess()).isFalse();
        assertThat(badResponse.getResponseObject()).isNull();
        assertThat(listOfString).contains(addToResponseWhenInError);
    }

    private Response<String> goodStringResponse(String response) {
        return new Response<>(true, null, logger, response);
    }

    private static final StatusMessage statusMessageForBadResponse = StatusMessage.newError(ResponseTest.TestMessageKey.MALFORMED_AUTH_URL);
    private Response<String> badStringResponse() {
        return new Response<>(false, statusMessageForBadResponse, logger, null);
    }

    private Response<List<String>> goodListOfStringResponse(String... responses) {
        return new Response<>(true, null, logger, Arrays.asList(responses));
    }

    private static final StatusMessage statusMessageForBadListResponse = StatusMessage.newError(ResponseTest.TestMessageKey.WRONG_API_KEY);
    private Response<List<String>> badListOfStringResponse() {
        return new Response<>(false, statusMessageForBadListResponse, logger,null);
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

        private static final MessagesResourcesManager messagesResourcesManager = new MessagesResourcesManager(TestMessageKey.class, "i18n/test-messages", logger);
        public MessagesResourcesManager getMessagesResourcesManager() { return messagesResourcesManager;}
    }
}

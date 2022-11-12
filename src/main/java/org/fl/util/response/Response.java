package org.fl.util.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Response<T> extends RawResponse {

	private final T responseObject;
	
    // ----------
    // Constructors
    // ----------

    public Response(Logger logger) {
        super(true, null, logger);
        this.responseObject = null;
    }

    public Response(Response<?> response, T responseObject) {
        super(response);
        this.responseObject = responseObject;
    }

    public Response(RawResponse rawResponse, T responseObject) {
        super(rawResponse);
        this.responseObject = responseObject;
    }

    public Response(boolean success, StatusMessage statusMessage, Logger logger, T responseObject) {
        super(success, statusMessage, logger);
        this.responseObject = responseObject;
    }

    public Response(Response<?> response, boolean success, T responseObject) {
        super(response);
        this.success = success;
        this.responseObject = responseObject;
    }

    // ----------
    // Getter
    // ----------
    
    public T getResponseObject() {
        return responseObject;
    }
    

    // ----------
    // Builder
    // ----------

    public static <U> Response<U> buildFromStatusMessages(List<StatusMessage> statusMessages, Logger logger) {
        if ((statusMessages == null) || (statusMessages.isEmpty())) {
            return new Response<>(true, null, logger, null);
        } else {
            return new Response<>(false, null, logger, (U)null).addStatusMessages(statusMessages);
        }
    }

    public static <U> Response<U> buildFromStatusMessages(Logger logger, StatusMessage ... statusMessages) {
        if ((statusMessages != null) && (statusMessages.length > 0)) {
            List<StatusMessage> statusMessageList = Arrays.stream(statusMessages).filter(Objects::nonNull).collect(Collectors.toList());
            return buildFromStatusMessages(statusMessageList, logger);
        } else {
            return new Response<>(true, null, logger, null);
        }
    }
    

    // Produce a Response from a list of Response :
    // StatusMessage are accumulated, success is true is all success of the list are true, response object is passed is parameter
    public static <U> Response<U> composeResponse(List<Response<?>> responses, Logger logger, U responseObject) {

        Response<U> composedResponse = new Response<>(true, null, logger, responseObject);
        if ((responses != null) && (! responses.isEmpty())) {
            responses.forEach(response -> {
                composedResponse.addStatusMessages(response.getStatusMessages());
                composedResponse.setSuccess(response.isSuccess() && composedResponse.isSuccess());
            });
        }
        return composedResponse;
    }
    
    

    // Produce a Response from a list of Response<U> (same type of response object) :
    // StatusMessage are accumulated, success is true is all success of the list are true, response object is the list of all success response object
    public static <U> Response<List<U>> composeResponse(List<Response<U>> responses, Logger logger) {

        List<U> responseObject = new ArrayList<>();
        Response<List<U>> composedResponse = new Response<>(true, null, logger, responseObject);
        if ((responses != null) && (! responses.isEmpty())) {
            responses.forEach(response -> {
                composedResponse.addStatusMessages(response.getStatusMessages());
                composedResponse.setSuccess(response.isSuccess() && composedResponse.isSuccess());
                if (response.getResponseObject() != null) {
                    responseObject.add(response.getResponseObject());
                }
            });
        }
        return composedResponse;
    }

    // ----------
    // Fluent API
    // ----------
    
    // If this Response is successful, then apply function (the input parameter is the responseObject of this Response, and it returns another Response)
    // Else return a new Response<U> with this Response (so success will be false and the status messages will be the same)
    public <U> Response<U> composeWithResponseIfSuccess(Function<T, Response<U>> chain) {
        if ((chain != null) && this.isSuccess()) {
            return chain.apply(this.getResponseObject()).addStatusMessages(this.getStatusMessages());
        } else {
            return new Response<>(this, null);
        }
    }

    // If this Response is successful, then apply function
    //  (the input parameter is the responseObject of this Response, and it returns another object, which will be enclosed in a new Response )
    // Else return a new Response<U> with this Response (so success will be false and the status messages will be the same)
    public <U> Response<U> composeIfSuccess(Function<T, U> chain) {
        if ((chain != null) && this.isSuccess()) {
            return new Response<>(this, chain.apply(this.getResponseObject()));
        } else {
            return new Response<>(this, null);
        }
    }


    // If this Response is successful, then apply function (the input parameter is this Response, and it returns this Response)
    // Else return this Response (so no change, success will be false and the status messages will be the same)
    public Response<T> applyIfSuccess(Consumer<Response<T>> chain) {
        if ((chain != null) && this.isSuccess()) {
            chain.accept(this);
        }
        return this;
    }

    // If this Response is on error, then apply function
    // Else return this
    public Response<T> composeWithResponseOnError(UnaryOperator<Response<T>> chain) {
        if ((chain != null) && !this.isSuccess()) {
            return chain.apply(this);
        } else {
            return this;
        }
    }
    
    public Response<T> addStatusMessagesWhenInError(Supplier<List<StatusMessage>> statusMessagesSupplier) {
        if (!this.isSuccess() && (statusMessagesSupplier != null)) {
            this.addStatusMessages(statusMessagesSupplier.get());
        }
        return this;
    }

    public Response<T> addStatusMessageWhenInError(Supplier<StatusMessage> statusMessageSupplier) {
        if (!this.isSuccess() && (statusMessageSupplier != null)) {
            this.addStatusMessage(statusMessageSupplier.get());
        }
        return this;
    }

    public Response<T> putInErrorWithStatusMessage(Supplier<StatusMessage> statusMessageSupplier) {
        if (statusMessageSupplier != null) {
            this.addStatusMessage(statusMessageSupplier.get());
        }
        this.success = false;
        return this;
    }

    public Response<T> putInErrorWithStatusMessages(Supplier<List<StatusMessage>> statusMessagesSupplier) {
        if (statusMessagesSupplier != null) {
            this.addStatusMessages(statusMessagesSupplier.get());
        }
        this.success = false;
        return this;
    }
    
    @Override
    public Response<T> addStatusMessages(List<StatusMessage> statusMessages) {
        super.addStatusMessages(statusMessages);
        return this;
    }

    @Override
    public Response<T> addStatusMessages(StatusMessage ... statusMessages) {
        super.addStatusMessages(Arrays.asList(statusMessages));
        return this;
    }

    @Override
    public Response<T> addStatusMessage(StatusMessage statusMessage) {
        super.addStatusMessage(statusMessage);
        return this;
    }
}

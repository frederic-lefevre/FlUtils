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

package org.fl.util.api;

import java.util.HashMap;
import java.util.logging.Logger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public class ApiErrorCodeBuilder {

	private final static String UNAUTHORIZED = "Unauthorized access to ";
	public final static int UNAUTHORIZED_CODE = 1000001;
	private final static String NO_LOGLEVEL = "No log level provided in the request ";
	public final static int NO_LOGLEVEL_CODE = 1000002;
	private final static String INVALID_LOGLEVEL = "Invalid log level provided in the request ";
	public final static int INVALID_LOGLEVEL_CODE = 1000003;
	private final static String EMPTY_RETURN_ERROR = "Empty return code error ";
	public final static int EMPTY_RETURN_ERROR_CODE = 1000004;
	private final static String ERROR_PROCESSING_JSON_BODY = "Processing error during Json body parsing for ";
	public final static int ERROR_PROCESSING_JSON_BODY_CODE = 1000005;
	private final static String NO_DATA_PROVIDED = "No data or invalid data provided for request ";
	public final static int NO_DATA_PROVIDED_CODE = 1000006;
	private final static String GENERAL_EXCEPTION = "Exception in API ";
	public final static int GENERAL_EXCEPTION_CODE = 1000007;
	private final static String COMPRESSION_EXCEPTION = "Exception when compressing Api return ";
	public final static int COMPRESSION_EXCEPTION_CODE = 1000008;
	private final static String INVALID_COMPRESSION_ELEM = "Invalid compression element in json ";
	public final static int INVALID_COMPRESSION_ELEM_CODE = 1000009;

	// Code from 1000000 are reserved
	public final static int MAX_USER_DEFINED_CODE = 999999;

	private static HashMap<Integer, ApiErrorCode> errorCodes;
	
	// Associate texts to numbers at class initialisation
	static {

		errorCodes = new HashMap<Integer, ApiErrorCode>();
		errorCodes.put(UNAUTHORIZED_CODE,
				new ApiErrorCode(UNAUTHORIZED_CODE, UNAUTHORIZED, ApiErrorCode.CORRECT_CALLER_CODE_ACTION));
		errorCodes.put(NO_LOGLEVEL_CODE,
				new ApiErrorCode(NO_LOGLEVEL_CODE, NO_LOGLEVEL, ApiErrorCode.CORRECT_CALLER_CODE_ACTION));
		errorCodes.put(INVALID_LOGLEVEL_CODE,
				new ApiErrorCode(INVALID_LOGLEVEL_CODE, INVALID_LOGLEVEL, ApiErrorCode.CORRECT_CALLER_CODE_ACTION));
		errorCodes.put(EMPTY_RETURN_ERROR_CODE,
				new ApiErrorCode(EMPTY_RETURN_ERROR_CODE, EMPTY_RETURN_ERROR, ApiErrorCode.RETRY_AND_REPORT_ACTION));
		errorCodes.put(ERROR_PROCESSING_JSON_BODY_CODE, new ApiErrorCode(ERROR_PROCESSING_JSON_BODY_CODE,
				ERROR_PROCESSING_JSON_BODY, ApiErrorCode.CORRECT_CALLER_CODE_ACTION));
		errorCodes.put(NO_DATA_PROVIDED_CODE,
				new ApiErrorCode(NO_DATA_PROVIDED_CODE, NO_DATA_PROVIDED, ApiErrorCode.CORRECT_CALLER_CODE_ACTION));
		errorCodes.put(GENERAL_EXCEPTION_CODE,
				new ApiErrorCode(GENERAL_EXCEPTION_CODE, GENERAL_EXCEPTION, ApiErrorCode.RETRY_AND_REPORT_ACTION));
		errorCodes.put(COMPRESSION_EXCEPTION_CODE, new ApiErrorCode(COMPRESSION_EXCEPTION_CODE, COMPRESSION_EXCEPTION,
				ApiErrorCode.RETRY_AND_REPORT_ACTION));
		errorCodes.put(INVALID_COMPRESSION_ELEM_CODE, new ApiErrorCode(INVALID_COMPRESSION_ELEM_CODE,
				INVALID_COMPRESSION_ELEM, ApiErrorCode.CORRECT_CALLER_CODE_ACTION));
	}

	public static JsonNode getErrorCode(int rc) {

		return getErrorCode(rc, null);
	}

	private final static String UNKNOWN_ERRCODE = "Unknown error code. Neeed to be registered";
	
	public static JsonNode getErrorCode(int rc, String msg) {
		 
		ApiErrorCode errorCode = errorCodes.get(rc) ;
		ObjectNode errCodeJson = JsonNodeFactory.instance.objectNode();
		errCodeJson.put(ApiJsonPropertyName.ERR_CODE, rc);
		
		String reason ;
		String action ;
		if (errorCode == null) {
			reason = UNKNOWN_ERRCODE ;
			action = ApiErrorCode.RETRY_AND_REPORT_ACTION ;
		} else {
			reason = errorCode.getReason() ;
			action = errorCode.getAction() ;
		}
		if (msg != null) {
			errCodeJson.put(ApiJsonPropertyName.REASON, reason + msg);
		} else {
			errCodeJson.put(ApiJsonPropertyName.REASON, reason);
		}
		errCodeJson.put(ApiJsonPropertyName.ERR_ACTION, action) ;
		return errCodeJson ;
	}
	
	// Register an error : the error code must inferior or equal to MAX_USER_DEFINED_CODE
	public static boolean registerApiError(int code, String msg, Logger log) {
		return registerApiError(code, msg, ApiErrorCode.UNDETERMINED_ACTION, log) ;
	}
	
	// Register an error : the error code must inferior or equal to
	// MAX_USER_DEFINED_CODE
	public static boolean registerApiError(int code, String msg, String action, Logger log) {

		boolean res;
		if (code <= MAX_USER_DEFINED_CODE) {
			ApiErrorCode s = errorCodes.get(code);

			if (s == null) {
				// no error registered yet for this code : ok to register this one
				errorCodes.put(code, new ApiErrorCode(code, msg, action));
				res = true;
			} else {
				// the error code is already registered
				log.severe("Trying to register already registered error code : " + code + "\n with message : " + msg);
				res = false;
			}

		} else {
			// the error code is in the reserved range
			res = false;
			log.severe("Trying to register error code : " + code + "\n in the reserved range with message : " + msg);
		}
		return res;
	}
}

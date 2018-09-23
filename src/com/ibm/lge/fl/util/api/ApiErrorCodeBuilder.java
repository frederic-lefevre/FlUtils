package com.ibm.lge.fl.util.api;

import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

public class ApiErrorCodeBuilder {

	private	  final static String UNAUTHORIZED					  	= "Unauthorized access to " ;
	public	  final static int	  UNAUTHORIZED_CODE				  	= 1000001 ;
	private   final static String NO_LOGLEVEL						= "No log level provided in the request " ;
	public 	  final static int 	  NO_LOGLEVEL_CODE 				  	= 1000002 ;
	private   final static String INVALID_LOGLEVEL				  	= "Invalid log level provided in the request " ;
	public 	  final static int 	  INVALID_LOGLEVEL_CODE 			= 1000003 ;
	private   final static String EMPTY_RETURN_ERROR				= "Empty return code error " ;
	public 	  final static int 	  EMPTY_RETURN_ERROR_CODE			= 1000004 ;
	private   final static String ERROR_PROCESSING_JSON_BODY 		= "Processing error during Json body parsing for " ;
	public 	  final static int 	  ERROR_PROCESSING_JSON_BODY_CODE   = 1000005 ;
	private   final static String NO_DATA_PROVIDED		 		  	= "No data or invalid data provided for request " ;
	public 	  final static int 	  NO_DATA_PROVIDED_CODE  		  	= 1000006 ;
	private   final static String GENERAL_EXCEPTION	 		  		= "Exception in API " ;
	public 	  final static int 	  GENERAL_EXCEPTION_CODE  	  		= 1000007 ;
	private   final static String COMPRESSION_EXCEPTION			  	= "Exception when compressing Api return " ;
	public 	  final static int 	  COMPRESSION_EXCEPTION_CODE		= 1000008 ;
	private   final static String INVALID_COMPRESSION_ELEM		  	= "Invalid compression element in json " ;
	public 	  final static int 	  INVALID_COMPRESSION_ELEM_CODE		= 1000009 ;

	// Code from 1000000 are reserved
	public final static int MAX_USER_DEFINED_CODE = 999999 ;
	
	private static HashMap<Integer,String> errorCodes ;
	
	// Associate texts to numbers at class initialisation
	static {
		
		errorCodes = new HashMap<Integer,String>() ;
		errorCodes.put(UNAUTHORIZED_CODE,				UNAUTHORIZED) ;
		errorCodes.put(NO_LOGLEVEL_CODE, 				NO_LOGLEVEL) ;
		errorCodes.put(INVALID_LOGLEVEL_CODE, 			INVALID_LOGLEVEL) ;
		errorCodes.put(EMPTY_RETURN_ERROR_CODE, 		EMPTY_RETURN_ERROR) ;
		errorCodes.put(ERROR_PROCESSING_JSON_BODY_CODE, ERROR_PROCESSING_JSON_BODY) ;
		errorCodes.put(NO_DATA_PROVIDED_CODE, 			NO_DATA_PROVIDED) ;
		errorCodes.put(GENERAL_EXCEPTION_CODE, 			GENERAL_EXCEPTION) ;
		errorCodes.put(COMPRESSION_EXCEPTION_CODE, 		COMPRESSION_EXCEPTION) ;
		errorCodes.put(INVALID_COMPRESSION_ELEM_CODE, 	INVALID_COMPRESSION_ELEM) ;
	}
	
	public static JsonObject getErrorCode(int rc) {
		
		return getErrorCode(rc, null) ;
	}
	
	public static JsonObject getErrorCode(int rc, String msg) {
		 
		JsonObject errCodeJson = new JsonObject() ;
		errCodeJson.addProperty(ApiJsonPropertyName.ERR_CODE, rc);
		
		if (msg != null) {
			errCodeJson.addProperty(ApiJsonPropertyName.REASON, errorCodes.get(rc) + msg);
		} else {
			errCodeJson.addProperty(ApiJsonPropertyName.REASON, errorCodes.get(rc));
		}
		return errCodeJson ;
	}
	
	// Register an error : the error code must inferior or equal to MAX_USER_DEFINED_CODE
	public static boolean registerApiError(int code, String msg, Logger log) {
		
		boolean res ;
		if (code <= MAX_USER_DEFINED_CODE) {
			String s = errorCodes.get(code) ;
			
			if (s == null) {
				// no error registered yet for this code : ok to register this one
				errorCodes.put(code, msg) ;
				res = true ;
			} else {
				// the error code is already registered
				log.severe("Trying to register already registered error code : " + code + "\n with message : " + msg);
				res = false ;
			}
					
		} else {
			// the error code is in the reserved range
			res = false ;
			log.severe("Trying to register error code : " + code + "\n in the reseverd range with message : " + msg);
		}
		return res ;
	}
}

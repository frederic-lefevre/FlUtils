package com.ibm.lge.fl.util.api;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.CompressionUtils;
import com.ibm.lge.fl.util.ExecutionDurations;
import com.ibm.lge.fl.util.json.JsonUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ApiReturn {

	// Limit trace size of api return when level is FINE
	private final static int TRACE_FINER_LIMIT = 8192 ;

	// Value for Json return
	public final static String OK        		 = "OK" ;
	public final static String KO        		 = "KO" ;

	private JsonObject 			apiReturnJson ;
	private Logger 				aLog ;
	private boolean 			onError ;
	private ExecutionDurations 	execDurations ;
	private JsonObject 			additionnalInfos ;
	private JsonObject 			subReturnCode ;
	private Charset				responseCharset ;
		
	public ApiReturn(ExecutionDurations ed, Charset rc, Logger l) {
		
		aLog 			 = l ;
		onError 		 = false ;
		apiReturnJson 	 = null ;
		execDurations	 = ed ;
		subReturnCode	 = null ;
		additionnalInfos = new JsonObject() ;
		responseCharset	 = rc ;
	}
	
	public void setDataReturn(JsonElement dataReturn) {
		
		apiReturnJson = new JsonObject() ;
		apiReturnJson.addProperty(ApiJsonPropertyName.OPERATION, OK);
		onError = false ;
		if (dataReturn != null) {
			apiReturnJson.add(ApiJsonPropertyName.DATA, dataReturn) ;
		}
	}
	
	public void setErrorReturn(int errCode) {
		
		apiReturnJson= new JsonObject() ;
		onError = true ;
		apiReturnJson.addProperty(ApiJsonPropertyName.OPERATION, KO);
		apiReturnJson.add(ApiJsonPropertyName.ERROR, ApiErrorCodeBuilder.getErrorCode(errCode, null)) ;
	}
	
	public void setErrorReturn(int errCode, String msg) {
		
		apiReturnJson= new JsonObject() ;
		onError = true ;
		apiReturnJson.addProperty(ApiJsonPropertyName.OPERATION, KO);
		apiReturnJson.add(ApiJsonPropertyName.ERROR, ApiErrorCodeBuilder.getErrorCode(errCode, msg)) ;
	}
	
	private final static String API_RET_TRACE_TITLE = "Api return for " ;
	
	// Api return json, with logging. Include a blank at the end of info.
	public String getApiReturnJson(String info) {
		
		JsonObject ret = getApiReturnJsonObject(info) ;
		String retStr ;
		if (aLog.isLoggable(Level.FINE)) {
			retStr = JsonUtils.jsonPrettyPrint(ret) ;
			StringBuilder traceStrBuild ;			
			if ((aLog.isLoggable(Level.FINER)) || (retStr.length() < TRACE_FINER_LIMIT + 2)) {
				traceStrBuild = new StringBuilder(retStr.length() + info.length() + API_RET_TRACE_TITLE.length() + 8) ;
				traceStrBuild.append(API_RET_TRACE_TITLE).append(info).append("\n").append(retStr) ;
				aLog.finer(traceStrBuild.toString());
			} else {
				traceStrBuild = new StringBuilder(TRACE_FINER_LIMIT + info.length() + API_RET_TRACE_TITLE.length() + 8) ;
				traceStrBuild.append(API_RET_TRACE_TITLE).append(info).append("\n").append(retStr.substring(0, TRACE_FINER_LIMIT)) ;
				aLog.fine(traceStrBuild.toString());
			}
		} else {
			retStr = ret.toString() ;
		}
		return retStr;
	}
	
	private final static String COMPRESS_ERROR_MSG =  "Error in ApiReturn when compressing string " ;
	public byte[] getCompressedApiReturn(String info, CompressionUtils.SupportedCompression compressAlgo) {
		
		// Result to return
		byte[] compressedArray = null ;
		
		// Get the api return as a string
		String returnString = getApiReturnJson(info) ;
		
		// encode the string with the defined charset
		byte[] stringReturnAsBytes = null ;
	
		stringReturnAsBytes = returnString.getBytes(responseCharset) ;
		
		if (!onError) {

			switch (compressAlgo) {
			case GZIP:
				compressedArray = CompressionUtils.compressGzip(stringReturnAsBytes, aLog) ;
				break ;
			case DEFLATE:
				compressedArray = CompressionUtils.compressDeflate(stringReturnAsBytes, aLog) ;
				break ;
			default:
				aLog.severe("Unexpected compression scheme: " + compressAlgo);
				compressedArray = stringReturnAsBytes ;
			}
			
			
			if (compressedArray == null) {
				String errorMsg ;
				if (info == null) {
					errorMsg = COMPRESS_ERROR_MSG + "null" ;
				} else {
					errorMsg = COMPRESS_ERROR_MSG + first100char(info) + "..." ;
				}
				aLog.severe(errorMsg);
				setErrorReturn(ApiErrorCodeBuilder.COMPRESSION_EXCEPTION_CODE, errorMsg) ;
			}		
		}
		if ((aLog.isLoggable(Level.FINE)) && (!onError)) {
			aLog.fine("Compression applied. Original return character number=" + returnString.length() + 
					  " Compressed return byte number=" + compressedArray.length);
		}
		return compressedArray ;
	}
	
	private String first100char(String s) {
		if (s.length() > 99) {
			return s.substring(0, 99) ;
		} else {
			return s ;
		}
	}
	private JsonObject getApiReturnJsonObject(String info) {
			
		execDurations.endProcedurePoint();
		if ((apiReturnJson != null) && (info != null)) {
			if (aLog.isLoggable(execDurations.getTriggerLevel())) {
				additionnalInfos.add(ApiJsonPropertyName.DURATION, execDurations.getJsonExecutionDuration()) ;
				apiReturnJson.add(ApiJsonPropertyName.ADDITIONAL_INFOS, additionnalInfos) ;
				aLog.info(info + execDurations.getTotalDuration());
			}
			if (subReturnCode != null) {
				apiReturnJson.add(ApiJsonPropertyName.SUB_RETURN_CODE, subReturnCode);
			}
		} else {
			setErrorReturn(ApiErrorCodeBuilder.EMPTY_RETURN_ERROR_CODE) ;
		}
		return apiReturnJson;
	}
	
	public boolean isOnError() {
		return onError;
	}

	public JsonObject getAdditionnalInfos() {
		return additionnalInfos;
	}
	
	public void copyReturns(ApiReturn source) {
		
		onError = source.isOnError() ;
		apiReturnJson = source.apiReturnJson ;
	}

	public void setSubReturnCode(JsonObject subReturnCode) {
		this.subReturnCode = subReturnCode;
	}

	public Charset getResponseCharset() {
		return responseCharset;
	}
}

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

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.CompressionUtils;
import org.fl.util.ExecutionDurations;
import org.fl.util.json.JsonUtils;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public class ApiReturn {

	// Limit trace size of api return when level is FINE
	private final static int TRACE_FINER_LIMIT = 8192;

	// Value for Json return
	public final static String OK = "OK";
	public final static String KO = "KO";

	private ObjectNode apiReturnJson;
	private final Logger aLog;
	private boolean onError;
	private final ExecutionDurations execDurations;
	private ObjectNode additionnalInfos;
	private ObjectNode subReturnCode;
	private Charset responseCharset;

	public ApiReturn(ExecutionDurations ed, Charset rc, Logger l) {

		aLog = l;
		onError = false;
		apiReturnJson = null;
		execDurations = ed;
		subReturnCode = null;
		additionnalInfos = JsonNodeFactory.instance.objectNode();
		responseCharset = rc;
	}

	public void setDataReturn(JsonNode dataReturn) {

		apiReturnJson = JsonNodeFactory.instance.objectNode();
		apiReturnJson.put(ApiJsonPropertyName.OPERATION, OK);
		onError = false;
		if (dataReturn != null) {
			apiReturnJson.set(ApiJsonPropertyName.DATA, dataReturn);
		}
	}
	
	public void setErrorReturn(int errCode) {
		
		apiReturnJson= JsonNodeFactory.instance.objectNode();
		onError = true ;
		apiReturnJson.put(ApiJsonPropertyName.OPERATION, KO);
		apiReturnJson.set(ApiJsonPropertyName.ERROR, ApiErrorCodeBuilder.getErrorCode(errCode, null)) ;
	}
	
	public void setErrorReturn(int errCode, String msg) {
		
		apiReturnJson= JsonNodeFactory.instance.objectNode();
		onError = true ;
		apiReturnJson.put(ApiJsonPropertyName.OPERATION, KO);
		apiReturnJson.set(ApiJsonPropertyName.ERROR, ApiErrorCodeBuilder.getErrorCode(errCode, msg)) ;
	}
	
	private final static String API_RET_TRACE_TITLE = "Api return for " ;
	
	// Api return json, with logging. Include a blank at the end of info.
	public String getApiReturnJson(String info) {
		
		JsonNode ret = getApiReturnJsonObject(info) ;
		String retStr ;
		if (aLog.isLoggable(Level.FINE)) {
			try {
				retStr = JsonUtils.jsonPrettyPrint(ret) ;
			} catch (JacksonException e) {
				aLog.log(Level.SEVERE, "Exception pretty printing API return", e);
				retStr = ret.toString();
			}
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
	private JsonNode getApiReturnJsonObject(String info) {
			
		execDurations.endProcedurePoint();
		if ((apiReturnJson != null) && (info != null)) {
			if (aLog.isLoggable(execDurations.getTriggerLevel())) {
				additionnalInfos.set(ApiJsonPropertyName.DURATION, execDurations.getJsonExecutionDuration()) ;
				apiReturnJson.set(ApiJsonPropertyName.ADDITIONAL_INFOS, additionnalInfos) ;
				aLog.info(info + execDurations.getTotalDuration());
			}
			if (subReturnCode != null) {
				apiReturnJson.set(ApiJsonPropertyName.SUB_RETURN_CODE, subReturnCode);
			}
		} else {
			setErrorReturn(ApiErrorCodeBuilder.EMPTY_RETURN_ERROR_CODE) ;
		}
		return apiReturnJson;
	}
	
	public boolean isOnError() {
		return onError;
	}

	public JsonNode getAdditionnalInfos() {
		return additionnalInfos;
	}
	
	public void copyReturns(ApiReturn source) {
		
		onError = source.isOnError() ;
		apiReturnJson = source.apiReturnJson ;
	}
 
	public void setSubReturnCode(ObjectNode subReturnCode) {
		this.subReturnCode = subReturnCode;
	}

	public Charset getResponseCharset() {
		return responseCharset;
	}
}

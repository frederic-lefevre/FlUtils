package com.ibm.lge.fl.util.api;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import com.ibm.lge.fl.util.ExecutionDurations;
import com.ibm.lge.fl.util.json.JsonUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ApiReturn {

	// Expected compression ratio
	private final static int EXPECTED_COMPRESS_RATIO = 6 ;
	
	// Intermediate compression buffer size (1 Mega)
	private final static int COMPRESS_BUFFER_SIZE = 1048576 ;
			
	// Limit trace size of api return when level is FINE
	private final static int TRACE_FINER_LIMIT = 8192 ;

	// Value for Json return
	private final static String OK        		 = "OK" ;
	private final static String KO        		 = "KO" ;

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
			StringBuilder retStrBuild ;			
			if ((aLog.isLoggable(Level.FINER)) || (retStr.length() < TRACE_FINER_LIMIT + 2)) {
				retStrBuild = new StringBuilder(retStr.length() + info.length() + API_RET_TRACE_TITLE.length() + 8) ;
				retStrBuild.append(API_RET_TRACE_TITLE).append(info).append("\n").append(retStr) ;
			} else {
				retStrBuild = new StringBuilder(TRACE_FINER_LIMIT + info.length() + API_RET_TRACE_TITLE.length() + 8) ;
				retStrBuild.append(API_RET_TRACE_TITLE).append(info).append("\n").append(retStr.substring(0, TRACE_FINER_LIMIT)) ;
			}
			retStr = retStrBuild.toString() ;
		} else {
			retStr = ret.toString() ;
		}
		return retStr;
	}
	
	public byte[] getCompressedApiReturn(String info) {
		
		// Result to return
		byte[] compressedArray = null ;
		
		// Get the api return as a string
		String returnString = getApiReturnJson(info) ;
		
		// encode the string with the defined charset
		byte[] stringReturnAsBytes = null ;
	
		stringReturnAsBytes = returnString.getBytes(responseCharset) ;
		
		if (!onError) {

			// Create a Deflater to compress the encoded bytes
			Deflater compresser = new Deflater();
			compresser.setInput(stringReturnAsBytes);
			compresser.finish();

			try (ByteArrayOutputStream bos = new ByteArrayOutputStream(stringReturnAsBytes.length/EXPECTED_COMPRESS_RATIO) ) {
				
				byte[] buffer = new byte[COMPRESS_BUFFER_SIZE];           
				while(!compresser.finished())
				{             
					int bytesCompressed = compresser.deflate(buffer);
					bos.write(buffer,0,bytesCompressed);
				}	
				
				//get the compressed byte array from output stream
				compressedArray = bos.toByteArray();
				
			}  catch(Exception ioe) {
				aLog.log(Level.SEVERE, "Exception in ApiReturn when compressing Api return", ioe);
				setErrorReturn(ApiErrorCodeBuilder.COMPRESSION_EXCEPTION_CODE, ioe.toString()) ;
			}		
		}
		if ((aLog.isLoggable(Level.FINE)) && (!onError)) {
			aLog.fine("Compression applied. Original return character number=" + returnString.length() + 
					  " Compressed return byte number=" + compressedArray.length);
		}
		return compressedArray ;
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

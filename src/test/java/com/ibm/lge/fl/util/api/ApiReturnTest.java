package com.ibm.lge.fl.util.api;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.lge.fl.util.CompressionUtils;
import com.ibm.lge.fl.util.ExecutionDurations;

class ApiReturnTest {

	private static final Logger logger = Logger.getLogger(ApiReturnTest.class.getName()) ;
	
	@Test
	void errorReturn() {
		
		ApiErrorCodeBuilder.registerApiError(1234, "Mon code de test", logger) ;
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, logger) ;
		
		assertFalse(apiReturn.isOnError()) ;
			
		apiReturn.setErrorReturn(1234);
				
		assertTrue(apiReturn.isOnError()) ;
		
		String ret = apiReturn.getApiReturnJson("Info retour ") ;
		JsonObject jsonRet = JsonParser.parseString(ret).getAsJsonObject() ;

		assertEquals(ApiReturn.KO, jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()) ;
		
		JsonObject errorJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.ERROR) ;
		assertEquals(1234, errorJson.get(ApiJsonPropertyName.ERR_CODE).getAsInt()) ;
		assertEquals("Mon code de test", errorJson.get(ApiJsonPropertyName.REASON).getAsString()) ;

	}

	@Test
	void normalReturn() {
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, logger) ;
		
		JsonObject sampleReturn = new JsonObject() ;
		sampleReturn.addProperty("prop1", "contenu de la prop1");
		sampleReturn.addProperty("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		String ret = apiReturn.getApiReturnJson("Info retour ") ;
		JsonObject jsonRet = JsonParser.parseString(ret).getAsJsonObject() ;
		
		assertTrue(jsonRet.has(ApiJsonPropertyName.OPERATION)) ;
		assertTrue(jsonRet.has(ApiJsonPropertyName.DATA)) ;
		
		assertEquals(ApiReturn.OK, jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()) ;
		
		JsonObject dataJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.DATA) ;
		assertEquals("contenu de la prop1", dataJson.get("prop1").getAsString()) ;
		assertEquals("contenu de la prop2", dataJson.get("prop2").getAsString()) ;
	}
	
	@Test
	void testDuration() {
		
		String sequenceName = "test" ;
		ExecutionDurations execDuration = new ExecutionDurations(sequenceName, logger, Level.SEVERE) ;
		
		ApiReturn apiReturn = new ApiReturn(execDuration, StandardCharsets.UTF_8, logger) ;
		
		JsonObject sampleReturn = new JsonObject() ;
		sampleReturn.addProperty("prop1", "contenu de la prop1");

		apiReturn.setDataReturn(sampleReturn);
		
		String ret = apiReturn.getApiReturnJson("Info retour ") ;
		JsonObject jsonRet = JsonParser.parseString(ret).getAsJsonObject() ;
		
		assertTrue(jsonRet.has(ApiJsonPropertyName.ADDITIONAL_INFOS)) ;
		
		JsonObject aiJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.ADDITIONAL_INFOS) ;
		
		assertTrue(aiJson.has(ApiJsonPropertyName.DURATION)) ;
		
		JsonObject durationJson = aiJson.getAsJsonObject(ApiJsonPropertyName.DURATION) ;
		assertTrue(durationJson.has(ExecutionDurations.TOTAL_DURATION)) ;
		assertTrue(durationJson.has(sequenceName + "1")) ;
		
	}
	
	@Test
	void compressedReturn() {
		
		Charset charSetForReturn = StandardCharsets.UTF_8 ;
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), charSetForReturn, logger) ;
		
		JsonObject sampleReturn = new JsonObject() ;
		sampleReturn.addProperty("prop1", "contenu de la prop1");
		sampleReturn.addProperty("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		byte[] ret = apiReturn.getCompressedApiReturn("Info retour ") ;
		
		String decompressedRet = CompressionUtils.decompressDeflateString(ret, charSetForReturn, logger) ;
		
		JsonObject jsonRet = JsonParser.parseString(decompressedRet).getAsJsonObject() ;
		
		assertTrue(jsonRet.has(ApiJsonPropertyName.OPERATION)) ;
		assertTrue(jsonRet.has(ApiJsonPropertyName.DATA)) ;
		
		assertEquals(ApiReturn.OK, jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()) ;
		
		JsonObject dataJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.DATA) ;
		assertEquals("contenu de la prop1", dataJson.get("prop1").getAsString()) ;
		assertEquals("contenu de la prop2", dataJson.get("prop2").getAsString()) ;
	}
}

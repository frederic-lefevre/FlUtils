package com.ibm.lge.fl.util.api;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.ExecutionDurations;

class ApiReturnTest {

	private static final Logger logger = Logger.getLogger(ApiReturnTest.class.getName()) ;
	
	@Test
	void test1() {
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, logger) ;
		
		assertFalse(apiReturn.isOnError()) ;
		
		apiReturn.setErrorReturn(ApiErrorCodeBuilder.UNAUTHORIZED_CODE);
		
		assertTrue(apiReturn.isOnError()) ;
	}

}

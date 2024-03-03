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

package org.fl.util.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.CompressionUtils;
import org.fl.util.ExecutionDurations;
import org.fl.util.FilterCounter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class ApiReturnTest {

	private static final Logger logger = Logger.getLogger(ApiReturnTest.class.getName());
	private static final FilterCounter filterCounter = new FilterCounter();
	
	@BeforeAll
	static void silentLog() {
		logger.setFilter(filterCounter);
	}
	
	@BeforeEach
	void resetLogRecordCounter() {
		filterCounter.resetLogRecordCounts();
	}
	
	@Test
	void errorReturn() {
		
		ApiErrorCodeBuilder.registerApiError(1234, "Mon code de test", logger);
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, logger);
		
		assertThat(apiReturn.isOnError()).isFalse();
			
		apiReturn.setErrorReturn(1234);
				
		assertThat(apiReturn.isOnError()).isTrue();
		
		String ret = apiReturn.getApiReturnJson("Info retour ");
		JsonObject jsonRet = JsonParser.parseString(ret).getAsJsonObject();

		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()).isEqualTo(ApiReturn.KO);
		
		JsonObject errorJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.ERROR);
		assertThat(errorJson.get(ApiJsonPropertyName.ERR_CODE).getAsInt()).isEqualTo(1234);
		assertThat(errorJson.get(ApiJsonPropertyName.REASON).getAsString()).isEqualTo("Mon code de test");
		
		assertThat(filterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(filterCounter.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}

	@Test
	void normalReturn() {
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, logger);
		
		JsonObject sampleReturn = new JsonObject() ;
		sampleReturn.addProperty("prop1", "contenu de la prop1");
		sampleReturn.addProperty("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		String ret = apiReturn.getApiReturnJson("Info retour ");
		JsonObject jsonRet = JsonParser.parseString(ret).getAsJsonObject();
		
		assertThat(jsonRet.has(ApiJsonPropertyName.OPERATION)).isTrue();
		assertThat(jsonRet.has(ApiJsonPropertyName.DATA)).isTrue();
		
		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()).isEqualTo(ApiReturn.OK);
		
		JsonObject dataJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.DATA);
		assertThat(dataJson.get("prop1").getAsString()).isEqualTo("contenu de la prop1");
		assertThat(dataJson.get("prop2").getAsString()).isEqualTo("contenu de la prop2");
		
		assertThat(filterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(filterCounter.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
	
	@Test
	void testDuration() {
		
		String sequenceName = "test" ;
		ExecutionDurations execDuration = new ExecutionDurations(sequenceName, logger, Level.SEVERE);
		
		ApiReturn apiReturn = new ApiReturn(execDuration, StandardCharsets.UTF_8, logger);
		
		JsonObject sampleReturn = new JsonObject();
		sampleReturn.addProperty("prop1", "contenu de la prop1");

		apiReturn.setDataReturn(sampleReturn);
		
		String ret = apiReturn.getApiReturnJson("Info retour ");
		JsonObject jsonRet = JsonParser.parseString(ret).getAsJsonObject();
		
		assertThat(jsonRet.has(ApiJsonPropertyName.ADDITIONAL_INFOS)).isTrue();
		
		JsonObject aiJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.ADDITIONAL_INFOS);
		
		assertThat(aiJson.has(ApiJsonPropertyName.DURATION)).isTrue();
		
		JsonObject durationJson = aiJson.getAsJsonObject(ApiJsonPropertyName.DURATION);
		assertThat(durationJson.has(ExecutionDurations.TOTAL_DURATION)).isTrue();
		assertThat(durationJson.has(sequenceName + "1")).isTrue();
		
		assertThat(filterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(filterCounter.getLogRecordCount(Level.INFO)).isEqualTo(1);
		
	}
	
	@Test
	void compressedDeflateReturn() {
		
		Charset charSetForReturn = StandardCharsets.UTF_8 ;
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), charSetForReturn, logger) ;
		
		JsonObject sampleReturn = new JsonObject() ;
		sampleReturn.addProperty("prop1", "contenu de la prop1");
		sampleReturn.addProperty("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		byte[] ret = apiReturn.getCompressedApiReturn("Info retour ", CompressionUtils.SupportedCompression.DEFLATE);
		
		String decompressedRet = CompressionUtils.decompressDeflateString(ret, charSetForReturn, logger);
		
		JsonObject jsonRet = JsonParser.parseString(decompressedRet).getAsJsonObject();
		
		assertThat(jsonRet.has(ApiJsonPropertyName.OPERATION)).isTrue();
		assertThat(jsonRet.has(ApiJsonPropertyName.DATA)).isTrue();
		
		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()).isEqualTo(ApiReturn.OK);
		
		JsonObject dataJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.DATA);
		assertThat(dataJson.get("prop1").getAsString()).isEqualTo("contenu de la prop1");
		assertThat(dataJson.get("prop2").getAsString()).isEqualTo("contenu de la prop2");
		
		assertThat(filterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(filterCounter.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
	
	@Test
	void compresseGzipReturn() {
		
		Charset charSetForReturn = StandardCharsets.UTF_8;
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), charSetForReturn, logger);
		
		JsonObject sampleReturn = new JsonObject();
		sampleReturn.addProperty("prop1", "contenu de la prop1");
		sampleReturn.addProperty("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		byte[] ret = apiReturn.getCompressedApiReturn("Info retour ", CompressionUtils.SupportedCompression.GZIP);
		
		String decompressedRet = CompressionUtils.decompressGzipString(ret, charSetForReturn, logger);
		
		JsonObject jsonRet = JsonParser.parseString(decompressedRet).getAsJsonObject();
		
		assertThat(jsonRet.has(ApiJsonPropertyName.OPERATION)).isTrue();
		assertThat(jsonRet.has(ApiJsonPropertyName.DATA)).isTrue();
		
		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).getAsString()).isEqualTo(ApiReturn.OK);
		
		JsonObject dataJson = jsonRet.getAsJsonObject(ApiJsonPropertyName.DATA);
		assertThat(dataJson.get("prop1").getAsString()).isEqualTo("contenu de la prop1");
		assertThat(dataJson.get("prop2").getAsString()).isEqualTo("contenu de la prop2");
		
		assertThat(filterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(filterCounter.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
}

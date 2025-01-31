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

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.fl.util.CompressionUtils;
import org.fl.util.ExecutionDurations;
import org.fl.util.LoggerCounter;
import org.fl.util.json.JsonUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class ApiReturnTest {
	
	@Test
	void errorReturn() throws JsonMappingException, JsonProcessingException {
		
		LoggerCounter noLog = LoggerCounter.getLogger();
		
		ApiErrorCodeBuilder.registerApiError(1234, "Mon code de test", noLog);
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, noLog);
		
		assertThat(apiReturn.isOnError()).isFalse();
			
		apiReturn.setErrorReturn(1234);
				
		assertThat(apiReturn.isOnError()).isTrue();
		
		String ret = apiReturn.getApiReturnJson("Info retour ");
		JsonNode jsonRet = JsonUtils.getObjectMapper().readTree(ret);

		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).asText()).isEqualTo(ApiReturn.KO);
		
		JsonNode errorJson = jsonRet.get(ApiJsonPropertyName.ERROR);
		assertThat(errorJson.get(ApiJsonPropertyName.ERR_CODE).asInt()).isEqualTo(1234);
		assertThat(errorJson.get(ApiJsonPropertyName.REASON).asText()).isEqualTo("Mon code de test");
		
		assertThat(noLog.getLogRecordCount()).isEqualTo(1);
		assertThat(noLog.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}

	@Test
	void normalReturn() throws JsonMappingException, JsonProcessingException {
		
		LoggerCounter noLog = LoggerCounter.getLogger();
		
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), StandardCharsets.UTF_8, noLog);
		
		ObjectNode sampleReturn = JsonNodeFactory.instance.objectNode();
		sampleReturn.put("prop1", "contenu de la prop1");
		sampleReturn.put("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		String ret = apiReturn.getApiReturnJson("Info retour ");
		JsonNode jsonRet = JsonUtils.getObjectMapper().readTree(ret);
		
		assertThat(jsonRet.has(ApiJsonPropertyName.OPERATION)).isTrue();
		assertThat(jsonRet.has(ApiJsonPropertyName.DATA)).isTrue();
		
		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).asText()).isEqualTo(ApiReturn.OK);
		
		JsonNode dataJson = jsonRet.get(ApiJsonPropertyName.DATA);
		assertThat(dataJson.get("prop1").asText()).isEqualTo("contenu de la prop1");
		assertThat(dataJson.get("prop2").asText()).isEqualTo("contenu de la prop2");
		
		assertThat(noLog.getLogRecordCount()).isEqualTo(1);
		assertThat(noLog.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
	
	@Test
	void testDuration() throws JsonMappingException, JsonProcessingException {
		
		LoggerCounter noLog = LoggerCounter.getLogger();
		
		String sequenceName = "test" ;
		ExecutionDurations execDuration = new ExecutionDurations(sequenceName, noLog, Level.SEVERE);
		
		ApiReturn apiReturn = new ApiReturn(execDuration, StandardCharsets.UTF_8, noLog);
		
		ObjectNode sampleReturn = JsonNodeFactory.instance.objectNode();
		sampleReturn.put("prop1", "contenu de la prop1");

		apiReturn.setDataReturn(sampleReturn);
		
		String ret = apiReturn.getApiReturnJson("Info retour ");
		JsonNode jsonRet = JsonUtils.getObjectMapper().readTree(ret);
		
		assertThat(jsonRet.has(ApiJsonPropertyName.ADDITIONAL_INFOS)).isTrue();
		
		JsonNode aiJson = jsonRet.get(ApiJsonPropertyName.ADDITIONAL_INFOS);
		
		assertThat(aiJson.has(ApiJsonPropertyName.DURATION)).isTrue();
		
		JsonNode durationJson = aiJson.get(ApiJsonPropertyName.DURATION);
		assertThat(durationJson.has(ExecutionDurations.TOTAL_DURATION)).isTrue();
		assertThat(durationJson.has(sequenceName + "1")).isTrue();
		
		assertThat(noLog.getLogRecordCount()).isEqualTo(1);
		assertThat(noLog.getLogRecordCount(Level.INFO)).isEqualTo(1);
		
	}
	
	@Test
	void compressedDeflateReturn() throws JsonMappingException, JsonProcessingException {
		
		LoggerCounter noLog = LoggerCounter.getLogger();
		
		Charset charSetForReturn = StandardCharsets.UTF_8 ;
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), charSetForReturn, noLog) ;
		
		ObjectNode sampleReturn = JsonNodeFactory.instance.objectNode();
		sampleReturn.put("prop1", "contenu de la prop1");
		sampleReturn.put("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		byte[] ret = apiReturn.getCompressedApiReturn("Info retour ", CompressionUtils.SupportedCompression.DEFLATE);
		
		String decompressedRet = CompressionUtils.decompressDeflateString(ret, charSetForReturn, noLog);
		
		JsonNode jsonRet = JsonUtils.getObjectMapper().readTree(decompressedRet);
		
		assertThat(jsonRet.has(ApiJsonPropertyName.OPERATION)).isTrue();
		assertThat(jsonRet.has(ApiJsonPropertyName.DATA)).isTrue();
		
		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).asText()).isEqualTo(ApiReturn.OK);
		
		JsonNode dataJson = jsonRet.get(ApiJsonPropertyName.DATA);
		assertThat(dataJson.get("prop1").asText()).isEqualTo("contenu de la prop1");
		assertThat(dataJson.get("prop2").asText()).isEqualTo("contenu de la prop2");
		
		assertThat(noLog.getLogRecordCount()).isEqualTo(1);
		assertThat(noLog.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
	
	@Test
	void compresseGzipReturn() throws JsonMappingException, JsonProcessingException {
		
		LoggerCounter noLog = LoggerCounter.getLogger();
		
		Charset charSetForReturn = StandardCharsets.UTF_8;
		ApiReturn apiReturn = new ApiReturn(new ExecutionDurations("test"), charSetForReturn, noLog);
		
		ObjectNode sampleReturn = JsonNodeFactory.instance.objectNode();
		sampleReturn.put("prop1", "contenu de la prop1");
		sampleReturn.put("prop2", "contenu de la prop2");

		apiReturn.setDataReturn(sampleReturn);
		
		byte[] ret = apiReturn.getCompressedApiReturn("Info retour ", CompressionUtils.SupportedCompression.GZIP);
		
		String decompressedRet = CompressionUtils.decompressGzipString(ret, charSetForReturn, noLog);
		
		JsonNode jsonRet = JsonUtils.getObjectMapper().readTree(decompressedRet);
		
		assertThat(jsonRet.has(ApiJsonPropertyName.OPERATION)).isTrue();
		assertThat(jsonRet.has(ApiJsonPropertyName.DATA)).isTrue();
		
		assertThat(jsonRet.get(ApiJsonPropertyName.OPERATION).asText()).isEqualTo(ApiReturn.OK);
		
		JsonNode dataJson = jsonRet.get(ApiJsonPropertyName.DATA);
		assertThat(dataJson.get("prop1").asText()).isEqualTo("contenu de la prop1");
		assertThat(dataJson.get("prop2").asText()).isEqualTo("contenu de la prop2");
		
		assertThat(noLog.getLogRecordCount()).isEqualTo(1);
		assertThat(noLog.getLogRecordCount(Level.INFO)).isEqualTo(1);
	}
}

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

package org.fl.util.os;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

class OperatingInfoTest {

	private static final String PROPERTY_KEY = "testPropertyKey";
	private static final String PROPERTY_VALUE = "testPropertyValue";
	
	@BeforeEach
	void setProp() {
		System.setProperty(PROPERTY_KEY, PROPERTY_VALUE);
	}
	
	@Test
	void testOperatingInfoWithLookup() {
		
		JsonNode operatingInfo = OperatingInfo.getInfo(true);
		assertOperatingInfo(operatingInfo, true);
	}
	
	@Test
	void testOperatingInfoWithoutLookup() {
		
		JsonNode operatingInfo = OperatingInfo.getInfo(false);
		assertOperatingInfo(operatingInfo, false);
	}
	
	private void assertOperatingInfo(JsonNode operatingInfo, boolean withLookup) {
		
		assertThat(operatingInfo.has("defaultCharset")).isTrue();
		assertThat(operatingInfo.has("networkInformation")).isTrue();
		assertThat(operatingInfo.has("systemEnvironment")).isTrue();
		assertThat(operatingInfo.has("runtimeInformation")).isTrue();
		assertThat(operatingInfo.has("newLine")).isTrue();
		assertThat(operatingInfo.has("availableCharset")).isTrue();
		assertThat(operatingInfo.has("systemProperties")).isTrue();
		assertThat(operatingInfo.has("fileSystemsInformation")).isTrue();
		assertThat(operatingInfo.size()).isEqualTo(8);
		
		assertThat(operatingInfo.get("defaultCharset").asText()).isEqualTo(Charset.defaultCharset().name());
		
		JsonNode newLine = operatingInfo.get("newLine");
		assertThat(newLine.isArray()).isTrue();
		assertThat(newLine.elements()).toIterable().hasSize(2).map(element -> element.asText())
			.satisfiesExactlyInAnyOrder(
					stringElement -> assertThat(stringElement).startsWith("Newline unicode code point sequence:"),
					stringElement -> assertThat(stringElement).startsWith("Newline as default charset byte sequence:"));
		
		
		JsonNode systemEnvironment = operatingInfo.get("systemEnvironment");
		assertThat(systemEnvironment.isObject()).isTrue();
		assertThat(systemEnvironment.isArray()).isFalse();
		
		JsonNode availableCharset = operatingInfo.get("availableCharset");
		assertThat(availableCharset.isArray()).isTrue();
		Stream.of(StandardCharsets.class.getDeclaredFields())
			.forEach(field -> {
				try {
					if (field.get(null) instanceof Charset charset) {
						assertThat(availableCharset.elements()).toIterable().map(element -> element.asText()).contains(charset.name());
					} else {
						fail("A field of StandardCharsets is not a Charset ...");
					}
				} catch (Exception e) {
					fail("Exception on getting StandardCharsets field corresponding object", e);
				}
			});
				
		JsonNode systemProperties = operatingInfo.get("systemProperties");
		assertThat(systemProperties.has("java.specification.version")).isTrue();
		assertThat(systemProperties.has("java.class.path")).isTrue();
		assertThat(systemProperties.has("user.dir")).isTrue();
		assertThat(systemProperties.has("file.separator")).isTrue();
		assertThat(systemProperties.has(PROPERTY_KEY)).isTrue();
		assertThat(systemProperties.get(PROPERTY_KEY).asText()).isEqualTo(PROPERTY_VALUE);
		
		JsonNode networkInformation = operatingInfo.get("networkInformation");
		assertThat(networkInformation.has("machineName")).isTrue();
		assertThat(networkInformation.has("IPv4addresses")).isTrue();
		assertThat(networkInformation.has("IPv6addresses")).isTrue();
		assertThat(networkInformation.has("otherAddresses")).isTrue();
		assertThat(networkInformation.has("networkInterfaces")).isTrue();
		assertThat(networkInformation.size()).isEqualTo(5);
		assertThat(
			Stream.of("IPv4addresses", "IPv6addresses", "otherAddresses")
				.map(fieldName -> networkInformation.get(fieldName))
				.filter(ipAddresses -> {
					assertThat(ipAddresses.isArray()).isTrue();
					return (ipAddresses.size() > 0);
				})
				.map(ipAddresses -> ipAddresses.get(0))
				.map(ipAddress -> ipAddress.has("IPaddress") && (ipAddress.has("hostname") == withLookup))
				.reduce(Boolean::logicalAnd).orElse(false))
			.isTrue();

	}

}

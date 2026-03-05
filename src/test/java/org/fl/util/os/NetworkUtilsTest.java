/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;

class NetworkUtilsTest {
	
	@Test
	void testGetNetworkInformationWithoutLookup() {
		
		JsonNode networkInformation = NetworkUtils.getNetworkInformation(false);
		assertNetworkInformation(networkInformation, false);
	}
	
	@Test
	void testGetNetworkInformationWithLookup() {
		
		JsonNode networkInformation = NetworkUtils.getNetworkInformation(true);
		assertNetworkInformation(networkInformation, true);
	}
	
	@Test
	void testGetIpV4List() {
		
		List<String> iPv4List = NetworkUtils.getIPv4List();
		
		JsonNode networkInformation = NetworkUtils.getNetworkInformation(false);
		assertThat(networkInformation.has("IPv4addresses")).isTrue();
		
		JsonNode  iPv4Json = networkInformation.get("IPv4addresses");
		assertThat(iPv4Json.isArray()).isTrue();
		
		assertThat(iPv4List.size()).isEqualTo(iPv4Json.size());
		
		assertThat(iPv4Json.values())
			.map(element -> element.get("IPaddress").asString())
			.allMatch(ip -> iPv4List.contains(ip));
	}
	
	private void assertNetworkInformation(JsonNode networkInformation, boolean withLookup) {
		
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

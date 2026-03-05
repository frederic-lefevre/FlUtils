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

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.function.IntFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.fl.util.file.FilesUtils;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public class OperatingInfo {
	
	private static final Logger logger = Logger.getLogger(OperatingInfo.class.getName());
	
	private OperatingInfo() {
	}

	public static JsonNode getInfo(boolean withIpLookup) {

		return JsonNodeFactory.instance.objectNode()
				.put("defaultCharset", Charset.defaultCharset().name())
				.setAll(Map.of(
					"runtimeInformation", getRuntimeInformation(),
					"systemProperties", getSystemProperties(),
					"systemEnvironment", getSystemEnv(),
					"newLine", getNewLineInformation(),
					"availableCharset", getAvailableCharSets(),
					"networkInformation", NetworkUtils.getNetworkInformation(withIpLookup),
					"fileSystemsInformation", FilesUtils.getFileSystemsInformation(logger)
					));
	}

	private static ArrayNode getRuntimeInformation() {
		
		Runtime rt = Runtime.getRuntime();
		
		return JsonNodeFactory.instance.arrayNode()
			.add("Free Memory usable for objects=" + rt.freeMemory() + " bytes")
			.add("Maximum Memory available for the JVM=" + rt.maxMemory() + " bytes")
			.add("Total Memory usable for objects=" + rt.totalMemory() + " bytes")
			.add("Number of processors=" + rt.availableProcessors())
			.add("Runtime version=" + Runtime.version());
	}
	
	private static final String HEX_PREFIX = "Ox";
	private static IntFunction<String> intToHexaString = i -> HEX_PREFIX.concat(Integer.toHexString(i).toUpperCase());
	
	private static ArrayNode getNewLineInformation() {
		
		String newLine = System.getProperty("line.separator");
		byte[] newLineBytes = newLine.getBytes();

		return JsonNodeFactory.instance.arrayNode()
			.add("Newline unicode code point sequence:"
				.concat(
					newLine
						.codePoints()
						.mapToObj(intToHexaString)
						.collect(Collectors.joining(", "))))
			.add("Newline as default charset byte sequence:"
				.concat(
					IntStream.range(0, newLineBytes.length)
						.map(i -> newLineBytes[i])
						.mapToObj(intToHexaString)
						.collect(Collectors.joining(" "))));
	}
	
	private static ObjectNode getSystemProperties() {

		Properties systemProperties = System.getProperties();
		ObjectNode res = JsonNodeFactory.instance.objectNode();
		systemProperties.stringPropertyNames().forEach(key -> res.put(key, systemProperties.getProperty(key)));
		return res;
	}
	
	private static ObjectNode getSystemEnv() {
	
		Map<String, String> sysEnv = System.getenv();
		ObjectNode res = JsonNodeFactory.instance.objectNode();
		sysEnv.keySet().forEach(key -> res.put(key, sysEnv.get(key)));
		return res;
	}
	
	private static ArrayNode getAvailableCharSets() {
		
		ArrayNode charSetJson = JsonNodeFactory.instance.arrayNode();
		Charset.availableCharsets().keySet().forEach(key -> charSetJson.add(key));
		return charSetJson;
	}
}

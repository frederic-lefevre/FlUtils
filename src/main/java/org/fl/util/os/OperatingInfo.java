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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OperatingInfo {
	
	public OperatingInfo() {
		
	}

	public JsonNode getInfo(boolean withIpLookup) {

		StringBuilder info = new StringBuilder();
		Map<String, String> sysEnv = System.getenv();
		Properties sysProp = System.getProperties();
		String newLine = System.getProperty("line.separator");
		byte[] newLineBytes = newLine.getBytes();
		StringBuilder newLineCodePoint = new StringBuilder();
		for (int i = 0; i < newLine.length(); i++) {
			newLineCodePoint.append(Integer.toHexString(newLine.codePointAt(i)).toUpperCase()).append(" ");
		}
		StringBuilder newLineBytesString = new StringBuilder();
		for (byte b : newLineBytes) {
			newLineBytesString.append(b).append(" ");
		}

		Runtime rt = Runtime.getRuntime();

		long freeMem = rt.freeMemory();
		long maxMem = rt.maxMemory();
		long totalMem = rt.totalMemory();
		int nbAvailProc = rt.availableProcessors();

		ObjectNode opInfoJson = JsonNodeFactory.instance.objectNode();

		ArrayNode rtInfos = JsonNodeFactory.instance.arrayNode();
		info.append("Free Memory usable for objects=").append(freeMem).append(" bytes");
		rtInfos.add(info.toString());
		info.setLength(0);
		info.append("Maximum Memory available for the JVM=").append(maxMem).append(" bytes");
		rtInfos.add(info.toString());
		info.setLength(0);
		info.append("Total Memory usable for objects=").append(totalMem).append(" bytes");
		rtInfos.add(info.toString());
		info.setLength(0);
		info.append("Number of processors=").append(nbAvailProc);
		rtInfos.add(info.toString());
		info.setLength(0);
		opInfoJson.set("runtimeInformation", rtInfos);

		opInfoJson.set("systemProperties", printProp(sysProp));
		opInfoJson.set("systemEnvironment", printSysenv(sysEnv));

		ArrayNode nlInfos = JsonNodeFactory.instance.arrayNode();
		info.append("Newline unicode code point sequence:").append(newLineCodePoint);
		nlInfos.add(info.toString());
		info.setLength(0);
		info.append("Newline as byte sequence:").append(newLineBytesString);
		nlInfos.add(info.toString());
		info.setLength(0);
		opInfoJson.set("newLine", nlInfos);

		opInfoJson.put("defaultCharset", Charset.defaultCharset().name());
		opInfoJson.set("availableCharset", printCharSet());

		NetworkUtils nu = new NetworkUtils(withIpLookup);
		opInfoJson.set("networkInterfaces", nu.getNetworkInterfaces());
		opInfoJson.set("IPv4addresses", nu.getIPv4());
		opInfoJson.set("IPv6addresses", nu.getIPv6());
		opInfoJson.set("Otheraddresses", nu.getOtherAddresses());

		opInfoJson.put("machineName", nu.getMachineName());

		return opInfoJson;
	}

	private ObjectNode printProp(Properties prop) {

		ObjectNode res = JsonNodeFactory.instance.objectNode();
		prop.stringPropertyNames().forEach(key -> res.put(key, prop.getProperty(key)));
		return res;
	}
	
	private ObjectNode printSysenv(Map <String,String> sysenv) {
	
		ObjectNode res = JsonNodeFactory.instance.objectNode();
		sysenv.keySet().forEach(key -> res.put(key, sysenv.get(key)));
		return res;
	}
	
	private ObjectNode printCharSet() {
		
		ObjectNode charSetJson = JsonNodeFactory.instance.objectNode();
		Charset.availableCharsets().entrySet().forEach(entry -> charSetJson.put(entry.getKey(), entry.getValue().name()));
		return charSetJson;
	}
}

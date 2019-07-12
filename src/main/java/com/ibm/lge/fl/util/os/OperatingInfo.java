package com.ibm.lge.fl.util.os;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class OperatingInfo {
	
	public OperatingInfo() {
		
	}

	public JsonObject getInfo(boolean withIpLookup) {

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

		JsonObject opInfoJson = new JsonObject() ;
		
		JsonArray rtInfos = new JsonArray() ;
		info.append("Free Memory usable for objects=").append(freeMem).append(" bytes") ;
		rtInfos.add(info.toString());
		info.setLength(0) ;
		info.append("Maximum Memory available for the JVM=").append(maxMem).append(" bytes") ;
		rtInfos.add(info.toString());
		info.setLength(0) ;
		info.append("Total Memory usable for objects=").append(totalMem).append(" bytes") ;
		rtInfos.add(info.toString());
		info.setLength(0) ;
		info.append("Number of processors=").append(nbAvailProc);
		rtInfos.add(info.toString());
		info.setLength(0) ;		
		opInfoJson.add("runtimeInformation",  rtInfos) ;
		
		opInfoJson.add("systemProperties", printProp(sysProp)) ;
		opInfoJson.add("systemEnvironment", printSysenv(sysEnv)) ;
		
		JsonArray nlInfos = new JsonArray() ;
		info.append("Newline unicode code point sequence:").append(newLineCodePoint);
		nlInfos.add(info.toString());
		info.setLength(0) ;
		info.append("Newline as byte sequence:").append(newLineBytesString);
		nlInfos.add(info.toString());
		info.setLength(0) ;
		opInfoJson.add("newLine", nlInfos) ;
		
		opInfoJson.addProperty("defaultCharset", Charset.defaultCharset().name());
		opInfoJson.add("availableCharset", printCharSet()) ;

		NetworkUtils nu = new NetworkUtils(withIpLookup);
		opInfoJson.add("networkInterfaces", nu.getNetworkInterfaces()) ;
		opInfoJson.add("IPv4addresses", 	nu.getIPv4()) ;
		opInfoJson.add("IPv6addresses", 	nu.getIPv6()) ;
		opInfoJson.add("Otheraddresses", 	nu.getOtherAddresses()) ;
		
		opInfoJson.addProperty("machineName", nu.getMachineName()) ;

		return opInfoJson ;
	}

	private JsonArray printProp(Properties prop) {
		
		 Set<String> keys = prop.stringPropertyNames() ;
		 JsonArray res = new JsonArray() ;
		
		for (String k : keys)   {
			JsonObject jProp = new JsonObject() ;
			jProp.addProperty(k, prop.getProperty(k));
			res.add(jProp);
		}
		return res ;
	}
	
	private JsonArray printSysenv(Map <String,String> sysenv) {
	
		 Set<String> keys = sysenv.keySet() ;
		 JsonArray res = new JsonArray() ;
			
		for (String k : keys)   {
			JsonObject jProp = new JsonObject() ;
			jProp.addProperty(k, sysenv.get(k));
			res.add(jProp);
		}
		return res ;
	}
	
	private JsonArray printCharSet() {
		
		Set<String> charSetNames = Charset.availableCharsets().keySet() ;
		JsonArray  res = new JsonArray() ;
		
		for (String csn : charSetNames) {
			res.add(csn) ;
		}
		return res ;
	}
}

package com.ibm.lge.fl.util;

public class HttpHeader {

	
	private String key ;
	private String value ;
	
	public HttpHeader(String k, String v) {
		key   = k ;
		value = v ;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	
}

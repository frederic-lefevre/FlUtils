package com.ibm.lge.fl.util;

import java.nio.charset.Charset;

public class HttpContentTypeHeader extends HttpHeader {

	// Some useful string for http content type headers 
	public final static String CONTENT_TYPE 	= "Content-Type" ;
	public final static String APPLICATION_JSON = "application/json" ;

	private final static String CHAR_SET = ";charset=" ;
	
	public HttpContentTypeHeader(String contentTypeValue, Charset cs) {
			
		super(CONTENT_TYPE, contentTypeValue + CHAR_SET + cs.name());
	}
	
	public HttpContentTypeHeader(String contentTypeValue) {
		
		super(CONTENT_TYPE, contentTypeValue);
	}

}

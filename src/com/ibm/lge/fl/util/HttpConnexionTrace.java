package com.ibm.lge.fl.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpConnexionTrace {

	public static StringBuilder getTrace(HttpURLConnection con, String title, int bufSize, Logger sLog) {
		
		StringBuilder trace = new StringBuilder(bufSize) ;
		trace.append(title) ;
		String responseMessage ;
		if (con != null) {
			try {
				responseMessage = con.getResponseMessage() ;
			} catch (IOException e) {
				responseMessage = "Exception when getting response message " + e;
				sLog.log(Level.SEVERE, responseMessage, e);		
			}
			int responseCode ;
			try {
				responseCode = con.getResponseCode() ;
				trace.append("\n Response code=").append(responseCode) ;
			} catch (IOException e) {
				String msg = "Exception when getting response code " + e;
				trace.append(" response code=").append(msg) ;
				sLog.log(Level.SEVERE, msg, e);		
			}
			
			// Set response message and header fields		
			trace.append("\n Response message=").append(responseMessage) ;
				
			Map<String, List<String>> headerFields = con.getHeaderFields() ;
			if (headerFields != null) {
				Set<String> headerKeys = headerFields.keySet() ;
				if (headerKeys != null) {
					for(String key : headerKeys) {
						trace.append("\n Header field key=").append(key).append("\n Header field values=\n") ;
						List<String> values = headerFields.get(key) ;
						if (values != null) {
							for (String value : values) {
								trace.append(value).append("\n") ;
							}
						}
					}
				}
			}
		}
		return trace ;
	}
}

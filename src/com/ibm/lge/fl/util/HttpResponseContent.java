package com.ibm.lge.fl.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponseContent {

	private static int RECEIVE_BUF_SIZE =  200*1024 ;
	
	private HttpURLConnection httpCon ;
	
	// HTTP response content
	private CharBuffer content ;
	private ByteBuffer bytesContent ;
	
	// HTTP response code (200, 404 ...etc)
	private int responseCode ;
	
	private Logger hLog ;
	
	private boolean responseReceived ;
	
	public HttpResponseContent(HttpURLConnection con, Charset cs, boolean decompressIfCompressed, Logger l) {
		
		hLog    = l ;
		httpCon = con ;
		content = null ;
		responseReceived = false ;
		
		try {
			responseCode     = con.getResponseCode() ;
			responseReceived = true ;
						
			// Set content
			InputStream is = null;
			if (responseCode == HttpURLConnection.HTTP_OK) {
				is = con.getInputStream() ;
			} else {
				is = con.getErrorStream() ;			
			}
			if (is != null) {
				String contentType = con.getContentType() ;
				boolean decompress = false ;
				if (decompressIfCompressed && (contentType.contains("application/zip"))) {
					decompress = true ;
				}
				if (cs == null) {
					bytesContent = readContentAsByte(is, decompress) ;
				} else {
					content = readContent(is, cs, decompress) ;
				}
			}
			
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "Exception when getting response content", e);
		}		
	}

	// Will return null if charset was null
	public CharBuffer getContent() { 
		return content; 
	}
	
	// Will return null if charset was not null
	public ByteBuffer getByteContent() { 
		return bytesContent; 
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public String toString() { 
		
		int contentSize = 0 ;
		if (content != null) {
			contentSize = content.length() ;
		}
		int bufSize = 512 + contentSize ;
		StringBuilder result = HttpConnexionTrace.getTrace(httpCon, "", bufSize, hLog);
		
		// add content
		if (content != null) {
			result.append("\n response content=\n").append(content) ;
		}
		
		return result.toString() ; 
	}

	private CharBuffer readContent(InputStream is, Charset cs, boolean isCompressed) 	{
		
		ChannelReaderDecoder chan = new ChannelReaderDecoder(
				is,
				cs,
				null, 
				RECEIVE_BUF_SIZE,
				isCompressed,
				hLog) ;
		return chan.readAllChar() ;
		
	}
	
	private ByteBuffer readContentAsByte(InputStream is, boolean isCompressed) 	{
		
		ChannelReaderDecoder chan = new ChannelReaderDecoder(
				is,
				null,
				null, 
				RECEIVE_BUF_SIZE,
				isCompressed,
				hLog) ;
		return chan.readAllBytes() ;
		
	}
	
	public boolean isResponseCodeSucces() {
		return (responseCode >= 200) && (responseCode < 300) ;
	}

	public boolean isResponseReceived() {
		return responseReceived;
	}
}

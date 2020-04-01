package com.ibm.lge.fl.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {

	private static int RECEIVE_BUF_SIZE =  200*1024 ;

	// Trust manager that trusts all certificate
	private static TrustManager[] trustAllCerts ;
	
	static {
		
		// init trust manager that trusts all certificate
		trustAllCerts = new TrustManager[] { 
			      new X509TrustManager() {
			        public X509Certificate[] getAcceptedIssuers() { 
			          return new X509Certificate[0]; 
			        }
			        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
			        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
			    }};		
	}
	
	// Provide a SSL context that trusts all certificates
	public static SSLContext allTrusterSslContext(Logger log) {
			
			SSLContext sc = null ;
			try {
				sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new SecureRandom());
			} catch (NoSuchAlgorithmException e) {
				log.log(Level.SEVERE, "SSLContext.getInstance with SSL parameter failed", e);
			} catch (KeyManagementException e) {
				log.log(Level.SEVERE, "SSLContext init failed", e);
			}			
			return sc ;
	}
	
	
	private static InputStream getDecodedInputStream(HttpResponse<InputStream> httpResponse, Logger hLog) {
		
	    String encoding = httpResponse.headers().firstValue("Content-Encoding").orElse("") ;
	    try {
	        switch (encoding) {
	            case "":
	                return httpResponse.body();
	            case "gzip":
	                return new GZIPInputStream(httpResponse.body());
	            case "deflate":
	            	return new InflaterInputStream(httpResponse.body());
	            default:
	            	hLog.severe("HttpUtils.getDecodedInputStream - Unexpected Content-Encoding: \n" + readHttpResponseInfos(httpResponse)) ;
	                throw new UnsupportedOperationException("Unexpected Content-Encoding: " + encoding);
	        }
	    } catch (IOException e) {
	    	hLog.log(Level.SEVERE, "HttpUtils.getDecodedInputStream IOException", e) ;
	        throw new UncheckedIOException(e);
	    }
	}

	// Read http response body with decompression if necessary
	public static CharBuffer readHttpResponse(HttpResponse<InputStream> httpResponse, Charset charSet, Logger hLog) {
		return readHttpResponse(httpResponse, null, hLog) ;
	}
	
	// Read http response body with decompression if necessary, and log decompressed content as bytes in a file
	public static CharBuffer readHttpResponse(HttpResponse<InputStream> httpResponse,  Charset charSet, File fileTrace, Logger hLog) {
		ChannelReaderDecoder chan = new ChannelReaderDecoder(
				getDecodedInputStream(httpResponse, hLog),
				charSet,
				fileTrace, 
				RECEIVE_BUF_SIZE,				
				hLog) ;
		return chan.readAllChar() ;
	}
	
	// Read http response body with no interpretation, as bytes
	public static ByteBuffer readHttpResponseAsBytes(HttpResponse<InputStream> httpResponse, Logger hLog) {
		ChannelReaderDecoder chan = new ChannelReaderDecoder(
				httpResponse.body(),
				null,
				null, 
				RECEIVE_BUF_SIZE,				
				hLog) ;
		return chan.readAllBytes() ;
	}
	
	public static String readHttpResponseInfos(HttpResponse<?> httpResponse) {
		return "Status code=" + httpResponse.statusCode() + "\n\n"
				+ httpResponse.headers().toString() ;
	}
	
	public static boolean isResponseCodeSucces(HttpResponse<?> httpResponse) {
		if (httpResponse != null) {
			return (httpResponse.statusCode() >= 200) && (httpResponse.statusCode() < 300) ;
		} else {
			return false ;
		}
	}
}

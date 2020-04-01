package com.ibm.lge.fl.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {

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
}

package com.ibm.lge.fl.util;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// Alternate class to fl.util.httpConnexion
// The aim of those classes is to avoid the use of heavy packages like apache httpClient
public class HttpLink {

	private final static String HTTPS  = "https" ;
	private final static String POST   = "POST" ;
	private final static String GET    = "GET" ;
	private final static String PUT    = "PUT" ;
	private final static String DELETE = "DELETE" ;
	
	private Logger hLog ;
	private URL    url ;
	
	private boolean isHttps ;
	
	private Charset charset ;
	
	// no HTTPS certificate checking
	private boolean noCertCheck ;
	
	// Trust manager that trusts all certificate
	private static TrustManager[] trustAllCerts ;
	
	// HostnameVerifier that ignores differences between given hostname and certificate hostname
	private static HostnameVerifier hostnameVerifier ;
	
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
		
		// Ignore differences between given hostname and certificate hostname
		hostnameVerifier = new HostnameVerifier() {
	      public boolean verify(String hostname, SSLSession session) { return true; }
	    };
	}
	
	// The url string must be already encoded (escape reserved char)
	public HttpLink(String urlEncodedString, Charset cs, Logger l) {
		
		init(urlEncodedString, cs, l) ;
		
	}

	// The url string must be already encoded (escape reserved char)
	public HttpLink(String urlEncodedString, Charset cs, boolean ncc, Logger l) {
		
		init(urlEncodedString, cs, l) ;
		noCertCheck = ncc ;		
	}
	
	private void init(String urlEncodedString, Charset cs, Logger l) {
		
		hLog 		= l ;
		charset 	= cs ;
		//			by default certificate will be checked
		noCertCheck = false ;
		try {
			url = new URL(urlEncodedString) ;
			
			isHttps = url.getProtocol().equalsIgnoreCase(HTTPS) ;
			
		} catch (MalformedURLException e) {
			url = null ;
			hLog.log(Level.SEVERE, "MalformedURLException for url " + urlEncodedString, e);
		}
	}
	
	public HttpResponseContent sendPost(String body, ArrayList<HttpHeader> headerParams) {
		
		return sendHttpRequest(POST, body, headerParams);
	}
	
	public HttpResponseContent sendGet(ArrayList<HttpHeader> headerParams) {
		
		return sendHttpRequest(GET, null, headerParams);
	}

	public HttpResponseContent sendDelete(ArrayList<HttpHeader> headerParams) {
		
		return sendHttpRequest(DELETE, null, headerParams);
	}

	public HttpResponseContent sendPut(String body, ArrayList<HttpHeader> headerParams) {
		
		return sendHttpRequest(PUT, body, headerParams);
	}

	private HttpResponseContent sendHttpRequest(String method, String body, ArrayList<HttpHeader> headerParams) {
		
		HttpResponseContent result = null ;
		if (url != null) {
			
			if (hLog.isLoggable(Level.FINEST)) {
				StringBuffer buff = new StringBuffer() ;
				buff.append("Sending ") ;
				if (isHttps) {
					buff.append("HTTPS ") ;
				} else {
					buff.append("HTTP ") ;
				}
				buff.append(method).append(" to ").append(url.toString()).append(" with body=\n" ).append(body) ; 
				hLog.fine(buff.toString());
			}
			
			try {
			
				// open the connection
				if (isHttps) {
					
					HttpsURLConnection scon = (HttpsURLConnection)url.openConnection() ;
					
					if (noCertCheck) {
						// Install the all-trusting trust manager for this connection
						try {
							SSLContext sc = SSLContext.getInstance("SSL");
							sc.init(null, trustAllCerts, new SecureRandom());
							scon.setSSLSocketFactory(sc.getSocketFactory());
							scon.setHostnameVerifier(hostnameVerifier) ;
						} catch (Exception e) {
							hLog.log(Level.SEVERE, "Exception when trying to create a trust manager that disable certificate validation", e) ;
						}						
					}
					
					// Receive response
					result = getHttpResponseContent(scon, method, body, headerParams) ;
					
				} else {
					
					HttpURLConnection con = (HttpURLConnection)url.openConnection() ;
					
					// Receive response
					result = getHttpResponseContent(con, method, body, headerParams) ;
				}
										
				if (hLog.isLoggable(Level.FINEST)) {
					hLog.fine("HTTP request result:\n " + result.toString());
				}				
				
			} catch (Exception e) {
				hLog.log(Level.SEVERE, "Exception when sending post to url " + url.toString(), e);
			}
		
		} else {
			hLog.severe("Cannot send http request : url is null") ; 
		}
		return result ;
	}
	
	private HttpResponseContent getHttpResponseContent(HttpURLConnection con, String method, String body, ArrayList<HttpHeader> headerParams) {
		
		HttpResponseContent result = null ;			
		try {

			// set method
			con.setRequestMethod(method);
			
			// set the headers, if any
			if (headerParams != null) {
				for (HttpHeader httpHeader : headerParams) {
					con.setRequestProperty(httpHeader.getKey(), httpHeader.getValue());
					if (hLog.isLoggable(Level.FINEST)) {
						hLog.finest("Header key=" + httpHeader.getKey() + "; header value=" + httpHeader.getValue());
					}
				}
			}
			
			// process body if any
			if ((body != null) && (! body.isEmpty())) {
				con.setDoOutput(true) ;
				con.setRequestProperty("Content-Length", Integer.toString(body.length()));
				
				// send the body
				try (BufferedWriter wr =  new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), charset))) {

					wr.append(body) ;
					wr.flush() ;
					
				} catch (Exception e1) {
					hLog.log(Level.SEVERE, "Exception writing outputstream on url " + url.toString(), e1);
				}
			}
			
			// Receive response
			result = new HttpResponseContent(con, charset, hLog) ;
			
			con.disconnect();
		} catch (Exception e) {
			hLog.log(Level.SEVERE, "Exception when sending post to url " + url.toString(), e);
		}

		return result ;
	}
}

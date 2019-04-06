package com.ibm.lge.fl.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.ibm.lge.fl.util.json.JsonUtils;

public class HttpConnexion {

	private static final String HTTP = "http://" ;
	private static final String HTTPS =  "https://" ;
	private static final String cookieKey = "Set-Cookie" ;
	private static final String COOKIE = "Cookie";
	private static final char cookieFieldSeparator = ';' ;
	
	// Host and port for connexion
	private String host ;
	private String port ;
	private boolean secureHttp ;
	
	// manage session cookie
	private boolean sessionCookie ;
	// cookies from the first request
	private List<String> cookies ;
	// cookie string to be added on subsequent request
	private String cookieString ;
	
	private Logger hLog;
	
	private Charset charset ;
	
	public HttpConnexion(String hHost, String hPort, boolean sCookie, boolean sHttp, boolean disableCertificateValidation, Charset cs, Logger l) {
		
		host = hHost ;
		port = hPort ;
		secureHttp = sHttp ;
		hLog = l ;
		sessionCookie = sCookie ;
		cookies = null ;
		charset = cs ;
		
		if (disableCertificateValidation) disableCertificateValidation(null, hLog) ;
	}

	public HttpResponseContent getHttp(String path, String parameter, boolean decompressIfCompressed) {
		
		HttpResponseContent result = null ;
		
		URL url = createUrl(path + parameter) ;
		if (hLog.isLoggable(Level.FINEST)) {
			hLog.finest("Http get on url : " + url.toString());
		}
		try {
			
			if (url != null) {
				HttpURLConnection con ;
				if (secureHttp) {
					con = (HttpsURLConnection)url.openConnection() ;
				} else {
					con = (HttpURLConnection)url.openConnection() ;
				}
				
				con.setUseCaches(false);
		        con.setRequestMethod("GET");
		        con.setDoInput(true);
		        con.setDoOutput(true);
		        con.setRequestProperty("Accept", "application/json; charset=" + charset.name());
		        con.setRequestProperty("Accept-Charset", charset.name());
		        
		        if ((sessionCookie) && (cookies != null)) {
		        	con.setRequestProperty(COOKIE, cookieString);	
		        }
		        
				con.connect();
				
				// eventually store cookies
				if ((sessionCookie) && (cookies == null)) {
					cookies = getCookies(con) ;
					cookieString = buildCookieString(cookies) ;
				}
				
				result = getResponse(con, decompressIfCompressed);
				
				if (hLog.isLoggable(Level.FINEST)) {
					hLog.finest("HTTP GET result:\n " + result.toString());
				}
				
				con.disconnect();
			}
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "IOException when in http get on url " + url.toString(), e);
		}
		return result ;
	}
	
	public HttpResponseContent postHttp(String path, String parameter, String content, boolean decompressIfCompressed) {
		
		HttpResponseContent result = null ;
		
		URL url = createUrl(path + parameter) ;
		if (hLog.isLoggable(Level.FINEST)) {
			hLog.finest("Http post on url : " + url.toString());
		}
		try {
			
			if (url != null) {
				HttpURLConnection con  ;
				if (secureHttp) {
					con = (HttpsURLConnection)url.openConnection() ;
				} else {
					con = (HttpURLConnection)url.openConnection() ;
				}

				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setRequestProperty("Content-Type", "application/json; charset=" + charset.name());
				con.setRequestProperty("Accept-Charset", charset.name());

				if ((sessionCookie) && (cookies != null)) {
					con.setRequestProperty(COOKIE, cookieString);	
				}

				// POST content	        
				writeContent(con, content);
				if (hLog.isLoggable(Level.FINEST)) {
					hLog.finest("Content written to POST:\n" +  JsonUtils.jsonPrettyPrint(content, hLog));
				}
				con.connect();

				// eventually store cookies
				if ((sessionCookie) && (cookies == null)) {
					cookies = getCookies(con) ;
					cookieString = buildCookieString(cookies) ;
				}

				result = getResponse(con, decompressIfCompressed);

				if (hLog.isLoggable(Level.FINEST)) {
					hLog.finest("HTTP POST result:\n " + result.toString());
				}

				con.disconnect();
			}
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "IOException in Http POST on url" + url.toString(), e);
		}
		return result ;
	}
	
	public HttpResponseContent deleteHttp(String path, String parameter, boolean decompressIfCompressed) {
		
		HttpResponseContent result = null ;
		
		URL url = createUrl(path + parameter) ;
		if (hLog.isLoggable(Level.FINEST)) {
			hLog.finest("Http get on url : " + url.toString());
		}
		try {
			
			if (url != null) {
				HttpURLConnection con ;
				if (secureHttp) {
					con = (HttpsURLConnection)url.openConnection() ;
				} else {
					con = (HttpURLConnection)url.openConnection() ;
				}
				
				con.setUseCaches(false);
		        con.setRequestMethod("DELETE");
		        con.setDoInput(true);
		        con.setDoOutput(true);
		        con.setRequestProperty("Accept", "application/json; charset=" + charset.name());
		        con.setRequestProperty("Accept-Charset", charset.name());
		        
		        if ((sessionCookie) && (cookies != null)) {
		        	con.setRequestProperty(COOKIE, cookieString);	
		        }
		        
				con.connect();
				
				// eventually store cookies
				if ((sessionCookie) && (cookies == null)) {
					cookies = getCookies(con) ;
					cookieString = buildCookieString(cookies) ;
				}
				
				result = getResponse(con, decompressIfCompressed);
				
				if (hLog.isLoggable(Level.FINEST)) {
					hLog.finest("HTTP DELETE result:\n " + result.toString());
				}
				
				con.disconnect();
			}
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "IOException when in http delete on url " + url.toString(), e);
		}
		return result ;
	}
	
	public static void disableCertificateValidation(HttpsURLConnection con, Logger log) {
	    // Create a trust manager that does not validate certificate chains
	    TrustManager[] trustAllCerts = new TrustManager[] { 
	      new X509TrustManager() {
	        public X509Certificate[] getAcceptedIssuers() { 
	          return new X509Certificate[0]; 
	        }
	        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
	        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	    }};

	    // Ignore differences between given hostname and certificate hostname
	    HostnameVerifier hv = new HostnameVerifier() {
	      public boolean verify(String hostname, SSLSession session) { return true; }
	    };

	    // Install the all-trusting trust manager
	    try {
	      SSLContext sc = SSLContext.getInstance("SSL");
	      sc.init(null, trustAllCerts, new SecureRandom());
	      if (con != null) {
		      con.setSSLSocketFactory(sc.getSocketFactory());
		      con.setHostnameVerifier(hv);	    	  
	      } else {
		      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		      HttpsURLConnection.setDefaultHostnameVerifier(hv);
	      }
	    } catch (Exception e) {
	    	if (log != null) {
	    		log.log(Level.SEVERE, "Exception when trying to create a trust manager that disable certificate validation", e) ;
	    	}
	    }
	  }
	
	private List<String> getCookies(HttpURLConnection con) {
		Map<String, List<String>> headerFields = con.getHeaderFields() ;
		return headerFields.get(cookieKey) ;
	}
	
	private String buildCookieString(List<String> cookies) {
		
		StringBuilder buf = new StringBuilder("") ;
		if ((cookies != null) && (cookies.size() > 0)) {
			for (String cookie : cookies) {
				buf.append(cookie.substring(0, cookie.indexOf(cookieFieldSeparator) + 1)) ;
			}
			// remove the last separator
			buf.setLength(buf.length() - 1);
		}
		return buf.toString() ;
	}
	
	private HttpResponseContent getResponse(HttpURLConnection con, boolean decompressIfCompressed) {
		return new HttpResponseContent(con, charset, decompressIfCompressed, hLog) ;
	}
	
	
	private void writeContent(HttpURLConnection con, String content)  {

		try {    
			OutputStream os = con.getOutputStream();
			OutputStreamWriter osr = new OutputStreamWriter(os, charset);
			PrintWriter pw = new PrintWriter(osr);
			pw.write(content);
			pw.flush();
			pw.close();
		} catch (UnsupportedEncodingException e) {
			hLog.log(Level.SEVERE, "UnsupportedEncodingException when writing REST post content", e); 
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "IOException when writing REST post content", e);  
		}

	}
	
	private URL createUrl(String path) {
		
		StringBuilder buf = new StringBuilder() ;
		
		if (secureHttp) {
			buf.append(HTTPS) ;
		} else {
			buf.append(HTTP) ;
		}
		
		buf.append(host) ;
		if ((port != null) && (! port.isEmpty())) {
			buf.append(':').append(port) ;
		}
		buf.append(path) ;
		try {
			return new URL(buf.toString()) ;
		} catch (MalformedURLException e) {
			
			hLog.log(Level.SEVERE, "MalformedURLException for url " + buf, e);
			return null ;
		}
	}
	
}

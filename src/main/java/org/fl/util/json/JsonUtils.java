package org.fl.util.json;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

	// pretty print Json string
	public static String jsonPrettyPrint(String rawJson, Logger uLog) {
	
		String res = "" ;
		if ((rawJson != null) && (! rawJson.isEmpty())) {
			try {
				JsonObject json = JsonParser.parseString(rawJson).getAsJsonObject();
	
				res = jsonPrettyPrint(json);
	
			} catch (Exception e) {
				uLog.log(Level.SEVERE, "Exception when pretty printing json string\n" + rawJson, e);
				res = rawJson ;
			}
		}
		return res;	
	}
	
	// pretty print Json object
	public static String jsonPrettyPrint(JsonObject jsonObject) {
	
		GsonBuilder gsonBuilder = new GsonBuilder() ;
		gsonBuilder.disableHtmlEscaping() ;
		Gson gson = gsonBuilder.setPrettyPrinting().create();

		return gson.toJson(jsonObject);	
	}
	
	// Read a JsonObject from an input stream
	// If the stream is empty , an empty JsonObject is returned (can be checked with size() method)
	// If there is a processing error, null is returned
	public static JsonObject getJsonObjectFromInputStream(InputStream is,  Charset cs, Logger cLog) {
		
		JsonObject jsonObject = null ;
		StringBuilder out = null ;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, cs))) {
		
			if (cLog.isLoggable(Level.FINEST)) {
				// Put the input in a StringBuilder to be able to log it
				 out = new StringBuilder();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            out.append(line);
		        }
		        
		        if (out != null) {
		        	if (out.length() > 0) {
		        		String outString = out.toString() ;
		        		cLog.finest("getJsonObjectFromInputStream: String read from input " + outString) ;
		        		// parse the POST body to get a JsonObject
		        		jsonObject = JsonParser.parseString(outString).getAsJsonObject() ;
		        	} else {
		        		// empty json object
		        		jsonObject = new JsonObject() ;
		        	}
		        }
			} else {
				jsonObject = JsonParser.parseReader(reader).getAsJsonObject() ;
			}
	        
		} catch (Exception e1) {
			jsonObject = null ;
			cLog.log(Level.SEVERE, "Exception reading json input stream", e1) ;
			if (cLog.isLoggable(Level.FINEST)) {
				if (out != null) {
					cLog.finest("getJsonObjectFromInputStream: String read from input " + out.toString()) ;
				} else {
					cLog.finest("getJsonObjectFromInputStream: StringBuilder for storing input is null");
				}
			}
		}
		return jsonObject ;
		
	}
	
	// Read a JsonObject from a path
	public static JsonObject getJsonObjectFromPath(Path path, Charset cs, Logger cLog) {
		
		JsonObject jsonObject = null ;
		StringBuilder out = null ;

		try (BufferedReader reader = Files.newBufferedReader(path, cs)) {
			
			if (cLog.isLoggable(Level.FINEST)) {
				// Put the input in a StringBuilder to be able to log it
				out = new StringBuilder();
		        String line;
		        while ((line = reader.readLine()) != null) {
		            out.append(line);
		        }
		        
		        if (out != null) {
		        	if (out.length() > 0) {
		        		String outString = out.toString() ;
		        		cLog.finest("getJsonObjectFromInputStream: String read from input " + outString) ;

		        		// parse the POST body to get a JsonObject
		        		jsonObject = JsonParser.parseString(outString).getAsJsonObject() ;
		        	} else {
		        		// empty json object
		        		jsonObject = new JsonObject() ;
		        	}
		        }
			} else {
				jsonObject = JsonParser.parseReader(reader).getAsJsonObject() ;
			}
	        
		} catch (Exception e) {
			cLog.log(Level.SEVERE, "Erreur en lisant le fichier " + path, e) ;
		}
		return jsonObject ;
	}
	
	public static String getAsStringOrNull(JsonElement jElem) {
		if (jElem != null) {
			return jElem.getAsString() ;
		} else {
			return null ;
		}
	}
	
	public static String getAsStringOrBlank(JsonElement jElem) {
		if (jElem != null) {
			return jElem.getAsString() ;
		} else {
			return "" ;
		}
	}
}
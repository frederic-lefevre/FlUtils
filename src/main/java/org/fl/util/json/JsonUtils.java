/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.json;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.JsonNodeFactory;

public class JsonUtils {

	private static final ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
	
	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	// pretty print Json string
	public static String jsonStringPrettyPrint(String rawJson, Logger logger) {
	
		if ((rawJson != null) && (! rawJson.isEmpty())) {
			try {
				return mapper.writeValueAsString(mapper.readValue(rawJson, Object.class));	
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Exception when pretty printing json string\n" + rawJson, e);
				return rawJson ;
			}
		} else {
			return rawJson;
		}

	}
	
	// pretty print Json object
	public static String jsonPrettyPrint(JsonNode jsonNode) throws JacksonException {
	
		return mapper.writeValueAsString(jsonNode);
	}
	
	// Read a JsonNode from an input stream
	// If the stream is empty , an empty JsonNode is returned (can be checked with size() method)
	// If there is a processing error, null is returned
	public static JsonNode getJsonObjectFromInputStream(InputStream is, Charset cs, Logger cLog) {

		StringBuilder out = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, cs))) {

			if (cLog.isLoggable(Level.FINEST)) {
				// Put the input in a StringBuilder to be able to log it
				out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}

				if (out.length() > 0) {
					String outString = out.toString();
					cLog.finest("getJsonObjectFromInputStream: String read from input " + outString);
					// parse the POST body to get a JsonObject
					return mapper.readTree(outString);
				} else {
					// empty json object
					return JsonNodeFactory.instance.objectNode();
				}

			} else {
				return mapper.readTree(reader);
			}

		} catch (Exception e1) {
			cLog.log(Level.SEVERE, "Exception reading json input stream", e1);
			if (cLog.isLoggable(Level.FINEST)) {
				if (out != null) {
					cLog.finest("getJsonObjectFromInputStream: String read from input " + out.toString());
				} else {
					cLog.finest("getJsonObjectFromInputStream: StringBuilder for storing input is null");
				}
			}
			return null;
		}
	}
	
	// Read a JsonObject from a path
	public static JsonNode getJsonObjectFromPath(Path path, Charset cs, Logger cLog) {

		try (BufferedReader reader = Files.newBufferedReader(path, cs)) {

			if (cLog.isLoggable(Level.FINEST)) {

				cLog.finest("About to read json file " + path);

				// Put the input in a StringBuilder to be able to log it
				StringBuilder out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}

				if (out.length() > 0) {
					String outString = out.toString();
					cLog.finest("getJsonObjectFromInputStream: String read from input " + outString);

					// parse the POST body to get a JsonObject
					return mapper.readTree(outString);
				} else {
					// empty json object

					cLog.finest(path + " is an empty file");
					return JsonNodeFactory.instance.objectNode();
				}

			} else {
				return mapper.readTree(reader);
			}

		} catch (Exception e) {
			cLog.log(Level.SEVERE, "Erreur en lisant le fichier " + path, e);
			return null;
		}
	}
	
	public static String getAsStringOrNull(JsonNode jElem) {
		if (jElem != null) {
			return jElem.asString() ;
		} else {
			return null ;
		}
	}
	
	public static String getAsStringOrBlank(JsonNode jElem) {
		if (jElem != null) {
			return jElem.asString() ;
		} else {
			return "" ;
		}
	}
}
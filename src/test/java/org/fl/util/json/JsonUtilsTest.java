/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.logging.Logger;

import org.fl.util.LoggerCounter;

class JsonUtilsTest {

	private final static Logger logger = Logger.getLogger(JsonUtilsTest.class.getName());
	
	@Test
	void shouldAcceptNullString() {	
		assertThat(JsonUtils.jsonStringPrettyPrint(null, logger)).isNull();
	}
	
	@Test
	void shouldAcceptEmptyString() {
		
		assertThat(JsonUtils.jsonStringPrettyPrint("", logger)).isEmpty();
	}
	
	@Test
	void shouldAcceptNonJsonString() {
		LoggerCounter noLog = LoggerCounter.getLogger();
		String nonJsonString = " ** Not a Json string";
		assertThat(JsonUtils.jsonStringPrettyPrint(nonJsonString, noLog)).isEqualTo(nonJsonString);
	}
	
	private static final String rawAlbumStr = """
{"titre": "Portrait in jazz","format":{"cd": 1,"audioFiles":[{"bitDepth": 16 ,"samplingRate":44.1,"source" : "MOFI Fidelity Sound Lab", "type" : "FLAC"}]}, 
"auteurCompositeurs":[{"nom":"Evans","prenom":"Bill","naissance":"1929-08-16","mort":"1980-09-15"}],    								
"enregistrement":["1959-12-28","1959-12-28"],"liensUrl":["http://somwhere"]}""" ;
	
	private static final String prettyAlbumStr = """
{
  "titre" : "Portrait in jazz",
  "format" : {
    "cd" : 1,
    "audioFiles" : [ {
      "bitDepth" : 16,
      "samplingRate" : 44.1,
      "source" : "MOFI Fidelity Sound Lab",
      "type" : "FLAC"
    } ]
  },
  "auteurCompositeurs" : [ {
    "nom" : "Evans",
    "prenom" : "Bill",
    "naissance" : "1929-08-16",
    "mort" : "1980-09-15"
  } ],
  "enregistrement" : [ "1959-12-28", "1959-12-28" ],
  "liensUrl" : [ "http://somwhere" ]
}""" ;

	@Test
	void shouldPrettyPrintString() {
		
		// --Debugging help
		//prettyAlbumStr.chars().filter(c -> (c != 10) && (c != 13)).forEach(c -> {System.out.print(Character.toString(c)); System.out.print('|');});
		//System.out.println();
		//JsonUtils.jsonStringPrettyPrint(rawAlbumStr).chars().filter(c -> (c != 10) && (c != 13)).forEach(c -> {System.out.print(Character.toString(c)); System.out.print('|');});
		//System.out.println();
		//prettyAlbumStr.chars().filter(c -> (c != 10) && (c != 13)).forEach(c -> {System.out.print(c); System.out.print('|');});
		//JsonUtils.jsonStringPrettyPrint(rawAlbumStr).chars().filter(c -> (c != 10) && (c != 13)).forEach(c -> {System.out.print(c); System.out.print('|');});
		//System.out.println();
		// --
		
		assertThat(JsonUtils.jsonStringPrettyPrint(rawAlbumStr, logger)).isEqualToNormalizingNewlines(prettyAlbumStr);
	}
}

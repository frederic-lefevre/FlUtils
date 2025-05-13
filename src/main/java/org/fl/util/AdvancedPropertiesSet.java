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

package org.fl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//
// Class to generate a list of AdvancedProperties from a set of files
//
public class AdvancedPropertiesSet {

	private final List<AdvancedProperties> apList;

	public AdvancedPropertiesSet(FileSet fs, Logger log) {

		apList = fs.getFileList().stream()
			.map(path -> {
				Properties prop = new Properties();
				try (BufferedReader reader = Files.newBufferedReader(path)) {			
					prop.load(reader);
				} catch (IOException e) {
					log.log(Level.SEVERE, "IO Exception when loading properties file " + Objects.toString(path), e);
				}
				return new AdvancedProperties(prop, log);
			})
			.toList();	
	}

	public List<AdvancedProperties> getApList() {
		return apList;
	}
	
}

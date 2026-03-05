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

package org.fl.util.file.multiThreadedTransformer;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SampleItemProcessor extends ItemProcessor {

	private static final Logger logger = Logger.getLogger(SampleItemProcessor.class.getName());
			
	private String linePrefix;

	public SampleItemProcessor(String lp) {
		linePrefix = lp;
	}
	
	@Override
	public ItemProcessor getClone() {
		return new SampleItemProcessor(linePrefix);
	}

	@Override
	public CharSequence processItem(ArrayList<String> currentEntry) {
		StringBuilder xFormLines = new StringBuilder();
		
		// Intervertit les lignes et ajoute un prefix
		for (int lineNum = currentEntry.size()-1; lineNum > -1; lineNum--) {
			String line = currentEntry.get(lineNum);
			if (line.startsWith("ATYPIC LINE")) {
				try {
					atypicEntries.put("Atypic line found=" + line);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "Exception when processing atypic line" + line, e);
				}
			}
			xFormLines.append(linePrefix).append(lineNum).append(line).append("\n");
		}
		return xFormLines;
	}
	
	
}

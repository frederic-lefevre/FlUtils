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

package org.fl.util.swing.logConfiguration;

import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import javax.swing.JComboBox;

import org.fl.util.JsonLogFormatter;
import org.fl.util.PlainLogFormatter;

public class FormatterComboBox extends JComboBox<String> {

	private static final long serialVersionUID = 1L;

	private static final String[] AVAILABLE_FORMATTER_NAMES = {
			SimpleFormatter.class.getName(),
			XMLFormatter.class.getName(),
			JsonLogFormatter.class.getName(), 
			PlainLogFormatter.class.getName()};
	
	public FormatterComboBox() {	
		super(AVAILABLE_FORMATTER_NAMES);	
	}
	
	public String[] getAvailableFormatterNames() {
		return AVAILABLE_FORMATTER_NAMES;
	}
	
	public static Formatter getNewChoosenFormatter(String formatterName) {
		
		if (SimpleFormatter.class.getName().equals(formatterName)) {
			return new SimpleFormatter();
		} else if (PlainLogFormatter.class.getName().equals(formatterName)) {
			return new PlainLogFormatter();
		} else if (JsonLogFormatter.class.getName().equals(formatterName)) {
			return new JsonLogFormatter();
		} else if (XMLFormatter.class.getName().equals(formatterName)) {
			return new XMLFormatter();
		} else {
			return null;
		}
	}
}

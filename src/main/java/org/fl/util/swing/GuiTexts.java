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

package org.fl.util.swing;

import java.util.Locale;
import java.util.ResourceBundle;

public class GuiTexts {
	
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	
	private static GuiTexts instance;
	private final ResourceBundle textResource;
	
	static void init(Locale locale) {
		instance = new GuiTexts(locale);
	}
	
	static void reset() {
		instance = null;
	}
	
	static Locale getDefaultLocale() {
		return DEFAULT_LOCALE;
	}
	
	private GuiTexts() {
		textResource = ResourceBundle.getBundle("ApplicationTabPane", DEFAULT_LOCALE);
	}
	
	private GuiTexts(Locale locale) {
		ResourceBundle localTextResource = ResourceBundle.getBundle("ApplicationTabPane", locale);
		if (!localTextResource.getLocale().getLanguage().equals(locale.getLanguage()) &&
				(!localTextResource.getLocale().getLanguage().equals(DEFAULT_LOCALE.getLanguage()))) {
			// The language of the returned bundle is neither the one requested nor the DEFAULT_LOCALE one
			// (it is surely the Locale.getDefault() language)
			textResource = ResourceBundle.getBundle("ApplicationTabPane", DEFAULT_LOCALE);			
		} else {
			textResource = localTextResource;
		}
	}
	
	private static GuiTexts getInstance() {
		if (instance == null) {
			instance = new GuiTexts();
		}
		return instance;
	}
	
	public static String getText(String textKey) {
		return getInstance().textResource.getString(textKey);
	}
}

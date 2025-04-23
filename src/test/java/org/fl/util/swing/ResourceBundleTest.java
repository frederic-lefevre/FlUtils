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

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

class ResourceBundleTest {

	@Test
	void testLoadingFrenchResource() {
		
		ResourceBundle resource = ResourceBundle.getBundle("ApplicationTabPane", Locale.FRENCH);
		
		assertThat(resource).isNotNull();
		assertThat(resource.getString("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Configuration des logs");
	}
	
	@Test
	void testLoadingFranceResource() {
		
		ResourceBundle resource = ResourceBundle.getBundle("ApplicationTabPane", Locale.FRANCE);
		
		assertThat(resource).isNotNull();
		assertThat(resource.getString("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Configuration des logs");
	}
	
	@Test
	void testLoadingUKResource() {
		
		ResourceBundle resource = ResourceBundle.getBundle("ApplicationTabPane", Locale.UK);
		
		assertThat(resource).isNotNull();
		assertThat(resource.getString("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Log configuration");
	}
	
	@Test
	void testLoadingEnglishResource() {
		
		ResourceBundle resource = ResourceBundle.getBundle("ApplicationTabPane", Locale.ENGLISH);
		
		assertThat(resource).isNotNull();
		assertThat(resource.getString("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Log configuration");
	}
	
	@Test
	void testLoadingUSResource() {
		
		ResourceBundle resource = ResourceBundle.getBundle("ApplicationTabPane", Locale.US);
		
		assertThat(resource).isNotNull();
		assertThat(resource.getString("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Log configuration");
	}
	
	@Test
	void testBundlePresence() {
		
		ResourceBundle resourceFr = ResourceBundle.getBundle("ApplicationTabPane", Locale.FRENCH);
		assertThat(resourceFr.getLocale()).isEqualTo(Locale.FRENCH);
		
		ResourceBundle resourceUs = ResourceBundle.getBundle("ApplicationTabPane", Locale.ENGLISH);
		assertThat(resourceUs.getLocale()).isEqualTo(Locale.ENGLISH);
		
		ResourceBundle resourceDe = ResourceBundle.getBundle("ApplicationTabPane", Locale.GERMANY);
		assertThat(resourceDe.getLocale()).isNotEqualTo(Locale.GERMANY);
	}
	
	@Test
	void testKeysPresence() {
		
		ResourceBundle resourceFr = ResourceBundle.getBundle("ApplicationTabPane", Locale.FRENCH);		
		ResourceBundle resourceUs = ResourceBundle.getBundle("ApplicationTabPane", Locale.ENGLISH);
		
		resourceFr.getKeys().asIterator().forEachRemaining(key -> assertThat(resourceUs.containsKey(key)).isTrue());
		resourceUs.getKeys().asIterator().forEachRemaining(key -> assertThat(resourceFr.containsKey(key)).isTrue());
	}
}

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

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class PropertiesStorageTest {
	
	@Test
	void testPropertiesStorageWithUriNullParam() throws Exception {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(PropertiesStorage.class.getName()));
		
		PropertiesStorage ps = new PropertiesStorage(null, (URI)null);
		
		testPropertiesStorageWithNullParam(ps);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(3);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(2);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}
	
	@Test
	void testPropertiesStorageWithPathNullParam() throws Exception {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(PropertiesStorage.class.getName()));
		
		PropertiesStorage ps = new PropertiesStorage(null, (Path)null);
		
		testPropertiesStorageWithNullParam(ps);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(3);
		assertThat(logRecordCounter.getLogRecordCount(Level.WARNING)).isEqualTo(2);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}
	
	private void testPropertiesStorageWithNullParam(PropertiesStorage ps) {
			
		assertThat(ps).isNotNull();	
		assertThat(ps.getPropertyLocation()).isNull();
		
		AdvancedProperties props = ps.getAdvanced(null);
		assertThat(props).isNotNull().isEmpty();
	}
	
	@Test
	void testPropertiesStorageWithInvalidSystemProperty() throws Exception {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(PropertiesStorage.class.getName()));
		
		assertThatExceptionOfType(URISyntaxException.class)
			.isThrownBy(() ->
				 new PropertiesStorage( "os.name", (URI)null));
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		
	}
}

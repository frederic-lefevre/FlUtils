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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class PropertiesStorageTest {
	
	@Test
	void testPropertiesStorageWithUriNullParam() throws Exception {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(PropertiesStorage.class.getName()));
		
		assertThatNullPointerException().isThrownBy(() -> new PropertiesStorage(null));
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}
	
	@Test
	void testPropertiesStorageWithUnexistantUri() throws Exception {
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(PropertiesStorage.class.getName()));
		
		PropertiesStorage ps = new PropertiesStorage(new URI("file:///tmp/doesNotExists.properties"));
		
		assertThat(ps).isNotNull();	
		assertThat(ps.getPropertyLocation()).isNotNull().asString().endsWith("/tmp/doesNotExists.properties");
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull().isEmpty();
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}
	
	@Test
	void testPropertiesStorageWithUri() throws URISyntaxException, Exception {
		
		URI propertyUri = new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties");
		
		PropertiesStorage ps = new PropertiesStorage(propertyUri);
		
		assertThat(ps).isNotNull();	
		assertThat(ps.getPropertyLocation()).isNotNull();
		assertThat(ps.getPropertyLocation().toString()).isEqualTo(propertyUri.toURL().toString());
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		assertThat(props.get("doesNotExist")).isNull();
		assertThat(props.get("logging.CloudantLogHandler.encode")).isEqualTo("UTF-8");
	}
	
	@Test
	void testPropertiesStorageWithRelativeUri() throws URISyntaxException, Exception {
		
		URI propertyUri = new URI("test1.properties");
		
		PropertiesStorage ps = new PropertiesStorage(propertyUri);
		
		assertThat(ps).isNotNull();	
		assertThat(ps.getPropertyLocation()).isNotNull();
		assertThat(ps.getPropertyLocation().toString()).endsWith(propertyUri.toString());
		
		AdvancedProperties props = ps.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		assertThat(props.get("doesNotExist")).isNull();
		assertThat(props.get("logging.CloudantLogHandler.encode")).isEqualTo("UTF-8");
	}
	
	@Test
	void testPropertiesStorageSave() throws URISyntaxException, Exception {
		
		// Source properties
		URI propertyUri = new URI("file:///FredericPersonnel/EclipseOxygenWorkspace/FlUtils/src/test/resources/test1.properties");
		PropertiesStorage propertySource = new PropertiesStorage(propertyUri);
		
		// Save property to another location
		URI propertyCopyUri = new URI("file:///ForTests/FlUtils/test1.properties");
		propertySource.save(propertyCopyUri);
		
		// Read back saved properties
		PropertiesStorage propertyCopied = new PropertiesStorage(propertyCopyUri);
		
		assertThat(propertyCopied).isNotNull();	
		assertThat(propertyCopied.getPropertyLocation()).isNotNull();
		assertThat(propertyCopied.getPropertyLocation().toString()).isEqualTo(propertyCopyUri.toURL().toString());
		
		AdvancedProperties props = propertyCopied.getAdvancedProperties();
		assertThat(props).isNotNull();
		
		assertThat(props.get("doesNotExist")).isNull();
		assertThat(props.get("logging.CloudantLogHandler.encode")).isEqualTo("UTF-8");
		
		// Delete copied properties
		Files.delete(Paths.get(propertyCopyUri));
	}

}

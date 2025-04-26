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

package org.fl.util.file;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class FilesUtilsTest {

	private static final Logger logger = Logger.getLogger(FilesUtilsTest.class.getName());
	
	@Test
	void testshouldFindFileStore() throws IOException {
		
		Path unexistantPath = Path.of(URI.create("file:///C:/ForTests/FlUtils/does/not/exists"));
		Path existantPath = Path.of(URI.create("file:///C:/ForTests/FlUtils"));
		
		assertThat(unexistantPath).doesNotExist();
		assertThat(existantPath).exists();
		
		FileStore fileStoreOfUnexistantPath = FilesUtils.findFileStore(unexistantPath, logger);
		FileStore fileStoreOfExistantPath = FilesUtils.findFileStore(unexistantPath, logger);
		
		assertThat(fileStoreOfUnexistantPath).isNotNull()
			.isEqualTo(fileStoreOfExistantPath)
			.isEqualTo(Files.getFileStore(existantPath));
	}
}

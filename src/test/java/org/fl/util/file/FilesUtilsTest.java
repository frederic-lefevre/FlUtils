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

package org.fl.util.file;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class FilesUtilsTest {

	private static final Logger logger = Logger.getLogger(FilesUtilsTest.class.getName());
	
	@Test
	void testshouldFindFileStore() throws IOException {
		
		Path unexistantPath = Path.of(URI.create("file:///ForTests/FlUtils/does/not/exists"));
		Path existantPath = Path.of(URI.create("file:///ForTests/FlUtils"));
		
		assertThat(unexistantPath).doesNotExist();
		assertThat(existantPath).exists();
		
		FileStore fileStoreOfUnexistantPath = FilesUtils.findFileStore(unexistantPath);
		FileStore fileStoreOfExistantPath = FilesUtils.findFileStore(existantPath);
		
		assertThat(fileStoreOfUnexistantPath).isNotNull()
			.isEqualTo(fileStoreOfExistantPath)
			.isEqualTo(Files.getFileStore(existantPath));
	}
	
	@Test
	void testshoulNotdFindFileStore() throws IOException {
		
		Path unexistantPath = Path.of(URI.create("file:///X:/ForTests/FlUtils/does/not/exists"));
		
		assertThat(unexistantPath).doesNotExist();
		
		FileStore fileStoreOfUnexistantPath = FilesUtils.findFileStore(unexistantPath);
		
		assertThat(fileStoreOfUnexistantPath).isNull();
	}
	
	@Test
	void testshouldFindMountPoint() throws URISyntaxException {
		
		Path unexistantPath = FilesUtils.uriStringToAbsolutePath("file:///ForTests/FlUtils/does/not/exists");
		Path existantPath = FilesUtils.uriStringToAbsolutePath("file:///ForTests/FlUtils");
		
		assertThat(unexistantPath).doesNotExist();
		assertThat(existantPath).exists();
		
		Path mountPointOfUnexistantPath = FilesUtils.findMountPoint(unexistantPath, logger);
		Path mountPointOfExistantPath = FilesUtils.findMountPoint(existantPath, logger);
		
		assertThat(mountPointOfUnexistantPath).isNotNull()
			.isEqualTo(mountPointOfExistantPath)
			.isEqualTo(FilesUtils.uriStringToAbsolutePath("file:///"));
	}
	
	@Test
	void shouldGetAbsolutePathFromUriWithWindowsDrive() throws Exception {
		
		String srcPath = "file:///C:/FredericPersonnel/photos/";
		
		Path path = FilesUtils.uriStringToAbsolutePath(srcPath);
		
		assertThat(path).isNotNull().isAbsolute().exists();
	}
	
	@Test
	void shouldGetAbsolutePathFromUriWithoutWindowsDrive() throws Exception {
		
		String srcPath = "file:///FredericPersonnel/photos/";
		
		Path path = FilesUtils.uriStringToAbsolutePath(srcPath);
		
		assertThat(path)
			.isNotNull()
			.isAbsolute()
			.exists()
			.isEqualTo(FilesUtils.uriStringToAbsolutePath("file:///C:/FredericPersonnel/photos/"))
			.isEqualTo(Path.of("C:\\FredericPersonnel\\photos"))
			.isNotEqualTo(Path.of("\\FredericPersonnel\\photos"));
	}
	
	@Test
	void shouldGetAbsolutePathFromUriWithUpperCaseScheme() throws Exception {
		
		String srcPath = "FILE:///FredericPersonnel/photos/";
		
		Path path = FilesUtils.uriStringToAbsolutePath(srcPath);
		
		assertThat(path)
			.isNotNull()
			.isAbsolute()
			.exists()
			.isEqualTo(FilesUtils.uriStringToAbsolutePath("file:///C:/FredericPersonnel/photos/"))
			.isEqualTo(Path.of("C:\\FredericPersonnel\\photos"));
	}
	
	@Test
	void shouldGetAbsolutePathFromUriUnexistantFile() throws Exception {
		
		String srcPath = "file:///FredericPersonnel/photos/doesNotExists";
		
		Path path = FilesUtils.uriStringToAbsolutePath(srcPath);
		
		assertThat(path)
			.isNotNull()
			.isAbsolute()
			.doesNotExist()
			.isEqualTo(FilesUtils.uriStringToAbsolutePath("file:///C:/FredericPersonnel/photos/doesNotExists"))
			.isEqualTo(Path.of("C:\\FredericPersonnel\\photos\\doesNotExists"));
	}
	
	@Test
	void shouldThrowExceptionForUriWithoutScheme() throws Exception {
		
		String srcPath = "/FredericPersonnel/photos/";		
		assertThatIllegalArgumentException().isThrownBy(() -> FilesUtils.uriStringToAbsolutePath(srcPath));
	}
	
	@Test
	void shouldThrowExceptionForUriWithNonFileSchemeScheme() throws Exception {
		
		String srcPath = "http:///somewhere.org/FredericPersonnel/photos/";		
		assertThatIllegalArgumentException().isThrownBy(() -> FilesUtils.uriStringToAbsolutePath(srcPath));
	}
	
	@Test
	void shouldThrowExceptionForNonHierarchicalUri() throws Exception {
		
		String srcPath = "file:FredericPersonnel/photos/";		
		assertThatIllegalArgumentException().isThrownBy(() -> FilesUtils.uriStringToAbsolutePath(srcPath));
	}
	
	@Test
	void nullParameterShouldThrowException() throws Exception {
		
		assertThatNullPointerException().isThrownBy(() -> FilesUtils.uriStringToAbsolutePath(null));
	}
	
	@Test
	void emptyParameterShouldThrowException() throws Exception {
		
		assertThatIllegalArgumentException().isThrownBy(() -> FilesUtils.uriStringToAbsolutePath(""));
	}
}

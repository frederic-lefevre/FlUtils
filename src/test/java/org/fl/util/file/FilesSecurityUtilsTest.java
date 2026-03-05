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
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class FilesSecurityUtilsTest {

	@Test
	void shouldThrowsNullPointerException() throws IOException {
		
		assertThatNullPointerException().isThrownBy(() -> FilesSecurityUtils.setWritable(null, null));
	}
	
	@Test
	void shouldNoSuchFileException() throws IOException {
		
		Path unexistantPath = Path.of(URI.create("file:///ForTests/FlUtils/DoesNotExists"));
		
		assertThatExceptionOfType(NoSuchFileException.class).isThrownBy(() -> FilesSecurityUtils.setWritable(unexistantPath, null));
	}
	
	@Test
	void shouldNoSuchFileException2() throws IOException {
		
		Path existantPath = Path.of(URI.create("file:///ForTests/FlUtils/MyLogDir"));
		Path unexistantPath = Path.of(URI.create("file:///ForTests/FlUtils/DoesNotExists"));
		
		LogRecordCounter logRecordCounter = 
				FilterCounter.getLogRecordCounter(Logger.getLogger(FilesSecurityUtils.class.getName()));
		
		FilesSecurityUtils.setWritable(existantPath, unexistantPath);
		
		assertThat(logRecordCounter.getLogRecordCount()).isEqualTo(2);
		assertThat(logRecordCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(2);
		assertThat(logRecordCounter.getLogRecords())
			.allSatisfy(logRecord -> {
				assertThat(logRecord.getMessage()).contains("Exception trying to set");
				assertThat(logRecord.getThrown()).isExactlyInstanceOf(NoSuchFileException.class); });
	}
	
	@Test
	void shouldSetWritable() throws IOException {
		
		Path existantPath = Path.of(URI.create("file:///ForTests/FlUtils/MyLogDir"));
		
		FilesSecurityUtils.setWritable(existantPath, null);
		
		assertThat(seemsDeletable(existantPath)).isTrue();
	}
	
	private boolean seemsDeletable(Path path) throws IOException {
		
		FileStore fileStore = Files.getFileStore(path);
		
		if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
			
			AclFileAttributeView aclAttr = Files.getFileAttributeView(path, AclFileAttributeView.class);
			List<AclEntry> aclEntries = aclAttr.getAcl();
			
			return aclEntries.stream()
				.filter(aclEntry -> aclEntry.type().equals(AclEntryType.ALLOW))
				.anyMatch(aclEntry -> aclEntry.permissions().stream().anyMatch(permission -> permission.equals(AclEntryPermission.DELETE)) );
				
		} else if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
			
			Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
			
			return permissions.contains(PosixFilePermission.OWNER_WRITE) &&
					permissions.contains(PosixFilePermission.GROUP_WRITE) &&
					permissions.contains(PosixFilePermission.OTHERS_WRITE);
		}
		return false;
	}
}

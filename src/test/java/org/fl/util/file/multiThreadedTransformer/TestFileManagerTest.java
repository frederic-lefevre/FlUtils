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

package org.fl.util.file.multiThreadedTransformer;

import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TestFileManagerTest {

	@Test
	void produceLineTest() {
		
		String expectedResult = "a10;b10;c10;d10;e10;f10;g10;h10;i10;j10;";
		assertThat(TestFileManager.produceLine(10)).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	void produceAtypicLineTest() {
		
		String expectedResult = "ATYPIC LINEa10;b10;c10;d10;e10;f10;g10;h10;i10;j10;";
		assertThat(TestFileManager.produceAtypicLine(10)).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	void produceWrongLineTest() {
		
		String expectedResult = "WRONG LINEa10;b10;c10;d10;e10;f10;g10;h10;i10;j10;";
		assertThat(TestFileManager.produceWrongLine(10)).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	void produceRegularFileTest() throws URISyntaxException, IOException {
		
		String fileName = TestFileManager.TEST_DATA_FOLDER + "regularFileNameTest.csv";
		Path regularTestFile = TestFileManager.writeRegularLinesFile(fileName);
		
		assertThat(regularTestFile).exists().isRegularFile();
		
		String expectedFirstLine = "a0;b0;c0;d0;e0;f0;g0;h0;i0;j0;";
		assertThat(getFirstLineOfFile(regularTestFile)).isNotNull().isEqualTo(expectedFirstLine);
		
		assertThat(Files.deleteIfExists(regularTestFile)).isTrue();
		assertThat(regularTestFile).doesNotExist();
	}
	
	@Test
	void produceAtypicFileTest() throws URISyntaxException, IOException {
		
		String fileName = TestFileManager.TEST_DATA_FOLDER + "atypicFileNameTest.csv";
		Path atypicTestFile = TestFileManager.writeAtypicLinesFile(fileName);
		
		assertThat(atypicTestFile).exists().isRegularFile();
		
		String expectedFirstLine = "ATYPIC LINEa0;b0;c0;d0;e0;f0;g0;h0;i0;j0;";
		assertThat(getFirstLineOfFile(atypicTestFile)).isNotNull().isEqualTo(expectedFirstLine);
		
		assertThat(Files.deleteIfExists(atypicTestFile)).isTrue();
		assertThat(atypicTestFile).doesNotExist();
	}
	
	@Test
	void produceWrongFileTest() throws URISyntaxException, IOException {
		
		String fileName = TestFileManager.TEST_DATA_FOLDER + "wrongFileNameTest.csv";
		Path wrongTestFile = TestFileManager.writeWrongLinesFile(fileName);
		
		assertThat(wrongTestFile).exists().isRegularFile();
		
		String expectedFirstLine = "WRONG LINEa0;b0;c0;d0;e0;f0;g0;h0;i0;j0;";
		assertThat(getFirstLineOfFile(wrongTestFile)).isNotNull().isEqualTo(expectedFirstLine);
		
		assertThat(Files.deleteIfExists(wrongTestFile)).isTrue();
		assertThat(wrongTestFile).doesNotExist();
	}
	
	private String getFirstLineOfFile(Path path) {
		try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
			return reader.readLine();
		} catch (Exception e) {
			return null;
		}
	}
}

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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TestFileManagerTest {

	@Test
	void produceLineTest() throws URISyntaxException {
		
		TestFileManager testFileManager = new TestFileManager();
		
		String expectedResult = "a10;b10;c10;d10;e10;f10;g10;h10;i10;j10;";
		assertThat(testFileManager.produceLine(10)).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	void produceAtypicLineTest() throws URISyntaxException {
		
		TestFileManager testFileManager = new TestFileManager();
		
		String expectedResult = "ATYPIC LINEa10;b10;c10;d10;e10;f10;g10;h10;i10;j10;";
		assertThat(testFileManager.produceAtypicLine(10)).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	void produceWrongLineTest() throws URISyntaxException {
		
		TestFileManager testFileManager = new TestFileManager();
		
		String expectedResult = "WRONG LINEa10;b10;c10;d10;e10;f10;g10;h10;i10;j10;";
		assertThat(testFileManager.produceWrongLine(10)).isNotNull().isEqualTo(expectedResult);
	}
	
	@Test
	void produceRegularFileTest() throws URISyntaxException, IOException {
		
		TestFileManager testFileManager = new TestFileManager("regularFileNameTest.csv", "atypicFileNameTest.csv", "wrongFileNameTest.csv", "inputTestFileTest.csv");

		Path regularTestFile = testFileManager.writeRegularLinesFile();
		
		assertThat(regularTestFile).exists().isRegularFile();
		
		String expectedFirstLine = "a0;b0;c0;d0;e0;f0;g0;h0;i0;j0;";
		assertThat(getFirstLineOfFile(regularTestFile, testFileManager.getCharset())).isNotNull().isEqualTo(expectedFirstLine);
		
		assertThat(Files.deleteIfExists(regularTestFile)).isTrue();
		assertThat(regularTestFile).doesNotExist();
	}
	
	@Test
	void produceAtypicFileTest() throws URISyntaxException, IOException {
		
		TestFileManager testFileManager = new TestFileManager("regularFileNameTest.csv", "atypicFileNameTest.csv", "wrongFileNameTest.csv", "inputTestFileTest.csv");
		
		Path atypicTestFile = testFileManager.writeAtypicLinesFile();
		
		assertThat(atypicTestFile).exists().isRegularFile();
		
		String expectedFirstLine = "ATYPIC LINEa0;b0;c0;d0;e0;f0;g0;h0;i0;j0;";
		assertThat(getFirstLineOfFile(atypicTestFile, testFileManager.getCharset())).isNotNull().isEqualTo(expectedFirstLine);
		
		assertThat(Files.deleteIfExists(atypicTestFile)).isTrue();
		assertThat(atypicTestFile).doesNotExist();
	}
	
	@Test
	void produceWrongFileTest() throws URISyntaxException, IOException {
		
		TestFileManager testFileManager = new TestFileManager("regularFileNameTest.csv", "atypicFileNameTest.csv", "wrongFileNameTest.csv", "inputTestFileTest.csv");

		Path wrongTestFile = testFileManager.writeWrongLinesFile();
		
		assertThat(wrongTestFile).exists().isRegularFile();
		
		String expectedFirstLine = "WRONG LINEa0;b0;c0;d0;e0;f0;g0;h0;i0;j0;";
		assertThat(getFirstLineOfFile(wrongTestFile, testFileManager.getCharset())).isNotNull().isEqualTo(expectedFirstLine);
		
		assertThat(Files.deleteIfExists(wrongTestFile)).isTrue();
		assertThat(wrongTestFile).doesNotExist();
	}
	
	@Test
	void produceInputTestFile() throws URISyntaxException, IOException {
		
		TestFileManager testFileManager = new TestFileManager("regularFileNameTest2.csv", "atypicFileNameTest2.csv", "wrongFileNameTest2.csv", "inputTestFileTest.csv");
		
		Path inputTestPath = testFileManager.writeAllTestsFiles();
		
		assertThat(inputTestPath).exists().isRegularFile().hasSize(719700);
		
		assertThat(testFileManager.getRegularLinesPath()).exists().isRegularFile().isNotEmptyFile();
		assertThat(testFileManager.getWrongLinesPath()).exists().isRegularFile().isNotEmptyFile();
		assertThat(testFileManager.getAtypicLinesPath()).exists().isRegularFile().isNotEmptyFile();
		
		testFileManager.deleAllTestFiles();
		
		assertThat(inputTestPath).doesNotExist();		
		assertThat(testFileManager.getRegularLinesPath()).doesNotExist();
		assertThat(testFileManager.getWrongLinesPath()).doesNotExist();
		assertThat(testFileManager.getAtypicLinesPath()).doesNotExist();
	}
	
	private String getFirstLineOfFile(Path path, Charset charset) {
		try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), charset))) {
			return reader.readLine();
		} catch (Exception e) {
			return null;
		}
	}
}

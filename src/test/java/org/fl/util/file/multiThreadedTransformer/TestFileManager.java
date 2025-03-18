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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class TestFileManager {

	private static final Logger logger = Logger.getLogger(TestFileManager.class.getName());
	
	public static final long NUMBER_OF_REGULAR_LINE = 10000L;	
	public static final long NUMBER_OF_ATYPIC_LINE = 1000L;
	public static final long NUMBER_OF_WRONG_LINE = 1000L;
	
	public static final String ATYPIC_LINE_PREFIX = "ATYPIC LINE";
	public static final String WRONG_LINE_PREFIX = "WRONG LINE";
	
	public static final String ADDED_LINE_PREFIX_LINE_PREFIX = "Line prefix: ";
	
	public static final int NB_COLUMN = 10;
	
	private static final String TEST_DATA_FOLDER = "file:///ForTests/FlUtils/multiThreadedTransformer/";
	private static final String REGULAR_LINES_FILE_NAME = TEST_DATA_FOLDER + "regularLines.csv";
	private static final String ATYPIC_LINES_FILE_NAME = TEST_DATA_FOLDER + "atypicLines.csv";
	private static final String WRONG_LINES_FILE_NAME = TEST_DATA_FOLDER + "wrongLines.csv";
	
	private static Path regularLinesFile;
	private static Path atypicLinesFile;
	private static Path wrongLinesFile;
	
	protected static String produceLine(long lineNumber) {
		
		StringBuilder sb = new StringBuilder();		
		IntStream.rangeClosed('a', 'z')
			.limit(NB_COLUMN)
			.forEachOrdered(c -> sb.append((char)c).append(lineNumber).append(";"));
		return sb.toString();
	}
	
	protected static String produceAtypicLine(long lineNumber) {
		return ATYPIC_LINE_PREFIX + produceLine(lineNumber);
	}
	
	
	protected static String produceWrongLine(long lineNumber) {
		return WRONG_LINE_PREFIX + produceLine(lineNumber);
	}
	
	protected static File writeRegularLinesFile() throws URISyntaxException {	
		regularLinesFile = Paths.get(new URI(REGULAR_LINES_FILE_NAME));
		return writeTestFile(regularLinesFile, NUMBER_OF_REGULAR_LINE, (l) -> produceLine(l));
	}
	
	protected static File writeAtypicLinesFile() throws URISyntaxException {	
		atypicLinesFile = Paths.get(new URI(ATYPIC_LINES_FILE_NAME));
		return writeTestFile(atypicLinesFile, NUMBER_OF_ATYPIC_LINE, (l) -> produceAtypicLine(l));
	}
	
	protected static File writeWrongLinesFile() throws URISyntaxException {	
		wrongLinesFile = Paths.get(new URI(WRONG_LINES_FILE_NAME));
		return writeTestFile(wrongLinesFile, NUMBER_OF_WRONG_LINE, (l) -> produceWrongLine(l));
	}
	
	protected static boolean deleteRegularTestFiles() throws IOException {
		return Files.deleteIfExists(regularLinesFile);
	}
	
	protected static boolean deleteAtypicTestFiles() throws IOException {
		return Files.deleteIfExists(atypicLinesFile);
	}
	
	protected static boolean deleteWrongTestFiles() throws IOException {
		return Files.deleteIfExists(wrongLinesFile);
	}
	
	private static File writeTestFile(Path pathName, long numberOfline, Function<Long, String> lineProducer) throws URISyntaxException {
		
		try (BufferedWriter outputStream = Files.newBufferedWriter(pathName, StandardCharsets.UTF_8)) {
			for (long l=0; l < numberOfline; l++) {
				outputStream.write(lineProducer.apply(l));
				outputStream.write("\n");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException while writing the file: " + pathName);
		}
		return pathName.toFile();
	}
}

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.SplittableRandom;
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
	
	protected static final String TEST_DATA_FOLDER = "file:///ForTests/FlUtils/multiThreadedTransformer/";
	
	// Files used for the MultiThreadedTransformerTest
	private static final String REGULAR_LINES_FILE_NAME = TEST_DATA_FOLDER + "regularLines.csv";
	private static final String ATYPIC_LINES_FILE_NAME = TEST_DATA_FOLDER + "atypicLines.csv";
	private static final String WRONG_LINES_FILE_NAME = TEST_DATA_FOLDER + "wrongLines.csv";
	private static final String INPUT_TEST_FILE_NAME = TEST_DATA_FOLDER + "inputTestFile.csv";
	private static final String REGULAR_LINES_OUTPUT_FILE_NAME = TEST_DATA_FOLDER + "regularLinesOutput.csv";
	private static final String ATYPIC_LINES_OUTPUT_FILE_NAME = TEST_DATA_FOLDER + "atypicLinesOutput.csv";
	private static final String WRONG_LINES_OUTPUT_FILE_NAME = TEST_DATA_FOLDER + "wrongLinesOutput.csv";
	
	private final Path regularLinesPath;
	private final Path atypicLinesPath;
	private final Path wrongLinesPath;
	private final Path inputTestPath;
	private final Path regularLinesOutputPath;
	private final Path atypicLinesOutputPath;
	private final Path wrongLinesOutputPath;
	
	private final Charset charset = StandardCharsets.UTF_8;
	
	public TestFileManager() throws URISyntaxException {
		regularLinesPath = Paths.get(new URI(REGULAR_LINES_FILE_NAME));
		atypicLinesPath = Paths.get(new URI(ATYPIC_LINES_FILE_NAME));
		wrongLinesPath = Paths.get(new URI(WRONG_LINES_FILE_NAME));
		inputTestPath = Paths.get(new URI(INPUT_TEST_FILE_NAME));
		regularLinesOutputPath = Paths.get(new URI(REGULAR_LINES_OUTPUT_FILE_NAME));
		atypicLinesOutputPath = Paths.get(new URI(ATYPIC_LINES_OUTPUT_FILE_NAME));
		wrongLinesOutputPath = Paths.get(new URI(WRONG_LINES_OUTPUT_FILE_NAME));
	}
	
	public TestFileManager(String regularLinesFileName, String atypicLinesFileName, String wrongLinesFileName, String inputTestFileName) throws URISyntaxException {
		regularLinesPath = Paths.get(new URI(TEST_DATA_FOLDER + regularLinesFileName));
		atypicLinesPath = Paths.get(new URI(TEST_DATA_FOLDER + atypicLinesFileName));
		wrongLinesPath = Paths.get(new URI(TEST_DATA_FOLDER + wrongLinesFileName));
		inputTestPath = Paths.get(new URI(TEST_DATA_FOLDER + inputTestFileName));
		
		// Should not be used
		regularLinesOutputPath = null;
		atypicLinesOutputPath = null;
		wrongLinesOutputPath = null;
	}
	
	public Path getRegularLinesPath() {
		return regularLinesPath;
	}

	public Path getAtypicLinesPath() {
		return atypicLinesPath;
	}

	public Path getWrongLinesPath() {
		return wrongLinesPath;
	}

	public Path getInputTestPath() {
		return inputTestPath;
	}

	public Path getRegularLinesOutputPath() {
		return regularLinesOutputPath;
	}

	public Path getAtypicLinesOutputPath() {
		return atypicLinesOutputPath;
	}

	public Path getWrongLinesOutputPath() {
		return wrongLinesOutputPath;
	}

	public Charset getCharset() {
		return charset;
	}

	protected String produceLine(long lineNumber) {
		
		StringBuilder sb = new StringBuilder();		
		IntStream.rangeClosed('a', 'z')
			.limit(NB_COLUMN)
			.forEachOrdered(c -> sb.append((char)c).append(lineNumber).append(";"));
		return sb.toString();
	}
	
	protected String produceAtypicLine(long lineNumber) {
		return ATYPIC_LINE_PREFIX + produceLine(lineNumber);
	}
	
	
	protected String produceWrongLine(long lineNumber) {
		return WRONG_LINE_PREFIX + produceLine(lineNumber);
	}
	
	protected Path writeRegularLinesFile() throws URISyntaxException {	
		return writeTestFile(regularLinesPath, NUMBER_OF_REGULAR_LINE, (l) -> produceLine(l));
	}
	
	protected Path writeAtypicLinesFile() throws URISyntaxException {	
		return writeTestFile(atypicLinesPath, NUMBER_OF_ATYPIC_LINE, (l) -> produceAtypicLine(l));
	}
	
	protected Path writeWrongLinesFile() throws URISyntaxException {	
		return writeTestFile(wrongLinesPath, NUMBER_OF_WRONG_LINE, (l) -> produceWrongLine(l));
	}
	
	private Path writeTestFile(Path pathName, long numberOfline, Function<Long, String> lineProducer) throws URISyntaxException {
		
		try (BufferedWriter outputStream = Files.newBufferedWriter(pathName, charset)) {
			for (long l=0; l < numberOfline; l++) {
				outputStream.write(lineProducer.apply(l));
				outputStream.write("\n");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException while writing the file: " + pathName);
		}
		return pathName;
	}
	
	protected Path writeAllTestsFiles() throws URISyntaxException {
		writeRegularLinesFile();
		writeAtypicLinesFile();
		writeWrongLinesFile();

		try (BufferedReader regularLinesReader = Files.newBufferedReader(regularLinesPath, charset); 
			 BufferedReader atypicLinesReader = Files.newBufferedReader(atypicLinesPath, charset); 
			 BufferedReader wrongLinesReader = Files.newBufferedReader(wrongLinesPath, charset);
			 BufferedWriter outputStream = Files.newBufferedWriter(inputTestPath, charset)) {
			
			LineSources lineSources = new LineSources(List.of(		
					new LineSource(regularLinesReader,NUMBER_OF_REGULAR_LINE),
					new LineSource(atypicLinesReader,NUMBER_OF_ATYPIC_LINE),
					new LineSource(wrongLinesReader,NUMBER_OF_WRONG_LINE)					
					));
			
			String line;
			while((line = lineSources.readLine()) != null) {
				outputStream.write(line);
				outputStream.write("\n");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException while writing the file: ");
		}
		return inputTestPath;
	}
	
	protected void deleAllTestFiles() throws IOException {
		
		Files.deleteIfExists(regularLinesPath);
		Files.deleteIfExists(atypicLinesPath);
		Files.deleteIfExists(wrongLinesPath);
		Files.deleteIfExists(inputTestPath);
		
		deleteIfNotNull(regularLinesOutputPath);
		deleteIfNotNull(wrongLinesOutputPath);
		deleteIfNotNull(atypicLinesOutputPath);
	}
	
	private void deleteIfNotNull(Path path) throws IOException {
		if (path != null) {
			Files.deleteIfExists(path);
		}
	}
	
	private static class LineSource {
		
		private final BufferedReader reader;
		private final long nbOfLines;
		private boolean exhausted;
		
		public LineSource(BufferedReader reader, long nbOfLines) {
			this.reader = reader;
			this.nbOfLines = nbOfLines;
			exhausted = nbOfLines < 1;
		}

		public BufferedReader getReader() {
			return reader;
		}

		public long getNbOfLines() {
			return nbOfLines;
		}

		public boolean isExhausted() {
			return exhausted;
		}
		
		public void setExhausted() {
			exhausted = true;
		}
	}
	
	private static class LineSources {
		
		private static final SplittableRandom random = new SplittableRandom();
		
		List<LineSource> lineSources;
		
		public LineSources(List<LineSource> lineSources) {
			this.lineSources = lineSources;
		}
		
		// Pick a random line from the lineSources (random depending on the size of each line source)
		public String readLine() throws IOException {
			
			long linesOfActiveSources = linesOfActiveSources();
			if (linesOfActiveSources == 0) {
				// all files have been read
				return null;
			}
			long sourceNum = random.nextLong(0, linesOfActiveSources());
			long currentSourcesNbLines = 0;
			for (int i = 0; i < lineSources.size(); i++) {
				if (! lineSources.get(i).exhausted) {
					currentSourcesNbLines = currentSourcesNbLines + lineSources.get(i).nbOfLines;
					if (sourceNum < currentSourcesNbLines) {
						String lineRead = lineSources.get(i).getReader().readLine();
						if (lineRead == null) {
							lineSources.get(i).setExhausted();
							return readLine();
						} else {
							return lineRead;
						}
					}
				}
			}
			return null;
		}
		
		public long linesOfActiveSources() {
			return lineSources.stream()
					.filter(lineSource -> !lineSource.isExhausted())
					.map(lineSource -> lineSource.getNbOfLines())
					.reduce(0L, Long::sum);
		}
		
	}
}

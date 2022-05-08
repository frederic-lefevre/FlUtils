package org.fl.util.io;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class ForkableInputStreamTest {

	private static final Logger logger = Logger.getLogger(ForkableInputStreamTest.class.getName());
	
    @Test
    @Tag("UnitTest")
    void forkOneOutputStream() throws IOException {

        String source = "a string in output";
        InputStream in = stringToInputStream(source);
        ForkableInputStream forkableInputStream = new ForkableInputStream(in, logger);

        // Fork an outputStream
        ByteArrayOutputStream forkedOutputStream1 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream1)).isTrue();

        // Now read the source through the ForkableInputStream
        String readSource = inputStreamToString(forkableInputStream);

        String result1 = forkedOutputStream1.toString();
        assertThat(result1).isEqualTo(source).isEqualTo(readSource);
    }

    private static Stream<Arguments> sourcesAndBufferCapacities() {
        String generatedString = generateRandomAlphaNumericString(322);
        String generatedString2 = generateRandomAlphaNumericString(16000);
        String string3 = "a string for another test in output";
        String string4 = "1";
        return Stream.of(
                Arguments.of(string3, -1, false, stringToInputStream(string3), streamToString),
                Arguments.of(string4, 1024, false, stringToInputStream(string4), streamToString),
                Arguments.of(generatedString, 16, true, stringToInputStream(generatedString), streamToString),
                Arguments.of(generatedString2, 0, false, stringToInputStream(generatedString2), streamToString),
                Arguments.of(string3, -1, false, new BufferedInputStream(stringToInputStream(string3)), streamToString),
                Arguments.of(string4, 1024, false, new BufferedInputStream(stringToInputStream(string4)), streamToString),
                Arguments.of(generatedString, 16, true, new BufferedInputStream(stringToInputStream(generatedString)), streamToString),
                Arguments.of(generatedString2, 0, false, new BufferedInputStream(stringToInputStream(generatedString2)), streamToString),
                Arguments.of(string3, -1, false, stringToInputStream(string3), streamToStringThroughBuffer),
                Arguments.of(string4, 1024, false, stringToInputStream(string4), streamToStringThroughBuffer),
                Arguments.of(generatedString, 16, true, stringToInputStream(generatedString), streamToStringThroughBuffer),
                Arguments.of(generatedString2, 0, false, stringToInputStream(generatedString2), streamToStringThroughBuffer),
                Arguments.of(string3, -1, false, new BufferedInputStream(stringToInputStream(string3)), streamToStringThroughBuffer),
                Arguments.of(string4, 1024, false, new BufferedInputStream(stringToInputStream(string4)), streamToStringThroughBuffer),
                Arguments.of(generatedString, 16, true, new BufferedInputStream(stringToInputStream(generatedString)), streamToStringThroughBuffer),
                Arguments.of(generatedString2, 0, false, new BufferedInputStream(stringToInputStream(generatedString2)), streamToStringThroughBuffer)
        );
    }

    private static final Function<InputStream, String> streamToString = in -> {
        try {
            return inputStreamToString(in);
        } catch (IOException e) {
            fail("Exception converting Stream to String", e);
            return null;
        }
    };

    private static final Function<InputStream, String> streamToStringThroughBuffer = in -> {
        try {
            return inputStreamToStringThroughBuffer(in);
        } catch (IOException e) {
            fail("Exception converting Stream to String through buffer", e);
            return null;
        }
    };

    @ParameterizedTest
    @MethodSource("sourcesAndBufferCapacities")
    @Tag("UnitTest")
    void forkFiveOutputStream(String source, int bufferCapacity, boolean forceCapacity, InputStream sourceInputStream, Function<InputStream, String> streamToString) {

        ForkableInputStream forkableInputStream ;
        if (forceCapacity) {
            forkableInputStream = new ForkableInputStream(sourceInputStream, bufferCapacity, true, logger);
        } else if (bufferCapacity == 0) {
            // try with default constructor
            forkableInputStream = new ForkableInputStream(sourceInputStream, logger);
        } else {
            forkableInputStream = new ForkableInputStream(sourceInputStream, bufferCapacity, logger);
        }

        // Fork 5 outputStream
        ByteArrayOutputStream forkedOutputStream1 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream1)).isTrue();
        ByteArrayOutputStream forkedOutputStream2 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream2)).isTrue();
        ByteArrayOutputStream forkedOutputStream3 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream3)).isTrue();
        ByteArrayOutputStream forkedOutputStream4 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream4)).isTrue();
        ByteArrayOutputStream forkedOutputStream5 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream5)).isTrue();

        // Now read the source through the ForkableInputStream
        String readSource = streamToString.apply(forkableInputStream);

        String result1 = forkedOutputStream1.toString();
        String result2 = forkedOutputStream2.toString();
        String result3 = forkedOutputStream3.toString();
        String result4 = forkedOutputStream4.toString();
        String result5 = forkedOutputStream5.toString();
        assertThat(readSource)
                .isEqualTo(source)
                .isEqualTo(result1)
                .isEqualTo(result2)
                .isEqualTo(result3)
                .isEqualTo(result4)
                .isEqualTo(result5);
    }

    private static Stream<Arguments> skipTestSources() {
    	String generatedString = generateRandomAlphaNumericString(322);
        String generatedString2 = generateRandomAlphaNumericString(16000);
        String string3 = "a string for another test in output";
        return Stream.of(
                Arguments.of(string3, stringToInputStream(string3)),
                Arguments.of(generatedString, stringToInputStream(generatedString)),
                Arguments.of(generatedString2, stringToInputStream(generatedString2)),
                Arguments.of(string3, new BufferedInputStream(stringToInputStream(string3))),
                Arguments.of(generatedString, new BufferedInputStream(stringToInputStream(generatedString))),
                Arguments.of(generatedString2, new BufferedInputStream(stringToInputStream(generatedString2)))
        );
    }

    @ParameterizedTest
    @MethodSource("skipTestSources")
    @Tag("UnitTest")
    void testSkip(String source, InputStream sourceInputStream) throws IOException {

        final int stringLength = source.length();

        if (stringLength > 10) {
            final int nbFirstReads = stringLength / 3;
            final int nbSkipped = stringLength / 3;

            String expectedResult = source.substring(0, nbFirstReads) + source.substring(nbFirstReads + nbSkipped);

            ForkableInputStream forkableInputStream = new ForkableInputStream(sourceInputStream, logger);

            // Add 2 forked outputStreams
            ByteArrayOutputStream forkedOutputStream1 = new ByteArrayOutputStream();
            assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream1)).isTrue();
            ByteArrayOutputStream forkedOutputStream2 = new ByteArrayOutputStream();
            assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream2)).isTrue();

            // Read first part
            byte[] tab = new byte[expectedResult.length() + 3];
            int firstReadLength = forkableInputStream.read(tab, 0, nbFirstReads);
            assertThat(firstReadLength).isEqualTo(nbFirstReads);

            // Skip middle
            long skippedBytes = forkableInputStream.skip(nbSkipped);
            assertThat(skippedBytes).isEqualTo(nbSkipped);

            // Read the remaining
            int remainingLength = forkableInputStream.read(tab, nbFirstReads, expectedResult.length() - nbFirstReads + 1);
            assertThat(remainingLength).isEqualTo(stringLength - (nbFirstReads + nbSkipped));

            // check string read from inputStream
            String readString = new String(tab, 0, expectedResult.length());
            assertThat(readString).isEqualTo(expectedResult);

            // Check that bytes have been written to outputStreams
            String result1 = forkedOutputStream1.toString();
            String result2 = forkedOutputStream2.toString();
            assertThat(result1).isEqualTo(result2).isEqualTo(expectedResult);
            
            forkableInputStream.close();
        }
    }

    @Test
    @Tag("UnitTest")
    void checkThatReadArrayIsSupported() throws IOException {

        String source = "a string in output";
        InputStream in = stringToInputStream(source);
        ForkableInputStream forkableInputStream = new ForkableInputStream(in, logger);

        ByteArrayOutputStream forkedOutputStream1 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream1)).isTrue();

        byte[] tab = new byte[source.length() + 10];
        int length = forkableInputStream.read(tab);

        // Check that bytes have been written to outputStream
        String result1 = forkedOutputStream1.toString();
        assertThat(result1).isEqualTo(source).hasSize(length);
        
        forkableInputStream.close();
    }

    @Test
    @Tag("UnitTest")
    void checkThatReadArrayWithOffsetIsSupported() throws IOException {

        String source = "a string in output";
        InputStream in = stringToInputStream(source);

        // ForkableInputStream can be put in a try with resources
        try (ForkableInputStream forkableInputStream = new ForkableInputStream(in, logger);
             ByteArrayOutputStream forkedOutputStream1 = new ByteArrayOutputStream()) {

            assertThat(forkableInputStream.addForkedOutputStream(forkedOutputStream1)).isTrue();

            int offset = 3;
            int readLength = source.length() + 5;
            byte[] tab = new byte[offset + readLength + 10];
            int length = forkableInputStream.read(tab, offset, readLength);

            // check string read from inputStream
            String readString = new String(tab, offset, length);
            assertThat(readString).isEqualTo(source);

            // Check that bytes have been written to outputStream
            String result1 = forkedOutputStream1.toString();
            assertThat(result1).isEqualTo(source).hasSize(length);

        } catch (Exception e) {
            fail("Exception with ForkableInputStream", e);
            throw(e);  // just to suppress a warning
        }
    }

    private static InputStream stringToInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String inputStreamToString(InputStream is) throws IOException {
    	return new String (is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static String inputStreamToStringThroughBuffer(InputStream is) throws IOException {
    	return new String ((new BufferedInputStream(is)).readAllBytes(), StandardCharsets.UTF_8);
    }
    
	private static String generateRandomAlphaNumericString(int stringLength) {
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    return random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	}
}
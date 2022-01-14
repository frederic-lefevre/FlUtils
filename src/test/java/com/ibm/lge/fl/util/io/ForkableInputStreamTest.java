package com.ibm.lge.fl.util.io;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
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

        // Fork an OutputStream
        ByteArrayOutputStream forkedOuputStream1 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream1)).isTrue();

        // Now read the source through the ForkableInputStream
        String readSource = intputStreamToString(forkableInputStream);

        String result1 = forkedOuputStream1.toString();
        assertThat(result1).isEqualTo(source).isEqualTo(readSource);
    }

    private static Stream<Arguments> sourcesAndBufferCapacities() {
        String generatedString = generateRandomAlphaNumericString(322);
        String generatedString2 = generateRandomAlphaNumericString(16000);
        return Stream.of(
                Arguments.of("a string for another test in output", -1, false),
                Arguments.of("1", 1024, false),
                Arguments.of(generatedString, 16, true),
                Arguments.of(generatedString2, 0, false)
        );
    }

    @ParameterizedTest
    @MethodSource("sourcesAndBufferCapacities")
    @Tag("UnitTest")
    void forkFiveOutputStream(String source, int bufferCapacity, boolean forceCapacity) throws IOException {

        InputStream in = stringToInputStream(source);
        ForkableInputStream forkableInputStream ;
        if (forceCapacity) {
            forkableInputStream = new ForkableInputStream(in, bufferCapacity, forceCapacity, logger);
        } else if (bufferCapacity == 0) {
            // try with default constructor
            forkableInputStream = new ForkableInputStream(in, logger);
        } else {
            forkableInputStream = new ForkableInputStream(in, bufferCapacity, logger);
        }

        // Fork 5 outputstream
        ByteArrayOutputStream forkedOuputStream1 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream1)).isTrue();
        ByteArrayOutputStream forkedOuputStream2 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream2)).isTrue();
        ByteArrayOutputStream forkedOuputStream3 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream3)).isTrue();
        ByteArrayOutputStream forkedOuputStream4 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream4)).isTrue();
        ByteArrayOutputStream forkedOuputStream5 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream5)).isTrue();

        // Now read the source through the ForkableInputStream
        String readSource = intputStreamToString(forkableInputStream);

        String result1 = forkedOuputStream1.toString();
        String result2 = forkedOuputStream2.toString();
        String result3 = forkedOuputStream3.toString();
        String result4 = forkedOuputStream4.toString();
        String result5 = forkedOuputStream5.toString();
        assertThat(readSource)
                .isEqualTo(source)
                .isEqualTo(result1)
                .isEqualTo(result2)
                .isEqualTo(result3)
                .isEqualTo(result4)
                .isEqualTo(result5);
    }

    @Test
    @Tag("UnitTest")
    void checkThatReadArrayIsSupported() throws IOException {

        String source = "a string in output";
        InputStream in = stringToInputStream(source);
        ForkableInputStream forkableInputStream = new ForkableInputStream(in, logger);

        ByteArrayOutputStream forkedOuputStream1 = new ByteArrayOutputStream();
        assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream1)).isTrue();

        byte[] tab = new byte[source.length() + 10];
        int length = forkableInputStream.read(tab);

        // Check that bytes have been written to outputStream
        String result1 = forkedOuputStream1.toString();
        forkableInputStream.close();
        assertThat(result1).isEqualTo(source).hasSize(length);
    }

    @Test
    @Tag("UnitTest")
    void checkThatReadArrayWithOffsetIsSupported() throws IOException {

        String source = "a string in output";
        InputStream in = stringToInputStream(source);
        
        // ForkableInputStream can be put in a try with resources
        try (ForkableInputStream forkableInputStream = new ForkableInputStream(in, logger);
        	 ByteArrayOutputStream forkedOuputStream1 = new ByteArrayOutputStream()) {

        	assertThat(forkableInputStream.addForkedOutputStream(forkedOuputStream1)).isTrue();

        	int offset = 3;
        	int readLength = source.length() + 5;
        	byte[] tab = new byte[offset + readLength + 10];
        	int length = forkableInputStream.read(tab, offset, readLength);

        	// check string read from inputStream
            String readString = new String(tab, offset, length);
            assertThat(readString).isEqualTo(source);
            
        	// Check that bytes have been written to outputStream
        	String result1 = forkedOuputStream1.toString();
        	assertThat(result1).isEqualTo(source).hasSize(length);
        } catch (Exception e) {
            fail("Exception with ForkeableInputStream", e);
        }
    }
    
    private InputStream stringToInputStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    private String intputStreamToString(InputStream is) throws IOException {
    	return new String (is.readAllBytes(), StandardCharsets.UTF_8);
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

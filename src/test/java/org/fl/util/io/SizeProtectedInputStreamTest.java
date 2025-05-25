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

package org.fl.util.io;

import static org.assertj.core.api.Assertions.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SizeProtectedInputStreamTest {

	private static Stream<Arguments> overFlowNumber() {
		return Stream.of(
                Arguments.of(1),Arguments.of(2),Arguments.of(3));
	}
	
	private static Stream<Arguments> underFlowNumber() {
		return Stream.of(
                Arguments.of(2),Arguments.of(1),Arguments.of(0));
	}
	
	@ParameterizedTest
    @MethodSource("overFlowNumber")
	void shouldRaiseException(int overFlowByteNumber) {

		int maxInputStreamBytes = 322;
		int[] readInput = new int[maxInputStreamBytes + overFlowByteNumber + 1];

		InputStream inputStream = generateRandomAlphaNumericInputStream(maxInputStreamBytes + overFlowByteNumber);
		
		int idx = 0;
		try (SizeProtectedInputStream sizeProtectedInputStream = new SizeProtectedInputStream(inputStream, maxInputStreamBytes)) {
			
			int b = sizeProtectedInputStream.read();
			while (b != -1) {
				
				readInput[idx] = b;
				idx++;
				b = sizeProtectedInputStream.read();
			}
			fail("Fail to raise the right exception. Number of bytes read=" + idx);
			
		} catch (OverflowIOException e) {
			assertThat(idx).isEqualTo(maxInputStreamBytes);
		} catch (IOException e) {
			fail("IOException reading SizeProtectedInputStream", e);
		}

	}

	@ParameterizedTest
    @MethodSource("underFlowNumber")
	void shouldNotRaiseException(int underFlowByteNumber) {

		int maxInputStreamBytes = 322;
		int[] readInput = new int[maxInputStreamBytes - underFlowByteNumber + 1];

		InputStream inputStream = generateRandomAlphaNumericInputStream(maxInputStreamBytes - underFlowByteNumber);
		
		int idx = 0;
		try (SizeProtectedInputStream sizeProtectedInputStream = new SizeProtectedInputStream(inputStream, maxInputStreamBytes)) {
			
			int b = sizeProtectedInputStream.read();
			while (b != -1) {
				
				readInput[idx] = b;
				idx++;
				b = sizeProtectedInputStream.read();
			}
			assertThat(idx).isEqualTo(maxInputStreamBytes - underFlowByteNumber);
			
		} catch (OverflowIOException e) {
			fail("Fail to read all bytes. Number of bytes read=" + idx);
		} catch (IOException e) {
			fail("IOException reading SizeProtectedInputStream", e);
		}

	}
	
	private static InputStream generateRandomAlphaNumericInputStream(int streamLength) {
		return new ByteArrayInputStream(generateRandomAlphaNumericString(streamLength).getBytes(StandardCharsets.UTF_8));
	}

	private static String generateRandomAlphaNumericString(int stringLength) {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(stringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}
}

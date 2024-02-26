/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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
*/package org.fl.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.fl.util.CompressionUtils.SupportedCompression;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCharSequence;

public class CompressionUtilsTest {

	private static final Logger logger = Logger.getLogger(CompressionUtilsTest.class.getName());
			
	private final static String DORMEUR_DU_VAL = """
C’est un trou de verdure où chante une rivière,
Accrochant follement aux herbes des haillons 
D’argent ; où le soleil, de la montagne fière,
Luit : c’est un petit val qui mousse de rayons.""";

	
	@Test
	void shouldCompressDecompressGzip() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		String dormeurUnzipped = CompressionUtils.decompressGzipString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertThat(dormeurUnzipped).isEqualTo(DORMEUR_DU_VAL);
	}
	
	@Test
	void shouldCompressDecompressDeflate() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		String dormeurUnzipped = CompressionUtils.decompressDeflateString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertThat(dormeurUnzipped).isEqualTo(DORMEUR_DU_VAL);
	}
	
	@Test
	void shouldNotDecompressGzipAsDeflate() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		String dormeurUnzipped = CompressionUtils.decompressDeflateString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertThat(dormeurUnzipped).isNull();
	}
	
	@Test
	void shouldNotDecompressDeflateAsGzip() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		String dormeurUnzipped = CompressionUtils.decompressGzipString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertThat(dormeurUnzipped).isNull();
	}
	
	@Test
	void shouldDecompressDeflateInputStream() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, SupportedCompression.DEFLATE, StandardCharsets.UTF_8, null, logger);
		
		assertThatCharSequence(dormeurUnzipped).isNotNull().hasToString(DORMEUR_DU_VAL);
	}
	
	@Test
	void shouldNotDecompressDeflateInputStreamAsGzip() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, SupportedCompression.GZIP, StandardCharsets.UTF_8, null, logger);
		
		assertThatCharSequence(dormeurUnzipped).isNull();
	}
	
	@Test
	void shouldNotDecompressGzipInputStreamAsDeflate() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, SupportedCompression.DEFLATE, StandardCharsets.UTF_8, null, logger);
		
		assertThatCharSequence(dormeurUnzipped).isNull();
	}
	
	@Test
	void shouldNotDecompressGzipInputStreamAsUnknown() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(StandardCharsets.UTF_8), logger);
		
		assertThat(dormeurZipped).isNotNull().hasSizeGreaterThan(1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, null, StandardCharsets.UTF_8, null, logger);
		
		assertThatCharSequence(dormeurUnzipped).isNull();
		
	}
}

package com.ibm.lge.fl.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.ibm.lge.fl.util.CompressionUtils.SupportedCompression;

import static org.junit.jupiter.api.Assertions.*;

public class CompressionUtilsTest {

	private static final Logger logger = Logger.getLogger(CompressionUtilsTest.class.getName()) ;
			
	private final static String DORMEUR_DU_VAL = 
			
	"C’est un trou de verdure où chante une rivière,    " +
	"Accrochant follement aux herbes des haillons       " +
	"D’argent ; où le soleil, de la montagne fière,     " +
	"Luit : c’est un petit val qui mousse de rayons.    " ;

	
	@Test
	void shouldCompressDecompressGzip() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		String dormeurUnzipped = CompressionUtils.decompressGzipString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertEquals(DORMEUR_DU_VAL, dormeurUnzipped);
	}
	
	@Test
	void shouldCompressDecompressDeflate() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		String dormeurUnzipped = CompressionUtils.decompressDeflateString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertEquals(DORMEUR_DU_VAL, dormeurUnzipped);
	}
	
	@Test
	void shouldNotDecompressGzipAsDeflate() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		String dormeurUnzipped = CompressionUtils.decompressDeflateString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertEquals(null, dormeurUnzipped);
	}
	
	@Test
	void shouldNotDecompressDeflateAsGzip() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		String dormeurUnzipped = CompressionUtils.decompressGzipString(dormeurZipped, StandardCharsets.UTF_8, logger);
		
		assertEquals(null, dormeurUnzipped);
	}
	
	@Test
	void shouldDecompressDeflateInputStream() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, SupportedCompression.DEFLATE, StandardCharsets.UTF_8, null, logger);
		
		assertNotNull(dormeurUnzipped);
		assertEquals(DORMEUR_DU_VAL, dormeurUnzipped.toString());
	}
	
	@Test
	void shouldNotDecompressDeflateInputStreamAsGzip() {
		
		byte[] dormeurZipped = CompressionUtils.compressDeflate(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, SupportedCompression.GZIP, StandardCharsets.UTF_8, null, logger);
		
		assertNull(dormeurUnzipped);
	}
	
	@Test
	void shouldNotDecompressGzipInputStreamAsDeflate() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, SupportedCompression.DEFLATE, StandardCharsets.UTF_8, null, logger);
		
		assertNull(dormeurUnzipped);
	}
	
	@Test
	void shouldNotDecompressGzipInputStreamAsUnknown() {
		
		byte[] dormeurZipped = CompressionUtils.compressGzip(DORMEUR_DU_VAL.getBytes(), logger);
		
		assertNotNull(dormeurZipped);
		assertTrue(dormeurZipped.length > 1);
		
		InputStream dormeurCompressedStream = new ByteArrayInputStream(dormeurZipped) ;
		CharBuffer dormeurUnzipped = CompressionUtils.decompressInputStream(dormeurCompressedStream, null, StandardCharsets.UTF_8, null, logger);
		
		assertNull(dormeurUnzipped);
	}
}

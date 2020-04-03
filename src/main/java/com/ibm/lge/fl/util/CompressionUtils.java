package com.ibm.lge.fl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

	public static byte[] compressGzip(byte[] data, Logger logger) throws IOException {
		
		byte[] compressed ;
		try (ByteArrayOutputStream bos  = new ByteArrayOutputStream();
			 GZIPOutputStream 	   gzip = new GZIPOutputStream(bos)) {
			gzip.write(data);
			compressed = bos.toByteArray();
		
		} catch (Exception e) {
			compressed = null ;
			logger.log(Level.SEVERE, "Exception during gzip decompress", e);
		}
		return compressed;
	}
	
	public static String decompressGzipString(byte[] compressed, Charset charset, Logger logger) {
		
		String ret ;
		try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed)) ) {
		
			ChannelReaderDecoder chan = new ChannelReaderDecoder(
				gis,
				charset,
				null, 
				1024,				
				logger) ;
			ret = chan.readAllChar().toString() ;
		} catch (Exception e) {
			ret = null ;
			logger.log(Level.SEVERE, "Exception during gzip decompress", e);
		}
		return ret;
	}
	
	public static ByteBuffer decompressGzip(byte[] compressed, Logger logger) {
		
		ByteBuffer ret ;
		try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed)) ) {
		
			ChannelReaderDecoder chan = new ChannelReaderDecoder(
				gis,
				null,
				null, 
				1024,				
				logger) ;
			ret = chan.readAllBytes() ;
		} catch (Exception e) {
			ret = null ;
			logger.log(Level.SEVERE, "Exception during gzip decompress", e);
		}
		return ret;
	}
}

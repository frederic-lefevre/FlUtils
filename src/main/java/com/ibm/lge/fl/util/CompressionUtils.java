package com.ibm.lge.fl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {

	public static byte[] compressGzip(byte[] data, Logger logger) {
		
		byte[] compressed ;
		try (ByteArrayOutputStream bos  = new ByteArrayOutputStream(data.length);
			 GZIPOutputStream 	   gzip = new GZIPOutputStream(bos)) {
			gzip.write(data);
			compressed = bos.toByteArray();
		
		} catch (Exception e) {
			compressed = null ;
			logger.log(Level.SEVERE, "Exception during gzip decompress", e);
		}
		return compressed;
	}
	
	// Intermediate compression max buffer size (1 Mega)
	private final static int COMPRESS_BUFFER_SIZE = 1048576 ;

	public static byte[] compressDeflate(byte[] data, Logger logger) {
		
		// Create a Deflater to compress the bytes
		Deflater compresser = new Deflater();
		compresser.setInput(data);
		compresser.finish();

		byte[] compressedArray ;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length) ) {
			
			int buffSize ;
			if (data.length < COMPRESS_BUFFER_SIZE) {
				buffSize = data.length ;
			} else {
				buffSize = COMPRESS_BUFFER_SIZE ;
			}
			byte[] buffer = new byte[buffSize];           
			while(!compresser.finished())
			{             
				int bytesCompressed = compresser.deflate(buffer);
				bos.write(buffer,0,bytesCompressed);
			}	
			
			//get the compressed byte array from output stream
			compressedArray = bos.toByteArray();
			
		}  catch(Exception ioe) {
			logger.log(Level.SEVERE, "Exception when compressing Api with defate", ioe);
			compressedArray = null;
		}
		return compressedArray ;
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

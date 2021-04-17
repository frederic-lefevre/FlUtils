package com.ibm.lge.fl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public class CompressionUtils {
	
	public static byte[] compressGzip(byte[] data, Logger logger) {
		
		byte[] compressed ;
		try (ByteArrayOutputStream bos  = new ByteArrayOutputStream(data.length);
			 GZIPOutputStream 	   gzip = new GZIPOutputStream(bos)) {
			gzip.write(data);
			gzip.close();
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
	
	private static int RECEIVE_BUF_SIZE = 200*1024 ;
	private static int COMPRESS_RATIO	= 6 ;
	private static int getReceiveBufSize(byte[] compressed) {
		long receiveBufSize = 0;
		if (compressed != null) {
			receiveBufSize = compressed.length*COMPRESS_RATIO ;
			if (receiveBufSize > RECEIVE_BUF_SIZE) {
				receiveBufSize = RECEIVE_BUF_SIZE ;
			}
		}
		return (int) receiveBufSize ;
	}
	
	public static String decompressGzipString(byte[] compressed, Charset charset, Logger logger) {
		return decompressBytesToString(compressed, SupportedCompression.GZIP, charset, null, logger);
	}
	
	public static String decompressDeflateString(byte[] compressed, Charset charset, Logger logger) {
		return decompressBytesToString(compressed, SupportedCompression.DEFLATE, charset, null, logger);
	}
	
	public static ByteBuffer decompressGzip(byte[] compressed, Logger logger) {
		
		try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed)) {
			return decompressInputStream(bis, SupportedCompression.GZIP, getReceiveBufSize(compressed), logger) ;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during GZIP decompress", e);
			return null ;
		}
	}
	
	public static ByteBuffer decompressDeflate(byte[] compressed, Logger logger) {
		
		try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed)) {
			return decompressInputStream(bis, SupportedCompression.DEFLATE, getReceiveBufSize(compressed), logger) ;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during deflate decompress", e);
			return null ;
		}
	}

	public enum SupportedCompression { GZIP, DEFLATE } ;
	
	private static InputStream getDecodedInputStream(SupportedCompression compressAlgo, InputStream compressed, Logger hLog) {

		if (compressAlgo != null) {
			switch (compressAlgo) {
			case GZIP:
				try {
					return new GZIPInputStream(compressed);
				} catch (IOException e) {
					hLog.log(Level.SEVERE, "IOException in GZIPInputStream creation", e);
					return null;
				}
			case DEFLATE:
				return new InflaterInputStream(compressed);
			default:
				hLog.severe("Unexpected compression scheme: " + compressAlgo);
				return null;
			}
		} else {
			hLog.severe("Null compression algorithm in getDecodedInputStream");
			return null ;
		}
	}
	
	public static ByteBuffer decompressInputStream(InputStream compressed, SupportedCompression compressSchema, int buffSize, Logger logger) {
		
		try (InputStream targetStream = getDecodedInputStream(compressSchema, compressed, logger)) {
			ChannelReaderDecoder chan = new ChannelReaderDecoder(
					targetStream,
					null,
					null, 
					buffSize,				
					logger) ;
			return chan.readAllBytes() ;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during decompress " + compressSchema, e);
			return null ;
		}
	}
	
	public static CharBuffer decompressInputStream(InputStream compressed, SupportedCompression compressSchema, Charset charSet, File fileTrace, int buffSize, Logger logger) {
		
		try (InputStream targetStream = getDecodedInputStream(compressSchema, compressed, logger)) {
			ChannelReaderDecoder chan = new ChannelReaderDecoder(
					targetStream,
					charSet,
					fileTrace, 
					buffSize,				
					logger) ;
			return chan.readAllChar() ;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during decompress " + compressSchema, e);
			return null ;
		}
	}
	
	public static ByteBuffer decompressInputStream(InputStream compressed, SupportedCompression compressSchema, Logger logger) {
		return decompressInputStream(compressed, compressSchema, RECEIVE_BUF_SIZE, logger) ;
	}
	
	public static CharBuffer decompressInputStream(InputStream compressed, SupportedCompression compressSchema, Charset charSet, File fileTrace, Logger logger) {
		return decompressInputStream(compressed, compressSchema, charSet, fileTrace, RECEIVE_BUF_SIZE, logger) ;
	}
	
	private static String decompressBytesToString(byte[] compressed, SupportedCompression compressSchema, Charset charSet, File fileTrace, Logger logger) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed)) {
			CharBuffer buffer = decompressInputStream(bis, compressSchema, charSet, null, getReceiveBufSize(compressed), logger);
			if (buffer == null) {
				return null;
			} else {
				return buffer.toString() ;
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception during GZIP string decompress", e);
			return null ;
		}

	}
}

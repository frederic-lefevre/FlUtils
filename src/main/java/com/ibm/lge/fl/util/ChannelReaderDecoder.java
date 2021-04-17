package com.ibm.lge.fl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChannelReaderDecoder {

	// Logger
	private final Logger sLog ;
	
	private final InputStream inputStream ;
	private final Charset 	charSet ;
	
	// file to trace the binary content of the bytes read
	// If null, there will be no trace
	private final File		fileTrace ;

	// Size of byte buffers that receive the bytes
	private final int bufferSize ;
	
	public ChannelReaderDecoder(InputStream is, Charset cs, File f, int bs, Logger l) {
		super();
		sLog 		 = l;
		if (cs == null) {
			charSet	 = StandardCharsets.UTF_8 ;
		} else {
			charSet	 = cs ;
		}
		fileTrace	 = f ;
		bufferSize	 = bs ;
		inputStream  = is;		
	}
	
	public CharBuffer readAllChar() {
		
		CharBuffer cb = null ;
		if (inputStream != null) {
			
			try {
				// read all bytes in a ByteBuffer
				ByteBuffer totalBuffer = readAllBytes() ;
				
				if (totalBuffer != null) {
					// Decode the totalBuffer, according to a charSet, into a CharBuffer
					try {
						cb = charSet.newDecoder()
							.onMalformedInput(CodingErrorAction.REPORT)
					        .onUnmappableCharacter(CodingErrorAction.REPORT)
					        .decode(totalBuffer);
					} catch (Exception e) {
						sLog.log(Level.SEVERE, "decoder exception ", e);
					}
					
					// log bytes to a file if requested
					if (fileTrace != null) {
						totalBuffer.rewind() ;
						FileOutputStream fo = new FileOutputStream(fileTrace, false) ;
					    FileChannel wChannel = fo.getChannel();
					    wChannel.write(totalBuffer);
					    wChannel.close();
					    fo.close();
					}
				}
			} catch (Exception e1) {
				sLog.log(Level.SEVERE, "Exception reading and decoding channel", e1);
			}
		}
		return cb ;
	}
	
	public ByteBuffer readAllBytes() {
		
		ByteBuffer totalBuffer = null ;
		ArrayList<ByteBuffer> byteBuffers = new ArrayList<ByteBuffer>() ;
		ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);

		try (ReadableByteChannel in = Channels.newChannel(inputStream)) {
					
			int bytesRead = in.read(byteBuffer); // first read into buffer.
			while (bytesRead != -1) {
			  
			  if (! byteBuffer.hasRemaining()) {
				  // byteBuffer is full. Add it to the list of buffers and allocate a new one 
				  byteBuffers.add(byteBuffer) ;
				  byteBuffer = ByteBuffer.allocate(bufferSize);
			  }
			  bytesRead = in.read(byteBuffer);
			}
			// add the last buffer to the list of buffers
			byteBuffers.add(byteBuffer) ;
			
			if (byteBuffers.size() > 1) {
				// Compute the total size received
				int totalSize = 0 ;
				for (ByteBuffer bBuff : byteBuffers) {
					bBuff.flip();  //make buffer ready for read
					totalSize = totalSize + bBuff.remaining() ;
				}
				// Transfer all bytes in a totalBuffer
				totalBuffer =  ByteBuffer.allocate(totalSize + 16);
				for (ByteBuffer bBuff : byteBuffers) {
					totalBuffer.put(bBuff) ;
				}
			} else {
				// no need to copy all byteBuffer into totalBuffer : there is only one
				totalBuffer = byteBuffer ;
			}
			totalBuffer.flip() ;
			
		} catch (Exception e1) {
			sLog.log(Level.SEVERE, "Exception reading and decoding channel", e1);
		}
		return totalBuffer ;
	}
}

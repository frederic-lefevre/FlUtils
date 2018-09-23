package com.ibm.lge.fl.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FileLineTransformer {

	private Path sourceFilePath ;
	private Path resultFilePath ;
	
	private Charset sourceCharset ;
	private Charset resultCharset ;
	
	private Logger iLog ;
	
	public FileLineTransformer(Path cfp, Path crfp, Logger l) {
		
		sourceFilePath = cfp ;
		sourceCharset  = StandardCharsets.UTF_8 ;
		resultFilePath = crfp ;
		resultCharset  = StandardCharsets.UTF_8 ;
		iLog 		   = l ;
	}

	public FileLineTransformer(Path cfp, Charset sCs,  Path crfp, Charset rCs, Logger l) {
		
		sourceFilePath = cfp ;
		sourceCharset  = sCs ;
		resultFilePath = crfp ;
		resultCharset  = rCs ;
		iLog 		   = l ;
	}
	
	public void process() {
		
		if (sourceFilePath == null) {
			iLog.severe("source file path null");
		} else if (! Files.exists(sourceFilePath)) {
			iLog.severe("Source file non existent: " + sourceFilePath);
		} else {
			try (BufferedReader bf = Files.newBufferedReader(sourceFilePath, sourceCharset) ;
				 BufferedWriter bw = Files.newBufferedWriter(resultFilePath, resultCharset) ) {
								
				if (!Files.exists(resultFilePath)) {
					Files.createDirectories(resultFilePath.getParent()) ;
					Files.createFile(resultFilePath) ;
				}
				
				String line ;
				long lineNumber = 0 ;
				while ((line = bf.readLine()) != null) {
					
					String lineResult = processFileLine(line, lineNumber) ;
					lineNumber++ ;
					if ((lineResult != null) && (! lineResult.isEmpty())) {
						bw.write(lineResult);
						bw.newLine();
					}
				}
				
			} catch (Exception e) {
				iLog.log(Level.SEVERE, "Exception reading ioc csv file", e);
			}
		} 
	}

	protected abstract String processFileLine(String line, long lineNumber) ;
}

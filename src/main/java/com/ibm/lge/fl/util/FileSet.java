package com.ibm.lge.fl.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSet {

	// Logger
	private Logger fLog;
	
	// The root path for this FileSet
	private Path rootPath ;
	
	// Set is recursive or not
	private boolean recursive ;
	
	// Filters to include and exclude files
	private ArrayList<PathMatcher> includeMatcher ;
	private ArrayList<PathMatcher> excludeMatcher ;
		
	// Instance that process visited file 
	private SimpleFileVisitor<Path> allPathVisitor ;
	private SimpleFileVisitor<Path> filterFile ;
	private SimpleFileVisitor<Path> filterDirectory ;
	
	// List of all path under the root path (do not take filter into account)
	private ArrayList<Path> allPathList ;
	
	// List of files in this FileSet
	private ArrayList<Path> fileList ;
	
	// List of directories in this FileSet
	private ArrayList<Path> directoryList ;
	
	// File comparator to sort files by last modified date
	private Comparator<Path> lastModifiedComp ;
	
	public FileSet(String root, Logger l) {
		super();
		
		// set Logger
		fLog = l ;
		
		// Recursive by default
		recursive = true ;
		
		try {
			rootPath = Paths.get(root);
			if (! Files.exists(rootPath)) {
				fLog.warning("Directory does not exist: " + rootPath.toString()) ;
			} else if (! Files.isReadable(rootPath) ) {
				fLog.warning("Directory is not readable: " + rootPath.toString()) ;
			}
		} catch (Exception e) {
			rootPath = null ;
			fLog.log(Level.SEVERE, "Exception in FileSet creation for root=" + root, e) ;
		}
		
		lastModifiedComp =  new Comparator<Path>(){
		    public int compare(Path f1, Path f2) {
		        try {
					return  Files.getLastModifiedTime(f2).compareTo(Files.getLastModifiedTime(f1)) ;
				} catch (IOException e) {
					fLog.log(Level.SEVERE, "Exception when comparing files for last modified date", e) ;
					return 0 ;
				}
		    } } ;
		    
		// default: include everything and exclude nothing
		includeMatcher = null ;
		excludeMatcher = null ;		
		
		// all  in this file set
		allPathList = new ArrayList<Path>() ;
		
		// files in this file set
		fileList = new ArrayList<Path>() ;
		
		// directory in this file set
		directoryList = new ArrayList<Path>() ;
		
		// visitFile method is called each time a path is visited
		allPathVisitor = new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				if (fLog.isLoggable(Level.FINEST)) {
					fLog.finest("File included: " + file) ;
				}
				fileList.add(file) ;				
				
				return FileVisitResult.CONTINUE;
			} 
		} ;
		
		// visitFile method is called each time a file is visited
		filterFile = new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				
				// check if the file matches
				if (attrs.isRegularFile()) {
					if (fileMatch(file.getFileName())) {
						if (fLog.isLoggable(Level.FINEST)) {
							fLog.finest("File included: " + file) ;
						}
						fileList.add(file) ;
					} else if (fLog.isLoggable(Level.FINEST)) {
						// the file does not match the patterns
						fLog.finest("File excluded: " + file) ;
					}
				}
				return FileVisitResult.CONTINUE;
			} 
		} ;
		
		// preVisitDirectory method is called each time a directory is visited
		filterDirectory = new SimpleFileVisitor<Path>() {
			
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				
				// check if the file is a directory
				if (attrs.isDirectory()) {
					if (fLog.isLoggable(Level.FINEST)) {
						fLog.finest("Directory included: " + dir) ;
					}
					directoryList.add(dir) ;
				}
				return FileVisitResult.CONTINUE;
			} 
		} ;
	}
	
	public void setIncludeFilters(String[]  iFilters) {
		includeMatcher = new ArrayList<PathMatcher>() ;
		for (String pattern : iFilters) {
			includeMatcher.add(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
		}
	}

	public void setExcludeFilters(String[] eFilters) {
		excludeMatcher = new ArrayList<PathMatcher>() ;
		for (String pattern : eFilters) {
			excludeMatcher.add(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
		}
	}
	
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}


	public boolean fileMatch(Path file) {
		
		boolean included = true;
		boolean excluded = false;
		if ((includeMatcher != null) && (includeMatcher.size() > 0)) {
			included = false ;
			for(PathMatcher iMatch : includeMatcher) {
				if (iMatch.matches(file)) {
					included =true ;
				}
			}
		}
		if ((excludeMatcher != null) && (excludeMatcher.size() > 0)) {
			for(PathMatcher iMatch : excludeMatcher) {
				if (iMatch.matches(file)) {
					excluded =true ;
				}
			}
		}
		return (included && !excluded) ;
	}
	
	public ArrayList<Path> getFileList() {
		
		scan(filterFile) ;
		return fileList ;
	}
	
	public ArrayList<Path> getDirectyList() {
		
		scan(filterDirectory) ;
		return directoryList ;
	}
	
	public ArrayList<Path> getAllPathList() {
		scan(allPathVisitor) ;
		return allPathList;
	}

	public ArrayList<Path> getFileListOrderedByLastModified() {
		
		scan(filterFile) ;
		Collections.sort(fileList, lastModifiedComp);
		return fileList ;
	}
	
	public ArrayList<Path> getFileListOrdered(Comparator<Path> comparator) {
		
		scan(filterFile) ;
		Collections.sort(fileList, comparator);
		return fileList ;
	}
	
	// scan selects all the acceptable files and directories and put them in fileList and directoryList
	private void scan(SimpleFileVisitor<Path> sfv) {
		
		// reset the files and directories list
		fileList = new ArrayList<Path>() ;
		directoryList = new ArrayList<Path>() ;
		
		EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		int maxDepth ;
		if (recursive) {
			maxDepth = Integer.MAX_VALUE ;
		} else {
			maxDepth = 1 ;
		}
		
		if (rootPath != null) {
			if (! Files.exists(rootPath)) {
				fLog.severe("Directory does not exist: " + rootPath.toString()) ;
			} else if (! Files.isReadable(rootPath) ) {
				fLog.severe("Directory is not readable: " + rootPath.toString()) ;
			} else {
				try {
					Files.walkFileTree(rootPath, options, maxDepth, sfv) ;
				} catch (IOException e) {
					fLog.log(Level.SEVERE, "Exception when processing dir in recursive mode", e) ;
				}
			}
		}
	}
	
}

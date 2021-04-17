package com.ibm.lge.fl.util.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.spi.FileSystemProvider;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class FilesUtils {

	// Copy a directory tree
	public static boolean copyDirectoryTree(Path source, Path target, Logger log) throws IOException {
		
		CopyDirectoryVisitor copyDirVisitor = new CopyDirectoryVisitor(source, target, log) ;
		Files.walkFileTree(source, copyDirVisitor) ;
		return copyDirVisitor.isSuccessful() ;
	}
	
	// Delete a directory tree
	public static boolean deleteDirectoryTree(Path treePath, boolean force, Logger log) throws IOException {
		
		DeleteDirectoryVisitor deleteDirVisitor = new DeleteDirectoryVisitor(force, log) ;
		Files.walkFileTree(treePath,  deleteDirVisitor) ;
		return deleteDirVisitor.isSuccessful() ;
	}
	
	
	private static class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {
		
		private Logger  fLog ;
		private boolean success ;
		private boolean force ;
		
		public DeleteDirectoryVisitor(boolean f, Logger l) {
			super() ;
			fLog  	= l ;
			force 	= f ;
			success = true ;
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			try {
				Files.delete(file);
			} catch (AccessDeniedException e) {
				// if "force" try to set the file writable and retry the delete
				if (force) {
					try {
						FilesSecurityUtils.setWritable(file, null) ;	
						Files.delete(file);
					} catch (Exception e1) {
						success = false ;
						fLog.log(Level.SEVERE, "Exception trying to set file writable and delete the file " + file, e1) ;
					}
				} else {
					success = false ;
		    		fLog.log(Level.SEVERE, "AccessDeniedException deleting the file " + file, e);
				}
	    	} catch (Exception e) {
	    		success = false ;
	    		fLog.log(Level.SEVERE, "Exception deleting the file " + file, e);
	    	}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
			if (e == null) {
				try {
					Files.delete(dir);
				} catch (AccessDeniedException e1) {
					// if "force" try to set the file writable and retry the delete
					if (force) {
						try {
							FilesSecurityUtils.setWritable(dir, null) ;	
							Files.delete(dir);
						} catch (Exception e2) {
							success = false ;
							fLog.log(Level.SEVERE, "Exception trying to set file writable and delete the file " + dir, e2) ;
						}
					} else {
						success = false ;
			    		fLog.log(Level.SEVERE, "AccessDeniedException deleting the file " + dir, e1);
					}
		    	} catch (Exception e1) {
		    		success = false ;
		    		fLog.log(Level.SEVERE, "Exception deleting the directory " + dir, e1);
		    	}
				return FileVisitResult.CONTINUE;
			} else {
				// directory iteration failed. Continue nevertheless
				success = false ;
				return FileVisitResult.CONTINUE;
			}
		}
		
		public boolean isSuccessful() {
			return success ;
		}
	}
	
	private static class CopyDirectoryVisitor extends SimpleFileVisitor<Path> {
		
	    private Path 	targetPath ;
	    private Path 	sourcePath ;
	    private Logger 	fLog ;
	    private boolean success ;
	    
	    public CopyDirectoryVisitor(Path src, Path tgt, Logger l) {
	    	super() ;
	    	sourcePath = src ;
	        targetPath = tgt;
	        fLog 	   = l ;
	        success	   = true ;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	        if (sourcePath == null) {
	            sourcePath = dir;
	        } else {
	        	try {
	        		Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
	        	} catch (Exception e) {
	        		success = false ;
		    		fLog.log(Level.SEVERE, "Exception creating directory " + dir, e);
		    		return FileVisitResult.SKIP_SUBTREE ;
		    	}
	        }
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file,	 BasicFileAttributes attrs) throws IOException {
	    	try {
	    		Files.copy(file, targetPath.resolve(sourcePath.relativize(file)), StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
	    	} catch (Exception e) {
	    		success = false ;
	    		fLog.log(Level.SEVERE, "Exception copying the file " + file, e);
	    	}
	    	return FileVisitResult.CONTINUE;
	    }
	    
		public boolean isSuccessful() {
			return success ;
		}
	}
	
	
	// Get informations about the filestore of a given path and return them in a JsonObject
	public static JsonObject getFileStoreInformation(Path path, Logger logger) {
		
		JsonObject fsInfos ;
		try {
			
			FileStore fileStore = Files.getFileStore(path) ;
			fsInfos = getFileStoreInformation(fileStore, logger) ;
		} catch (Exception e) {
			fsInfos = new JsonObject() ;
			fsInfos.addProperty("error", 	"No fileStore associated to the path " + path);
			logger.log(Level.FINE, "Exception when getting FileStore informations for file " + path, e) ;
		}
				
		return fsInfos ;
	}
	
	// Get informations about the filestore and return them in a JsonObject
	public static JsonObject getFileStoreInformation(FileStore fileStore, Logger logger) {
		
		JsonObject fsInfos = new JsonObject() ;
		
		fsInfos.addProperty("name", 			fileStore.name());
		fsInfos.addProperty("type", 			fileStore.type());
		fsInfos.addProperty("isReadOnly", 		fileStore.isReadOnly());
		try {
			fsInfos.addProperty("totalSpace", 		fileStore.getTotalSpace());
			fsInfos.addProperty("unallocatedSpace", fileStore.getUnallocatedSpace());
			fsInfos.addProperty("usablSpace", 		fileStore.getUsableSpace());
			
		} catch (Exception e) {
			fsInfos.addProperty("error", 			"Unaccessible filestore");
			logger.log(Level.FINE, "Exception when getting FileStore informations", e) ;
		}
		return fsInfos ;
	}
	
	// Get informations about the FilesSystems and FileSystemProviders and return them in a JsonObject
	public static JsonObject getFileSystemsInformation(Logger logger) {
		
		JsonObject fssInfos  = new JsonObject() ;
		
		// FileSystemProviders infos
		JsonArray fpInfosArray = new JsonArray() ;
		List<FileSystemProvider> fsProviders = FileSystemProvider.installedProviders() ;	
		for (FileSystemProvider fsProvider : fsProviders) {
			fpInfosArray.add(fsProvider.getScheme()) ;
		}
		fssInfos.add("fileSystemProviderSchemes", fpInfosArray);
		
		// default file system infos
		FileSystem fs = FileSystems.getDefault() ;
		
		JsonObject defaultFsInfos = new JsonObject() ;
		defaultFsInfos.addProperty("defaultSeparator", fs.getSeparator()) ;	
		FileSystemProvider defaultProvider = fs.provider() ;
		String defaultScheme ;
		if (defaultProvider == null) {
			defaultScheme = "no provider found for default file system" ;
		} else {
			defaultScheme = defaultProvider.getScheme() ;
		}
		defaultFsInfos.addProperty("providerScheme", defaultScheme) ;

		JsonArray attViewsArray = new JsonArray() ;
		Set<String> attViews = fs.supportedFileAttributeViews() ;
		for (String attView : attViews) {
			attViewsArray.add(attView) ;
		}
		defaultFsInfos.add("supportedFileAttributesViews", attViewsArray);
		
		JsonArray fsInfosArray = new JsonArray() ;
		Iterable<FileStore> fileStores = fs.getFileStores() ;
		for (FileStore fileStore : fileStores) {
			fsInfosArray.add(getFileStoreInformation(fileStore, logger));
		}
		defaultFsInfos.add("FileStoresInfos", fsInfosArray) ;
		
		JsonArray rpInfosArray = new JsonArray() ;
		Iterable<Path> rootPaths = fs.getRootDirectories() ;
		for (Path rootPath : rootPaths) {
			JsonObject fsInfos = new JsonObject() ;
			fsInfos.addProperty("fileSystemRootPath", rootPath.toString());
			fsInfos.add("fileStoreInfos", getFileStoreInformation(rootPath, logger)) ;
			rpInfosArray.add(fsInfos);
		}
		defaultFsInfos.add("rootDirectoriesInfos", rpInfosArray) ;
		
		fssInfos.add("defaultFileSystemInfos", defaultFsInfos);
		return fssInfos ;
	}
	
 	public static BasicFileAttributes appendFileInformations(Path path, StringBuilder infos, Logger logger) {
		
		BasicFileAttributes basicAttributes = null ;
		
		if (infos == null) {
			infos = new StringBuilder() ;
			logger.fine("StringBuilder in parameter of FilesUtils.appendFileInformations is null. StringBuilder created") ;
		}
		
		if (path == null) {
			infos.append(" file is null\n") ;
		} else if (Files.notExists(path)) {
			infos.append(" ").append(path).append(" does not exist\n") ;
		} else { 
			if (! Files.exists(path)) {
				infos.append(" ").append(path).append(" existence is not known. Trying to get informations....\n") ;
			} else {
				infos.append(" ").append(path).append("\n") ;
			}
			try {
				
				FileStore fileStore = Files.getFileStore(path) ;
				
				if (fileStore.supportsFileAttributeView(BasicFileAttributeView.class)) {
					
					infos.append(" BasicFileAttributes:\n") ;
					basicAttributes = Files.readAttributes(path, BasicFileAttributes.class);
					long size = basicAttributes.size() ;
					
					FileTime lastModified = basicAttributes.lastModifiedTime() ;
					FileTime lastAccessed = basicAttributes.lastAccessTime() ;
					FileTime creationTime = basicAttributes.creationTime() ;
					
					if (basicAttributes.isRegularFile()) {
						infos.append("   is a regular file\n") ;
					} else if (basicAttributes.isDirectory()) {
						infos.append("   is a directory\n") ;
					} else if (basicAttributes.isSymbolicLink()) {
						infos.append("   is a symbolic link\n") ;
					} else if (basicAttributes.isOther()) {
						infos.append("   is something else (than a regular file, directory or symbolic link\n") ;
					} else {
						infos.append("   must be really strange...\n") ;
					}
					
					infos.append("   Its size is " + size + " bytes\n") ;
					infos.append("   Last modified time is ").append(printFileTime(lastModified)).append("\n") ;
					infos.append("   Last accessed time is ").append(printFileTime(lastAccessed)).append("\n") ;
					infos.append("   Creation time is      ").append(printFileTime(creationTime)).append("\n\n") ;
					
				} else {
					infos.append(" BasicFileAttributes is not supported\n\n") ;
				}
				
				if (fileStore.supportsFileAttributeView(FileOwnerAttributeView.class)) {
					infos.append(" FileOwnerAttributes:\n") ;
					FileOwnerAttributeView ownerView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
					UserPrincipal userPrincipal = ownerView.getOwner() ;
					infos.append("   User principal : ").append(userPrincipal.toString()).append("\n\n") ;
				} else {
					infos.append(" FileOwnerAttributes is not supported\n\n") ;
				}

				if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
					infos.append(" AclFileAttributes:\n") ;
					AclFileAttributeView aclView = Files.getFileAttributeView(path, AclFileAttributeView.class);
					
					List<AclEntry> aclEntries = aclView.getAcl() ;
					infos.append("   Acl Entries:\n") ;
					for (AclEntry aclEntry : aclEntries) {
						UserPrincipal userPrincipal = aclEntry.principal() ;
						infos.append("     User principal : ").append(userPrincipal.toString()).append("\n") ;
						
						AclEntryType aclEntryType = aclEntry.type() ;
						infos.append("     Acl entry type: ").append(aclEntryType.toString()).append("\n") ;
						
						infos.append("     Acl entry flags:\n       ") ;
						Set<AclEntryFlag> entryFlags = aclEntry.flags() ;
						if ((entryFlags != null) && (! entryFlags.isEmpty())) {
							for (AclEntryFlag entryFlag : entryFlags) {
								infos.append(entryFlag.toString()).append(" ") ;
							}
							infos.append("\n") ;
						} else {
							infos.append("No entry flags\n") ;
						}
						
						infos.append("     Acl entry permissions:\n       ") ;
						Set<AclEntryPermission> entryPermissions = aclEntry.permissions() ;
						if ((entryPermissions != null) && (! entryPermissions.isEmpty())) {
							for (AclEntryPermission entryPermission : entryPermissions) {
								infos.append(entryPermission.toString()).append(" ") ;
							}
							infos.append("\n\n") ;
						} else {
							infos.append("No entry permissions\n") ;
						}
					}
				} else {
					infos.append(" AclFileAttributes is not supported\n\n") ;
				}

				if (fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
					infos.append(" DosFileAttributes:\n") ;
					
					DosFileAttributes dosAttributes = Files.readAttributes(path, DosFileAttributes.class);
					infos.append("   is archive:   ").append(dosAttributes.isArchive()).append("\n") ;
					infos.append("   is hidden:    ").append(dosAttributes.isHidden()).append("\n") ;
					infos.append("   is read only: ").append(dosAttributes.isReadOnly()).append("\n") ;
					infos.append("   is system:    ").append(dosAttributes.isSystem()).append("\n\n") ;
				} else {
					infos.append(" DosFileAttributes is not supported\n\n") ;
				}

				if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
					infos.append(" PosixFileAttributes:\n") ;
					
					PosixFileAttributes posixAttributes = Files.readAttributes(path, PosixFileAttributes.class) ;
					
					GroupPrincipal groupPrincipal 		 = posixAttributes.group() ;
					UserPrincipal userPrincipal 		 = posixAttributes.owner() ;
					Set<PosixFilePermission> permissions = posixAttributes.permissions() ;
					
					infos.append("   Owner: ").append(userPrincipal.toString()).append("\n") ;
					infos.append("   Group: ").append(groupPrincipal.toString()).append("\n") ;
					appendPosixPermissions(infos, permissions) ;
					infos.append("\n\n") ;
				} else {
					infos.append(" PosixFileAttributes is not supported\n\n") ;
				}

				if (fileStore.supportsFileAttributeView(UserDefinedFileAttributeView.class)) {
					infos.append(" UserDefinedFileAttributes:\n") ;
					
					UserDefinedFileAttributeView userDefinedView = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
					
					List<String> userDefinedAttributes = userDefinedView.list() ;
					if ((userDefinedAttributes != null) && (! userDefinedAttributes.isEmpty())) {
						for (String attributeName : userDefinedAttributes) {
							infos.append("   ").append(attributeName).append(" attribute value:\n") ;
							
							// get attribute value
							infos.append("   ").append(attributeName).append("\n") ;
							ByteBuffer buf = ByteBuffer.allocate(userDefinedView.size(attributeName));
							userDefinedView.read(attributeName, buf);
						    buf.flip();
						    infos.append(Charset.defaultCharset().decode(buf)) ;
						    infos.append("\n\n") ;  
						}
					} else {
						infos.append("   UserDefinedFileAttributes are supported but there is no attribute defined\n") ;
					}
				} else {
					infos.append(" UserDefinedFileAttributes is not supported\n\n") ;
				}
				
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Exception when appending informations for file " + path, e) ;
			}
		}
		return basicAttributes ;
	}
	
	private final static String datePattern = "uuuu-MM-dd HH:mm:ss.SSS VV" ;
	private final static String unknownTime = "not known" ;
	private static String printFileTime(FileTime fileTime) {
		
		if (fileTime == null) {
			return unknownTime ;
		}  else {
			return DateTimeFormatter.ofPattern(datePattern).format(ZonedDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault())) ;
		}
	}
	
	private final static String READ_PERMISSION    = "r";
	private final static String WRITE_PERMISSION   = "w"; 
	private final static String EXECUTE_PERMISSION = "x"; 
	private final static String NO_PERMISSION 	   = "-"; 
	private static void appendPosixPermissions(StringBuilder buff, Set<PosixFilePermission> permissions) {
		
		buff.append(getOnePermission(permissions, PosixFilePermission.OWNER_READ,     READ_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.OWNER_WRITE,    WRITE_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.OWNER_EXECUTE,  EXECUTE_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.GROUP_READ,     READ_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.GROUP_WRITE,    WRITE_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.GROUP_EXECUTE,  EXECUTE_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.OTHERS_READ,    READ_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.OTHERS_WRITE,   WRITE_PERMISSION)) ;
		buff.append(getOnePermission(permissions, PosixFilePermission.OTHERS_EXECUTE, EXECUTE_PERMISSION)) ;
	}
	
	private static String getOnePermission(Set<PosixFilePermission> permissions, PosixFilePermission permission, String s) {
		if (permissions.contains(permission)) {
			return s ;
		} else {
			return  NO_PERMISSION ;
		}
	}
	
	public static Path getMountPoint(Path path, Logger logger) {
		
		Path mountPoint = path ;
		
		try {
			FileStore fileStore = Files.getFileStore(path) ;

			Path parent = path ;
			FileStore parentFileStore = null ;
			do {
				parent = parent.getParent() ;
				if (parent != null) {
					parentFileStore = Files.getFileStore(mountPoint) ;
					mountPoint = parent ;
				}
			} while ((parent != null) && (fileStore.equals(parentFileStore))) ;
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception when searching mount point for file " + path, e) ;
		}
		
		return mountPoint ;
	}
	
	public static long folderSize(Path folder, Logger logger) {
		long size = 0;
		try {
			size = Files.walk(folder).filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Exception when calculating folder size of " + folder, e) ;
		}
		return size ;		
	}
	
}

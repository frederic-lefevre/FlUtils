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
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.os.OSCommandController;

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
	

	
	// Set permission for a file.
	// If it does not exists the file is created with the given permission
	public static void setFilePermission(Path fPath, String permissions, Logger pLog) {
		setPermission(false, fPath, permissions, pLog) ;
	}
	
	// Set permission for a directory.
	// If it does not exists the directory is created with the given permission
	public static void setDirectoryPermission(Path fPath, String permissions, Logger pLog) {
		setPermission(true, fPath, permissions, pLog) ;
	}
	
	private static void setPermission(boolean isDirectory, Path fPath, String permissions, Logger pLog) {
		
		if (! OSCommandController.isOSWindows()) {
			// permission setting does not work on Windows
			
			FileAttribute<Set<PosixFilePermission>> fileAttributes = null ;
			Set<PosixFilePermission> perms = null;
			if ((permissions != null) && (! permissions.isEmpty())) {
				try {
					perms = PosixFilePermissions.fromString(permissions);
					fileAttributes = PosixFilePermissions.asFileAttribute(perms);
					
					if (Files.exists(fPath))  {
						Files.setPosixFilePermissions(fPath, perms) ;
					} else if (isDirectory) {
						Files.createDirectory(fPath, fileAttributes) ;
					} else {
						Files.createFile(fPath, fileAttributes) ;
					}
					
				} catch (IOException e) {
					pLog.log(Level.SEVERE, "IOException when creating a policy file " + fPath + " with permission parameter " + permissions, e) ;
				} catch (Exception e) {
					pLog.log(Level.SEVERE, "Exception when parsing posix file permission parmeter: " + permissions, e) ;
				}
			}
		}
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
						setWritable(file) ;	
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
							setWritable(dir) ;	
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
	
	public static void setWritable(Path path) throws IOException {

		AclFileAttributeView aclAttr = Files.getFileAttributeView(path, AclFileAttributeView.class);

		UserPrincipalLookupService upls = path.getFileSystem().getUserPrincipalLookupService();
		UserPrincipal user = upls.lookupPrincipalByName(System.getProperty("user.name"));

		AclEntry.Builder builder = AclEntry.newBuilder();
		builder.setPermissions(EnumSet.of(AclEntryPermission.READ_DATA, AclEntryPermission.EXECUTE,
				AclEntryPermission.READ_ACL, AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.READ_NAMED_ATTRS,
				AclEntryPermission.WRITE_ACL, AclEntryPermission.DELETE, AclEntryPermission.WRITE_DATA));
		builder.setPrincipal(user);
		builder.setType(AclEntryType.ALLOW);
		aclAttr.setAcl(Collections.singletonList(builder.build()));
		 
		if (! OSCommandController.isOSWindows()) {
			// posix permission setting does not work on Windows
			
			Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
			// add owners permission
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			// add group permissions
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_WRITE);
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			// add others permissions
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_WRITE);
			perms.add(PosixFilePermission.OTHERS_EXECUTE);
			Files.setPosixFilePermissions(path, perms);
		} else {
			 Files.setAttribute(path, "dos:readonly", false);
		}
	}
	
	public static void getFileStoreInformation(Path path, StringBuilder fsInfos, Logger logger) {
		
		try {
			
			FileStore fileStore = Files.getFileStore(path) ;
			fsInfos.append("FileStore name=").append(fileStore.name()).append("\n") ;
			fsInfos.append("FileStore type=").append(fileStore.type()).append("\n") ;
			fsInfos.append("FileStore readOnly=").append(fileStore.isReadOnly()).append("\n") ;
			fsInfos.append("FileStore total space=      ").append(fileStore.getTotalSpace()).append("\n") ;
			fsInfos.append("FileStore Unallocated space=").append(fileStore.getUnallocatedSpace()).append("\n") ;
			fsInfos.append("FileStore Usable space=     ").append(fileStore.getUsableSpace()).append("\n") ;
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception when appending FileStore informations for file " + path, e) ;
		}
	}
	
	public static void getFileStoreInformation(StringBuilder fsInfos, Logger logger) {
		
		FileSystem fs = FileSystems.getDefault() ;
		
		Iterable<Path> rootPaths = fs.getRootDirectories() ;
		for (Path rootPath : rootPaths) {
			fsInfos.append("FileSystem Root path=").append(rootPath.toString()).append("\n") ;
			getFileStoreInformation(rootPath, fsInfos, logger) ;
		}
	}
	
 	public static BasicFileAttributes appendFileInformations(Path path, StringBuilder infos, Logger logger) {
		
		BasicFileAttributes basicAttributes = null ;
		
		if (infos == null) {
			infos = new StringBuilder() ;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("StringBuilder in poarameter of FilesUtils.appendFileInformations is null. StringBuilder created") ;
			}
		}
		
		if (path == null) {
			infos.append(" file is null\n") ;
		} else if  (! Files.exists(path)) {
			infos.append(" ").append(path).append(" does not exist\n") ;
		} else {
			infos.append(" ").append(path).append("\n") ;
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
}

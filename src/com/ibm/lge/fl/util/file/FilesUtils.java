package com.ibm.lge.fl.util.file;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
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
}

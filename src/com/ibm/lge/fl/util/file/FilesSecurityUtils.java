package com.ibm.lge.fl.util.file;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilesSecurityUtils {

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
		
		try {
			
			FileStore fileStore = Files.getFileStore(fPath) ;
		
			if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
				FileAttribute<Set<PosixFilePermission>> fileAttributes = null ;
				Set<PosixFilePermission> perms = null;
				if ((permissions != null) && (! permissions.isEmpty())) {
					
						perms = PosixFilePermissions.fromString(permissions);
						fileAttributes = PosixFilePermissions.asFileAttribute(perms);
						
						if (Files.exists(fPath))  {
							Files.setPosixFilePermissions(fPath, perms) ;
						} else if (isDirectory) {
							Files.createDirectory(fPath, fileAttributes) ;
						} else {
							Files.createFile(fPath, fileAttributes) ;
						}
						
					
				}
			}
		}catch (IOException e) {
			pLog.log(Level.SEVERE, "IOException when creating a policy file " + fPath + " with permission parameter " + permissions, e) ;
		} catch (Exception e) {
			pLog.log(Level.SEVERE, "Exception when parsing posix file permission parameter: " + permissions, e) ;
		}
	}

	private static ArrayList<UserPrincipal> getAclPrincipals(Path path) throws IOException {
		
		ArrayList<UserPrincipal> userPrincipals = new ArrayList<UserPrincipal>() ;
		FileStore fileStore = Files.getFileStore(path) ;
		if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
					
			AclFileAttributeView aclView = Files.getFileAttributeView(path, AclFileAttributeView.class);
			
			List<AclEntry> aclEntries = aclView.getAcl() ;
			if (aclEntries != null) {
				for (AclEntry aclEntry : aclEntries) {
					userPrincipals.add(aclEntry.principal()) ;
				}
			}
		}
		return userPrincipals ;
	}
	
	// Set a file writable : use all the possible attributes views
	// For the ACL view, set rights to the user running the current process
	// and all the users defined in the ACL of sourcePath
	public static void setWritable(Path path, Path sourcePath) throws IOException {

		FileStore fileStore = Files.getFileStore(path) ;
		
		// Get the UserPrincipal of the running process
		UserPrincipalLookupService upls = path.getFileSystem().getUserPrincipalLookupService();
		UserPrincipal user = upls.lookupPrincipalByName(System.getProperty("user.name"));

		if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
			
			AclFileAttributeView aclAttr = Files.getFileAttributeView(path, AclFileAttributeView.class);
			
			ArrayList<AclEntry> entries = new ArrayList<AclEntry>() ;
			
			// set the file writable for the user running this process
			entries.add(buildAclEntry(path, user)) ;
			
			// set the file writable for all the users defined in the ACL of sourcePath
			if (sourcePath != null) {
				List<UserPrincipal> users = getAclPrincipals(sourcePath) ;
				if (users != null) {
					for (UserPrincipal userPrincipal : users) {
						if (! userPrincipal.equals(user)) {
							entries.add(buildAclEntry(path, userPrincipal)) ;
						}
					}
				}
			}
			aclAttr.setAcl(entries) ;
			
		}
		
		if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
			
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
		} 
		
		if (fileStore.supportsFileAttributeView(FileOwnerAttributeView.class)) {
			FileOwnerAttributeView ownerView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
			ownerView.setOwner(user) ;
		}
	}
	
	private static AclEntry buildAclEntry(Path path, UserPrincipal user) throws IOException {
		
		AclEntry.Builder builder = AclEntry.newBuilder();
		Set<AclEntryPermission> allPermissions = new HashSet<AclEntryPermission>(Arrays.asList( AclEntryPermission.values())) ;	
		builder.setPermissions(allPermissions);
		builder.setPrincipal(user);
		builder.setType(AclEntryType.ALLOW);
		return builder.build() ;
		
		
	}
	
}

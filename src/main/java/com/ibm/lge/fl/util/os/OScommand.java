package com.ibm.lge.fl.util.os;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OScommand extends Thread {

	private String  command ;
	private Logger  oLog;
	private boolean processSuccessfull ; 
	private boolean waitTermination ;
	
	public OScommand(String cmd, boolean w, Logger l) {
		super();
		command 		= cmd ;
		waitTermination = false ;
		oLog 			= l ;
	}

	public void run() {
		
		Runtime r = Runtime.getRuntime();
		
		try {
			Process p = r.exec(command);
			oLog.fine("Command launched: " + command) ;
			
			if (waitTermination) {
				int ret = p.waitFor() ;
				oLog.info("Command terminated: " + command + " Return=" + ret) ;
			}
			
			// we assume the command is successfull (ret=0 is only a convention) 
			processSuccessfull = true ;
			
		} catch (IOException e) {
			processSuccessfull = false ;
			oLog.log(Level.SEVERE, "IOException executing command " + command, e) ;
		} catch (SecurityException e) {
			processSuccessfull = false ;
			oLog.log(Level.SEVERE, "SecurityException executing command " + command, e) ;
		} catch (InterruptedException e) {
			processSuccessfull = false ;
			oLog.log(Level.WARNING, "InterruptedException executing command " + command, e) ;
		} catch (Exception e) {
			processSuccessfull = false ;
			oLog.log(Level.SEVERE, "Exception executing command " + command, e) ;
		}
	}

	public boolean isProcessSuccessfull() {
		return processSuccessfull;
	}

	public String getCommand() {
		return command;
	}
}

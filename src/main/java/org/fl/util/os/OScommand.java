/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.os;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OScommand extends Thread {

	private final String command;
	private final Logger oLog;
	private boolean processSuccessfull;
	private final boolean waitTermination;
	
	public OScommand(String cmd, boolean w, Logger l) {
		super();
		command = cmd;
		waitTermination = w;
		oLog = l;
	}

	public void run() {
		
		Runtime r = Runtime.getRuntime();
		
		try {
			Process p = r.exec(command);
			oLog.fine(() -> "Command launched: " + command) ;
			
			if (waitTermination) {
				int ret = p.waitFor() ;
				oLog.info(() -> "Command terminated: " + command + " Return=" + ret) ;
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

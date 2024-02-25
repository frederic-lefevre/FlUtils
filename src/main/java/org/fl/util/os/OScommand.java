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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OScommand extends Thread {

	private final String command;
	private final Logger oLog;
	private final List<String> commandParameters;
	private final List<String> commandOptions;
	private final boolean waitTermination;
	private boolean processSuccessfull;
	
	public OScommand(String cmd, List<String> p, List<String> o, boolean w, Logger l) {
		super();
		command = cmd;
		commandParameters = p;
		commandOptions = o;
		waitTermination = w;
		oLog = l;
	}

	public void run() {
		
		Runtime r = Runtime.getRuntime();
		List<String> cmdAndParams = new ArrayList<>();
		cmdAndParams.add(getCommand());
		if ((commandOptions != null) && !commandOptions.isEmpty()) {
			cmdAndParams.addAll(commandOptions);
		}
		if ((commandParameters != null) && !commandParameters.isEmpty()) {
			cmdAndParams.addAll(cmdAndParams);
		}
		
		try {
			Process p = r.exec(cmdAndParams.toArray(new String[0]));
			oLog.fine(() -> "Command launched: " + cmdAndParams.toString()) ;
			
			if (waitTermination) {
				int ret = p.waitFor() ;
				oLog.info(() -> "Command terminated: " + cmdAndParams.toString() + " Return=" + ret) ;
			}
			
			// we assume the command is successfull (ret=0 is only a convention) 
			processSuccessfull = true ;
			
		} catch (IOException e) {
			processSuccessfull = false ;
			oLog.log(Level.SEVERE, "IOException executing command " + cmdAndParams.toString(), e) ;
		} catch (SecurityException e) {
			processSuccessfull = false ;
			oLog.log(Level.SEVERE, "SecurityException executing command " + cmdAndParams.toString(), e) ;
		} catch (InterruptedException e) {
			processSuccessfull = false ;
			oLog.log(Level.WARNING, "InterruptedException executing command " + cmdAndParams.toString(), e) ;
		} catch (Exception e) {
			processSuccessfull = false ;
			oLog.log(Level.SEVERE, "Exception executing command " + cmdAndParams.toString(), e) ;
		}
	}

	public boolean isProcessSuccessfull() {
		return processSuccessfull;
	}

	public String getCommand() {
		return command;
	}
}

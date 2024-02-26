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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OSCommandController {

	private ArrayList<OScommand> commands ;
	private ArrayList<OScommand> runningCommands ;
	private Logger oLog ;
	private int maxParallelCommand ;
	private int passiveWait ;

	// n = max number of command running in parallel
	public OSCommandController(int n, Logger l) {
		super();
		oLog = l ;
		maxParallelCommand = n ;
		passiveWait = 2000 ;                            // default value, may be overwrite by setter
		commands = new ArrayList<OScommand>() ;
		runningCommands = new ArrayList<OScommand>() ;
	}
	
	public void addCommand(String cmd, List<String> params, List<String> options) {
		
		commands.add(new OScommand(cmd, params, options, true, oLog)) ;
	}
	
	// execute all commands and exit when it is over
	public boolean executeCommands() {
		
		for (OScommand cmd : commands) {
			if (runningCommands.size() < maxParallelCommand) {
				cmd.start() ;
				runningCommands.add(cmd) ;
			} else {
				waitOneThreadEnd() ;
				cmd.start() ;
				runningCommands.add(cmd) ;
			}
		}
		return waitAllThreadsEnd() ;
	}

	public void setPassiveWait(int passiveWait) {
		this.passiveWait = passiveWait;
	}
	
	// wait until the number of threads alive is equal or less than maxParallelCommand
	private void waitOneThreadEnd() {
		
		try {
            boolean nbThreadsTooHigh = true ;
            while (nbThreadsTooHigh) {
                if (passiveWait > 0) {
                    Thread.sleep(passiveWait) ;
                }
                
                Iterator<OScommand> cmdIterator = runningCommands.iterator() ;
                while (cmdIterator.hasNext()) {
                    if (! cmdIterator.next().isAlive()) {
                    	cmdIterator.remove() ;
                    	nbThreadsTooHigh = false ;
                    }
                }
            } 
        } catch (InterruptedException e) {
            oLog.log(Level.SEVERE, "Interruption during wait file process end", e) ;
        }
	}
	
	// wait until the end of all threads
	private boolean waitAllThreadsEnd() {
		
		boolean success = true ;
		try {
            while (runningCommands.size() > 0) {
                if (passiveWait > 0) {
                    Thread.sleep(passiveWait) ;
                }
                
                Iterator<OScommand> cmdIterator = runningCommands.iterator() ;
                while (cmdIterator.hasNext()) {
                    if (! cmdIterator.next().isAlive()) {
                    	cmdIterator.remove() ;
                    }
                }
            }
            for (OScommand cmd : commands) {
            	success = cmd.isProcessSuccessfull() && success ;
            }
        } catch (InterruptedException e) {
        	success = false ;
            oLog.log(Level.SEVERE, "Interruption during wait file process end", e) ;
        }
		return success ;
	}
	
	public static boolean isOSWindows() {
		String operatingSystem = System.getProperty("os.name");
		return operatingSystem.startsWith("Windows") ;
	}
}

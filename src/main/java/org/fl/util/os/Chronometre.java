/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

public class Chronometre {

	private boolean running ;
	private long 	lastStartTime ;
	private long 	previousValue ;
	private long 	lastGetTime;
	
	public Chronometre() {
		running 	  = false ;
		previousValue = 0 ;
	}

	public void start() {
		lastStartTime = System.currentTimeMillis() ;
		lastGetTime   = lastStartTime ;
		running 	  = true ;
	}

	public long getValue() {
		if (running) {
			lastGetTime = System.currentTimeMillis() ;
			return previousValue + (lastGetTime - lastStartTime) ;
		} else {
			return previousValue;
		}
	}
	
	public long getDeltaValue() {
		if (running) {
			long t = lastGetTime ;
			lastGetTime = System.currentTimeMillis()  ;
			return (lastGetTime - t) ;
		} else {
			return 0;
		}
	}
	
	public long pause() {
		if (running) {
			lastGetTime = System.currentTimeMillis() ;
			previousValue = previousValue + (lastGetTime - lastStartTime) ;
			running = false ;
		}
		return previousValue ;
	}
}

/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

package org.fl.util.api;

public class ApiErrorCode {

	private int    code ;
	private String reason ;
	private String action ;
	
	public final static String WAIT_AND_RETRY_ACTION 	  = "waitAndRetry" ;
	public final static String RETRY_ACTION			 	  = "retry" ;
	public final static String UNDETERMINED_ACTION	 	  = "undetermined" ;
	public final static String REPORT_ACTION 		 	  = "report" ;
	public final static String RETRY_AND_REPORT_ACTION 	  = "retryAndReport" ;
	public final static String CORRECT_CALLER_CODE_ACTION = "correctCallerCode" ;
	public final static String REFORMULATE_QUERY_ACTION   = "reformulateQuery" ;
	
	public ApiErrorCode(int c, String r) {
		code   = c ;
		reason = r ;
		action = UNDETERMINED_ACTION ;
	}

	public ApiErrorCode(int c, String r, String a) {
		code   = c ;
		reason = r ;
		action = a ;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getCode() {
		return code;
	}

	public String getReason() {
		return reason;
	}
}

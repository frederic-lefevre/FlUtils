package com.ibm.lge.fl.util.api;

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

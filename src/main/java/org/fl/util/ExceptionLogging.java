package org.fl.util;

public class ExceptionLogging {

	public static String printExceptionInfos(Throwable e, int maxCauseLevel) {
					
		StringBuilder exceptionInfos = new StringBuilder() ;
				
		addExceptionInfos(e, exceptionInfos, 0, maxCauseLevel) ;
		
		return exceptionInfos.toString() ;
	}
	
	private static void addExceptionInfos(Throwable e, StringBuilder exceptionInfos, int currentCauseLevel, int maxCauseLevel) {
		
		if (e != null) {
			exceptionInfos.append("Exception " + e.getMessage()) ;
			
			StackTraceElement[] stackElems = e.getStackTrace() ;
			if (stackElems != null){
				for (StackTraceElement stackElem : stackElems) {
					exceptionInfos.append("\n\t\tat ")
								  .append(stackElem.getClassName())
								  .append(" ")
								  .append(stackElem.getMethodName())
								  .append(" (")
								  .append(stackElem.getFileName())
								  .append(":")
								  .append(stackElem.getLineNumber())
								  .append(")") ;
					
				}
			}
			Throwable cause = e.getCause() ;
			if (cause != null) {
				currentCauseLevel++ ;
				if (currentCauseLevel < maxCauseLevel) {
					exceptionInfos.append("\nCaused by ") ;
					addExceptionInfos(cause, exceptionInfos, currentCauseLevel, maxCauseLevel) ;
				} else {
					exceptionInfos.append("\n More cause omitted") ;
				}
			}
			exceptionInfos.append('\n') ;
		}
	}
}

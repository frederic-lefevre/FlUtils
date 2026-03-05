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

package org.fl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlScript {

	private List<String> sqlScript ;
	private Logger sLog ;
	
	// Line comment delimiter : after these char, everything is comment to the end of the line
	private final static String[] commentDelimiters = {"#", "--" } ;
	
	// Between "beginComment" and "endComment", everything is comment.
	private final static String beginComment = "/*" ;
	private final static String endComment = "*/" ;
	
	private boolean insideEnclosedComment ;
	
	
	public SqlScript(Path sqlFile, String endStatement, Logger log) {
		
		sLog = log ;
		sqlScript = new ArrayList<>() ;
		StringBuilder statement = new StringBuilder();
		
		try (BufferedReader reader = Files.newBufferedReader(sqlFile)) {
		    String line = null;
		    insideEnclosedComment = false ;
		    
		    while ((line = reader.readLine()) != null) {
		    	
		    	// remove comment and append the result to the current statement
		    	String codeLine = removeComment(line).trim() ;
		    	if ((codeLine != null) && (! codeLine.isEmpty())) {
		    		statement.append(codeLine) ;
		    	}
		    	
		    	// if it is the end of a statement, store it
		    	if (statement.length() > 0) {
			    	if (statement.lastIndexOf(endStatement) == statement.length() - endStatement.length()) {

			    		// remove the end statement delimiter
			    		String st = statement.substring(0, statement.length() - endStatement.length()).toString() ;
			    		
			    		sLog.finest(() -> "Add statement=\n" + st);
			    		sqlScript.add(st) ;
			    		
			    		// new buffer to hold the next statement
			    		statement = new StringBuilder();
			    	} else if ((codeLine != null) && (! codeLine.isEmpty())) {
			    		statement.append(' ') ;
			    	}
		    	}
		       
		    }
		} catch (IOException e) {
		    sLog.log(Level.SEVERE, "IOexception reading SQL file " + sqlFile, e);
		}
		
	}
	
	public boolean executeStatements(String driverClass, String dbUrl, String dbUser, String dbPassword) {
		
		boolean success = true ;
		try {
			// Load the IBM Data Server Driver for JDBC and SQLJ with DriverManager
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			success = false ;
		    sLog.log(Level.SEVERE,"jdbc driver not found : " + driverClass, e) ;
		}
		
		Connection conn = null ;
		try {
			
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword) ;
			Statement ps = null;
			
			if (conn != null) {
				
				for (String statement : sqlScript) {
					
					sLog.finest(() -> "Execute statement=\n" + statement);
					ps = conn.createStatement();	
					
					if (ps != null) {
						
						try {
							ps.execute(statement) ;
						 
							ps.close() ;
						} catch (SQLException e) {
							success = false ;
							sLog.log(Level.WARNING, "SQLException executing statement " + statement, e) ;
						}
					
					} else {
						success = false ;
						sLog.severe("Cannot get statement") ;
					}
				}
			} else {
				success = false ;
				sLog.severe("Cannot get DB connection") ;
			}
			
		} catch (SQLException e) {
			success = false ;
			sLog.log(Level.WARNING, "SQLException  ", e) ;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				success = false ;
				sLog.log(Level.WARNING, "Exception when closing connection ", e) ;
			}
		}
		
		return success ;
	}
	
	private String removeComment(String line) {
		return removeEnclosedComment(removeLineComment(line, commentDelimiters), beginComment, endComment) ;
	}
	
	private String removeLineComment(String line, String[] delimiters) {
		
		for (String delimiter : delimiters) {
			int dIndex = line.indexOf(delimiter) ;
			if (dIndex == 0) {
				// all line is comment
				return "" ;
			} else if (dIndex > 0) {
				// there is a comment at one point in the line
				line = line.substring(0, dIndex) ;
			}
		}
		return line ;
	}
	
	// Warning : recursive function
	private String removeEnclosedComment(String line, String bDel, String eDel) {
		
		if (insideEnclosedComment) {
			// the line begin is inside a comment, so search for the end
			
			int deIndex = line.indexOf(eDel) ;
			if (deIndex > -1) {
				// the comment ends in this line. Remove the comment et process the remaining
				insideEnclosedComment = false ;
				return removeEnclosedComment(line.substring(deIndex + eDel.length()), bDel, eDel) ;
			} else {
				// no end delimiter, all line is comment
				return "" ;
			}
		} else {
			// the line begin is not inside a comment
			
			int dbIndex = line.indexOf(bDel) ;
			int deIndex = line.indexOf(eDel, dbIndex + bDel.length()) ;
			if ((dbIndex > -1) && (deIndex > -1)) {
				// there is a begin and end comment delimiter in this line
				
				if ((dbIndex > 0) && (deIndex + eDel.length() < line.length())) {
					// A comment is inside the line
					return line.substring(0, dbIndex) + removeEnclosedComment(line.substring(deIndex + eDel.length()), bDel, eDel) ;
				} else if (dbIndex > 0) {
					// the line ends with a single comment
					return line.substring(0, dbIndex) ;
				} else {
					// the line is begining with a comment
					return removeEnclosedComment(line.substring(deIndex + eDel.length()), bDel, eDel) ;
				}
			} else if (dbIndex > -1) {
				// there is a begin and but no end comment delimiter in this line
				// return the begining of the line and indicate we are inside a comment
				insideEnclosedComment = true ;
				return line.substring(0, dbIndex) ;
			} else {
				// there is no comment in the line
				return line ;
			}
		}
	}
	
	public String printRawSqlStatements() {
		
		StringBuilder sb = new StringBuilder() ;
		for (String statement : sqlScript) {
			sb.append(statement).append("\n");
		}
		return sb.toString() ;
	}
}

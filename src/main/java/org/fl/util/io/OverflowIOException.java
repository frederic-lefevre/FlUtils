package org.fl.util.io;

import java.io.IOException;

public class OverflowIOException extends IOException {

	private static final long serialVersionUID = 4222004829169812787L;

	public OverflowIOException(String message) {
        super(message);
    }
	
}

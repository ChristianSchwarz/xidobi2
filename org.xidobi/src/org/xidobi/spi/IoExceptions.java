/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 02.09.2013 10:07:11
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.spi;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility Class that creates {@link IOException}'s for common cases.
 * @author Christian Schwarz
 *
 */
public class IoExceptions {

	
	/**
	 * Returns a new {@link IOException} indicating that the port is closed.
	 * 
	 * @return a new {@link IOException}, never <code>null</code>
	 */
	@Nonnull
	public static IOException portClosedException() {
		return portClosedException(null,null);
	}

	/**
	 * Returns a new {@link IOException} indicating that the port is closed. 
	 * 
	 * @param portName the name of the port, may be <code>null</code>
	 * 
	
	 * @return a new {@link IOException}, never <code>null</code>
	 */
	@Nonnull
	public static IOException portClosedException(@Nullable String portName) {
		return portClosedException(portName,null);
	}
	
	/**
	 * Returns a new {@link IOException} indicating that the port is closed. 
	 * 
	 * @param portName the name of the port, may be <code>null</code>
	 * 
	 * @param message
	 *            error description, may be <code>null</code>
	 * @return a new {@link IOException}, never <code>null</code>
	 */
	@Nonnull
	public static IOException portClosedException(@Nullable String portName,@Nullable String message) {
		if (message == null)
			message = "";
		else
			message = " Cause: " + message;
		
		if (portName==null)
			portName="";
		else
			portName= portName+" ";
		
		
		return new IOException("Port " + portName + "was closed!" + message);
	}
}

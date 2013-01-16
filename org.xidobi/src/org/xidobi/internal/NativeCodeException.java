/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.01.2013 15:54:32
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

/**
 * This exception is used to indicate an unexpected result, behavior or error that was detected
 * after a native method call.
 * 
 * @author Christian Schwarz
 * 
 */
public class NativeCodeException extends RuntimeException {

	/**	 */
	private static final long serialVersionUID = 8424047512526437203L;

	/**
	 * Creates a new NativeCodeException to indicate an unexpected result, behavior or error that
	 * was detected after a native method call.
	 * 
	 * @param message
	 *            can be <code>null</code>
	 */
	public NativeCodeException(String message) {
		super(message);
	}
}
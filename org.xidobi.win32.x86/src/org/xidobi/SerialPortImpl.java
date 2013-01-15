/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 10:27:07
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

/**
 * {@link SerialPort} implementation for the win32 x86 Platform.
 * 
 * @author Christian Schwarz
 * 
 */
public class SerialPortImpl implements SerialPort {

	/**
	 * 
	 */
	public SerialPortImpl(	OS os,
							int handle) {
		checkArgumentNotNull(os, "os");
		
	}

	public void write(byte[] data) throws IOException {
		
	}

	public byte[] read() throws IOException {
		return null;
	}

	public void close() throws IOException {}
}

/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 10:27:07
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import java.io.IOException;

/**
 * @author Christian Schwarz
 *
 */
public class SerialPortImpl implements SerialPort {

	/**
	 * 
	 */
	SerialPortImpl(OS os, int handle) {}

	public void write(byte[] data) throws IOException {}

	public byte[] read() throws IOException {
		return null;
	}

	public void close() throws IOException {}
}

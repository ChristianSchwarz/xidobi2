/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 15.01.2013 11:25:35
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import static org.xidobi.OS.FILE_FLAG_OVERLAPPED;
import static org.xidobi.OS.GENERIC_READ;
import static org.xidobi.OS.GENERIC_WRITE;
import static org.xidobi.OS.OPEN_EXISTING;

import java.io.IOException;

import org.xidobi.structs.DCB;

/**
 * @author Christian Schwarz
 * 
 */
public class SerialPortHandleImpl implements SerialPortHandle {

	private final OS os;

	/**
	 * Creates a new Handle using the native win32-API provided by the {@link OS}.
	 * 
	 * @param os
	 *            must not be <code>null</code>
	 */
	public SerialPortHandleImpl(OS os) {
		
		this.os = os;

	}

	public SerialPort open(String portName, SerialPortSettings settings) throws IOException {
		int handle = os.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

		if (handle == -1)
			throw new IOException("Unable to open " + portName + " :" + os.GetLastError());

		DCB dcb = new DCB();
		os.GetCommState(handle, dcb);
		dcb.BaudRate = 9600;
		os.SetCommState(handle, dcb);

		return new SerialPortImpl(os, handle);
	}

}

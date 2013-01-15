/*
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xidobi;

import static org.xidobi.OS.FILE_FLAG_OVERLAPPED;
import static org.xidobi.OS.GENERIC_READ;
import static org.xidobi.OS.GENERIC_WRITE;
import static org.xidobi.OS.OPEN_EXISTING;
import static org.xidobi.internal.Preconditions.checkNotNull;

import java.io.IOException;

import org.xidobi.structs.DCB;

/**
 * 
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class SerialPortHandleImpl implements SerialPortHandle {

	/** the native Win32-API, never <code>null</code> */
	private final OS os;

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link OS}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public SerialPortHandleImpl(OS os) {
		this.os = checkNotNull(os, "os");

	}

	/**
	 * Opens a serial port with the given control settings and returns the connected serial port.
	 * 
	 * @param portName
	 *            the name of the port to be open, must not be <code>null</code>
	 * @param settings
	 *            the control settings for the port, must not be <code>null</code>
	 * @return a connected serial port, never <code>null</code>
	 * @throws IOException
	 *             <ul>
	 *             <li>if the port cannot be opened</li>
	 *             <li>if the current control settings cannot be retrieved</li>
	 *             <li>if the control settings cannot be set</li>
	 *             </ul>
	 */
	public SerialPort open(String portName, SerialPortSettings settings) throws IOException {
		checkNotNull(portName, "portName");
		checkNotNull(settings, "settings");

		int handle = os.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

		if (handle == -1)
			throw newIOExceptionWithLastErrorCode("Unable to open port >" + portName + "<!");

		DCB dcb = new DCB();

		boolean isGetCommStateSuccessful = os.GetCommState(handle, dcb);
		if (!isGetCommStateSuccessful)
			throw newIOExceptionWithLastErrorCode("Unable to retrieve the current control settings for port >" + portName + "<!");

		// dcb.BaudRate = 9600;

		boolean isSetCommStateSuccessful = os.SetCommState(handle, dcb);
		if (!isSetCommStateSuccessful)
			throw newIOExceptionWithLastErrorCode("Unable to set the control settings for port >" + portName + "<!");

		return new SerialPortImpl(os, handle);
	}

	/**
	 * Returns a new {@link IOException} containing the given message and the error code that is
	 * returned by {@link OS#GetLastError()}.
	 */
	private IOException newIOExceptionWithLastErrorCode(String message) {
		return new IOException(message + " (Error-Code: " + os.GetLastError() + ")");
	}

}

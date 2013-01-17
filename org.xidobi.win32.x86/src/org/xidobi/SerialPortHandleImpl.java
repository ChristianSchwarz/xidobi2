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
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.structs.DCB;

/**
 * Handle that opens serial ports.
 * 
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialPort
 */
public class SerialPortHandleImpl implements SerialPortHandle {

	/** the native Win32-API, never <code>null</code> */
	@Nonnull
	private final OS os;

	/** the name of this port, eg. "COM1" */
	@Nonnull
	private final String portName;

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link OS}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port
	 */
	public SerialPortHandleImpl(@Nonnull OS os,
								@Nonnull String portName) {
		this.portName = checkArgumentNotNull(portName, "portName");
		this.os = checkArgumentNotNull(os, "os");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPortHandle#open(java.lang.String, org.xidobi.SerialPortSettings)
	 */
	public SerialPort open(SerialPortSettings settings) throws IOException {
		checkArgumentNotNull(settings, "settings");

		int handle = os.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

		if (handle == OS.INVALID_HANDLE_VALUE)
			throw newIOExceptionWithLastErrorCode("Unable to open port >" + portName + "<!");

		DCB dcb = new DCB();

		boolean succeed = os.GetCommState(handle, dcb);
		if (!succeed) {
			os.CloseHandle(handle);
			throw newIOExceptionWithLastErrorCode("Unable to retrieve the current control settings for port >" + portName + "<!");
		}

		// dcb.BaudRate = 9600;

		boolean isSetCommStateSuccessful = os.SetCommState(handle, dcb);
		if (!isSetCommStateSuccessful) {
			os.CloseHandle(handle);
			throw newIOExceptionWithLastErrorCode("Unable to set the control settings for port >" + portName + "<!");
		}

		return new SerialPortImpl(this,os, handle);
	}

	/**
	 * Returns a new {@link IOException} containing the given message and the error code that is
	 * returned by {@link OS#GetLastError()}.
	 */
	private IOException newIOExceptionWithLastErrorCode(String message) {
		return new IOException(message + " (Error-Code: " + os.GetLastError() + ")");
	}

	@Nonnull
	public String getPortName() {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

}

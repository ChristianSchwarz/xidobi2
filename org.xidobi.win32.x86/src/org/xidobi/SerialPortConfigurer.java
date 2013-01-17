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

import static org.xidobi.OS.INVALID_HANDLE_VALUE;
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import org.xidobi.structs.DCB;

/**
 * Configures the serial port with the serial port settings.
 * 
 * @author Tobias Breﬂler
 * 
 * @see SerialPortSettings
 */
public class SerialPortConfigurer {

	/** the native Win32-API, never <code>null</code> */
	private final OS os;

	/**
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 */
	public SerialPortConfigurer(OS os) {
		this.os = checkArgumentNotNull(os, "os");
	}

	/**
	 * Configures the serial port to the given settings.
	 * 
	 * @param handle
	 *            the handle of the serial port, must not be {@link OS#INVALID_HANDLE_VALUE}
	 * @param settings
	 *            the settings for the serial port, must not be <code>null</code>
	 * @throws IOException
	 *             <ul>
	 *             <li>when the current control settings cannot be retrieved
	 *             <li>when the control settings cannot be set
	 *             </ul>
	 */
	public void setupSerialPort(int handle, SerialPortSettings settings) throws IOException {
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		checkArgumentNotNull(settings, "settings");

		DCB dcb = new DCB();

		boolean isGetCommStateSuccessful = os.GetCommState(handle, dcb);
		if (!isGetCommStateSuccessful)
			throw newIOExceptionWithErrorCode("Can't retrieve current control settings!");

		// dcb.BaudRate = 9600;

		boolean isSetCommStateSuccessful = os.SetCommState(handle, dcb);
		if (!isSetCommStateSuccessful)
			throw newIOExceptionWithErrorCode("Can't set control settings!");

	}

	/**
	 * Creates and returns an {@link IOException} with the given message and the last error code
	 * from the Win-API.
	 */
	private IOException newIOExceptionWithErrorCode(String message) {
		int errorCode = os.GetLastError();
		return new IOException(message + " (Error-Code: " + errorCode + ")");
	}
}

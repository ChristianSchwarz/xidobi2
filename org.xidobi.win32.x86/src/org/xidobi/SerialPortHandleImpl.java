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

import static org.xidobi.OS.ERROR_ACCESS_DENIED;
import static org.xidobi.OS.ERROR_FILE_NOT_FOUND;
import static org.xidobi.OS.FILE_FLAG_OVERLAPPED;
import static org.xidobi.OS.GENERIC_READ;
import static org.xidobi.OS.GENERIC_WRITE;
import static org.xidobi.OS.INVALID_HANDLE_VALUE;
import static org.xidobi.OS.OPEN_EXISTING;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.structs.DCB;

/**
 * {@link SerialPortHandle} to open a serial port.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialPortHandle
 */
public class SerialPortHandleImpl implements SerialPortHandle {

	/** the native Win32-API, never <code>null</code> */
	@Nonnull
	private final WinApi win;

	/** the name of this port, eg. "COM1", never <code>null</code> */
	@Nonnull
	private final String portName;

	/**
	 * configures the native DCB "struct" with the values from the serial port settings, never
	 * <code>null</code>
	 */
	@Nonnull
	private final DCBConfigurator configurator;

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link WinApi}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port, must not be <code>null</code>
	 */
	public SerialPortHandleImpl(@Nonnull WinApi os,
								@Nonnull String portName) {
		this(os, portName, new DCBConfigurator());
	}

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link WinApi}.
	 * 
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port, must not be <code>null</code>
	 * @param configurator
	 *            configures the native DCB "struct" with the values from the serial port settings,
	 *            must not be <code>null</code>
	 */
	public SerialPortHandleImpl(@Nonnull WinApi win,
								@Nonnull String portName,
								@Nonnull DCBConfigurator configurator) {
		this.portName = checkArgumentNotNull(portName, "portName");
		this.win = checkArgumentNotNull(win, "win");
		this.configurator = checkArgumentNotNull(configurator, "configurator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPortHandle#open(SerialPortSettings)
	 */
	public SerialPort open(SerialPortSettings settings) throws IOException {
		checkArgumentNotNull(settings, "settings");

		final int handle = tryOpen(portName);
		try {
			applySettings(handle, settings);
		}
		catch (IOException e) {
			win.CloseHandle(handle);
			throw e;
		}

		return new SerialPortImpl(this, win, handle);
	}

	/**
	 * Tries to open the port.
	 * 
	 * @return the HANDLE of the port on success
	 * @exception IOException
	 *                if the port is already open or does not exist
	 */
	private int tryOpen(final String portName) throws IOException {
		int handle = win.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

		if (handle != INVALID_HANDLE_VALUE)
			return handle;

		win.getPreservedError();
		int err = win.getPreservedError();

		System.err.println("invalid handle(" + handle + ")->" + err);
		switch (err) {
			case ERROR_ACCESS_DENIED:
				throw new IOException("Port in use (" + portName + ")!");
			case ERROR_FILE_NOT_FOUND:
				throw new IOException("Port not found (" + portName + ")!");
		}
		throw lastError("Unable to open port (" + portName + ")!", err);
	}

	/**
	 * Tries to apply the {@link SerialPortSettings} to the port.
	 * 
	 * @throws IOException
	 *             if it was not possible to apply the settings e.g. if they are invalid
	 */
	private void applySettings(final int handle, final SerialPortSettings settings) throws IOException {
		final DCB dcb = new DCB();

		if (!win.GetCommState(handle, dcb))
			throw lastError("Unable to retrieve the current control settings for port (" + portName + ")!", win.getPreservedError());

		configurator.configureDCB(dcb, settings);

		if (!win.SetCommState(handle, dcb))
			throw lastError("Unable to set the control settings (" + portName + ")!", win.getPreservedError());
	}

	/**
	 * Returns a new {@link IOException} containing the given message and the error code that is
	 * returned by {@link WinApi#GetLastError()}.
	 */
	private IOException lastError(String message, int errorCode) {
		return new IOException(message + " (Error-Code: " + errorCode + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPortHandle#getPortName()
	 */
	@Nonnull
	public String getPortName() {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

}

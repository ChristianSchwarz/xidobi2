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

import static org.xidobi.WinApi.ERROR_ACCESS_DENIED;
import static org.xidobi.WinApi.ERROR_FILE_NOT_FOUND;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.FILE_FLAG_OVERLAPPED;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.PURGE_RXCLEAR;
import static org.xidobi.WinApi.PURGE_TXCLEAR;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newIOException;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xidobi.spi.NativeCodeException;
import org.xidobi.structs.DCB;
import org.xidobi.utils.Throwables;

/**
 * {@link SerialPort} to open a serial port.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialPort
 */
public class SerialPortImpl implements SerialPort {

	/** the native Win32-API, never <code>null</code> */
	@Nonnull
	private final WinApi os;

	/** the name of this port, eg. "COM1", never <code>null</code> */
	@Nonnull
	private final String portName;

	/**
	 * configures the native DCB "struct" with the values from the serial port settings, never
	 * <code>null</code>
	 */
	@Nonnull
	private final DCBConfigurator configurator;

	/** The additional description for the serial port, maybe <code>null</code> */
	@Nullable
	private String description;

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link WinApi}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port, must not be <code>null</code>
	 * @param description
	 *            the additional description for the serial port, maybe <code>null</code>
	 */
	public SerialPortImpl(	@Nonnull WinApi os,
							@Nonnull String portName,
							@Nullable String description) {
		this(os, portName, description, new DCBConfigurator());
	}

	/**
	 * Creates a new handle using the native Win32-API provided by the {@link WinApi}.
	 * 
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param portName
	 *            the name of this port, must not be <code>null</code>
	 * @param description
	 *            the additional description for the serial port, maybe <code>null</code>
	 * @param configurator
	 *            configures the native DCB "struct" with the values from the serial port settings,
	 *            must not be <code>null</code>
	 */
	public SerialPortImpl(	@Nonnull WinApi os,
							@Nonnull String portName,
							@Nullable String description,
							@Nonnull DCBConfigurator configurator) {
		this.portName = checkArgumentNotNull(portName, "portName");
		this.os = checkArgumentNotNull(os, "os");
		this.configurator = checkArgumentNotNull(configurator, "configurator");
		this.description = description;
	}

	/** {@inheritDoc} */
	@Nonnull
	public SerialConnection open(@Nonnull SerialPortSettings settings) throws IOException {
		checkArgumentNotNull(settings, "settings");

		final int handle = tryOpen(portName);
		try {
			applySettings(handle, settings);
			clearIOBuffers(handle);
			registerRxEvent(handle);
		}
		catch (IOException e) {
			os.CloseHandle(handle);
			throw e;
		}
		catch (NativeCodeException e) {
			os.CloseHandle(handle);
			throw e;
		}

		return new SerialConnectionImpl(this, os, handle);
	}

	/**
	 * Tries to open the port and returns the handle of the port.
	 * 
	 * @return the handle of the port on success
	 * @throws IOException
	 *             if the port is already open or does not exist
	 */
	private int tryOpen(final String portName) throws IOException {
		int handle = os.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);

		if (handle != INVALID_HANDLE_VALUE)
			return handle;

		int err = os.GetLastError();

		switch (err) {
			case ERROR_ACCESS_DENIED:
				throw new IOException("Port in use (" + portName + ")!");
			case ERROR_FILE_NOT_FOUND:
				throw new IOException("Port not found (" + portName + ")!");
		}
		throw lastError("Unable to open port (" + portName + ")!");
	}

	/**
	 * Tries to apply the {@link SerialPortSettings} to the port.
	 * 
	 * @throws IOException
	 *             if it was not possible to apply the settings e.g. if they are invalid
	 */
	private void applySettings(final int handle, final SerialPortSettings settings) throws IOException {
		final DCB dcb = new DCB();

		if (!os.GetCommState(handle, dcb))
			throw lastError("Unable to retrieve the current control settings for port (" + portName + ")!");

		configurator.configureDCB(dcb, settings);

		if (!os.SetCommState(handle, dcb))
			throw lastError("Unable to set the control settings (" + portName + ")!");
	}

	/**
	 * Discards all characters from the output and input buffer of a specified communications
	 * resource.
	 * 
	 * @param handle
	 *            the handle of the port to clear
	 */
	private void clearIOBuffers(final int handle) {
		if (os.PurgeComm(handle, PURGE_RXCLEAR | PURGE_TXCLEAR))
			return;
		throw Throwables.newNativeCodeException(os, "PurgeComm failed!", os.GetLastError());
	}

	/**
	 * Set the event mask to the given handle in order to be notified about received bytes.
	 * 
	 * @param portHandle
	 */
	private void registerRxEvent(int portHandle) {
		if (os.SetCommMask(portHandle, EV_RXCHAR))
			return;

		throw newNativeCodeException(os, "SetCommMask failed!", os.GetLastError());
	}

	/**
	 * Returns a new {@link IOException} containing the given message and the error code that is
	 * returned by {@link WinApi#GetLastError()}.
	 */
	private IOException lastError(String message) {
		return newIOException(os, message, os.GetLastError());
	}

	/** {@inheritDoc} */
	@Nonnull
	public String getPortName() {
		return portName;
	}

	/** {@inheritDoc} */
	@Nullable
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "SerialPortImpl [portName=" + getPortName() + ", description=" + getDescription() + "]";
	}
}
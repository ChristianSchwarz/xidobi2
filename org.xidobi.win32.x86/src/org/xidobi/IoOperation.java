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

import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

/**
 * Abstract class for I/O operations.
 * 
 * @author Christian Schwarz
 */
public abstract class IoOperation implements Closeable {

	/** the serial port, never <code>null</code> */
	protected final SerialPort port;
	/** the native Win32-API, never <code>null</code> */
	protected final WinApi os;
	/** the native handle of the serial port */
	protected final int handle;

	/** Receives the number of bytes that are read or written. */
	protected final DWORD numberOfBytesTransferred;
	/** Overlapped */
	protected final OVERLAPPED overlapped;

	/**
	 * Creates a new I/O operation.
	 * 
	 * @param port
	 *            the serial port, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public IoOperation(	@Nonnull SerialPort port,
						@Nonnull WinApi os,
						int handle) {
		this.port = checkArgumentNotNull(port, "port");
		this.os = checkArgumentNotNull(os, "os");
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;

		// initialize shared resources:
		overlapped = newOverlapped(os);
		numberOfBytesTransferred = new DWORD(os);
	}

	/** Creates a new overlapped with an event object. */
	private OVERLAPPED newOverlapped(WinApi os) {
		OVERLAPPED overlapped = new OVERLAPPED(os);

		overlapped.hEvent = os.CreateEventA(0, true, false, null);
		if (overlapped.hEvent != 0)
			return overlapped;

		overlapped.dispose();
		throw newNativeCodeException(os, "Create overlapped event failed!", os.getPreservedError());
	}

	/** {@inheritDoc} */
	public void close() {
		//@formatter:off
		try {
			numberOfBytesTransferred.dispose();
		} finally { try {
			os.CloseHandle(overlapped.hEvent);
		} finally {
			overlapped.dispose();
		}}
		//@formatter:on
	}

	/**
	 * Returns a new {@link IOException} indicating that the port is closed. Subclasses may use this
	 * to throw a consitent {@link IOException}, if a closed port was detected.
	 * <p>
	 * <b>NOTE:</b> This method is also used by {@link #read()} and {@link #write(byte[])} to throw
	 * an {@link IOException} if the port is closed. Overriding it may have consequences to the
	 * caller.
	 * 
	 * @param message
	 *            error description may be <code>null</code>
	 */
	@Nonnull
	protected IOException portClosedException(@Nullable String message) {
		if (message == null)
			message = "";
		else
			message = " " + message;
		return new IOException("Port " + port.getPortName() + " is closed!" + message);
	}

}
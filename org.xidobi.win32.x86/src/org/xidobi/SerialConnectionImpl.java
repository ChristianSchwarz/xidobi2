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
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.internal.AbstractSerialConnection;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

/**
 * {@link SerialConnection} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialConnection
 */
public class SerialConnectionImpl extends AbstractSerialConnection {

	/** The maximum size of the buffer that receives the read bytes */
	private static int MAX_BUFFER_SIZE = 1024;

	/** the native Win32-API, never <code>null</code> */
	private final WinApi os;
	/** The HANDLE of the opened port */
	private final int handle;

	/** Write timeout in milliseconds */
	private int writeTimeout = 2000;
	/** Read timeout in milliseconds */
	private int readTimeout = 2000;

	/**
	 * Creates a new serial port.
	 * 
	 * @param portHandle
	 *            a serial port handle, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public SerialConnectionImpl(@Nonnull SerialPort portHandle,
								@Nonnull WinApi os,
								int handle) {
		super(portHandle);
		this.os = checkArgumentNotNull(os, "os");
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
	}

	@Override
	protected void writeInternal(byte[] data) throws IOException {

		DWORD numberOfBytesWritten = new DWORD(os);
		OVERLAPPED overlapped = new OVERLAPPED(os);

		try {
			// create event object
			overlapped.hEvent = os.CreateEventA(0, true, false, null);
			if (overlapped.hEvent == 0)
				throw newNativeCodeException(os, "CreateEventA illegally returned 0!", os.getPreservedError());

			// write data to serial port
			boolean writeFileResult = os.WriteFile(handle, data, data.length, numberOfBytesWritten, overlapped);

			if (writeFileResult)
				// the write operation succeeded immediatly
				return;

			// TODO the I/O operation is pending
		}
		finally {
			disposeAndCloseSafe(numberOfBytesWritten, overlapped);
		}
	}

	/** Disposes the given resources. */
	private void disposeAndCloseSafe(DWORD dword, OVERLAPPED overlapped) {
		try {
			if (overlapped.hEvent != 0)
				os.CloseHandle(overlapped.hEvent);
		}
		finally {
			try {
				overlapped.dispose();
			}
			finally {
				dword.dispose();
			}
		}
	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {
		return null;
	}

	@Override
	protected void closeInternal() throws IOException {
		boolean success = os.CloseHandle(handle);
		if (!success)
			throw newNativeCodeException(os, "CloseHandle failed unexpected!", os.getPreservedError());
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
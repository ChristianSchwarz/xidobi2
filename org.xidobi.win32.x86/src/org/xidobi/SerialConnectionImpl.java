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

import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.internal.AbstractSerialConnection;
import org.xidobi.internal.NativeCodeException;
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

		DWORD numberOfBytesTransferred = new DWORD(os);
		OVERLAPPED overlapped = new OVERLAPPED(os);

		try {
			// create event object
			overlapped.hEvent = os.CreateEventA(0, true, false, null);
			if (overlapped.hEvent == 0)
				throw newNativeCodeException(os, "CreateEventA illegally returned 0!", os.getPreservedError());

			// write data to serial port
			boolean writeFileResult = os.WriteFile(handle, data, data.length, numberOfBytesTransferred, overlapped);

			if (writeFileResult)
				// the write operation succeeded immediatly
				return;

			int lastError = os.getPreservedError();

			if (lastError == ERROR_INVALID_HANDLE)
				throw portClosedException("Write operation failed, because the handle is invalid!");
			if (lastError != ERROR_IO_PENDING)
				throw newNativeCodeException(os, "WriteFile failed unexpected!", lastError);

			// wait for pending I/O operation to complete
			int waitResult = os.WaitForSingleObject(overlapped.hEvent, writeTimeout);
			switch (waitResult) {
				case WAIT_OBJECT_0:
					// I/O operation has finished
					boolean overlappedResult = os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true);
					if (!overlappedResult)
						throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());

					// verify that the number of transferred bytes is equal to the data length that
					// was written:
					int bytesWritten = numberOfBytesTransferred.getValue();
					if (bytesWritten != data.length)
						throw new NativeCodeException("GetOverlappedResult returned an unexpected number of transferred bytes! Transferred: " + bytesWritten + ", expected: " + data.length);
					return;
				case WAIT_TIMEOUT:
					// I/O operation timed out

					// TODO Maybe we should purge the serial port here, so all outstanding data will
					// be cleared?

					throw new IOException("Write operation timed out after " + writeTimeout + " milliseconds!");
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
				case WAIT_FAILED:
					throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.getPreservedError());
			}
			throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.getPreservedError());
		}
		finally {
			disposeAndCloseSafe(numberOfBytesTransferred, overlapped);
		}
	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {
		return null;
	}

	/** Disposes the given resources. */
	private void disposeAndCloseSafe(DWORD numberOfBytes, OVERLAPPED overlapped) {
		try {
			if (overlapped.hEvent != 0)
				os.CloseHandle(overlapped.hEvent);
		}
		finally {
			try {
				overlapped.dispose();
			}
			finally {
				numberOfBytes.dispose();
			}
		}
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
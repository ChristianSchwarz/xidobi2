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
package io.xidobi.win32;

import static com.sun.jna.platform.win32.WinError.*;
import static com.sun.jna.platform.win32.WinBase.*;
import static com.sun.jna.platform.win32.Kernel32.*;
import static io.xidobi.win32.Throwables.newNativeCodeException;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.SerialPort;
import org.xidobi.spi.NativeCodeException;
import org.xidobi.spi.Writer;

import com.sun.jna.platform.win32.Kernel32;

/**
 * Implementation for write operations.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class WriterImpl extends IoOperationImpl implements Writer {

	/** Write timeout in milliseconds */
	private int writeTimeout = 2000;

	/**
	 * Creates a new write operation.
	 * 
	 * @param port
	 *            the serial port, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public WriterImpl(	@Nonnull SerialPort port,
						@Nonnull Kernel32 os,
						HANDLE handle) {
		super(port, os, handle);
	}

	/** {@inheritDoc} */
	public void write(@Nonnull byte[] data) throws IOException {
		disposeLock.lock();
		try {
			checkIfClosedOrDisposed();

			resetOverlappedEventHandle();

			// write data to serial port
			boolean succeed = os.WriteFile(handle, data, data.length, numberOfBytesTransferred, overlapped);

			if (succeed) {
				// the write operation succeeded immediately
				if (numberOfBytesTransferred.getValue() != data.length)
					throw new NativeCodeException("WriteFile returned an unexpected number of transferred bytes! Transferred: " + numberOfBytesTransferred.getValue() + ", expected: " + data.length);
				return;
			}

			int lastError = os.GetLastError();
			if (lastError != ERROR_IO_PENDING)
				handleNativeError("WriteFile", lastError);

			// wait for pending I/O operation to complete
			int waitResult = os.WaitForSingleObject(overlapped.hEvent, writeTimeout);
			switch (waitResult) {
				case WAIT_OBJECT_0: // IO operation has finished
					if (!os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true))
						handleNativeError("GetOverlappedResult", os.GetLastError());

					// verify that the number of transferred bytes is equal to the data length that
					// was written:
					if (numberOfBytesTransferred.getValue() != data.length)
						throw new NativeCodeException("GetOverlappedResult returned an unexpected number of transferred bytes! Transferred: " + numberOfBytesTransferred.getValue() + ", expected: " + data.length);
					return;
				case WAIT_TIMEOUT:
					// I/O operation has timed out
					throw new IOException("Write operation timed out after " + writeTimeout + " milliseconds!");
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
				case WAIT_FAILED:
					handleNativeError("WaitForSingleObject", os.GetLastError());
				default:
					throw newNativeCodeException( "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.GetLastError());
			}
		}
		finally {
			disposeLock.unlock();
		}
	}

}

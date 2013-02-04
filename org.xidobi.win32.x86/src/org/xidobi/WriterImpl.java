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
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.spi.NativeCodeException;
import org.xidobi.spi.Writer;

/**
 * Implementation for write operations.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class WriterImpl extends IoOperation implements Writer {

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
						@Nonnull WinApi os,
						int handle) {
		super(port, os, handle);
	}

	public void write(@Nonnull byte[] data) throws IOException {
		// we dont need this if we create the event handle with manualReset=false
		os.ResetEvent(overlapped.hEvent);

		// write data to serial port
		boolean succeed = os.WriteFile(handle, data, data.length, numberOfBytesTransferred, overlapped);

		if (succeed)// the write operation succeeded immediatly
			return;

		int lastError = os.getPreservedError();
		if (lastError == ERROR_INVALID_HANDLE)
			throw portClosedException("Write operation failed, because the handle is invalid!");
		if (lastError != ERROR_IO_PENDING)
			throw newNativeCodeException(os, "WriteFile failed unexpected!", lastError);

		// wait for pending I/O operation to complete
		int waitResult = os.WaitForSingleObject(overlapped.hEvent, writeTimeout);
		switch (waitResult) {
			case WAIT_OBJECT_0: // IO operation has finished
				if (!os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true))
					throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());

				// verify that the number of transferred bytes is equal to the data length that
				// was written:
				if (numberOfBytesTransferred.getValue() != data.length)
					throw new NativeCodeException("GetOverlappedResult returned an unexpected number of transferred bytes! Transferred: " + numberOfBytesTransferred.getValue() + ", expected: " + data.length);
				return;
			case WAIT_TIMEOUT:// IO operation has timed out

				// TODO Maybe we should purge the serial port here, so all outstanding data will
				// be cleared?

				throw new IOException("Write operation timed out after " + writeTimeout + " milliseconds!");
			case WAIT_ABANDONED:
				throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
			case WAIT_FAILED:
				throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.getPreservedError());
			default:
				throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.getPreservedError());
		}

	}
}

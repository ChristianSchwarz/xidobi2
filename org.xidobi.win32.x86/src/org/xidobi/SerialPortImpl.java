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

import org.xidobi.internal.AbstractSerialPort;
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

/**
 * {@link SerialPort} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 */
public class SerialPortImpl extends AbstractSerialPort {

	/** the native Win32-API, never <code>null</code> */
	private final WinApi win;
	/** The HANDLE of the opened port */
	private final int handle;
	/**milliseconds */
	private int writeTimeout= 2000;

	/**
	 * @param portHandle
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the handle of the serial port
	 */
	public SerialPortImpl(	SerialPortHandle portHandle,
							WinApi win,
							int handle) {
		super(portHandle);
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
		this.win = checkArgumentNotNull(win, "win");
	}

	@Override
	protected void writeInternal(byte[] data) throws IOException {
		final int eventHandle = win.CreateEventA(0, true, false, null);
		if (eventHandle == 0)
			throw newNativeCodeException(win, "CreateEventA returned unexpected with 0!", win.getPreservedError());

		OVERLAPPED overlapped=null;
		try {
			overlapped= new OVERLAPPED(win);
			overlapped.hEvent = eventHandle;

			INT lpNumberOfBytesWritten = new INT();
			boolean succeed = win.WriteFile(handle, data, data.length, lpNumberOfBytesWritten, overlapped);
			if (succeed)
				// The write operation finished immediatly
				return;

			int lastError = win.getPreservedError();
			//check if an error occured or the operation is pendig
			if (lastError != ERROR_IO_PENDING)
				throw newNativeCodeException(win, "WriteFile failed unexpected!", lastError);

			// the operation is pending, lets wait for completion
			int eventResult = win.WaitForSingleObject(eventHandle, writeTimeout);

			switch (eventResult) {
				case WAIT_OBJECT_0:
					INT lpNumberOfBytesTransferred = new INT();
					succeed = win.GetOverlappedResult(handle, overlapped, lpNumberOfBytesTransferred, true);
					lastError = win.getPreservedError();
					if (!succeed)
						throw newNativeCodeException(win, "GetOverlappedResult failed unexpected!", lastError);
					if (lpNumberOfBytesTransferred.value!=data.length)
						throw new NativeCodeException("GetOverlappedResult returned an unexpected number of bytes transferred! Transferred: "+lpNumberOfBytesTransferred.value+" expected: "+data.length);
					break;
				case WAIT_TIMEOUT:
					throw new IOException("Write timeout after "+writeTimeout+" ms!");

				case WAIT_FAILED:
					throw newNativeCodeException(win,"WaitForSingleObject returned an unexpected value: WAIT_FAILED!",win.getPreservedError());
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
			}
		}
		finally {
			try{
			overlapped.dispose();
			}finally{
				win.CloseHandle(eventHandle);
			}
		}

	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	protected void closeInternal() throws IOException {
		win.CloseHandle(handle);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Write Test!!!
		super.finalize();
		close();
	}
}

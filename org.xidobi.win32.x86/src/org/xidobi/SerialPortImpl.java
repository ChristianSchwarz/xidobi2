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

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.internal.AbstractSerialPort;
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

/**
 * {@link SerialPort} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 */
public class SerialPortImpl extends AbstractSerialPort {

	/** the native Win32-API, never <code>null</code> */
	private final WinApi os;
	/** The HANDLE of the opened port */
	private final int handle;

	/**
	 * @param portHandle
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the handle of the serial port
	 */
	public SerialPortImpl(	SerialPortHandle portHandle,
							WinApi os,
							int handle) {
		super(portHandle);
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
		this.os = checkArgumentNotNull(os, "os");
	}

	@Override
	protected void writeInternal(byte[] data) throws IOException {
		int eventHandle = os.CreateEventA(0, true, false, null);
		if (eventHandle == 0)
			throw new NativeCodeException("CreateEventA returned unexpected with 0! Error Code: " + os.getLastNativeError());

		OVERLAPPED overlapped = new OVERLAPPED(os);
		try {
			overlapped.hEvent = eventHandle;

			INT lpNumberOfBytesWritten = new INT();
			boolean succeed = os.WriteFile(handle, data, data.length, lpNumberOfBytesWritten, overlapped);

			int eventResult = os.WaitForSingleObject(eventHandle, 2000);

			switch (eventResult) {
				case WAIT_OBJECT_0:
					INT lpNumberOfBytesTransferred = new INT();
					succeed = os.GetOverlappedResult(handle, overlapped, lpNumberOfBytesTransferred, true);
					break;
				case WinApi.WAIT_FAILED:
				case WinApi.WAIT_ABANDONED:
				case WinApi.WAIT_TIMEOUT:
					//TODO
					break;
			}
		}
		finally {
			overlapped.dispose();
		}

	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	protected void closeInternal() throws IOException {
		os.CloseHandle(handle);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Write Test!!!
		super.finalize();
		close();
	}
}

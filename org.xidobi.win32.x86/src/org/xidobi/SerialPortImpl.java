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
import static org.xidobi.OS.WAIT_OBJECT_0;
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.INT;
import org.xidobi.structs.OVERLAPPED;

/**
 * {@link SerialPort} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 */
public class SerialPortImpl implements SerialPort {

	/** the native Win32-API, never <code>null</code> */
	private final OS os;
	/** The HANDLE of the opened port */
	private final int handle;
	/** the write buffer {@code 2048 byte}*/
	private final byte[] writeBuffer;

	/**
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the handle of the serial port
	 */
	public SerialPortImpl(	OS os,
							int handle) {
		checkArgument(handle!=INVALID_HANDLE_VALUE, "handle","Invalid handle value (-1)!");
		this.handle = handle;
		this.os = checkArgumentNotNull(os, "os");
		writeBuffer = new byte[2048];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPort#write(byte[])
	 */
	public void write(byte[] data) throws IOException {
		int eventHandle = os.CreateEventA(0, true, false, null);
		if (eventHandle==0)
			throw new NativeCodeException("CreateEventA returned unexpected with 0! Error Code:"+os.GetLastError());

		OVERLAPPED overlapped = new OVERLAPPED(os);
		overlapped.hEvent = eventHandle;
		
		INT lpNumberOfBytesWritten = new INT();
		boolean succeed = os.WriteFile(handle, writeBuffer, 9, lpNumberOfBytesWritten, overlapped);
		

		int eventResult = os.WaitForSingleObject(eventHandle, 2000);
		

		if (eventResult == WAIT_OBJECT_0) {
			INT lpNumberOfBytesTransferred = new INT();
			succeed = os.GetOverlappedResult(handle, overlapped, lpNumberOfBytesTransferred, true);
			
		}
		else {
			System.err.println("Wait: " + eventResult);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPort#read()
	 */
	public byte[] read() throws IOException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {}
}

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

import static org.xidobi.utils.Throwables.newNativeCodeException;

import javax.annotation.Nonnull;

import org.xidobi.spi.BasicSerialConnection;

/**
 * Implementation of interface {@link SerialConnection} for Windows (32-bit) on x86 platforms.
 * 
 * @author Tobias Breﬂler
 * 
 * @see SerialConnection
 */
public class SerialConnectionImpl extends BasicSerialConnection {

	/** the native Win32-API */
	private WinApi os;
	/** the native handle of the serial port */
	private int handle;

	/**
	 * @param port
	 *            the serial port, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public SerialConnectionImpl(@Nonnull SerialPort port,
								@Nonnull WinApi os,
								int handle) {
		super(port, new ReaderImpl(port, os, handle), new WriterImpl(port, os, handle));

		this.os = os;
		this.handle = handle;
	}

	@Override
	protected void closeInternal() {
		try {
			// FIXME terminate all pending read or write operations. PurgeComm blocks in some cases,
			// so we can't use it!
			// boolean purgeCommResult = os.PurgeComm(handle, WinApi.PURGE_TXABORT |
			// WinApi.PURGE_RXABORT | WinApi.PURGE_TXCLEAR | WinApi.PURGE_RXCLEAR);
			// if (!purgeCommResult)
			// throw newNativeCodeException(os, "PurgeComm failed unexpected!", os.GetLastError());
		}
		finally {
			// close the handle of the serial port.
			boolean closeHandleResult = os.CloseHandle(handle);
			if (!closeHandleResult)
				throw newNativeCodeException(os, "CloseHandle failed unexpected!", os.GetLastError());
		}
	}

}

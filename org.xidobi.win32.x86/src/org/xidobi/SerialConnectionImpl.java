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

import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.PURGE_RXABORT;
import static org.xidobi.WinApi.PURGE_RXCLEAR;
import static org.xidobi.WinApi.PURGE_TXABORT;
import static org.xidobi.WinApi.PURGE_TXCLEAR;
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
		//@formatter:off
		try { try { 
			cancelIO();
		} finally {	try {
			purgeComm();
		} finally {
			releaseWaitCommEvent();
		}}} finally {
			closePortHandle();
		}
		// @formatter:on
	}

	/**
	 * 
	 */
	private void cancelIO() {
		boolean cancelIoResult = os.CancelIo(handle);
		if (!cancelIoResult) {
			System.out.print("CancelIo failed! " + os.GetLastError());
			throw newNativeCodeException(os, "CancelIo failed unexpected!", os.GetLastError());
		}
	}

	/**
	 * Discards all characters from the output or input buffer of a specified communications
	 * resource and terminates pending read or write operations.
	 */
	private void purgeComm() {
		boolean purgeCommResult = os.PurgeComm(handle, PURGE_RXABORT | PURGE_TXABORT | PURGE_TXCLEAR | PURGE_RXCLEAR);
		if (!purgeCommResult)
			throw newNativeCodeException(os, "PurgeComm failed unexpected!", os.GetLastError());
	}

	/**
	 * <b>IMPORTANT:</b> Releases the <code>WaitCommEvent</code> function. This is necessary,
	 * because the asynchronous <code>WaitCommEvent</code>, doesn't return immediatly on
	 * <code>WAIT_FAILED</code>. It can cause a memory access violation error, because the resources
	 * are disposed too early.
	 */
	private void releaseWaitCommEvent() {
		boolean setCommMaskResult = os.SetCommMask(handle, EV_RXCHAR);
		if (!setCommMaskResult)
			throw newNativeCodeException(os, "SetCommMask failed unexpected!", os.GetLastError());
	}

	/** Closes the handle of the serial port. */
	private void closePortHandle() {
		// close the handle of the serial port.
		boolean closeHandleResult = os.CloseHandle(handle);
		if (!closeHandleResult)
			throw newNativeCodeException(os, "CloseHandle failed unexpected!", os.GetLastError());
	}
}

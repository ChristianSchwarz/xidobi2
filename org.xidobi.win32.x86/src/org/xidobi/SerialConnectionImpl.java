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

import static org.xidobi.WinApi.ERROR_ACCESS_DENIED;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.OPEN_EXISTING;
import static org.xidobi.WinApi.PURGE_RXABORT;
import static org.xidobi.WinApi.PURGE_RXCLEAR;
import static org.xidobi.WinApi.PURGE_TXABORT;
import static org.xidobi.WinApi.PURGE_TXCLEAR;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import javax.annotation.Nonnull;

import org.xidobi.spi.BasicSerialConnection;

/**
 * Implementation of the interface {@link SerialConnection} for Windows (32-bit) on x86 platforms.
 * 
 * @author Tobias Breﬂler
 * 
 * @see SerialConnection
 * @see BasicSerialConnection
 */
public class SerialConnectionImpl extends BasicSerialConnection {

	/**
	 * Specifies how often the port should be re-open in order to determine if the port is actualy
	 * closed.
	 */
	private static final int TERMINATION_POLL_INTERVAL = 200;

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
		}}} finally { try {
			closePortHandle(handle);
		} finally {
			awaitCloseTermination();
		}}
		// @formatter:on
	}

	/** Cancels all pending I/O operations. */
	private void cancelIO() {
		boolean cancelIoResult = os.CancelIo(handle);
		if (!cancelIoResult)
			throw newNativeCodeException(os, "CancelIo failed unexpected!", os.GetLastError());
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
	private void closePortHandle(int handle) {
		boolean closeHandleResult = os.CloseHandle(handle);
		if (!closeHandleResult)
			throw newNativeCodeException(os, "CloseHandle failed unexpected!", os.GetLastError());
	}

	/** Awaits the termination of all pending I/O operations. */
	private void awaitCloseTermination() {

		String portName = getPort().getPortName();

		// IMPORTANT: We need this workaround, because when the close operation returns, the pending
		// I/O operations in the background are not canceled immediatly. We must wait until all I/O
		// operations are finished. Only then we can dispose all allocated resources in the next
		// step. The only way to find out that all pending I/O operations are finished and the
		// port is really closed, is to re-open the port. If it is successfull, all pending
		// operations should be terminated.

		while (true) {
			int handle = os.CreateFile("\\\\.\\" + portName, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0);
			if (handle != INVALID_HANDLE_VALUE) {
				// port was closed successful
				closePortHandle(handle);
				return;
			}
			int lastError = os.GetLastError();
			if (lastError == ERROR_ACCESS_DENIED) {
				// the port is currently busy, we must wait and poll again
				sleepUninterruptibly(TERMINATION_POLL_INTERVAL);
				continue;
			}
		}
	}

	/** Invokes <code>Thread.sleep(int)</code> uninterruptibly. */
	private void sleepUninterruptibly(int duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException e) {
			// TODO Do we really wan't to ignore this exception?
		}
	}
}

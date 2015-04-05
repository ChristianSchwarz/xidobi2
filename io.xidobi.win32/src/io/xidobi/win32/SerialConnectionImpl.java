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

import static com.sun.jna.platform.win32.WinBase.EV_RXCHAR;
import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;
import static com.sun.jna.platform.win32.WinBase.PURGE_RXABORT;
import static com.sun.jna.platform.win32.WinBase.PURGE_RXCLEAR;
import static com.sun.jna.platform.win32.WinBase.PURGE_TXABORT;
import static com.sun.jna.platform.win32.WinBase.PURGE_TXCLEAR;
import static com.sun.jna.platform.win32.WinError.ERROR_ACCESS_DENIED;
import static com.sun.jna.platform.win32.WinError.ERROR_BAD_COMMAND;
import static com.sun.jna.platform.win32.WinError.ERROR_FILE_NOT_FOUND;
import static com.sun.jna.platform.win32.WinError.ERROR_GEN_FAILURE;
import static com.sun.jna.platform.win32.WinError.ERROR_INVALID_HANDLE;
import static com.sun.jna.platform.win32.WinError.ERROR_NOT_READY;
import static com.sun.jna.platform.win32.WinError.ERROR_OPERATION_ABORTED;
import static io.xidobi.win32.Throwables.newNativeCodeException;
import static java.lang.Thread.sleep;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.annotation.Nonnull;

import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.spi.BasicSerialConnection;
import org.xidobi.spi.NativeCodeException;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;

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
	 * Specifies how often the port should be re-open in order to determine if the port is actualy closed.
	 */
	private static final int TERMINATION_POLL_INTERVAL = 200;

	/** the native Win32-API */
	private final Kernel32 os;
	/** the native handle of the serial port */
	private final HANDLE handle;

	/**
	 * @param port
	 *            the serial port, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public SerialConnectionImpl(@Nonnull SerialPort port, @Nonnull Kernel32 os, HANDLE handle) {
		super(port, new ReaderImpl(port.getPortName(), os, handle), new WriterImpl(port.getPortName(), os, handle));

		this.os = os;
		this.handle = handle;
	}

	@Override
	protected void closeInternal() throws IOException {
		// @formatter:off
		try {
			cancelIO();
		} finally {
			try {
				purgeComm();
			} finally {
				try {
					releaseWaitCommEvent();
				} finally {
					try {
						closePortHandle(handle);
					} finally {
						awaitCloseTermination();
					}
				}
			}
		}
		// @formatter:on
	}

	/** Cancels all pending I/O operations. */
	private void cancelIO() {
		boolean cancelIoResult = os.CancelIo(handle);
		if (!cancelIoResult)
			handleNativeError("CancelIo", os.GetLastError());
	}

	/**
	 * Discards all characters from the output or input buffer of a specified communications resource and terminates
	 * pending read or write operations.
	 */
	private void purgeComm() {
		boolean purgeCommResult = os.PurgeComm(handle, PURGE_RXABORT | PURGE_TXABORT | PURGE_TXCLEAR | PURGE_RXCLEAR);
		if (!purgeCommResult)
			handleNativeError("PurgeComm", os.GetLastError());
	}

	/**
	 * <b>IMPORTANT:</b> Releases the <code>WaitCommEvent</code> function. This is necessary, because the asynchronous
	 * <code>WaitCommEvent</code>, doesn't return immediatly on <code>WAIT_FAILED</code>. It can cause a memory access
	 * violation error, because the resources are disposed too early.
	 */
	private void releaseWaitCommEvent() {
		boolean setCommMaskResult = os.SetCommMask(handle, EV_RXCHAR);
		if (!setCommMaskResult)
			handleNativeError("SetCommMask", os.GetLastError());
	}

	/** Closes the handle of the serial port. */
	private void closePortHandle(HANDLE handle) {
		boolean closeHandleResult = os.CloseHandle(handle);
		if (!closeHandleResult)
			throw newNativeCodeException("CloseHandle failed unexpected!", os.GetLastError());
	}

	/** Awaits the termination of all pending I/O operations. */
	private void awaitCloseTermination() throws IOException {

		String portName = getPort().getPortName();

		// IMPORTANT: We need this workaround, because when the close operation returns, the pending
		// I/O operations in the background are not canceled immediatly. We must wait until all I/O
		// operations are finished. Only then we can dispose all allocated resources in the next
		// step. The only way to find out that all pending I/O operations are finished and the
		// port is really closed, is to re-open the port. If it is successfull, all pending
		// operations should be terminated.

		while (true) {
			HANDLE handle = os.CreateFile("\\\\.\\" + portName, Kernel32.GENERIC_READ | Kernel32.GENERIC_WRITE, 0, null, Kernel32.OPEN_EXISTING, 0, null);
			if (handle != INVALID_HANDLE_VALUE) {
				// port was closed successful
				closePortHandle(handle);
				return;
			}
			int lastError = os.GetLastError();
			switch (lastError) {
			case ERROR_ACCESS_DENIED:
				// the port is currently busy, we must wait and poll again
				sleepUninterruptibly(TERMINATION_POLL_INTERVAL);
				continue;
			case ERROR_FILE_NOT_FOUND:
				// the port couldn't been found, maybe the hardware was removed
				return;
			default:
				throw newNativeCodeException("Couldn't wait for close termination! CreateFileA failed unexpected!", lastError);
			}
		}
	}

	/** Invokes <code>Thread.sleep(int)</code> uninterruptibly. */
	private void sleepUninterruptibly(int duration) throws IOException {
		try {
			sleep(duration);
		} catch (InterruptedException e) {
			throw new InterruptedIOException(e.getMessage());
		}
	}

	/**
	 * Handles the native error.
	 * <p>
	 * This method throws a {@link NativeCodeException}, if the given error code is none of the following:
	 * <ul>
	 * <li>{@link WinApi#ERROR_INVALID_HANDLE ERROR_INVALID_HANDLE}
	 * <li>{@link WinApi#ERROR_OPERATION_ABORTED ERROR_OPERATION_ABORTED}
	 * <li>{@link WinApi#ERROR_ACCESS_DENIED ERROR_ACCESS_DENIED}
	 * <li>{@link WinApi#ERROR_NOT_READY ERROR_NOT_READY}
	 * <li>{@link WinApi#ERROR_BAD_COMMAND ERROR_BAD_COMMAND}
	 * <li>{@link WinApi#ERROR_GEN_FAILURE ERROR_GEN_FAILURE}
	 * </ul>
	 * 
	 * @param nativeMethodName
	 *            the name of the native method, must not be <code>null</code>
	 * @param errorCode
	 *            the last error code
	 * @exception NativeCodeException
	 *                for all unexpected error codes
	 */
	protected final void handleNativeError(@Nonnull String nativeMethodName, int errorCode) {
		checkArgumentNotNull(nativeMethodName, "nativeMethodName");
		switch (errorCode) {
		case ERROR_INVALID_HANDLE:
		case ERROR_OPERATION_ABORTED:
		case ERROR_GEN_FAILURE:
		case ERROR_NOT_READY:
		case ERROR_BAD_COMMAND:
		case ERROR_ACCESS_DENIED:
			return;
		default:
			throw newNativeCodeException(nativeMethodName + " failed unexpected!", errorCode);
		}
	}
}

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
import static io.xidobi.win32.Throwables.newNativeCodeException;
import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.xidobi.SerialPort;
import org.xidobi.spi.IoOperation;
import org.xidobi.spi.NativeCodeException;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.OVERLAPPED;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Abstract class for I/O operations.
 * <p>
 * Subclasses are:
 * 
 * <ul>
 * <li>{@link ReaderImpl} for read operations</li>
 * <li>{@link WriterImpl} for write operations</li>
 * </ul>
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see IoOperation
 * @see WriterImpl
 * @see ReaderImpl
 */
public abstract class IoOperationImpl implements IoOperation {

	/** the serial port, never <code>null</code> */
	@Nonnull
	protected final SerialPort port;
	/** the native Win32-API, never <code>null</code> */
	@Nonnull
	protected final Kernel32 os;
	/** the native handle of the serial port */
	protected final HANDLE handle;

	/** Receives the number of bytes that are read or written. */
	protected final IntByReference numberOfBytesTransferred;
	/** Overlapped */
	protected final OVERLAPPED overlapped;

	/**
	 * Ensures that the shared resources can only be disposed, when no read or
	 * write operations are in progress.
	 */
	protected final Lock disposeLock = new ReentrantLock(true);

	/**
	 * <ul>
	 * <li> <code>true</code> if this instance is disposed
	 * <li> <code>false</code> if this instance is not disposed
	 * </ul>
	 */
	private boolean isDisposed;

	/**
	 * <ul>
	 * <li> <code>true</code> if this I/O operation is closed
	 * <li> <code>false</code> if this I/O operation is not closed
	 * </ul>
	 */
	private boolean isClosed;

	/**
	 * Creates a new I/O operation.
	 * 
	 * @param port
	 *            the serial port, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public IoOperationImpl(@Nonnull SerialPort port, @Nonnull Kernel32 os, HANDLE handle) {
		this.port = checkArgumentNotNull(port, "port");
		this.os = checkArgumentNotNull(os, "os");
		checkArgument(handle != Kernel32.INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;

		// initialize shared resources:
		overlapped = newOverlapped(os);
		numberOfBytesTransferred = new IntByReference();
	}

	/** Creates a new overlapped with an event object. */
	private OVERLAPPED newOverlapped(Kernel32 os) {
		OVERLAPPED overlapped = new OVERLAPPED();

		// Create an event for the overlapped struct
		overlapped.hEvent = os.CreateEvent(null, true, false, null);
		if (overlapped.hEvent != null)
			return overlapped;

		throw newNativeCodeException("Create overlapped event failed!", os.GetLastError());
	}

	/** Resets the overlapped event handle */
	protected final void resetOverlappedEventHandle() throws IOException {
		boolean resetEventResult = os.ResetEvent(overlapped.hEvent);
		if (!resetEventResult)
			handleNativeError("ResetEvent", os.GetLastError());
	}

	/**
	 * Handles the native error and throws an {@link IOException}, if the given
	 * error code is one of the following:
	 * <ul>
	 * <li>{@link WinApi#ERROR_INVALID_HANDLE ERROR_INVALID_HANDLE}
	 * <li>{@link WinApi#ERROR_OPERATION_ABORTED ERROR_OPERATION_ABORTED}
	 * <li>{@link WinApi#ERROR_ACCESS_DENIED ERROR_ACCESS_DENIED}
	 * <li>{@link WinApi#ERROR_GEN_FAILURE ERROR_GEN_FAILURE}
	 * <li>{@link WinApi#ERROR_BAD_COMMAND ERROR_BAD_COMMAND}
	 * </ul>
	 * Otherwise it throws a {@link NativeCodeException} with the error code and
	 * the given name of the native method.
	 * 
	 * @param nativeMethodName
	 *            the name of the native method, must not be <code>null</code>
	 * @param errorCode
	 *            the last error code
	 * @throws IOException
	 *             if the error code is one of the following:
	 *             <ul>
	 *             <li>{@link WinApi#ERROR_INVALID_HANDLE ERROR_INVALID_HANDLE}
	 *             <li>
	 *             {@link WinApi#ERROR_OPERATION_ABORTED
	 *             ERROR_OPERATION_ABORTED}
	 *             <li>
	 *             {@link WinApi#ERROR_ACCESS_DENIED ERROR_ACCESS_DENIED}
	 *             <li>
	 *             {@link WinApi#ERROR_GEN_FAILURE ERROR_GEN_FAILURE}
	 *             <li>
	 *             {@link WinApi#ERROR_BAD_COMMAND ERROR_BAD_COMMAND}
	 *             <li>
	 *             {@link WinApi#ERROR_NOT_READY ERROR_NOT_READY}
	 *             </ul>
	 * @exception NativeCodeException
	 *                for all unexpected error codes
	 */
	protected final void handleNativeError(@Nonnull String nativeMethodName, int errorCode) throws IOException {
		checkArgumentNotNull(nativeMethodName, "nativeMethodName");

		switch (errorCode) {
		case ERROR_INVALID_HANDLE:
			throw portClosedException("I/O operation failed, because the handle is invalid.");
		case ERROR_ACCESS_DENIED:
			throw portClosedException("I/O operation failed, because access denied.");
		case ERROR_OPERATION_ABORTED:
			throw portClosedException("I/O operation has been aborted.");
		case ERROR_GEN_FAILURE:
			throw portClosedException("I/O operation failed, because a device attached to the system is not functioning.");
		case ERROR_BAD_COMMAND:
			throw portClosedException("I/O operation failed, because the device doesn't recognize the command.");
		case ERROR_NOT_READY:
			throw portClosedException("I/O operation failed, because the device is not ready.");
		default:
			throw newNativeCodeException(nativeMethodName + " failed unexpected!", errorCode);
		}
	}

	/** {@inheritDoc} */
	@OverridingMethodsMustInvokeSuper
	public void close() throws IOException {
		checkIfClosedOrDisposed();

		// Close handle for overlapped event
		boolean closeHandleResult = os.CloseHandle(overlapped.hEvent);
		if (!closeHandleResult)
			throw newNativeCodeException("CloseHandle failed unexpected!", os.GetLastError());

		isClosed = true;
	}

	/**
	 * Returns <code>true</code>, if this I/O operation is closed.
	 * 
	 * @return <ul>
	 *         <li> <code>true</code> if closed
	 *         <li> <code>false</code> if not closed
	 *         </ul>
	 */
	protected final boolean isClosed() {
		return isClosed;
	}

	/**
	 * Throws an {@link IOException} if this I/O operation was already closed or
	 * an {@link IllegalStateException} if this instance was already disposed.
	 * 
	 * @throws IOException
	 *             when this I/O operation was already closed
	 * @throws IllegalStateException
	 *             when this instance was already disposed
	 */
	protected final void checkIfClosedOrDisposed() throws IOException {
		if (isClosed())
			throw portClosedException(null);
		checkIfDisposed();
	}

	/**
	 * Returns a new {@link IOException} indicating that the port is closed.
	 * Subclasses may use this to throw a consitent {@link IOException}, if a
	 * closed port was detected.
	 * <p>
	 * <b>NOTE:</b> This method is also used by {@link #read()} and
	 * {@link #write(byte[])} to throw an {@link IOException} if the port is
	 * closed. Overriding it may have consequences to the caller.
	 * 
	 * @param message
	 *            error description, may be <code>null</code>
	 */
	@Nonnull
	protected final IOException portClosedException(@Nullable String message) {
		if (message == null)
			message = "";
		else
			message = " " + message;
		return new IOException("Port " + port.getPortName() + " was closed!" + message);
	}

	/** {@inheritDoc} */
	public final void dispose() {

		disposeLock.lock();
		try {
			checkIfDisposed();
		} finally {
			isDisposed = true;
			disposeLock.unlock();
		}

	}



	/**
	 * Returns <code>true</code> if this instance is disposed.
	 * 
	 * @return <ul>
	 *         <li> <code>true</code> if this instance is disposed
	 *         <li> <code>false</code> if this instance is not disposed
	 *         </ul>
	 */
	protected final boolean isDisposed() {
		return isDisposed;
	}

	/**
	 * Throws an {@link IllegalStateException} if this instance was already
	 * disposed.
	 * 
	 * @throws IllegalStateException
	 *             when this instance was already disposed
	 */
	protected final void checkIfDisposed() {
		if (isDisposed())
			throw new IllegalStateException("The instance of " + getClass().getName() + " was already disposed!");
	}

}
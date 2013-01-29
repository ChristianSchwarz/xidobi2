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

import org.xidobi.internal.AbstractSerialConnection;
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

import static java.lang.Integer.toHexString;
import static org.xidobi.SerialConnectionImpl.IOState.FINISHED;
import static org.xidobi.SerialConnectionImpl.IOState.PENDING;
import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.ERROR_OPERATION_ABORTED;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;
import static org.xidobi.internal.Preconditions.checkArgument;
import static org.xidobi.internal.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.getErrorMessage;
import static org.xidobi.utils.Throwables.newIOException;
import static org.xidobi.utils.Throwables.newNativeCodeException;

/**
 * {@link SerialConnection} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialConnection
 */
public class SerialConnectionImpl extends AbstractSerialConnection {

	/** the native Win32-API, never <code>null</code> */
	private final WinApi win;
	/** The HANDLE of the opened port */
	private final int handle;

	/** Write timeout in milliseconds */
	private int writeTimeout = 2000;
	/** Read timeout in milliseconds */
	private int readTimeout = 2000;
	/** The size of the buffer that receives the read bytes */
	private int bufferSize = 255;

	/**
	 * Creates a new serial port.
	 * 
	 * @param portHandle
	 *            a serial port handle, must not be <code>null</code>
	 * @param win
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public SerialConnectionImpl(@Nonnull SerialPort portHandle,
								@Nonnull WinApi win,
								int handle) {
		super(portHandle);
		this.win = checkArgumentNotNull(win, "win");
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
	}

	@Override
	protected void writeInternal(byte[] data) throws IOException {
		final int eventHandle = createEventHandle();

		OVERLAPPED overlapped = null;
		try {
			overlapped = createOverlapped(eventHandle);

			IOState state = write(overlapped, data);
			if (state == FINISHED)
				return;

			// the operation is pending, lets wait for completion
			int eventResult = await(eventHandle, writeTimeout);

			switch (eventResult) {
				case WAIT_OBJECT_0:
					processWriteResult(overlapped, data.length);
					return;
				case WAIT_TIMEOUT:
					throw new IOException("Write timeout after " + writeTimeout + " ms!");
				case WAIT_FAILED:
					throw waitFailedException();
				case WAIT_ABANDONED:
					throw waitAbdonnedException();
				default:
					throw unexpectedWaitResult(eventResult);
			}
		}
		finally {
			dispose(eventHandle, overlapped);
		}

	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {

		// loop until we have read some data or an exception occurs
		int eventHandle = createEventHandle();
		OVERLAPPED overlapped = null;
		// TODO Optimize: The buffer just needs to be initialized once.
		NativeByteArray readBuffer = null;
		try {
			overlapped = createOverlapped(eventHandle);
			readBuffer = new NativeByteArray(win, bufferSize);
			while (true) {

				byte[] result = read(overlapped, readBuffer);
				if (result != null)
					return result;

				// the operation is pending, lets wait for completion
				int waitResult = await(eventHandle, 50);

				switch (waitResult) {
					case WAIT_OBJECT_0:
						return processReadResult(overlapped, readBuffer);
					case WAIT_TIMEOUT:
						continue;// no bytes were received in the specified time, lets retry
					case WAIT_FAILED:
						throw waitFailedException();
					case WAIT_ABANDONED:
						throw waitAbdonnedException();
					default:
						throw unexpectedWaitResult(waitResult);
				}
			}
		}
		finally {
			try {
				if (readBuffer != null)
					readBuffer.dispose();
			}
			finally {
				dispose(eventHandle, overlapped);
			}
		}
	}

	/**
	 * @param eventHandle
	 * @return
	 */
	private OVERLAPPED createOverlapped(int eventHandle) {
		OVERLAPPED overlapped = new OVERLAPPED(win);
		overlapped.hEvent = eventHandle;
		return overlapped;
	}

	@Override
	protected void closeInternal() throws IOException {
		boolean success = win.CloseHandle(handle);
		if (!success)
			throw newNativeCodeException(win, "CloseHandle failed unexpected!", win.getPreservedError());
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Write Test!!!
		super.finalize();
		close();
	}

	private NativeCodeException waitFailedException() {
		return newNativeCodeException(win, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", win.getPreservedError());
	}

	private NativeCodeException waitAbdonnedException() {
		return new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
	}

	private int createEventHandle() {
		final int eventHandle = win.CreateEventA(0, true, false, null);
		if (eventHandle == 0)
			throw newNativeCodeException(win, "CreateEventA returned unexpected with 0!", win.getPreservedError());
		return eventHandle;
	}

	private NativeCodeException unexpectedWaitResult(int eventResult) {
		return newNativeCodeException(win, "WaitForSingleObject returned an unexpected value: 0x" + toHexString(eventResult), win.getPreservedError());
	}

	private int await(final int eventHandle, int timeout) {
		return win.WaitForSingleObject(eventHandle, timeout);
	}

	/**
	 * @return <ul>
	 *         <li> <code>true</code>, if the operation is completed
	 *         <li> <code>false</code>, if the operation is pending
	 *         </ul>
	 */
	private IOState write(OVERLAPPED overlapped, byte[] data) throws IOException {
		DWORD lpNumberOfBytesWritten = new DWORD(win);
		boolean succeed = win.WriteFile(handle, data, data.length, lpNumberOfBytesWritten, overlapped);
		if (succeed)
			// the write operation finished immediatly
			return FINISHED;

		int lastError = win.getPreservedError();
		// check if an error occured or the operation is pendig ...
		if (lastError == ERROR_IO_PENDING)
			return PENDING;

		if (lastError == ERROR_INVALID_HANDLE)
			throw newIOException(win, "Write operation failed, because the handle is invalid! Maybe the serial port was closed before.", lastError);

		throw newNativeCodeException(win, "WriteFile failed unexpected!", lastError);

	}

	private byte[] read(OVERLAPPED overlapped, NativeByteArray result) throws IOException {
		DWORD lpNumberOfBytesRead = new DWORD(win);
		boolean succeed = win.ReadFile(handle, result, result.size(), lpNumberOfBytesRead, overlapped);
		if (succeed) {
			// the read operation finished immediatly
			int bytesRead = lpNumberOfBytesRead.getValue();
			return result.getByteArray(bytesRead);
		}

		int lastError = win.getPreservedError();
		// check if an error occured or the operation is pendig ...
		if (lastError == ERROR_IO_PENDING)
			return null;
		if (lastError == ERROR_INVALID_HANDLE)
			throw newIOException(win, "Read operation failed, because the handle is invalid! Maybe the serial port was closed before.", lastError);

		throw newNativeCodeException(win, "ReadFile failed unexpected!", lastError);
	}

	/**
	 * @param overlapped
	 * @param data
	 *            the byte[] that was used as read buffer
	 * @return
	 * @throws IOException
	 */
	private byte[] processReadResult(OVERLAPPED overlapped, NativeByteArray data) throws IOException {
		int numberOfBytesRead = getNumberOfTransferredBytes(overlapped);
		return data.getByteArray(numberOfBytesRead);
	}

	/**
	 * @param overlapped
	 * @param data
	 * @throws IOException
	 */
	private void processWriteResult(OVERLAPPED overlapped, int expectedBytesWritten) throws IOException {
		int numberOfBytesTransferred = getNumberOfTransferredBytes(overlapped);

		if (numberOfBytesTransferred != expectedBytesWritten)
			throw new NativeCodeException("GetOverlappedResult returned an unexpected number of bytes transferred! Transferred: " + numberOfBytesTransferred + " expected: " + expectedBytesWritten);
	}

	/**
	 * @param overlapped
	 * @return
	 * @throws IOException
	 */
	private int getNumberOfTransferredBytes(OVERLAPPED overlapped) throws IOException {
		DWORD numberOfBytesRead = new DWORD(win);
		boolean succeed = win.GetOverlappedResult(handle, overlapped, numberOfBytesRead, true);
		if (succeed)
			return numberOfBytesRead.getValue();

		int lastError = win.getPreservedError();
		if (lastError == ERROR_OPERATION_ABORTED)
			throw portClosedException(getErrorMessage(win, lastError));

		throw newNativeCodeException(win, "GetOverlappedResult failed unexpected!", lastError);

	}

	/**
	 * Disposed/Closes the handle and the {@link OVERLAPPED}-struct.
	 */
	private void dispose(final int eventHandle, OVERLAPPED overlapped) {
		// We have to dispose all previously allocated resources:
		try {
			if (overlapped != null)
				overlapped.dispose();
		}
		finally {
			win.CloseHandle(eventHandle);
		}
	}

	static enum IOState {
		PENDING,
		FINISHED
	}
}

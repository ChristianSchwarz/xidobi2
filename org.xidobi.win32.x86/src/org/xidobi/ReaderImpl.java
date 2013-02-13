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
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;
import static org.xidobi.utils.Throwables.newNativeCodeException;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.spi.NativeCodeException;
import org.xidobi.spi.Reader;
import org.xidobi.structs.COMSTAT;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;

/**
 * Implementation for read operations.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class ReaderImpl extends IoOperationImpl implements Reader {

	/** Timeout for native <code>ReadFile</code> operation. */
	private static final int READ_FILE_TIMEOUT = 100;

	/** Read timeout in milliseconds */
	private int readTimeout = 100;

	/** Buffer for read data */
	private NativeByteArray readBuffer;

	private DWORD eventMask;

	/**
	 * Creates a new read operation.
	 * 
	 * @param port
	 *            the serial port, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public ReaderImpl(	SerialPort port,
						WinApi os,
						int handle) {
		super(port, os, handle);

		eventMask = new DWORD(os);
	}

	/** {@inheritDoc} */
	@Nonnull
	public byte[] read() throws IOException {
		disposeLock.lock();
		try {
			checkIfClosedOrDisposed();

			os.ResetEvent(overlapped.hEvent);

			// Repeat until data is available:
			while (true) {

				// wait for some data to arrive
				awaitArrivalOfData();

				// how many bytes are available for read?
				int availableBytes = getAvailableBytes();
				if (availableBytes == 0)
					// there is no data available for read
					continue;

				// now we can read the available data
				return readAvailableBytes(availableBytes);
			}
		}
		finally {
			disposeLock.unlock();
		}
	}

	/** Blocks until data arrives or an {@link IOException} is thrown. */
	private void awaitArrivalOfData() throws IOException {

		// reset eventMask
		eventMask.setValue(0);

		boolean succeed = os.WaitCommEvent(handle, eventMask, overlapped);
		if (succeed) {
			// event was signaled immediatly, the input buffer contains data
			checkEventMask(eventMask);
			return;
		}

		int lastError = os.GetLastError();
		if (lastError == ERROR_INVALID_HANDLE)
			throw portClosedException("Read operation failed, because the handle is invalid!");
		if (lastError != ERROR_IO_PENDING)
			throw newNativeCodeException(os, "WaitCommEvent failed unexpected!", os.GetLastError());

		// Repeat until some data arrived:
		while (true) {

			// wait for pending operation to complete
			int waitResult = os.WaitForSingleObject(overlapped.hEvent, readTimeout);

			switch (waitResult) {
				case WAIT_OBJECT_0:
					// wait finished successfull
					checkEventMask(eventMask);
					return;
				case WAIT_TIMEOUT:
					// operation has timed out
					continue;
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
				case WAIT_FAILED:
					lastError = os.GetLastError();
					if (lastError == ERROR_INVALID_HANDLE)
						throw portClosedException("Read operation failed, because the handle is invalid!");
					throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", lastError);
				default:
					throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.GetLastError());
			}
		}
	}

	/** Returns the number of bytes that are available to read. */
	private int getAvailableBytes() {
		COMSTAT lpStat = new COMSTAT();
		INT lpErrors = new INT(0);
		boolean succeed = os.ClearCommError(handle, lpErrors, lpStat);
		if (!succeed)
			throw newNativeCodeException(os, "ClearCommError failed unexpected!", os.GetLastError());
		return lpStat.cbInQue;
	}

	/** Reads and returns the data that is available in the read buffer. */
	private byte[] readAvailableBytes(int numberOfBytesToRead) throws IOException {

		// create a new read buffer
		newReadBuffer(numberOfBytesToRead);

		boolean readFileResult = os.ReadFile(handle, readBuffer, numberOfBytesToRead, numberOfBytesTransferred, overlapped);
		if (readFileResult)
			// the read operation succeeded immediatly
			return readBuffer.getByteArray();

		int lastError = os.GetLastError();
		if (lastError == ERROR_INVALID_HANDLE)
			throw portClosedException("Read operation failed, because the handle is invalid!");
		if (lastError != ERROR_IO_PENDING)
			throw newNativeCodeException(os, "ReadFile failed unexpected!", lastError);

		// wait for pending I/O operation to complete
		int waitResult = os.WaitForSingleObject(overlapped.hEvent, READ_FILE_TIMEOUT);
		switch (waitResult) {
			case WAIT_OBJECT_0:
				// I/O operation has finished
				boolean overlappedResult = os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true);
				if (!overlappedResult)
					throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.GetLastError());

				// verify that the number of read bytes is equal to the number of available
				// bytes:
				int bytesRead = numberOfBytesTransferred.getValue();
				if (bytesRead != numberOfBytesToRead)
					throw new NativeCodeException("GetOverlappedResult returned an unexpected number of read bytes! Read: " + bytesRead + ", expected: " + numberOfBytesToRead);
				return readBuffer.getByteArray();
			case WAIT_TIMEOUT:
				// ReadFile has timed out. This should not happen, because we determined that
				// data is available
				throw new NativeCodeException("ReadFile timed out after " + READ_FILE_TIMEOUT + " milliseconds!");
			case WAIT_ABANDONED:
				throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
			case WAIT_FAILED:
				lastError = os.GetLastError();
				if (lastError == ERROR_INVALID_HANDLE)
					throw portClosedException("Read operation failed, because the handle is invalid!");
				throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.GetLastError());
			default:
				throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.GetLastError());
		}
	}

	/** Disposes the current buffer and creates a new one. */
	private void newReadBuffer(int numberOfBytesToRead) {
		if (readBuffer != null)
			readBuffer.dispose();
		readBuffer = new NativeByteArray(os, numberOfBytesToRead);
	}

	/**
	 * Throws a {@link NativeCodeException}, when the <code>EV_RXCHAR</code> flag in the given
	 * <code>eventMask</code> is not set or an {@link IOException} when the <code>eventMask</code>
	 * is 0.
	 */
	private void checkEventMask(DWORD eventMask) throws IOException {
		int mask = eventMask.getValue();
		if (mask == 0)
			throw new IOException("Read operation failed, because a communication error event was signaled!");
		if ((mask & EV_RXCHAR) != EV_RXCHAR)
			throw new NativeCodeException("WaitCommEvt was signaled for unexpected event! Got: " + mask + ", expected: " + EV_RXCHAR);
	}

	@Override
	protected void disposeInternal() {
		//@formatter:off
		try {
			eventMask.dispose();
		} finally {
			if (readBuffer != null) {
				readBuffer.dispose();
			}
		}
		//@formatter:on
	}

	// -- FOR DEBUGGING ONLY: -----------------
	@Override
	public void dispose() {
		super.dispose();
	}
	// ----------------------------------------
}

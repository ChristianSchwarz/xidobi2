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

import org.xidobi.internal.AbstractSerialConnection;
import org.xidobi.internal.NativeCodeException;
import org.xidobi.structs.COMSTAT;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

/**
 * {@link SerialConnection} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see SerialConnection
 */
public class SerialConnectionImpl extends AbstractSerialConnection {

	/** A timeout for the ReadFile operation */
	private final static int READ_FILE_TIMEOUT = 1000;

	/** the native Win32-API, never <code>null</code> */
	private final WinApi os;
	/** The HANDLE of the opened port */
	private final int handle;

	/** Write timeout in milliseconds */
	private int writeTimeout = 2000;
	/** Read timeout in milliseconds */
	private int readTimeout = 2000;

	/**
	 * Creates a new serial port.
	 * 
	 * @param portHandle
	 *            a serial port handle, must not be <code>null</code>
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the native handle of the serial port
	 */
	public SerialConnectionImpl(@Nonnull SerialPort portHandle,
								@Nonnull WinApi os,
								int handle) {
		super(portHandle);
		this.os = checkArgumentNotNull(os, "os");
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
	}

	@Override
	protected void writeInternal(byte[] data) throws IOException {

		DWORD numberOfBytesTransferred = new DWORD(os);
		OVERLAPPED overlapped = new OVERLAPPED(os);

		try {
			// create event object
			overlapped.hEvent = os.CreateEventA(0, true, false, null);
			if (overlapped.hEvent == 0)
				throw newNativeCodeException(os, "CreateEventA illegally returned 0!", os.getPreservedError());

			// write data to serial port
			boolean writeFileResult = os.WriteFile(handle, data, data.length, numberOfBytesTransferred, overlapped);

			if (writeFileResult)
				// the write operation succeeded immediatly
				return;

			int lastError = os.getPreservedError();
			if (lastError == ERROR_INVALID_HANDLE)
				throw portClosedException("Write operation failed, because the handle is invalid!");
			if (lastError != ERROR_IO_PENDING)
				throw newNativeCodeException(os, "WriteFile failed unexpected!", lastError);

			// wait for pending I/O operation to complete
			int waitResult = os.WaitForSingleObject(overlapped.hEvent, writeTimeout);
			switch (waitResult) {
				case WAIT_OBJECT_0:
					// I/O operation has finished
					boolean overlappedResult = os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true);
					if (!overlappedResult)
						throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());

					// verify that the number of transferred bytes is equal to the data length that
					// was written:
					int bytesWritten = numberOfBytesTransferred.getValue();
					if (bytesWritten != data.length)
						throw new NativeCodeException("GetOverlappedResult returned an unexpected number of transferred bytes! Transferred: " + bytesWritten + ", expected: " + data.length);
					return;
				case WAIT_TIMEOUT:
					// I/O operation has timed out

					// TODO Maybe we should purge the serial port here, so all outstanding data will
					// be cleared?

					throw new IOException("Write operation timed out after " + writeTimeout + " milliseconds!");
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
				case WAIT_FAILED:
					throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.getPreservedError());
			}
			throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.getPreservedError());
		}
		finally {
			disposeAndCloseSafe(numberOfBytesTransferred, overlapped);
		}
	}

	@Override
	@Nonnull
	protected byte[] readInternal() throws IOException {

		DWORD numberOfBytesRead = new DWORD(os);
		OVERLAPPED overlapped = new OVERLAPPED(os);

		try {
			// create event object
			overlapped.hEvent = os.CreateEventA(0, true, false, null);
			if (overlapped.hEvent == 0)
				throw newNativeCodeException(os, "CreateEventA illegally returned 0!", os.getPreservedError());

			// wait for some data to arrive
			awaitArrivalOfData(overlapped);
			int availableBytes = getAvailableBytes();
			if (availableBytes == 0)
				throw new NativeCodeException("Arrival of data was signaled, but number of available bytes is 0!");

			// now we can read the available data
			return readAvailableBytes(availableBytes, numberOfBytesRead, overlapped);
		}
		finally {
			disposeAndCloseSafe(numberOfBytesRead, overlapped);
		}
	}

	/** Blocks until data arrives or an {@link IOException} is thrown. */
	private void awaitArrivalOfData(OVERLAPPED overlapped) throws IOException {

		DWORD eventMask = new DWORD(os);

		try {
			boolean waitCommEventResult = os.WaitCommEvent(handle, eventMask, overlapped);
			if (waitCommEventResult) {
				// event was signaled immediatly
				checkForRXCHARFlag(eventMask);
				return;
			}

			int lastError = os.getPreservedError();
			if (lastError == ERROR_INVALID_HANDLE)
				throw portClosedException("Read operation failed, because the handle is invalid!");
			if (lastError != ERROR_IO_PENDING)
				throw newNativeCodeException(os, "WaitCommEvent failed unexpected!", os.getPreservedError());

			// wait for pending operation to complete
			int waitResult = os.WaitForSingleObject(overlapped.hEvent, readTimeout);
			switch (waitResult) {
				case WAIT_OBJECT_0:
					// wait finished successfull
					boolean overlappedResult = os.GetOverlappedResult(handle, overlapped, eventMask, true);
					if (!overlappedResult)
						throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());
					checkForRXCHARFlag(eventMask);
					return;
				case WAIT_TIMEOUT:
					// operation has timed out

					// TODO What should happen, when a timeout occurs?

					break;
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
				case WAIT_FAILED:
					throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.getPreservedError());
			}
			throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.getPreservedError());
		}
		finally {
			try {
				os.ResetEvent(overlapped.hEvent);
			}
			finally {
				eventMask.dispose();
			}
		}
	}

	/** Returns the number of bytes that are available to read. */
	private int getAvailableBytes() {
		COMSTAT lpStat = new COMSTAT();
		boolean clearCommErrorResult = os.ClearCommError(handle, new INT(0), lpStat);
		if (!clearCommErrorResult)
			throw newNativeCodeException(os, "ClearCommError failed unexpected!", os.getPreservedError());
		return lpStat.cbInQue;
	}

	/** Reads and returns the data that is available in the read buffer. */
	private byte[] readAvailableBytes(int availableBytes, DWORD numberOfBytesRead, OVERLAPPED overlapped) throws IOException {

		NativeByteArray data = new NativeByteArray(os, availableBytes);

		try {

			boolean readFileResult = os.ReadFile(handle, data, availableBytes, numberOfBytesRead, overlapped);
			if (readFileResult)
				// the read operation succeeded immediatly
				return data.getByteArray();

			int lastError = os.getPreservedError();
			if (lastError == ERROR_INVALID_HANDLE)
				throw portClosedException("Read operation failed, because the handle is invalid!");
			if (lastError != ERROR_IO_PENDING)
				throw newNativeCodeException(os, "ReadFile failed unexpected!", lastError);

			// wait for pending I/O operation to complete
			int waitResult = os.WaitForSingleObject(overlapped.hEvent, READ_FILE_TIMEOUT);
			switch (waitResult) {
				case WAIT_OBJECT_0:
					// I/O operation has finished
					boolean overlappedResult = os.GetOverlappedResult(handle, overlapped, numberOfBytesRead, true);
					if (!overlappedResult)
						throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());

					// verify that the number of read bytes is equal to the number of available
					// bytes:
					int bytesRead = numberOfBytesRead.getValue();
					if (bytesRead != availableBytes)
						throw new NativeCodeException("GetOverlappedResult returned an unexpected number of read bytes! Read: " + bytesRead + ", expected: " + availableBytes);
					return data.getByteArray();
				case WAIT_TIMEOUT:
					// ReadFile has timed out. This should not happen, because we determined that
					// data is available
					throw new NativeCodeException("ReadFile timed out after " + READ_FILE_TIMEOUT + " milliseconds!");
				case WAIT_ABANDONED:
					throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
				case WAIT_FAILED:
					throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.getPreservedError());
			}
			throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.getPreservedError());
		}
		finally {
			data.dispose();
		}
	}

	/**
	 * Throws an {@link NativeCodeException} when the <code>EV_RXCHAR</code> flag in the given
	 * <code>eventMask</code> is not set.
	 */
	private void checkForRXCHARFlag(DWORD eventMask) {
		int mask = eventMask.getValue();
		if ((mask & EV_RXCHAR) != EV_RXCHAR)
			throw new NativeCodeException("WaitCommEvt was signaled for unexpected event! Got: " + mask + ", expected: " + EV_RXCHAR);
	}

	/** Disposes the given resources. */
	private void disposeAndCloseSafe(DWORD numberOfBytes, OVERLAPPED overlapped) {
		try {
			if (overlapped.hEvent != 0)
				os.CloseHandle(overlapped.hEvent);
		}
		finally {
			try {
				overlapped.dispose();
			}
			finally {
				numberOfBytes.dispose();
			}
		}
	}

	@Override
	protected void closeInternal() throws IOException {
		boolean success = os.CloseHandle(handle);
		if (!success)
			throw newNativeCodeException(os, "CloseHandle failed unexpected!", os.getPreservedError());
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

}
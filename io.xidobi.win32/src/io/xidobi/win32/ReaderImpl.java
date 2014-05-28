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

import static com.sun.jna.platform.win32.WinBase.WAIT_ABANDONED;
import static com.sun.jna.platform.win32.WinBase.WAIT_FAILED;
import static com.sun.jna.platform.win32.WinBase.WAIT_OBJECT_0;
import static com.sun.jna.platform.win32.WinError.ERROR_IO_PENDING;
import static com.sun.jna.platform.win32.WinError.WAIT_TIMEOUT;
import static io.xidobi.win32.Throwables.newNativeCodeException;
import static java.lang.Thread.interrupted;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import org.xidobi.SerialPort;
import org.xidobi.spi.NativeCodeException;
import org.xidobi.spi.Reader;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

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
	public ReaderImpl(SerialPort port, Kernel32 os, HANDLE handle) {
		super(port, os, handle);

		
	}

	/** {@inheritDoc} */
	@Nonnull
	public byte[] read() throws IOException {
		disposeLock.lock();
		try {
			checkIfClosedOrDisposed();

			resetOverlappedEventHandle();

			// Repeat until data is available:
			while (true) {

				// check if the current thread is interrupted
				if (interrupted())
					throw new InterruptedIOException("The thread for the read operation is interrupted!");

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
		} finally {
			disposeLock.unlock();
		}
	}

	/** Blocks until data arrives or an {@link IOException} is thrown. */
	private void awaitArrivalOfData() throws IOException {

		// reset eventMask
		DWORD eventMask = new DWORD();

		boolean succeed = os.WaitCommEvent(handle, eventMask, overlapped);
		if (succeed) {
			// event was signaled immediatly, the input buffer contains data
			checkEventMask(eventMask);
			return;
		}

		int lastError = os.GetLastError();
		if (lastError != ERROR_IO_PENDING)
			handleNativeError("WaitCommEvent", lastError);

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
				handleNativeError("WaitForSingleObject", os.GetLastError());
			default:
				throw newNativeCodeException("WaitForSingleObject returned unexpected value! Got: " + waitResult, os.GetLastError());
			}
		}
	}

	/** Returns the number of bytes that are available to read. */
	private int getAvailableBytes() throws IOException {
		WinBase.COMSTAT lpStat = new WinBase.COMSTAT();

		IntByReference lpErrors = new IntByReference();

		boolean succeed = os.ClearCommError(handle, lpErrors, lpStat);
		if (!succeed)
			handleNativeError("ClearCommError", os.GetLastError());
		return lpStat.cbInQue;
	}

	/** Reads and returns the data that is available in the read buffer. */
	private byte[] readAvailableBytes(int numberOfBytesToRead) throws IOException {

		// create a new read buffer
		ByteBuffer readBuffer = ByteBuffer.allocate(numberOfBytesToRead);

		boolean readFileResult = os.ReadFile(handle, readBuffer, numberOfBytesToRead, numberOfBytesTransferred, overlapped);
		if (readFileResult)
			// the read operation succeeded immediatly
			return readBuffer.array();

		int lastError = os.GetLastError();
		if (lastError != ERROR_IO_PENDING)
			handleNativeError("ReadFile", lastError);

		// wait for pending I/O operation to complete
		int waitResult = os.WaitForSingleObject(overlapped.hEvent, READ_FILE_TIMEOUT);
		switch (waitResult) {
		case WAIT_OBJECT_0:
			// I/O operation has finished
			return getOverlappedResult(numberOfBytesToRead, readBuffer);
		case WAIT_TIMEOUT:
			throw new NativeCodeException("ReadFile timed out after " + READ_FILE_TIMEOUT + " milliseconds! This should not happen, because we determined that data is available.");
		case WAIT_ABANDONED:
			throw new NativeCodeException("WaitForSingleObject returned the unexpected value: WAIT_ABANDONED!");
		case WAIT_FAILED:
			handleNativeError("WaitForSingleObject", os.GetLastError());
		default:
			throw newNativeCodeException("WaitForSingleObject returned unexpected value! Got: " + waitResult, os.GetLastError());
		}
	}

	private byte[] getOverlappedResult(int numberOfBytesToRead, ByteBuffer readBuffer) throws IOException {
		boolean overlappedResult = os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true);
		if (!overlappedResult)
			handleNativeError("GetOverlappedResult", os.GetLastError());

		// verify that the number of read bytes is equal to the number of
		// available
		// bytes:
		int bytesRead = numberOfBytesTransferred.getValue();
		if (bytesRead != numberOfBytesToRead)
			throw new NativeCodeException("GetOverlappedResult returned an unexpected number of read bytes! Read: " + bytesRead + ", expected: " + numberOfBytesToRead);
		byte[] array = readBuffer.array();
		return array;
	}

	/**
	 * Throws an {@link IOException}, when the <code>EV_RXCHAR</code> flag in
	 * the given <code>eventMask</code> is 0.
	 * <p>
	 * NOTICE: We have to ignore wrong event masks, because some serial port
	 * drivers are signaling events we haven't registered for.
	 */
	private void checkEventMask(DWORD eventMask) throws IOException {
		int mask = eventMask.intValue();
		if (mask == 0)
			throw portClosedException("Read operation failed, because a communication error event was signaled!");

	}

}

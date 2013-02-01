/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 01.02.2013 15:58:24
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.spi.NativeCodeException;
import org.xidobi.spi.Reader;
import org.xidobi.structs.COMSTAT;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.INT;
import org.xidobi.structs.NativeByteArray;
import org.xidobi.structs.OVERLAPPED;

import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.EV_RXCHAR;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;
import static org.xidobi.utils.Throwables.newNativeCodeException;

/**
 * @author Christian Schwarz
 * 
 */
public class ReaderImpl extends IoOperation implements Reader {

	private static final int READ_FILE_TIMEOUT = 0;
	
	private int readTimeout = 2000;

	/**
	 * @param port
	 * @param os
	 * @param handle
	 */
	public ReaderImpl(	SerialPort port,
						WinApi os,
						int handle) {
		super(port, os, handle);
	}

	@Nonnull
	public byte[] read() throws IOException {
			// wait for some data to arrive
			awaitArrivalOfData(overlapped);
			int availableBytes = getAvailableBytes();
			if (availableBytes == 0)
				throw new NativeCodeException("Arrival of data was signaled, but number of available bytes is 0!");

			// now we can read the available data
			return readAvailableBytes(availableBytes, numberOfBytesTransferred, overlapped);
	}

	/** Blocks until data arrives or an {@link IOException} is thrown. */
	private void awaitArrivalOfData(OVERLAPPED overlapped) throws IOException {

		try {
			final DWORD evtMask = new DWORD(os);
			try {
				boolean succeed = os.WaitCommEvent(handle, evtMask, overlapped);
				if (succeed) {
					// event was signaled immediatly, the input buffer contains data
					checkEventMask(evtMask);
					return;
				}
			}
			finally {
				evtMask.dispose();
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
					final DWORD bytesRead = new DWORD(os);
					try {
						if (!os.GetOverlappedResult(handle, overlapped, bytesRead, true))
							throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());
					}
					finally {
						bytesRead.dispose();
					}
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
			os.ResetEvent(overlapped.hEvent);
		}
	}

	/** Returns the number of bytes that are available to read. */
	private int getAvailableBytes() {
		COMSTAT lpStat = new COMSTAT();
		boolean succeed = os.ClearCommError(handle, new INT(0), lpStat);
		if (!succeed)
			throw newNativeCodeException(os, "ClearCommError failed unexpected!", os.getPreservedError());
		return lpStat.cbInQue;
	}

	/** Reads and returns the data that is available in the read buffer. */
	private byte[] readAvailableBytes(int numberOfBytesToRead, DWORD numberOfBytesRead, OVERLAPPED overlapped) throws IOException {

		NativeByteArray data = new NativeByteArray(os, numberOfBytesToRead);

		try {

			boolean readFileResult = os.ReadFile(handle, data, numberOfBytesToRead, numberOfBytesRead, overlapped);
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
					if (bytesRead != numberOfBytesToRead)
						throw new NativeCodeException("GetOverlappedResult returned an unexpected number of read bytes! Read: " + bytesRead + ", expected: " + numberOfBytesToRead);
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
	private void checkEventMask(DWORD eventMask) {
		int mask = eventMask.getValue();
		if ((mask & EV_RXCHAR) != EV_RXCHAR)
			throw new NativeCodeException("WaitCommEvt was signaled for unexpected event! Got: " + mask + ", expected: " + EV_RXCHAR);
	}
}

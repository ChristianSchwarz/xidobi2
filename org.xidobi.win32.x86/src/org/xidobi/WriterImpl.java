/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 01.02.2013 11:14:33
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xidobi.spi.NativeCodeException;
import org.xidobi.spi.Writer;
import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

import static org.xidobi.WinApi.ERROR_INVALID_HANDLE;
import static org.xidobi.WinApi.ERROR_IO_PENDING;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.WAIT_ABANDONED;
import static org.xidobi.WinApi.WAIT_FAILED;
import static org.xidobi.WinApi.WAIT_OBJECT_0;
import static org.xidobi.WinApi.WAIT_TIMEOUT;
import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newNativeCodeException;

/**
 * @author Christian Schwarz
 * 
 */
public class WriterImpl implements Writer {

	private final int handle;
	private final WinApi os;
	private int writeTimeout = 2000;
	private final DWORD numberOfBytesTransferred;
	private final SerialPort port;
	private final OVERLAPPED overlapped;

	/**
	 * 
	 * @param port
	 * @param os
	 * @param handle
	 */
	public WriterImpl(	@Nonnull SerialPort port,
						@Nonnull WinApi os,
						int handle) {
		this.port = checkArgumentNotNull(port,"port");
		this.os = checkArgumentNotNull(os, "os");
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
		numberOfBytesTransferred = new DWORD(os);
		overlapped = newOverlapped(os);
	}

	/**
	 * @param os
	 * @return
	 */
	private OVERLAPPED newOverlapped(WinApi os) {
		OVERLAPPED overlapped = new OVERLAPPED(os);

		overlapped.hEvent = os.CreateEventA(0, true, false, null);
		if (overlapped.hEvent != 0)
			return overlapped;

		overlapped.dispose();
		throw newNativeCodeException(os, "CreateEventA illegally returned 0!", os.getPreservedError());
	}

	public void write(@Nonnull byte[] data) throws IOException {
		// we dont need this if we create the event handle with manualReset=false
		os.ResetEvent(overlapped.hEvent);

		// write data to serial port
		boolean succeed = os.WriteFile(handle, data, data.length, numberOfBytesTransferred, overlapped);

		if (succeed)// the write operation succeeded immediatly
			return;

		int lastError = os.getPreservedError();
		if (lastError == ERROR_INVALID_HANDLE)
			throw portClosedException("Write operation failed, because the handle is invalid!");
		if (lastError != ERROR_IO_PENDING)
			throw newNativeCodeException(os, "WriteFile failed unexpected!", lastError);

		// wait for pending I/O operation to complete
		int waitResult = os.WaitForSingleObject(overlapped.hEvent, writeTimeout);
		switch (waitResult) {
			case WAIT_OBJECT_0: // IO operation has finished
				if (!os.GetOverlappedResult(handle, overlapped, numberOfBytesTransferred, true))
					throw newNativeCodeException(os, "GetOverlappedResult failed unexpected!", os.getPreservedError());

				// verify that the number of transferred bytes is equal to the data length that
				// was written:
				if (numberOfBytesTransferred.getValue() != data.length)
					throw new NativeCodeException("GetOverlappedResult returned an unexpected number of transferred bytes! Transferred: " + numberOfBytesTransferred.getValue() + ", expected: " + data.length);
				return;
			case WAIT_TIMEOUT:// IO operation has timed out

				// TODO Maybe we should purge the serial port here, so all outstanding data will
				// be cleared?

				throw new IOException("Write operation timed out after " + writeTimeout + " milliseconds!");
			case WAIT_ABANDONED:
				throw new NativeCodeException("WaitForSingleObject returned an unexpected value: WAIT_ABANDONED!");
			case WAIT_FAILED:
				throw newNativeCodeException(os, "WaitForSingleObject returned an unexpected value: WAIT_FAILED!", os.getPreservedError());
			default:
				throw newNativeCodeException(os, "WaitForSingleObject returned unexpected value! Got: " + waitResult, os.getPreservedError());
		}

	}

	public void close() {
		//@formatter:off
		try {
			numberOfBytesTransferred.dispose();
		}finally{ try{
			os.CloseHandle(overlapped.hEvent);
		}finally{
			overlapped.dispose();
		}}
		//@formatter:on
	}

	/**
	 * Returns a new {@link IOException} indicating that the port is closed. Subclasses may use this
	 * to throw a consitent {@link IOException}, if a closed port was detected.
	 * <p>
	 * <b>NOTE:</b> This method is also used by {@link #read()} and {@link #write(byte[])} to throw
	 * an {@link IOException} if the port is closed. Overriding it may have consequences to the
	 * caller.
	 * 
	 * @param message
	 *            error description may be <code>null</code>
	 */
	@Nonnull
	protected IOException portClosedException(@Nullable String message) {
		if (message == null)
			message = "";
		else
			message = " " + message;
		return new IOException("Port " + port.getPortName() + " is closed!" + message);
	}
}

/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 01.02.2013 15:52:13
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi;

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.xidobi.structs.DWORD;
import org.xidobi.structs.OVERLAPPED;

import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.spi.Preconditions.checkArgument;
import static org.xidobi.spi.Preconditions.checkArgumentNotNull;
import static org.xidobi.utils.Throwables.newNativeCodeException;

/**
 * @author Christian Schwarz
 *
 */
public class IoOperation implements Closeable{

	protected final int handle;
	protected final WinApi os;
	protected final DWORD numberOfBytesTransferred;
	protected final SerialPort port;
	protected final OVERLAPPED overlapped;


	/**
	 * 
	 * @param port
	 * @param os
	 * @param handle
	 */
	public IoOperation(	@Nonnull SerialPort port,
						@Nonnull WinApi os,
						int handle) {
		this.port = checkArgumentNotNull(port,"port");
		this.os = checkArgumentNotNull(os, "os");
		checkArgument(handle != INVALID_HANDLE_VALUE, "handle", "Invalid handle value (-1)!");
		this.handle = handle;
		overlapped = newOverlapped(os);
		numberOfBytesTransferred = new DWORD(os);
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

	/** {@inheritDoc} */
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
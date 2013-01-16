/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.01.2013 16:54:34
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.internal;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandle;

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

/**
 * A basic implementation of the {@link SerialPort} to provide synchonisation and proper behaviour
 * when the port is closed.
 * 
 * @author Christian Schwarz
 * 
 */
public abstract class AbstractSerialPort implements SerialPort {

	@Nonnull
	private final SerialPortHandle portHandle;

	/**
	 * Creates a new instance with the {@link SerialPortHandle}.
	 * 
	 * @param portHandle
	 *            must not be <code>null</code>
	 * @exception IllegalArgumentException
	 *                if {@code portHandle==null}
	 */
	protected AbstractSerialPort(@Nonnull SerialPortHandle portHandle) {
		this.portHandle = portHandle;
		checkArgumentNotNull(portHandle, "portHandle");
	}

	/**
	 * <ul>
	 * <li> <code>true</code>, if {@link #close()} was called
	 * <li> <code>false</code>, if this port is open
	 * </ul>
	 */
	private volatile boolean isClosed;

	public final void write(byte[] data) throws IOException {
		ensurePortIsOpen();
	}

	public byte[] read() throws IOException {
		return null;
	}

	public final void close() throws IOException {
		if (isClosed)
			return;

		closeInternal();
		isClosed = true;
	}

	/**
	 * The implementation must release all native resources.
	 * <p>
	 * It is guaranteed that this method will only be called on the first call of {@link #close()},
	 * if this method returns normal (without exception).
	 */
	protected abstract void closeInternal() throws IOException;

	/**
	 * Throw an {@link IOException} if this port is closed.
	 * 
	 * @throws IOException
	 *             if this port is closed
	 */
	private void ensurePortIsOpen() throws IOException {
		if (isClosed)
			throw new IOException("Port " + portHandle.getPortName() + " is closed!");
	}
}

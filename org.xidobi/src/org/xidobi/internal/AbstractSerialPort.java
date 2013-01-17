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

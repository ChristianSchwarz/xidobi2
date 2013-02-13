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

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Repesents a connected Serial-Port. Clients must call {@link #close()} to free this serial port
 * after usage!
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public interface SerialConnection extends Closeable {

	/**
	 * Writes the given <code>byte[]</code>. All bytes of the array were written.
	 * 
	 * @param data
	 *            must not be <code>null</code>
	 * @throws IOException
	 *             if this port was closed or an unexpected I/O error occurs.
	 */
	void write(@Nonnull byte[] data) throws IOException;

	/**
	 * Reads from this Serialport and returns the read byte's or throws an {@link IOException} when
	 * the port was closed or an other I/O error occurs. This method blocks until at least one byte
	 * can be returned or an {@link IOException} is thrown.
	 * 
	 * @return the received <code>byte[]</code>, never <code>null</code>
	 * @throws IOException
	 *             if this port was closed or an unexpected I/O error occurs.
	 */
	@Nonnull
	byte[] read() throws IOException;

	/**
	 * Returns <code>true</code>, if the serial port is closed.
	 * 
	 * @return <ul>
	 *         <li> <code>true</code> if the serial port is closed
	 *         <li> <code>false</code> if the serial port is open
	 *         </ul>
	 */
	boolean isClosed();

}
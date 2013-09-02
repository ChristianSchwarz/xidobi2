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
package org.xidobi.spi;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * This interface is implemented by service provider to read from a serial port. The Lifecyle of a
 * {@link Reader} is controlled by the {@link BasicSerialConnection} it is attached to.
 * 
 * @author Christian Schwarz
 * 
 * @see BasicSerialConnection
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface Reader extends IoOperation {

	/**
	 * The implementation must block until at least one byte can be returned or an
	 * {@link IOException} is thrown.
	 * <p>
	 * This method will be called by {@link BasicSerialConnection#read()} only if the port is open.
	 * <p>
	 * <b>IMPORTANT:</b> Dont call this method yourself! Otherwise there is no guaratee that the
	 * port is currently open!
	 * 
	 * @return the byte's read from the port, never <code>null</code>
	 * @throws IOException
	 *             if this port was closed or an unexpected I/O error occurs or the thread was
	 *             interrupted
	 */
	@Nonnull
	byte[] read() throws IOException;

}
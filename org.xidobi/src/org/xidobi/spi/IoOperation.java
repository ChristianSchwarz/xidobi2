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

/**
 * Interface for I/O operations, which are used by {@link BasicSerialConnection}.
 * <p>
 * NOTE: This interface is not intendet to be implemented or extended by clients. It is primary used
 * aggreagte common methods used by both sub-interfaces {@link Reader} and {@link Writer}.
 * {@link Writer}.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 * 
 * @see BasicSerialConnection
 * @see Reader
 * @see Writer
 * 
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface IoOperation {

	/**
	 * This method is the first called in the "close"-sequence of
	 * {@link BasicSerialConnection#close()}. It is guaranteed that it will be called only once when
	 * {@link BasicSerialConnection#close()} is invoked.
	 * <p>
	 * 
	 * The implementation normally closes native handles, that where aquired by this I/O operation.
	 * <p>
	 * <b>IMPORTANT:</b> Dont call this method yourself! Otherwise there is no guaratee that the
	 * port is currently open!
	 * @throws IOException if an I/O-Operation failed
	 * 
	 */
	void performActionBeforeConnectionClosed() throws IOException;

	/**
	 * The implementation must dispose all resources that was allocated by this I/O operation.
	 * <p>
	 * This method will be called by {@link BasicSerialConnection#close()} if the port is not
	 * closed.
	 * <p>
	 * <b>IMPORTANT:</b> Dont call this method yourself! Otherwise there is no guaratee that the
	 * port is currently open!
	 */
	void performActionAfterConnectionClosed();

}

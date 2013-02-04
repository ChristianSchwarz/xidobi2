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

import java.io.Closeable;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * This interface is implemented by service provider to read from a serial port.
 * 
 * @author Christian Schwarz
 * 
 * @see BasicSerialConnection
 */
public interface Reader extends Closeable {

	/**
	 * Reads from this Serialport and returns the read byte's or throws an {@link IOException} when
	 * the port was closed or an other I/O error occurs. This method blocks until at least one byte
	 * can be returned or an {@link IOException} is thrown.
	 * 
	 * @return the received byte[], never <code>null</code>
	 * @throws IOException
	 *             if this port was closed or an unexpected I/O error occurs.
	 */
	@Nonnull
	byte[] read() throws IOException;

}
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
 * This interface is implemented by service provider to write to a serial port.
 * 
 * @author Christian Schwarz
 * 
 * @see BasicSerialConnection
 */
public interface Writer extends IoOperation {

	/**
	 * The implementation must write the given {@code byte[]} to the port.
	 * <p>
	 * This method will be called by {@link BasicSerialConnection#write(byte[])}, if following
	 * conditions apply:
	 * <ul>
	 * <li>the port is open
	 * <li>{@code data != null}.
	 * </ul>
	 * <b>IMPORTANT:</b> Dont call this method yourself! Otherwise there is no guaratee that the
	 * port is currently open and data is not <code>null</code>!
	 * 
	 * @param data
	 *            never <code>null</code>
	 * @throws IOException
	 *             when the write operation timed out or the serial port is not open
	 */
	void write(@Nonnull byte[] data) throws IOException;

}

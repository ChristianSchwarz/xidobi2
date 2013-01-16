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

import static org.xidobi.internal.Preconditions.checkArgumentNotNull;

import java.io.IOException;

/**
 * {@link SerialPort} implementation for Windows (32bit) x86 Platform.
 * 
 * @author Christian Schwarz
 */
public class SerialPortImpl implements SerialPort {

	/** the native Win32-API, never <code>null</code> */
	private OS os;

	/**
	 * @param os
	 *            the native Win32-API, must not be <code>null</code>
	 * @param handle
	 *            the handle of the serial port
	 */
	public SerialPortImpl(	OS os,
							int handle) {
		this.os = checkArgumentNotNull(os, "os");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPort#write(byte[])
	 */
	public void write(byte[] data) throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xidobi.SerialPort#read()
	 */
	public byte[] read() throws IOException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public void close() throws IOException {}
}

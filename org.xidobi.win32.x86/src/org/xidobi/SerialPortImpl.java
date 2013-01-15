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

import static org.xidobi.internal.Preconditions.checkNotNull;

import java.io.IOException;

/**
 * {@link SerialPort} implementation for the win32 x86 Platform.
 * 
 * @author Christian Schwarz
 * 
 */
public class SerialPortImpl implements SerialPort {

	/**
	 * 
	 */
	public SerialPortImpl(	OS os,
							int handle) {
		checkNotNull(os, "os");
	}

	public void write(byte[] data) throws IOException {
		
	}

	public byte[] read() throws IOException {
		return null;
	}

	public void close() throws IOException {}
}

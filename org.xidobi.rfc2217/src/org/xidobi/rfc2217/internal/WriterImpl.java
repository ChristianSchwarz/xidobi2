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
package org.xidobi.rfc2217.internal;

import static org.xidobi.spi.Preconditions.checkArgumentNotNull;

import java.io.IOException;
import java.io.OutputStream;

import org.xidobi.spi.Writer;

/**
 * Writer-implementation for a telnet-connection.
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
@SuppressWarnings("restriction")
final class WriterImpl implements Writer {

	/** The outputstream that belongs to the connection. */
	private OutputStream outputStream;

	/**
	 * Creates a new Writer.
	 * 
	 * @param outputStream
	 *            the outputstream that belongs to the connection, must not be <code>null</code>
	 */
	public WriterImpl(OutputStream outputStream) {
		this.outputStream = checkArgumentNotNull(outputStream, "outputStream");

	}

	public void write(byte[] data) throws IOException {
		checkArgumentNotNull(data, "data");
		outputStream.write(data);
		outputStream.flush();
	}

	public void performActionBeforeConnectionClosed() throws IOException {
	}

	public void performActionAfterConnectionClosed() {
	}

}

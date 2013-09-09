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

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.xidobi.rfc2217.Rfc2217SerialPort;
import org.xidobi.spi.BasicSerialConnection;

/**
 * @author Christian Schwarz
 */
public class SerialConnectionImpl extends BasicSerialConnection {

	private TelnetClient telnetClient;

	/**
	 * @param parent
	 *            the serial port, must not be <code>null</code>
	 * @param reader
	 * @param writer
	 */
	public SerialConnectionImpl(@Nonnull Rfc2217SerialPort parent,
								@Nonnull TelnetClient telnetClient) {
		super(parent, new ReaderImpl(telnetClient.getInputStream()), new WriterImpl(telnetClient.getOutputStream()));
		this.telnetClient = telnetClient;
	}

	@Override
	protected void closeInternal() throws IOException {
		telnetClient.disconnect();
	}

}
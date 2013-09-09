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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.xidobi.rfc2217.Rfc2217SerialPort;

/**
 * Tests the class {@link SerialConnectionImpl}
 * 
 * @author Christian Schwarz
 */
public class TestSerialConnectionImpl {

	private SerialConnectionImpl connection;

	@Mock
	private Rfc2217SerialPort parent;

	@Mock
	private TelnetClient telnetClient;

	@Before
	public void setup() {
		initMocks(this);

		when(telnetClient.getInputStream()).thenReturn(mock(InputStream.class));
		when(telnetClient.getOutputStream()).thenReturn(mock(OutputStream.class));

		connection = new SerialConnectionImpl(parent, telnetClient);
	}

	/**
	 * When the connection is closed, the telnetClient must be closed, too.
	 */
	@Test
	public void close_closesTelnetClient() throws IOException {
		connection.close();
		verify(telnetClient).disconnect();
	}
}

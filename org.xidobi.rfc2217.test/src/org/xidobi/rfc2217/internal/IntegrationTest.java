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

import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.Integer.toHexString;
import static java.net.InetSocketAddress.createUnresolved;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPortSettings;
import org.xidobi.rfc2217.Rfc2217SerialPort;

/**
 * Integrationtest for the RFC2217-implementation.
 * 
 * @author Christian Schwarz
 * @author Peter-René Jeschke
 */
public class IntegrationTest {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */

	private Rfc2217SerialPort port;

	private SerialConnection connection;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@After
	public void tearDown() {
		closeQuietly(connection);
	}

	/**
	 * 
	 */
	@Test
	public void testNam2e() throws Exception {
		port = new Rfc2217SerialPort(createUnresolved("192.168.98.31", 23));
		connection = port.open(SerialPortSettings.from9600bauds8N1().create());
		System.out.println(connection.getPort());

		connection.write("Hallo!".getBytes());
	}

	/**
	 * When a message is to be sent after the port is closed, an {@link IOException} should be
	 * thrown.
	 */
	@Test
	public void write_portIsClosed() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Port RFC2217@192.168.98.31:23 was closed!");

		port = new Rfc2217SerialPort(createUnresolved("192.168.98.31", 23));
		connection = port.open(SerialPortSettings.from9600bauds8N1().create());

		connection.close();
		connection.write(new byte[0]);
	}

	/**
	 * When the user tries to read after the port is closed, an {@link IOException} should be
	 * thrown.
	 */
	@Test
	public void read_portIsClosed() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("Port RFC2217@192.168.98.31:23 was closed!");

		port = new Rfc2217SerialPort(createUnresolved("192.168.98.31", 23));
		connection = port.open(SerialPortSettings.from9600bauds8N1().create());

		connection.close();
		connection.read();
	}

	@Test
	@Ignore
	public void name() throws IOException, InterruptedException {
		port = new Rfc2217SerialPort(createUnresolved("192.168.200.81", 23));
		connection = port.open(SerialPortSettings.from9600bauds8N1().create());

		while (true) {
			byte[] data = connection.read();
			String x = "";
			for (byte b : data) {
				String hex = toHexString(b & 0xff);
				if (hex.length() == 1)
					hex = "0" + hex;
				x += hex + " ";
			}
			System.out.println(x);
			Thread.sleep(1000);
		}
	}

}

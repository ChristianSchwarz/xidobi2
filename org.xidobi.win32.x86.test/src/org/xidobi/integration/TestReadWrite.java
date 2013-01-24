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
package org.xidobi.integration;

import static java.lang.Thread.sleep;
import static org.xidobi.OS.OS;
import static org.xidobi.SerialPortSettings.from9600_8N1;

import java.io.IOException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandleImpl;
import org.xidobi.SerialPortImpl;
import org.xidobi.SerialPortSettings;

/**
 * Integration test for classes {@link SerialPortImpl} and {@link SerialPortHandleImpl}.
 * 
 * @author Christian Schwarz
 * @author Tobias Breﬂler
 */
public class TestReadWrite extends AbstractIntegrationTest {

	/** Settings for the serial port */
	private static final SerialPortSettings PORT_SETTINGS = from9600_8N1().create();

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private SerialPortHandleImpl portHandle;

	private SerialPort connection;

	@Override
	protected void setUp() {
		portHandle = new SerialPortHandleImpl(OS, getAvailableSerialPort());
	}

	@After
	@SuppressWarnings("javadoc")
	public void tearDown() throws Exception {
		if (connection != null)
			connection.close();
	}

	/**
	 * Verifies open, write and close of a serial port.
	 * 
	 * @throws Exception
	 */
	@Test
	public void openWriteClose() throws Exception {
		connection = portHandle.open(PORT_SETTINGS);
		connection.write("Hallo".getBytes());
	}

	/**
	 * Verifies open, read and close of a serial port.
	 * 
	 * @throws Exception
	 */
	@Test
	public void openReadClose() throws Exception {
		connection = portHandle.open(PORT_SETTINGS);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sleep(100);
					connection.read();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		sleep(100);

		connection.close();

		sleep(3000);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when the same serial port is opened two
	 * times.
	 * 
	 * @throws Exception
	 */
	@Test
	public void open2x() throws Exception {
		connection = portHandle.open(PORT_SETTINGS);

		exception.expect(IOException.class);
		exception.expectMessage("Port in use");

		portHandle.open(PORT_SETTINGS);
	}

	/**
	 * Verifies that an {@link IOException} is thrown, when an non-existing serial port is opened.
	 * 
	 * @throws Exception
	 */
	@Test
	public void openNoneExistingPort() throws Exception {
		portHandle = new SerialPortHandleImpl(OS, "XXX");

		exception.expect(IOException.class);
		exception.expectMessage("Port not found");

		connection = portHandle.open(PORT_SETTINGS);
	}

}
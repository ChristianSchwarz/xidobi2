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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.xidobi.OS.OS;
import static org.xidobi.SerialPortSettings.from9600_8N1;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
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
public class TestReadWrite {

	/** Settings for the serial port */
	private static final SerialPortSettings PORT_SETTINGS = from9600_8N1().create();

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private SerialPortHandleImpl portHandle;

	private SerialPort connection;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		portHandle = new SerialPortHandleImpl(OS, "COM1");
	}

	@After
	@SuppressWarnings("javadoc")
	public void tearDown() throws IOException {
		if (connection != null)
			connection.close();
	}

	@Test
	public void openWriteClose() throws IOException {
		connection = portHandle.open(PORT_SETTINGS);
		connection.write("Hallo".getBytes());
	}

	@Test
	public void openReadClose() throws IOException {
		connection = portHandle.open(PORT_SETTINGS);

		byte[] result = connection.read();

		assertThat(result, is(notNullValue()));
	}

	@Test
	public void open2x() throws Exception {
		connection = portHandle.open(PORT_SETTINGS);

		exception.expect(IOException.class);
		exception.expectMessage("Port in use");

		portHandle.open(PORT_SETTINGS);
	}

	@Test
	public void openNoneExistingPort() throws Exception {
		portHandle = new SerialPortHandleImpl(OS, "XXX");

		exception.expect(IOException.class);
		exception.expectMessage("Port not found");

		connection = portHandle.open(PORT_SETTINGS);
	}

}
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

import static org.xidobi.OS.OS;
import static org.xidobi.SerialPortSettings.from9600_8N1;

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
public class TestOpenClose extends AbstractIntegrationTest {

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
	 * Verifies open and close of a serial port.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 1500)
	public void openReadCloseLoop() throws Exception {
		for (int i = 0; i < 50; i++) {
			connection = portHandle.open(PORT_SETTINGS);
			connection.close();
		}
	}

}
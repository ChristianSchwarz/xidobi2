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

import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.OS.OS;
import static org.xidobi.SerialPortSettings.from9600_8N1;
import static org.xidobi.WinApi.FILE_FLAG_NO_BUFFERING;
import static org.xidobi.WinApi.FILE_FLAG_OVERLAPPED;
import static org.xidobi.WinApi.GENERIC_READ;
import static org.xidobi.WinApi.GENERIC_WRITE;
import static org.xidobi.WinApi.INVALID_HANDLE_VALUE;
import static org.xidobi.WinApi.OPEN_EXISTING;

import java.io.IOException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.DCBConfigurator;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortSettings;
import org.xidobi.WriterImpl;
import org.xidobi.structs.DCB;

/**
 * Integration test for class {@link WriterImpl}.
 * 
 * @author Christian Schwarz
 */
public class TestWriterImpl extends AbstractIntegrationTest {

	/** Settings for the serial port */
	private static final SerialPortSettings PORT_SETTINGS = from9600_8N1().create();

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	private WriterImpl writer;

	@Mock
	private SerialPort portHandle;

	/** system handle of the serial port */
	private int handle;

	@Override
	protected void setUp() throws Exception {
		initMocks(this);

		String port = getAvailableSerialPort();

		handle = OS.CreateFile("\\\\.\\" + port, GENERIC_READ | GENERIC_WRITE, 0, 0, OPEN_EXISTING, FILE_FLAG_OVERLAPPED | FILE_FLAG_NO_BUFFERING, 0);
		if (handle == INVALID_HANDLE_VALUE)
			throw new IOException("Invalid handle! " + OS.GetLastError());

		final DCB dcb = new DCB();

		if (!OS.GetCommState(handle, dcb))
			throw new IOException("Unable to retrieve the current control settings for port (" + port + ")!");

		new DCBConfigurator().configureDCB(dcb, PORT_SETTINGS);
		if (!OS.SetCommState(handle, dcb))
			throw new IOException("Unable to set the control settings!");

		writer = new WriterImpl(portHandle, OS, handle);
	}

	@After
	@SuppressWarnings("javadoc")
	public void tearDown() throws Exception {
		writer.close();
		OS.CloseHandle(handle);
	}

	/**
	 * Tests the write operation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void write() throws Exception {
		for (int i = 0; i < 20; i++) {
			writer.write("Hello".getBytes());
			writer.write("World".getBytes());
		}
	}
}

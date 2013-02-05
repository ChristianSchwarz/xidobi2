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

import static org.xidobi.SerialPortSettings.from9600_8N1;

import org.junit.Test;
import org.xidobi.OS;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortFinderImpl;
import org.xidobi.WinApi;

/**
 * Integration test for class {@link OS}.
 * 
 * @author Tobias Breﬂler
 */
public class TestOpenWriteReadClose extends AbstractIntegrationTest {

	/** Class under test */
	private WinApi os = OS.OS;

	/**
	 * Test for open and close of a serial port.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 1500)
	public void openAndClose() throws Exception {

		SerialPortFinderImpl finder = new SerialPortFinderImpl(os);

		SerialPort serialPort = finder.get(getAvailableSerialPort());

		SerialConnection connection = serialPort.open(from9600_8N1().create());
		connection.close();
	}

	/**
	 * Test for open, write and close of a serial port.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 1500)
	public void write() throws Exception {
		SerialPortFinderImpl finder = new SerialPortFinderImpl(os);

		SerialPort serialPort = finder.get(getAvailableSerialPort());

		SerialConnection connection = serialPort.open(from9600_8N1().create());
		connection.write("This is just a test".getBytes());
		connection.close();
	}

	/**
	 * Test for open, read and close of a serial port.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 1500)
	public void read() throws Exception {
		SerialPortFinderImpl finder = new SerialPortFinderImpl(os);

		SerialPort serialPort = finder.get(getAvailableSerialPort());

		final SerialConnection connection = serialPort.open(from9600_8N1().create());
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					connection.read();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		Thread.sleep(100);
		connection.close();
	}

}

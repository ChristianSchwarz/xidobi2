package org.xidobi.integration;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandle;
import org.xidobi.SerialPortHandleImpl;
import org.xidobi.SerialPortSettings;

import static org.xidobi.OS.OS;
import static org.xidobi.SerialPortSettings.bauds;

public class TestWrite {

	/**
	 * 
	 */
	private static final SerialPortSettings PORT_SETTINGS = bauds(9600).create();

	@Test
	public void openWriteClose() throws IOException {
		SerialPortHandle portHandle = new SerialPortHandleImpl(OS, "COM1");

		SerialPort connection = null;
		try {
			connection = portHandle.open(PORT_SETTINGS);
			connection.write("Hallo".getBytes());
		}
		finally {
			if (connection != null)
				connection.close();

		}
	}

	
	@Test
	public void open2x() throws Exception {
		SerialPortHandle portHandle = new SerialPortHandleImpl(OS, "COM1");

		SerialPort connection = null;
		try {
			connection = portHandle.open(PORT_SETTINGS);
			portHandle.open(PORT_SETTINGS);
		}
		finally {
			if (connection != null)
				connection.close();

		}
	}
	
	@Test
	public void openNoneExistingPort() throws Exception {
		SerialPortHandle portHandle = new SerialPortHandleImpl(OS, "XXX");
		
		SerialPort connection = null;
		try {
			connection = portHandle.open(PORT_SETTINGS);
			
		}
		finally {
			if (connection != null)
				connection.close();
			
		}
	}

}
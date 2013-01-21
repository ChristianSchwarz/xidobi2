package org.xidobi.integration;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortHandleImpl;
import org.xidobi.SerialPortSettings;

import static org.xidobi.OS.OS;
import static org.xidobi.SerialPortSettings.from9600_8n1;

public class TestWrite {

	/**
	 * 
	 */
	private static final SerialPortSettings PORT_SETTINGS = from9600_8n1().create();

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private SerialPortHandleImpl portHandle;

	private SerialPort connection;

	@Before
	public void setUp() {
		portHandle = new SerialPortHandleImpl(OS, "COM1");
	}

	@After
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
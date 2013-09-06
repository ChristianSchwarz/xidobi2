/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 11:31:13
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.Integer.toHexString;
import static java.net.InetSocketAddress.createUnresolved;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.junit.After;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPortSettings;
import org.xidobi.rfc2217.Rfc2217SerialPort;







import com.google.common.io.Closeables;

import static java.net.InetSocketAddress.createUnresolved;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Christian Schwarz
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
	public void tearDown() throws IOException {
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

	/**
	 * Wenn when, then.
	 * @throws InterruptedException 
	 */
	@Test
	public void name() throws IOException, InterruptedException {
		port = new Rfc2217SerialPort(createUnresolved("192.168.200.111", 10001));
		connection = port.open(SerialPortSettings.from9600bauds8N1().create());

		while (true){
			byte[] data=connection.read();
			String x = "";
			for (byte b : data) {
				String hex = toHexString(b & 0xff);
				if (hex.length()==1)
					hex = "0"+hex;
				x+=hex+" ";
			}
			System.out.println(x);
			Thread.sleep(1000);
		}
	}

}

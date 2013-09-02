/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 11:31:13
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPortSettings;
import org.xidobi.rfc2217.Rfc2217SerialPort;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.CommandProcessor;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.DecoderErrorHandler;

import static java.net.InetSocketAddress.createUnresolved;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Christian Schwarz
 * 
 */
public class IntegrationTest {

	/**
	 * @author Christian Schwarz
	 * 
	 */
	private final class NegotiationListener implements TelnetNotificationHandler {
		public void receivedNegotiation(int negotiation_code, int option_code) {

			String txt = null;

			switch (negotiation_code) {
				case TelnetNotificationHandler.RECEIVED_DO:
					txt = "DO " + option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_DONT:
					txt = "DONT " + option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_WILL:
					txt = "WILL " + option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_WONT:
					txt = "WONT " + option_code;
					break;
				case TelnetNotificationHandler.RECEIVED_COMMAND:
					txt = "COMMAND " + option_code;
					break;
			}

			System.out.println(txt);
			System.out.flush();
		}
	}

	private ReentrantLock lock = new ReentrantLock();
	private Condition negotiationReceived = lock.newCondition();

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */

	private Rfc2217SerialPort port;

	@Mock
	private SerialConnection connection;
	@Mock
	private CommandProcessor processor;
	@Mock
	private DecoderErrorHandler errorHandler;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * 
*/
	@Test
	public void testNam2e() throws Exception {
		port = new Rfc2217SerialPort(createUnresolved("192.168.98.31", 23));
		connection = port.open(SerialPortSettings.from9600bauds8N1().create());
		System.out.println(connection.getPort());
	}

}

/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 11:31:13
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetInputListener;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.xidobi.SerialPortSettings;
import org.xidobi.rfc2217.Rfc2217SerialPort;

import static java.lang.Thread.sleep;
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

	private SerialConnectionImpl port;

	@Mock
	private SerialConnectionImpl connection;

	@Before
	public void setUp() {
		initMocks(this);
	}

	/**
	 * 
	 */
	@Test
	public void testName() throws Exception {
		TelnetClient telnetClient = new TelnetClient();
		telnetClient.setReaderThread(true);
		telnetClient.addOptionHandler(new BinaryOptionHandler());
		telnetClient.addOptionHandler(new ComPortOptionHandler());
		telnetClient.registerNotifHandler(new NegotiationListener());
		telnetClient.registerInputListener(new TelnetInputListener() {

			public void telnetInputAvailable() {
				System.out.println("telnetInputAvailable");
			}
		});

		System.out.println("connect");
		telnetClient.connect("192.168.98.31");
		sleep(1000);

		System.out.println(telnetClient.getLocalOptionState(0));
		System.out.println(telnetClient.getRemoteOptionState(0));
		System.out.println(telnetClient.getLocalOptionState(45));
		System.out.println(telnetClient.getRemoteOptionState(45));
		
		telnetClient.disconnect();
	}

	/**
	 * 
*/
	@Test
	public void testNam2e() throws Exception {
		new Rfc2217SerialPort(createUnresolved("192.168.98.31", 23)).open(SerialPortSettings.from9600bauds8N1().create());
	}

}

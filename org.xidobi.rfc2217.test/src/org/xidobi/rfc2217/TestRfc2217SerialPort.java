/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.08.2013 10:18:45
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.xidobi.SerialPortSettings;

import static java.net.InetSocketAddress.createUnresolved;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;

/**
 * Tests the class {@link Rfc2217SerialPort}
 * 
 * @author Christian Schwarz
 * 
 */
@SuppressWarnings("javadoc")
public class TestRfc2217SerialPort {

	/** Default Port Settings */
	private static final SerialPortSettings PORT_SETTINGS = SerialPortSettings.from9600bauds8N1().create();

	/** The Dummy Address of an Acess Server */
	private static final InetSocketAddress ACCESS_SERVER_ADDRESS = createUnresolved("host", 23);

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	private Rfc2217SerialPort port;

	@Mock
	private TelnetClient telnetClient;

	@Captor
	private ArgumentCaptor<TelnetNotificationHandler> notificationHandler;



	/**
	 * Init's the {@link Rfc2217SerialPort} with an unresolved Address.
	 */
	@Before
	public void setUp() {
		initMocks(this);
		port = new TestableRfc2217Port(ACCESS_SERVER_ADDRESS);

	}

	/**
	 * If argument {@code accessServer} is <code>null</code> an {@link IllegalArgumentException}
	 * must be thrown.
	 */
	@Test
	public void new_nullAddress() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Parameter >accessServer< must not be null!");

		new Rfc2217SerialPort(null);
	}
	
	

	/**
	 * If argument {@code settings} is <code>null</code> an {@link IllegalArgumentException} must be
	 * thrown.
	 * 
	 * @throws IOException
	 */
	@Test
	public void open_nullSetting() throws IOException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Parameter >settings< must not be null!");

		port.open(null);
	}
	

	/**
	 * The Portname must represent the address of the access server in the form
	 * {@code "RFC2217@"+hostname+":"+port}
	 * 
	 * @see #ACCESS_SERVER_ADDRESS
	 */
	@Test
	public void getPortName() {
		assertThat(port.getPortName(), is("RFC2217@host:23"));
	}

	/**
	 * If the port is not open the description must be <code>null</code>
	 */
	@Test
	public void getDescription_whenNotOpened() {
		assertThat(port.getDescription(), is(nullValue()));
	}

	/**
	 * If a positiv value of milli seconds is passed, no exception must be thrown!
	 */
	@Test
	public void setNegotiationTimeout()  {
		port.setNegotiationTimeout(500);
	}
	
	/**
	 * If a negative value of milli seconds is passed, the setter must fail fast, to indicate an error!
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegotiationTimeout_negative()  {
		port.setNegotiationTimeout(-500);
	}

	/**
	 * If the host is unknown an IOException must be thrown.
	 * 
	 * @throws IOException
	 */
	@Test(expected = IOException.class)
	public void open_unknownHost() throws IOException {
		doThrow(IOException.class).when(telnetClient).connect(anyString(), anyInt());

		port.open(PORT_SETTINGS);
	}

	/**
	 * If the Binary Telnet Option is not negotiation within 10milli seconds a IOException must be thrown
	 * to indicate that this option was not accepted or refused by the access server.
	 */

	@Test(timeout = 100)
	public void open_binaryOptionTimeout() throws Exception {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option");

		port.setNegotiationTimeout(10);
		port.open(PORT_SETTINGS);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final class TestableRfc2217Port extends Rfc2217SerialPort {

		TestableRfc2217Port(InetSocketAddress accessServer) {
			super(accessServer);
		}

		@Override
		protected TelnetClient createTelnetClient() {
			return telnetClient;
		}
	}

//	/**
//	 * Opens the given port asynchron with the given settings.
//	 * 
//	 * @return the Future containing the result of the {@code open()} - Operation
//	 */
//	private ListenableFuture<SerialConnection> openAsync(final Rfc2217SerialPort port, final SerialPortSettings portSettings) {
//		final SettableFuture<SerialConnection> f = SettableFuture.create();
//
//		new Thread() {
//			public void run() {
//				try {
//					f.set(port.open(portSettings));
//				}
//				catch (IOException e) {
//					f.setException(e);
//				}
//			};
//		}.start();
//
//		return f;
//	}

}

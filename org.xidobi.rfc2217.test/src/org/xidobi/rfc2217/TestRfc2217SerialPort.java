/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 16.08.2013 10:18:45
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPortSettings;
import org.xidobi.rfc2217.internal.ComPortOptionHandler;

import testtools.ByteBuffer;
import testtools.MessageBuilder;
import static java.lang.Thread.sleep;
import static java.net.InetSocketAddress.createUnresolved;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.xidobi.DataBits.DATABITS_8;
import static org.xidobi.Parity.PARITY_NONE;
import static org.xidobi.StopBits.STOPBITS_1;
import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_DO;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_DONT;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_WILL;
import static org.apache.commons.net.telnet.TelnetOption.BINARY;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static org.junit.Assert.assertThat;
import static testtools.MessageBuilder.baudRateResponse;
import static testtools.MessageBuilder.dataBitsResponse;
import static testtools.MessageBuilder.flowControlResponse;
import static testtools.MessageBuilder.parityResponse;
import static testtools.MessageBuilder.signatureRequest;
import static testtools.MessageBuilder.signatureResponse;
import static testtools.MessageBuilder.stopBitsResponse;

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

	@Captor
	private ArgumentCaptor<ComPortOptionHandler> comPortOptionHandler;

	private Future<SerialConnection> open;

	/**
	 * Init's the {@link Rfc2217SerialPort} with an unresolved Address.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		initMocks(this);
		port = new TestableRfc2217Port(ACCESS_SERVER_ADDRESS);
		doNothing().when(telnetClient).registerNotifHandler(notificationHandler.capture());
		doNothing().when(telnetClient).addOptionHandler(comPortOptionHandler.capture());
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
	public void setNegotiationTimeout() {
		port.setNegotiationTimeout(500);
	}

	/**
	 * If a negative value of milli seconds is passed, the setter must fail fast, to indicate an
	 * error!
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegotiationTimeout_negative() {
		port.setNegotiationTimeout(-500);
	}

	/**
	 * If the access server refuse to accept com-port-options, the telnet client must be
	 * disconnected an an {@link IOException} must be thrown. The com-port-option is required to
	 * apply the serial settings on the access server.
	 * 
	 */
	@Test(timeout = 5000)
	public void open() throws Throwable {
		when(telnetClient.getInputStream()).thenReturn(mock(InputStream.class));
		when(telnetClient.getOutputStream()).thenReturn(mock(OutputStream.class));
		final int bauds = PORT_SETTINGS.getBauds();

		accessServerSends(baudRateResponse(bauds)).when(telnetClient).sendSubnegotiation(baudRateRequest(9600));
		accessServerSends(dataBitsResponse(8)).when(telnetClient).sendSubnegotiation(dataBitsRequest(8));
		accessServerSends(parityResponse(1)).when(telnetClient).sendSubnegotiation(parityRequest(1));
		accessServerSends(stopBitsResponse(1)).when(telnetClient).sendSubnegotiation(stopbitsRequest(1));
		accessServerSends(flowControlResponse(1)).when(telnetClient).sendSubnegotiation(flowControlRequest(1));
		accessServerSends(signatureResponse("Signature Info")).when(telnetClient).sendSubnegotiation(signatureRequest("").toIntArray());

		open = openAsync(port, PORT_SETTINGS);

		accessServerSendsNegotiation(RECEIVED_DO, COM_PORT_OPTION);
		accessServerSendsNegotiation(RECEIVED_DO, BINARY);
		accessServerSendsNegotiation(RECEIVED_WILL, BINARY);

		SerialConnection connection = await(open);
		assertThat(connection, is(notNullValue()));
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
	 * If the Binary Telnet Option is not negotiation within 10milli seconds a IOException must be
	 * thrown to indicate that this option was not accepted or refused by the access server.
	 */
	@Test(timeout = 100)
	public void open_failedBinaryOptionTimeout() throws Exception {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option");

		port.setNegotiationTimeout(10);
		try {
			port.open(PORT_SETTINGS);
		}
		finally {
			verify(telnetClient).disconnect();
		}
	}

	/**
	 * If the access server refuse to accept com-port-options, the telnet client must be
	 * disconnected an an {@link IOException} must be thrown. The com-port-option is required to
	 * apply the serial settings on the access server.
	 * 
	 */
	@Test(timeout = 500)
	public void open_failedComOptionRefused() throws Throwable {
		exception.expect(IOException.class);
		exception.expectMessage("refused to accept option: " + COM_PORT_OPTION);

		open = openAsync(port, PORT_SETTINGS);
		awaitValue(notificationHandler, 300).receivedNegotiation(RECEIVED_DONT, COM_PORT_OPTION);
		verify(telnetClient, timeout(300)).disconnect();
		await(open);
	}

	/**
	 * If the access server refuse to accept com-port-options, the telnet client must be
	 * disconnected an an {@link IOException} must be thrown. The com-port-option is required to
	 * apply the serial settings on the access server.
	 * 
	 */
	@Test(timeout = 500)
	public void open_failedBaudRateRefused() throws Throwable {
		final int bauds = PORT_SETTINGS.getBauds();
		accessServerSends(baudRateResponse(bauds + 10)).when(telnetClient).sendSubnegotiation(baudRateRequest(bauds));

		open = openAsync(port, PORT_SETTINGS);

		accessServerSendsNegotiation(RECEIVED_DO, COM_PORT_OPTION);
		accessServerSendsNegotiation(RECEIVED_DO, BINARY);
		accessServerSendsNegotiation(RECEIVED_WILL, BINARY);

		exception.expect(IOException.class);
		exception.expectMessage("The baud rate setting was refused (" + bauds + ")!");

		await(open);
	}

	/**
	 * If the access server refuse to accept com-port-options, the telnet client must be
	 * disconnected an an {@link IOException} must be thrown. The com-port-option is required to
	 * apply the serial settings on the access server.
	 * 
	 */
	@Test(timeout = 500)
	public void open_failedDataBitsRefused() throws Throwable {
		final int bauds = PORT_SETTINGS.getBauds();
		accessServerSends(baudRateResponse(bauds)).when(telnetClient).sendSubnegotiation(baudRateRequest(bauds));

		accessServerSends(dataBitsResponse(10)).when(telnetClient).sendSubnegotiation(dataBitsRequest(8));

		open = openAsync(port, PORT_SETTINGS);

		accessServerSendsNegotiation(RECEIVED_DO, COM_PORT_OPTION);
		accessServerSendsNegotiation(RECEIVED_DO, BINARY);
		accessServerSendsNegotiation(RECEIVED_WILL, BINARY);

		exception.expect(IOException.class);
		exception.expectMessage("The data bits setting was refused (" + DATABITS_8 + ")!");

		await(open);
	}

	/**
	 * If the access server refuse to accept com-port-options, the telnet client must be
	 * disconnected an an {@link IOException} must be thrown. The com-port-option is required to
	 * apply the serial settings on the access server.
	 * 
	 */
	@Test(timeout = 500)
	public void open_failedParityRefused() throws Throwable {
		final int bauds = PORT_SETTINGS.getBauds();
		accessServerSends(baudRateResponse(bauds)).when(telnetClient).sendSubnegotiation(baudRateRequest(bauds));
		accessServerSends(dataBitsResponse(8)).when(telnetClient).sendSubnegotiation(dataBitsRequest(8));
		accessServerSends(parityResponse(4)).when(telnetClient).sendSubnegotiation(parityRequest(1));

		open = openAsync(port, PORT_SETTINGS);

		accessServerSendsNegotiation(RECEIVED_DO, COM_PORT_OPTION);
		accessServerSendsNegotiation(RECEIVED_DO, BINARY);
		accessServerSendsNegotiation(RECEIVED_WILL, BINARY);

		exception.expect(IOException.class);
		exception.expectMessage("The parity setting was refused (" + PARITY_NONE + ")!");

		await(open);
	}

	/**
	 * If the access server refuse to accept com-port-options, the telnet client must be
	 * disconnected an an {@link IOException} must be thrown. The com-port-option is required to
	 * apply the serial settings on the access server.
	 * 
	 */
	@Test(timeout = 5000)
	public void open_failedStopbitsRefused() throws Throwable {
		final int bauds = PORT_SETTINGS.getBauds();
		accessServerSends(baudRateResponse(bauds)).when(telnetClient).sendSubnegotiation(baudRateRequest(bauds));
		accessServerSends(dataBitsResponse(8)).when(telnetClient).sendSubnegotiation(dataBitsRequest(8));
		accessServerSends(parityResponse(1)).when(telnetClient).sendSubnegotiation(parityRequest(1));
		accessServerSends(stopBitsResponse(4)).when(telnetClient).sendSubnegotiation(stopbitsRequest(1));

		open = openAsync(port, PORT_SETTINGS);

		accessServerSendsNegotiation(RECEIVED_DO, COM_PORT_OPTION);
		accessServerSendsNegotiation(RECEIVED_DO, BINARY);
		accessServerSendsNegotiation(RECEIVED_WILL, BINARY);

		exception.expect(IOException.class);
		exception.expectMessage("The stop bits setting was refused (" + STOPBITS_1 + ")!");

		await(open);
	}

	// /////////////////////////////// Utility-Methods
	// ////////////////////////////////////////////////////////////////////////////
	/** emulates the behaviour of the access server sending an negotiation */
	private void accessServerSendsNegotiation(int negotiationCode, int optionCode) throws TimeoutException {
		TelnetNotificationHandler handler = awaitValue(notificationHandler, 2000);
		handler.receivedNegotiation(negotiationCode, optionCode);
	}

	/**
	 * Sends the given response under the specified condition.
	 * <p>
	 * Usage Sample:
	 * 
	 * <pre>
	 * accessServerSends(baudRateResponse(bauds)).when(telnetClient).sendSubnegotiation(baudRateRequest(9600));
	 * </pre>
	 */
	private Stubber accessServerSends(final ByteBuffer resp) {
		return doAnswer(receivedCommand(resp));
	}

	/**
	 * @param buildSetBaudRateResponse
	 * @return
	 */
	private Answer<Void> receivedCommand(final ByteBuffer resp) {
		return new Answer<Void>() {

			public Void answer(InvocationOnMock invocation) throws Throwable {
				telnetReceivedCommand(resp);
				return null;
			}
		};
	}

	private void telnetReceivedCommand(ByteBuffer resp) throws TimeoutException {
		ComPortOptionHandler comOption = awaitValue(comPortOptionHandler, 200);
		int[] data = resp.toIntArray();
		comOption.answerSubnegotiation(data, data.length);
	}

	/**
	 * @return
	 */
	private int[] dataBitsRequest(int dataBits) {
		return MessageBuilder.dataBitsRequest(dataBits).toIntArray();
	}

	/**
	 * Argument-Matcher that checks for the binary form of a baud-rate request using the given
	 * baud-rate
	 */
	private int[] baudRateRequest(int bauds) {
		return MessageBuilder.baudRateRequest(bauds).toIntArray();
	}

	private int[] parityRequest(int parity) {
		return MessageBuilder.parityRequest(parity).toIntArray();
	}

	private int[] stopbitsRequest(int stopbits) {
		return MessageBuilder.stopBitsRequest(stopbits).toIntArray();
	}

	private int[] flowControlRequest(int flowControl) {
		return MessageBuilder.flowControlRequest(flowControl).toIntArray();
	}

	private final class TestableRfc2217Port extends Rfc2217SerialPort {

		TestableRfc2217Port(InetSocketAddress accessServer) {
			super(accessServer);
		}

		@Override
		protected TelnetClient createTelnetClient() {
			return telnetClient;
		}
	}

	/**
	 * Waits if necessary for the computation to complete or throws an Exception if the future was
	 * canceled, interrupted or terminated unexpected. Any {@link ExecutionException} will be
	 * transformed to its cause exception.
	 * 
	 * @return
	 */
	private <T> T await(Future<T> future) throws Throwable {
		try {
			return future.get();
		}
		catch (ExecutionException e) {
			throw e.getCause();
		}
	}

	/**
	 * Opens the given port asynchron with the given settings.
	 * 
	 * @return the Future containing the result of the {@code open()} - Operation
	 */
	private Future<SerialConnection> openAsync(final Rfc2217SerialPort port, final SerialPortSettings portSettings) {
		ExecutorService e = newSingleThreadExecutor();

		Callable<SerialConnection> task = new Callable<SerialConnection>() {

			public SerialConnection call() throws Exception {
				return port.open(portSettings);
			}
		};

		final Future<SerialConnection> f = e.submit(task);

		e.shutdown();

		return f;

	}

	/**
	 * Returns the {@link ArgumentCaptor}'s value once it become available.
	 */
	private <T> T awaitValue(ArgumentCaptor<T> captor, long millis) throws TimeoutException {

		while (millis > 0) {
			if (!captor.getAllValues().isEmpty())
				return captor.getValue();

			try {
				sleep(5);
			}
			catch (InterruptedException ignore) {}

			millis -= 5;

		}

		throw new TimeoutException();

	}

}

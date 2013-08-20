/*
 *
 * Copyright Gemtec GmbH 2009-2013
 * Erstellt am: 14.08.2013 09:02:05
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;

import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetOptionHandler;
import org.xidobi.SerialConnection;
import org.xidobi.SerialPort;
import org.xidobi.SerialPortSettings;
import org.xidobi.rfc2217.internal.BinaryOptionHandler;
import org.xidobi.rfc2217.internal.ComPortOptionHandler;
import org.xidobi.rfc2217.internal.NegotiationHandler;
import org.xidobi.rfc2217.internal.SerialConnectionImpl;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_DO;
import static org.apache.commons.net.telnet.TelnetNotificationHandler.RECEIVED_WILL;
import static org.apache.commons.net.telnet.TelnetOption.BINARY;

/**
 * Implements the client side of the RFC2217 serial over telnet protocol as {@link SerialPort}.
 * 
 * @author Christian Schwarz
 * 
 */
public class Rfc2217SerialPort implements SerialPort {

	/** The address of the access server, we are connected to */
	private final InetSocketAddress accessServer;
	/**
	 * defines the timeout that is used for the negotiation phase of
	 * {@link #open(SerialPortSettings)} , in milli seconds
	 */
	private long negotiationTimeout;

	/**
	 * Creates a new {@link Rfc2217SerialPort} that that will be connected to the given Access
	 * Server Address. This port ist initial not open.
	 * 
	 * @param accessServer
	 *            the adress of the Access Server
	 * 
	 * @exception IllegalArgumentException
	 *                if <code>null</code> is passed
	 */
	public Rfc2217SerialPort(@Nonnull InetSocketAddress accessServer) {
		if (accessServer == null)
			throw new IllegalArgumentException("Parameter >accessServer< must not be null!");
		this.accessServer = accessServer;
	}

	/**
	 * Opens this serial port by establihing a Telnet session with the access server, defined in the
	 * constructor.
	 * 
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 *             if the port cannot be opened, this may occure if ...
	 *             <ul>
	 *             <li>the access server is unreachable
	 *             <li>the access server refuse the Telnet connection
	 *             <li>a timeout occures during connect
	 *             <li>the access server refuse the {@link SerialPortSettings}
	 *             </ul>
	 */
	@Nonnull
	@WillCloseWhenClosed
	public SerialConnection open(@Nonnull SerialPortSettings settings) throws IOException {
		if (settings == null)
			throw new IllegalArgumentException("Parameter >settings< must not be null!");

		TelnetClient telnetClient = createTelnetClient();
		configure(telnetClient);
		connect(telnetClient);
		awaitNegotiation(telnetClient);

		return new SerialConnectionImpl(this, telnetClient);

	}

	/**
	 * Subclasses may override this method to create an own {@link TelnetClient} or to add special
	 * {@link TelnetOptionHandler}'s to it.
	 * 
	 * @return a new {@link TelnetClient} instance that is not connected
	 */
	@Nonnull
	protected TelnetClient createTelnetClient() {
		return new TelnetClient();
	}

	/**
	 * Configures the {@link TelnetClient}, adds handler for BINARY-OPERATION and
	 * COMPORT-OPTION.
	 */
	private void configure(@Nonnull TelnetClient telnetClient) throws IOException {
		telnetClient.setReaderThread(true);
		try {
			telnetClient.addOptionHandler(new BinaryOptionHandler());
			telnetClient.addOptionHandler(new ComPortOptionHandler());
		}
		catch (InvalidTelnetOptionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Connects the Telnet Client to the access server. An {@link IOException} will be thrown if it
	 * is not possible, e.g. if the host is unknown or cannot be resolved.
	 */
	private void connect(@Nonnull TelnetClient telnetClient) throws IOException {
		telnetClient.connect(accessServer.getHostString(), accessServer.getPort());
	}

	/**
	 * Awaits the the end of the negotiation phase. An {@link IOException} will thrown if the
	 * connection was closed, the negotiation phase timed out or at least one option is not
	 * supported by the access server.
	 */
	private void awaitNegotiation(@Nonnull TelnetClient telnetClient) throws IOException {
		NegotiationHandler nh = new NegotiationHandler(telnetClient);

		nh.awaitOptionState(BINARY,RECEIVED_DO, negotiationTimeout);
		nh.awaitOptionState(BINARY,RECEIVED_WILL, negotiationTimeout);
		nh.awaitOptionState(COM_PORT_OPTION,RECEIVED_DO, negotiationTimeout);
	}

	/**
	 * Returns the name of this port in the form {@code "RFC2217@"+hostname+":"+port}, e.g
	 * "RFC2217@192.168.0.15:23". {@inheritDoc}
	 */
	@Nonnull
	public String getPortName() {
		return "RFC2217@" + accessServer.getHostString() + ":" + accessServer.getPort();
	}

	/**
	 * Returns the signature information as defined in RFC2217 of the acces server once this port is
	 * open. If this port is not open <code>null</code> will be returned.
	 * <p>
	 * The signature information may be combination of any characters, there is no defined structure
	 * of the text. It may contain manufactor information, version number information, or any other
	 * information. Thus clients should not rely on this!
	 * 
	 * @return <ul>
	 *         <li><code>null</code>, if this port is not open</li> <li>signature information of the
	 *         access server as defined in RFC2217, if this port is open</li>
	 *         </ul>
	 */
	@Nullable
	public String getDescription() {
		return null;
	}

	/**
	 * Sets the negotiation timeout in milli seconds for all telnet options. This method must be
	 * called before {@link #open(SerialPortSettings)} otherwise it has no effect. The
	 * {@link #open(SerialPortSettings)} will wait atmost the given number of milli seconds to
	 * receive notifications about the required options.
	 * 
	 * @param milliSeconds
	 *            the milli seconds until all options must be negotiated or refused by the access
	 *            server
	 * 
	 * @see #open(SerialPortSettings)
	 */
	public void setNegotiationTimeout(@Nonnegative long milliSeconds) {
		if (milliSeconds < 0)
			throw new IllegalArgumentException("The negotiation timeout must be positive! Got:" + milliSeconds);
		negotiationTimeout = milliSeconds;

	}

}

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
import org.xidobi.rfc2217.internal.BlockingCommandSender;
import org.xidobi.rfc2217.internal.ComPortOptionHandler;
import org.xidobi.rfc2217.internal.ComPortOptionHandler.DecoderErrorHandler;
import org.xidobi.rfc2217.internal.NegotiationHandler;
import org.xidobi.rfc2217.internal.SerialConnectionImpl;
import org.xidobi.rfc2217.internal.commands.AbstractControlCmd;
import org.xidobi.rfc2217.internal.commands.BaudrateControlCmd;
import org.xidobi.rfc2217.internal.commands.DataBitsControlCmd;

import static org.xidobi.rfc2217.internal.RFC2217.COM_PORT_OPTION;
import static org.apache.commons.net.telnet.TelnetOption.BINARY;

/**
 * Implements the client side of the RFC2217 serial over telnet protocol as {@link SerialPort}.
 * 
 * @author Christian Schwarz
 * 
 */
public class Rfc2217SerialPort implements SerialPort {

	/** The address of the access server, we are connected to */
	@Nonnull
	private final InetSocketAddress accessServer;
	/**
	 * defines the timeout that is used for the negotiation phase of
	 * {@link #open(SerialPortSettings)} , in milli seconds, default is 1second
	 */
	private long negotiationTimeout = 5000;

	/**
	 * Used to await the option negotiations during {@link #open(SerialPortSettings)}
	 * 
	 * @see #awaitNegotiation(TelnetClient)
	 */
	private NegotiationHandler negotiationHandler;
	private BlockingCommandSender commandSender;

	@Nonnull
	private final DecoderErrorHandler commandErrorHandler = new DecoderErrorHandler() {

		public void onDecoderError(IOException e) {

		}
	};

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

		createNegotiationHandler(telnetClient);
		createCommandSender(telnetClient);

		configure(telnetClient);
		connect(telnetClient);
		try {
			awaitNegotiation(telnetClient);

			sendPortSettings(telnetClient, settings);

			return new SerialConnectionImpl(this, telnetClient);
		}
		catch (IOException e) {
			disconnect(telnetClient);
			throw e;
		}
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
	 * Configures the {@link TelnetClient}, adds handler for BINARY-OPERATION and COMPORT-OPTION.
	 */
	private void configure(@Nonnull TelnetClient telnetClient) throws IOException {
		telnetClient.setReaderThread(true);
		try {
			telnetClient.addOptionHandler(new BinaryOptionHandler());
			telnetClient.addOptionHandler(new ComPortOptionHandler(commandSender, commandErrorHandler));
		}
		catch (InvalidTelnetOptionException e) {
			throw new IllegalStateException(e);
		}
	}

	private void createNegotiationHandler(TelnetClient telnetClient) {
		negotiationHandler = new NegotiationHandler(telnetClient);
	}

	private void createCommandSender(TelnetClient telnetClient) {
		commandSender = new BlockingCommandSender(telnetClient);
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

		negotiationHandler.awaitAcceptOptionNegotiation(COM_PORT_OPTION, negotiationTimeout);
		negotiationHandler.awaitAcceptOptionNegotiation(BINARY, negotiationTimeout);
		negotiationHandler.awaitSendOptionNegotiation(BINARY, negotiationTimeout);
	}

	/**
	 * Sets the given {@link SerialPortSettings} on the access server. 
	 * @throws IOException if the access server refused to set one of the properties.
	 */
	private void sendPortSettings(TelnetClient telnetClient, SerialPortSettings settings) throws IOException {
		sendAndValidate(new BaudrateControlCmd(settings.getBauds()), "The baud rate setting was refused (" + settings.getBauds() + ")!");

		sendAndValidate(new DataBitsControlCmd(settings.getDataBits()), "The data bits setting was refused (" + settings.getDataBits() + ")!");

	}

	/**
	 *Send the given command to the access server and valides the response. 
	 * @throws IOException if the send operation failed or the access server did not respone with the same content (to indicate that the operation was successfull)
	 */
	private void sendAndValidate(final AbstractControlCmd req, final String errorMessage) throws IOException {
		AbstractControlCmd resp = commandSender.send(req);
		if (!resp.equals(req))
			throw new IOException(errorMessage);
	}

	/** Disconnects the {@link TelnetClient} and sucks all kind of thrown Exceptions. */
	private void disconnect(TelnetClient telnetClient) {
		if (telnetClient == null)
			return;

		try {
			telnetClient.disconnect();
		}
		catch (Exception ignore) {}

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

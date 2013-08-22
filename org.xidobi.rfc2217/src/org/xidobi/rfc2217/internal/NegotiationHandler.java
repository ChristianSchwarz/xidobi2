/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 16:03:32
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.xidobi.rfc2217.internal.UpdatingGuard.Predicate;


/**
 * This class is used to await notifications during the negotiation phase of a telnet session.
 * <p>
 * IMPORTANT: This class must be instantiated BEFORE the {@link TelnetClient} is connected.
 * Otherwise this class is not able to work as expected! the reason is that it is otherwise not
 * possible to track option negotiations.
 * 
 * 
 * @author Christian Schwarz
 * 
 */
public class NegotiationHandler  {

	/**
	 * Listen for option negotiations. Accepted and refused options are stored to dedicated
	 * collections for further operations.
	 */
	private TelnetNotificationHandler handler = new TelnetNotificationHandler() {

		public void receivedNegotiation(int negotiationCode, int optionCode) {

			switch (negotiationCode) {

				case RECEIVED_WILL:
					willingToSend.add(optionCode);
					break;
				case RECEIVED_WONT:
					refusedToSend.add(optionCode);
					break;
				case RECEIVED_DO:
					willingToAccept.add(optionCode);
					break;
				case RECEIVED_DONT:
					refusedToAccept.add(optionCode);
					break;
			}

			negotiationChange.signalAll();
		}
	};
	

	/** contains the options that the access server is willing to send*/
	private final Set<Integer> willingToSend = new HashSet<Integer>();
	/** contains the options that the access server refused to send*/
	private final Set<Integer> refusedToSend = new HashSet<Integer>();
	/** contains the options that the access server is willing to accept*/
	private final Set<Integer> willingToAccept = new HashSet<Integer>();
	/** contains the options that the access server refused to accept*/
	private final Set<Integer> refusedToAccept = new HashSet<Integer>();

	private final UpdatingGuard negotiationChange = new UpdatingGuard();
	
	/**
	 * @param telnetClient
	 *            the Telnet Client to be observed
	 */
	public NegotiationHandler(@Nonnull TelnetClient telnetClient) {
		if (telnetClient == null)
			throw new IllegalArgumentException("Parameter >telnetClient< must not be null!");
		if (telnetClient.isConnected())
			throw new IllegalStateException("The Telnet Client must not be connected! Please instantiate this class before it is connected!");

		telnetClient.registerNotifHandler(handler);
	}

	/**
	 * Waits until the given option is accepted (Telnet:WILL) or refused (Telnet:WONT) by the access server or an timeout
	 * occures. Nothing happens if within the given time the access server acknoledged that it is
	 * willing to send the option. If an timeout occures or the access server denied to send the
	 * option an {@link IOException} will be thrown.
	 * 
	 * @param optionCode
	 *            the code of the option to wait for, in the range [1..255]
	 * @param negotiationTimeout
	 *            the number of milli seconds to wait at most
	 * @throws IOException
	 */
	public void awaitSendOptionNegotiation(final int optionCode, long negotiationTimeout) throws IOException {
		Predicate acceptStatus = new Predicate() {
			public boolean isSatisfied() {
				return willingToSend.contains(optionCode) || refusedToSend.contains(optionCode);
			}
		};
	
		final boolean timeout = !negotiationChange.awaitUninterruptibly(acceptStatus, negotiationTimeout);
		if (timeout)
			throw new IOException("The access server timed out to negotiate option: " + optionCode + "!");
		if (refusedToSend.contains(optionCode))
			throw new IOException("The access server refused to send option: " + optionCode + "!");
	}

	/**
	 * Waits until the given option is accepted (Telnet:DO) or refused (Telnet:DONT) by the access server or an timeout
	 * occures. Nothing happens if within the given time the access server acknoledged that it is
	 * willing to accept the option. If an timeout occures or the access server denied to accept the
	 * option an {@link IOException} will be thrown.
	 * 
	 * @param optionCode
	 *            the code of the option to wait for, in the range [1..255]
	 * @param negotiationTimeout
	 *            the number of milli seconds to wait at most
	 * @throws IOException
	 */
	public void awaitAcceptOptionNegotiation(final int optionCode, @Nonnegative long negotiationTimeout) throws IOException {
		Predicate acceptStatus = new Predicate() {
			public boolean isSatisfied() {
				return willingToAccept.contains(optionCode) || refusedToAccept.contains(optionCode);
			}
		};

		final boolean timeout = !negotiationChange.awaitUninterruptibly(acceptStatus, negotiationTimeout);
		if (timeout)
			throw new IOException("The access server timed out to negotiate option: " + optionCode + "!");
		if (refusedToAccept.contains(optionCode))
			throw new IOException("The access server refused to accept option: " + optionCode + "!");
	}
}

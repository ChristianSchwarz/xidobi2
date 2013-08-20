/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 16:03:32
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;

import javax.annotation.Nonnegative;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;

/**
 * This class is used to await notifications during the negotiation phase of a telnet session.
 * 
 * @author Christian Schwarz
 * 
 */
public class NegotiationHandler {

	private TelnetNotificationHandler handler = new TelnetNotificationHandler() {

		public void receivedNegotiation(int negotiation_code, int option_code) {

			try {

			}
			finally {

			}
		}
	};

	/**
	 * @param telnetClient
	 *            the Telnet Client to be observed
	 */
	public NegotiationHandler(TelnetClient telnetClient) {
		if (telnetClient==null)
			throw new IllegalArgumentException("Parameter >telnetClient< must not be null!");
		
		telnetClient.registerNotifHandler(handler);
	}

	/**
	 * Waits until the given option is accepted or refused by the access server and returns the
	 * state. If the state of the given option code was not received within the given milliseconds
	 * an {@link IOException} will be thrown.
	 * 
	 * @param optionCode
	 *            the code of the option to wait for, in the range [1..255]
	 * @param expectedOptionState
	 *            the expected state, one of:
	 *            <ul>
	 *            <li>{@link TelnetNotificationHandler#RECEIVED_DO}
	 *            <li>{@link TelnetNotificationHandler#RECEIVED_DONT}
	 *            <li>{@link TelnetNotificationHandler#RECEIVED_WILL}
	 *            <li>{@link TelnetNotificationHandler#RECEIVED_WONT}
	 *            <ul>
	 * 
	 * @param negotiationTimeout
	 *            the number of milli seconds to wait at most
	 * @throws IOException
	 */
	public void awaitOptionState(int optionCode, int expectedOptionState, @Nonnegative long negotiationTimeout) throws IOException {

		throw new UnsupportedOperationException("Not implemented yet!");
	}

}

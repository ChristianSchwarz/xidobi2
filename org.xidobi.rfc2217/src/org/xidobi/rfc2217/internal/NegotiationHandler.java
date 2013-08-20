/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 16:03:32
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
public class NegotiationHandler {

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

			negotiationLock.tryLock();
			try {
				negotiationReceived.signalAll();
			}
			finally {
				negotiationLock.unlock();
			}
		}
	};
	/** The {@link TelnetClient} to be checked for accepted options */
	@Nonnull
	private final TelnetClient telnetClient;

	private final Set<Integer> willingToSend = new HashSet<Integer>();
	private final Set<Integer> refusedToSend = new HashSet<Integer>();
	private final Set<Integer> willingToAccept = new HashSet<Integer>();
	private final Set<Integer> refusedToAccept = new HashSet<Integer>();

	private final Lock negotiationLock = new ReentrantLock();

	private final Condition negotiationReceived = negotiationLock.newCondition();

	/**
	 * @param telnetClient
	 *            the Telnet Client to be observed
	 */
	public NegotiationHandler(@Nonnull TelnetClient telnetClient) {
		if (telnetClient == null)
			throw new IllegalArgumentException("Parameter >telnetClient< must not be null!");
		if (telnetClient.isConnected())
			throw new IllegalStateException("The Telnet Client must not be connected! Please instantiate this class before it is connected!");

		this.telnetClient = telnetClient;

		telnetClient.registerNotifHandler(handler);
	}

	/**
	 * Waits until the given option is accepted or refused by the access server or an timeout
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
	public void awaitWillAcceptOption(int optionCode, @Nonnegative long negotiationTimeout) throws IOException {
		long startTime = currentTimeMillis(); 

		long remainingTime = negotiationTimeout;
		do {
			if (willingToAccept.contains(optionCode))
				return;
			if (refusedToAccept.contains(optionCode))
				throw new IOException("The access server refused to accept option: " + optionCode + "!");

			awaitNotification(remainingTime);
			remainingTime= currentTimeMillis() - startTime;
			
		}
		while (remainingTime > 0);

	}

	/**
	 * @param remainingTime
	 */
	protected void awaitNotification(long remainingTime) {
		negotiationLock.tryLock();
		try {
			 negotiationReceived.await(remainingTime, MILLISECONDS);
		}
		catch (InterruptedException ignore) {}
		finally {
			negotiationLock.unlock();
		}
	}

	/**
	 * Waits until the given option is accepted or refused by the access server or an timeout
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
	public void awaitWillSendOption(int optionCode, long negotiationTimeout) throws IOException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

}

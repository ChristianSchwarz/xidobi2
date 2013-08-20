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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
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

	/** contains the options that the access server is willing to send*/
	private final Set<Integer> willingToSend = new HashSet<Integer>();
	/** contains the options that the access server refused to send*/
	private final Set<Integer> refusedToSend = new HashSet<Integer>();
	/** contains the options that the access server is willing to accept*/
	private final Set<Integer> willingToAccept = new HashSet<Integer>();
	/** contains the options that the access server refused to accept*/
	private final Set<Integer> refusedToAccept = new HashSet<Integer>();

	/** This lock guards the {@link #negotiationReceived}-Condition*/
	private final Lock negotiationLock = new ReentrantLock();
	/** This condition is signaled everytime a option negotiation was received*/
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
	 * willing to send the option. If an timeout occures or the access server denied to send the
	 * option an {@link IOException} will be thrown.
	 * 
	 * @param optionCode
	 *            the code of the option to wait for, in the range [1..255]
	 * @param negotiationTimeout
	 *            the number of milli seconds to wait at most
	 * @throws IOException
	 */
	public void awaitWillSendOption(final int optionCode, long negotiationTimeout) throws IOException {
		OptionStatus acceptStatus = new OptionStatus() {
			public boolean isStatusKnown() {
				return willingToSend.contains(optionCode) || refusedToSend.contains(optionCode);
			}
	
			public void throwIOExceptionIfRefused() throws IOException {
				if (refusedToSend.contains(optionCode))
					throw new IOException("The access server refused to send option: " + optionCode + "!");
			}
		};
	
		final boolean timeout = !loopUntilOptionStatusIsKnown(acceptStatus, negotiationTimeout);
		if (timeout)
			throw new IOException("The access server timed out to negotiate option: " + optionCode + "!");
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
	public void awaitWillAcceptOption(final int optionCode, @Nonnegative long negotiationTimeout) throws IOException {
		OptionStatus acceptStatus = new OptionStatus() {
			public boolean isStatusKnown() {
				return willingToAccept.contains(optionCode) || refusedToAccept.contains(optionCode);
			}

			public void throwIOExceptionIfRefused() throws IOException {
				if (refusedToAccept.contains(optionCode))
					throw new IOException("The access server refused to accept option: " + optionCode + "!");
			}
		};

		final boolean timeout = !loopUntilOptionStatusIsKnown(acceptStatus, negotiationTimeout);
		if (timeout)
			throw new IOException("The access server timed out to negotiate option: " + optionCode + "!");
	}

	/**
	 * Returns <code>true</code> if the loop finished because the option was accepted return
	 * <code>false</code> if an timeout was detected, or throws an {@link IOException} if the option
	 * was refused.
	 */
	private boolean loopUntilOptionStatusIsKnown(OptionStatus option, long timeoutMs) throws IOException {
		long startTime = currentTimeMillis();

		long remainingTime = timeoutMs;
		do {
			if (option.isStatusKnown()) {
				option.throwIOExceptionIfRefused();
				return true;
			}

			awaitNotification(remainingTime);

			final long elapsedMs = currentTimeMillis() - startTime;
			remainingTime = max(timeoutMs - elapsedMs, 0);

		}
		while (remainingTime > 0);

		return false;

	}

	/**
	 * Waits for any option notification, send by the access server.
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

	/** This interface is used to implement the specific behavior of will accept and will send*/
	private interface OptionStatus {
		boolean isStatusKnown();

		void throwIOExceptionIfRefused() throws IOException;
	}
}

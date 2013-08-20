/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 19.08.2013 17:13:52
 * Erstellt von: Christian Schwarz 
 */
package org.xidobi.rfc2217.internal;

import java.io.IOException;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOption;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations.Mock;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

import static org.mockito.Mockito.verify;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import static org.apache.commons.net.telnet.TelnetNotificationHandler.*;
import static org.apache.commons.net.telnet.TelnetOption.BINARY;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests the class {@link NegotiationHandler}
 * 
 * @author Christian Schwarz
 * 
 */
public class TestNegotiationHandler {

	/** needed to verifiy exception */
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/** class under test */
	@InjectMocks
	private NegotiationHandler handler;

	@Mock
	private TelnetClient telnetClient;

	@Captor
	private ArgumentCaptor<TelnetNotificationHandler> notificationHandler;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		initMocks(this);
		verify(telnetClient).registerNotifHandler(notificationHandler.capture());
	}

	/**
	 * If <code>null</code> is passed to the constructor an {@link IllegalArgumentException} must be
	 * thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void new_nullTelnetClient() {
		new NegotiationHandler(null);

	}

	/**
	 * {@link NegotiationHandler#awaitWillAcceptOption(int, long)} must return immediatly if the
	 * access server signaled that it is willing to accept the option, before the method was called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_acceptedBeforeCall() throws IOException {
		notifyNegotiationReceived(RECEIVED_DO, BINARY);

		handler.awaitWillAcceptOption(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitWillAcceptOption(int, long)} must throw an IOException if the
	 * access server signaled that it refused to accept the option, before the method was called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_refusedBeforeCall() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server refused to accept option: " + BINARY + "!");

		notifyNegotiationReceived(RECEIVED_DONT, BINARY);

		handler.awaitWillAcceptOption(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitWillAcceptOption(int, long)} must return if the access server
	 * signaled that it is willing to accept the option, before the method was called.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_refusedWhileWaiting() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server refused to accept option: " + BINARY + "!");

		notifyAsyncNegotiationReceived(RECEIVED_DONT, BINARY, 20);

		handler.awaitWillAcceptOption(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitWillAcceptOption(int, long)} must return if the access server
	 * is willing to accept the option while waiting.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_acceptedWhileWaiting() throws IOException {
		notifyAsyncNegotiationReceived(RECEIVED_DO, BINARY, 20);

		handler.awaitWillAcceptOption(BINARY, 1000);
	}

	/**
	 * {@link NegotiationHandler#awaitWillAcceptOption(int, long)} must throw an IOException if the
	 * access server didn't answer within the given time if it is willing to accept the option.
	 * <p>
	 * No notification of an refused or accepted option is send in this test case.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 200)
	public void awaitWillAcceptOption_timeout() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option: " + BINARY + "!");

		long start = currentTimeMillis();
		handler.awaitWillAcceptOption(BINARY, 50);
		assertThat(currentTimeMillis() - start, is(lessThan(100L)));
	}

	/**
	 * {@link NegotiationHandler#awaitWillAcceptOption(int, long)} must throw an IOException if the
	 * negotiation of the given option timed out.
	 * <p>
	 * 
	 * An irrelevant notification of an the interested option is send in this test case, while
	 * waiting.This must have no effect to the waiting method.
	 * 
	 * @throws IOException
	 */
	@Test(timeout = 100)
	public void awaitWillAcceptOption_timeout2() throws IOException {
		exception.expect(IOException.class);
		exception.expectMessage("The access server timed out to negotiate option: " + BINARY + "!");

		notifyAsyncNegotiationReceived(RECEIVED_DO, TelnetOption.ECHO, 20);

		long start = currentTimeMillis();
		handler.awaitWillAcceptOption(BINARY, 50);
		assertThat(currentTimeMillis() - start, is(lessThan(100L)));
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	protected void notifyNegotiationReceived(int negotiationCode, int optionCode) {
		notificationHandler.getValue().receivedNegotiation(negotiationCode, optionCode);
	}

	protected void notifyAsyncNegotiationReceived(final int negotiationCode, final int optionCode, final long delayMs) {
		new Thread() {
			@Override
			public void run() {
				try {
					sleep(delayMs);
					notifyNegotiationReceived(negotiationCode, optionCode);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}